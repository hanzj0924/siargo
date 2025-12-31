package cn.jbolt.common.config;

import java.io.File;

import cn.jbolt.core.util.JBoltDateUtil;

/**   
 * 上传控制 定义的目录
 * @ClassName:  UploadFolder   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月24日 上午12:00:48   
 */
public class JBoltUploadFolder {
	public static final String SEPARATOR = File.separator;
	
	/**
	 * 获取今天的日期对应文件夹名
	 * @return
	 */
	public static String todayFolder(){
		return JBoltDateUtil.getNowStr("yyyyMMdd");
	}
	/**
	 * 地址上追加到最后 今天的日期对应文件名
	 * @param path
	 * @return
	 */
	public static String todayFolder(String path){
		return path + SEPARATOR + todayFolder();
	}
	public static final String DEMO_EDITOR_IMAGE = "demo" + SEPARATOR + "editor";
	public static final String DEMO_IMAGE_UPLOADER = "demo" + SEPARATOR + "imguploader";
	public static final String DEMO_FILE_UPLOADER = "demo" + SEPARATOR + "fileuploader";
	public static final String EDITOR_SUMMERNOTE_IMAGE = "summernote" + SEPARATOR + "image";
	public static final String EDITOR_NEDITOR_IMAGE = "neditor" + SEPARATOR + "image";
	public static final String EDITOR_NEDITOR_WORD_IMAGE = "neditor" + SEPARATOR + "wordimage";
	public static final String EDITOR_NEDITOR_VIDEO = "neditor" + SEPARATOR + "video";
	public static final String MALL_GOODS_IMAGE = "mall" + SEPARATOR + "goods";
	public static final String MALL_BRAND_IMAGE = "mall" + SEPARATOR + "brand";
	public static final String WECHAT_MPINFO = "wechat" + SEPARATOR + "mpinfo";
	public static final String WECHAT_AUTOREPLY_REPLYCONTENT = "wechat" + SEPARATOR + "mp" + SEPARATOR + "autoreply" + SEPARATOR + "replycontent";
	public static final String WECHAT_MEDIA = "wechat" + SEPARATOR + "mp" + SEPARATOR + "media";
	public static final String USER_AVATAR = "user" + SEPARATOR + "avatar";
	public static final String CODE_GEN_TEST_IMG = "code_gen" + SEPARATOR + "test_imgs";
	public static final String CODE_GEN_TEST_FILE = "code_gen" + SEPARATOR + "test_files";
	public static final String DEMO_JBOLTTABLE_EXCEL = "demo" + SEPARATOR + "jbolttable" + SEPARATOR + "excel";
	public static final String DEMO_APITEST_IMAGE = "demo" + SEPARATOR + "apitest" + SEPARATOR + "image";
	public static final String SYSNOTICE_FILES = "sysnotice" + SEPARATOR + "files";
	public static final String IMPORT_EXCEL_TEMP_FOLDER = "exceltemps";
}
