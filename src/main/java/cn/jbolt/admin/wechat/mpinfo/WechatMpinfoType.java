package cn.jbolt.admin.wechat.mpinfo;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 微信公众平台 账号类型
 * @ClassName:  WechatMpinfoType   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年3月17日   
 */
public enum WechatMpinfoType{
		DYH("订阅号",1),
		FWH("服务号",2),
		QYWX("企业微信",3),
		XCX("微信小程序",4);
		private String text;
		private int value;
		private WechatMpinfoType(String text,int value) {
			this.text=text;
			this.value=value;
		}
		
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	
		public int getValue() {
			return value;
		}
	
		public void setValue(int value) {
			this.value = value;
		}
		//枚举类加入到JBoltEnum管理器中
		static {
			JBoltEnum.addToTvBeanMap(WechatMpinfoType.class);
		}
}