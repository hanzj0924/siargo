package cn.jbolt.admin.siargo.qarep;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.admin.siargo.customer.CustomerService;
import cn.jbolt.core.base.config.JBoltConfig;

import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import java.time.LocalDate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Product;
import cn.jbolt.siargo.model.Qareport;
/**
 * 检验报告单管理 Controller
 * @ClassName: QareportAdminController
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/qarep", viewPath = "/_view/admin/siargo/qarep")

public class QareportAdminController extends JBoltBaseController {

	private static final Log LOG = Log.getLog(QareportAdminController.class);

	/** 检验报告单服务 */
	@Inject
	private QareportService service;
	/** PDF生成服务 */
	@Inject
	private PDFService pdfservice;
	/** Excel解析服务 */
	@Inject
	private ExcelService excelservice;
	/** 产品服务 */
	@Inject
	private ProductService proservice;
	/** 客户服务 */
	@Inject
	private CustomerService custservice;
	/** 角色服务 */
	@Inject
	private RoleService roleService;
	/** 产品驳回历史服务 */
	@Inject
	private ProductRejectLogService productRejectLogService;

	/**
	 * 解析逗号分隔的ID字符串为List&lt;Long&gt;
	 * @param idsJson 逗号分隔的ID字符串
	 * @return ID列表；格式非法（含非数字）时返回null
	 */
	private List<Long> parseIds(String idsJson) {
		try {
			return Arrays.stream(idsJson.split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.map(Long::parseLong)
					.collect(Collectors.toList());
		} catch (NumberFormatException e) {
			return null;
		}
	}

   /**
	* 首页
	*/
	public void index() {
		Long userId = JBoltUserKit.getUserId();
		
		// 报告单子权限：管理员/报告单 角色可覆盖，或直接拥有该子角色
		set("accuracy",  roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_ACCURACY));
		set("leaktest",   roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_LEAK_TEST));
		set("appearance", roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_APPEARANCE));
		set("packaging",  roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_PACKAGING));
		set("approval",   roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_APPROVAL));
		
		render("index.html");
	}

	/**
	 * 获取各流程阶段的数量统计
	 * URL: /admin/siargo/qarep/getFlowCounts
	 * @return 各阶段数量统计
	 */
	public void getFlowCounts() {
		renderJsonData(service.getFlowCounts());
	}
	
	/**
	 * 处理Excel导入
	 * URL: /admin/siargo/qarep/importExcel
	 * <p>Excel导入流程：</p>
	 * <ol>
	 *   <li>接收上传的Excel文件</li>
	 *   <li>验证文件格式为xls或xlsx</li>
	 *   <li>读取Excel内容并解析为数据列表</li>
	 *   <li>提取订单号、型号、编号等关键信息</li>
	 *   <li>返回处理结果供前端表单使用</li>
	 * </ol>
	 */
    public void importExcel() {
        try {
            // 获取上传的文件
            UploadFile uploadFile = getFile();
            if (uploadFile == null) {
                renderJsonFail("请上传excel文件");
                return;
            }
            if(notExcel(uploadFile)){
            	// 非Excel文件早退时删除已上传的临时文件，避免临时目录堆积
            	File tempFile = uploadFile.getFile();
            	if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
            		LOG.warn("Excel导入临时文件删除失败: " + tempFile.getAbsolutePath());
            	}
    			renderJsonFail("请上传excel文件");
    			return;
    		}
            
            File excelFile = uploadFile.getFile();
            
            // 统一入口：自动检测模板类型并提取数据
            Map<String, Object> result = excelservice.processExcelFile(excelFile);
            
            if (result == null || result.isEmpty()) {
                renderFail("Excel文件中没有数据");
                return;
            }
            
            // 返回处理结果
            result.put("success", true);
            renderJsonData(result);
            
        } catch (Exception e) {
            LOG.error("Excel导入失败", e);
            renderFail("导入失败：" + e.getMessage());
            return;
        }
    }
    
	/**
	 * 批量生成上个月已放行报告单的PDF
	 * URL: /admin/siargo/qarep/toPdfs
	 * <p>用于月度归档，将上个月已完成最终放行的报告单批量生成PDF文件</p>
	 * <p>WinRAR 路径从 config.properties 的 winrar_exe_path 配置读取</p>
	 * @throws Exception PDF生成异常
	 */
	public void toPdfs() throws Exception {
	    String pdfsrc = "export/LastMonthPDF";
	    List<Record> records = service.getIds();
	    if (records == null || records.isEmpty()) {
	    	renderFail("无上月数据!");
	    	return;
	    }

	    // ========== 压缩程序配置前置校验（避免PDF生成完才发现无法压缩） ==========
	    String winrarExe = JBoltConfig.prop.get("winrar_exe_path");
	    if (StrKit.isBlank(winrarExe)) {
	    	renderFail("未配置 WinRAR 路径，请在 config.properties 中添加 winrar_exe_path 配置项！");
	    	return;
	    }
	    if (!new File(winrarExe).exists()) {
	    	renderFail("WinRAR 程序不存在：" + winrarExe + "，请检查 winrar_exe_path 配置！");
	    	return;
	    }

	    List<String> failList = new ArrayList<>();
	    for (Record record : records) {
	    	Long id = record.getLong("id");
	        String failMsg = pdfservice.generateReportPdf(id,pdfsrc);
	        if (failMsg != null) {
	        	failList.add(failMsg);
	        }
	    }
	    if (!failList.isEmpty()) {
	    	String outputDir = PathKit.getWebRootPath() + "/" + pdfsrc;
	    	PDFService.writeFailLog(failList, outputDir);
	    }
	    
	    // 压缩PDF文件
	    int lastMonth = LocalDate.now().minusMonths(1).getMonthValue();
	    String rarName = lastMonth + "月报告单.rar";
	    String exportDir = PathKit.getWebRootPath() + "/export/LastMonthPDF/";
	    String rarPath = exportDir + rarName;
	    String srcDir = PathKit.getWebRootPath() + "/export/LastMonthPDF/*";
	    
	    try {
	    	String[] cmd = { winrarExe, "a", "-r", rarPath, srcDir };
	    	ProcessBuilder pb = new ProcessBuilder(cmd);
	    	pb.redirectErrorStream(true);
	    	Process process = pb.start();
	    	try (java.io.BufferedReader reader = new java.io.BufferedReader(
	    	        new java.io.InputStreamReader(process.getInputStream(), "GBK"))) {
	    	    while (reader.readLine() != null) { }
	    	}
	    	int exitCode = process.waitFor();
	    	if (exitCode != 0) {
	    		renderFail("压缩失败，WinRAR 返回码：" + exitCode);
	    		return;
	    	}
	    } catch (Exception e) {
	    	LOG.error("WinRAR 压缩PDF失败", e);
	    	renderFail("压缩失败：" + e.getMessage());
	    	return;
	    }
	    
	    renderJsonSuccess("已完成，PDF已打包为 " + rarName + "，请前往服务器 export 目录查看！");
    }
    
	/**
	 * 批量生成选中报告单的PDF
	 * URL: /admin/siargo/qarep/toPdf
	 * <p>用于日常操作中批量导出选中报告单的PDF文件</p>
	 * @throws Exception PDF生成异常
	 */
	public void toPdf() throws Exception {
		// PDF输出目录：正式PDF目录
	    String pdfsrc = "export/PDF";
		// 解析前端传入的产品ID列表（逗号分隔）
		String idsJson = getPara("ids");
		if (StrKit.isBlank(idsJson)) {
			renderJsonFail("请选择要生成PDF的数据");
			return;
		}
	    List<Long> ids = parseIds(idsJson);
	    if (ids == null || ids.isEmpty()) {
	    	renderJsonFail("参数格式错误");
	    	return;
	    }
	    
	    List<String> failList = new ArrayList<>();
	    for (int i =0; i < ids.size() ; i++) {
	    	String failMsg = pdfservice.generateReportPdf(ids.get(i),pdfsrc);
	    	if (failMsg != null) {
	    		failList.add(failMsg);
	    	}
        }
	    if (!failList.isEmpty()) {
	    	String outputDir = PathKit.getWebRootPath() + "/" + pdfsrc;
	    	PDFService.writeFailLog(failList, outputDir);
	    }
	    
	    service.clearPaginateCache();
	    renderJsonSuccess();
    }
	
	/**
	 * 查询所有客户名称列表
	 * URL: /admin/siargo/qarep/getCustName
	 * <p>用于新增报告单时选择客户</p>
	 */
	public void getCustName() {	
		renderJsonData(custservice.findAll());
	}
	
  	/**
	 * 获取报告单列表数据（分页）
	 * URL: /admin/siargo/qarep/datas
	 * <p>支持按日期范围、产品类型、检验进度筛选</p>
	 */
 	public void datas() {
		Date startTime = null;
		Date endTime = null;
		
		// ========== 解析日期范围参数 ==========
		if (isOk(getPara("dateRange"))) {
			// 日期范围格式：开始日期~结束日期
			String[] dates = getPara("dateRange").split("~");
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (dates.length == 2) {
				try {
					startTime = sdf.parse(dates[0].trim());
					endTime = sdf.parse(dates[1].trim());
				} catch (ParseException e) {
					// 日期解析失败直接返回错误，避免带着null条件继续查询
					renderJsonFail("日期格式错误");
					return;
				}
			}
		}
		
		// 获取产品类型和检验进度筛选条件
		int prodType = getInt("prodType") == null? 0 : getInt("prodType");
		int insp = getInt("insp") == null? 0 : getInt("insp");
		
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),prodType,insp,startTime,endTime));
		
	}
	
   /**
	* 新增
	*/
	public void add() {
		render("add.html");
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		Qareport qareport=service.qareportFindByProId(getLong(0)); 
		if(qareport == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("qapro",qareport);
		render("edit.html");
	}
	
	/**
	 * 编辑产品描述页面
	 * URL: /admin/siargo/qarep/editDes
	 * @param id 产品ID（URL路径参数）
	 */
	public void editDes() {
		// 查询走ProductService，Controller不直接操作Model
		Product product = proservice.findById(getLong(0));
		if(product == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("product",product);
		render("editdes.html");
	}
	
	
	/**
	 * 报告单详情页面
	 * URL: /admin/siargo/qarep/details
	 * @param id 产品ID（请求参数）
	 */
	public void details() {
		Long proId = getParaToLong("id");
		if (proId == null) {
			renderFail("参数错误");
			return;
		}
		Qareport qareport=service.qareportFindByProId(proId); 
		if(qareport == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("qapro",qareport);
		// 查询该报告单下的全部产品
		List<Product> products = service.findProductsByReportId(qareport.getLong("id"));
		set("products", products);
		render("details.html");
	}
	
	/**
	 * 批量检验批准操作
	 * URL: /admin/siargo/qarep/batchInspection
	 * <p>根据检验进度更新不同级别的批准信息：</p>
	 * <ul>
	 *   <li>insp=2：精度检验批准</li>
	 *   <li>insp=6：成品检漏检验批准</li>
	 *   <li>insp=3：外观检验批准</li>
	 *   <li>insp=4：包装检验批准</li>
	 *   <li>insp=5：批准放行</li>
	 * </ul>
	 * <p>服务端会按目标环节校验当前用户角色（211~215，超管豁免），并使用条件更新防并发</p>
	 */
	public void batchInspection() {
        Integer insp = getParaToInt("insp");
        String idsJson = getPara("ids");
        // 参数校验：目标阶段与ID列表必填
        if (insp == null || StrKit.isBlank(idsJson)) {
        	renderJsonFail("参数错误");
        	return;
        }
        List<Long> ids = parseIds(idsJson);
        if (ids == null || ids.isEmpty()) {
        	renderJsonFail("参数格式错误");
        	return;
        }

        // Db.tx() 手动事务 —— 缓存清理和通知均在事务提交后执行
        final Ret[] retHolder = {null};
        boolean txOk = Db.tx(() -> {
            retHolder[0] = service.batchUpdateInspStatus(ids, insp);
            return retHolder[0] != null && retHolder[0].isOk();
        });
        if (!txOk) {
            renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "操作失败");
            return;
        }
        // === afterCommit: 缓存清理 + 异步通知 ===
        service.clearFlowCountsCache();
        service.notifyNextStageUsers(insp);
        String msg = retHolder[0].getStr("msg");
        if (StrKit.notBlank(msg)) {
        	// 部分成功：把未成功的单号信息反馈给前端
        	renderJsonSuccess(msg);
        } else {
        	renderJsonSuccess();
        }
	}
	
	/**
	 * 审批工作台页面（JBoltLayer抽屉iframe加载）
	 * URL: /admin/siargo/qarep/approval
	 * <p>根据目标检验阶段和选中产品ID列表加载待审批产品数据：</p>
	 * <ul>
	 *   <li>insp=2：精度检验批准</li>
	 *   <li>insp=6：成品检漏检验批准</li>
	 *   <li>insp=3：外观检验批准</li>
	 *   <li>insp=4：包装检验批准</li>
	 *   <li>insp=5：批准放行</li>
	 * </ul>
	 */
	public void approval() {
		Integer insp = getParaToInt("insp");
		String idsJson = getPara("ids");

		// 参数校验：目标阶段必须在2~5范围内，且产品ID列表不能为空
		if (insp == null || insp < QarepConst.INSP_APPROVE_MIN || insp > QarepConst.INSP_APPROVE_MAX
				|| StrKit.isBlank(idsJson)) {
			renderFail("参数错误");
			return;
		}

		List<Long> ids = parseIds(idsJson);
		if (ids == null || ids.isEmpty()) {
			renderFail("参数格式错误");
			return;
		}

		List<Record> products = service.findApprovalProducts(ids);
		// 精度审批（insp=2）时，下一环节名称按选中产品的成品检漏标记动态展示：
		// 全有→成品检漏待检，全无→外观待检，混合→“成品检漏/外观待检”
		if (insp == QarepConst.INSP_PENDING_APPEARANCE) {
			boolean anyLt = false;
			boolean allLt = true;
			for (Record p : products) {
				Integer ltStatus = p.getInt("lt_status");
				boolean hasLt = ltStatus != null && ltStatus == QarepConst.LT_STATUS_YES;
				if (hasLt) {
					anyLt = true;
				} else {
					allLt = false;
				}
			}
			set("nextStageName", allLt ? "成品检漏待检" : (anyLt ? "成品检漏/外观待检" : "外观待检"));
		}
		set("insp", insp);
		set("products", products);
		render("approval.html");
	}

	/**
	 * 批量驳回至上一阶段
	 * URL: /admin/siargo/qarep/batchReject
	 * <p>将选中产品的检验进度回退到上一阶段，同时记录驳回原因、驳回人和驳回时间</p>
	 * <p>服务端会按当前环节校验当前用户角色（212~215，超管豁免），并使用条件更新防并发</p>
	 */
	public void batchReject() {
		String idsJson = getPara("ids");
		String rejectDes = getPara("rejectDes");

		// 参数校验：产品ID列表不能为空
		if (StrKit.isBlank(idsJson)) {
			renderJsonFail("请选择要驳回的数据");
			return;
		}
		// 参数校验：驳回原因必填
		if (StrKit.isBlank(rejectDes)) {
			renderJsonFail("请填写驳回原因");
			return;
		}

		List<Long> ids = parseIds(idsJson);
		if (ids == null || ids.isEmpty()) {
			renderJsonFail("参数格式错误");
			return;
		}

		// Db.tx() 手动事务 —— 缓存清理在事务提交后执行
		final String trimmedDes = rejectDes.trim();
		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.batchRejectInspStatus(ids, trimmedDes);
			return retHolder[0] != null && retHolder[0].isOk();
		});
		if (!txOk) {
			renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "操作失败");
			return;
		}
		// === afterCommit: 缓存清理 ===
		service.clearFlowCountsCache();
		String msg = retHolder[0].getStr("msg");
		if (StrKit.notBlank(msg)) {
			renderJsonSuccess(msg);
		} else {
			renderJsonSuccess();
		}
	}

	/**
	 * 产品驳回历史弹窗
	 * URL: /admin/siargo/qarep/rejectHistory?productId=xxx
	 * <p>展示指定产品的全部驳回记录（按时间倒序）</p>
	 */
	public void rejectHistory() {
		Long productId = getParaToLong("productId");
		if (productId == null) {
			renderFail("参数错误");
			return;
		}
		set("logs", productRejectLogService.findLogsByProductId(productId));
		render("reject_history.html");
	}
	
	
   /**
	* 保存
	* <p>Db.tx() 包裹事务：先完成全部参数校验与四个列表（models/numbers/qis/qsis）长度一致性校验，
	* 再统一落库；Service写库失败返回Ret.fail，Db.tx()回滚；缓存清理在事务提交后执行</p>
	*/
	public void save() {
    	
    	String qisJson = getPara("qis");
    	String qsisJson = getPara("qsis");
    	String dessJson = getPara("dess");
    	String modelsJson = getPara("models");
    	String numbersJson = getPara("numbers");

    	// ========== 参数判空（防NPE） ==========
    	if (StrKit.isBlank(qisJson) || StrKit.isBlank(qsisJson)
    			|| StrKit.isBlank(modelsJson) || StrKit.isBlank(numbersJson)) {
    		renderJsonFail("参数不完整，请检查型号/编号/送检数量/检验数量！");
    		return;
    	}

    	if (!qisJson.matches("^[0-9,]*$")) {
    		renderFail("检验数量格式不对，重新输入！");
    		return;
		}
    	
    	if (!qsisJson.matches("^[0-9,]*$")) {
			renderFail("送检数量格式不对，重新输入！");
			return;
		}
    	
    	List<Long> qis;
    	List<Long> qsis;
    	try {
    		qis = Arrays.stream(qisJson.split(","))
	                .map(String::trim)
	                .map(Long::parseLong)
	                .toList();
    		qsis = Arrays.stream(qsisJson.split(","))
	                .map(String::trim)
	                .map(Long::parseLong)
	                .toList();
    	} catch (NumberFormatException e) {
    		renderJsonFail("数量格式不对，重新输入！");
    		return;
    	}
    	
    	final List<String> dess;
    	if (!StrKit.isBlank(dessJson)) {
    		dess = Arrays.stream(dessJson.split(","))
                    .map(String::trim)
                    .map(String::valueOf)
                    .toList();
		} else {
			dess = List.of();
		}

    	List<String> models = Arrays.stream(modelsJson.split(","))
                .map(String::trim)
                .map(String::valueOf)
                .toList();
    	List<String> numbers = Arrays.stream(numbersJson.split(","))
                .map(String::trim)
                .map(String::valueOf)
                .toList();

    	// ========== 四个列表长度一致性校验 ==========
    	int rowCount = models.size();
    	if (rowCount == 0 || numbers.size() != rowCount || qis.size() != rowCount || qsis.size() != rowCount) {
    		renderJsonFail("型号/编号/送检数量/检验数量条数不一致，请检查输入！");
    		return;
    	}
    	
    	Qareport qareport = getModel(Qareport.class, "qareport");
    	Product product = getModel(Product.class, "product");

    	// ========== 报告单基础信息服务端校验（前端 data-rule 可绕过，必须服务端兜底） ==========
    	if (qareport == null) {
    		renderJsonFail("参数不完整，请检查报告单信息！");
    		return;
    	}
    	// 防注入：新增场景不允许携带报告单ID（若前端注入已有ID，会跳过创建直接绑定/覆盖已有报告单）
    	if (isOk(qareport.getId())) {
    		renderJsonFail("参数错误：新增报告单不允许携带报告单ID！");
    		return;
    	}
    	// 订单号必填（PDF 文件名以订单号拼接，空值会导致输出文件异常；列类型为数字，天然无路径穿越风险）
    	Long orderId = qareport.getOrderId();
    	if (orderId == null || orderId <= 0) {
    		renderJsonFail("订单号必填！");
    		return;
    	}
    	// 客户必填
    	if (notOk(qareport.getCustId())) {
    		renderJsonFail("请选择客户！");
    		return;
    	}

    	// ========== 产品公共参数校验 ==========
    	if (product.getInsp() == null) {
    		renderJsonFail("请选择检验进度！");
    		return;
    	}
    	if (product.getInsp() > QarepConst.INSP_PENDING_APPEARANCE) {
    		renderJsonFail("未检验精度，请重新选择检验进度！");
    		return;
    	}
    	if (product.getFlowRange() == null && product.getType() != null
    			&& product.getType() == QarepConst.PROD_TYPE_LARGE_FLOW) {
			renderFail("请输入流量范围！");
			return;
		}
    	if (product.getCuc() != null && (product.getCuc() < 6 || product.getCuc() > 25)) {
			renderFail("整机最大电流6-24mA，请重新输入！");
			return;
		}
		if (product.getCucmin() != null && product.getCucmin() > 31) {
			renderFail("整机电流超过30mA，请重新输入！");
			return;
		}
		if (product.getCucmax() != null && product.getCucmax() > 21) {
			renderFail("整机电流超过20mA，请重新输入！");
			return;
		}
		if (product.getPv() != null && (product.getPv() < 2.7 || product.getPv() > 99.999)) {
			renderFail("脉冲电压低于2.7V或超上限99.999，请重新输入！");
			return;
		}
		if (product.getZp() != null && product.getZp() > 30) {
			renderFail("零点内码超过30，请重新输入！");
			return;
		}
		if (product.getFl() != null && (product.getFl() < 2.7 || product.getFl() > 99.99)) {
			renderFail("故障电平低于2.7V或超上限99.99，请重新输入！");
			return;
		}
		if (product.getBv() != null && (product.getBv() > 3.3495 || product.getBv() < 3.2505)) {
			renderFail("电池电压3.2505V-3.3495V，请重新输入！");
			return;
		}
		if (product.getLa() != null && product.getLa() > 50) {
			renderFail("本地地址超过50，请重新输入！");
			return;
		}
		if (product.getThv() != null && product.getThv() > 1690) {
			renderFail("热头电压超过1690，请重新输入！");
			return;
		}

		// ========== 逐行预校验：送检数量不能小于检验数量（全部通过后才开始落库） ==========
		for (int i = 0; i < rowCount; i++) {
			if (qsis.get(i) < qis.get(i)) {
				renderFail("送检数量小于检验数量，重新输入！");
				return;
			}
		}

		// ========== 校验全部通过，统一落库（Db.tx() 包裹事务） ==========
		final Ret[] retHolder = {Ret.ok()};
		boolean txOk = Db.tx(() -> {
			for (int i = 0; i < rowCount; i++) {
				product.setQi(qis.get(i).intValue());
				product.setQsi(qsis.get(i).intValue());
				product.setModel(models.get(i));
				product.setNumber(numbers.get(i));

				if (i < dess.size()) {
					product.setDes(dess.get(i));
				}
				Ret ret = service.save(qareport, product);
				if (ret.isFail()) {
					retHolder[0] = ret;
					return false; // 触发回滚
				}
			}
			return true;
		});
		if (!txOk) {
			renderJsonFail(retHolder[0].getStr("msg") != null ? retHolder[0].getStr("msg") : "保存失败");
			return;
		}
		// === afterCommit: 缓存清理 ===
		service.clearFlowCountsCache();
    	renderJsonSuccess();
	}
	
   /**
	* 更新
	* <p>Db.tx() 包裹事务，更新成功后清缓存</p>
	*/
	public void update() {
		Qareport qareport = getModel(Qareport.class, "qareport");
		Product product = getModel(Product.class, "product");
		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.update(qareport, product);
			return retHolder[0] != null && retHolder[0].isOk();
		});
		if (txOk) {
			// === afterCommit: 缓存清理 ===
			service.clearFlowCountsCache();
		}
		renderJson(retHolder[0] != null ? retHolder[0] : Ret.fail("更新失败"));
	}
    
    /**
	* 更新Des
	* <p>Db.tx() 包裹事务，更新成功后清分页缓存</p>
	*/
	public void updateDes() {
		Product prold = getModel(Product.class, "product");
		if (prold == null || notOk(prold.getId())) {
			renderJsonFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.updateDes(prold.getId(), prold.getDes());
			return retHolder[0] != null && retHolder[0].isOk();
		});
		if (txOk) {
			service.clearPaginateCache();
		}
		renderJson(retHolder[0] != null ? retHolder[0] : Ret.fail("更新失败"));
	}
    
   /**
	* 删除（软删除到回收站）
	*/
	public void deleteByIds() {
		String idsJson = getPara("ids");
		if (StrKit.isBlank(idsJson)) {
			renderFail("请选择要删除的数据");
			return;
		}
		String deleteDes = getPara("delete_des"); // 删除原因
		// 服务端强制必填（前端弹窗已校验，此处兜底防绕过）
		if (StrKit.isBlank(deleteDes)) {
			renderJsonFail("请填写删除原因！");
			return;
		}

		List<Long> ids = parseIds(idsJson);
		if (ids == null || ids.isEmpty()) {
			renderFail("请选择要删除的数据");
			return;
		}

		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.batchSoftDeleteProduct(ids, deleteDes);
			return retHolder[0] != null && retHolder[0].isOk();
		});
		if (!txOk) {
			renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "删除失败");
			return;
		}
		// === afterCommit: 缓存清理 ===
		service.clearFlowCountsCache();
		renderJsonSuccess();
	}

	/**
	 * 回收站列表页面（服务端渲染 + JBolt原生分页）
	 * URL: /admin/siargo/qarep/inactiveList
	 */
	public void inactiveList() {
		render("inactiveList.html");
	}

	/**
	 * 回收站列表AJAX数据接口（用于dialog弹窗内搜索和分页）
	 * URL: /admin/siargo/qarep/inactiveDatas
	 */
	public void inactiveDatas() {
		renderJsonData(service.paginateInactiveListDatas(getPageNumber(), getPageSize(), getKeywords()));
	}

	/**
	 * 恢复报告单（从回收站还原）
	 * URL: /admin/siargo/qarep/restore/:id
	 */
	public void restore() {
		Long id = getLong(0);
		if (id == null) {
			renderFail("参数错误");
			return;
		}
		final boolean[] successHolder = {false};
		boolean txOk = Db.tx(() -> {
			successHolder[0] = service.restoreProduct(id);
			return successHolder[0];
		});
		if (!txOk || !successHolder[0]) {
			renderFail("数据不存在");
			return;
		}
		// === afterCommit: 缓存清理 ===
		service.clearFlowCountsCache();
		renderJsonSuccess();
	}

	/**
	 * 永久删除报告单（物理删除）
	 * URL: /admin/siargo/qarep/permanentDelete
	 * <p>afterCommit 模式：物理文件删除不可回滚，必须在事务提交后执行——</p>
	 * <ol>
	 *   <li>事务外收集 PDF 物理文件路径（getPdfPathsByIds）</li>
	 *   <li>Db.tx() 仅删除数据库记录（驳回历史/产品/空报告单，Service.permanentDelete）</li>
	 *   <li>事务提交成功后统一删除物理文件（deletePhysicalPdfs）+ 清理缓存</li>
	 * </ol>
	 */
	public void permanentDelete() {
		String idsJson = getPara("ids");
		if (StrKit.isBlank(idsJson)) {
			renderFail("参数错误");
			return;
		}
		List<Long> ids = parseIds(idsJson);
		if (ids == null || ids.isEmpty()) {
			renderFail("参数格式错误");
			return;
		}
		// 1. 事务外收集 PDF 物理文件路径（文件删除不可回滚，见规范 6.3）
		List<String> pdfPaths = service.getPdfPathsByIds(ids);
		// 2. 手动事务：仅删除数据库记录
		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.permanentDelete(ids);
			return retHolder[0] != null && retHolder[0].isOk();
		});
		if (!txOk) {
			renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "删除失败");
			return;
		}
		// 3. === afterCommit: 缓存清理 + 物理文件删除 ===
		service.clearFlowCountsCache();
		service.deletePhysicalPdfs(pdfPaths);
		renderJsonSuccess();
	}

}
