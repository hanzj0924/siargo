package cn.jbolt.admin.siargo.qarep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.jfinal.aop.Inject;
import com.jfinal.kit.PathKit;

import cn.jbolt.siargo.model.Product;
import cn.jbolt.siargo.model.Qareport;

/**
 * PDF Service
 * 
 * @ClassName: PDFService
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
public class PDFService {
	/** 检验报告单服务，用于查询报告单数据 */
	@Inject
	private QareportService qaservice;
	
	/**
	 * 根据产品ID生成检验报告单PDF文件
	 * <p>根据产品类型和型号选择对应的PDF模板，填充数据后生成PDF文件</p>
	 * @param id 产品ID
	 * @param pdfsrc PDF输出目录（如：export/PDF 或 export/LastMonthPDF）
	 * @return 成功返回null，失败返回 "报告单号;失败原因"
	 */
	public String generateReportPdf(Long id,String pdfsrc) {
		// 查询数据
		Qareport report = qaservice.qareportFindByProId(id);
		if (report == null) {
			return "产品ID:" + id + ";未找到对应报告单数据";
		}

		// 初始化数据
		String proModel = report.getStr("sp_model");
		String prodType = report.getStr("prod_type");
		String pdfver = report.getStr("sp_pdfver");
		String orderId = report.getOrderId().toString();
		String formnum = report.getFormnum().toString();
		
		OutputStream os = null;
        PdfStamper ps = null;
        PdfReader reader = null;

		// 获取 web 根目录
		String webRootPath = PathKit.getWebRootPath();
        // PDF版号
     	String folderVer = "/G" + pdfver;
        // 生成的文件路径
        String outputFileName = webRootPath + "/"+ pdfsrc + folderVer +"/" + report.getOrderId().toString() + "_" + id.toString() + ".pdf";
        // 获取完整模板路径
        String inputFileName = getInputFile(webRootPath,prodType,pdfver,proModel);
        
        //模板文件夹是否存在
    	File inputPdfFolder = new File(webRootPath + "/reporttemplates" + folderVer);
		if (!inputPdfFolder.exists()) {
			inputPdfFolder.mkdirs();
		}
        
        //PDF文件夹是否存在
    	File PdfFolder = new File(webRootPath + "/"+ pdfsrc + folderVer);
		if (!PdfFolder.exists()) {
			PdfFolder.mkdirs();
		}
		
		// ========== 清理旧文件 ==========
		//如果目标PDF文件已存在，先删除
		File oldPdfFile = new File(webRootPath + report.getStr("sp_pdfstr"));
	    if (oldPdfFile.exists()) {
	        boolean oldFileScuess= oldPdfFile.delete();
	        
	        // 如果删除失败，尝试强制删除（释放文件句柄）
	        if (!oldFileScuess) {
	        	
	            System.gc(); // 触发垃圾回收，释放文件句柄
	            
	            try {
	                Thread.sleep(200); // 等待一下
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	            
            }
	    }
        
		// ========== 生成PDF文件 ==========
    	try {
            os = new FileOutputStream(new File(outputFileName));
            
            // 读入PDF表单模板
            reader = new PdfReader(inputFileName);
            
            // 根据表单生成一个新的PDF
            ps = new PdfStamper(reader, os);
            
            // 获取PDF表单字段
            AcroFields form = ps.getAcroFields();
            
            // 给表单添加中文字体（宋体）
            BaseFont bf = BaseFont.createFont(webRootPath + "/assets/fonts/SIMSUN.TTC,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            form.addSubstitutionFont(bf);
            
            // 构建数据映射并填充表单
            Map<String, String> dataMap = buildDataMap(report);
            
            // 遍历数据映射，给PDF表单字段赋值
            for (String key : dataMap.keySet()) {
                form.setField(key, dataMap.get(key).toString());
            }
            // 设置表单为只读（扁平化）
            ps.setFormFlattening(true);
            
            // 更新 PDF 地址
            if (pdfsrc.equals("export/PDF")) {
            	Product product = new Product().findById(id);
        		product.setPdfstr("/" + pdfsrc + folderVer + "/"  + report.getOrderId().toString() + "_" + id.toString() + ".pdf");
        		product.update();
			}
    		
        } catch (Exception e) {
            System.out.println("===============PDF导出失败=============");
            e.printStackTrace();
            String failKey = "报告单号：" + formnum + " ； 订单号：" + orderId;
            return failKey + " ;  失败原因：" + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        } finally {
            try {
                ps.close();
                reader.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

	/**
	 * 将导出失败记录写入txt文件
	 * <p>文件名格式：导出失败记录_yyyyMMdd_HHmmss.txt，输出到指定目录</p>
	 * @param failList 失败记录列表，每项格式为 "报告单号;失败原因"
	 * @param outputDir 输出目录路径
	 */
	public static void writeFailLog(List<String> failList, String outputDir) {
		if (failList == null || failList.isEmpty()) {
			return;
		}
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String fileName = outputDir + File.separator + "导出失败记录_" + timestamp + ".txt";
		File dir = new File(outputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			for (String record : failList) {
				writer.write(record);
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println("===============写入失败记录文件失败=============");
			e.printStackTrace();
		}
	}


	/**
	 * 构建PDF表单字段与报告单数据的映射关系
	 * <p>根据产品类型和型号，映射不同的检验参数到PDF表单字段</p>
	 * <p>字段映射规则：</p>
	 * <ul>
	 *   <li>基础信息：报告单编号、客户名称、订单号、型号、编号等</li>
	 *   <li>检验人员信息：精度检验、功能检验、批准检验、最终放行的姓名、时间、邮箱</li>
	 *   <li>产品类型特定参数：小流量(para2/para6/para7)、大流量(cuc/thv/zp/fl等)</li>
	 * </ul>
	 * @param report 检验报告单数据
	 * @return PDF表单字段名与值的映射Map
	 */
	private Map<String, String> buildDataMap(Qareport report) {
		Map<String, String> map = new HashMap<String, String>();
		String proModel = safeStr(report.getStr("sp_model"), "sp_model");
		
		// ========== 基础信息映射 ==========
		map.put("formnum", safeStr(report.getFormnum(), "formnum"));
		map.put("sp_qsi", safeStr(report.getStr("sp_qsi"), "sp_qsi"));
		map.put("sp_qi", safeStr(report.getStr("sp_qi"), "sp_qi"));
		map.put("sc_name", safeStr(report.getStr("sc_name"), "sc_name"));
		map.put("order_id", safeStr(report.getOrderId(), "order_id"));
		map.put("sp_model", proModel);
		
		// 报告类型：1=产成品，2=退修品
		String repType = safeStr(report.getStr("rep_type"), "rep_type");
		if (repType.equals("1")) {
			map.put("rep_type_name", "■ 产成品 □退修品");
		} else if (repType.equals("2")) {
			map.put("rep_type_name", "□产成品 ■退修品");
		}

		// ========== 检验人员信息映射 ==========
		map.put("c_time", safeStr(report.getStr("c_time"), "c_time"));
		map.put("sp_number", safeStr(report.getStr("sp_number"), "sp_number"));
		// 精度检验人员
		map.put("accq_name", safeStr(report.getStr("accq_name"), "accq_name"));
		map.put("accq_time", safeStr(report.getStr("accq_time"), "accq_time"));
		map.put("accq_email", report.getStr("accq_email") == null? "" : report.getStr("accq_email"));
		// 外观检验人员
		map.put("funq_name", safeStr(report.getStr("funq_name"), "funq_name"));
		map.put("funq_time", safeStr(report.getStr("funq_time"), "funq_time"));
		map.put("funq_email", report.getStr("funq_email") == null? "" : report.getStr("funq_email"));
		// 包装检验人员
		map.put("appq_name", safeStr(report.getStr("appq_name"), "appq_name"));
		map.put("appq_time", safeStr(report.getStr("appq_time"), "appq_time"));
		map.put("appq_email", report.getStr("appq_email") == null? "" : report.getStr("appq_email"));
		// 最终批准人员
		map.put("allq_name", safeStr(report.getStr("allq_name"), "allq_name"));
		map.put("allq_time", safeStr(report.getStr("allq_time"), "allq_time"));
		map.put("allq_email", report.getStr("allq_email") == null? "" : report.getStr("allq_email"));
		
		// ========== 小流量产品类型参数映射（prod_type=2）==========
		//小流量
		String prodType = safeStr(report.getStr("prod_type"), "prod_type");
		if (prodType.equals("2")) {
			// MF66型号：para2标记为不适用
			if (proModel.contains("MF66")) {
				map.put("para2", "/");
			} else {
				map.put("para2", "ok");
			}
			// MF52型号：para6标记为合格
			if (proModel.contains("MF52")) {
				map.put("para6", "ok");
			} else {
				map.put("para6", "/");
			}
			// MF57型号：para7标记为合格
			if (proModel.contains("MF57")) {
				map.put("para7", "ok");
			} else {
				map.put("para7", "/");
			}
		} 
		// ========== 大流量产品类型参数映射（prod_type=3）==========
		//大流量
		else if (prodType.equals("3")) {
			map.put("flow_range", report.getStr("flow_name")  == null? "" : report.getStr("flow_name"));
			
			// GD型号（中低压）：整机电流、热头电压、零点内码、故障电平
			if (proModel.contains("GD")) {
				map.put("cuc", safeStr(report.getStr("sp_cuc"), "sp_cuc"));
				map.put("thv", safeStr(report.getStr("sp_thv"), "sp_thv"));
				map.put("zp", safeStr(report.getStr("sp_zp"), "sp_zp"));
				map.put("fl", safeStr(report.getStr("sp_fl"), "sp_fl"));
				
			}
			// FD-E型号（工业表-脉冲型）：整机电流范围、脉冲电压、本地地址
			if (proModel.contains("FD-E")) {
				map.put("cucmax", safeStr(report.getStr("sp_cucmax"), "sp_cucmax"));
				map.put("cucmin", safeStr(report.getStr("sp_cucmin"), "sp_cucmin"));
				map.put("pv", safeStr(report.getStr("sp_pv"), "sp_pv"));
				map.put("pulseValue", "ok");
				map.put("la", safeStr(report.getStr("sp_la"), "sp_la"));
				map.put("thv", safeStr(report.getStr("sp_thv"), "sp_thv"));
				map.put("zp", safeStr(report.getStr("sp_zp"), "sp_zp"));
				map.put("fl", "/");
				map.put("bv", "/");
				
			}
			// FD-D型号（工业表-普通型）：无脉冲电压参数，有故障电平和电池电压
			else if (proModel.contains("FD-D"))  { 
				map.put("cucmax", safeStr(report.getStr("sp_cucmax"), "sp_cucmax"));
				map.put("cucmin", safeStr(report.getStr("sp_cucmin"), "sp_cucmin"));
				map.put("pv", safeStr(report.getStr("sp_pv"), "sp_pv"));
				map.put("pulseValue", "/");
				map.put("la", safeStr(report.getStr("sp_la"), "sp_la"));
				map.put("thv", safeStr(report.getStr("sp_thv"), "sp_thv"));
				map.put("fl", safeStr(report.getStr("sp_fl"), "sp_fl"));
				map.put("zp", safeStr(report.getStr("sp_zp"), "sp_zp"));
				map.put("bv", safeStr(report.getStr("sp_bv"), "sp_bv"));
			} 
		}
		return map;
	}

	/**
	 * 安全获取字符串值，为空时抛出包含字段名的异常
	 */
	private String safeStr(Object value, String fieldName) {
		if (value == null) {
			throw new IllegalArgumentException("字段[" + fieldName + "]为空");
		}
		return value.toString();
	}

	/**
	 * 根据产品类型和型号构建PDF模板路径
	 * <p>模板选择规则：</p>
	 * <ul>
	 *   <li>产品类型1（传感器）：使用传感器模板</li>
	 *   <li>产品类型2（小流量）：MFC/BC型号使用控制器模板，其他使用小流量计模板</li>
	 *   <li>产品类型3（大流量）：GD型号使用中低压模板，FD型号使用工业表模板</li>
	 * </ul>
	 * @param webRootPath Web应用根目录
	 * @param prodType 产品类型（1=传感器，2=小流量，3=大流量）
	 * @param pdfver PDF版本号
	 * @param proModel 产品型号
	 * @return PDF模板文件的完整路径
	 * @throws RuntimeException 未找到对应模板时抛出异常
	 */
	public String getInputFile(String webRootPath, String prodType, String pdfver , String proModel) {
		// 基础模板目录路径
		String inputFileName = webRootPath + "/reporttemplates/G" + pdfver;
		
		// ========== 传感器产品类型（prod_type=1）==========
		if (prodType.equals("1")) {
			switch(pdfver){
	         case "2":
	        	 inputFileName = inputFileName + "/传感器模板.pdf";
	        	 break;
	         case "3":
	        	 break;
	         case "4":
	        	 break;
	         default:
	        	 throw new RuntimeException("未找到传感器对应版号模板，请联系开发者");
	      }
			
		}else if(prodType.equals("2")){
			
			// ========== 小流量产品类型（prod_type=2）==========
			// MFC/BC型号使用控制器模板
			if (proModel.contains("MFC") || proModel.contains("BC")) {
				switch(pdfver){
		         case "2":
		        	 inputFileName = inputFileName + "/控制器模板.pdf";
		        	 break;
		         case "3":
		        	 break;
		         case "4":
		        	 break;
		         default:
		        	 throw new RuntimeException("未找到控制器对应版号模板，请联系开发者");
		      }
			} else {
				// 其他小流量型号使用小流量计模板
				switch(pdfver){
		         case "2":
		        	 inputFileName = inputFileName + "/小流量计模板.pdf";
		        	 break;
		         case "3":
		        	 break;
		         case "4":
		        	 break;
		         default:
		        	 throw new RuntimeException("未找到小流量对应版号模板，请联系开发者");
		      }
			}
			
		}else if(prodType.equals("3")){
			
			// ========== 大流量产品类型（prod_type=3）==========
			// GD型号使用中低压模板
			if (proModel.contains("GD")) {
				switch(pdfver){
		         case "2":
		        	 inputFileName = inputFileName + "/中低压模板.pdf";
		        	 break;
		         case "3":
		        	 break;
		         case "4":
		        	 break;
		         default:
		        	 throw new RuntimeException("未找到中低压对应版号模板，请联系开发者");
		      }
				
			}else if (proModel.contains("FD")) {
				// FD型号使用工业表模板
				switch(pdfver){
		         case "2":
		        	 inputFileName = inputFileName + "/工业表模板.pdf";
		        	 break;
		         case "3":
		        	 break;
		         case "4":
		        	 break;
		         default:
		        	 throw new RuntimeException("未找到工业表对应版号模板，请联系开发者");
		      }
			}else if (proModel.contains("MF2032") || proModel.contains("MF2025")){
				switch(pdfver){
		         case "2":
		        	 inputFileName = inputFileName + "/小流量计模板.pdf";
		        	 break;
		         case "3":
		        	 break;
		         case "4":
		        	 break;
		         default:
		        	 throw new RuntimeException("未找到工业表对应版号模板，请联系开发者");
		      }
			}else{
				throw new RuntimeException("未找到对应大流量计模板，请检查型号是否有错(区分大小写)： " + proModel);
			}
		}else {
			throw new RuntimeException("未找到对应模板，请联系开发者");
		}
		return inputFileName;
	}
	

}