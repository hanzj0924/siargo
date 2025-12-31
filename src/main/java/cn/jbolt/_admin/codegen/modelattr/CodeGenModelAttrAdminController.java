package cn.jbolt._admin.codegen.modelattr;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.model.CodeGenModelAttr;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.para.JBoltPara;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.paragetter.Para;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.activerecord.tx.TxConfig;
import com.jfinal.plugin.activerecord.tx.TxReadCommitted;

/**
 * JBolt 开发平台 - 可视化代码生成器 codeGenModel管理
 * @ClassName:  CodeGenModelAdminController
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年11月24日
 */
@CheckPermission(PermissionKey.JBOLT_CODE_GEN)
@UnCheckIfSystemAdmin
public class CodeGenModelAttrAdminController extends JBoltBaseController {
	@Inject
	private CodeGenModelAttrService service;

	/**
	 * 每个表的属性数据源
	 */
	public void datas() {
		renderJsonData(service.getCodeGenModelAttrs(getLong(0),"sort_rank",false));
	}

	/**
	 * isPkey切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsPkey() {
		renderJson(service.toggleAttrBoolean("is_pkey",getLong(0),getLong(1)));
	}
	/**
	 * isNeedCheckExists切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsNeedCheckExists() {
		renderJson(service.toggleAttrBoolean("is_need_check_exists",getLong(0),getLong(1)));
	}

	/**
	 * is_table_col切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsTableCol() {
		renderJson(service.toggleAttrBoolean("is_table_col",getLong(0),getLong(1)));
	}

	/**
	 * is_import_col切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsImportCol() {
		renderJson(service.toggleAttrBoolean("is_import_col",getLong(0),getLong(1)));
	}

	/**
	 * is_export_col切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsExportCol() {
		renderJson(service.toggleAttrBoolean("is_export_col",getLong(0),getLong(1)));
	}

	/**
	 * is_need_translate切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsNeedTranslate() {
		renderJson(service.toggleAttrBoolean("is_need_translate",getLong(0),getLong(1)));
	}

	/**
	 * is_sortable切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsSortable() {
		renderJson(service.toggleAttrBoolean("is_sortable",getLong(0),getLong(1)));
	}

	/**
	 * is_search_ele切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsSearchEle() {
		renderJson(service.toggleAttrBoolean("is_search_ele",getLong(0),getLong(1)));
	}

	/**
	 * is_fuzzy_query切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsFuzzyQuery() {
		renderJson(service.toggleAttrBoolean("is_fuzzy_query",getLong(0),getLong(1)));
	}

	/**
	 * is_form_ele切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsFormEle() {
		renderJson(service.toggleAttrBoolean("is_form_ele",getLong(0),getLong(1)));
	}

	/**
	 * is_required切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsRequired() {
		renderJson(service.toggleAttrBoolean("is_required",getLong(0),getLong(1)));
	}

	/**
	 * is_search_required切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsSearchRequired() {
		renderJson(service.toggleAttrBoolean("is_search_required",getLong(0),getLong(1)));
	}

	/**
	 * is_keywords_column切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsKeywordsColumn() {
		renderJson(service.toggleAttrBoolean("is_keywords_column",getLong(0),getLong(1)));
	}

	/**
	 * is_single_line切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsSingleLine() {
		renderJson(service.toggleAttrBoolean("is_single_line",getLong(0),getLong(1)));
	}

	/**
	 * is_table_switchbtn切换boolean
	 */
	@Before(Tx.class)
	public void toggleIsTableSwitchbtn() {
		renderJson(service.toggleAttrBoolean("is_table_switchbtn",getLong(0),getLong(1)));
	}

	/**
	 * 表格单元格提交更新
	 * @param codeGenModelAttr
	 */
	@Before(TxReadCommitted.class)
	public void updateColumn(@Para("") CodeGenModelAttr codeGenModelAttr) {
		renderJson(service.updateOneColumn(codeGenModelAttr));
	}

	/**
	 * 移动表单位置交换位置
	 * @param codeGenId
	 * @param id
	 * @param prevId
	 * @param nextId
	 */
	@Before(Tx.class)
	public void moveFormEle(Long codeGenId,Long id,Long prevId,Long nextId) {
		renderJson(service.moveFormEle(codeGenId,id,prevId,nextId));
	}
	/**
	 * 移动表格位置交换位置
	 * @param codeGenId
	 * @param id
	 * @param prevId
	 * @param nextId
	 */
	@Before(Tx.class)
	public void moveTableEle(Long codeGenId,Long id,Long prevId,Long nextId) {
		renderJson(service.moveTableEle(codeGenId,id,prevId,nextId));
	}

	/**
	 * 重新同步字段
	 */
	@Before(Tx.class)
	public void sync() {
		renderJson(service.syncAttrs(getLong(0)));
	}
	/**
	 *更新字段form ele配置
	 */
	@Before(Tx.class)
	public void updateFormEleConfig(JBoltPara para) {
		renderJson(service.updateFormEleConfig(para));
	}

	/**
	 * 获取列编辑表单中列的json数据
	 */
	public void colEditorData(){
		renderJsonData(service.findByIdLoadColumns(getLong(0),CodeGenModelAttr.ID,CodeGenModelAttr.TABLE_LABEL,CodeGenModelAttr.IS_NEED_FIXED_WIDTH,CodeGenModelAttr.TABLE_COL_WIDTH,CodeGenModelAttr.SORT_RANK_INTABLE));
	}

	/**
	 * 提交单列的配置修改
	 */
	public void submitColConfig(){
		renderJson(service.updateTableColConfig(getModel(CodeGenModelAttr.class,"attr")));
	}

	/**
	 * 上移 在table中显示的顺序
	 */
	public void tableColUp(){
		renderJson(service.tableColUp(getLong(0)));
	}

	/**
	 * 下移 在table中显示的顺序
	 */
	public void tableColDown(){
		renderJson(service.tableColDown(getLong(0)));
	}
}
