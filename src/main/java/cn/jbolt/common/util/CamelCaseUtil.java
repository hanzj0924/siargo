package cn.jbolt.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.CPI;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.hutool.core.map.MapUtil;
/**
 * 驼峰转换工具类
 * @ClassName:  CamelCaseUtil   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年1月11日   
 */
public class CamelCaseUtil {
	/**
	 * 将record中columns的key转为驼峰
	 * @param recordPage
	 * @return
	 */
	public static void keyToCamelCase(Page<Record> recordPage) {
		if(recordPage!=null) {
			keyToCamelCase(recordPage.getList());
		}
	}
	/**
	 * 将record中columns的key转为驼峰
	 * @param records
	 * @return
	 */
	public static void keyToCamelCase(List<Record> records) {
		if(records!=null&&records.size()>0) {
			for(Record record:records) {
				keyToCamelCase(record);
			}
		}
	}
	/**
	 * 将record中columns的key转为驼峰
	 * @param record
	 * @return
	 */
	public static void keyToCamelCase(Record record) {
		if(record!=null&&record.getColumns()!=null&&record.getColumns().isEmpty()==false) {
			CPI.setColumnsMap(record, MapUtil.toCamelCaseMap(record.getColumns()));
		}
	}
	/**
	 * 将record中columns的key转为驼峰
	 * @param record
	 * @return
	 */
	public static Map<String, Object> convert(Record record) {
		if(record==null) {return null;}
		Map<String, Object> columns=record.getColumns();
		if(columns!=null&&columns.isEmpty()==false) {
			return MapUtil.toCamelCaseMap(columns);
		}
		return columns;
	}
	
	/**
	 * 将record中columns的key转为驼峰
	 * @param records
	 * @return
	 */
	public static List<Map<String, Object>> convert(List<Record> records) {
		if(records==null||records.size()==0) {return Collections.emptyList();}
			List<Map<String, Object>> listMap=new ArrayList<Map<String,Object>>();
			for(Record record:records) {
				listMap.add(convert(record));
			}
		return listMap;
	}
	
	/**
	 * 将record中columns的key转为驼峰
	 * @param recordPage
	 * @return
	 */
	public static Page<Map<String, Object>> convert(Page<Record> recordPage) {
		if(recordPage==null) {return null;}
		List<Map<String, Object>> mapList=convert(recordPage.getList());
		return new Page<Map<String,Object>>(mapList, recordPage.getPageNumber(), recordPage.getPageSize(), recordPage.getTotalPage(), recordPage.getTotalRow());
	}
}
