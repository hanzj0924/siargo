package cn.jbolt.admin.appdevcenter;

import cn.jbolt.core.enumutil.JBoltEnum;
/**
 * API应用开发中心 应用类型
 * @ClassName:  ApplicationType   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年3月18日   
 */
public enum ApplicationType {
	PC_INNER_PLATFORM("内置_平台自身_PC网页",101),
	PC_WEB("PC网页",1),
	MOBILE_H5("普通移动端H5",2),
	MP_H5("微信公众平台H5",3),
	WECHAT_XCX("微信小程序",4),
	ALIPAY_XCX("支付宝小程序",5),
	DOUYIN_XCX("抖音小程序",6),
	BAIDU_XCX("百度小程序",7),
	TOUTIAO_XCX("头条小程序",8),
	QQ_XCX("QQ小程序",9),
	MOBILE_APP("移动端APP",10),
	PC_DESKTOP("PC桌面端软件",11),
	OTHER("其它",12);
	private String text;
	private int value;
	private ApplicationType(String text,int value) {
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
	//加到JBolt Enum管理器
	static {
		JBoltEnum.addToTvBeanMap(ApplicationType.class);
	}
	
}
