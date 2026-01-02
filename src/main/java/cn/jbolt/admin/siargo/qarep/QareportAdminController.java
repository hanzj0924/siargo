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
            // 1. 获取上传的文件
            UploadFile uploadFile = getFile();
            if(notExcel(uploadFile)){
    			renderJsonFail("请上传excel文件");
    			return;
    		}
            
            File excelFile = uploadFile.getFile();
            
            // 2. 读取Excel文件
            List<Map<String, Object>> dataList = excelservice.readExcel(excelFile);
            
            if (dataList == null || dataList.isEmpty()) {
                renderFail("Excel文件中没有数据");
                return;
            }
            
            // 3. 处理数据，提取所需信息
            Map<String, Object> result = excelservice.processExcelData(dataList);
            
            // 4. 返回处理结果
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
		// PDF禁止缓存
	    getResponse().setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	    getResponse().setHeader("Pragma", "no-cache");
	    getResponse().setDateHeader("Expires", 0);
	    
		String idsJson = getPara("ids");
	    List<Long> ids = Arrays.stream(idsJson.split(","))
	            .map(String::trim)
	            .map(Long::parseLong)
	            .collect(Collectors.toList());
	    for (int i =0; i < ids.size() ; i++) {
	    	pdfservice.generateReportPdf(ids.get(i),"PDF");
        }
			
	    renderJsonSuccess();
    }
	
	public void accqHtml() {
		
		StringBuilder html = new StringBuilder();
        
        // HTML 头部和基础样式
        html.append("<!DOCTYPE html>");
        html.append("<html lang='zh-CN'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>小流量计出厂检验项目</title>");
        html.append("<style>");
        html.append("body { font-family: 'Microsoft YaHei', Arial, sans-serif; margin: 20px; }");
        html.append("table { width: 100%; border-collapse: collapse; border: 2px solid #333; margin-top: 20px; }");
        html.append("th, td { border: 1px solid #333; padding: 8px 12px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; font-weight: bold; text-align: center; }");
        html.append(".table-header { background-color: #e6e6e6; font-weight: bold; }");
        html.append("h2 { text-align: center; color: #333; }");
        html.append(".sub { vertical-align: sub; font-size: smaller; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        
        // 标题
        html.append("<h2>小流量计出厂检验项目 SIARRE-QC12-02 G/1 </h2>");
        
        // 表格开始
        html.append("<table>");
        
        // 表头
        html.append("<tr class='table-header'>");
        html.append("<th width='10%'>序号</th>");
        html.append("<th width='30%'>检验项目</th>");
        html.append("<th width='60%'>标准、要求</th>");
        html.append("</tr>");
        
        // 数据行
        html.append("<tr>");
        html.append("<td>1</td>");
        html.append("<td>参数设置（软件版本、最大流量、气体系数、流量单位）</td>");
        html.append("<td>见订单及对应产品检验指导书（订单无特殊要求均为默认值，结果查询生产管理系统产品记录）</td>");
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>2</td>");
        html.append("<td>气密性</td>");
        html.append("<td>最大工作压力5min无泄漏(结果查询生产管理系统产品打压信息)</td>");
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>3</td>");
        html.append("<td>精度/误差</td>");
        html.append("<td rowspan='2'>见附表一</td>");
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>4</td>");
        html.append("<td>重复性E<span class='sub'>r</span>=(E<span class='sub'>max</span>-E<span class='sub'>min</span>)/d<span class='sub'>n</span></td>");
        // 注意：这一行的第三列因为与上一行合并，所以不单独写
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>5</td>");
        html.append("<td>GDCF值</td>");
        html.append("<td>GDCF=1000(A/N/O); 特殊气体GDCF值按照《MEMS气体质量流量计特殊气体校准作业规范》</td>");
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>6</td>");
        html.append("<td>高、低报警（MF52XX）</td>");
        html.append("<td>高：maxSLMP、低：0SLMP（见工装指示灯亮）</td>");
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>7</td>");
        html.append("<td>温度显示(MF57XX)</td>");
        html.append("<td>±3℃</td>");
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>8</td>");
        html.append("<td>气体类型/控制方式（BC/MFC1000）</td>");
        html.append("<td>(Air:0 Ar:1 CO2:2 N2:3 O2:4 N2:5 H2:6 He:7) (模拟:0 数字:2)</td>");
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>9</td>");
        html.append("<td>外观（含显示盒、连接线）</td>");
        html.append("<td>结构吻合良好，螺钉紧固，滤网安装正确平整、洁净，外观洁净，无损伤，标签、标示正确、完好。</td>");
        html.append("</tr>");
        
        html.append("<tr>");
        html.append("<td>10</td>");
        html.append("<td>包装</td>");
        html.append("<td>产品配线、电源适配器等包装配件齐备，产品标签、合格证书、说明书、产品质量跟踪卡、装箱单等随机文件齐备（特殊包装要求见订单）</td>");
        html.append("</tr>");
        
        html.append("</table>");
        
        html.append("</body>");
        html.append("</html>");
		renderHtml(html.toString());
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
                		//传感器没有成品检验，直接包装待检
                		if (product.getType() == 1) {
                			product.set("insp", 3);
						} else {
							product.set("insp", insp);
						}
                		product.set("accq_uid", JBoltUserKit.getUserId());
                		product.set("accq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
                	}else if(insp == 3){
                		product.set("insp", insp);
                		product.set("funq_uid", JBoltUserKit.getUserId());
                		product.set("funq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
					}else if(insp == 4){
						product.set("insp", insp);
						product.set("appq_uid", JBoltUserKit.getUserId());
						product.set("appq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
					}else if(insp == 5){
						product.set("insp", insp);
						product.set("allq_uid", JBoltUserKit.getUserId());
						product.set("allq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
					}
                		
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
    	int i = 0;
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

    	for (; i < modles.size() && i < numbers.size(); i++) {
    		
    		if (qsis.get(i) < qis.get(i)) {
    			renderFail("送检数量应大于检验数量，重新输入！");
    			return;
    		}

    		product.setQi(qis.get(i));
    		product.setQsi(qsis.get(i));
    		product.setModle(modles.get(i));
    		product.setNumber(numbers.get(i));
    		
    		if (dess.size()>0) {
    			if(!StrKit.isBlank(dess.get(i))) {
        			product.setDes(dess.get(i));
    			}else {
    				product.setDes(null);
    			}
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
