package cn.jbolt.starter;

import com.jfinal.config.JFinalConfig;
import com.jfinal.server.undertow.UndertowConfig;

import cn.jbolt.core.server.JBoltServer;
/**
 * 项目服务器自定义
 * @ClassName:  JBoltServer
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年8月7日
 *
 */
public class ProjectServer extends JBoltServer {
	@Override
	public String getProjectName() {
		return "矽翔质管部管理系统";
	}

	@Override
	public String getProjectVersion() {
		return "2.4.0";
	}

	@Override
	protected void printOtherServerLog() {
		//举例 注意冒号位置统一
		//System.out.println("System Name          : " + JBoltConfig.SYSTEM_NAME);
		//System.out.println("Copyright Company    : " + JBoltConfig.SYSTEM_COPYRIGHT_COMPANY);
	}

	protected ProjectServer(UndertowConfig undertowConfig) {
		super(undertowConfig);
	}

	public static ProjectServer create(Class<? extends JFinalConfig> jfinalConfigClass, String undertowConfig) {
		return new ProjectServer(new UndertowConfig(jfinalConfigClass, undertowConfig));
	}

	public static ProjectServer create(String jfinalConfigClass, String undertowConfig) {
		return new ProjectServer(new UndertowConfig(jfinalConfigClass, undertowConfig));
	}

}
