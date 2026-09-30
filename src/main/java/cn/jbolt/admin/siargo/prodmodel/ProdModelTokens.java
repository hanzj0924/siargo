package cn.jbolt.admin.siargo.prodmodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** 型号只存参数片段；旧格式仅用于读取提取，不保留任何外层目录信息或组合条件。 */
public final class ProdModelTokens {
    /** siargo_prod_model.model_desc 为 TEXT，容量为 65535 字节。 */
    public static final int MAX_BYTES = 65535;
    private ProdModelTokens() {}

    public static List<JSONArray> branches(String source) {
        if (source == null || source.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES)
            throw new IllegalArgumentException("型号参数为空或超过" + MAX_BYTES + "字节");
        Object parsed;
        try { parsed = JSON.parse(source); }
        catch (RuntimeException error) { throw new IllegalArgumentException("型号参数格式错误"); }
        List<JSONArray> branches = new ArrayList<>();
        if (parsed instanceof JSONObject old) {
            if (!"2".equals(old.getString("version")) || !(old.get("variants") instanceof JSONArray variants))
                throw new IllegalArgumentException("型号参数格式错误");
            for (Object item : variants) {
                if (!(item instanceof JSONObject variant) || !(variant.get("tokens") instanceof JSONArray tokens))
                    throw new IllegalArgumentException("型号分支缺少参数片段");
                branches.add(tokens);
            }
        } else if (parsed instanceof JSONArray root) {
            boolean nested = root.stream().anyMatch(item -> item instanceof JSONArray);
            if (nested) {
                for (Object item : root) {
                    if (!(item instanceof JSONArray tokens)) throw new IllegalArgumentException("型号分支不能混用片段和数组");
                    branches.add(tokens);
                }
            } else branches.add(root);
        } else throw new IllegalArgumentException("型号参数须为片段数组");
        if (branches.isEmpty() || branches.size() > 12) throw new IllegalArgumentException("型号分支数量须为1至12组");
        for (JSONArray tokens : branches) {
            if (tokens.isEmpty() || tokens.size() > 40) throw new IllegalArgumentException("每组型号须有1至40个参数片段");
            for (Object item : tokens) {
                if (item instanceof String) continue;
                if (!(item instanceof JSONObject slot)) throw new IllegalArgumentException("型号参数片段格式错误");
                if (slot.containsKey("t")) slot.put("t", ProdModelSelectionSchema.id(slot.get("t")));
                if (slot.containsKey("valueIds")) {
                    if (!(slot.get("valueIds") instanceof JSONArray ids)) throw new IllegalArgumentException("参数值须为数组");
                    for (int i = 0; i < ids.size(); i++) ids.set(i, ProdModelSelectionSchema.id(ids.get(i)));
                }
            }
        }
        return branches;
    }

    public static String normalize(String source) {
        List<JSONArray> branches = branches(source);
        String result = JSON.toJSONString(branches.size() == 1 ? branches.get(0) : branches,
                SerializerFeature.WriteMapNullValue);
        if (result.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES)
            throw new IllegalArgumentException("型号参数超过" + MAX_BYTES + "字节");
        return result;
    }
}
