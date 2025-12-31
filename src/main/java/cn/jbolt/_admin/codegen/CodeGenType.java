package cn.jbolt._admin.codegen;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 代码生成器 构建类型
 * @ClassName:  CodeGenType   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年12月17日19:57:28  
 */
public enum CodeGenType {
	ADMIN("后台管理类","Admin"),
	API("API接口开发类","Api");
	private String text;
	private String value;
	private CodeGenType(String text,String value) {
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
	//加到JBolt Enum管理器
	static {
		JBoltEnum.addToTvBeanMap(CodeGenType.class);
	}
	
}
