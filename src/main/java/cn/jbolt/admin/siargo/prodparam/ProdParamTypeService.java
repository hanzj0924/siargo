package cn.jbolt.admin.siargo.prodparam;

import com.jfinal.plugin.activerecord.Page;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.ProdParamType;
/**
 * 型号参数 Service
 * @ClassName: ProdParamTypeService   
 * @author: hanzj
 * @date: 2026-09-02 16:26  
 */
public class ProdParamTypeService extends JBoltBaseService<ProdParamType> {
	private final ProdParamType dao=new ProdParamType().dao();
	@Override
	protected ProdParamType dao() {
		return dao;
	}
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<ProdParamType> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("sort_rank","asc", pageNumber, pageSize, keywords, "type_name", Okv.by("is_active", 1));
	}

	/**
	 * 全部种类（左卡片 mgr portal 全量渲染数据源，按展示顺序）
	 * @return
	 */
	public List<ProdParamType> getListAll() {
		return dao.find("select * from siargo_prod_param_type where is_active=1 order by sort_rank asc,id asc");
	}

	/**
	 * 启用中的种类（输入器 □ 选种类的数据源）
	 * @return
	 */
	public List<ProdParamType> options() {
		return getListAll();
	}

	/**
	 * 判断种类是否启用（SQL 判启用规避 Boolean 陷阱）
	 * @param id
	 * @return
	 */
	public boolean isActiveType(Long id) {
		if(notOk(id)) {return false;}
		return Db.queryLong("select count(1) from siargo_prod_param_type where id=? and is_active=1", id)>0;
	}

	/** 管理入口只允许读取未删除的种类。 */
	public ProdParamType findActiveById(Long id) {
		ProdParamType type=findById(id);
		return type!=null&&Boolean.TRUE.equals(type.getBoolean(ProdParamType.IS_ACTIVE))?type:null;
	}

	/** 写参数值前锁定其种类，避免同时删除种类后产生有效子项。 */
	public ProdParamType findActiveByIdForUpdate(Long id) {
		if(notOk(id)) {return null;}
		return dao.findFirst("select * from siargo_prod_param_type where id=? and is_active=1 for update", id);
	}

	/**
	 * 种类名是否已存在
	 * @param typeName
	 * @param excludeId 编辑排除自身
	 * @return
	 */
	protected boolean existsTypeName(String typeName, Long excludeId) {
		if(excludeId==null) {
			return Db.queryLong("select count(1) from siargo_prod_param_type where type_name=? and is_active=1", typeName)>0;
		}
		return Db.queryLong("select count(1) from siargo_prod_param_type where type_name=? and id<>? and is_active=1", typeName, excludeId)>0;
	}

	/**
	 * 保存
	 * @param prodParamType
	 * @return
	 */
	public Ret save(ProdParamType prodParamType) {
		if(prodParamType==null || isOk(prodParamType.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		String typeName=prodParamType.getTypeName();
		if(notOk(typeName)) {
			return fail("种类名称不能为空");
		}
		if(typeName.length()>20) {
			return fail("种类名称长度不能超过20");
		}
		if(existsTypeName(typeName, null)) {return fail("种类名称已存在");}
		prodParamType.set("sort_rank", nextActiveSortRank());
		prodParamType.set("is_active", 1);
		boolean success=prodParamType.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(prodParamType.getId(), JBoltUserKit.getUserId(), prodParamType.getTypeName());
		}
		return ret(success);
	}

	/** 软删除或手动排序可能留下空隙，新记录必须追加到有效列表末尾。 */
	protected int nextActiveSortRank() {
		return Db.queryInt("select coalesce(max(sort_rank),0)+1 from siargo_prod_param_type where is_active=1");
	}

	/**
	 * 更新
	 * @param prodParamType
	 * @return
	 */
	public Ret update(ProdParamType prodParamType) {
		if(prodParamType==null || notOk(prodParamType.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		ProdParamType dbProdParamType=findActiveById(prodParamType.getId());
		if(dbProdParamType==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		String typeName=prodParamType.getTypeName();
		if(notOk(typeName)) {
			return fail("种类名称不能为空");
		}
		if(typeName.length()>20) {
			return fail("种类名称长度不能超过20");
		}
		if(existsTypeName(typeName, prodParamType.getId())) {return fail("种类名称已存在");}
		prodParamType.remove(ProdParamType.IS_ACTIVE);
		boolean success=updateActiveRecord(prodParamType);
		if(success) {
			//添加日志
			//addUpdateSystemLog(prodParamType.getId(), JBoltUserKit.getUserId(), prodParamType.getTypeName());
		}
		return ret(success);
	}

	/** 只写可编辑字段，条件更新保证已软删除记录不会被编辑请求复活。 */
	protected boolean updateActiveRecord(ProdParamType type) {
		return Db.update("update siargo_prod_param_type set type_name=? where id=? and is_active=1",
				type.getStr("type_name"), type.getLong("id"))==1;
	}
	
	/**
	 * 删除 指定多个ID
	 * @param ids
	 * @return
	 */
	public Ret deleteByBatchIds(String ids) {
		List<Long> selected=parseDeleteIds(ids);
		if(selected==null) {return fail(JBoltMsg.PARAM_ERROR);}
		List<ProdParamType> records=findActiveByIdsForUpdate(selected);
		if(records.size()!=selected.size()) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		for(ProdParamType type:records) {
			String error=checkCanDelete(type, null);
			if(error!=null) {return fail(error);}
		}
		for(ProdParamType type:records) {
			if(!softDeleteRecord(type.getLong("id"))) {return fail("删除失败，请刷新后重试");}
		}
		return SUCCESS;
	}

	protected List<ProdParamType> findActiveByIdsForUpdate(List<Long> ids) {
		String placeholders=String.join(",", Collections.nCopies(ids.size(), "?"));
		return dao.find("select * from siargo_prod_param_type where is_active=1 and id in ("+placeholders+") order by id for update", ids.toArray());
	}

	protected boolean softDeleteRecord(Long id) {
		return Db.update("update siargo_prod_param_type set is_active=0 where id=? and is_active=1", id)==1;
	}

	private static List<Long> parseDeleteIds(String source) {
		if(source==null||source.isBlank()) {return null;}
		String[] parts=source.split(",", -1);
		if(parts.length>500) {return null;}
		Set<Long> ids=new LinkedHashSet<>();
		try {
			for(String part:parts) {
				String text=part.strip();
				if(!text.matches("[1-9][0-9]*")||!ids.add(Long.parseLong(text))) {return null;}
			}
		} catch(NumberFormatException error) {return null;}
		return new ArrayList<>(ids);
	}
	
	/**
	 * 删除数据后执行的回调
	 * @param prodParamType 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(ProdParamType prodParamType, Kv kv) {
		//addDeleteSystemLog(prodParamType.getId(), JBoltUserKit.getUserId(),prodParamType.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param prodParamType 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(ProdParamType prodParamType, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(prodParamType, kv);
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
		return shift(id, -1);
	}
	
	/**
	 * 下移
	 * @param id
	 * @return
	 */
	public Ret down(Long id) {
		return shift(id, 1);
	}

	private Ret shift(Long id, int offset) {
		if(notOk(id)) {return fail(JBoltMsg.PARAM_ERROR);}
		List<ProdParamType> records=new ArrayList<>(listActiveForUpdate());
		int index=-1;
		for(int i=0;i<records.size();i++) {
			if(id.equals(records.get(i).getLong("id"))) {index=i;break;}
		}
		if(index<0) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		int target=index+offset;
		if(target<0) {return fail("已经是第一个");}
		if(target>=records.size()) {return fail("已经是最后一个");}
		Collections.swap(records, index, target);
		return writeSortRanks(records);
	}

	protected List<ProdParamType> listActiveForUpdate() {
		return dao.find("select * from siargo_prod_param_type where is_active=1 order by sort_rank asc,id asc for update");
	}

	private Ret writeSortRanks(List<ProdParamType> records) {
		for(int i=0;i<records.size();i++) {
			ProdParamType type=records.get(i);
			if(Integer.valueOf(i+1).equals(type.getInt("sort_rank"))) {continue;}
			Ret updated=updateColumn(type.getLong("id"), ProdParamType.SORT_RANK, i+1);
			if(!updated.isOk()) {return updated;}
		}
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
		//ProdParamType prodParamType=findById(id);
		//if(prodParamType==null){
		//	return fail("数据不存在或已被删除");
		//}
		//Integer rank=prodParamType.getSortRank();
		//if(rank==null||rank<=0){
		//	return fail("顺序需要初始化");
		//}
		return SUCCESS;
	}
	
	/**
	 * 初始化排序
	 */
	public Ret initRank(){
		return writeSortRanks(listActiveForUpdate());
	}
	
	/**
	 * 检测是否可以删除
	 * @param prodParamType model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkInUse(ProdParamType prodParamType, Kv kv) {
		//种类下有参数值则阻止删除
		if(Db.queryLong("select count(1) from siargo_prod_param_value where param_type_id=? and is_active=1", prodParamType.getId())>0) {
			return "该种类下存在参数值，无法删除";
		}
		//种类已被产品型号引用则阻止删除
		if(Db.queryLong("select count(1) from siargo_prod_model_param where param_type_id=?", prodParamType.getId())>0) {
			return "该种类已被产品型号引用，无法删除";
		}
		return null;
	}
	
}
