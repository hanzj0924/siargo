package cn.jbolt.extend.config;

import cn.hutool.setting.Setting;
import cn.jbolt.core.kit.*;
import cn.jbolt.core.safe.XssHandler;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

import cn.jbolt.core.base.config.JBoltExtendDatabaseConfig;
import cn.jbolt.core.handler.base.JBoltBaseHandler;
/**
 * 二开专用项目工程配置类
 * @ClassName:  MineProjectConfig
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2020年3月17日
 */
public class ExtendProjectConfig {
	private static final Log LOG=Log.getLog(ExtendProjectConfig.class);
	/**
	 * 常量配置
	 * @param me
	 */
	public static void configConstant(Constants me) {
		LOG.debug("调用二开扩展配置:configConstant");
	}
	/**
	 * 路由配置
	 * @param me
	 */
	public static void configRoute(Routes me) {
		LOG.debug("调用二开扩展配置:configRoute");
		//调用使用可视化生成器生成的路由配置信息
		ProjectCodeGenRoutesConfig.config(me);
	}
	/**
	 * 插件配置
	 * @param me
	 * @param cron4jPlugin
	 */
	public static void configPlugin(Plugins me, Cron4jPlugin cron4jPlugin) {
		LOG.debug("调用二开扩展配置:configPlugin");
		//事务配置
		configTx();
		//如果需要二开 增加自己的自动调度任务
		configCron4jPlugin(me,cron4jPlugin);
	}

	/**
	 * 事务相关自定义配置txFun等
	 */
	private static void configTx() {
		LOG.debug("调用二开扩展配置:configTx");
	}

	/**
	 * 配置自动调度插件
	 * @param me
	 * @param cron4jPlugin
	 */
	private static void configCron4jPlugin(Plugins me,Cron4jPlugin cron4jPlugin) {
		LOG.debug("调用二开扩展配置:configCron4jPlugin");
		//可以直接在 cron4jPlugin 中addTask
//		cron4jPlugin.addTask("0-59/1 * * * *", new WechatMediaDownloaTask());

		//也可以自己添加新的cron4jPlugin
//	    Cron4jPlugin cron4jPlugin2 = new Cron4jPlugin();
//	    cron4jPlugin2.addTask("0-59/1 * * * *", new WechatMediaDownloaTask());
//	    me.add(cron4jPlugin2);
	}

	/**
	 * 全局拦截器配置
	 * @param me
	 */
	public static void configInterceptor(Interceptors me) {
		LOG.debug("调用二开扩展配置:configInterceptor");
	}
	/**
	 * 全局handler处理器配置
	 * @param me
	 * @param baseHandler
	 * @param xssHandler
	 */
	public static void configHandler(Handlers me, JBoltBaseHandler baseHandler, XssHandler xssHandler) {
		LOG.debug("调用二开扩展配置:configHandler");
		//配置baseHandler 处理页面basePath pmkey 静态资源html直接访问拦截等
		//baseHandler.unlimited("/assets/plugins/","/neditor/");
		//配置xss 略过哪个目录和target
		//xssHandler.unlimited("admin/sysnotice/save","admin/sysnotice/update");
	}

	/**
	 * 模板引擎配置
	 * @param me
	 */
	public static void configEngine(Engine me) {
		LOG.debug("调用二开扩展配置:configEngine");
	}

	/**
	 * 启动后处理
	 */
	public static void onStart() {
		LOG.debug("调用二开扩展配置:onStart");
	}

	/**
	 * 结束前处理
	 */
	public static void onStop() {
		LOG.debug("调用二开扩展配置:onStop");
	}

	/**
	 * 扩展主库的插件配置 arp和sql模板
	 * @param druidPlugin
	 * @param arp
	 * @param sqlEngine
	 */
	public static void configMainDbPlugin(DruidPlugin druidPlugin, ActiveRecordPlugin arp, Engine sqlEngine) {
		LOG.debug("调用二开扩展配置:configMainDbPlugin");
	}

	/**
	 * 扩展数据源插件配置 arp和sql模板
	 * @param dbPlugin
	 * @param arp
	 * @param sqlEngine
	 * @param configName
	 * @param dbSetting
	 */
	public static void configExtendDbPlugins(DruidPlugin dbPlugin, ActiveRecordPlugin arp, Engine sqlEngine, String configName, Setting dbSetting){
		LOG.debug("调用二开扩展配置:configExtendDbPlugins");
	}

	/**
	 * 手动管理添加 新开项目的需要给租户分表的Model
	 */
	public static void configProjectSaasSeparateTableModels() {
		LOG.debug("调用二开扩展配置:configProjectSaasSeparateTableModels");
	}

	/**
	 * 配置租户获得当前是否可访问的处理器
	 * @return
	 */
	public static JBoltSaasTenantAccessibleProcessor configSaasTenantAccessibleProcessor() {
		return null;
	}

	/**
	 * 配置租户列表获取的处理器
	 * @return
	 */
	public static JBoltSaasTenantGetAllProcessor configSaasTenantAllDatasProcessor() {
		return null;
	}

	/**
	 * 配置租户ID转sn的转换器
	 * @return
	 */
	public static JBoltSaasTenantIdToSnProcessor configSaasTenantIdToSnProcessor() {
		return null;
	}
	/**
	 * 配置租户ID转Name的转换器
	 * @return
	 */
	public static JBoltSaasTenantIdToNameProcessor configSaasTenantIdToNameProcessor() {
		return null;
	}

	/**
	 * 配置租户sn转name的转换器
	 * @return
	 */
	public static JBoltSaasTenantSnToNameProcessor configSaasTenantSnToNameProcessor() {
		return null;
	}

	/**
	 * 配置租户SN转ID的转换器
	 * @return
	 */
	public static JBoltSaasTenantSnToIdProcessor configSaasTenantSnToIdProcessor() {
		return null;
	}
}
