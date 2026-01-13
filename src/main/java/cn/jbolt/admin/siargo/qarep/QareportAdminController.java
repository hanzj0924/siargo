package cn.jbolt.admin.siargo.qarep;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.siargo.customer.CustomerService;
import cn.jbolt.admin.siargo.dict.DictionarytypeService;
import cn.jbolt.admin.siargo.siargoutil.SiargoUtil;

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
import com.jfinal.plugin.activerecord.tx.Tx;
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
@Path(value = "/admin/siargo/qarep", viewPath = "/_view/admin/siargo/qarep")

public class QareportAdminController extends JBoltBaseController {

	@Inject
	private QareportService service;
	@Inject
	private PDFService pdfservice;
	@Inject
	private ExcelService excelservice;
	@Inject
	private ProductService proservice;
	@Inject
	private CustomerService custservice;
	@Inject
	private DictionarytypeService dicttypeservice;
	
   /**
	* 首页
	*/
	public void index() {
				
		render("index.html");
	}

	/**
     * 处理Excel导入
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
	 * 生成PDF
	 */
	public void toPdf() throws Exception {
	    String pdfsrc = "PDF";
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
	* 查询所有客户名称
	*/
	public void getCustName() {	
		renderJsonData(custservice.findAll());
	}
	
	
	/**
	* 查询数据字典
	*/
	public void getDict() {	
		renderJsonData(dicttypeservice.getDictName(getKeywords()));
	}
  	
  	/**
	* 数据源
	*/
	public void datas() {
		Date startTime = null;
		Date endTime = null;
		
		if (isOk(getPara("dateRange"))) {
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
	* 报告单详情
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
	* 检验批准
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
                		product.set("accq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
                	}else if(insp == 3){
                		product.set("funq_uid", JBoltUserKit.getUserId());
                		product.set("funq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
					}else if(insp == 4){
						product.set("appq_uid", JBoltUserKit.getUserId());
						product.set("appq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
					}else if(insp == 5){
						product.set("allq_uid", JBoltUserKit.getUserId());
						product.set("allq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
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
    	String modlesJson = getPara("modles");
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

    	List<String> modles = Arrays.stream(modlesJson.split(","))
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

    	for (int i = 0; i < modles.size() && i < numbers.size(); i++) {
    		
    		if (qsis.get(i) < qis.get(i)) {
    			renderFail("送检数量小于检验数量，重新输入！");
    			return;
    		}
    		
    		product.setQi(qis.get(i));
    		product.setQsi(qsis.get(i));
    		product.setModle(modles.get(i));
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
	* 删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
    	String idsJson = getPara("ids");

        List<Long> ids = Arrays.stream(idsJson.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());

            for (Long id : ids) {
            	Product product = proservice.findById(id); 
                if (product != null) {
                	product.set("delete_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
                	product.set("vd", 0);
                	product.update();
                }
            }
            renderJsonSuccess();
	}
	
	
}
