package cn.jbolt.common.util;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.jfinal.kit.StrKit;

/**
 * 日期工具类
 * @author Michael
 *
 */
public class DateUtil {
    public static final String YMDE = "yyyy-MM-dd EEEE";
    public static final String YMD = "yyyy-MM-dd";
    public static final String HMS = "HH:mm:ss";
    public static final String HM = "HH:mm";
    public static final String YMDHMSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YMDHMSS2 = "yyyyMMddHHmmssSSS";
    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMDHM = "yyyy-MM-dd HH:mm";
    public static final String MDHM = "MM-dd HH:mm";
    public static final String MD = "MM-dd";
    /**
     * 取小值
     */
    public static final int FLOOR=1;
    /**
     * 取大值
     */
    public static final int CEIL=2;
    /**
     * 四舍五入
     */
    public static final int ROUND=3;
    
    
    public static Date getNow(){
    	return Calendar.getInstance().getTime();
    }
    public static String getNowStr(){
    	return format(getNow(), YMD);
    }
    public static String getNowStr(String pattern){
       return format(getNow(), pattern);
    }

    public static String format(Date date, String pattern) {
    	if (date == null) {
    		return "";
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("",Locale.CHINA);
    	sdf.applyPattern(pattern);
    	return sdf.format(date);
    }
    public static String formatWithT(Date date, String pattern) {
      String value=format(date, pattern);
      if(StrKit.isBlank(value)) {return "";}
      return value.replace(" ", "T");
    }

    public static Date getNowDate() {
        return new Date();
    }

    public static int thisMonthMaxDate(Date date){
    	Calendar cal = Calendar.getInstance(); 
    	cal.clear();
    	cal.setTime(date);
    	return cal.getActualMaximum(Calendar.DATE);
    }
    

    /**
     * 设置之间为0时0分0秒
     * @param date
     * @return
     */
    public static Date HHmmssTo000000(Date date) {
    	if(date==null) {return null;}
    	Calendar cal = getCalendarByDate(date);
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	return cal.getTime();
    }
    /**
     * 设置之间为0时0分0秒
     * @param date
     * @return
     */
    public static String HHmmssTo000000Str(Date date) {
    	if(date==null) {return null;}
        Calendar cal = getCalendarByDate(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return format(cal.getTime(), YMDHMS);
    }

    /**
     * 设置之间为23时59分59秒
     * @param date
     * @return
     */
    public static Date HHmmssTo235959(Date date) {
    	if(date==null) {return null;}
    	Calendar cal = getCalendarByDate(date);
    	cal.set(Calendar.HOUR_OF_DAY, 23);
    	cal.set(Calendar.MINUTE, 59);
    	cal.set(Calendar.SECOND, 59);
    	cal.set(Calendar.MILLISECOND, 999);
    	return cal.getTime();
    }
    /**
     * 设置之间为23时59分59秒
     * @param date
     * @return
     */
    public static String HHmmssTo235959Str(Date date) {
    	if(date==null) {return null;}
        Calendar cal = getCalendarByDate(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return format(cal.getTime(), YMDHMS);
    }

    /**
     * 判断是否是星期一
     * 
     * @param date
     * @return
     */
    public static boolean isMonday(Date date) {
        return getCalendarByDate(date).get(Calendar.DAY_OF_WEEK) == 2;
    }

    /**
     * 判断是否是一个月的第一天
     * 
     * @param date
     * @return
     */
    public static boolean isFirstDayOfTheMonth(Date date) {
        return isFirstDayOfTheMonth(getCalendarByDate(date));
    }

    /**
     * 判断是否是一个月的第一天
     * 
     * @param date
     * @return
     */
    public static boolean isFirstDayOfTheMonth(Calendar date) {
        return date.get(Calendar.DAY_OF_MONTH) == 1;
    }

    /**
     * 判断是否是一个季度的第一天
     * 
     * @param date
     * @return
     */
    public static boolean isFirstDayOfTheQuarter(Date date) {
        Calendar cal = getCalendarByDate(date);
        int month = cal.get(Calendar.MONTH) + 1;
        if (month != 1 && month != 4 && month != 7 && month != 10) {
            return false;
        }
        return isFirstDayOfTheMonth(date);
    }

    /**
     * 判断是否是当前年的第一天
     * @param date
     * @return
     */
    public static boolean isFirstDayOfTheYear(Date date) {
        Calendar cal = getCalendarByDate(date);
        int month = cal.get(Calendar.MONTH) + 1;// 得到月份
        return month == 1 && isFirstDayOfTheMonth(date);
    }

    /**
     * 得到季度
     * @param cal
     * @return
     */
    public static int getQuarterNumber(Calendar cal) {
        int month = cal.get(Calendar.MONTH) + 1;
        if (month >= 1 && month <= 3) {
            return 1;
        }
        if (month >= 4 && month <= 6) {
            return 2;
        }
        if (month >= 7 && month <= 9) {
            return 3;
        }
        if (month >= 10 && month <= 12) {
            return 4;
        }
        return 0;
    }
    
    public static Calendar getCalendarByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(date);
        return cal;
    }
    /**
     * 上周周一
     * @param date
     * @return
     */
    public static Date lastWeekMonday(Date date) {
    	Date thisWeekMonday=thisWeekMonday(date);
        Calendar cal = getCalendarByDate(thisWeekMonday);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        return cal.getTime();
    }
    public static String getWeek(Date date){
    	SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
    	return dateFm.format(date);
    }
    public static String getWeek2(Date date){
    	String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
        	w = 0;
        }
        return weekDays[w];
    }
    /**
     * 上周星期天
     * @param date
     * @return
     */
    public static Date lastWeekSunday(Date date) {
    	Date thisWeekSunday=thisWeekSunday(date);
        Calendar cal = getCalendarByDate(thisWeekSunday);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        return cal.getTime();
    }

    /**
     * 上个月第一天
     * @param date
     * @return
     */
    public static Date lastMonthFirstDay(Date date) {
        Calendar cal = getCalendarByDate(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 上个月最后一天
     * @param date
     * @return
     */
    public static Date lastMonthLastDay(Date date) {
        Calendar cal = getCalendarByDate(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        return cal.getTime();
    }

    /**
     * 上个季度第一个月
     * @param date
     * @return
     */
    public static int lastQuarterFirstMonth(Date date) {
        Calendar cal = getCalendarByDate(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
        return cal.get(Calendar.MONTH);
    }

    /**
     * 上个季度最后一个月
     * @param date
     * @return
     */
    public static int lastQuarterLastMonth(Date date) {
        Calendar cal = getCalendarByDate(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        return cal.get(Calendar.MONTH);
    }
    
    /**
     * 昨天
     * @return
     */
    public static Calendar getYesterdayCalendar() {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.DATE, -1);
    	return c;
    }

    /**
     * 昨天
     * @return
     */
    public static Date getYesterday() {
        return getYesterdayCalendar().getTime();
    }

    /**
     * 判断是否是昨天
     * 
     * @param finishTime
     * @return
     */
    public static boolean isYesterday(Date finishTime) {
        Calendar yesterday = DateUtil.getYesterdayCalendar();
        Calendar finishday = Calendar.getInstance();
        finishday.setTime(finishTime);
        boolean result = yesterday.get(Calendar.YEAR) == finishday
                .get(Calendar.YEAR)
                && yesterday.get(Calendar.MONTH) == finishday
                        .get(Calendar.MONTH)
                && yesterday.get(Calendar.DATE) == finishday.get(Calendar.DATE);
        return result;
    }

    /**
     * 判断不是今天
     * 
     * @param c
     * @return
     */
    public static boolean isNotToday(Calendar c) {
        Calendar today = Calendar.getInstance();
        boolean result = today.get(Calendar.YEAR) != c.get(Calendar.YEAR)
                || today.get(Calendar.MONTH) != c.get(Calendar.MONTH)
                || today.get(Calendar.DATE) != c.get(Calendar.DATE);
        return result;
    }

    /**
     * 本周一
     * @param date
     * @return
     */
    public static Date thisWeekMonday(Date date) {
    	if(isMonday(date)){return date;}
    	Calendar c = getCalendarByDate(date);
    	 while (true) {
             c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
             if (isMonday(c.getTime())) {
                 return c.getTime();
             }
         }
    }
    /**
     * 本周日
     * @param date
     * @return
     */
    public static Date thisWeekSunday(Date date) {
    	if(isSunday(date)){return date;}
    	Calendar c = getCalendarByDate(date);
    	 while (true) {
             c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
             if (isSunday(c.getTime())) {
                 return c.getTime();
             }
         }
    }

    /**
     * 昨天
     * @param c
     * @return
     */
    public static Calendar getYesterday(Calendar c) {
        Calendar c2 = getCalendarByDate(c.getTime());
        c2.add(Calendar.DATE, -1);
        return c2;
    }

    /**
     * 本年第一天
     * @param date
     * @return
     */
    public static Date thisYearFirstDay(Date date) {
    	Calendar c = getCalendarByDate(date);
    	c.set(Calendar.MONTH, 0);
    	c.set(Calendar.DATE, 1);
    	return c.getTime();
    }
    /**
     * 本年最后一天
     * @param date
     * @return
     */
    public static Date thisYearLastDay(Date date) {
    	Calendar c = getCalendarByDate(date);
    	c.set(Calendar.MONTH, 11);
    	c.set(Calendar.DATE, 31);
    	return c.getTime();
    }
    /**
     * 本月第一天
     * @param date
     * @return
     */
    public static Date thisMonthFirstDay(Date date) {
    	Calendar c = getCalendarByDate(date);
    	c.set(Calendar.DATE, 1);
    	return c.getTime();
    }
    /**
     * 本季度第一天
     * @param date
     * @return
     */
    public static Date thisQuarterFirstDay(Date date) {
    	int firstMonth=thisQuarterFirstMonth(date);
        Calendar c = getCalendarByDate(date);
        c.set(Calendar.MONTH, firstMonth-1);
        c.set(Calendar.DATE, 1);
        return c.getTime();
    }

    /**
     * 本季度第一个月
     * @param date
     * @return
     */
    public static int thisQuarterFirstMonth(Date date) {
        Calendar c = getCalendarByDate(date);
        int q=getQuarterNumber(c);
        int month=1;
        switch (q) {
        case 1:
        	month=1;
        	break;
        case 2:
        	month=4;
        	break;
        case 3:
        	month=7;
        	break;
		case 4:
			month=10;
			break;
		}
        return month;
    }

    /**
     * 本季度最后一个月
     * @param date
     * @return
     */
    public static int thisQuarterLastMonth(Date date) {
            Calendar c = getCalendarByDate(date);
            int q=getQuarterNumber(c);
            int month=1;
            switch (q) {
            case 1:
            	month=3;
            	break;
            case 2:
            	month=6;
            	break;
            case 3:
            	month=9;
            	break;
    		case 4:
    			month=12;
    			break;
    		}
            return month;
        }

    /**
     * 周末
     * @param calWhichDay
     * @return
     */
    public static boolean isSunday(Date calWhichDay) {
        return getCalendarByDate(calWhichDay).get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    /**
     * 月最后一天
     * @param calWhichDay
     * @return
     */
    public static boolean isLastDayOfTheMonth(Date calWhichDay) {
        Calendar c = getCalendarByDate(calWhichDay);
        c.add(Calendar.DATE, 1);
        if (c.get(Calendar.DATE) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 季度最后一天
     * @param calWhichDay
     * @return
     */
    public static boolean isLastDayOfTheQuarter(Date calWhichDay) {
        if (!isLastDayOfTheMonth(calWhichDay)) {
            return false;
        }
        int currentMonth = calWhichDay.getMonth() + 1;
        if (currentMonth == 3 || currentMonth == 6 || currentMonth == 9
                || currentMonth == 12) {
            return true;
        }
        return false;
    }

    /**
     * 年最后一天
     * @param calWhichDay
     * @return
     */
    public static boolean isLastDayOfTheYear(Date calWhichDay) {
        Calendar c = getCalendarByDate(calWhichDay);
        if (c.get(Calendar.MONTH) + 1 == 12 && c.get(Calendar.DATE) == 31) {
            return true;
        }
        return false;
    }

    /**
     * 获得一年前的时间
     * 
     * @param now
     * @return
     */
    public static Date get365Before(Date now) {
        Calendar cal = getCalendarByDate(now);
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }

    /**
     * 指定年龄开始日期
     * @param age
     * @return
     */
    public static String getByAgeStart(int age) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MONTH, 0);
        now.set(Calendar.DATE, 1);
        now.add(Calendar.YEAR, -age);
        return format(now.getTime(), "yyyy-MM-dd");
    }
    /**
     * 指定年龄结束日期
     * @param age
     * @return
     */
    public static String getByAgeEnd(int age) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MONTH, 11);
        now.set(Calendar.DATE, 31);
        now.add(Calendar.YEAR, -age);
        return format(now.getTime(), "yyyy-MM-dd");
    }
    /**
     * 将字符串转为时间
     * @param date
     * @param format
     * @return
     */
    public static Calendar getFromStr(String date,String format){
        Calendar cal=Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return cal;
    }
    public static Date toDate(Date date){
    	return date;
    }
    public static Date toDate(String date){
    	return getDate(date);
    }
    public static Date toDate(String date,String pattern){
    	return getDate(date,pattern);
    }
    /**
     * 将字符串转为时间
     * @param date
     * @param format
     * @return
     */
    public static Date getDate(String date){
    	if(date==null||date.isEmpty()){
    		return null;
    	}
        String pattern = null ;
        
       //TODO 换成使用正则表达式 对常用的日期类型都做支持
        if(date.contains("T")){
        	date=date.replaceAll("T", " ");
        }
        if (date.contains(":")) {
            if(StringUtil.count(date, ':')==2){
                pattern=DateUtil.YMDHMS;
               }else if(StringUtil.count(date, ':')==1){
                   pattern=DateUtil.YMDHM;
               }
        } else {
              pattern=DateUtil.YMD; 
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将字符串转为时间
     * @param date
     * @param format
     * @return
     */
    public static Date getDate(String date,String format){
    	if(StrKit.isBlank(date)) {
    		return null;
    	}
    	 if(date.contains("T")){
         	date=date.replaceAll("T", " ");
         }
        if(format==null){
            return getDate(date);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 得到几天后的时间
     * @param minute
     * @return
     */
    public static Date getDaysAfter(Date date,int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }
    /**
     * 得到几天后的时间
     * @param minute
     * @return
     */
    public static Date getDaysAfter(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        return calendar.getTime();
    }
    /**
     * 获得几分钟后的时间
     * @param minute
     * @return
     */
    public static Date getMinutesAfter(int minute) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MINUTE, minute);
    	return calendar.getTime();
    }
    /**
     * 获得几秒后的时间
     * @param seconds
     * @return
     */
    public static Date getSecondsAfter(int seconds) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.SECOND, seconds);
    	return calendar.getTime();
    }
    /**
     * 获得几秒前的时间
     * @param seconds
     * @return
     */
    public static Date getSecondsBefore(int seconds) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.SECOND, -seconds);
    	return calendar.getTime();
    }
    /**
     * 获得几秒后的时间
     * @param date
     * @param seconds
     * @return
     */
    public static Date getSecondsAfter(Date date,int seconds) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(Calendar.SECOND, seconds);
    	return calendar.getTime();
    }
    /**
     * 获得几秒前的时间
     * @param date
     * @param seconds
     * @return
     */
    public static Date getSecondsBefore(Date date,int seconds) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(Calendar.SECOND, -seconds);
    	return calendar.getTime();
    }
    
    /**
     * 获得几分钟后的时间
     * @param date
     * @param minute
     * @return
     */
    public static Date getMinutesAfter(Date date,int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }
    
    
    /**
     * 获得几小时后的时间
     * @param minute
     * @return
     */
    public static Date getHourAfter(int hour) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.HOUR_OF_DAY, hour);
    	return calendar.getTime();
    }

    /**
     * 获得几小时后的时间
     * @param date
     * @param minute
     * @return
     */
    public static Date getHourAfter(Date date,int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }
    /**
     * 得到几天后的时间
     * @param minute
     * @return
     */
    public static Date getDaysAgo(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -day);
        return calendar.getTime();
    }
    /**
     * 得到几天后的时间
     * @param minute
     * @return
     */
    public static Date getDaysAgo(Date date,int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -day);
        return calendar.getTime();
    }
    /**
     * 获得几分钟前的时间
     * @param minute
     * @return
     */
    public static Date getMinutesAgo(int minute) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MINUTE, -minute);
    	return calendar.getTime();
    }
    /**
     * 获得几分钟前的时间
     * @param date
     * @param minute
     * @return
     */
    public static Date getMinutesAgo(Date date,int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -minute);
        return calendar.getTime();
    }
    

    /**
     * 获得几小时前的时间
     * @param minute
     * @return
     */
    public static Date getHourAgo(int hour) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.HOUR_OF_DAY, -hour);
    	return calendar.getTime();
    }
    /**
     * 获得几小时前的时间
     * @param date
     * @param minute
     * @return
     */
    public static Date getHourAgo(Date date,int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -hour);
        return calendar.getTime();
    }

    /**
     * 判断两个日期是否同一天
     * @param a
     * @param b
     * @return
     */
    public static boolean isSameDay(Calendar a, Calendar b) {
        if(a.get(Calendar.YEAR)!=b.get(Calendar.YEAR)){
            return false;
        }
        if(a.get(Calendar.MONTH)!=b.get(Calendar.MONTH)){
            return false;
        }
        if(a.get(Calendar.DATE)!=b.get(Calendar.DATE)){
            return false;
        }
        return true;
    }
    /**
     * 是前天
     * @param calWhichDay
     * @return
     */
    public static boolean isTheDayBeforeYesterday(Date calWhichDay) {
        Calendar c=Calendar.getInstance();
        c.setTime(calWhichDay);
        
        Calendar c2=Calendar.getInstance();
        c2.add(Calendar.DATE, -2);
        
        boolean result = c.get(Calendar.YEAR)==c2.get(Calendar.YEAR)&&
                c.get(Calendar.MONTH)==c2.get(Calendar.MONTH)&&
                c.get(Calendar.DATE)==c2.get(Calendar.DATE);
        
        return result;
    }
    /**
     * 昨天或更近的时间
     * @param calWhichDay
     * @return
     */
    public static boolean isYesterdayOrMoreRecent(Date calWhichDay) {
        return DateUtil.HHmmssTo000000(calWhichDay).compareTo(DateUtil.HHmmssTo000000(DateUtil.getYesterdayCalendar().getTime()))>=0;
    }
    /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int daysBetween(Date aDate,Date bDate){    
       Long time=(bDate.getTime()-aDate.getTime())/(1000*3600*24);
       return time.intValue();           
    }
    /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int daysBetween(Date aDate,Date bDate,int mode){ 
       double time=(bDate.getTime()-aDate.getTime())*1.0/(1000*3600*24);
       if(mode==FLOOR){
    	   return (int) Math.floor(time);
       }else if(mode==CEIL){
    	   return (int)Math.ceil(time);
       }else if(mode==ROUND){
    	   return (int)Math.round(time);
       }
       return (int)time;
    }
    /**
     * 本月最后一天
     * @param date
     * @return
     */
    public static Date thisMonthLastDay(Date date){
    	Calendar c = getCalendarByDate(nextMonthFirstDay(date));
    	c.add(Calendar.DAY_OF_MONTH, -1);
    	return c.getTime();
    }
    /**
	 * 本季度最后一天
	 * @param date
	 * @return
	 */
	public static Date thisQuarterLastDay(Date date){
		int lastMonth=thisQuarterLastMonth(date);
		Calendar c = getCalendarByDate(date);
		c.set(Calendar.MONTH, lastMonth-1);
		return thisMonthLastDay(c.getTime());
	}
	/**
	 * 下个月的第一天
	 * @param date
	 * @return
	 */
	public static Date nextMonthFirstDay(Date date){
		Calendar c = getCalendarByDate(date);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DATE, 1);
		return c.getTime();
	}
	/**
	 * 明天
	 * @param date
	 * @return
	 */
	public static Date tomorrow(Date date){
		Calendar c = getCalendarByDate(date);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}
	/**
	 * 昨天
	 * @param date
	 * @return
	 */
	public static Date yesterday(Date date){
		 Calendar c = getCalendarByDate(date);
	     c.add(Calendar.DATE, -1);
	     return c.getTime();
	}
	
	/**
	 * 下周一
	 * @param date
	 * @return
	 */
	public static Date nextWeekMonday(Date date){
		Date thisWeekMonday=DateUtil.thisWeekMonday(date);
		Calendar c = getCalendarByDate(thisWeekMonday);
		c.add(Calendar.DAY_OF_MONTH, 7);
		return c.getTime();
	}
	/**
	 * 得到指定日期所在周的所有天
	 * @param date
	 * @return
	 */
	public static Date[] getOneWeekAllDays(Date date){
		Date[] dates=new Date[7];
		Date thisWeekMonday=DateUtil.thisWeekMonday(date);
		dates[0]=thisWeekMonday;
		dates[1]=tomorrow(dates[0]);
		dates[2]=tomorrow(dates[1]);
		dates[3]=tomorrow(dates[2]);
		dates[4]=tomorrow(dates[3]);
		dates[5]=tomorrow(dates[4]);
		dates[6]=tomorrow(dates[5]);
		return dates;
	}
	/**
	 * 下周日
	 * @param date
	 * @return
	 */
	public static Date nextWeekSunday(Date date){
		Date thisWeekSunday=DateUtil.thisWeekSunday(date);
		Calendar c = getCalendarByDate(thisWeekSunday);
	    c.add(Calendar.DAY_OF_MONTH, 7);
	    return c.getTime();
	}
	/**
	 * 得到一年最后一天
	 * @param year
	 * @return
	 */
    public static Date getYearLastDay(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }
    /**
     * 得到一年第一天
     * @param year
     * @return
     */
    public static Date getYearFirstDay(int year){
    	Calendar calendar=Calendar.getInstance();
    	calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		return calendar.getTime();
    }
	
	public static String getTime(Time time) {
		if(time==null){return null;}
		return time.toString();
	}

	public static Time getTime(String time) {
		if(time==null){return null;}
	        if (time.contains(":")) {
	        	String[] strs=time.split(":");
	            if(StringUtil.count(time, ':')==2){
	                 return new Time(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
	               }else if(StringUtil.count(time, ':')==1){
	            	   return new Time(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), 0);
	               }
	        }
	        return null;
	}
	
	
	
	/**
	 * 得到周期内的所有日期
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<Date> getContainDays(Date startDate,Date endDate){
		List<Date> dates = new ArrayList<Date>();
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start.set(Calendar.HOUR, 1);
		start.set(Calendar.MINUTE, 1);
		start.set(Calendar.SECOND, 1);
		start.set(Calendar.MILLISECOND, 1);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		end.set(Calendar.HOUR, 1);
		end.set(Calendar.MINUTE, 1);
		end.set(Calendar.SECOND, 1);
		end.set(Calendar.MILLISECOND, 2);
		
		while(start.before(end)){
			dates.add(start.getTime());
			start.add(Calendar.DATE, 1);
		}
		return dates;
	}
	
	

	public static void main(String[] args) {
//			Date[] dates=getOneWeekAllDays(new Date());
//			for(Date d:dates){
//				System.out.println(format(d, "yyyy-MM-dd"));
//			}
//			System.out.println("上周一:"+format(lastWeekMonday(new Date()), "yyyy-MM-dd"));
//			System.out.println("上周日:"+format(lastWeekSunday(new Date()), "yyyy-MM-dd"));
//			System.out.println("本周一:"+format(thisWeekMonday(new Date()), "yyyy-MM-dd"));
//			System.out.println("本周日:"+format(thisWeekSunday(new Date()), "yyyy-MM-dd"));
//			System.out.println("下周一:"+format(nextWeekMonday(new Date()), "yyyy-MM-dd"));
//			System.out.println("下周日:"+format(nextWeekSunday(new Date()), "yyyy-MM-dd"));
//			System.out.println("今年第一天:"+format(getYearFirstDay(2017), "yyyy-MM-dd"));
//			System.out.println("今年最后一天:"+format(getYearLastDay(2017), "yyyy-MM-dd"));
//			System.out.println("季度第一天:"+format(thisQuarterFirstDay(new Date()), "yyyy-MM-dd"));
//			System.out.println("季度最后一天:"+format(thisQuarterLastDay(new Date()), "yyyy-MM-dd"));
//			System.out.println("年第一天:"+format(thisYearFirstDay(new Date()), "yyyy-MM-dd"));
//			System.out.println("年最后一天:"+format(thisYearLastDay(new Date()), "yyyy-MM-dd"));
//			
//			
//			
//			
//			Calendar start = Calendar.getInstance();
//			Calendar end = Calendar.getInstance();
//			end.add(Calendar.DATE, 2);
//			List<Date> ds =getContainDays(start.getTime(), end.getTime());
//			for(Date d:ds){
//				System.out.println(d);
//			}
			
//			
//		 Calendar cal = Calendar.getInstance();
//		 cal.add(Calendar.YEAR, -1);
//		 cal.set(Calendar.MONTH, 11);
//		 cal.set(Calendar.DATE, 31);
//		 WeekBean bean = getThisWeek(cal.getTime());
//		 System.out.println(bean.getYear()+":"+bean.getWeek()+bean.getStartDay()+":"+bean.getEndDay());
//			
		 
	
		System.out.println(getAge(getDate("1989-03-04")));
		 
		}
	
	public static int getAge(Date birthDay) {
	        Calendar cal = Calendar.getInstance();
	 
	        if (cal.before(birthDay)) {
	            throw new IllegalArgumentException(
	                    "The birthDay is before Now.It's unbelievable!");
	        }
	        int yearNow = cal.get(Calendar.YEAR);
	        int monthNow = cal.get(Calendar.MONTH);
	        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
	        cal.setTime(birthDay);
	 
	        int yearBirth = cal.get(Calendar.YEAR);
	        int monthBirth = cal.get(Calendar.MONTH);
	        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
	 
	        int age = yearNow - yearBirth;
	 
	        if (monthNow <= monthBirth) {
	            if (monthNow == monthBirth) {
	                if (dayOfMonthNow < dayOfMonthBirth) age--;
	            }else{
	                age--;
	            }
	        }
	        return age;
	}
	/**
	 * 获取距今耗时
	 * @param startTime
	 * @return
	 */
	public static String getUsedTimeStr(Date startTime) {
		return getTimeDiff(startTime, getNow());
	}
	
	/**
          * 计算两个时间差
     */
    public static String getTimeDiff(Date endDate, Date nowDate)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }
	 
      
}