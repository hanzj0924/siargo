package cn.jbolt.admin.wechat.mpinfo;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 已认证 未认证状态
 * @ClassName:  WechatMpinfoAuthenticated   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年3月17日   
 */
public enum WechatMpinfoAuthenticated {
	TRUE("已认证",true),
	FALSE("未认证",false);
	private String text;
	private Boolean value;
	private WechatMpinfoAuthenticated(String text,Boolean value) {
		this.text=text;
		this.value=value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Boolean getValue() {
		return value;
	}
	public void setValue(Boolean value) {
		this.value = value;
	}
	//加入到JBolt Enum管理器
	static {
		JBoltEnum.addToTvBeanMap(WechatMpinfoAuthenticated.class);
	}
	
}
