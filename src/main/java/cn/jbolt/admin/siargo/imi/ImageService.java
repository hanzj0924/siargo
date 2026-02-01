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

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

import cn.hutool.core.io.FileUtil;
import cn.jbolt.admin.siargo.siargoutil.SiargoUtil;
import cn.jbolt.common.config.JBoltUploadFolder;
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
	public Ret save( Image image, String tempPath) {
		Image dbImage = new Image();
		File tempFile = new File(webRootPath + tempPath);
		String localPath = File.separator + "upload" + File.separator + 
				JBoltUploadFolder.SIARGO_UPLOAD_IMI + File.separator + 
				dicIdFindBySn(image.getSupplierId()).getStr("id");
		
    	File locaFolder = new File(webRootPath + localPath);
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
		 
		dbImage.set("supplier_id", image.getSupplierId());
		dbImage.set("storage_name", getFileName(tempFile));
		dbImage.set("file_path", FileUtil.normalize(localPath + File.separator + tempFile.getName()));
		dbImage.set("md5_hash", md5);
		dbImage.set("description", image.getDescription());
		dbImage.set("upload_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		dbImage.set("uploader_id", JBoltUserKit.getUserId());
		dbImage.set("status", STATUS_NORMAL);

		boolean success = dbImage.save();
		
		if (success) {
			try {
				Files.move(
						tempFile.toPath(),
						Paths.get(webRootPath, localPath + File.separator + tempFile.getName()),
				        StandardCopyOption.REPLACE_EXISTING  // 如果目标文件存在则替换
				    );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 添加日志
			// addSaveSystemLog(image.getId(), JBoltUserKit.getUserId(), image.getName());
		}
		return ret(success);
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
	public String renameFile(String oldFilePath, String newName) {
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
	 */
	public Ret update(Image image) {
		if (image == null || notOk(image.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		
		Image dbImage = findById(image.getId());
		if (dbImage == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}

		if (image.getStatus() == STATUS_DELETED) {
			image.set("deleted_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		}
		
		File tempFile = new File(webRootPath + image.getFilePath());
		String localPath = File.separator + "upload" + File.separator + 
				JBoltUploadFolder.SIARGO_UPLOAD_IMI + File.separator + 
				dicIdFindBySn(image.getSupplierId()).getStr("id");
		
    	File locaFolder = new File(webRootPath + localPath);
		if (!locaFolder.exists()) {
			locaFolder.mkdirs();
		}
		
		// 计算MD5（用于去重）
		String md5 = getMd5(tempFile);

		// 检查图片是否已存在
		if (!image.getFilePath().equals(dbImage.getFilePath())) {
			Image existImage = findByMd5(md5); 
			 	if (existImage != null){ 
			 		return fail("图片 " + existImage.getStorageName() + " 已经存在！"); 
			 	}
		}
		
		if (!image.getStorageName().equals(dbImage.getStorageName())) {
			image.set("file_path", FileUtil.normalize(renameFile(dbImage.getFilePath(),image.getStorageName())));
		} else {
			image.set("file_path", FileUtil.normalize(image.getFilePath()));
		}

		image.set("md5_hash", md5);
		image.set("description", image.getDescription());
		image.set("updated_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		image.set("update_id", JBoltUserKit.getUserId());
		boolean success = image.update();
		if (success) {
			if (!(image.getFilePath().equals(dbImage.getFilePath()) && image.getStorageName().equals(dbImage.getStorageName()))) {
				try {
					File oldFile = new File(webRootPath + dbImage.getFilePath());
					oldFile.delete();
					
					Files.move(
							tempFile.toPath(),
							Paths.get(webRootPath, localPath + File.separator + tempFile.getName()),
					        StandardCopyOption.REPLACE_EXISTING  // 如果目标文件存在则替换
					    );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
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
		
		dbImage.set("deleted_time",SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
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