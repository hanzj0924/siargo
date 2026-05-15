package cn.jbolt.admin.siargo.equipment.certificate;

import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
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
	private static final Log LOG = Log.getLog(EquipmentCertificateService.class);
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
	 * 删除策略：
	 * 1. 删除磁盘上的证书图片文件
	 * 2. 记录操作日志
	 * @param equipmentCertificate 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return null表示正常完成
	 */
	@Override
	protected String afterDelete(EquipmentCertificate equipmentCertificate, Kv kv) {
		// 删除磁盘上的证书图片文件
		String imageUrl = equipmentCertificate.getStr("image_url");
		deleteCertificateFile(imageUrl);
		// 记录删除日志
		addDeleteSystemLog(equipmentCertificate.getId(), JBoltUserKit.getUserId(), imageUrl);
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
		return ProjectSystemLogTargetType.EQUIPMENTCERTIFICATE.getValue();
	}

	// -------------------------------------------------------------------------
	// 查询方法
	// -------------------------------------------------------------------------

	/**
	 * 根据对比记录ID查询关联的所有证书
	 * @param comparisonId 对比记录ID
	 * @return 证书列表
	 */
	public List<EquipmentCertificate> findByComparisonId(Long comparisonId) {
		if (notOk(comparisonId)) return null;
		return dao.find("SELECT * FROM siargo_equipment_certificate WHERE comparison_id = ? ORDER BY id", comparisonId);
	}

	/**
	 * 根据设备ID查询所有证书
	 * @param equipmentId 设备ID
	 * @return 证书列表
	 */
	public List<EquipmentCertificate> findByEquipmentId(Long equipmentId) {
		if (notOk(equipmentId)) return null;
		return dao.find("SELECT * FROM siargo_equipment_certificate WHERE equipment_id = ? ORDER BY id DESC", equipmentId);
	}

	/**
	 * 查询设备当前有效证书（status=1）
	 * @param equipmentId 设备ID
	 * @return 有效的证书记录，无则返回null
	 */
	public EquipmentCertificate findValidByEquipmentId(Long equipmentId) {
		if (notOk(equipmentId)) return null;
		return dao.findFirst("SELECT * FROM siargo_equipment_certificate WHERE equipment_id = ? AND status = 1 LIMIT 1", equipmentId);
	}

	// -------------------------------------------------------------------------
	// 保存与更新方法
	// -------------------------------------------------------------------------

	/**
	 * 为对比记录保存证书
	 * 逻辑：解析 imageUrls（逗号分隔），将临时文件移动到正式目录，创建证书记录
	 * @param comparisonId 对比记录ID
	 * @param equipmentId 设备ID
	 * @param imageUrls 逗号分隔的临时文件相对路径列表
	 */
	public void saveCertificatesForComparison(Long comparisonId, Long equipmentId, String imageUrls) {
		saveCertificatesForComparison(comparisonId, equipmentId, imageUrls, null, null);
	}

	/**
	 * 为对比记录保存证书（含证书日期和描述）
	 * 逻辑：解析 imageUrls（逗号分隔），将临时文件移动到正式目录，创建证书记录
	 * @param comparisonId 对比记录ID
	 * @param equipmentId 设备ID
	 * @param imageUrls 逗号分隔的临时文件相对路径列表
	 * @param certificateDate 证书日期（可为null）
	 * @param certificateRemark 证书描述（可为null）
	 */
	public void saveCertificatesForComparison(Long comparisonId, Long equipmentId, String imageUrls, String certificateDate, String certificateRemark) {
		if (notOk(comparisonId) || notOk(equipmentId) || imageUrls == null || imageUrls.trim().isEmpty()) {
			return;
		}
		// 将该设备当前的有效证书更新为无效
		Db.update("UPDATE siargo_equipment_certificate SET status = 2 WHERE equipment_id = ? AND status = 1", equipmentId);

		List<String> urls = Arrays.stream(imageUrls.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());

		String tempPrefix = localPath + "temp/";

		for (String url : urls) {
			String finalUrl = url;
			// 判断是否为临时路径，需要移动文件到正式目录
			if (url.startsWith(tempPrefix)) {
				try {
					String safePath = normalizeTempPath(url);
					File tempFile = new File(webRootPath + safePath);
					if (!tempFile.exists()) continue;

					String targetDir = localPath + equipmentId + "/";
					File targetFolder = new File(webRootPath + targetDir);
					if (!targetFolder.exists()) targetFolder.mkdirs();

					String targetPath = targetDir + tempFile.getName();
					File targetFile = new File(webRootPath + targetPath);
					Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					finalUrl = targetPath;
				} catch (Exception e) {
					LOG.error("移动证书文件异常: " + url, e);
					continue; // 单张失败不阻断整体
				}
			}
			// 创建证书记录
			EquipmentCertificate cert = new EquipmentCertificate();
			cert.set("comparison_id", comparisonId);
			cert.set("equipment_id", equipmentId);
			cert.set("image_url", finalUrl);
			cert.setStatus(1);
			if (certificateDate != null && !certificateDate.trim().isEmpty()) {
				cert.set("certificate_date", certificateDate);
			}
			if (certificateRemark != null && !certificateRemark.trim().isEmpty()) {
				cert.set("remark", certificateRemark);
			}
			cert.save();
		}
	}

	/**
	 * 更新对比记录的证书
	 * 逻辑：先删除旧证书记录和文件，再保存新证书
	 * @param comparisonId 对比记录ID
	 * @param equipmentId 设备ID
	 * @param imageUrls 逗号分隔的图片路径
	 */
	public void updateCertificatesForComparison(Long comparisonId, Long equipmentId, String imageUrls) {
		updateCertificatesForComparison(comparisonId, equipmentId, imageUrls, null, null);
	}

	/**
	 * 更新对比记录的证书（含证书日期和描述）
	 * 逻辑：先删除旧证书记录和文件，再保存新证书
	 * @param comparisonId 对比记录ID
	 * @param equipmentId 设备ID
	 * @param imageUrls 逗号分隔的图片路径
	 * @param certificateDate 证书日期（可为null）
	 * @param certificateRemark 证书描述（可为null）
	 */
	public void updateCertificatesForComparison(Long comparisonId, Long equipmentId, String imageUrls, String certificateDate, String certificateRemark) {
		// 先删除旧证书记录和文件
		deleteByComparisonId(comparisonId);
		// 再保存新证书
		if (isOk(imageUrls)) {
			saveCertificatesForComparison(comparisonId, equipmentId, imageUrls, certificateDate, certificateRemark);
		}
	}

	/**
	 * 删除某对比记录的所有证书（含磁盘文件）
	 * @param comparisonId 对比记录ID
	 */
	public void deleteByComparisonId(Long comparisonId) {
		if (notOk(comparisonId)) return;
		List<EquipmentCertificate> certs = findByComparisonId(comparisonId);
		if (certs != null && !certs.isEmpty()) {
			for (EquipmentCertificate cert : certs) {
				deleteCertificateFile(cert.getStr("image_url"));
			}
		}
		Db.delete("DELETE FROM siargo_equipment_certificate WHERE comparison_id = ?", comparisonId);
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

	// -------------------------------------------------------------------------
	// 私有工具方法
	// -------------------------------------------------------------------------

	/**
	 * 删除单个证书图片的磁盘文件
	 * 文件不存在时静默跳过，删除失败只记录警告
	 * @param imageUrl 图片相对路径
	 */
	private void deleteCertificateFile(String imageUrl) {
		if (imageUrl == null || imageUrl.isEmpty()) {
			return;
		}
		try {
			String physicalPath = webRootPath + imageUrl;
			File file = new File(physicalPath);
			if (file.exists() && file.isFile()) {
				boolean deleted = file.delete();
				if (deleted) {
					LOG.info("证书图片删除成功: " + physicalPath);
				} else {
					LOG.warn("证书图片删除失败: " + physicalPath);
				}
			}
		} catch (Exception e) {
			LOG.error("删除证书图片文件异常: " + imageUrl, e);
		}
	}

}