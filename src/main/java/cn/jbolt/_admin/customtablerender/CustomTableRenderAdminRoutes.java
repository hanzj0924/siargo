package cn.jbolt._admin.customtablerender;

import cn.jbolt.core.permission.JBoltAdminAuthInterceptor;
import com.jfinal.config.Routes;

/** admin后台业务自定义表格渲染模块 */
public class CustomTableRenderAdminRoutes extends Routes {

    @Override
    public void config() {
        this.addInterceptor(new JBoltAdminAuthInterceptor());
        this.scan("cn.jbolt._admin.customtablerender.");
    }

}
