package cn.jbolt.admin.siargo.qarep.uploadImport;

import cn.jbolt.admin.siargo.qarep.product.ProductSeriesService;
import cn.jbolt.admin.siargo.qarep.product.ReportProductInput;
import cn.jbolt.admin.siargo.qarep.siargoconst.QarepConst;
import cn.jbolt.common.storage.SiargoStorage;
import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.*;

/** Excel 只解析预填数据；两类模板统一返回独立产品卡片。 */
public class ExcelService {
    private static final Log LOG = Log.getLog(ExcelService.class);
    private static final Pattern NUMBER_SUFFIX = Pattern.compile("^(.*?)([0-9]+)$");
    @Inject private ProductSeriesService seriesService;

    public Map<String, Object> processExcelFile(File file) throws Exception {
        try (FileInputStream input = new FileInputStream(file); Workbook workbook = WorkbookFactory.create(input)) {
            return processWorkbook(workbook);
        } finally {
            if (file != null && file.exists()) {
                try { SiargoStorage.forReportResources().deleteFile(file.toPath()); }
                catch (Exception ex) { LOG.warn("删除导入临时文件失败", ex); }
            }
        }
    }

    Map<String, Object> processWorkbook(Workbook workbook) {
        if (workbook.getNumberOfSheets() == 0) return Map.of();
        DataFormatter formatter = new DataFormatter();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
            Sheet sheet = workbook.getSheetAt(index);
            if (sheet.getSheetName().startsWith("检定记录")) return processRecordSheet(sheet, formatter, evaluator);
        }
        Sheet sheet = workbook.getSheetAt(0);
        Row header = sheet.getRow(0);
        if (header == null) return Map.of();
        Map<Integer, String> headers = new LinkedHashMap<>();
        for (int column = 0; column < header.getLastCellNum(); column++) {
            String name = cellValue(header.getCell(column), formatter, evaluator);
            if (!name.isBlank()) headers.put(column, name);
        }
        if (!headers.containsValue("型号")) throw new IllegalArgumentException("Excel 缺少型号列");
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int index = 1; index <= sheet.getLastRowNum(); index++) {
            Row row = sheet.getRow(index);
            if (row == null) continue;
            Map<String, Object> values = new LinkedHashMap<>();
            for (Map.Entry<Integer, String> column : headers.entrySet()) {
                values.put(column.getValue(), cellValue(row.getCell(column.getKey()), formatter, evaluator));
            }
            if (values.values().stream().anyMatch(value -> !text(value).isBlank())) {
                values.put("_row", index + 1);
                rows.add(values);
            }
        }
        return processExcelData(rows);
    }

    Map<String, Object> processExcelData(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) return Map.of();
        Set<String> orders = new LinkedHashSet<>();
        Set<Integer> reportTypes = new LinkedHashSet<>();
        Map<String, List<Map<String, Object>>> groups = new LinkedHashMap<>();
        for (int index = 0; index < rows.size(); index++) {
            Map<String, Object> row = rows.get(index);
            if (row.values().stream().allMatch(value -> text(value).isBlank())) continue;
            String model = text(row.get("型号"));
            if (model.isBlank()) throw new IllegalArgumentException("第 " + row.getOrDefault("_row", index + 2) + " 行缺少型号");
            String order = text(row.get("订单号"));
            if (!order.isBlank()) orders.add(order);
            String repair = text(row.get("返修表"));
            if (!repair.isBlank()) {
                if ("YES".equalsIgnoreCase(repair)) reportTypes.add(QarepConst.REP_TYPE_REPAIR);
                else if ("NO".equalsIgnoreCase(repair)) reportTypes.add(QarepConst.REP_TYPE_NORMAL);
                else throw new IllegalArgumentException("第 " + row.getOrDefault("_row", index + 2) + " 行返修表仅支持 YES 或 NO");
            }
            groups.computeIfAbsent(model, ignored -> new ArrayList<>()).add(row);
        }
        if (orders.size() > 1) throw new IllegalArgumentException("Excel 包含不同订单号，请按订单分别导入");
        if (reportTypes.size() > 1) throw new IllegalArgumentException("Excel 包含不同报告单类型，请分别导入");
        if (groups.isEmpty()) return Map.of();
        BiFunction<String, String, Map<String, Object>> matcher = createProductMatcher();
        List<Map<String, Object>> products = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> group : groups.entrySet()) {
            List<String> numbers = new ArrayList<>();
            Set<String> descriptions = new LinkedHashSet<>();
            for (Map<String, Object> row : group.getValue()) {
                String number = text(row.get("编号"));
                if (!number.isBlank()) numbers.add(number);
                for (String key : List.of("描述", "备注", "产品描述", "型号描述")) {
                    String description = text(row.get(key));
                    if (!description.isBlank()) descriptions.add(description);
                }
            }
            products.add(newProduct(group.getKey(), compressNumbers(numbers), group.getValue().size(),
                    group.getValue().size(), String.join("\n", descriptions), matcher));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", orders.isEmpty() ? null : orders.iterator().next());
        result.put("repType", reportTypes.isEmpty() ? QarepConst.REP_TYPE_NORMAL : reportTypes.iterator().next());
        result.put("products", products);
        return result;
    }

    private Map<String, Object> processRecordSheet(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator) {
        String models = cellValue(sheet.getRow(0) == null ? null : sheet.getRow(0).getCell(4), formatter, evaluator);
        String number = cellValue(sheet.getRow(1) == null ? null : sheet.getRow(1).getCell(4), formatter, evaluator);
        if (models.isBlank()) throw new IllegalArgumentException("检定记录缺少型号规格");
        String[] modelItems = models.split("[,，\\r\\n]+", -1);
        String[] numberItems = number.split("[,，\\r\\n]+", -1);
        if (modelItems.length > 1 && !number.isBlank() && numberItems.length != modelItems.length) {
            throw new IllegalArgumentException("检定记录的型号与编号条数不一致，请分别导入或整理为逐行数据");
        }
        BiFunction<String, String, Map<String, Object>> matcher = createProductMatcher();
        List<Map<String, Object>> products = new ArrayList<>();
        for (int index = 0; index < modelItems.length; index++) {
            if (modelItems[index].isBlank()) throw new IllegalArgumentException("检定记录含空型号，请检查分隔符");
            products.add(newProduct(modelItems[index].trim(), modelItems.length == 1 ? number
                    : number.isBlank() ? "" : numberItems[index].trim(), null, null, "", matcher));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", null);
        result.put("repType", null);
        result.put("products", products);
        return result;
    }

    private Map<String, Object> newProduct(String model, String number, Integer qsi, Integer qi, String des,
                                           BiFunction<String, String, Map<String, Object>> matcher) {
        Map<String, Object> product = new LinkedHashMap<>();
        product.put("model", model);
        product.put("number", number);
        product.put("qsi", qsi);
        product.put("qi", qi);
        product.put("des", des);
        product.put("lt_status", null);
        product.put("flow_range", "");
        for (String field : ReportProductInput.ELECTRICAL_FIELDS) product.put(field, null);
        product.putAll(matcher.apply(model, des));
        return product;
    }

    /** 目录只属于本次导入；下一次导入重新读取，及时反映型号和参数的变更。 */
    protected BiFunction<String, String, Map<String, Object>> createProductMatcher() {
        var series = seriesService.loadMatchingSeries();
        return (model, des) -> seriesService.match(model, des, series);
    }

    static String compressNumbers(List<String> numbers) {
        List<String> ranges = new ArrayList<>();
        for (int start = 0; start < numbers.size();) {
            int end = start;
            while (end + 1 < numbers.size() && consecutive(numbers.get(end), numbers.get(end + 1))) end++;
            ranges.add(start == end ? numbers.get(start) : numbers.get(start) + "-" + numbers.get(end));
            start = end + 1;
        }
        return String.join(",", ranges);
    }

    private static boolean consecutive(String previous, String current) {
        Matcher left = NUMBER_SUFFIX.matcher(previous), right = NUMBER_SUFFIX.matcher(current);
        if (!left.matches() || !right.matches() || !left.group(1).equals(right.group(1))) return false;
        boolean padded = left.group(2).startsWith("0") || right.group(2).startsWith("0");
        if (padded && left.group(2).length() != right.group(2).length()) return false;
        return new BigInteger(left.group(2)).add(BigInteger.ONE).equals(new BigInteger(right.group(2)));
    }

    private String cellValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        return cell == null ? "" : formatter.formatCellValue(cell, evaluator).trim();
    }
    private static String text(Object value) { return value == null ? "" : value.toString().trim(); }
}
