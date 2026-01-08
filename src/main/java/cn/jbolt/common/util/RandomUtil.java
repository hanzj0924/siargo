package cn.jbolt.common.util;

import java.util.Random;

public class RandomUtil {
    private static final Random random = new Random();
    /**
     * 随机生成大写字母和数字混合的指定长度字符串
     * @param length
     * @return
     */
    public static String random(int length) { // length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /**
     * 生成指定长度的字符串 只有小写字母
     * @param length
     * @return
     */
    public static String randomLow(int length) { // length表示生成字符串的长度生成小写字母sn
    	String base = "abcdefghijklmnqstuwxyz";
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < length; i++) {
    		int number = random.nextInt(base.length());
    		sb.append(base.charAt(number));
    	}
    	return sb.toString();
    }
    /**
     * 生成指定长度的字符串 小写的带数字
     * @param length
     * @return
     */
    public static String randomLowWithNumber(int length) {
    	String base = "abcdefghijklmnqstuwxyz0123456789";
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < length; i++) {
    		int number = random.nextInt(base.length());
    		sb.append(base.charAt(number));
    	}
    	return sb.toString();
    }
    /**
     * 生成指定长度数字随机串
     * @param length
     * @return
     */
    public static Integer randomNumberToInt(int length) { // length表示生成字符串的长度生成小写字母sn
    	return Integer.parseInt(randomNumber(length));
    }
    /**
     * 生成指定长度数字随机串
     * @param length
     * @return
     */
    public static String randomNumber(int length) { // length表示生成字符串的长度生成小写字母sn
    	String base = "123456789";
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < length; i++) {
    		int number = random.nextInt(base.length());
    		sb.append(base.charAt(number));
    	}
    	return sb.toString();
    }

}
