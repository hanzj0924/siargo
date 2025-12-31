package cn.jbolt.admin.wechat.mpinfo;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.appdevcenter.ApplicationService;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.cache.JBoltApplicationCache;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.enumutil.JBoltEnableEnum;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;

/**   
 * 微信公众平台管理
 * @ClassName:  WechatMpinfoAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月7日 下午5:15:36   
 */
@CheckPermission(PermissionKey.WECHAT_MPINFO)
@OnlySaasPlatform
public class WechatMpinfoAdminController extends JBoltBaseController {
	@Inject
	private WechatMpinfoService service;
	@Inject
	private ApplicationService applicationService;
	/**
	 * 首页
	 */
	public void index(){
		render("index.html");
	}
	/**
	 * 数据源
	 */
	public void datas(){
		renderJsonData(service.paginateAdminList(getPageNumber(),getPageSize(),getKeywords(),getEnable(),getType(),getInt("subjectType"),getBoolean("isAuthenticated")));
	}
	
	/**
	 * 上传公众平台相关图片
	 */
	public void uploadImage(){
		//上传到今天的文件夹下
		String todayFolder=JBoltUploadFolder.todayFolder();
		String uploadPath=JBoltUploadFolder.WECHAT_MPINFO+"/"+todayFolder;
		UploadFile file=getFile("file",uploadPath);
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJsonData(JFinal.me().getConstants().getBaseUploadPath()+"/"+uploadPath+"/"+file.getFileName());
	}
	/**
	 * 进入新增表单
	 */
	public void add(){
		render("add.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getModel(WechatMpinfo.class,"wechatMpinfo")));
	}
	/**
	 * 编辑
	 */
	public void edit(){
		WechatMpinfo wechatMpinfo=service.findById(getLong(0));
		if(wechatMpinfo==null){
			renderFormFail(JBoltMsg.DATA_NOT_EXIST);
		}else{
			set("wechatMpinfo",wechatMpinfo);
			render("edit.html");
		}
	}
	/**
	 * 编辑关联的APP
	 */
	public void editLinkApp(){
		WechatMpinfo wechatMpinfo=service.findById(getLong(0));
		if(wechatMpinfo==null){
			renderFormFail(JBoltMsg.DATA_NOT_EXIST);
		}else{
			set("wechatMpinfo",wechatMpinfo);
			set("application",JBoltApplicationCache.me.get(wechatMpinfo.getLinkAppId()));
			render("editlinkapp.html");
		}
	}
	
	/**
	 * 获取application列表
	 * 通过关键字匹配 
	 * autocomplete组件使用
	 */
	public void apps() {
		renderJsonData(applicationService.getAutocompleteListByWechatMpinfoType(getInt("type"),get("q"),getInt("limit",10)));
	}
	
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getModel(WechatMpinfo.class,"wechatMpinfo")));
	}
	/**
	 * 删除一个公众平台
	 */
	public void delete(){
		renderJson(service.delete(getLong(0)));
	}
	/**
	 * 清空所有配置 慎用
	 */
	public void clearAllConfigs(){
		renderJson(service.clearAllConfigs(getLong(0)));
	}
	/**
	 * 切换启用状态
	 */
	public void toggleEnable(){
		renderJson(service.toggleEnable(getLong(0)));
	}
	/**
	 * 切换认证状态
	 */
	public void toggleAuthenticated(){
		renderJson(service.toggleAuthenticated(getLong(0)));
	}
	/**
	 * enable 启用状态数据源
	 */
	public void enableOptions(){
		renderJsonData(JBoltEnum.getEnumOptionList(JBoltEnableEnum.class));
	}
	/**
	 * isAuthenticated  是否认证数据源
	 */
	public void isAuthenticatedOptions(){
		renderJsonData(JBoltEnum.getEnumOptionList(WechatMpinfoAuthenticated.class));
	}
	/**
	 * 类型数据源
	 */
	public void typeOptions(){
		renderJsonData(JBoltEnum.getEnumOptionList(WechatMpinfoType.class));
	}
	/**
	 * 主体类型数据源
	 */
	public void subjectTypeOptions(){
		renderJsonData(JBoltEnum.getEnumOptionList(JBoltWechatMpinfoSubjectType.class));
	}
	
	/**
	 * 提交关联APP
	 */
	@Before(Tx.class)
	public void submitLinkApp() {
		renderJson(service.submitLinkApp(getLong("mpId"),getLong("appId")));
	}
	/**
	 * 解除关联APP
	 */
	@Before(Tx.class)
	public void removeLinkApp() {
		renderJson(service.removeLinkApp(getLong(0)));
	}
}
