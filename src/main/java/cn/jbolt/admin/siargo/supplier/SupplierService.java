package cn.jbolt.admin.siargo.supplier;

import com.jfinal.plugin.activerecord.Page;
import java.util.List;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Supplier;
/**
 * 供应商管理 Service
 * @ClassName: SupplierService   
 * @author: hanzj
 * @date: 2026-03-19 16:30  
 */
public class SupplierService extends JBoltBaseService<Supplier> {

	/** Supplier数据访问对象 */
	private final Supplier dao=new Supplier().dao();
	
	@Override
	protected Supplier dao() {
		return dao;
	}
	
	/**
	 * 后台管理分页查询供应商数据
	 * 按排序字段降序排列，支持按名称关键词搜索
	 * @param pageNumber 页码
	 * @param pageSize 每页条数
	 * @param keywords 搜索关键词
	 * @return 供应商分页数据
	 */
	public Page<Supplier> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("sort_rank","desc", pageNumber, pageSize, keywords, "name");
	}
	
	/**
	 * 保存新增供应商信息
	 * 校验参数有效性后，自动设置排序序号并执行保存操作
	 * @param supplier 供应商实体对象
	 * @return 操作结果，成功返回成功信息，失败返回错误信息
	 */
	public Ret save(Supplier supplier) {
		if(supplier==null || isOk(supplier.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(supplier.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		// 新增时自动设置排序序号为当前最大值+1
		supplier.setSortRank(getNextSortRank());
		boolean success=supplier.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(supplier.getId(), JBoltUserKit.getUserId(), supplier.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新供应商信息
	 * 校验参数有效性和数据存在性后执行更新操作
	 * @param supplier 供应商实体对象
	 * @return 操作结果，成功返回成功信息，失败返回错误信息
	 */
	public Ret update(Supplier supplier) {
		if(supplier==null || notOk(supplier.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		Supplier dbSupplier=findById(supplier.getId());
		if(dbSupplier==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(supplier.getName(), supplier.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=supplier.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(supplier.getId(), JBoltUserKit.getUserId(), supplier.getName());
		}
		return ret(success);
	}
	
	/**
	 * 批量删除指定多个ID的供应商
	 * @param ids 供应商ID字符串，多个ID用逗号分隔
	 * @return 操作结果
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids,true);
	}
	
	/**
	 * 删除数据后执行的回调方法
	 * 用于执行删除后的额外业务逻辑，如记录系统日志
	 * @param supplier 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return 返回null表示正常执行
	 */
	@Override
	protected String afterDelete(Supplier supplier, Kv kv) {
		//addDeleteSystemLog(supplier.getId(), JBoltUserKit.getUserId(),supplier.getName());
		return null;
	}
	
	/**
	 * 检测供应商数据是否可以删除
	 * 检查供应商是否被其他业务引用，若被引用则阻止删除
	 * @param supplier 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return 返回null表示可删除，返回错误信息则阻止删除
	 */
	@Override
	public String checkCanDelete(Supplier supplier, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(supplier, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return 系统日志目标类型
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}
	
	/**
	 * 供应商排序上移
	 * 将指定供应商的排序序号减1，与上一条记录交换位置
	 * @param id 供应商ID
	 * @return 操作结果
	 */
	public Ret up(Long id) {
		Supplier supplier=findById(id);
		if(supplier==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=supplier.getSortRank();
		// 校验排序序号是否有效
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		// 已是第一条，无法上移
		if(rank==1){
			return fail("已经是第一个");
		}
		// 查找上一条记录
		Supplier upSupplier=findFirst(Okv.by("sort_rank", rank-1));
		if(upSupplier==null){
			return fail("顺序需要初始化");
		}
		// 交换排序序号
		upSupplier.setSortRank(rank);
		supplier.setSortRank(rank-1);
		
		upSupplier.update();
		supplier.update();
		return SUCCESS;
	}
	
	/**
	 * 供应商排序下移
	 * 将指定供应商的排序序号加1，与下一条记录交换位置
	 * @param id 供应商ID
	 * @return 操作结果
	 */
	public Ret down(Long id) {
		Supplier supplier=findById(id);
		if(supplier==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=supplier.getSortRank();
		// 校验排序序号是否有效
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		int max=getCount();
		// 已是最后一条，无法下移
		if(rank==max){
			return fail("已经是最后已一个");
		}
		// 查找下一条记录
		Supplier upSupplier=findFirst(Okv.by("sort_rank", rank+1));
		if(upSupplier==null){
			return fail("顺序需要初始化");
		}
		// 交换排序序号
		upSupplier.setSortRank(rank);
		supplier.setSortRank(rank+1);
		
		upSupplier.update();
		supplier.update();
		return SUCCESS;
	}
	
	/**
	 * 供应商灵活移动排序
	 * 将指定供应商移动到目标供应商的位置
	 * @param id 当前供应商ID
	 * @param otherId 目标位置供应商ID
	 * @return 操作结果
	 */
	public Ret move(Long id,Long otherId) {
	//TODO 未完整实现 有待底层实现
		//Supplier supplier=findById(id);
		//if(supplier==null){
		//	return fail("数据不存在或已被删除");
		//}
		//Integer rank=supplier.getSortRank();
		//if(rank==null||rank<=0){
		//	return fail("顺序需要初始化");
		//}
		return SUCCESS;
	}
	
	/**
	 * 初始化所有供应商的排序序号
	 * 按现有顺序重新设置排序序号，从1开始递增
	 * @return 操作结果
	 */
	public Ret initRank(){
		List<Supplier> allList=findAll();
		if(allList.size()>0){
			// 遍历所有记录，按顺序设置排序序号
			for(int i=0;i<allList.size();i++){
				allList.get(i).setSortRank(i+1);
			}
			batchUpdate(allList);
		}
		//添加日志
		//addUpdateSystemLog(null, JBoltUserKit.getUserId(), "所有数据", "的顺序:初始化所有");
		return SUCCESS;
	}
	
	/**
	 * 检测供应商是否被其他业务引用
	 * @param supplier model
	 * @param kv 携带额外参数一般用不上
	 * @return 返回null表示未被引用，返回错误信息表示被引用
	 */
	@Override
	public String checkInUse(Supplier supplier, Kv kv) {
		//这里用来覆盖 检测Supplier是否被其它表引用
		return null;
	}
	
}
