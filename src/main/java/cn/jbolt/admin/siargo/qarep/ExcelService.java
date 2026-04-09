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
 * Excel Service
 * 
 * @ClassName: ExcelService
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
public class ExcelService {

	/**
     * 读取Excel文件内容，支持xls和xlsx格式
     * <p>解析Excel第一行为标题行，后续行为数据行，返回以标题为key的数据列表</p>
     * @param file Excel文件对象
     * @return 解析后的数据列表，每个元素为一行数据的Map（key为列标题，value为单元格值）
     * @throws Exception 文件格式不支持或读取异常时抛出
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
     * 获取单元格的值，根据单元格类型自动转换
     * <p>支持字符串、数字、布尔、公式、空白等类型，数字类型会处理科学计数法问题</p>
     * @param cell 单元格对象
     * @return 单元格值的字符串表示，空白单元格返回空字符串
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
     * 处理Excel数据，提取检验报告单所需的订单和产品信息
     * <p>从Excel数据中提取：订单号、型号列表、编号范围、数量统计</p>
     * <p>编号格式：同型号连续编号合并为"起始编号-结束编号"格式</p>
     * @param dataList 从Excel读取的原始数据列表
     * @return 包含orderId、modles、numbers、qsis的处理结果
     */
    Map<String, Object> processExcelData(List<Map<String, Object>> dataList) {
        Map<String, Object> result = new HashMap<>();
        
        if (dataList.isEmpty()) {
            return result;
        }
        
        // ========== 提取订单基本信息 ==========
        // 提取订单号（取第一个数据的订单号）
        String orderId = (String) dataList.get(0).get("订单号");
        result.put("orderId", orderId != null ? orderId : "");
        		
        // ========== 提取型号信息并去重 ==========
        // 使用LinkedHashSet保持原表格顺序，避免型号重复
        Set<String> modelSet = new LinkedHashSet<>();
        for (Map<String, Object> row : dataList) {
            String model = (String) row.get("型号");
            if (model != null && !model.trim().isEmpty()) {
                modelSet.add(model);
            }
        }
        
        String models = String.join(",", modelSet);
        result.put("modles", models);
        		
        // ========== 按型号分组提取编号 ==========
        // 将每个型号对应的编号收集到列表中
        Map<String, List<String>> modelNumbersMap = new LinkedHashMap<>();
        for (Map<String, Object> row : dataList) {
            String model = (String) row.get("型号");
            String number = (String) row.get("编号");
            
            if (model != null && number != null && !model.trim().isEmpty() && !number.trim().isEmpty()) {
                modelNumbersMap.computeIfAbsent(model, k -> new ArrayList<>()).add(number);
            }
        }
        
        // ========== 构建编号范围和数量统计 ==========
        // 编号格式：单个编号直接显示，连续编号合并为"起始-结束"格式
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
        
        // ========== 构建最终返回结果 ==========
        // 将各型号的编号范围和数量用逗号连接成字符串
        String numbers = String.join(",", numberRanges);
        String qsis = String.join(",", quantities);
        
        result.put("numbers", numbers);
        result.put("qsis", qsis);
        
        return result;
    }
	
	

}