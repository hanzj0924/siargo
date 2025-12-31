package cn.jbolt.extend.config;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 项目级别二开扩展自定义系统内模块
 * 例如现在默认内置PLATFORM_INNER_ADMIN 核心后台管理
 * 我开发的探知号知圈后台管理模块 就可以在这里定义 GROUP_ADMIN(101)
 * 从101开始
 * @ClassName:  ExtendProjectOfModuleType
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2023年2月24日15:46:20
 */
public enum ExtendProjectOfModule {
	PLATFORM_INNER_ADMIN("核心后台管理", 1);
//	GROUP_ADMIN("知圈后台管理", 101);
	private String text;
	private int value;
	private ExtendProjectOfModule(String text, int value) {
		this.text = text;
		this.value = value;
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
		JBoltEnum.addToTvBeanMap(ExtendProjectOfModule.class);
	}
	
}
