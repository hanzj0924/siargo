package cn.jbolt.admin.siargo.dms.file;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.io.File;
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

import cn.jbolt.common.util.DateUtil;
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
	private static final Log LOG = Log.getLog(DmsFileService.class);
	private final DmsFile dao=new DmsFile().dao();
	private final DmsFileKeyword keywordDao = new DmsFileKeyword().dao();
	
	public static final int STATUS_NORMAL = 1;
	public static final int STATUS_DELETED = 0;
	
	@Override
	protected DmsFile dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param categoryId 类别ID（必传）
	 * @param keywords 关键字（同时搜索 file_name 和 keyword）
	 * @param isActive 生效状态（可选）
	 * @param activeDate 生效日期（可选）
	 * @return
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, Long categoryId,
			String keywords, Integer isActive, String activeDate) {
		if (categoryId == null) {
			return new Page<Record>();
		}
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT f.id AS id, f.category_id AS categoryId, ")
				.append("f.file_name AS fileName, f.file_path AS filePath, f.file_ext AS fileExt, ")
				.append("f.description AS description, f.modify_date AS modifyDate, ")
				.append("f.is_active AS isActive, f.active_date AS activeDate, ")
				.append("f.upload_time AS uploadTime, ju.name AS uploaderName, f.status AS status, ")
				.append("GROUP_CONCAT(DISTINCT k.keyword ORDER BY k.id SEPARATOR ',') AS keywords");
		
		StringBuilder fromSql = new StringBuilder();
		fromSql.append(" FROM siargo_dms_file f ")
				.append("LEFT JOIN jb_user ju ON ju.id = f.uploader_id ")
				.append("LEFT JOIN siargo_dms_file_keyword k ON k.file_id = f.id ")
				.append("WHERE f.category_id = ? AND f.status = ?");
		
		List<Object> params = new ArrayList<>();
		params.add(categoryId);
		params.add(STATUS_NORMAL);
		
		// 关键字搜索：同时匹配 file_name 和 keyword
		if (StrKit.notBlank(keywords)) {
			fromSql.append(" AND (f.file_name LIKE ? OR k.keyword LIKE ?)");
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
		
		return Db.paginate(pageNumber, pageSize, selectSql.toString(), 
				fromSql.toString(), params.toArray());
	}
	
	/**
	 * 全局搜索（跨所有类别）
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param keywords 关键字
	 * @return
	 */
	public Page<Record> paginateGlobalSearch(int pageNumber, int pageSize, String keywords) {
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT f.id AS id, f.category_id AS categoryId, ")
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
		
		// 关键字搜索：同时匹配 file_name 和 keyword
		if (StrKit.notBlank(keywords)) {
			fromSql.append(" AND (f.file_name LIKE ? OR k.keyword LIKE ?)");
			params.add("%" + keywords + "%");
			params.add("%" + keywords + "%");
		}
		
		fromSql.append(" GROUP BY f.id, f.category_id, f.file_name, f.file_path, f.file_ext, ")
			   .append("f.description, f.modify_date, f.is_active, f.active_date, ")
			   .append("f.upload_time, f.uploader_id, f.status, c.name ")
			   .append("ORDER BY f.is_active DESC, f.active_date DESC");
		
		return Db.paginate(pageNumber, pageSize, selectSql.toString(), 
				fromSql.toString(), params.toArray());
	}
	
	/**
	 * 失效文件分页查询（is_active = 0 且 status = 1）
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param keywords 关键字（搜索文件名）
	 * @return
	 */
	public Page<Record> paginateInactiveDatas(int pageNumber, int pageSize, String keywords) {
		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT f.id AS id, f.category_id AS categoryId, ")
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
	 * 保存
	 * @param dmsFile
	 * @param keywordsStr 逗号分隔的关键字字符串
	 * @return
	 */
	public Ret save(DmsFile dmsFile, String keywordsStr) {
		if(dmsFile==null || isOk(dmsFile.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		dmsFile.setStatus(STATUS_NORMAL);
		
		boolean success=dmsFile.save();
		if(success) {
			saveKeywords(dmsFile.getId(), keywordsStr);
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param dmsFile
	 * @param keywordsStr 逗号分隔的关键字字符串
	 * @return
	 */
	public Ret update(DmsFile dmsFile, String keywordsStr) {
		if(dmsFile==null || notOk(dmsFile.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		DmsFile dbDmsFile=findById(dmsFile.getId());
		if(dbDmsFile==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		
		boolean success=dmsFile.update();
		if(success) {
			// 先删除旧关键字，再重新插入新关键字
			deleteKeywordsByFileId(dmsFile.getId());
			saveKeywords(dmsFile.getId(), keywordsStr);
		}
		return ret(success);
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
	 * @param dmsFile 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(DmsFile dmsFile, Kv kv) {
		//addDeleteSystemLog(dmsFile.getId(), JBoltUserKit.getUserId(),dmsFile.getName());
		
		// 删除关联的关键字记录
		deleteKeywordsByFileId(dmsFile.getId());
		
		// 删除物理文件
		String filePath = dmsFile.getFilePath();
		if (StrKit.notBlank(filePath)) {
			try {
				String physicalPath = PathKit.getWebRootPath() + filePath;
				File file = new File(physicalPath);
				if (file.exists() && file.isFile()) {
					boolean deleted = file.delete();
					if (deleted) {
						LOG.info("物理文件删除成功: " + physicalPath);
					} else {
						LOG.warn("物理文件删除失败: " + physicalPath);
					}
				}
			} catch (Exception e) {
				LOG.error("删除物理文件异常: " + filePath, e);
			}
		}
		
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
	 * @param id 文件ID
	 * @return
	 */
	public Ret toggleActive(Long id) {
		if (id == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		DmsFile dmsFile = findById(id);
		if (dmsFile == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		// 切换状态：1->0, 0->1
		Integer currentActive = dmsFile.getIsActive();
		dmsFile.setIsActive(currentActive != null && currentActive == 1 ? 0 : 1);
		dmsFile.setModifyDate(new java.util.Date());
		boolean success = dmsFile.update();
		return ret(success);
	}
	
}