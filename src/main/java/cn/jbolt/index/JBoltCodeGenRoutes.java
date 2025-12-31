package cn.jbolt.index;

import cn.jbolt._admin.codegen.CodeGenAdminController;
import cn.jbolt._admin.codegen.modelattr.CodeGenModelAttrAdminController;
import cn.jbolt.core.permission.JBoltAdminAuthInterceptor;
import com.jfinal.config.Routes;

/**
 * admin后台的路由配置
 * @ClassName:  AdminRoutes
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2019年3月26日 下午12:25:20
 */
public class JBoltCodeGenRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin/_jbolt_code_gen");
		this.addInterceptor(new JBoltAdminAuthInterceptor());
		this.add("/admin/codegen", CodeGenAdminController.class,"/");
		this.add("/admin/codegen/modelattr", CodeGenModelAttrAdminController.class,"/modelattr");
	}

}
