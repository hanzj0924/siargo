package cn.jbolt.common.enums;

import cn.jbolt._admin.codegen.CodeGenState;
import cn.jbolt.core.enumutil.JBoltEnum;

public enum WechatAutoreplyType {
    SUBSCRIBE("关注后回复",1),
    KEYWORDS("关键词回复",2),
    DEFAULT("默认回复",3),
    ;
    private String text;
    private int value;
    private WechatAutoreplyType(String text,int value) {
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
        JBoltEnum.addToTvBeanMap(WechatAutoreplyType.class);
    }
}
