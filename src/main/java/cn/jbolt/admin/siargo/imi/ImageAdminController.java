package cn.jbolt.admin.siargo.imi;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Image;

/**
 * 来料到货单管理 Controller
 * 
 * @ClassName: ImageAdminController
 * @author: hanzj
 * @date: 2026-01-30 16:19
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/imi", viewPath = "/_view/admin/siargo/imi")
//true
public class ImageAdminController extends JBoltBaseController {

	@Inject
	private ImageService service;

	/**
	 * 上传图片到临时文件
	 */
	public void uploadImages() {

		String tempUploadPath = JBoltUploadFolder.SIARGO_UPLOAD_IMI + File.separator + "temp" + File.separator;
		List<UploadFile> files = getFiles(tempUploadPath);
		if (files == null || files.size() == 0) {
			renderJsonFail("请选择图片后上传");
			return;
		}
		
		StringBuilder msg = new StringBuilder();
		files.forEach(file -> {
			if (notImage(file)) {
				msg.append(file.getOriginalFileName() + "不是图片类型文件;");
			}
		});
		if (msg.length() > 0) {
			renderJsonFail(msg.toString());
			return;
		}
		
		List<String> retFiles = new ArrayList<String>();
		StringBuilder errormsg = new StringBuilder();
		String tempPath =File.separator + "upload" + File.separator ;
		for (UploadFile uploadFile : files) {
			try {
	            // 获取原始文件名
	            String originalFileName = uploadFile.getOriginalFileName();
	            // 如果原始文件名为空，则使用当前文件名
	            String fileName = originalFileName != null ? originalFileName : uploadFile.getFileName();
	            
	            // 获取当前文件的完整路径
	            File currentFile = uploadFile.getFile();
	            // 创建目标文件对象（使用原始文件名）
	            File targetFile = new File(currentFile.getParent(), fileName);
	            
	            // 重命名文件
	            if (currentFile.renameTo(targetFile)) {
	                // 重命名成功，使用新的文件名构建返回路径
	                retFiles.add(tempPath + tempUploadPath + fileName);
	            } else {
	                // 重命名失败，使用原文件名
	                errormsg.append("文件 " + uploadFile.getFileName() + " 重命名失败;");
	                retFiles.add(tempPath + tempUploadPath + uploadFile.getFileName());
	            }
	        } catch (Exception e) {
	            // 发生异常，使用原文件名
	            errormsg.append("文件 " + uploadFile.getFileName() + " 处理失败: " + e.getMessage() + ";");
	        }
		}
		if (retFiles.size() == 0) {
			renderJsonFail(errormsg.toString());
			return;
		}
		renderJsonData(retFiles, errormsg.toString());
	}

	/**
	 * 首页
	 */
	public void index() {
		render("index.html");
	}

	/**
	 * 数据源
	 */
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(), getPageSize(), getKeywords(), getPara("supplierId")));
	}

	/**
	 * 新增
	 */
	public void add() {
		render("add.html");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Image image = service.findById(getLong(0));
		if (image == null) {
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("image", image);
		render("edit.html");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Image image = getModel(Image.class, "image");
		String uploadPathJson = getPara("imgUploadUrl");
		List<String> uploadPaths = Arrays.stream(uploadPathJson.split(","))
				.map(String::trim)
				.map(String::valueOf)
				.collect(Collectors.toList());
		
		for (int i = 0; i < uploadPaths.size() && i < uploadPaths.size(); i++) {
			service.save(image,uploadPaths.get(i));
		}
		renderJsonSuccess();
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Image.class, "image"), null));
	}

	/**
	 * 批量删除
	 */
	@Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}

}
