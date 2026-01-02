package cn.jbolt.admin.siargo.qarep;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

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

	/**
	 * 根据 ID 生成 PDF 报告
	 */
	public void generateReportPdf(Long id,String src) throws Exception {

		// 1. 查询数据
		Qareport report = qaservice.qareportFindByProId(id);
		if (report == null) {
			throw new RuntimeException("未找到对应报告数据");
		}
		String proModle = report.getStr("sp_modle");
		

		// 2. 构建替换数据映射
		Map<String, String> dataMap = buildDataMap(report);

		// 3. 获取 web 根目录，替换 Word 模板
		String webRootPath = PathKit.getWebRootPath();
		String wordTemplatePath = null;
		
		if (report.getStr("prod_type").equals("1")) {
			wordTemplatePath = webRootPath + "/reporttemplates/传感器模板.docx";
		} else if(report.getStr("prod_type").equals("2")){
			wordTemplatePath = webRootPath + "/reporttemplates/小流量计模板.docx";
		}else if(report.getStr("prod_type").equals("3")){
			if (proModle.contains("MF66")) {
				//wordTemplatePath = webRootPath + "/reporttemplates/大流量计模板.docx";
			}else if (proModle.contains("MF66")) {
				//wordTemplatePath = webRootPath + "/reporttemplates/大流量计模板.docx";
			}else {
				throw new RuntimeException("未找到对应大流量计模板，请检查型号是否有错(区分大小写)： " + proModle);
			}
			
		}else {
			throw new RuntimeException("未找到对应模板，请联系开发者");
		}
		
		String tempWordPath = webRootPath + "/temp/temp_" + System.currentTimeMillis() + ".docx";
		replaceWordKeepFormat(wordTemplatePath, tempWordPath, dataMap);

		// 4. 转换为 PDF
		String pdfPath = tempWordPath.replace(".docx", ".pdf");
		convertWordToPdf(tempWordPath, pdfPath);
		
		// 5. 如果目标文件已存在，先删除
		File oldPdfFile = new File(webRootPath + report.getStr("sp_pdfstr"));
	    if (oldPdfFile.exists()) {
	        boolean oldFileScuess= oldPdfFile.delete();
	        if (!oldFileScuess) {
	            // 删除失败，尝试强制删除
	            System.gc(); // 触发垃圾回收，释放文件句柄
	            try {
	                Thread.sleep(100); // 等待一下
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	            
	            if (!oldFileScuess) {
	                // 如果还是删除失败，尝试其他方法
	                throw new RuntimeException("无法删除已存在的文件，请联系开发者: " + webRootPath + report.getStr("sp_pdfstr"));
	    		}
            }
	    }
	    
	    // 6. 重命名 PDF  
	 	String finalPdfPath = webRootPath + "/"+src+"/" + report.getOrderId().toString() + "_" + id.toString() + ".pdf";
		new File(pdfPath).renameTo(new File(finalPdfPath));

		// 7. 清理临时 Word 文件
		new File(tempWordPath).delete();
		
		// 8. 更新 PDF 地址
		Product product = new Product().findById(id);
		product.setPdfstr("/PDF/" + report.getOrderId().toString() + "_" + id.toString() + ".pdf");
		product.update();
		
	}

	/**
	 * 构建数据映射
	 */
	private Map<String, String> buildDataMap(Qareport report) {
		Map<String, String> map = new HashMap<>();
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
		map.put("funq_name", report.getStr("funq_name"));
		map.put("funq_time", report.getStr("funq_time"));
		map.put("appq_name", report.getStr("appq_name"));
		map.put("appq_time", report.getStr("appq_time"));
		map.put("allq_name", report.getStr("allq_name"));
		map.put("allq_time", report.getStr("allq_time"));
		
		
		//检验项目 小流量
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
			if (proModle.contains("MFC1000") || proModle.contains("BC")) {
				map.put("para8", "ok");
			} else {
				map.put("para8", "/");
			}	
		} 
		//大流量
		else if (report.getStr("prod_type").equals("3")) {
			if (proModle.contains("GD")) {
				map.put("cuc", report.getStr("sp_cuc"));
				map.put("fl", report.getStr("sp_fl"));
			} 
			
			if (proModle.contains("XD")) {
				map.put("cucmax", report.getStr("sp_cucmax"));
				map.put("cucmin", report.getStr("sp_cucmin"));
				map.put("pv", report.getStr("sp_cpv"));
				map.put("thv", report.getStr("sp_thv"));
				map.put("zp", report.getStr("sp_zp"));
			} 
		} 
		
		return map;
	}

	/**
	 * 替换 Word 模板中的占位符
	 */
	public static void replaceWordKeepFormat(String templatePath, String outputPath, Map<String, String> dataMap)
			throws IOException {
		FileInputStream fis = new FileInputStream(templatePath);
		XWPFDocument doc = new XWPFDocument(fis);
		fis.close();

		// 1. 处理所有段落
		for (XWPFParagraph paragraph : doc.getParagraphs()) {
			replaceInParagraph(paragraph, dataMap);
		}

		// 2. 处理所有表格
		for (XWPFTable table : doc.getTables()) {
			for (XWPFTableRow row : table.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph paragraph : cell.getParagraphs()) {
						replaceInParagraph(paragraph, dataMap);
					}
				}
			}
		}

		// 3. 处理页眉
		for (XWPFHeader header : doc.getHeaderList()) {
			for (XWPFParagraph paragraph : header.getParagraphs()) {
				replaceInParagraph(paragraph, dataMap);
			}
		}

		// 4. 处理页脚
		for (XWPFFooter footer : doc.getFooterList()) {
			for (XWPFParagraph paragraph : footer.getParagraphs()) {
				replaceInParagraph(paragraph, dataMap);
			}
		}

		// 保存文档
		FileOutputStream fos = new FileOutputStream(outputPath);
		doc.write(fos);
		fos.close();
		doc.close();
	}

	private static void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> dataMap) {
	    List<XWPFRun> runs = paragraph.getRuns();
	    if (runs == null || runs.isEmpty()) {
	        return;
	    }
	    
	    // 合并所有 run 的文本
	    StringBuilder fullText = new StringBuilder();
	    for (XWPFRun run : runs) {
	        String text = run.getText(0);
	        if (text != null) {
	            fullText.append(text);
	        }
	    }
	    
	    String originalText = fullText.toString();
	    String newText = originalText;
	    
	    // 替换所有占位符
	    for (Map.Entry<String, String> entry : dataMap.entrySet()) {
	        String placeholder = "${" + entry.getKey() + "}";
	        String replacement = entry.getValue() != null ? entry.getValue() : "";
	        
	        // 确保替换时保持适当的格式
	        if (newText.contains(placeholder)) {
	            newText = newText.replace(placeholder, replacement);
	        }
	    }
	    
	    // 如果文本没有变化，直接返回
	    if (originalText.equals(newText)) {
	        return;
	    }
	    
	    // 清空所有 run
	    for (XWPFRun run : runs) {
	        run.setText("", 0);
	    }
	    
	    // 将新文本放入第一个 run
	    if (!runs.isEmpty()) {
	        runs.get(0).setText(newText, 0);
	    }
	}

	/**
	 * 调用 Office COM 组件将 Word 转换为 PDF（仅限 Windows）
	 */
	private void convertWordToPdf(String wordPath, String pdfPath) throws Exception {
		// 确保路径是绝对路径
		wordPath = new File(wordPath).getAbsolutePath();
		pdfPath = new File(pdfPath).getAbsolutePath();

		// 使用 VBScript 调用 Word 转换（无需额外依赖）
		String script = "Set objWord = CreateObject(\"Word.Application\")\n" + "objWord.Visible = False\n"
				+ "objWord.DisplayAlerts = False\n" + "Set objDoc = objWord.Documents.Open(\""
				+ wordPath.replace("\\", "\\\\") + "\")\n" + "objDoc.SaveAs \"" + pdfPath.replace("\\", "\\\\")
				+ "\", 17 ' 17 = wdFormatPDF\n" + "objDoc.Close\n" + "objWord.Quit\n" + "Set objDoc = Nothing\n"
				+ "Set objWord = Nothing";

		// 将脚本写入临时文件
		String scriptPath = "convert.vbs";
		try (FileWriter writer = new FileWriter(scriptPath)) {
			writer.write(script);
		}

		// 执行 VBScript
		ProcessBuilder pb = new ProcessBuilder("cscript", "//Nologo", scriptPath);
		Process process = pb.start();
		int exitCode = process.waitFor();

		// 清理临时脚本文件
		new File(scriptPath).delete();

		if (exitCode != 0) {
			throw new RuntimeException("Word 转 PDF 失败，请确保已安装 Microsoft Office");
		}
	}
	

}