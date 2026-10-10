package cn.jbolt.admin.appdevcenter;

import com.jfinal.core.Controller;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.para.JBoltValidator;

/** 应用开发中心 管理应用 参数校验 */
public class ApplicationValidator extends JBoltValidator {
    @Override
    protected void validate(Controller c) {
        validateJBoltLong(0, JBoltMsg.PARAM_ERROR + ":数据ID");
    }


}
