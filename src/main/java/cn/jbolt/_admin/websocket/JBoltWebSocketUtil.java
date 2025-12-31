package cn.jbolt._admin.websocket;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cn.jbolt.core.cache.JBoltCacheKit;
import cn.jbolt.core.cache.JBoltOnlineUserCache;
import cn.jbolt.core.para.JBoltParaValidator;
import cn.jbolt.core.service.JBoltOnlineUserService;
import com.jfinal.aop.Aop;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.jbolt.core.base.config.JBoltConfig;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * JBoltWebsocket工具类
 * @ClassName:  JBoltWebSocketUtil
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年10月26日
 */
public class JBoltWebSocketUtil {
	private static final Log LOG = Log.getLog("JBoltWebsocketLog");
	private static final JBoltOnlineUserService onlineUserService = Aop.get(JBoltOnlineUserService.class);
	//客户端map 总部
	private static final Map<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>> CLIENTS = new ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>>();
	//saas模式的租户客户端map
    private static final Map<String, ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>>> TENANT_CLIENTS = new ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>>>();
	public static String getGuestWebsocketToken(String key){
		if(StrKit.isBlank(key)){
			return null;
		}
		return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, key, new IDataLoader() {
			@Override
			public Object load() {
				return key;
			}
		},1800);
	}

	public static boolean checkGuestWebsocketToken(String key){
		if(StrKit.notBlank(key)){
			return false;
		}
		String token = JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, key);
		return StrKit.notBlank(token) && token.equals(key);
	}
    /**
     * 添加新客户端
     * @param client
     */
    public static void addClient(JBoltWebSocketServerEndpoint client) {
    	if(JBoltConfig.SAAS_ENABLE && client.isTenant()) {
    		addTenantClient(client);
    	}else {
			ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> map = CLIENTS.get(client.getToken());
			if(map == null){
				map = new ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>();
				CLIENTS.put(client.getToken(),map);
			}
			map.put(client.getSession().getId(),client);
    	}
    }

    /**
     * 添加新客户端 saas模式 租户
     * @param client
     */
    public static void addTenantClient(JBoltWebSocketServerEndpoint client) {
    	ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>> tenantClientMap = getTenantClients(client.getTenantSn());
    	if(tenantClientMap == null) {
    		tenantClientMap = new ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>>();
    		TENANT_CLIENTS.put(client.getTenantSn(), tenantClientMap);
    	}
		ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> map = tenantClientMap.get(client.getToken());
		if(map == null){
			map = new ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>();
			tenantClientMap.put(client.getToken(),map);
		}
		map.put(client.getSession().getId(),client);
    }

    /**
     * 移除客户端
     * @param jboltWebSocketSession
     */
    public static void removeClient(JBoltWebSocketSession jboltWebSocketSession) {
    	if(JBoltConfig.SAAS_ENABLE && jboltWebSocketSession.isTenant()) {
    		removeTenantClient(jboltWebSocketSession.getTenantSn(),jboltWebSocketSession.getToken(),jboltWebSocketSession.getSession().getId());
    	}else {
			ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> map = CLIENTS.get(jboltWebSocketSession.getToken());
			if(map!=null && !map.isEmpty()){
				map.remove(jboltWebSocketSession.getSession().getId());
			}
    	}
    }

    /**
     * 删除租户下某一客户端
     * @param tenantSn
     * @param token
     */
    public static void removeTenantClient(String tenantSn, String token,String sessionId) {
    	ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>> tenantClientMap = getTenantClients(tenantSn);
    	if(tenantClientMap != null && !tenantClientMap.isEmpty()) {
			ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> map = tenantClientMap.get(token);
			if(map!=null && !map.isEmpty()){
				map.remove(sessionId);
			}
    	}
	}

    /**
     * 获取指定租户下的client
     * @param token
     * @param tenantSn
     * @return
     */
	public static ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> getTenantClientMap(String token, String tenantSn) {
		ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>> tenantClientMap = getTenantClients(tenantSn);
    	if(tenantClientMap != null) {
			return tenantClientMap.get(token);
    	}
		return null;
	}


	/**
	 * 获取指定租户下的client
	 * @param token
	 * @param tenantSn
	 * @return
	 */
	public static JBoltWebSocketServerEndpoint getTenantClient(String token, String tenantSn,String sessionId) {
		ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> map = getTenantClientMap(token, tenantSn);
		return map==null?null:map.get(sessionId);
	}

	/**
     * 获取指定客户端
     * @param token
     * @return
     */
	public static ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> getClientMap(String token) {
		return CLIENTS.get(token);
	}

	/**
	 * 获取指定客户端
	 * @param token
	 * @param sessionId
	 * @return
	 */
	public static JBoltWebSocketServerEndpoint getClient(String token,String sessionId) {
		ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> map = getClientMap(token);
		return map == null?null:map.get(sessionId);
	}
	/**
	 * 获取非租户用户在线数量
	 * @return
	 */
	public static int getClientUserCount() {
		return CLIENTS.size();
	}
	/**
	 * 获取非租户session在线数量
	 * @return
	 */
	public static int getClientSessionCount() {
		int size = 0;
		if(!CLIENTS.isEmpty()){
			for(Entry<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>> clients:CLIENTS.entrySet()) {
				size = size + clients.getValue().size();
			}
		}
		return size;
	}
	/**
	 * 获取租户用户在线数量
	 * @return
	 */
	public static int getTenantClientUserCount() {
		return TENANT_CLIENTS.size();
	}

	/**
	 * 获取租户Session在线数量
	 * @return
	 */
	public static int getTenantClientSessionCount() {
		int size = 0;
		if(!TENANT_CLIENTS.isEmpty()) {
			for(Entry<String, ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>>> clients:TENANT_CLIENTS.entrySet()) {
				for(Entry<String,ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>> cs:clients.getValue().entrySet()){
					size = size + cs.getValue().size();
				}
			}
		}
		return size;
	}

	public static int getTotalClientUserCount(){
		return getClientUserCount() + getTenantClientUserCount();
	}

	public static int getTotalClientSessionCount(){
		return getClientSessionCount() + getTenantClientSessionCount();
	}

	/**
	 * 获取非租户客户端
	 * @return
	 */
	public static Map<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>> getClients() {
		return CLIENTS;
	}
	/**
	 * 获取所有租户客户端
	 * @return
	 */
	public static Map<String, ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>>> getAllTenantClients() {
		return TENANT_CLIENTS;
	}
	/**
	 * 获取所有租户客户端
	 * @param tenantSn
	 * @return
	 */
	public static ConcurrentHashMap<String, ConcurrentHashMap<String,JBoltWebSocketServerEndpoint>> getTenantClients(String tenantSn) {
		return TENANT_CLIENTS.get(tenantSn);
	}

	/**
	 * 发送全部
	 * @param outMsg
	 */
	public static void sendAllMessage(JBoltWebSocketMsg outMsg) {
		if(JBoltConfig.SAAS_ENABLE && outMsg.isTenant()) {
			sendOneTenantAllMessage(outMsg);
		}else {
			CLIENTS.forEach((token,clients)->{
				if(clients!=null&& !clients.isEmpty()){
					clients.forEach((sessionId,client)->client.sendMessage(client,outMsg));
				}
			});
		}

	}
	/**
	 * 给租户下所有客户端发
	 * @param outMsg
	 */
	public static void sendOneTenantAllMessage(JBoltWebSocketMsg outMsg) {
		ConcurrentHashMap<String, ConcurrentHashMap<String, JBoltWebSocketServerEndpoint>> tenantClientMap = getTenantClients(outMsg.getTenantSn());
		if(tenantClientMap != null) {
			tenantClientMap.forEach((token,clientsMap)->{
				if(clientsMap!=null && !clientsMap.isEmpty()){
					clientsMap.forEach((sessionId,clients)->{

					});
				}
			});
		}
	}

	/**
	 * 发送全部
	 * @param outMsg
	 */
	public static void sendMessage(JBoltWebSocketMsg outMsg) {
		Object from = outMsg.getFrom();
		Object to   = outMsg.getTo();
		if((from == null && to == null) || to.equals(JBoltWebSocketMsg.TO_SYSTEM)) {
			throw new RuntimeException("请指定from或者to");
		}
		if(to.toString().length()>19) {
			sendMessage(to.toString(), outMsg);
			return;
		}
		sendMessageToUser(Long.parseLong(to.toString()), outMsg);
	}

	/**
	 * 发送消息
	 * @param token
	 * @param outMsg
	 */
	public static void sendMessage(String token,JBoltWebSocketMsg outMsg) {
		if(StrKit.isBlank(token)){
			LOG.error("websocket sendMessage：param token is null");
			return;
		}
		String sessionId = outMsg.getClientSessionId();
		if(StrKit.isBlank(sessionId)){
			ConcurrentHashMap<String,JBoltWebSocketServerEndpoint> clients = null;
			if(outMsg.isTenant()) {
				clients = getTenantClientMap(outMsg.getTenantSn(),token);
			}else {
				clients = getClientMap(token);
			}

			if(clients != null && !clients.isEmpty()) {
				if(outMsg.getFrom() == null) {
					outMsg.setFrom(JBoltWebSocketMsg.FROM_SYSTEM);
				}
				String msg = outMsg.toJson();
				clients.forEach((sid,client)->client.sendMessage(msg));
			}
		}else{
			JBoltWebSocketServerEndpoint client = null;
			if(outMsg.isTenant()) {
				client = getTenantClient(token, outMsg.getTenantSn(),sessionId);
			}else {
				client = getClient(token,sessionId);
			}
			if(client!=null){
				client.sendMessage(client,outMsg);
			}
		}
	}


	 /**
     * 发送给指定用户
     * @param userId
     * @param outMsg
     */
    public static void sendMessageToUser(Long userId, JBoltWebSocketMsg outMsg) {
		if(JBoltParaValidator.notOk(userId)){
			LOG.error("websocket sendMessageToUser：param userId is null");
			return;
		}
		List<String> tokens = JBoltOnlineUserCache.me.getOnlineUserSessionListByUserId(userId);
		if(tokens!=null && tokens.size()>0) {
			tokens.forEach((token)->{
				if(StrKit.notBlank(token)) {
					sendMessage(token, outMsg);
				}
			});
		}
	}

    /**
     * 批量发送给多人
     * @param userIds
     * @param outMsg
     */
	public static void sendMessageToUsers(Long[] userIds, JBoltWebSocketMsg outMsg) {
		if(userIds == null || userIds.length == 0) {
			return;
		}
		for(Long userId:userIds) {
			sendMessageToUser(userId, outMsg);
		}
	}

	/**
	 * 指定多部门用户发送
	 * @param postIds
	 * @param outMsg
	 */
	public static void sendMessageToUserByPosts(Long[] postIds, JBoltWebSocketMsg outMsg) {
		if(postIds == null || postIds.length == 0) {
			return;
		}
		for(Long postId:postIds) {
			sendMessageToUserByPost(postId, outMsg);
		}
	}

	/**
	 * 指定部门用户发送
	 * @param postId
	 * @param outMsg
	 */
	public static void sendMessageToUserByPost(Long postId, JBoltWebSocketMsg outMsg) {
		List<String> tokens = onlineUserService.getSessionListByPostId(postId);
		if(tokens!=null && tokens.size()>0) {
			tokens.forEach((token)->{
				if(StrKit.notBlank(token)) {
					sendMessage(token, outMsg);
				}
			});
		}
	}

	/**
	 * 发送消息给指定多个部门下用户
	 * @param deptIds
	 * @param outMsg
	 */
	public static void sendMessageToUserByDepts(Object[] deptIds, JBoltWebSocketMsg outMsg) {
		if(deptIds == null || deptIds.length == 0) {
			return;
		}
		for(Object deptId:deptIds) {
			sendMessageToUserByDept(deptId, outMsg);
		}
	}



	/**
	 * 发送消息给指定部门下用户
	 * @param deptId
	 * @param outMsg
	 */
	public static void sendMessageToUserByDept(Object deptId, JBoltWebSocketMsg outMsg) {
		List<String> tokens = onlineUserService.getSessionListByDeptId(deptId);
		if(tokens!=null && tokens.size()>0) {
			tokens.forEach((token)->{
				if(StrKit.notBlank(token)) {
					sendMessage(token, outMsg);
				}
			});
		}
	}
	/**
	 * 发送消息给指定多个角色下用户
	 * @param roleIds
	 * @param outMsg
	 */
	public static void sendMessageToUserByRoles(Object[] roleIds, JBoltWebSocketMsg outMsg) {
		if(roleIds == null || roleIds.length == 0) {
			return;
		}
		for(Object roleId:roleIds) {
			sendMessageToUserByRole(roleId, outMsg);
		}
	}



	/**
	 * 发送消息给指定角色下用户
	 * @param roleId
	 * @param outMsg
	 */
	public static void sendMessageToUserByRole(Object roleId, JBoltWebSocketMsg outMsg) {
		List<String> tokens = onlineUserService.getSessionListByRoleId(roleId);
		if(tokens!=null && tokens.size()>0) {
			tokens.forEach((token)->{
				if(StrKit.notBlank(token)) {
					sendMessage(token, outMsg);
				}
			});
		}
	}

	/**
	 * 删除客户端 传JBoltWebSocketServerEndpoint
	 * @param client
	 */
	public static void removeClient(JBoltWebSocketServerEndpoint client) {
		removeClient(client.getJbsession());
	}



}
