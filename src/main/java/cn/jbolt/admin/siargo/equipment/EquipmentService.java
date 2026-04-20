package cn.jbolt.admin.siargo.equipment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
	 * @return
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, String keywords, String category, String filter) {
		String select = "SELECT e.*, lr.creator_time, lr.auditor_time, lr.audit_status, "
			+ "cu.name AS creator_name, au.name AS auditor_name, "
			+ "(SELECT c.image_url FROM siargo_equipment_certificate c WHERE c.equipment_id = e.id ORDER BY c.certificate_date DESC, c.id DESC LIMIT 1) AS latest_cert_image";
		StringBuilder from = new StringBuilder("FROM siargo_equipment e ");
		from.append("LEFT JOIN (SELECT er1.* FROM siargo_equipment_record er1 INNER JOIN (SELECT equipment_id, MAX(id) AS max_id FROM siargo_equipment_record GROUP BY equipment_id) er2 ON er1.id = er2.max_id) lr ON lr.equipment_id = e.id ");
		from.append("LEFT JOIN jb_user cu ON cu.id = lr.creator_id ");
		from.append("LEFT JOIN jb_user au ON au.id = lr.auditor_id ");
		
		List<Object> params = new ArrayList<>();
		List<String> conditions = new ArrayList<>();
		
		// 分类筛选
		if (isOk(category)) {
			conditions.add("e.category = ?");
			params.add(category);
		}
		
		// 过滤条件
		if ("expired".equals(filter)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 15);
			java.util.Date expireDate = cal.getTime();
			conditions.add("e.next_inspection_date IS NOT NULL AND e.next_inspection_date <= ?");
			params.add(expireDate);
		} else if ("audit".equals(filter)) {
			conditions.add("EXISTS (SELECT 1 FROM siargo_equipment_record er WHERE er.equipment_id = e.id AND er.id = (SELECT MAX(er2.id) FROM siargo_equipment_record er2 WHERE er2.equipment_id = e.id) AND er.audit_status = 1)");
		} else if ("repairing".equals(filter)) {
			conditions.add("e.status = 2");
		} else if ("sealed".equals(filter)) {
			conditions.add("e.status IN (3, 4)");
		}
		
		// 关键词模糊匹配
		if (isOk(keywords)) {
			conditions.add("(e.equipment_no LIKE ? OR e.name LIKE ?)");
			String kw = "%" + keywords + "%";
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
				+ ", SUM(CASE WHEN (SELECT er.audit_status FROM siargo_equipment_record er WHERE er.equipment_id = siargo_equipment.id ORDER BY er.id DESC LIMIT 1) = 1 THEN 1 ELSE 0 END) AS audit"
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
	 * @return
	 */
	public Ret batchInspection(String ids, java.util.Date lastDate, java.util.Date nextDate) {
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
			addUpdateSystemLog(ids, userId, "批量校准设备：" + ids);
			// 为每台设备创建编制记录
			for (Long equipmentId : idList) {
				// 查询设备状态
				Record equip = Db.findFirst("SELECT status FROM siargo_equipment WHERE id = ?", equipmentId);
				String statusText = "未知";
				if (equip != null) {
					Integer st = equip.getInt("status");
					if (st != null) {
						switch (st) {
							case 1: statusText = "合格"; break;
							case 2: statusText = "维修中"; break;
							case 3: statusText = "已封存"; break;
							case 4: statusText = "报废"; break;
							case 5: statusText = "不合格"; break;
						}
					}
				}
				String description = "批量编制：检校对比结果【" + statusText + "】";
				long recordId = JBoltSnowflakeKit.me.nextId();
				Db.update("INSERT INTO siargo_equipment_record (id, equipment_id, record_type, record_date, description, user_id, creator_id, creator_time, audit_status) VALUES (?, ?, '1', ?, ?, ?, ?, NOW(), 1)",
					recordId, equipmentId, lastDate, description, userId, userId);
			}
		}
		return ret(count > 0);
	}
	
	/**
	 * 批量审核
	 * @param ids 逗号分隔的ID
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
		// 更新每台设备最新record的审核状态
		for (Long equipmentId : idList) {
			Db.update("UPDATE siargo_equipment_record SET audit_status = 2, auditor_id = ?, auditor_time = NOW() WHERE equipment_id = ? AND id = (SELECT max_id FROM (SELECT MAX(id) AS max_id FROM siargo_equipment_record WHERE equipment_id = ?) t)",
				userId, equipmentId, equipmentId);
		}
		clearOverviewCountsCache();
		addUpdateSystemLog(ids, userId, "批量审核设备：" + ids);
		return ret(true);
	}
	
	/**
	 * 批量更改状态
	 * @param ids 逗号分隔的ID
	 * @param status 设备状态（1-合格 2-维修中 3-已封存 4-报废）
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
		Object[] params = new Object[idList.size() + 1];
		params[0] = status;
		for (int i = 0; i < idList.size(); i++) {
			params[1 + i] = idList.get(i);
		}
		String sql = "UPDATE siargo_equipment SET status = ? WHERE id IN (" + placeholders + ")";
		int count = Db.update(sql, params);
		if (count > 0) {
			clearOverviewCountsCache();
			addUpdateSystemLog(ids, userId, "批量更改设备状态：" + ids);
		}
		return ret(count > 0);
	}
	
	/**
	 * 设备维修校准记录分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param equipmentId
	 * @return
	 */
	public Page<Record> paginateRecordDatas(int pageNumber, int pageSize, Long equipmentId) {
		return Db.paginate(pageNumber, pageSize,
				"SELECT er.*, u.name AS user_name, " +
				"d.name AS record_type_name, " +
				"cu.name AS creator_name, au.name AS auditor_name, " +
				"(SELECT c.image_url FROM siargo_equipment_certificate c WHERE c.record_id = er.id ORDER BY c.id LIMIT 1) AS cert_image_url",
				"FROM siargo_equipment_record er " +
				"LEFT JOIN jb_user u ON u.id = er.user_id " +
				"LEFT JOIN jb_user cu ON cu.id = er.creator_id " +
				"LEFT JOIN jb_user au ON au.id = er.auditor_id " +
				"LEFT JOIN jb_dictionary d ON d.sn = er.record_type AND d.type_id = (SELECT id FROM jb_dictionary_type WHERE type_key = 'siargo_equipment_record_type' LIMIT 1) " +
				"WHERE er.equipment_id = ? ORDER BY er.record_date DESC",
				equipmentId);
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