package cn.jbolt.admin.siargo.qarep;

import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Product;
/**
 * 检验报告单管理 Service
 * @ClassName: ProductService   
 * @author: hanzj
 * @date: 2025-12-16 17:06  
 */
public class ProductService extends JBoltBaseService<Product> {
	private final Product dao=new Product().dao();
	@Override
	protected Product dao() {
		return dao;
	}
	
	
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
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<Product> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("id","desc", pageNumber, pageSize, keywords, "order_id");
	}
	
	/**
	 * 保存
	 * @param product
	 * @return
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
	 * 更新
	 * @param product
	 * @return
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
	 * 删除 指定多个ID
	 * @param ids
	 * @return
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids,true);
	}
	
	/**
	 * 删除数据后执行的回调
	 * @param product 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(Product product, Kv kv) {
		//addDeleteSystemLog(product.getId(), JBoltUserKit.getUserId(),product.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param product 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(Product product, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(product, kv);
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