package cn.jbolt.admin.wechat.media;

import java.util.ArrayList;
import java.util.List;

import cn.jbolt.common.enums.WechatMediaType;
import cn.jbolt.core.enumutil.JBoltEnum;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.config.WechatConfigService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.model.WechatMedia;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.base.JBoltPageSize;
import cn.jbolt.core.bean.Option;
import cn.jbolt.core.bean.OptionBean;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheck;
/**
 * 微信公众平台素材库管理
 * @ClassName:  WechatMediaAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年6月25日   
 */
@CheckPermission(PermissionKey.WECHAT_MEDIA)
@OnlySaasPlatform
public class WechatMediaAdminController extends JBoltBaseController {
	@Inject
	private WechatMediaService service;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatConfigService wechatConfigService;
	@Before(WechatMediaMgrValidator.class)
	public void index() {
		Long mpId=getLong(0);
		WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
		if(mpinfo==null) {renderDialogFail("微信公众平台信息不存在");return;}
		String type=get("type", WechatMediaType.NEWS.getValue());
		set("pageData", service.paginateAdminList(mpId,type,getKeywords(),getPageNumber(),getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST_20)));
		keepPara("keywords");
		set("mpId", mpId);
		set("type", type);
		render("index.html");
	}
	@Before(WechatMediaMgrValidator.class)
	public void download() {
		renderJson(service.downloadWechatMedia(getLong(0)));
	}
	@CheckPermission({PermissionKey.WECHAT_AUTOREPLY_DEFAULT,PermissionKey.WECHAT_AUTOREPLY_KEYWORDS,PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE})
	public void choose() {
		Long mpId=getLong(0);
		String type=get(1);
		if(notOk(mpId)||notOk(type)) {renderDialogFail(JBoltMsg.PARAM_ERROR);return;}
		WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
		if(mpinfo==null) {renderDialogFail("微信公众平台信息不存在");return;}
		set("pageData", service.paginateAdminList(mpId,type,getKeywords(),getPageNumber(),getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST_20)));
		keepPara("keywords");
		set("mpId", mpId);
		set("type", type);
		render("choose.html");
	}
	@CheckPermission({PermissionKey.WECHAT_AUTOREPLY_DEFAULT,PermissionKey.WECHAT_AUTOREPLY_KEYWORDS,PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE})
	@Before(WechatMediaMgrValidator.class)
	public void chooseIt() {
		renderJson(service.getReplyChooseInfo(getLong(0),getLong(1)));
	}
	@UnCheck
	public void types() {
		renderJsonData(JBoltEnum.getEnumOptionList(WechatMediaType.class));
	}
	
	@Before(WechatMediaMgrValidator.class)
	public void syncAll() {
		renderJson(service.syncAll(getLong(0)));
	}
	@Before(WechatMediaMgrValidator.class)
	public void syncNewDatas() {
		renderJson(service.syncNewDatas(getLong(0)));
	}
}
