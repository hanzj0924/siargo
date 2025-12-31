package cn.jbolt._admin.topnav;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.Permission;
import cn.jbolt.core.model.Topnav;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;

@CheckPermission(PermissionKey.TOPNAV)
@UnCheckIfSystemAdmin
public class TopnavMenuAdminController extends JBoltBaseController {
	@Inject
	private PermissionService permissionService;
	@Inject
	private TopnavMenuService service;
	@Inject
	private TopnavService topnavService;
	/**
	 * 设置导航菜单
	 */
	public void setting() {
		Long topnavId = getLong(0);
		if (notOk(topnavId)) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Topnav topnav = topnavService.findById(topnavId);
		if (topnav == null) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		List<Permission> permissions = permissionService.getLevel1MenusJoinTopnavMenu();
		set("dataList", permissions);
		set("topnavId", topnavId);
		render("editleftmenus.html");
	}

	/**
	 * 获取左侧导航选中数据
	 */
	public void getChecked() {
		renderJsonData(service.getCheckedLeftMenus(getLong(0)));
	}
	/**
	 * 清空一个顶部导航下的菜单配置
	 */
//	@Before(Tx.class)
//	public void clear() {
//		renderJson(service.clearTopnavMenus(getLong(0)));
//	}
	/**
	 * 清空所有顶部导航下的菜单配置
	 */
//	@Before(Tx.class)
//	public void clearAll() {
//		renderJson(service.clearAllTopnavMenus());
//	}
	/**
	 * 提交一个顶部导航下的菜单配置
	 */
	@Before(Tx.class)
	public void submit() {
		Long topnavId=getLong("topnavId");
		String permissionStr=get("permissions");
		renderJson(service.submit(topnavId,permissionStr));
	}
}
