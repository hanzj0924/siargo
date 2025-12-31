package cn.jbolt.admin.wechat.autoreply;

import java.util.ArrayList;
import java.util.List;

import cn.jbolt.common.enums.WechatReplyContentType;
import cn.jbolt.core.enumutil.JBoltEnum;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.common.model.WechatReplyContent;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.bean.Option;
import cn.jbolt.core.bean.OptionBean;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.permission.UnCheck;

/**
 * 微信公众平台自动回复内容设置管理
 * 
 * @ClassName: WechatAutoReplyAdminController
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年6月20日
 */
public class WechatReplyContentAdminController extends JBoltBaseController {
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;
	@Inject
	private WechatReplyContentService service;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void index() {
		Long autoReplyId=getLong(0);
		Ret ret=service.checkPermission(autoReplyId);
		if(ret.isFail()) {renderDialogFailRet(ret);return;}
		set("datas", service.getListByAutoReplyId(autoReplyId));
		set("autoReplyId", autoReplyId);
		render("index.html");
	}
	


	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void add() {
		Long autoReplyId=getLong(0);
		Ret ret=service.checkPermission(autoReplyId);
		if(ret.isFail()) {renderDialogFail(ret.getStr("msg"));return;}
		
		WechatAutoreply wechatAutoreply=ret.getAs("data");
		Long mpId=wechatAutoreply.getMpId();
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {renderDialogFail("关联微信公众平台不存在");return;}
		
		set("mpId", mpId);
		set("autoReplyId", autoReplyId);
		render("add.html");
	}
	
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void edit() {
		Long autoReplyId=getLong(0);
		Long id=getLong(1);
		//根据TYPE 判断是否有权限
		Ret ret=service.checkPermission(autoReplyId);
		if(ret.isFail()) {renderDialogFail(ret.getStr("msg"));return;}
		WechatAutoreply wechatAutoreply=ret.getAs("data");
		
		Long mpId=wechatAutoreply.getMpId();
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {renderDialogFail("关联微信公众平台不存在");return;}
		
		set("mpId", mpId);
		set("autoReplyId", autoReplyId);
		WechatReplyContent wechatReplyContent=service.findById(id);
		if(wechatReplyContent==null) {
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		if(wechatReplyContent.getMpId().longValue()!=wechatAutoreply.getMpId().longValue()) {renderDialogFail("参数异常:公众平台mpId");return;}
		if(wechatReplyContent.getAutoReplyId().longValue()!=autoReplyId.longValue()) {renderDialogFail("参数异常:所属规则 autoReplyId");return;}
		set("wechatReplyContent", wechatReplyContent);
		render("edit.html");
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void up(){
		renderJson(service.doUp(getLong(0),getLong(1)));
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void down(){
		renderJson(service.doDown(getLong(0),getLong(1)));
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void initRank(){
		renderJson(service.doInitRank(getLong(0)));
	}
	
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void delete() {
		renderJson(service.delete(getLong(0),getLong(1)));
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void toggleEnable() {
		renderJson(service.toggleEnable(getLong(0),getLong(1)));
	}
	@UnCheck
	public void types() {
		renderJsonData(JBoltEnum.getEnumOptionList(WechatReplyContentType.class));
	}
	
	/**
	 * 上传公众平台相关图片
	 */
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void uploadImage(){
		Long autoReplyId=getLong(0);
		Ret ret=service.checkPermission(autoReplyId);
		if(ret.isFail()) {renderDialogFail(ret.getStr("msg"));return;}
		//上传到今天的文件夹下
		String todayFolder=JBoltUploadFolder.todayFolder();
		String uploadPath=JBoltUploadFolder.WECHAT_AUTOREPLY_REPLYCONTENT+"/"+todayFolder;
		UploadFile file=getFile("img",uploadPath);
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJsonData(JFinal.me().getConstants().getBaseUploadPath()+"/"+uploadPath+"/"+file.getFileName());
	}
	
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void save() {
		renderJson(service.save(getLong(0),getModel(WechatReplyContent.class,"wechatReplyContent")));
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void update() {
		renderJson(service.update(getLong(0),getModel(WechatReplyContent.class,"wechatReplyContent")));
	}

}
