package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt.wechat.api.test.JBoltWechatTestApiController;

/** 微信公众平台前端配置 */
public class WechatTestRoutes extends Routes {

    @Override
    public void config() {
        this.setMappingSuperClass(true);
        this.setBaseViewPath("/_view/_test/wechat");
        this.add("/test/wechat", JBoltWechatTestApiController.class, "/");
    }

}
