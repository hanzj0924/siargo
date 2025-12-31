package cn.jbolt._admin.cache;

import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.cache.JBoltCache;
import cn.jbolt.core.cache.JBoltCacheKit;
import cn.jbolt.core.cache.JBoltWechatConfigCache;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.jfinal.weixin.sdk.msg.out.OutMsg;

public class JboltWechatConfigCache extends JBoltCache {
    @Override
    public String getCacheTypeName() {
        return null;
    }

}
