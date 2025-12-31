package cn.jbolt._admin.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import com.jfinal.kit.StrKit;

import cn.jbolt.core.cache.JBoltOnlineUserCache;
/**
 * JBolt平台 websocket session
 * @ClassName:  JBoltWebSocketSession   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年10月2日   
 */
public class JBoltWebSocketSession {
	//会话
    private Session session;
    //登录用户授权的TOKEN
    private String token;
    //用户UserId
    private Object userId;
    //租户SN 非saas模式就是null
    private String tenantSn;
    //是否为租户
    private boolean isTenant;
    
    public JBoltWebSocketSession(Session session,String token) {
    	this(session, token, null);
    }
    public JBoltWebSocketSession(Session session,String token,String tenantSn) {
    	this.session = session;
    	this.token   = token;
    	this.tenantSn   = tenantSn;
    	this.isTenant   = StrKit.notBlank(tenantSn);
    	this.userId = JBoltOnlineUserCache.me.getUserIdBySessionId(token);
    }
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Object getUserId() {
		return userId;
	}
	public void setUserId(Object userId) {
		this.userId = userId;
	}
	/**
	 * 返回指定类型userId 
	 * @param <T>
	 * @return
	 */
    @SuppressWarnings("unchecked")
	public <T> T getUserIdAs() {
	    return (T) userId;
    }
    /**
     * 关闭session
     */
    public void close() {
    	if(session!=null) {
    		try {
    			session.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
   /**
    * 关闭session
    * @param closeReason
    */
    public void close(CloseReason closeReason) {
    	if(session!=null) {
    		try {
				session.close(closeReason);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public boolean isOpen() {
		return session==null?false:session.isOpen();
	}
	public String getTenantSn() {
		return tenantSn;
	}
	public void setTenantSn(String tenantSn) {
		this.tenantSn = tenantSn;
	}
	public boolean isTenant() {
		return isTenant;
	}
	public void setTenant(boolean isTenant) {
		this.isTenant = isTenant;
	}
}
