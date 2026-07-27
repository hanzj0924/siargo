package cn.jbolt.admin.siargo.qarep.pdffolder;

import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.PdfFolder;
import cn.jbolt.siargo.model.PdfTemplate;
/**
 * 报告单模板 Controller
 * @ClassName: PdfFolderAdminController
 * @author: hanzj
 * @date: 2026-07-20 13:37
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/qarep/pdffolder", viewPath = "/_view/admin/siargo/pdffolder")
//true
public class PdfFolderAdminController extends JBoltBaseController {

	@Inject
	private PdfFolderService service;

	@Inject
	private PdfTemplateService pdfTemplateService;
	
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
		PdfFolder pdfFolder=service.findById(getLong(0)); 
		if(pdfFolder == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("pdfFolder",pdfFolder);
		render("edit.html");
	}
	
  /**
	* 保存
	*/
    @Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(PdfFolder.class, "pdfFolder")));
	}
	
   /**
	* 更新
	*/
    @Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(PdfFolder.class, "pdfFolder")));
	}
	
   /**
	* 批量删除
	*/
    @Before(Tx.class)
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}

	// ======================== 版号文件夹管理 ========================

	/** 获取所有已创建的版号文件夹 */
	public void folders() {
		renderJsonData(service.listAll());
	}

	/** 获取字典中所有可用版号（标记是否已创建） */
	public void dictVersions() {
		renderJsonData(service.listDictVersions());
	}

	/** 创建版号文件夹（从字典联动） */
	@Before(Tx.class)
	public void createFolder() {
		renderJson(service.createVersionFolder(getLong("dictId")));
	}

	/** 删除版号文件夹 */
	@Before(Tx.class)
	public void deleteFolder() {
		renderJson(service.deleteVersionFolder(get("pdfver")));
	}

	// ======================== 模板文件管理 ========================

	/** 获取指定版号的模板文件列表 */
	public void templates() {
		renderJsonData(pdfTemplateService.listTemplates(get("pdfver")));
	}

	/** 上传模板文件 */
	public void upload() {
		String ver = get("pdfver");
		com.jfinal.upload.UploadFile file = getFile("file");
		renderJson(pdfTemplateService.uploadTemplate(ver, file));
	}

	/** 删除模板文件 */
	public void deleteFile() {
		renderJson(pdfTemplateService.deleteTemplate(get("pdfver"), get("fileName")));
	}

	// ======================== 匹配规则管理 ========================

	/** 获取匹配规则列表 */
	public void rules() {
		renderJsonData(pdfTemplateService.paginateRules(get("pdfver"), getPageNumber(), getPageSize()));
	}

	/** 保存规则（新增/编辑） */
	@Before(Tx.class)
	public void saveRule() {
		renderJson(pdfTemplateService.saveRule(getModel(PdfTemplate.class, "rule")));
	}

	/** 删除规则 */
	@Before(Tx.class)
	public void deleteRule() {
		renderJson(pdfTemplateService.deleteRule(getLong("id")));
	}

	// ======================== 缓存管理 ========================

	/** 清除缓存 */
	public void clearCache() {
		service.clearCache();
		pdfTemplateService.clearCache();
		renderJsonSuccess("缓存已刷新");
	}

}
