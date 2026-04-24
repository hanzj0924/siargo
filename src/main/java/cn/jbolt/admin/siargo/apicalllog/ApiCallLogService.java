package cn.jbolt.admin.siargo.apicalllog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.ApiCallLog;
/**
 * API调用记录 Service
 * @ClassName: ApiCallLogService   
 * @author: hanzj
 * @date: 2026-04-21 16:46  
 */
public class ApiCallLogService extends JBoltBaseService<ApiCallLog> {
	private static final Log LOG = Log.getLog(ApiCallLogService.class);
	private final ApiCallLog dao=new ApiCallLog().dao();

	private volatile Kv statsCache;
	private volatile long statsCacheTime;
	private volatile List<Record> dailyStatsCache;
	private volatile long dailyStatsCacheTime;
	private final ReentrantLock statsLock = new ReentrantLock();
	private final ReentrantLock dailyStatsLock = new ReentrantLock();
	private static final long CACHE_TTL = 30 * 60 * 1000L;

	@Override
	protected ApiCallLog dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<ApiCallLog> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("create_time", "desc", pageNumber, pageSize, keywords, "api_path");
	}

	/**
	 * 后台管理分页查询（增强版，支持响应状态、日期范围筛选）
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param responseStatus
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize,
			String keywords, String responseStatus, String startDate, String endDate) {
		StringBuilder where = new StringBuilder("FROM siargo_api_call_log");
		List<String> conditions = new ArrayList<>();
		List<Object> params = new ArrayList<>();

		if (StrKit.notBlank(keywords)) {
			conditions.add("api_path LIKE ?");
			params.add("%" + keywords.trim() + "%");
		}
		if (StrKit.notBlank(responseStatus)) {
			conditions.add("response_status = ?");
			params.add(responseStatus);
		}
		if (StrKit.notBlank(startDate)) {
			conditions.add("create_time >= ?");
			params.add(startDate + " 00:00:00");
		}
		if (StrKit.notBlank(endDate)) {
			conditions.add("create_time <= ?");
			params.add(endDate + " 23:59:59");
		}

		if (!conditions.isEmpty()) {
			where.append(" WHERE ").append(String.join(" AND ", conditions));
		}
		where.append(" ORDER BY create_time DESC");

		return Db.paginate(pageNumber, pageSize, "SELECT *", where.toString(), params.toArray());
	}
	
	/**
	 * 保存
	 * @param apiCallLog
	 * @return
	 */
	public Ret save(ApiCallLog apiCallLog) {
		if(apiCallLog==null || isOk(apiCallLog.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(apiCallLog.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=apiCallLog.save();
		if(success) {
			//添加日志
			addSaveSystemLog(apiCallLog.getId(), JBoltUserKit.getUserId(), apiCallLog.getApiPath());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param apiCallLog
	 * @return
	 */
	public Ret update(ApiCallLog apiCallLog) {
		if(apiCallLog==null || notOk(apiCallLog.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		ApiCallLog dbApiCallLog=findById(apiCallLog.getId());
		if(dbApiCallLog==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(apiCallLog.getName(), apiCallLog.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=apiCallLog.update();
		if(success) {
			//添加日志
			addUpdateSystemLog(apiCallLog.getId(), JBoltUserKit.getUserId(), apiCallLog.getApiPath());
		}
		return ret(success);
	}
	
	/**
	 * 删除 指定多个ID
	 * @param ids
	 * @return
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids,true);
	}
	
	/**
	 * 删除数据后执行的回调
	 * @param apiCallLog 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(ApiCallLog apiCallLog, Kv kv) {
		addDeleteSystemLog(apiCallLog.getId(), JBoltUserKit.getUserId(), apiCallLog.getApiPath());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param apiCallLog 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(ApiCallLog apiCallLog, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(apiCallLog, kv);
	}
	
	/**
	 * 记录API调用日志（系统自动产生，不走 SystemLog 审计）
	 * @return
	 */
	public boolean logApiCall(String apiPath, String apiMethod, String orderId,
			String orderIds, String requestIp, String userAgent,
			String responseStatus, Integer responseCode, String responseMsg,
			Long costTime, Long jboltAppId) {
		try {
			boolean success = new ApiCallLog()
				.setApiPath(apiPath).setApiMethod(apiMethod)
				.setOrderId(orderId).setOrderIds(orderIds)
				.setRequestIp(requestIp).setUserAgent(userAgent)
				.setResponseStatus(responseStatus).setResponseCode(responseCode)
				.setResponseMsg(responseMsg).setCostTime(costTime)
				.setJboltAppId(jboltAppId).save();
			if (success) {
				clearStatsCache();
			}
			return success;
		} catch (Exception e) {
			LOG.error("记录API调用日志失败", e);
			return false;
		}
	}

	/**
	 * 获取API调用统计（30分钟缓存）
	 * @return
	 */
	public Kv getCallStats() {
		// 无锁快速读
		if (statsCache != null && (System.currentTimeMillis() - statsCacheTime) < CACHE_TTL) {
			return statsCache;
		}
		statsLock.lock();
		try {
			// 双重检查
			if (statsCache != null && (System.currentTimeMillis() - statsCacheTime) < CACHE_TTL) {
				return statsCache;
			}
			Record record = Db.findFirst(
				"SELECT COUNT(*) AS total, " +
				"SUM(CASE WHEN response_status='ok' THEN 1 ELSE 0 END) AS success_count, " +
				"SUM(CASE WHEN response_status='fail' THEN 1 ELSE 0 END) AS fail_count, " +
				"IFNULL(AVG(cost_time), 0) AS avg_cost_time " +
				"FROM siargo_api_call_log"
			);
			Kv kv = Kv.create();
			kv.set("total", record.getLong("total"));
			kv.set("successCount", record.getLong("success_count"));
			kv.set("failCount", record.getLong("fail_count"));
			kv.set("avgCostTime", record.getLong("avg_cost_time"));
			long total = record.getLong("total");
			kv.set("successRate", total > 0 ? String.format("%.1f", record.getLong("success_count") * 100.0 / total) : "0.0");
			statsCache = kv;
			statsCacheTime = System.currentTimeMillis();
			return kv;
		} finally {
			statsLock.unlock();
		}
	}

	/**
	 * 清除统计缓存
	 */
	public void clearStatsCache() {
		statsCache = null;
		dailyStatsCache = null;
	}

	/**
	 * 获取最近30天每日调用量统计（30分钟缓存），供图表展示
	 * @return
	 */
	public List<Record> getDailyStats() {
		// 无锁快速读
		if (dailyStatsCache != null && (System.currentTimeMillis() - dailyStatsCacheTime) < CACHE_TTL) {
			return dailyStatsCache;
		}
		dailyStatsLock.lock();
		try {
			// 双重检查
			if (dailyStatsCache != null && (System.currentTimeMillis() - dailyStatsCacheTime) < CACHE_TTL) {
				return dailyStatsCache;
			}
			List<Record> list = Db.find(
				"SELECT DATE(create_time) AS day, " +
				"SUM(CASE WHEN response_status='ok' THEN 1 ELSE 0 END) AS success_count, " +
				"SUM(CASE WHEN response_status='fail' THEN 1 ELSE 0 END) AS fail_count " +
				"FROM siargo_api_call_log " +
				"WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
				"GROUP BY DATE(create_time) ORDER BY day"
			);
			dailyStatsCache = Collections.unmodifiableList(list);
			dailyStatsCacheTime = System.currentTimeMillis();
			return dailyStatsCache;
		} finally {
			dailyStatsLock.unlock();
		}
	}

	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.ApiCallLog.getValue();
	}
	
}