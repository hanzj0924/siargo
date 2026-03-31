package cn.jbolt.admin.siargo.dms.file;

import com.jfinal.aop.Inject;
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
import java.util.Arrays;
import java.util.HashSet;
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

	@Inject
	private DmsFileService service;
	
	/** Web 根目录路径 */
	private static final String webRootPath = PathKit.getWebRootPath();
	/** 允许的文件扩展名 */
	private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
			"doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf",
			"jpg", "jpeg", "png", "gif", "bmp"
	));
	/** 上传路径前缀 */
	private static final String UPLOAD_PATH_PREFIX = "/upload/";
	
   /**
	* 首页
	*/
	public void index() {
		render("index.html");
	}
  	
  	/**
	* 数据源（按类别查询）
	* 注：取消分页，返回全部数据（使用极大 pageSize 保持 Page 格式兼容性）
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
	 * 全局搜索（跨所有类别）
	 * 注：取消分页，返回全部数据（使用极大 pageSize 保持 Page 格式兼容性）
	 */
	public void globalSearch() {
		String keywords = getPara("keywords");
		renderJsonData(service.paginateGlobalSearch(1, Integer.MAX_VALUE, keywords));
	}
	
	/**
	 * 失效文件列表页面
	 */
	public void inactiveList() {
		render("inactiveList.html");
	}
	
	/**
	 * 失效文件数据源
	 */
	public void inactiveDatas() {
		renderJsonData(service.paginateInactiveDatas(getPageNumber(), getPageSize(), getKeywords()));
	}
	
   /**
	* 新增
	*/
	public void add() {
		Long categoryId = getLong("categoryId");
		set("categoryId", categoryId);
		set("dmsFile", new DmsFile());
		set("keywords", "");
		render("add.html");
	}
	
   /**
	* 编辑
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
	 * 删除临时文件
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
	 */
	@Before(Tx.class)
	public void toggleActive() {
		renderJson(service.toggleActive(getLong(0)));
	}
	
	/**
	 * 切换文件是否生效（前端开关按钮调用）
	 */
	@Before(Tx.class)
	public void changeActive() {
		renderJson(service.toggleActive(getLong(0)));
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		DmsFile dmsFile = getModel(DmsFile.class, "dmsFile");
		String keywordsStr = getPara("keywords");
		String tempFilePath = getPara("tempFilePath");
		
		// 临时文件到正式目录
		if (StrKit.notBlank(tempFilePath)) {
			String normalizedPath = tempFilePath.replace("\\", "/");
			File tempFile = new File(webRootPath + normalizedPath);
			if (!tempFile.exists() || !tempFile.isFile()) {
				renderJsonFail("临时文件不存在");
				return;
			}
			
			// 目标目录：/upload/dms/{categoryId}/{YYYYMM}/
			String targetDir = UPLOAD_PATH_PREFIX + JBoltUploadFolder.SIARGO_UPLOAD_DMS + "/" 
					+ dmsFile.getCategoryId() + "/" ;
			File targetFolder = new File(webRootPath + targetDir);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}
			
			String targetPath = targetDir + tempFile.getName();
			File targetFile = new File(webRootPath + targetPath);
			
			try {
				Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				renderJsonFail("文件移动失败: " + e.getMessage());
				return;
			}
			
			// 设置文件路径和扩展名
			dmsFile.setFilePath(targetPath);
			dmsFile.setFileExt(getFileExtension(tempFile.getName()));
			// 文件名去掉后缀
			String originalName = tempFile.getName();
			String nameWithoutExt = originalName.contains(".") ? originalName.substring(0, originalName.lastIndexOf(".")) : originalName;
			dmsFile.setFileName(nameWithoutExt);
		}
		
		// 设置上传时间和上传者
		dmsFile.set("upload_time", DateUtil.getDateString(DateUtil.YMDHMS));
		dmsFile.set("uploader_id", JBoltUserKit.getUserId());
		
		renderJson(service.save(dmsFile, keywordsStr));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		DmsFile dmsFile = getModel(DmsFile.class, "dmsFile");
		String keywordsStr = getPara("keywords");
		renderJson(service.update(dmsFile, keywordsStr));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
	/**
	 * 获取文件扩展名（不带点）
	 */
	private String getFileExtension(String fileName) {
		if (StrKit.isBlank(fileName)) {
			return "";
		}
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
	}
	
	
}
