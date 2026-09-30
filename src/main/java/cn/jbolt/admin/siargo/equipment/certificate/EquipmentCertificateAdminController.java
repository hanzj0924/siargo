package cn.jbolt.admin.siargo.equipment.certificate;

import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.common.storage.SiargoUploadFiles;
import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.upload.UploadFile;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentCertificate;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.admin.siargo.equipment.EquipmentService;
/**
 * 设备管理证书记录 Controller
 * @ClassName: EquipmentCertificateAdminController
 * @author: hanzj
 * @date: 2026-04-20
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/equipment/certificate", viewPath = "/_view/admin/siargo/equipment/certificate")
//true
public class EquipmentCertificateAdminController extends JBoltBaseController {

	@Inject
	private EquipmentCertificateService service;
	@Inject
	private EquipmentService equipmentService;

	/**
	 * 上传证书图片到临时目录（由记录表单调用）
	 */
    public void uploadImages() {
        SiargoStorage storage = SiargoStorage.forBusiness(SiargoStorage.Business.EQCERT);
        try {
            List<UploadFile> files = getFiles(SiargoUploadFiles.newUploadDirectory(storage));
            if (files == null || files.isEmpty()) { renderJsonFail("请选择图片后上传"); return; }
            for (UploadFile file : files) if (notImage(file)) { renderJsonFail("仅支持图片文件"); return; }
            List<String> urls = new ArrayList<>(), names = new ArrayList<>();
            for (UploadFile file : files) {
                urls.add(SiargoUploadFiles.accept(storage, file));
                names.add(service.getFileName(storage.resolveUrl(urls.get(urls.size() - 1)).toFile()));
            }
            renderJsonData(urls, "");
        } catch (Exception e) { renderJsonFail("上传失败：" + e.getMessage()); }
    }

	/**
	 * 按设备ID查看证书（设备列表"查看"按钮调用）
	 */
	public void viewByEquipment() {
		Long equipmentId = getLong("equipmentId");
		if (notOk(equipmentId)) { renderFail(JBoltMsg.PARAM_ERROR); return; }
		List<EquipmentCertificate> certs = service.findByEquipmentId(equipmentId);
		set("certs", certs);
		render("/_view/admin/siargo/equipment/certificates.html");
	}

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
		renderJsonData(service.paginateAdminDatas(getPageNumber(), getPageSize(), getKeywords()));
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(EquipmentCertificate.class, "equipmentCertificate")));
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(EquipmentCertificate.class, "equipmentCertificate")));
	}

	/**
	 * 批量删除
	 * <p>Db.tx() 手动事务 + afterCommit：物理文件删除不可回滚，
	 * 先在事务外收集路径，事务内仅删 DB，提交后统一删除文件并清缓存</p>
	 */
	public void deleteByIds() {
		String idsJson = get("ids");
		if (StrKit.isBlank(idsJson)) {
			renderFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Long[] ids = getIdsToLongArray();
		// 1. 事务外收集待删证书的物理文件路径（文件删除不可回滚）
		List<String> filePaths = service.queryFilePathsByIds(ids != null ? Arrays.asList(ids) : new ArrayList<>());
		// 2. 事务内仅删 DB 记录
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
		service.deletePhysicalFiles(filePaths);
		equipmentService.clearOverviewCountsCache();
		renderJson(retHolder[0] != null ? retHolder[0] : Ret.fail("删除失败"));
	}

	/**
	 * 批量删除临时文件（取消上传或关闭弹窗时由前端调用）
	 */
    public void deleteTempFiles() {
        String urls = getPara("filePaths");
        renderJson(SiargoUploadFiles.delete(SiargoStorage.forBusiness(SiargoStorage.Business.EQCERT),
                urls == null ? java.util.List.of() : java.util.Arrays.stream(urls.split(",")).map(String::trim).filter(v -> !v.isEmpty()).toList(), true));
    }

}
