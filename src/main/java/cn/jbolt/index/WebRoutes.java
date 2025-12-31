package cn.jbolt.index;

import com.jfinal.config.Routes;

public class WebRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_web");
		this.add("/", IndexController.class);
	}

}
