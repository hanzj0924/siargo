package cn.jbolt._admin.msgcenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.ArrayUtil;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.JBoltProSystemLogTargetType;
import cn.jbolt.common.model.SysNotice;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.cache.JBoltDeptCache;
import cn.jbolt.core.cache.JBoltPostCache;
import cn.jbolt.core.cache.JBoltRoleCache;
import cn.jbolt.core.cache.JBoltUserCache;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.db.sql.SqlExpress;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.User;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.util.JBoltArrayUtil;
import net.dreamlu.event.EventKit;
/**
 * 系统通知管理 Service
 * @ClassName: SysNoticeService   
 * @author: JBolt-Generator
 * @date: 2021-04-03 18:56  
 */
public class SysNoticeService extends JBoltBaseService<SysNotice> {
	private SysNotice dao=new SysNotice().dao();
	@Inject
	private SysNoticeReaderService sysNoticeReaderService;
	@Override
	protected SysNotice dao() {
		return dao;
	}
		
    /**
	 * 后台管理分页查询true 
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param delFlag 
	 * @param sortType 
	 * @param sortColumn 
	 * @return
	 */
	public Page<SysNotice> paginateAdminDatas(int pageNumber, int pageSize, String keywords, Boolean delFlag, String sortColumn, String sortType) {
		Page<SysNotice> page = paginateByKeywords(getTableSelectColumnStrWithout("content"),sortColumn,sortType, pageNumber, pageSize, keywords, "title,content",Okv.by("del_flag", delFlag==null?false:delFlag));
		if(page!=null&&page.getTotalRow()>0) {
			page.getList().forEach(notice->processReceiverValues(notice));
		}
		return page;
	}
	public void processReceiverValues(SysNotice notice) {
		notice.put("receiverValues",processReceiverValues(notice.getReceiverType(),notice.getReceiverValue()));
	}
    private String processReceiverValues(Integer receiverType, String receiverValue) {
	  if(notOk(receiverType)) {return "全部";}
	  if(receiverType.intValue()==1) {return "全部";}
	  if(notOk(receiverValue)) {return "未设置";}
	  Long[] values = JBoltArrayUtil.toDisLong(receiverValue, ",");
	  if(values==null||values.length==0) {return "未设置";}
	  List<String> names = new ArrayList<String>();
	  switch (receiverType.intValue()) {
		  case 2://角色
			  for(Long id:values) {
				  names.add(JBoltRoleCache.me.getName(id));
			  }
			  break;
		  case 3://部门
			  for(Long id:values) {
				  names.add(JBoltDeptCache.me.getSnAndName(id));
			  }
			  break;
		  case 4://岗位
			  for(Long id:values) {
				  names.add(JBoltPostCache.me.getName(id));
			  }
			  break;
	  	  case 5://用户
	  		 for(Long id:values) {
				  names.add(JBoltUserCache.me.getName(id));
			  }
	  	      break;
	   }
	  if(names.size()==0) {
		  return "未设置";
	  }
	return JBoltArrayUtil.join(names, ",");
}

    /**
	 * 保存
	 * @param sysNotice
	 * @return
	 */
	public Ret save(SysNotice sysNotice) {
		if(sysNotice==null || isOk(sysNotice.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		sysNotice.setCreateUserId(JBoltUserKit.getUserId());
		sysNotice.setUpdateUserId(JBoltUserKit.getUserId());
		sysNotice.setDelFlag(false);
		boolean success=sysNotice.save();
		if(success) {
			EventKit.post(sysNotice);
			addSaveSystemLog(sysNotice.getId(), JBoltUserKit.getUserId(), sysNotice.getTitle());
		}
		return ret(success);
	}
	
   /**
	 * 保存
	 * @param sysNotice
	 * @return
	 */
	public Ret update(SysNotice sysNotice) {
		if(sysNotice==null || notOk(sysNotice.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//更新时需要判断数据存在
		SysNotice dbSysNotice=findById(sysNotice.getId());
		if(dbSysNotice==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
		sysNotice.setUpdateUserId(JBoltUserKit.getUserId());
		boolean success=sysNotice.update();
		if(success) {
			addUpdateSystemLog(sysNotice.getId(), JBoltUserKit.getUserId(), sysNotice.getTitle());
		}
		return ret(success);
	}
	
   /**
	  * 更新 指定多个ID 假删除
	 * @param ids
	 * @param delFlag
	 * @return
	 */
	public Ret updateDelFlagByBatchIds(String ids,boolean delFlag) {
		if(notOk(ids)) {return fail(JBoltMsg.PARAM_ERROR);}
		update(updateSql().set("del_flag", delFlag).in("id", ids));
		return SUCCESS;
	}
	
	/**
	 * 检查是否有权访问
	 * @param user
	 * @param sysNotice
	 * @return
	 */
	public Ret checkUserHasAuth(User user, SysNotice sysNotice) {
		Integer receiverType = sysNotice.getReceiverType();
		if(notOk(receiverType)) {return fail("通知异常，未设置接收类型");}
		int receiverTypeValue= receiverType.intValue();
		if(receiverTypeValue==1) {return SUCCESS;}
		String receiverValue = sysNotice.getReceiverValue();
		if(notOk(receiverValue)) {return fail("通知异常，未设置接收人");}
		List<String> values = JBoltArrayUtil.listFrom(receiverValue, ",");
		if(notOk(values)) {return fail("通知异常，未设置接收人");}
		boolean has = false;
		switch (receiverTypeValue) {
			  case 1://全部
				  has = true;
				  break;
			  case 2://角色
				  if(notOk(user.getRoles())) {
					  return fail("当前用户未设置角色");
				  }
				  has = checkIndexOf(values,user.getRoles());
				  break;
			  case 3://部门
				  if(notOk(user.getDeptId())) {
					  return fail("当前用户未设置所属部门");
				  }
				  has = values.contains(user.getDeptId().toString());
				  break;
			  case 4://岗位
				  if(notOk(user.getPosts())) {
					  return fail("当前用户未设置所属岗位");
				  }
				  has = checkIndexOf(values,user.getPosts());
				  break;
		  	  case 5://用户
		  		  has = values.contains(user.getId().toString());
		  	      break;
	   }
		return has?SUCCESS:fail(JBoltMsg.NOPERMISSION);
	}
	/**
	 * 检测用户字符串里是否包含list中的任一数据
	 * @param values
	 * @param checkValues
	 * @return
	 */
	private boolean checkIndexOf(List<String> values, String checkValues) {
		if(notOk(checkValues)) {return false;}
		List<String> checkValueList = JBoltArrayUtil.listFrom(checkValues, ",");
		if(notOk(checkValueList)) {return false;}
		checkValues = "," + checkValues + ",";
		for(String v:values) {
			if(checkValues.contains(","+v+",")) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * 批量删除
	 * @param ids
	 * @param realDelete
	 * @return
	 */
	public Ret deleteNotices(String ids,Boolean realDelete) {
		if(notOk(ids)||realDelete==null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		if(realDelete) {
			return deleteByIds(ids);
		}
		return updateDelFlagByBatchIds(ids, true);
	}
	
	@Override
	protected String afterRealDelete(SysNotice sysNotice, Kv kv) {
		sysNoticeReaderService.deleteBySysNoticeId(sysNotice.getId());
		return null;
	}
	
	/**
	 * 还原数据
	 * @param ids
	 * @return
	 */
	public Ret restoreNotices(String ids) {
		return updateDelFlagByBatchIds(ids, false);
	}
	/**
	 * 获取消息中心前十条数据
	 * @return
	 */
	public List<SysNotice> getMsgCenterPortalDatas() {
		Page<SysNotice> pageData = paginateUserSysNotices(1, 10, null, JBoltUserKit.getUserId(), ID, "desc", false,false,false,true,"id","title","create_time","update_time","type","priority_level");
		return pageData.getList();
	}
	/**
	 * 标记已读
	 * @param id
	 * @return
	 */
	public Ret markAsRead(Long id) {
		if(notOk(id)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		SysNotice sysNotice = findById(id); 
		if(sysNotice == null){
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		Ret ret=checkUserHasAuth(JBoltUserKit.getUser(),sysNotice);
		if(ret.isFail()) {
			return fail(ret.getStr("msg"));
		}
		
		ret = sysNoticeReaderService.addReader(id);
		if(ret.isFail()) {
			return ret;
		}
		//更新阅读数量
		updateSysNoticeReadCount(id);
		return SUCCESS;
	}
	/**
	 * 更新通知的阅读数
	 * @param id
	 */
	public void updateSysNoticeReadCount(Long id) {
		update(updateSql().set("read_count", new SqlExpress("read_count+1")).eq("id", id));
	}
	
	/**
	 * 标记全部已读
	 * @return
	 */
	public Ret markAllAsRead() {
		List<Long> ids = getAllUnReadIds(JBoltUserKit.getUserId());
		if(notOk(ids)) {return SUCCESS;}
		return markMultiAsRead(ids);
	}
	/**
	 * 获取指定用户未读IDS
	 * @param userId
	 * @return
	 */
	public List<Long> getAllUnReadIds(Long userId) {
		Sql sql = selectSql().selectId();
		Sql notInSql = sysNoticeReaderService.selectSql().select("sys_notice_id").eq("user_id", userId);
		sql.notInSql("id", notInSql);
		return query(sql);
	}
	/**
	 * 获取指定用户未读IDS
	 * @param userId
	 * @return
	 */
	public boolean existUnread(Object userId) {
		if(notOk(userId)) {return false;}
		User user = JBoltUserCache.me.get(Long.parseLong(userId.toString()));
		if(user == null){
			return false;
		}
		Sql sql = getUserUnReadNoticeSql(user,null, "id", "asc", false, false,
				false, true, ID);
		return exists(sql);
	}
	/**
	 * 分页获取指定用户可读数据
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param userId
	 * @param sortColumn
	 * @param sortType
	 * @param readed
	 * @param delFlag
	 * @param needProcessReadedState
	 * @param columnWithOrWithout
	 * @param columns
	 * @return
	 */
	public Page<SysNotice> paginateUserSysNotices(int pageNumber,int pageSize,String keywords,Long userId,String sortColumn, String sortType,Boolean readed,boolean delFlag,boolean needProcessReadedState,Boolean columnWithOrWithout,String... columns) {
		User user = JBoltUserCache.me.get(userId);
		if(user == null){
			return emptyPage(pageSize);
		}
		Sql sql = getUserUnReadNoticeSql(user,keywords, sortColumn, sortType, readed, delFlag,
				needProcessReadedState, columnWithOrWithout, columns).page(pageNumber, pageSize);
		return paginate(sql);
	}

	private Sql getUserUnReadNoticeSql(User user,String keywords,String sortColumn, String sortType,
			Boolean readed, boolean delFlag, boolean needProcessReadedState, Boolean columnWithOrWithout,
			String... columns) {
		Sql sql = selectSql();
		String mainPre="";
		String joinPre="";
		if(needProcessReadedState) {
			mainPre="sn.";
			joinPre="sr.";
		}
		if(isOk(keywords)) {
			sql.likeMulti(keywords, mainPre+"title",mainPre+"content");
		}
		if(isOk(sortColumn)) {
			if(isOk(sortType)){
				sql.orderBy(mainPre+sortColumn, sortType);
			}else {
				sql.asc(mainPre+sortColumn);
			}
		}
		
		sql.eq(mainPre+DEL_FLAG, delFlag);
		
		Sql subSql = sysNoticeReaderService.selectSql().select("sys_notice_id").eq("user_id", user.getId());
		if(readed!=null) {
			if(readed) {
				sql.inSql(mainPre+ID, subSql);
			}else {
				sql.notInSql(mainPre+ID, subSql);
			}
		}
		
		if(needProcessReadedState) {
			sql.leftJoin(sysNoticeReaderService.table(), "sr", "sr.sys_notice_id = sn.id");
			sql.from(table(), SN);
			
			if(columnWithOrWithout!=null && isOk(columns)) {
				if(columnWithOrWithout) {
					List<String> newCols=new ArrayList<String>();
					for(String s:columns) {
						newCols.add(mainPre="."+s);
					}
					newCols.add("sr.sys_notice_id as readed");
					columns = newCols.toArray(new String[columns.length]);
					sql.select(columns);
				}else {
					if(!ArrayUtil.contains(columns,"id")){
						columns = ArrayUtil.append(columns,"id");
					}
					sql.distinct(mainPre+"id,"+getTableSelectColumnStrWithoutWithPre(mainPre,columns)+",sr.sys_notice_id as readed ");
				}
			}
		}else {
			if(columnWithOrWithout!=null) {
				if(columnWithOrWithout) {
					sql.select(columns);
				}else {
					sql.select(getTableSelectColumnsWithout(columns));
				}
			}
		}
		sql.bracketLeft();
		//1、全部
		sql.eq(mainPre+"receiver_type", 1);
		
		//2、角色
		String roleIds = user.getRoles();
		if(isOk(roleIds)) {
			String[] roleIdArray = JBoltArrayUtil.from(roleIds, ",");
			if(isOk(roleIdArray)) {
				sql.or();

				sql.bracketLeft();
				
				sql.eq(mainPre+"receiver_type", 2);
				int i=-1;
				int size = roleIdArray.length;
				for(String roleId:roleIdArray) {
					i++;
					sql.bracketLeft();
					sql.findInSet(roleId, mainPre+"receiver_value", true);
					sql.bracketRight();
					if(i<size-1) {
						sql.or();
					}
				}
				
				sql.bracketRight();
			}
		}
		
		//3、部门
		Long deptId = user.getDeptId();
		if(isOk(deptId)) {
			sql.or();
			sql.bracketLeft();
			sql.eq(mainPre+"receiver_type", 3);
			sql.findInSet(deptId, mainPre+"receiver_value", true);
			sql.bracketRight();
		}
		//4、岗位
		String postIds = user.getPosts();
		if(isOk(postIds)) {
			String[] postIdArray = JBoltArrayUtil.from(postIds, ",");
			if(isOk(postIdArray)) {
				sql.or();
				
				sql.bracketLeft();
				
				sql.eq(mainPre+"receiver_type", 4);
				int i=-1;
				int size = postIdArray.length;
				for(String postId:postIdArray) {
					i++;
					sql.bracketLeft();
					sql.findInSet(postId, mainPre+"receiver_value", true);
					sql.bracketRight();
					if(i<size-1) {
						sql.or();
					}
				}
				
				sql.bracketRight();
			}
		}
		
		//5、用户
		sql.or();
		sql.bracketLeft();
		sql.eq(mainPre+"receiver_type", 5);
		sql.findInSet(user.getId(), mainPre+"receiver_value", true);
		sql.bracketRight();
		
		sql.bracketRight();
		return sql;
	}
	/**
	 * 批量处理已读
	 * @param idsStr
	 * @return
	 */
	public Ret markMultiAsRead(String idsStr) {
		if(notOk(idsStr)) {return fail(JBoltMsg.PARAM_ERROR);}
		Long[] ids = JBoltArrayUtil.toDisLong(idsStr, ",");
		if(notOk(ids)) {return fail(JBoltMsg.PARAM_ERROR);}
		return markMultiAsRead(ids);
	}
	/**
	 * 批量处理已读
	 * @param ids
	 * @return
	 */
	public Ret markMultiAsRead(Long[] ids) {
		return markMultiAsRead(Arrays.stream(ids).collect(Collectors.toList()));
	}

	/**
	 * 批量处理已读
	 * @param ids
	 * @return
	 */
	public Ret markMultiAsRead(List<Long> ids) {
		if(!isOk(ids)) {return fail(JBoltMsg.PARAM_ERROR);}
		ids.forEach(id->{
			sysNoticeReaderService.addReader(id);
			updateSysNoticeReadCount(id);
		});	
		return SUCCESS;
	}

	@Override
	protected int systemLogTargetType() {
		return JBoltProSystemLogTargetType.SYSTEM_NOTICE.getValue();
	}
}