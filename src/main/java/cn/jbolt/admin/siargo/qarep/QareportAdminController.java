package cn.jbolt.admin.siargo.qarep;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.JBoltUserAuthKit;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.admin.siargo.customer.CustomerService;
import cn.jbolt.common.util.DateUtil;
import cn.jbolt.common.util.StringUtil;

import com.jfinal.core.Path;
import com.jfinal.kit.StrKit;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Customer;
import cn.jbolt.siargo.model.Product;
import cn.jbolt.siargo.model.Qareport;
/**
 * 检验报告单管理 Controller
 * @ClassName: QareportAdminController
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
@CheckPermission(PermissionKey.SIARGO)
@Path(value = "/admin/siargo/qarep", viewPath = "/_view/admin/siargo/qarep")

public class QareportAdminController extends JBoltBaseController {

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
	
   /**
	* 首页
	*/
	public void index() {
		//批准权限
		Long approvalRoleId = roleService.findIdByName("批准");
		boolean has = approvalRoleId != null && JBoltUserAuthKit.hasRole(JBoltUserKit.getUserId(), approvalRoleId);
		
		set("approval", has);
		render("index.html");
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
            if(notExcel(uploadFile)){
    			renderJsonFail("请上传excel文件");
    			return;
    		}
            
            File excelFile = uploadFile.getFile();
            
            // 读取Excel文件
            List<Map<String, Object>> dataList = excelservice.readExcel(excelFile);
            
            if (dataList == null || dataList.isEmpty()) {
                renderFail("Excel文件中没有数据");
                return;
            }
            
            // 处理数据，提取所需信息
            Map<String, Object> result = excelservice.processExcelData(dataList);
            
            // 返回处理结果
            result.put("success", true);
            renderJsonData(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            renderFail("导入失败：" + e.getMessage());
            return;
        }
    }
    
	/**
	 * 批量生成上个月已放行报告单的PDF
	 * URL: /admin/siargo/qarep/toPdfs
	 * <p>用于月度归档，将上个月已完成最终放行的报告单批量生成PDF文件</p>
	 * @throws Exception PDF生成异常
	 */
	public void toPdfs() throws Exception {
	    String pdfsrc = "export/LastMonthPDF";
	    List<Record> records = service.getIds();
	    if (records == null || records.isEmpty()) {
	    	renderFail("无上月数据!");
	    	return;
	    }
	    for (Record record : records) {
	    	Long id = record.getLong("id");
	        pdfservice.generateReportPdf(id,pdfsrc);
	    }
	    
	    renderJsonSuccess("已完成，前往服务器桌面查看！");
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
	    List<Long> ids = Arrays.stream(idsJson.split(","))
	            .map(String::trim)
	            .map(Long::parseLong)
	            .collect(Collectors.toList());
	    
	    for (int i =0; i < ids.size() ; i++) {
	    	pdfservice.generateReportPdf(ids.get(i),pdfsrc);
        }
			
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
					e.printStackTrace();
					renderError(0, "日期格式错误");
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
		Product product = getModel(Product.class, "product").findById(getLong(0));
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
		Qareport qareport=service.qareportFindByProId(Long.parseLong(getPara("id"))); 
		if(qareport == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("qapro",qareport);
		render("details.html");
	}
	
	/**
	 * 批量检验批准操作
	 * URL: /admin/siargo/qarep/batchInspection
	 * <p>根据检验进度更新不同级别的批准信息：</p>
	 * <ul>
	 *   <li>insp=2：精度检验批准</li>
	 *   <li>insp=3：功能检验批准</li>
	 *   <li>insp=4：批准检验</li>
	 *   <li>insp=5：最终放行</li>
	 * </ul>
	 */
	public void batchInspection() {
        Integer insp = getParaToInt("insp");
        String idsJson = getPara("ids");
        
        // 将字符串转换为List<Long>
        List<Long> ids = Arrays.stream(idsJson.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());

            for (int i =0; i < ids.size() ; i++) {
            	Product product = proservice.findById(ids.get(i));
                if (product != null) {
                	if(insp == 2) {
                		product.set("accq_uid", JBoltUserKit.getUserId());
                		product.set("accq_time", DateUtil.getDateString(DateUtil.YMDHMS));
                	}else if(insp == 3){
                		product.set("funq_uid", JBoltUserKit.getUserId());
                		product.set("funq_time", DateUtil.getDateString(DateUtil.YMDHMS));
					}else if(insp == 4){
						product.set("appq_uid", JBoltUserKit.getUserId());
						product.set("appq_time", DateUtil.getDateString(DateUtil.YMDHMS));
					}else if(insp == 5){
						product.set("allq_uid", JBoltUserKit.getUserId());
						product.set("allq_time", DateUtil.getDateString(DateUtil.YMDHMS));
					}
                	product.set("insp", insp);
                	product.update();
                }
            }
            renderJsonSuccess();
	}
	
	
   /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
    	
    	String qisJson = getPara("qis");
    	String qsisJson = getPara("qsis");
    	String dessJson = getPara("dess");
    	String modelsJson = getPara("models");
    	String numbersJson = getPara("numbers");

    	if (!qisJson.matches("^[0-9,]*$")) {
    		renderFail("检验数量格式不对，重新输入！");
    		return;
		}
    	
    	if (!qsisJson.matches("^[0-9,]*$")) {
			renderFail("送检数量格式不对，重新输入！");
			return;
		}
    	
    	List<Long> qis = Arrays.stream(qisJson.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    	List<Long> qsis = Arrays.stream(qsisJson.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    	
    	List<String> dess = new ArrayList<String>();
    	if (!StrKit.isBlank(dessJson)) {
    		dess = Arrays.stream(dessJson.split(","))
                    .map(String::trim)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
		}

    	List<String> models = Arrays.stream(modelsJson.split(","))
                .map(String::trim)
                .map(String::valueOf)
                .collect(Collectors.toList());
    	List<String> numbers = Arrays.stream(numbersJson.split(","))
                .map(String::trim)
                .map(String::valueOf)
                .collect(Collectors.toList());
    	
    	Qareport qareport = getModel(Qareport.class, "qareport");
    	Product product = getModel(Product.class, "product");
    	
    	if (product.getFlowRange() == null && product.getType() == 3) {
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

    	for (int i = 0; i < models.size() && i < numbers.size(); i++) {
    		
    		if (qsis.get(i) < qis.get(i)) {
    			renderFail("送检数量小于检验数量，重新输入！");
    			return;
    		}
    		
    		product.setQi(qis.get(i));
    		product.setQsi(qsis.get(i));
    		product.setModel(models.get(i));
    		product.setNumber(numbers.get(i));
    		
    		if (i>= 0 && i < dess.size()) {
        		product.setDes(dess.get(i));
			}
    		service.save(qareport,product);
        }
    	renderJsonSuccess();
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Qareport.class, "qareport"),getModel(Product.class, "product")));
	}
    
    /**
	* 更新Des
	*/
    @Before(Tx.class)
	public void updateDes() {
    	Product prold = getModel(Product.class, "product");

    	Product product = proservice.findById(prold.getId());
    	if(product == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}

    	product.setDes(StringUtil.isEmpty(prold.getDes())? "" : prold.getDes().trim());
		renderJsonData(product.update());
	}
    
   /**
	* 删除（软删除到回收站）
	*/
    @Before(Tx.class)
	public void deleteByIds() {
    	String idsJson = getPara("ids");
    	if (StrKit.isBlank(idsJson)) {
    		renderFail("请选择要删除的数据");
    		return;
    	}
    	String deleteDes = getPara("delete_des"); // 删除原因，可为空

        List<Long> ids = Arrays.stream(idsJson.split(","))
                .filter(s -> !s.trim().isEmpty())
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
        	renderFail("请选择要删除的数据");
        	return;
        }

        for (Long id : ids) {
        	Product product = proservice.findById(id); 
            if (product != null) {
            	product.set("delete_time", DateUtil.getDateString(DateUtil.YMDHMS));
            	product.set("vd", 0);
            	product.set("delete_des", deleteDes);
            	product.update();
            }
        }
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
		Product product = proservice.findById(id);
		if (product == null) {
			renderFail("数据不存在");
			return;
		}
		product.set("vd", 1);
		product.set("delete_time", null);
		product.set("delete_des", null);
		renderJsonData(product.update());
	}

	/**
	 * 永久删除报告单（物理删除）
	 * URL: /admin/siargo/qarep/permanentDelete
	 */
	@Before(Tx.class)
	public void permanentDelete() {
		String idsJson = getPara("ids");
		if (StrKit.isBlank(idsJson)) {
			renderFail("参数错误");
			return;
		}
		List<Long> ids = Arrays.stream(idsJson.split(","))
				.map(String::trim)
				.map(Long::parseLong)
				.collect(Collectors.toList());
		for (Long id : ids) {
			Product product = proservice.findById(id);
			if (product != null) {
				// 删除前获取关联信息用于日志记录
				String logDesc = "";
				Qareport qareport = service.findById(product.getReportId());
				if (qareport != null) {
					String formnum = qareport.getFormnum() != null ? String.valueOf(qareport.getFormnum()) : "";
					String orderId = qareport.getOrderId() != null ? String.valueOf(qareport.getOrderId()) : "";
					String model = product.getModel() != null ? product.getModel() : "";
					String number = product.getNumber() != null ? product.getNumber() : "";
					String customerName = "";
					if (qareport.getCustId() != null) {
						Customer customer = custservice.findById(qareport.getCustId());
						if (customer != null) {
							customerName = customer.getName();
						}
					}
					String deleteDes = product.getDeleteDes() != null ? product.getDeleteDes() : "";
					logDesc = " 报告单编号：" + formnum + " ==订单号：" + orderId + " ==型号：" + model + " ==编号：" + number + " ==客户：" + customerName + " ==删除原因：" + deleteDes;
				}
				product.delete();
				// 记录永久删除操作日志
				service.logDelete(id, JBoltUserKit.getUserId(), logDesc);
			}
		}
		renderJsonSuccess();
	}

}
