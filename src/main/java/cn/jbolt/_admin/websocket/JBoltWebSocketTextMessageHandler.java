package cn.jbolt._admin.websocket;

import com.jfinal.log.Log;
/**
 * JBolt websocket 文本消息处理器
 * @ClassName:  JBoltWebSocketTextMessageHandler   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年10月1日   
 */
public class JBoltWebSocketTextMessageHandler {
	private static final Log LOG = Log.getLog("JBoltWebsocketLog");
	public static final JBoltWebSocketTextMessageHandler me = new JBoltWebSocketTextMessageHandler();
	
	/**
	 * 核心文本消息处理
	 * @param inMsg
	 * @param session
	 * @return
	 */
	public JBoltWebSocketMsg process(JBoltWebSocketMsg inMsg, JBoltWebSocketSession session) {
		JBoltWebSocketMsg outMsg = null;
		String text = inMsg.getData().toString();
		LOG.debug("收到文字消息处理："+text);
		if(inMsg._isToOther()) {
			outMsg = inMsg.toTextOutMsg("哈喽 你好");
		}else {
			outMsg = inMsg.toTextRetOutMsg("ok 收到你的文本消息");
		}
		return outMsg;
	}
}
