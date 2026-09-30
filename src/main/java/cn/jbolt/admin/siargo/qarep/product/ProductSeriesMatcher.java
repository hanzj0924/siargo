package cn.jbolt.admin.siargo.qarep.product;

import cn.jbolt.admin.siargo.prodmodel.ProdModelTokens;
import cn.jbolt.admin.siargo.prodmodel.ProdModelSelectionSchema;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 仅按 model_desc 中的型号识别段提供导入建议；系列名称只用于展示。 */
public final class ProductSeriesMatcher {
    private static final int MAX_IDENTITIES = 10000;
    /** 只有这些型号身份段允许位于横杠之后；普通规格段仍终止身份解析。 */
    private static final Set<String> IDENTITY_SLOT_KEYS = Set.of("version", "productSequence", "productFamily", "diameter", "valve");
    private ProductSeriesMatcher() {}

    public record Parameter(String typeId, String value, boolean active) {}
    public record Series(String id, String name, Integer prodType, String typeName,
                         String modelDesc, List<String> identities, List<String> truncationPrefixes, List<String> completePatterns) {
        public Series(String id, String name, Integer prodType, String typeName,
                      String modelDesc, Map<String, Parameter> params) {
            this(id, name, prodType, typeName, modelDesc, compileTemplate(modelDesc, params), compileCompletePatterns(modelDesc, params));
        }
        private Series(String id, String name, Integer prodType, String typeName, String modelDesc, Template template, List<String> completePatterns) {
            this(id, name, prodType, typeName, modelDesc, template.identities(), template.truncationPrefixes(), completePatterns);
        }
        public Series(String id, String name, Integer prodType, String typeName, String modelDesc, List<String> identities, List<String> truncationPrefixes) {
            this(id, name, prodType, typeName, modelDesc, identities, truncationPrefixes, List.of());
        }
        public Series {
            identities = List.copyOf(identities);
            truncationPrefixes = List.copyOf(truncationPrefixes);
            completePatterns = List.copyOf(completePatterns);
        }
    }
    private record Template(List<String> identities, List<String> truncationPrefixes) {}
    private record Hit(int start, int end, int score) {}
    public record Candidate(Series series, int modelScore, int descriptionScore) {
        public int score() { return Math.max(modelScore, descriptionScore); }
    }
    public record Match(Candidate selected, String status, String reason, List<Candidate> candidates) {}

    public static String normalize(String input) {
        return normalizeText(input).replaceAll("(?U)\\s+", "");
    }

    private static String normalizeText(String input) {
        if (input == null) return "";
        StringBuilder normalized = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if ((c >= 'Ａ' && c <= 'Ｚ') || (c >= 'ａ' && c <= 'ｚ') || (c >= '０' && c <= '９')) c -= 0xfee0;
            if (c >= 'a' && c <= 'z') c -= 32;
            normalized.append(c);
        }
        return normalized.toString().replace('－', '-').replace('—', '-').replace('–', '-').replace('−', '-');
    }

    /** 只读取模板中显式引用的字典值，不从 descriptions 文案生成型号。 */
    public static Set<Long> referencedValueIds(String modelDesc) {
        Set<Long> ids = new LinkedHashSet<>();
        for (JSONArray branch : branches(modelDesc)) for (Object part : branch) {
            if (part instanceof JSONObject slot && slot.get("valueIds") instanceof JSONArray values)
                for (Object value : values) ids.add(Long.parseLong(value.toString()));
        }
        return ids;
    }

    /** 展开型号身份段；横杠后的版本等身份段继续读取，流量、接口等后续规格不要求填齐。 */
    public static List<String> identities(String modelDesc, Map<String, Parameter> params) {
        return compileTemplate(modelDesc, params).identities();
    }

    private static Template compileTemplate(String modelDesc, Map<String, Parameter> params) {
        Set<String> result = new LinkedHashSet<>();
        Set<String> prefixes = new LinkedHashSet<>();
        branchLoop: for (JSONArray branch : branches(modelDesc)) {
            List<String> variants = new ArrayList<>(List.of(""));
            Set<String> branchPrefixes = new LinkedHashSet<>();
            for (Object part : branch) {
                if (part instanceof String literal) {
                    // 例如 GD G 的空白将型号和公称流量分开；固定 MF5806-G 的内部横线保留。
                    String[] chunks = literal.split("(?U)\\s+", 2);
                    String text = normalize(chunks[0]);
                    variants.replaceAll(prefix -> prefix + text);
                    if (chunks.length > 1) break;
                } else if (part instanceof JSONObject slot) {
                    String slotKey = slot.getString("key");
                    if (variants.stream().allMatch(prefix -> prefix.contains("-"))
                            && (slotKey == null || !IDENTITY_SLOT_KEYS.contains(slotKey))) break;
                    if (slot.containsKey("input")) { variants.clear(); break; }
                    Set<String> values = new LinkedHashSet<>();
                    List<String> slotCodes = new ArrayList<>();
                    if (slot.get("valueIds") instanceof JSONArray ids) for (Object id : ids) {
                        Parameter parameter = params.get(id.toString());
                        if (parameter != null && parameter.active() && parameter.typeId().equals(slot.getString("t"))) {
                            try {
                                slotCodes.add(ProdModelSelectionSchema.resolveCode(slot, id.toString(), parameter.value()));
                            } catch (IllegalArgumentException invalidMapping) {
                                continue branchLoop;
                            }
                        } else if (slot.containsKey("allowedCombinations")) {
                            continue branchLoop;
                        }
                    }
                    try {
                        List<List<String>> combinations = ProdModelSelectionSchema.allowedCombinations(slot, slotCodes);
                        if (combinations != null) {
                            for (List<String> combination : combinations) values.add(normalize(String.join("", combination)));
                        } else {
                            for (String code : slotCodes) values.add(normalize(code));
                            if (slot.getBooleanValue("optional")) values.add("");
                        }
                    } catch (IllegalArgumentException invalidCombination) {
                        continue branchLoop;
                    }
                    if (values.isEmpty() || (long) variants.size() * values.size() > MAX_IDENTITIES) {
                        variants.clear(); break;
                    }
                    List<String> expanded = new ArrayList<>();
                    for (String prefix : variants) for (String value : values) expanded.add(prefix + value);
                    variants = expanded;
                }
                // 只允许模板节点边界的完整数字型号截断；固定 FS4001E 不可截成 FS4001。
                for (String variant : variants) if (variant.matches("[A-Z]+[0-9]{4,}")) branchPrefixes.add(variant);
            }
            for (String variant : variants) {
                String identity = variant.replaceAll("-+$", "");
                if (!identity.isEmpty()) {
                    result.add(identity);
                    for (String prefix : branchPrefixes) if (identity.startsWith(prefix) && identity.length() > prefix.length()
                            && !Character.isDigit(identity.charAt(prefix.length()))) prefixes.add(prefix);
                }
            }
        }
        return new Template(result.stream().sorted().toList(), prefixes.stream().sorted().toList());
    }

    private static List<JSONArray> branches(String modelDesc) {
        try { return ProdModelTokens.branches(modelDesc); }
        catch (IllegalArgumentException invalidTemplate) { return List.of(); }
    }

    /** 同前缀分支消歧使用完整参数结构；无法完整解释的旧自由输入结构不参与推断。 */
    private static List<String> compileCompletePatterns(String modelDesc, Map<String, Parameter> params) {
        List<String> patterns = new ArrayList<>();
        try {
            for (JSONArray branch : branches(modelDesc)) {
                StringBuilder pattern = new StringBuilder("(?<![A-Z0-9-])");
                for (Object part : branch) {
                    if (part instanceof String literal) {
                        pattern.append(Pattern.quote(normalizeText(literal)));
                        continue;
                    }
                    JSONObject slot = (JSONObject) part;
                    if (slot.containsKey("input") || !(slot.get("valueIds") instanceof JSONArray ids) || ids.isEmpty()) return List.of();
                    List<String> codes = new ArrayList<>();
                    for (Object id : ids) {
                        Parameter parameter = params.get(id.toString());
                        if (parameter == null || !parameter.active() || !parameter.typeId().equals(slot.getString("t"))) return List.of();
                        codes.add(ProdModelSelectionSchema.resolveCode(slot, id.toString(), parameter.value()));
                    }
                    List<List<String>> combinations = ProdModelSelectionSchema.allowedCombinations(slot, codes);
                    List<String> alternatives = new ArrayList<>();
                    if (combinations != null) for (List<String> combination : combinations) alternatives.add(String.join("", combination));
                    else {
                        if ("multiple".equals(slot.getString("selection"))) return List.of();
                        alternatives.addAll(codes);
                        if (slot.getBooleanValue("optional")) alternatives.add("");
                    }
                    if (alternatives.isEmpty()) return List.of();
                    String prefix = slot.getString("prefix") == null ? "" : slot.getString("prefix");
                    pattern.append("(?:").append(String.join("|", alternatives.stream().distinct()
                            .map(code -> Pattern.quote(normalizeText(code.isEmpty() ? "" : prefix + code))).toList())).append(')');
                }
                pattern.append("(?![A-Z0-9-])");
                patterns.add(pattern.toString());
            }
        } catch (IllegalArgumentException invalid) { return List.of(); }
        return patterns;
    }

    private static Candidate completeMatch(String source, List<Candidate> tied) {
        if (tied.stream().anyMatch(candidate -> candidate.series().completePatterns().isEmpty())) return null;
        Set<String> shared = new LinkedHashSet<>(tied.get(0).series().identities());
        for (Candidate candidate : tied) shared.retainAll(candidate.series().identities());
        String normalized = normalizeText(source);
        if (shared.stream().noneMatch(identity -> boundaryPattern(identity).matcher(normalized).find())) return null;
        List<Candidate> matched = tied.stream().filter(candidate -> candidate.series().completePatterns().stream()
                .anyMatch(pattern -> Pattern.compile(pattern).matcher(normalized).find())).toList();
        return matched.size() == 1 ? matched.get(0) : null;
    }

    /** 100/98 为完整识别段，95 为完整四位数字型号的模板截断前缀。 */
    public static int score(String source, Series series) {
        return sourceScores(source, List.of(series)).get(series);
    }

    private static List<Hit> hits(String value, Series series) {
        List<Hit> found = new ArrayList<>();
        for (String identity : series.identities()) {
            Matcher matcher = boundaryPattern(identity).matcher(value);
            while (matcher.find()) found.add(new Hit(matcher.start(), matcher.end(), value.equals(identity) ? 100 : 98));
        }
        for (String prefix : series.truncationPrefixes()) {
            Matcher matcher = boundaryPattern(prefix).matcher(value);
            while (matcher.find()) found.add(new Hit(matcher.start(), matcher.end(), 95));
        }
        return found;
    }

    private static Pattern boundaryPattern(String identity) {
        return Pattern.compile("(?<![A-Z0-9])" + Pattern.quote(identity) + "(?![A-Z0-9])");
    }

    private static Map<Series, Integer> sourceScores(String source, List<Series> seriesList) {
        String value = normalizeText(source).replaceAll("(?U)\\s+", " ").strip();
        Map<Series, List<Hit>> evidence = new LinkedHashMap<>();
        Map<Integer, Integer> longestEnd = new LinkedHashMap<>();
        for (Series series : seriesList) {
            List<Hit> found = hits(value, series);
            evidence.put(series, found);
            for (Hit hit : found) if (hit.score() >= 98) longestEnd.merge(hit.start(), hit.end(), Math::max);
        }
        Map<Series, Integer> scores = new LinkedHashMap<>();
        for (Map.Entry<Series, List<Hit>> entry : evidence.entrySet()) {
            int best = 0;
            for (Hit hit : entry.getValue()) {
                // 只压制同一次命中的较短段；备注另一个位置的系列证据仍保留。
                if (hit.end() < longestEnd.getOrDefault(hit.start(), hit.end())) continue;
                best = Math.max(best, hit.score());
            }
            scores.put(entry.getKey(), best);
        }
        return scores;
    }

    public static Match match(String model, String description, List<Series> seriesList) {
        List<Candidate> candidates = new ArrayList<>();
        Map<Series, Integer> modelScores = sourceScores(model, seriesList);
        Map<Series, Integer> descriptionScores = sourceScores(description, seriesList);
        for (Series series : seriesList) {
            Candidate candidate = new Candidate(series, modelScores.get(series), descriptionScores.get(series));
            if (candidate.score() > 0) candidates.add(candidate);
        }
        boolean descriptionFirst = candidates.stream().anyMatch(candidate -> candidate.descriptionScore() > 0);
        candidates.sort(Comparator.<Candidate>comparingInt(candidate -> sourceScore(candidate, descriptionFirst)).reversed()
                .thenComparing(Comparator.comparingInt(Candidate::score).reversed())
                .thenComparing(candidate -> candidate.series().id()));
        List<Candidate> top = List.copyOf(candidates.subList(0, Math.min(3, candidates.size())));
        if (candidates.isEmpty()) return new Match(null, "unmatched", "未识别到产品型号，请手动选择!", top);
        Candidate first = candidates.get(0);
        if (candidates.size() > 1 && sourceScore(first, descriptionFirst) == sourceScore(candidates.get(1), descriptionFirst)) {
            List<Candidate> tied = candidates.stream().filter(candidate -> sourceScore(candidate, descriptionFirst) == sourceScore(first, descriptionFirst)).toList();
            Candidate complete = completeMatch(descriptionFirst ? description : model, tied);
            if (complete != null) return new Match(complete, "matched", descriptionFirst ? "备注按完整 model_desc 唯一匹配，备注优先" : "型号按完整 model_desc 唯一区分产品分支", top);
            return new Match(null, "ambiguous", descriptionFirst ? "备注含多个同等匹配的型号系列，请人工核对" : "型号对应多个系列，请人工核对", top);
        }
        return new Match(first, "matched", descriptionFirst ? "备注按 model_desc 唯一匹配，备注优先" : "型号按 model_desc 唯一匹配", top);
    }

    private static int sourceScore(Candidate candidate, boolean description) {
        return description ? candidate.descriptionScore() : candidate.modelScore();
    }
}
