package cn.jbolt.wechat.api;

import cn.jbolt._admin.cache.JBoltWechatUserCache;
import cn.jbolt.core.api.*;
import cn.jbolt.core.kit.JBoltHttpSchemeKit;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.SnsApi;

import cn.jbolt.admin.wechat.user.WechatUserService;
import cn.jbolt.core.wechat.JBoltWechatApi;
import cn.jbolt.common.model.WechatUser;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.cache.JBoltWechatConfigCache;
/**
 * 内置微信公众平台API
 * @ClassName:  JBoltWechatApiController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年5月22日   
 */
public class JBoltWechatApiController extends JBoltApiBaseController {
	@Inject
	private WechatUserService wechatUserService;
	/**
	 * 跳转授权URL
	 * @param scope
	 */
	@UnCheckJBoltApi
	public void authorize(@Para(value ="scope", defaultValue = "snsapi_userinfo") String scope) {
		String redirect_uri = JBoltHttpSchemeKit.getFull() + JBoltConfig.DOMAIN+"/api/wechat/authCallback?jboltappid="+JBoltApiKit.getAppId();
		String wechatAppId  = JBoltApiKit.getWechatAppId() ;
		String url          = JBoltWechatApi.use(wechatAppId).call(()->SnsAccessTokenApi.getAuthorizeURL(wechatAppId, redirect_uri, "snsapi_base".equalsIgnoreCase(scope)));
		if(notOk(url)) {
			renderH5PageFail("生成授权URL失败");
			return;
		}
		redirect(url);
	}
	
	/**
	 * 授权成功回调
	 */
	@UnCheckJBoltApi
	public void authCallback(@Para("code") String code,@Para("state") String state) {
		if(notOk(code)) {
			renderH5PageFail("授权回调失败，没有获取到code");
			return;
		}
		String wechatAppId  = JBoltApiKit.getWechatAppId();
		String secret       = JBoltWechatConfigCache.me.getAppSecret(JBoltApiKit.getApplicationLinkTargetId());
		SnsAccessToken accessToken  = JBoltWechatApi.use(wechatAppId).call(()->SnsAccessTokenApi.getSnsAccessToken(wechatAppId, secret, code));
		if(!accessToken.isAvailable()) {
			renderH5PageFail("授权回调失败，"+accessToken.getErrorMsg());
			return;
		}
		//拿到openId
		String openId = accessToken.getOpenid();
		Long mpId   = JBoltApiKit.getWechatMpId();
		//判断是否存在用户信息
		WechatUser wechatUser = JBoltWechatUserCache.me.getApiWechatUserByMpOpenId(mpId, openId);
		//执行获取用户信息
		ApiResult apiResult=JBoltWechatApi.use(wechatAppId).call(()->SnsApi.getUserInfo(accessToken.getAccessToken(), accessToken.getOpenid()));
		if(!apiResult.isSucceed()) {
			renderH5PageFail("授权回调获取用户信息失败，"+apiResult.getErrorMsg());
			return;
		}
		
		Ret ret = null;
		if(wechatUser == null) {
			ret = wechatUserService.addUnSubscribeWechatUserInfo(mpId,openId,apiResult);
		}else {
			ret = wechatUserService.updateSubscribeWechatUserInfo(mpId,wechatUser.getId(),apiResult);
		}
		
		if(ret.isFail()) {
			renderH5PageFail("授权回调创建用户信息失败，"+ret.getStr("msg"));
			return;
		}
		wechatUser = ret.getAs("data");

		//用户存在的话 设置到threadLocal中拦截器得 用
		JBoltApiUser apiUser = JBoltApiKit.processBindUser(new JBoltApiUserBean(JBoltApiKit.getApplicationId(),wechatUser.getId(), wechatUser.getNickname()),wechatUser.getBindUser());
		//创建JWT
		String[] jwts = JBoltApiJwtManger.me().createJBoltApiTokens(JBoltApiKit.getApplication(), apiUser, 7200000L,604800000L);
		if(notOk(jwts)){
			renderH5PageFail("授权回调创建用户令牌失败");
			return;
		}
		//将jwt放在响应里
		set(JBoltApiJwtManger.JBOLT_API_TOKEN_KEY,jwts[0]);
		//将refreshjwt放在响应里
		set(JBoltApiJwtManger.JBOLT_API_REFRESH_TOKEN_KEY,jwts[1]);
		render("/_view/_test/wechat/authsuccess.html");
	}

}
