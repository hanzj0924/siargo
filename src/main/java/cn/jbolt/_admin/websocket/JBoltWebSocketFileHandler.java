package cn.jbolt._admin.websocket;

import com.jfinal.log.Log;
/**
 * JBolt websocket 文本消息处理器
 * @ClassName:  JBoltWebSocketTextMessageHandler   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年10月1日   
 */
public class JBoltWebSocketFileHandler {
	private static final Log LOG = Log.getLog("JBoltWebsocketLog");
	public static final JBoltWebSocketFileHandler me = new JBoltWebSocketFileHandler();
	public JBoltWebSocketMsg process(JBoltWebSocketMsg inMsg, JBoltWebSocketSession session) {
		JBoltWebSocketMsg outMsg = null;
		
		return outMsg;
	}
}
