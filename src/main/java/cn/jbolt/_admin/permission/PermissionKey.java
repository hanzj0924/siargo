package cn.jbolt._admin.permission;
import cn.jbolt.core.service.JBoltPermissionService;
/**
 * 权限定义KEY  用于在注解里使用
 * 举例 @CheckPermission(PermissionKey.USER)
 * 此文件由JBoltGeneratorGUI生成 请勿手动修改
 */
public class PermissionKey {
    /**
	 * 空权限
	 */
	public static final String NONE = JBoltPermissionService.PERMISSION_KEY_NONE;
	/**
	 * 数据分析
	 */
	public static final String DASHBOARD = "dashboard";
	/**
	 * 系统管理
	 */
	public static final String SYSTEMMGR = "systemmgr";
	/**
	 * 用户管理
	 */
	public static final String USER = "user";
	/**
	 * 部门管理
	 */
	public static final String DEPT = "dept";
	/**
	 * 岗位管理
	 */
	public static final String POST = "post";
	/**
	 * 权限配置
	 */
	public static final String ROLE_PERMISSION_MENU = "role_permission_menu";
	/**
	 * 角色管理
	 */
	public static final String ROLE = "role";
	/**
	 * 顶部导航
	 */
	public static final String TOPNAV = "topnav";
	/**
	 * 权限资源
	 */
	public static final String PERMISSION = "permission";
	/**
	 * 字典参数
	 */
	public static final String DICTIONARY_CONFIG = "dictionary_config";
	/**
	 * 数据字典
	 */
	public static final String DICTIONARY = "dictionary";
	/**
	 * 全局参数
	 */
	public static final String GLOBALCONFIG = "globalconfig";
	/**
	 * 系统通知
	 */
	public static final String SYS_NOTICE = "sys_notice";
	/**
	 * 七牛配置
	 */
	public static final String QINIU_CONFIG = "qiniu_config";
	/**
	 * 七牛账号
	 */
	public static final String QINIU = "qiniu";
	/**
	 * 七牛Bucket
	 */
	public static final String QINIU_BUCKET = "qiniu_bucket";
	/**
	 * 敏感词词库
	 */
	public static final String SENSITIVE_WORD = "sensitive_word";
	/**
	 * 系统监控
	 */
	public static final String JBOLT_MONITOR = "jbolt_monitor";
	/**
	 * Druid数据库监控
	 */
	public static final String DRUID_MONITOR = "druid_monitor";
	/**
	 * 服务器监控
	 */
	public static final String JBOLT_SERVER_MONITOR = "jbolt_server_monitor";
	/**
	 * 日志监控
	 */
	public static final String JBOLT_LOG_MONITOR = "jbolt_log_monitor";
	/**
	 * 登录日志
	 */
	public static final String JBOLT_LOGIN_LOG = "jbolt_login_log";
	/**
	 * 关键操作日志
	 */
	public static final String SYSTEMLOG = "systemlog";
	/**
	 * 在线用户
	 */
	public static final String ONLINE_USER = "online_user";
	/**
	 * 开发平台
	 */
	public static final String DEV_PLATFORM = "dev_platform";
	/**
	 * 应用中心
	 */
	public static final String APPLICATION = "application";
	/**
	 * 微信公众平台
	 */
	public static final String WECHAT_MPINFO = "wechat_mpinfo";
	/**
	 * 基础配置
	 */
	public static final String WECHAT_CONFIG_BASEMGR = "wechat_config_basemgr";
	/**
	 * 菜单配置
	 */
	public static final String WECHAT_MENU = "wechat_menu";
	/**
	 * 支付配置
	 */
	public static final String WECHAT_CONFIG_PAYMGR = "wechat_config_paymgr";
	/**
	 * 关注回复
	 */
	public static final String WECHAT_AUTOREPLY_SUBSCRIBE = "wechat_autoreply_subscribe";
	/**
	 * 关键词回复
	 */
	public static final String WECHAT_AUTOREPLY_KEYWORDS = "wechat_autoreply_keywords";
	/**
	 * 默认回复
	 */
	public static final String WECHAT_AUTOREPLY_DEFAULT = "wechat_autoreply_default";
	/**
	 * 素材库
	 */
	public static final String WECHAT_MEDIA = "wechat_media";
	/**
	 * 用户管理
	 */
	public static final String WECHAT_USER = "wechat_user";
	/**
	 * 其它配置
	 */
	public static final String WECHAT_CONFIG_EXTRAMGR = "wechat_config_extramgr";
	/**
	 * 报表设计器
	 */
	public static final String UREPORT_DESIGNER = "ureport_designer";
	/**
	 * 打印设计器
	 */
	public static final String HIPRINT_DESIGN = "hiprint_design";
	/**
	 * 代码生成器
	 */
	public static final String JBOLT_CODE_GEN = "jbolt_code_gen";
	/**
	 * 开发文档
	 */
	public static final String ADMIN_DEV_DOC = "admin_dev_doc";
	/**
	 * 数据库文档
	 */
	public static final String ADMIN_DEV_DOC_DATABASE = "admin_dev_doc_database";
	/**
	 * 独立逻辑权限
	 */
	public static final String LOGIC_PERMISSION = "logic_permission";
	/**
	 * Ureport报表查看权
	 */
	public static final String UREPORT_DETAIL = "ureport_detail";
	/**
	 * siargo
	 */
	public static final String SIARGO = "siargo";
	/**
	 * 报告单管理
	 */
	public static final String QAREP = "qarep";
	/**
	 * 客户管理
	 */
	public static final String CUSTOMER = "customer";
}
