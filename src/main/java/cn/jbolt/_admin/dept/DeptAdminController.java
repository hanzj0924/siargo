package cn.jbolt._admin.dept;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.cache.JBoltGlobalConfigCache;
import cn.jbolt.core.common.enums.JBoltDeptMgrType;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.Dept;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 *
 * @ClassName: DeptAdminController
 * @author: JFinal学院-小木
 * @date: 2021-02-07 20:34
 */
@CheckPermission(PermissionKey.DEPT)
@UnCheckIfSystemAdmin
public class DeptAdminController extends JBoltBaseController {

	@Inject
	private DeptService service;

  /**
	* 首页
	*/
	public void index() {
		String html = "index.html";
		JBoltDeptMgrType type = JBoltGlobalConfigCache.me.getSystemDeptMgrType();
		if(type == JBoltDeptMgrType.JS_TREE){
			html = "treemgr.html";
		}
		render(html);
	}
	/**
	 * 数据源
	 */
	public void datas() {
		renderJsonData(service.getTreeTableDatas());
	}

	/**
	 * crud树的数据源
	 */
	public void crudJsTreeDatas() {
		renderJsonData(service.getAllCrudJsTreeDatas(getLong("selectId"),getInt("openLevel",0)));
	}
	/**
	 * select数据源
	 */
	@UnCheck
	public void options() {
		renderJsonData(service.getTreeDatas(false,true));
	}
	/**
	 * select数据源 只需要enable=true的
	 */
	@UnCheck
	public void enableOptions() {
		renderJsonData(service.getTreeDatas(true,true));
	}
  /**
	* JSTree树形数据源
	*/
	@UnCheck
	public void enableJsTreeDatas() {
		renderJsonData(service.getEnableJsTree(getLong("selectId"),getInt("openLevel",0)));
	}

  /**
	* 新增
	*/
	public void add() {
		set("pid", getLong(0,0L));
		JBoltDeptMgrType type = JBoltGlobalConfigCache.me.getSystemDeptMgrType();
		set("showPortalBtn",type == JBoltDeptMgrType.JS_TREE);
		render("add.html");
	}

	/**
	 * 新增Dialog
	 */
	public void addInDialog() {
		set("pid", getLong(0,0L));
		render("add.html");
	}

  /**
	* 编辑
	*/
	public void edit() {
		JBoltDeptMgrType type = JBoltGlobalConfigCache.me.getSystemDeptMgrType();
		Dept dept=service.findById(getLong(0));
		if(dept == null){
			String msg = JBoltMsg.DATA_NOT_EXIST;
			if(type == JBoltDeptMgrType.JS_TREE){
				msg = "请选择有效部门数据后编辑";
			}
			renderDialogFail(msg);
			return;
		}
		set("dept",dept);
		set("showPortalBtn",type == JBoltDeptMgrType.JS_TREE);
		render("edit.html");
	}

  /**
	* 保存
	*/
	@Before(Tx.class)
	public void save() {
		Dept dept = getModel(Dept.class, "dept");
		Ret ret = service.save(dept);
		if(ret.isOk()){
			ret.set("data",dept.getId());
		}
		renderJson(ret);
	}

  /**
	* 更新
	*/
	@Before(Tx.class)
	public void update() {
		Dept dept = getModel(Dept.class, "dept");
		Ret ret = service.update(dept);
		if(ret.isOk()){
			ret.set("data",dept.getId());
		}
		renderJson(ret);
	}

  /**
	* 删除
	*/
	@Before(Tx.class)
	public void delete() {
		renderJson(service.delete(getLong(0)));
	}

  /**
	* 切换启用状态
	*/
	@Before(Tx.class)
	public void toggleEnable() {
		renderJson(service.toggleEnable(getLong(0)));
	}

  /**
	* 排序 上移
	*/
	@Before(Tx.class)
	public void up() {
		renderJson(service.up(getLong(0)));
	}

  /**
	* 排序 下移
	*/
	@Before(Tx.class)
	public void down() {
		renderJson(service.down(getLong(0)));
	}

  /**
	* 排序 初始化
	*/
	@Before(Tx.class)
	public void initRank() {
		renderJson(service.initRank());
	}

	/**
	 * 处理所有部门的deptPath
	 */
	@Before(Tx.class)
	public void processAllDeptPath() {
		service.processAllDeptPath();
		renderJsonSuccess();
	}

	/**
	 * jstree 节点move
	 */
	@Before(Tx.class)
	public void move(){
		renderJson(service.move(getLong("id"),getLong("pid"),getInt("rank")));
	}

}
