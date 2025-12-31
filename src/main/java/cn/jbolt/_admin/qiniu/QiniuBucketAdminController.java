package cn.jbolt._admin.qiniu;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Okv;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.model.QiniuBucket;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;

/**
 * 七牛Bucket管理 Controller
 * @ClassName: QiniuBucketAdminController   
 * @author: JBolt-Generator
 * @date: 2021-10-14 11:27  
 */
@CheckPermission(PermissionKey.QINIU_BUCKET)
@UnCheckIfSystemAdmin
@OnlySaasPlatform
public class QiniuBucketAdminController extends JBoltBaseController {

	@Inject
	private QiniuBucketService service;
	
   /**
	* 首页
	*/
	public void index() {
		set("qiniuId", getLong("qiniuId"));
		render("index.html");
	}
  	
	/**
	 * 从线上同步所有账号的bucket
	 */
	public void syncAllQiniuBuckets() {
		renderJson(service.syncAllQiniuBuckets());
	}
	/**
	 * 从线上同步bucket
	 */
	@Before(Tx.class)
	public void syncFromQiniu() {
		renderJson(service.syncFromQiniu(getLong("qiniuId")));
	}
  	/**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),getLong("qiniuId"),get("zone")));
	}

	/**
	 * options数据源
	 */
	public void options() {
		Long qiniuId = getLong(0);
		if(notOk(qiniuId)){
			renderJsonSuccess();
			return;
		}
		renderJsonData(service.getOptionList("name","sn", Okv.by("qiniu_id",qiniuId)));
	}
	
   /**
	* 新增
	*/
	public void add() {
		set("qiniuId", getLong("qiniuId"));
		render("add.html");
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		QiniuBucket qiniuBucket=service.findById(getLong(0)); 
		if(qiniuBucket == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("qiniuBucket",qiniuBucket);
		render("edit.html");
	}
	
   /**
	* 保存
	*/
	@Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(QiniuBucket.class, "qiniuBucket")));
	}
	
   /**
	* 更新
	*/
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(QiniuBucket.class, "qiniuBucket")));
	}
	
   /**
	* 删除
	*/
	@Before(Tx.class)
	public void delete() {
		renderJson(service.delete(getLong(0)));
	}
	
  /**
	* 切换toggleIsDefault
	*/
	@Before(Tx.class)
	public void toggleIsDefault() {
		renderJson(service.toggleIsDefault(getLong(0)));
	}
	
    /**
	* 切换启用状态
	*/
	@Before(Tx.class)
	public void toggleEnable() {
		renderJson(service.toggleEnable(getLong(0)));
	}
	
	
}
