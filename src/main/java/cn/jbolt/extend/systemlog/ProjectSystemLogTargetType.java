package cn.jbolt.extend.systemlog;

import cn.jbolt.core.enumutil.JBoltEnum;
/**
 * 项目中systemLog关联类型
 * @ClassName:  ProjectSystemLogTargetType   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2022年2月24日   
 */
public enum ProjectSystemLogTargetType {
	NONE("NONE",0),
	CUSTOM_BUSINESS("系统业务定义",10001),
	QAREPORT("检验报告单",20001),
	
	EQUIPMENT("设备",20002),
	EQUIPMENTCERTIFICATERECORD("设备证书记录",20004),
	EQUIPMENTRECORD("设备记录",20003)
	;
	private String text;
	private int value;
	private ProjectSystemLogTargetType(String text,int value) {
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
	
	//加入到JBolt Enum管理器
	static {
		JBoltEnum.addToTvBeanMap(ProjectSystemLogTargetType.class);
	}
}