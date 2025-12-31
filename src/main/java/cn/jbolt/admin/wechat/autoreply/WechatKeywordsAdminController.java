package cn.jbolt.admin.wechat.autoreply;

import java.util.ArrayList;
import java.util.List;

import cn.jbolt.common.enums.WechatKeywordsType;
import cn.jbolt.core.enumutil.JBoltEnum;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.model.WechatKeywords;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.bean.Option;
import cn.jbolt.core.bean.OptionBean;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
/**
 * 微信公众平台自动回复规则中的触发关键词管理
 * @ClassName:  WechatKeywordsAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年7月1日   
 */
@CheckPermission(PermissionKey.WECHAT_AUTOREPLY_KEYWORDS)
@OnlySaasPlatform
public class WechatKeywordsAdminController extends JBoltBaseController {
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;
	@Inject
	private WechatKeywordsService service;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	
	@Before(WechatKeywordsMgrValidator.class)
	public void index() {
		Long autoReplyId=getLong(0);
		set("datas", service.getListByAutoReplyId(autoReplyId));
		set("autoReplyId", autoReplyId);
		render("index.html");
	}
	
	
	@Before(WechatKeywordsMgrValidator.class)
	public void add() {
		Long autoReplyId=getLong(0);
		Ret checkRet=service.checkCanOpt(autoReplyId);
		if(checkRet.isFail()) {
			renderDialogFailRet(checkRet);
			return;
		}
		Kv checkResultKv=checkRet.getAs("data");
		Long mpId=checkResultKv.getLong("mpId");
		set("autoReplyId",autoReplyId);
		set("mpId", mpId);
		render("add.html");
	}
	
	
	@Before(WechatKeywordsMgrValidator.class)
	public void edit() {
		Long autoReplyId=getLong(0);
		Long id=getLong(1);
		Ret checkRet=service.checkCanOpt(autoReplyId);
		if(checkRet.isFail()) {
			renderDialogFailRet(checkRet);
			return;
		}
		WechatKeywords wechatKeywords=service.findById(id);
		if(wechatKeywords==null) {
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		Kv checkResultKv=checkRet.getAs("data");;
		Long mpId=checkResultKv.getLong("mpId");
		if(wechatKeywords.getMpId().longValue()!=mpId.longValue()) {renderDialogFail("参数异常:公众平台mpId");return;}
		if(wechatKeywords.getAutoReplyId().longValue()!=autoReplyId.longValue()) {renderDialogFail("参数异常:所属规则 autoReplyId");return;}
		set("wechatKeywords", wechatKeywords);
		set("autoReplyId",autoReplyId);
		set("mpId",mpId);
		render("edit.html");
	}
	
	public void types() {
		renderJsonData(JBoltEnum.getEnumOptionList(WechatKeywordsType.class));
	}
	
	@Before(WechatKeywordsMgrValidator.class)
	public void save() {
		renderJson(service.save(getLong(0),getModel(WechatKeywords.class,"wechatKeywords")));
	}
	@Before(WechatKeywordsMgrValidator.class)
	public void update() {
		renderJson(service.update(getLong(0),getModel(WechatKeywords.class,"wechatKeywords")));
	}
	@Before(WechatKeywordsMgrValidator.class)
	public void delete() {
		renderJson(service.delete(getLong(0),getLong(1)));
	}
}
