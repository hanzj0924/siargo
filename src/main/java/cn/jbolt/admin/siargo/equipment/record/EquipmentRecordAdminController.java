package cn.jbolt.admin.siargo.equipment.record;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;

import cn.jbolt.common.util.DateUtil;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentRecord;
import cn.jbolt.siargo.model.EquipmentCertificate;
import cn.jbolt.siargo.model.Equipment;
/**
 * 设备记录 Controller
 * @ClassName: EquipmentRecordAdminController
 * @author: hanzj
 * @date: 2026-04-18 15:45
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/equipment/record", viewPath = "/_view/admin/siargo/equipment/record")
//true
public class EquipmentRecordAdminController extends JBoltBaseController {

	@Inject
	private EquipmentRecordService service;
	
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
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),getLong("batchId")));
	}
	
   /**
	* 新增
	*/
	public void add() {
		Long equipmentId = getLong("equipmentId");
		Long batchId = getLong("batchId");
		set("equipmentId", equipmentId);
		set("batchId", batchId);
		EquipmentRecord equipmentRecord = new EquipmentRecord();
		equipmentRecord.set("record_date", DateUtil.getDateString(DateUtil.YMDHMS));
		set("equipmentRecord", equipmentRecord);
		set("equipmentCertificate", new EquipmentCertificate());
		set("certificateImageUrls", "");
		if (equipmentId != null) {
			Equipment equipment = new Equipment().dao().findById(equipmentId);
			set("equipment", equipment != null ? equipment : new Equipment());
		} else {
			set("equipment", new Equipment());
		}
		render("add.html");
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		EquipmentRecord equipmentRecord = service.findById(getLong(0));
		if(equipmentRecord == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("equipmentRecord", equipmentRecord);
		// 查询关联的证书信息（含证书日期/备注）
		EquipmentCertificate cert = service.findCertificateByRecordId(equipmentRecord.getId());
		set("equipmentCertificate", cert != null ? cert : new EquipmentCertificate());
		// 查询关联证书图片URL（逗号分隔，供上传器回显）
		set("certificateImageUrls", service.findCertificateImageUrlsByRecordId(equipmentRecord.getId()));
		// 查询关联的设备信息
		Equipment equipment = new Equipment().dao().findById(equipmentRecord.getEquipmentId());
		set("equipment", equipment != null ? equipment : new Equipment());
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		EquipmentRecord record = getModel(EquipmentRecord.class, "equipmentRecord");
		String imageUrls = get("certificateImageUrls");
		String certDate = get("equipmentCertificate.certificateDate");
		String certRemark = get("equipmentCertificate.remark");
		renderJson(service.saveWithCertificate(record, imageUrls, certDate, certRemark));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		EquipmentRecord record = getModel(EquipmentRecord.class, "equipmentRecord");
		String imageUrls = get("certificateImageUrls");
		String certDate = get("equipmentCertificate.certificateDate");
		String certRemark = get("equipmentCertificate.remark");
		renderJson(service.updateWithCertificate(record, imageUrls, certDate, certRemark));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
	
}
