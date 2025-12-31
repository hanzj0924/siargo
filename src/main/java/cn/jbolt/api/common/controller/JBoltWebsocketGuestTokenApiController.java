package cn.jbolt.api.common.controller;

import cn.jbolt._admin.websocket.JBoltWebSocketUtil;
import cn.jbolt.core.Interceptor.JBoltWebsocketGuestInterceptor;
import cn.jbolt.core.api.JBoltApiBaseController;
import cn.jbolt.core.api.OpenAPI;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.crossorigin.CrossOrigin;
import cn.jbolt.core.kit.JBoltSaasTenantKit;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.aop.Before;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;

/**
 * 标准JBolt API写法调用websocket guest用户的token和check
 * 主要API项目前端使用
 */
@CrossOrigin
@Path("/api/ws/guest/token")
public class JBoltWebsocketGuestTokenApiController extends JBoltApiBaseController {
    /**
     * 获取当前访客 websocketGuestToken
     */
    @Before(JBoltWebsocketGuestInterceptor.class)
    @OpenAPI
    public void gen(){
        String token = "guest_ws_token_"+ JBoltUserKit.getUserId();
        Kv kv = Kv.by("token", JBoltWebSocketUtil.getGuestWebsocketToken(token));
        if(JBoltConfig.SAAS_ENABLE && JBoltSaasTenantKit.me.isSelfRequest()) {
            kv.set("tenantSn",JBoltSaasTenantKit.me.getSn());
        }
        renderJBoltApiSuccessWithData(kv);
    }

    @OpenAPI
    public void check(){
        renderJBoltApiSuccessWithData(JBoltWebSocketUtil.checkGuestWebsocketToken(get("token")));
    }
}
