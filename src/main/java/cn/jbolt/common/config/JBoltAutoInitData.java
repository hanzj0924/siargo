package cn.jbolt.common.config;

import cn.jbolt.core.base.config.JBoltConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Aop;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.hutool.core.io.FileUtil;
import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.admin.appdevcenter.ApplicationService;
import cn.jbolt.core.model.Permission;
import cn.jbolt.core.model.User;

/**
 * 检测并自动初始化数据库
 * 在这里实现，文件由JBolt官方操作，他人请勿修改
 * @ClassName:  JBoltAutoInitData
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年01月19日
 */
public class JBoltAutoInitData {
	public static final JBoltAutoInitData me=new JBoltAutoInitData();
	private final Log LOG=Log.getLog(JBoltAutoInitData.class);
	private UserService userService;
	private PermissionService permissionService;
	private DictionaryService dictionaryService;
	private ApplicationService applicationService;
	private static final String PERMISSION_JSON="permission_init.json";
	private JBoltAutoInitData() {
		userService=Aop.get(UserService.class);
		permissionService=Aop.get(PermissionService.class);
		dictionaryService=Aop.get(DictionaryService.class);
		applicationService=Aop.get(ApplicationService.class);
	}
	/**
	 * 执行
	 */
	public void exe() {
		LOG.debug("JBolt自动检测并初始化数据：开始执行");
		boolean needInitSuperAdminUser=checkNeedInitSuperAdminUser();
		if(needInitSuperAdminUser) {
			initSuperAdminUser();
		}
		boolean needInitPermission=checkNeedInitPermission();
		if(needInitPermission) {
			initPermission();
		}
		if(JBoltConfig.ONSTART_CHECK_AND_INIT_DICTIONARY_ENABLE){
			checkAndInitDictionary();
		}
		checkAndInitSystemInnerApplication();
		LOG.debug("JBolt自动检测并初始化数据：执行完成");
	}
	/**
	 *	检测并初始化系统内置application 作为后台管理默认使用 其他业务不得操作此application
	 */
	private void checkAndInitSystemInnerApplication() {
		LOG.debug("正在检测和初始化[系统内置Application]...");
		applicationService.checkAndInitPcInnerPlatformApplication();
		LOG.debug("[系统内置Application]处理完成...");
	}
	/**
	 *  检测缺失字典类型和数据
	 */
	private void checkAndInitDictionary() {
		LOG.debug("正在检测和初始化[系统内置字典]...");
		dictionaryService.checkAndInit();
		LOG.debug("[系统内置字典]处理完成...");
	}

	/**
	 * 检测是否需要初始化user
	 * @return
	 */
	private boolean checkNeedInitSuperAdminUser() {
		LOG.debug("正在检测是否需要进行[系统User-超管员]数据初始化...");
		boolean hasSuperAdmin=userService.checkExistSuperAdmin();
		LOG.debug("经检测"+(hasSuperAdmin?"不需要":"需要")+"执行[系统User-超管员]数据初始化");
		return !hasSuperAdmin;
	}

	/**
	 * 检测是否需要初始化permission
	 * @return
	 */
	private boolean checkNeedInitPermission() {
		LOG.debug("正在检测是否需要进行[系统Permission-权限资源]数据初始化...");
		int totalCount=permissionService.getCount();
		boolean hasPermission=totalCount>0;
		LOG.debug("经检测"+(hasPermission?"不需要":"需要")+"执行[系统Permission-权限资源]数据初始化");
		return !hasPermission;
	}

	/**
	 * 执行权限资源定义表初始化
	 */
	private void initPermission() {
		LOG.debug("正在初始化[系统Permission-权限资源]...");
		LOG.debug("正在读取权限资源初始化JSON文件["+PERMISSION_JSON+"]...");
		if(!FileUtil.exist(PERMISSION_JSON)) {
			LOG.error("[系统Permission-权限资源]相关初始化json文件未找到");
			return;
		}
		String json=FileUtil.readUtf8String(PERMISSION_JSON);
		if(StrKit.isBlank(json)) {
			LOG.error("[系统Permission-权限资源]相关初始化json文件中未读取到任何数据");
			return;
		}
		LOG.debug("正在解析并初始化权限资源...");
		JSONArray permissions=JSON.parseArray(json);
		if(permissions==null|| permissions.isEmpty()) {
			LOG.error("数据解析失败,无法进行[系统Permission-权限资源]初始化...");
			return;
		}
		LOG.debug("数据解析成功，开始入库...");
		processPermissionsInsert(permissions);
		LOG.debug("[系统Permission-权限资源]数据入库完成...");
	}

	private void processPermissionsInsert(JSONArray permissions) {
		if(permissions!=null&& !permissions.isEmpty()) {
			JSONObject permissionObj;
			Permission permission;
			int size=permissions.size();
			for(int i=0;i<size;i++) {
				permissionObj=permissions.getJSONObject(i);
				permission=convertToPermission(permissionObj);
				LOG.debug("正在初始化:权限[{}]...",permission.getTitle()+"_"+permission.getPermissionKey());
				permission.save();
				processSonPermissionsInsert(permissionObj,permission);
			}
		}
	}

	private Permission convertToPermission(JSONObject permissionObj) {
		Permission permission=new Permission();
		permission.autoProcessIdValue();
		permission.setPid(0L);
		permission.setIcons(permissionObj.getString("icons"));
		permission.setIsMenu(permissionObj.getBoolean("isMenu"));
		permission.setIsSystemAdminDefault(permissionObj.getBoolean("isSystemAdminDefault"));
		permission.setIsTargetBlank(permissionObj.getBoolean("isTargetBlank"));
		permission.setOpenOption(permissionObj.getString("openOption"));
		permission.setOpenType(permissionObj.getInteger("openType"));
		permission.setPermissionKey(permissionObj.getString("permissionKey"));
		permission.setSortRank(permissionObj.getInteger("sortRank"));
		permission.setTitle(permissionObj.getString("title"));
		permission.setUrl(permissionObj.getString("url"));
		permission.setPermissionLevel(permissionObj.getInteger("permissionLevel"));
		return permission;
	}
	private void processSonPermissionsInsert(JSONObject permissionObj, Permission permission) {
		JSONArray sons=permissionObj.getJSONArray("items");
		if(sons!=null&& !sons.isEmpty()) {
			int size=sons.size();
			JSONObject sonObj;
			Permission son;
			for(int i=0;i<size;i++) {
				sonObj=sons.getJSONObject(i);
				son=convertToPermission(sonObj);
				LOG.debug("正在初始化:权限[{}]...",son.getTitle()+"_"+son.getPermissionKey());
				son.setPid(permission.getId());
				son.save();
				processSonPermissionsInsert(sonObj,son);
			}
		}

	}

	/**
	 * 执行用户初始化
	 */
	private void initSuperAdminUser() {
		LOG.debug("正在初始化[系统User-超管员]...");
		User user=new User();
		user.setName("总管理");
		user.setEnable(true);
		user.setPinyin("zgl,zongguanli");
		user.setSex(1);
		user.setUsername("admin");
		user.setIsSystemAdmin(true);
		String password="123";
		String pwdSalt=HashKit.generateSaltForSha256();
		user.setPwdSalt(pwdSalt);
		user.setPassword(userService.calPasswordWithSalt(password, pwdSalt));
		user.autoProcessIdValue();
		user.setCreateUserId(0L);
		user.setUpdateUserId(0L);
		boolean success=user.save();
		LOG.debug("初始化[系统User-超管员]："+(success?"成功":"失败"));
	}
}
