package cn.jbolt.admin.siargo.equipment.certificate;

import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentCertificate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 设备管理证书记录 Service
 * @ClassName: EquipmentCertificateService   
 * @author: hanzj
 * @date: 2026-04-18 15:45  
 */
public class EquipmentCertificateService extends JBoltBaseService<EquipmentCertificate> {
	private final EquipmentCertificate dao=new EquipmentCertificate().dao();

	/** Web 根目录绝对路径 */
	public static final String webRootPath = PathKit.getWebRootPath();
	/** 证书图片本地存储路径前缀（统一 "/" 分隔符）*/
	public static final String localPath = "/upload/" + JBoltUploadFolder.SIARGO_UPLOAD_EQUIPMENT_CERTIFICATE + "/";

	@Override
	protected EquipmentCertificate dao() {
		return dao;
	}

	/**
	 * 获取 Web 根目录路径（供 Controller 调用）
	 */
	public String getWebRootPath() {
		return webRootPath;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<EquipmentCertificate> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("certificate_date","desc", pageNumber, pageSize, keywords, "certificate_date");
	}
	
	/**
	 * 保存
	 * @param equipmentCertificate
	 * @return
	 */
	public Ret save(EquipmentCertificate equipmentCertificate) {
		if(equipmentCertificate==null || isOk(equipmentCertificate.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(equipmentCertificate.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=equipmentCertificate.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(equipmentCertificate.getId(), JBoltUserKit.getUserId(), equipmentCertificate.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param equipmentCertificate
	 * @return
	 */
	public Ret update(EquipmentCertificate equipmentCertificate) {
		if(equipmentCertificate==null || notOk(equipmentCertificate.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		EquipmentCertificate dbEquipmentCertificate=findById(equipmentCertificate.getId());
		if(dbEquipmentCertificate==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(equipmentCertificate.getName(), equipmentCertificate.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=equipmentCertificate.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(equipmentCertificate.getId(), JBoltUserKit.getUserId(), equipmentCertificate.getName());
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
	 * @param equipmentCertificate 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(EquipmentCertificate equipmentCertificate, Kv kv) {
		//addDeleteSystemLog(equipmentCertificate.getId(), JBoltUserKit.getUserId(),equipmentCertificate.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param equipmentCertificate 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(EquipmentCertificate equipmentCertificate, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(equipmentCertificate, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.EQUIPMENTCERTIFICATERECORD.getValue();
	}

	// -------------------------------------------------------------------------
	// 图片上传与管理
	// -------------------------------------------------------------------------

	/**
	 * 批量保存证书图片：将临时目录文件移动到正式目录，并为每张图片创建 EquipmentCertificate 记录。
	 * <p>
	 * 使用 Db.tx() 包装，任一步骤失败时回滚数据库事务并删除已移动的文件及剩余临时文件。
	 *
	 * @param equipmentId 设备ID
	 * @param imageUrls   逗号分隔的临时文件相对路径列表（相对于 webRootPath）
	 * @return Ret 操作结果
	 */
	public Ret saveCertificateImages(Long equipmentId, String imageUrls) {
		if (notOk(equipmentId) || imageUrls == null || imageUrls.trim().isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}

		List<String> tempPaths = Arrays.stream(imageUrls.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());

		if (tempPaths.isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}

		List<String> movedFilePaths = new ArrayList<>();
		final String[] errorMsg = {null};

		boolean txSuccess = Db.tx(() -> {
			for (int i = 0; i < tempPaths.size(); i++) {
				String tempPath = tempPaths.get(i);
				Ret ret;
				try {
					// 路径安全校验：禁止路径穿越，强制前缀校验
					String safePath = normalizeTempPath(tempPath);
					ret = saveSingleCertificateImage(equipmentId, safePath);
				} catch (IllegalArgumentException e) {
					ret = fail("第" + (i + 1) + "张图片路径非法：" + e.getMessage());
				} catch (Exception e) {
					ret = fail("保存第" + (i + 1) + "张图片时异常：" + e.getMessage());
				}
				if (ret.isFail()) {
					errorMsg[0] = (String) ret.get("msg");
					// 删除已移动到目标位置的文件
					for (String filePath : movedFilePaths) {
						File f = new File(webRootPath + filePath);
						if (f.exists()) f.delete();
					}
					// 删除当前及剩余的临时文件
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
	 * 保存单张证书图片（须在事务内调用）。
	 * <p>
	 * 将临时文件从 equipment_certificate/temp/ 移动到 equipment_certificate/{equipmentId}/，
	 * 并创建对应的 EquipmentCertificate 记录。
	 *
	 * @param equipmentId 设备ID
	 * @param tempPath    临时文件相对路径（相对于 webRootPath）
	 * @return Ret，成功时携带 filePath（相对路径）
	 */
	public Ret saveSingleCertificateImage(Long equipmentId, String tempPath) {
		File tempFile = new File(webRootPath + tempPath);
		if (!tempFile.exists() || !tempFile.isFile()) {
			return fail("临时文件不存在：" + tempPath);
		}

		// 目标目录：localPath + equipmentId + /
		String targetDir = localPath + equipmentId + "/";
		File targetFolder = new File(webRootPath + targetDir);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}

		String targetPath = targetDir + tempFile.getName();
		File targetFile = new File(webRootPath + targetPath);

		// ① 先移动文件
		try {
			Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return fail("文件移动失败：" + e.getMessage());
		}

		// ② 再写数据库
		EquipmentCertificate cert = new EquipmentCertificate();
		cert.set("equipment_id", equipmentId);
		cert.set("image_url", targetPath);

		boolean success = cert.save();
		if (!success) {
			// 数据库写入失败，回滚文件（移回临时位置）
			try {
				Files.move(targetFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				ex.printStackTrace();
				targetFile.delete();
			}
			return fail("数据保存失败");
		}

		return ret(true).set("filePath", targetPath);
	}

	/**
	 * 校验并规范化临时文件路径，防止路径穿越。
	 * <p>
	 * 规则：路径不能含 ".."，且必须以证书临时目录前缀开头。
	 *
	 * @param path 前端传入的文件路径
	 * @return 规范化后的安全路径
	 * @throws IllegalArgumentException 如果路径不合法
	 */
	public String normalizeTempPath(String path) {
		if (path == null || path.trim().isEmpty()) {
			throw new IllegalArgumentException("文件路径不能为空");
		}
		String normalized = path.replace("\\", "/").trim();
		// 禁止路径穿越
		if (normalized.contains("..")) {
			throw new IllegalArgumentException("非法文件路径");
		}
		// 必须以证书临时目录前缀开头
		String prefix = localPath + "temp/";
		if (!normalized.startsWith(prefix)) {
			throw new IllegalArgumentException("只能操作证书临时目录下的文件");
		}
		return normalized;
	}

	/**
	 * 获取文件名（不含扩展名）
	 *
	 * @param file 文件对象
	 * @return 不含扩展名的文件名
	 */
	public String getFileName(File file) {
		String fileName = file.getName();
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
	}

}