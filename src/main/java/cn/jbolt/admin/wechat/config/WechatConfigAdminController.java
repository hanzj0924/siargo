package cn.jbolt.admin.wechat.config;

import cn.hutool.core.io.FileUtil;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoType;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.WechatConfig;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.util.JBoltRealUrlUtil;
import cn.jbolt.core.wechat.WechatConfigKey;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MediaApi;
import com.jfinal.weixin.sdk.api.MediaApi.MediaType;

import java.util.ArrayList;
import java.util.List;

/**   
 * 微信公众平台配置
 * @ClassName:  WechatConfigAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月12日 下午8:35:23   
 */
@OnlySaasPlatform
public class WechatConfigAdminController extends JBoltBaseController {
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatConfigService service;
	@CheckPermission(PermissionKey.WECHAT_CONFIG_BASEMGR)
	public void baseMgr(){
		Long mpId=getLong(0);
		List<WechatConfig> configs=service.checkAndInitConfig(mpId,WechatConfig.TYPE_BASE);
		set("configList",configs);
		set("configCount",configs.size());
		set("mpId", mpId);
		render("basemgr.html");
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_EXTRAMGR)
	public void extraMgr(){
		Long mpId=getLong(0);
		List<WechatConfig> configs=service.checkAndInitConfig(mpId,WechatConfig.TYPE_EXTRA);
		for(WechatConfig config:configs) {
			if(config.getConfigKey().equals(WechatConfigKey.MUSIC_POST_MEDIAID)&&isOk(config.getConfigValue())) {
				String value=JFinal.me().getConstants().getBaseUploadPath()+"/wechatconfig/musicpost/"+config.getConfigValue()+".jpg";
				config.put("imgUrl",JBoltRealUrlUtil.getWechatImage(value));
			}
		}
		set("configList",configs);
		set("configCount",configs.size());
		set("mpId", mpId);
		render("extramgr.html");
	}
	
	/**
	 * 上传图片到微信素材库
	 */
	@CheckPermission(PermissionKey.WECHAT_CONFIG_BASEMGR)
	public void uploadWechatMusicImgMedia(){
		UploadFile file=getFile("file","wechatconfig/musicpost");
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		Long mpId=getLong(0);
		String appId=service.getWechatConfigAppId(mpId);
		if(notOk(appId)) {
			renderJsonFail("当前操作微信公众平台，配置异常：APPID");
			return;
		}
		ApiConfigKit.setThreadLocalAppId(appId);
		ApiResult apiResult=null;
		try {
			apiResult=MediaApi.addMaterial(file.getFile(), MediaType.IMAGE);
		} finally {
			ApiConfigKit.removeThreadLocalAppId();
		}
		if(apiResult.isSucceed()) {
			String mediaId=apiResult.getStr("media_id");
			FileUtil.rename(file.getFile(), mediaId+".jpg", false, true);
			renderJsonData(mediaId);
		}else {
			renderJsonData(apiResult.getErrorMsg());
		}
		
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_BASEMGR)
	@Before(Tx.class)
	public void submitBaseConfig(){
		submitConfig(WechatConfig.TYPE_BASE);
		
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_EXTRAMGR)
	@Before(Tx.class)
	public void submitExtraConfig(){
		submitConfig(WechatConfig.TYPE_EXTRA);
		
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_PAYMGR)
	public void payMgr(){
		Long mpId=getLong(0);
		List<WechatConfig> configs=service.checkAndInitConfig(mpId,WechatConfig.TYPE_PAY);
		set("configList",configs);
		set("configCount",configs.size());
		set("mpId", mpId);
		render("paymgr.html");
	}
	private void submitConfig(int type){
		Integer configCount=getInt("configCount");
		if(notOk(configCount)){
			renderJsonFail(JBoltMsg.PARAM_ERROR);
		}else{
			Long mpId=getLong("mpId");
			WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
			if(mpinfo==null){
				renderJsonFail("微信公众平台信息不存在");
				return;
			}
			if(type==WechatConfig.TYPE_EXTRA) {
				if(!mpinfo.getEnable()){
					renderJsonFail("请在启用状态下操作更新此配置");
					return;
				}
				
			}else {
				if(mpinfo.getEnable()!=null&&mpinfo.getEnable()) {
					renderJsonFail("微信公众平台启用时无法更新配置信息，请在非启用状态下操作");
					return;
				}
			}
			 
			
				
			boolean isWxa=mpinfo.getType().intValue()==WechatMpinfoType.XCX.getValue();
			List<WechatConfig> configs=new ArrayList<WechatConfig>();
			for(int i=0;i<configCount;i++){
				configs.add(getModel(WechatConfig.class,"config["+i+"]"));
			}
			renderJson(service.updateConfigs(mpId,type,isWxa,configs));
		}
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_PAYMGR)
	@Before(Tx.class)
	public void submitPayConfig(){
		submitConfig(WechatConfig.TYPE_PAY);
	}
}
