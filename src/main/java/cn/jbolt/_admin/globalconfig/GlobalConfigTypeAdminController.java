package cn.jbolt._admin.globalconfig;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.GlobalConfigType;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
/**
 * 全局参数类型
 * @ClassName:  GlobalConfigTypeAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年1月15日   
 */

@CheckPermission(PermissionKey.GLOBALCONFIG)
@UnCheckIfSystemAdmin
public class GlobalConfigTypeAdminController extends JBoltBaseController {
	
	@Inject
	private GlobalConfigTypeService service;
	public void options() {
		renderJsonData(service.getOptionList());
	}
	public void datas() {
		renderJsonData(service.getAdminList());
	}
	
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
		GlobalConfigType type=service.findById(getLong(0));
		if(type==null) {
			renderFormFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		if(type.getBuiltIn()) {
			renderFormFail("系统内置类型不可编辑");
			return;
		}
		set("globalConfigType", type);
		render("edit.html");
	}
	
	/**
	 *保存
	 */
	@Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(GlobalConfigType.class,"globalConfigType")));
	}
	
	/**
	 *保存
	 */
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(GlobalConfigType.class,"globalConfigType")));
	}
	
	/**
	 *删除
	 */
	@Before(Tx.class)
	public void delete() {
		renderJson(service.delete(getLong(0)));
	}
	
	/**
	* 排序 上移
	*/
	@Before(Tx.class)
	public void up() {
		renderJson(service.doUp(getLong(0)));
	}
	
	/**
	 * 排序 下移
	 */
	@Before(Tx.class)
	public void down() {
		renderJson(service.doDown(getLong(0)));
	}
	
  /**
	* 排序初始化
	*/
	@Before(Tx.class)
	public void initRank() {
		renderJson(service.initRank());
	}
	
	
}
