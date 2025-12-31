package cn.jbolt.starter;

import cn.jbolt.core.util.JBoltArrayUtil;
import com.jfinal.core.JFinal;
import com.jfinal.server.undertow.WebBuilder;
import cn.jbolt._admin.websocket.JBoltWebSocketServerEndpoint;
import cn.jbolt.common.config.ProjectConfig;
import cn.jbolt.core.base.config.JBoltConfig;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.DisallowedMethodsHandler;
import io.undertow.util.HttpString;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目启动器
 * @ClassName:  Starter
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2019年11月9日
 */
public class Starter {
	/**
	 * 配置Filter
	 * @param builder
	 */
	public void configFilter(WebBuilder builder) {
		//配置不允许访问的httpMethods
		configDisallowedHttpMethods(builder);
		// 配置 Filter
		//builder.addFilter("myFilter", "com.abc.MyFilter");
		//builder.addFilterUrlMapping("myFilter", "/*");
		//builder.addFilterInitParam("myFilter", "key", "value");
	}

	/**
	 * 配置不允许访问的httpMethods
	 * @param builder
	 */
	private void configDisallowedHttpMethods(WebBuilder builder) {
		String disallowedMethodsStr = JBoltConfig.prop.get("disallowed_http_methods","TRACE,TRACK");
		String[] disallowedMethodsArray = JBoltArrayUtil.from3(disallowedMethodsStr);
		if(disallowedMethodsArray!=null&& disallowedMethodsArray.length>0){
			builder.getDeploymentInfo().addInitialHandlerChainWrapper(new HandlerWrapper() {
				@Override
				public HttpHandler wrap(HttpHandler handler) {
					List<HttpString> disallowedMethods = new ArrayList<HttpString>();
					for(String method:disallowedMethodsArray){
						disallowedMethods.add(HttpString.tryFromString(method.toUpperCase()));
					}
					return new DisallowedMethodsHandler(handler,disallowedMethods.toArray(new HttpString[0]));
				}
			});
		}
	}

	/**
	 * 配置Servlet
	 * @param builder
	 */
	public void configServlet(WebBuilder builder) {
		// 配置 Servlet
		//builder.addServlet("myServlet", "com.abc.MyServlet");
		//builder.addServletMapping("myServlet", "*.do");
		//builder.addServletInitParam("myServlet", "key", "value");
		if(JBoltConfig.JBOLT_UREPORT_ENABLE) {
			builder.addServlet("ureportServlet", "com.bstek.ureport.console.UReportServlet");
			builder.addServletMapping("ureportServlet", "/ureport/*");
		}
	}
	/**
	 * 配置监听Listener
	 * @param builder
	 */
	public void configListener(WebBuilder builder) {
//		builder.addListener("cn.jbolt.xxxListener");
		// 配置 SessionListener
//		builder.getDeploymentInfo().addSessionListener(new JBoltSessionListener());
		if(JBoltConfig.JBOLT_UREPORT_ENABLE) {
			// 配置 Listener
			builder.addListener("org.springframework.web.context.ContextLoaderListener");
			//配置spring applicationContext.xml的位置
			builder.addInitParameter("contextConfigLocation", "classpath:applicationContext.xml");
		}
	}
	/**
	 * 配置webSocket
	 * @param builder
	 */
	public void configWebSocket(WebBuilder builder) {
		// 配置 WebSocket，MyWebSocket 需使用 ServerEndpoint 注解
		//builder.addWebSocketEndpoint("com.abc.MyWebSocket");
		if(JBoltConfig.JBOLT_WEBSOCKET_ENABLE) {
			builder.addWebSocketEndpoint(JBoltWebSocketServerEndpoint.class);
		}
	}
	/**
	 * 配置JSP
	 * @param builder
	 */
	public void configJsp(WebBuilder builder) {
//		builder.getDeploymentInfo().addServlet(JspServletBuilder.createServlet("Default Jsp Servlet", "*.jsp"));
//	    HashMap tagLibraryInfo = new HashMap();
//	    JspServletBuilder.setupDeployment(builder.getDeploymentInfo(), new HashMap(),
//	    tagLibraryInfo, new HackInstanceManager());
	}
	/**
	 * 配置默认errorPage welcomePage等默认页面
	 * @param builder
	 */
	public void configDefaultPage(WebBuilder builder) {
		builder.add404ErrorPage("/_view/_admin/common/msg/undertow_404.html");
		builder.addErrorPage(500,"/_view/_admin/common/msg/undertow_500.html");
//		builder.addWelcomePage("/_view/_admin/common/msg/undertow_welcome.html");
	}
	/**
	 * 创建并启动
	 */
	public void run() {
		JBoltConfig.loadConfig();
		ProjectServer.create(ProjectConfig.class,"undertow.properties")
					 .configWeb(builder -> {
					  	//配置Filter
					  	configFilter(builder);
					  	//配置Servlet
					  	configServlet(builder);
					  	//配置监听Listener
					  	configListener(builder);
					  	//配置webSocket
					  	configWebSocket(builder);
					  	//配置Jsp支持
		//			  	configJsp(builder);
					  	//配置一些Undertow默认页面 404 welcomepage等
					  	configDefaultPage(builder);
					  	//配置sentinel
					  	configSentinel(builder);
				       }).start();
	}

	/**
	 * 配置Sentinel
	 * @param builder
	 */
	private void configSentinel(WebBuilder builder) {
		if(!JBoltConfig.JBOLT_SENTINEL_ENABLE) {return;}
		//配置Filter
//		builder.addFilter("sentinelFilter", "com.alibaba.csp.sentinel.adapter.servlet.CommonFilter");
//		builder.addFilterUrlMapping("sentinelFilter", "/*");
//		builder.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY, "false");
	}

	/**
	 * 启动器入口
	 * @param args
	 */
	public static void main(String[] args) {
		new Starter().run();
	}
}
