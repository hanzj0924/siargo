package cn.jbolt.admin.siargo.equipment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.aop.Inject;
import cn.jbolt.admin.siargo.equipment.certificate.EquipmentCertificateService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.kit.JBoltSnowflakeKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.siargo.model.Equipment;
import cn.jbolt.siargo.model.EquipmentRecord;
import cn.jbolt.siargo.model.EquipmentInspectionBatch;

import java.util.concurrent.locks.ReentrantLock;
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
	 * @return
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, String keywords, String category, String filter, String status) {
		String select = "SELECT e.*, "
			+ "(SELECT c.image_url FROM siargo_equipment_certificate c WHERE c.equipment_id = e.id ORDER BY c.certificate_date DESC, c.id DESC LIMIT 1) AS latest_cert_image, "
			+ "lb.creator_time, lb.auditor_time, lb.audit_status, lb.status AS inspection_status, "
			+ "cu.name AS creator_name, au.name AS auditor_name";
		StringBuilder from = new StringBuilder("FROM siargo_equipment e ");
		from.append("LEFT JOIN (SELECT er1.id, er1.equipment_id FROM siargo_equipment_record er1 WHERE er1.id IN (SELECT MAX(id) FROM siargo_equipment_record GROUP BY equipment_id)) lr ON lr.equipment_id = e.id ");
		from.append("LEFT JOIN (SELECT ib1.id, ib1.equipment_id, ib1.creator_id, ib1.auditor_id, ib1.creator_time, ib1.auditor_time, ib1.audit_status, ib1.status FROM siargo_equipment_inspection_batch ib1 WHERE ib1.id IN (SELECT MAX(id) FROM siargo_equipment_inspection_batch GROUP BY equipment_id)) lb ON lb.equipment_id = e.id ");
		from.append("LEFT JOIN jb_user cu ON cu.id = lb.creator_id ");
		from.append("LEFT JOIN jb_user au ON au.id = lb.auditor_id ");
		
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
		
		// 过滤条件
		if ("expired".equals(filter)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 15);
			java.util.Date expireDate = cal.getTime();
			conditions.add("e.next_inspection_date IS NOT NULL AND e.next_inspection_date <= ?");
			params.add(expireDate);
		} else if ("audit".equals(filter)) {
			conditions.add("EXISTS (SELECT 1 FROM siargo_equipment_inspection_batch ib WHERE ib.equipment_id = e.id AND ib.id = (SELECT MAX(ib2.id) FROM siargo_equipment_inspection_batch ib2 WHERE ib2.equipment_id = e.id) AND ib.audit_status = 1)");
		} else if ("repairing".equals(filter)) {
			conditions.add("e.status = 2");
		} else if ("sealed".equals(filter)) {
			conditions.add("e.status IN (3, 4)");
		}
		
		// 关键词模糊匹配
		if (isOk(keywords)) {
			conditions.add("(e.equipment_no LIKE ? OR e.name LIKE ? OR e.model LIKE ?)");
			String kw = "%" + keywords + "%";
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
				+ ", SUM(CASE WHEN next_inspection_date IS NOT NULL AND next_inspection_date <= DATE_ADD(CURDATE(), INTERVAL 15 DAY) THEN 1 ELSE 0 END) AS expired"
				+ ", SUM(CASE WHEN (SELECT ib.audit_status FROM siargo_equipment_inspection_batch ib WHERE ib.equipment_id = siargo_equipment.id ORDER BY ib.id DESC LIMIT 1) = 1 THEN 1 ELSE 0 END) AS audit"
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
			counts.set("expired",   toLong(row.getLong("expired")));
			counts.set("audit",     toLong(row.getLong("audit")));
		} else {
			counts.set("total", 0L);
			counts.set("repairing", 0L);
			counts.set("sealed", 0L);
			counts.set("expired", 0L);
			counts.set("audit", 0L);
		}
		return counts;
	}

	/** 将Long值转为0L（null安全） */
	private long toLong(Long val) {
		return val != null ? val : 0L;
	}
	
	/**
	 * 批量校准
	 * @param ids 逗号分隔的ID
	 * @param lastDate 上次校准日期
	 * @param nextDate 下次校准日期
	 * @param status 检定结果：1-合格 2-不合格
	 * @return
	 */
	public Ret batchInspection(String ids, java.util.Date lastDate, java.util.Date nextDate, Integer status, String description) {
		if (notOk(ids)) {
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
		// 仅更新检校日期，不再更新设备使用状态
		Object[] params = new Object[idList.size() + 2];
		params[0] = lastDate;
		params[1] = nextDate;
		for (int i = 0; i < idList.size(); i++) {
			params[2 + i] = idList.get(i);
		}
		String sql = "UPDATE siargo_equipment SET last_inspection_date = ?, next_inspection_date = ? WHERE id IN (" + placeholders + ")";
		int count = Db.update(sql, params);
		if (count > 0) {
			clearOverviewCountsCache();
			// 检定结果文案：1-合格 2-不合格
			int inspectionStatus = (status != null) ? status : 1;
			// 设备记录的事件描述：根据检校状态自动生成
			String recordDescription = "检校对比结果【" + ((inspectionStatus == 1) ? "合格" : "不合格") + "】";
			// 为每台设备创建检校批次记录和设备记录
			for (Long equipmentId : idList) {
				// 验证：设备状态非正常时，不能设置检校结果为合格
				if (inspectionStatus == 1) {
					Integer equipmentStatus = Db.queryInt("SELECT status FROM siargo_equipment WHERE id = ?", equipmentId);
					if (equipmentStatus == null || equipmentStatus != 1) {
						String equipmentNo = Db.queryStr("SELECT equipment_no FROM siargo_equipment WHERE id = ?", equipmentId);
						return fail("【" + (equipmentNo != null ? equipmentNo : "") + "】状态异常，不能填合格！");
					}
				}
				// 创建检校批次记录（description使用用户输入的检校概况）
				long batchId = JBoltSnowflakeKit.me.nextId();
				Db.update("INSERT INTO siargo_equipment_inspection_batch (id, equipment_id, batch_date, description, status, creator_id, creator_time, audit_status) VALUES (?, ?, ?, ?, ?, ?, NOW(), 1)",
					batchId, equipmentId, lastDate, description, inspectionStatus, userId);

				// 生成设备记录，关联批次ID（description使用自动生成的事件描述）
				long recordId = JBoltSnowflakeKit.me.nextId();
				Db.update("INSERT INTO siargo_equipment_record (id, equipment_id, batch_id, record_type, record_date, description, user_id) VALUES (?, ?, ?, '1', NOW(), ?, ?)",
					recordId, equipmentId, batchId, recordDescription, userId);
			}
			// 根据检校批次状态同步设备使用状态
			if (inspectionStatus == 1 || inspectionStatus == 2) {
				Object[] syncParams = new Object[idList.size() + 2];
				syncParams[0] = inspectionStatus;
				for (int i = 0; i < idList.size(); i++) {
					syncParams[1 + i] = idList.get(i);
				}
				syncParams[syncParams.length - 1] = inspectionStatus;
				Db.update("UPDATE siargo_equipment SET status = ? WHERE id IN (" + placeholders + ") AND status != ?", syncParams);
			}
		}
		return ret(count > 0);
	}
	
	/**
	 * 批量审核（操作检校批次表）
	 * @param ids 逗号分隔的设备ID
	 * @return
	 */
	public Ret batchAudit(String ids) {
		if (notOk(ids)) {
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
		// 更新每台设备最新检校批次的审核状态
		for (Long equipmentId : idList) {
			// 验证是否已审核
			Integer auditStatus = Db.queryInt("SELECT audit_status FROM siargo_equipment_inspection_batch WHERE equipment_id = ? AND id = (SELECT max_id FROM (SELECT MAX(id) AS max_id FROM siargo_equipment_inspection_batch WHERE equipment_id = ?) t)", equipmentId, equipmentId);
			if (auditStatus != null && auditStatus == 2) {
				return fail("请勿重复审核！");
			}

			Db.update("UPDATE siargo_equipment_inspection_batch SET audit_status = 2, auditor_id = ?, auditor_time = NOW() WHERE equipment_id = ? AND id = (SELECT max_id FROM (SELECT MAX(id) AS max_id FROM siargo_equipment_inspection_batch WHERE equipment_id = ?) t)",
				userId, equipmentId, equipmentId);

			// 获取最新批次ID
			Long latestBatchId = Db.queryLong("SELECT MAX(id) FROM siargo_equipment_inspection_batch WHERE equipment_id = ?", equipmentId);

			// 生成审核事件记录
			long recordId = JBoltSnowflakeKit.me.nextId();
			Db.update("INSERT INTO siargo_equipment_record " +
				"(id, equipment_id, batch_id, record_type, record_date, description, user_id) " +
				"VALUES (?, ?, ?, '1', NOW(), ?, ?)",
				recordId, equipmentId, latestBatchId, "检校对比结果【已审核】", userId);
		}
		clearOverviewCountsCache();
		return ret(true);
	}
	
	/**
	 * 批量更改状态
	 * @param ids 逗号分隔的ID
	 * @param status 设备使用状态（1-正常 2-维修中 3-已封存 4-报废）
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
		// 1. 先查询所有设备的旧状态
		Map<Long, Integer> oldStatusMap = new HashMap<>();
		for (Long equipmentId : idList) {
			Integer oldStatus = Db.queryInt("SELECT status FROM siargo_equipment WHERE id = ?", equipmentId);
			oldStatusMap.put(equipmentId, oldStatus);
		}
		// 2. 执行 UPDATE
		Object[] params = new Object[idList.size() + 1];
		params[0] = status;
		for (int i = 0; i < idList.size(); i++) {
			params[1 + i] = idList.get(i);
		}
		String sql = "UPDATE siargo_equipment SET status = ? WHERE id IN (" + placeholders + ")";
		int count = Db.update(sql, params);
		if (count > 0) {
			clearOverviewCountsCache();
			// 当新状态为1（正常）或2（不合格）时，将该设备最新检校批次状态同步更新（仅在批次状态不一致时才更新）
			if (status == 1 || status == 2) {
				Object[] idParams = new Object[idList.size() + 2];
				idParams[0] = status;
				for (int i = 0; i < idList.size(); i++) {
					idParams[1 + i] = idList.get(i);
				}
				idParams[idList.size() + 1] = status;
				Db.update("UPDATE siargo_equipment_inspection_batch SET status = ? WHERE id IN " +
					"(SELECT max_id FROM (SELECT MAX(id) AS max_id FROM siargo_equipment_inspection_batch WHERE equipment_id IN (" +
					placeholders + ") GROUP BY equipment_id) tmp) AND status != ?",
					idParams);
			}
			// 3. 为每台设备生成状态变更记录
			String newStatusText = getStatusText(status);
			for (Long equipmentId : idList) {
				Integer oldStatus = oldStatusMap.get(equipmentId);
				String oldStatusText = getStatusText(oldStatus);
				String description = oldStatusText + "->" + newStatusText;
				// 查询最新批次ID
				Long latestBatchId = Db.queryLong("SELECT MAX(id) FROM siargo_equipment_inspection_batch WHERE equipment_id = ?", equipmentId);
				// 生成设备记录
				long recordId = JBoltSnowflakeKit.me.nextId();
				Db.update("INSERT INTO siargo_equipment_record " +
					"(id, equipment_id, batch_id, record_type, record_date, description, user_id) " +
					"VALUES (?, ?, ?, '1', NOW(), ?, ?)",
					recordId, equipmentId, latestBatchId, description, userId);
			}
		}
		return ret(count > 0);
	}

	/**
	 * 获取设备状态文本
	 * @param status 状态值（1-正常 2-维修中 3-已封存 4-报废）
	 * @return 状态文本
	 */
	private String getStatusText(Integer status) {
		if (status == null) return "未知";
		switch (status) {
			case 1: return "正常";
			case 2: return "维修中";
			case 3: return "已封存";
			case 4: return "报废";
			default: return "未知";
		}
	}
	
	/**
	 * 设备维修校准记录分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param equipmentId
	 * @param batchId 检校批次ID，不为null时按批次过滤
	 * @return
	 */
	public Page<Record> paginateRecordDatas(int pageNumber, int pageSize, Long equipmentId, Long batchId) {
		// batchId 为 null 或 <= 0 时，表示未选中任何批次，直接返回空页
		if (batchId == null || batchId <= 0) {
			return new Page<>(new ArrayList<>(), pageNumber, pageSize, 0, 0);
		}
		String select = "SELECT er.*, u.name AS user_name, " +
				"d.name AS record_type_name, " +
				"(SELECT c.image_url FROM siargo_equipment_certificate c WHERE c.record_id = er.id ORDER BY c.id LIMIT 1) AS cert_image_url";
		StringBuilder from = new StringBuilder("FROM siargo_equipment_record er " +
				"LEFT JOIN jb_user u ON u.id = er.user_id " +
				"LEFT JOIN jb_dictionary d ON d.sn = er.record_type AND d.type_id = (SELECT id FROM jb_dictionary_type WHERE type_key = 'siargo_equipment_record_type' LIMIT 1) " +
				"WHERE er.equipment_id = ? AND er.batch_id = ?");
		List<Object> params = new ArrayList<>();
		params.add(equipmentId);
		params.add(batchId);
		from.append(" ORDER BY er.record_date DESC");
		return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
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
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.EQUIPMENT.getValue();
	}
	
}