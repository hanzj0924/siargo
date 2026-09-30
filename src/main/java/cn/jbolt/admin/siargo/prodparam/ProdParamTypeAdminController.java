package cn.jbolt.admin.siargo.prodparam;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.kit.Ret;
import java.util.function.Supplier;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.ProdParamType;
/**
 * 型号参数 Controller
 * @ClassName: ProdParamTypeAdminController
 * @author: hanzj
 * @date: 2026-09-02 16:26
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/prodparam", viewPath = "/_view/admin/siargo/prodparam")
//true
public class ProdParamTypeAdminController extends JBoltBaseController {

	@Inject
	private ProdParamTypeService service;
	
   /**
	* 首页
	*/
	public void index() {
		render("index.html");
	}
  	
   /**
	* 左侧卡片数据源（portal 片段，全量渲染无分页）
	*/
	public void mgr() {
		set("prodParamTypes",service.getListAll());
		render("mgrportal.html");
	}

   /**
	* 启用种类 options（输入器 □ 选种类数据源）
	*/
	public void options() {
		renderJsonData(service.options());
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
		ProdParamType prodParamType=service.findActiveById(getLong(0));
		if(prodParamType == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("prodParamType",prodParamType);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(ProdParamType.class, "prodParamType")));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(ProdParamType.class, "prodParamType")));
	}

   /**
	* 批量删除
	*/
	public void deleteByIds() {
		renderJsonInTx(() -> service.deleteByBatchIds(get("ids")));
	}
	
  /**
	* 排序 上移
	*/
	public void up() {
		renderJsonInTx(() -> service.up(getLong(0)));
	}
	
  /**
	* 排序 下移
	*/
	public void down() {
		renderJsonInTx(() -> service.down(getLong(0)));
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
	public void initRank() {
		renderJsonInTx(service::initRank);
	}

	/** 多步写的软失败必须使整个事务回滚。 */
	private void renderJsonInTx(Supplier<Ret> action) {
		final Ret[] result={null};
		boolean committed=Db.tx(() -> {
			result[0]=action.get();
			return result[0]!=null&&result[0].isOk();
		});
		if(!committed&&(result[0]==null||result[0].isOk())) {
			renderJsonFail("操作失败，请刷新后重试");
			return;
		}
		renderJson(result[0]);
	}
}
