package cn.jbolt.common.util;

import cn.hutool.core.util.NumberUtil;

/**
* @author mmm E-mail:909854136@qq.com
* @version 创建时间：2018年12月27日 下午4:44:16
* 类说明
*/
public class DataSizeUtil {
	public static String getPrintSize(double size) {
		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else {
			size = size / 1024;
		}
		//如果原字节数除于1024之后，少于1024，则可以直接以B作为单位
		//接下去以此类推
		if (size < 1024) {
			return NumberUtil.decimalFormat("0.00",size) + "K";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			return NumberUtil.decimalFormat("0.00",size) + "M";
		} else {
			size = size * 100 / 1024;
			return NumberUtil.decimalFormat("0.00",(size / 100)) + "G";
		}
	}
	
	public static void main(String[] args) {
		System.out.println(DataSizeUtil.getPrintSize(1456320564));
	}
	


}
