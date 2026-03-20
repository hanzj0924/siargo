package cn.jbolt.admin.siargo.supplier;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Supplier;
/**
 * 供应商管理 Controller
 * @ClassName: SupplierAdminController
 * @author: hanzj
 * @date: 2026-03-19 16:30
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/supplier", viewPath = "/_view/admin/siargo/supplier")
//true
public class SupplierAdminController extends JBoltBaseController {

	@Inject
	private SupplierService service;
	
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
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords()));
	}
	
	/**
	* 查询所有供应商
	*/
	public void getSupplierName() {	
		renderJsonData(service.findAll());
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
		Supplier supplier=service.findById(getLong(0)); 
		if(supplier == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("supplier",supplier);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Supplier.class, "supplier")));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Supplier.class, "supplier")));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
  /**
	* 排序 上移
	*/
    @Before(Tx.class)
	public void up() {
		renderJson(service.up(getLong(0)));
	}
	
  /**
	* 排序 下移
	*/
    @Before(Tx.class)
	public void down() {
		renderJson(service.down(getLong(0)));
	}
	
  /**
	*  灵活移动排序
	*/
    @Before(Tx.class)
	public void move() {
		renderJson(service.move(getLong("id"),getLong("otherId")));
	}
	
  /**
	* 排序 初始化
	*/
    @Before(Tx.class)
	public void initRank() {
		renderJson(service.initRank());
	}
	
	
}
