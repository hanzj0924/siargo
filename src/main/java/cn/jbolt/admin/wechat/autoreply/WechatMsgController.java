package cn.jbolt.admin.wechat.autoreply;

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.InLocationMsg;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InNotDefinedEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

import cn.jbolt.admin.wechat.config.WechatConfigService;
import cn.jbolt.admin.wechat.user.WechatUserService;
import cn.jbolt.core.base.config.JBoltConfig;

/**
 * 微信公众号被动消息处理
 * 
 * @ClassName: WechatMsgController
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年5月14日 上午5:39:56
 */
public class WechatMsgController extends MsgControllerAdapter {
	@Inject
	private WechatConfigService wechatConfigService;
	@Inject
	private WechatReplyContentService wechatReplyContentService;
	@Inject
	private WechatMenuEventProcesser wechatMenuEventProcesser;
	@Inject
	private WechatUserService wechatUserService;

	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		String appId = ApiConfigKit.getAppId();
		if (StrKit.isBlank(appId)) {
			super.renderDefault();
			return;
		}
		String openId=inFollowEvent.getFromUserName();
		switch (inFollowEvent.getEvent()) {
		case InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE://关注
			processSubscribe(appId,openId);
			break;
		case InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE://取消关注
			processUnSubscribe(appId);
			break;
		}
	}
	/**
	 * 处理取消关注的业务
	 * @param appId
	 */
	private void processUnSubscribe(String appId) {
		super.renderDefault();
		//TODO 将微信用户的关注状态设置为false
	}
	/**
	 * 处理用户的关注业务
	 * @param appId
	 * @param openId 
	 */
	private void processSubscribe(String appId, String openId) {
		OutMsg outMsg = wechatReplyContentService.getWechcatSubscribeOutMsg(appId,openId);
		renderJBoltOutMsg(outMsg);
		
		//微信用户表里需要同步用户信息和关注状态
		wechatUserService.syncSubscribeUserInfo(appId,openId);
		
	}
	/**
	 * 响应返回outMsg
	 * @param outMsg
	 */
	private void renderJBoltOutMsg(OutMsg outMsg) {
		if (outMsg == null) {
			renderDefault();
		} else {
			if(outMsg instanceof OutTextMsg) {
				OutTextMsg outTextMsg=(OutTextMsg)outMsg;
				String content=outTextMsg.getContent();
				//如果返回的conent是特定的rendernull 就不处理任何回复了
				if(StrKit.notBlank(content)&&"rendernull".equals(content.trim())) {
					super.renderDefault();
					return;
				}
				
			}
			processOutMsg(outMsg);
			render(outMsg);
		}
	}
		
	/**
	 * 默认回复
	 */
	@Override
	protected void renderDefault() {
		String appId = ApiConfigKit.getAppId();
		if (StrKit.isBlank(appId)) {
			super.renderDefault();
		} else {
			InMsg inMsg=getInMsg();
			OutMsg outMsg = wechatReplyContentService.getWechcatDefaultOutMsg(appId,inMsg.getFromUserName());
			if (outMsg == null) {
				super.renderDefault();
			} else {
				if(outMsg instanceof OutTextMsg) {
					OutTextMsg outTextMsg=(OutTextMsg)outMsg;
					if("rendernull".equals(outTextMsg.getContent().trim())) {
						super.renderDefault();
						return;
					}
				}
				outMsg.setToUserName(inMsg.getFromUserName());
				outMsg.setFromUserName(inMsg.getToUserName());
				outMsg.setCreateTime(outMsg.now());
				render(outMsg);
			}
		}

	}
	/**
	 * 设置回复的对象和时间
	 * @param outMsg
	 */
	private void processOutMsg(OutMsg outMsg) {
		InMsg inMsg = getInMsg();
		outMsg.setToUserName(inMsg.getFromUserName());
		outMsg.setFromUserName(inMsg.getToUserName());
		outMsg.setCreateTime(outMsg.now());
	}
	/**
	 * 处理文字消息
	 * 作为关键词回复请求入口
	 */
	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
		String appId = ApiConfigKit.getAppId();
		if (StrKit.isBlank(appId)) {
			super.renderDefault();
			return;
		}
		OutMsg outMsg=wechatReplyContentService.getWechcatKeywordsOutMsg(appId,inTextMsg.getContent(),inTextMsg.getFromUserName());
		renderJBoltOutMsg(outMsg);
	}
	/**
	 * 处理收到带参二维码事件
	 */
	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
		super.processInQrCodeEvent(inQrCodeEvent);
	}
	@Override
	protected void processIsNotDefinedEvent(InNotDefinedEvent inNotDefinedEvent) {
		// TODO Auto-generated method stub
		super.processIsNotDefinedEvent(inNotDefinedEvent);
	}
	
	/**
	 * 处理公众号自定义菜单事件
	 */
	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		String appId = ApiConfigKit.getAppId();
		if (StrKit.isBlank(appId)) {
			super.renderDefault();
			return;
		}
		if(JBoltConfig.DEV_MODE) {
			System.out.println("菜单：Event:" + inMenuEvent.getEvent());
			System.out.println("菜单：EventKey:" + inMenuEvent.getEventKey());
			System.out.println("菜单：MsgType:" + inMenuEvent.getMsgType());
			System.out.println("菜单：openId:" + inMenuEvent.getFromUserName());
//			System.out.println("菜单：MenuId:" + inMenuEvent.getMenuId());
			System.out.println("菜单：公众平台原始ID:" + inMenuEvent.getToUserName());
		}
		//调用专门处理自定义菜单事件的处理器
		renderJBoltOutMsg(wechatMenuEventProcesser.process(appId,inMenuEvent));
		
	}
	
	@Override
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
		OutTextMsg outTextMsg = new OutTextMsg();
		outTextMsg.setContent("收到您发过来的位置信息:"+inLocationMsg.getLabel()+"["+inLocationMsg.getLocation_X()+":"+inLocationMsg.getLocation_Y()+"]");
		renderJBoltOutMsg(outTextMsg);
	}
	
}
