package cn.jbolt.admin.siargo.qarep;

import java.util.List;

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
	public Product productFindByQaId(Long id) {
		String sql = "SELECT * \n"
				+ "FROM\n"
				+ "  `siargo_product` sp\n"
				+ "WHERE\n"
				+ "  sp.vd = 1\n"
				+ "  AND sp.report_id = ? ";
				
		
		return dao.findFirst(sql,id);
	}

	/**
	 * 根据报告单ID查询该报告单下的全部有效产品信息（含字典翻译、各环节检验人姓名、各环节驳回计数、最新驳回记录）
	 * <p>从 QareportService 收敛而来：Product 相关查询统一走本 Service</p>
	 * <p>驳回数据分两部分：</p>
	 * <ul>
	 *   <li>reject_count_2/3/4：各环节历史驳回次数（>0 时前端显示"驳"角标）</li>
	 *   <li>reject_insp/reject_des/reject_time/reject_name/reject_insp_name：最新一条驳回记录（用于当前驳回状态节点展示）</li>
	 * </ul>
	 * @param reportId 报告单ID
	 * @return 产品列表
	 */
	public List<Product> findProductsByReportId(Long reportId) {
		String sql = "SELECT sp.*, "
			+ "d_type.NAME AS type_name, "
			+ "d_insp.NAME AS insp_name, "
			+ "accq_user.NAME AS accq_name, "
			+ "funq_user.NAME AS funq_name, "
			+ "lt_user.NAME AS lt_name, "
			+ "appq_user.NAME AS appq_name, "
			+ "allq_user.NAME AS allq_name, "
			// 各环节驳回历史计数（用于角标显示）
			+ "(SELECT COUNT(*) FROM siargo_product_reject_log r2 WHERE r2.product_id = sp.id AND r2.reject_insp = 2) AS reject_count_2, "
			+ "(SELECT COUNT(*) FROM siargo_product_reject_log r3 WHERE r3.product_id = sp.id AND r3.reject_insp = 3) AS reject_count_3, "
			+ "(SELECT COUNT(*) FROM siargo_product_reject_log r4 WHERE r4.product_id = sp.id AND r4.reject_insp = 4) AS reject_count_4, "
			+ "(SELECT COUNT(*) FROM siargo_product_reject_log r6 WHERE r6.product_id = sp.id AND r6.reject_insp = 6) AS reject_count_6, "
			// 最新一条驳回记录（用于当前驳回状态节点详情）
			+ "rl.reject_insp, rl.reject_des, "
			+ "DATE_FORMAT(rl.reject_time, '%Y-%m-%d %H:%i') AS reject_time, "
			+ "reject_user.NAME AS reject_name, "
			+ "CASE rl.reject_insp WHEN 2 THEN '外观检验' WHEN 3 THEN '包装检验' WHEN 4 THEN '批准' WHEN 6 THEN '成品检漏检验' ELSE '未知环节' END AS reject_insp_name "
			+ "FROM siargo_product sp "
			+ "LEFT JOIN jb_dictionary AS d_type ON d_type.type_key = 'siargo_prod_type' "
			+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(sp.type AS CHAR) "
			+ "AND d_type.enable = '1' "
			+ "LEFT JOIN jb_dictionary AS d_insp ON d_insp.type_key = 'siargo_insp' "
			+ "AND d_insp.sn COLLATE utf8mb4_general_ci = CAST(sp.insp AS CHAR) "
			+ "AND d_insp.enable = '1' "
			+ "LEFT JOIN jb_user AS accq_user ON accq_user.id = sp.accq_uid "
			+ "LEFT JOIN jb_user AS funq_user ON funq_user.id = sp.funq_uid "
			+ "LEFT JOIN jb_user AS lt_user ON lt_user.id = sp.lt_uid "
			+ "LEFT JOIN jb_user AS appq_user ON appq_user.id = sp.appq_uid "
			+ "LEFT JOIN jb_user AS allq_user ON allq_user.id = sp.allq_uid "
			// 关联最新一条驳回日志（按 id DESC 取第一条）
			+ "LEFT JOIN siargo_product_reject_log AS rl ON rl.id = "
			+ "(SELECT rl2.id FROM siargo_product_reject_log rl2 WHERE rl2.product_id = sp.id ORDER BY rl2.id DESC LIMIT 1) "
			+ "LEFT JOIN jb_user AS reject_user ON reject_user.id = rl.reject_uid "
			+ "WHERE sp.report_id = ? AND sp.vd = 1 "
			+ "ORDER BY sp.id ASC";
		return dao.find(sql, reportId);
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
