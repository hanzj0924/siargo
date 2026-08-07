package cn.jbolt.admin.siargo.equipment.comparison;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.kit.StrKit;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentComparison;
import cn.jbolt.siargo.model.EquipmentCertificate;
import cn.jbolt.admin.siargo.equipment.certificate.EquipmentCertificateService;
import cn.jbolt.admin.siargo.equipment.EquipmentService;
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
	@Inject
	private EquipmentService equipmentService;
	
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
	* <p>Db.tx() 手动事务 + afterCommit：级联删除关联证书记录时，
	* 物理文件删除不可回滚，事务外收集路径、事务内仅删 DB、提交后统一删除</p>
	*/
	public void deleteByIds() {
		String idsJson = get("ids");
		if (StrKit.isBlank(idsJson)) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Long[] ids = getIdsToLongArray();
		// 1. 事务外收集对比记录关联证书的物理文件路径
		List<String> filePaths = equipmentCertificateService.queryFilePathsByComparisonIds(ids != null ? Arrays.asList(ids) : new ArrayList<>());
		// 2. 事务内删 DB（对比记录 + 级联证书记录）
		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.deleteByBatchIds(idsJson);
			return retHolder[0] != null && retHolder[0].isOk();
		});
		if (!txOk) {
			renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "删除失败");
			return;
		}
		// 3. afterCommit: 删除物理文件 + 清概览缓存
		equipmentCertificateService.deletePhysicalFiles(filePaths);
		equipmentService.clearOverviewCountsCache();
		renderJson(retHolder[0] != null ? retHolder[0] : Ret.fail("删除失败"));
	}
	
	/**
	* 批量审核
	* <p>Db.tx() 手动事务：逐条审核，中途 Ret.fail 需整体回滚（@Before(Tx.class) 不认 Ret.fail）</p>
	*/
	public void batchAudit() {
		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.batchAudit(get("ids"));
			return retHolder[0] != null && retHolder[0].isOk();
		});
		if (!txOk) {
			renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "批量审核失败");
			return;
		}
		renderJson(retHolder[0] != null ? retHolder[0] : Ret.fail("批量审核失败"));
	}
	

}
