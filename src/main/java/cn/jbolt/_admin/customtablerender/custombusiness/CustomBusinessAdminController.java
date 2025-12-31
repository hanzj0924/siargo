package cn.jbolt._admin.customtablerender.custombusiness;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.core.paragetter.Para;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt._admin.customtablerender.common.model.CustomBusiness;
/**
 * 系统业务定义
 * @ClassName: CustomBusinessAdminController
 * @author: 总管理
 * @date: 2023-05-30 00:05
 */
//@CheckPermission(PermissionKey.ADMIN_CUSTOM_BUSINESS)
@UnCheckIfSystemAdmin
@Path(value = "/admin/customBusiness", viewPath = "/_view/_admin/customtablerender/custombusiness")
public class CustomBusinessAdminController extends JBoltBaseController {

	@Inject
	private CustomBusinessService service;
   /**
	* 首页
	*/
	public void index() {
		render("index.html");
	}
   /**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.getAdminDatas(getPageNumber(), getPageSize(), getKeywords(), getBoolean("enable")));
	}

   /**
	* 新增
	*/
	public void add() {
		render("add.html");
	}

   /**
	* 保存
	*/
	@Before(Tx.class)
	public void save(@Para("customBusiness")CustomBusiness customBusiness) {
		renderJson(service.save(customBusiness));
	}

   /**
	* 编辑
	*/
	public void edit() {
		CustomBusiness customBusiness=service.findById(getLong(0));
		if(customBusiness == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("customBusiness",customBusiness);
		render("edit.html");
	}

   /**
	* 更新
	*/
	@Before(Tx.class)
	public void update(@Para("customBusiness")CustomBusiness customBusiness) {
		renderJson(service.update(customBusiness));
	}

   /**
	* 删除
	*/
	@Before(Tx.class)
	public void delete() {
		renderJson(service.deleteById(getLong(0),true));
	}

   /**
	* 切换启用状态
	*/
	@Before(Tx.class)
	public void toggleEnable() {
		renderJson(service.toggleEnable(getLong(0)));
	}

   /**
	* autocomplete组件专用数据
	*/
	public void autocompleteDatas() {
        renderJsonData(service.getAutocompleteList(get("q"), getInt("limit",20),true,"name,remark"));

	}

   /**
	* 得到select radio checkbox专用options数据
	*/
	public void options() {
       renderJson(service.getOptionListEnable("name","id"));
	}


}
