package cn.jbolt._admin.globalconfig;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 内置全局参数类型 枚举
 * @ClassName:  JBoltGlobalConfigTypeBuiltInEnum   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年01月18日  
 */
public enum JBoltGlobalConfigTypeBuiltInEnum {
	SYS_CONFIG("系统配置","sys_config"),
	ADMIN_LOGIN("后台登录","admin_login"),
	ADMIN_UI("后台样式","admin_ui"),
	WECHAT_DEV("微信开发","wechat_dev");
	private String text;
	private String value;
	private JBoltGlobalConfigTypeBuiltInEnum(String text,String value) {
		this.text=text;
		this.value=value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	//加入到JBolt Enum管理器
	static {
		JBoltEnum.addToTvBeanMap(JBoltGlobalConfigTypeBuiltInEnum.class);
	}
	
}
