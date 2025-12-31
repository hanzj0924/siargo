package cn.jbolt.admin.siargo.dict;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;

import javax.swing.undo.AbstractUndoableEdit;

import org.checkerframework.framework.qual.FromByteCode;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.siargo.model.Dictionary;
/**
 * 检验报告单管理 Service
 * @ClassName: DictionaryService   
 * @author: hanzj
 * @date: 2025-11-28 13:53  
 */
public class DictionaryService extends JBoltBaseService<Dictionary> {
	private final Dictionary dao=new Dictionary().dao();
	@Override
	protected Dictionary dao() {
		return dao;
	}
	


	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<Dictionary> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
		return paginateByKeywords("id","desc", pageNumber, pageSize, keywords, "order_id");
	}
	
	/**
	 * 保存
	 * @param dictionary
	 * @return
	 */
	public Ret save(Dictionary dictionary) {
		if(dictionary==null || isOk(dictionary.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//if(existsName(dictionary.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=dictionary.save();
		if(success) {
			//添加日志
			//addSaveSystemLog(dictionary.getId(), JBoltUserKit.getUserId(), dictionary.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param dictionary
	 * @return
	 */
	public Ret update(Dictionary dictionary) {
		if(dictionary==null || notOk(dictionary.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		Dictionary dbDictionary=findById(dictionary.getId());
		if(dbDictionary==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		//if(existsName(dictionary.getName(), dictionary.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=dictionary.update();
		if(success) {
			//添加日志
			//addUpdateSystemLog(dictionary.getId(), JBoltUserKit.getUserId(), dictionary.getName());
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
	 * @param dictionary 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(Dictionary dictionary, Kv kv) {
		//addDeleteSystemLog(dictionary.getId(), JBoltUserKit.getUserId(),dictionary.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param dictionary 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(Dictionary dictionary, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(dictionary, kv);
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