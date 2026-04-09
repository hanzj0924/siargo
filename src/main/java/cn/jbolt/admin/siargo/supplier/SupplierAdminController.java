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

	/** 供应商管理服务 */
	@Inject
	private SupplierService service;
	
	/**
	 * 跳转到供应商管理首页
	 * URL: GET /admin/siargo/supplier
	 */
	public void index() {
		render("index.html");
	}
  
	/**
	 * 获取供应商分页数据列表
	 * URL: GET /admin/siargo/supplier/datas
	 * @return 供应商分页数据JSON
	 */
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords()));
	}
	
	/**
	 * 查询所有供应商列表
	 * URL: GET /admin/siargo/supplier/getSupplierName
	 * @return 所有供应商数据JSON
	 */
	public void getSupplierName() {	
		renderJsonData(service.findAll());
	}
	
	/**
	 * 跳转到新增供应商页面
	 * URL: GET /admin/siargo/supplier/add
	 */
	public void add() {
		render("add.html");
	}
	
	/**
	 * 跳转到编辑供应商页面
	 * URL: GET /admin/siargo/supplier/edit/{id}
	 * @param id 供应商ID，从URL路径获取
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
	 * 保存新增的供应商信息
	 * URL: POST /admin/siargo/supplier/save
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Supplier.class, "supplier")));
	}
	
	/**
	 * 更新供应商信息
	 * URL: POST /admin/siargo/supplier/update
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Supplier.class, "supplier")));
	}
	
	/**
	 * 批量删除供应商
	 * URL: POST /admin/siargo/supplier/deleteByIds
	 * @param ids 供应商ID列表，多个ID用逗号分隔
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
	/**
	 * 供应商排序上移
	 * URL: POST /admin/siargo/supplier/up/{id}
	 * @param id 供应商ID，从URL路径获取
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void up() {
		renderJson(service.up(getLong(0)));
	}
	
	/**
	 * 供应商排序下移
	 * URL: POST /admin/siargo/supplier/down/{id}
	 * @param id 供应商ID，从URL路径获取
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void down() {
		renderJson(service.down(getLong(0)));
	}
	
	/**
	 * 供应商灵活移动排序
	 * URL: POST /admin/siargo/supplier/move
	 * @param id 当前供应商ID
	 * @param otherId 目标位置供应商ID
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void move() {
		renderJson(service.move(getLong("id"),getLong("otherId")));
	}
	
	/**
	 * 初始化供应商排序
	 * URL: POST /admin/siargo/supplier/initRank
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void initRank() {
		renderJson(service.initRank());
	}
	

}
