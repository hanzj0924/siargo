package cn.jbolt._admin.userconfig;

import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;

import cn.jbolt.core.base.JBoltGlobalConfigKey;
import cn.jbolt.core.cache.JBoltUserConfigCache;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.UnCheck;
/**
 * 用户自己使用的配置
 * @ClassName:  UserConfigAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月25日   
 */
public class UserConfigAdminController extends JBoltBaseController {
	@Inject
	private UserConfigService service;
	@UnCheck
	public void index() {
		set("userConfigs", service.getAdminList(JBoltUserKit.getUserId()));
		render("index.html");
	}
	@UnCheck
	public void toggleBooleanConfig() {
		Long userId=JBoltUserKit.getUserId();
		Ret ret=service.toggleBooleanConfig(userId,getLong(0));
		if(ret.isOk()) {
			processUserConfigCookie(userId,ret.getAs("data"));
		}
		renderJson(ret);
	}
	/**
	 * 根据
	 * @param configKey
	 */
	private void processUserConfigCookie(Long userId,String configKey) {
		switch (configKey) {
//		case JBoltGlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS:
//			boolean glass=JBoltUserConfigCache.me.getJBoltLoginFormStyleGlass(userId);
//			setCookie("jbolt_login_glassStyle",glass+"" ,60*60*24*7*4,JFinal.me().getContextPath());
//			break;
		case JBoltGlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR:
			boolean blur=JBoltUserConfigCache.me.getJBoltLoginBgimgBlur(userId);
			setCookie("jbolt_login_bgimgBlur",blur+"" ,60*60*24*7*4,JFinal.me().getContextPath());
			break;
		case JBoltGlobalConfigKey.JBOLT_LOGIN_NEST:
			boolean nest=JBoltUserConfigCache.me.getJBoltLoginNest(userId);
			setCookie("jbolt_login_nest",nest+"" ,60*60*24*7*4,JFinal.me().getContextPath());
			break;
		}
	}
	/**
	 * 更新Value
	 */
	@UnCheck
	public void changeStringValue(){
		renderJson(service.changeStringValue(JBoltUserKit.getUserId(),getLong("id"),get("value")));
	}
}
