package cn.jbolt.admin.wechat.media;

import com.jfinal.core.Controller;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.para.JBoltValidator;
import cn.jbolt.core.util.JBoltArrayUtil;

/** 微信公众平台资源库 */
public class WechatMediaMgrValidator extends JBoltValidator {
    private static final String[] actionNames = new String[]{"chooseIt"};

    @Override
    protected void validate(Controller c) {
        validateJBoltLong(0, JBoltMsg.PARAM_ERROR + ":微信公众平台mpId");
        String actionName = getActionMethodName();
        if (JBoltArrayUtil.contains(actionNames, actionName)) {
            validateJBoltLong(1, JBoltMsg.PARAM_ERROR + ":数据ID");
        }
    }


}
