package cn.jbolt.admin.wechat.media;

import com.jfinal.plugin.cron4j.ITask;

/** 每隔一分钟执行一次图片下载任务 */
public class WechatMediaDownloaTask implements ITask {

    @Override
    public void run() {
// WechatMediaService service=Aop.get(WechatMediaService.class);
//		service.downloadWechatMedia();
    }

    @Override
    public void stop() {

    }

}
