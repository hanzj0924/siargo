package cn.jbolt._admin.cache;

import cn.jbolt.admin.wechat.autoreply.WechatReplyContentService;
import cn.jbolt.common.model.WechatReplyContent;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.cache.JBoltCache;
import cn.jbolt.core.cache.JBoltCacheKit;
import cn.jbolt.core.cache.JBoltWechatConfigCache;
import com.jfinal.aop.Aop;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.jfinal.weixin.sdk.msg.out.OutMsg;

/**
 * 微信自动回复内容cache
 */
public class JBoltWechatReplyContentCache extends JBoltCache {
    public static final JBoltWechatReplyContentCache me = new JBoltWechatReplyContentCache();
    WechatReplyContentService service = Aop.get(WechatReplyContentService.class);
    private static final String TYPE_NAME = "wechat_reply_content";
    private static final String PRE_API = "api_";
    public String getCacheTypeName() {
        return TYPE_NAME;
    }



    /**
     * 从cache获取到公众平台默认回复
     *
     * @return
     */
    public OutMsg getWechcatDefaultOutMsg(String appId, String openId) {
        if (StrKit.isBlank(appId)) {
            return null;
        }
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(WechatReplyContent.class,"defaultmsg_", appId), new IDataLoader() {
            @Override
            public Object load() {
                return service.getWechcatDefaultOutMsg(appId, openId);
            }
        });
    }

    /**
     * 从cache获取到公众平台关注回复
     *
     * @param appId
     * @param openId
     * @return
     */
    public OutMsg getWechcatSubscribeOutMsg(String appId, String openId) {
        if (StrKit.isBlank(appId)) {
            return null;
        }
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(WechatReplyContent.class,"subscribemsg_", appId), new IDataLoader() {
            @Override
            public Object load() {
                return service.getWechcatSubscribeOutMsg(appId, openId);
            }
        });
    }

    /**
     * 删除微信公众平台默认自动回复消息
     *
     * @param mpId
     */
    public void removeWechcatDefaultOutMsg(Long mpId) {
        removeWechcatDefaultOutMsgByAppId(JBoltWechatConfigCache.me.getAppId(mpId));
    }

    /**
     * 删除微信公众平台关注自动回复消息
     *
     * @param mpId
     */
    public void removeWechcatSubscribeOutMsg(Long mpId) {
        removeWechcatSubscribeOutMsg(JBoltWechatConfigCache.me.getAppId(mpId));
    }

    /**
     * 删除微信公众平台关注自动回复消息
     *
     * @param appId
     */
    public void removeWechcatSubscribeOutMsg(String appId) {
        if (StrKit.notBlank(appId)) {
            JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(WechatReplyContent.class,"subscribemsg_", appId));
        }
    }

    /**
     * 删除微信公众平台默认自动回复消息
     *
     * @param appId 公众号APPID
     */
    public void removeWechcatDefaultOutMsgByAppId(String appId) {
        if (StrKit.notBlank(appId)) {
            JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(WechatReplyContent.class,"defaultmsg_", appId));
        }
    }
}
