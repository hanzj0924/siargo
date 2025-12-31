package cn.jbolt.admin.wechat.user;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;

import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.para.JBoltValidator;

public class WechatUserMgrValidator extends JBoltValidator {
	@Inject
	private WechatUserService wechatUserService;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Override
	protected void validate(Controller c) {
		validateJBoltLong(0, JBoltMsg.PARAM_ERROR);
		Long mpId=c.getLong(0);
		if(mpId!=null&&mpId>0) {
			WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
			if(mpinfo==null) {
				setErrorMsg("微信公众平台"+JBoltMsg.DATA_NOT_EXIST);
			}else {
				c.setAttr("mpinfo", mpinfo);
				boolean exist=wechatUserService.tableExist(mpId);
				if(exist==false) {
					setErrorMsg(JBoltMsg.TABLE_NOT_EXIST);
				}
			}
			
		}
	}
}
