package cn.jbolt._admin.systemlog;

import java.util.Date;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltPageSize;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import cn.jbolt.core.util.JBoltDateUtil;
@CheckPermission(PermissionKey.SYSTEMLOG)
@UnCheckIfSystemAdmin
public class SystemLogAdminController extends JBoltBaseController {
	@Inject
	private SystemLogService service;
	public void index() {
		Date startTime=getDate("startTime",JBoltDateUtil.getYesterday());
		Date endTime=getDate("endTime",JBoltDateUtil.getNowDate());
		String keywords=getKeywords();
		set("pageData", service.paginateAdminList(getPageNumber(),getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST_30),keywords,startTime,endTime));
		set("startTime", startTime);
		set("endTime", endTime);
		setKeywords(keywords);
		render("index.html");
	}
}
