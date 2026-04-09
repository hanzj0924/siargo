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

	/** 客户管理服务 */
	@Inject
	private CustomerService service;
	
	/**
	 * 跳转到客户管理首页
	 * URL: GET /admin/siargo/customer
	 */
	public void index() {
		render("index.html");
	}
	
	/**
	 * 获取客户分页数据列表
	 * URL: GET /admin/siargo/customer/datas
	 * @return 客户分页数据JSON
	 */
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords()));
	}
	
	/**
	 * 跳转到新增客户页面
	 * URL: GET /admin/siargo/customer/add
	 */
	public void add() {
		render("add.html");
	}
	
	/**
	 * 跳转到编辑客户页面
	 * URL: GET /admin/siargo/customer/edit/{id}
	 * @param id 客户ID，从URL路径获取
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
	 * 保存新增的客户信息
	 * URL: POST /admin/siargo/customer/save
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Customer.class, "customer")));
	}
	
	/**
	 * 更新客户信息
	 * URL: POST /admin/siargo/customer/update
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Customer.class, "customer")));
	}
	
	/**
	 * 删除指定客户
	 * URL: POST /admin/siargo/customer/delete/{id}
	 * @param id 客户ID，从URL路径获取
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void delete() {
		renderJson(service.delete(getLong(0)));
	}
	

}
