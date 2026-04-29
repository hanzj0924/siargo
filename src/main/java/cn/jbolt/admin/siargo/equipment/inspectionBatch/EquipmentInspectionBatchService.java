package cn.jbolt.admin.siargo.equipment.inspectionBatch;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentInspectionBatch;
import cn.jbolt.siargo.model.EquipmentRecord;
import cn.jbolt.siargo.model.EquipmentCertificate;

import java.io.File;
import java.util.List;
import cn.jbolt.common.util.DateUtil;
/**
 * 检校批次记录 Service
 * @ClassName: EquipmentInspectionBatchService   
 * @author: hanzj
 * @date: 2026-04-26 14:43  
 */
public class EquipmentInspectionBatchService extends JBoltBaseService<EquipmentInspectionBatch> {
	private static final Log LOG = Log.getLog(EquipmentInspectionBatchService.class);
	private final EquipmentInspectionBatch dao=new EquipmentInspectionBatch().dao();
	@Override
	protected EquipmentInspectionBatch dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<EquipmentInspectionBatch> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("batch_date","desc", pageNumber, pageSize, keywords, "batch_date");
	}

	/**
	 * 按设备ID分页查询批次列表
	 * @param pageNumber 页码
	 * @param pageSize 每页大小
	 * @param equipmentId 设备ID
	 * @return
	 */
	public Page<Record> paginateByEquipmentId(int pageNumber, int pageSize, Long equipmentId) {
		return Db.paginate(pageNumber, pageSize,
			"SELECT ib.*, " +
			"u1.name AS creator_name, " +
			"ib.creator_time AS creator_time, " +
			"u2.name AS auditor_name, " +
			"ib.auditor_time AS auditor_time, " +
			"(SELECT COUNT(*) FROM siargo_equipment_record WHERE batch_id = ib.id) AS record_count",
			"FROM siargo_equipment_inspection_batch ib " +
			"LEFT JOIN jb_user u1 ON u1.id = ib.creator_id " +
			"LEFT JOIN jb_user u2 ON u2.id = ib.auditor_id " +
			"WHERE ib.equipment_id = ? " +
			"ORDER BY ib.batch_date DESC",
			equipmentId);
	}

	/**
	 * 审核单个批次
	 * @param batchId 批次ID
	 * @param auditorId 审核人ID
	 */
	public Ret audit(Long batchId, Long auditorId) {
		EquipmentInspectionBatch batch = findById(batchId);
		if (batch == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 验证是否已审核
		if (batch.getAuditStatus() != null && batch.getAuditStatus() == 2) {
			return fail("请勿重复审核！");
		}

		batch.setAuditorId(auditorId);
		batch.set("auditor_time", DateUtil.getDateString(DateUtil.YMDHMS));
		batch.setAuditStatus(2);
		batch.update();

		// 生成审核事件记录
		long recordId = JBoltSnowflakeKit.me.nextId();
		Db.update("INSERT INTO siargo_equipment_record " +
			"(id, equipment_id, batch_id, record_type, record_date, description, user_id) " +
			"VALUES (?, ?, ?, '1', NOW(), ?, ?)",
			recordId, batch.getEquipmentId(), batchId, "检校对比结果【已审核】", auditorId);
		return ret(true);
	}

	/**
	 * 批量审核
	 * @param ids 逗号分隔的批次ID
	 * @param auditorId 审核人ID
	 */
	public Ret batchAudit(String ids, Long auditorId) {
		if (notOk(ids)) return fail(JBoltMsg.PARAM_ERROR);
		String[] idArr = ids.split(",");
		for (String idStr : idArr) {
			String trimmed = idStr.trim();
			if (trimmed.isEmpty()) continue;
			try {
				Ret ret = audit(Long.valueOf(trimmed), auditorId);
				if (ret.isFail()) {
					return ret;
				}
			} catch (NumberFormatException e) {
				// 跳过非法ID
			}
		}
		return ret(true);
	}

	/**
	 * 统计批次下记录数
	 * @param batchId 批次ID
	 * @return 记录数量
	 */
	public int countRecordsByBatchId(Long batchId) {
		if (notOk(batchId)) return 0;
		Long count = Db.queryLong("SELECT COUNT(*) FROM siargo_equipment_record WHERE batch_id = ?", batchId);
		return count != null ? count.intValue() : 0;
	}
	
	/**
	 * 保存
	 * @param equipmentInspectionBatch
	 * @return
	 */
	public Ret save(EquipmentInspectionBatch equipmentInspectionBatch) {
		if(equipmentInspectionBatch==null || isOk(equipmentInspectionBatch.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(equipmentInspectionBatch.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		// 验证：设备状态非正常时，不能设置检校结果为合格
		if (equipmentInspectionBatch.getStatus() != null && equipmentInspectionBatch.getStatus() == 1) {
			Long equipmentId = equipmentInspectionBatch.getEquipmentId();
			Integer equipmentStatus = Db.queryInt("SELECT status FROM siargo_equipment WHERE id = ?", equipmentId);
			if (equipmentStatus == null || equipmentStatus != 1) {
				String equipmentNo = Db.queryStr("SELECT equipment_no FROM siargo_equipment WHERE id = ?", equipmentId);
				return fail("【" + (equipmentNo != null ? equipmentNo : "") + "】状态异常，不能填合格！");
			}
		}
		boolean success=equipmentInspectionBatch.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(equipmentInspectionBatch.getId(), JBoltUserKit.getUserId(), equipmentInspectionBatch.getName());
			// 自动创建一条检校记录
			createInspectionRecord(equipmentInspectionBatch);
			// 根据检校批次状态同步设备使用状态
			syncEquipmentStatus(equipmentInspectionBatch);
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param equipmentInspectionBatch
	 * @return
	 */
	public Ret update(EquipmentInspectionBatch equipmentInspectionBatch) {
		if(equipmentInspectionBatch==null || notOk(equipmentInspectionBatch.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		EquipmentInspectionBatch dbEquipmentInspectionBatch=findById(equipmentInspectionBatch.getId());
		if(dbEquipmentInspectionBatch==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(equipmentInspectionBatch.getName(), equipmentInspectionBatch.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		// 验证：设备状态非正常时，不能设置检校结果为合格
		if (equipmentInspectionBatch.getStatus() != null && equipmentInspectionBatch.getStatus() == 1) {
			Long equipmentId = equipmentInspectionBatch.getEquipmentId();
			Integer equipmentStatus = Db.queryInt("SELECT status FROM siargo_equipment WHERE id = ?", equipmentId);
			if (equipmentStatus == null || equipmentStatus != 1) {
				String equipmentNo = Db.queryStr("SELECT equipment_no FROM siargo_equipment WHERE id = ?", equipmentId);
				return fail("【" + (equipmentNo != null ? equipmentNo : "") + "】状态异常，不能填合格！");
			}
		}
		boolean success=equipmentInspectionBatch.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(equipmentInspectionBatch.getId(), JBoltUserKit.getUserId(), equipmentInspectionBatch.getName());
			// 自动创建一条检校记录
			createInspectionRecord(equipmentInspectionBatch);
			// 根据检校批次状态同步设备使用状态
			syncEquipmentStatus(equipmentInspectionBatch);
		}
		return ret(success);
	}
	
	/**
	 * 根据检校批次状态同步设备使用状态
	 * status=1（合格）→ equipment.status=1（正常）
	 * status=2（不合格）→ equipment.status=2（维修中）
	 */
	private void syncEquipmentStatus(EquipmentInspectionBatch batch) {
		Integer batchStatus = batch.getStatus();
		Long equipmentId = batch.getEquipmentId();
		if (batchStatus != null && (batchStatus == 1 || batchStatus == 2)) {
			Db.update("UPDATE siargo_equipment SET status = ? WHERE id = ? AND status != ?",
				batchStatus, equipmentId, batchStatus);
		}
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
	 * 批次操作时自动创建一条检校记录
	 */
	private void createInspectionRecord(EquipmentInspectionBatch batch) {
		Integer status = batch.getStatus();
		int inspectionStatus = (status != null) ? status : 1;
		String statusText = (inspectionStatus == 1) ? "合格" : "不合格";
		String description = "检校结果【" + statusText + "】";

		long recordId = JBoltSnowflakeKit.me.nextId();
		Long userId = JBoltUserKit.getUserId();
		Db.update("INSERT INTO siargo_equipment_record (id, equipment_id, batch_id, record_type, record_date, description, user_id) VALUES (?, ?, ?, '1', NOW(), ?, ?)",
			recordId, batch.getEquipmentId(), batch.getId(), description, userId);
	}
	
	/**
	 * 删除数据后执行的回调
	 * @param equipmentInspectionBatch 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(EquipmentInspectionBatch equipmentInspectionBatch, Kv kv) {
		// 级联删除批次下所有记录及关联的证书
		try {
			Long batchId = equipmentInspectionBatch.getId();
			List<EquipmentRecord> records = new EquipmentRecord().dao()
				.find("SELECT * FROM siargo_equipment_record WHERE batch_id = ?", batchId);
			if (records != null && !records.isEmpty()) {
				for (EquipmentRecord record : records) {
					// 删除关联的证书图片文件和数据库记录
					try {
						List<EquipmentCertificate> certs = new EquipmentCertificate().dao()
							.find("SELECT * FROM siargo_equipment_certificate WHERE record_id = ?", record.getId());
						if (certs != null) {
							for (EquipmentCertificate cert : certs) {
								String imageUrl = cert.getStr("image_url");
								if (isOk(imageUrl)) {
									deleteCertificateFile(imageUrl);
								}
							}
						}
						Db.delete("DELETE FROM siargo_equipment_certificate WHERE record_id = ?", record.getId());
					} catch (Exception e) {
						LOG.error("级联删除证书记录异常，recordId=" + record.getId(), e);
					}
				}
				// 删除所有关联的 equipment_record
				Db.delete("DELETE FROM siargo_equipment_record WHERE batch_id = ?", batchId);
			}
		} catch (Exception e) {
			LOG.error("级联删除批次关联记录异常，batchId=" + equipmentInspectionBatch.getId(), e);
		}
		return null;
	}

	/**
	 * 删除单个证书图片的磁盘文件
	 */
	private void deleteCertificateFile(String imageUrl) {
		if (imageUrl == null || imageUrl.isEmpty()) return;
		try {
			String physicalPath = PathKit.getWebRootPath() + imageUrl;
			File file = new File(physicalPath);
			if (file.exists() && file.isFile()) {
				boolean deleted = file.delete();
				if (deleted) {
					LOG.info("证书图片删除成功: " + physicalPath);
				} else {
					LOG.warn("证书图片删除失败: " + physicalPath);
				}
			}
		} catch (Exception e) {
			LOG.error("删除证书图片文件异常: " + imageUrl, e);
		}
	}
	
	/**
	 * 检测是否可以删除
	 * @param equipmentInspectionBatch 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(EquipmentInspectionBatch equipmentInspectionBatch, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(equipmentInspectionBatch, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.EquipmentInspectionBatch.getValue();
	}
	
}