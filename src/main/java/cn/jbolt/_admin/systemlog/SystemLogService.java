package cn.jbolt._admin.systemlog;

import cn.jbolt.base.JBoltProSystemLogTargetType;
import cn.jbolt.base.JBoltProSystemLogType;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.core.service.JBoltSystemLogService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.extend.systemlog.ProjectSystemLogType;

public class SystemLogService extends JBoltSystemLogService {

	@Override
	public String targetTypeToName(int targetType) {
		String name = JBoltEnum.getTextByValue(JBoltProSystemLogTargetType.class, targetType);
		if (notOk(name)) {
			name = JBoltEnum.getTextByValue(ProjectSystemLogTargetType.class, targetType);
			if (notOk(name)) {
				name = "未指定";
			}
		}
		return name;
	}

	@Override
	public String typeToName(int type) {
		String name = JBoltEnum.getTextByValue(JBoltProSystemLogType.class, type);
		if (notOk(name)) {
			name = JBoltEnum.getTextByValue(ProjectSystemLogType.class, type);
			if (notOk(name)) {
				name = "未指定";
			}
		}
		return name;
	}
	
	/**
	 * 自行扩展log关联URL
	 */
	@Override
	protected String processSystemLogUrl(int targetType, Object targetId) {
		String url = processJBoltProSystemLogUrl(targetType, targetId);
		if(notOk(url)) {
			url = processProjectSystemLogUrl(targetType, targetId);
		}
		return url;
	}
	/**
	 * 项目二开扩展
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	private String processProjectSystemLogUrl(int targetType, Object targetId) {
		ProjectSystemLogTargetType targetTypeEnum = JBoltEnum.getEnumObjectByValue(ProjectSystemLogTargetType.class, targetType);
		if(targetTypeEnum == null) {return null;}
		String url = null;
		switch (targetTypeEnum) {
//		case HIPRINT_TPL:
//			url = "admin/hiprinttpl/show/" + targetId;
//			break;
		default:
			break;
		}
		return url;
	}
	
	/**
	 * pro内置处理
	 * @param targetType
	 * @param targetId
	 * @return
	 */
	private String processJBoltProSystemLogUrl(int targetType, Object targetId) {
		JBoltProSystemLogTargetType targetTypeEnum = JBoltEnum.getEnumObjectByValue(JBoltProSystemLogTargetType.class, targetType);
		if(targetTypeEnum == null) {return null;}
		String url = null;
		switch (targetTypeEnum) {
		case HIPRINT_TPL:
			url = "admin/hiprinttpl/show/" + targetId;
			break;
		default:
			break;
		}
		return url;
	}
}
