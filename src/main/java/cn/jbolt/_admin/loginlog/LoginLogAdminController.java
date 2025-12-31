package cn.jbolt._admin.loginlog;

import java.util.Date;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.base.JBoltPageSize;
import cn.jbolt.core.bean.JBoltDateRange;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.LoginLog;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import cn.jbolt.core.util.JBoltDateUtil;
/**
 * 登录日志controller
 * @ClassName:  LoginLogAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年5月12日   
 */
@CheckPermission(PermissionKey.JBOLT_LOGIN_LOG)
@UnCheckIfSystemAdmin
public class LoginLogAdminController extends JBoltBaseController {
	@Inject
	private LoginLogService service;
	public void index() {
		JBoltDateRange dateRange=getDateRange();
		Date startTime=dateRange.getStartDate(JBoltDateUtil.getYesterday());
		Date endTime=dateRange.getEndDate(JBoltDateUtil.getNowDate());
		String keywords=getKeywords();
		set("pageData", service.paginateAdminList(getPageNumber(),getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST_30),keywords,startTime,endTime));
		setKeywords(keywords);
		setDateRange(dateRange,JBoltDateUtil.format(startTime, JBoltDateUtil.YMD)+" ~ "+JBoltDateUtil.format(endTime, JBoltDateUtil.YMD));
		render("index.html");
	}
	/**
	 * 查看详情 
	 * Jboltlayer样式内容
	 */
	public void detail() {
		Long id=getLong(0);
		if(notOk(id)){
			renderAjaxPortalFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		LoginLog log=service.findById(id);
		if(log == null) {
			renderAjaxPortalFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("log", log);
		render("detail.html");
		
	}
}
