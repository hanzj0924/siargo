package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt.admin.wechat.autoreply.WechatMsgController;
import cn.jbolt.admin.wechat.autoreply.WechatWxaMsgController;

/** 微信公众平台前端配置 */
public class WechatRoutes extends Routes {

    @Override
    public void config() {
        this.setMappingSuperClass(true);
        this.add("/wx/msg", WechatMsgController.class);
        this.add("/wxa/msg", WechatWxaMsgController.class);
    }

}
