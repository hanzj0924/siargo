package cn.jbolt._admin.user;

import cn.jbolt.common.model.UserExtend;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 用户扩展信息service
 */
public class UserExtendService extends JBoltBaseService<UserExtend> {
    private UserExtend dao = new UserExtend().dao();
    @Override
    protected UserExtend dao() {
        return dao;
    }

    @Override
    protected int systemLogTargetType() {
        return 0;
    }

    @Before(Tx.class)
    public Ret initSaveOneExtend(Long userId) {
        UserExtend extend = new UserExtend();
        extend.setId(userId);
        boolean success = extend.save();
        return success?successWithData(extend):FAIL;
    }

    /**
     * 添加一个扩展信息
     * @param extend
     * @return
     */
    public Ret saveByUser(UserExtend extend) {
        if(extend == null || notOk(extend.getId())){
            return fail("保存用户扩展信息失败,"+JBoltMsg.PARAM_ERROR);
        }
        boolean success = extend.save();
        return ret(success);
    }

    /**
     * 更新一个扩展信息
     * @param extend
     * @return
     */
    public Ret updateByUser(UserExtend extend) {
        if(extend == null || notOk(extend.getId())){
            return fail("更新用户扩展信息失败,"+JBoltMsg.PARAM_ERROR);
        }
        boolean success = extend.update();
        return ret(success);
    }
}
