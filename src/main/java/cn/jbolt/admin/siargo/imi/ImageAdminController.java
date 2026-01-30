package cn.jbolt.admin.siargo.imi;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Image;
/**
 * 来料到货单管理 Controller
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
	* 首页
	*/
	public void index() {
		render("index.html");
	}
  	
  	/**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),getPara("supplierId")));
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
		Image image=service.findById(getLong(0)); 
		if(image == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("image",image);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Image.class, "image")));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Image.class, "image")));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
	
}
