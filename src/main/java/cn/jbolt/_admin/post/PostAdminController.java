package cn.jbolt._admin.post;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.Post;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
/**
 * 
 * @ClassName: PostAdminController   
 * @author: JFinal学院-小木
 * @date: 2021-02-11 12:12  
 */
@CheckPermission(PermissionKey.POST)
@UnCheckIfSystemAdmin
public class PostAdminController extends JBoltBaseController {

	@Inject
	private PostService service;
	
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
	 * select数据源
	 */
	@UnCheck
	public void enableOptions() {
		renderJsonData(service.getEnableOptionsList());
	}
  /**
	* select数据源
	*/
	@UnCheck
	public void options() {
		renderJsonData(service.getOptionListOrderBy("sort_rank", "asc"));
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
		Post post=service.findById(getLong(0)); 
		if(post == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("post",post);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
	@Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(Post.class, "post")));
	}
	
  /**
	* 更新
	*/
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(Post.class, "post")));
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
	
	
}