package cn.jbolt.admin.siargo.qarep.product;

import cn.jbolt.siargo.model.Product;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** 报告单产品卡片输入：只接受逐产品 JSON，白名单隔离行数据和服务器字段。 */
public final class ReportProductInput {
    public static final List<String> ELECTRICAL_FIELDS = List.of(
            "cuc", "cucmax", "cucmin", "pv", "thv", "zp", "fl", "bv", "la");
    public static final List<String> EDITABLE_FIELDS = List.of(
            "siargo_prod_model_id", "model", "number", "qsi", "qi", "des", "lt_status",
            "flow_range", "cuc", "cucmax", "cucmin", "pv", "thv", "zp", "fl", "bv", "la", "pdfver");

    private ReportProductInput() {}

    public static Ret parse(String source, Product defaults) {
        if (source == null || source.isBlank()) return Ret.fail("请添加产品并填写产品信息");
        if (source.length() > 2_000_000) return Ret.fail("产品数据过大，请分批提交");
        final JSONArray rows;
        try {
            rows = JSON.parseArray(source);
        } catch (RuntimeException ex) {
            return Ret.fail("产品数据格式错误，请刷新页面后重试");
        }
        if (rows == null || rows.isEmpty() || rows.size() > 500) return Ret.fail("每份报告单需要 1 至 500 个产品模块");
        List<Product> products = new ArrayList<>();
        for (int index = 0; index < rows.size(); index++) {
            if (!(rows.get(index) instanceof JSONObject row)) return Ret.fail("产品 #" + (index + 1) + " 数据格式错误");
            try {
                if (row.containsKey("id") || row.containsKey("report_id")) throw new IllegalArgumentException("新增产品不能携带记录 ID");
                Product product = new Product();
                Object seriesId = row.get("siargo_prod_model_id");
                if (!(seriesId instanceof String) || !seriesId.toString().matches("[1-9][0-9]*")) {
                    throw new IllegalArgumentException("请选择型号系列");
                }
                product.set("siargo_prod_model_id", Long.valueOf(seriesId.toString()));
                product.set("model", string(row.get("model")));
                product.set("number", string(row.get("number")));
                product.set("des", string(row.get("des")));
                product.set("flow_range", string(row.get("flow_range")));
                product.set("qsi", integer(row.get("qsi"), "送检数量"));
                product.set("qi", integer(row.get("qi"), "检验数量"));
                product.set("lt_status", integer(row.get("lt_status"), "成品检漏"));
                for (String field : ELECTRICAL_FIELDS) {
                    Object value = row.get(field);
                    if (List.of("thv", "zp", "la").contains(field)) product.set(field, integer(value, field));
                    else product.set(field, decimal(value, field));
                }
                product.set("pdfver", defaults == null ? null : defaults.getStr("pdfver"));
                product.set("insp", defaults == null ? null : defaults.getInt("insp"));
                String error = validateBasics(product);
                if (error != null) throw new IllegalArgumentException(error);
                products.add(product);
            } catch (RuntimeException ex) {
                String message = ex instanceof NumberFormatException ? "数值格式错误或超出范围" : ex.getMessage();
                return Ret.fail("产品 #" + (index + 1) + "：" + message);
            }
        }
        return Ret.ok().set("data", products);
    }

    public static String validateBasics(Product product) {
        if (product == null) return "产品信息不能为空";
        if (string(product.get("model")).isBlank()) return "产品型号不能为空";
        if (string(product.get("model")).length() > 255) return "产品型号不能超过 255 个字符";
        if (string(product.get("number")).isBlank()) return "产品编号不能为空";
        if (string(product.get("number")).length() > 255) return "产品编号不能超过 255 个字符";
        if (string(product.get("des")).length() > 1000) return "产品描述不能超过 1000 个字符";
        if (string(product.get("flow_range")).length() > 255) return "流量范围不能超过 255 个字符";
        if (product.getInt("qsi") == null || product.getInt("qi") == null) return "送检数量和检验数量不能为空";
        if (product.getInt("qsi") < 0 || product.getInt("qi") < 0) return "数量不能为负数";
        if (product.getInt("qsi") < product.getInt("qi")) return "送检数量不能小于检验数量";
        Integer lt = product.getInt("lt_status");
        if (lt == null || (lt != 1 && lt != 2)) return "请选择成品检漏状态";
        String version = string(product.get("pdfver"));
        if (version.isBlank() || version.length() > 10) return "请选择正确的报告单版本";
        return null;
    }

    private static String string(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private static Integer integer(Object value, String label) {
        String input = string(value);
        if (input.isBlank()) return null;
        if (!input.matches("-?[0-9]+")) throw new IllegalArgumentException(label + "必须为整数");
        return Integer.valueOf(input);
    }

    private static Double decimal(Object value, String label) {
        String input = string(value);
        if (input.isBlank()) return null;
        try {
            double result = new BigDecimal(input).doubleValue();
            if (!Double.isFinite(result)) throw new NumberFormatException();
            return result;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + "必须为有限数值");
        }
    }
}
