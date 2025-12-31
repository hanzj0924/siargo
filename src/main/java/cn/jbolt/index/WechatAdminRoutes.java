package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt.core.permission.JBoltAdminAuthInterceptor;
import cn.jbolt.admin.wechat.autoreply.WechatAutoReplyAdminController;
import cn.jbolt.admin.wechat.autoreply.WechatKeywordsAdminController;
import cn.jbolt.admin.wechat.autoreply.WechatReplyContentAdminController;
import cn.jbolt.admin.wechat.config.WechatConfigAdminController;
import cn.jbolt.admin.wechat.media.WechatMediaAdminController;
import cn.jbolt.admin.wechat.menu.WechatMenuAdminController;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoAdminController;
import cn.jbolt.admin.wechat.user.WechatUserAdminController;
/**
 * admin后台 微信管理模块相关 的路由配置
 * @ClassName:  WechatAdminRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日15:03:24   
 */
public class WechatAdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin/_wechat");
		this.addInterceptor(new JBoltAdminAuthInterceptor());
		this.add("/admin/wechat/mpinfo", WechatMpinfoAdminController.class,"/mpinfo");
		this.add("/admin/wechat/config", WechatConfigAdminController.class,"/config");
		this.add("/admin/wechat/menu", WechatMenuAdminController.class,"/menu");
		this.add("/admin/wechat/autoreply", WechatAutoReplyAdminController.class,"/autoreply");
		this.add("/admin/wechat/keywords", WechatKeywordsAdminController.class,"/autoreply/keywords");
		this.add("/admin/wechat/replycontent", WechatReplyContentAdminController.class,"/autoreply/replycontent");
		this.add("/admin/wechat/media", WechatMediaAdminController.class,"/media");
		this.add("/admin/wechat/user", WechatUserAdminController.class,"/user");
	}

}
