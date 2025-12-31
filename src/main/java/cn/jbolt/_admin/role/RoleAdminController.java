package cn.jbolt._admin.role;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.Role;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
@CheckPermission(PermissionKey.ROLE)
@UnCheckIfSystemAdmin
public class RoleAdminController extends JBoltBaseController {
	@Inject
	private RoleService service;
	@Inject
	private UserService userService;
	/**
	 * 管理首页
	 */
	public void index(){
		render("index.html");
	}

	public void datas() {
		renderJsonData(service.getAllRoleTreeDatas());
	}
	@UnCheck
	public void options(){
		renderJsonData(service.getAllRoleTreeDatas());
	}
	/**
	 * 查询role上所有用户列表进入页面
	 */
	public void users() {
		set("roleId", getLong(0));
		set("isSystemAdmin",JBoltUserKit.isSystemAdmin());
		render("users.html");
	}
	/**
	 * 角色下用户数据查询
	 */
	public void userDatas() {
		renderJsonData(userService.paginateUsersByRoleId(getPageNumber(),getPageSize(),getLong("roleId")));
	}
	
	/**
	 * 新增
	 */
	public void add(){
		render("add.html");
	}
	/**
	 * 新增Item
	 */
	public void addItem(){
		set("pid", getLong(0,0L));
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		Role role=service.findById(getLong(0));
		if(role==null) {
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("role", role);
		set("pid", role.getPid());
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getModel(Role.class, "role")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getModel(Role.class, "role")));
	}
	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete(){
		renderJson(service.delete(getLong()));
	}
	
	/**
	 * 清空角色上的用户列表
	 */
	@Before(Tx.class)
	public void clearUsers() {
		renderJson(userService.clearUsersByRole(getLong()));
	}
}
