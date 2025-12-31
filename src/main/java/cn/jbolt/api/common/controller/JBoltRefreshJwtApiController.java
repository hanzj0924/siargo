package cn.jbolt.api.common.controller;

import cn.jbolt.core.api.JBoltApiBaseController;
import cn.jbolt.core.api.JBoltApiJwtManger;
import cn.jbolt.core.api.JBoltApplyJWT;
import cn.jbolt.core.api.httpmethod.JBoltHttpGet;
import cn.jbolt.core.crossorigin.CrossOrigin;
import com.jfinal.core.Path;

/**
 * JBolt内置公用 api jwt刷新专用接口
 * @author 山东小木
 * @date 2022年6月16日 19:48:56
 */
@CrossOrigin
@Path("/api/jwt/refresh")
public class JBoltRefreshJwtApiController extends JBoltApiBaseController {
    @JBoltApplyJWT
    @JBoltHttpGet
    public void index(){
        renderJBoltApiRet(JBoltApiJwtManger.me().refreshJwt(this));
    }
}
