package cn.jbolt._admin.qiniu;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.model.Qiniu;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;

/**
 * 七牛账号管理 Controller
 * @ClassName: QiniuAdminController   
 * @author: JBolt-Generator
 * @date: 2021-10-13 22:44  
 */
@CheckPermission(PermissionKey.QINIU)
@UnCheckIfSystemAdmin
@OnlySaasPlatform
public class QiniuAdminController extends JBoltBaseController {

	@Inject
	private QiniuService service;
	/**
	 * 请求生成upload token
	 * @param bucketSn bucket的 sn
	 */
	@UnCheck
	public void uploadParas(@Para("sn")String bucketSn) {
		renderJson(service.genUploadParas(bucketSn));
	}
   /**
	* 首页
	*/
	public void index() {
		render("index.html");
	}
	/**
	 * 作为其他组件的选项列表
	 */
	@CheckPermission({PermissionKey.QINIU,PermissionKey.QINIU_BUCKET})
	public void options() {
		renderJsonData(service.getOptionList());
	}
  	
  	/**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),getType(),getBoolean("isDefault"),getEnable()));
	}
	
   /**
	* 新增
	*/
	public void add() {
		render("add.html");
	}
	/**
	 * 检查是否存在冲突默认数据
	 */
	public void checkDefaultExist() {
		renderJsonData(service.checkColumnTrueExist("is_default",getLong(0)));
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		Qiniu qiniu=service.findById(getLong(0)); 
		if(qiniu == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("qiniu",qiniu);
		render("edit.html");
	}
	
    /**
	* 保存
	*/
	@Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Qiniu.class, "qiniu")));
	}
	
   /**
	* 更新
	*/
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Qiniu.class, "qiniu")));
	}
	
   /**
	* 删除
	*/
	@Before(Tx.class)
	public void delete() {
		renderJson(service.delete(getLong(0)));
	}
	
   /**
	* 切换启用状态
	*/
	@Before(Tx.class)
	public void toggleEnable() {
		renderJson(service.toggleEnable(getLong(0)));
	}
	
   /**
	* 切换toggleIsDefault
	*/
	@Before(Tx.class)
	public void toggleIsDefault() {
		renderJson(service.toggleIsDefault(getLong(0)));
	}
	
	
}
