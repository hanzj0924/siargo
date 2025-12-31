package cn.jbolt._admin.customtablerender.custombusiness;

import com.jfinal.plugin.activerecord.Page;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.kit.JBoltUserKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt._admin.customtablerender.common.model.CustomBusiness;
/**
 * 系统业务定义
 * @ClassName: CustomBusinessService
 * @author: 总管理
 * @date: 2023-05-30 00:05  
 */
public class CustomBusinessService extends JBoltBaseService<CustomBusiness> {
	private final CustomBusiness dao=new CustomBusiness().dao();
	@Override
	protected CustomBusiness dao() {
		return dao;
	}

	@Override
    protected int systemLogTargetType() {
        return ProjectSystemLogTargetType.CUSTOM_BUSINESS.getValue();
    }
		
	/**
	 * 后台管理数据查询
	 * @param pageNumber 第几页
	 * @param pageSize   每页几条数据
	 * @param keywords   关键词
     * @param enable 是否启用
	 * @return
	 */
	public Page<CustomBusiness> getAdminDatas(int pageNumber, int pageSize, String keywords, Boolean enable) {
	    //创建sql对象
	    Sql sql = selectSql().page(pageNumber,pageSize);
	    //sql条件处理
        sql.eqBooleanToChar("enable",enable);
        //关键词模糊查询
        sql.likeMulti(keywords,"name", "remark");
        //排序
        sql.desc("id");
		return paginate(sql);
	}
	
	/**
	 * 保存
	 * @param customBusiness
	 * @return
	 */
	public Ret save(CustomBusiness customBusiness) {
		if(customBusiness==null || isOk(customBusiness.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		if(existsName(customBusiness.getName())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=customBusiness.save();
		if(success) {
			//添加日志
			addSaveSystemLog(customBusiness.getId(), JBoltUserKit.getUserId(), customBusiness.getName());
		}
		return ret(success);
	}
	
	/**
	 * 更新
	 * @param customBusiness
	 * @return
	 */
	public Ret update(CustomBusiness customBusiness) {
		if(customBusiness==null || notOk(customBusiness.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		CustomBusiness dbCustomBusiness=findById(customBusiness.getId());
		if(dbCustomBusiness==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
        if(existsName(customBusiness.getName(), customBusiness.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
		boolean success=customBusiness.update();
		if(success) {
			//添加日志
			addUpdateSystemLog(customBusiness.getId(), JBoltUserKit.getUserId(), customBusiness.getName());
		}
		return ret(success);
	}
	
	/**
	 * 检测是否可以删除
	 * @param customBusiness 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(CustomBusiness customBusiness, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(customBusiness, kv);
	}
	
	/**
	 * 删除数据后执行的回调
	 * @param customBusiness 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(CustomBusiness customBusiness, Kv kv) {
		addDeleteSystemLog(customBusiness.getId(), JBoltUserKit.getUserId(),customBusiness.getName());
		return null;
	}
	
	/**
	 * 检测是否可以删除
	 * @param customBusiness model
	 * @param kv 携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkInUse(CustomBusiness customBusiness, Kv kv) {
		//这里用来覆盖 检测是否被其它表引用
		return null;
	}
	
	/**
	 * toggle操作执行后的回调处理
	 */
	@Override
	protected String afterToggleBoolean(CustomBusiness customBusiness, String column, Kv kv) {
		addUpdateSystemLog(customBusiness.getId(), JBoltUserKit.getUserId(), customBusiness.getName(),"的字段["+column+"]值:"+customBusiness.get(column));
		/**
		switch(column){
		    case "enable":
		        break;
		}
		*/
		return null;
	}
	
}