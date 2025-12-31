package cn.jbolt._admin.globalconfig;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.PathKit;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltGlobalConfigKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.GlobalConfig;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;

/**
 * 全局配置项
* @author 小木 qq:909854136
* @version 创建时间：2018年12月25日 下午11:13:57
*/
@CheckPermission(PermissionKey.GLOBALCONFIG)
@UnCheckIfSystemAdmin
public class GlobalConfigAdminController extends JBoltBaseController {
	@Inject
	private GlobalConfigTypeService globalConfigTypeService;
	@Inject
	private GlobalConfigService service;

	public void index(){
		service.checkAndInit();
		render("index.html");
	}
	/**
	 * 数据源
	 * @param keywords
	 * @param builtIn
	 * @param type
	 */
	public void datas(String keywords,Boolean builtIn,Long type) {
		renderJsonData(service.getAdminList(type,builtIn,keywords));
	}
	/**
	 * 列出登录背景图数据源
	 */
	private List<String> getLoginBgimgs() {
		List<String> imgs=new ArrayList<String>();
		FileUtil.loopFiles(PathKit.getWebRootPath()+"/assets/css/img/loginbg",(file)->{
			imgs.add("assets/css/img/loginbg/"+file.getName());
			return false;
		});
		return imgs;
	}
	/**
	 * 列出登录背景视频数据源
	 */
	private List<String> getLoginBgvideos() {
		List<String> videos=new ArrayList<String>();
		FileUtil.loopFiles(PathKit.getWebRootPath()+"/assets/css/video/loginbg",(file)->{
			String name=file.getName();
			String suffix=FileNameUtil.getSuffix(name);
			if(isOk(suffix)&&(suffix.equalsIgnoreCase("mp4")||suffix.equalsIgnoreCase("webm"))) {
				videos.add("assets/css/video/loginbg/"+name);
			}
			return false;
		});
		return videos;
	}

	/**
	 * 编辑
	 */
	public void edit(){
		GlobalConfig globalConfig=service.findById(getLong(0));
		if(globalConfig!=null&&globalConfig.getBuiltIn()!=null&&globalConfig.getBuiltIn()==false) {
			renderFormFail("参数为非系统内置参数，无法通过此处编辑");
			return;
		}
		set("globalConfig", globalConfig);
		if(globalConfig!=null&&JBoltGlobalConfigKey.JBOLT_LOGIN_BGIMG.equals(globalConfig.getConfigKey())) {
			set("imgs", getLoginBgimgs());
			set("videos", getLoginBgvideos());
		}
		render("edit.html");
	}
	/**
	 * 更新
	 */
	public void update(){
		GlobalConfig globalConfig=getModel(GlobalConfig.class, "globalConfig");
		renderJson(service.update(globalConfig));
	}
	/**
	 * 刷新assetsVersion
	 */
	public void changeAssetsVersion(){
		renderJson(service.changeAssetsVersion());
	}

	/**
	 * 编辑非内置
	 */
	public void editCustomConfig(){
		GlobalConfig globalConfig=service.findById(getLong(0));
		if(globalConfig!=null&&globalConfig.getBuiltIn()!=null&&globalConfig.getBuiltIn()) {
			renderFormFail("参数为系统内置参数，无法通过此处编辑");
			return;
		}
		set("globalConfig", globalConfig);
		render("editcus.html");
	}

	/**
	 * 更新自定义参数
	 */
	public void updateCustomConfig(){
		GlobalConfig globalConfig=getModel(GlobalConfig.class, "globalConfig");
		renderJson(service.updateCustomConfig(globalConfig));
	}
	/**
	 * 保存自定义参数
	 */
	public void saveCustomConfig(){
		GlobalConfig globalConfig=getModel(GlobalConfig.class, "globalConfig");
		renderJson(service.saveCustomConfig(globalConfig));
	}

	/**
	 * 切换Boolean类型的值
	 */
	public void toggleBooleanValue() {
		renderJson(service.toggleBooleanValue(getLong(0)));
	}

	/**
	 * 删除自定义参数
	 */
	public void deleteCustomConfig(){
		renderJson(service.deleteCustomConfig(getLong(0)));
	}
	/**
	 * 清空缓存 指定type
	 */
	public void clearCacheByType() {
		Long typeId = getLong(0);
		if(notOk(typeId)){
			renderJsonFail("参数错误");
			return;
		}
		service.clearCacheByType(typeId);
		globalConfigTypeService.deleteCacheById(typeId);
		renderJsonSuccess();
	}
	/**
	 * 清空缓存
	 */
	public void clearCache() {
		service.clearCache();
		globalConfigTypeService.clearCache();
		renderJsonSuccess();
	}

	/**
	 * 清空缓存 重新初始化
	 */
	public void clearReset() {
		renderJson(service.clearReset());
	}

	/**
	 * 新增参数
	 */
	public void addCustomConfig() {
		Long typeId=getLong(0);
		if(notOk(typeId)) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		set("typeId", typeId);
		render("addcus.html");
	}

}
