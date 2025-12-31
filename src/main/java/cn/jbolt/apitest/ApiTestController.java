package cn.jbolt.apitest;

import java.util.Map;

import cn.jbolt._admin.cache.JBoltWechatUserCache;
import cn.jbolt.core.api.*;
import cn.jbolt.core.api.httpmethod.JBoltHttpGet;
import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Okv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.PaymentException;
import com.jfinal.wxaapp.WxaConfigKit;
import com.jfinal.wxaapp.api.WxaOrder;
import com.jfinal.wxaapp.api.WxaPayApi;

import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.common.model.WechatUser;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.core.cache.JBoltWechatConfigCache;
import cn.jbolt.core.crossorigin.CrossOrigin;
import cn.jbolt.core.kit.JBoltControllerKit;
import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.core.service.JBoltFileService;
import cn.jbolt.core.util.JBoltIpUtil;
/**
 * 测试API
 * @ClassName:  ApiTestController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月12日   
 */
@CrossOrigin
public class ApiTestController extends JBoltApiBaseController {
	@Inject
	private JBoltFileService jBoltFileService;
	@Clear
	public void paycallback() {
		String xmlResult=getRawData();
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
		Long mpId = getLong(0);
		String paternerKey = JBoltWechatConfigCache.me.getPaternerKey(mpId);
		if(notOk(paternerKey)) {
			 JBoltControllerKit.renderWxaPayResultFail(this,"微信支付配置错误:paternerKey");
			 return;
		}
		if(!PaymentKit.verifyNotify(result, paternerKey)){
			 JBoltControllerKit.renderWxaPayResultFail(this,"签名校验失败");
			 return;
		}

        String return_code = result.get("return_code");
        if (notOk(return_code)) {
        	JBoltControllerKit.renderWxaPayResultFail(this,"FAIL");
        }else{
        	String result_code = result.get("result_code");
        	Ret ret=null;
 	        if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){
 	        	String out_trade_no = result.get("out_trade_no");
 	        	String transaction_id = result.get("transaction_id");
 	        	ret=doProcessWeixinPaySuccessCallback(out_trade_no,transaction_id);
 	        	if(ret.isFail()){
 		        	JBoltControllerKit.renderWxaPayResultFail(this, ret.getStr("msg"));
 	        	}else{
 	               JBoltControllerKit.renderWxaPayResultSuccess(this, ret.getStr("msg"));
 	        	}
 	        }else{
 	        	System.out.println("-----------------FAIL----------------");
 	        	System.out.println(JSON.toJSONString(result));
                JBoltControllerKit.renderWxaPayResultFail(this,"FAIL");
 	        }
        }
	}
	/**
	 * 处理微信支付成功回调业务 自己实现
	 * @param out_trade_no
	 * @param transaction_id
	 * @return
	 */
	private Ret doProcessWeixinPaySuccessCallback(String out_trade_no, String transaction_id) {
		return Ret.ok();
	}
	
	
	/**
	 * 微信小程序统一下单
	 */
	public void unifiedorder() {
		String mchId = JBoltWechatConfigCache.me.getMchId(JBoltApiKit.getWechatMpId());
		if(notOk(mchId)) {
			renderJBoltApiFail("微信支付配置错误:mchId");
			return;
		}
		String paternerKey = JBoltWechatConfigCache.me.getPaternerKey(JBoltApiKit.getWechatMpId());
		if(notOk(paternerKey)) {
			renderJBoltApiFail("微信支付配置错误:paternerKey");
			return;
		}
		//从数据库里拿到保存到本地的订单ID
		Long orderId = 1L;
		Map<String, String> result=null;
		try {
			result = doUnifiedorderRewardRoleOrder(mchId,paternerKey);
			if(result==null || result.isEmpty()) {
				renderJBoltApiFail("下单失败,请返回重试");
				return;
			}
			renderJBoltApiSuccess("下单成功，请支付", Okv.by("orderId", orderId).set("payInfo",result));
		} catch (PaymentException e) {
			e.printStackTrace();
			renderJBoltApiFail(e.getReturnMsg());
		}
	}
	/**
	 * 统一下单 
	 * 这里是demo演示 没有放在service里 自己业务 自己放在service里
	 * 相关参数 自己调整
	 * @param mchId
	 * @param paternerKey
	 * @return
	 * @throws PaymentException
	 */
	private Map<String, String> doUnifiedorderRewardRoleOrder(String mchId, String paternerKey) throws PaymentException {
		Long wxaUserId = JBoltApiKit.getApiUserIdToLong();
		Long mpId = JBoltApiKit.getWechatMpId();
		WechatUser wxuser= JBoltWechatUserCache.me.getApiWechatUserByApiUserId(mpId, wxaUserId);
		String openId=wxuser.getOpenId();
		String appId = JBoltWechatConfigCache.me.getAppId(mpId);
		WxaOrder wxaOrder=new WxaOrder(appId, mchId, paternerKey);
		wxaOrder.setOpenId(openId);
		wxaOrder.setOutTradeNo(JBoltSnowflakeKit.me.nextIdStr());
		wxaOrder.setBody("加入JFinal开发者计划");
		wxaOrder.setNotifyUrl(PropKit.get("domain")+"/api/test/paycallback/"+mpId);
		double amout = 0.1;
		wxaOrder.setTotalFee(Math.round(amout*100)+"");
		wxaOrder.setSpbillCreateIp(JBoltIpUtil.getIp(getRequest()));
		try {
			WxaConfigKit.setThreadLocalAppId(appId);
			return WxaPayApi.unifiedOrder(wxaOrder);
		} finally {
			WxaConfigKit.removeThreadLocalAppId();
		}
	}
	/**
	 * API测试
	 */
	public void go() {
		//进入这个方法 是需要拦截器校验request Header中的JWT信息的
		renderJBoltApiSuccess("卧槽！API 测试通过");
	}
	
	/**
	 * API测试
	 */
	public void floor() {
		renderJBoltApiSuccessWithData(Okv.by("id", 1).set("name","山东小木"));
	}
	/**
	 * API测试
	 */
	@OpenAPI
	public void uploadImgUseOpenApi() {
		//上传到今天的文件夹下
		String todayFolder=JBoltUploadFolder.todayFolder();
		String uploadPath=JBoltUploadFolder.DEMO_APITEST_IMAGE+"/"+todayFolder;
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file.getContentType())){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJBoltApiRet(jBoltFileService.saveImageFile(file,uploadPath));
	}
	
	/**
	 * API测试
	 */
	public void uploadImage() {
		//上传到今天的文件夹下
		String todayFolder=JBoltUploadFolder.todayFolder();
		String uploadPath=JBoltUploadFolder.DEMO_APITEST_IMAGE+"/"+todayFolder;
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file.getContentType())){
			renderJBoltApiFail("请上传图片类型文件");
			return;
		}
		renderJBoltApiRet(jBoltFileService.saveImageFile(file,uploadPath));
	}
	
	/**
	 * API首次访问 签发JWT
	 */
	@JBoltApplyJWT
	public void login() {
		//用户存在的话 设置到threadLocal中拦截器得 这里需要开发者自行设置登录后的用户信息进去
		JBoltApiKit.setApplyJwtUser(new JBoltApiUserBean("1536967936978325504","1536968835087863808", "zhangsan"));
		//模拟用户信息 这里可能是微信授权登录
		renderJBoltApiSuccess("登录成功",Okv.by("appId", getAppId()));
	}

	/**
	 * 刷新jwt
	 */
	@JBoltApplyJWT
	@JBoltHttpGet
	public void refreshJwt(){
		renderJBoltApiRet(JBoltApiJwtManger.me().refreshJwt(this));
	}

	/**
	 * API首次访问 签发JWT
	 */
	@JBoltApplyJWT
	public void applyJwt() {
		//用户存在的话 设置到threadLocal中拦截器得 这里需要开发者自行设置登录后的用户信息进去
		JBoltApiKit.setApplyJwtUser(new JBoltApiUserBean("1536967936978325504","1536968835087863808", "zhangsan"));
		//模拟用户信息 这里可能是微信授权登录
		renderJBoltApiSuccess("登录成功",Okv.by("appId", getAppId()));
	}
}
