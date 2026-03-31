package cn.jbolt.admin.siargo.dms.category;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.DmsCategory;
/**
 * 文件类别表管理 Controller
 * @ClassName: DmsCategoryAdminController
 * @author: hanzj
 * @date: 2026-03-23 13:31
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/dms/category", viewPath = "/_view/admin/siargo/dms/category")
//true
public class DmsCategoryAdminController extends JBoltBaseController {

	@Inject
	private DmsCategoryService service;
	
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
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords()));
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
		DmsCategory dmsCategory=service.findById(getLong(0)); 
		if(dmsCategory == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("dmsCategory",dmsCategory);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(DmsCategory.class, "dmsCategory")));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(DmsCategory.class, "dmsCategory")));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
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
	*  灵活移动排序
	*/
    @Before(Tx.class)
	public void move() {
		renderJson(service.move(getLong("id"),getLong("otherId")));
	}
	
  /**
	* 排序 初始化
	*/
    @Before(Tx.class)
	public void initRank() {
		renderJson(service.initRank());
	}
	
	/**
	 * 获取所有类别列表，附带每个类别下的文件数量
	 */
	public void getCategoryListWithCount() {
		renderJsonData(service.getCategoryListWithCount());
	}
	
	
}
