package cn.jbolt._admin.onlineuser;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.common.enums.JBoltUserOnlineState;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;

/**
 * 在线用户管理 Controller
 * @ClassName: OnlineUserAdminController   
 * @author: JBolt-Generator
 * @date: 2021-03-18 23:00  
 */
@CheckPermission(PermissionKey.ONLINE_USER)
@UnCheckIfSystemAdmin
public class OnlineUserAdminController extends JBoltBaseController {

	@Inject
	private OnlineUserService service;
	
   /**
	* 首页
	*/
	public void index() {
		render("index.html");
	}
  	
  	/**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),getState()));
	}
	/**
	 * 状态选项
	 */
	public void stateOptions() {
		renderJsonData(JBoltEnum.getEnumOptionList(JBoltUserOnlineState.class));
	}
	
	/**
	 * 强退用户
	 */
	public void forcedOffline() {
		renderJson(service.forcedOffline(getLong(0)));
	}
	
	
}