package cn.jbolt._admin.cache;
import cn.jbolt.admin.wechat.user.WechatUserService;
import cn.jbolt.common.model.WechatUser;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.bean.Option;
import cn.jbolt.core.bean.OptionBean;
import cn.jbolt.core.cache.JBoltCache;
import cn.jbolt.core.cache.JBoltCacheKit;
import cn.jbolt.core.util.JBoltArrayUtil;
import com.jfinal.aop.Aop;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信用户
 * 缓存工具类
 */
public class JBoltWechatUserCache extends JBoltCache {
    public static final JBoltWechatUserCache me = new JBoltWechatUserCache();
    WechatUserService service = Aop.get(WechatUserService.class);
    private static final String TYPE_NAME = "wechat_user";
    private static final String PRE_API = "api_";
    public String getCacheTypeName() {
        return TYPE_NAME;
    }
    /**
     * 获取默认头像
     * @return
     */
    public String getDefaultAvatar(){
        return JBoltConfig.JBOLT_WECHAT_USER_DEFAULT_AVATAR;
    }


    /**
     * 根据多个ID拼接字符串查询列表
     * @param mpId
     * @param ids
     * @return
     */
    public List<Option> getApiWechatUserOptionsByIdsStr(Long mpId, String ids){
        if(hasNotOk(mpId,ids)){return null;}
        return getApiWechatUserOptionsByIds(mpId, JBoltArrayUtil.from3(ids));
    }

    /**
     * 根据多个ID拼接字符串查询列表
     * @param mpId
     * @param ids
     * @return
     */
    public List<WechatUser> getApiWechatUsersByIdsStr(Long mpId,String ids){
        if(hasNotOk(mpId,ids)){return null;}
        return getApiWechatUsersByIds(mpId, JBoltArrayUtil.from3(ids));
    }


    /**
     * 根据多个ID查询列表
     * @param mpId
     * @param idArray
     * @return
     */
    public List<Option> getApiWechatUserOptionsByIds(Long mpId, String[] idArray){
        if(hasNotOk(mpId,idArray)){return null;}
        List<Option> users = new ArrayList<>();
        WechatUser wechatUser;
        for(String id:idArray){
            wechatUser = getApiWechatUserByApiUserId(mpId,id);
            if(wechatUser!=null){
                users.add(new OptionBean(wechatUser.getNickname(),id));
            }
        }
        return users;
    }

    /**
     * 根据多个ID查询列表
     * @param mpId
     * @param idArray
     * @return
     */
    public List<WechatUser> getApiWechatUsersByIds(Long mpId,String[] idArray){
        if(hasNotOk(mpId,idArray)){return null;}
        List<WechatUser> users = new ArrayList<>();
        WechatUser wechatUser;
        for(String id:idArray){
            wechatUser = getApiWechatUserByApiUserId(mpId,id);
            if(wechatUser!=null){
                users.add(wechatUser);
            }
        }
        return users;
    }


    /**
     * 从缓存里获取wechatUser
     * @param mpId
     * @param id
     * @return
     */
    public WechatUser getApiWechatUserByApiUserId(Long mpId, Object id) {
        if(mpId == null || id == null || mpId <=0 || id.toString().equals("0")){return null;}
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(WechatUser.class,PRE_API,mpId.toString(),id.toString()), new IDataLoader() {
            @Override
            public Object load() {
                WechatUser wechatUser = service.findByIdToWechatUser(mpId, id);
                if (wechatUser != null) {
                    wechatUser.removeNullValueAttrs().remove("last_auth_time","first_auth_time","first_login_time","last_login_time","checked_time","remark","group_id","tag_ids","subscribe_scene","qr_scene","qr_scene_str","check_code","session_key","update_time");
                }
                return wechatUser;
            }
        });
    }

    /**
     * 删除wechatUser
     * @param mpId
     * @param id
     */
    public void removeApiWechatUser(Long mpId, Long id) {
        if(mpId == null || id == null || mpId <=0 || id <=0){return;}
        JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(WechatUser.class,PRE_API,mpId.toString(),id.toString()));
    }

    /**
     * 从缓存里获取wechatUser 昵称
     * @param mpId
     * @param id
     * @return
     */
    public String getNickName(Long mpId, Long id) {
        if(mpId == null || id == null || mpId <=0 || id <=0){return null;}
        WechatUser apiUser = getApiWechatUserByApiUserId(mpId, id);
        return apiUser==null?null:apiUser.getNickname();
    }

    /**
     * 获取openId
     * @param mpId
     * @param id
     * @return
     */
    public String getOpenId(Long mpId, Long id) {
        if(mpId == null || id == null || mpId <=0 || id <=0){return null;}
        WechatUser apiUser = getApiWechatUserByApiUserId(mpId, id);
        return apiUser==null?null:apiUser.getOpenId();
    }

    /**
     * 获取unionId
     * @param mpId
     * @param id
     * @return
     */
    public String getUnionId(Long mpId, Long id) {
        if(mpId == null || id == null || mpId <=0 || id <=0){return null;}
        WechatUser apiUser = getApiWechatUserByApiUserId(mpId, id);
        return apiUser==null?null:apiUser.getUnionId();
    }

    /**
     * 从缓存里获取wechatUser 头像
     * @param mpId
     * @param id
     * @return
     */
    public String getAvatar(Long mpId, Long id) {
        if(mpId == null || id == null || mpId <=0 || id <=0){return null;}
        WechatUser apiUser = getApiWechatUserByApiUserId(mpId, id);
        if(apiUser == null){return getDefaultAvatar();}
        String userAvatar = apiUser.getHeadImgUrl();
        return (userAvatar==null|| userAvatar.trim().isEmpty())?getDefaultAvatar():userAvatar;
    }





    /**
     * 根据mpid和openId拿到wechat用户信息 api专用
     * @param mpId
     * @param openId
     * @return
     */
    public WechatUser getApiWechatUserByMpOpenId(Long mpId, String openId) {
        if (mpId == null || StrKit.isBlank(openId)) {
            return null;
        }
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME,buildCacheKey(WechatUser.class,"api_byopenid_", mpId.toString() , openId), new IDataLoader() {
                    @Override
                    public Object load() {
                        return service.getApiWechatUserByOpenId(mpId, openId);
                    }
                });
    }

    /**
     * 根据mpid和openId删除wechat用户信息 api专用
     *
     * @param mpId
     * @param openId
     */
    public void removeApiWechatUserByMpOpenId(Long mpId, String openId) {
        if (mpId != null && StrKit.notBlank(openId)) {
            JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(WechatUser.class,"api_byopenid_", mpId.toString() , openId));
        }
    }


    /**
     * 根据mpid和unionId拿到wechat用户信息 api专用
     *
     * @param mpId
     * @param unionId
     * @return
     */
    public WechatUser getApiWechatUserByMpUnionId(Long mpId, String unionId) {
        if (mpId == null || StrKit.isBlank(unionId)) {
            return null;
        }
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME,
                buildCacheKey(WechatUser.class,"api_byunionid_", mpId.toString(), unionId), new IDataLoader() {
                    @Override
                    public Object load() {
                        return service.getApiWechatUserByUnionId(mpId, unionId);
                    }
                });
    }

    /**
     * 根据mpid和unionId删除wechat用户信息 api专用
     *
     * @param mpId
     * @param unionId
     */
    public void removeApiWechatUserByMpUnionId(Long mpId, String unionId) {
        if (mpId != null && StrKit.notBlank(unionId)) {
            JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME,
                    buildCacheKey(WechatUser.class,"api_byunionid_", mpId.toString(), unionId));
        }
    }

    /**
     * 是否enable
     * @param mpId
     * @param id
     * @return
     */
    public Boolean isEnable(Long mpId, Long id) {
        if(mpId == null || id == null || mpId <=0 || id <=0){return false;}
        WechatUser user = getApiWechatUserByApiUserId(mpId, id);
        return user != null && user.getEnable();
    }

}
