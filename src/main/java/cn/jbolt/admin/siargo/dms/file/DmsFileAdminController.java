package cn.jbolt.admin.siargo.dms.file;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.common.util.DateUtil;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.DmsFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
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
		
		// 校验文件类型
		String originalFileName = uploadFile.getOriginalFileName();
		String extension = getFileExtension(originalFileName);
		if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
			// 删除不允许的文件
			uploadFile.getFile().delete();
			renderJsonFail("不支持的文件类型，仅允许: doc, docx, xls, xlsx, ppt, pptx, pdf, jpg, jpeg, png, gif, bmp");
			return;
		}
		
		// 重命名为原始文件名
		File currentFile = uploadFile.getFile();
		String fileName = StrKit.notBlank(originalFileName) ? originalFileName : uploadFile.getFileName();
		File targetFile = new File(currentFile.getParent(), fileName);
		
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
	 * 切换文件生效状态
	 * URL: POST /admin/siargo/dms/file/toggleActive/{id}
	 * @param id 文件ID（从URL路径获取）
	 * @return 操作结果JSON
	 */
	@Before(Tx.class)
	public void toggleActive() {
		renderJson(service.toggleActive(getLong(0)));
	}
	
	/**
	 * 切换文件是否生效（前端开关按钮调用）
	 * URL: POST /admin/siargo/dms/file/changeActive/{id}
	 * @param id 文件ID（从URL路径获取）
	 * @return 操作结果JSON
	 */
	@Before(Tx.class)
	public void changeActive() {
		renderJson(service.toggleActive(getLong(0)));
	}
	
	/**
	 * 保存文件记录（支持批量文件保存）
	 * URL: POST /admin/siargo/dms/file/save
	 * 业务流程：
	 * 1. 解析临时文件路径（支持多个文件，逗号分隔）
	 * 2. 将临时文件移动到正式目录（/upload/siargo/dms/{categoryId}/）
	 * 3. 创建文件记录并保存到数据库
	 * 4. 保存文件关键字关联
	 * 5. 异常时回滚：将已移动的文件移回临时目录
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void save() {
		DmsFile dmsFileTemplate = getModel(DmsFile.class, "dmsFile");
		String keywordsStr = getPara("keywords");
		String tempFilePath = getPara("tempFilePath");
		
		if (StrKit.isBlank(tempFilePath)) {
			renderJsonFail("请上传文件");
			return;
		}
		
		// 支持多文件：逗号分隔
		String[] tempPaths = tempFilePath.split(",");
		// 记录已移动的文件，异常时尝试回滚（移回 temp 目录）
		List<File[]> movedFiles = new ArrayList<>();
		
		try {
			for (String path : tempPaths) {
				String singlePath = path.trim();
				if (StrKit.isBlank(singlePath)) continue;
				
				String normalizedPath = singlePath.replace("\\", "/");
				File tempFile = new File(webRootPath + normalizedPath);
				if (!tempFile.exists() || !tempFile.isFile()) {
					throw new RuntimeException("临时文件不存在: " + tempFile.getName());
				}
				
				// 目标目录
				String targetDir = UPLOAD_PATH_PREFIX + JBoltUploadFolder.SIARGO_UPLOAD_DMS + "/"
						+ dmsFileTemplate.getCategoryId() + "/";
				File targetFolder = new File(webRootPath + targetDir);
				if (!targetFolder.exists()) {
					targetFolder.mkdirs();
				}
				
				String targetPath = targetDir + tempFile.getName();
				File targetFile = new File(webRootPath + targetPath);
				
				try {
					Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					movedFiles.add(new File[]{targetFile, tempFile});
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("文件移动失败: " + e.getMessage());
				}
				
				// 创建新的 DmsFile 记录
				DmsFile dmsFile = new DmsFile();
				dmsFile.setCategoryId(dmsFileTemplate.getCategoryId());
				dmsFile.setActiveDate(dmsFileTemplate.getActiveDate());
				dmsFile.setDescription(dmsFileTemplate.getDescription());
				Integer isActive = dmsFileTemplate.getIsActive();
				dmsFile.setIsActive(isActive != null ? isActive : 1);
				dmsFile.setFilePath(targetPath);
				dmsFile.setFileExt(getFileExtension(tempFile.getName()));
				
				// 文件名去掉后缀
				String originalName = tempFile.getName();
				String nameWithoutExt = originalName.contains(".")
						? originalName.substring(0, originalName.lastIndexOf("."))
						: originalName;
				dmsFile.setFileName(nameWithoutExt);
				
				// 设置上传时间和上传者
				dmsFile.setUploadTime(new java.util.Date());
				dmsFile.setUploaderId(JBoltUserKit.getUserId());
				dmsFile.setStatus(1);
				
				boolean success = dmsFile.save();
				if (!success) {
					throw new RuntimeException("数据库保存失败");
				}
				
				// 保存关键字
				service.saveKeywords(dmsFile.getId(), keywordsStr);
			}
		} catch (RuntimeException e) {
			// 尝试将已移动的文件回滚到 temp 目录
			for (File[] pair : movedFiles) {
				File moved = pair[0];
				File original = pair[1];
				try {
					if (moved.exists()) {
						Files.move(moved.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			renderJsonFail(e.getMessage());
			return;
		}
		
		renderJsonSuccess();
	}
	
	/**
	 * 更新文件信息
	 * URL: POST /admin/siargo/dms/file/update
	 * @param dmsFile 文件信息模型
	 * @param keywords 关键字（逗号分隔）
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void update() {
		DmsFile dmsFile = getModel(DmsFile.class, "dmsFile");
		String keywordsStr = getPara("keywords");
		renderJson(service.update(dmsFile, keywordsStr));
	}
	
	/**
	 * 批量删除文件
	 * URL: POST /admin/siargo/dms/file/deleteByIds
	 * @param ids 文件ID列表（逗号分隔）
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
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
	
	
}
