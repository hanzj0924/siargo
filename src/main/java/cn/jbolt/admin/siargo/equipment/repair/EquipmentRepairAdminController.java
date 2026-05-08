package cn.jbolt.admin.siargo.equipment.repair;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentRepair;
/**
 * 设备维护记录 Controller
 * @ClassName: EquipmentRepairAdminController
 * @author: hanzj
 * @date: 2026-05-06 17:28
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/equipment/repair", viewPath = "/_view/admin/siargo/equipment/repair")
//true
public class EquipmentRepairAdminController extends JBoltBaseController {

	@Inject
	private EquipmentRepairService service;
	
   /**
	* 首页
	*/
	public void index() {
		render("index.html");
	}
  	
  	/**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords()));
	}
	
   /**
	* 新增
	*/
	public void add() {
		set("equipmentId", getLong("equipmentId"));
		set("comparisonId", getLong("comparisonId"));
		set("equipmentRepair", new EquipmentRepair());
		render("add.html");
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		EquipmentRepair equipmentRepair=service.findById(getLong(0)); 
		if(equipmentRepair == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("equipmentRepair",equipmentRepair);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(EquipmentRepair.class, "equipmentRepair")));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(EquipmentRepair.class, "equipmentRepair")));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
	
}
