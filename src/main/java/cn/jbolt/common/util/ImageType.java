package cn.jbolt.common.util;
public enum ImageType { 
     JPG("jpg"), PNG("png"), JPEG("jpeg"), GIF("gif"), BMP("bmp"); 
     private final String value; 
     private static final String[] types= {"jpg","png","jpeg","gif","bmp"};
     private ImageType(String type) { 
            this.value = type; 
     } 
     public boolean eq(String type) {
    	 return type!=null&&this.value.equals(type);
     }
     public static boolean isImage(String type) {
    	 return ArrayUtil.contains(types, type);
     }
     public static boolean notImage(String type) {
    	 return ArrayUtil.contains(types, type)==false;
     }
}