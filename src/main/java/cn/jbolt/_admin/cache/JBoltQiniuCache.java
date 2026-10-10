package cn.jbolt._admin.cache;

import cn.jbolt._admin.qiniu.QiniuBucketService;
import cn.jbolt._admin.qiniu.QiniuService;
import cn.jbolt.common.model.Qiniu;
import cn.jbolt.common.model.QiniuBucket;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.cache.JBoltCache;
import cn.jbolt.core.cache.JBoltCacheKit;
import com.jfinal.aop.Aop;
import com.jfinal.plugin.ehcache.IDataLoader;

public class JBoltQiniuCache extends JBoltCache {
    public static final JBoltQiniuCache me = new JBoltQiniuCache();
    QiniuService service = Aop.get(QiniuService.class);
    QiniuBucketService bucketService = Aop.get(QiniuBucketService.class);
    private static final String TYPE_NAME = "qiniu";

    public String getCacheTypeName() {
        return TYPE_NAME;
    }

    /** 通过七牛账号ID 获取七牛账号 */
    public Qiniu get(Long id) {
        return service.findById(id);
    }

    /** 通过七牛账号SN 获取七牛账号 */
    public Qiniu getBySn(String sn) {
        return service.getCacheByKey(sn);
    }

    /** 通过七牛账号ID 获取七牛Name */
    public String getName(Long id) {
        Qiniu qiniu = get(id);
        return qiniu == null ? null : qiniu.getName();
    }

    /** 通过七牛账号SN 获取七牛账号ID */
    public Long getIdBySn(String sn) {
        Qiniu qiniu = getBySn(sn);
        return qiniu == null ? null : qiniu.getId();
    }

    /** 通过七牛账号SN 获取七牛Name */
    public String getNameBySn(String sn) {
        Qiniu qiniu = getBySn(sn);
        return qiniu == null ? null : qiniu.getName();
    }

    /** 获得bucket所在七牛账号 */
    public Qiniu getByBucketSn(String bucketSn) {
        return get(getQiniuIdByBucketSn(bucketSn));
    }

    /** 获得bucket所在七牛账号Id */
    public Long getQiniuIdByBucketSn(String bucketSn) {
        QiniuBucket qiniuBucket = getBucketBySn(bucketSn);
        return qiniuBucket == null ? null : qiniuBucket.getQiniuId();
    }

    /** 根据bucket Id获取qiniubucket */
    public QiniuBucket getBucket(Long bucketId) {
        return bucketService.findById(bucketId);
    }

    /** 根据bucket sn获取qiniubucket */
    public QiniuBucket getBucketBySn(String bucketSn) {
        return bucketService.getCacheByKey(bucketSn);
    }

    /** 获取默认七牛账号 */
    public Qiniu getDefault() {
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(Qiniu.class, "default_"),
                new IDataLoader() {
                    @Override
                    public Object load() {
                        return service.getDefault();
                    }
                });
    }

    /** 删除默认七牛账号 */
    public void removeDefault() {
        JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(Qiniu.class, "default_"));
    }

    /** 获取指定七牛账号下的默认bucket */
    public QiniuBucket getDefaultBucket(Long qiniuId) {
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME,
                buildCacheKey(Qiniu.class, "default_bucket_", qiniuId.toString()), new IDataLoader() {
                    @Override
                    public Object load() {
                        return bucketService.getQiniuBucketDefault(qiniuId);
                    }
                });
    }

    /** 删除默认七牛bucket */
    public void removeDefaultBucket(Long qiniuId) {
        JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME,
                buildCacheKey(Qiniu.class, "default_bucket_", qiniuId.toString()));
    }

    /** 据七牛SN 获取指定七牛账号下的默认bucket 根 */
    public QiniuBucket getDefaultBucketByQiniuSn(String sn) {
        return getDefaultBucket(getIdBySn(sn));
    }
}
