package cn.jbolt.common.util;

import cn.hutool.core.util.StrUtil;

/** 给备注信息处理的工具类 */
public class RemarkUtil {
    /** 多行变单行 */
    public static String changeToSingleLine(String str) {
        if (StrUtil.isBlank(str)) {
            return "";
        }
        return StrUtil.removeAllLineBreaks(str);
    }
}
