package cn.jbolt.admin.siargo.qarep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
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
	@Inject
	private QareportService qaservice;
	
	public void generateReportPdf(Long id,String pdfsrc) {
		// 查询数据
		Qareport report = qaservice.qareportFindByProId(id);
		if (report == null) {
			throw new RuntimeException("未找到对应报告单数据");
		}
		
		// 初始化数据
		String proModle = report.getStr("sp_modle");
		String prodType = report.getStr("prod_type");
		String pdfver = report.getStr("sp_pdfver");
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
        String inputFileName = getInputFile(webRootPath,prodType,pdfver,proModle);
        
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
		
		//如果目标PDF文件已存在，先删除
		File oldPdfFile = new File(webRootPath + report.getStr("sp_pdfstr"));
	    if (oldPdfFile.exists()) {
	        boolean oldFileScuess= oldPdfFile.delete();
	        
	        // 如果删除失败，尝试强制删除
	        if (!oldFileScuess) {
	        	
	            System.gc(); // 触发垃圾回收，释放文件句柄
	            
	            try {
	                Thread.sleep(100); // 等待一下
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	            
	            // 再次删除，如果还是删除失败，尝试其他方法
	            if (!oldFileScuess) {
	                throw new RuntimeException("无法删除已存在的文件，请联系开发者: " + webRootPath + report.getStr("sp_pdfstr"));
	    		}
            }
	    }
        
	    try {
            os = new FileOutputStream(new File(outputFileName));
            
            // 读入pdf表单
            reader = new PdfReader(inputFileName);
            
            // 根据表单生成一个新的pdf
            ps = new PdfStamper(reader, os);
            
            // 获取pdf表单
            AcroFields form = ps.getAcroFields();
            
            // 给表单添加中文字体
            BaseFont bf = BaseFont.createFont(webRootPath + "/assets/fonts/SIMSUN.TTC,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            form.addSubstitutionFont(bf);
            
            // 映射数据
            Map<String, String> dataMap = buildDataMap(report);
            
            // 遍历data 给pdf表单表格赋值
            for (String key : dataMap.keySet()) {
                form.setField(key, dataMap.get(key).toString());
            }
            ps.setFormFlattening(true);
            
            // 更新 PDF 地址
    		Product product = new Product().findById(id);
    		product.setPdfstr("/" + pdfsrc + folderVer + "/"  + report.getOrderId().toString() + "_" + id.toString() + ".pdf");
    		product.update();
    		
        } catch (Exception e) {
            System.out.println("===============PDF导出失败=============");
            e.printStackTrace();
        } finally {
            try {
                ps.close();
                reader.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


	/**
	 * 构建数据映射
	 */
	private Map<String, String> buildDataMap(Qareport report) {
		Map<String, String> map = new HashMap<String, String>();
		String proModle = report.getStr("sp_modle");
		
		map.put("formnum", report.getFormnum().toString());
		map.put("sp_qsi", report.getStr("sp_qsi"));
		map.put("sp_qi", report.getStr("sp_qi"));
		map.put("sc_name", report.getStr("sc_name"));
		map.put("order_id", report.getOrderId().toString());
		map.put("sp_modle", report.getStr("sp_modle"));
		if (report.getStr("rep_type").equals("1")) {
			map.put("rep_type_name", "■ 产成品 □退修品");
		} else if (report.getStr("rep_type").equals("2")) {
			map.put("rep_type_name", "□产成品 ■退修品");
		}

		map.put("c_time", report.getStr("c_time"));
		map.put("sp_number", report.getStr("sp_number"));
		map.put("accq_name", report.getStr("accq_name"));
		map.put("accq_time", report.getStr("accq_time"));
		map.put("accq_email", report.getStr("accq_email") == null? "" : report.getStr("accq_email"));
		map.put("funq_name", report.getStr("funq_name"));
		map.put("funq_time", report.getStr("funq_time"));
		map.put("funq_email", report.getStr("funq_email") == null? "" : report.getStr("funq_email"));
		map.put("appq_name", report.getStr("appq_name"));
		map.put("appq_time", report.getStr("appq_time"));
		map.put("appq_email", report.getStr("appq_email") == null? "" : report.getStr("appq_email"));
		map.put("allq_name", report.getStr("allq_name"));
		map.put("allq_time", report.getStr("allq_time"));
		map.put("allq_email", report.getStr("allq_email") == null? "" : report.getStr("allq_email"));
		
		//小流量
		if (report.getStr("prod_type").equals("2")) {
			if (proModle.contains("MF66")) {
				map.put("para2", "/");
			} else {
				map.put("para2", "ok");
			}
			if (proModle.contains("MF52")) {
				map.put("para6", "ok");
			} else {
				map.put("para6", "/");
			}
			if (proModle.contains("MF57")) {
				map.put("para7", "ok");
			} else {
				map.put("para7", "/");
			}
		} 
		//大流量
		else if (report.getStr("prod_type").equals("3")) {
			map.put("flow_range", report.getStr("sp_flow_range_name")  == null? "" : report.getStr("sp_flow_range_name"));
			
			if (proModle.contains("GD")) {
				map.put("cuc", report.getStr("sp_cuc"));
				map.put("thv", report.getStr("sp_thv"));
				map.put("zp", report.getStr("sp_zp"));
				map.put("fl", report.getStr("sp_fl"));
				
			}if (proModle.contains("FD-E")) {
				map.put("cucmax", report.getStr("sp_cucmax"));
				map.put("cucmin", report.getStr("sp_cucmin"));
				map.put("pv", report.getStr("sp_pv"));
				map.put("pulseValue", "ok");
				map.put("la", report.getStr("sp_la"));
				map.put("thv", report.getStr("sp_thv"));
				map.put("zp", report.getStr("sp_zp"));
				map.put("fl", "/");
				map.put("bv", "/");
				
			}else if (proModle.contains("FD-D"))  { 
				map.put("cucmax", report.getStr("sp_cucmax"));
				map.put("cucmin", report.getStr("sp_cucmin"));
				map.put("pv", report.getStr("sp_pv"));
				map.put("pulseValue", "/");
				map.put("la", report.getStr("sp_la"));
				map.put("thv", report.getStr("sp_thv"));
				map.put("fl", report.getStr("sp_fl"));
				map.put("zp", report.getStr("sp_zp"));
				map.put("bv", report.getStr("sp_bv"));
			} 
		}
		return map;
	}

	
	/**
	 * 构建模板路径
	 */
	public String getInputFile(String webRootPath, String prodType, String pdfver , String proModle) {
		String inputFileName = webRootPath + "/reporttemplates/G" + pdfver;
		
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
			
			if (proModle.contains("MFC") || proModle.contains("BC")) {
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
			
			if (proModle.contains("GD")) {
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
				
			}else if (proModle.contains("FD")) {
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
			}else if (proModle.contains("MF2032")){
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
				throw new RuntimeException("未找到对应大流量计模板，请检查型号是否有错(区分大小写)： " + proModle);
			}
		}else {
			throw new RuntimeException("未找到对应模板，请联系开发者");
		}
		return inputFileName;
	}
	

}