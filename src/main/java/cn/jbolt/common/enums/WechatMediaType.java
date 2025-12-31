package cn.jbolt.common.enums;

import cn.jbolt.core.enumutil.JBoltEnum;

/**
 * 微信素材类型
 */
public enum WechatMediaType {
   NEWS("图文","news"),
   IMG("图片","image"),
   VIDEO("视频","video"),
   VOICE("音频","voice")
   ;
   private String text;
   private String value;
   private WechatMediaType(String text,String value) {
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
      JBoltEnum.addToTvBeanMap(WechatMediaType.class);
   }
}
