package cn.jbolt._admin.sensitiveword;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.model.SensitiveWord;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import cn.jbolt.core.sensitiveword.JBoltSensitiveWordUtil;
import cn.jbolt.core.service.JBoltSensitiveWordService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.plugin.activerecord.tx.Tx;


@CheckPermission(PermissionKey.SENSITIVE_WORD)
@UnCheckIfSystemAdmin
public class SensitiveWordAdminController extends JBoltBaseController {
    @Inject
    JBoltSensitiveWordService service;
    public void index(){
        render("index.html");
    }

    public void counts(){
        int totalCount = service.getCount();
        int enableCount = service.getCount(Okv.by("enable",TRUE));
        int disabledCount = service.getCount(Okv.by("enable",FALSE));
        Kv datas = Kv.create();
        datas.set("totalCount", totalCount);
        datas.set("enableCount", enableCount);
        datas.set("disabledCount",disabledCount);
        datas.set("loadedCount",JBoltSensitiveWordUtil.me.getCount());
        renderJsonData(datas);
    }

    public void add(){
        render("add.html");
    }

    public void edit(){
        SensitiveWord word = service.findById(getLong(0));
        if(word == null){
            renderFail(JBoltMsg.DATA_NOT_EXIST);
            return;
        }
        set("sensitiveWord",word);
        render("edit.html");
    }

    public void reload(){
       JBoltSensitiveWordUtil.me.reload();
       renderJsonSuccess();
    }

    public void test(){
        String content = get("content");
        if(notOk(content)){
            renderJsonFail("请输入需要检测的内容");
            return;
        }
        renderJsonData(JBoltSensitiveWordUtil.me.findAllWords(content));
    }

    public void mgr(){
        render("mgr.html");
    }

    public void datas(){
        renderJsonData(service.paginateAdminDatas(getPageNumber(),getPageSize(),getKeywords(),getEnable()));
    }

    public void save(){
        renderJson(service.save(getModel(SensitiveWord.class,"sensitiveWord")));
    }

    @Before(Tx.class)
    public void update(){
        renderJson(service.update(getModel(SensitiveWord.class,"sensitiveWord")));
    }

    @Before(Tx.class)
    public void toggleEnable(){
        renderJson(service.toggleBoolean(getLong(0),"enable"));
    }

    @Before(Tx.class)
    public void delete(){
        renderJson(service.delete(getLong(0)));
    }

    @Before(Tx.class)
    public void deleteByIds(){
        renderJson(service.deleteByIds(get("ids")));
    }
}
