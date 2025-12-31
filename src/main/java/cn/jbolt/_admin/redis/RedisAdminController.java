package cn.jbolt._admin.redis;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.OnlySaasPlatform;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import com.jfinal.core.Path;
import com.jfinal.core.paragetter.Para;
import com.jfinal.plugin.redis.Redis;

/**
 * redis操作
 */
@CheckPermission(PermissionKey.JBOLT_SERVER_MONITOR)
@UnCheckIfSystemAdmin
@OnlySaasPlatform
public class RedisAdminController extends JBoltBaseController {
    /**
     * 删除当前redis db中的数据
     * @param cacheName
     */
    public void removeCurrentDatabaseAllDatas(@Para("cacheName")String cacheName){
        if(notOk(cacheName)){
            renderJsonFail(JBoltMsg.PARAM_ERROR);
            return;
        }
        Redis.use(cacheName).flushDB();
        String database = JBoltConfig.redisSettings.getByGroup("database",cacheName);
        if(notOk(database)){
            database = "DB0";
        }else{
            database = "DB"+database;
        }
        renderJsonSuccess("["+cacheName+"]中的["+ database +"]的数据已清空");
    }
}
