package cn.jbolt.api.common.controller;

import cn.jbolt._admin.user.UserService;
import cn.jbolt.core.api.*;
import cn.jbolt.core.api.httpmethod.JBoltHttpGet;
import cn.jbolt.core.api.httpmethod.JBoltHttpMethod;
import cn.jbolt.core.cache.JBoltPermissionCache;
import cn.jbolt.core.cache.JBoltUserCache;
import cn.jbolt.core.crossorigin.CrossOrigin;
import cn.jbolt.core.model.User;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.Ret;


/**
 * 非微信服务号 小程序环境使用
 * 使用jb_user登录信息 登录拿到jwt授权 使用系统用户登录APP H5页面等场景
 */
@CrossOrigin
@Path("/api/user/auth")
public class JBoltUserAuthApiController extends JBoltApiBaseController {
    @Inject
    UserService service;

    /**
     * 执行登录授权
     * @param userName 账号
     * @param password 密码
     */
    @JBoltApplyJWT
    @JBoltHttpMethod({HttpMethod.GET,HttpMethod.POST})
    public void index(@Para("userName")String userName,@Para("password")String password) {
        if(hasNotOk(userName,password)){
            renderJBoltApiFail("用户名或密码错误");
            return;
        }
        //执行查询用户
        Ret ret = service.getUser(userName,password);
        if(ret.isFail()){
            renderJBoltApiRet(ret);
            return;
        }
        //用户存在 判断状态
        User user = ret.getAs("data");
        if(user == null){
            renderJBoltApiFail("用户["+userName+"]信息不存在");
            return;
        }
        //判断状态
        if(!user.getEnable()){
            renderJBoltApiFail("用户["+userName+"]已被禁用");
            return;
        }
        //设置当前登录有用户
        JBoltApiKit.setApplyJwtUser(new JBoltApiUserBean(JBoltApiKit.getApplicationId(),user.getId(), user.getUsername(),true));
        //告知客户端登录成功
        renderJBoltApiSuccess();
    }

    /**
     * 获取当前调用api的jbuser的所有权限keys
     */
    @JBoltHttpGet
    public void permissionKeys(){
        renderJBoltApiSuccessWithData(
                JBoltPermissionCache.me.getRolePermissionKeySet(
                        JBoltUserCache.me.getRoles(
                                JBoltApiKit.getApiUserIdToLong()
                        )
                )
        );
    }
}
