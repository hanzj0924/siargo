package cn.jbolt.admin.siargo.equipment.certificate;

import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.common.storage.SiargoUploadFiles;
import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
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
	private SiargoStorage storage() { return SiargoStorage.forBusiness(SiargoStorage.Business.EQCERT); }
	/** 证书图片本地存储路径前缀（统一 "/" 分隔符）*/
	

	@Override
	protected EquipmentCertificate dao() {
		return dao;
	}

	/**
	 * 获取 Web 根目录路径（供 Controller 调用）
	 */
	public String getWebRootPath() {
		return PathKit.getWebRootPath();
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
		try {
            var file = storage().resolveUrl(equipmentCertificate.getStr("image_url"));
            if (equipmentCertificate.getLong("equipment_id") == null
                    || !file.startsWith(storage().path(equipmentCertificate.getEquipmentId().toString()))
                    || !Files.isRegularFile(file)) return fail("证书文件必须已归入对应设备目录");
        } catch (Exception e) { return fail("证书文件路径无效"); }
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
		// 文件替换只由检校/设备业务入口处理，普通编辑保留文件及归属。
        equipmentCertificate.set("image_url", dbEquipmentCertificate.getStr("image_url"));
        equipmentCertificate.set("equipment_id", dbEquipmentCertificate.getLong("equipment_id"));
        equipmentCertificate.set("comparison_id", dbEquipmentCertificate.getLong("comparison_id"));
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
		// 物理文件删除由 Controller 在事务提交后统一执行（afterCommit），此处仅记录日志
		addDeleteSystemLog(equipmentCertificate.getId(), JBoltUserKit.getUserId(), equipmentCertificate.getStr("image_url"));
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
		return dao.find("SELECT * FROM siargo_equipment_certificate WHERE comparison_id = ? ORDER BY certificate_date DESC", comparisonId);
	}

	/**
	 * 根据设备ID查询所有证书
	 * @param equipmentId 设备ID
	 * @return 证书列表
	 */
	public List<EquipmentCertificate> findByEquipmentId(Long equipmentId) {
		if (notOk(equipmentId)) return null;
		return dao.find("SELECT * FROM siargo_equipment_certificate WHERE equipment_id = ? ORDER BY certificate_date DESC, id DESC", equipmentId);
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


	/**
	 * 为对比记录保存证书（含证书日期和描述）
	 * 逻辑：解析 imageUrls（逗号分隔），将临时文件移动到正式目录，创建证书记录
	 * @param comparisonId 对比记录ID
	 * @param equipmentId 设备ID
	 * @param imageUrls 逗号分隔的临时文件相对路径列表
	 * @param certificateDate 证书日期（可为null）
	 * @param certificateRemark 证书描述（可为null）
	 */


	/**
	 * 更新对比记录的证书
	 * 逻辑：先删除旧证书记录和文件，再保存新证书
	 * @param comparisonId 对比记录ID
	 * @param equipmentId 设备ID
	 * @param imageUrls 逗号分隔的图片路径
	 */


	/**
	 * 更新对比记录的证书（含证书日期和描述）
	 * 逻辑：先删除旧证书记录和文件，再保存新证书
	 * @param comparisonId 对比记录ID
	 * @param equipmentId 设备ID
	 * @param imageUrls 逗号分隔的图片路径
	 * @param certificateDate 证书日期（可为null）
	 * @param certificateRemark 证书描述（可为null）
	 */


	/**
	 * 删除某对比记录的所有证书（含磁盘文件）
	 * @param comparisonId 对比记录ID
	 */


	// -------------------------------------------------------------------------
	// 物理文件删除辅助（afterCommit 使用）
	// -------------------------------------------------------------------------

	/**
	 * 查询多条证书记录的物理文件路径（供 Controller 在事务提交后统一删除）
	 * @param ids 证书记录ID列表
	 * @return 物理文件绝对路径列表（剔除空值/含 .. 的非法路径）
	 */
	public List<String> queryFilePathsByIds(List<Long> ids) {
		List<String> paths = new ArrayList<>();
		if (ids == null || ids.isEmpty()) {
			return paths;
		}
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < ids.size(); i++) {
			if (i > 0) placeholders.append(",");
			placeholders.append("?");
		}
		List<Record> rows = Db.find("SELECT image_url FROM siargo_equipment_certificate WHERE id IN (" + placeholders + ")", ids.toArray());
		if (rows != null) {
			for (Record row : rows) {
				String url = row.getStr("image_url");
				if (url != null && !url.isEmpty() && !url.contains("..")) {
					paths.add(storage().resolveUrl(url).toString());
				}
			}
		}
		return paths;
	}

	/**
	 * 查询多个对比记录关联的证书物理文件路径（供删除对比记录时 afterCommit 统一删除）
	 * @param comparisonIds 对比记录ID列表
	 * @return 物理文件绝对路径列表
	 */
	public List<String> queryFilePathsByComparisonIds(List<Long> comparisonIds) {
		List<String> paths = new ArrayList<>();
		if (comparisonIds == null || comparisonIds.isEmpty()) {
			return paths;
		}
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < comparisonIds.size(); i++) {
			if (i > 0) placeholders.append(",");
			placeholders.append("?");
		}
		List<Record> rows = Db.find("SELECT image_url FROM siargo_equipment_certificate WHERE comparison_id IN (" + placeholders + ")", comparisonIds.toArray());
		if (rows != null) {
			for (Record row : rows) {
				String url = row.getStr("image_url");
				if (url != null && !url.isEmpty() && !url.contains("..")) {
					paths.add(storage().resolveUrl(url).toString());
				}
			}
		}
		return paths;
	}

	/**
	 * 批量删除物理文件（供 Controller 在事务提交后调用；带路径穿越二次过滤）
	 * @param paths 待删除的物理文件绝对路径列表
	 */
    public void deletePhysicalFiles(List<String> paths) {
        if (paths == null) return;
        for (String path : paths) try { storage().deleteFile(java.nio.file.Path.of(path)); }
        catch (Exception e) { LOG.error("证书文件清理失败：" + path, e); }
    }

	/**
	 * 仅删除某对比记录关联的证书记录（不删物理文件，供删除对比记录 afterCommit 流程使用）
	 * @param comparisonId 对比记录ID
	 */
	public void deleteRecordsByComparisonId(Long comparisonId) {
		if (notOk(comparisonId)) {
			return;
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
        Prepared prepared = null;
        boolean committed = false;
        try {
            prepared = prepare(equipmentId, null, imageUrls, null, null, false);
            Prepared work = prepared;
            if (!Db.tx(work::persist)) return work.rollback("证书保存失败");
            committed = true;
            return work.committed();
        } catch (Exception e) { return prepared == null || committed ? fail(e.getMessage()) : prepared.rollback(e.getMessage()); }
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
        return storage().toUrl(SiargoUploadFiles.temp(storage(), path, true));
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
        Ret r = SiargoUploadFiles.delete(storage(), java.util.List.of(imageUrl), false);
        if (r.isFail()) LOG.error("证书清理失败：" + r.get("failedFiles"));
    }


    /** 文件准备在事务外完成；数据库写入和设备/对比记录共用一个真正的事务。 */
    public Prepared prepare(Long equipmentId, Long comparisonId, String urls, String date, String remark, boolean replace) throws IOException {
        if (equipmentId == null || equipmentId <= 0) throw new IOException("设备编号无效");
        Prepared prepared = new Prepared(equipmentId, comparisonId, replace);
        try {
            if (replace && comparisonId != null) {
                prepared.oldFiles.addAll(findByComparisonId(comparisonId).stream().map(r -> r.getStr("image_url")).toList());
            }
            java.util.Set<String> seen = new java.util.HashSet<>();
            for (String url : (urls == null || urls.isBlank() ? new String[0] : urls.split(","))) {
                String normalized = url.trim();
                if (normalized.isEmpty() || !seen.add(normalized)) continue;
                java.nio.file.Path source = storage().resolveUrl(normalized), target = source;
                if (!Files.isRegularFile(source)) throw new IOException("证书文件不存在");
                if (source.startsWith(storage().path("temp"))) {
                    source = SiargoUploadFiles.temp(storage(), normalized, true);
                    target = storage().path(String.valueOf(equipmentId), java.util.UUID.randomUUID() + "_" + source.getFileName());
                    prepared.moves.move(source, target);
                } else {
                    if (!prepared.oldFiles.contains(normalized) || !source.startsWith(storage().path(String.valueOf(equipmentId))))
                        throw new IOException("只能保留当前对比记录已有证书或使用新上传临时文件");
                }
                EquipmentCertificate row = new EquipmentCertificate();
                row.set("equipment_id", equipmentId).set("comparison_id", comparisonId).set("image_url", storage().toUrl(target)).set("status", 1);
                if (date != null && !date.isBlank()) row.set("certificate_date", date);
                if (remark != null && !remark.isBlank()) row.set("remark", remark);
                prepared.rows.add(row);
            }
            return prepared;
        } catch (Exception e) {
            Ret result = prepared.rollback("证书准备失败：" + e.getMessage());
            throw new IOException(result.getStr("msg"), e);
        }
    }

    public final class Prepared {
        private final Long equipmentId, comparisonId;
        private final boolean replace;
        private final SiargoUploadFiles.Moves moves = new SiargoUploadFiles.Moves(storage());
        private final List<EquipmentCertificate> rows = new ArrayList<>();
        private final List<String> oldFiles = new ArrayList<>();
        private Prepared(Long equipmentId, Long comparisonId, boolean replace) {
            this.equipmentId = equipmentId; this.comparisonId = comparisonId; this.replace = replace;
        }
        public boolean persist() {
            if (!rows.isEmpty() && comparisonId != null)
                Db.update("UPDATE siargo_equipment_certificate SET status=2 WHERE equipment_id=? AND status=1", equipmentId);
            if (replace && comparisonId != null) deleteRecordsByComparisonId(comparisonId);
            for (EquipmentCertificate row : rows) if (!row.save()) return false;
            return true;
        }
        public Ret rollback(String message) { return moves.rollback(message); }
        public Ret committed() {
            List<String> failures = new ArrayList<>();
            for (String old : oldFiles) {
                if (rows.stream().anyMatch(row -> java.util.Objects.equals(row.getStr("image_url"), old))) continue;
                if (Db.queryLong("SELECT COUNT(*) FROM siargo_equipment_certificate WHERE image_url=?", old) > 0) continue;
                try { storage().deleteFile(storage().resolveUrl(old)); }
                catch (Exception e) { failures.add(old + "：" + e.getMessage()); }
            }
            return Ret.ok().set("cleanupFailures", failures);
        }
    }
}
