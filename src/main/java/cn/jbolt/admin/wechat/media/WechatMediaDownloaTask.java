package cn.jbolt.admin.wechat.media;

import com.jfinal.plugin.cron4j.ITask;
/**
 * 每隔一分钟执行一次图片下载任务
 * @ClassName:  WechatMediaDownloaTask   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年6月26日   
 */
public class WechatMediaDownloaTask implements ITask {

	@Override
	public void run() {
//		WechatMediaService service=Aop.get(WechatMediaService.class);
//		service.downloadWechatMedia();
	}

	@Override
	public void stop() {
		
	}

}
