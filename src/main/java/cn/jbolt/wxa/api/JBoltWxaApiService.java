package cn.jbolt.wxa.api;

import cn.jbolt._admin.cache.JBoltWechatUserCache;
import cn.jbolt.admin.appdevcenter.ApplicationService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.admin.wechat.user.WechatUserService;
import cn.jbolt.common.model.WechatUser;
import cn.jbolt.core.api.*;
import cn.jbolt.core.model.Application;
import cn.jbolt.core.para.JBoltPara;
import cn.jbolt.core.service.base.JBoltCommonService;
import cn.jbolt.core.util.JBoltArrayUtil;
import cn.jbolt.core.wechat.JBoltWechatApi;
import cn.jbolt.core.wechat.JBoltWechatApi.Type;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.wxaapp.api.WxaUserApi;

import java.util.ArrayList;
import java.util.List;
/**
 * 微信小程序APIService 业务处理
 * @ClassName:  WxaApiService
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2020年3月17日
 */
public class JBoltWxaApiService extends JBoltCommonService {
	@Inject
	private WechatUserService wechatUserService;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private ApplicationService applicationService;
	/**
	 * 微信小程序用户进入小程序 调用登录
	 * @param code
	 * @return
	 */
	@Before(Tx.class)
	public JBoltApiRet login(String code) {
		//获取当前访问Application
		Application application=JBoltApiKit.getApplication();
		if(notOk(code)){
			return JBoltApiRet.WX_LOGIN_CODE_NULL(application);
		}
		//得到微信小程序配置的APPID
		String wechatAppId=JBoltApiKit.getWechatAppId();
		Long mpId=JBoltApiKit.getWechatMpId();
		//执行API调用获取sessionKey和OpenId unionid
		ApiResult apiResult = JBoltWechatApi.use(wechatAppId,Type.MP_WXA).call(()->WxaUserApi.getSessionKey(code));
		//调用成功
		if(apiResult.isSucceed()){
			String openId=apiResult.getStr("openid");
			String unionId=apiResult.getStr("unionid");
			String sessionKey=apiResult.getStr("session_key");
			//从cache里拿到对应微信用户
			WechatUser wechatUser= JBoltWechatUserCache.me.getApiWechatUserByMpOpenId(mpId, openId);
			if(wechatUser==null){
				//如果没有，说明是第一次登录 保存用户信息
				wechatUser=wechatUserService.saveWxaUser(application.getLinkTargetId(),openId,unionId,sessionKey);
				if(wechatUser==null){
					return JBoltApiRet.WECHAT_XCX_USER_CREATE_FAIL(application);
				}
			}else {
				Ret ret=wechatUserService.updateWxaUserLoginInfo(application.getLinkTargetId(),wechatUser,openId,unionId,sessionKey);
				if(ret.isFail()) {
					return JBoltApiRet.API_FAIL(application,"登录失败,"+ret.getStr("msg"));
				}
			}
			//用户存在的话 设置到threadLocal中拦截器得 用
			JBoltApiKit.setApplyJwtUser(processBindUser(new JBoltApiUserBean(application.getId(),wechatUser.getId(), wechatUser.getNickname(),sessionKey),wechatUser.getBindUser()));
			return JBoltApiRet.API_SUCCESS;
		}
		return JBoltApiRet.API_FAIL(application,"登录失败,"+apiResult.getErrorMsg());

	}
	/**
	 * 处理绑定信息
	 * @param apiUser
	 * @param bindUser
	 * @return
	 */
	public JBoltApiUserBean processBindUser(JBoltApiUserBean apiUser,String bindUser) {
		if(StrKit.isBlank(bindUser)) {return apiUser;}
		apiUser.setBindUsers(convertToBindUsers(bindUser));
		return apiUser;
	}
	/**
	 * 转换成Bean
	 * @param bindUser
	 * @return
	 */
	private List<JBoltApiBindUserBean> convertToBindUsers(String bindUser) {
		String[] barray=JBoltArrayUtil.from(bindUser, ",");
		if(barray==null||barray.length==0) {return null;}
		List<JBoltApiBindUserBean> beans=new ArrayList<JBoltApiBindUserBean>();
		JBoltApiBindUserBean bean;
		for(String bu:barray) {
			bean=covertToBindUser(bu);
			if(bean!=null) {
				beans.add(bean);
			}
		}
		return beans;
	}
	private JBoltApiBindUserBean covertToBindUser(String bindUser) {
		if(StrKit.isBlank(bindUser)|| !bindUser.contains("_")) {return null;}
		String[] arr=bindUser.split("_");
		if(arr.length != 2 || StrKit.isBlank(arr[0]) || StrKit.isBlank(arr[1])) {return null;}
		return new JBoltApiBindUserBean(Integer.parseInt(arr[0].trim()), arr[1].trim());
	}
	/**
	 * 解密用户信息 更新到数据库
	 * @param param
	 * @return
	 */
	public JBoltApiRet decryptUserInfo(JBoltPara param) {
		String errMsg=param.getString("errMsg");
		//获取当前访问Application
		Application application=JBoltApiKit.getApplication();
		if(StrKit.isBlank(errMsg)||errMsg.equals("getUserProfile:ok")==false) {
			return JBoltApiRet.WECHAT_XCX_GETUSERINFO_FAIL(application);
		}
		//得到微信小程序配置的APPID
		String wechatAppId=JBoltApiKit.getWechatAppId();
		Long mpId=JBoltApiKit.getWechatMpId();
		Object apiUserId=JBoltApiKit.getJwtParseRet().getUserId();
		String sessionKey=JBoltApiKit.getJwtParseRet().getUserSessionKey();
		String rawData=param.getString("rawData");
		String signature=param.getString("signature");
		//检查数据完整性
		boolean success=JBoltWechatApi.use(wechatAppId,Type.MP_WXA).call(()->WxaUserApi.checkUserInfo(sessionKey, rawData, signature));
		if(!success) {
			return JBoltApiRet.WECHAT_XCX_CHECK_USER_INFO_FAIL(application);
		}
		//准备解密
		String encryptedData=param.getString("encryptedData");
		String iv=param.getString("iv");
		ApiResult userInfoResult=JBoltWechatApi.use(wechatAppId,Type.MP_WXA).call(()->WxaUserApi.getUserInfo(sessionKey, encryptedData, iv));
		if(!userInfoResult.isSucceed()) {
			return JBoltApiRet.WECHAT_XCX_DECRYPTUSERINFO_FAIL(application);
		}
		//更新到数据库里
		Ret ret=wechatUserService.updateWxaUserInfoByDecrypt(mpId,wechatAppId,apiUserId,userInfoResult);
		if(ret.isFail()) {
			return JBoltApiRet.WECHAT_XCX_UPDATE_USERINFO_FAIL(application,ret.getStr("msg"));
		}
		return JBoltApiRet.API_SUCCESS;
	}


	/**
	 * 解密手机信息 更新到数据库
	 * @param param
	 * @return
	 */
	public JBoltApiRet decryptPhoneNumber(JBoltPara param) {
		String errMsg=param.getString("errMsg");
		//获取当前访问Application
		Application application=JBoltApiKit.getApplication();
		if(StrKit.isBlank(errMsg)||errMsg.equalsIgnoreCase("getphonenumber:ok")==false) {
			return JBoltApiRet.WECHAT_XCX_GETPHONENUMBER_FAIL(application);
		}
		//得到微信小程序配置的APPID
		String wechatAppId=JBoltApiKit.getWechatAppId();
		Long mpId=JBoltApiKit.getWechatMpId();
		Object apiUserId=JBoltApiKit.getJwtParseRet().getUserId();
		String sessionKey=JBoltApiKit.getJwtParseRet().getUserSessionKey();
		//准备解密
		String encryptedData=param.getString("encryptedData");
		String iv=param.getString("iv");
		ApiResult userInfoResult=JBoltWechatApi.use(wechatAppId,Type.MP_WXA).call(()->WxaUserApi.getUserInfo(sessionKey, encryptedData, iv));
		if(!userInfoResult.isSucceed()) {
			return JBoltApiRet.WECHAT_XCX_DECRYPTUSERINFO_FAIL(application);
		}
		//更新到数据库里
		Ret ret=wechatUserService.updateWxaPhoneNumberByDecrypt(mpId,wechatAppId,apiUserId,userInfoResult);
		if(ret.isFail()) {
			return JBoltApiRet.WECHAT_XCX_UPDATE_USER_PHONENUMBER_FAIL(application,ret.getStr("msg"));
		}
		return JBoltApiRet.successWithData(ret.getAs("data"));
	}

	/**
	 * 绑定其他用户表的用户信息
	 * @param type
	 * @param username
	 * @param password
	 * @return
	 */
	public JBoltApiRet bindOtherUser(Integer type, String username, String password) {
		//获取当前访问Application
		Application application=JBoltApiKit.getApplication();
		if(notOk(type)||notOk(username)||notOk(password)) {
			return JBoltApiRet.WECHAT_XCX_BINDUSER_PARAM_ERROR(application);
		}
		//得到微信小程序配置的APPID
		Long mpId=JBoltApiKit.getWechatMpId();
		Long apiUserId=JBoltApiKit.getJwtParseRet().getUserIdToLong();
		WechatUser wechatUser=wechatUserService.findByIdToWechatUserFromCache(mpId,apiUserId);
		JBoltApiRet ret = wechatUserService.bindOtherUer(application,mpId,wechatUser,type,username,password);
		if(ret.isFail()) {
			return ret;
		}
		//用户存在的话 设置到threadLocal中拦截器得 用
		JBoltApiKit.setApplyJwtUser(processBindUser(new JBoltApiUserBean(application.getId(),wechatUser.getId(), wechatUser.getNickname()),wechatUser.getBindUser()));
		return JBoltApiRet.API_SUCCESS;
	}
	/**
	 * 获取自身微信用户信息
	 * @return
	 */
	public JBoltApiRet getMyWechatUserInfo() {
		//获取当前访问Application
		Application application=JBoltApiKit.getApplication();
		Long wechatUserId=JBoltApiKit.getApiUserIdToLong();
		//得到微信小程序配置的APPID
//		String wechatAppId=JBoltApiKit.getWechatAppId();
		Long mpId=JBoltApiKit.getWechatMpId();
		WechatUser wechatUser=wechatUserService.findByIdToWechatUserFromCache(mpId, wechatUserId);
		if(wechatUser==null) {
			return JBoltApiRet.API_FAIL(application, "无法获取当前用户信息");
		}
		return JBoltApiRet.API_SUCCESS_WITH_DATA(wechatUser);
	}

	/**
	 * 解除绑定用户
	 * @param type
	 * @param userId
	 * @return
	 */
	public JBoltApiRet unbindOtherUser(Integer type, Object userId) {
		//获取当前访问Application
		Application application=JBoltApiKit.getApplication();
		if(notOk(type)||notOk(userId)) {
			return JBoltApiRet.WECHAT_XCX_BINDUSER_PARAM_ERROR(application);
		}
		//得到微信小程序配置的 mpid
//		String wechatAppId=JBoltApiKit.getWechatAppId();
		Long mpId=JBoltApiKit.getWechatMpId();
		Long apiUserId=JBoltApiKit.getApiUserIdToLong();
		WechatUser wechatUser=wechatUserService.findByIdToWechatUserFromCache(mpId,apiUserId);
		JBoltApiRet ret = wechatUserService.unbindOtherUser(application,mpId,wechatUser,type,userId);
		if(ret.isFail()) {
			return ret;
		}
		//用户存在的话 设置到threadLocal中拦截器得 用
		JBoltApiKit.setApplyJwtUser(processBindUser(new JBoltApiUserBean(application.getId(),wechatUser.getId(), wechatUser.getNickname()),wechatUser.getBindUser()));
		return JBoltApiRet.API_SUCCESS;
	}
}
