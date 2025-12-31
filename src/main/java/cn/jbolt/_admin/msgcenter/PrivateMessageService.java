package cn.jbolt._admin.msgcenter;

import cn.jbolt.common.model.PrivateMessage;
import cn.jbolt.core.service.base.JBoltBaseService;

public class PrivateMessageService extends JBoltBaseService<PrivateMessage> {
	private PrivateMessage dao = new PrivateMessage();
	@Override
	protected PrivateMessage dao() {
		return dao;
	}
	
	public boolean existUnread(Integer userId) {
		return false;
	}

	@Override
	protected int systemLogTargetType() {
		// TODO Auto-generated method stub
		return 0;
	}

}
