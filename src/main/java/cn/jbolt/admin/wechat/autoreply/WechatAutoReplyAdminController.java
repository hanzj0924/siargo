package cn.jbolt.admin.wechat.autoreply;

import java.util.ArrayList;
import java.util.List;

import cn.jbolt.common.enums.WechatAutoreplyReplyType;
import cn.jbolt.common.enums.WechatAutoreplyType;
import cn.jbolt.core.enumutil.JBoltEnum;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.bean.Option;
import cn.jbolt.core.bean.OptionBean;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.JBoltUserAuthKit;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;

/**
 * 微信公众平台自动回复设置管理
 * 
 * @ClassName: WechatAutoReplyAdminController
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年6月20日
 */
@UnCheckIfSystemAdmin
@OnlySaasPlatform
public class WechatAutoReplyAdminController extends JBoltBaseController {
	@Inject
	private WechatAutoReplyService service;
	@CheckPermission(PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE)
	@Before(WechatAutoReplyMgrValidator.class)
	public void subscribeReplyMgr() {
		set("title", "关注后回复");
		Long mpId=getLong(0);
		set("action", "/admin/wechat/autoreply/subscribeReplyMgr/"+mpId);
		mgr(mpId, WechatAutoreplyType.SUBSCRIBE.getValue(), getKeywords());
	}
	@CheckPermission(PermissionKey.WECHAT_AUTOREPLY_KEYWORDS)
	@Before(WechatAutoReplyMgrValidator.class)
	public void keywordsReplyMgr() {
		set("title", "关键词回复");
		Long mpId=getLong(0);
		set("action", "/admin/wechat/autoreply/keywordsReplyMgr/"+mpId);
		mgr(mpId, WechatAutoreplyType.KEYWORDS.getValue(), getKeywords());
	}
	@CheckPermission(PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE)
	@Before(WechatAutoReplyMgrValidator.class)
	public void defaultReplyMgr() {
		set("title", "默认回复");
		Long mpId=getLong(0);
		set("action", "/admin/wechat/autoreply/defaultReplyMgr/"+mpId);
		mgr(mpId, WechatAutoreplyType.DEFAULT.getValue(), getKeywords());
	}

	private void mgr(Long mpId, int type, String keywords) {
		set("pageData",service.paginateAdminMgrList(mpId, type, keywords, getPageNumber(), getPageSize()));
		set("mpId", mpId);
		set("type", type);
		set("keywords", keywords);
		render("index.html");
	}
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void add() {
		Long mpId=getLong(0);
		Integer type=getInt(1);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogFail(JBoltMsg.NOPERMISSION);
			return;
		}
		set("mpId", mpId);
		set("type", type);
		render("add.html");
	}
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void edit() {
		Long mpId=getLong(0);
		Integer type=getInt(1);
		Long id=getLong(2);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogFail(JBoltMsg.NOPERMISSION);
			return;
		}
		WechatAutoreply wechatAutoreply=service.findById(id);
		if(wechatAutoreply==null) {
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		if(wechatAutoreply.getMpId().longValue()!=mpId.longValue()) {renderDialogFail("参数异常:公众平台mpId");return;}
		if(wechatAutoreply.getType().intValue()!=type.intValue()) {renderDialogFail("参数异常:公众平台type");return;}
		set("wechatAutoreply", wechatAutoreply);
		set("mpId", mpId);
		set("type", type);
		render("edit.html");
	}
	
	@UnCheck
	@Before({WechatAutoReplyMpIdAndTypeValidator.class,Tx.class})
	public void delete() {
		Long mpId=getLong(0);
		Integer type=getInt(1);
		Long id=getLong(2);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogFail(JBoltMsg.NOPERMISSION);
			return;
		}
		renderJson(service.deleteAutoreply(mpId,type,id));
	}
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void toggleEnable() {
		Long mpId=getLong(0);
		Integer type=getInt(1);
		Long id=getLong(2);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogFail(JBoltMsg.NOPERMISSION);
			return;
		}
		renderJson(service.toggleEnable(mpId,type,id));
	}
	
	
	@UnCheck
	public void replyTypes() {
		renderJsonData(JBoltEnum.getEnumOptionList(WechatAutoreplyReplyType.class));
	}
	private boolean checkPermission(Integer type) {
		boolean hasPermission=false;
		Long userId = JBoltUserKit.getUserId();
		WechatAutoreplyType wechatAutoreplyType = JBoltEnum.getEnumObjectByValue(WechatAutoreplyType.class,type);
		if(wechatAutoreplyType != null){
			switch (wechatAutoreplyType) {
			case SUBSCRIBE:
				hasPermission=JBoltUserAuthKit.hasPermission(userId,true, PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE);
				break;
			case KEYWORDS:
				hasPermission=JBoltUserAuthKit.hasPermission(userId,true, PermissionKey.WECHAT_AUTOREPLY_KEYWORDS);
				break;
			case DEFAULT:
				hasPermission=JBoltUserAuthKit.hasPermission(userId,true, PermissionKey.WECHAT_AUTOREPLY_DEFAULT);
				break;
			}
		}
		return hasPermission;
	}
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void save() {
		Long mpId=getLong(0);
		Integer type=getInt(1);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogFail(JBoltMsg.NOPERMISSION);
			return;
		}
		renderJson(service.save(mpId,type,getModel(WechatAutoreply.class,"wechatAutoreply")));
	}
	
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void update() {
		Long mpId=getLong(0);
		Integer type=getInt(1);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogFail(JBoltMsg.NOPERMISSION);
			return;
		}
		renderJson(service.update(mpId,type,getModel(WechatAutoreply.class,"wechatAutoreply")));
	}

}
