package cn.jbolt._admin.dictionary;


import java.util.List;

import cn.jbolt.core.cache.JBoltDictionaryCache;
import cn.jbolt.core.common.enums.DictionaryTypeMode;
import cn.jbolt.core.para.JBoltNoUrlPara;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.Dictionary;
import cn.jbolt.core.model.DictionaryType;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
/**
 * 字典管理Controller
 * @ClassName:  DictionaryAdminController
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2019年11月9日
 */
@CheckPermission({PermissionKey.DICTIONARY})
@UnCheckIfSystemAdmin
@OnlySaasPlatform
public class DictionaryAdminController extends JBoltBaseController {
	@Inject
	private DictionaryService service;
	@Inject
	private DictionaryTypeService dictionaryTypeService;

	@Before(JBoltNoUrlPara.class)
	public void datas(){
		Long typeId = getLong("typeId");
		DictionaryType type = dictionaryTypeService.findById(typeId);
		if(type == null) {
			renderJsonData("");
			return;
		}
		List<Dictionary> dics = service.getListByTypeId(typeId,getKeywords(), getBoolean("enable",true));
		Kv extraData=Kv.by("typeLevel", type.getModeLevel());
		//使用这个专门的方法 render出去
		renderJBoltTableJsonData(dics,extraData);
	}
	/**
	 * options
	 */
	@UnCheck
	@Before(JBoltNoUrlPara.class)
	public void options(){
		renderJsonData(JBoltDictionaryCache.me.getListByTypeKey(get("key"),true));
	}
	/**
	 * 根据获取一级options
	 */
	@UnCheck
	@Before(JBoltNoUrlPara.class)
	public void poptions(){
		renderJsonData(service.getRootOptionListByTypeKey(get("key"),true));
	}
	/**
	 * 子类级别数据 根据父类ID获取数据
	 */
	@UnCheck
	@Before(JBoltNoUrlPara.class)
	public void soptions(){
		renderJsonData(service.getSonOptionListByTypeKey(get("key"),getLong("pid"),true));
	}
	/**
	 * 子类级别数据 根据父类SN获取数据
	 */
	@UnCheck
	@Before(JBoltNoUrlPara.class)
	public void soptionsByPsn(){
		renderJsonData(service.getSonOptionListByTypeKeyAndPsn(get("key"),get("psn"),true));
	}

	public void checkandinit() {
		renderJson(service.checkAndInit());
	}
	/**
	 * 加载管理portal
	 */
	public void mgr(){
		Long typeId=getLong(0);
		if(notOk(typeId)){
			renderAjaxPortalFail("选择左侧分类查询数据");
			return;
		}else{
			DictionaryType type=dictionaryTypeService.findById(typeId);
			if(type==null){
				renderAjaxPortalFail("参数异常，加载失败");
				return;
			}
			initMgr(type);
		}

		render("mgrportal.html");
	}

	private void initMgr(DictionaryType type){
		set("dictionaryType", type);
		List<Dictionary> dictionaries=service.getListByTypeId(type.getId(),null);
		set("dictionaries",dictionaries);
		if(type.getModeLevel()== DictionaryTypeMode.LEVEL_MULTI.getValue()){
			set("dataTotalCount", service.getCountByTypeId(type.getId()));
		}else{
			set("dataTotalCount", dictionaries.size());
		}
	}
	/**
	 * 管理首页
	 */
	public void index(){
		render("index_ajax.html");
	}
	/**
	 * 从日志过来的显示一个
	 */
	public void show(){
		Long dictionaryId=getLong(0);
		if(notOk(dictionaryId)){
			renderPjaxFail("数据不存在或已被删除");
		}else{
			Dictionary dictionary=service.findById(dictionaryId);
			if(dictionary==null){
				renderPjaxFail("数据不存在或已被删除");
			}else{
				Long typeId=dictionary.getTypeId();
				set("typeId", typeId);
				set("dataList",service.getListByTypeId(typeId,null));
				set("dataTotalCount", service.getCountByTypeId(typeId));
				set("showId", dictionaryId);
				//TODO #mmm 前端页面实现show效果
				render("index.html");
			}

		}
	}
	/**
	 * 除了自己以外的其它所有数据
	 */
	public void select(){
		renderJsonData(service.getListByTypeId(getLong(0),null));
	}

	/**
	 * 新增
	 */
	public void add(){
		Long typeId=getLong("typeId");
		if(notOk(typeId)) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		set("typeId", typeId);
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType.getModeLevel()==DictionaryTypeMode.LEVEL_MULTI.getValue()) {
			set("needPidSelect",true);
		}
		render("add.html");
	}
	/**
	 * 新增
	 */
	public void addItem(){
		Long typeId=getLong(0);
		Long pid=getLong(1);
		if(notOk(typeId)||notOk(pid)) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType==null) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		if(dictionaryType.getModeLevel()==DictionaryTypeMode.LEVEL_MULTI.getValue()) {
			set("needPidSelect",true);
		}
		set("typeId",typeId);
		set("pid", pid);
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		Long typeId=getLong(0);
		Long id=getLong(1);
		if(notOk(typeId)||notOk(id)) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Dictionary dictionary=service.findById(id);
		if(dictionary==null) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType==null) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		if(dictionaryType.getModeLevel()==DictionaryTypeMode.LEVEL_MULTI.getValue()) {
			set("needPidSelect",true);
		}
		set("dictionary",dictionary );
		set("typeId", typeId);
		set("pid", dictionary.getPid());
		render("edit.html");
	}
	/**
	 * 编辑
	 */
	public void editItem(){
		Long typeId=getLong(0);
		Long id=getLong(1);
		if(notOk(typeId)||notOk(id)) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Dictionary dictionary=service.findById(id);
		if(dictionary==null) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		DictionaryType dictionaryType=dictionaryTypeService.findById(typeId);
		if(dictionaryType==null) {
			renderFormFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		if(dictionaryType.getModeLevel()==DictionaryTypeMode.LEVEL_MULTI.getValue()) {
			set("needPidSelect",true);
		}
		set("typeId", typeId);
		set("dictionary",dictionary );
		set("pid", dictionary.getPid());
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		Dictionary dictionary=getModel(Dictionary.class, "dictionary");
		renderJson(service.save(dictionary));
	}
	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update(){
		Dictionary dictionary=getModel(Dictionary.class, "dictionary");
		renderJson(service.update(dictionary));
	}
	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete(){
		renderJson(service.deleteDictionaryById(getLong(0),false));
	}
	/**
	 * 上移
	 */
	public void up(){
		renderJson(service.doUp(getLong(0)));
	}
	/**
	 * 下移
	 */
	public void down(){
		renderJson(service.down(getLong(0)));
	}
	/**
	 * 初始化顺序
	 */
	public void initRank(){
		renderJson(service.initRank(getLong("typeId")));
	}
	/**
	 * 清空分类下的字典数据
	 */
	public void clearByType(){
		renderJson(service.clearByTypeId(getLong("typeId")));
	}

	@Before(Tx.class)
	public void toggleEnable(){
		renderJson(service.toggleBoolean(getLong(0),Dictionary.ENABLE));
	}
}
