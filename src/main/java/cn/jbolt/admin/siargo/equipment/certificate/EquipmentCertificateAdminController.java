package cn.jbolt.admin.siargo.equipment.certificate;

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

	/**
	 * 上传证书图片到临时目录（由记录表单调用）
	 */
	public void uploadImages() {
		String tempUploadPath = JBoltUploadFolder.SIARGO_UPLOAD_EQUIPMENT_CERTIFICATE + "/temp/";
		List<UploadFile> files = getFiles(tempUploadPath);
		if (files == null || files.isEmpty()) {
			renderJsonFail("请选择图片后上传");
			return;
		}
		StringBuilder typeErrMsg = new StringBuilder();
		files.forEach(file -> {
			if (notImage(file)) {
				typeErrMsg.append(file.getOriginalFileName()).append(" 不是图片类型文件;");
			}
		});
		if (typeErrMsg.length() > 0) {
			renderJsonFail(typeErrMsg.toString());
			return;
		}
		List<String> retFiles = new ArrayList<>();
		StringBuilder errormsg = new StringBuilder();
		String tempPathPrefix = "/upload/";
		for (UploadFile uploadFile : files) {
			try {
				String originalFileName = uploadFile.getOriginalFileName();
				String fileName = (originalFileName != null && !originalFileName.isEmpty())
						? originalFileName : uploadFile.getFileName();
				File currentFile = uploadFile.getFile();
				File targetFile = new File(currentFile.getParent(), fileName);
				if (!currentFile.renameTo(targetFile)) {
					errormsg.append("文件 ").append(uploadFile.getFileName()).append(" 重命名失败;");
					targetFile = currentFile;
				}
				retFiles.add(tempPathPrefix + tempUploadPath + targetFile.getName());
			} catch (Exception e) {
				errormsg.append("文件 ").append(uploadFile.getFileName())
						.append(" 处理失败: ").append(e.getMessage()).append(";");
			}
		}
		if (retFiles.isEmpty()) {
			renderJsonFail(errormsg.toString());
			return;
		}
		renderJsonData(retFiles, errormsg.toString());
	}

	/**
	 * 按设备ID查看证书（设备列表"查看"按钮调用）
	 */
	public void viewByEquipment() {
		Long equipmentId = getLong("equipmentId");
		if (notOk(equipmentId)) { renderFail(JBoltMsg.PARAM_ERROR); return; }
		List<EquipmentCertificate> certs = new EquipmentCertificate().dao()
			.find("SELECT * FROM siargo_equipment_certificate WHERE equipment_id = ? ORDER BY certificate_date DESC, id DESC", equipmentId);
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
	 */
	@Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}

	/**
	 * 批量删除临时文件（取消上传或关闭弹窗时由前端调用）
	 */
	public void deleteTempFiles() {
		String filePaths = get("filePaths");
		if (filePaths == null || filePaths.trim().isEmpty()) {
			renderJsonSuccess();
			return;
		}
		String webRootPath = service.getWebRootPath();
		String[] paths = filePaths.split(",");
		for (String path : paths) {
			path = path.trim();
			if (path.isEmpty()) continue;
			// 安全校验：只允许删除临时目录下的文件
			try {
				String safePath = service.normalizeTempPath(path);
				File file = new File(webRootPath + safePath);
				if (file.exists() && file.isFile()) {
					file.delete();
				}
			} catch (IllegalArgumentException e) {
				// 非法路径，忽略
			}
		}
		renderJsonSuccess();
	}

}
