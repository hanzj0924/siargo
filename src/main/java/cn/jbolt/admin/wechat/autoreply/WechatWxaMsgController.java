package cn.jbolt.admin.wechat.autoreply;

import com.jfinal.wxaapp.api.WxaMessageApi;
import com.jfinal.wxaapp.jfinal.WxaMsgController;
import com.jfinal.wxaapp.msg.bean.WxaImageMsg;
import com.jfinal.wxaapp.msg.bean.WxaMiniProgramPageMsg;
import com.jfinal.wxaapp.msg.bean.WxaTextMsg;
import com.jfinal.wxaapp.msg.bean.WxaUserEnterSessionMsg;

/**   
 * 微信小程序客服消息处理
 * @ClassName:  WechatWxaMsgController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月14日 上午5:39:56   
 */
public class WechatWxaMsgController extends WxaMsgController {

	@Override
	protected void processTextMsg(WxaTextMsg textMsg) {
		System.out.println("文本消息");
		WxaMessageApi.sendText(textMsg.getFromUserName(), "文本消息");
	}

	@Override
	protected void processImageMsg(WxaImageMsg imageMsg) {
		System.out.println("图片消息");
		WxaMessageApi.sendText(imageMsg.getFromUserName(), "图片消息");
	}

	@Override
	protected void processUserEnterSessionMsg(WxaUserEnterSessionMsg userEnterSessionMsg) {
		System.out.println("会话消息");
		WxaMessageApi.sendText(userEnterSessionMsg.getFromUserName(), "会话消息");
	}

	@Override
	protected void processWxaMiniProgramPageMsgMsg(WxaMiniProgramPageMsg wxaMiniProgramPageMsg) {
		System.out.println("小程序卡片消息");
		WxaMessageApi.sendText(wxaMiniProgramPageMsg.getFromUserName(), "小程序卡片消息："+wxaMiniProgramPageMsg.getTitle());
	}

	

}
