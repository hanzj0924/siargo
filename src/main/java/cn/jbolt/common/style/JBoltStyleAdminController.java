package cn.jbolt.common.style;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.globalconfig.GlobalConfigService;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.UnCheck;
/**
 * jbolt的全局样式配置
 * @ClassName:  JBoltStyleAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年7月14日   
 */
public class JBoltStyleAdminController extends JBoltBaseController {
	@Inject
	private GlobalConfigService globalConfigService;
	@UnCheck
	public void change() {
		String style=get("style");
		if(isOk(style)) {
			renderJson(globalConfigService.updateJboltStyle(style));
		}else {
			renderJsonFail(JBoltMsg.PARAM_ERROR);
		}
	}
}
