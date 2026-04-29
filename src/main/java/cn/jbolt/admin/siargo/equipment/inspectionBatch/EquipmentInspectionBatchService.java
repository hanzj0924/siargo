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
import java.util.Date;
import java.util.List;
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
	public void audit(Long batchId, Long auditorId) {
		EquipmentInspectionBatch batch = findById(batchId);
		if (batch != null) {
			batch.setAuditorId(auditorId);
			batch.setAuditorTime(new Date());
			batch.setAuditStatus(2);
			batch.update();
		}
	}

	/**
	 * 批量审核
	 * @param ids 逗号分隔的批次ID
	 * @param auditorId 审核人ID
	 */
	public void batchAudit(String ids, Long auditorId) {
		if (notOk(ids)) return;
		String[] idArr = ids.split(",");
		for (String idStr : idArr) {
			String trimmed = idStr.trim();
			if (trimmed.isEmpty()) continue;
			try {
				audit(Long.valueOf(trimmed), auditorId);
			} catch (NumberFormatException e) {
				// 跳过非法ID
			}
		}
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
		boolean success=equipmentInspectionBatch.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(equipmentInspectionBatch.getId(), JBoltUserKit.getUserId(), equipmentInspectionBatch.getName());
			// 自动创建一条检校记录
			createInspectionRecord(equipmentInspectionBatch);
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
		boolean success=equipmentInspectionBatch.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(equipmentInspectionBatch.getId(), JBoltUserKit.getUserId(), equipmentInspectionBatch.getName());
			// 自动创建一条检校记录
			createInspectionRecord(equipmentInspectionBatch);
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
	 * 批次操作时自动创建一条检校记录
	 */
	private void createInspectionRecord(EquipmentInspectionBatch batch) {
		Integer status = batch.getStatus();
		int inspectionStatus = (status != null) ? status : 1;
		String statusText = (inspectionStatus == 1) ? "合格" : "不合格";
		String description = "检校结果【" + statusText + "】";

		long recordId = JBoltSnowflakeKit.me.nextId();
		Long userId = JBoltUserKit.getUserId();
		Db.update("INSERT INTO siargo_equipment_record (id, equipment_id, batch_id, record_type, record_date, description, user_id, creator_id, creator_time, audit_status) VALUES (?, ?, ?, '1', NOW(), ?, ?, ?, NOW(), 1)",
			recordId, batch.getEquipmentId(), batch.getId(), description, userId, userId);
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