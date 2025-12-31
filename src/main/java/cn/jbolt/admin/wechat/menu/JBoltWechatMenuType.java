package cn.jbolt.admin.wechat.menu;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 微信公众号 菜单类型
 * @ClassName:  JBoltWechatMenuType   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年3月26日   
 *    
 */
public enum JBoltWechatMenuType {
	/**
	 * 无操作
	 */
	TYPE_NONE("无操作","none"),
	/**
	 * URL地址
	 */
	TYPE_VIEW("跳转URL","view"),
	/**
	 * 匹配关键词
	 */
	TYPE_KEYWORDS("关联关键词","keywords"),
	/**
	 * 内置事件
	 */
	TYPE_EVENT("普通click事件","click"),
	/**
	 * 调用系统相机拍照
	 */
	TYPE_PIC_SYSPHOTO("调用系统相机拍照","pic_sysphoto"),
	/**
	 * 调用系统相机拍照或者从相册选择
	 */
	TYPE_PIC_PHOTO_OR_ALBUM("调用系统相机拍照或者相册发图","pic_photo_or_album"),
	/**
	 * 调用微信相册发图
	 */
	TYPE_PIC_WEIXIN("微信相册发图","pic_weixin"),
	/**
	 * 调用扫码推送事件
	 */
	TYPE_SCANCODE_PUSH("扫码推送事件","scancode_push"),
	/**
	 * 调用扫码推送事件带提示
	 */
	TYPE_SCANCODE_WAITMSG("扫码推送事件带提示","scancode_waitmsg"),
	/**
	 * 调用地理位置选择器
	 */
	TYPE_LOCATION_SELECT("地理位置选择器","location_select"),
	/**
	 * 下发消息
	 */
	TYPE_MEDIA_ID("下发消息(永久素材ID)","media_id"),
	/**
	 * 只能跳转公众号图文消息URL
	 */
	TYPE_VIEW_LIMITED("跳转号内图文消息URL","view_limited"),
	/**
	 * 微信小程序
	 */
	TYPE_MINIPROGRAM("微信小程序","miniprogram");
	private String text;
	private String value;
	private JBoltWechatMenuType(String text,String value) {
		this.text = text;
		this.value = value;
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
	
	//枚举类加入到JBoltEnum管理器中
	static {
		JBoltEnum.addToTvBeanMap(JBoltWechatMenuType.class);
	}
}
