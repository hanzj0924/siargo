package cn.jbolt.admin.siargo.dms.file;

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
	/** 文件数据访问对象 */
	private final DmsFile dao=new DmsFile().dao();
	/** 文件关键字数据访问对象 */
	private final DmsFileKeyword keywordDao = new DmsFileKeyword().dao();
	
	/** 正常状态：文件有效 */
	public static final int STATUS_NORMAL = 1;
	/** 删除状态：文件已标记删除 */
	public static final int STATUS_DELETED = 0;
	/** 文件上传路径前缀 */
	public static final String UPLOAD_PATH_PREFIX = "/upload/";
	/** 临时目录相对路径前缀 */
	public static final String TEMP_PATH_PREFIX = UPLOAD_PATH_PREFIX + JBoltUploadFolder.SIARGO_UPLOAD_DMS + "/temp/";
	
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
		if (template == null || notOk(template.getCategoryId()) || tempPaths == null || tempPaths.length == 0) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		String webRootPath = PathKit.getWebRootPath();
		// 事务外先做全部临时路径的安全校验
		List<File> tempFiles = new ArrayList<>();
		for (String path : tempPaths) {
			String singlePath = path == null ? "" : path.trim();
			if (StrKit.isBlank(singlePath)) {
				continue;
			}
			try {
				tempFiles.add(validateTempFile(singlePath));
			} catch (IllegalArgumentException e) {
				return fail(e.getMessage());
			}
		}
		if (tempFiles.isEmpty()) {
			return fail("请上传文件");
		}
		
		String targetDir = UPLOAD_PATH_PREFIX + JBoltUploadFolder.SIARGO_UPLOAD_DMS + "/" + template.getCategoryId() + "/";
		File targetFolder = new File(webRootPath + targetDir);
		if (!targetFolder.exists() && !targetFolder.mkdirs()) {
			return fail("创建目标目录失败");
		}
		
		// 记录已移动的文件对 [目标文件, 原临时文件]，事务失败时移回
		List<File[]> movedFiles = new ArrayList<>();
		final String[] errorMsg = {null};
		boolean txOk = Db.tx(() -> {
			for (File tempFile : tempFiles) {
				String targetPath = targetDir + tempFile.getName();
				File targetFile = new File(webRootPath + targetPath);
				try {
					Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					movedFiles.add(new File[]{targetFile, tempFile});
				} catch (IOException e) {
					LOG.error("文件移动失败: " + tempFile.getName(), e);
					errorMsg[0] = "文件移动失败: " + tempFile.getName();
					return false;
				}
				
				DmsFile dmsFile = new DmsFile();
				dmsFile.setCategoryId(template.getCategoryId());
				dmsFile.setActiveDate(template.getActiveDate());
				dmsFile.setDescription(template.getDescription());
				Integer isActive = template.getIsActive();
				dmsFile.setIsActive(isActive != null ? isActive : 1);
				dmsFile.setFilePath(targetPath);
				dmsFile.setFileExt(getFileExt(tempFile.getName()));
				dmsFile.setFileName(getFileNameWithoutExt(tempFile.getName()));
				dmsFile.setUploadTime(new java.util.Date());
				dmsFile.setUploaderId(JBoltUserKit.getUserId());
				dmsFile.setStatus(STATUS_NORMAL);
				if (!dmsFile.save()) {
					errorMsg[0] = "数据库保存失败";
					return false;
				}
				saveKeywords(dmsFile.getId(), keywordsStr);
			}
			return true;
		});
		if (!txOk) {
			// 数据库已回滚，物理文件移回临时目录，保持文件与记录一致
			moveFilesBack(movedFiles);
			return fail(errorMsg[0] != null ? errorMsg[0] : "保存失败");
		}
		return SUCCESS;
	}
	
	/**
	 * 更新文件信息（支持替换物理文件）
	 * 业务场景：编辑文件基本信息，同时更新关键字关联（先删后插）；
	 * tempFilePath 非空时将新文件移入正式目录并更新文件元数据，
	 * 旧物理文件路径通过返回 Ret 的 oldFilePath 携带，由 Controller 在事务提交后删除
	 * @param dmsFile 文件信息模型
	 * @param keywordsStr 逗号分隔的关键字字符串
	 * @param tempFilePath 新上传的临时文件路径（可选，非空时替换原文件）
	 * @return 操作结果，成功且替换了文件时携带 oldFilePath
	 */
	public Ret update(DmsFile dmsFile, String keywordsStr, String tempFilePath) {
		if(dmsFile==null || notOk(dmsFile.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		DmsFile dbDmsFile=findById(dmsFile.getId());
		if(dbDmsFile==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		
		String oldFilePath = null;
		File movedTarget = null;
		File movedSource = null;
		// 替换文件：校验临时路径 -> 移入正式目录 -> 更新文件元数据
		if (StrKit.notBlank(tempFilePath)) {
			File tempFile;
			try {
				tempFile = validateTempFile(tempFilePath.trim());
			} catch (IllegalArgumentException e) {
				return fail(e.getMessage());
			}
			Long categoryId = dmsFile.getCategoryId() != null ? dmsFile.getCategoryId() : dbDmsFile.getCategoryId();
			String webRootPath = PathKit.getWebRootPath();
			String targetDir = UPLOAD_PATH_PREFIX + JBoltUploadFolder.SIARGO_UPLOAD_DMS + "/" + categoryId + "/";
			File targetFolder = new File(webRootPath + targetDir);
			if (!targetFolder.exists() && !targetFolder.mkdirs()) {
				return fail("创建目标目录失败");
			}
			String targetPath = targetDir + tempFile.getName();
			File targetFile = new File(webRootPath + targetPath);
			try {
				Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				LOG.error("替换文件移动失败: " + tempFile.getName(), e);
				return fail("文件移动失败: " + tempFile.getName());
			}
			movedTarget = targetFile;
			movedSource = tempFile;
			oldFilePath = dbDmsFile.getFilePath();
			dmsFile.setFilePath(targetPath);
			dmsFile.setFileExt(getFileExt(tempFile.getName()));
			dmsFile.setFileName(getFileNameWithoutExt(tempFile.getName()));
			dmsFile.setUploadTime(new java.util.Date());
			dmsFile.setUploaderId(JBoltUserKit.getUserId());
		}
		
		boolean success=dmsFile.update();
		if(!success) {
			// 数据库更新失败，新文件移回临时目录
			if (movedTarget != null) {
				List<File[]> pairs = new ArrayList<>();
				pairs.add(new File[]{movedTarget, movedSource});
				moveFilesBack(pairs);
			}
			return fail("更新失败");
		}
		// 先删除旧关键字，再重新插入新关键字
		deleteKeywordsByFileId(dmsFile.getId());
		saveKeywords(dmsFile.getId(), keywordsStr);
		
		Ret ret = Ret.ok();
		// 旧文件与新文件路径不同时，携带旧路径供 Controller 在事务提交后删除物理文件
		if (oldFilePath != null && !oldFilePath.equals(dmsFile.getFilePath())) {
			ret.set("oldFilePath", oldFilePath);
		}
		return ret;
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
		if (StrKit.isBlank(path)) {
			throw new IllegalArgumentException("文件路径不能为空");
		}
		String normalized = path.replace("\\", "/").trim();
		// 禁止路径穿越
		if (normalized.contains("..")) {
			throw new IllegalArgumentException("非法文件路径");
		}
		// 必须以临时目录前缀开头
		if (!normalized.startsWith(TEMP_PATH_PREFIX)) {
			throw new IllegalArgumentException("只能操作临时目录下的文件");
		}
		String webRootPath = PathKit.getWebRootPath();
		File file = new File(webRootPath + normalized);
		try {
			String canonicalBase = new File(webRootPath + TEMP_PATH_PREFIX).getCanonicalPath() + File.separator;
			if (!file.getCanonicalPath().startsWith(canonicalBase)) {
				throw new IllegalArgumentException("非法文件路径");
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("路径解析失败");
		}
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("临时文件不存在: " + file.getName());
		}
		return file;
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
	public void deletePhysicalFiles(List<String> filePaths) {
		if (filePaths == null || filePaths.isEmpty()) {
			return;
		}
		String webRootPath = PathKit.getWebRootPath();
		for (String filePath : filePaths) {
			if (StrKit.isBlank(filePath)) {
				continue;
			}
			try {
				File file = new File(webRootPath + filePath);
				if (file.exists() && file.isFile()) {
					if (file.delete()) {
						LOG.info("物理文件删除成功: " + filePath);
					} else {
						LOG.warn("物理文件删除失败: " + filePath);
					}
				}
			} catch (Exception e) {
				LOG.error("删除物理文件异常: " + filePath, e);
			}
		}
	}
	
	/**
	 * 将已移动的文件移回原位置（事务失败时的物理文件回滚）
	 * @param movedFiles 文件对列表，每项为 [当前位置, 原位置]
	 */
	private void moveFilesBack(List<File[]> movedFiles) {
		for (File[] pair : movedFiles) {
			try {
				if (pair[0].exists()) {
					Files.move(pair[0].toPath(), pair[1].toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (IOException e) {
				LOG.error("回滚移动文件失败: " + pair[0].getName(), e);
			}
		}
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