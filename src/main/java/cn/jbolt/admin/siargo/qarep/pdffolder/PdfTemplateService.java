package cn.jbolt.admin.siargo.qarep.pdffolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.jfinal.aop.Inject;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.PdfTemplate;

/**
 * PDF模板规则匹配 Service
 * @ClassName: PdfTemplateService
 * @author: hanzj
 * @date: 2026-07-20
 */
public class PdfTemplateService extends JBoltBaseService<PdfTemplate> {

	private final PdfTemplate dao = new PdfTemplate().dao();

	@Override
	protected PdfTemplate dao() {
		return dao;
	}

	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}

	// ======================== DCL 缓存 ========================

	private volatile Map<String, List<PdfTemplate>> cachedRules;
	private volatile long cacheTimestamp;
	private final ReentrantLock cacheLock = new ReentrantLock();
	private static final long CACHE_TTL = 60 * 60 * 1000L; // 1小时

	@Inject
	private PdfFolderService pdfFolderService;

	/**
	 * 匹配模板文件名
	 * @param pdfver   版号
	 * @param prodType 产品类型
	 * @param proModel 产品型号
	 * @return template_file 或 null（未命中）
	 */
	public String matchTemplate(String pdfver, String prodType, String proModel) {
		List<PdfTemplate> rules = getRulesByVersion(pdfver);
		if (rules == null || rules.isEmpty()) return null;

		for (PdfTemplate rule : rules) {
			if (!prodType.equals(rule.getStr("prod_type"))) continue;
			String keywords = rule.getStr("model_keywords");
			if (keywords == null) {
				// 兜底规则：model_keywords 为 NULL 表示不限型号
				return rule.getStr("template_file");
			}
			String[] keyArr = keywords.split(",");
			for (String key : keyArr) {
				if (proModel.contains(key.trim())) {
					return rule.getStr("template_file");
				}
			}
		}
		return null;
	}

	/**
	 * 获取指定版号的规则列表（DCL 双重检查锁缓存）
	 */
	private List<PdfTemplate> getRulesByVersion(String pdfver) {
		// 第一重检查：无锁快速路径
		if (cachedRules != null &&
				(System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL) {
			return cachedRules.get(pdfver);
		}
		cacheLock.lock();
		try {
			// 第二重检查：防止并发穿透
			if (cachedRules != null &&
					(System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL) {
				return cachedRules.get(pdfver);
			}
			Map<String, List<PdfTemplate>> fresh = loadAllRules();
			cachedRules = fresh;
			cacheTimestamp = System.currentTimeMillis();
			return fresh.get(pdfver);
		} finally {
			cacheLock.unlock();
		}
	}

	/**
	 * 从数据库加载全部启用的模板规则，按 pdfver 分组
	 */
	private Map<String, List<PdfTemplate>> loadAllRules() {
		List<PdfTemplate> all = find(
				"SELECT * FROM siargo_pdf_template WHERE is_active = 1 ORDER BY pdfver, prod_type, priority ASC");
		Map<String, List<PdfTemplate>> map = new HashMap<>();
		for (PdfTemplate rule : all) {
			String ver = rule.getStr("pdfver");
			map.computeIfAbsent(ver, k -> new ArrayList<>()).add(rule);
		}
		return map;
	}

	/**
	 * 清除缓存（数据变更后调用）
	 */
	public void clearCache() {
		cachedRules = null;
		cacheTimestamp = 0;
	}

	// ======================== 模板文件管理 ========================

	/**
	 * 列出指定版号下的模板文件
	 * @param ver 版号
	 * @return 文件列表（fileName + fileSize）
	 */
	public List<Map<String, Object>> listTemplates(String ver) {
		List<Map<String, Object>> result = new ArrayList<>();
		String templatePath = getTemplatePath(ver);
		if (templatePath == null) return result;

		String physicalDir = PathKit.getWebRootPath() + templatePath;
		File dir = new File(physicalDir);
		if (!dir.exists() || !dir.isDirectory()) return result;

		File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf"));
		if (files == null) return result;

		for (File file : files) {
			Map<String, Object> item = new HashMap<>();
			item.put("fileName", file.getName());
			item.put("fileSize", file.length());
			result.add(item);
		}
		return result;
	}

	/**
	 * 上传模板文件
	 * @param ver  版号
	 * @param file 上传的文件
	 * @return Ret
	 */
	public Ret uploadTemplate(String ver, UploadFile file) {
		if (file == null) return fail("请选择文件");

		String templatePath = getTemplatePath(ver);
		if (templatePath == null) return fail("未找到该版号的路径配置");

		String originalFileName = file.getOriginalFileName();
		// 校验文件类型
		if (!originalFileName.toLowerCase().endsWith(".pdf")) {
			return fail("仅支持 PDF 文件");
		}
		// 安全检查：文件名不能包含路径穿越字符
		if (originalFileName.contains("..") || originalFileName.contains("/")
				|| originalFileName.contains("\\")) {
			return fail("非法文件名");
		}

		String physicalDir = PathKit.getWebRootPath() + templatePath;
		File dir = new File(physicalDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File targetFile = new File(dir, originalFileName);
		boolean success = file.getFile().renameTo(targetFile);
		return ret(success);
	}

	/**
	 * 删除模板文件
	 * @param ver      版号
	 * @param fileName 文件名
	 * @return Ret
	 */
	public Ret deleteTemplate(String ver, String fileName) {
		if (fileName == null || fileName.isEmpty()) return fail("文件名不能为空");

		// 安全检查：fileName 不能包含路径分隔符或 ..
		if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
			return fail("非法文件名");
		}

		String templatePath = getTemplatePath(ver);
		if (templatePath == null) return fail("未找到该版号的路径配置");

		String physicalPath = PathKit.getWebRootPath() + templatePath + File.separator + fileName;
		File file = new File(physicalPath);
		if (!file.exists()) return fail("文件不存在");

		boolean success = file.delete();
		return ret(success);
	}

	/**
	 * 获取版号对应的模板存储路径（委托 PdfFolderService 缓存）
	 */
	private String getTemplatePath(String ver) {
		return pdfFolderService.getTemplatePath(ver);
	}

	// ======================== 规则 CRUD ========================

	/**
	 * 分页查询规则
	 * @param pdfver     版号
	 * @param pageNumber 页码
	 * @param pageSize   每页大小
	 * @return Page
	 */
	public Page<Record> paginateRules(String pdfver, int pageNumber, int pageSize) {
		// id 转为字符串，避免前端雪花ID精度丢失
		return Db.paginate(pageNumber, pageSize,
				"SELECT CAST(id AS CHAR) AS id, pdfver, prod_type, model_keywords, template_file, error_hint, priority, is_active",
				"FROM siargo_pdf_template WHERE pdfver = ? AND is_active = 1 ORDER BY prod_type, priority ASC",
				pdfver);
	}

	/**
	 * 保存规则（新增或更新）
	 * @param rule 规则对象
	 * @return Ret
	 */
	public Ret saveRule(PdfTemplate rule) {
		if (rule == null) return fail("参数错误");
		boolean success;
		if (rule.getId() != null) {
			success = rule.update();
		} else {
			success = rule.save();
		}
		if (success) clearCache();
		return ret(success);
	}

	/**
	 * 删除规则
	 * @param id 规则ID
	 * @return Ret
	 */
	public Ret deleteRule(Long id) {
		if (id == null) return fail("参数错误");
		Ret ret = deleteById(id);
		if (ret.isOk()) clearCache();
		return ret;
	}

}
