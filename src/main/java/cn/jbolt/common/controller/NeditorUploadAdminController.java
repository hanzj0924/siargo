package cn.jbolt.common.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.NoUrlPara;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.ExceededSizeException;
import com.jfinal.upload.UploadFile;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.jbolt.common.config.JBoltUploadFolder;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.service.JBoltFileService;
import cn.jbolt.core.util.JBoltRealUrlUtil;
/**
 * 给系统NEditor组件提供的默认上传路径
 * @ClassName:  NeditorUploadAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月1日   
 */
public class NeditorUploadAdminController extends JBoltBaseController {
	@Inject
	private JBoltFileService jboltFileService;
	@UnCheck
	@Before(NoUrlPara.class)
	public void index() {
		renderNull();
	}
	@UnCheck
	public void wordimg(){
		String todayFolder=JBoltUploadFolder.todayFolder();
		String uploadPath=JBoltUploadFolder.EDITOR_NEDITOR_WORD_IMAGE+"/"+todayFolder;
		UploadFile file=null;
		try {
			file= getFile("file", uploadPath);
		} catch (ExceededSizeException e) {
			String errorMsg="文件尺寸太大了，不能超过"+FileUtil.readableFileSize(JFinal.me().getConstants().getMaxPostSize());
        	LOG.error(errorMsg);
        	renderNull();
        	return;
		} catch (RuntimeException e) {
			String errorMsg="文件上传失败";
			LOG.error(errorMsg);
			renderNull();
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
	@UnCheck
	public void image(){
		String todayFolder=JBoltUploadFolder.todayFolder();
		String uploadPath=JBoltUploadFolder.EDITOR_NEDITOR_IMAGE+"/"+todayFolder;
		UploadFile file=null;
		try {
			file= getFile("file", uploadPath);
		} catch (ExceededSizeException e) {
			String errorMsg="文件尺寸太大了，不能超过"+FileUtil.readableFileSize(JFinal.me().getConstants().getMaxPostSize());
			LOG.error(errorMsg);
			renderNull();
			return;
		} catch (RuntimeException e) {
			String errorMsg="文件上传失败";
			LOG.error(errorMsg);
			renderNull();
			return;
		}
		if (file != null && file.getFile() != null&&file.getFile().exists()) {
			String originalName = file.getOriginalFileName();
			//保存图片类文件
			Ret saveRet=jboltFileService.saveImageFile(file,uploadPath);
			if(saveRet.isOk()) {
				renderJson(Ret.by("code",200)
						.set("state", "SUCCESS")
						.set("url", JBoltRealUrlUtil.getImage(saveRet.get("data")))
						.set("original",originalName));
			}else {
				renderJson(Ret.by("code", "error").set("message","上传失败"));
			}
			
		}
		
		
	}
	
	@UnCheck
	public void catchImage(){
		String todayFolder=JBoltUploadFolder.todayFolder();
		String uploadPath=JBoltUploadFolder.EDITOR_NEDITOR_IMAGE+"/"+todayFolder;
		String filePath=JFinal.me().getConstants().getBaseUploadPath()+"/"+uploadPath;
		String[] fileUrls=getParaValues("file[]");
		Kv kv=Kv.by("code", 200).set("state","SUCCESS");
		Kv temp;
		List<Kv> kvs=new ArrayList<Kv>();
		String dest,suf,fileName,originName;
		UploadFile uploadFile;
		Ret saveRet;
		if(!FileUtil.isAbsolutePath(filePath)) {
			filePath=PathKit.getWebRootPath()+"/"+filePath;
		}
		filePath=FileUtil.normalize(filePath);
		
		for(String url:fileUrls) {
			originName=FileNameUtil.getName(url);
			if(StrKit.isBlank(originName)) {
				originName=url;
			}
			suf=FileNameUtil.getSuffix(originName);
			if(StrKit.isBlank(suf)){
				suf="jpg";
			}
			fileName=IdUtil.fastSimpleUUID()+"."+suf;
			dest=filePath+File.separator+fileName;
			HttpUtil.downloadFileFromUrl(url, dest);
			if(!FileUtil.isFile(dest)||!FileUtil.exist(dest)) {
				continue;
			}
			uploadFile=new UploadFile("file", filePath, fileName, originName, "");
			//保存图片类文件
			saveRet=jboltFileService.saveImageFile(uploadFile,uploadPath);
			if(saveRet.isFail()) {
				continue;
			}
			
			temp=Kv.by("state", "SUCCESS");
			temp.set("source",url);
			temp.set("url",JBoltRealUrlUtil.getImage(saveRet.get("data")));
			kvs.add(temp);
		}
		kv.set("list",kvs);
		renderJson(kv);
	}
        @UnCheck
        public void video(){
        	String todayFolder=JBoltUploadFolder.todayFolder();
        	String uploadPath=JBoltUploadFolder.EDITOR_NEDITOR_VIDEO+"/"+todayFolder;
            UploadFile file=null;
            try {
            	file= getFile("file", uploadPath);
            } catch (ExceededSizeException e) {
            	String errorMsg="文件尺寸太大了，不能超过"+FileUtil.readableFileSize(JFinal.me().getConstants().getMaxPostSize());
            	LOG.error(errorMsg);
            	renderJson(Ret.by("code", "exceed_size").set("message",errorMsg));
            	return;
			} catch (RuntimeException e) {
				String errorMsg="文件上传失败";
				LOG.error(errorMsg);
				renderJson(Ret.by("code", "upload_error").set("message",errorMsg));
				return;
			}
    		if (file != null && file.getFile() != null&&file.getFile().exists()) {
                String originalName = file.getOriginalFileName();
                //保存视频文件
                Ret saveRet=jboltFileService.saveVideoFile(file,uploadPath);
                if(saveRet.isOk()) {
                	renderJson(Ret.by("code",200)
                    		.set("state", "SUCCESS")  //下面这几个都是必须返回给ueditor的数据
                    		.set("url", JBoltRealUrlUtil.get(saveRet.get("data")))  //文件上传后的路径
                            .set("original",originalName));
                }else {
                	renderJson(Ret.by("code", "error").set("message","上传失败"));
                }
    		}
            
 
    }
}