package cn.jbolt.wechat.api.test;

import cn.jbolt.core.controller.base.JBoltBaseController;
/**
 * JBolt 微信公众号 H5测试
 * @ClassName:  JBoltWechatTestApiController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年5月22日   
 *    
 */
public class JBoltWechatTestApiController extends JBoltBaseController {
	/**
	 * 进入测试首页
	 */
	public void index() {
		render("index.html");
	}
}
