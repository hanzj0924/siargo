package cn.jbolt.admin.siargo.equipment.comparison;

import java.util.Calendar;
import java.util.Date;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.aop.Inject;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentComparison;
import cn.jbolt.admin.siargo.equipment.EquipmentService;
import cn.jbolt.admin.siargo.equipment.certificate.EquipmentCertificateService;
import cn.jbolt.common.util.DateUtil;
/**
 * 检校对比记录 Service
 * @ClassName: EquipmentComparisonService   
 * @author: hanzj
 * @date: 2026-05-06 17:25  
 */
public class EquipmentComparisonService extends JBoltBaseService<EquipmentComparison> {
	private final EquipmentComparison dao=new EquipmentComparison().dao();

	@Inject
	private EquipmentService equipmentService;

	@Inject
	private EquipmentCertificateService equipmentCertificateService;

	@Override
	protected EquipmentComparison dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<EquipmentComparison> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("comparison_date","desc", pageNumber, pageSize, keywords, "comparison_date");
	}
	
	/**
	 * 保存
	 * @param equipmentComparison
	 * @param certificateImageUrls 证书图片URL（逗号分隔）
	 * @param certificateDate 证书日期
	 * @param certificateRemark 证书描述
	 * @return
	 */
	public Ret save(EquipmentComparison equipmentComparison, String certificateImageUrls, String certificateDate, String certificateRemark) {
		if(equipmentComparison==null || isOk(equipmentComparison.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 前置校验：设备已封存或报废时不允许新增对比
		Long equipmentId = equipmentComparison.getEquipmentId();
		Integer equipmentStatus = Db.queryInt("SELECT status FROM siargo_equipment WHERE id = ?", equipmentId);
		if (equipmentStatus != null && (equipmentStatus == 3 || equipmentStatus == 4)) {
			String equipmentNo = Db.queryStr("SELECT equipment_no FROM siargo_equipment WHERE id = ?", equipmentId);
			String statusText = (equipmentStatus == 3) ? "已封存" : "报废";
			return fail("【" + (equipmentNo != null ? equipmentNo : "") + "】已" + statusText + "，不能新增对比记录！");
		}
		// 自动设置创建人、创建时间、审核状态
		equipmentComparison.setCreatorId(JBoltUserKit.getUserId());
		equipmentComparison.set("creator_time", DateUtil.getDateString(DateUtil.YMDHMS));
		equipmentComparison.setAuditStatus(1);
		boolean success=equipmentComparison.save();
		if(success) {
			// 根据对比结果联动设备状态
			syncEquipmentStatus(equipmentId, equipmentComparison.getResult());
			// 定期对比（comparison_type=1）时，同步更新设备检校日期
			syncInspectionDate(equipmentComparison);
			// 保存证书图片
			if(isOk(certificateImageUrls)) {
				try {
					equipmentCertificateService.saveCertificatesForComparison(equipmentComparison.getId(), equipmentId, certificateImageUrls, certificateDate, certificateRemark);
				} catch (Exception e) {
					System.err.println("对比记录保存成功但证书图片保存失败: " + e.getMessage());
				}
			}
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param equipmentComparison
	 * @param certificateImageUrls 证书图片URL（逗号分隔）
	 * @param certificateDate 证书日期
	 * @param certificateRemark 证书描述
	 * @return
	 */
	public Ret update(EquipmentComparison equipmentComparison, String certificateImageUrls, String certificateDate, String certificateRemark) {
		if(equipmentComparison==null || notOk(equipmentComparison.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		EquipmentComparison dbEquipmentComparison=findById(equipmentComparison.getId());
		if(dbEquipmentComparison==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(equipmentComparison.getName(), equipmentComparison.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=equipmentComparison.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(equipmentComparison.getId(), JBoltUserKit.getUserId(), equipmentComparison.getName());
			// 根据对比结果联动设备状态（合格→正常，不合格→维修中）
			syncEquipmentStatus(equipmentComparison.getEquipmentId(), equipmentComparison.getResult());
			// 更新证书图片
			if(isOk(certificateImageUrls)) {
				try {
					equipmentCertificateService.updateCertificatesForComparison(equipmentComparison.getId(), equipmentComparison.getEquipmentId(), certificateImageUrls, certificateDate, certificateRemark);
				} catch (Exception e) {
					System.err.println("对比记录更新成功但证书图片更新失败: " + e.getMessage());
				}
			} else {
				// 如果没有传图片URL，清空关联证书
				equipmentCertificateService.deleteByComparisonId(equipmentComparison.getId());
			}
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
	 * 审核单条对比记录
	 * @param comparisonId 对比记录ID
	 * @return
	 */
	public Ret audit(Long comparisonId) {
		EquipmentComparison comparison = findById(comparisonId);
		if (comparison == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 验证是否已审核
		if (comparison.getAuditStatus() != null && comparison.getAuditStatus() == 2) {
			return fail("请勿重复审核！");
		}
		Long userId = JBoltUserKit.getUserId();
		comparison.setAuditorId(userId);
		comparison.set("auditor_time", DateUtil.getDateString(DateUtil.YMDHMS));
		comparison.setAuditStatus(2);
		comparison.update();

		// 审核后联动设备状态
		syncEquipmentStatus(comparison.getEquipmentId(), comparison.getResult());

		return ret(true);
	}

	/**
	 * 批量审核
	 * @param ids 逗号分隔的对比记录ID
	 * @return
	 */
	public Ret batchAudit(String ids) {
		if (notOk(ids)) return fail(JBoltMsg.PARAM_ERROR);
		String[] idArr = ids.split(",");
		for (String idStr : idArr) {
			String trimmed = idStr.trim();
			if (trimmed.isEmpty()) continue;
			try {
				Ret ret = audit(Long.valueOf(trimmed));
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
	 * 定期对比时同步更新设备检校日期
	 * last_inspection_date = 对比日期
	 * next_inspection_date = 对比日期 + 3个月 - 1天
	 */
	private void syncInspectionDate(EquipmentComparison equipmentComparison) {
		Integer comparisonType = equipmentComparison.getComparisonType();
		if (comparisonType == null || comparisonType != 1) {
			return;
		}
		Date comparisonDate = equipmentComparison.getComparisonDate();
		if (comparisonDate == null) {
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(comparisonDate);
		cal.add(Calendar.MONTH, 3);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Db.update("UPDATE siargo_equipment SET last_inspection_date = ?, next_inspection_date = ? WHERE id = ?",
			comparisonDate, cal.getTime(), equipmentComparison.getEquipmentId());
	}

	/**
	 * 根据对比结果联动设备状态
	 * result=1（合格）→ equipment.status=1（正常）
	 * result=2（不合格）→ equipment.status=2（维修中）
	 */
	private void syncEquipmentStatus(Long equipmentId, Integer result) {
		if (result == null) {
			return;
		}
		if (result == 1) {
			Db.update("UPDATE siargo_equipment SET status = 1 WHERE id = ? AND status != 1",
				equipmentId);
			equipmentService.clearOverviewCountsCache();
		} else if (result == 2) {
			Db.update("UPDATE siargo_equipment SET status = 2 WHERE id = ? AND status != 2",
				equipmentId);
			equipmentService.clearOverviewCountsCache();
		}
	}

	/**
	 * 删除数据后执行的回调
	 * @param equipmentComparison 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(EquipmentComparison equipmentComparison, Kv kv) {
		addDeleteSystemLog(equipmentComparison.getId(), JBoltUserKit.getUserId(), equipmentComparison.getDescription());
		// 级联删除关联的证书记录（同时清理磁盘图片文件）
		equipmentCertificateService.deleteByComparisonId(equipmentComparison.getId());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param equipmentComparison 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(EquipmentComparison equipmentComparison, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(equipmentComparison, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.EQUIPMENTCOMPARISON.getValue();
	}
	
}