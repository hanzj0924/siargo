package cn.jbolt.common.config;

import java.util.Date;

import com.jfinal.log.Log;

import cn.hutool.core.date.DateUtil;

/**
 * 一切版本过渡之间的自动处理升级业务
 * 在这里实现，文件由JBolt官方操作，他人请勿修改
 * @ClassName:  JBoltAutoUpgrade   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月7日   
 */
public class JBoltAutoUpgrade {
	public static final JBoltAutoUpgrade me=new JBoltAutoUpgrade();
	private Log LOG=Log.getLog(JBoltAutoUpgrade.class);
	private JBoltAutoUpgrade() {
	}
	/**
	 * 执行
	 */
	public void exe() {
		//LOG.info("JBolt自动升级：开始执行");
		//LOG.info("JBolt自动升级：执行完成");
	}
	public boolean canUpgrade(String upgradeDate) {
		Date date_upgradeDate=DateUtil.parse(upgradeDate);
		Date now=new Date();
		return now.after(date_upgradeDate);
	}
	/**
	 * 升级
	 */
	public void up20200502() {
		if(canUpgrade("2020-05-01 23:59:59")) {
			LOG.info("JBolt自动升级：正在执行-up20200502");
			    
			LOG.info("JBolt自动升级：执行完毕-up20200502");
		}
	}
}
