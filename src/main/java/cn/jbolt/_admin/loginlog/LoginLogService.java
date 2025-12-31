package cn.jbolt._admin.loginlog;

import java.util.Date;

import cn.jbolt.core.bean.JBoltDateRange;
import cn.jbolt.core.db.sql.Sql;
import com.jfinal.kit.Okv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.core.model.LoginLog;
import cn.jbolt.core.service.JBoltLoginLogService;
import cn.jbolt.core.util.JBoltDateUtil;
/**
 * 登录日志记录
 * @ClassName:  LoginLogService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年5月12日   
 */
public class LoginLogService extends JBoltLoginLogService{
	/**
	 * 登录日志管理查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Page<LoginLog> paginateAdminList(Integer pageNumber, Integer pageSize, String keywords,Date startTime,Date endTime) {
		Sql sql = selectSql().page(pageNumber,pageSize).bwDate("create_time",startTime,endTime);
		sql.like("username",keywords);
		sql.orderById(true);
		return paginate(sql);
	}

}
