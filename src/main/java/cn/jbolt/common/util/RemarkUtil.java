package cn.jbolt.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * 给备注信息处理的工具类
 * @ClassName:  RemarkUtil   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年2月1日   
 */
public class RemarkUtil {
	/**
	 * 多行变单行
	 * @param str
	 * @return
	 */
	public static String changeToSingleLine(String str) {
		if(StrUtil.isBlank(str)) {return "";}
		return StrUtil.removeAllLineBreaks(str);
	}
}
