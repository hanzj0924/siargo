package cn.jbolt.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

public class ArrayUtil {
	/**
	 * 列表内的数据拼接成字符串
	 * @param lists
	 * @param split
	 * @return
	 */
	public static <T> String join(List<T> lists,String split){
		return join(lists, split, false);
	}
	/**
	 * 列表内的数据拼接成字符串
	 * @param list
	 * @param split
	 * @param bothEndsAppendSplit 两端是否携带分隔符
	 * @return
	 */
	public static <T> String join(List<T> list,String split,boolean bothEndsAppendSplit){
		if(list==null||list.size()==0){return null;}
		if(list.size()==1) {return list.get(0).toString();}
		StringBuilder sb=new StringBuilder();
		boolean isFirst=true;
		for(T s:list){
			if(isFirst) {
				isFirst=false;
				if(bothEndsAppendSplit) {
					sb.append(split);
				}
			}else {
				sb.append(split);
			}
			sb.append(s);
		}
		if(bothEndsAppendSplit) {
			sb.append(split);
		}
		return sb.toString();
	}
	/**
	 * 数组内的数据拼接成字符串
	 * @param array
	 * @param split
	 * @return
	 */
	public static String join(Object[] array,String split){
		return join(array, split, false);
	}
	/**
	 * 数组内的数据拼接成字符串
	 * @param array
	 * @param split
	 * @param bothEndsAppendSplit 两端是否携带分隔符
	 * @return
	 */
	public static String join(Object[] array,String split,boolean bothEndsAppendSplit){
		if(array==null||array.length==0){return null;}
		if(array.length==1) {return array[0].toString();}
		StringBuilder sb=new StringBuilder();
		boolean isFirst=true;
		for(Object s:array){
			if(isFirst) {
				isFirst=false;
				if(bothEndsAppendSplit) {
					sb.append(split);
				}
			}else {
				sb.append(split);
			}
			sb.append(s.toString());
		}
		if(bothEndsAppendSplit) {
			sb.append(split);
		}
		return sb.toString();
	}
	/**
	 * 数组内的数据拼接成字符串
	 * @param array
	 * @param split
	 * @return
	 */
	public static String join(String[] array,String split){
		return join(array, split,false);
	}
	
	/**
	 * 数组内的数据拼接成字符串
	 * @param array
	 * @param split
	 * @param bothEndsAppendSplit 两端是否携带分隔符
	 * @return
	 */
	public static String join(String[] array,String split,boolean bothEndsAppendSplit){
		if(array==null||array.length==0){return null;}
		if(array.length==1) {return array[0].toString();}
		StringBuilder sb=new StringBuilder();
		boolean isFirst=true;
		for(String s:array){
			if(isFirst) {
				isFirst=false;
				if(bothEndsAppendSplit) {
					sb.append(split);
				}
			}else {
				sb.append(split);
			}
			sb.append(s);
		}
		if(bothEndsAppendSplit) {
			sb.append(split);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String[] strs=new String[] {"1","m","x","0"};
		System.out.println(join(strs, ","));
		System.out.println(join(strs, ",",true));
		Integer[] ints=new Integer[] {1,2,3,4,5};
		System.out.println(join(ints, ","));
		System.out.println(join(ints, ",",true));
		List<Integer> lists=new ArrayList<Integer>();
		lists.add(1);
		lists.add(1);
		lists.add(1);
		lists.add(1);
		System.out.println(join(lists, ","));
		System.out.println(join(lists, ",",true));
	}
	
	/**
	 * 从字符串分割有效数组
	 * @param str
	 * @param split
	 * @return
	 */
	public static String[] from(String str,String split){
		if(StrKit.isBlank(str)){return new String[0];}
		String[] array=str.split(split);
		if(array.length==0){return array;}
		List<String> list=new ArrayList<String>();
		for(String s:array){
			if(StrKit.isBlank(s)){continue;}
			list.add(s.trim());
		}
		return list.toArray(new String[list.size()]);
	}
	/**
	 * 从字符串分割有效数组 转为Integer
	 * @param str
	 * @param split
	 * @return
	 */
	public static Integer[] toInt(String str,String split){
		if(StrKit.isBlank(str)){return new Integer[0];}
		String[] array=str.split(split);
		if(array.length==0){return new Integer[0];}
		List<Integer> list=new ArrayList<Integer>();
		for(String s:array){
			if(StrKit.isBlank(s)){continue;}
			list.add(Integer.valueOf(s.trim()));
		}
		return list.toArray(new Integer[list.size()]);
	}
	/**
	 * 从字符串分割有效数组 转为BigDecimal
	 * @param str
	 * @param split
	 * @return
	 */
	public static BigDecimal[] toBigDecimal(String str,String split){
		if(StrKit.isBlank(str)){return new BigDecimal[0];}
		String[] array=str.split(split);
		if(array.length==0){return new BigDecimal[0];}
		List<BigDecimal> list=new ArrayList<BigDecimal>();
		for(String s:array){
			if(StrKit.isBlank(s)){continue;}
			list.add(new BigDecimal(s.trim()));
		}
		return list.toArray(new BigDecimal[list.size()]);
	}
	/**
	 * 从字符串分割有效数组 转为Long
	 * @param str
	 * @param split
	 * @return
	 */
	public static Long[] toLong(String str,String split){
		if(StrKit.isBlank(str)){return new Long[0];}
		String[] array=str.split(split);
		if(array.length==0){return new Long[0];}
		List<Long> list=new ArrayList<Long>();
		for(String s:array){
			if(StrKit.isBlank(s)){continue;}
			list.add(Long.valueOf(s.trim()));
		}
		return list.toArray(new Long[list.size()]);
	}
	/**
	 * 从字符串分割有效数组 去重版
	 * @param str
	 * @param split
	 * @return
	 */
	public static String[] from2(String str,String split){
		String[] array=from(str, split);
		if(array!=null&&array.length==0) {return array;}
		Set<String> set = new HashSet<String>(Arrays.asList(array));
		return set.toArray(new String[set.size()]);
	}
	/**
	 * 从字符串分割有效数组 去重版 有顺序
	 * @param str
	 * @return
	 */
	public static String[] from3(String str){
		return from3(str,",");
	}
	/**
	 * 从字符串分割有效数组 去重版 有顺序
	 * @param str
	 * @param split
	 * @return
	 */
	public static String[] from3(String str,String split){
		String[] array=from(str, split);
		if(array!=null&&array.length==0) {return array;}
		Set<String> set = new LinkedHashSet<String>(Arrays.asList(array));
		return set.toArray(new String[set.size()]);
	}
	/**
	 * 从字符串分割有效数组 去重版
	 * @param str
	 * @param split
	 * @return
	 */
	public static Integer[] toDisInt(String str,String split){
		Integer[] array=toInt(str, split);
		Set<Integer> set = new HashSet<Integer>(Arrays.asList(array));
		return set.toArray(new Integer[set.size()]);
	}
	/**
	 * 从字符串分割有效数组 去重版
	 * @param str
	 * @param split
	 * @return
	 */
	public static Long[] toDisLong(String str,String split){
		Long[] array=toLong(str, split);
		Set<Long> set = new HashSet<Long>(Arrays.asList(array));
		return set.toArray(new Long[set.size()]);
	}
	/**
	 * 判断数组是否包含指定内容
	 * @param arrays
	 * @param targetValue
	 * @return
	 */
	public static  boolean contains(String[] arrays,String targetValue){
		if(arrays==null){
			return false;
		}
		Set<String> set = new HashSet<String>(Arrays.asList(arrays));
		return set.contains(targetValue);
	}
	/**
	 * 判断数组是否包含指定内容
	 * @param arrays
	 * @param targetValue
	 * @return
	 */
	public static  boolean contains(String[] arrays,Integer targetValue){
		if(targetValue==null||arrays==null||arrays.length==0){return false;}
		return contains(arrays,targetValue.toString());
	}
    /**
     * 判断数组是否包含指定内容
     * @param arrays
     * @param targetValue
     * @return
     */
    public static  boolean contains(String[] arrays,Long targetValue){
    	if(targetValue==null||arrays==null||arrays.length==0){return false;}
        return contains(arrays,targetValue.toString());
    }
    /**
     * List中所有Item对象的指定字段提取构成数组
     * @param list
     * @param columnName
     * @return
     */
    public static Integer[] getIntegerArray(List<? extends Model<?>>  list,String columnName){
    	if(list.size()>0){
    		Integer[] arrays=new Integer[list.size()];
    		for(int i=0;i<list.size();i++){
    			arrays[i]=list.get(i).getInt(columnName);
    		}
    		return arrays;
    	}else{
    		return null;
    	}
    }
    /**
     * List中所有Item对象的指定字段提取构成去重数组
     * @param list
     * @param columnName
     * @return
     */
    public static Integer[] getDistinctIntegerArray(List<? extends Model<?>>  list,String columnName){
    	if(list.size()>0){
    		Set<Integer> set = new HashSet<Integer>();
    		for(int i=0;i<list.size();i++){
    			set.add(list.get(i).getInt(columnName));
    		}
    		return set.toArray(new Integer[set.size()]);
    	}else{
    		return null;
    	}
    }
    /**
     * List中所有Item对象的指定字段提取构成去重List
     * @param list
     * @param columnName
     * @return
     */
    public static List<String> getDistinctStringList(List<? extends Model<?>>  list,String columnName){
    	if(list.size()>0){
    		Set<String> set = new HashSet<String>();
    		for(int i=0;i<list.size();i++){
    			set.add(list.get(i).getStr(columnName));
    		}
    		return new ArrayList<String>(set);
    	}else{
    		return null;
    	}
    }
    /**
     * List中所有Item对象的指定字段提取构成去重List
     * @param list
     * @param columnName
     * @return
     */
    public static List<Long> getDistinctLongList(List<? extends Model<?>>  list,String columnName){
    	if(list.size()>0){
    		Set<Long> set = new HashSet<Long>();
    		for(int i=0;i<list.size();i++){
    			set.add(list.get(i).getLong(columnName));
    		}
    		return new ArrayList<Long>(set);
    	}else{
    		return null;
    	}
    }
    /**
     * List中所有Item对象的指定字段提取构成去重List
     * @param list
     * @param columnName
     * @return
     */
    public static List<Integer> getDistinctIntegerList(List<? extends Model<?>>  list,String columnName){
    	if(list.size()>0){
    		Set<Integer> set = new HashSet<Integer>();
    		for(int i=0;i<list.size();i++){
    			set.add(list.get(i).getInt(columnName));
    		}
    		return new ArrayList<Integer>(set);
    	}else{
    		return null;
    	}
    }
    /**
     * List中所有Item对象的指定字段提取构成数组
     * @param list
     * @param columnName
     * @return
     */
    public static Long[] getLongArray(List<? extends Model<?>>  list,String columnName){
    	if(list.size()>0){
    		Long[] arrays=new Long[list.size()];
    		for(int i=0;i<list.size();i++){
				arrays[i]=list.get(i).getLong(columnName);
			}
    		return arrays;
    	}else{
    		return null;
    	}
    }
    /**
     * List中所有Item对象的指定字段提取构成数组
     * @param list
     * @param columnName
     * @return
     */
    public static String[] getStringArray(List<? extends Model<?>>  list,String columnName){
    	if(list.size()>0){
    		String[] arrays=new String[list.size()];
    		for(int i=0;i<list.size();i++){
				arrays[i]=list.get(i).getStr(columnName);
			}
    		return arrays;
    	}else{
    		return null;
    	}
    }

	public static boolean notEmpty(Object[] param) {
		return param!=null&&param.length>0;
	}
	public static boolean isEmpty(Object[] param) {
		return param==null||param.length==0;
	}
	public static String merge(String str1,String str2,String split){
		if(StringUtil.isBlank(str1)&&StringUtil.isBlank(str2)){return null;}
		if(StringUtil.isBlank(str1)&&StringUtil.isNotBlank(str2)){return str2;}
		if(StringUtil.isNotBlank(str1)&&StringUtil.isBlank(str2)){return str1;}
		String result=null;
		String[] arr1=from(str1, split);
		String[] arr2=from(str2, split);
		if((arr1==null||arr1.length==0)&&(arr2==null||arr2.length==0)){return null;}
		if((arr1==null||arr1.length==0)&&(arr2!=null&&arr2.length>0)){return str2;}
		if((arr1!=null&&arr1.length>0)&&(arr2==null||arr2.length==0)){return str1;}
		Set<String> ids=new HashSet<String>();
		for(String s1:arr1){
			ids.add(s1);
		}
		for(String s2:arr2){
			ids.add(s2);
		}
		String array[]=ids.toArray(new String[ids.size()]);
		result=join(array, split);
		return result;
	}
	@SuppressWarnings("unchecked")
	public static <T> List<T> listFrom(String str, String split) {
		if(StrKit.isBlank(str)){return Collections.emptyList();}
		String[] array=str.split(split);
		if(array.length==0){return Collections.emptyList();}
		List<T> list=new ArrayList<T>();
		for(String s:array){
			if(StrKit.isBlank(s)){continue;}
			list.add((T)s.trim());
		}
		return list;
	}
	
	public static String[] merge(String[] arr1, String[] arr2) {
		Set<String> ids=new HashSet<String>();
		for(String s1:arr1){
			ids.add(s1);
		}
		for(String s2:arr2){
			ids.add(s2);
		}
		return ids.toArray(new String[ids.size()]);
	}
	
	public static Integer[] merge(Integer[] arr1, Integer[] arr2) {
		Set<Integer> ids=new HashSet<Integer>();
		for(Integer s1:arr1){
			ids.add(s1);
		}
		for(Integer s2:arr2){
			ids.add(s2);
		}
		return ids.toArray(new Integer[ids.size()]);
	}

}
