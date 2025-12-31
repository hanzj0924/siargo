package cn.jbolt._admin.qiniu;

import cn.jbolt._admin.cache.JBoltQiniuCache;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.jbolt.common.model.Qiniu;
import cn.jbolt.common.model.QiniuBucket;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.common.enums.JBoltSystemLogTargetType;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.db.sql.SqlExpress;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.util.JBoltArrayUtil;
import cn.jbolt.core.util.JBoltQiniuUtil;

/**
 * 七牛账号管理 Service
 *
 * @ClassName: QiniuService
 * @author: JBolt-Generator
 * @date: 2021-10-13 22:44
 */
public class QiniuService extends JBoltBaseService<Qiniu> {
    private Qiniu dao = new Qiniu().dao();
    @Inject
    private QiniuBucketService qiniuBucketService;

    @Override
    protected Qiniu dao() {
        return dao;
    }

    /**
     * 后台管理分页查询true
     *
     * @param pageNumber
     * @param pageSize
     * @param keywords
     * @param type
     * @param enable
     * @return
     */
    public Page<Qiniu> paginateAdminDatas(int pageNumber, int pageSize, String keywords, Integer type, Boolean isDefault, Boolean enable) {
        return paginate(selectSql()//拿到新的默认此模块查询语句
                .page(pageNumber, pageSize)//分页
                .eq("type", type)//type条件查询 无需自己if判断null和0 内置自动
                .eq("enable", enable)//enable条件查询 无需判断null 自动处理
                .eq("is_default", isDefault)//eis_default条件查询 无需判断null 自动处理
                .likeMulti(keywords, "name", "sn", "remark", "ak", "sk")//多列like模糊查询 自动处理null
                .orderById(true)//按照id倒序排列
        );
    }


    /**
     * 后台管理分页查询true
     *
     * @param pageNumber
     * @param pageSize
     * @param keywords
     * @param type
     * @param enable
     * @return
     */
    public Page<Record> paginateAdminRecordDatas(int pageNumber, int pageSize, String keywords, Integer type, Boolean enable) {
        Sql sql = selectSql().page(pageNumber, pageSize);
        sql.eq("type", type);
        sql.eq("enable", enable);
        sql.likeMulti(keywords, "name", "sn", "remark", "ak", "sk");
        sql.orderById(true);
        return paginateRecord(sql);
    }

    /**
     * 保存
     *
     * @param qiniu
     * @return
     */
    public Ret save(Qiniu qiniu) {
        if (qiniu == null || isOk(qiniu.getId()) || notOk(qiniu.getType()) || notOk(qiniu.getName()) || notOk(qiniu.getSn()) || notOk(qiniu.getAk()) || notOk(qiniu.getSk())) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        if (exists("name", qiniu.getName())) {
            return fail(JBoltMsg.DATA_SAME_NAME_EXIST);
        }
        if (exists("sn", qiniu.getSn())) {
            return fail(JBoltMsg.DATA_SAME_SN_EXIST);
        }
        if (exists("ak", qiniu.getAk())) {
            return fail("AK[" + qiniu.getAk() + "]已存在");
        }
        if (exists("sk", qiniu.getSk())) {
            return fail("SK[" + qiniu.getSk() + "]已存在");
        }
        qiniu.setEnable(false);
        if (qiniu.getIsDefault() == null) {
            qiniu.setIsDefault(false);
        }
        qiniu.setCreateUserId(JBoltUserKit.getUserId());
        qiniu.setUpdateUserId(JBoltUserKit.getUserId());
        qiniu.setBucketCount(0);
        boolean success = qiniu.save();
        if (success) {
            //如果设置为default 就把其它的已经default的设置为false
            if (qiniu.getIsDefault()) {
                changeColumnFalseByExcludedId(IS_DEFAULT, qiniu.getId());
            }
            //添加日志
            addSaveSystemLog(qiniu.getId(), JBoltUserKit.getUserId(), qiniu.getName());
        }
        return ret(success);
    }

    /**
     * 更新
     *
     * @param qiniu
     * @return
     */
    public Ret update(Qiniu qiniu) {
        if (qiniu == null || notOk(qiniu.getId()) || notOk(qiniu.getType()) || notOk(qiniu.getName()) || notOk(qiniu.getSn()) || notOk(qiniu.getAk()) || notOk(qiniu.getSk())) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        //更新时需要判断数据存在
        Qiniu dbQiniu = findById(qiniu.getId());
        if (dbQiniu == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        if (exists("name", qiniu.getName(), qiniu.getId())) {
            return fail(JBoltMsg.DATA_SAME_NAME_EXIST);
        }
        if (exists("sn", qiniu.getSn(), qiniu.getId())) {
            return fail(JBoltMsg.DATA_SAME_SN_EXIST);
        }
        if (exists("ak", qiniu.getAk(), qiniu.getId())) {
            return fail("AK[" + qiniu.getAk() + "]已存在");
        }
        if (exists("sk", qiniu.getSk(), qiniu.getId())) {
            return fail("SK[" + qiniu.getSk() + "]已存在");
        }
        qiniu.remove("enable", "create_user_id");
        if (qiniu.getIsDefault() == null) {
            qiniu.setIsDefault(false);
        }
        qiniu.setUpdateUserId(JBoltUserKit.getUserId());
        boolean success = qiniu.update();
        if (success) {
            //如果前端传了true 并且数据库里的不是true
            if ((qiniu.getIsDefault() != null && qiniu.getIsDefault()) && !dbQiniu.getIsDefault()) {
                changeColumnFalseByExcludedId(IS_DEFAULT, qiniu.getId());
            }
            //添加日志
            addUpdateSystemLog(qiniu.getId(), JBoltUserKit.getUserId(), qiniu.getName());
        }
        return ret(success);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public Ret delete(Long id) {
        return deleteById(id, true);
    }

    /**
     * 删除数据后执行的回调
     *
     * @param qiniu 要删除的model
     * @param kv    携带额外参数一般用不上
     * @return
     */
    @Override
    protected String afterDelete(Qiniu qiniu, Kv kv) {
        if(qiniu.getIsDefault()){
            JBoltQiniuCache.me.removeDefault();
        }
        JBoltQiniuCache.me.removeDefaultBucket(qiniu.getId());
        addDeleteSystemLog(qiniu.getId(), JBoltUserKit.getUserId(), qiniu.getName());
        return null;
    }

    /**
     * 检测是否可以删除
     *
     * @param qiniu 要删除的model
     * @param kv    携带额外参数一般用不上
     * @return
     */
    @Override
    public String checkCanDelete(Qiniu qiniu, Kv kv) {
        //如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
        return checkInUse(qiniu, kv);
    }

    /**
     * 检测是否可以删除
     *
     * @param qiniu model
     * @param kv    携带额外参数一般用不上
     * @return
     */
    @Override
    public String checkInUse(Qiniu qiniu, Kv kv) {
        //这里用来覆盖 检测Qiniu是否被其它表引用
        boolean hasBucket = qiniuBucketService.checkQiniuHasBucket(qiniu.getId());
        if (hasBucket) {
            return "此账号下已经存在Bucket，不能直接删除";
        }
        return null;
    }


    @Override
    protected String afterToggleBoolean(Qiniu qiniu, String column, Kv kv) {
        switch (column) {
            case "enable":
                addUpdateSystemLog(qiniu.getId(), JBoltUserKit.getUserId(), qiniu.getName(), "的启用状态:" + qiniu.getEnable());
                break;
            case "is_default":
                if (qiniu.getIsDefault()) {
                    changeColumnFalseByExcludedId(IS_DEFAULT, qiniu.getId());
                }
                //删除default缓存
                JBoltQiniuCache.me.removeDefault();
                addUpdateSystemLog(qiniu.getId(), JBoltUserKit.getUserId(), qiniu.getName(), "的是否默认属性:" + qiniu.getIsDefault());
                break;

            default:
                break;
        }
        JBoltQiniuCache.me.removeDefaultBucket(qiniu.getId());
        return null;
    }

    /**
     * 执行七牛账号的bucketCount更新
     *
     * @param qiniuId
     */
    public void updateBucketCount(Long qiniuId) {
        if (isOk(qiniuId)) {
            Sql bucketCountSql = qiniuBucketService.selectSql().count().eq("qiniu_id", qiniuId);
            Sql sql = updateSql().set("bucket_count", new SqlExpress("(" + bucketCountSql.toSql() + ")")).eq(ID, qiniuId);
            update(sql);
        }
    }

    /**
     * 获取默认七牛账号
     *
     * @return
     */
    public Qiniu getDefault() {
        return findFirst(selectSql().eq("is_default", TRUE));
    }

    /**
     * 生成指定bucket的token & region
     *
     * @param bucketSn
     * @return
     */
    public Ret genUploadParas(String bucketSn) {
        Qiniu qiniu = null;
        QiniuBucket qiniuBucket = null;
        //如果没传或者传default 就获取默认default
        if (notOk(bucketSn) || bucketSn.trim().equals("default")) {
            //获取默认七牛账号
            qiniu = JBoltQiniuCache.me.getDefault();
            if (qiniu == null) {
                return fail("未指定bucket的sn将使用默认七牛账号，但系统尚未设置默认七牛账号");
            }
            qiniuBucket = JBoltQiniuCache.me.getDefaultBucket(qiniu.getId());
            if (qiniuBucket == null) {
                return fail("未指定bucket的sn将使用默认七牛账号，但默认七牛账号里尚未设置默认Bucket");
            }
        } else {
            qiniuBucket = JBoltQiniuCache.me.getBucketBySn(bucketSn);
            if (qiniuBucket == null) {
                return fail("指定的bucketSn无效");
            }
            qiniu = JBoltQiniuCache.me.get(qiniuBucket.getQiniuId());
            if (qiniu == null) {
                return fail("未找到指定bucket所属七牛账号信息");
            }
        }
        String domain = qiniuBucket.getDomainUrl();
        if (notOk(domain)) {
            return fail("bucket[" + qiniuBucket.getName() + ":+" + qiniuBucket.getSn() + "]未设置绑定域名");
        }
        if (domain.indexOf(",") != -1) {
            String[] urlArr = JBoltArrayUtil.from(domain, ",");
            if (urlArr == null || urlArr.length == 0) {
                return fail("bucket[" + qiniuBucket.getName() + ":+" + qiniuBucket.getSn() + "]设置绑定域名格式不正确");
            }
            domain = urlArr[0];
        }
        Kv result = Kv.by("token", JBoltQiniuUtil.genToken(qiniu.getAk(), qiniu.getSk(), qiniuBucket.getName()));
        result.set("region", qiniuBucket.getRegion());
        result.set("domain", domain);
        return successWithData(result);
    }

    @Override
    protected int systemLogTargetType() {
        return JBoltSystemLogTargetType.QINIU.getValue();
    }
}