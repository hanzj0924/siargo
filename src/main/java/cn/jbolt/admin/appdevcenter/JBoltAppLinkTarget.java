package cn.jbolt.admin.appdevcenter;
/**
 * 关联对象bean
 * @ClassName:  LinkTarget   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年3月22日   
 */
public class JBoltAppLinkTarget {
	private Object id;
	private String name;
	private String logo;
	private String typeName;
	private String subjectTypeName;
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getSubjectTypeName() {
		return subjectTypeName;
	}
	public void setSubjectTypeName(String subjectTypeName) {
		this.subjectTypeName = subjectTypeName;
	}
}
