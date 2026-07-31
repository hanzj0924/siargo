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

import com.jfinal.log.Log;


/**
 * Excel Service
 * 
 * @ClassName: ExcelService
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
public class ExcelService {

	private static final Log LOG = Log.getLog(ExcelService.class);

	/**
     * 读取sheet 0的列式数据（旧模板格式：104842.xls类型）
     * <p>解析Excel第一行为标题行，后续行为数据行，返回以标题为key的数据列表</p>
     * @param workbook 已打开的Workbook对象
     * @return 解析后的数据列表
     */
    private List<Map<String, Object>> readSheet0(Workbook workbook) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return dataList;
        }
        
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return dataList;
        }
        
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(getCellValue(cell));
        }
        
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
            
            if (!rowData.isEmpty()) {
                dataList.add(rowData);
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
     * 根据首个型号自动判定产品类型（siargo_prod_type 字典 sn）
     * <p>classifier → 字典 sn 映射：1(小流量)→2, 2(大流量)→3, 3(传感器)→1</p>
     * <p>两个模板（旧模板/检定记录模板）共用此映射，避免重复维护</p>
     * @param firstModel 首个型号字符串
     * @return 产品类型字典 sn；无法判定时返回null
     */
    private Integer resolveProdType(String firstModel) {
        if (firstModel == null || firstModel.isEmpty()) {
            return null;
        }
        int classified = ProductModelClassifier.classify(firstModel);
        if (classified == 1) {
            return QarepConst.PROD_TYPE_SMALL_FLOW;
        }
        if (classified == 2) {
            return QarepConst.PROD_TYPE_LARGE_FLOW;
        }
        if (classified == 3) {
            return QarepConst.PROD_TYPE_SENSOR;
        }
        return null;
    }
    
    /**
     * 处理Excel数据，提取检验报告单所需的订单和产品信息
     * <p>从Excel数据中提取：订单号、型号列表、编号范围、数量统计</p>
     * <p>编号格式：同型号连续编号合并为"起始编号-结束编号"格式</p>
     * @param dataList 从Excel读取的原始数据列表
     * @return 包含orderId、models、numbers、qsis的处理结果
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
        
        // 提取报告单类型（返修表列，对应Excel K列）
        // YES → repType=2（退修品），NO或其他 → repType=1（产成品）
        String repTypeStr = (String) dataList.get(0).get("返修表");
        int repType = (repTypeStr != null && repTypeStr.trim().equalsIgnoreCase("YES")) ? 2 : 1;
        result.put("repType", repType);
        		
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
        result.put("models", models);
        
        // 根据型号自动判定产品类型（统一映射逻辑见 resolveProdType）
        Integer prodType = null;
        if (!modelSet.isEmpty()) {
            prodType = resolveProdType(modelSet.iterator().next());
        }
        result.put("prodType", prodType);
        		
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
        result.put("qis", qsis);
        
        return result;
    }
	
	/**
	 * 查找检定记录sheet（名称以"检定记录"开头，用于匹配检定记录A0/A1等）
	 * @param workbook 已打开的Workbook对象
	 * @return 找到的Sheet对象，未找到返回null
	 */
	private Sheet findJdRecordSheet(Workbook workbook) {
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			String sheetName = workbook.getSheetName(i);
			if (sheetName != null && sheetName.startsWith("检定记录")) {
				return workbook.getSheetAt(i);
			}
		}
		return null;
	}

	/**
	 * 处理PFQVF81007类型Excel模板（检定记录A* sheet，表单式布局）
	 * <p>从表单中提取：型号规格(models)、产品编号(numbers)</p>
	 * <p>订单号/报告类型/送检数量/检验数量不提取，由用户手动填写，对应字段设为null</p>
	 * @param sheet 检定记录sheet
	 * @return 统一样式的result Map（不可用字段值为null）
	 */
	private Map<String, Object> processPfqTemplate(Sheet sheet) {
		Map<String, Object> result = new HashMap<>();
		
		// 读取型号规格 (R0C4, 第0行第4列)
		Row row0 = sheet.getRow(0);
		String models = null;
		if (row0 != null) {
			String val = getCellValue(row0.getCell(4));
			if (val != null && !val.isEmpty()) {
				models = val;
			}
		}
		
		// 读取产品编号 (R1C4, 第1行第4列)
		Row row1 = sheet.getRow(1);
		String numbers = null;
		if (row1 != null) {
			String val = getCellValue(row1.getCell(4));
			if (val != null && !val.isEmpty()) {
				numbers = val;
			}
		}
		
		
		// 根据型号规格自动判定产品类型（统一映射逻辑见 resolveProdType）
		// 取第一个型号进行分类（多型号以逗号分隔时取首个）
		Integer prodType = null;
		if (models != null && !models.isEmpty()) {
			prodType = resolveProdType(models.split(",")[0].trim());
		}
        
        result.put("orderId", null);
		result.put("repType", null);
		result.put("prodType", prodType);
		result.put("models", models);
		result.put("numbers", numbers);
		result.put("qsis", null);
		result.put("qis", null);
		
		return result;
	}

	/**
	 * 处理Excel文件导入的统一入口
	 * <p>自动检测模板类型（旧模板 vs 检定记录模板），路由到对应的提取逻辑</p>
	 * <p>负责文件生命周期管理：打开→检测→提取→关闭→删除临时文件</p>
	 * @param file Excel文件对象
	 * @return 统一样式的result Map
	 * @throws Exception 文件格式不支持或读取异常时抛出
	 */
	public Map<String, Object> processExcelFile(File file) throws Exception {
		FileInputStream fis = null;
		Workbook workbook = null;
		
		try {
			fis = new FileInputStream(file);
			String fileName = file.getName().toLowerCase();
			if (fileName.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(fis);
			} else if (fileName.endsWith(".xls")) {
				workbook = new HSSFWorkbook(fis);
			} else {
				throw new Exception("不支持的文件格式，请上传Excel文件");
			}
			
			// 检测是否为检定记录模板（名称以"检定记录"开头的sheet）
			Sheet jdSheet = findJdRecordSheet(workbook);
			if (jdSheet != null) {
				return processPfqTemplate(jdSheet);
			}
			
			// 回退到旧模板逻辑（读取sheet 0列式数据）
			List<Map<String, Object>> dataList = readSheet0(workbook);
			if (dataList == null || dataList.isEmpty()) {
				return new HashMap<>();
			}
			return processExcelData(dataList);
			
		} finally {
			if (fis != null) {
				try { fis.close(); } catch (Exception e) {}
			}
			if (workbook != null) {
				try { workbook.close(); } catch (Exception e) {}
			}
			if (file != null && file.exists()) {
				try {
					file.delete();
				} catch (Exception e) {
					LOG.warn("删除临时文件失败: " + e.getMessage(), e);
				}
			}
		}
	}

}
