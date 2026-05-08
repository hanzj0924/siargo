package cn.jbolt.admin.siargo.equipment.comparison;

import java.util.List;
import java.util.stream.Collectors;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentComparison;
import cn.jbolt.siargo.model.EquipmentCertificate;
import cn.jbolt.admin.siargo.equipment.certificate.EquipmentCertificateService;
/**
 * 检校批次记录 Controller
 * @ClassName: EquipmentComparisonAdminController
 * @author: hanzj
 * @date: 2026-05-06 17:25
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/equipment/comparison", viewPath = "/_view/admin/siargo/equipment/comparison")
//true
public class EquipmentComparisonAdminController extends JBoltBaseController {

	@Inject
	private EquipmentComparisonService service;
	@Inject
	private EquipmentCertificateService equipmentCertificateService;
	
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
		set("equipmentComparison", new EquipmentComparison());
		set("equipmentCertificate", new EquipmentCertificate());
		render("add.html");
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		EquipmentComparison equipmentComparison=service.findById(getLong(0)); 
		if(equipmentComparison == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("equipmentComparison",equipmentComparison);
		// 查询关联证书，拼接图片URL
		Long comparisonId = equipmentComparison.getId();
		List<EquipmentCertificate> certs = equipmentCertificateService.findByComparisonId(comparisonId);
		if(certs != null && !certs.isEmpty()) {
			String certificateImageUrls = certs.stream()
				.map(c -> c.getStr("image_url"))
				.filter(url -> url != null && !url.isEmpty())
				.collect(Collectors.joining(","));
			set("certificateImageUrls", certificateImageUrls);
			set("equipmentCertificate", certs.get(0));
		}
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		String certificateImageUrls = getPara("certificateImageUrls");
		String certificateDate = getPara("equipmentCertificate.certificateDate");
		String certificateRemark = getPara("equipmentCertificate.remark");
		renderJson(service.save(getModel(EquipmentComparison.class, "equipmentComparison"), certificateImageUrls, certificateDate, certificateRemark));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		String certificateImageUrls = getPara("certificateImageUrls");
		String certificateDate = getPara("equipmentCertificate.certificateDate");
		String certificateRemark = getPara("equipmentCertificate.remark");
		renderJson(service.update(getModel(EquipmentComparison.class, "equipmentComparison"), certificateImageUrls, certificateDate, certificateRemark));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
	/**
	* 批量审核
	*/
	@Before(Tx.class)
	public void batchAudit() {
		renderJson(service.batchAudit(get("ids")));
	}
	

}
