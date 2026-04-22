package jbolt;

import cn.hutool.core.util.IdUtil;
import cn.jbolt.siargo.model.Equipment;
import cn.jbolt.siargo.model.EquipmentRecord;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.Date;
import java.util.List;

/**
 * 批量为所有设备生成一条记录的测试类
 * <p>
 * 功能：查询数据库中所有设备，对每台设备插入一条指定类型的记录。
 * 类似批量审核功能 EquipmentAdminController.batchAudit()，
 * 但操作目标是 siargo_equipment_record 表。
 * </p>
 *
 * <p><b>运行前准备：</b></p>
 * <ol>
 *   <li>将下方 {@code DB_USER} 和 {@code DB_PASSWORD} 改为本地数据库的明文账号和密码</li>
 *   <li>确认 {@code DB_URL} 指向正确的数据库地址</li>
 *   <li>确认 {@code RECORD_TYPE} 对应字典 {@code siargo_equipment_record_type} 中的有效 sn 值</li>
 *   <li>直接运行此测试类（无需启动 Web 容器）</li>
 * </ol>
 *
 * @author siargo
 * @date 2026-04-21
 */
@TestInstance(Lifecycle.PER_CLASS)
public class BatchEquipmentRecordTest {

    // ==================== 数据库连接配置 ====================

    /**
     * 数据库连接URL（与 dbconfig/mysql/config.properties 中 jdbc_url 一致）
     */
    private static final String DB_URL =
            "jdbc:mysql://127.0.0.1:3306/siargodev"
            + "?characterEncoding=utf8&useSSL=false&rewriteBatchedStatements=true"
            + "&autoReconnect=true&zeroDateTimeBehavior=convertToNull"
            + "&serverTimezone=Asia/Shanghai";

    /**
     * 数据库用户名（明文，替换 config.properties 中的加密值）
     * TODO: 填写实际明文用户名
     */
    private static final String DB_USER = "root";

    /**
     * 数据库密码（明文，替换 config.properties 中的加密值）
     * TODO: 填写实际明文密码
     */
    private static final String DB_PASSWORD = "siargo";

    // ==================== 业务配置 ====================

    /**
     * 要生成的记录类型（对应字典 siargo_equipment_record_type 的 sn 值）
     * 例如：inspection=检校、maintenance=保养、repair=维修
     * TODO: 按需修改为目标记录类型
     */
    private static final String RECORD_TYPE = "7";

    /**
     * 记录描述
     * TODO: 按需修改
     */
    private static final String RECORD_DESCRIPTION = "2026-04-21之前台账数据，请查看EXCEL";

    // ==================== 插件引用（用于关闭） ====================

    private DruidPlugin druidPlugin;
    private ActiveRecordPlugin arp;

    // ==================== 初始化与清理 ====================

    @BeforeAll
    public void setUp() {
        System.out.println("========== 批量为设备生成记录 - 测试开始 ==========");

        // 1. 初始化 Druid 数据源
        druidPlugin = new DruidPlugin(DB_URL, DB_USER, DB_PASSWORD);
        druidPlugin.start();

        // 2. 初始化 ActiveRecord 插件，注册全部相关 Model
        arp = new ActiveRecordPlugin(druidPlugin);
        arp.addMapping("siargo_equipment", "id", Equipment.class);
        arp.addMapping("siargo_equipment_record", "id", EquipmentRecord.class);
        arp.setDevMode(true);
        arp.start();

        System.out.println("数据库连接初始化完成");
        System.out.println();
    }

    @AfterAll
    public void tearDown() {
        if (arp != null) {
            arp.stop();
        }
        if (druidPlugin != null) {
            druidPlugin.stop();
        }
        System.out.println("========== 测试结束，数据库连接已关闭 ==========");
    }

    // ==================== 测试方法 ====================

    /**
     * 批量为所有设备生成一条记录
     * <p>
     * 逻辑：
     * 1. 查询所有设备
     * 2. 对每台设备新建一条 EquipmentRecord
     * 3. 汇总成功/跳过数量并打印结果
     * </p>
     */
    @Test
    public void testBatchGenerateRecords() {
        System.out.println("--- testBatchGenerateRecords ---");

        // 1. 查询所有目标设备
        List<Equipment> equipmentList = queryAllEquipments();
        if (equipmentList == null || equipmentList.isEmpty()) {
            System.out.println("⚠ 未查询到任何设备，测试终止");
            return;
        }
        System.out.println("查询到设备总数: " + equipmentList.size());

        // 2. 逐台设备生成记录
        int successCount = 0;
        int skipCount = 0;
        int failCount = 0;
        Date now = new Date();

        for (Equipment equipment : equipmentList) {
            Long equipmentId = equipment.getId();
            if (equipmentId == null) {
                skipCount++;
                continue;
            }

            try {
                boolean saved = saveOneRecord(equipmentId, now);
                if (saved) {
                    successCount++;
                    System.out.println("  [OK] 设备 ID=" + equipmentId
                            + "  编号=" + equipment.getStr("equipment_no")
                            + "  名称=" + equipment.getStr("name"));
                } else {
                    failCount++;
                    System.out.println("  [FAIL] 设备 ID=" + equipmentId + " 保存失败");
                }
            } catch (Exception e) {
                failCount++;
                System.out.println("  [ERROR] 设备 ID=" + equipmentId + " 异常: " + e.getMessage());
            }
        }

        // 3. 输出汇总
        System.out.println();
        System.out.println("========== 批量生成记录结果汇总 ==========");
        System.out.println("设备总数  : " + equipmentList.size());
        System.out.println("成功插入  : " + successCount);
        System.out.println("跳过(无ID): " + skipCount);
        System.out.println("失败      : " + failCount);
        System.out.println("==========================================");
        System.out.println("✓ testBatchGenerateRecords 执行完成");
    }

    /**
     * 仅预览将要操作的设备列表，不写入数据（用于验证查询条件）
     */
    @Test
    public void testPreviewEquipments() {
        System.out.println("--- testPreviewEquipments (仅预览，不写入) ---");

        List<Equipment> equipmentList = queryAllEquipments();
        if (equipmentList == null || equipmentList.isEmpty()) {
            System.out.println("未查询到任何设备");
            return;
        }

        System.out.println("将要处理的设备列表（共 " + equipmentList.size() + " 台）：");
        for (Equipment eq : equipmentList) {
            System.out.printf("  ID=%-20s  NO=%-20s  NAME=%s%n",
                    eq.getId(),
                    eq.getStr("equipment_no"),
                    eq.getStr("name"));
        }
        System.out.println("✓ testPreviewEquipments 预览完成（未写入数据库）");
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 查询目标设备列表
     * @return 设备列表
     */
    private List<Equipment> queryAllEquipments() {
        String sql = "SELECT id, equipment_no, name FROM siargo_equipment ORDER BY id";
        return new Equipment().dao().find(sql);
    }

    /**
     * 为指定设备保存一条记录
     *
     * @param equipmentId 设备ID
     * @param now         当前时间，用作记录日期和编制时间
     * @return 是否保存成功
     */
    private boolean saveOneRecord(Long equipmentId, Date now) {
        EquipmentRecord record = new EquipmentRecord();
        // 使用雪花算法生成唯一ID（与项目 @TableBind idGenMode = SNOWFLAKE 保持一致）
        record.setId(IdUtil.getSnowflake(1, 1).nextId());
        record.setEquipmentId(equipmentId);
        record.setRecordType(RECORD_TYPE);
        record.setRecordDate(now);
        record.setDescription(RECORD_DESCRIPTION);
        record.setCreatorTime(now);
        // 审核状态：0-未审核
        record.setAuditStatus(0);
        return record.save();
    }
}
