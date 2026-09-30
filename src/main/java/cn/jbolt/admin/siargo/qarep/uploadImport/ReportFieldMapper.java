package cn.jbolt.admin.siargo.qarep.uploadImport;

import cn.jbolt.admin.siargo.prodmodel.ProdModelSelectionSchema;
import cn.jbolt.admin.siargo.prodmodel.ProdModelTokens;
import cn.jbolt.admin.siargo.qarep.product.ProductSeriesMatcher;
import cn.jbolt.siargo.model.Qareport;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 实测参数取产品数据，检验项按关联系列及该分支的选型规则填写。 */
public class ReportFieldMapper {
    public static final Map<String, String> NUMERIC_FIELDS;
    static {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("thv", "热头电压");
        fields.put("zp", "零点内码");
        NUMERIC_FIELDS = java.util.Collections.unmodifiableMap(fields);
    }

    private static String text(Object value) { return value == null ? "" : value.toString(); }

    public Map<String, String> buildDataMap(Qareport report) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String field : new String[]{"formnum", "sp_qsi", "sp_qi", "sc_name", "order_id", "sp_model", "c_time", "sp_number"}) {
            map.put(field, text(report.get(field)));
        }
        String type = text(report.get("rep_type"));

        if ("1".equals(type))
            map.put("rep_type_name", "■产成品 □退修品");
        else if ("2".equals(type))
            map.put("rep_type_name", "□产成品 ■退修品");

        for (String stage : new String[]{"accq", "lt", "funq", "appq", "allq"}) {
            for (String suffix : new String[]{"name", "time", "email"}) {
                String field = stage + "_" + suffix;
                map.put(field, text(report.get(field)));
            }
        }

        String flowName = text(report.get("flow_name"));
        map.put("flow_range", flowName.isBlank() ? text(report.get("sp_flow_range")) : flowName);
        for (String field : NUMERIC_FIELDS.keySet()) {
            map.put(field, text(report.get("sp_" + field)));
        }

        String series = text(report.get("model_series"));
        map.put("para2", "MF65/6600".equals(series) ? "ok" : "/");
        map.put("para6", "MF5200".equals(series) ? "ok" : "/");
        map.put("para7", "MF5700".equals(series) ? "ok" : "/");

        if ("MFI".equals(series)){
            map.put("cucmax", text(report.get("sp_cucmax")));
            map.put("cucmin", text(report.get("sp_cucmin")));
            map.put("pv", text(report.get("sp_pv")));
            map.put("pulseValue", "/");
        }

        if ("MF-GD".equals(series)){
            map.put("cuc", text(report.get("sp_cuc")));
            map.put("fl", text(report.get("sp_fl")));
        }

        if ("MF-FD".equals(series)){
            map.put("cucmax", text(report.get("sp_cucmax")));
            map.put("cucmin", text(report.get("sp_cucmin")));
            map.put("pv", text(report.get("sp_pv")));
            map.put("la", text(report.get("sp_la")));
            map.put("pulseValue", "/");
            if ("E".equals(secondSelectionValue(report))){
                map.put("pulseValue", "ok");
                map.put("fl", "/");
                map.put("bv", "/");
            }else {
                map.put("fl", text(report.get("sp_fl")));
                map.put("bv", text(report.get("sp_bv")));
            }
        }

        return map;
    }

    private String secondSelectionValue(Qareport report) {
        String model = ProductSeriesMatcher.normalize(text(report.get("sp_model")));
        try {
            if (model.isEmpty()) throw new IllegalArgumentException("完整型号为空");
            if (text(report.get("selection_rule")).isBlank()) throw new IllegalArgumentException("关联系列的选型规则缺失");
            JSONObject values = JSON.parseObject(text(report.get("selection_values")));
            if (values == null || values.isEmpty()) throw new IllegalArgumentException("选型参数字典缺失");
            Set<String> matches = new LinkedHashSet<>();
            for (JSONArray tokens : ProdModelTokens.branches(text(report.get("selection_rule")))) {
                StringBuilder expression = new StringBuilder("^");
                int position = 0;
                boolean afterSecond = false;
                boolean complete = true;
                for (Object token : tokens) {
                    if (token instanceof String literal) {
                        String fixed = ProductSeriesMatcher.normalize(literal);
                        // 旧二代在内径与后续固定片段间多一个连接符，只兼容此格式差异。
                        if (position == 1 && !fixed.isEmpty() && Character.isLetter(fixed.charAt(0))) expression.append("-?");
                        expression.append(Pattern.quote(fixed));
                        if (position == 2 && !fixed.isEmpty()) afterSecond = true;
                        continue;
                    }
                    if (position == 2) { complete = false; break; }
                    JSONObject slot = (JSONObject) token;
                    JSONArray ids = slot.getJSONArray("valueIds");
                    if (ids == null || ids.isEmpty() || "multiple".equals(slot.getString("selection")))
                        throw new IllegalArgumentException("前两个选型参数缺少可选值或不是单选参数");
                    List<String> codes = new ArrayList<>();
                    for (Object id : ids) {
                        JSONObject value = values.getJSONObject(id.toString());
                        if (value == null || !text(slot.get("t")).equals(value.getString("type"))) continue;
                        String code = ProdModelSelectionSchema.resolveCode(slot, id.toString(), value.getString("value"));
                        if (!code.isEmpty()) codes.add(Pattern.quote(ProductSeriesMatcher.normalize(code)));
                    }
                    if (codes.isEmpty()) throw new IllegalArgumentException("选型参数没有可用编码");
                    expression.append(Pattern.quote(ProductSeriesMatcher.normalize(text(slot.get("prefix")))));
                    expression.append(++position == 2 ? "(" : "(?:").append(String.join("|", codes)).append(")");
                }
                if (position != 2 || (!complete && !afterSecond)) continue;
                if (complete) expression.append("$");
                Matcher match = Pattern.compile(expression.toString()).matcher(model);
                if (match.find()) matches.add(match.group(1));
            }
            if (matches.isEmpty()) throw new IllegalArgumentException("无法按选型规则解析第2个参数（供电方式）");
            if (matches.size() != 1) throw new IllegalArgumentException("选型规则匹配到多个供电方式");
            return matches.iterator().next();
        } catch (com.alibaba.fastjson.JSONException invalidJson) {
            throw selectionError(report, "选型规则或参数字典格式错误");
        } catch (IllegalArgumentException invalidRule) {
            throw selectionError(report, invalidRule.getMessage());
        }
    }

    private IllegalArgumentException selectionError(Qareport report, String reason) {
        return new IllegalArgumentException("MF-FD 供电方式解析失败：" + reason
                + "；完整型号：" + text(report.get("sp_model")) + "；订单号：" + text(report.get("order_id")));
    }
}
