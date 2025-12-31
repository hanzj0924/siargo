package cn.jbolt.admin.appdevcenter;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt._admin.user.UserService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoType;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.Application;
import cn.jbolt.core.service.JBoltApplicationService;
/**
 * Api应用中心管理Service
 * @ClassName:  ApplicationService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月10日   
 *    
 *
 */
public class ApplicationService extends JBoltApplicationService {
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private UserService userService;
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param type
	 * @param enable 
	 * @return
	 */
	public Page<Application> paginateAdminList(Integer pageNumber, Integer pageSize, String keywords, Integer type, Boolean enable) {
		if(notOk(keywords)&&notOk(type)&&enable==null) {
			return paginate("id","desc",pageNumber, pageSize);
		}
		Okv otherParas=Okv.create();
		if(isOk(type)) {
			otherParas.set("type", type);
		}
		otherParas.setIfNotNull("enable", enable);
		
		if(notOk(keywords)) {
			return paginate(otherParas,"id","desc",pageNumber, pageSize);
		}
		return paginateByKeywords("id","desc",pageNumber, pageSize, keywords, "name,brief_info,app_id", otherParas);
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public Ret deleteApplication(Long id) {
		return deleteById(id,true);
	}
	
	@Override
	public String checkCanDelete(Application application, Kv kv) {
		if(application.getIsInner()!=null && application.getIsInner()) {
			return "内置应用，不可删除";
		}
		return checkInUse(application, kv);
	}
	@Override
	public String checkInUse(Application application, Kv kv) {
		if(isOk(application.getLinkTargetId())) {
			return "此应用已被其它数据绑定使用，不能删除";
		}
		return null;
	}
	
	@Override
	protected String afterDelete(Application application, Kv kv) {
		//TODO 删除统计信息
		//TODO 关联信息等
		processLinkTarget(application.getLinkTargetId(),application.getType());
		addDeleteSystemLog(application.getId(), JBoltUserKit.getUserId(), application.getName());
		return null;
	}
	
	/**
	 * 删除关联数据
	 * @param linkTargetId
	 * @param type
	 */
	private void processLinkTarget(Long linkTargetId, Integer type) {
		ApplicationType et=JBoltEnum.getEnumObjectByValue(ApplicationType.class, type);
		if(et == null) {return;}
		switch (et) {
		case WECHAT_XCX:
			wechatMpinfoService.removeLinkApp(linkTargetId);
			break;
		case MP_H5:
			wechatMpinfoService.removeLinkApp(linkTargetId);
			break;
		case ALIPAY_XCX:
			//TODO 待实现
			break;
		case BAIDU_XCX:
			//TODO 待实现
			break;
		case DOUYIN_XCX:
			//TODO 待实现
			break;
		case MOBILE_APP:
			//TODO 待实现
			break;
		case MOBILE_H5:
			//TODO 待实现
			break;
		case PC_DESKTOP:
			//TODO 待实现
			break;
		case PC_WEB:
			//TODO 待实现
			break;
		case QQ_XCX:
			//TODO 待实现
			break;
		case TOUTIAO_XCX:
			//TODO 待实现
			break;
		case OTHER:
			//TODO 待实现
			break;
		default:
			break;
		}
		
	}
	@Override
	public String checkCanToggle(Application app, String column, Kv kv) {
		if(app.getIsInner() != null && app.getIsInner()) {
			return "内置应用，不可操作";
		}
		return null;
	}
	/**
	 * 切换状态
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Long id) {
		Ret ret=toggleBoolean(id, "enable");
		if(ret.isOk()){
			Application application=ret.getAs("data");
			addUpdateSystemLog(id, JBoltUserKit.getUserId(),  application.getName(), "的状态为["+(application.getEnable()?"启用]":"禁用]"));
			return successWithData(application.getEnable());
		}
		return ret;
	}
	/**
	 * 切换接口是否NeedCheckSign
	 * @param id
	 * @return
	 */
	public Ret toggleNeedCheckSign( Long id) {
		Ret ret=toggleBoolean(id, "need_check_sign");
		if(ret.isOk()){
			Application application=ret.getAs("data");
			addUpdateSystemLog(id, JBoltUserKit.getUserId(),  application.getName(), "的属性[是否开启接口校验Signature]为["+(application.getNeedCheckSign()?"开启]":"关闭]"));
			return successWithData(application.getNeedCheckSign());
		}
		return FAIL;
	}
	/**
	 * 变更AppSecret
	 * @param id
	 * @return
	 */
	public Ret changeAppSecret(Long id) {
		if(notOk(id)) {return fail(JBoltMsg.PARAM_ERROR);}
		Application application=findById(id);
		if(application==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		application.setAppSecret(genAppSecret());
		boolean success=application.update();
		if(success) {
			//cache
			addUpdateSystemLog(application.getId(), JBoltUserKit.getUserId(), application.getName(), "的AppSecret");
		}
		return ret(success);
	}
	/**
	 * 保存
	 * @param app
	 * @return
	 */
	public Ret save( Application app) {
		if(app==null||isOk(app.getId())||notOk(app.getName())||notOk(app.getType())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		String name=app.getName().trim();
		if(existsName(name)) {
			return fail(JBoltMsg.DATA_SAME_NAME_EXIST);
		}
		app.setName(name);
		Date now=new Date();
		app.setCreateTime(now);
		app.setUserId(JBoltUserKit.getUserId());
		app.setUpdateTime(now);
		app.setUpdateUserId(JBoltUserKit.getUserId());
		app.setEnable(false);
		app.setAppId(genAppId());
		app.setAppSecret(genAppSecret());
		if(app.getNeedCheckSign()==null) {
			app.setNeedCheckSign(true);
		}
		boolean success=app.save();
		if(success) {
			//添加日志
			addSaveSystemLog(app.getId(), JBoltUserKit.getUserId(), app.getName());
		}
		return ret(success);
	}
	
	
	/**
	 * 更新
	 * @param app
	 * @return
	 */
	public Ret update(Application app) {
		if(app==null||notOk(app.getId())||notOk(app.getName())||notOk(app.getType())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		String name=app.getName().trim();
		if(existsName(name,app.getId())) {
			return fail(JBoltMsg.DATA_SAME_NAME_EXIST);
		}
		app.setName(name);
		app.setUpdateUserId(JBoltUserKit.getUserId());
		boolean success=app.update();
		if(success) {
			//添加日志
			addUpdateSystemLog(app.getId(), JBoltUserKit.getUserId(), app.getName());
		}
		return ret(success);
	}
	/**
	 * 绑定微信公众平台
	 * @param application
	 * @param mpId
	 * @param mpinfoType
	 * @return
	 */
	public Ret submitLinkTargetByWechatMpinfo(Application application, Long mpId, Integer mpinfoType) {
		if(notOk(mpinfoType)||application==null||notOk(mpId)) {return fail(JBoltMsg.PARAM_ERROR);}
		return null;
	}
	/**
	 * 根据微信公众平台类型 选择应用列表
	 * @param wechatMpinfoType
	 * @param keywords
	 * @param pageSize
	 * @return
	 */
	public List<Application> getAutocompleteListByWechatMpinfoType(Integer wechatMpinfoType, String keywords, int pageSize) {
		if(notOk(wechatMpinfoType)) {return Collections.emptyList();}
		int appType=0;
		if(wechatMpinfoType.intValue()==WechatMpinfoType.XCX.getValue()) {
			appType=ApplicationType.WECHAT_XCX.getValue();
		}else {
			appType=ApplicationType.MP_H5.getValue();
		}
		Page<Application> pageData=paginateByKeywords("id", "asc", 1, pageSize, keywords, "name,app_id",Okv.by("type", appType));
		return pageData.getList();
	}
	
	/**
	 * 获取关联对象
	 * @param type
	 * @param linkTargetId
	 * @return
	 */
	public JBoltAppLinkTarget getLinkTarget(Integer type, Long linkTargetId) {
		if(notOk(type)||notOk(linkTargetId)) {return null;}
		ApplicationType  typeEnum=JBoltEnum.getEnumObjectByValue(ApplicationType.class, type);
		JBoltAppLinkTarget jBoltAppLinkTarget=null;
		switch (typeEnum) {
		case WECHAT_XCX:
			jBoltAppLinkTarget=wechatMpinfoService.getAppLinkTargetInfo(linkTargetId);
			break;
		case MP_H5:
			jBoltAppLinkTarget=wechatMpinfoService.getAppLinkTargetInfo(linkTargetId);
			break;
		case ALIPAY_XCX:
			break;
		case BAIDU_XCX:
			break;
		case DOUYIN_XCX:
			break;
		case MOBILE_APP:
			break;
		case MOBILE_H5:
			break;
		case OTHER:
			break;
		case PC_DESKTOP:
			break;
		case PC_WEB:
			break;
		case QQ_XCX:
			break;
		case TOUTIAO_XCX:
			break;
		default:
			break;
		}
		return jBoltAppLinkTarget;
	}
	/**
	 * 清除关联对象 特殊情况使用 就是他关联的对象找不到了才会出
	 * @param id
	 * @return
	 */
	public Ret removeLinkTarget(Long id) {
		Application application=findById(id);
		if(application==null) {return fail(JBoltMsg.PARAM_ERROR);}
		Long linkTargetId=application.getLinkTargetId();
		if(notOk(linkTargetId)) {
			return fail("应用尚未关联任何对象");
		}
		application.setLinkTargetId(null);
		boolean success=application.update();
		if(success) {
			addUpdateSystemLog(id, JBoltUserKit.getUserId(),  application.getName(), "，解除了关联的"+application.getTypeName()+"[id:"+linkTargetId+"]");
		}
		return ret(success);
	}
	

}
