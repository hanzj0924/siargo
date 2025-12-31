package cn.jbolt._admin.websocket;
/**
 * JBolt里 websocket通讯 command定义
 * @ClassName:  JBoltWebSocketCommand
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年10月2日
 *
 */
public interface JBoltWebSocketCommand {
	String PING = "ping";
	String PONG = "pong";
	String SERVER_TIME = "server_time";
	String CLIENT_USER_COUNT = "client_user_count";
	String CLIENT_SESSION_COUNT = "client_session_count";
	String TENANT_CLIENT_USER_COUNT = "tenant_client_user_count";
	String TENANT_CLIENT_SESSION_COUNT = "tenant_client_session_count";
	String TOTAL_CLIENT_USER_COUNT = "total_client_user_count";
	String TOTAL_CLIENT_SESSION_COUNT = "total_client_session_count";
	String MSGCENTER_CHECK_UNREAD = "msgcenter_check_unread";
	String CHECK_LAST_PWD_UPDATE_TIME = "check_last_pwd_update_time";
}
