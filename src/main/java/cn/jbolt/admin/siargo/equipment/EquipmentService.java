package cn.jbolt.admin.siargo.equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.aop.Inject;
import cn.jbolt.admin.siargo.equipment.certificate.EquipmentCertificateService;
import cn.jbolt.admin.siargo.equipment.comparison.EquipmentComparisonService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.siargo.model.EquipmentComparison;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Equipment;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import com.alibaba.fastjson.JSON;
import cn.jbolt.common.util.DateUtil;
/**
 * 设备管理 Service
 * @ClassName: EquipmentService   
 * @author: hanzj
 * @date: 2026-04-18 10:47  
 */
public class EquipmentService extends JBoltBaseService<Equipment> {
	private final Equipment dao=new Equipment().dao();

	@Inject
	private EquipmentCertificateService equipmentCertificateService;

	@Inject
	private EquipmentComparisonService equipmentComparisonService;


	// ========== 概览统计缓存（30分钟有效期） ==========
	private static final long OVERVIEW_CACHE_TTL = 30 * 60 * 1000L; // 30分钟
	private volatile Kv cachedOverviewCounts;
	private volatile long cacheTimestamp;
	private final ReentrantLock cacheLock = new ReentrantLock();
	@Override
	protected Equipment dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param category 分类筛选
	 * @param filter 过滤条件：expired/audit/repairing/sealed
	 * @param status 状态筛选
	 * @param inspectionMethod 检定方式筛选
	 * @param inspectionCycle 检校周期筛选
	 * @return
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, String keywords, String category, String filter, String status, String inspectionMethod, String inspectionCycle) {
		String select = "SELECT e.*, "
			+ "(SELECT c.image_url FROM siargo_equipment_certificate c WHERE c.equipment_id = e.id AND c.status = 1 LIMIT 1) AS cert_image, "
			+ "(SELECT c.certificate_date FROM siargo_equipment_certificate c WHERE c.equipment_id = e.id AND c.status = 1 LIMIT 1) AS cert_date, "
			+ "lc.creator_time, lc.auditor_time, lc.audit_status, lc.result AS inspection_status, "
			+ "cu.name AS creator_name, au.name AS auditor_name";
		StringBuilder from = new StringBuilder("FROM siargo_equipment e ");
		from.append("LEFT JOIN siargo_equipment_comparison lc ON lc.equipment_id = e.id AND NOT EXISTS (SELECT 1 FROM siargo_equipment_comparison newer WHERE newer.equipment_id = lc.equipment_id AND (newer.comparison_date > lc.comparison_date OR (newer.comparison_date = lc.comparison_date AND newer.creator_time > lc.creator_time) OR (newer.comparison_date = lc.comparison_date AND newer.creator_time = lc.creator_time AND newer.id > lc.id))) ");
		from.append("LEFT JOIN jb_user cu ON cu.id = lc.creator_id ");
		from.append("LEFT JOIN jb_user au ON au.id = lc.auditor_id ");
		
		List<Object> params = new ArrayList<>();
		List<String> conditions = new ArrayList<>();
		
		// 分类筛选
		if (isOk(category)) {
			conditions.add("e.category = ?");
			params.add(category);
		}
		
		// 状态筛选
		if (isOk(status)) {
			conditions.add("e.status = ?");
			params.add(status);
		}
		
		// 检定方式筛选
		if (isOk(inspectionMethod)) {
			conditions.add("e.inspection_method = ?");
			params.add(inspectionMethod);
		}
		
		// 检校周期筛选（按单位：年/月）
		if (isOk(inspectionCycle)) {
			conditions.add("e.inspection_cycle_unit = ?");
			params.add(inspectionCycle);
		}
		
		// 过滤条件
		if ("expired".equals(filter)) {
			conditions.add("e.next_inspection_date IS NOT NULL AND e.next_inspection_date <= DATE_ADD(CURDATE(), INTERVAL 15 DAY)");
		} else if ("cert_expired".equals(filter)) {
			conditions.add("EXISTS (SELECT 1 FROM siargo_equipment_certificate ec WHERE ec.equipment_id = e.id AND ec.status = 1 AND ec.certificate_date + INTERVAL 9 MONTH <= NOW())");
		} else if ("audit".equals(filter)) {
			conditions.add("EXISTS (SELECT 1 FROM siargo_equipment_comparison ec WHERE ec.equipment_id = e.id AND NOT EXISTS (SELECT 1 FROM siargo_equipment_comparison newer WHERE newer.equipment_id = ec.equipment_id AND (newer.comparison_date > ec.comparison_date OR (newer.comparison_date = ec.comparison_date AND newer.creator_time > ec.creator_time) OR (newer.comparison_date = ec.comparison_date AND newer.creator_time = ec.creator_time AND newer.id > ec.id))) AND ec.audit_status = 1)");
		} else if ("repairing".equals(filter)) {
			conditions.add("e.status = 2");
		} else if ("sealed".equals(filter)) {
			conditions.add("e.status IN (3, 4)");
		} else if ("abnormal".equals(filter)) {
			conditions.add("e.status = 5");
		}
		
		// 关键词模糊匹配
		if (isOk(keywords)) {
			conditions.add("(e.equipment_no LIKE ? OR e.name LIKE ? OR e.model LIKE ? OR e.factory_no LIKE ?)");
			String kw = "%" + keywords + "%";
			params.add(kw);
			params.add(kw);
			params.add(kw);
			params.add(kw);
		}
		
		if (!conditions.isEmpty()) {
			from.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
		}
		
		String orderBy = "ORDER BY e.equipment_no ASC";
		
		return Db.paginate(pageNumber, pageSize, select, 
			from.toString() + orderBy, 
			params.toArray());
	}
	
	/**
	 * 获取概览统计数量（带30分钟缓存）
	 * @return Kv 包含 total, cat_1~cat_5, repairing, sealed, expired, audit
	 */
	public Kv getOverviewCounts() {
		// 先检查缓存是否有效（无锁快速路径）
		if (cachedOverviewCounts != null && (System.currentTimeMillis() - cacheTimestamp) < OVERVIEW_CACHE_TTL) {
			return cachedOverviewCounts;
		}
		// 缓存失效，加锁查询并刷新缓存
		cacheLock.lock();
		try {
			// 双重检查：防止多线程同时穿透
			if (cachedOverviewCounts != null && (System.currentTimeMillis() - cacheTimestamp) < OVERVIEW_CACHE_TTL) {
				return cachedOverviewCounts;
			}
			Kv counts = loadOverviewCountsFromDb();
			cachedOverviewCounts = counts;
			cacheTimestamp = System.currentTimeMillis();
			return counts;
		} finally {
			cacheLock.unlock();
		}
	}

	/**
	 * 主动清除概览统计缓存（数据变更时调用）
	 */
	public void clearOverviewCountsCache() {
		cachedOverviewCounts = null;
		cacheTimestamp = 0;
	}

	/**
	 * 从数据库加载概览统计数量
	 */
	private Kv loadOverviewCountsFromDb() {
		Kv counts = Kv.create();
		// 初始化分类默认值
		for (int i = 1; i <= 5; i++) {
			counts.set("cat_" + i, 0L);
		}
		// 合并为单条SQL：一次性统计所有指标，避免多次查询导致的性能损耗
		String sql = "SELECT"
				+ "  COUNT(*) AS total"
				+ ", SUM(CASE WHEN category = '1' THEN 1 ELSE 0 END) AS cat_1"
				+ ", SUM(CASE WHEN category = '2' THEN 1 ELSE 0 END) AS cat_2"
				+ ", SUM(CASE WHEN category = '3' THEN 1 ELSE 0 END) AS cat_3"
				+ ", SUM(CASE WHEN category = '4' THEN 1 ELSE 0 END) AS cat_4"
				+ ", SUM(CASE WHEN category = '5' THEN 1 ELSE 0 END) AS cat_5"
				+ ", SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) AS repairing"
				+ ", SUM(CASE WHEN status IN (3, 4) THEN 1 ELSE 0 END) AS sealed"
				+ ", SUM(CASE WHEN status = 5 THEN 1 ELSE 0 END) AS abnormal"
				+ ", SUM(CASE WHEN next_inspection_date IS NOT NULL AND next_inspection_date <= DATE_ADD(CURDATE(), INTERVAL 15 DAY) THEN 1 ELSE 0 END) AS expired"
				+ ", (SELECT COUNT(*) FROM siargo_equipment se INNER JOIN siargo_equipment_certificate ec ON ec.equipment_id = se.id AND ec.status = 1 AND ec.certificate_date + INTERVAL 9 MONTH <= NOW()) AS cert_expired"
				+ ", SUM(CASE WHEN (SELECT ec.audit_status FROM siargo_equipment_comparison ec WHERE ec.equipment_id = siargo_equipment.id ORDER BY ec.comparison_date DESC, ec.creator_time DESC LIMIT 1) = 1 THEN 1 ELSE 0 END) AS audit"
				+ " FROM siargo_equipment";
		Record row = Db.findFirst(sql);
		if (row != null) {
			counts.set("total",     toLong(row.getLong("total")));
			counts.set("cat_1",     toLong(row.getLong("cat_1")));
			counts.set("cat_2",     toLong(row.getLong("cat_2")));
			counts.set("cat_3",     toLong(row.getLong("cat_3")));
			counts.set("cat_4",     toLong(row.getLong("cat_4")));
			counts.set("cat_5",     toLong(row.getLong("cat_5")));
			counts.set("repairing", toLong(row.getLong("repairing")));
			counts.set("sealed",    toLong(row.getLong("sealed")));
			counts.set("abnormal",  toLong(row.getLong("abnormal")));
			counts.set("expired",   toLong(row.getLong("expired")));
			counts.set("cert_expired", toLong(row.getLong("cert_expired")));
			counts.set("audit",     toLong(row.getLong("audit")));
		} else {
			counts.set("total", 0L);
			counts.set("repairing", 0L);
			counts.set("sealed", 0L);
			counts.set("abnormal", 0L);
			counts.set("expired", 0L);
			counts.set("cert_expired", 0L);
			counts.set("audit", 0L);
		}
		return counts;
	}

	/** 将Long值转为0L（null安全） */
	private long toLong(Long val) {
		return val != null ? val : 0L;
	}
	
	/**
	 * 批量定期对比
	 * @param ids 逗号分隔的设备ID
	 * @param lastDate 最近检校日期（作为comparison_date）
	 * @param nextDate 下次检校日期
	 * @param status 对比结果：1-合格 2-不合格
	 * @return
	 */
	public Ret batchInspection(String ids, java.util.Date lastDate, java.util.Date nextDate, Integer status) {
		if (notOk(ids)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Long userId = JBoltUserKit.getUserId();
		// 解析ids为Long列表
		String[] idStrArr = ids.split(",");
		List<Long> idList = new ArrayList<>();
		for (String idStr : idStrArr) {
			String trimmed = idStr.trim();
			if (trimmed.isEmpty()) continue;
			try {
				idList.add(Long.valueOf(trimmed));
			} catch (NumberFormatException e) {
				return fail(JBoltMsg.PARAM_ERROR);
			}
		}
		if (idList.isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < idList.size(); i++) {
			if (i > 0) placeholders.append(",");
			placeholders.append("?");
		}
		// 更新设备检校日期
		Object[] params = new Object[idList.size() + 2];
		params[0] = lastDate;
		params[1] = nextDate;
		for (int i = 0; i < idList.size(); i++) {
			params[2 + i] = idList.get(i);
		}
		String sql = "UPDATE siargo_equipment SET last_inspection_date = ?, next_inspection_date = ? WHERE id IN (" + placeholders + ")";
		int count = Db.update(sql, params);
		if (count > 0) {
			// 为每台设备创建 EquipmentComparison 定期对比记录
			for (Long equipmentId : idList) {
				// 对比结果合格时，验证设备状态必须为正常
				if (status != null && status == 1) {
					Integer equipmentStatus = Db.queryInt("SELECT status FROM siargo_equipment WHERE id = ?", equipmentId);
					if (equipmentStatus == null || equipmentStatus != 1) {
						String equipmentNo = Db.queryStr("SELECT equipment_no FROM siargo_equipment WHERE id = ?", equipmentId);
						return fail("【" + (equipmentNo != null ? equipmentNo : "") + "】状态异常，请先维修！");
					}
				}
				EquipmentComparison comparison = new EquipmentComparison();
				comparison.setEquipmentId(equipmentId);
				comparison.setComparisonType(1); // 定期对比
				comparison.setComparisonDate(lastDate); // comparison_date=最近检校日期
				comparison.setResult(status != null ? status : 1); // 对比结果
				// description、auditor_id、auditor_time 显式为null
				comparison.setDescription(null);
				comparison.setAuditorId(null);
				comparison.set("auditor_time", null);
				comparison.setCreatorId(userId); // 关联当前用户
				comparison.set("creator_time", DateUtil.getDateString(DateUtil.YMDHMS)); // 今天(年-月-日 时:分:秒)
				comparison.setAuditStatus(1); // audit_status=1
				comparison.save();
			}
			// 对比结果联动设备状态：result=1(合格)→status=1(正常)，result=2(不合格)→status=2(维修中)
			if (status != null && (status == 1 || status == 2)) {
				Object[] syncParams = new Object[idList.size() + 1];
				syncParams[0] = status; // result值直接等于设备status值
				for (int i = 0; i < idList.size(); i++) {
					syncParams[1 + i] = idList.get(i);
				}
				Db.update("UPDATE siargo_equipment SET status = ? WHERE id IN (" + placeholders + ")", syncParams);
			}
			clearOverviewCountsCache();
		}
		return ret(count > 0);
	}
	
	/**
	 * 批量审核（查找设备时间线最新一条待审核记录进行审核）
	 * @param ids 逗号分隔的设备ID
	 * @return
	 */
	public Ret batchAudit(String ids) {
		if (notOk(ids)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 解析ids为Long列表（设备ID），防止SQL注入
		String[] idStrArr = ids.split(",");
		List<Long> idList = new ArrayList<>();
		for (String idStr : idStrArr) {
			String trimmed = idStr.trim();
			if (trimmed.isEmpty()) continue;
			try {
				idList.add(Long.valueOf(trimmed));
			} catch (NumberFormatException e) {
				return fail(JBoltMsg.PARAM_ERROR);
			}
		}
		if (idList.isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 查询每台设备最新的待审核对比记录并调用审核
		String sql = "SELECT id, comparison_date as event_date, audit_status "
			+ "FROM siargo_equipment_comparison WHERE equipment_id = ? AND audit_status = 1 "
			+ "ORDER BY comparison_date DESC LIMIT 1";
		for (Long equipmentId : idList) {
			Record record = Db.findFirst(sql, equipmentId);
			if (record == null) {
				return fail("没有待审核的记录");
			}
			Long recordId = record.getLong("id");
			Ret ret = equipmentComparisonService.audit(recordId);
			if (ret.isFail()) {
				return ret;
			}
		}
		clearOverviewCountsCache();
		return ret(true);
	}
	
	/**
	 * 批量更改状态
	 * @param ids 逗号分隔的ID
	 * @param status 设备使用状态（1-正常 2-维修中 3-已封存 4-报废 5-异常）
	 * @return
	 */
	public Ret batchStatus(String ids, Integer status) {
		if (notOk(ids)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		if (status == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Long userId = JBoltUserKit.getUserId();
		// 解析ids为Long列表，防止SQL注入
		String[] idStrArr = ids.split(",");
		List<Long> idList = new ArrayList<>();
		for (String idStr : idStrArr) {
			String trimmed = idStr.trim();
			if (trimmed.isEmpty()) continue;
			try {
				idList.add(Long.valueOf(trimmed));
			} catch (NumberFormatException e) {
				return fail(JBoltMsg.PARAM_ERROR);
			}
		}
		if (idList.isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < idList.size(); i++) {
			if (i > 0) placeholders.append(",");
			placeholders.append("?");
		}
		// 设备状态验证：检查最新对比记录的result
		if (status == 1 || status == 2) {
			for (Long equipmentId : idList) {
				Integer latestResult = Db.queryInt(
					"SELECT result FROM siargo_equipment_comparison WHERE equipment_id = ? ORDER BY id DESC LIMIT 1",
					equipmentId);
				if (status == 1 && latestResult != null && latestResult == 2) {
					String equipmentNo = Db.queryStr("SELECT equipment_no FROM siargo_equipment WHERE id = ?", equipmentId);
					return fail("【" + (equipmentNo != null ? equipmentNo : "") + "】对比结果不合格，请先维修！");
				}
				if (status == 2 && latestResult != null && latestResult == 1) {
					String equipmentNo = Db.queryStr("SELECT equipment_no FROM siargo_equipment WHERE id = ?", equipmentId);
					return fail("【" + (equipmentNo != null ? equipmentNo : "") + "】对比结果合格，无法维修！");
				}
			}
		}
		// 执行 UPDATE
		Object[] params = new Object[idList.size() + 1];
		params[0] = status;
		for (int i = 0; i < idList.size(); i++) {
			params[1 + i] = idList.get(i);
		}
		String sql = "UPDATE siargo_equipment SET status = ? WHERE id IN (" + placeholders + ")";
		int count = Db.update(sql, params);
		if (count > 0) {
			clearOverviewCountsCache();
			// 当新状态为1（正常）或2（维修中）时，同步更新最新检校批次结果（status 1→result 2=合格, status 2→result 3=不合格）
			if (status == 1 || status == 2) {
				int comparisonResult = status + 1; // 设备状态→对比结果映射
				Object[] idParams = new Object[idList.size() + 2];
				idParams[0] = comparisonResult;
				for (int i = 0; i < idList.size(); i++) {
					idParams[1 + i] = idList.get(i);
				}
				idParams[idList.size() + 1] = comparisonResult;
				Db.update("UPDATE siargo_equipment_comparison SET result = ? WHERE id IN " +
					"(SELECT max_id FROM (SELECT MAX(id) AS max_id FROM siargo_equipment_comparison WHERE equipment_id IN (" +
					placeholders + ") GROUP BY equipment_id) tmp) AND result != ?",
					idParams);
			}

		}
		return ret(count > 0);
	}

	/**
	 * 设备对比记录分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param equipmentId
	 * @return
	 */
	public Page<Record> paginateRecordDatas(int pageNumber, int pageSize, Long equipmentId) {
		String select = "SELECT ec.*, u.name AS creator_name, au.name AS auditor_name, " +
				"(SELECT c.image_url FROM siargo_equipment_certificate c WHERE c.comparison_id = ec.id ORDER BY c.id LIMIT 1) AS cert_image_url";
		String from = "FROM siargo_equipment_comparison ec " +
				"LEFT JOIN jb_user u ON u.id = ec.creator_id " +
				"LEFT JOIN jb_user au ON au.id = ec.auditor_id " +
				"WHERE ec.equipment_id = ? ORDER BY ec.comparison_date DESC";
		return Db.paginate(pageNumber, pageSize, select, from, equipmentId);
	}
	
	/**
	 * 保存
	 * @param equipment
	 * @param certificateImageUrls 证书图片路径（逗号分隔），可为空
	 * @return
	 */
	public Ret save(Equipment equipment, String certificateImageUrls) {
		if(equipment==null || isOk(equipment.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(equipment.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=equipment.save();
		if(success) {
			clearOverviewCountsCache();
			//添加日志
			//addSaveSystemLog(equipment.getId(), JBoltUserKit.getUserId(), equipment.getName());
			//关联证书图片入库（非必填，失败不阻断设备保存）
			if(isOk(certificateImageUrls)) {
				try {
					equipmentCertificateService.saveCertificateImages(equipment.getId(), certificateImageUrls);
				} catch (Exception e) {
					System.err.println("设备保存成功但证书图片保存失败，equipmentId=" + equipment.getId() + "，原因：" + e.getMessage());
				}
			}
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param equipment
	 * @return
	 */
	public Ret update(Equipment equipment) {
		if(equipment==null || notOk(equipment.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		Equipment dbEquipment=findById(equipment.getId());
		if(dbEquipment==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(equipment.getName(), equipment.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=equipment.update();
		if(success) {
			clearOverviewCountsCache();
			//添加日志
			//addUpdateSystemLog(equipment.getId(), JBoltUserKit.getUserId(), equipment.getName());
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
	 * @param equipment 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(Equipment equipment, Kv kv) {
		clearOverviewCountsCache();
		addDeleteSystemLog(equipment.getId(), JBoltUserKit.getUserId(),equipment.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param equipment 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(Equipment equipment, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(equipment, kv);
	}
	
	/**
	 * 单列更新后清除概览统计缓存
	 */
	@Override
	public Ret updateOneColumn(Equipment equipment) {
		Ret result = super.updateOneColumn(equipment);
		if(result.isOk()) {
			clearOverviewCountsCache();
		}
		return result;
	}
	
	/**
	 * 合并查询对比+维修记录，按日期倒序分页
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param equipmentId 设备ID
	 * @return 分页数据
	 */
	public Page<Record> paginateTimelineDatas(int pageNumber, int pageSize, Long equipmentId) {
		String select = "SELECT CAST(c.id AS CHAR) as id, c.comparison_date as event_date, c.description, "
			+ "c.result as status_value, c.creator_id, c.creator_time, c.audit_status, "
			+ "c.comparison_type, cu.name as creator_name, "
			+ "GROUP_CONCAT(DISTINCT cert.image_url) as cert_images, MAX(cert.certificate_date) as cert_date, c.auditor_time, MAX(au.name) as auditor_name ";
		String sqlExceptSelect = "FROM siargo_equipment_comparison c "
			+ "LEFT JOIN jb_user cu ON cu.id = c.creator_id "
			+ "LEFT JOIN jb_user au ON au.id = c.auditor_id "
			+ "LEFT JOIN siargo_equipment_certificate cert ON cert.comparison_id = c.id "
			+ "WHERE c.equipment_id = ? "
			+ "GROUP BY c.id "
			+ "ORDER BY c.comparison_date DESC, c.creator_time DESC";
		Page<Record> page = Db.paginate(pageNumber, pageSize, select, sqlExceptSelect, equipmentId);
		List<Record> list = page.getList();
		if (list != null && !list.isEmpty()) {
			// 收集不合格（status_value == 2）的对比 ID
			List<Object> comparisonIds = new ArrayList<>();
			for (Record rec : list) {
				if (rec.getInt("status_value") != null && rec.getInt("status_value") == 2) {
					comparisonIds.add(rec.getStr("id"));
				}
			}
			
			if (!comparisonIds.isEmpty()) {
				// 构建 IN 条件占位符
				StringBuilder placeholders = new StringBuilder();
				for (int i = 0; i < comparisonIds.size(); i++) {
					if (i > 0) placeholders.append(",");
					placeholders.append("?");
				}
				
				// 批量查询所有关联维修记录
				List<Record> repairs = Db.find(
					"SELECT CAST(id AS CHAR) as id, CAST(comparison_id AS CHAR) as comparison_id, repair_type, repair_date, "
					+ "fault_description, repair_action, repair_result, repairer, "
					+ "completion_date "
					+ "FROM siargo_equipment_repair "
					+ "WHERE comparison_id IN (" + placeholders.toString() + ") "
					+ "ORDER BY repair_date ASC",
					comparisonIds.toArray()
				);
				
				// 按 comparison_id 分组
				Map<String, List<Record>> repairMap = new HashMap<>();
				for (Record repair : repairs) {
					String cid = repair.getStr("comparison_id");
					repairMap.computeIfAbsent(cid, k -> new ArrayList<>()).add(repair);
				}
				
				// 将维修列表以 JSON 字符串设置到对比记录
				for (Record rec : list) {
					String id = rec.getStr("id");
					List<Record> repairList = repairMap.get(id);
					if (repairList != null && !repairList.isEmpty()) {
						rec.set("repairs_json", JSON.toJSONString(repairList));
					}
				}
			}
		}
		return page;
	}
	
	/**
	 * 检查设备是否有待审核的对比记录
	 * @param equipmentId 设备ID
	 * @return true-有待审核数据，false-无
	 */
	public boolean hasPendingAudit(Long equipmentId) {
		// 检查最新对比记录是否为待审核（使用NOT EXISTS确定最新记录，避免ORDER BY被JFinal剥离）
		Integer compCount = Db.queryInt(
			"SELECT COUNT(*) FROM siargo_equipment_comparison ec "
			+ "WHERE ec.equipment_id = ? AND ec.audit_status = 1 "
			+ "AND NOT EXISTS (SELECT 1 FROM siargo_equipment_comparison newer "
			+ "WHERE newer.equipment_id = ec.equipment_id "
			+ "AND (newer.comparison_date > ec.comparison_date "
			+ "OR (newer.comparison_date = ec.comparison_date AND newer.creator_time > ec.creator_time) "
			+ "OR (newer.comparison_date = ec.comparison_date AND newer.creator_time = ec.creator_time AND newer.id > ec.id)))",
			equipmentId);
		return compCount != null && compCount > 0;
	}

	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.EQUIPMENT.getValue();
	}
	
}