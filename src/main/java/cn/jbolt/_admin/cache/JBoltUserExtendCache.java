package cn.jbolt._admin.cache;
import cn.jbolt._admin.user.UserExtendService;
import cn.jbolt.common.model.UserExtend;
import cn.jbolt.core.cache.JBoltCache;
import com.jfinal.aop.Aop;

/**
 * 用户扩展信息缓存工具类
 */
public class JBoltUserExtendCache extends JBoltCache {
    public static final JBoltUserExtendCache me = new JBoltUserExtendCache();
    UserExtendService service = Aop.get(UserExtendService.class);

    public UserExtend get(Long id) {
        return service.findById(id);
    }

    private static final String TYPE_NAME = "user";
    public String getCacheTypeName() {
        return TYPE_NAME;
    }
}
