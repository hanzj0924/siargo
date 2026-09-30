package cn.jbolt.admin.siargo.prodparam.value;

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
import cn.jbolt.admin.siargo.prodparam.ProdParamTypeService;
import cn.jbolt.siargo.model.ProdParamType;
import cn.jbolt.siargo.model.ProdParamValue;
/**
 * 型号参数值 Controller（右侧卡片：某参数种类下的可选值维护）
 * @ClassName: ProdParamValueAdminController
 * @author: hanzj
 * @date: 2026-09-03
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/prodparam/value", viewPath = "/_view/admin/siargo/prodparam/value")
public class ProdParamValueAdminController extends JBoltBaseController {

	@Inject
	private ProdParamValueService service;
	@Inject
	private ProdParamTypeService typeService;

	/**
	 * 右侧卡片数据源（portal 片段，全量渲染无分页）
	 */
	public void mgr() {
		Long typeId=getLong(0);
		if(notOk(typeId)) {
			renderAjaxPortalFail("请先在左侧选择参数种类");
			return;
		}
		ProdParamType type=typeService.findActiveById(typeId);
		if(type==null) {
			renderAjaxPortalFail("参数种类不存在，可能已被删除");
			return;
		}
		set("paramType",type);
		set("paramValues",service.getListByType(typeId));
		render("mgrportal.html");
	}

	/**
	 * 新增（URL 带参数种类id）
	 */
	public void add() {
		Long typeId=getLong(0);
		if(notOk(typeId)) {
			renderFormFail("缺少参数种类");
			return;
		}
		ProdParamType type=typeService.findActiveById(typeId);
		if(type==null) {
			renderFormFail("参数种类不存在，可能已被删除");
			return;
		}
		ProdParamValue prodParamValue=new ProdParamValue();
		prodParamValue.setParamTypeId(typeId);
		set("prodParamValue",prodParamValue);
		set("paramTypeName",type.getTypeName());
		render("add.html");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		ProdParamValue prodParamValue=service.findActiveById(getLong(0));
		if(prodParamValue == null){
			renderFormFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		ProdParamType type=typeService.findActiveById(prodParamValue.getParamTypeId());
		if(type==null) {
			renderFormFail("参数种类不存在或已删除");
			return;
		}
		set("prodParamValue",prodParamValue);
		set("paramTypeName",type.getTypeName());
		render("edit.html");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(ProdParamValue.class, "prodParamValue")));
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(ProdParamValue.class, "prodParamValue")));
	}

	/**
	 * 批量物理删除；型号结构、值关联和参数值必须在同一事务内全部成功。
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
	 * 排序 初始化（某种类内）
	 */
	public void initRank() {
		renderJsonInTx(() -> service.initRankByType(getLong(0)));
	}

	/**
	 * 某种类下启用值的 options（输入器 □ 值多选数据源）
	 */
	public void options() {
		renderJsonData(service.optionsByType(getLong(0)));
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
