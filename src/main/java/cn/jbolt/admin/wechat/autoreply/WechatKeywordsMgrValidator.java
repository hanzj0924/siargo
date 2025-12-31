package cn.jbolt.admin.wechat.autoreply;

import com.jfinal.core.Controller;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.para.JBoltValidator;
import cn.jbolt.core.util.JBoltArrayUtil;
/**
 * 自动回复规则中的触发关键词管理
 * @ClassName:  WechatKeywordsMgrValidator   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年6月22日   
 */
public class WechatKeywordsMgrValidator extends JBoltValidator {
	private static final String[] actionNames= new String[]{"edit","delete","toggleEnable"};

	@Override
	protected void validate(Controller c) {
		validateJBoltLong(0, JBoltMsg.PARAM_ERROR+":微信公众平台autoReplyId");
		String actionName=getActionMethodName();
		if(JBoltArrayUtil.contains(actionNames, actionName)) {
			validateJBoltLong(1,  JBoltMsg.PARAM_ERROR+":数据ID");
		}
	}


}
