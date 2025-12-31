package cn.jbolt._admin.codegen;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 代码生成器 生成状态
 * @ClassName:  CodeGenStyle   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年9月25日10:40:18   
 */
public enum CodeGenState {
	NONE("新创建未生成",1),
	NOT_GEN("更新后未生成",2),
	ONLY_MODEL_GEN("更新只生成Model",3),
	ONLY_MAIN_LOGIC_GEN("更新只生成主逻辑",4),
	GENED("已执行全部生成",5);
	private String text;
	private int value;
	private CodeGenState(String text,int value) {
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
		JBoltEnum.addToTvBeanMap(CodeGenState.class);
	}
	
}
