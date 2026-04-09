package cn.jbolt.admin.siargo.dms.category;

import com.jfinal.plugin.activerecord.Page;
import java.util.List;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.DmsCategory;
import com.jfinal.plugin.activerecord.Record;
/**
 * 文件类别表管理 Service
 * @ClassName: DmsCategoryService   
 * @author: hanzj
 * @date: 2026-03-23 13:31  
 */
public class DmsCategoryService extends JBoltBaseService<DmsCategory> {
	/** 数据访问对象 */
	private final DmsCategory dao=new DmsCategory().dao();
	@Override
	protected DmsCategory dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<DmsCategory> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("sort_rank","asc", pageNumber, pageSize, keywords, "name");
	}
	
	/**
	 * 保存
	 * @param dmsCategory
	 * @return
	 */
	public Ret save(DmsCategory dmsCategory) {
		if(dmsCategory==null || isOk(dmsCategory.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(dmsCategory.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		dmsCategory.setSortRank(getNextSortRank());
		boolean success=dmsCategory.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(dmsCategory.getId(), JBoltUserKit.getUserId(), dmsCategory.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param dmsCategory
	 * @return
	 */
	public Ret update(DmsCategory dmsCategory) {
		if(dmsCategory==null || notOk(dmsCategory.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		DmsCategory dbDmsCategory=findById(dmsCategory.getId());
		if(dbDmsCategory==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(dmsCategory.getName(), dmsCategory.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=dmsCategory.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(dmsCategory.getId(), JBoltUserKit.getUserId(), dmsCategory.getName());
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
	 * @param dmsCategory 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(DmsCategory dmsCategory, Kv kv) {
		//addDeleteSystemLog(dmsCategory.getId(), JBoltUserKit.getUserId(),dmsCategory.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param dmsCategory 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(DmsCategory dmsCategory, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(dmsCategory, kv);
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
	 * 类别排序上移
	 * 业务逻辑：将当前类别与排序序号比它小1的类别交换序号
	 * @param id 类别ID
	 * @return 操作结果
	 */
	public Ret up(Long id) {
		DmsCategory dmsCategory=findById(id);
		if(dmsCategory==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=dmsCategory.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		// 已经是第一个，无法上移
		if(rank==1){
			return fail("已经是第一个");
		}
		// 找到上一个类别（排序序号比当前小1）
		DmsCategory upDmsCategory=findFirst(Okv.by("sort_rank", rank-1));
		if(upDmsCategory==null){
			return fail("顺序需要初始化");
		}
		// 交换两个类别的排序序号
		upDmsCategory.setSortRank(rank);
		dmsCategory.setSortRank(rank-1);
		
		upDmsCategory.update();
		dmsCategory.update();
		return SUCCESS;
	}
	
	/**
	 * 类别排序下移
	 * 业务逻辑：将当前类别与排序序号比它大1的类别交换序号
	 * @param id 类别ID
	 * @return 操作结果
	 */
	public Ret down(Long id) {
		DmsCategory dmsCategory=findById(id);
		if(dmsCategory==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=dmsCategory.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		// 获取类别总数，判断是否已经是最后一个
		int max=getCount();
		// 已经是最后一个，无法下移
		if(rank==max){
			return fail("已经是最后已一个");
		}
		// 找到下一个类别（排序序号比当前大1）
		DmsCategory upDmsCategory=findFirst(Okv.by("sort_rank", rank+1));
		if(upDmsCategory==null){
			return fail("顺序需要初始化");
		}
		// 交换两个类别的排序序号
		upDmsCategory.setSortRank(rank);
		dmsCategory.setSortRank(rank+1);
		
		upDmsCategory.update();
		dmsCategory.update();
		return SUCCESS;
	}
	
	/**
	 * 灵活移动类别排序位置
	 * 业务场景：将类别移动到另一个类别的位置，中间的类别相应调整
	 * 注意：当前方法尚未完整实现，待后续补充移动逻辑
	 * @param id 要移动的类别ID
	 * @param otherId 目标位置的类别ID
	 * @return 操作结果
	 */
	public Ret move(Long id,Long otherId) {
	//TODO 未完整实现 有待底层实现
		//DmsCategory dmsCategory=findById(id);
		//if(dmsCategory==null){
		//	return fail("数据不存在或已被删除");
		//}
		//Integer rank=dmsCategory.getSortRank();
		//if(rank==null||rank<=0){
		//	return fail("顺序需要初始化");
		//}
		return SUCCESS;
	}
	
	/**
	 * 初始化排序
	 */
	public Ret initRank(){
		List<DmsCategory> allList=findAll();
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
	 * @param dmsCategory model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkInUse(DmsCategory dmsCategory, Kv kv) {
		//这里用来覆盖 检测DmsCategory是否被其它表引用
		return null;
	}
	
	/**
	 * 获取所有类别列表，附带每个类别下的文件数量
	 * 用于前端展示类别选择器或统计信息
	 * @return 类别列表（Record格式），每项包含 id, name, sortRank, fileCount
	 */
	public List<Record> getCategoryListWithCount() {
		// 使用驼峰风格别名，与前端 JavaScript 保持一致
		String sql = "SELECT c.id, c.name, c.sort_rank AS sortRank, COUNT(f.id) AS fileCount " +
				"FROM siargo_dms_category c " +
				"LEFT JOIN siargo_dms_file f ON f.category_id = c.id AND f.status = 1 " +
				"GROUP BY c.id, c.name, c.sort_rank " +
				"ORDER BY c.sort_rank ASC";
		return Db.find(sql);
	}
	
}