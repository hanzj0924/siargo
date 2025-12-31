package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt.wechat.api.test.JBoltWechatTestApiController;
/**
 * 微信公众平台前端配置
 * @ClassName:  WechatRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日15:03:24   
 */
public class WechatTestRoutes extends Routes {

	@Override
	public void config() {
		this.setMappingSuperClass(true);
		this.setBaseViewPath("/_view/_test/wechat");
		this.add("/test/wechat", JBoltWechatTestApiController.class,"/");
	}

}
