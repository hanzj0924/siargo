package cn.jbolt.admin.siargo.dms.file;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.DmsFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * 文件类别表管理 Controller
 * @ClassName: DmsFileAdminController
 * @author: hanzj
 * @date: 2026-03-23 13:45
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/dms/file", viewPath = "/_view/admin/siargo/dms/file")
//true
public class DmsFileAdminController extends JBoltBaseController {

	/** 文件管理服务 */
	@Inject
	private DmsFileService service;
	
	/** Web 根目录路径 */
	private static final String webRootPath = PathKit.getWebRootPath();
	/** 允许上传的文件扩展名集合（文档和图片类型） */
	private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
			"doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf",
			"jpg", "jpeg", "png", "gif", "bmp"
	));
	/** 文件上传路径前缀 */
	private static final String UPLOAD_PATH_PREFIX = "/upload/";
	
	/**
	 * 进入文件管理首页
	 * URL: GET /admin/siargo/dms/file
	 */
	public void index() {
		render("index.html");
	}
  	
	/**
	 * 获取文件数据源（按类别查询）
	 * URL: GET /admin/siargo/dms/file/datas
	 * 注：取消分页，返回全部数据（使用极大 pageSize 保持 Page 格式兼容性）
	 * @param categoryId 类别ID
	 * @param keywords 关键字（搜索文件名或关键字）
	 * @param isActive 生效状态筛选
	 * @param activeDate 生效日期筛选
	 * @return 文件列表数据JSON
	 */
	public void datas() {
		Long categoryId = getLong("categoryId");
		String keywords = getPara("keywords");
		Integer isActive = getInt("isActive");
		String activeDate = getPara("activeDate");
		
		renderJsonData(service.paginateAdminDatas(1, Integer.MAX_VALUE, 
				categoryId, keywords, isActive, activeDate));
	}
	
	/**
	 * 全局搜索文件（跨所有类别）
	 * URL: GET /admin/siargo/dms/file/globalSearch
	 * 业务场景：用户在首页搜索框输入关键字，检索所有类别下的匹配文件
	 * 注：取消分页，返回全部数据
	 * @param keywords 搜索关键字
	 * @return 匹配的文件列表JSON
	 */
	public void globalSearch() {
		String keywords = getPara("keywords");
		renderJsonData(service.paginateGlobalSearch(1, Integer.MAX_VALUE, keywords));
	}
	
	/**
	 * 进入失效文件列表页面
	 * URL: GET /admin/siargo/dms/file/inactiveList
	 * 失效文件：is_active = 0 且 status = 1 的文件记录
	 */
	public void inactiveList() {
		Page<Record> pageData = service.paginateInactiveDatas(getPageNumber(), getPageSize(), getKeywords());
		set("pageData", pageData);
		render("inactiveList.html");
	}
	
	/**
	 * 获取失效文件数据源（返回JSON）
	 * URL: GET /admin/siargo/dms/file/inactiveDatas
	 * @return 失效文件分页数据JSON
	 */
	public void inactiveDatas() {
		renderJsonData(service.paginateInactiveDatas(getPageNumber(), getPageSize(), getKeywords()));
	}
	
	/**
	 * 进入新增文件页面
	 * URL: GET /admin/siargo/dms/file/add
	 * @param categoryId 所属类别ID
	 */
	public void add() {
		Long categoryId = getLong("categoryId");
		set("categoryId", categoryId);
		set("dmsFile", new DmsFile());
		set("keywords", "");
		render("add.html");
	}
	
	/**
	 * 进入编辑文件页面
	 * URL: GET /admin/siargo/dms/file/edit/{id}
	 * @param id 文件ID（从URL路径获取）
	 */
	public void edit() {
		DmsFile dmsFile=service.findById(getLong(0)); 
		if(dmsFile == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		// 获取关键字
		String keywords = service.getKeywordsByFileId(dmsFile.getId());
		set("dmsFile", dmsFile);
		set("keywords", keywords);
		render("edit.html");
	}
	
	/**
	 * 上传文件到临时目录
	 * URL: POST /admin/siargo/dms/file/uploadFile
	 * 业务流程：接收上传文件 -> 校验文件类型 -> 保存到临时目录 -> 返回临时路径
	 * 临时目录：/upload/siargo/dms/temp/
	 * @return 临时文件路径JSON
	 */
	public void uploadFile() {
		String tempUploadPath = JBoltUploadFolder.SIARGO_UPLOAD_DMS + "/temp/";
		UploadFile uploadFile = getFile("file", tempUploadPath);
		if (uploadFile == null) {
			renderJsonFail("请选择文件后上传");
			return;
		}
		
		// 净化文件名：截取路径分隔符后的纯文件名，剔除路径穿越片段
		String originalFileName = uploadFile.getOriginalFileName();
		String fileName = sanitizeFileName(StrKit.notBlank(originalFileName) ? originalFileName : uploadFile.getFileName());
		if (StrKit.isBlank(fileName)) {
			uploadFile.getFile().delete();
			renderJsonFail("文件名不合法");
			return;
		}
		
		// 校验文件类型
		String extension = getFileExtension(fileName);
		if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
			// 删除不允许的文件
			uploadFile.getFile().delete();
			renderJsonFail("不支持的文件类型，仅允许: doc, docx, xls, xlsx, ppt, pptx, pdf, jpg, jpeg, png, gif, bmp");
			return;
		}
		
		// 重命名为净化后的原始文件名，并二次校验目标仍位于临时目录内
		File currentFile = uploadFile.getFile();
		File targetFile = new File(currentFile.getParent(), fileName);
		try {
			String canonicalParent = currentFile.getParentFile().getCanonicalPath();
			if (!targetFile.getCanonicalPath().startsWith(canonicalParent + File.separator)) {
				currentFile.delete();
				renderJsonFail("文件名不合法");
				return;
			}
		} catch (IOException e) {
			currentFile.delete();
			renderJsonFail("路径解析失败");
			return;
		}
		
		if (!currentFile.renameTo(targetFile)) {
			targetFile = currentFile;
		}
		
		String tempPath = UPLOAD_PATH_PREFIX + tempUploadPath + targetFile.getName();
		renderJsonData(tempPath);
	}
	
	/**
	 * 删除临时目录中的文件
	 * URL: POST /admin/siargo/dms/file/deleteTempFile
	 * 安全策略：仅允许删除临时目录下的文件，防止路径遍历攻击
	 * @param filePath 要删除的文件路径
	 * @return 操作结果JSON
	 */
	public void deleteTempFile() {
		String filePath = getPara("filePath");
		if (StrKit.isBlank(filePath)) {
			renderJsonFail("文件路径不能为空");
			return;
		}
		String normalizedPath = filePath.replace("\\", "/");
		String tempPrefix = "/upload/" + JBoltUploadFolder.SIARGO_UPLOAD_DMS + "/temp/";
		if (!normalizedPath.startsWith(tempPrefix)) {
			renderJsonFail("只能删除临时目录下的文件");
			return;
		}
		File file = new File(webRootPath + normalizedPath);
		try {
			String canonicalBase = new File(webRootPath + tempPrefix).getCanonicalPath();
			String canonicalFile = file.getCanonicalPath();
			if (!canonicalFile.startsWith(canonicalBase)) {
				renderJsonFail("只能删除临时目录下的文件");
				return;
			}
		} catch (IOException e) {
			renderJsonFail("路径解析失败");
			return;
		}
		if (!file.exists()) {
			renderJsonSuccess("文件不存在，已跳过");
			return;
		}
		if (file.delete()) {
			renderJsonSuccess("临时文件已删除");
		} else {
			renderJsonFail("删除失败，请重试");
		}
	}
	
	/**
	 * 文件下载
	 * URL: GET /admin/siargo/dms/file/download/{id}
	 * 业务流程：根据ID查找文件记录 -> 读取物理文件 -> 设置响应头 -> 输出文件流
	 * @param id 文件ID（从URL路径获取）
	 */
	public void download() {
		Long id = getLong(0);
		if (id == null) {
			renderJsonFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		
		DmsFile dmsFile = service.findById(id);
		if (dmsFile == null) {
			renderJsonFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		
		String filePath = dmsFile.getFilePath();
		File file = new File(webRootPath + filePath);
		if (!file.exists()) {
			renderJsonFail("文件不存在");
			return;
		}
		
		// 设置响应头
		String fileName = dmsFile.getFileName();
		String fileExt = dmsFile.getFileExt();
		if (StrKit.notBlank(fileExt) && !fileName.endsWith("." + fileExt)) {
			fileName = fileName + "." + fileExt;
		}
		
		try {
			String encodedFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			getResponse().setContentType("application/octet-stream");
			getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
			getResponse().setContentLength((int) file.length());
			
			try (FileInputStream fis = new FileInputStream(file);
				 OutputStream os = getResponse().getOutputStream()) {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = fis.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				os.flush();
			}
			renderNull();
		} catch (IOException e) {
			e.printStackTrace();
			renderJsonFail("文件下载失败: " + e.getMessage());
		}
	}
	
	/**
	 * 切换文件生效状态（列表开关按钮 / 失效列表恢复生效调用）
	 * URL: POST /admin/siargo/dms/file/toggleActive/{id}
	 * @param id 文件ID（从URL路径获取）
	 * @return 操作结果JSON
	 */
	public void toggleActive() {
		Long id = getLong(0);
		final Ret[] retHolder = {null};
		Db.tx(() -> {
			retHolder[0] = service.toggleActive(id);
			return retHolder[0].isOk();
		});
		if (retHolder[0] != null) {
			renderJson(retHolder[0]);
		} else {
			renderJsonFail("操作失败");
		}
	}
	
	/**
	 * 保存文件记录（支持批量文件保存）
	 * URL: POST /admin/siargo/dms/file/save
	 * 事务与文件移动逻辑由 Service 内部 Db.tx() 管理，
	 * 失败时数据库整体回滚且已移动的文件移回临时目录
	 * @return 操作结果JSON
	 */
	public void save() {
		DmsFile dmsFileTemplate = getModel(DmsFile.class, "dmsFile");
		String keywordsStr = getPara("keywords");
		String tempFilePath = getPara("tempFilePath");
		
		if (StrKit.isBlank(tempFilePath)) {
			renderJsonFail("请上传文件");
			return;
		}
		
		// 支持多文件：逗号分隔
		renderJson(service.saveBatch(dmsFileTemplate, keywordsStr, tempFilePath.split(",")));
	}
	
	/**
	 * 更新文件信息（支持替换物理文件）
	 * URL: POST /admin/siargo/dms/file/update
	 * 事务提交后再删除被替换的旧物理文件，避免回滚时文件无法恢复
	 * @param dmsFile 文件信息模型
	 * @param keywords 关键字（逗号分隔）
	 * @param tempFilePath 新上传的临时文件路径（可选，非空时替换原文件）
	 * @return 操作结果JSON
	 */
	public void update() {
		DmsFile dmsFile = getModel(DmsFile.class, "dmsFile");
		String keywordsStr = getPara("keywords");
		String tempFilePath = getPara("tempFilePath");
		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.update(dmsFile, keywordsStr, tempFilePath);
			return retHolder[0].isOk();
		});
		if (txOk && retHolder[0] != null) {
			// 事务提交后删除被替换的旧物理文件
			String oldFilePath = retHolder[0].getStr("oldFilePath");
			if (StrKit.notBlank(oldFilePath)) {
				service.deletePhysicalFiles(Collections.singletonList(oldFilePath));
			}
			renderJsonSuccess();
			return;
		}
		if (retHolder[0] != null) {
			renderJson(retHolder[0]);
		} else {
			renderJsonFail("更新失败");
		}
	}
	
	/**
	 * 批量删除文件
	 * URL: POST /admin/siargo/dms/file/deleteByIds
	 * 事务内仅删除数据库记录与关键字关联，
	 * 物理文件在事务提交后删除，避免回滚时文件无法恢复
	 * @param ids 文件ID列表（逗号分隔）
	 * @return 操作结果JSON
	 */
	public void deleteByIds() {
		String ids = get("ids");
		if (StrKit.isBlank(ids)) {
			renderJsonFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		// 事务前先收集物理文件路径（删除后无法再查）
		List<String> filePaths = service.getFilePathsByIds(ids);
		final Ret[] retHolder = {null};
		boolean txOk = Db.tx(() -> {
			retHolder[0] = service.deleteByBatchIds(ids);
			return retHolder[0].isOk();
		});
		if (txOk) {
			// 事务提交后删除物理文件
			service.deletePhysicalFiles(filePaths);
		}
		if (retHolder[0] != null) {
			renderJson(retHolder[0]);
		} else {
			renderJsonFail("删除失败");
		}
	}
	
	/**
	 * 获取文件扩展名（不带点）
	 * @param fileName 文件名
	 * @return 扩展名（小写，不含点）
	 */
	private String getFileExtension(String fileName) {
		if (StrKit.isBlank(fileName)) {
			return "";
		}
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
	}
	
	/**
	 * 净化上传文件名：只保留最后一个路径分隔符之后的纯文件名，剔除 .. 片段，防止路径穿越
	 * @param fileName 原始文件名（可能含路径）
	 * @return 净化后的纯文件名，不合法时返回空字符串
	 */
	private String sanitizeFileName(String fileName) {
		if (StrKit.isBlank(fileName)) {
			return "";
		}
		String name = fileName.replace("\\", "/");
		int slashIndex = name.lastIndexOf('/');
		if (slashIndex >= 0) {
			name = name.substring(slashIndex + 1);
		}
		name = name.replace("..", "").trim();
		return name;
	}
	
	
}
