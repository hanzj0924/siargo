package cn.jbolt.admin.siargo.prodmodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 删除字典值时精确移除型号槽内引用；空槽与空组合保留，等待用户重新维护。 */
final class ProdModelValueRemoval {
    private ProdModelValueRemoval() {}

    static String remove(String source, Set<String> deletedIds,
                         Map<String, ? extends Collection<String>> references,
                         Map<String, String> typeNames, Map<String, String> dictionaryCodes) {
        List<JSONArray> branches = ProdModelTokens.branches(source);
        boolean changed = false;
        for (JSONArray branch : branches) {
            int position = 0;
            for (Object item : branch) {
                if (!(item instanceof JSONObject slot)) continue;
                position++;
                if (!slot.containsKey("t")) continue;
                JSONArray valueIds = slot.getJSONArray("valueIds");
                if (slot.size() == 1) {
                    Collection<String> oldReferences = references.get(slot.getString("t"));
                    if (oldReferences == null || oldReferences.stream().noneMatch(deletedIds::contains)) continue;
                    // 旧 t-only 结构依赖关联表取值，删除前固化到原段位，防止最后一个值删除后丢槽。
                    valueIds = new JSONArray(new ArrayList<>(oldReferences));
                    slot.put("key", "slot_" + position);
                    slot.put("label", typeNames.getOrDefault(slot.getString("t"), "参数" + position));
                    slot.put("valueIds", valueIds);
                }
                if (valueIds == null) continue;
                Set<String> removedCodes = new LinkedHashSet<>(), survivingCodes = new LinkedHashSet<>();
                JSONArray survivingIds = new JSONArray();
                boolean removed = false;
                JSONObject mappings = slot.getJSONObject("codes");
                for (Object value : valueIds) {
                    String id = value.toString();
                    String code = mappings != null && mappings.containsKey(id)
                            ? mappings.getString(id) : dictionaryCodes.get(id);
                    if (deletedIds.contains(id)) {
                        removed = true;
                        if (code != null) removedCodes.add(code);
                    } else {
                        survivingIds.add(id);
                        if (code != null) survivingCodes.add(code);
                    }
                }
                if (!removed) continue;
                changed = true;
                slot.put("valueIds", survivingIds);
                removeMappings(slot, "codes", deletedIds);
                removeMappings(slot, "descriptions", deletedIds);
                // 同码的另一条字典记录仍被当前槽选中时，编码约束继续有效。
                removedCodes.removeAll(survivingCodes);
                removeGroupCodes(slot, removedCodes);
                removeCombinations(slot, removedCodes);
                clampSelectionCount(slot, survivingIds.size());
            }
        }
        if (!changed) return source;
        String result = JSON.toJSONString(branches.size() == 1 ? branches.get(0) : branches,
                SerializerFeature.WriteMapNullValue);
        if (result.getBytes(StandardCharsets.UTF_8).length > ProdModelTokens.MAX_BYTES)
            throw new IllegalArgumentException("删除参数值后的型号结构超过" + ProdModelTokens.MAX_BYTES + "字节");
        return result;
    }

    private static void removeMappings(JSONObject slot, String field, Set<String> deletedIds) {
        JSONObject mappings = slot.getJSONObject(field);
        if (mappings != null) deletedIds.forEach(mappings::remove);
    }

    private static void removeGroupCodes(JSONObject slot, Set<String> removedCodes) {
        JSONArray groups = slot.getJSONArray("groups");
        if (groups == null) return;
        JSONArray remaining = new JSONArray();
        for (Object item : groups) {
            JSONObject group = (JSONObject) item;
            JSONArray values = group.getJSONArray("values");
            values.removeIf(value -> removedCodes.contains(value.toString()));
            if (values.isEmpty()) continue;
            if (group.containsKey("max") && group.getIntValue("max") > values.size())
                group.put("max", values.size());
            remaining.add(group);
        }
        slot.put("groups", remaining);
    }

    private static void removeCombinations(JSONObject slot, Set<String> removedCodes) {
        JSONArray combinations = slot.getJSONArray("allowedCombinations");
        if (combinations == null) return;
        // 删除整组而非组内某个编码：CV 删除 V 后不应变成原本未允许的 C。
        combinations.removeIf(item -> ((JSONArray) item).stream()
                .anyMatch(code -> removedCodes.contains(code.toString())));
        // 即使变空也保留字段；缺少字段表示不限制组合，两者业务语义不同。
    }

    private static void clampSelectionCount(JSONObject slot, int remainingCount) {
        JSONArray combinations = slot.getJSONArray("allowedCombinations");
        if (combinations != null && !combinations.isEmpty()) {
            int min = Integer.MAX_VALUE, max = 0;
            for (Object item : combinations) {
                int count = ((JSONArray) item).size();
                min = Math.min(min, count); max = Math.max(max, count);
            }
            slot.put("minSelect", min); slot.put("maxSelect", max);
            return;
        }
        for (String field : List.of("minSelect", "maxSelect")) {
            if (remainingCount == 0 || (slot.containsKey(field) && slot.getIntValue(field) > remainingCount))
                slot.put(field, remainingCount);
        }
    }
}
