package cn.jbolt.common.controller;

import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.UnCheck;

/** Neditor Preview */
@UnCheck
public class NeditorPreviewAdminController extends JBoltBaseController {

    public void index() {
        render("/assets/plugins/neditor/dialogs/preview/preview.html");
    }

}
