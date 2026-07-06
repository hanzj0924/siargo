package cn.jbolt.admin.siargo.qarep;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 产品型号分类工具类
 * <p>
 * 根据产品型号字符串分析其所属产品类型。
 * 采用 HashMap 最长前缀匹配 + 特例正则 + contains 子串三级策略。
 * 新增规则只需在 static 块中一行 put，不涉及其他修改。
 *
 * @author hanzj
 * @date 2026-07-03
 */
public class ProductModelClassifier {

    /** 合法前缀（型号以此集合中任一字符串开头方可继续分析，否则返回 0） */
    private static final String[] GATE_PREFIXES = {"MF", "FS", "MFC", "BC", "PFLOW"};

    /** 大流量子串（含任一即判定 type=2，最先检查，优先级最高） */
    private static final String[] HIGH_FLOW_CONTAINS =
        {"GD", "FD-E", "-F-E", "FD-D", "-F-D", "MFI"};

    /** 大流量精确前缀（type=2） */
    private static final String[] HIGH_FLOW_PREFIXES =
        {"MF2025", "MF2032"};

    /**
     * 前缀 → 类型映射（type=1 小流量 / type=3 传感器）
     * 匹配时从模型串取 2~7 字符逐级查表，命中即返回，天然支持最长前缀优先。
     */
    private static final Map<String, Integer> PREFIX_MAP = new HashMap<>();
    static {
        // ===== Type 1：小流量 =====
        put("MFC", 1);
        put("MF47", 1);  
        put("FS47", 1);
        put("MF52", 1);  
        put("MF56", 1);  
        put("MF57", 1);
        put("MF50", 1);  
        put("MF45", 1);  
        put("MF46", 1);
        put("BC", 1);

        // ===== Type 3：传感器 =====
        put("MF40", 3);  
        put("FS40", 3);
        put("FS50", 3);
        put("FS3400", 3); 
        put("FS3410", 3); 
        put("FS3430", 3);
        put("FS430", 3);
        put("FS1015", 3);
        put("FS35001", 3);
        put("MF3000", 3);
        put("FS8001", 3); 
        put("FS8003", 3);
        put("FS6122", 3);
        put("FS6430", 3);
        put("FS1100", 3);
        put("FS6022", 3);
        put("FS7001", 3);  
        put("FS7002", 3);
        put("FSP1000", 3);
        put("MF34", 3);
        put("MF4308", 3);
        put("PFLOW", 3);
        put("AM1000", 3);
        put("AM1100", 3);
    }
    private static void put(String prefix, int type) {
        PREFIX_MAP.put(prefix, type);
    }

    /** MF6X00 小流量特例（如 MF6500、MF6600） */
    private static final Pattern LOW_FLOW_MF6X00 = Pattern.compile("^MF6\\d00");

    /**
     * 分析产品型号，返回产品类型
     *
     * @param model 产品型号字符串（大小写不敏感）
     * @return 0=客户定制/无法识别, 1=小流量, 2=大流量, 3=传感器
     */
    public static int classify(String model) {
        // 空值保护
        if (model == null || (model = model.trim().toUpperCase()).isEmpty()) {
            return 0;
        }
        // AM 系列传感器（AM1000、AM1100）作为特例，前缀不匹配标准门控但确认为传感器
        if (model.startsWith("AM")) {
            Integer amType = matchPrefixMap(model);
            return amType != null ? amType : 0;
        }
        // 前置过滤：非合法前缀 → 客户定制
        if (!hasGatePrefix(model)) {
            return 0;
        }
        // 优先级 1：大流量（子串 + 前缀）
        if (isHighFlow(model)) {
            return 2;
        }
        // 优先级 2：小流量（特例 + HashMap）
        if (isLowFlow(model)) {
            return 1;
        }
        // 优先级 3：传感器（HashMap 查表）
        Integer type = matchPrefixMap(model);
        if (type != null) {
            return type;
        }
        // 合法前缀但未命中任何规则 → 记录日志并返回 0，等待人工确认
        logUnmatched(model);
        return 0;
    }

    // ==================== 前置过滤 ====================

    /**
     * 检查型号是否以任一合法前缀开头
     */
    private static boolean hasGatePrefix(String model) {
        for (String prefix : GATE_PREFIXES) {
            if (model.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 大流量判断 ====================

    /**
     * 大流量判断（type=2）：先查 contains 子串，再查精确前缀
     */
    private static boolean isHighFlow(String model) {
        // contains 子串匹配
        for (String pattern : HIGH_FLOW_CONTAINS) {
            if (model.contains(pattern)) {
                return true;
            }
        }
        // 精确前缀匹配（MF2025 / MF2032）
        for (String prefix : HIGH_FLOW_PREFIXES) {
            if (model.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 小流量判断 ====================

    /**
     * 小流量判断（type=1）：特例优先，再走 HashMap 查表
     */
    private static boolean isLowFlow(String model) {
        // 特例 1：MF6X00（如 MF6500、MF6600）
        if (LOW_FLOW_MF6X00.matcher(model).find()) {
            return true;
        }
        // 特例 2：MF 开头且含 "HD"（如 MF19HD、MF25HD）
        if (model.startsWith("MF") && model.contains("HD")) {
            return true;
        }
        // HashMap 查表（命中 type=1）
        Integer type = matchPrefixMap(model);
        return type != null && type == 1;
    }

    // ==================== 前缀查表 ====================

    /**
     * 逐级前缀查表（2→7 字符）
     * <p>
     * 从模型串头部截取递增长度的前缀查询 HashMap，命中即返回对应类型。
     * 由于循环从短到长，更长的前缀（更精确的规则）会自然覆盖更短的匹配。
     *
     * @param model 已转为大写的型号字符串
     * @return 匹配到的类型，未命中返回 null
     */
    private static Integer matchPrefixMap(String model) {
        int maxLen = Math.min(model.length(), 7);
        for (int i = 2; i <= maxLen; i++) {
            Integer type = PREFIX_MAP.get(model.substring(0, i));
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    // ==================== 日志 ====================

    /**
     * 记录未匹配的型号，便于后续人工确认并补充规则
     */
    private static void logUnmatched(String model) {
        System.err.println("[ProductModelClassifier] 未匹配型号: " + model);
    }
}
