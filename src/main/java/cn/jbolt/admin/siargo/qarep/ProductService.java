package cn.jbolt.admin.siargo.qarep;

import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Product;
/**
 * 检验报告单管理 Service
 * @ClassName: ProductService   
 * @author: hanzj
 * @date: 2025-12-16 17:06  
 */
public class ProductService extends JBoltBaseService<Product> {
	/** 产品数据访问对象 */
	private final Product dao=new Product().dao();
	@Override
	protected Product dao() {
		return dao;
	}
	
	/**
	 * 根据报告单ID查询关联的产品记录
	 * @param id 报告单ID
	 * @return 产品记录，未找到时返回null
	 */
	public Product producutFindByQaId(Long id) {
		String sql = "SELECT * \n"
				+ "FROM\n"
				+ "  `siargo_product` sp\n"
				+ "WHERE\n"
				+ "  sp.vd = 1\n"
				+ "  AND sp.report_id = ? ";
				
		
		return dao.findFirst(sql,id);
	}
		
	/**
	 * 后台管理分页查询产品列表
	 * <p>按ID降序排列，支持按订单号关键字模糊搜索</p>
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @param keywords 搜索关键字（匹配订单号）
	 * @return 分页数据
	 */
	public Page<Product> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("id","desc", pageNumber, pageSize, keywords, "order_id");
	}
	
	/**
	 * 保存新产品记录
	 * <p>用于检验报告单创建时同时保存关联的产品数据</p>
	 * @param product 产品对象
	 * @return 操作结果
	 */
	public Ret save(Product product) {
		if(product==null || isOk(product.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(product.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=product.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(product.getId(), JBoltUserKit.getUserId(), product.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新产品记录
	 * <p>用于编辑检验报告单时更新关联的产品数据，更新前会检查数据是否存在</p>
	 * @param product 产品对象
	 * @return 操作结果
	 */
	public Ret update(Product product) {
		if(product==null || notOk(product.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		Product dbProduct=findById(product.getId());
		if(dbProduct==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(product.getName(), product.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=product.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(product.getId(), JBoltUserKit.getUserId(), product.getName());
		}
		return ret(success);
	}
	
	/**
	 * 批量删除产品记录
	 * <p>根据ID字符串批量删除，多个ID用逗号分隔</p>
	 * @param ids 产品ID字符串，多个ID用逗号分隔
	 * @return 操作结果
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids,true);
	}
	
	/**
	 * 删除数据后执行的回调方法
	 * <p>可用于执行删除后的清理操作或日志记录</p>
	 * @param product 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return 返回null表示正常执行删除
	 */
	@Override
	protected String afterDelete(Product product, Kv kv) {
		//addDeleteSystemLog(product.getId(), JBoltUserKit.getUserId(),product.getName());
		return null;
	}
	
	/**
	 * 检测产品是否可以被删除
	 * <p>检查产品是否被其他数据引用，如果被引用则阻止删除</p>
	 * @param product 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return 返回null表示可以删除，返回错误信息则阻止删除
	 */
	@Override
	public String checkCanDelete(Product product, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(product, kv);
	}
	
	/**
	 * 设置系统日志的目标类型
	 * <p>用于标识二开业务所属的日志类型，当前返回NONE表示不记录系统日志</p>
	 * @return 日志目标类型
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}
	
}