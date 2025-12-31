package cn.jbolt._admin.jboltfile;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.core.base.JBoltPageSize;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.JboltFile;
import cn.jbolt.core.service.JBoltFileService;

/**   
 * 文件库
 * @ClassName:  JBoltFileAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月24日 上午12:38:43   
 */
public class JBoltFileAdminController extends JBoltBaseController {
	@Inject
	private JBoltFileService service;
	public void index(){
		Page<JboltFile> pageData=service.paginateAdminData(getPageNumber(),getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST_20));
		set("pageData", pageData);
		render("index.html");
	}
	
}
