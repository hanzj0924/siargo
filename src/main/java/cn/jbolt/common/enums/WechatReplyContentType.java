package cn.jbolt.common.enums;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 微信公众号被动回复类型
 */
public enum WechatReplyContentType {
    NEWS("图文","news"),
    TEXT("文本","text"),
    IMG("图片","image"),
    VOICE("音频","voice"),
    VIDEO("视频","video"),
    MUSIC("音乐","music")
    ;
    private String text;
    private String value;
    private WechatReplyContentType(String text,String value) {
        this.text = text;
        this.value = value;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    //加到JBolt Enum管理器
    static {
        JBoltEnum.addToTvBeanMap(WechatReplyContentType.class);
    }
}
