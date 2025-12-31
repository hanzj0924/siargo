package cn.jbolt._admin.msgcenter;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.common.model.Todo;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.bean.JBoltDateRange;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.util.JBoltArrayUtil;
import net.dreamlu.event.EventKit;

/**
 * 待办事项管理 Service
 * 
 * @ClassName: TodoService
 * @author: JBolt-Generator
 * @date: 2021-04-17 17:43
 */
public class TodoService extends JBoltBaseService<Todo> {
	private Todo dao = new Todo().dao();

	@Override
	protected Todo dao() {
		return dao;
	}

	/**
	 * 保存
	 * 
	 * @param todo
	 * @return
	 */
	public Ret save(Todo todo) {
		if (todo == null || isOk(todo.getId()) || notOk(todo.getState()) || notOk(todo.getType())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		int type=todo.getType().intValue();
		switch (type) {
		case 1://无url 无content 只有title
			todo.remove("url","content");
			break;
		case 2://无url 有content 有title
			if(notOk(todo.getContent())) {
				return fail(JBoltMsg.PARAM_ERROR+" 如选择【无链接有内容】类型，需要填充内容");
			}
			todo.remove("url");
			break;
		case 3://有url 无content 有title
			if(notOk(todo.getUrl())) {
				return fail(JBoltMsg.PARAM_ERROR+" 如选择【有链接无内容】类型，需要设置链接");
			}
			todo.remove("content");
			break;
		case 4:
			if(notOk(todo.getUrl())) {
				return fail(JBoltMsg.PARAM_ERROR+" 如选择【有链接有内容】类型，需要设置链接");
			}
			if(notOk(todo.getContent())) {
				return fail(JBoltMsg.PARAM_ERROR+" 如选择【有链接有内容】类型，需要填充内容");
			}
			break;
		}
		Long userId = JBoltUserKit.getUserId();
		todo.setCreateUserId(userId);
		todo.setUpdateUserId(userId);
		todo.setUserId(userId);
		todo.setIsReaded(false);
		boolean success = todo.save();
		if(success) {
			EventKit.post(todo);
		}
		return ret(success);
	}

	/**
	 * 更新
	 * 
	 * @param todo
	 * @return
	 */
	public Ret update(Todo todo) {
		if (todo == null || notOk(todo.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 更新时需要判断数据存在
		Todo dbTodo = findById(todo.getId());
		if (dbTodo == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		boolean success = todo.update();
		return ret(success);
	}
	/**
	 * 标记为已读
	 * @param id
	 * @return
	 */
	public Ret markAsRead(Long id) {
		Todo todo = findById(id);
		if (todo == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		Long userId = JBoltUserKit.getUserId();
		if(userId.longValue() != todo.getUserId()) {
			return fail(JBoltMsg.NOPERMISSION+",只能修改属于自己的待办事项");
		}
		todo.setIsReaded(true);
		todo.setUpdateUserId(userId);
		todo.setUpdateTime(new Date());
		boolean success = todo.update();
		return ret(success);
	}
	/**
	 * 更新状态
	 * @param todoId
	 * @param state
	 * @return
	 */
	public Ret updateState(Long todoId, Integer state) {
		if (notOk(todoId) || notOk(state)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 更新时需要判断数据存在
		Todo todo = findById(todoId);
		if (todo == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		Long userId = JBoltUserKit.getUserId();
		if(userId.intValue() != todo.getUserId()) {
			return fail(JBoltMsg.NOPERMISSION+",只能修改属于自己的待办事项");
		}
		todo.setState(state);
		todo.setUpdateUserId(userId);
		boolean success = todo.update();
		return ret(success);
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public Ret delete(Long id) {
		return deleteById(id, false);
	}

	/**
	 * 读取判断是否存在需要用户处理的数据
	 * @param userId
	 * @return
	 */
	public boolean existNeedProcess(Object userId) {
		Sql sql = selectSql().eq("user_id", userId).bracketLeft().eq("state", 1).or().eq("state", 2).bracketRight();
		return exists(sql);
	}
	
	/**
	 * 获取待办 在 右上角消息中心layer中的未完成待办项
	 * @return
	 */
	public List<Todo> getMsgCenterPortalDatas() {
		Page<Todo> pageData = paginateUserTodos(1, 10, JBoltUserKit.getUserId(), "id", "desc",true,new Integer[] {1,2},true,"id","title","priority_level","state","specified_finish_time","create_time","update_time");
		return pageData.getList();
	}
	/**
	 * 查询用户待办数据
	 * @param pageNumber
	 * @param pageSize
	 * @param userId
	 * @param sortColumn
	 * @param sortType
	 * @param inState
	 * @param states
	 * @param columnWithOrWithout
	 * @param columns
	 * @return
	 */
	public Page<Todo> paginateUserTodos(int pageNumber, int pageSize, Long userId, String sortColumn, String sortType,Boolean inState,Integer[] states,Boolean columnWithOrWithout,String... columns) {
		Sql sql = selectSql().page(pageNumber, pageSize);
		sql.eq("user_id", userId);
		sql.orderBy(sortColumn, sortType);
		if(inState!=null && isOk(states)) {
			if(inState) {
				sql.in("state", states);
			}else {
				sql.notIn("state", states);
			}
		}
		if(columnWithOrWithout!=null && isOk(columns)) {
			if(columnWithOrWithout) {
				sql.select(columns);
			}else {
				sql.select(getTableSelectColumnsWithout(columns));
			}
		}
		return paginate(sql);
	}
	/**
	 *  底层查询方法 分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param userId
	 * @param sortColumn
	 * @param sortType
	 * @param type
	 * @param state
	 * @param dateTimeColumn
	 * @param dateRange
	 * @param columnWithOrWithout
	 * @param columns
	 * @return
	 */
	public Page<Todo> paginateUserTodos(Integer pageNumber, Integer pageSize, String keywords, Long userId,
			String sortColumn, String sortType, Integer type, Integer state, String dateTimeColumn,JBoltDateRange dateRange,Boolean columnWithOrWithout, String... columns) {
		if(isOk(dateTimeColumn) && !dao().hasColumn(dateTimeColumn)) {
			throw new RuntimeException("数据中没有这一列:"+dateTimeColumn);
		}
		Sql sql = selectSql().page(pageNumber, pageSize);
		if(isOk(keywords)) {
			sql.likeMulti(keywords, "title","content");
		}
		if(isOk(type)) {
			sql.eq("type", type);
		}
		if(isOk(state)) {
			sql.eq("state", state);
		}
		if(isOk(sortColumn)) {
			if(isOk(sortType)){
				sql.orderBy(sortColumn, sortType);
			}else {
				sql.asc(sortColumn);
			}
		}
		if(columnWithOrWithout != null) {
			if(columnWithOrWithout) {
				sql.select(columns);
			}else {
				sql.select(getTableSelectColumnsWithout(columns));
			}
		}
		
		if(isOk(dateTimeColumn) && dateRange!=null) {
			Date startDate = dateRange.getStartDate();
			if(isOk(startDate)) {
				sql.ge(dateTimeColumn, toStartTime(startDate));
			}
			Date endDate = dateRange.getEndDate();
			if(isOk(endDate)) {
				sql.le(dateTimeColumn, toEndTime(endDate));
			}
		}
		return paginate(sql);
	}
	
	/**
	 * 批量删除 指定用户的todos
	 * @param ids
	 * @return
	 */
	public Ret deleteMyTodoByBatchIds(String ids) {
		if(notOk(ids)) {return fail(JBoltMsg.PARAM_ERROR);}
		Long userId = JBoltUserKit.getUserId();
		if(notOk(userId)) {
			return fail(JBoltMsg.NOPERMISSION);
		}
		delete(deleteSql().eq("user_id", userId).in("id", ids));
		return SUCCESS;
	}
	
	/**
	 * 批量修改状态
	 * @param state
	 * @param ids
	 * @return
	 */
	public Ret batchUpdateState(Integer state,String ids) {
		if(notOk(state) || notOk(ids)) {return fail(JBoltMsg.PARAM_ERROR);}
		Long[] idArray = JBoltArrayUtil.toDisLong(ids, ",");
		if(notOk(idArray)) {return fail(JBoltMsg.PARAM_ERROR);}
		boolean success = tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				Long userId = JBoltUserKit.getUserId();
				List<Todo> todos = find(selectSql().eq("user_id", userId).in("id", idArray));
				if(todos.size()==0) {
					return true;
				}
				for(Todo todo:todos) {
					todo.setState(state);
					todo.setUpdateUserId(userId);
				}
				try {
					batchUpdate(todos);
					return true;
				} catch (Exception e) {
					LOG.error("批量更新todo状态失败:"+e.getMessage());
					throw new RuntimeException("批量更新todo状态失败");
				}
			}
		});
		return ret(success);
	}
	/**
	 * 是否存在未读
	 * @param userId
	 * @return
	 */
	public boolean existUnread(Object userId) {
		return exists(selectSql().selectId().eq("user_id", userId).eq("is_readed", FALSE));
	}

	@Override
	protected int systemLogTargetType() {
		// TODO Auto-generated method stub
		return 0;
	}
}