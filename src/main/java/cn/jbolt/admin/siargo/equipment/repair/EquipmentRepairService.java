package cn.jbolt.admin.siargo.equipment.repair;

import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentRepair;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 设备维护记录 Service
 * @ClassName: EquipmentRepairService   
 * @author: hanzj
 * @date: 2026-05-06 17:28  
 */
public class EquipmentRepairService extends JBoltBaseService<EquipmentRepair> {
	private final EquipmentRepair dao=new EquipmentRepair().dao();
	@Override
	protected EquipmentRepair dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<EquipmentRepair> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("equipment_id","desc", pageNumber, pageSize, keywords, "equipment_id");
	}
	
	/**
	 * 保存
	 * @param equipmentRepair
	 * @return
	 */
	public Ret save(EquipmentRepair equipmentRepair) {
		if(equipmentRepair==null || isOk(equipmentRepair.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		boolean success=equipmentRepair.save();
		return ret(success);
	}

	
	/**
	 * 更新
	 * @param equipmentRepair
	 * @return
	 */
	public Ret update(EquipmentRepair equipmentRepair) {
		if(equipmentRepair==null || notOk(equipmentRepair.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		EquipmentRepair dbEquipmentRepair=findById(equipmentRepair.getId());
		if(dbEquipmentRepair==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(equipmentRepair.getName(), equipmentRepair.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=equipmentRepair.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(equipmentRepair.getId(), JBoltUserKit.getUserId(), equipmentRepair.getName());
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
	 * @param equipmentRepair 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(EquipmentRepair equipmentRepair, Kv kv) {
		// 查询关联的设备名称
		String equipmentName = Db.queryStr("SELECT name FROM siargo_equipment WHERE id = ?", equipmentRepair.getEquipmentId());
		if (equipmentName == null) { equipmentName = ""; }
		// 拼接日志描述：设备名 + 维修日期 + 故障描述
		Date repairDate = equipmentRepair.getDate("repair_date");
		String repairDateStr = repairDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(repairDate) : "";
		String faultDescription = equipmentRepair.getStr("fault_description");
		if (faultDescription == null) { faultDescription = ""; }
		addDeleteSystemLog(equipmentRepair.getId(), JBoltUserKit.getUserId(),
				equipmentName + " " + repairDateStr + " " + faultDescription);
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param equipmentRepair 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(EquipmentRepair equipmentRepair, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(equipmentRepair, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.EQUIPMENTREPAIR.getValue();
	}
	
}