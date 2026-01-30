package cn.jbolt.admin.siargo.imi;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;

import java.util.Date;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.admin.siargo.siargoutil.SiargoUtil;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.siargo.model.Image;
/**
 * 来料到货单管理 Service
 * @ClassName: ImageService   
 * @author: hanzj
 * @date: 2026-01-30 16:19  
 */
public class ImageService extends JBoltBaseService<Image> {
	private final Image dao=new Image().dao();
	@Override
	protected Image dao() {
		return dao;
	}
	
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
				.select("si.supplier_id", "si.original_name", "si.storage_name", "si.file_path", 
						"si.md5_hash", "si.description", "si.upload_time", "si.status", "si.deleted_time", 
						"ju.name AS uploader_name")
				.page(pageNumber, pageSize).from("siargo_image", "si")
				.leftJoin("jb_user", "ju", "ju.id = si.uploader_id")
				.like("si.supplier_id", supplierId)
				.like("si.storage_name", keywords)
				.eq("si.status", 1)
				.orderBy("si.upload_time", true);
				;
		return paginateRecord(sql, true);
	}
	
	/**
	 * 保存
	 * @param image
	 * @return
	 */
	public Ret save(Image image) {
		if(image==null || isOk(image.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		
		image.set("original_name", "original_name");
		image.set("storage_name", "storage_name");
		image.set("file_path", "file_path");
		image.set("md5_hash", "md5_hash");
		image.set("uploader_id", JBoltUserKit.getUserId());
		image.set("upload_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		image.set("status", 1);

		boolean success=image.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(image.getId(), JBoltUserKit.getUserId(), image.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param image
	 * @return
	 */
	public Ret update(Image image) {
		if(image==null || notOk(image.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		Image dbImage=findById(image.getId());
		if(dbImage==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		
		if (image.getStatus() == 0) {
			image.set("deleted_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		}
		
		boolean success=image.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(image.getId(), JBoltUserKit.getUserId(), image.getName());
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
	 * @param image 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(Image image, Kv kv) {
		//addDeleteSystemLog(image.getId(), JBoltUserKit.getUserId(),image.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param image 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(Image image, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(image, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}
	
}