package cn.jbolt._admin.onlineuser;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.jfinal.plugin.cron4j.ITask;
/**
 * 每隔30秒执行一次删除任务 和 转离线任务
 * @ClassName:  JBoltOnlineUserClearTask   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年3月23日   
 *    
 */
public class JBoltOnlineUserClearTask implements ITask {
	private static final Log LOG = Log.getLog("JBoltCron4jLog");

	@Override
	public void run() {
		OnlineUserService service=Aop.get(OnlineUserService.class);
		service.deleteOfflineAndExpirationUser();
	}

	@Override
	public void stop() {
	}

}
