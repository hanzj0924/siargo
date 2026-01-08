package cn.jbolt.common.util;

import org.slf4j.LoggerFactory;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {

    public final static int DEFAULT_MAX_LENGTH = 100;
    private final static char SPLIT = ',';
    public final static String EMPATY = "";

    private final static HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();

    static {
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public static String getSpells(final String text) {
        return getSpells(text, DEFAULT_MAX_LENGTH);
    }

    public static String getSpells(final String text, final int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() == 0) {
            return EMPATY;
        }
        final char[] arr = text.toCharArray();
        final int len = arr.length;
        final String[] result = new String[len];
        //处理每个字符 放进result
        // 1. null 保留的英文
        // 2. 异常或者无结果为 空字符串
        // 3. 正常时保留第一个拼写        
        for (int i = 0; i < len; i++) {
            char c = arr[i];
            if (c > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat);
                    if (temp != null && temp.length != 0) {
                        result[i] = temp[0];
                    } else {
                        result[i] = EMPATY;
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    result[i] = EMPATY;
                    LoggerFactory.getLogger(PinYinUtil.class).warn("Exception occur when getFullSpell at index" + i + " of " + text, e);
                }
            } else {
                if (c==' ') {
                    arr[i] = '\0'; //NOTICE: 如果是空白符，设置成了 '\0';
                }
                result[i] = null;
            }
        }

        //开始拼结果
        final StringBuilder sb = new StringBuilder(maxLength + 6);
        //处理首字母， 假定不考虑限制的长度
        for (int i = 0; i < len; i++) {
            String string = result[i];
            if (string == null) {
                char c = arr[i];
                //忽略空白符
                if (c != '\0') {
                    sb.append(c);
                }
            } else if (string.length() != 0) {
                sb.append(string.charAt(0));
            }
        }
        //插入分割
        sb.append(SPLIT);
        //
        for (int i = 0; i < len && sb.length() < maxLength; i++) {
            String string = result[i];
            if (string == null) {
                char c = arr[i];
                //忽略空白符
                if (c != '\0') {
                    sb.append(c);
                }
            } else if (string.length() != 0) {
                sb.append(string);
            }
        }

        //导出
        if (sb.length() <= maxLength) {
            return sb.toString();
        } else {
            return sb.substring(0, maxLength);
        }
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变.
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {
        char[] arr = chinese.toCharArray();
        StringBuilder pybf = new StringBuilder(arr.length);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    //e.printStackTrace();
                    LoggerFactory.getLogger(PinYinUtil.class).warn("Exception occur when getFirstSpell of " + chinese, e);
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }

    /**
     * 获取汉字串拼音，英文字符不变.
     *
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    @Deprecated
    public static String getFullSpell(String chinese) {
        if (chinese == null) {
            return null;
        }
        StringBuilder pybf = new StringBuilder();
        char[] arr = chinese.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    LoggerFactory.getLogger(PinYinUtil.class).warn("Exception occur when getFullSpell of " + chinese, e);
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }
    
    public static void main(String[] args) {
        System.out.println(getFirstSpell("中国"));
        System.out.println(getFullSpell("中国"));
        System.out.println(getSpells("中国"));
    }
}
