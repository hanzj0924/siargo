package cn.jbolt.admin.devdoc.database;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.cache.JBoltCacheKit;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.db.datasource.JBoltDataSourceUtil;
import cn.jbolt.core.db.datasource.JBoltDatasource;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.plugin.activerecord.DbKit;
import java.util.ArrayList;
import java.util.List;

/**
 * JBolt平台内置后台开发平台使用的数据库开发文档
 */
@CheckPermission(PermissionKey.APPLICATION)
@UnCheckIfSystemAdmin
@OnlySaasPlatform
public class JBoltDatabaseDevDocController extends JBoltBaseController {
    public void index(){
        render("index.html");
    }

    /**
     * 查询所有可用数据源配置信息
     */
    public void datasources(){
        List<JBoltDatasource> mainDatasource = new ArrayList<>();
        JBoltDatasource mainDs = JBoltDataSourceUtil.me.getJBoltDatasource(DbKit.MAIN_CONFIG_NAME);
        mainDatasource.add(mainDs);
        List<JBoltDatasource> extendJBoltDataSources = JBoltDataSourceUtil.me.getExtendJBoltDataSources();
        if(isOk(extendJBoltDataSources)){
            mainDatasource.addAll(extendJBoltDataSources);
        }
        renderJsonData(mainDatasource);
    }

    /**
     * 打开一个数据源的table列表Layer页面
     */
    public void tableLayer(){
        String datasource = get("datasource");
        if(notOk(datasource)){
            renderFail("请指定具体数据源名称");
            return;
        }
        JBoltDatasource jBoltDatasource = JBoltDataSourceUtil.me.getJBoltDatasource(datasource);
        if(jBoltDatasource == null){
            renderFail("指定数据源["+datasource+"]不存在");
            return;
        }
        set("datasource",datasource);
        set("tableSize",jBoltDatasource.getTableSize());
        render("table_layer.html");
    }
    /**
     * 查询所有可用数据源配置信息
     */
    public void tables(){
        String datasource = get("datasource");
        if(notOk(datasource)){
            renderFail("请指定具体数据源名称");
            return;
        }
        JBoltDatasource jboltDatasource = JBoltDataSourceUtil.me.getJBoltDatasource(datasource);
        if(jboltDatasource == null){
            renderFail("指定数据源配置["+datasource+"]不存在");
            return;
        }
        renderJsonData(JBoltDataSourceUtil.me.getAllTablesFromCache(jboltDatasource));
    }

    /**
     * 刷新表数据
     */
    public void refreshTables(){
        String datasource = get("datasource");
        if(notOk(datasource)){
            renderFail("请指定具体数据源名称");
            return;
        }
        JBoltDatasource jboltDatasource = JBoltDataSourceUtil.me.getJBoltDatasource(datasource);
        if(jboltDatasource == null){
            renderFail("指定数据源配置["+datasource+"]不存在");
            return;
        }
        JBoltDataSourceUtil.me.refreshAllTablesCache(datasource);
        renderJsonSuccess();
    }
}
