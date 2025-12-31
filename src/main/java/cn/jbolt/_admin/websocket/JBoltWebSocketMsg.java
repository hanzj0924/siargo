package cn.jbolt._admin.websocket;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.kit.JBoltSaasTenantKit;

/**
 * JBolt websocket 传输消息体  abstract
 * @ClassName:  JBoltWebSocketMsg
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年10月1日
 */
public class JBoltWebSocketMsg {
	/**
	 * 谁发送-系统
	 */
	public static final int FROM_SYSTEM = -1;
	/**
	 * 发给谁-系统
	 */
	public static final int TO_SYSTEM = -1;
	/**
	 * 发给谁-系统 str
	 */
	public static final String TO_SYSTEM_STR = "-1";
	/**
	 * 类型-指令
	 */
	public static final int TYPE_COMMAND = 1;
	/**
	 * 类型-指令返回
	 */
	public static final int TYPE_COMMAND_RET = 2;
	/**
	 * 类型-文本字符
	 */
	public static final int TYPE_TEXT = 3;
	/**
	 * 类型-文件
	 */
	public static final int TYPE_FILE = 4;

	/**
	 * 从谁那里来
	 */
	private Object   from;
	/**
	 * 是否发给租户的下的
	 */
	private boolean  isTenant;
	/**
	 * 哪个租户下
	 */
	private String   tenantSn;
	/**
	 * 到谁哪里去
	 */
	private Object   to;
	/**
	 * 类型
	 */
	private Integer  type;
	/**
	 * 指令
	 */
	private String   command;
	/**
	 * 携带数据
	 */
	private Object   data;
	public JBoltWebSocketMsg(Object from, Object to, Integer type, String command, Object data) {
		this.from     = from;
		this.to       = to;
		this.type     = type;
		this.command  = command;
		this.data     = data;
		if(JBoltConfig.SAAS_ENABLE && JBoltSaasTenantKit.me.isSelfRequest()) {
			toTenant(JBoltSaasTenantKit.me.getSn());
		}
	}

	/**
	 * 客户端sessionId
	 */
	private String clientSessionId;
	/**
	 * 转为指令返回值输出消息 返回给自己
	 * @param data
	 * @return
	 */
	public JBoltWebSocketMsg toCommandRetOutMsg(Object data) {
		return new JBoltWebSocketMsg(this.from, this.from, TYPE_COMMAND_RET, this.command, data).toTenant(this.tenantSn);
	}
	/**
	 * 转为指令返回值输出消息 返回给自己
	 * @param command
	 * @param data
	 * @return
	 */
	public JBoltWebSocketMsg toCommandRetOutMsg(String command,Object data) {
		return new JBoltWebSocketMsg(this.from, this.from, TYPE_COMMAND_RET, command, data).toTenant(this.tenantSn);
	}
	/**
	 * 转为文本输出消息 返回给自己
	 * @param data
	 * @return
	 */
	public JBoltWebSocketMsg toTextRetOutMsg(Object data) {
		return new JBoltWebSocketMsg(this.from, this.from, TYPE_TEXT, this.command, data).toTenant(this.tenantSn);
	}

	/**
	 * 转为文本输出消息
	 * @param data
	 * @return
	 */
	public JBoltWebSocketMsg toTextOutMsg(Object data) {
		return new JBoltWebSocketMsg(this.from,this.to, TYPE_TEXT, this.command, data).toTenant(this.tenantSn);
	}

	public Object getFrom() {
		return from;
	}
	public JBoltWebSocketMsg setFrom(Object from) {
		this.from = from;
		return this;
	}
	public Object getTo() {
		return to;
	}
	public JBoltWebSocketMsg setTo(Object to) {
		this.to = to;
		return this;
	}
	public Integer getType() {
		return type;
	}
	public JBoltWebSocketMsg setType(Integer type) {
		this.type = type;
		return this;
	}
	public Object getData() {
		return data;
	}
	public JBoltWebSocketMsg setData(Object data) {
		this.data = data;
		return this;
	}
	public String getCommand() {
		return command;
	}
	public JBoltWebSocketMsg setCommand(String command) {
		this.command = command;
		return this;
	}
	public String toJson() {
		return JsonKit.toJson(this);
	}
	public boolean _isFile() {
		return type != null && type.intValue() == TYPE_FILE;
	}
	/**
	 * 判断是给服务器的消息
	 * @return
	 */
	public boolean _isToSystem() {
		return to != null && to.toString().equals(TO_SYSTEM_STR);
	}
	/**
	 * 判断是否为发给其他客户端
	 * @return
	 */
	public boolean _isToOther() {
		return to != null && !to.toString().equals(TO_SYSTEM_STR) && !to.toString().equals(from.toString());
	}
	/**
	 * 判断是给自己的消息
	 * @return
	 */
	public boolean _isToSelf() {
		return to != null && to.toString().equals(from.toString());
	}

	/**
	 * 创建文件消息
	 * @param from
	 * @param to
	 * @param data
	 * @return
	 */
	public static JBoltWebSocketMsg createFileMsg(Object from,Object to,Object data) {
		return new JBoltWebSocketMsg(from,to, TYPE_FILE, null, data);
	}
	/**
	 * 创建系统发送的文件消息
	 * @param to
	 * @param data
	 * @return
	 */
	public static JBoltWebSocketMsg createSystemFileMsg(Object to,Object data) {
		return new JBoltWebSocketMsg(FROM_SYSTEM,to, TYPE_FILE, null, data);
	}
	/**
	 * 创建文字消息
	 * @param from
	 * @param to
	 * @param data
	 * @return
	 */
	public static JBoltWebSocketMsg createTextMsg(Object from,Object to,Object data) {
		return new JBoltWebSocketMsg(from,to, TYPE_TEXT, null, data);
	}
	/**
	 * 创建系统发送的文字消息
	 * @param to
	 * @param data
	 * @return
	 */
	public static JBoltWebSocketMsg createSystemTextMsg(Object to,Object data) {
		return new JBoltWebSocketMsg(FROM_SYSTEM,to, TYPE_TEXT, null, data);
	}
	/**
	 * 创建文字消息
	 * @param from
	 * @param to
	 * @param command
	 * @param data
	 * @return
	 */
	public static JBoltWebSocketMsg createTextMsg(Object from,Object to,String command,Object data) {
		return new JBoltWebSocketMsg(from,to, TYPE_TEXT, null, data);
	}

	/**
	 * 创建指令消息
	 * @param from
	 * @param to
	 * @param command
	 * @param data
	 * @return
	 */
	public static JBoltWebSocketMsg createCommandMsg(Object from,Object to,String command,Object data) {
		return new JBoltWebSocketMsg(from,to, TYPE_COMMAND, command, data);
	}

	/**
	 * 创建系统发送指令消息
	 * @param to
	 * @param command
	 * @param data
	 * @return
	 */
	public static JBoltWebSocketMsg createSystemCommandMsg(Object to,String command,Object data) {
		return new JBoltWebSocketMsg(FROM_SYSTEM,to, TYPE_COMMAND, command, data);
	}

	/**
	 * 创建系统发送指令消息
	 * 默认to是-1 主要用来后面调用to(xx)
	 * @param command
	 * @param data
	 * @return
	 */
	public static JBoltWebSocketMsg createSystemCommandMsg(String command,Object data) {
		return new JBoltWebSocketMsg(FROM_SYSTEM, TO_SYSTEM, TYPE_COMMAND, command, data);
	}

	public JBoltWebSocketMsg to(Object to) {
		this.to = to;
		return this;
	}
	public String getTenantSn() {
		return tenantSn;
	}
	private void setTenantSn(String tenantSn) {
		this.tenantSn = tenantSn;
	}
	public JBoltWebSocketMsg toTenant(String tenantSn) {
		setTenantSn(tenantSn);
		setIsTenant(JBoltConfig.SAAS_ENABLE && StrKit.notBlank(tenantSn));
		return this;
	}
	public JBoltWebSocketMsg fromTenant(String tenantSn) {
		toTenant(tenantSn);
		return this;
	}
	public boolean isTenant() {
		return isTenant;
	}
	private void setIsTenant(boolean isTenant) {
		this.isTenant = isTenant;
	}


	public String getClientSessionId() {
		return clientSessionId;
	}

	public void setClientSessionId(String clientSessionId) {
		this.clientSessionId = clientSessionId;
	}

}
