package cn.jbolt.index;

import cn.jbolt._admin.dept.DeptAdminController;
import cn.jbolt._admin.dictionary.DictionaryAdminController;
import cn.jbolt._admin.dictionary.DictionaryTypeAdminController;
import cn.jbolt._admin.globalconfig.GlobalConfigAdminController;
import cn.jbolt._admin.globalconfig.GlobalConfigTypeAdminController;
import cn.jbolt._admin.hiprint.HiprintAdminController;
import cn.jbolt._admin.loginlog.LoginLogAdminController;
import cn.jbolt._admin.monitor.ServerMonitorAdminController;
import cn.jbolt._admin.msgcenter.JBoltMsgCenterAdminController;
import cn.jbolt._admin.msgcenter.SysNoticeAdminController;
import cn.jbolt._admin.msgcenter.TodoAdminController;
import cn.jbolt._admin.onlineuser.OnlineUserAdminController;
import cn.jbolt._admin.permission.PermissionAdminController;
import cn.jbolt._admin.post.PostAdminController;
import cn.jbolt._admin.qiniu.QiniuAdminController;
import cn.jbolt._admin.qiniu.QiniuBucketAdminController;
import cn.jbolt._admin.redis.RedisAdminController;
import cn.jbolt._admin.role.RoleAdminController;
import cn.jbolt._admin.rolepermission.RolePermissionAdminController;
import cn.jbolt._admin.sensitiveword.SensitiveWordAdminController;
import cn.jbolt._admin.systemlog.SystemLogAdminController;
import cn.jbolt._admin.topnav.TopnavAdminController;
import cn.jbolt._admin.topnav.TopnavMenuAdminController;
import cn.jbolt._admin.user.UserAdminController;
import cn.jbolt._admin.userconfig.UserConfigAdminController;
import cn.jbolt.common.controller.NeditorPreviewAdminController;
import cn.jbolt.common.controller.NeditorUploadAdminController;
import cn.jbolt.common.controller.SummernoteUploadAdminController;
import cn.jbolt.common.style.JBoltStyleAdminController;
import cn.jbolt.core.permission.JBoltAdminAuthInterceptor;
import com.jfinal.config.Routes;
/**
 * admin后台的路由配置
 * @ClassName:  AdminRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:25:20   
 */
public class AdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin");
		this.addInterceptor(new JBoltAdminAuthInterceptor());
		this.add("/admin", AdminIndexController.class,"/index");
		this.add("/admin/pjaxerror", PjaxErrorController.class);
		this.add("/admin/jboltstyle", JBoltStyleAdminController.class);
		this.add("/admin/user", UserAdminController.class,"/user");
		this.add("/admin/role", RoleAdminController.class,"/role");
		this.add("/admin/dictionarytype", DictionaryTypeAdminController.class,"/dictionary/type");
		this.add("/admin/dictionary", DictionaryAdminController.class,"/dictionary");
		this.add("/admin/permission", PermissionAdminController.class,"/permission");
		this.add("/admin/rolepermission", RolePermissionAdminController.class,"/rolepermission");
		this.add("/admin/systemlog", SystemLogAdminController.class,"/systemlog");
		this.add("/admin/loginlog", LoginLogAdminController.class,"/loginlog");
		this.add("/admin/globalconfig", GlobalConfigAdminController.class,"/globalconfig");
		this.add("/admin/globalconfig/type", GlobalConfigTypeAdminController.class,"/globalconfig/type");
		this.add("/admin/neditor/upload", NeditorUploadAdminController.class);
		this.add("/admin/neditor/preview", NeditorPreviewAdminController.class);
		this.add("/admin/summernote/upload", SummernoteUploadAdminController.class);
		this.add("/admin/userconfig", UserConfigAdminController.class,"/userconfig");
		this.add("/admin/servermonitor", ServerMonitorAdminController.class,"/servermonitor");
		this.add("/admin/topnav", TopnavAdminController.class,"/topnav");
		this.add("/admin/topnav/menu", TopnavMenuAdminController.class,"/topnav/menu");
		this.add("/admin/dept", DeptAdminController.class,"/dept");
		this.add("/admin/post", PostAdminController.class,"/post");
		this.add("/admin/onlineuser", OnlineUserAdminController.class,"/onlineuser");
		this.add("/admin/msgcenter", JBoltMsgCenterAdminController.class,"/msgcenter");
		this.add("/admin/sysnotice", SysNoticeAdminController.class,"/msgcenter/sysnotice");
		this.add("/admin/todo", TodoAdminController.class,"/msgcenter/todo");
		this.add("/admin/qiniu", QiniuAdminController.class,"/qiniu");
		this.add("/admin/qiniu/bucket", QiniuBucketAdminController.class,"/qiniu/bucket");
		this.add("/admin/sensitiveword", SensitiveWordAdminController.class,"/sensitiveword");
		this.add("/admin/redis", RedisAdminController.class);
		this.add("/admin/hiprint", HiprintAdminController.class,"/hiprint");
	}

}
