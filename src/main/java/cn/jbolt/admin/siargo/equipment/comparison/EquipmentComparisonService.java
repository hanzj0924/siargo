package cn.jbolt.admin.siargo.equipment.comparison;

import java.util.Calendar;
import java.util.Date;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
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
	private static final Log LOG = Log.getLog(EquipmentComparisonService.class);
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
    public Ret save(EquipmentComparison input, String urls, String date, String remark) {
        if (input == null || isOk(input.getId())) return fail(JBoltMsg.PARAM_ERROR);
        Integer state = Db.queryInt("SELECT status FROM siargo_equipment WHERE id=?", input.getEquipmentId());
        if (state == null || state == 3 || state == 4) return fail("设备不存在、已封存或已报废，不能新增对比");
        input.set("id", cn.hutool.core.util.IdUtil.getSnowflakeNextId()).set("creator_id", JBoltUserKit.getUserId())
                .set("creator_time", DateUtil.getDateString(DateUtil.YMDHMS)).set("audit_status", 1);
        return persistWithCertificates(input, urls, date, remark, false);
    }
	
	/**
	 * 更新
	 * @param equipmentComparison
	 * @param certificateImageUrls 证书图片URL（逗号分隔）
	 * @param certificateDate 证书日期
	 * @param certificateRemark 证书描述
	 * @return
	 */
    public Ret update(EquipmentComparison input, String urls, String date, String remark) {
        if (input == null || notOk(input.getId()) || findById(input.getId()) == null) return fail(JBoltMsg.PARAM_ERROR);
        return persistWithCertificates(input, urls, date, remark, true);
    }

    private Ret persistWithCertificates(EquipmentComparison input, String urls, String date, String remark, boolean update) {
        EquipmentCertificateService.Prepared prepared = null;
        boolean committed = false;
        try {
            prepared = equipmentCertificateService.prepare(input.getEquipmentId(), input.getId(), urls, date, remark, update);
            var work = prepared;
            boolean ok = Db.tx(() -> {
                if (!(update ? input.update() : input.save())) return false;
                syncEquipmentStatus(input.getEquipmentId(), input.getResult());
                if (!update) syncInspectionDate(input);
                return work.persist();
            });
            if (!ok) return work.rollback("对比记录或证书保存失败");
            committed = true;
            equipmentService.clearOverviewCountsCache();
            return work.committed();
        } catch (Exception e) { return prepared == null || committed ? fail(e.getMessage()) : prepared.rollback(e.getMessage()); }
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
		} else if (result == 2) {
			Db.update("UPDATE siargo_equipment SET status = 2 WHERE id = ? AND status != 2",
				equipmentId);
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
		// 级联删除关联的证书记录（只删 DB；物理文件由 Controller 在事务提交后统一删除）
		equipmentCertificateService.deleteRecordsByComparisonId(equipmentComparison.getId());
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
