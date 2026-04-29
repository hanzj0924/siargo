package cn.jbolt.admin.siargo.equipment.inspectionBatch;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentInspectionBatch;
import cn.jbolt.siargo.model.Equipment;
import cn.jbolt.core.kit.JBoltUserKit;
import java.util.Date;
/**
 * 检校批次记录 Controller
 * @ClassName: EquipmentInspectionBatchAdminController
 * @author: hanzj
 * @date: 2026-04-26 14:43
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/equipment/inspectionBatch", viewPath = "/_view/admin/siargo/equipment/inspectionBatch")
//true
public class EquipmentInspectionBatchAdminController extends JBoltBaseController {

	@Inject
	private EquipmentInspectionBatchService service;
	
   /**
	* 首页
	*/
	public void index() {
		Long equipmentId = getLong("equipmentId");
		if (equipmentId == null) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Equipment equipment = new Equipment().dao().findById(equipmentId);
		if (equipment == null) {
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("equipmentId", equipmentId);
		set("equipment", equipment);
		render("index.html");
	}
  	
  	/**
	* 数据源
	*/
	public void datas() {
		Long equipmentId = getLong("equipmentId");
		if (equipmentId != null) {
			renderJsonData(service.paginateByEquipmentId(getPageNumber(), getPageSize(), equipmentId));
		} else {
			renderJsonData(service.paginateAdminDatas(getPageNumber(), getPageSize(), getKeywords()));
		}
	}
	
   /**
	* 新增
	*/
	public void add() {
		set("equipmentId", getLong("equipmentId"));
		set("isAdd", true);
		render("add.html");
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		EquipmentInspectionBatch equipmentInspectionBatch=service.findById(getLong(0)); 
		if(equipmentInspectionBatch == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("equipmentInspectionBatch",equipmentInspectionBatch);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		EquipmentInspectionBatch batch = getModel(EquipmentInspectionBatch.class, "equipmentInspectionBatch");
		batch.setCreatorId(JBoltUserKit.getUserId());
		batch.setCreatorTime(new Date());
		batch.setAuditStatus(1);
		renderJson(service.save(batch));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(EquipmentInspectionBatch.class, "equipmentInspectionBatch")));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}

	/**
	* 审核单个批次
	*/
	@Before(Tx.class)
	public void audit() {
		Long id = getLong(0);
		service.audit(id, JBoltUserKit.getUserId());
		renderJsonSuccess();
	}

	/**
	* 批量审核
	*/
	@Before(Tx.class)
	public void batchAudit() {
		String ids = get("ids");
		service.batchAudit(ids, JBoltUserKit.getUserId());
		renderJsonSuccess();
	}
	

}
