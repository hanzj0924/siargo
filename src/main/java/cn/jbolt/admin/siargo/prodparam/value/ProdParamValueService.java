package cn.jbolt.admin.siargo.prodparam.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.jfinal.aop.Inject;
import cn.jbolt.admin.siargo.prodmodel.ProdModelValueDeletionService;
import cn.jbolt.admin.siargo.prodparam.ProdParamTypeService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.ProdParamValue;
/**
 * 参数可选值 Service
 * @ClassName: ProdParamValueService
 * @author: hanzj
 * @date: 2026-09-03 09:13
 */
public class ProdParamValueService extends JBoltBaseService<ProdParamValue> {
	private final ProdParamValue dao=new ProdParamValue().dao();
	@Inject
	private ProdParamTypeService typeService;
	@Inject
	private ProdModelValueDeletionService modelValueDeletionService;
	@Override
	protected ProdParamValue dao() {
		return dao;
	}

	/**
	 * 查询某个参数种类下的全部可选值（portal 右侧卡片数据源，值少全量渲染无分页）
	 * @param paramTypeId 参数种类id
	 * @return
	 */
	public List<ProdParamValue> getListByType(Long paramTypeId) {
		if(!typeService.isActiveType(paramTypeId)) {return new ArrayList<>();}
		return dao.find("select * from siargo_prod_param_value where param_type_id=? and is_active=1 order by sort_rank asc,id asc", paramTypeId);
	}

	/**
	 * 某个参数种类下启用中的可选值（输入器 □ 选值的多选数据源）
	 * @param paramTypeId
	 * @return
	 */
	public List<ProdParamValue> optionsByType(Long paramTypeId) {
		return getListByType(paramTypeId);
	}

	/**
	 * 判断某个值是否属于该种类且处于启用状态（后端二次校验用，SQL 判启用规避 Boolean 陷阱）
	 * @param typeId
	 * @param valueId
	 * @return
	 */
	public boolean isActiveValueOfType(Long typeId, Long valueId) {
		if(notOk(typeId)||notOk(valueId)) {return false;}
		if(!typeService.isActiveType(typeId)) {return false;}
		return Db.queryLong("select count(1) from siargo_prod_param_value where id=? and param_type_id=? and is_active=1", valueId, typeId)>0;
	}

	/** 管理入口不能读取已删除的值或已删除种类下的值。 */
	public ProdParamValue findActiveById(Long id) {
		ProdParamValue value=findById(id);
		return value!=null&&Boolean.TRUE.equals(value.getBoolean(ProdParamValue.IS_ACTIVE))
				&&typeService.isActiveType(value.getLong("param_type_id"))?value:null;
	}

	/**
	 * 按 id 集合查值记录（含停用，编辑回显时被引用过的停用值不丢失）
	 * @param ids
	 * @return
	 */
	public List<ProdParamValue> listValueRecordsByIds(Collection<Long> ids) {
		if(ids==null||ids.isEmpty()) {return new ArrayList<>();}
		StringBuilder sql=new StringBuilder("select * from siargo_prod_param_value where id in (");
		for(int i=0;i<ids.size();i++) {
			sql.append("?,");
		}
		sql.setLength(sql.length()-1);
		sql.append(")");
		return dao.find(sql.toString(), ids.toArray());
	}

	/** 型号保存与参数删除统一先按值 ID 加锁，包含历史停用值；须在调用方事务内执行。 */
	public List<ProdParamValue> listValueRecordsByIdsForUpdate(Collection<Long> ids) {
		if(ids==null||ids.isEmpty()) {return new ArrayList<>();}
		List<Long> selected=new ArrayList<>(new LinkedHashSet<>(ids));
		String placeholders=String.join(",", Collections.nCopies(selected.size(), "?"));
		return dao.find("select * from siargo_prod_param_value where id in ("+placeholders+") order by id for update", selected.toArray());
	}

	/**
	 * 保存
	 * @param prodParamValue
	 * @return
	 */
	public Ret save(ProdParamValue prodParamValue) {
		if(prodParamValue==null || isOk(prodParamValue.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Long paramTypeId=prodParamValue.getParamTypeId();
		String paramValue=prodParamValue.getParamValue();
		if(notOk(paramTypeId)||notOk(paramValue)) {
			return fail("参数种类和参数值不能为空");
		}
		if(typeService.findActiveByIdForUpdate(paramTypeId)==null) {
			return fail("参数种类不存在或已删除，请刷新后重新选择");
		}
		if(paramValue.length()>50) {
			return fail("参数值长度不能超过50");
		}
		prodParamValue.set("sort_rank", nextActiveSortRank(paramTypeId));
		prodParamValue.set("is_active", 1);
		boolean success=prodParamValue.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(prodParamValue.getId(), JBoltUserKit.getUserId(), prodParamValue.getParamValue());
		}
		return ret(success);
	}

	/** 只在当前种类的有效值之后追加，不能按有效记录数量推算序号。 */
	protected int nextActiveSortRank(Long paramTypeId) {
		return Db.queryInt("select coalesce(max(sort_rank),0)+1 from siargo_prod_param_value where param_type_id=? and is_active=1", paramTypeId);
	}

	/**
	 * 更新
	 * @param prodParamValue
	 * @return
	 */
	public Ret update(ProdParamValue prodParamValue) {
		if(prodParamValue==null || notOk(prodParamValue.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		ProdParamValue dbProdParamValue=findActiveById(prodParamValue.getId());
		if(dbProdParamValue==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//参数值所属种类不允许修改
		Long paramTypeId=prodParamValue.getParamTypeId();
		if(notOk(paramTypeId)||!paramTypeId.equals(dbProdParamValue.getParamTypeId())) {
			return fail("参数值所属种类不允许修改");
		}
		if(typeService.findActiveByIdForUpdate(paramTypeId)==null) {return fail("参数种类不存在或已删除");}
		String paramValue=prodParamValue.getParamValue();
		if(notOk(paramValue)) {
			return fail("参数值不能为空");
		}
		if(paramValue.length()>50) {
			return fail("参数值长度不能超过50");
		}
		prodParamValue.remove(ProdParamValue.IS_ACTIVE);
		boolean success=updateActiveRecord(prodParamValue);
		if(success) {
			//添加日志
			//addUpdateSystemLog(prodParamValue.getId(), JBoltUserKit.getUserId(), prodParamValue.getParamValue());
		}
		return ret(success);
	}

	/** 只更新内容，软删除标记、所属种类及排序均不接受编辑请求修改。 */
	protected boolean updateActiveRecord(ProdParamValue value) {
		return Db.update("update siargo_prod_param_value set param_value=?,description=? where id=? and param_type_id=? and is_active=1",
				value.getStr("param_value"), value.getStr("description"), value.getLong("id"), value.getLong("param_type_id"))==1;
	}

	/**
	 * 物理删除指定多个ID，并同步移除型号结构及关联中的值引用；由 Controller 持有完整事务。
	 * @param ids
	 * @return
	 */
	public Ret deleteByBatchIds(String ids) {
		List<Long> selected=parseDeleteIds(ids);
		if(selected==null) {return fail(JBoltMsg.PARAM_ERROR);}
		List<ProdParamValue> records=findActiveByIdsForUpdate(selected);
		if(records.size()!=selected.size()) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		if(!removeModelReferences(selected)) {return fail("型号参数引用清理失败，请刷新后重试");}
		for(ProdParamValue value:records) {
			if(!deleteValueRecord(value.getLong("id"))) {return fail("删除失败，请刷新后重试");}
		}
		return SUCCESS;
	}

	protected boolean removeModelReferences(List<Long> ids) {
		return modelValueDeletionService.removeParameterValues(ids);
	}

	protected List<ProdParamValue> findActiveByIdsForUpdate(List<Long> ids) {
		String placeholders=String.join(",", Collections.nCopies(ids.size(), "?"));
		return dao.find("select * from siargo_prod_param_value where is_active=1 and id in ("+placeholders+") order by id for update", ids.toArray());
	}

	protected boolean deleteValueRecord(Long id) {
		return Db.delete("delete from siargo_prod_param_value where id=? and is_active=1", id)==1;
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
	 * @param prodParamValue 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(ProdParamValue prodParamValue, Kv kv) {
		//addDeleteSystemLog(prodParamValue.getId(), JBoltUserKit.getUserId(),prodParamValue.getParamValue());
		return null;
	}

	/**
	 * 检测是否可以删除
	 * @param prodParamValue 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(ProdParamValue prodParamValue, Kv kv) {
		// 仅保护未清理关联的继承删除入口；管理入口 deleteByBatchIds 先清理引用再物理删除。
		return checkInUse(prodParamValue, kv);
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
	 * 上移（只在同一种类内交换）
	 * @param id
	 * @return
	 */
	public Ret up(Long id) {
		return shift(id, -1);
	}

	/**
	 * 下移（只在同一种类内交换）
	 * @param id
	 * @return
	 */
	public Ret down(Long id) {
		return shift(id, 1);
	}

	private Ret shift(Long id, int offset) {
		ProdParamValue value=findActiveById(id);
		if(value==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		Long typeId=value.getLong("param_type_id");
		if(typeService.findActiveByIdForUpdate(typeId)==null) {return fail("参数种类不存在或已删除");}
		List<ProdParamValue> records=new ArrayList<>(listActiveForUpdate(typeId));
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

	protected List<ProdParamValue> listActiveForUpdate(Long typeId) {
		return dao.find("select * from siargo_prod_param_value where param_type_id=? and is_active=1 order by sort_rank asc,id asc for update", typeId);
	}

	private Ret writeSortRanks(List<ProdParamValue> records) {
		for(int i=0;i<records.size();i++) {
			ProdParamValue value=records.get(i);
			if(Integer.valueOf(i+1).equals(value.getInt("sort_rank"))) {continue;}
			Ret updated=updateColumn(value.getLong("id"), ProdParamValue.SORT_RANK, i+1);
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
		return SUCCESS;
	}

	/**
	 * 初始化某个种类下的排序（按当前顺序重排 1..N）
	 * @param paramTypeId
	 */
	public Ret initRankByType(Long paramTypeId){
		if(typeService.findActiveByIdForUpdate(paramTypeId)==null) {return fail("参数种类不存在或已删除");}
		return writeSortRanks(listActiveForUpdate(paramTypeId));
	}

	/**
	 * 检测是否可以删除
	 * @param prodParamValue model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkInUse(ProdParamValue prodParamValue, Kv kv) {
		//列出全部引用型号；关联型号缺失时仍保留引用保护，并显示型号ID。
		List<String> modelNames=Db.query("select distinct coalesce(nullif(pm.model_series,''),concat('型号ID：',mp.model_id)) as model_name "
				+"from siargo_prod_model_param mp left join siargo_prod_model pm on pm.id=mp.model_id "
				+"where mp.param_value_id=? order by model_name", prodParamValue.getLong("id"));
		if(!modelNames.isEmpty()) {
			return "参数值已被【"+String.join("、", modelNames)+"】引用，无法删除 !";
		}
		return null;
	}

}
