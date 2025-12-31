package cn.jbolt.admin.wechat.autoreply;

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
/**
 * JBolt中处理公众号菜单核心类
 * @ClassName:  WechatMenuEventProcesser   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年7月6日   
 */
public class WechatMenuEventProcesser {
	@Inject
	private WechatReplyContentService wechatReplyContentService;
	/**
	 * 事件处理
	 * @param appId 
	 * @param inMenuEvent
	 * @return
	 */
	public OutMsg process(String appId, InMenuEvent inMenuEvent) {
		String eventKey=inMenuEvent.getEventKey();
		if(StrKit.notBlank(eventKey)&&eventKey.startsWith("keywords_")&&eventKey.length()>9) {
			return processKeywordsAutoReplyEvent(appId,inMenuEvent);
		}
		String event=inMenuEvent.getEvent();
		OutMsg outMsg=null;
		//根据具体的event 跳转处理方法 没有的扩展按照下方自己搞定
		switch (event) {
		case InMenuEvent.EVENT_INMENU_CLICK://CLICK菜单
			outMsg=processClickEvent(appId,inMenuEvent);
			break;
		case InMenuEvent.EVENT_INMENU_VIEW://view跳转链接的菜单
			outMsg=processViewEvent(appId,inMenuEvent);
			break;
		case InMenuEvent.EVENT_INMENU_PIC_WEIXIN://微信相册发图
			outMsg=processPicWechatEvent(appId,inMenuEvent);
			break;
		}
		return outMsg;
	}
	/**
	 * 处理菜单事件中点击弹出微信相册选图
	 * @param appId
	 * @param inMenuEvent
	 * @return
	 */
	private OutMsg processPicWechatEvent(String appId, InMenuEvent inMenuEvent) {
		return null;
	}
	/**
	 * 处理菜单事件中选择触发关键词的菜单事件
	 * @param appId 
	 * @param inMenuEvent
	 * @return
	 */
	private OutMsg processKeywordsAutoReplyEvent(String appId, InMenuEvent inMenuEvent) {
		String eventKey=inMenuEvent.getEventKey();
		if(StrKit.isBlank(eventKey)||eventKey.startsWith("keywords_")==false||eventKey.length()<=9) {return null;}
		String keywords=eventKey.split("_")[1];
		if(StrKit.isBlank(keywords)) {return null;}
		//最后直接把处理这个特殊菜单触发事件 转接给系统内的关键词自动回复业务处理
		return wechatReplyContentService.getWechcatKeywordsOutMsg(appId, keywords, inMenuEvent.getFromUserName());
	}
	/**
	 * 处理Event是VIEW的菜单事件
	 * @param appId 
	 * @param inMenuEvent
	 * @return
	 */
	private OutMsg processViewEvent(String appId, InMenuEvent inMenuEvent) {
		return null;
	}
	/**
	 * 处理Event是CLICK的菜单事件
	 * @param appId 
	 * @param inMenuEvent
	 * @return
	 */
	private OutMsg processClickEvent(String appId, InMenuEvent inMenuEvent) {
		String eventKey=inMenuEvent.getEventKey();
		OutMsg outMsg=null;
		switch (eventKey) {
		case "jbolt":
			outMsg=processJboltEvent();
			break;
		}
		return outMsg;
	}
	/**
	 * 处理EventKey是jbolt的事件
	 * @return
	 */
	private OutMsg processJboltEvent() {
		
		return new OutTextMsg().setContent("<a href='http://jbolt.cn'>点击进入JBolt官方网站</a>");
	}

}
