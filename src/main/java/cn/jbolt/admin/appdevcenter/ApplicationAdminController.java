package cn.jbolt.admin.appdevcenter;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.enumutil.JBoltEnableEnum;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.core.model.Application;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
/**
 * 应用开发者中心
 * 主要管理调用接口的Application
 * 不管是APP还是小程序或者其它端应用 都可以集成进来
 * 为调用开发API类应用准备
 * @ClassName:  AppDevCenterAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月12日   
 *    
 * 
 */
@CheckPermission(PermissionKey.APPLICATION)
@UnCheckIfSystemAdmin
@OnlySaasPlatform
public class ApplicationAdminController extends JBoltBaseController {
	@Inject
	private ApplicationService service;
	/**
	 * 应用中心首页
	 */
	public void index() {
		render("index.html");
	}
	/**
	 * 数据源
	 */
	public void datas() {
		renderJsonData(service.paginateAdminList(getPageNumber(),getPageSize(),getKeywords(),getType(),getEnable()));
	}
	/**
	 * 查看app的appId和appSecret信息
	 */
	@Before(ApplicationValidator.class)
	public void appinfo() {
		Long id=getLong(0);
		Application application=service.findById(id);
		if(application==null) {
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		setAttr("application", application);
		render("appinfo.html");
	}
	
	/**
	 * enable 启用状态数据源
	 */
	public void enableOptions(){
		renderJsonData(JBoltEnum.getEnumOptionList(JBoltEnableEnum.class));
	}
	/**
	 * Api应用中心应用类型列表数据源
	 */
	public void types() {
		renderJsonData(JBoltEnum.getEnumOptionList(ApplicationType.class));
	}
	/**
	 * Api应用中心应用类型列表数据源 form里用
	 */
	public void typesInForm() {
		renderJsonData(JBoltEnum.getEnumOptionListWithout(ApplicationType.class,ApplicationType.PC_INNER_PLATFORM.getValue()));
	}
	
	public void add() {
		render("add.html");
	}
	@Before(ApplicationValidator.class)
	public void edit() {
		Long id=getLong(0);
		Application application=service.findById(id);
		if(application==null) {
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		setAttr("application", application);
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save() {
		renderJson(service.save(getModel(Application.class, "application")));
	}
	/**
	 * 更新
	 */
	public void update() {
		renderJson(service.update(getModel(Application.class, "application")));
	}
	/**
	 * 删除
	 */
	@Before({Tx.class,ApplicationValidator.class})
	public void delete() {
		renderJson(service.deleteApplication(getLong(0)));
	}
	/**
	 * 切换状态
	 */
	@Before(ApplicationValidator.class)
	public void toggleEnable() {
		renderJson(service.toggleEnable(getLong(0)));
	}
	/**
	 * 切换是否需要接口needchecksign
	 */
	@Before(ApplicationValidator.class)
	public void toggleNeedCheckSign() {
		renderJson(service.toggleNeedCheckSign(getLong(0)));
	}
	/**
	 * 变更AppSecret
	 */
	@Before(ApplicationValidator.class)
	public void changeAppSecret() {
		renderJson(service.changeAppSecret(getLong(0)));
	}
	/**
	 * 查看关联对象
	 */
	@Before(ApplicationValidator.class)
	public void linkTarget() {
		Application application=service.findById(getLong(0));
		if(application==null) {
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("application", application);
		set("linkTarget", service.getLinkTarget(application.getType(),application.getLinkTargetId()));
		render("linktarget.html");
	}
	/**
	 * 解除关联对象
	 */
	@Before(ApplicationValidator.class)
	public void removeLinkTarget() {
		renderJson(service.removeLinkTarget(getLong(0)));
	}
}
