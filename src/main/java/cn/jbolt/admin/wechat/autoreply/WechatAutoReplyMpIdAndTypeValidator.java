package cn.jbolt.admin.wechat.autoreply;

import com.jfinal.core.Controller;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.para.JBoltValidator;

public class WechatAutoReplyMpIdAndTypeValidator extends JBoltValidator {

	@Override
	protected void validate(Controller c) {
		validateJBoltLong(0, JBoltMsg.PARAM_ERROR+":微信公众平台mpId");
		validateJBoltLong(1, JBoltMsg.PARAM_ERROR+":微信公众平台type");
		if(getActionMethodName().equals("edit")) {
			validateJBoltLong(2, JBoltMsg.PARAM_ERROR+":数据ID");
		}
	}
 

}
