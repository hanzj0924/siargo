package cn.jbolt.admin.siargo.equipment;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.JBoltUserAuthKit;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.core.cache.JBoltRoleCache;
import cn.jbolt.core.model.Role;
import cn.jbolt.admin.siargo.equipment.certificate.EquipmentCertificateService;
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
		set("audit", hasRoleOrAbove(userId, 221));
		
		render("index.html");
	}

	/**
	 * 检查用户是否拥有指定SN的角色或被其上级角色覆盖
	 * 规则：沿 pid 链向上遍历，type=1 的功能角色参与覆盖检查，type=0 的菜单角色跳过
	 *   管理员(sn=1) → 全局覆盖，单独判断
	 *   质检(sn=2, type=0) → 纯菜单入口，不覆盖任何子按钮
	 *   报告单(sn=21, type=1) → 覆盖 精度/外观/包装/批准
	 *   设备(sn=22, type=1) → 覆盖 设备审核
	 * 新增角色只需在 jb_role 中配置正确的 pid 和 type 即可生效，无需修改代码
	 */
	private boolean hasRoleOrAbove(Long userId, int sn) {
		// 管理员拥有全部权限
		Long adminRoleId = roleService.findIdBySn(1);
		if (adminRoleId != null && JBoltUserAuthKit.hasRole(userId, adminRoleId)) {
			return true;
		}
		
		Long roleId = roleService.findIdBySn(sn);
		if (roleId == null) return false;
		
		// 沿 pid 链向上遍历，跳过 type=0 的菜单角色
		Long currentId = roleId;
		while (currentId != null && currentId > 0) {
			Role role = JBoltRoleCache.me.get(currentId);
			if (role == null) break;
			
			Integer type = role.getInt("type");
			// type=1 的功能角色参与覆盖检查
			if (type != null && type == 1 && JBoltUserAuthKit.hasRole(userId, currentId)) {
				return true;
			}
			
			Long pid = role.getPid();
			if (pid == null || pid == 0) break;
			currentId = pid;
		}
		return false;
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
		set("ids", get("ids"));
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
		renderJsonData(service.paginateTimelineDatas(getPageNumber(), getPageSize(), equipmentId));
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
