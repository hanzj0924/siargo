package cn.jbolt.admin.siargo.prodmodel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jfinal.kit.Kv;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 型号参数片段的校验与展示；分支只在内存组织，槽内可保存允许的编码组合。 */
public final class ProdModelSelectionSchema {
    private static final String CODE = "[A-Za-z0-9 ._/+²³µμ-]*";

    private ProdModelSelectionSchema() {}

    /** 仅供运行时使用的分支视图，外层字段永不写入型号。 */
    public static JSONObject read(String source) {
        return read(source, Map.of(), Map.of());
    }

    public static JSONObject read(String source, Map<String, ? extends Collection<String>> references,
                                  Map<String, String> types) {
        JSONArray variants = new JSONArray();
        int branchIndex = 0;
        for (JSONArray tokens : ProdModelTokens.branches(source)) {
            int position = 0;
            for (Object item : tokens) if (item instanceof JSONObject slot) {
                position++;
                // 历史 t-only 参数的显示信息及值来自本次关联，仅作运行时补齐。
                if (slot.size() == 1 && slot.containsKey("t")) {
                    String type = slot.getString("t");
                    slot.put("key", "slot_" + position);
                    slot.put("label", types.getOrDefault(type, "参数" + position));
                    Collection<String> referenced = references.get(type);
                    if (referenced != null) slot.put("valueIds", new JSONArray(new ArrayList<>(referenced)));
                }
                // 参数名称只从当前种类记录补齐；原始输入槽交由校验或只读过滤处理。
                String typeId = slot.getString("t");
                if (typeId != null && types.get(typeId) != null) slot.put("label", types.get(typeId));
            }
            String prefix = tokens.get(0) instanceof String literal ? literal.replaceFirst("[- ]+$", "") : "型号";
            JSONObject variant = new JSONObject(true);
            variant.put("key", "branch_" + branchIndex);
            variant.put("name", prefix + "（" + (branchIndex + 1) + "）");
            variant.put("tokens", tokens);
            variants.add(variant); branchIndex++;
        }
        JSONObject schema = new JSONObject(true); schema.put("variants", variants); return schema;
    }

    /** 存储及编辑回显永远只含 tokens 数组。 */
    public static String normalize(String source) { return ProdModelTokens.normalize(source); }

    /** 历史读取只保留现存字典种类；不修改原记录，也不把自由输入映射为新种类。 */
    public static String dictionaryDescription(String source, Map<String, String> types) {
        JSONArray branches = new JSONArray();
        for (JSONArray tokens : ProdModelTokens.branches(source)) {
            dictionaryTokens(tokens, types);
            branches.add(tokens);
        }
        // 补入当前参数种类名称后，再按实际入库 JSON 检查 TEXT 容量。
        return normalize(JSON.toJSONString(branches.size() == 1 ? branches.get(0) : branches,
                SerializerFeature.WriteMapNullValue));
    }

    private static JSONObject dictionaryView(String source, Map<String, ? extends Collection<String>> references,
                                             Map<String, String> types) {
        JSONObject schema = read(source, references, types);
        for (Object item : schema.getJSONArray("variants")) {
            dictionaryTokens(((JSONObject) item).getJSONArray("tokens"), types);
        }
        return schema;
    }

    private static void dictionaryTokens(JSONArray tokens, Map<String, String> types) {
        tokens.removeIf(token -> token instanceof JSONObject slot
                && (slot.getString("t") == null || types.get(slot.getString("t")) == null));
        for (Object token : tokens) if (token instanceof JSONObject slot) {
            for (String field : List.of("input", "allowCustom", "customInput", "maxLength", "min", "max", "freeText", "descriptions"))
                slot.remove(field);
            // t-only 历史片段仍由关联记录补齐，不能提前添加 label 破坏识别条件。
            if (slot.size() > 1) slot.put("label", types.get(slot.getString("t")));
        }
    }

    /** 保存及组合只允许引用种类管理的参数，历史自由输入不能作为提交绕过校验。 */
    private static void requireDictionarySlot(JSONObject slot) {
        require(slot.containsKey("t"), "参数种类必须从种类管理中选择，请先手动添加缺少的种类");
        require(!slot.containsKey("input") && !slot.containsKey("customInput") && !slot.containsKey("freeText")
                && (!slot.containsKey("allowCustom") || Boolean.FALSE.equals(slot.get("allowCustom")))
                && !slot.containsKey("maxLength") && !slot.containsKey("min") && !slot.containsKey("max"),
                "参数只能选择种类管理中的值，不支持自由输入或自定义编码");
    }

    /** 验证全部分支的字典引用并集。 */
    public static String validate(String source, Map<String, ? extends Collection<String>> references) {
        return validate(source, references, null);
    }

    /** 字典编码参与条件校验，编辑删除值或改类型后不可保留失效条件。 */
    public static String validate(String source, Map<String, ? extends Collection<String>> references,
                                  Map<String, JSONObject> dictionary) {
        try {
            JSONObject schema = read(source, references, Map.of());
            JSONArray variants = schema.getJSONArray("variants");
            require(variants != null && !variants.isEmpty() && variants.size() <= 12, "型号分支数量须为1至12个");
            Set<String> variantKeys = new LinkedHashSet<>();
            Map<String, Set<String>> used = new LinkedHashMap<>();
            for (Object value : variants) {
                require(value instanceof JSONObject, "型号分支格式错误");
                JSONObject variant = (JSONObject) value;
                String variantKey = variant.getString("key");
                require(validKey(variantKey) && variantKeys.add(variantKey), "型号分支标识无效或重复");
                JSONArray tokens = variant.getJSONArray("tokens");
                require(tokens != null && !tokens.isEmpty() && tokens.size() <= 40, "每组型号须有1至40个结构项");
                Set<String> slotKeys = new LinkedHashSet<>();
                boolean content = false;
                for (Object item : tokens) {
                    if (item instanceof String literal) {
                        require(!literal.isEmpty() && literal.length() <= 200 && literal.matches(CODE), "型号固定字符无效");
                        content |= literal.matches(".*[A-Za-z0-9].*");
                        continue;
                    }
                    require(item instanceof JSONObject, "型号参数槽格式错误");
                    JSONObject slot = (JSONObject) item;
                    requireDictionarySlot(slot);
                    String key = slot.getString("key");
                    require(validKey(key) && slotKeys.add(key), "同一分支的参数槽标识无效或重复");
                    require(textLength(slot.getString("label"), 100), "请填写100字以内的参数槽名称");
                    String prefix = slot.getString("prefix");
                    require(prefix == null || (prefix.length() <= 20 && prefix.matches(CODE)), "可选参数的前导字符无效");
                    content = true;
                    String type = id(slot.get("t"));
                    Collection<String> available = references.get(type);
                    require(available != null, "参数种类未关联当前型号");
                    JSONArray values = slot.getJSONArray("valueIds");
                    require(values != null && !values.isEmpty() && values.size() <= 200, "每个枚举参数须选择1至200个值");
                    validateCodeMappings(slot);
                    Set<String> seen = new LinkedHashSet<>();
                    Set<String> codes = new LinkedHashSet<>();
                    for (Object candidate : values) {
                        String valueId = id(candidate);
                        require(seen.add(valueId) && available.contains(valueId), "参数值重复或不属于当前型号的参数种类");
                        if (dictionary != null) {
                            JSONObject record = dictionary.get(valueId);
                            require(record != null && type.equals(record.getString("paramTypeId")), "参数值不存在或已变更种类");
                            boolean uniqueCode = codes.add(resolveCode(slot, valueId, record.getString("paramValue")));
                            require(!slot.containsKey("allowedCombinations") || uniqueCode, "允许组合的参数槽不能包含相同编码的多个值");
                        }
                    }
                    used.computeIfAbsent(type, ignored -> new LinkedHashSet<>()).addAll(seen);
                    String selection = slot.getString("selection");
                    require(selection == null || "single".equals(selection) || "multiple".equals(selection), "参数选择方式无效");
                    int min = integer(slot, "minSelect", Boolean.TRUE.equals(slot.getBoolean("optional")) ? 0 : 1);
                    int max = integer(slot, "maxSelect", "multiple".equals(selection) ? values.size() : 1);
                    require(min >= 0 && max >= Math.max(1, min) && max <= values.size(), "参数多选数量范围无效");
                    require("multiple".equals(selection) || (min <= 1 && max == 1), "单选参数的数量范围无效");
                    JSONArray groups = slot.getJSONArray("groups");
                    if (groups != null) for (Object groupValue : groups) {
                        require(groupValue instanceof JSONObject, "参数分组格式错误");
                        JSONObject group = (JSONObject) groupValue;
                        require(group.getJSONArray("values") != null && !group.getJSONArray("values").isEmpty() && integer(group, "max", 1) >= 1, "参数分组限制无效");
                        validateCodes(group.getJSONArray("values"), dictionary == null ? null : codes, "参数分组引用了当前槽不存在的编码");
                    }
                    allowedCombinations(slot, dictionary == null ? null : codes);
                }
                require(content, "型号结构不能仅包含分隔符");
            }
            Map<String, Set<String>> expected = new LinkedHashMap<>();
            references.forEach((type, values) -> expected.put(type, new LinkedHashSet<>(values)));
            require(used.equals(expected), "全部分支的参数值与型号关联不一致，请刷新后核对");
            return null;
        } catch (RuntimeException error) {
            return error instanceof IllegalArgumentException && error.getMessage() != null ? error.getMessage() : "选型结构格式错误";
        }
    }

    /** values 按字典值 ID 提供数值；型号编码仅使用已校验的槽映射或字典编码。 */
    public static String compose(JSONObject schema, String variantKey, JSONObject choices, Map<String, JSONObject> values) {
        JSONArray incoming = schema.getJSONArray("variants");
        require(incoming != null && !incoming.isEmpty(), "型号参数为空");
        int branch = -1;
        for (int i = 0; i < incoming.size(); i++)
            if (variantKey != null && variantKey.equals(incoming.getJSONObject(i).getString("key"))) branch = i;
        require(branch >= 0, "请选择有效的型号分支");
        JSONArray roots = new JSONArray();
        for (Object item : incoming) roots.add(((JSONObject) item).getJSONArray("tokens"));
        Map<String, List<String>> references = new LinkedHashMap<>();
        values.forEach((id, value) -> references.computeIfAbsent(value.getString("paramTypeId"), ignored -> new ArrayList<>()).add(id));
        schema = read(JSON.toJSONString(roots.size() == 1 ? roots.get(0) : roots,
                SerializerFeature.WriteMapNullValue), references, Map.of());
        variantKey = "branch_" + branch;
        JSONObject variant = null;
        for (Object item : schema.getJSONArray("variants")) if (variantKey != null && variantKey.equals(((JSONObject) item).getString("key"))) variant = (JSONObject) item;
        require(variant != null, "请选择有效的型号分支");
        require(choices != null, "请选择选型参数");
        Set<String> keys = new LinkedHashSet<>();
        StringBuilder model = new StringBuilder();
        for (Object item : variant.getJSONArray("tokens")) {
            if (item instanceof String literal) { model.append(literal); continue; }
            JSONObject slot = (JSONObject) item;
            requireDictionarySlot(slot);
            String key = slot.getString("key"), label = slot.getString("label");
            keys.add(key);
            Object choice = choices.get(key);
            List<String> codes = new ArrayList<>();
            List<List<String>> combinations = resolveSlotCombinations(slot, values);
            require(!(choice instanceof JSONObject), label + "只能选择种类管理中的值");
            Set<String> chosen = new LinkedHashSet<>();
            boolean multiple = "multiple".equals(slot.getString("selection"));
            if (choice instanceof JSONArray array) {
                require(multiple, label + "只能选择一个值");
                for (Object value : array) require(chosen.add(id(value)), label + "不能重复选择");
            } else if (choice != null && !choice.toString().isEmpty()) chosen.add(id(choice));
            int min = integer(slot, "minSelect", Boolean.TRUE.equals(slot.getBoolean("optional")) ? 0 : 1);
            int max = integer(slot, "maxSelect", multiple ? slot.getJSONArray("valueIds").size() : 1);
            require(chosen.size() >= min && chosen.size() <= max, "请选择正确数量的" + label);
            for (Object itemId : slot.getJSONArray("valueIds")) {
                String valueId = id(itemId);
                if (!chosen.remove(valueId)) continue;
                JSONObject value = values.get(valueId);
                require(value != null && slot.getString("t").equals(value.getString("paramTypeId")), label + "的参数值不属于当前种类");
                codes.add(resolveCode(slot, valueId, value.getString("paramValue")));
            }
            require(chosen.isEmpty(), label + "包含当前参数槽不允许的值");
            JSONArray groups = slot.getJSONArray("groups");
            if (groups != null) for (Object groupValue : groups) {
                JSONObject group = (JSONObject) groupValue;
                long count = codes.stream().filter(code -> group.getJSONArray("values").contains(code)).count();
                require(count <= integer(group, "max", 1), label + "同一信号组不能重复组合");
            }
            if (combinations != null) {
                Set<String> chosenCodes = new LinkedHashSet<>(codes);
                require(chosenCodes.size() == codes.size(), label + "不能重复选择相同编码");
                List<String> matched = null;
                for (List<String> combination : combinations) {
                    if (combination.size() == chosenCodes.size() && chosenCodes.containsAll(combination)) {
                        matched = combination;
                        break;
                    }
                }
                require(matched != null, label + "不属于允许的参数组合");
                codes.clear();
                codes.addAll(matched);
            }
            String output = String.join("", codes);
            if (!output.isEmpty() && slot.getString("prefix") != null) model.append(slot.getString("prefix"));
            model.append(output);
        }
        require(keys.containsAll(choices.keySet()), "提交包含不属于当前分支的参数槽");
        require(model.length() > 0 && model.length() <= 1000, "组合后的型号为空或过长");
        return model.toString();
    }

    public static List<Kv> displayVariants(String source, Map<String, String> types, Map<String, JSONObject> values) {
        List<Kv> result = new ArrayList<>();
        Map<String, List<String>> references = new LinkedHashMap<>();
        values.forEach((id, value) -> references.computeIfAbsent(value.getString("paramTypeId"), ignored -> new ArrayList<>()).add(id));
        for (Object item : dictionaryView(source, references, types).getJSONArray("variants")) {
            JSONObject variant = (JSONObject) item;
            List<Kv> tokens = new ArrayList<>(), selections = new ArrayList<>();
            int position = 0;
            for (Object token : variant.getJSONArray("tokens")) {
                if (token instanceof String literal) { tokens.add(Kv.by("kind", "lit").set("text", literal)); continue; }
                JSONObject slot = (JSONObject) token;
                String typeId = slot.getString("t");
                String label = types.get(typeId);
                if (slot.getString("prefix") != null) tokens.add(Kv.by("kind", "lit").set("text", slot.getString("prefix")));
                tokens.add(Kv.by("kind", "param").set("position", ++position).set("typeName", label).set("typeId", slot.getString("t")).set("slotKey", slot.getString("key")));
                List<Kv> options = new ArrayList<>();
                JSONArray ids = slot.getJSONArray("valueIds");
                if (ids != null) for (Object id : ids) {
                    JSONObject value = values.get(id.toString());
                    if (value != null && typeId.equals(value.getString("paramTypeId"))) {
                        String paramValue = value.getString("paramValue");
                        paramValue = paramValue == null ? "" : paramValue;
                        String code = resolveCode(slot, id.toString(), paramValue);
                        String description = value.getString("description");
                        options.add(Kv.by("value", code).set("paramValue", paramValue).set("description", description));
                    }
                }
                selections.add(Kv.by("position", position).set("typeName", label).set("typeId", slot.getString("t")).set("slotKey", slot.getString("key")).set("values", options).set("optional", Boolean.TRUE.equals(slot.getBoolean("optional"))).set("selection", slot.getString("selection"))
                        .set("prefix", slot.getString("prefix"))
                        .set("minSelect", slot.getInteger("minSelect")).set("maxSelect", slot.getInteger("maxSelect"))
                        .set("groups", slot.getJSONArray("groups"))
                        .set("allowedCombinations", slot.getJSONArray("allowedCombinations")));
            }
            result.add(Kv.by("key", variant.getString("key")).set("name", variant.getString("name"))
                    .set("formatKey", ProdModelFormats.key(variant.getJSONArray("tokens"), types))
                    .set("tokens", tokens).set("selections", selections));
        }
        return result;
    }

    public static String id(Object value) {
        require(value != null && value.toString().matches("[1-9][0-9]*"), "参数ID格式错误");
        try { return Long.toString(Long.parseLong(value.toString())); }
        catch (NumberFormatException error) { throw new IllegalArgumentException("参数ID超出范围"); }
    }

    /** 编码保留原文；字典值对应 varchar(50)，槽内映射随 model_desc 的 TEXT 总字节数校验。 */
    public static String resolveCode(JSONObject slot, String valueId, String dictionaryValue) {
        JSONObject mappings = validateCodeMappings(slot);
        if (mappings != null && mappings.containsKey(valueId)) return (String) mappings.get(valueId);
        String code = dictionaryValue == null ? "" : dictionaryValue;
        require(code.codePointCount(0, code.length()) <= 50, "参数值不能超过50个字符");
        return code;
    }

    private static JSONObject validateCodeMappings(JSONObject slot) {
        if (!slot.containsKey("codes")) return null;
        require(slot.containsKey("t") && slot.get("valueIds") instanceof JSONArray, "编码映射只能用于已关联字典值的参数槽");
        require(slot.get("codes") instanceof JSONObject, "参数编码映射必须为对象");
        JSONObject mappings = (JSONObject) slot.get("codes");
        JSONArray valueIds = slot.getJSONArray("valueIds");
        for (String valueId : mappings.keySet()) {
            require(valueIds.contains(valueId), "参数编码映射引用了当前槽不存在的值");
            Object code = mappings.get(valueId);
            require(code instanceof String, "参数编码映射必须为字符串");
        }
        return mappings;
    }

    /** 返回按目录顺序保存的允许组合；未配置时为 null，未提供字典时仅检查结构及数量约束。 */
    public static List<List<String>> allowedCombinations(JSONObject slot, Collection<String> availableCodes) {
        if (!slot.containsKey("allowedCombinations")) return null;
        require(slot.containsKey("t") && !slot.containsKey("input") && slot.get("valueIds") instanceof JSONArray,
                "允许组合只能用于已关联字典值的枚举参数槽");
        require("multiple".equals(slot.getString("selection"))
                        && (!slot.containsKey("allowCustom") || Boolean.FALSE.equals(slot.get("allowCustom"))),
                "允许组合仅支持多选参数且不能允许自定义编码");
        require(slot.get("allowedCombinations") instanceof JSONArray, "允许组合必须为二维编码数组");
        JSONArray combinations = slot.getJSONArray("allowedCombinations");
        require(!combinations.isEmpty() && combinations.size() <= 200, "允许组合数量须为1至200组");
        int valueCount = slot.getJSONArray("valueIds").size();
        require(valueCount >= 1 && valueCount <= 200, "每个枚举参数须选择1至200个值");
        require(!slot.containsKey("optional") || slot.get("optional") instanceof Boolean, "允许组合的可选标记必须为布尔值");
        boolean optional = Boolean.TRUE.equals(slot.get("optional"));
        int min = integer(slot, "minSelect", optional ? 0 : 1);
        int max = integer(slot, "maxSelect", valueCount);
        require(min >= 0 && max >= Math.max(1, min) && max <= valueCount, "参数多选数量范围无效");
        Set<String> available = availableCodes == null ? null : new LinkedHashSet<>(availableCodes);
        require(available == null || (available.size() == availableCodes.size() && available.size() == valueCount),
                "允许组合的参数槽必须完整关联且不能包含相同编码的多个值");
        require(!slot.containsKey("groups") || slot.get("groups") instanceof JSONArray, "参数分组格式错误");
        JSONArray groups = slot.getJSONArray("groups");
        if (groups != null) for (Object item : groups) {
            require(item instanceof JSONObject, "参数分组格式错误");
            JSONObject group = (JSONObject) item;
            require(group.get("values") instanceof JSONArray, "参数分组格式错误");
            validateCodes(group.getJSONArray("values"), available, "参数分组引用了当前槽不存在的编码");
            require(integer(group, "max", 1) >= 1, "参数分组限制无效");
        }
        Set<Set<String>> seen = new LinkedHashSet<>();
        List<List<String>> result = new ArrayList<>();
        for (Object item : combinations) {
            require(item instanceof JSONArray, "每个允许组合必须为编码数组");
            JSONArray combination = (JSONArray) item;
            require(combination.size() >= min && combination.size() <= max, "允许组合不符合参数多选数量范围");
            require(!combination.isEmpty() || (optional && min == 0), "空组合仅适用于允许留空的参数槽");
            Set<String> codes = new LinkedHashSet<>();
            for (Object itemCode : combination) {
                require(itemCode instanceof String, "允许组合的编码必须为字符串");
                String code = (String) itemCode;
                require(codes.add(code), "允许组合内不能重复编码");
                require(available == null || available.contains(code), "允许组合引用了当前槽不存在的编码");
            }
            require(seen.add(codes), "允许组合不能重复，编码顺序不同仍视为同一组合");
            if (groups != null) for (Object groupItem : groups) {
                JSONObject group = (JSONObject) groupItem;
                long count = codes.stream().filter(code -> group.getJSONArray("values").contains(code)).count();
                require(count <= integer(group, "max", 1), "允许组合超出同一信号组的数量限制");
            }
            result.add(List.copyOf(codes));
        }
        return List.copyOf(result);
    }

    private static List<List<String>> resolveSlotCombinations(JSONObject slot, Map<String, JSONObject> values) {
        if (!slot.containsKey("allowedCombinations")) return null;
        require(slot.containsKey("t") && slot.get("valueIds") instanceof JSONArray, "允许组合只能用于已关联字典值的枚举参数槽");
        List<String> codes = new ArrayList<>();
        Set<String> ids = new LinkedHashSet<>();
        for (Object item : slot.getJSONArray("valueIds")) {
            String valueId = id(item);
            require(ids.add(valueId), "允许组合的参数槽不能重复引用参数值");
            JSONObject value = values.get(valueId);
            require(value != null && slot.getString("t").equals(value.getString("paramTypeId")), "允许组合的参数值不存在或已变更种类");
            codes.add(resolveCode(slot, valueId, value.getString("paramValue")));
        }
        return allowedCombinations(slot, codes);
    }

    private static void validateCodes(JSONArray codes, Set<String> available, String message) {
        require(codes != null && !codes.isEmpty() && codes.size() <= 200, message);
        Set<String> seen = new LinkedHashSet<>();
        for (Object code : codes) require(code instanceof String && seen.add((String) code)
                && (available == null || available.contains(code)), message);
    }

    private static int integer(JSONObject object, String key, int fallback) {
        Object value = object.get(key);
        if (value == null) return fallback;
        require(value.toString().matches("[0-9]+"), "数量与长度限制必须是非负整数");
        try { return Integer.parseInt(value.toString()); }
        catch (NumberFormatException error) { throw new IllegalArgumentException("数量与长度限制超出范围"); }
    }
    private static boolean validKey(String key) { return key != null && key.matches("[A-Za-z][A-Za-z0-9_-]{0,49}"); }
    private static boolean textLength(String text, int max) { return text != null && !text.isBlank() && text.length() <= max; }
    private static void require(boolean condition, String message) { if (!condition) throw new IllegalArgumentException(message); }
}
