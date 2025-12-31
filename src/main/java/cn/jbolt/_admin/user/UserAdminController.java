package cn.jbolt._admin.user;

import java.util.ArrayList;
import java.util.List;

import cn.jbolt.common.model.UserExtend;
import cn.jbolt.core.cache.JBoltGlobalConfigCache;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.extend.config.ExtendProjectOfModule;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.bean.Option;
import cn.jbolt.core.bean.OptionBean;
import cn.jbolt.core.cache.JBoltCacheInterceptor;
import cn.jbolt.core.cache.JBoltUserCache;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.User;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import cn.jbolt.core.service.JBoltFileService;

@CheckPermission(PermissionKey.USER)
@UnCheckIfSystemAdmin
public class UserAdminController extends JBoltBaseController {
	@Inject
	private UserService service;
	@Inject
	private UserExtendService userExtendService;
	@Inject
	private RoleService roleService;
	@Inject
	private JBoltFileService jboltFileService;
	/**
	 * 用户表数作为选项数据源
	 */
	@UnCheck
	public void options() {
		renderJsonData(service.getOptionList());
	}
	/**
	 * 管理首页
	 */
	public void index(){
		render("index_ajax.html");
	}
	/**
	 * 表格数据接口
	 */
	public void datas() {
		renderJsonData(service.paginateAdminList(getPageNumber(),getPageSize(),getKeywords(),getOfModule(),getInt("sex"),getBoolean("assignDept",true),getLong("deptId"),getLong("postId"),getLong("roleId"),getBoolean("enable")));
	}

	/**
	 *  系统通知 可用 选择用户数据接口
	 */
	@CheckPermission(PermissionKey.SYS_NOTICE)
	public void sysnoticeUsers() {
		renderJsonData(service.paginateSysNoticeList(getPageNumber(),getPageSize(),getKeywords(),getInt("sex"),getBoolean("assignDept",true),getLong("deptId"),getLong("postId"),getLong("roleId")));
	}
	/**
	  * 获取用户列表
	  * 通过关键字匹配
	 * autocomplete组件使用
	 */
	@UnCheck
	public void autocomplete() {
		renderJsonData(service.getAutocompleteList(get("q"), getInt("limit",10),true,"name,username,pinyin,phone"));
	}
	/**
	 * 上传头像
	 */
	public void uploadAvatar(){
		String uploadPath=JBoltUploadFolder.todayFolder(JBoltUploadFolder.USER_AVATAR);
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJson(jboltFileService.saveImageFile(file,uploadPath));
	}

	/**
	 * 上传头像
	 */
	@UnCheck
	public void uploadMyAvatar(){
		String uploadPath=JBoltUploadFolder.todayFolder(JBoltUploadFolder.USER_AVATAR);
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		Long userId=JBoltUserKit.getUserId();
		Ret ret=jboltFileService.saveImageFile(file,uploadPath);
		if(ret.isFail()) {
			renderJson(ret);
			return;
		}
		String url=ret.getStr("data");
		renderJson(service.updateUserAvatar(userId,url));
	}
	@Before(JBoltCacheInterceptor.class)
	@UnCheck
	public void sexOptions(){
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("男",User.SEX_MALE));
		options.add(new OptionBean("女",User.SEX_FEMALE));
		options.add(new OptionBean("未知",User.SEX_NONE));
		renderJsonData(options);
	}
	/**
	 * 进入自身密码修改界面
	 */
	@UnCheck
	public void pwd(){
		render("pwd.html");
	}
	/**
	 * 进入自身头像修改界面
	 */
	@UnCheck
	public void avatar(){
		setAttr("avatar", JBoltUserCache.me.getAvatar(JBoltUserKit.getUserId()));
		render("avatar.html");
	}
	/**
	 * 进入重置用户密码界面
	 */
	@CheckPermission(PermissionKey.USER)
	public void editpwd(){
		set("userId", getLong(0));
		render("editpwd.html");
	}
	/**
	 * 重置用户密码
	 */
	@CheckPermission(PermissionKey.USER)
	public void submitpwd(){
		Long userId=getLong("userId");
		String newPwd=get("newPwd");
		String reNewPwd=get("reNewPwd");
		if(hasNotOk(newPwd,reNewPwd)){
			renderJsonFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		if(!newPwd.equals(reNewPwd)){
			renderJsonFail("两次新密码输入不一致");
			return;
		}
		renderJson(service.submitPwd(userId,newPwd));
	}

	/**
	 * 修改用户自己的密码
	 */
	@UnCheck
	public void updatepwd(){
		String oldPwd=get("oldPwd");
		String newPwd=get("newPwd");
		String reNewPwd=get("reNewPwd");
		if(hasNotOk(oldPwd,newPwd,reNewPwd)){
			renderJsonFail(JBoltMsg.PARAM_ERROR);
			return;
			}
		if(!newPwd.equals(reNewPwd)){
			renderJsonFail("两次新密码输入不一致");
			return;
		}
		renderJson(service.updatePwd(oldPwd,newPwd));
	}


	/**
	 * 新增
	 */
	public void add(){
		set("roles", roleService.findAll());
		render("add.html");
	}

	/**
	 * 查看扩展信息
	 */
	public void extendForm(){
		processRenderExtendForm(false);
	}

	private void processRenderExtendForm(boolean readonlyMode) {
		boolean userExtendEnable = JBoltGlobalConfigCache.me.userExtendEnable();
		if(!userExtendEnable){
			renderFail("系统全局参数配置中未开启用户扩展支持");
			return;
		}
		set("readonlyMode",readonlyMode);
		Long id = getLong(0);
		if(notOk(id)){
			render("user_extend_form.html");
			return;
		}
		UserExtend extend = userExtendService.findById(id);
		if(extend == null){
			User user = service.findById(id);
			if(user == null){
				renderFail("用户信息不存在");
				return;
			}
			Ret ret = userExtendService.initSaveOneExtend(id);
			if(ret.isFail()){
				renderFail(ret.getStr("msg"));
				return;
			}
			extend = ret.getAs("data");
		}
		set("extend",extend);
		render("user_extend_form.html");
	}

	/**
	 * 查看扩展信息
	 */
	public void extendDetail(){
		processRenderExtendForm(true);
	}
	/**
	 * 编辑
	 */
	public void edit(){
		Long userId=getLong(0);
		Long myId=JBoltUserKit.getUserId();
		User user=service.findById(userId);
		if(user==null) {
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		User me=service.findById(myId);
		if((user.getIsSystemAdmin()!=null&&user.getIsSystemAdmin())&&(me.getIsSystemAdmin()==null||me.getIsSystemAdmin()==false)) {
			renderDialogFail("无权修改超管员信息");
			return;
		}
		set("user", user);
		render("edit.html");
	}
	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save(){
		renderJson(service.save(getModel(User.class, "user"),getModel(UserExtend.class, "extend")));
	}
	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update(){
		renderJson(service.update(getModel(User.class, "user"),getModel(UserExtend.class, "extend")));
	}
	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete(){
		renderJson(service.delete(getLong(0)));
	}
	/**
	 * 切换启用状态
	 */
	@Before(Tx.class)
	public void toggleEnable(){
		renderJson(service.toggleEnable(getLong(0)));
	}
	/**
	 * 删除角色
	 */
	@Before(Tx.class)
	public void deleteRole(){
		renderJson(service.deleteUserRole(getLong(0),getLong(1)));
	}
	/**
	 * 处理deptPath
	 */
	@Before(Tx.class)
	public void processAllDeptPath(){
		service.processAllDeptPath();
		renderJsonSuccess();
	}

	/**
	 * 列出模块
	 */
	@UnCheck
	public void modules(){
		renderJsonData(JBoltEnum.getEnumOptionList(ExtendProjectOfModule.class));
	}
}
