package cn.jbolt._admin.event;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.jbolt._admin.websocket.JBoltWebSocketMsg;
import cn.jbolt._admin.websocket.JBoltWebSocketUtil;
import cn.jbolt.common.model.SysNotice;
import cn.jbolt.common.model.Todo;
import cn.jbolt.core.model.OnlineUser;
import cn.jbolt.core.util.JBoltArrayUtil;
import net.dreamlu.event.core.EventListener;
/**
 * JBolt 内部事件监听处理
 * @ClassName:  JBoltEventListener
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年10月6日
 */
public class JBoltEventListener {
	private static final Log LOG = Log.getLog(JBoltEventListener.class);
	/**
	 * 全局通知事件监听处理
	 * @param notice
	 */
	@EventListener(async = true)
	public void sysNotice(SysNotice notice) {
		LOG.info("收到事件SysNotice开始处理");
		if(notice == null) {
			return;
		}
		Integer receiverType = notice.getReceiverType();
		if (receiverType == null || receiverType<=0) {
			return;
		}
		String receiverValue = notice.getReceiverValue();
		if (receiverType !=1 && StrKit.isBlank(receiverValue)) {
			return;
		}
		Long[] values = null;
		if(receiverType != 1) {
			values = JBoltArrayUtil.toDisLong(receiverValue, ",");
			if (values == null || values.length == 0) {
				return;
			}
		}
		String msg = "收到新通知,请及时查阅";
		//执行websocket推送
		switch (receiverType) {
			case 1://全部
				JBoltWebSocketUtil.sendAllMessage(JBoltWebSocketMsg.createSystemCommandMsg("new_notice", msg));
				break;
			case 2:// 角色
				JBoltWebSocketUtil.sendMessageToUserByRoles(values,JBoltWebSocketMsg.createSystemCommandMsg("new_notice", msg));
				break;
			case 3:// 部门
				JBoltWebSocketUtil.sendMessageToUserByDepts(values,JBoltWebSocketMsg.createSystemCommandMsg("new_notice", msg));
				break;
			case 4:// 岗位
				JBoltWebSocketUtil.sendMessageToUserByPosts(values,JBoltWebSocketMsg.createSystemCommandMsg("new_notice", msg));
				break;
			case 5:// 用户
				JBoltWebSocketUtil.sendMessageToUsers(values,JBoltWebSocketMsg.createSystemCommandMsg("new_notice", msg));
				break;
		}
	}

	/**
	 * todo事件监听处理
	 * @param todo
	 */
	@EventListener(async = true)
	public void todo(Todo todo) {
		LOG.info("收到事件Todo开始处理");
		if(todo == null) {
			LOG.error("事件TODO 内容为空");
			return;
		}
		if(todo.getUserId() == null || todo.getUserId() <= 0) {
			LOG.error("事件TODO["+todo.getId()+"] userId没指定");
			return;
		}
		String msg = "收到新待办事项,请及时处理";
		//执行websocket推送
		JBoltWebSocketUtil.sendMessageToUser(todo.getUserId(), JBoltWebSocketMsg.createSystemCommandMsg("new_todo", msg));
	}

	/**
	 * 用户强制下线 异端顶替处理
	 * @param onlineUser
	 */
	@EventListener(async = true)
	public void onlineUser(OnlineUser onlineUser) {
		LOG.info("收到事件onlineUser开始处理");
		if(onlineUser == null) {
			LOG.error("事件onlineUser 内容为空");
			return;
		}
		String msg = null;
		String command = null;
		if(onlineUser.isForcedOffline()) {
			msg = "当前用户被平台强退下线，如有疑问，请联系平台管理";
			command = "user_forced_offline";
		}else if(onlineUser.isTerminalOffline()) {
			msg = "当前用户在异端登录，本地已下线，如有疑问，请联系平台管理";
			command = "user_terminal_offline";
		}
		if(StrKit.notBlank(msg,command)) {
			//执行websocket推送
			JBoltWebSocketUtil.sendMessage(onlineUser.getSessionId(), JBoltWebSocketMsg.createSystemCommandMsg(command, msg));
		}
	}
}
