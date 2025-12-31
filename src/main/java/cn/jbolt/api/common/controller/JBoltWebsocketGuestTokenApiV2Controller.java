package cn.jbolt.api.common.controller;

import cn.jbolt._admin.websocket.JBoltWebSocketUtil;
import cn.jbolt.core.Interceptor.JBoltWebsocketGuestInterceptor;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.crossorigin.CrossOrigin;
import cn.jbolt.core.crossorigin.JBoltCrossOriginInterceptor;
import cn.jbolt.core.kit.JBoltSaasTenantKit;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

/**
 * 调用websocket guest用户的token和check
 * 主要后台使用
 */
@Before(JBoltCrossOriginInterceptor.class)
@CrossOrigin
@Path("/api/ws/guest/token/v2")
public class JBoltWebsocketGuestTokenApiV2Controller extends Controller {
    /**
     * 获取当前访客 websocketGuestToken
     */
    @Before(JBoltWebsocketGuestInterceptor.class)
    public void gen(){
        String token = "guest_ws_token_"+ JBoltUserKit.getUserId();
        Kv kv = Kv.by("token", JBoltWebSocketUtil.getGuestWebsocketToken(token));
        if(JBoltConfig.SAAS_ENABLE && JBoltSaasTenantKit.me.isSelfRequest()) {
            kv.set("tenantSn",JBoltSaasTenantKit.me.getSn());
        }
        renderJson(Ret.ok("data",kv));
    }

    public void check(){
        renderJson(Ret.ok("data",JBoltWebSocketUtil.checkGuestWebsocketToken(get("token"))));
    }
}
