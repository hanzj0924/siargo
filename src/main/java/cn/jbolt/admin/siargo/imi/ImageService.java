package cn.jbolt.admin.siargo.imi;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

import cn.hutool.core.io.FileUtil;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.common.util.DateUtil;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.siargo.model.Image;
import cn.jbolt.siargo.model.Product;

/**
 * 来料到货单管理 Service
 * 
 * @ClassName: ImageService
 * @author: hanzj
 * @date: 2026-01-30 16:19
 */
public class ImageService extends JBoltBaseService<Image> {
	private final Image dao = new Image().dao();

	@Override
	protected Image dao() {
		return dao;
	}

	public static final int STATUS_NORMAL = 1;
	public static final int STATUS_DELETED = 0;
	public static final String webRootPath = PathKit.getWebRootPath();
	public static final String localPath = File.separator + "upload" + 
			File.separator + JBoltUploadFolder.SIARGO_UPLOAD_IMI +  File.separator ;

	/**
	 * 后台管理分页查询
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param prodType
	 * @param insp
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, String keywords, String supplierId) {
		Sql sql = Sql.mysql()
				.select("si.id" ,"si.supplier_id" , "si.storage_name", "si.file_path", "si.md5_hash",
						"si.description", "si.upload_time","si.updated_time", "si.status", "si.deleted_time", 
						"jd.name AS supplier_name",
						"ju_uploader.name AS uploader_name", "ju_update.name AS update_name")
				.page(pageNumber, pageSize)
				.from("siargo_image", "si")
				.leftJoin("jb_user", "ju_uploader", "ju_uploader.id = si.uploader_id")
				.leftJoin("jb_user", "ju_update", "ju_update.id = si.update_id")
				.leftJoin("jb_dictionary", "jd", "jd.type_key = 'siargo_supplier'  "
						+ "AND jd.sn COLLATE utf8mb4_general_ci = CAST(si.supplier_id AS CHAR) "
						+ "AND jd.enable = '1' ")
				.like("si.supplier_id", supplierId)
				.like("si.storage_name", keywords)
				.eq("si.status", STATUS_NORMAL)
				.orderBy("si.upload_time", true);
		;
		return paginateRecord(sql, true);
	}

	/**
	 * 保存
	 * 
	 * @param image
	 * @return
	 */
	public Ret save(Image image, String tempPath) {
		Image dbImage = new Image();
		File tempFile = new File(webRootPath + tempPath);
		String path = localPath + dicIdFindBySn(image.getSupplierId()).getStr("id") + 
						File.separator + DateUtil.getNowStr(DateUtil.YM) + File.separator;
		
		File locaFolder = new File(webRootPath + path);
		if (!locaFolder.exists()) {
			locaFolder.mkdirs();
		}

		// 计算MD5（用于去重）
		String md5 = getMd5(tempFile);

		// 检查图片是否已存在
		Image existImage = findByMd5(md5); 
		if (existImage != null){ 
			return fail("图片 " + existImage.getStorageName() + " 已经存在！"); 
		}
		 
		String filePath = FileUtil.normalize(path + tempFile.getName());
		dbImage.set("supplier_id", image.getSupplierId());
		dbImage.set("storage_name", getFileName(tempFile));
		dbImage.set("file_path", filePath);
		dbImage.set("md5_hash", md5);
		dbImage.set("description", image.getDescription());
		dbImage.set("upload_time", DateUtil.getDateString(DateUtil.YMDHMS));
		dbImage.set("uploader_id", JBoltUserKit.getUserId());
		dbImage.set("status", STATUS_NORMAL);

		boolean success = dbImage.save();
		if (!success) {
			return fail("数据保存失败");
		}
		
		try {
			Files.move(
					tempFile.toPath(),
					Paths.get(webRootPath, path + tempFile.getName()),
					StandardCopyOption.REPLACE_EXISTING
				);
		} catch (IOException e) {
			e.printStackTrace();
			return fail("文件移动失败：" + e.getMessage());
		}
		
		return ret(true).set("filePath", filePath);
	}

	/**
	 * 批量保存（带事务和文件清理）
	 * 上传过程中任一图片报错，回滚数据库并删除本次已上传的所有图片
	 */
	public Ret saveBatch(Image image, List<String> tempPaths) {
		List<String> movedFilePaths = new ArrayList<>();
		final String[] errorMsg = {null};
		
		boolean txSuccess = Db.tx(() -> {
			for (int i = 0; i < tempPaths.size(); i++) {
				Ret ret;
				try {
					ret = save(image, tempPaths.get(i));
				} catch (Exception e) {
					ret = fail("保存第" + (i + 1) + "张图片时异常：" + e.getMessage());
				}
				if (ret.isFail()) {
					errorMsg[0] = (String) ret.get("msg");
					// 清理已移动到目标位置的文件
					for (String filePath : movedFilePaths) {
						File f = new File(webRootPath + filePath);
						if (f.exists()) f.delete();
					}
					// 清理当前及剩余的临时文件
					for (int j = i; j < tempPaths.size(); j++) {
						File tempFile = new File(webRootPath + tempPaths.get(j));
						if (tempFile.exists()) tempFile.delete();
					}
					return false; // 回滚事务
				}
				movedFilePaths.add((String) ret.get("filePath"));
			}
			return true; // 提交事务
		});
		
		if (!txSuccess) {
			return fail(errorMsg[0] != null ? errorMsg[0] : "保存失败");
		}
		return ret(true);
	}

	/**
	 * 计算文件的 MD5 值
	 * 
	 * @param file 文件对象
	 * @return MD5 字符串
	 */
	public String getMd5(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			return DigestUtils.md5Hex(fis);
		} catch (IOException e) {
			throw new RuntimeException("计算文件MD5失败", e);
		}
	}
	
	/**
	 * 获取文件名
	 * 
	 * @param file 文件对象
	 * @return MD5 字符串
	 */
	public String getFileName(File file) {
		//去文件名掉后缀
		 String fileName = file.getName();
		 String fileNameWithoutExtension;

		 int dotIndex = fileName.lastIndexOf('.');
		 if (dotIndex > 0) {
		     fileNameWithoutExtension = fileName.substring(0, dotIndex);
		 } else {
		     fileNameWithoutExtension = fileName; // 没有后缀的情况
		 }
		 return fileNameWithoutExtension;
	}
	
	/**
	 * 使用 Apache Commons IO 获取带点的扩展名
	 * @param file 文件对象
	 * @return 带点的扩展名
	 */
	public String getExtensionWithDotApacheCommons(File file) {
	    if (file == null) {
	        return "";
	    }
	    String extension = FilenameUtils.getExtension(file.getName());
	    return extension.isEmpty() ? "" : "." + extension;
	}
	
	/**
	 * 重命名文件
	 * @param oldFilePath 原文件完整路径（相对路径）
	 * @param newName 新的文件名（包含扩展名）
	 * @return 新文件的完整路径，如果重命名失败则返回 null
	 */
	public String getRenameFilePath(String oldFilePath, String newName) {
	    try {
	        // 构建原文件的绝对路径
	        File oldFile = new File(webRootPath + oldFilePath);
	        
	        // 获取原文件扩展名
	        String extension = getExtensionWithDotApacheCommons(oldFile);
	        
	        // 构建新文件的绝对路径
	        String newAbsolutePath = oldFile.getParent() + File.separator + newName + extension;
	        File newFile = new File(newAbsolutePath);
	        
	        // 检查新文件是否已存在
	        if (newFile.exists()) {
	            throw new RuntimeException("目标文件已存在: " + newAbsolutePath);
	        }
	        
	        // 执行重命名
	        boolean success = oldFile.renameTo(newFile);
	        
	        if (success) {
	            // 构建并返回相对于 web 根目录的路径 从绝对路径中去除 webRootPath 部分
	            String relativePath = newAbsolutePath.substring(webRootPath.length());
	            
	            // 将路径分隔符统一为 "/"（适用于 Web 路径）
	            relativePath = relativePath.replace(File.separator, "/");
	            
	            return relativePath;
	        } else {
	            return null;
	        }
	    } catch (Exception e) {
	        System.err.println("重命名文件时发生异常: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}

	/**
	 * 根据MD5查找已存在的图片
	 * 
	 * @param md5 字符串
	 * @return Image Image
	 */
	public Image findByMd5(String md5) {
		return dao.findFirst("SELECT * FROM siargo_image WHERE md5_hash = ? AND status = ?", md5, STATUS_NORMAL);
	}
	
	/**
	 * 根据Sn查找dictionary ID
	 * 
	 * @param sn 字符串
	 * @return Record Record
	 */
	public Record dicIdFindBySn(String sn) {
		Sql sql = Sql.mysql().select("jd.id")
				.from("jb_dictionary", "jd")
				.eq("jd.type_key", "siargo_supplier")
				.eq("jd.ENABLE", STATUS_NORMAL )
				.eq("jd.sn", sn);
		return findFirstRecord(sql);
	}

	/**
	 * 更新
	 * 
	 * @param image
	 * @return
	 * @throws IOException 
	 */
	public Ret update(Image image) throws IOException {
		if (image == null || notOk(image.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		
		Image dbImage = findById(image.getId());
		if (dbImage == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}

		// 变更检测
		boolean supplierChanged = !image.getSupplierId().equals(dbImage.getSupplierId());
		boolean storageNameChanged = !image.getStorageName().equals(dbImage.getStorageName());
		boolean fileChanged = !image.getFilePath().equals(dbImage.getFilePath()); // 是否上传了新文件
		
		String finalFilePath = dbImage.getFilePath();
		String finalMd5 = dbImage.getMd5Hash();
		
		if (fileChanged) {
			// 场景A：上传了新文件（fileChanged=true）
			File newFile = new File(webRootPath + image.getFilePath());
			
			// 计算新文件MD5，检查重复（排除自身ID）
			String newMd5 = getMd5(newFile);
			Image existImage = dao.findFirst(
				"SELECT * FROM siargo_image WHERE md5_hash = ? AND status = ? AND id != ?", 
				newMd5, STATUS_NORMAL, image.getId());
			if (existImage != null) {
				return fail("图片 " + existImage.getStorageName() + " 已经存在！");
			}
			
			// 确定目标目录：localPath + 供应商字典ID + / + YYYYMM + /
			String targetDir = localPath + dicIdFindBySn(image.getSupplierId()).getStr("id") + 
					File.separator + DateUtil.getNowStr(DateUtil.YM) + File.separator;
			File targetFolder = new File(webRootPath + targetDir);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}
			
			// 确定目标文件名：storageName + 新文件扩展名
			String extension = getExtensionWithDotApacheCommons(newFile);
			String targetFileName = image.getStorageName() + extension;
			String targetPath = targetDir + targetFileName;
			
			// 移动新文件到目标路径
			try {
				Files.move(
						newFile.toPath(),
						Paths.get(webRootPath + targetPath),
						StandardCopyOption.REPLACE_EXISTING
				);
			} catch (IOException e) {
				e.printStackTrace();
				return fail("文件移动失败：" + e.getMessage());
			}
			
			// 删除旧文件（如果旧文件存在）
			File oldFile = new File(webRootPath + dbImage.getFilePath());
			if (oldFile.exists()) {
				oldFile.delete();
			}
			
			finalFilePath = targetPath;
			finalMd5 = newMd5;
			
		} else if (supplierChanged || storageNameChanged) {
			// 场景B：没有上传新文件，但供应商或storageName变了
			File oldFile = new File(webRootPath + dbImage.getFilePath());
			String oldExtension = getExtensionWithDotApacheCommons(oldFile);
			
			// 确定目标目录
			String targetDir;
			if (supplierChanged) {
				// 供应商变了 → 新供应商目录
				targetDir = localPath + dicIdFindBySn(image.getSupplierId()).getStr("id") + 
						File.separator + DateUtil.getNowStr(DateUtil.YM) + File.separator;
			} else {
				// 供应商没变 → 保持旧文件所在目录
				targetDir = oldFile.getParent().substring(webRootPath.length()) + File.separator;
			}
			
			// 确保目标目录存在
			File targetFolder = new File(webRootPath + targetDir);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}
			
			// 确定目标文件名
			String targetFileName;
			if (storageNameChanged) {
				// storageName变了 → 新storageName + 旧文件扩展名
				targetFileName = image.getStorageName() + oldExtension;
			} else {
				// storageName没变 → 保持旧文件名
				targetFileName = oldFile.getName();
			}
			
			String targetPath = targetDir + targetFileName;
			
			// 只做一次 Files.move（从旧路径到新路径）
			try {
				Files.move(
						oldFile.toPath(),
						Paths.get(webRootPath + targetPath),
						StandardCopyOption.REPLACE_EXISTING
				);
			} catch (IOException e) {
				e.printStackTrace();
				return fail("文件移动失败：" + e.getMessage());
			}
			
			// 确保源文件已删除
			if (oldFile.exists()) {
				oldFile.delete();
			}
			
			finalFilePath = targetPath;
			// MD5不变，保留dbImage的md5_hash
		}
		// 场景C：什么都没变（仅改备注） - 不做文件操作，finalFilePath和finalMd5保持原值

		// 处理删除状态
		if (image.getStatus() == STATUS_DELETED) {
			image.set("deleted_time", DateUtil.getDateString(DateUtil.YMDHMS));
		}
		
		// 更新数据库记录
		image.set("file_path", FileUtil.normalize(finalFilePath));
		image.set("md5_hash", finalMd5);
		image.set("description", image.getDescription());
		image.set("updated_time", DateUtil.getDateString(DateUtil.YMDHMS));
		image.set("update_id", JBoltUserKit.getUserId());
		
		boolean success = image.update();
		if (success) {
			// 添加日志
			// addUpdateSystemLog(image.getId(), JBoltUserKit.getUserId(), image.getName());
		}
		return ret(success);
	}
	
	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public Ret delete(Long id) {
		Image dbImage = findById(id);
		if (dbImage == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		
		File oldFile = new File(webRootPath + dbImage.getFilePath());
		oldFile.delete();
		
		dbImage.set("deleted_time",DateUtil.getDateString(DateUtil.YMDHMS));
		dbImage.set("status", STATUS_DELETED);

		return ret(dbImage.update());
	}

	/**
	 * 删除 指定多个ID
	 * 
	 * @param ids
	 * @return
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids, true);
	}

	/**
	 * 删除数据后执行的回调
	 * 
	 * @param image 要删除的model
	 * @param kv    携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(Image image, Kv kv) {
		// addDeleteSystemLog(image.getId(), JBoltUserKit.getUserId(),image.getName());
		return null;
	}

	/**
	 * 检测是否可以删除
	 * 
	 * @param image 要删除的model
	 * @param kv    携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(Image image, Kv kv) {
		// 如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(image, kv);
	}

	/**
	 * 设置返回二开业务所属的关键systemLog的targetType
	 * 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}

}