package cn.jbolt.admin.appdevcenter;

import cn.jbolt.core.permission.JBoltAdminAuthInterceptor;
import com.jfinal.config.Routes;
/**
 * admin后台app dev center 应用开发中心 的路由配置
 * @ClassName:  AppDevCenterAdminRoutes
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:25:20   
 */
public class AppDevCenterAdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin/_app_dev_center");
		this.addInterceptor(new JBoltAdminAuthInterceptor());
		this.add("/admin/app", ApplicationAdminController.class,"/app");
	}

}
