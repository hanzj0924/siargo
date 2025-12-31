package cn.jbolt.admin.wechat.menu;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.model.WechatMenu;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;

/**   
 * 微信菜单管理
 * @ClassName:  WechatMenuAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月12日 下午11:37:20   
 */
@CheckPermission(PermissionKey.WECHAT_MENU)
@OnlySaasPlatform
public class WechatMenuAdminController extends JBoltBaseController {
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatMenuService service;
	/**
	 * 进入一个公众号的菜单管理界面
	 */
	public void mgr(){
		Long mpId=getLong(0);
		if(notOk(mpId)){
			renderDialogFail("参数异常");
			return;
		}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			renderDialogFail("微信公众平台信息不存在");
			return;
		}
		set("mpId", mpId);
		set("mpName", wechatMpinfo.getName());
		render("mgr.html");
	}
	/**
	 * 读取一级菜单数据
	 */
	public void level1List(){
		renderJsonData(service.getLevel1Menus(getLong(0)));
	}
	/**
	 * 读取二级菜单数据
	 */
	public void level2List(){
		renderJsonData(service.getLevel2Menus(getLong(0),getLong(1)));
	}
	/**
	 * 进入单个菜单的编辑界面
	 */
	public void edit(){
		Long mpId=getLong(0);
		Long id=getLong(1);
		if(isOk(id)){
			WechatMenu menu = service.findById(id);
			if(menu.getMpId().longValue() != mpId.longValue()){
				renderAjaxPortalFail("参数异常");
				return;
			}
			setAttr("menu", menu);
			set("action","admin/wechat/menu/update");
		}else{
			set("action","admin/wechat/menu/save");
		}
		set("mpId", mpId);
		render("form.html");
	}

	
	/**
	 * 新增数据
	 */
	public void save(){
		renderJson(service.save(getLong("mpId"),getModel(WechatMenu.class,"menu")));
	}
	/**
	 * 修改数据
	 */
	public void update(){
		renderJson(service.update(getLong("mpId"),getModel(WechatMenu.class,"menu")));
	}
	
	/**
	 * 删除数据
	 * @return
	 */
	public void delete(){
		renderJson(service.delete(getLong(0),getLong(1)));
	}
	/**
	 * 发布到公众号
	 * @return
	 */
	public void publish(){
		renderJson(service.publish(getLong(0)));
	}
	
	/**
	 * 类型列表
	 */
	public void types() {
		renderJsonData(JBoltEnum.getEnumOptionList(JBoltWechatMenuType.class));
	}
}
