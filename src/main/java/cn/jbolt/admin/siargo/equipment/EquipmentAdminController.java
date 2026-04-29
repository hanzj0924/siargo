package cn.jbolt.admin.siargo.equipment;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.JBoltUserAuthKit;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Equipment;
import cn.jbolt.siargo.model.EquipmentCertificate;
import java.util.List;
/**
 * 设备管理 Controller
 * @ClassName: EquipmentAdminController
 * @author: hanzj
 * @date: 2026-04-18 10:47
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/equipment", viewPath = "/_view/admin/siargo/equipment")
//true
public class EquipmentAdminController extends JBoltBaseController {

	@Inject
	private EquipmentService service;
	/** 角色服务 */
	@Inject
	private RoleService roleService;
	
   /**
	* 首页
	*/
	public void index() {
		Long userId = JBoltUserKit.getUserId();
		
		//管理员权限
		Long adminRoleId = roleService.findIdBySn(1);
		boolean isAdmin = adminRoleId != null && JBoltUserAuthKit.hasRole(userId, adminRoleId);
		
		//审核权限
		Long auditRoleId = roleService.findIdBySn(221);
		set("audit", isAdmin || (auditRoleId != null && JBoltUserAuthKit.hasRole(userId, auditRoleId)));
		render("index.html");
	}
  	
  	/**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),get("category"),get("filter"),get("status")));
	}
	
	/**
	* 获取概览统计数量
	*/
	public void getOverviewCounts() {
		renderJsonData(service.getOverviewCounts());
	}
	
	/**
	* 批量编制表单页
	*/
	public void batchInspectionForm() {
		set("ids", get("ids"));
		render("batchInspection.html");
	}
	
	/**
	* 批量校准
	*/
	@Before(Tx.class)
	public void batchInspection() {
		renderJson(service.batchInspection(get("ids"),getParaToDate("lastInspectionDate"),getParaToDate("nextInspectionDate"),getInt("status"),get("description")));
	}
	
	/**
	* 批量审核
	*/
	@Before(Tx.class)
	public void batchAudit() {
		renderJson(service.batchAudit(get("ids")));
	}
	
	/**
	* 设备维修校准记录页面
	*/
	public void records() {
		Long equipmentId = getLong("equipmentId");
		if (equipmentId == null) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Equipment equipment = service.findById(equipmentId);
		if (equipment == null) {
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("equipmentId", equipmentId);
		set("equipment", equipment);
		render("inspectionBatch/index.html");
	}
	
	/**
	* 设备维修校准记录数据源
	*/
	public void recordDatas() {
		Long equipmentId = getLong("equipmentId");
		if (equipmentId == null) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Long batchId = getLong("batchId");
		renderJsonData(service.paginateRecordDatas(getPageNumber(), getPageSize(), equipmentId, batchId));
	}
	
	/**
	* 查看证书
	*/
	public void certificates() {
		Long recordId = getLong("recordId");
		if (notOk(recordId)) { renderFail(JBoltMsg.PARAM_ERROR); return; }
		List<EquipmentCertificate> certs = new EquipmentCertificate().dao()
			.find("SELECT * FROM siargo_equipment_certificate WHERE record_id = ? ORDER BY certificate_date DESC", recordId);
		set("certs", certs);
		set("recordId", recordId);
		render("certificates.html");
	}
	
   /**
	* 新增
	*/
	public void add() {
		set("equipment", new Equipment());
		render("add.html");
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		Equipment equipment=service.findById(getLong(0)); 
		if(equipment == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("equipment",equipment);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Equipment.class, "equipment"), get("certificateImageUrls")));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Equipment.class, "equipment")));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
	/**
	* 批量更改状态表单页
	*/
	public void batchStatusForm() {
		set("ids", get("ids"));
		render("batchStatus.html");
	}
	
	/**
	* 批量更改状态
	*/
	@Before(Tx.class)
	public void batchStatus() {
		renderJson(service.batchStatus(get("ids"), getInt("status")));
	}
	

}
