package cn.jbolt.admin.devdoc.database;

import cn.jbolt.core.permission.JBoltAdminAuthInterceptor;
import com.jfinal.config.Routes;

/** JBolt平台内置后台开发平台使用开发文档的路由配置 */
public class DevDocAdminRoutes extends Routes {
    @Override
    public void config() {
        this.setBaseViewPath("/_view/_admin/_dev_doc");
        this.addInterceptor(new JBoltAdminAuthInterceptor());
        this.add("/admin/devdoc/database", JBoltDatabaseDevDocController.class, "/database");
    }

}
