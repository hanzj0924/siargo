package cn.jbolt._admin.codegen;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 代码生成器 生成样式类型
 * @ClassName:  CodeGenStyle   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年9月25日10:40:18   
 */
public enum CodeGenStyle {
	SINGLE("单表",1,"single.jf"),
	MASTER_SLAVE("主子表",2,"master_slave.jf");
	private String text;
	private int value;
	private String tpl;
	private CodeGenStyle(String text,int value,String tpl) {
		this.text = text;
		this.value = value;
		this.tpl = tpl;
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
	
	public String getTpl() {
		return tpl;
	}
	public void setTpl(String tpl) {
		this.tpl = tpl;
	}
	
	//加到JBolt Enum管理器
	static {
		JBoltEnum.addToTvBeanMap(CodeGenStyle.class);
	}
	
}
