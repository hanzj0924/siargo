package cn.jbolt.admin.wechat.user;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoType;
import cn.jbolt.core.base.JBoltPageSize;
import cn.jbolt.core.bean.Option;
import cn.jbolt.core.bean.OptionBean;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.User;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheck;
/**
 *   微信用户信息管理 公众号和小程序
 * @ClassName:  WechatUserAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年7月20日   
 */
@CheckPermission(PermissionKey.WECHAT_USER)
@OnlySaasPlatform
public class WechatUserAdminController extends JBoltBaseController {
	@Inject
	private WechatUserService service;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Before(WechatUserMgrValidator.class)
	public void index() {
		Long mpId=getLong(0);
		set("pageData", service.paginateAdminList(mpId,getPageNumber(), getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST_20),getKeywords(),getInt("sex")));
		keepPara();
		set("mpId", mpId);
		WechatMpinfo mpinfo=getAttr("mpinfo");
		set("isNotWxa", mpinfo.getType().intValue()!=WechatMpinfoType.XCX.getValue());
		render("index.html");
	}
	@UnCheck
	public void sexOptions() {
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("未知",User.SEX_NONE));
		options.add(new OptionBean("男",User.SEX_MALE));
		options.add(new OptionBean("女",User.SEX_FEMALE));
		renderJsonData(options);
	}
	/**
	 * 执行微信用户数据同步
	 */
	@Before(WechatUserMgrValidator.class)
	public void sync() {
		Long mpId=getLong(0);
		renderJson(service.sync(mpId));
	}
	/**
	 * 切换Enable状态
	 */
	@Before(WechatUserMgrValidator.class)
	public void toggleEnable() {
		Long mpId=getLong(0);
		Long id=getLong(1);
		renderJson(service.toggleEnable(mpId,id));
	}
	/**
	 * 同步个人信息
	 */
	@Before(WechatUserMgrValidator.class)
	public void syncUser() {
		Long mpId=getLong(0);
		Long id=getLong(1);
		renderJson(service.syncOneUserInfo(mpId,id));
	}
}
