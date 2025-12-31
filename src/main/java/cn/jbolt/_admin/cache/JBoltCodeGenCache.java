package cn.jbolt._admin.cache;

import cn.hutool.core.util.ClassUtil;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.bean.Option;
import cn.jbolt.core.bean.OptionBean;
import cn.jbolt.core.cache.JBoltCache;
import cn.jbolt.core.cache.JBoltCacheKit;
import cn.jbolt.core.service.base.IJBoltService;
import cn.jbolt.core.util.JBoltArrayUtil;
import com.jfinal.plugin.ehcache.IDataLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class JBoltCodeGenCache extends JBoltCache{
    public static final JBoltCodeGenCache me = new JBoltCodeGenCache();
    private static final String TYPE_NAME = "code_gen";
    public String getCacheTypeName() {
        return TYPE_NAME;
    }

    /**
     * 获取项目里的枚举类
     * @return
     */
    public List<Option> getCodeGenEnums(){
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, "codeGen_enums",new IDataLoader() {
            @Override
            public Object load() {
                return getJboltCodeGenEnums();
            }
        });
    }

    public List<Option> getJboltCodeGenEnums() {
        List<Option> options = new ArrayList<>();
        String[] packs = JBoltArrayUtil.from3(JBoltConfig.JBOLT_CODE_GEN_CLASS_SCAN_PACKAGES);
        for(String packageName:packs){
            Set<Class<?>> enums = ClassUtil.scanPackageBySuper(packageName, java.lang.Enum.class);
            if(enums!=null && enums.size()>0) {
                enums.forEach(en->{
                    options.add(new OptionBean(en.getName()));
                });
            }
        }

        if(options.size()>0){
            //排序
            options.sort(new Comparator<Option>() {
                @Override
                public int compare(Option o1, Option o2) {
                    return o1.getText().compareTo(o2.getText());
                }
            });
        }
        return options;
    }

    /**
     * 获取项目中的caches
     * @return
     */
    public List<Option> getCodeGenCaches() {
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, "codeGen_caches",new IDataLoader() {
            @Override
            public Object load() {
                return getJboltCodeGenCaches();
            }
        });
    }

    public List<Option> getJboltCodeGenCaches() {
        List<Option> options = new ArrayList<>();
        String[] packs = JBoltArrayUtil.from3(JBoltConfig.JBOLT_CODE_GEN_CLASS_SCAN_PACKAGES);
        for(String packageName:packs){
            Set<Class<?>> caches = ClassUtil.scanPackageBySuper(packageName, JBoltCache.class);
            if(caches!=null && caches.size()>0) {
                caches.forEach(en->{
                    options.add(new OptionBean(en.getName()));
                });
            }
        }

        if(options.size()>0){
            //排序
            options.sort(new Comparator<Option>() {
                @Override
                public int compare(Option o1, Option o2) {
                    return o1.getText().compareTo(o2.getText());
                }
            });
        }
        return options;
    }

    /**
     * 获取项目中的Service
     * @return
     */
    public List<Option> getCodeGenServices() {
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, "codeGen_services",new IDataLoader() {
            @Override
            public Object load() {
                return getJboltCodeGenServices();
            }
        });
    }

    public List<Option> getJboltCodeGenServices() {
        List<Option> options = new ArrayList<>();
        String[] packs = JBoltArrayUtil.from3(JBoltConfig.JBOLT_CODE_GEN_CLASS_SCAN_PACKAGES);
        for(String packageName:packs){
            Set<Class<?>> services = ClassUtil.scanPackageBySuper(packageName, IJBoltService.class);
            if(services!=null && services.size()>0) {
                services.forEach(en->{
                    options.add(new OptionBean(en.getName()));
                });
            }
        }
        if(options.size()>0){
            //排序
            options.sort(new Comparator<Option>() {
                @Override
                public int compare(Option o1, Option o2) {
                    return o1.getText().compareTo(o2.getText());
                }
            });
        }
        return options;
    }

    /**
     * 删除codegen用的caches
     */
    public void removeCodeGenCaches() {
        JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, "codeGen_caches");
    }

    /**
     * 删除codegen用的enums
     */
    public void removeCodeGenEnums() {
        JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, "codeGen_enums");
    }

    /**
     * 删除codegen用的services
     */
    public void removeCodeGenServices() {
        JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, "codeGen_services");
    }
}
