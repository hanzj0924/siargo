package cn.jbolt.apitest;

import com.jfinal.config.Routes;

/** API测试专用 */
public class ApiTestRoutes extends Routes {

    @Override
    public void config() {
        this.setMappingSuperClass(true);
        this.add("/api/test", ApiTestController.class);
    }

}
