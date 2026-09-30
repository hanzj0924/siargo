package cn.jbolt.admin.siargo.prodmodel;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/** 技术参数的只读表格布局：明确分组才合并，数值及备注保持原文。 */
final class ProdTechnicalTable {
    private static final Pattern FOOTNOTE_MARK = Pattern.compile("(?<=[\\p{IsHan}])[¹²³⁴⁵⁶⁷⁸⁹]+(?=[）)]|$)");
    private ProdTechnicalTable() {}

    static Kv build(List<Record> source) {
        List<Kv> rows = new ArrayList<>();
        boolean hasRemarks = false;
        if (source != null) for (Record record : source) {
            if (record == null) continue;
            String remark = record.getStr("remark");
            rows.add(Kv.by("paramName", displayName(record.getStr("paramName")))
                    .set("paramValue", record.getStr("paramValue")).set("unit", record.getStr("unit"))
                    .set("remark", remark).set("grouped", false).set("groupFirst", false).set("rowSpan", 1));
            hasRemarks |= remark != null && !remark.isEmpty();
        }
        return finish(rows, hasRemarks, new ArrayList<>());
    }

    /** 目录分支按列展示；单元格范围由目录元数据明确指定，不解析数值中的斜杠。 */
    static Kv build(List<Kv> columns, List<List<Record>> sources) {
        if (columns.size() != sources.size()) throw new IllegalArgumentException("技术参数列与数据数量不一致");
        Map<String, String> explicitAliases = new LinkedHashMap<>();
        for (List<Record> source : sources) {
            Map<String, Integer> occurrences = new LinkedHashMap<>();
            for (Record record : source) {
                String alias = rowAlias(record, occurrences), key = text(record.getStr("catalogRowKey"));
                if (!key.isEmpty()) explicitAliases.merge(alias, "key:" + key, (left, right) -> left.equals(right) ? left : "");
            }
        }
        Map<String, Kv> aligned = new LinkedHashMap<>();
        Map<String, List<Record>> records = new LinkedHashMap<>();
        for (int column = 0; column < sources.size(); column++) {
            Map<String, Integer> occurrences = new LinkedHashMap<>(), keyOccurrences = new LinkedHashMap<>();
            for (Record record : sources.get(column)) {
                String alias = rowAlias(record, occurrences), catalogKey = text(record.getStr("catalogRowKey"));
                String key = catalogKey.isEmpty() ? explicitAliases.getOrDefault(alias, alias) : "key:" + catalogKey;
                if (key.isEmpty()) key = alias;
                int duplicate = keyOccurrences.merge(key, 1, Integer::sum);
                if (duplicate > 1) key += "#duplicate:" + duplicate;
                Kv row = aligned.get(key);
                Integer rank = record.getInt("sortRank");
                if (row == null) {
                    row = Kv.by("paramName", displayName(record.getStr("paramName"))).set("rowKey", key)
                            .set("sortRank", rank == null ? Integer.MAX_VALUE : rank).set("order", aligned.size())
                            .set("grouped", false).set("groupFirst", false).set("rowSpan", 1);
                    aligned.put(key, row);
                    records.put(key, new ArrayList<>(java.util.Collections.nCopies(columns.size(), null)));
                } else if (rank != null && rank < row.getInt("sortRank")) row.set("sortRank", rank);
                records.get(key).set(column, record);
            }
        }
        List<Kv> rows = new ArrayList<>(aligned.values());
        rows.sort(Comparator.comparingInt((Kv row) -> row.getInt("sortRank")).thenComparingInt(row -> row.getInt("order")));
        boolean hasRemarks = false;
        for (Kv row : rows) {
            List<Record> original = records.get(row.getStr("rowKey"));
            List<String> units = original.stream().filter(Objects::nonNull).map(record -> text(record.getStr("unit"))).distinct().toList();
            String commonUnit = units.size() == 1 ? units.get(0) : "";
            List<Kv> cells = new ArrayList<>();
            for (int index = 0; index < original.size();) {
                Record record = original.get(index);
                int end = index + 1;
                while (end < original.size() && sameCatalogCell(record, original.get(end))) end++;
                String remark = record == null ? "" : record.getStr("remark");
                cells.add(Kv.by("value", record == null ? "" : record.getStr("paramValue"))
                        .set("unit", record == null ? "" : record.getStr("unit")).set("remark", remark)
                        .set("showUnit", units.size() > 1).set("missing", record == null).set("colSpan", end - index));
                hasRemarks |= remark != null && !remark.isEmpty();
                index = end;
            }
            row.set("cells", cells).set("unit", commonUnit)
                    .set("paramValue", cells.isEmpty() ? "" : cells.get(0).get("value"))
                    .set("remark", cells.isEmpty() ? "" : cells.get(0).get("remark"));
            row.remove("sortRank"); row.remove("order");
        }
        return finish(rows, hasRemarks, new ArrayList<>(columns));
    }

    private static String rowAlias(Record record, Map<String, Integer> occurrences) {
        String name = text(record.getStr("paramName"));
        return "name:" + name + "#" + occurrences.merge(name, 1, Integer::sum);
    }

    private static boolean sameCatalogCell(Record left, Record right) {
        if (left == null || right == null) return false;
        String key = text(left.getStr("catalogCellKey"));
        return !key.isEmpty() && key.equals(text(right.getStr("catalogCellKey")))
                && Objects.equals(left.getStr("paramValue"), right.getStr("paramValue"))
                && Objects.equals(left.getStr("unit"), right.getStr("unit"))
                && Objects.equals(left.getStr("remark"), right.getStr("remark"));
    }

    private static String text(String value) { return value == null ? "" : value; }

    /** 每个独立型号可含若干 PDF 规格子列；未指定列的记录进入该型号的默认列。 */
    static Kv buildCatalog(List<Kv> models, List<Record> source) {
        List<Kv> columns = new ArrayList<>();
        List<List<Record>> sources = new ArrayList<>();
        for (Kv model : models) {
            String modelId = model.getStr("modelId");
            Map<String, List<Record>> byColumn = new LinkedHashMap<>();
            for (Record record : source) if (modelId.equals(record.getStr("modelId")))
                byColumn.computeIfAbsent(text(record.getStr("catalogColumnKey")), ignored -> new ArrayList<>()).add(record);
            if (byColumn.isEmpty()) byColumn.put("", new ArrayList<>());
            List<Kv> modelColumns = new ArrayList<>();
            for (var entry : byColumn.entrySet()) {
                List<Record> records = entry.getValue();
                int rank = records.stream().map(record -> record.getInt("catalogColumnSort")).filter(Objects::nonNull).min(Integer::compareTo).orElse(0);
                String title = records.stream().map(record -> record.getStr("catalogColumnLabel"))
                        .filter(label -> label != null && !label.isBlank()).findFirst().orElse(model.getStr("title"));
                modelColumns.add(Kv.by("modelId", modelId).set("key", entry.getKey()).set("title", title)
                        .set("sort", rank).set("records", records));
            }
            modelColumns.sort(Comparator.comparingInt(column -> column.getInt("sort")));
            for (Kv column : modelColumns) {
                sources.add(column.getAs("records"));
                column.remove("records"); column.remove("sort"); columns.add(column);
            }
        }
        return build(columns, sources);
    }

    private static Kv finish(List<Kv> rows, boolean hasRemarks, List<Kv> columns) {
        boolean hasGroups = false;
        int start = 0;
        while (start < rows.size()) {
            String category = category(rows.get(start).getStr("paramName"));
            if (category == null) { start++; continue; }
            int end = start + 1;
            while (end < rows.size() && category.equals(category(rows.get(end).getStr("paramName")))) end++;
            if (end - start >= 2) {
                hasGroups = true;
                for (int i = start; i < end; i++) {
                    Kv row = rows.get(i);
                    String fullName = row.getStr("paramName");
                    row.set("paramName", fullName.substring(fullName.indexOf('－') + 1))
                            .set("grouped", true).set("groupFirst", i == start);
                    if (i == start) row.set("category", category).set("rowSpan", end - start);
                }
            }
            start = end;
        }
        return Kv.by("hasGroups", hasGroups).set("hasRemarks", hasRemarks).set("rows", rows)
                .set("columns", columns).set("hasMultipleColumns", columns.size() > 1);
    }

    /** 仅去掉中文说明结尾的脚注编号；I²C、m²/m³、T₆₃ 等技术记号保持原样。 */
    static String displayName(String name) {
        return name == null ? "" : FOOTNOTE_MARK.matcher(name).replaceAll("");
    }

    private static String category(String name) {
        int separator = name.indexOf('－');
        if (separator <= 0 || separator == name.length() - 1) return null;
        String prefix = name.substring(0, separator);
        return prefix.isBlank() || name.substring(separator + 1).isBlank() ? null : prefix;
    }
}
