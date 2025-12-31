package cn.jbolt._admin.customtablerender.custombusiness;
import cn.jbolt.core.cache.JBoltCache;
import com.jfinal.aop.Aop;
import cn.jbolt._admin.customtablerender.common.model.CustomBusiness;
import cn.jbolt._admin.customtablerender.custombusiness.CustomBusinessService;

public class CustomBusinessCache extends JBoltCache {
    public static final CustomBusinessCache me = new CustomBusinessCache();
    CustomBusinessService service = Aop.get(CustomBusinessService.class);
    private static final String TYPE_NAME = "jbc_custom_business";
    public String getCacheTypeName() {
        return TYPE_NAME;
    }

    /**
     * 通过ID 获取
     * @param id
     * @return
     */
    public CustomBusiness get(Long id) {
        return service.findById(id);
    }

    /**
     * 通过ID 获取Name
     * @param id
     * @return
     */
    public String getName(Long id) {
        CustomBusiness obj = get(id);
        return obj == null ? null : obj.getName();
    }


}

