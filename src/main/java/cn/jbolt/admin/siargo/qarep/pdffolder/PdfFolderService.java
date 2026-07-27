package cn.jbolt.admin.siargo.qarep.pdffolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
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
	private static final String BATCH_PATH = "/export/LastMonth"; // 上月打包固定路径

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
		return folder != null ? folder.getTemplatePath() : null;
	}

	/**
	 * 获取指定版号的日常PDF输出路径
	 * @param pdfver 版号
	 * @return 输出路径，未找到返回null
	 */
	public String getExportPath(String pdfver) {
		PdfFolder folder = getByVersion(pdfver);
		return folder != null ? folder.getExportPath() : null;
	}

	/**
	 * 获取上月打包输出路径（固定常量）
	 * @return 批量输出路径
	 */
	public String getBatchPath() {
		return BATCH_PATH;
	}

	/**
	 * 清空缓存（数据变更后调用）
	 */
	public void clearCache() {
		cachedFolders = null;
		cacheTimestamp = 0;
	}

	// ========================== 字典联动方法 ==========================

	/**
	 * 查询所有siargo_pdfver字典版号，并标记是否已创建folder
	 * @return 版号列表（含dict_id, sn, name, created标记）
	 */
	public List<Map<String, Object>> listDictVersions() {
		String sql = "SELECT d.id AS dict_id, d.sn, d.name, "
				+ "(SELECT COUNT(*) FROM siargo_pdf_folder f WHERE f.dict_id = d.id) AS created "
				+ "FROM jb_dictionary d "
				+ "WHERE d.type_key = 'siargo_pdfver' AND d.enable = '1' "
				+ "ORDER BY d.sort_rank ASC";
		List<Record> records = Db.find(sql);
		List<Map<String, Object>> result = new ArrayList<>();
		for (Record r : records) {
			Map<String, Object> item = new HashMap<>();
			item.put("dict_id", r.getLong("dict_id"));
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
		Record dict = Db.findFirst(
				"SELECT sn, name FROM jb_dictionary WHERE id = ? AND type_key = 'siargo_pdfver'", dictId);
		if (dict == null) {
			return fail("字典项不存在或类型不匹配");
		}
		String name = dict.getStr("name");

		// 2. 检查是否已存在（防重复）——使用 name 作为 pdfver
		PdfFolder existing = dao.findFirst(
				"SELECT * FROM siargo_pdf_folder WHERE pdfver = ?", name);
		if (existing != null) {
			return fail("版号 [" + name + "] 对应的路径配置已存在");
		}

		// 3. 派生路径
		String templatePath = "/_view/admin/siargo/pdffolder/reporttemplates/" + name;
		String exportPath = "/export/" + name;

		// 4. 创建物理目录（模板目录）
		String webRoot = PathKit.getWebRootPath();
		File templateDir = new File(webRoot + templatePath);
		if (!templateDir.exists()) {
			templateDir.mkdirs();
		}
		// 创建输出目录
		File exportDir = new File(webRoot + exportPath);
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}

		// 5. INSERT folder记录（主键由SNOWFLAKE自动生成）
		PdfFolder folder = new PdfFolder();
		folder.setPdfver(name);
		folder.setDictId(dictId);
		folder.setTemplatePath(templatePath);
		folder.setExportPath(exportPath);
		folder.setDescription(name);
		folder.setIsActive(1);
		boolean success = folder.save();

		// 6. 刷新缓存
		if (success) {
			clearCache();
		}

		return ret(success);
	}

	/**
	 * 根据版号删除folder记录及关联的模板规则
	 * @param pdfver 版号
	 * @return Ret
	 */
	public Ret deleteVersionFolder(String pdfver) {
		if (pdfver == null || pdfver.isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		PdfFolder folder = dao.findFirst(
				"SELECT * FROM siargo_pdf_folder WHERE pdfver = ?", pdfver);
		if (folder == null) {
			return fail("版号 [" + pdfver + "] 对应的文件夹不存在");
		}
		// 删除关联的模板规则
		Db.delete("DELETE FROM siargo_pdf_template WHERE pdfver = ?", pdfver);
		// 删除物理目录（模板目录 + 输出目录）
		deletePhysicalDir(folder.getTemplatePath());
		deletePhysicalDir(folder.getExportPath());
		// 删除folder记录
		boolean success = folder.delete();
		if (success) {
			clearCache();
		}
		return ret(success);
	}

	/**
	 * 删除物理目录（含子文件）
	 * @param relativePath 相对 webapp 的路径
	 */
	private void deletePhysicalDir(String relativePath) {
		if (relativePath == null || relativePath.isEmpty()) return;
		String fullPath = PathKit.getWebRootPath() + relativePath;
		File dir = new File(fullPath);
		if (dir.exists() && dir.isDirectory()) {
			deleteDirRecursively(dir);
		}
	}

	/**
	 * 递归删除目录
	 */
	private void deleteDirRecursively(File dir) {
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteDirRecursively(f);
				} else {
					f.delete();
				}
			}
		}
		dir.delete();
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
		if (pdfFolder == null || isOk(pdfFolder.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
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
		if (pdfFolder == null || notOk(pdfFolder.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		PdfFolder dbPdfFolder = findById(pdfFolder.getId());
		if (dbPdfFolder == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
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
		Ret ret = deleteByIds(ids, true);
		clearCache();
		return ret;
	}

	/**
	 * 删除数据后执行的回调
	 * @param pdfFolder 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(PdfFolder pdfFolder, Kv kv) {
		clearCache();
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
