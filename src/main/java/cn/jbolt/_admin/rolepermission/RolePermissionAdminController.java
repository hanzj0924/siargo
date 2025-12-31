package cn.jbolt._admin.rolepermission;

import java.util.List;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.Permission;
import cn.jbolt.core.model.Role;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
@CheckPermission(PermissionKey.ROLE)
@UnCheckIfSystemAdmin
public class RolePermissionAdminController extends JBoltBaseController {
	@Inject
	private RolePermissionService service;
	@Inject
	private PermissionService permissionService;
	@Inject
	private RoleService roleService;
	/**
	 * 进入角色分配资源的界面
	 */
	public void setting(){
		initSetting();
		render("setting.html");
	}
	/**
	 * 进入角色分配资源的界面 tree模式
	 */
	public void settingTree(){
		initSetting();
		render("setting_tree.html");
	}
	private void initSetting() {
		Long roleId=getLong(0);
		if(notOk(roleId)) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Role role=roleService.findById(roleId);
		if(role==null) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		List<Permission> permissions=null;
		if(isOk(role.getPid())) {
			permissions=permissionService.getParentPermissionsWithLevel(role.getPid());
		}else {
			permissions=permissionService.getAllPermissionsWithLevel();
		}
		
		set("dataList", permissions);
		set("roleId", roleId);
	}
	/**
	 * 提交角色分配资源变更
	 */
	public void submit(){
		Long roleId=getLong("roleId");
		String permissionStr=get("permissions");
		renderJson(service.doSubmit(roleId,permissionStr));
	}
	
	/**
	 *  获取角色已经设置的资源
	 */
	public void getCheckeds(){
		renderJsonData(service.getListByRole(getLong(0)));
	}
	/**
	 * 根据角色ID去清空此角色绑定的所有权限
	 */
	public void clear() {
		renderJson(service.deleteByRole(getLong(0)));
	}
}
