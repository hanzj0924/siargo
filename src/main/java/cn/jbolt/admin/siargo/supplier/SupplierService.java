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
	private final Supplier dao=new Supplier().dao();
	@Override
	protected Supplier dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<Supplier> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("sort_rank","desc", pageNumber, pageSize, keywords, "name");
	}
	
	/**
	 * 保存
	 * @param supplier
	 * @return
	 */
	public Ret save(Supplier supplier) {
		if(supplier==null || isOk(supplier.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(supplier.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		supplier.setSortRank(getNextSortRank());
		boolean success=supplier.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(supplier.getId(), JBoltUserKit.getUserId(), supplier.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param supplier
	 * @return
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
	 * 删除 指定多个ID
	 * @param ids
	 * @return
	 */
	public Ret deleteByBatchIds(String ids) {
		return deleteByIds(ids,true);
	}
	
	/**
	 * 删除数据后执行的回调
	 * @param supplier 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(Supplier supplier, Kv kv) {
		//addDeleteSystemLog(supplier.getId(), JBoltUserKit.getUserId(),supplier.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param supplier 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(Supplier supplier, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(supplier, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType 
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}
	
	/**
	 * 上移
	 * @param id
	 * @return
	 */
	public Ret up(Long id) {
		Supplier supplier=findById(id);
		if(supplier==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=supplier.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		if(rank==1){
			return fail("已经是第一个");
		}
		Supplier upSupplier=findFirst(Okv.by("sort_rank", rank-1));
		if(upSupplier==null){
			return fail("顺序需要初始化");
		}
		upSupplier.setSortRank(rank);
		supplier.setSortRank(rank-1);
		
		upSupplier.update();
		supplier.update();
		return SUCCESS;
	}
	
	/**
	 * 下移
	 * @param id
	 * @return
	 */
	public Ret down(Long id) {
		Supplier supplier=findById(id);
		if(supplier==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=supplier.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		int max=getCount();
		if(rank==max){
			return fail("已经是最后已一个");
		}
		Supplier upSupplier=findFirst(Okv.by("sort_rank", rank+1));
		if(upSupplier==null){
			return fail("顺序需要初始化");
		}
		upSupplier.setSortRank(rank);
		supplier.setSortRank(rank+1);
		
		upSupplier.update();
		supplier.update();
		return SUCCESS;
	}
	
	/**
	 * 移动
	 * @param id
	 * @param otherId
	 * @return
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
	 * 初始化排序
	 */
	public Ret initRank(){
		List<Supplier> allList=findAll();
		if(allList.size()>0){
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
	 * 检测是否可以删除
	 * @param supplier model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkInUse(Supplier supplier, Kv kv) {
		//这里用来覆盖 检测Supplier是否被其它表引用
		return null;
	}
	
}