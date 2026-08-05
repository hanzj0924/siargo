package cn.jbolt.admin.siargo.equipment;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.admin.siargo.equipment.certificate.EquipmentCertificateService;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Equipment;
import cn.jbolt.siargo.model.EquipmentCertificate;
import com.jfinal.plugin.activerecord.Record;
import com.alibaba.fastjson.JSON;
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
	/** 证书服务 */
	@Inject
	private EquipmentCertificateService certificateService;
	/** 角色服务 */
	@Inject
	private RoleService roleService;
	
   /**
	* 首页
	*/
	public void index() {
		Long userId = JBoltUserKit.getUserId();
		
		// 设备审核权限：管理员/设备 角色可覆盖，或直接拥有设备审核角色
		set("audit", roleService.hasRoleOrAbove(userId, 221));
		
		// 动态分类列表（从字典表查询，支持任意数量分类）
		List<Record> categories = service.getCategories();
		set("categories", categories);
		set("categoriesJson", JSON.toJSONString(categories));
		
		render("index.html");
	}
  	
  	/**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),get("category"),get("filter"),get("status"),get("inspectionMethod"),get("inspectionCycle")));
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
		String ids = get("ids");
		List<Record> equipmentList = service.findByIdsForBatch(ids);
		if (equipmentList == null || equipmentList.isEmpty()) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		set("ids", ids);
		set("equipmentList", equipmentList);
		set("equipmentListJson", JSON.toJSONString(equipmentList));
		render("batchInspection.html");
	}
	
	/**
	* 批量定期对比
	*/
	@Before(Tx.class)
	public void batchInspection() {
		renderJson(service.batchInspection(get("ids"),getParaToDate("lastInspectionDate"),getParaToDate("nextInspectionDate"),getInt("status")));
	}
	
	/**
	* 批量审核
	*/
	@Before(Tx.class)
	public void batchAudit() {
		renderJson(service.batchAudit(get("ids")));
	}
	
	/**
	* 设备对比记录页面
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
	* 设备对比记录数据源
	*/
	public void recordDatas() {
		Long equipmentId = getLong("equipmentId");
		if (equipmentId == null) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		renderJsonData(service.paginateRecordDatas(getPageNumber(), getPageSize(), equipmentId));
	}
	
	/**
	* 查看证书
	*/
	public void certificates() {
		Long comparisonId = getLong("comparisonId");
		if (notOk(comparisonId)) { renderFail(JBoltMsg.PARAM_ERROR); return; }
		List<EquipmentCertificate> certs = certificateService.findByComparisonId(comparisonId);
		set("certs", certs);
		set("comparisonId", comparisonId);
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
		String ids = get("ids");
		List<Record> equipmentList = service.findByIdsForBatch(ids);
		if (equipmentList == null || equipmentList.isEmpty()) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		set("ids", ids);
		set("equipmentList", equipmentList);
		set("equipmentListJson", JSON.toJSONString(equipmentList));
		render("batchStatus.html");
	}
	
	/**
	* 批量更改状态
	*/
	@Before(Tx.class)
	public void batchStatus() {
		renderJson(service.batchStatus(get("ids"), getInt("status")));
	}
	
	/**
	 * 设备时间线页面
	 */
	public void timeline() {
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
		set("equipment", equipment);
		render("timeline/index.html");
	}
	
	/**
	 * 时间线数据源 - 合并对比+维修记录按日期倒序
	 */
	public void timelineDatas() {
		Long equipmentId = getLong("equipmentId");
		if (equipmentId == null) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		renderJsonData(service.paginateTimelineDatas(getPageNumber(), getPageSize(), equipmentId, getInt("type")));
	}

	/**
	 * 检查设备是否有待审核数据
	 */
	public void checkPendingAudit() {
		Long equipmentId = getLong("equipmentId");
		if (equipmentId == null) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		renderJsonData(service.hasPendingAudit(equipmentId));
	}

	/**
	 * 获取设备当前状态
	 */
	public void getStatus() {
		Long equipmentId = getLong("equipmentId");
		if (equipmentId == null) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Integer status = service.getEquipmentStatus(equipmentId);
		if (status == null) {
			renderFail("设备不存在或状态异常");
			return;
		}
		renderJsonData(status);
	}

}
