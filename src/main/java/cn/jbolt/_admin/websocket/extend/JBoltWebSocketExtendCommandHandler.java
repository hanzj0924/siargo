package cn.jbolt._admin.websocket.extend;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.jbolt._admin.websocket.JBoltWebSocketMsg;
import cn.jbolt._admin.websocket.JBoltWebSocketSession;
/**
 * 二开扩展  JBolt websocket 自定义指令处理器
 * @ClassName:  JBoltWebSocketExtendCommandHandler   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年10月1日   
 */
public class JBoltWebSocketExtendCommandHandler {
	private static final Log LOG = Log.getLog("JBoltWebsocketLog");
	public static final JBoltWebSocketExtendCommandHandler me = new JBoltWebSocketExtendCommandHandler();
	/**
	 * 扩展自定义指令处理器逻辑
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
//			case JBoltWebSocketCommand.PING://心跳检测
//				outMsg = inJBoltMsg.toCommandRetOutMsg(JBoltWebSocketCommand.PONG,null);
//				break;
		}
		
		//有消息返回给客户端 就返回JBoltWebSocketMsg 没有就null
		return outMsg;
	}
}
