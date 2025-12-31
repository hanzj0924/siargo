package cn.jbolt.admin.siargo.customer;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Customer;
/**
 * 客户管理 Controller
 * @ClassName: CustomerAdminController
 * @author: hanzj
 * @date: 2025-11-27 11:06
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/customer", viewPath = "/_view/admin/siargo/customer")
//true
public class CustomerAdminController extends JBoltBaseController {

	@Inject
	private CustomerService service;
	
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
	* 新增
	*/
	public void add() {
		render("add.html");
	}
	
   /**
	* 编辑
	*/
	public void edit() {
		Customer customer=service.findById(getLong(0)); 
		if(customer == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("customer",customer);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Customer.class, "customer")));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Customer.class, "customer")));
	}
	
   /**
	* 删除
	*/
    @Before(Tx.class)
	public void delete() {
		renderJson(service.delete(getLong(0)));
	}
	
	
}
