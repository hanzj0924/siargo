package cn.jbolt.api.common.controller;

import cn.jbolt._admin.user.UserService;
import cn.jbolt.core.api.JBoltApiBaseController;
import cn.jbolt.core.api.httpmethod.JBoltHttpGet;
import cn.jbolt.core.api.httpmethod.JBoltHttpPost;
import cn.jbolt.core.crossorigin.CrossOrigin;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.User;
import cn.jbolt.core.para.JBoltPara;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;


/**
 * 用户信息接口 jb_user
 */
@CrossOrigin
@Path("/api/user")
public class JBoltUserInfoApiController extends JBoltApiBaseController {
    @Inject
    UserService service;

    /**
     * 获取个人用户信息接口
     * /api/user/mineInfo
     */
    @JBoltHttpGet
    public void mineInfo() {
        User user = JBoltUserKit.getUser();
        if(user==null){
            renderJBoltApiFail("用户信息获取失败");
            return;
        }
        renderJBoltApiSuccessWithData(user);
    }

    /**
     * 更新个人信息 目前只能更新name和avatar sex age 四个字段
     * /api/user/updateMineInfo
     * 参数 传JSON rawdata数据就可以{"name":"张三","avatar":"xxx",sex:1,age:22}
     * @param para
     */
    @JBoltHttpPost
    public void updateMineInfo(JBoltPara para){
        if(para == null || para.isEmpty()){
            renderJBoltApiFail("参数异常");
            return;
        }
        User user = JBoltUserKit.getUser();
        if(user==null){
            renderJBoltApiFail("用户信息不存在");
            return;
        }
        boolean needUpdate = false;
        if(para.containsKey("name")){
            String name = para.getString("name");
            if(isOk(name) && !user.getName().equals(name.trim())){
                user.setName(name.trim());
                needUpdate = true;
            }
        }
        if(para.containsKey("avatar")){
            String avatar = para.getString("avatar");
            if(isOk(avatar) && !user.getName().equals(avatar.trim())){
                user.setAvatar(avatar);
                needUpdate = true;
            }
        }
        if(para.containsKey("sex")){
            Integer sex = para.getInteger("sex");
            if(isOk(sex) && !user.getSex().equals(sex)){
                user.setSex(sex);
                needUpdate = true;
            }
        }
        if(para.containsKey("age")){
            Integer age = para.getInteger("age");
            if(isOk(age) && !user.getAge().equals(age)){
                user.setAge(age);
                needUpdate = true;
            }
        }
        if(!needUpdate){
            renderJBoltApiSuccess();
            return;
        }
        boolean success = user.update();
        if(!success){
            renderJBoltApiFail("更新失败，发生异常");
            return;
        }
        renderJBoltApiSuccess();
    }

}
