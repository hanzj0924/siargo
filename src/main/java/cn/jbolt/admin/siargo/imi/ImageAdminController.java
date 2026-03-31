package cn.jbolt.admin.siargo.imi;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Image;

/**
 * 来料到货单管理 Controller
 *
 * @ClassName: ImageAdminController
 * @author: hanzj
 * @date: 2026-01-30 16:19
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/imi", viewPath = "/_view/admin/siargo/imi")
public class ImageAdminController extends JBoltBaseController {

	@Inject
	private ImageService service;

	/**
	 * 上传图片到临时目录，返回临时路径列表。
	 * <p>
	 * 不做实际保存，仅将文件重命名为原始文件名并返回路径，
	 * 由前端确认后再调用 save() 正式入库。
	 */
	public void uploadImages() {
		boolean save = getParaToBoolean("save");
		// 统一使用 "/" 分隔符，避免 Windows 下 File.separator 导致路径不一致
		String tempUploadPath = JBoltUploadFolder.SIARGO_UPLOAD_IMI + "/temp/";
		List<UploadFile> files = getFiles(tempUploadPath);
		if (files == null || files.isEmpty()) {
			renderJsonFail("请选择图片后上传");
			return;
		}

		// 校验文件类型
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
		//收集所有文件名，而非只保留最后一个
		List<String> fileNames = new ArrayList<>();
		StringBuilder errormsg = new StringBuilder();
		String tempPathPrefix = "/upload/";

		for (UploadFile uploadFile : files) {
			try {
				String originalFileName = uploadFile.getOriginalFileName();
				String fileName = (originalFileName != null && !originalFileName.isEmpty())
						? originalFileName : uploadFile.getFileName();

				File currentFile = uploadFile.getFile();
				File targetFile  = new File(currentFile.getParent(), fileName);

				if (!currentFile.renameTo(targetFile)) {
					errormsg.append("文件 ").append(uploadFile.getFileName()).append(" 重命名失败;");
					// 重命名失败时仍使用原文件名路径，保证后续流程可继续
					targetFile = currentFile;
				}

				retFiles.add(tempPathPrefix + tempUploadPath + targetFile.getName());
				fileNames.add(service.getFileName(targetFile));
			} catch (Exception e) {
				errormsg.append("文件 ").append(uploadFile.getFileName())
						.append(" 处理失败: ").append(e.getMessage()).append(";");
			}
		}

		if (retFiles.isEmpty()) {
			renderJsonFail(errormsg.toString());
			return;
		}

		if (save) {
			renderJsonData(retFiles, errormsg.toString());
		} else {
			Map<String, Object> result = new HashMap<>();
			result.put("filesName", fileNames);   // 返回所有文件名列表
			result.put("files",     retFiles);
			result.put("message",   errormsg.toString());
			renderJsonData(result);
		}
	}

	/** 首页 */
	public void index() {
		render("index.html");
	}

	/** 数据源 */
	public void datas() {
		renderJsonData(service.paginateAdminDatas(
				getPageNumber(), getPageSize(), getKeywords(),
				getPara("supplierId"), getPara("yearMonth")));
	}

	/** 新增页面 */
	public void add() {
		render("add.html");
	}

	/** 编辑页面 */
	public void edit() {
		Image image = service.findById(getLong(0));
		if (image == null) {
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("image", image);
		render("edit.html");
	}

	/** 保存（批量） */
	public void save() {
		Image image = getModel(Image.class, "image");
		String uploadPathJson = getPara("imgUploadUrl");
		List<String> uploadPaths = Arrays.stream(uploadPathJson.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());

		renderJson(service.saveBatch(image, uploadPaths));
	}

	/** 更新 */
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Image.class, "image")));
	}

	/**
	 * 批量删除（逗号分隔的 ID）。
	 * <p>
	 * 检查每条删除结果，任一失败则整体返回失败。
	 */
	@Before(Tx.class)
	public void deleteByIds() {
		String idsJson = getPara("ids");
		List<Long> ids = Arrays.stream(idsJson.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(Long::parseLong)
				.collect(Collectors.toList());

		for (Long id : ids) {
			Ret ret = service.delete(id);
			if (ret.isFail()) {
				renderJson(ret);
				return;
			}
		}
		renderJsonSuccess();
	}

	/**
	 * 批量删除（框架 deleteByIds 方式）
	 */
	@Before(Tx.class)
	public void deleteByIds1() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}

	/**
	 * 删除临时文件。
	 * <p>
	 * 当前端上传组件删除某个文件时，同步删除 temp 目录中的临时文件。
	 * 前端传参示例：/admin/siargo/imi/deleteTempFile?filePath=/upload/siargo_imi/temp/xxx.jpg
	 */
	public void deleteTempFile() {
		String filePath = getPara("filePath");
		if (filePath == null || filePath.isEmpty()) {
			renderJsonFail("文件路径不能为空");
			return;
		}

		// 先统一路径分隔符，再做安全校验
		String normalizedPath = filePath.replace("\\", "/");

		// 安全校验：只允许删除 temp 目录下的文件，防止路径穿越攻击
		if (!normalizedPath.contains("/temp/")) {
			renderJsonFail("只能删除临时目录下的文件");
			return;
		}
		File file = new File(service.getWebRootPath() + normalizedPath);

		if (!file.exists()) {
			// 文件不存在，可能已被删除或路径错误，直接返回成功
			renderJsonSuccess("文件不存在，已跳过");
			return;
		}

		boolean deleted = file.delete();
		if (deleted) {
			renderJsonSuccess("临时文件已删除");
		} else {
			renderJsonFail("删除失败，请重试");
		}
	}

	/**
	 * 批量删除临时文件。
	 * <p>
	 * 接收逗号分隔的多个文件路径，同时删除多个 temp 目录下的临时文件。
	 */
	public void deleteTempFiles() {
		String filePathsJson = getPara("filePaths");
		if (filePathsJson == null || filePathsJson.isEmpty()) {
			renderJsonFail("文件路径不能为空");
			return;
		}

		List<String> paths = Arrays.stream(filePathsJson.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());

		int successCount = 0;
		int failCount = 0;
		List<String> failedFiles = new ArrayList<>();

		for (String filePath : paths) {
			// 先统一路径分隔符，再做安全校验
			String normalizedPath = filePath.replace("\\", "/");

			// 安全校验：只允许删除 temp 目录下的文件
			if (!normalizedPath.contains("/temp/")) {
				failCount++;
				failedFiles.add(filePath);
				continue;
			}
			File file = new File(service.getWebRootPath() + normalizedPath);

			if (!file.exists()) {
				// 文件不存在视为删除成功
				successCount++;
				continue;
			}

			if (file.delete()) {
				successCount++;
			} else {
				failCount++;
				failedFiles.add(filePath);
			}
		}

		Map<String, Object> result = new HashMap<>();
		result.put("successCount", successCount);
		result.put("failCount", failCount);
		result.put("failedFiles", failedFiles);

		if (failCount > 0) {
			renderJsonData(result, "部分文件删除失败");
		} else {
			renderJsonData(result, "所有临时文件已删除");
		}
	}
}
