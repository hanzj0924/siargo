package cn.jbolt.admin.siargo.prodmodel;

import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.common.storage.SiargoUploadFiles;
import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import cn.jbolt.core.base.JBoltMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.kit.Ret;
import java.util.function.Function;
import com.jfinal.upload.UploadFile;
import cn.jbolt.siargo.model.ProdModel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
/**
 * 产品型号 Controller
 * <p>新增/修改由交互式输入器弹窗页面以 JSON 提交，save/update 事务在 Service 内 Db.tx 管理，本层不加 @Before(Tx)。
 * <p>产品资料（机械尺寸图、技术参数）接口同样不加 @Before(Tx)：Service 内已有 Db.tx 与文件移动补偿，
 * 叠加声明式事务会让 Ret.fail 软失败无法回滚。
 * @ClassName: ProdModelAdminController
 * @author: hanzj
 * @date: 2026-09-02 16:16
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/prodmodel", viewPath = "/_view/admin/siargo/prodmodel")
//true
public class ProdModelAdminController extends JBoltBaseController {

	@Inject
	private ProdModelService service;
	@Inject
	private cn.jbolt.admin.siargo.qarep.product.ProductSeriesService productSeriesService;
	@Inject
	private ProdModelDimensionService dimensionService;
	@Inject
	private ProdTechnicalParamService technicalService;

	/** 机械尺寸图允许的图片扩展名，上传时先做快速拦截，落盘前 Service 再用 ImageIO 完整校验 */
	private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("png","jpg","jpeg","gif","bmp"));

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

	/** 产品卡片可搜索系列；编辑时允许回显当前已停用关联。 */
	public void options() {
		renderJsonData(productSeriesService.options(getLong("includeId")));
	}

   /**
	* 新增
	*/
	public void add() {
		render("add.html");
	}

   /**
	* 编辑（注入 editDataJson 供交互式输入器回显）
	*/
	public void edit() {
		ProdModel prodModel=service.findById(getLong(0));
		if(prodModel == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		String editDataJson=service.getEditDataJson(prodModel.getId());
		if(editDataJson == null){
			renderFail("操作失败");
			return;
		}
		set("prodModel",prodModel);
		set("editDataJson",editDataJson);
		render("edit.html");
	}

  /**
	* 保存（JSON 体：{series,prodType,isActive,productIntro,productFeatures,remark,segments:[{paramTypeId,valueIds[]}]})
	*/
	public void save() {
		jsonAction(service::save);
	}

   /**
	* 更新（JSON 体同 save，另含 id）
	*/
	public void update() {
		jsonAction(service::update);
	}

	/** 只读型号组合，按参数片段和服务端真实字典校验。 */
	public void compose() {
		jsonAction(service::compose);
	}

	private void jsonAction(Function<JSONObject, Ret> action) {
		try {
			Object body = JSON.parse(getRawData());
			if (!(body instanceof JSONObject json)) {
				renderJsonFail("请求数据格式错误，请刷新后重新提交");
				return;
			}
			renderJson(action.apply(json));
		} catch (com.alibaba.fastjson.JSONException | IllegalArgumentException | ClassCastException error) {
			renderJsonFail("请求参数格式错误，请核对型号和选型参数");
		}
	}

	/**
	 * 切换启用状态（列表滑动开关调用）
	 */
	public void toggleActive() {
		renderJson(service.toggleActive(getLong(0)));
	}

   /**
	* 批量删除（主表 + 选型附表 + 机械尺寸图 + 技术参数事务内联动删除，事务在 Service）
	*/
	public void deleteByIds() {
		renderJson(service.deleteByBatchIds(get("ids")));
	}

	// ==================== 产品资料：机械尺寸图 ====================

	/** 同系列全部目录分支的选型图，只读展示。 */
	public void selection() {
		ProdModel prodModel = service.findById(getLong(0));
		if (prodModel == null) {
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("selectionDataJson", service.getSelectionDataJson(prodModel));
		render("selection.html");
	}

	/**
	 * 产品资料展示弹窗（只读，排版参照产品目录）
	 * URL: GET /admin/siargo/prodmodel/materials/{id}
	 * @param id 产品系列ID（从URL路径获取）
	 */
	public void materials() {
		ProdModel prodModel=service.findById(getLong(0));
		if(prodModel == null){
			renderFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("prodModel",prodModel);
		setAttrs(service.getDisplayData(prodModel));
		render("materials.html");
	}

	/**
	 * 机械尺寸图列表
	 * URL: GET /admin/siargo/prodmodel/dimensionList?modelId=
	 */
	public void dimensionList() {
		Long modelId=getLong("modelId");
		if(modelId==null){
			renderJsonFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		renderJsonData(dimensionService.listJsonForModel(modelId));
	}

	/**
	 * 新增机械尺寸图
	 * URL: POST /admin/siargo/prodmodel/dimensionSave
	 * JSON 体：{modelId,title,imagePath,sortRank,remark}
	 */
	public void dimensionSave() {
		renderJson(dimensionService.persistFromJson(JSON.parseObject(getRawData()),false));
	}

	/**
	 * 修改机械尺寸图（JSON 体同新增，另含 id）
	 * URL: POST /admin/siargo/prodmodel/dimensionUpdate
	 */
	public void dimensionUpdate() {
		renderJson(dimensionService.persistFromJson(JSON.parseObject(getRawData()),true));
	}

	/**
	 * 删除机械尺寸图
	 * URL: POST /admin/siargo/prodmodel/dimensionDelete/{id}
	 */
	public void dimensionDelete() {
		renderJson(dimensionService.remove(getLong(0)));
	}

	/**
	 * 上传机械尺寸图到本次请求独享的临时目录，返回临时URL；
	 * 正式落盘与失败移回补偿在保存时由 Service 的事务完成。
	 * URL: POST /admin/siargo/prodmodel/dimensionUpload
	 */
	public void dimensionUpload() {
		SiargoStorage storage=dimensionService.storage();
		try {
			UploadFile file=getFile("file", SiargoUploadFiles.newUploadDirectory(storage));
			if(file==null){
				renderJsonFail("请选择图片后上传");
				return;
			}
			java.nio.file.Path uploaded=file.getFile().toPath();
			String name=SiargoStorage.safeSegment(file.getOriginalFileName());
			if(!ALLOWED_IMAGE_EXTENSIONS.contains(getFileExtension(name).toLowerCase(Locale.ROOT))){
				storage.deleteFile(uploaded);
				renderJsonFail("仅支持 PNG/JPG/JPEG/GIF/BMP 图片");
				return;
			}
			if(!dimensionService.validImage(uploaded)){
				storage.deleteFile(uploaded);
				renderJsonFail("图片无法解析或超出限制（不超过20MB、4000万像素）");
				return;
			}
			renderJsonData(SiargoUploadFiles.accept(storage,file));
		} catch (Exception e) {
			renderJsonFail("上传失败："+e.getMessage());
		}
	}

	/**
	 * 清理临时目录中未保存的上传文件；SiargoUploadFiles 限定只能操作 temp 目录，防路径穿越
	 * URL: POST /admin/siargo/prodmodel/deleteTempFile
	 */
	public void deleteTempFile() {
		String url=getPara("filePath");
		if(StrKit.isBlank(url)){
			renderJsonFail("文件路径不能为空");
			return;
		}
		renderJson(SiargoUploadFiles.delete(dimensionService.storage(), List.of(url), true));
	}

	// ==================== 产品资料：技术参数 ====================

	/**
	 * 技术参数列表
	 * URL: GET /admin/siargo/prodmodel/technicalList?modelId=
	 */
	public void technicalList() {
		Long modelId=getLong("modelId");
		if(modelId==null){
			renderJsonFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		renderJsonData(technicalService.listJsonForModel(modelId));
	}

	/**
	 * 新增技术参数
	 * URL: POST /admin/siargo/prodmodel/technicalSave
	 * JSON 体：{modelId,paramName,paramValue,unit,sortRank,remark}
	 */
	public void technicalSave() {
		renderJson(technicalService.persistFromJson(JSON.parseObject(getRawData()),false));
	}

	/**
	 * 修改技术参数（JSON 体同新增，另含 id）
	 * URL: POST /admin/siargo/prodmodel/technicalUpdate
	 */
	public void technicalUpdate() {
		renderJson(technicalService.persistFromJson(JSON.parseObject(getRawData()),true));
	}

	/**
	 * 删除技术参数
	 * URL: POST /admin/siargo/prodmodel/technicalDelete/{id}
	 */
	public void technicalDelete() {
		renderJson(technicalService.remove(getLong(0)));
	}

	/**
	 * 获取文件扩展名（不带点）
	 * @param fileName 文件名
	 * @return 扩展名，无扩展名时返回空串
	 */
	private String getFileExtension(String fileName) {
		if(StrKit.isBlank(fileName)){
			return "";
		}
		int dotIndex=fileName.lastIndexOf('.');
		return dotIndex>0?fileName.substring(dotIndex+1):"";
	}

}
