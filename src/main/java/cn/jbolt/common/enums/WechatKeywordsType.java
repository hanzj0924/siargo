package cn.jbolt.common.enums;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 微信关键词匹配方式
 */
public enum WechatKeywordsType {
    LIKE("模糊匹配",1),
    EQUALS("全匹配",2)
    ;
    private String text;
    private int value;
    private WechatKeywordsType(String text,int value) {
        this.text = text;
        this.value = value;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    //加到JBolt Enum管理器
    static {
        JBoltEnum.addToTvBeanMap(WechatKeywordsType.class);
    }
}
