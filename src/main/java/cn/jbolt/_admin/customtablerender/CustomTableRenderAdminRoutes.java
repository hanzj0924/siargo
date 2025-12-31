package cn.jbolt._admin.customtablerender;

import cn.jbolt.core.permission.JBoltAdminAuthInterceptor;
import com.jfinal.config.Routes;

/**
 * admin后台业务自定义表格渲染模块
 * @ClassName:  CustomTableRenderAdminRoutes
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:  2023年5月26日16:44:06
 */
public class CustomTableRenderAdminRoutes extends Routes {

	@Override
	public void config() {
		this.addInterceptor(new JBoltAdminAuthInterceptor());
		this.scan("cn.jbolt._admin.customtablerender.");
	}

}
