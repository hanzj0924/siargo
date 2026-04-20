package cn.jbolt.admin.siargo.equipment.record;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.EquipmentRecord;
import cn.jbolt.admin.siargo.equipment.certificate.EquipmentCertificateService;
import cn.jbolt.siargo.model.EquipmentCertificate;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 设备记录 Service
 * @ClassName: EquipmentRecordService   
 * @author: hanzj
 * @date: 2026-04-18 15:45  
 */
public class EquipmentRecordService extends JBoltBaseService<EquipmentRecord> {
	/** 日志对象 */
	private static final Log LOG = Log.getLog(EquipmentRecordService.class);

	private final EquipmentRecord dao=new EquipmentRecord().dao();

	@Inject
	private EquipmentCertificateService equipmentCertificateService;

	@Override
	protected EquipmentRecord dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return Db.paginate(pageNumber, pageSize,
				"SELECT er.*, d.name AS record_type_name",
				"FROM siargo_equipment_record er " +
				"LEFT JOIN jb_dictionary d ON d.sn = er.record_type AND d.type_id = (SELECT id FROM jb_dictionary_type WHERE type_key = 'siargo_equipment_record_type' LIMIT 1) " +
				"ORDER BY er.record_date DESC");
	}
	
	/**
	 * 保存
	 * @param equipmentRecord
	 * @return
	 */
	public Ret save(EquipmentRecord equipmentRecord) {
		if(equipmentRecord==null || isOk(equipmentRecord.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(equipmentRecord.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=equipmentRecord.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(equipmentRecord.getId(), JBoltUserKit.getUserId(), equipmentRecord.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param equipmentRecord
	 * @return
	 */
	public Ret update(EquipmentRecord equipmentRecord) {
		if(equipmentRecord==null || notOk(equipmentRecord.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		EquipmentRecord dbEquipmentRecord=findById(equipmentRecord.getId());
		if(dbEquipmentRecord==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(equipmentRecord.getName(), equipmentRecord.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=equipmentRecord.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(equipmentRecord.getId(), JBoltUserKit.getUserId(), equipmentRecord.getName());
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
	 * 1. 删除关联的证书图片文件（磁盘）
	 * 2. 删除关联的证书数据库记录
	 * 3. 记录操作日志
	 * @param equipmentRecord 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return null表示正常完成
	 */
	@Override
	protected String afterDelete(EquipmentRecord equipmentRecord, Kv kv) {
		// 级联删除证书图片文件和数据库记录
		try {
			Long recordId = equipmentRecord.getId();
			// 查询关联的所有证书图片URL
			String imageUrlsStr = findCertificateImageUrlsByRecordId(recordId);
			if (imageUrlsStr != null && !imageUrlsStr.isEmpty()) {
				String[] imageUrls = imageUrlsStr.split(",");
				for (String imageUrl : imageUrls) {
					String url = imageUrl.trim();
					if (!url.isEmpty()) {
						deleteCertificateFile(url);
					}
				}
			}
			// 删除数据库中的证书记录
			Db.delete("DELETE FROM siargo_equipment_certificate WHERE record_id = ?", recordId);
		} catch (Exception e) {
			LOG.error("级联删除证书记录异常，recordId=" + equipmentRecord.getId(), e);
		}
		// 记录永久删除操作日志
		addDeleteSystemLog(equipmentRecord.getId(), JBoltUserKit.getUserId(), equipmentRecord.getDescription());
		return null;
	}

	/**
	 * 删除单个证书图片的磁盘文件
	 * 文件不存在时静默跳过，删除失败只记录警告，不抛出异常
	 * @param imageUrl 图片相对路径（如 /upload/siargo/equipment_certificate/1/xxx.jpg）
	 */
	private void deleteCertificateFile(String imageUrl) {
		if (imageUrl == null || imageUrl.isEmpty()) {
			return;
		}
		try {
			String physicalPath = PathKit.getWebRootPath() + imageUrl;
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
	
	/**
	 * 检测是否可以删除
	 * @param equipmentRecord 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(EquipmentRecord equipmentRecord, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(equipmentRecord, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.EQUIPMENTRECORD.getValue();
	}

	// ===== 证书同步保存/更新相关方法 =====

	/**
	 * 保存记录并同步保存证书
	 */
	public Ret saveWithCertificate(EquipmentRecord equipmentRecord, String imageUrls, String certDate, String certRemark) {
		if(equipmentRecord==null || isOk(equipmentRecord.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		boolean success = equipmentRecord.save();
		if(success && isOk(imageUrls)) {
			saveCertificatesForRecord(equipmentRecord.getId(), equipmentRecord.getEquipmentId(), imageUrls, certDate, certRemark);
		}
		return ret(success);
	}

	/**
	 * 更新记录并同步更新证书
	 */
	public Ret updateWithCertificate(EquipmentRecord equipmentRecord, String imageUrls, String certDate, String certRemark) {
		if(equipmentRecord==null || notOk(equipmentRecord.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		EquipmentRecord dbEquipmentRecord = findById(equipmentRecord.getId());
		if(dbEquipmentRecord==null) { return fail(JBoltMsg.DATA_NOT_EXIST); }
		boolean success = equipmentRecord.update();
		if(success) {
			// 删除旧证书记录
			Db.delete("DELETE FROM siargo_equipment_certificate WHERE record_id = ?", equipmentRecord.getId());
			// 重新保存证书
			if(isOk(imageUrls)) {
				saveCertificatesForRecord(equipmentRecord.getId(), equipmentRecord.getEquipmentId(), imageUrls, certDate, certRemark);
			}
		}
		return ret(success);
	}

	/**
	 * 为记录保存证书图片（处理文件移动 + 完整字段写库）
	 * @param recordId 记录ID
	 * @param equipmentId 设备ID
	 * @param imageUrls 逗号分隔的图片路径
	 * @param certDate 证书日期 yyyy-MM-dd
	 * @param certRemark 证书备注
	 */
	private void saveCertificatesForRecord(Long recordId, Long equipmentId, String imageUrls, String certDate, String certRemark) {
		List<String> urls = Arrays.stream(imageUrls.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());

		Date certificateDate = null;
		if(isOk(certDate)) {
			try {
				certificateDate = new SimpleDateFormat("yyyy-MM-dd").parse(certDate);
			} catch (Exception e) {
				// 日期解析失败则忽略
			}
		}

		String tempPrefix = EquipmentCertificateService.localPath + "temp/";
		String webRootPath = EquipmentCertificateService.webRootPath;

		for(String url : urls) {
			String finalUrl = url;
			// 判断是否为临时路径，需要移动文件到正式目录
			if(url.startsWith(tempPrefix)) {
				try {
					String safePath = equipmentCertificateService.normalizeTempPath(url);
					java.io.File tempFile = new java.io.File(webRootPath + safePath);
					if(!tempFile.exists()) continue;

					String targetDir = EquipmentCertificateService.localPath + equipmentId + "/";
					java.io.File targetFolder = new java.io.File(webRootPath + targetDir);
					if(!targetFolder.exists()) targetFolder.mkdirs();

					String targetPath = targetDir + tempFile.getName();
					java.io.File targetFile = new java.io.File(webRootPath + targetPath);
					java.nio.file.Files.move(tempFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					finalUrl = targetPath;
				} catch (Exception e) {
					continue; // 单张失败不阻断整体
				}
			}
			// 创建证书记录（包含完整字段）
			EquipmentCertificate cert = new EquipmentCertificate();
			cert.set("record_id", recordId);
			cert.set("equipment_id", equipmentId);
			cert.set("image_url", finalUrl);
			cert.set("certificate_date", certificateDate);
			cert.set("remark", isOk(certRemark) ? certRemark : null);
			cert.save();
		}
	}

	/**
	 * 根据记录ID查询关联的第一条证书（用于编辑回显）
	 */
	public EquipmentCertificate findCertificateByRecordId(Long recordId) {
		if(notOk(recordId)) return null;
		return new EquipmentCertificate().dao()
			.findFirst("SELECT * FROM siargo_equipment_certificate WHERE record_id = ? ORDER BY id LIMIT 1", recordId);
	}

	/**
	 * 根据记录ID查询所有证书图片URL（逗号分隔，用于图片上传器回显）
	 */
	public String findCertificateImageUrlsByRecordId(Long recordId) {
		if(notOk(recordId)) return "";
		List<EquipmentCertificate> certs = new EquipmentCertificate().dao()
			.find("SELECT image_url FROM siargo_equipment_certificate WHERE record_id = ?", recordId);
		if(certs == null || certs.isEmpty()) return "";
		return certs.stream().map(c -> c.getStr("image_url")).collect(Collectors.joining(","));
	}

}