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

	/** 文件类别服务 */
	@Inject
	private DmsCategoryService service;
	
	/**
	 * 进入文件类别管理首页
	 * URL: GET /admin/siargo/dms/category
	 */
	public void index() {
		render("index.html");
	}
	
	/**
	 * 获取文件类别分页数据
	 * URL: GET /admin/siargo/dms/category/datas
	 * @return 分页数据JSON
	 */
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords()));
	}
	
	/**
	 * 进入新增文件类别页面
	 * URL: GET /admin/siargo/dms/category/add
	 */
	public void add() {
		render("add.html");
	}
	
	/**
	 * 进入编辑文件类别页面
	 * URL: GET /admin/siargo/dms/category/edit/{id}
	 * @param id 类别ID（从URL路径获取）
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
	 * 保存新增的文件类别
	 * URL: POST /admin/siargo/dms/category/save
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(DmsCategory.class, "dmsCategory")));
	}
	
	/**
	 * 更新文件类别信息
	 * URL: POST /admin/siargo/dms/category/update
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(DmsCategory.class, "dmsCategory")));
	}
	
	/**
	 * 批量删除文件类别
	 * URL: POST /admin/siargo/dms/category/deleteByIds
	 * @param ids 类别ID列表（逗号分隔）
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}
	
	/**
	 * 类别排序上移
	 * URL: POST /admin/siargo/dms/category/up/{id}
	 * @param id 类别ID（从URL路径获取）
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void up() {
		renderJson(service.up(getLong(0)));
	}
	
	/**
	 * 类别排序下移
	 * URL: POST /admin/siargo/dms/category/down/{id}
	 * @param id 类别ID（从URL路径获取）
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void down() {
		renderJson(service.down(getLong(0)));
	}
	
	/**
	 * 灵活移动类别排序位置
	 * URL: POST /admin/siargo/dms/category/move
	 * @param id 要移动的类别ID
	 * @param otherId 目标位置的类别ID
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void move() {
		renderJson(service.move(getLong("id"),getLong("otherId")));
	}
	
	/**
	 * 初始化所有类别的排序序号
	 * URL: POST /admin/siargo/dms/category/initRank
	 * @return 操作结果JSON
	 */
    @Before(Tx.class)
	public void initRank() {
		renderJson(service.initRank());
	}
	
	/**
	 * 获取所有类别列表，附带每个类别下的文件数量
	 * URL: GET /admin/siargo/dms/category/getCategoryListWithCount
	 * @return 类别列表JSON，每项包含 id, name, sortRank, fileCount
	 */
	public void getCategoryListWithCount() {
		renderJsonData(service.getCategoryListWithCount());
	}
	
	
}
