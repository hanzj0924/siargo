package cn.jbolt.admin.siargo.apicalllog;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.kit.Kv;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.ApiCallLog;
/**
 * API调用记录 Controller
 * @ClassName: ApiCallLogAdminController
 * @author: hanzj
 * @date: 2026-04-21 16:46
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/apicalllog", viewPath = "/_view/admin/siargo/apicalllog")
//true
public class ApiCallLogAdminController extends JBoltBaseController {

	@Inject
	private ApiCallLogService service;
	
   /**
	* 首页
	*/
	public void index() {
		render("index.html");
	}
  	
  	/**
	* 数据源 - 支持筛选
	*/
	public void datas() {
		String responseStatus = get("responseStatus");
		String startDate = get("startDate");
		String endDate = get("endDate");
		renderJsonData(service.paginateAdminDatas(getPageNumber(), getPageSize(),
			getKeywords(), responseStatus, startDate, endDate));
	}
	
   /**
	* 只读详情
	*/
	public void detail() {
		ApiCallLog apiCallLog = service.findById(getLong(0)); 
		if(apiCallLog == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("apiCallLog", apiCallLog);
		render("detail.html");
	}
	
   /**
	* 统计数据 JSON（供前端 AJAX 异步加载）
	*/
	public void statsData() {
		Kv data = Kv.create();
		data.set("callStats", service.getCallStats());
		data.set("dailyStats", service.getDailyStats());
		renderJsonData(data);
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	

}
