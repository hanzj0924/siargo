package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt.wechat.api.JBoltWechatApiController;
import cn.jbolt.wxa.api.JBoltWxaApiController;

/** 微信小程序API接口controller */
public class WechatApiRoutes extends Routes {

    @Override
    public void config() {
        this.setMappingSuperClass(true);
        this.add("/api/wxa", JBoltWxaApiController.class);
        this.add("/api/wechat", JBoltWechatApiController.class);
    }

}
