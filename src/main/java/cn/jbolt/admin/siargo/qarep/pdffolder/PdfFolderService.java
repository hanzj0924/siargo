package cn.jbolt.admin.siargo.qarep.pdffolder;

import java.io.File;
import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.admin.siargo.qarep.uploadImport.PdfStoragePaths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.PdfFolder;

/**
 * 报告单模板 Service
 * @ClassName: PdfFolderService
 * @author: hanzj
 * @date: 2026-07-20 13:37
 */
public class PdfFolderService extends JBoltBaseService<PdfFolder> {
	private final PdfFolder dao = new PdfFolder().dao();

	@Override
	protected PdfFolder dao() {
		return dao;
	}

	// === 缓存相关 ===
	private volatile Map<String, PdfFolder> cachedFolders;
	private volatile long cacheTimestamp;
	private final ReentrantLock cacheLock = new ReentrantLock();
	private static final long CACHE_TTL = 2 * 60 * 60 * 1000L; // 2小时
	private SiargoStorage storage() { return SiargoStorage.forReportTemplates(); }
	private SiargoStorage exportStorage() { return SiargoStorage.forBusiness(SiargoStorage.Business.QAREP); }

	// ========================== 路径查询方法 ==========================

	/**
	 * 根据版号获取PdfFolder（DCL缓存模式）
	 * @param pdfver 版号
	 * @return PdfFolder实例，未找到返回null
	 */
	public PdfFolder getByVersion(String pdfver) {
		// 第一重检查：无锁快速路径
		Map<String, PdfFolder> local = cachedFolders;
		if (local != null && (System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL) {
			PdfFolder folder = local.get(pdfver);
			if (folder != null) return folder;
		}
		cacheLock.lock();
		try {
			// 第二重检查：防止并发穿透
			local = cachedFolders;
			if (local != null && (System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL) {
				return local.get(pdfver);
			}
			// 加载全部启用的folder
			List<PdfFolder> all = dao.find("SELECT * FROM siargo_pdf_folder WHERE is_active = 1");
			Map<String, PdfFolder> map = new HashMap<>();
			for (PdfFolder f : all) {
				map.put(f.getStr("pdfver"), f);
			}
			cachedFolders = map;
			cacheTimestamp = System.currentTimeMillis();
			return map.get(pdfver);
		} finally {
			cacheLock.unlock();
		}
	}

	/**
	 * 获取指定版号的模板路径
	 * @param pdfver 版号
	 * @return 模板路径，未找到返回null
	 */
    public String getTemplatePath(String pdfver) {
        PdfFolder folder = getByVersion(pdfver);
        return folder == null ? null : PdfStoragePaths.templateUrl(storage(), folder.getStr("pdfver"));
    }

	/**
	 * 清空缓存（数据变更后调用）
	 */
	public void clearCache() {
		cachedFolders = null;
		cacheTimestamp = 0;
	}

	// ========================== 字典联动方法 ==========================

	private Record findVersionDictionary(Long dictId) {
		if (notOk(dictId)) return null;
		return Db.findFirst("SELECT name FROM jb_dictionary WHERE id = ? "
				+ "AND type_key = 'siargo_pdfver' AND enable = '1'", dictId);
	}

	/**
	 * 查询所有siargo_pdfver字典版号，并标记是否已创建folder
	 * @return 版号列表（含dict_id, sn, name, created标记）
	 */
	public List<Map<String, Object>> listDictVersions() {
		String sql = "SELECT CAST(d.id AS CHAR) AS dict_id, d.sn, d.name, "
				+ "(SELECT COUNT(*) FROM siargo_pdf_folder f WHERE f.dict_id = d.id) AS created "
				+ "FROM jb_dictionary d "
				+ "WHERE d.type_key = 'siargo_pdfver' AND d.enable = '1' "
				+ "ORDER BY d.sort_rank ASC";
		List<Record> records = Db.find(sql);
		List<Map<String, Object>> result = new ArrayList<>();
		for (Record r : records) {
			Map<String, Object> item = new HashMap<>();
			item.put("dict_id", r.getStr("dict_id"));
			item.put("sn", r.getStr("sn"));
			item.put("name", r.getStr("name"));
			item.put("created", r.getInt("created") > 0);
			result.add(item);
		}
		return result;
	}

	/**
	 * 根据字典ID创建版号folder记录及物理目录
	 * @param dictId 字典ID（jb_dictionary.id）
	 * @return Ret
	 */
	public Ret createVersionFolder(Long dictId) {
		if (notOk(dictId)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 1. 查询字典项
		Record dict = findVersionDictionary(dictId);
		if (dict == null) {
			return fail("版号字典项不存在或未启用");
		}
		String pdfver = dict.getStr("name");
		if (pdfver == null || pdfver.isBlank()) return fail("版号字典名称未配置");

		// 2. 以字典项及其名称防重复。
		PdfFolder existing = dao.findFirst(
				"SELECT * FROM siargo_pdf_folder WHERE dict_id = ? OR pdfver = ?", dictId, pdfver);
		if (existing != null) {
			return fail("版号 [" + pdfver + "] 对应的路径配置已存在");
		}

		// 3. 派生路径
		String templatePath = PdfStoragePaths.templateUrl(storage(), pdfver);
		String exportPath = PdfStoragePaths.url(exportStorage(), "reports", pdfver);

		// 4. 共享存储创建目录；目录失败不能继续写入数据库。
        try {
            storage().ensureDirectory(PdfStoragePaths.templateSegments(pdfver));
            exportStorage().ensureDirectory(PdfStoragePaths.versionSegments("reports", pdfver));
        } catch (Exception e) { return fail("创建版号目录失败：" + e.getMessage()); }

		// 5. INSERT folder记录（主键由SNOWFLAKE自动生成）
		PdfFolder folder = new PdfFolder();
		folder.set("pdfver", pdfver);
		folder.set("dict_id", dictId);
		folder.set("template_path", templatePath);
		folder.set("export_path", exportPath);
		folder.set("description", pdfver);
		folder.set("is_active", 1);
		boolean success = folder.save();

		// 6. 刷新缓存
		if (success) {
			clearCache();
		}

		return ret(success);
	}

	/**
	 * 根据版号删除folder记录及关联的模板规则
	 * <p>先执行DB删除，全部成功后再删除物理目录，避免DB删除失败时文件已被删的不一致</p>
	 * @param pdfver 版号
	 * @return Ret
	 */
	public Ret deleteVersionFolder(String pdfver) {
		if (pdfver == null || pdfver.isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		PdfFolder folder = dao.findFirst(
				"SELECT * FROM siargo_pdf_folder WHERE pdfver = ? FOR UPDATE", pdfver);
		if (folder == null) {
			return fail("版号 [" + pdfver + "] 对应的文件夹不存在");
		}
		if (Db.queryLong("SELECT COUNT(*) FROM siargo_product WHERE pdfver=?", pdfver) > 0) {
            return fail("版号仍被产品引用，不能删除模板及报告目录");
        }
        // 不删除含文件目录，避免删除未引用但仍需保留的历史产物。
        for (String directory : collectFolderDirs(pdfver)) {
            try (var entries = java.nio.file.Files.walk(checkedFolderDirectory(java.nio.file.Path.of(directory)))) {
                if (entries.anyMatch(java.nio.file.Files::isRegularFile)) return fail("版号目录仍有文件，请先处理文件");
            } catch (java.nio.file.NoSuchFileException ignored) {
            } catch (Exception e) { return fail("目录检查失败：" + e.getMessage()); }
        }
		// 模板及其系列关联须从模板管理显式解除，版号删除不得隐式级联。
        if (Db.queryLong("SELECT COUNT(*) FROM siargo_pdf_template WHERE pdfver=?", pdfver) > 0) {
            return fail("版号仍有报告模板，请先处理模板及关联系列");
        }
		// 2. 删除folder记录（DB）
		boolean success = folder.delete();
		if (!success) {
			return fail("删除版号 [" + pdfver + "] 记录失败");
		}
		// 3. 物理目录删除由 Controller 在事务提交后统一执行（afterCommit，deletePhysicalDirs）
		return Ret.ok();
	}

	/**
	 * 收集版号对应待删物理目录（模板目录 + 新旧输出目录），供 Controller afterCommit 删除
	 * @param pdfver 版号
	 * @return 物理目录绝对路径列表
	 */
	public List<String> collectFolderDirs(String pdfver) {
		List<String> dirs = new ArrayList<>();
		PdfFolder folder = dao.findFirst(
				"SELECT * FROM siargo_pdf_folder WHERE pdfver = ?", pdfver);
		if (folder == null) {
			return dirs;
		}
		String templatePath = PdfStoragePaths.templateUrl(storage(), pdfver);
		String exportPath = PdfStoragePaths.url(exportStorage(), "reports", pdfver);
		if (templatePath != null && !templatePath.isEmpty()) {
			dirs.add(storage().resolveUrl(templatePath).toString());
		}
		if (exportPath != null && !exportPath.isEmpty()) {
			dirs.add(exportStorage().resolveUrl(exportPath).toString());
		}
        // 保留对旧上传目录报告的检查，避免版号删除漏掉未迁移的历史文件。
        dirs.add(PdfStoragePaths.path(SiargoStorage.forReportResources(), "reports", pdfver).toString());
		return dirs;
	}

    private java.nio.file.Path checkedFolderDirectory(java.nio.file.Path directory) {
        SiargoStorage exports = exportStorage();
        if (directory.toAbsolutePath().normalize().startsWith(exports.root())) return exports.checked(directory);
        SiargoStorage templates = storage();
        return directory.toAbsolutePath().normalize().startsWith(templates.root())
                ? templates.checked(directory) : SiargoStorage.forReportResources().checked(directory);
    }

	/**
	 * 批量删除物理目录（供 Controller 在事务提交后调用）
	 * @param dirs 物理目录绝对路径列表
	 */
	public void deletePhysicalDirs(List<String> dirs) {
		if (dirs == null) {
			return;
		}
		for (String fullPath : dirs) {
			if (fullPath == null || fullPath.isEmpty()) {
				continue;
			}
			File dir = new File(fullPath);
			if (dir.exists() && dir.isDirectory()) {
				deleteEmptyDirectory(dir);
			}
		}
	}

	/**
	 * 仅删除已校验的空目录
	 */
    private void deleteEmptyDirectory(File dir) {
        try {
            java.nio.file.Path safe = checkedFolderDirectory(dir.toPath());
            // 仅删除空目录；不递归删除模板或历史报告。
            java.nio.file.Files.deleteIfExists(safe);
        } catch (Exception e) { throw new IllegalStateException("版号目录清理失败：" + e.getMessage(), e); }
    }

	/**
	 * 查询所有folder记录（按pdfver升序）
	 * @return 所有folder列表
	 */
	public List<PdfFolder> listAll() {
		return dao.find("SELECT * FROM siargo_pdf_folder ORDER BY pdfver ASC");
	}

	// ========================== 基础CRUD方法 ==========================

	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<PdfFolder> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("pdfver,is_active", "desc", pageNumber, pageSize, keywords, "pdfver");
	}

	/**
	 * 保存
	 * @param pdfFolder
	 * @return
	 */
	public Ret save(PdfFolder pdfFolder) {
		if (pdfFolder == null || isOk(pdfFolder.getLong("id"))) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Long dictId = pdfFolder.getLong("dict_id");
		Record dict = findVersionDictionary(dictId);
		if (dict == null) return fail("版号字典项不存在或未启用");
		String pdfver = dict.getStr("name");
		if (pdfver == null || pdfver.isBlank()) return fail("版号字典名称未配置");
		if (dao.findFirst("SELECT id FROM siargo_pdf_folder WHERE dict_id = ? OR pdfver = ?", dictId, pdfver) != null) {
			return fail("版号 [" + dict.getStr("name") + "] 对应的路径配置已存在");
		}
		pdfFolder.set("pdfver", pdfver);
		pdfFolder.set("template_path", PdfStoragePaths.templateUrl(storage(), pdfver));
        pdfFolder.set("export_path", PdfStoragePaths.url(exportStorage(), "reports", pdfver));
        boolean success = pdfFolder.save();
		if (success) {
			clearCache();
		}
		return ret(success);
	}

	/**
	 * 更新
	 * @param pdfFolder
	 * @return
	 */
	public Ret update(PdfFolder pdfFolder) {
		if (pdfFolder == null || notOk(pdfFolder.getLong("id"))) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		PdfFolder dbPdfFolder = findById(pdfFolder.getLong("id"));
		if (dbPdfFolder == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		Long dictId = pdfFolder.getLong("dict_id");
		Record dict = findVersionDictionary(dictId);
		if (dict == null) return fail("版号字典项不存在或未启用");
		String pdfver = dict.getStr("name");
		if (pdfver == null || pdfver.isBlank()) return fail("版号字典名称未配置");
		if (!java.util.Objects.equals(dbPdfFolder.getLong("dict_id"), dictId)
				|| !java.util.Objects.equals(dbPdfFolder.getStr("pdfver"), pdfver)) {
			return fail("已有版号不能直接改名，请新增版号");
		}
		pdfFolder.set("pdfver", pdfver);
        pdfFolder.set("template_path", PdfStoragePaths.templateUrl(storage(), pdfver));
        pdfFolder.set("export_path", PdfStoragePaths.url(exportStorage(), "reports", pdfver));
        boolean success = pdfFolder.update();
		if (success) {
			clearCache();
		}
		return ret(success);
	}

	/**
	 * 删除 指定多个ID
	 * @param ids
	 * @return
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids, true);
	}

	/**
	 * 删除数据后执行的回调
	 * @param pdfFolder 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(PdfFolder pdfFolder, Kv kv) {
		return null;
	}

	/**
	 * 检测是否可以删除
	 * @param pdfFolder 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(PdfFolder pdfFolder, Kv kv) {
		if (Db.queryLong("SELECT COUNT(*) FROM siargo_product WHERE pdfver=?", pdfFolder.getStr("pdfver")) > 0) return "版号仍被产品引用";
        if (Db.queryLong("SELECT COUNT(*) FROM siargo_pdf_template WHERE pdfver=?", pdfFolder.getStr("pdfver")) > 0) return "版号仍有模板规则";
        return checkInUse(pdfFolder, kv);
	}

	/**
	 * 设置返回二开业务所属的关键systemLog的targetType
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}

}
