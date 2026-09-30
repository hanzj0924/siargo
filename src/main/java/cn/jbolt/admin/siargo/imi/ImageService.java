package cn.jbolt.admin.siargo.imi;

import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.common.storage.SiargoUploadFiles;
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
import com.jfinal.log.Log;
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
	private static final Log LOG = Log.getLog(ImageService.class);
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
	private SiargoStorage storage() { return SiargoStorage.forBusiness(SiargoStorage.Business.IMI); }
	/** 统一使用 "/" 作为路径分隔符，避免 Windows File.separator 存入数据库后 Web 访问异常 */
	

	/**
	 * 获取 Web 根目录路径（供 Controller 调用）
	 */
	public String getWebRootPath() {
		return PathKit.getWebRootPath();
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
        return saveBatch(image, java.util.List.of(tempPath));
    }

	/**
	 * 批量保存（带事务和文件清理）。
	 * <p>
	 * 任一图片保存失败时，回滚数据库事务，并将本次已移动的文件恢复到临时位置。
	 * <p>
	 * 异常回滚逻辑：
	 * <ol>
     *   <li>事务回滚：通过 Db.tx() 自动回滚数据库操作</li>
     *   <li>文件回滚：将已移动文件按逆序移回临时目录</li>
     *   <li>补偿失败保留现存文件，并返回逐项恢复失败信息</li>
	 * </ol>
	 *
	 * @param image     公共元数据（supplierId、description）
	 * @param tempPaths 临时文件相对路径列表
	 * @return Ret 操作结果，失败时携带错误信息
	 */
    public Ret saveBatch(Image image, List<String> tempPaths) {
        if (image == null || image.getLong("supplier_id") == null || tempPaths == null || tempPaths.isEmpty()) return fail("请选择供应商并上传图片");
        SiargoUploadFiles.Moves moves = new SiargoUploadFiles.Moves(storage());
        List<Image> pending = new ArrayList<>();
        java.util.Set<String> hashes = new java.util.HashSet<>();
        try {
            for (String url : tempPaths) {
                java.nio.file.Path source = SiargoUploadFiles.temp(storage(), url, true);
                String md5 = getMd5(source.toFile());
                if (md5 == null || !hashes.add(md5) || findByMd5(md5) != null) return moves.rollback("图片重复或无法读取");
                java.nio.file.Path target = storage().path(String.valueOf(image.getLong("supplier_id")),
                        DateUtil.getNowStr(DateUtil.YM), java.util.UUID.randomUUID() + "_" + source.getFileName());
                moves.move(source, target);
                Image row = new Image();
                row.set("supplier_id", image.getLong("supplier_id")).set("storage_name", getFileName(source.toFile()))
                        .set("file_path", storage().toUrl(target)).set("md5_hash", md5).set("description", image.getStr("description"))
                        .set("upload_time", DateUtil.getDateString(DateUtil.YMDHMS)).set("uploader_id", JBoltUserKit.getUserId()).set("status", STATUS_NORMAL);
                pending.add(row);
            }
            boolean ok = Db.tx(() -> { for (Image row : pending) if (!row.save()) return false; return true; });
            return ok ? Ret.ok().set("files", pending.stream().map(row -> row.getStr("file_path")).toList())
                    : moves.rollback("保存失败");
        } catch (Exception e) { return moves.rollback("保存失败：" + e.getMessage()); }
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
        if (image == null || notOk(image.getId())) return fail(JBoltMsg.PARAM_ERROR);
        Image old = findById(image.getId());
        if (old == null) return fail(JBoltMsg.DATA_NOT_EXIST);
        java.nio.file.Path created = null;
        boolean committed = false;
        try {
            String oldUrl = old.getStr("file_path");
            java.nio.file.Path original = storage().resolveUrl(oldUrl);
            boolean changed = !Objects.equals(image.getStr("file_path"), oldUrl);
            boolean renamed = !Objects.equals(image.getStr("storage_name"), old.getStr("storage_name"));
            boolean supplier = !Objects.equals(image.getLong("supplier_id"), old.getLong("supplier_id"));
            java.nio.file.Path source = changed ? SiargoUploadFiles.temp(storage(), image.getStr("file_path"), true) : original;
            String md5 = old.getStr("md5_hash");
            if (changed) {
                md5 = getMd5(source.toFile());
                if (dao.findFirst("SELECT id FROM siargo_image WHERE md5_hash=? AND status=? AND id<>?",
                        md5, STATUS_NORMAL, image.getId()) != null) return fail("图片已经存在");
            }
            if (changed || renamed || supplier) {
                String base = SiargoStorage.safeSegment(image.getStr("storage_name"));
                String name = java.util.UUID.randomUUID() + "_" + base + getExtensionWithDotApacheCommons(source.toFile());
                java.nio.file.Path parent = supplier || changed ? storage().ensureDirectory(String.valueOf(image.getLong("supplier_id")), DateUtil.getNowStr(DateUtil.YM))
                        : storage().checked(original.getParent());
                java.nio.file.Path candidate = storage().checked(parent.resolve(name));
                storage().copyNew(source, candidate); // 保留旧文件和临时文件直到数据库真正提交。
                created = candidate;
                image.set("file_path", storage().toUrl(created));
            } else image.set("file_path", oldUrl);
            image.set("md5_hash", md5).set("updated_time", DateUtil.getDateString(DateUtil.YMDHMS)).set("update_id", JBoltUserKit.getUserId());
            if (java.util.Objects.equals(image.getInt("status"), STATUS_DELETED)) image.set("deleted_time", DateUtil.getDateString(DateUtil.YMDHMS));
            if (!Db.tx(() -> image.update())) return fail("更新失败");
            committed = true;
            if (created != null) {
                if (Db.queryLong("SELECT COUNT(*) FROM siargo_image WHERE file_path=?", oldUrl) == 0) storage().deleteFile(original);
                if (changed) storage().deleteFile(source);
            }
            return Ret.ok();
        } catch (Exception e) { return fail((committed ? "已更新，但旧文件清理失败：" : "更新失败：") + e.getMessage()); }
        finally {
            if (!committed && created != null) {
                try { storage().deleteFile(created); } catch (Exception e) { LOG.error("失败产物清理失败", e); }
            }
        }
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

		// 软删除：仅更新数据库状态；物理文件删除由 Controller 在事务提交后统一执行（afterCommit）
		dbImage.set("deleted_time", DateUtil.getDateString(DateUtil.YMDHMS));
		dbImage.set("status", STATUS_DELETED);
		boolean success = dbImage.update();
		if (!success) {
			return fail("删除失败");
		}

		return ret(true);
	}

	/**
	 * 批量删除（逗号分隔的 ID 字符串）
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids, true);
	}

	/**
	 * 查询多条图片记录的物理文件路径（供 Controller 在事务提交后统一删除）
	 * @param ids 图片ID列表
	 * @return 物理文件绝对路径列表（剔除空值/含 .. 的非法路径）
	 */
	public List<String> queryFilePathsByIds(List<Long> ids) {
		List<String> paths = new ArrayList<>();
		if (ids == null || ids.isEmpty()) {
			return paths;
		}
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < ids.size(); i++) {
			if (i > 0) placeholders.append(",");
			placeholders.append("?");
		}
		List<Record> rows = Db.find("SELECT file_path FROM siargo_image WHERE id IN (" + placeholders + ")", ids.toArray());
		if (rows != null) {
			for (Record row : rows) {
				String fp = row.getStr("file_path");
				if (fp != null && !fp.isEmpty() && !fp.contains("..")) {
					paths.add(storage().resolveUrl(fp).toString());
				}
			}
		}
		return paths;
	}

	/**
	 * 批量删除物理文件（供 Controller 在事务提交后调用）
	 * @param paths 物理文件绝对路径列表
	 */
    public void deletePhysicalFiles(List<String> paths) {
        if (paths == null) return;
        for (String path : paths) try { storage().deleteFile(java.nio.file.Path.of(path)); }
        catch (Exception e) { LOG.error("图片清理失败：" + path, e); }
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
		// 记录永久删除操作日志
		addDeleteSystemLog(image.getId(), JBoltUserKit.getUserId(), image.getStorageName());
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
