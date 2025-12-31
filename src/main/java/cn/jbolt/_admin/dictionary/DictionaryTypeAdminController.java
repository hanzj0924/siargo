package cn.jbolt._admin.dictionary;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Okv;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltPageSize;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.DictionaryType;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;

/**
 * 数据字典类型管理Controller
 * @ClassName:  DictionaryTypeAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 */
@CheckPermission({PermissionKey.DICTIONARY})
@UnCheckIfSystemAdmin
@OnlySaasPlatform
public class DictionaryTypeAdminController extends JBoltBaseController {
	@Inject
	private DictionaryTypeService service;
	/**
	 * 加载管理portal
	 */
//	public void mgr(){
//		set("dictionaryTypes",service.findAll());
//		render("mgrportal.html");
//	}
	/**
	 * 管理页面分页读取数据源
	 */
	public void datas(){
		renderJsonData(service.paginateByKeywords("id","desc", getPageNumber(), getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST_15), getKeywords(), "name,type_key", Okv.by("enable",getBoolean("enable",true))));
	}
	
	/**
	 * 低代码模块专用
	 */
	public void codeGenOptions(){
		renderJsonData(service.getOptionList("name","type_key",Okv.by("enable",TRUE)));
	}
	
	public void add(){
		render("add.html");
	}
	public void edit(){
		Long id=getLong(0);
		if(notOk(id)){
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		DictionaryType type=service.findById(id);
		if(type==null){
			renderFormFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		if(type.getIsBuildIn()){
			renderFormFail("内置字典类型，不可修改");
			return;
		}
		set("dictionaryType",type);
		render("edit.html");
	}
	public void save(){
		renderJson(service.save(getModel(DictionaryType.class, "dictionaryType")));
	}
	@Before(Tx.class)
	public void update(){
		renderJson(service.update(getModel(DictionaryType.class, "dictionaryType")));
	}
	@Before(Tx.class)
	public void delete(){
		renderJson(service.delete(getLong(0)));
	}
	@Before(Tx.class)
	public void toggleEnable(){
		renderJson(service.toggleBoolean(getLong(0),DictionaryType.ENABLE));
	}
}
