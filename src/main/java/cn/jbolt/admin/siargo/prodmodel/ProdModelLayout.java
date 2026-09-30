package cn.jbolt.admin.siargo.prodmodel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 型号参数片段的列表摘要与编辑回显。 */
public final class ProdModelLayout {
    private ProdModelLayout() {}

    static String validate(String modelDesc, Collection<String> typeIds) {
        try {
            Map<String, Set<String>> refs = new LinkedHashMap<>();
            for (JSONArray branch : ProdModelTokens.branches(modelDesc)) {
                for (Object item : branch) if (item instanceof JSONObject slot && slot.containsKey("t")) {
                    Set<String> ids = refs.computeIfAbsent(slot.getString("t"), ignored -> new LinkedHashSet<>());
                    if (slot.getJSONArray("valueIds") == null) ids.add("1");
                    else for (Object id : slot.getJSONArray("valueIds")) ids.add(id.toString());
                }
            }
            if (!refs.keySet().equals(new LinkedHashSet<>(typeIds))) return "型号参数种类与关联不一致";
            return ProdModelSelectionSchema.validate(modelDesc, refs);
        } catch (RuntimeException error) { return "型号参数格式错误"; }
    }

    static List<Kv> tokens(String series, String modelDesc, Map<String, String> typeNames) {
        try { return ProdModelSelectionSchema.displayVariants(modelDesc, typeNames, Map.of()).get(0).getAs("tokens"); }
        catch (RuntimeException error) { return legacyTokens(series, typeNames); }
    }

    private static List<Kv> legacyTokens(String series, Map<String, String> typeNames) {
        // 无法解析的早期记录仍保留只读展示线索，保存时由统一参数校验阻止。
        List<Kv> result = new ArrayList<>();
        result.add(Kv.by("kind", "lit").set("text", series == null ? "" : series));
        int position = 0;
        for (var type : typeNames.entrySet()) {
            result.add(Kv.by("kind", "lit").set("text", "-"));
            result.add(Kv.by("kind", "param").set("typeId", type.getKey())
                .set("typeName", type.getValue()).set("position", ++position));
        }
        return result;
    }

    static String description(List<Kv> tokens) {
        StringBuilder text = new StringBuilder();
        for (Kv token : tokens)
            text.append("lit".equals(token.getStr("kind")) ? token.getStr("text") : token.getStr("typeName"));
        return text.toString();
    }

    static String editDescription(String modelDesc) {
        try { return ProdModelTokens.normalize(modelDesc); }
        catch (RuntimeException error) { return modelDesc; }
    }

    static String summary(String series, String modelDesc, Map<String, String> typeNames) {
        return String.join(" / ", summaryBranches(series, modelDesc, typeNames));
    }

    /** 分支按型号格式划分；管径、气体、量程等规格条件在格式内部保留。 */
    public static List<String> summaryBranches(String series, String modelDesc, Map<String, String> typeNames) {
        return displayDescription(series, modelDesc, typeNames).branches();
    }

    /** 一次解析生成纯文本及轻量片段，显示结构不携带参数 ID 或选项。 */
    static DisplayDescription displayDescription(String series, String modelDesc, Map<String, String> typeNames) {
        List<List<Kv>> tokenBranches = new ArrayList<>();
        try {
            for (Kv branch : ProdModelFormats.display(modelDesc, typeNames, Map.of()))
                tokenBranches.add(branch.getAs("tokens"));
        } catch (RuntimeException error) { tokenBranches = List.of(legacyTokens(series, typeNames)); }
        List<String> descriptions = new ArrayList<>();
        List<List<Kv>> parts = new ArrayList<>();
        for (List<Kv> tokens : tokenBranches) {
            StringBuilder description = new StringBuilder();
            List<Kv> branchParts = new ArrayList<>();
            for (Kv token : tokens) {
                String kind = "lit".equals(token.getStr("kind")) ? "lit" : "param";
                String text = String.valueOf(token.getStr("lit".equals(kind) ? "text" : "typeName"));
                branchParts.add(Kv.by("kind", kind).set("text", text));
                description.append(text);
            }
            descriptions.add(description.toString());
            parts.add(branchParts);
        }
        return new DisplayDescription(descriptions, parts);
    }

    record DisplayDescription(List<String> branches, List<List<Kv>> parts) {
        String summary() { return String.join(" / ", branches); }
    }
}
