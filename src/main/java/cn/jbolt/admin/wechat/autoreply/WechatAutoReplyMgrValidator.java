package cn.jbolt.admin.wechat.autoreply;

import com.jfinal.core.Controller;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.para.JBoltValidator;
import cn.jbolt.core.util.JBoltArrayUtil;

public class WechatAutoReplyMgrValidator extends JBoltValidator {
	private static final String[] actionNames= new String[]{"toggleEnable"};
	@Override
	protected void validate(Controller c) {
		validateJBoltLong(0, JBoltMsg.PARAM_ERROR);
		String actionName=getActionMethodName();
		if(JBoltArrayUtil.contains(actionNames, actionName)) {
			validateJBoltLong(1,  JBoltMsg.PARAM_ERROR+":数据ID");
		}
	}
}
