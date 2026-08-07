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

import cn.jbolt.siargo.model.Product;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.jfinal.aop.Inject;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import cn.jbolt.admin.siargo.qarep.pdffolder.PdfFolderService;
import cn.jbolt.admin.siargo.qarep.pdffolder.PdfTemplateService;
import cn.jbolt.siargo.model.Qareport;

/**
 * PDF Service
 * 
 * @ClassName: PDFService
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
public class PDFService {

	private static final Log LOG = Log.getLog(PDFService.class);

	/** 检验报告单服务，用于查询报告单数据 */
	@Inject
	private QareportService qaservice;
	@Inject
	private PdfFolderService pdfFolderService;
	@Inject
	private PdfTemplateService pdfTemplateService;

	// ==================== 公开方法 ====================

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
		// 空安全转换（order_id/formnum 为 Long，历史脏数据可能为 null，避免 NPE）
		String orderId = report.getOrderId() != null ? report.getOrderId().toString() : null;
		String formnum = report.getFormnum() != null ? report.getFormnum().toString() : null;
		String failKey = "报告单号：" + (formnum != null ? formnum : "空") + " ； 订单号：" + (orderId != null ? orderId : "空");
		if (orderId == null || formnum == null) {
			return failKey + " ;  失败原因：报告单数据不完整（订单号或编号为空）";
		}
		
		OutputStream os = null;
        PdfStamper ps = null;
        PdfReader reader = null;

		// 获取 web 根目录
		String webRootPath = PathKit.getWebRootPath();
        // 获取完整模板路径
        String inputFileName = getInputFile(webRootPath, prodType, pdfver, proModel);

        // 确定输出目录（来自 DB 配置，必须做路径穿越校验）
        String outputDir;
        if ("export/PDF".equals(pdfsrc)) {
            outputDir = safeRelativePath(pdfFolderService.getExportPath(pdfver), "输出目录(exportPath)");
        } else {
            outputDir = safeRelativePath(pdfFolderService.getBatchPath(), "输出目录(batchPath)");
        }
        if (outputDir == null) {
            return failKey + " ;  失败原因：输出目录配置非法";
        }
        // 确保输出目录存在
        File outputDirFile = new File(webRootPath + "/" + outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }
        // 第三层校验：canonical 路径二次确认（防符号链接/编码绕过，见规范 6.5）
        try {
            File webRootFile = new File(webRootPath);
            if (!outputDirFile.getCanonicalPath().startsWith(webRootFile.getCanonicalPath())) {
                LOG.warn("检测到非法输出目录（canonical越界）: " + outputDir);
                return failKey + " ;  失败原因：输出目录配置非法";
            }
        } catch (IOException e) {
            LOG.warn("输出目录canonical校验失败: " + outputDir, e);
            return failKey + " ;  失败原因：输出目录配置非法";
        }
        String outputFileName = webRootPath + "/" + outputDir + "/" + orderId + "_" + id + ".pdf";
		
		// ========== 清理旧文件 ==========
		//如果目标PDF文件已存在，先删除（sp_pdfstr 可能为空/含穿越标记，跳过清理，新文件会覆盖写入）
		String oldPdfstr = report.getStr("sp_pdfstr");
		File oldPdfFile = null;
		if (oldPdfstr != null && !oldPdfstr.isEmpty() && !oldPdfstr.contains("..")) {
			oldPdfFile = new File(webRootPath + (oldPdfstr.startsWith("/") ? oldPdfstr : "/" + oldPdfstr));
		}
	    if (oldPdfFile != null && oldPdfFile.exists()) {
	        boolean oldFileDeleted = oldPdfFile.delete();

	        // 如果删除失败（可能文件句柄被短暂占用），稍等后重试一次
	        // 生成PDF时所有流句柄均在 finally 中确保关闭，此处不再依赖 System.gc()
	        if (!oldFileDeleted) {
	            try {
	                Thread.sleep(200);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	            if (!oldPdfFile.delete()) {
	                LOG.warn("旧PDF文件删除失败（将被后续生成覆盖）: " + oldPdfFile.getAbsolutePath());
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
            if ("export/PDF".equals(pdfsrc)) {
            	Product product = new Product().findById(id);
        		product.setPdfstr(outputDir + "/" + orderId + "_" + id + ".pdf");
        		product.update();
			}
    		
        } catch (Exception e) {
            LOG.error("PDF导出失败, 报告单号：" + formnum + " ; 订单号：" + orderId, e);
            return failKey + " ;  失败原因：" + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e) {
                LOG.warn("关闭PdfStamper失败", e);
            }
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                LOG.warn("关闭PdfReader失败", e);
            }
            try {
                if (os != null) os.close();
            } catch (Exception e) {
                LOG.warn("关闭输出流失败", e);
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
			LOG.error("写入失败记录文件失败: " + fileName, e);
		}
	}

	// ==================== 模板路径解析 ====================

	/**
	 * 根据产品类型和型号构建PDF模板路径
	 * <p>从数据库配置中获取模板存储路径和规则匹配结果</p>
	 * @param webRootPath Web应用根目录
	 * @param prodType 产品类型（1=传感器，2=小流量，3=大流量）
	 * @param pdfver PDF版本号
	 * @param proModel 产品型号
	 * @return PDF模板文件的完整路径
	 * @throws RuntimeException 未找到对应模板时抛出异常
	 */
	public String getInputFile(String webRootPath, String prodType, String pdfver, String proModel) {
		if (proModel == null || proModel.isEmpty()) {
			throw new RuntimeException("产品型号为空，无法选择模板");
		}
		// 1. 从 folder 配置表获取模板存储路径（来自 DB 配置，必须做路径穿越校验）
		String templatePath = safeRelativePath(pdfFolderService.getTemplatePath(pdfver), "模板目录(templatePath)");
		if (templatePath == null) {
			throw new RuntimeException("版号 " + pdfver + " 未配置或配置非法，请在模板管理中添加");
		}
		// 2. 从 template 规则表匹配模板文件名（必须为单段文件名，拒绝路径穿越）
		String templateFile = pdfTemplateService.matchTemplate(pdfver, prodType, proModel);
		if (templateFile == null) {
			throw new RuntimeException("未找到对应模板，请检查型号是否有错(区分大小写)： " + proModel);
		}
		if (templateFile.contains("/") || templateFile.contains("\\") || templateFile.contains("..")) {
			throw new RuntimeException("模板文件名配置非法: " + templateFile);
		}
		// 3. 拼接完整路径（路径来自DB，经校验后使用）
		return webRootPath + "/" + templatePath + "/" + templateFile;
	}

	// ==================== 数据映射构建 ====================

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
		map.put("accq_email", optionalStr(report.getStr("accq_email")));
		// 外观检验人员
		map.put("funq_name", safeStr(report.getStr("funq_name"), "funq_name"));
		map.put("funq_time", safeStr(report.getStr("funq_time"), "funq_time"));
		map.put("funq_email", optionalStr(report.getStr("funq_email")));
		// 成品检漏检验人员（可选取值：无成品检漏产品不输出，避免 PDF 生成失败）
		map.put("lt_name", optionalStr(report.getStr("lt_name")));
		map.put("lt_time", optionalStr(report.getStr("lt_time")));
		map.put("lt_email", optionalStr(report.getStr("lt_email")));
		// 包装检验人员
		map.put("appq_name", safeStr(report.getStr("appq_name"), "appq_name"));
		map.put("appq_time", safeStr(report.getStr("appq_time"), "appq_time"));
		map.put("appq_email", optionalStr(report.getStr("appq_email")));
		// 最终批准人员
		map.put("allq_name", safeStr(report.getStr("allq_name"), "allq_name"));
		map.put("allq_time", safeStr(report.getStr("allq_time"), "allq_time"));
		map.put("allq_email", optionalStr(report.getStr("allq_email")));
		
		// ========== 产品类型特定参数映射 ==========
		String prodType = safeStr(report.getStr("prod_type"), "prod_type");
		if (prodType.equals("2")) {
			buildSmallFlowParams(map, report, proModel);
		} else if (prodType.equals("3")) {
			buildLargeFlowParams(map, report, proModel);
		}

		return map;
	}

	/**
	 * 构建小流量产品类型参数映射（prod_type=2）
	 * @param map PDF表单字段映射
	 * @param report 检验报告单数据
	 * @param proModel 产品型号
	 */
	private void buildSmallFlowParams(Map<String, String> map, Qareport report, String proModel) {
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

	/**
	 * 构建大流量产品类型参数映射（prod_type=3）
	 * @param map PDF表单字段映射
	 * @param report 检验报告单数据
	 * @param proModel 产品型号
	 */
	private void buildLargeFlowParams(Map<String, String> map, Qareport report, String proModel) {
		map.put("flow_range", optionalStr(report.getStr("flow_name")));

		// GD型号（中低压）：整机电流、热头电压、零点内码、故障电平
		if (proModel.contains("GD")) {
			map.put("cuc", safeStr(report.getStr("sp_cuc"), "sp_cuc"));
			map.put("thv", safeStr(report.getStr("sp_thv"), "sp_thv"));
			map.put("zp", safeStr(report.getStr("sp_zp"), "sp_zp"));
			map.put("fl", safeStr(report.getStr("sp_fl"), "sp_fl"));
		}
		// FD-E型号（工业表-脉冲型）、MFXX-F-E(旧型号)：整机电流范围、脉冲电压、本地地址
		else if (proModel.contains("FD-E") || proModel.contains("-F-E")) {
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
		// FD-D型号（工业表-普通型）、MFXX-F-D(旧型号)：无脉冲电压参数，有故障电平和电池电压
		else if (proModel.contains("FD-D") || proModel.contains("-F-D")) {
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
		// MFI型号（插入式）：无脉冲电压参数，有故障电平和电池电压
		else if (proModel.contains("MFI")) {
			map.put("cucmax", safeStr(report.getStr("sp_cucmax"), "sp_cucmax"));
			map.put("cucmin", safeStr(report.getStr("sp_cucmin"), "sp_cucmin"));
			map.put("pv", safeStr(report.getStr("sp_pv"), "sp_pv"));
			map.put("pulseValue", "/");
			map.put("thv", safeStr(report.getStr("sp_thv"), "sp_thv"));
			map.put("zp", safeStr(report.getStr("sp_zp"), "sp_zp"));
		}
		// MF2025、MF2032型号
		else if (proModel.contains("MF2025") || proModel.contains("MF2032")) {
			map.put("para2", "ok");
			map.put("para6", "/");
			map.put("para7", "/");
		} else {
			throw new RuntimeException("未识别到大流量计，请检查型号是否有误");
		}
	}

	// ==================== 工具方法 ====================

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
	 * 可选字符串值，为null时返回空字符串
	 * @param value 原始值
	 * @return value为null返回""，否则返回value.toString()
	 */
	private String optionalStr(Object value) {
		return value == null ? "" : value.toString();
	}

	/**
	 * 规范化并校验相对路径（来自 DB 配置，防路径穿越，见规范 6.5）
	 * <p>校验规则：</p>
	 * <ol>
	 *   <li>拒绝包含 ".." 的路径穿越</li>
	 *   <li>拒绝真正的绝对路径（\ 开头即UNC路径、盘符开头）</li>
	 *   <li>以 / 开头视为相对webRoot的路径（模板管理写入格式），统一去掉前导斜杠，避免拼接出双斜杠</li>
	 *   <li>统一去尾部斜杠，避免拼接出双斜杠</li>
	 * </ol>
	 * @param path 原始相对路径
	 * @param desc 配置项描述（用于告警日志）
	 * @return 规范化后的相对路径（无头尾斜杠），非法时返回 null
	 */
	private String safeRelativePath(String path, String desc) {
		if (path == null || path.isEmpty()) {
			return null;
		}
		String dir = path.trim();
		// 第一层：拒绝路径穿越
		if (dir.contains("..")) {
			LOG.warn("检测到非法" + desc + "（含..）: " + path);
			return null;
		}
		// 第二层：拒绝真正的绝对路径（\ 开头即UNC路径、盘符开头）
		if (dir.startsWith("\\") || dir.matches("^[a-zA-Z]:.*")) {
			LOG.warn("检测到非法" + desc + "（绝对路径）: " + path);
			return null;
		}
		// 第三层：去掉前导 /（DB中模板管理写入的路径以 / 开头，表示相对webRoot的路径）
		while (dir.startsWith("/")) {
			dir = dir.substring(1);
		}
		// 第四层：统一去尾部斜杠，避免拼接出双斜杠
		while (dir.endsWith("/") || dir.endsWith("\\")) {
			dir = dir.substring(0, dir.length() - 1);
		}
		return dir;
	}

}
