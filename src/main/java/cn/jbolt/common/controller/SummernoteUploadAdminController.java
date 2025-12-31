package cn.jbolt.common.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.ext.interceptor.NoUrlPara;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.service.JBoltFileService;
import cn.jbolt.core.util.JBoltRealUrlUtil;
/**
 * 给系统Summernote编辑器组件提供的默认上传路径
 * @ClassName:  SummernoteUploadAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月1日   
 */
public class SummernoteUploadAdminController extends JBoltBaseController {
	@Inject
	private JBoltFileService jboltFileService;
	@UnCheck
	@Before(NoUrlPara.class)
	public void index() {
		renderNull();
	}
	@UnCheck
	public void image(){
		String todayFolder=JBoltUploadFolder.todayFolder();
		String uploadPath=JBoltUploadFolder.EDITOR_SUMMERNOTE_IMAGE+"/"+todayFolder;
		UploadFile file=null;
		try {
			file= getFile("file", uploadPath);
		} catch (RuntimeException e) {
			return;
		}
		if (file != null && file.getFile() != null&&file.getFile().exists()) {
			if(notImage(file.getContentType())){
				renderJsonFail("请上传图片类型文件");
				return;
			}
			//保存图片类文件
			Ret ret=jboltFileService.saveImageFile(file,uploadPath);
			if(ret.isFail()) {
				renderJson(ret);
			}else {
				renderJsonData(JBoltRealUrlUtil.getImage(ret.get("data")));
			}
		}
	}
}
