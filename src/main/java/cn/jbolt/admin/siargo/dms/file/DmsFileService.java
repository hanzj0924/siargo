package cn.jbolt.admin.siargo.dms.file;

import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.common.storage.SiargoUploadFiles;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.siargo.model.DmsFile;
import cn.jbolt.siargo.model.DmsFileKeyword;
/**
 * 文件类别表管理 Service
 * @ClassName: DmsFileService   
 * @author: hanzj
 * @date: 2026-03-23 13:45  
 */
public class DmsFileService extends JBoltBaseService<DmsFile> {
	/** 日志对象 */
	private static final Log LOG = Log.getLog(DmsFileService.class);
    private static final Object FILE_WRITE_LOCK = new Object();
	/** 文件数据访问对象 */
	private final DmsFile dao=new DmsFile().dao();
	/** 文件关键字数据访问对象 */
	private final DmsFileKeyword keywordDao = new DmsFileKeyword().dao();
	
	/** 正常状态：文件有效 */
	public static final int STATUS_NORMAL = 1;
	/** 删除状态：文件已标记删除 */
	public static final int STATUS_DELETED = 0;
	/** 文件上传路径前缀 */
	private SiargoStorage storage() { return SiargoStorage.forBusiness(SiargoStorage.Business.DMS); }
	/** 临时目录相对路径前缀 */

	
	@Override
	protected DmsFile dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * 搜索逻辑：同时匹配文件名(file_name)和关键字表(keyword)中的内容
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param categoryId 类别ID（为空时返回空页，前端未选择类别时右侧保持空白）
	 * @param keywords 关键字（同时搜索 file_name 和 keyword）
	 * @param isActive 生效状态（可选）
	 * @param activeDate 生效日期（可选，格式：yyyy-MM）
	 * @return 分页数据
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, Long categoryId,
			String keywords, Integer isActive, String activeDate) {
		// 未选择类别时右侧保持空白
		if (categoryId == null) {
			return new Page<>(new ArrayList<>(), pageNumber, pageSize, 0, 0);
		}
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT CAST(f.id AS CHAR) AS id, CAST(f.category_id AS CHAR) AS categoryId, ")
				.append("f.file_name AS fileName, f.file_path AS filePath, f.file_ext AS fileExt, ")
				.append("f.description AS description, f.modify_date AS modifyDate, ")
				.append("f.is_active AS isActive, f.active_date AS activeDate, ")
				.append("f.upload_time AS uploadTime, ju.name AS uploaderName, f.status AS status, ")
				.append("GROUP_CONCAT(DISTINCT k.keyword ORDER BY k.id SEPARATOR ',') AS keywords");
		
		StringBuilder fromSql = new StringBuilder();
		fromSql.append(" FROM siargo_dms_file f ")
				.append("LEFT JOIN jb_user ju ON ju.id = f.uploader_id ")
				.append("LEFT JOIN siargo_dms_file_keyword k ON k.file_id = f.id ")
				.append("WHERE f.status = ?");
		
		List<Object> params = new ArrayList<>();
		params.add(STATUS_NORMAL);
		
		// 类别过滤
		fromSql.append(" AND f.category_id = ?");
		params.add(categoryId);
		
		// 关键字搜索逻辑：同时匹配文件名和关键字表
		// 注：用 EXISTS 子查询匹配关键字，避免 WHERE 过滤 JOIN 行导致 GROUP_CONCAT 丢失未命中的关键字
		if (StrKit.notBlank(keywords)) {
			fromSql.append(" AND (f.file_name LIKE ? OR EXISTS (SELECT 1 FROM siargo_dms_file_keyword k2 WHERE k2.file_id = f.id AND k2.keyword LIKE ?))");
			params.add("%" + keywords + "%");
			params.add("%" + keywords + "%");
		}
		
		// 生效状态过滤
		if (isActive != null) {
			fromSql.append(" AND f.is_active = ?");
			params.add(isActive);
		}
		
		// 生效日期过滤（按年月匹配）
		if (StrKit.notBlank(activeDate)) {
			fromSql.append(" AND DATE_FORMAT(f.active_date, '%Y-%m') = ?");
			params.add(activeDate.substring(0, 7));
		}
		
		fromSql.append(" GROUP BY f.id, f.category_id, f.file_name, f.file_path, f.file_ext, ")
			   .append("f.description, f.modify_date, f.is_active ")
			   .append("ORDER BY f.is_active DESC, f.active_date DESC");
		
		return Db.paginate(pageNumber, pageSize, true, selectSql.toString(), 
				fromSql.toString(), params.toArray());
	}
	
	/**
	 * 全局搜索（跨所有类别）
	 * 业务场景：用户在首页搜索框输入关键字，检索所有类别下的匹配文件
	 * 搜索逻辑：同时匹配文件名(file_name)和关键字表(keyword)中的内容
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param keywords 搜索关键字
	 * @return 分页数据，额外包含 categoryName 字段
	 */
	public Page<Record> paginateGlobalSearch(int pageNumber, int pageSize, String keywords) {
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT CAST(f.id AS CHAR) AS id, CAST(f.category_id AS CHAR) AS categoryId, ")
				.append("f.file_name AS fileName, f.file_path AS filePath, f.file_ext AS fileExt, ")
				.append("f.description AS description, f.modify_date AS modifyDate, ")
				.append("f.is_active AS isActive, f.active_date AS activeDate, ")
				.append("f.upload_time AS uploadTime, ju.name AS uploaderName, f.status AS status, ")
				.append("c.name AS categoryName, ")
				.append("GROUP_CONCAT(DISTINCT k.keyword ORDER BY k.id SEPARATOR ',') AS keywords");
		
		StringBuilder fromSql = new StringBuilder();
		fromSql.append(" FROM siargo_dms_file f ")
				.append("LEFT JOIN jb_user ju ON ju.id = f.uploader_id ")
				.append("LEFT JOIN siargo_dms_file_keyword k ON k.file_id = f.id ")
				.append("LEFT JOIN siargo_dms_category c ON c.id = f.category_id ")
				.append("WHERE f.status = ?");
		
		List<Object> params = new ArrayList<>();
		params.add(STATUS_NORMAL);
		
		// 全局搜索关键字逻辑：同时匹配文件名和关键字表
		// 注：用 EXISTS 子查询匹配关键字，避免 WHERE 过滤 JOIN 行导致 GROUP_CONCAT 丢失未命中的关键字
		if (StrKit.notBlank(keywords)) {
			fromSql.append(" AND (f.file_name LIKE ? OR EXISTS (SELECT 1 FROM siargo_dms_file_keyword k2 WHERE k2.file_id = f.id AND k2.keyword LIKE ?))");
			params.add("%" + keywords + "%");
			params.add("%" + keywords + "%");
		}
		
		fromSql.append(" GROUP BY f.id, f.category_id, f.file_name, f.file_path, f.file_ext, ")
			   .append("f.description, f.modify_date, f.is_active, f.active_date, ")
			   .append("f.upload_time, f.uploader_id, f.status, c.name ")
			   .append("ORDER BY f.is_active DESC, f.active_date DESC");
		
		return Db.paginate(pageNumber, pageSize, true, selectSql.toString(), 
				fromSql.toString(), params.toArray());
	}
	
	/**
	 * 失效文件分页查询
	 * 失效文件定义：is_active = 0（未生效）且 status = 1（未删除）的文件记录
	 * 业务场景：管理员查看所有已标记为失效的文件，便于管理或重新激活
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param keywords 关键字（搜索文件名）
	 * @return 失效文件分页数据
	 */
	public Page<Record> paginateInactiveDatas(int pageNumber, int pageSize, String keywords) {
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT CAST(f.id AS CHAR) AS id, CAST(f.category_id AS CHAR) AS categoryId, ")
				.append("f.file_name AS fileName, f.file_path AS filePath, f.file_ext AS fileExt, ")
				.append("f.description AS description, f.modify_date AS modifyDate, ")
				.append("f.is_active AS isActive, f.active_date AS activeDate, ")
				.append("f.upload_time AS uploadTime, f.uploader_id AS uploaderId, f.status AS status, ")
				.append("c.name AS categoryName");
		
		StringBuilder fromSql = new StringBuilder();
		fromSql.append(" FROM siargo_dms_file f ")
				.append("LEFT JOIN siargo_dms_category c ON c.id = f.category_id ")
				.append("WHERE f.is_active = 0 AND f.status = ?");
		
		List<Object> params = new ArrayList<>();
		params.add(STATUS_NORMAL);
		
		// 关键字搜索：匹配 file_name
		if (StrKit.notBlank(keywords)) {
			fromSql.append(" AND f.file_name LIKE ?");
			params.add("%" + keywords + "%");
		}
		
		fromSql.append(" ORDER BY f.upload_time DESC");
		
		return Db.paginate(pageNumber, pageSize, selectSql.toString(), 
				fromSql.toString(), params.toArray());
	}
	
	/**
	 * 批量保存文件记录（临时文件移入正式目录 + 元数据入库 + 关键字关联）
	 * 事务策略：内部手动 Db.tx()，任一文件处理失败则整体回滚数据库，并将已移动的物理文件移回临时目录
	 * 安全策略：事务外先对全部临时路径做路径穿越校验，任一不合法直接失败
	 * @param template 文件公共信息模板（类别、生效日期、备注、生效状态）
	 * @param keywordsStr 逗号分隔的关键字字符串
	 * @param tempPaths 临时文件相对路径数组
	 * @return 操作结果
	 */
    public Ret saveBatch(DmsFile template, String keywordsStr, String[] tempPaths) {
        return saveBatch(template, keywordsStr, tempPaths, null);
    }

    /** overwriteTokens 仅携带用户已确认的文件快照；没有确认只返回提示，不移动文件。 */
    public Ret saveBatch(DmsFile template, String keywordsStr, String[] tempPaths, String overwriteTokens) {
        synchronized (FILE_WRITE_LOCK) {
            try { return saveBatchLocked(template, keywordsStr, tempPaths, parseOverwriteTokens(overwriteTokens)); }
            catch (Exception e) { return fail("保存失败：" + e.getMessage()); }
        }
    }

    private Ret saveBatchLocked(DmsFile template, String keywordsStr, String[] tempPaths, java.util.Map<String, String> tokens) {
        if (template == null || isOk(template.getLong("id")) || notOk(template.getLong("category_id")) || tempPaths == null || tempPaths.length == 0)
            return fail(JBoltMsg.PARAM_ERROR);
        SiargoStorage storage = storage();
        SiargoUploadFiles.Moves moves = new SiargoUploadFiles.Moves(storage);
        List<DmsFile> pending = new ArrayList<>();
        List<java.nio.file.Path> backups = new ArrayList<>(), sources = new ArrayList<>();
        List<OverwriteInfo> plans = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        boolean committed = false;
        try {
            List<Ret> conflicts = new ArrayList<>();
            // 整批预检完成且所有覆盖均已确认后才移动；选择“否”不会影响任何已有文件。
            for (String url : tempPaths) {
                if (url == null || url.isBlank()) continue;
                java.nio.file.Path source = validateTempFile(url.trim()).toPath();
                String filename = source.getFileName().toString();
                if (!seen.add(filename.toLowerCase(java.util.Locale.ROOT))) return fail("同一批次请勿重复选择同名文件");
                OverwriteInfo plan = overwriteInfo(template.getLong("category_id"), filename);
                sources.add(source); plans.add(plan);
                if (plan.conflict() && !plan.token().equals(tokens.get(filename))) conflicts.add(plan.prompt());
            }
            if (sources.isEmpty()) return fail("请上传文件");
            if (!conflicts.isEmpty()) return overwriteRequired(conflicts);
            for (int i = 0; i < sources.size(); i++) {
                java.nio.file.Path source = sources.get(i);
                OverwriteInfo plan = plans.get(i);
                preserveTarget(storage, moves, plan, backups);
                moves.move(source, plan.target());
                DmsFile row = new DmsFile();
                if (plan.existing() != null) row.set("id", plan.existing().getLong("id"));
                row.set("category_id", template.getLong("category_id")).set("active_date", template.get("active_date"))
                        .set("description", template.getStr("description"))
                        .set("is_active", template.get("is_active") == null
                                ? (plan.existing() == null ? 1 : plan.existing().get("is_active")) : template.get("is_active"))
                        .set("file_path", storage.toUrl(plan.target())).set("file_ext", getFileExt(plan.filename()))
                        .set("file_name", getFileNameWithoutExt(plan.filename()))
                        .set("upload_time", new java.util.Date()).set("uploader_id", JBoltUserKit.getUserId()).set("status", STATUS_NORMAL);
                pending.add(row);
            }
            boolean ok = Db.tx(() -> {
                if (!lockCategory(template.getLong("category_id"))) return false;
                for (int i = 0; i < pending.size(); i++) {
                    DmsFile row = pending.get(i);
                    OverwriteInfo plan = plans.get(i);
                    if (!sameDatabaseTarget(plan, template.getLong("category_id"))) return false;
                    if (!(plan.existing() == null ? row.save() : row.update())) return false;
                    if (plan.existing() != null) deleteKeywordsByFileId(row.getLong("id"));
                    saveKeywords(row.getLong("id"), keywordsStr);
                }
                return true;
            });
            if (!ok) return moves.rollback("文件记录已变化或保存失败，请刷新后重试");
            committed = true;
            cleanupPublished(plans, backups);
            return Ret.ok();
        } catch (Exception e) {
            return committed ? Ret.ok().set("cleanupWarning", e.getMessage()) : moves.rollback("保存失败：" + e.getMessage());
        }
    }
	
	/**
	 * 更新文件信息（支持替换物理文件）
	 * 业务场景：编辑文件基本信息，同时更新关键字关联（先删后插）；
	 * tempFilePath 非空时将新文件移入正式目录并更新文件元数据，
	 * 类别目录内保留原文件名；同名经确认后复用同名记录，失败恢复旧文件
	 * @param dmsFile 文件信息模型
	 * @param keywordsStr 逗号分隔的关键字字符串
	 * @param tempFilePath 新上传的临时文件路径（可选，非空时替换原文件）
	 * @return 操作结果，失败时先恢复旧文件，再删除本次新上传文件；恢复失败则保留现场
	 */
    public Ret update(DmsFile input, String keywordsStr, String tempFilePath) {
        return update(input, keywordsStr, tempFilePath, null);
    }

    public Ret update(DmsFile input, String keywordsStr, String tempFilePath, String overwriteTokens) {
        synchronized (FILE_WRITE_LOCK) {
            Ret result;
            try { result = updateLocked(input, keywordsStr, tempFilePath, parseOverwriteTokens(overwriteTokens)); }
            catch (Exception e) { result = fail("更新失败：" + e.getMessage()); }
            // updateLocked 已完成事务回滚和文件逆序恢复；恢复不完整时不得继续删除任何现场文件。
            Object recoveryFailures = result.get("fileRecoveryFailures");
            if (result.isFail() && !Boolean.TRUE.equals(result.getBoolean("requiresOverwrite")) && StrKit.notBlank(tempFilePath)
                    && (recoveryFailures == null || recoveryFailures instanceof List<?> failures && failures.isEmpty())) {
                try {
                    SiargoStorage storage = storage();
                    java.nio.file.Path temp = SiargoUploadFiles.temp(storage, tempFilePath.trim(), false);
                    storage.deleteFile(temp);
                    result.set("tempFileDeleted", true)
                            .set("msg", result.getStr("msg") + "；本次临时文件已清理，如需替换请重新上传");
                } catch (Exception cleanup) {
                    result.set("tempFileDeleted", false)
                            .set("cleanupWarning", "临时文件清理失败：" + cleanup.getMessage())
                            .set("msg", result.getStr("msg") + "；临时文件未清理，请检查清理提示");
                }
            }
            return result;
        }
    }

    private Ret updateLocked(DmsFile input, String keywordsStr, String tempFilePath, java.util.Map<String, String> tokens) {
        if (input == null || notOk(input.getId())) return fail(JBoltMsg.PARAM_ERROR);
        DmsFile edited = findById(input.getId());
        if (edited == null || !java.util.Objects.equals(edited.getInt("status"), STATUS_NORMAL)) return fail(JBoltMsg.DATA_NOT_EXIST);
        SiargoStorage storage = storage();
        SiargoUploadFiles.Moves moves = new SiargoUploadFiles.Moves(storage);
        List<java.nio.file.Path> backups = new ArrayList<>();
        boolean committed = false;
        try {
            Long category = input.getLong("category_id") == null ? edited.getLong("category_id") : input.getLong("category_id");
            if (notOk(category)) return fail("请选择文件类别");
            OverwriteInfo plan = null;
            DmsFile selected = edited;
            java.nio.file.Path source = null;
            if (StrKit.notBlank(tempFilePath)) {
                source = validateTempFile(tempFilePath.trim()).toPath();
                plan = overwriteInfo(category, source.getFileName().toString());
                if (plan.conflict() && !plan.token().equals(tokens.get(plan.filename())))
                    return overwriteRequired(java.util.List.of(plan.prompt()));
                // 编辑 A 时选择已有 B 并确认覆盖：只更新 B，保留 A，不删除或合并其他业务记录。
                if (plan.existing() != null) selected = plan.existing();
            }
            final DmsFile original = selected;
            final OverwriteInfo confirmedPlan = plan;
            String old = original.getStr("file_path");
            String expectedState = rowState(original);
            input.set("id", original.getLong("id")).set("category_id", category).set("file_path", old)
                    .set("file_name", original.getStr("file_name")).set("file_ext", original.getStr("file_ext"));
            if (plan != null) {
                preserveTarget(storage, moves, plan, backups);
                moves.move(source, plan.target());
                input.set("file_path", storage.toUrl(plan.target())).set("file_ext", getFileExt(plan.filename()))
                        .set("file_name", getFileNameWithoutExt(plan.filename()))
                        .set("upload_time", new java.util.Date()).set("uploader_id", JBoltUserKit.getUserId());
            }
            boolean ok = Db.tx(() -> {
                java.util.SortedSet<Long> categories = new java.util.TreeSet<>();
                categories.add(original.getLong("category_id")); categories.add(category);
                for (Long categoryId : categories) if (!lockCategory(categoryId)) return false;
                DmsFile current = dao.findFirst("SELECT * FROM siargo_dms_file WHERE id=? FOR UPDATE", original.getLong("id"));
                if (!expectedState.equals(rowState(current))) return false;
                if (confirmedPlan != null) {
                    if (!sameDatabaseTarget(confirmedPlan, category)) return false;
                } else if (hasSameName(category, input.getStr("file_name"), input.getStr("file_ext"), input.getLong("id"))) {
                    // 仅修改资料没有上传文件时，不借此覆盖另一条记录。
                    return false;
                }
                if (!input.update()) return false;
                deleteKeywordsByFileId(input.getId()); saveKeywords(input.getId(), keywordsStr);
                return true;
            });
            if (!ok) return moves.rollback("文件记录已变化或更新失败，请刷新后重试");
            committed = true;
            cleanupPublished(plan == null ? java.util.List.of() : java.util.List.of(plan), backups);
            if (old != null && !old.isBlank() && !java.util.Objects.equals(old, input.getStr("file_path")))
                deleteUnreferencedFile(old);
            return Ret.ok();
        } catch (Exception e) {
            return committed ? Ret.ok().set("cleanupWarning", e.getMessage()) : moves.rollback("更新失败：" + e.getMessage());
        }
    }
	
	/**
	 * 删除 指定多个ID
	 * @param ids
	 * @return
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids,true);
	}
	
	/**
	 * 删除数据后执行的回调
	 * 删除策略：仅删除关联的关键字记录（同事务内）；
	 * 物理文件删除移交 Controller 在事务提交后执行（afterCommit），
	 * 避免事务回滚时物理文件已被删除无法恢复
	 * @param dmsFile 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return null表示正常完成
	 */
	@Override
	protected String afterDelete(DmsFile dmsFile, Kv kv) {
		//addDeleteSystemLog(dmsFile.getId(), JBoltUserKit.getUserId(),dmsFile.getName());
		
		// 删除关联的关键字记录
		deleteKeywordsByFileId(dmsFile.getId());
		
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param dmsFile 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(DmsFile dmsFile, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(dmsFile, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}
	
	// -------------------------------------------------------------------------
	// 关键字管理方法
	// -------------------------------------------------------------------------
	
	/**
	 * 保存关键字
	 * @param fileId 文件ID
	 * @param keywordsStr 逗号分隔的关键字字符串
	 */
	public void saveKeywords(Long fileId, String keywordsStr) {
		if (StrKit.isBlank(keywordsStr) || fileId == null) {
			return;
		}
		String[] keywords = keywordsStr.split(",");
		List<DmsFileKeyword> keywordList = new ArrayList<>();
		for (String keyword : keywords) {
			String trimmed = keyword.trim();
			if (StrKit.notBlank(trimmed)) {
				DmsFileKeyword kw = new DmsFileKeyword();
				kw.setFileId(fileId);
				kw.setKeyword(trimmed);
				keywordList.add(kw);
			}
		}
		if (!keywordList.isEmpty()) {
			Db.batchSave(keywordList, keywordList.size());
		}
	}
	
	/**
	 * 删除指定文件的所有关键字
	 * @param fileId 文件ID
	 */
	public void deleteKeywordsByFileId(Long fileId) {
		if (fileId == null) {
			return;
		}
		Db.delete("DELETE FROM siargo_dms_file_keyword WHERE file_id = ?", fileId);
	}
	
	/**
	 * 获取指定文件的所有关键字（逗号分隔字符串）
	 * @param fileId 文件ID
	 * @return 逗号分隔的关键字字符串
	 */
	public String getKeywordsByFileId(Long fileId) {
		if (fileId == null) {
			return "";
		}
		List<DmsFileKeyword> keywords = keywordDao.find(
				"SELECT * FROM siargo_dms_file_keyword WHERE file_id = ?", fileId);
		if (keywords == null || keywords.isEmpty()) {
			return "";
		}
		return keywords.stream()
				.map(DmsFileKeyword::getKeyword)
				.filter(StrKit::notBlank)
				.collect(Collectors.joining(","));
	}
	
	/**
	 * 切换文件的生效状态
	 * 状态切换规则：is_active = 1 时切换为 0，is_active = 0 或 null 时切换为 1
	 * @param id 文件ID
	 * @return 操作结果
	 */
	public Ret toggleActive(Long id) {
		if (id == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		DmsFile dmsFile = findById(id);
		if (dmsFile == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		// 状态切换：1->0, 0或null->1
		Integer currentActive = dmsFile.getIsActive();
		dmsFile.setIsActive(currentActive != null && currentActive == 1 ? 0 : 1);
		dmsFile.setModifyDate(new java.util.Date());
		boolean success = dmsFile.update();
		return ret(success);
	}
	
    /** 上传前只读探测，返回确认所针对的记录和文件快照，不创建暂存目录。 */
    public Ret checkOverwrite(Long category, String filename) {
        synchronized (FILE_WRITE_LOCK) {
            try { return overwriteInfo(category, filename).prompt().set("state", "ok"); }
            catch (Exception e) { return fail("检查文件失败：" + e.getMessage()); }
        }
    }

    private record OverwriteInfo(String filename, java.nio.file.Path target, DmsFile existing,
                                 boolean conflict, String token, String rowState) {
        Ret prompt() {
            return Ret.ok().set("fileName", filename).set("requiresOverwrite", conflict)
                    .set("overwriteToken", token)
                    .set("targetId", existing == null ? null : existing.getLong("id").toString());
        }
    }

    private Ret overwriteRequired(List<Ret> conflicts) {
        return Ret.fail("存在同名文件，是否覆盖当前文件？")
                .set("requiresOverwrite", true).set("conflicts", conflicts);
    }

    private java.util.Map<String, String> parseOverwriteTokens(String json) {
        java.util.Map<String, String> result = new java.util.HashMap<>();
        if (StrKit.isBlank(json)) return result;
        com.alibaba.fastjson.JSONObject values = com.alibaba.fastjson.JSON.parseObject(json);
        if (values == null) return result;
        for (String filename : values.keySet()) result.put(SiargoStorage.safeSegment(filename), values.getString(filename));
        return result;
    }

    /** 同名不是拒绝条件；多条历史记录指向同一名称时不能擅自选择要覆盖哪一条。 */
    private DmsFile sameNameRecord(Long category, String filename, boolean lock) {
        List<DmsFile> matches = dao.find("SELECT * FROM siargo_dms_file WHERE category_id=? AND file_name=? "
                        + "AND COALESCE(file_ext,'')=? AND status=? LIMIT 2" + (lock ? " FOR UPDATE" : ""),
                category, getFileNameWithoutExt(filename), getFileExt(filename), STATUS_NORMAL);
        if (matches.size() > 1) throw new IllegalArgumentException("同名历史记录不止一条，请先明确保留的记录");
        return matches.isEmpty() ? null : matches.get(0);
    }

    private OverwriteInfo overwriteInfo(Long category, String filename) throws Exception {
        if (notOk(category)) throw new IllegalArgumentException("请选择文件类别");
        SiargoStorage.safeSegment(filename);
        SiargoStorage storage = storage();
        java.nio.file.Path target = newFilePath(category, filename);
        DmsFile existing = sameNameRecord(category, filename, false);
        boolean conflict = existing != null || Files.exists(target);
        String state = rowState(existing);
        // 确认绑定到完整记录和文件内容；确认后文件或资料变化时，必须重新询问，不能沿用旧确认。
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        digest.update((category + "\n" + filename + "\n" + state).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        java.util.Set<java.nio.file.Path> files = new java.util.LinkedHashSet<>();
        files.add(target);
        if (existing != null && StrKit.notBlank(existing.getStr("file_path"))) files.add(storage.resolveUrl(existing.getStr("file_path")));
        for (java.nio.file.Path file : files) {
            digest.update(file.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            if (Files.exists(file)) {
                if (!Files.isRegularFile(file)) throw new IllegalArgumentException("同名路径不是普通文件");
                digest.update((byte) 1);
                try (var input = Files.newInputStream(file)) {
                    byte[] buffer = new byte[65536]; int count;
                    while ((count = input.read(buffer)) != -1) digest.update(buffer, 0, count);
                }
            } else digest.update((byte) 0);
        }
        return new OverwriteInfo(filename, target, existing, conflict,
                conflict ? java.util.HexFormat.of().formatHex(digest.digest()) : "", state);
    }

    private String rowState(DmsFile row) {
        if (row == null) return "absent";
        // 排序后包含整条记录，避免确认期间更改关键业务属性而仍然通过旧快照。
        java.util.Map<String, Object> values = new java.util.TreeMap<>();
        for (String column : row._getAttrNames()) values.put(column, row.get(column));
        return com.alibaba.fastjson.JSON.toJSONString(values);
    }

    private boolean sameDatabaseTarget(OverwriteInfo plan, Long category) {
        return plan.rowState().equals(rowState(sameNameRecord(category, plan.filename(), true)));
    }

    private void preserveTarget(SiargoStorage storage, SiargoUploadFiles.Moves moves,
                                OverwriteInfo plan, List<java.nio.file.Path> backups) throws IOException {
        if (!Files.exists(plan.target())) return;
        Long allowedId = plan.existing() == null ? 0L : plan.existing().getLong("id");
        if (Db.queryLong("SELECT COUNT(*) FROM siargo_dms_file WHERE file_path=? AND id<>?",
                storage.toUrl(plan.target()), allowedId) > 0)
            throw new IOException("该物理文件被其他记录共用，不能直接覆盖");
        java.nio.file.Path backup = storage.path("temp", java.util.UUID.randomUUID().toString(), plan.filename());
        moves.move(plan.target(), backup);
        backups.add(backup);
    }

    /** 仅在数据库提交成功后丢弃恢复用暂存文件；失败交由 Moves 逆序恢复。 */
    private void cleanupPublished(List<OverwriteInfo> plans, List<java.nio.file.Path> backups) throws IOException {
        for (java.nio.file.Path backup : backups) storage().deleteFile(backup);
        for (OverwriteInfo plan : plans) {
            String old = plan.existing() == null ? null : plan.existing().getStr("file_path");
            if (StrKit.notBlank(old) && !storage().resolveUrl(old).equals(plan.target())) deleteUnreferencedFile(old);
        }
    }

    private void deleteUnreferencedFile(String url) throws IOException {
        if (Db.queryLong("SELECT COUNT(*) FROM siargo_dms_file WHERE file_path=?", url) == 0)
            storage().deleteFile(storage().resolveUrl(url));
    }

    /** 正式文件直接放在类别目录下，保留原名，不使用随机前缀或子目录。 */
    java.nio.file.Path newFilePath(Long category, String filename) {
        return storage().path(category.toString(), SiargoStorage.safeSegment(filename));
    }

    private boolean lockCategory(Long category) {
        return Db.findFirst("SELECT id FROM siargo_dms_category WHERE id=? FOR UPDATE", category) != null;
    }

    /** 必须持有类别行锁；当前读确保能看到此前并发事务刚提交的同名文件。 */
    private boolean hasSameName(Long category, String name, String extension, Long excludingId) {
        return Db.findFirst("SELECT id FROM siargo_dms_file WHERE category_id=? AND file_name=? "
                + "AND COALESCE(file_ext,'')=? AND status=? AND id<>? LIMIT 1 FOR UPDATE",
                category, name, extension == null ? "" : extension, STATUS_NORMAL, excludingId) != null;
    }

	// -------------------------------------------------------------------------
	// 文件路径安全与物理文件操作
	// -------------------------------------------------------------------------
	
	/**
	 * 校验临时文件路径安全性（路径穿越防护）
	 * 校验规则：禁止 ".."、强制临时目录前缀、canonical 路径二次确认
	 * @param path 前端传入的临时文件相对路径
	 * @return 校验通过的临时文件对象
	 * @throws IllegalArgumentException 路径非法或文件不存在
	 */
    public File validateTempFile(String path) {
        return SiargoUploadFiles.temp(storage(), path, true).toFile();
    }
	
	/**
	 * 根据ID列表查询对应的文件存储路径
	 * 业务场景：Controller 在删除事务提交前收集路径，提交后删除物理文件
	 * @param ids 逗号分隔的文件ID字符串
	 * @return 文件路径列表（非法ID自动忽略）
	 */
	public List<String> getFilePathsByIds(String ids) {
		List<String> paths = new ArrayList<>();
		if (StrKit.isBlank(ids)) {
			return paths;
		}
		List<Object> idParams = new ArrayList<>();
		for (String idStr : ids.split(",")) {
			try {
				idParams.add(Long.parseLong(idStr.trim()));
			} catch (NumberFormatException ignored) {
				// 非法ID忽略
			}
		}
		if (idParams.isEmpty()) {
			return paths;
		}
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < idParams.size(); i++) {
			placeholders.append(i == 0 ? "?" : ",?");
		}
		return Db.query("SELECT file_path FROM siargo_dms_file WHERE id IN (" + placeholders + ")", idParams.toArray());
	}
	
	/**
	 * 批量删除物理文件（供 Controller 在事务提交后调用）
	 * 失败仅记录日志，不影响业务结果
	 * @param filePaths 文件相对路径列表
	 */
    public void deletePhysicalFiles(List<String> paths) {
        Ret result = SiargoUploadFiles.delete(storage(), paths, false);
        if (result.isFail()) LOG.error("文件清理失败：" + result.get("failedFiles"));
    }
	

	
	/**
	 * 获取文件扩展名（不含点）
	 * @param fileName 文件名
	 * @return 扩展名（不含点），无扩展名时返回空字符串
	 */
	private String getFileExt(String fileName) {
		if (StrKit.isBlank(fileName)) {
			return "";
		}
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
	}
	
	/**
	 * 获取文件名（不含扩展名）
	 * @param fileName 文件名
	 * @return 不含扩展名的文件名
	 */
	private String getFileNameWithoutExt(String fileName) {
		if (StrKit.isBlank(fileName)) {
			return "";
		}
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
	}
	
}
