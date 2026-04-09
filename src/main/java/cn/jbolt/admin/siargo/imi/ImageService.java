package cn.jbolt.admin.siargo.imi;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

import cn.hutool.core.io.FileUtil;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.common.util.DateUtil;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.siargo.model.Image;

/**
 * 来料到货单管理 Service
 *
 * @ClassName: ImageService
 * @author: hanzj
 * @date: 2026-01-30 16:19
 */
public class ImageService extends JBoltBaseService<Image> {
	private final Image dao = new Image().dao();

	@Override
	protected Image dao() {
		return dao;
	}

	/** 正常状态 */
	public static final int STATUS_NORMAL = 1;
	/** 已删除状态 */
	public static final int STATUS_DELETED = 0;
	/** Web 根目录绝对路径 */
	public static final String webRootPath = PathKit.getWebRootPath();
	/** 统一使用 "/" 作为路径分隔符，避免 Windows File.separator 存入数据库后 Web 访问异常 */
	public static final String localPath = "/upload/" + JBoltUploadFolder.SIARGO_UPLOAD_IMI + "/";

	/**
	 * 获取 Web 根目录路径（供 Controller 调用）
	 */
	public String getWebRootPath() {
		return webRootPath;
	}

	// -------------------------------------------------------------------------
	// 查询
	// -------------------------------------------------------------------------

	/**
	 * 后台管理分页查询
	 *
	 * @param pageNumber  页码
	 * @param pageSize    每页条数
	 * @param keywords    文件名关键字
	 * @param supplierId  供应商ID
	 * @param yearMonth   年月（yyyy-MM）
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize,
			String keywords, String supplierId, String yearMonth) {
		Sql sql = Sql.mysql()
				.select("si.id", "si.supplier_id", "si.storage_name", "si.file_path", "si.md5_hash",
						"si.description", "si.upload_time", "si.updated_time", "si.status", "si.deleted_time",
						"ss.name AS supplier_name",
						"ju_uploader.name AS uploader_name", "ju_update.name AS update_name")
				.page(pageNumber, pageSize)
				.from("siargo_image", "si")
				.leftJoin("siargo_supplier", "ss", "ss.id = si.supplier_id")
				.leftJoin("jb_user", "ju_uploader", "ju_uploader.id = si.uploader_id")
				.leftJoin("jb_user", "ju_update", "ju_update.id = si.update_id")
				.like("si.supplier_id", supplierId)
				.like("si.storage_name", keywords)
				.eq("si.status", STATUS_NORMAL);
		if (StrKit.notBlank(yearMonth)) {
			// yearMonth 格式为 "yyyy-MM"
			String startTime = yearMonth + "-01 00:00:00";
			java.time.YearMonth ym = java.time.YearMonth.parse(yearMonth);
			int lastDay = ym.lengthOfMonth();
			String endTime = yearMonth + "-" + String.format("%02d", lastDay) + " 23:59:59";
			sql.ge("si.upload_time", startTime);
			sql.le("si.upload_time", endTime);
		}
		sql.orderBy("si.upload_time", true);
		return paginateRecord(sql, true);
	}

	// -------------------------------------------------------------------------
	// 保存
	// -------------------------------------------------------------------------

	/**
	 * 保存单张图片（先移动文件，再写数据库，保证一致性）。
	 * <p>
	 * 调用方须保证此方法在同一个数据库事务内执行，以便文件移动失败时可整体回滚。
	 * <p>
	 * 业务流程：
	 * <ol>
	 *   <li>校验临时文件是否存在</li>
     *   <li>计算文件 MD5，检查是否已存在（去重）</li>
     *   <li>移动文件到目标目录（按供应商ID和年月组织）</li>
     *   <li>写入数据库记录</li>
     *   <li>若数据库写入失败，回滚文件（移回临时位置或删除）</li>
	 * </ol>
	 *
	 * @param image    包含 supplierId、description 等元数据
	 * @param tempPath 临时文件相对路径（相对于 webRootPath）
	 * @return Ret，成功时携带 filePath（相对路径，以 "/" 开头）
	 */
	public Ret save(Image image, String tempPath) {
		File tempFile = new File(webRootPath + tempPath);
		if (!tempFile.exists() || !tempFile.isFile()) {
			return fail("临时文件不存在：" + tempPath);
		}

		// 目标目录：localPath + supplierId + / + YYYYMM + /
		String targetDir = localPath + image.getSupplierId() + "/"
				+ DateUtil.getNowStr(DateUtil.YM) + "/";
		File targetFolder = new File(webRootPath + targetDir);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}

		// 计算 MD5（用于去重）
		String md5 = getMd5(tempFile);
		Image existImage = findByMd5(md5);
		if (existImage != null) {
			// 临时文件已无用，清理掉
			tempFile.delete();
			return fail("图片 " + existImage.getStorageName() + " 已经存在！");
		}

		// 目标路径（统一 "/"）
		String targetPath = FileUtil.normalize(targetDir + tempFile.getName());
		File targetFile = new File(webRootPath + targetPath);

		// ① 先移动文件
		try {
			Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return fail("文件移动失败：" + e.getMessage());
		}

		// ② 再写数据库
		Image dbImage = new Image();
		dbImage.set("supplier_id", image.getSupplierId());
		dbImage.set("storage_name", getFileName(tempFile));
		dbImage.set("file_path", targetPath);
		dbImage.set("md5_hash", md5);
		dbImage.set("description", image.getDescription());
		dbImage.set("upload_time", DateUtil.getDateString(DateUtil.YMDHMS));
		dbImage.set("uploader_id", JBoltUserKit.getUserId());
		dbImage.set("status", STATUS_NORMAL);

		boolean success = dbImage.save();
		if (!success) {
			// 数据库写入失败，回滚文件（将文件移回临时位置）
			try {
				Files.move(targetFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				ex.printStackTrace();
				// 回滚文件失败时直接删除目标文件，避免孤立文件
				targetFile.delete();
			}
			return fail("数据保存失败");
		}

		return ret(true).set("filePath", targetPath);
	}

	/**
	 * 批量保存（带事务和文件清理）。
	 * <p>
	 * 任一图片保存失败时，回滚数据库事务，并删除本次已移动到目标位置的所有文件及剩余临时文件。
	 * <p>
	 * 异常回滚逻辑：
	 * <ol>
     *   <li>事务回滚：通过 Db.tx() 自动回滚数据库操作</li>
     *   <li>文件回滚：删除已移动到目标位置的文件</li>
     *   <li>临时文件清理：删除当前及剩余未处理的临时文件</li>
	 * </ol>
	 *
	 * @param image     公共元数据（supplierId、description）
	 * @param tempPaths 临时文件相对路径列表
	 * @return Ret 操作结果，失败时携带错误信息
	 */
	public Ret saveBatch(Image image, List<String> tempPaths) {
		List<String> movedFilePaths = new ArrayList<>();
		final String[] errorMsg = {null};

		boolean txSuccess = Db.tx(() -> {
			for (int i = 0; i < tempPaths.size(); i++) {
				Ret ret;
				try {
					ret = save(image, tempPaths.get(i));
				} catch (Exception e) {
					ret = fail("保存第" + (i + 1) + "张图片时异常：" + e.getMessage());
				}
				if (ret.isFail()) {
					errorMsg[0] = (String) ret.get("msg");
					// 清理已移动到目标位置的文件
					for (String filePath : movedFilePaths) {
						File f = new File(webRootPath + filePath);
						if (f.exists()) f.delete();
					}
					// 清理当前及剩余的临时文件（save() 内部已清理 MD5 重复的临时文件，
					// 这里处理文件移动失败后仍留在 temp 目录的情况）
					for (int j = i; j < tempPaths.size(); j++) {
						File tempFile = new File(webRootPath + tempPaths.get(j));
						if (tempFile.exists()) tempFile.delete();
					}
					return false; // 回滚事务
				}
				movedFilePaths.add((String) ret.get("filePath"));
			}
			return true; // 提交事务
		});

		if (!txSuccess) {
			return fail(errorMsg[0] != null ? errorMsg[0] : "保存失败");
		}
		return ret(true);
	}

	// -------------------------------------------------------------------------
	// 更新
	// -------------------------------------------------------------------------

	/**
	 * 更新图片记录。
	 * <p>
	 * 文件操作策略：先完成所有文件操作，再更新数据库，保证一致性。
	 * 若数据库更新失败，尝试将文件回滚到原路径。
	 * <p>
	 * 支持三种业务场景：
	 * <ul>
     *   <li>场景A：上传了新文件 - 移动新文件、删除旧文件、更新 MD5</li>
     *   <li>场景B：未换文件但供应商或文件名变更 - 移动/重命名文件</li>
     *   <li>场景C：仅改备注 - 不做文件操作</li>
	 * </ul>
	 *
	 * @param image 前端传入的更新数据
	 * @return Ret 操作结果
	 */
	public Ret update(Image image) {
		if (image == null || notOk(image.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}

		Image dbImage = findById(image.getId());
		if (dbImage == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}

		// 使用 Objects.equals 防止 NPE（#9）
		boolean supplierChanged   = !Objects.equals(image.getSupplierId(),   dbImage.getSupplierId());
		boolean storageNameChanged = !Objects.equals(image.getStorageName(), dbImage.getStorageName());
		// fileChanged：前端传来的路径与数据库不同，说明上传了新的临时文件
		boolean fileChanged = !Objects.equals(image.getFilePath(), dbImage.getFilePath());

		String finalFilePath = dbImage.getFilePath();
		String finalMd5      = dbImage.getMd5Hash();

		// 用于回滚：记录文件操作前的旧路径
		String rollbackOldPath = null;
		String rollbackNewPath = null;

		if (fileChanged) {
			// ── 场景A：上传了新文件 ──────────────────────────────────────────
			File newFile = new File(webRootPath + image.getFilePath());
			if (!newFile.exists() || !newFile.isFile()) {
				return fail("新文件不存在，请重新上传");
			}

			// 计算新文件 MD5，检查重复（排除自身）
			String newMd5 = getMd5(newFile);
			Image existImage = dao.findFirst(
					"SELECT * FROM siargo_image WHERE md5_hash = ? AND status = ? AND id != ?",
					newMd5, STATUS_NORMAL, image.getId());
			if (existImage != null) {
				return fail("图片 " + existImage.getStorageName() + " 已经存在！");
			}

			// 目标目录
			String targetDir = localPath + image.getSupplierId() + "/"
					+ DateUtil.getNowStr(DateUtil.YM) + "/";
			File targetFolder = new File(webRootPath + targetDir);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}

			// 目标文件名：storageName + 新文件扩展名
			String extension     = getExtensionWithDotApacheCommons(newFile);
			String targetFileName = image.getStorageName() + extension;
			String targetPath    = FileUtil.normalize(targetDir + targetFileName);

			// ① 先移动新文件
			try {
				Files.move(newFile.toPath(), Paths.get(webRootPath + targetPath),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				return fail("文件移动失败：" + e.getMessage());
			}

			// ② 删除旧文件
			File oldFile = new File(webRootPath + dbImage.getFilePath());
			if (oldFile.exists()) {
				oldFile.delete();
			}

			rollbackOldPath = targetPath;
			rollbackNewPath = dbImage.getFilePath(); // 旧文件已删，回滚时只能删新文件
			finalFilePath   = targetPath;
			finalMd5        = newMd5;

		} else if (supplierChanged || storageNameChanged) {
			// ── 场景B：未换文件，但供应商或文件名变了 ──────────────────────
			File oldFile = new File(webRootPath + dbImage.getFilePath());
			if (!oldFile.exists()) {
				return fail("原始文件不存在，无法重命名/移动");
			}
			String oldExtension = getExtensionWithDotApacheCommons(oldFile);

			// 目标目录
			String targetDir;
			if (supplierChanged) {
				targetDir = localPath + image.getSupplierId() + "/"
						+ DateUtil.getNowStr(DateUtil.YM) + "/";
			} else {
				// 供应商未变，保持原目录（统一为 "/" 分隔符）
				String parentAbs = oldFile.getParentFile().getAbsolutePath();
				String parentRel = parentAbs.substring(webRootPath.length())
						.replace(File.separator, "/");
				if (!parentRel.endsWith("/")) parentRel += "/";
				targetDir = parentRel;
			}

			File targetFolder = new File(webRootPath + targetDir);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}

			// 目标文件名
			String targetFileName = storageNameChanged
					? image.getStorageName() + oldExtension
					: oldFile.getName();
			String targetPath = FileUtil.normalize(targetDir + targetFileName);

			// 先移动文件
			try {
				Files.move(oldFile.toPath(), Paths.get(webRootPath + targetPath),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				return fail("文件移动失败：" + e.getMessage());
			}

			rollbackOldPath = targetPath;
			rollbackNewPath = dbImage.getFilePath();
			finalFilePath   = targetPath;
			// MD5 不变，保留 dbImage 的 md5_hash
		}
		// ── 场景C：仅改备注，不做文件操作 ──────────────────────────────────

		// 处理软删除时间
		if (image.getStatus() == STATUS_DELETED) {
			image.set("deleted_time", DateUtil.getDateString(DateUtil.YMDHMS));
		}

		// 再更新数据库
		image.set("file_path",     FileUtil.normalize(finalFilePath));
		image.set("md5_hash",      finalMd5);
		image.set("description",   image.getDescription());
		image.set("updated_time",  DateUtil.getDateString(DateUtil.YMDHMS));
		image.set("update_id",     JBoltUserKit.getUserId());

		boolean success = image.update();
		if (!success && rollbackOldPath != null) {
			// 数据库更新失败，尝试将文件回滚到原路径
			try {
				Files.move(Paths.get(webRootPath + rollbackOldPath),
						Paths.get(webRootPath + rollbackNewPath),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				ex.printStackTrace();
				// 回滚失败时记录日志，人工介入
				System.err.println("[ImageService] 文件回滚失败，需人工处理: "
						+ rollbackOldPath + " -> " + rollbackNewPath);
			}
			return fail("数据更新失败");
		}
		return ret(success);
	}

	// -------------------------------------------------------------------------
	// 删除
	// -------------------------------------------------------------------------

	/**
	 * 软删除单条记录：先更新数据库状态，再删除文件，避免文件删除失败导致数据不一致。
	 * <p>
	 * 业务场景：
	 * <ol>
     *   <li>将记录状态更新为已删除（STATUS_DELETED），记录删除时间</li>
     *   <li>删除物理文件（失败时仅记录日志，不影响业务返回）</li>
	 * </ol>
	 *
	 * @param id 图片ID
	 * @return Ret 操作结果
	 */
	public Ret delete(Long id) {
		Image dbImage = findById(id);
		if (dbImage == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}

		// 先更新数据库状态
		dbImage.set("deleted_time", DateUtil.getDateString(DateUtil.YMDHMS));
		dbImage.set("status", STATUS_DELETED);
		boolean success = dbImage.update();
		if (!success) {
			return fail("删除失败");
		}

		// 再删除文件（失败时记录日志，不影响业务返回）
		File oldFile = new File(webRootPath + dbImage.getFilePath());
		if (oldFile.exists()) {
			boolean deleted = oldFile.delete();
			if (!deleted) {
				System.err.println("[ImageService] 文件删除失败，需人工清理: " + dbImage.getFilePath());
			}
		}

		return ret(true);
	}

	/**
	 * 批量删除（逗号分隔的 ID 字符串）
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids, true);
	}

	// -------------------------------------------------------------------------
	// 工具方法
	// -------------------------------------------------------------------------

	/**
	 * 计算文件的 MD5 值
	 *
	 * @param file 文件对象
	 * @return MD5 十六进制字符串
	 * @throws RuntimeException 文件读取失败时抛出
	 */
	public String getMd5(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			return DigestUtils.md5Hex(fis);
		} catch (IOException e) {
			throw new RuntimeException("计算文件MD5失败", e);
		}
	}

	/**
	 * 获取文件名（不含扩展名）
	 *
	 * @param file 文件对象
	 * @return 不含扩展名的文件名
	 */
	public String getFileName(File file) {
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
	}

	/**
	 * 获取文件扩展名（含点，如 ".jpg"）
	 *
	 * @param file 文件对象
	 * @return 带点的扩展名，无扩展名时返回空字符串
	 */
	public String getExtensionWithDotApacheCommons(File file) {
		if (file == null) return "";
		String extension = FilenameUtils.getExtension(file.getName());
		return extension.isEmpty() ? "" : "." + extension;
	}

	/**
	 * 重命名文件，返回新的相对路径（以 "/" 分隔）
	 *
	 * @param oldFilePath 原文件相对路径（相对于 webRootPath）
	 * @param newName     新文件名（不含扩展名）
	 * @return 新文件相对路径；重命名失败返回 null
	 * @throws RuntimeException 目标文件已存在时抛出
	 */
	public String getRenameFilePath(String oldFilePath, String newName) {
		try {
			File oldFile = new File(webRootPath + oldFilePath);
			String extension = getExtensionWithDotApacheCommons(oldFile);
			String newAbsolutePath = oldFile.getParent() + File.separator + newName + extension;
			File newFile = new File(newAbsolutePath);

			if (newFile.exists()) {
				throw new RuntimeException("目标文件已存在: " + newAbsolutePath);
			}

			boolean success = oldFile.renameTo(newFile);
			if (success) {
				// 统一为 "/" 分隔符
				return newAbsolutePath.substring(webRootPath.length()).replace(File.separator, "/");
			}
			return null;
		} catch (Exception e) {
			System.err.println("重命名文件时发生异常: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据 MD5 查找已存在的图片（仅查正常状态）
	 *
	 * @param md5 MD5 字符串
	 * @return 已存在的 Image，不存在返回 null
	 */
	public Image findByMd5(String md5) {
		return dao.findFirst(
				"SELECT * FROM siargo_image WHERE md5_hash = ? AND status = ?",
				md5, STATUS_NORMAL);
	}


	// -------------------------------------------------------------------------
	// 框架回调
	// -------------------------------------------------------------------------

	@Override
	protected String afterDelete(Image image, Kv kv) {
		// addDeleteSystemLog(image.getId(), JBoltUserKit.getUserId(), image.getName());
		return null;
	}

	@Override
	public String checkCanDelete(Image image, Kv kv) {
		return checkInUse(image, kv);
	}

	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}
}
