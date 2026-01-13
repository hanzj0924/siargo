package cn.jbolt.admin.siargo.qarep;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * ExcelService Service
 * 
 * @ClassName: ExcelService
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
public class ExcelService {

	/**
     * 读取Excel文件内容
     */
    List<Map<String, Object>> readExcel(File file) throws Exception {
        List<Map<String, Object>> dataList = new ArrayList<>();
        FileInputStream fis = null;
        Workbook workbook = null;
        
        try {
            fis = new FileInputStream(file);
            
            // 根据文件扩展名创建不同的Workbook
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new Exception("不支持的文件格式，请上传Excel文件");
            }
            
            // 读取第一个sheet
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return dataList;
            }
            
            // 获取标题行（第一行）
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return dataList;
            }
            
            // 获取列标题
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValue(cell));
            }
            
            // 从第二行开始读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                Map<String, Object> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String header = headers.get(j);
                    if (header != null && !header.trim().isEmpty()) {
                        rowData.put(header, getCellValue(cell));
                    }
                }
                
                // 只添加有数据的行
                if (!rowData.isEmpty()) {
                    dataList.add(rowData);
                }
            }
            
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (Exception e) {}
            }
            if (workbook != null) {
                try { workbook.close(); } catch (Exception e) {}
            }
            
         // 删除临时文件
            if (file != null && file.exists()) {
                try {
                    file.delete();
                } catch (Exception e) {
                    System.err.println("删除临时文件失败: " + e.getMessage());
                }
            }
        }
        
        return dataList;
    }
    
    /**
     * 获取单元格的值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理数字，防止科学计数法
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        // 如果是整数
                        return String.valueOf((long) value);
                    } else {
                        // 如果是小数
                        return String.valueOf(value);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return cell.getCellFormula();
                    }
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    /**
     * 处理Excel数据，提取所需信息
     */
    Map<String, Object> processExcelData(List<Map<String, Object>> dataList) {
        Map<String, Object> result = new HashMap<>();
        
        if (dataList.isEmpty()) {
            return result;
        }
        
        // 提取订单号（取第一个数据的订单号）
        String orderId = (String) dataList.get(0).get("订单号");
        result.put("orderId", orderId != null ? orderId : "");
        
        // 提取型号并去重，保持原表格顺序
        Set<String> modelSet = new LinkedHashSet<>();
        for (Map<String, Object> row : dataList) {
            String model = (String) row.get("型号");
            if (model != null && !model.trim().isEmpty()) {
                modelSet.add(model);
            }
        }
        
        String models = String.join(",", modelSet);
        result.put("modles", models);
        
        // 提取编号，并按型号分组，保持原表格顺序
        Map<String, List<String>> modelNumbersMap = new LinkedHashMap<>();
        for (Map<String, Object> row : dataList) {
            String model = (String) row.get("型号");
            String number = (String) row.get("编号");
            
            if (model != null && number != null && !model.trim().isEmpty() && !number.trim().isEmpty()) {
                modelNumbersMap.computeIfAbsent(model, k -> new ArrayList<>()).add(number);
            }
        }
        
        // 构建编号字符串（格式：开始编号-结束编号）
        List<String> numberRanges = new ArrayList<>();
        List<String> quantities = new ArrayList<>();
        
        for (Map.Entry<String, List<String>> entry : modelNumbersMap.entrySet()) {
            List<String> numbers = entry.getValue();
            
            if (!numbers.isEmpty()) {
                // 不需要重新排序，保持原顺序
                String startNumber = numbers.get(0);
                String endNumber = numbers.get(numbers.size() - 1);
                
                // 构建编号范围
                String numberRange;
                if (startNumber.equals(endNumber)) {
                    numberRange = startNumber;
                } else {
                    numberRange = startNumber + "-" + endNumber;
                }
                
                numberRanges.add(numberRange);
                quantities.add(String.valueOf(numbers.size()));
            }
        }
        
        // 构建返回结果
        String numbers = String.join(",", numberRanges);
        String qsis = String.join(",", quantities);
        
        result.put("numbers", numbers);
        result.put("qsis", qsis);
        
        return result;
    }
	
	

}