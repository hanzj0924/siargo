package cn.jbolt.admin.siargo.dict;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;

import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.siargo.model.Dictionarytype;
/**
 * 检验报告单管理 Service
 * @ClassName: DictionarytypeService   
 * @author: hanzj
 * @date: 2025-11-28 13:53  
 */
public class DictionarytypeService extends JBoltBaseService<Dictionarytype> {
	private final Dictionarytype dao=new Dictionarytype().dao();
	@Override
	protected Dictionarytype dao() {
		return dao;
	}
	
	
	
	/**
	 * 字典分组查询
	 * @param keywords
	 * @return
	 */
	public List<Record> getDictName( String keywords) {
		Sql sql = Sql.mysql()
				.select("dic.key as dickey","dic.value as dicvalue")
				.from(table(),"dict")
				.leftJoin("siargo_dictionary","dic","dic.type_id = dict.id")
				.like("dict_type", keywords)
				.orderBy("dic.id", false);
		return findRecord(sql);
	}
	
	
		
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<Dictionarytype> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("id","desc", pageNumber, pageSize, keywords, "order_id");
	}
	
	/**
	 * 保存
	 * @param dictionarytype
	 * @return
	 */
	public Ret save(Dictionarytype dictionarytype) {
		if(dictionarytype==null || isOk(dictionarytype.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(dictionarytype.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=dictionarytype.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(dictionarytype.getId(), JBoltUserKit.getUserId(), dictionarytype.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param dictionarytype
	 * @return
	 */
	public Ret update(Dictionarytype dictionarytype) {
		if(dictionarytype==null || notOk(dictionarytype.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		Dictionarytype dbDictionarytype=findById(dictionarytype.getId());
		if(dbDictionarytype==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(dictionarytype.getName(), dictionarytype.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=dictionarytype.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(dictionarytype.getId(), JBoltUserKit.getUserId(), dictionarytype.getName());
		}
		return ret(success);
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public Ret delete(Long id) {
		return deleteById(id,true);
	}
	
	/**
	 * 删除数据后执行的回调
	 * @param dictionarytype 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(Dictionarytype dictionarytype, Kv kv) {
		//addDeleteSystemLog(dictionarytype.getId(), JBoltUserKit.getUserId(),dictionarytype.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param dictionarytype 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(Dictionarytype dictionarytype, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(dictionarytype, kv);
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