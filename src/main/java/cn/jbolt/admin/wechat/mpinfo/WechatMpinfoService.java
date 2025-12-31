package cn.jbolt.admin.wechat.mpinfo;

import java.util.Date;

import cn.jbolt.core.db.sql.Sql;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.admin.appdevcenter.ApplicationService;
import cn.jbolt.admin.appdevcenter.ApplicationType;
import cn.jbolt.admin.appdevcenter.JBoltAppLinkTarget;
import cn.jbolt.admin.wechat.autoreply.WechatAutoReplyService;
import cn.jbolt.admin.wechat.config.WechatConfigService;
import cn.jbolt.admin.wechat.media.WechatMediaService;
import cn.jbolt.admin.wechat.menu.WechatMenuService;
import cn.jbolt.admin.wechat.user.WechatUserService;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.Application;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.service.JBoltWechatMpinfoService;
import cn.jbolt.core.util.JBoltRealUrlUtil;

/**
 * 微信公众平台管理
 * @ClassName:  WechatMpinfoService
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2019年5月7日 下午5:18:47
 */
public class WechatMpinfoService extends JBoltWechatMpinfoService {
	@Inject
	private WechatConfigService wechatConfigService;
	@Inject
	private WechatMenuService wechatMenuService;
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;
	@Inject
	private WechatMediaService wechatMediaService;
	@Inject
	private WechatUserService wechatUserService;
	@Inject
	private ApplicationService applicationService;

	/**
	 * 保存
	 * @param wechatMpinfo
	 * @return
	 */
	public Ret save(WechatMpinfo wechatMpinfo) {
		if(wechatMpinfo==null||isOk(wechatMpinfo.getId())||notOk(wechatMpinfo.getName())||notOk(wechatMpinfo.getType())){
			return fail(JBoltMsg.PARAM_ERROR);
		}
		String name=wechatMpinfo.getName().trim();
		if(existsName(name)){
			return fail(JBoltMsg.DATA_SAME_NAME_EXIST);
		}
		wechatMpinfo.setName(name);
		//添加的必须是false 因为启用这个是有操作的
		wechatMpinfo.setEnable(false);
		wechatMpinfo.setUserId(JBoltUserKit.getUserId());
		wechatMpinfo.setCreateTime(new Date());
		wechatMpinfo.setUpdateUserId(JBoltUserKit.getUserId());
		wechatMpinfo.setUpdateTime(new Date());
		boolean success=wechatMpinfo.save();
		if(success){
			//添加日志
			addSaveSystemLog(wechatMpinfo.getId(), JBoltUserKit.getUserId(),wechatMpinfo.getName());
			//生成对应的关注微信用户表
			boolean createRet=wechatUserService.createTable(wechatMpinfo.getId());
			if(!createRet) {
				return success("公众平台创建成功，但是表生成失败！请检查后修改此公众平台，可再次生成。");
			}
		}
		return ret(success);
	}
	/**
	 * 更新
	 * @param wechatMpinfo
	 * @return
	 */
	public Ret update(WechatMpinfo wechatMpinfo) {
		if(wechatMpinfo==null||notOk(wechatMpinfo.getId())||notOk(wechatMpinfo.getName())||notOk(wechatMpinfo.getType())){
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//不能轻易修改enable
		wechatMpinfo.remove("enable");
		String name=wechatMpinfo.getName().trim();
		if(existsName(name, wechatMpinfo.getId())){
			return fail(JBoltMsg.DATA_SAME_NAME_EXIST);
		}
		wechatMpinfo.setName(name);
		wechatMpinfo.setUpdateUserId(JBoltUserKit.getUserId());
		wechatMpinfo.setUpdateTime(new Date());
		boolean success=wechatMpinfo.update();
		if(success){
			//添加日志
			addUpdateSystemLog(wechatMpinfo.getId(), JBoltUserKit.getUserId(),wechatMpinfo.getName());
			//生成对应的关注微信用户表
			boolean createRet=wechatUserService.createTable(wechatMpinfo.getId());
			if(!createRet) {
				return success("公众平台更新成功，但是表生成失败！请检查后修改此公众平台，可再次生成。");
			}
		}
		return ret(success);
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public Ret delete(Long id) {
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			//添加日志
			WechatMpinfo wechatMpinfo=ret.getAs("data");
			addDeleteSystemLog(id, JBoltUserKit.getUserId(),wechatMpinfo.getName());
		}
		return ret;
	}
	@Override
	public String checkCanDelete(WechatMpinfo model, Kv kv) {
		// TODO Auto-generated method stub
		return super.checkCanDelete(model, kv);
	}

	@Override
	public String checkInUse(WechatMpinfo wechatMpinfo,Kv kv) {
		boolean wechatConfig=wechatConfigService.checkWechatMpinfoInUse(wechatMpinfo.getId());
		if(wechatConfig){return "此公众平台下已经存在相关配置信息";}
		boolean wechatMenu=wechatMenuService.checkWechatMpinfoInUse(wechatMpinfo.getId());
		if(wechatMenu){return "此公众平台下已经存在菜单配置";}
		boolean wechatAutoReply=wechatAutoReplyService.checkWechatMpinfoInUse(wechatMpinfo.getId());
		if(wechatAutoReply){return "此公众平台下已经存在自动回复规则配置";}
		return null;
	}
	/**
	 * 切换启动/禁用状态
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Long id) {
		Ret ret=toggleBoolean(id, "enable");
		if(ret.isOk()){
			//添加日志
			WechatMpinfo wechatMpinfo=ret.getAs("data");
			if(wechatMpinfo.getEnable()){
				Ret cRet=wechatConfigService.configOneMpinfo(wechatMpinfo);
				if(cRet.isFail()){
					wechatMpinfo.setEnable(false);
					wechatMpinfo.update();
					return cRet;
				}
			}else{
				wechatConfigService.removeOneEnableApiConfig(wechatMpinfo.getId());
			}
			//添加日志
			addUpdateSystemLog(id, JBoltUserKit.getUserId(),wechatMpinfo.getName(),"的启用状态:"+wechatMpinfo.getEnable());
			return successWithData(wechatMpinfo.getEnable());
		}
		return ret;
	}

	/**
	 * 切换认证状态
	 * @param id
	 * @return
	 */
	public Ret toggleAuthenticated(Long id) {
		Ret ret=toggleBoolean(id, "is_authenticated");
		if(ret.isOk()){
			//添加日志
			WechatMpinfo wechatMpinfo=ret.getAs("data");
			addUpdateSystemLog(id, JBoltUserKit.getUserId(),wechatMpinfo.getName(),"的认证状态:"+wechatMpinfo.getIsAuthenticated());
			return successWithData(wechatMpinfo.getIsAuthenticated());
		}
		return ret;
	}
	/**
	 * 后台管理分页读取
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param enable
	 * @param type
	 * @param subjectType
	 * @param isAuthenticated
	 * @return
	 */
	public Page<WechatMpinfo> paginateAdminList(int pageNumber, int pageSize, String keywords, Boolean enable,
			Integer type, Integer subjectType, Boolean isAuthenticated) {
		Sql sql = selectSql().page(pageNumber, pageSize);
		sql.likeMulti(keywords,"name","wechat_id");
		sql.enableEq(enable);
		sql.eq("type", type);
		sql.eq("subject_type", subjectType);
		sql.eqBooleanToChar("is_authenticated",isAuthenticated);
		sql.orderById(true);
		return paginate(sql);
	}

	/**
	 * 清空配置
	 * @param mpId
	 * @return
	 */
	public Ret clearAllConfigs(Long mpId) {
		if(JBoltConfig.DEMO_MODE) {return fail(JBoltMsg.DEMO_MODE_CAN_NOT_DELETE);}
		//公众平台配置删除
		wechatConfigService.deleteByMpId(mpId);
		//删除菜单
		wechatMenuService.deleteByMpId(mpId);
		//删除自动回复设置
		wechatAutoReplyService.deleteByMpId(mpId);
		//素材库
		wechatMediaService.deleteByMpId(mpId);
		return SUCCESS;
	}
	/**
	 * 确定关联APP 操作
	 * @param mpId
	 * @param appId
	 * @return
	 */
	public Ret submitLinkApp(Long mpId, Long appId) {
		if(notOk(mpId)||notOk(appId)) {return fail(JBoltMsg.PARAM_ERROR);}
		WechatMpinfo wechatMpinfo=findById(mpId);
		if(wechatMpinfo==null) {return fail("公众平台"+JBoltMsg.DATA_NOT_EXIST);}
		Application application=applicationService.findById(appId);
		if(application==null) {return fail("关联应用"+JBoltMsg.DATA_NOT_EXIST);}
		if(isOk(application.getLinkTargetId())&&application.getLinkTargetId().longValue()!=mpId.longValue()) {
			return fail("应用["+application.getName()+"]已经被其它 "+application.getTypeName()+" 关联");
		}

		if(wechatMpinfo.getType().intValue()==WechatMpinfoType.XCX.getValue()) {
			if(application.getType().intValue()!=ApplicationType.WECHAT_XCX.getValue()) {
				return fail("应用["+application.getName()+"]类型不是微信小程序,请换个微信小程序应用绑定");
			}
		}else if(application.getType().intValue()!=ApplicationType.MP_H5.getValue()) {
				return fail("应用["+application.getName()+"]类型不是微信公众平台H5,请换个微信公众平台H5应用绑定");
			}

		if(isOk(application.getLinkTargetId())&&application.getLinkTargetId().longValue()==mpId.longValue()) {
			return SUCCESS;
		}
		wechatMpinfo.setLinkAppId(appId);
		application.setLinkTargetId(mpId);
		boolean success_wx=wechatMpinfo.update();
		boolean success_app=application.update();
		if(success_wx&&success_app) {
			addUpdateSystemLog(mpId, JBoltUserKit.getUserId(),  wechatMpinfo.getName(), "的关联应用为:["+application.getName()+"("+application.getTypeName()+")]");
		}
		return ret(success_wx&&success_app);
	}
	/**
	 * 解除关联的application
	 * @param mpId
	 * @return
	 */
	public Ret removeLinkApp(Long mpId) {
		if(notOk(mpId)) {return fail(JBoltMsg.PARAM_ERROR);}
		WechatMpinfo wechatMpinfo=findById(mpId);
		if(wechatMpinfo==null) {return fail("公众平台"+JBoltMsg.DATA_NOT_EXIST);}
		Long linkAppId=wechatMpinfo.getLinkAppId();
		if(notOk(linkAppId)) {
			return fail("公众平台尚未关联任何应用");
		}
		wechatMpinfo.setLinkAppId(null);
		boolean success=wechatMpinfo.update();
		if(success) {
			applicationService.removeLinkTarget(linkAppId);
			addUpdateSystemLog(mpId, JBoltUserKit.getUserId(),  wechatMpinfo.getName(), " 与关联应用解除绑定");
		}
		return ret(success);
	}
	/**
	 * 获取APP关联对象信息
	 * @param id
	 * @return
	 */
	public JBoltAppLinkTarget getAppLinkTargetInfo(Long id) {
		WechatMpinfo m=findById(id);
		if(m==null) {return null;}
		JBoltAppLinkTarget jBoltAppLinkTarget=new JBoltAppLinkTarget();
		jBoltAppLinkTarget.setName(m.getName());
		jBoltAppLinkTarget.setLogo(JBoltRealUrlUtil.getImage(m.getLogo()));
		jBoltAppLinkTarget.setId(id);
		jBoltAppLinkTarget.setTypeName(m.getTypeName());
		jBoltAppLinkTarget.setSubjectTypeName(m.getSubjectTypeName());
		return jBoltAppLinkTarget;

	}

}
