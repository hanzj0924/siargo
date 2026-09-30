package cn.jbolt.admin.siargo.prodmodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** 型号格式与规格条件的只读视图。规格条件保留源记录，不合并可选值的适用范围。 */
final class ProdModelFormats {
    private ProdModelFormats() {}

    /** 与编辑器采用相同的结构口径：固定文字、参数类型、位置、输入方式和前缀。 */
    static String key(JSONArray tokens, Map<String, String> types) {
        List<Object> shape = new ArrayList<>();
        StringBuilder literal = new StringBuilder();
        for (Object item : tokens) {
            if (item instanceof String text) { literal.append(text); continue; }
            flushLiteral(shape, literal);
            JSONObject slot = (JSONObject) item;
            JSONArray descriptor = new JSONArray();
            descriptor.add("slot"); descriptor.add(parameterKey(slot, types));
            descriptor.add(slot.getString("input") == null ? "" : slot.getString("input"));
            descriptor.add(slot.getString("prefix") == null ? "" : slot.getString("prefix"));
            shape.add(descriptor);
        }
        flushLiteral(shape, literal);
        return JSON.toJSONString(shape);
    }

    /** PDF 中最大流量的不同单位仍占同一段；公称流量等其他物理含义保持独立。 */
    private static String parameterKey(JSONObject slot, Map<String, String> types) {
        String typeId = slot.getString("t");
        String type = typeId == null ? null : types.get(typeId);
        return "maxFlow".equals(slot.getString("key"))
                && ("最大流量".equals(type) || "最大体积流量".equals(type)) ? "role:maxFlow" : typeId;
    }

    private static void flushLiteral(List<Object> shape, StringBuilder literal) {
        if (literal.length() == 0) return;
        shape.add(List.of("literal", literal.toString())); literal.setLength(0);
    }

    static List<Kv> display(String source, Map<String, String> types, Map<String, JSONObject> values) {
        Map<String, List<Kv>> grouped = new LinkedHashMap<>();
        for (Kv variant : ProdModelSelectionSchema.displayVariants(source, types, values))
            grouped.computeIfAbsent(variant.getStr("formatKey"), ignored -> new ArrayList<>()).add(variant);
        List<Kv> formats = new ArrayList<>();
        for (var group : grouped.entrySet()) formats.add(format(group.getKey(), group.getValue(), types));
        return formats;
    }

    private static Kv copy(Kv source) { return Kv.create().set(source); }

    private static Kv format(String key, List<Kv> sources, Map<String, String> types) {
        Kv first = sources.get(0);
        List<Kv> sourceTokens = first.getAs("tokens");
        List<Kv> firstSelections = first.getAs("selections");
        List<Kv> tokens = new ArrayList<>(), common = new ArrayList<>(), headers = new ArrayList<>();
        List<Integer> varying = new ArrayList<>();
        List<String> sourceKeys = new ArrayList<>();
        for (Kv source : sources) sourceKeys.add(source.getStr("key"));
        int position = 0;
        for (Kv original : sourceTokens) {
            Kv token = copy(original);
            if ("param".equals(token.getStr("kind"))) {
                final int slotIndex = position++;
                Kv selection = firstSelections.get(slotIndex);
                String typeId = selection.getStr("typeId"), originalName = selection.getStr("typeName");
                String title = typeId == null ? originalName : types.getOrDefault(typeId, originalName);
                boolean mixedTypes = sources.stream().anyMatch(source -> !Objects.equals(typeId,
                        ((List<Kv>) source.getAs("selections")).get(slotIndex).getStr("typeId")));
                if (mixedTypes && ("最大流量".equals(title) || "最大体积流量".equals(title))) title = "最大流量";
                token.set("typeName", title);
                Kv heading = copy(selection).set("typeName", title);
                if (sources.stream().allMatch(source -> sameSelection(selection,
                        ((List<Kv>) source.getAs("selections")).get(slotIndex)))) common.add(heading);
                else { varying.add(slotIndex); headers.add(heading); }
            }
            tokens.add(token);
        }
        List<Kv> conditions = new ArrayList<>();
        if (!varying.isEmpty()) for (Kv source : sources) {
            List<Kv> selections = source.getAs("selections"), cells = new ArrayList<>();
            for (int index : varying) cells.add(copy(selections.get(index)));
            conditions.add(Kv.by("key", source.getStr("key")).set("selections", cells));
        }
        return Kv.by("key", "format_" + first.getStr("key")).set("formatKey", key)
                .set("tokens", tokens).set("selections", common).set("conditionHeaders", headers)
                .set("conditions", conditions).set("caseCount", sources.size()).set("sourceKeys", sourceKeys);
    }

    private static boolean sameSelection(Kv left, Kv right) {
        Kv a = copy(left), b = copy(right);
        a.remove("slotKey"); b.remove("slotKey");
        return a.equals(b);
    }
}
