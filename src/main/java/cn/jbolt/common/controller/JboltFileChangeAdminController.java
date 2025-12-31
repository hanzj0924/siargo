package cn.jbolt.common.controller;

import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.service.JBoltFileService;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;

@Path(value = "/admin/jboltfileChange")
@UnCheck
public class JboltFileChangeAdminController extends JBoltBaseController {
    @Inject
    private JBoltFileService service;
    public void index(){
        renderJsonData(service.getListByIds(get("ids")));
    }
}
