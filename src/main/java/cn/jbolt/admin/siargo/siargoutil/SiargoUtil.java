package cn.jbolt.admin.siargo.siargoutil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SiargoUtil {
	
	public static final String PATTERN_DATE_TIME_MONTH = "yyyy-MM-dd";
	public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE_TIME_MILLISECOND = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String EXPORT_FORMAT_DATE_TIME = "yyyyMMdd-hhmmss";

	/**
	 * 判断字符串是否全是数字
	 * @param str
	 * @return boolean
	 */
	public static boolean isNumeric(String str) {
	    return str != null && 
	           !str.isEmpty() && 
	           str.chars().allMatch(Character::isDigit);
	}
	
	/**
     * 将Date转化为指定格式字符串 <br>
     * PATTERN_DATE_TIME_MONTH yyyy-MM-dd;<br>
     * PATTERN_DATE_TIME yyyy-MM-dd HH:mm:ss;<br>
     * PATTERN_DATE_TIME_MILLISECOND yyyy-MM-dd HH:mm:ss.SSS;<br>
     * EXPORT_FORMAT_DATE_TIME yyyyMMdd-hhmmss
     * @param fromFormat
     * @return String
     */
    public static String getDateString(String fromFormat) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(fromFormat));
    }
}
