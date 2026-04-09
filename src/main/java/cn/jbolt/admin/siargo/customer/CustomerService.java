package cn.jbolt.admin.siargo.customer;

import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;

import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.siargo.model.Customer;
/**
 * 客户管理 Service
 * @ClassName: CustomerService   
 * @author: hanzj
 * @date: 2025-11-27 11:06  
 */
public class CustomerService extends JBoltBaseService<Customer> {

	/** Customer数据访问对象 */
	private final Customer dao=new Customer().dao();
	
	@Override
	protected Customer dao() {
		return dao;
	}
	
	/**
	 * 后台管理分页查询客户数据
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param keywords 搜索关键词
	 * @return 客户分页数据
	 */
	public Page<Customer> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("id","desc", pageNumber, pageSize, keywords, "name");
	}
	
	/**
	 * 保存新增客户信息
	 * 校验参数有效性后执行保存操作
	 * @param customer 客户实体对象
	 * @return 操作结果，成功返回成功信息，失败返回错误信息
	 */
	public Ret save(Customer customer) {
		if(customer==null || isOk(customer.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(customer.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=customer.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(customer.getId(), JBoltUserKit.getUserId(), customer.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新客户信息
	 * 校验参数有效性和数据存在性后执行更新操作
	 * @param customer 客户实体对象
	 * @return 操作结果，成功返回成功信息，失败返回错误信息
	 */
	public Ret update(Customer customer) {
		if(customer==null || notOk(customer.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		Customer dbCustomer=findById(customer.getId());
		if(dbCustomer==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(customer.getName(), customer.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=customer.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(customer.getId(), JBoltUserKit.getUserId(), customer.getName());
		}
		return ret(success);
	}
	
	/**
	 * 删除指定客户
	 * 执行删除前会检查数据是否可被删除
	 * @param id 客户ID
	 * @return 操作结果
	 */
	public Ret delete(Long id) {
		return deleteById(id,true);
	}
	
	/**
	 * 删除数据后执行的回调方法
	 * 用于执行删除后的额外业务逻辑，如记录系统日志
	 * @param customer 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return 返回null表示正常执行，返回错误信息则阻止删除
	 */
	@Override
	protected String afterDelete(Customer customer, Kv kv) {
		//addDeleteSystemLog(customer.getId(), JBoltUserKit.getUserId(),customer.getName());
		return null;
	}
	
	/**
	 * 检测客户数据是否可以删除
	 * 检查客户是否被其他业务引用，若被引用则阻止删除
	 * @param customer 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return 返回null表示可删除，返回错误信息则阻止删除
	 */
	@Override
	public String checkCanDelete(Customer customer, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(customer, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return 系统日志目标类型
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}
	
}
