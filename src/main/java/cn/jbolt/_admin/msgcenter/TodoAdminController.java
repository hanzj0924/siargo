package cn.jbolt._admin.msgcenter;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt.common.model.Todo;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.UnCheck;

/**
 * 待办事项管理 Controller
 * @ClassName: TodoAdminController
 * @author: JBolt-Generator
 * @date: 2021-04-17 17:43
 */
@UnCheck
public class TodoAdminController extends JBoltBaseController {

	@Inject
	private TodoService service;

	/**
	* 新增
	*/
	public void add() {
		render("add.html");
	}

   /**
	* 编辑
	*/
	public void edit() {
		Todo todo=service.findById(getLong(0));
		if(todo == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("todo",todo);
		render("edit.html");
	}

  /**
	* 保存
	*/
	@Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Todo.class, "todo")));
	}

   /**
	* 更新
	*/
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Todo.class, "todo")));
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		renderJson(service.delete(getLong(0)));
	}
	/**
	 * 批量删除
	 */
	@Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteMyTodoByBatchIds(get("ids")));
	}
	/**
	 * 改状态
	 */
	@Before(Tx.class)
	public void updateState() {
		renderJson(service.updateState(getLong(0),getInt(1)));
	}
   /**
	* 批量改状态
	*/
	@Before(Tx.class)
	public void batchUpdateState() {
		renderJson(service.batchUpdateState(getState(),get("ids")));
	}

	/**
	 * 读取Dashboard todos
	 */
	public void dashboardDatas(){
		renderJsonData(service.paginate(service.selectSql().page(getPageNumber(),getPageSize(6)).in("state",1,2,5).asc("specified_finish_time")));
	}

	/**
	 * 详情
	 */
	public void detail() {
		Long todoId = getLong(0);
		if(notOk(todoId)) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Todo todo=service.findById(todoId);
		if(todo == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		Long userId = JBoltUserKit.getUserId();
		if(todo.getUserId().longValue() != userId.longValue()) {
			renderDialogFail(JBoltMsg.NOPERMISSION);
		}
		set("todo",todo);
		render("detail.html");
	}

}
