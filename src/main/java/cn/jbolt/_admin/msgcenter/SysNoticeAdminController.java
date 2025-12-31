package cn.jbolt._admin.msgcenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jbolt.core.cache.JBoltUserCache;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.common.model.SysNotice;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.cache.JBoltDeptCache;
import cn.jbolt.core.cache.JBoltPostCache;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.JboltFile;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.service.JBoltFileService;

/**
 * 系统通知管理 Controller
 * @ClassName: SysNoticeAdminController
 * @author: JBolt-Generator
 * @date: 2021-04-03 18:56
 */
@CheckPermission(PermissionKey.SYS_NOTICE)
public class SysNoticeAdminController extends JBoltBaseController {

	@Inject
	private SysNoticeService service;
	@Inject
	private JBoltFileService jBoltFileService;

   /**
	* 首页
	*/
	public void index() {
		setDefaultSortInfo("create_time","desc");
		render("index.html");
	}

  	/**
	* 数据源
	*/
	public void datas() {
		renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),getBoolean("delFlag",false),getSortColumn("create_time"),getSortType("desc")));
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
		Long noticeId = getLong(0);
		if(notOk(noticeId)) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		SysNotice sysNotice=service.findById(noticeId);
		if(sysNotice == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		service.processReceiverValues(sysNotice);
		set("sysNotice",sysNotice);
		set("files",jBoltFileService.getListByIds(sysNotice.getFiles()));
		render("edit.html");
	}
	/**
	 * 查看附件列表
	 */
	public void files() {
		Long noticeId = getLong(0);
		if(notOk(noticeId)) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		SysNotice sysNotice=service.findById(noticeId);
		if(sysNotice == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		set("noticeId", noticeId);
		set("filesValue",sysNotice.getFiles());
		set("files",jBoltFileService.getListByIds(sysNotice.getFiles()));
		render("files.html");
	}


	/**
	 *  更新附件列表
	 */
	public void updateFiles() {
		renderJson(service.updateColumn(getLong("id"), "files", get("files")));
	}

	/**
	 * 上传多文件
	 */
	public void uploadFiles(){
		//上传到今天的文件夹下
		String uploadPath=JBoltUploadFolder.todayFolder(JBoltUploadFolder.SYSNOTICE_FILES);
		List<UploadFile> files=getFiles(uploadPath);
		if(files==null || files.size()==0) {
			renderJsonFail("请选择文件后上传");
			return;
		}

		List<JboltFile> retFiles=new ArrayList<JboltFile>();
		JboltFile jboltFile;
		StringBuilder errormsg = new StringBuilder();
		for(UploadFile uploadFile:files) {
			jboltFile=jBoltFileService.saveJBoltFile(uploadFile,uploadPath,JboltFile.FILE_TYPE_ATTACHMENT);
			if(jboltFile!=null){
				retFiles.add(jboltFile);
			}else {
				errormsg.append(uploadFile.getOriginalFileName()).append("上传失败;");
			}
		}
		if(retFiles.size()==0) {
			renderJsonFail(errormsg.toString());
			return;
		}
		renderJsonData(retFiles,errormsg.toString());
	}

	public void pdfViewer(){
		renderPdfViewer();
	}

	public void download(){
		JboltFile jboltFile = jBoltFileService.findById(getLong(0));
		if(jboltFile == null){
			renderFail("文件信息不存在");
			return;
		}
		File file = new File(jboltFile.getLocalPath());
		if(!file.exists()){
			renderFail("文件不存在");
			return;
		}
		renderFile(file, jboltFile.getFileName());
	}

   /**
	* 详情
	*/
	public void detail() {
		SysNotice sysNotice=service.findById(getLong(0));
		if(sysNotice == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		service.processReceiverValues(sysNotice);
		set("sysNotice",sysNotice);
		set("files",jBoltFileService.getListByIds(sysNotice.getFiles()));
		render("detail.html");
	}

  /**
	* 保存
	*/
	@Before(Tx.class)
	public void save() {
		renderJson(service.save(getModel(SysNotice.class, "sysNotice")));
	}

   /**
	* 更新
	*/
	@Before(Tx.class)
	public void update() {
		renderJson(service.update(getModel(SysNotice.class, "sysNotice")));
	}

	/**
	 * 批量删除
	 */
	@Before(Tx.class)
	public void batchDelete() {
		renderJson(service.deleteNotices(get("ids"),false));
	}
   /**
	* 批量还原恢复回收站数据
	*/
	@Before(Tx.class)
	public void batchRestore() {
		renderJson(service.restoreNotices(get("ids")));
	}

	/**
	 * 根据类型进入不同选择界面
	 */
	public void chooseReceiver() {
		Integer receiverType = getInt("receiverType");
		if(notOk(receiverType)) {
			renderPageFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		//5是在字典表里定义的 接收者类型里的编码sn 定死
		if(receiverType.intValue() == 5) {
			set("hasDept", JBoltDeptCache.me.checkHasDept());
			set("hasPost", JBoltPostCache.me.checkHasPost());
			String ids = get("ids");
			if(isOk(ids)){
				set("selectedUsers",JBoltUserCache.me.getListByIdsStr(ids));
			}
		}
		keepPara("ids");
		render("choose_"+receiverType.intValue()+".html");
	}

}
