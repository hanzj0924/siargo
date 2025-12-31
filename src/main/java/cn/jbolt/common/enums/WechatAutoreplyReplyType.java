package cn.jbolt.common.enums;

import cn.jbolt.core.enumutil.JBoltEnum;

public enum WechatAutoreplyReplyType {
    RANDOMONE("随机一条",1),
    ALL("全部",2)
    ;
    private String text;
    private int value;
    private WechatAutoreplyReplyType(String text, int value) {
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
        JBoltEnum.addToTvBeanMap(WechatAutoreplyReplyType.class);
    }
}
