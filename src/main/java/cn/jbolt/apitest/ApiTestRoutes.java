package cn.jbolt.apitest;

import com.jfinal.config.Routes;
/**
 * API测试专用
 * @ClassName:  ApiTestRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年3月16日   
 */
public class ApiTestRoutes extends Routes {

	@Override
	public void config() {
		this.setMappingSuperClass(true);
		this.add("/api/test", ApiTestController.class);
	}

}
