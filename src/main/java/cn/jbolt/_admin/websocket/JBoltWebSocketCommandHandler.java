package cn.jbolt._admin.websocket;

import java.util.Date;

import cn.jbolt._admin.user.UserService;
import com.jfinal.aop.Aop;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.jbolt._admin.msgcenter.SysNoticeService;
import cn.jbolt._admin.msgcenter.TodoService;
import cn.jbolt._admin.websocket.extend.JBoltWebSocketExtendCommandHandler;
/**
 * JBolt websocket 指令处理器
 * @ClassName:  JBoltWebSocketCommandHandler
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年10月1日
 */
public class JBoltWebSocketCommandHandler {
	private static final Log LOG = Log.getLog("JBoltWebsocketLog");
	public static final JBoltWebSocketCommandHandler me = new JBoltWebSocketCommandHandler();
	/**
	 * 核心指令处理器逻辑
	 * @param inMsg
	 * @param session
	 * @return
	 */
	public JBoltWebSocketMsg process(JBoltWebSocketMsg inMsg, JBoltWebSocketSession session) {
		String command = inMsg.getCommand();
		if(StrKit.isBlank(command)) {
			LOG.error("无效的command,不予处理");
			return null;
		}
		JBoltWebSocketMsg outMsg = null;
		//拿到指令 switch判断并处理
		switch (command) {
			case JBoltWebSocketCommand.PING://心跳检测
//				LOG.debug("收到心跳检测ping");
				outMsg = inMsg.toCommandRetOutMsg(JBoltWebSocketCommand.PONG,null);
				break;
			case JBoltWebSocketCommand.SERVER_TIME://服务器时间
				outMsg = inMsg.toCommandRetOutMsg(new Date());
				break;
			case JBoltWebSocketCommand.CLIENT_USER_COUNT:
				outMsg = inMsg.toCommandRetOutMsg(JBoltWebSocketUtil.getClientUserCount());
				break;
			case JBoltWebSocketCommand.CLIENT_SESSION_COUNT:
				outMsg = inMsg.toCommandRetOutMsg(JBoltWebSocketUtil.getClientSessionCount());
				break;
			case JBoltWebSocketCommand.TENANT_CLIENT_USER_COUNT:
				outMsg = inMsg.toCommandRetOutMsg(JBoltWebSocketUtil.getTenantClientUserCount());
				break;
			case JBoltWebSocketCommand.TENANT_CLIENT_SESSION_COUNT:
				outMsg = inMsg.toCommandRetOutMsg(JBoltWebSocketUtil.getTenantClientSessionCount());
				break;
			case JBoltWebSocketCommand.TOTAL_CLIENT_SESSION_COUNT:
				outMsg = inMsg.toCommandRetOutMsg(JBoltWebSocketUtil.getTotalClientSessionCount());
				break;
			case JBoltWebSocketCommand.TOTAL_CLIENT_USER_COUNT:
				outMsg = inMsg.toCommandRetOutMsg(JBoltWebSocketUtil.getTotalClientUserCount());
				break;
			case JBoltWebSocketCommand.MSGCENTER_CHECK_UNREAD://检测前端消息中心是否有未读
				outMsg = processMsgCenterCheckUnreadCommand(inMsg, session.getUserId());
				break;
			case JBoltWebSocketCommand.CHECK_LAST_PWD_UPDATE_TIME://检测是否超过规定检测密码修改天数
				outMsg = processCheckLastPwdUpdateTimeCommand(inMsg, session.getUserId());
				break;
			default://没有内置处理 就去扩展里找
				outMsg = JBoltWebSocketExtendCommandHandler.me.process(inMsg, session);
				break;
		}
		if(outMsg != null){
			outMsg.setClientSessionId(inMsg.getClientSessionId());
		}
		//有消息返回给客户端 就返回JBoltWebSocketMsg 没有就null
		return outMsg;
	}

	/**
	 * 处理检测消息中心是否有用户未读
	 * @param inMsg
	 * @param userId
	 * @return
	 */
	protected JBoltWebSocketMsg processMsgCenterCheckUnreadCommand(JBoltWebSocketMsg inMsg, Object userId) {
		SysNoticeService sysNoticeService = Aop.get(SysNoticeService.class);
		boolean needRedDot = sysNoticeService.existUnread(userId);
		if(!needRedDot) {
			TodoService todoService = Aop.get(TodoService.class);
			needRedDot = todoService.existUnread(userId);
			if(!needRedDot) {
				needRedDot = todoService.existNeedProcess(userId);
			}
//			if(!needRedDot) {
//				needRedDot = privateMessageService.existUnread(userId);
//			}
		}
		return inMsg.toCommandRetOutMsg(needRedDot);
	}
	/**
	 * 处理检测用户是否最后一次密码修改时间间隔超期
	 * @param inMsg
	 * @param userId
	 * @return
	 */
	protected JBoltWebSocketMsg processCheckLastPwdUpdateTimeCommand(JBoltWebSocketMsg inMsg, Object userId) {
		UserService userService = Aop.get(UserService.class);
		return inMsg.toCommandRetOutMsg(userService.checkUserLastPwdUpdateTime(Long.parseLong(userId.toString())));
	}
}
