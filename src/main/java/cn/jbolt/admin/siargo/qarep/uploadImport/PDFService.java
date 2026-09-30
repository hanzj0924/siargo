package cn.jbolt.admin.siargo.qarep.uploadImport;

import cn.jbolt.admin.siargo.qarep.QareportService;
import cn.jbolt.admin.siargo.qarep.product.ProductService;
import cn.jbolt.admin.siargo.qarep.pdffolder.PdfTemplateService;
import cn.jbolt.admin.siargo.qarep.siargoconst.QarepConst;
import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.siargo.model.Qareport;
import com.jfinal.aop.Inject;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.*;

/**
 * 检验报告 PDF 的应用层统一入口，供手动导出与月度归档共用。
 *
 * <p>本类负责批次编排、发布及结果汇总；报告查询交给 QareportService，模板选择、
 * 字段映射、PDF 填充和 RAR 压缩分别交给独立组件，文件边界由 SiargoStorage 校验。</p>
 * <p>正式生成按产品逐项提交，不使用覆盖整批的事务；单项失败不会撤销其他成功项。
 * 月度归档仅生成任务副本，不修改产品正式 PDF 地址，也不删除正式 PDF。</p>
 * <p>这里处理的是检验报告生成，不参与 DMS 技术通知单上传文件的命名或元数据修改。</p>
 */
public class PDFService {
    private static final Log LOG = Log.getLog(PDFService.class);
    @Inject
    private QareportService reports;
    @Inject
    private ProductService products;
    @Inject
    private ReportTemplateResolver templates;
    @Inject
    private ReportFieldMapper mapper;
    @Inject
    private PdfRenderer renderer;
    @Inject
    private RarArchiver archiver;

    /**
     * 生成指定产品的 PDF；重复 ID 按首次出现顺序去重。
     *
     * @param ids  产品主键列表，不是报告单主键列表；不能为空或包含无效 ID
     * @param mode 正式发布或月度归档；按月份筛选由对应的归档入口负责
     * @return 准备失败时直接返回 Ret.fail；完成批次后返回成功数、失败明细、清理警告、
     * PDF 地址列表、归档地址（无归档时为 null）和任务目录地址。
     * successCount 为成功 PDF 数；failCount 为错误条数，可能包含收尾错误，
     * 因而不一定等于产品失败数。部分成功也返回 fail，调用方仍须展示成功产物；
     * warnings 包含模板字段、未填写参数及清理提示，不将已成功发布的 PDF 改判为失败。
     */
    public Ret generate(List<Long> ids, Mode mode) {
        return generate(ids, mode, YearMonth.now().minusMonths(1).toString());
    }

    private Ret generate(List<Long> ids, Mode mode, String archivePeriod) {
        if (mode == null || ids == null || ids.isEmpty() || ids.stream().anyMatch(id -> id == null || id <= 0)) {
            return Ret.fail("请选择有效的产品记录");
        }
        List<String> failures = new ArrayList<>(), warnings = new ArrayList<>(), outputs = new ArrayList<>();
        List<Path> archiveInputs = new ArrayList<>();
        SiargoStorage templateStorage = SiargoStorage.forReportTemplates();
        SiargoStorage storage = SiargoStorage.forBusiness(SiargoStorage.Business.QAREP);
        Path task;
        Path rar = null;
        String archiveUrl = null;
        // 区间用于归档目录与文件名；本方法不按日期再次过滤传入的产品 ID。
        try {
            // 先检查压缩程序路径，再创建任务，避免程序缺失时仍逐个生成月度副本。
            if (mode == Mode.MONTHLY_ARCHIVE) rar = archiver.executable(JBoltConfig.prop.get("winrar_exe_path"));
            // 两种模式都隔离每次任务，防止重复提交或并发任务混用中间产物。
            // 导出中间产物与失败记录也保留在 export 下；本类不删除空目录或失败记录。
            task = mode == Mode.MONTHLY_ARCHIVE
                    ? storage.ensureDirectory("archives", archivePeriod, UUID.randomUUID().toString())
                    : storage.ensureDirectory("pdf-tasks", UUID.randomUUID().toString());
        } catch (Exception e) {
            return Ret.fail("PDF 任务准备失败：" + e.getMessage());
        }
        for (Long id : new LinkedHashSet<>(ids)) {
            Path draft = null, published = null;
            String reportLabel = "报告单（编号未知）";
            // moved 表示草稿已移动；committed 表示本项已经发布，异常清理不可再撤销它。
            // 月度模式的 committed 只表示副本已收集，不代表发生了数据库提交。
            boolean committed = false, moved = false;
            try {
                // 复用同一条查询、模板解析和字段映射链路，保证两种模式内容规则一致。
                Qareport report = reports.qareportFindByProId(id);
                if (report == null) throw new IllegalArgumentException("未找到报告单数据");
                Object order = report.get("order_id"), form = report.get("formnum");
                if (form != null && !form.toString().isBlank()) reportLabel = "报告单" + form;
                String approvalError = validateApprovedReport(report);
                if (approvalError != null) throw new IllegalArgumentException(approvalError);
                if (order == null || form == null) throw new IllegalArgumentException("订单号或报告单编号为空");
                String version = report.getStr("sp_pdfver");
                ReportTemplateResolver.ResolvedTemplate resolved = templates.resolveSnapshot(templateStorage, version, seriesId(report.get("siargo_prod_model_id")));
                Path template = resolved.path();
                Set<String> templateFields = PdfRenderer.fieldNames(template);
                Map<String, String> fields = mapper.buildDataMap(report);
                for (String warning : PdfTemplateService.missingValueWarnings(templateFields, fields)) warnings.add(reportLabel + "：" + warning);
                String name = SiargoStorage.safeSegment(order.toString()) + "_" + id;
                draft = storage.checked(task.resolve(name + ".part"));
                // renderer 完整关闭 PDF 并逐页读取校验后才返回；.part 不作为正式地址入库。
                renderer.render(template, draft, Path.of(PathKit.getWebRootPath(), "assets/fonts/SIMSUN.TTC"), fields);
                if (mode == Mode.FORMAL) {
                    // 正式报告使用新的唯一文件名，不覆盖旧文件；DMS 的原文件名规则不适用于此处。
                    storage.ensureDirectory(PdfStoragePaths.versionSegments("reports", version));
                    published = PdfStoragePaths.path(storage, "reports", version, name + "_" + UUID.randomUUID() + ".pdf");
                    storage.moveNew(draft, published);
                    moved = true;
                    String old = report.getStr("sp_pdfstr"), current = storage.toUrl(published);
                    // 发布事务复验模板、报告与产品快照，变化时拒绝发布旧内容。
                    Ret changed = reports.publishPdf(report, resolved.template(), current);
                    if (changed.isFail()) throw new IllegalStateException(changed.getStr("msg"));
                    committed = true;
                    outputs.add(current);
                    if (old != null && !old.isBlank() && !old.equals(current)) {
                        // 只有新地址提交成功，且旧文件没有其他产品引用时，才允许清理旧文件。
                        String cleanupWarning = products.cleanupOldPdfAfterPublication(old);
                        if (cleanupWarning != null) warnings.add(reportLabel + " 旧文件清理失败：" + cleanupWarning);
                    }
                } else {
                    // 月度副本始终留在本任务目录；归档输入显式收集，不扫描历史目录。
                    published = storage.checked(task.resolve(name + ".pdf"));
                    storage.moveNew(draft, published);
                    moved = true;
                    committed = true;
                    outputs.add(storage.toUrl(published));
                    archiveInputs.add(published);
                }
            } catch (Exception e) {
                failures.add(reportLabel + "：" + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
                LOG.error("PDF 生成失败，产品：" + id, e);
                try {
                    // 只清理本项草稿和未发布的新文件；正式文件删除前还要检查数据库引用。
                    // 清理失败留警告并继续下一项，不用删除旧文件来掩盖异常。
                    if (draft != null) storage.deleteFile(draft);
                    if (!committed && moved) {
                        if (mode == Mode.FORMAL) products.deleteUnreferencedPdf(storage.toUrl(published));
                        else storage.deleteFile(published);
                    }
                } catch (Exception cleanup) {
                    warnings.add("失败产物清理异常：" + cleanup.getMessage());
                }
            }
        }
        // 正式地址已由各单项事务提交，此时才使报告列表缓存失效；月度副本不触碰该缓存。
        if (mode == Mode.FORMAL && !outputs.isEmpty()) reports.clearPaginateCache();
        try {
            if (!failures.isEmpty()) {
                // 失败记录与本批 PDF 同目录；月度部分成功时一并入包，便于核对缺项。
                Path log = storage.checked(task.resolve("导出失败记录.txt"));
                Files.write(log, failures, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
                archiveInputs.add(log);
            }
            if (mode == Mode.MONTHLY_ARCHIVE && !outputs.isEmpty()) {
                // 仅打包明确的本批输入；archiver 检查压缩退出码并执行 RAR 完整性测试。
                // 压缩失败保留已生成的副本与任务日志，便于排查，不影响正式 PDF。
                archiveUrl = storage.toUrl(archiver.archive(rar, task, archiveInputs, archivePeriod + "月报告单.rar"));
            }
        } catch (Exception e) {
            failures.add("任务收尾失败：" + e.getMessage());
        }
        String message = "成功 " + outputs.size() + " 项，失败 " + failures.size() + " 项";
        return (failures.isEmpty() ? Ret.ok("msg", message) : Ret.fail(message))
                .set("successCount", outputs.size()).set("failCount", failures.size())
                .set("failures", failures).set("warnings", warnings).set("outputs", outputs)
                .set("archiveUrl", archiveUrl).set("taskUrl", storage.toUrl(task));
    }

    /** 本年度月份区间归档；只读业务数据，不更新产品正式 PDF 地址。 */
    public Ret generateMonthRange(String yearValue, String startMonthValue, String endMonthValue) {
        Integer year = integerParameter(yearValue);
        Integer startMonth = integerParameter(startMonthValue);
        Integer endMonth = integerParameter(endMonthValue);
        String error = validateMonthRange(year, startMonth, endMonth, YearMonth.now());
        if (error != null) return Ret.fail(error);

        YearMonth start = YearMonth.of(year, startMonth);
        YearMonth end = YearMonth.of(year, endMonth);
        List<com.jfinal.plugin.activerecord.Record> rows = reports.getIdsByReleaseMonthRange(
                start.atDay(1), end.plusMonths(1).atDay(1));
        if (rows == null || rows.isEmpty()) return Ret.fail("所选月份区间没有可归档的报告单");
        String archivePeriod = start.equals(end) ? start.toString() : start + "至" + end;
        return generate(rows.stream().map(row -> row.getLong("id")).toList(), Mode.MONTHLY_ARCHIVE, archivePeriod);
    }

    static String validateMonthRange(Integer year, Integer startMonth, Integer endMonth, YearMonth currentMonth) {
        if (currentMonth.getMonthValue() == 1) return "今年尚无已结束的月份可供导出";
        if (year == null || year != currentMonth.getYear()) return "仅支持导出本年度报告单，请刷新页面后重试";
        int maxMonth = currentMonth.getMonthValue() - 1;
        if (startMonth == null || endMonth == null || startMonth < 1 || endMonth < 1
                || startMonth > maxMonth || endMonth > maxMonth) {
            return "请选择今年1月至" + maxMonth + "月之间的月份区间";
        }
        if (startMonth > endMonth) return "开始月份不能晚于结束月份";
        return null;
    }

    private static Integer integerParameter(String value) {
        if (value == null || !value.matches("[0-9]+")) return null;
        try { return Integer.valueOf(value); }
        catch (NumberFormatException error) { return null; }
    }

    static Long seriesId(Object value) {
        if (value == null || !value.toString().matches("[1-9][0-9]*")) return null;
        try { return Long.valueOf(value.toString()); }
        catch (NumberFormatException error) { return null; }
    }

    /** 生成入口服务端核验流程完成和签名；不能仅依赖按钮显示条件。 */
    static String validateApprovedReport(Qareport report) {
        Object valid = report.get("vd");
        if (!Boolean.TRUE.equals(valid) && !String.valueOf(QarepConst.VD_VALID).equals(String.valueOf(valid))) return "产品已删除或无效，不能生成报告";
        if (!String.valueOf(QarepConst.INSP_COMPLETED).equals(Objects.toString(report.get("insp"), ""))) return "产品尚未完成批准放行，不能生成报告";
        Map<String, String> stages = new LinkedHashMap<>();
        stages.put("accq", "精度检验");
        if (String.valueOf(QarepConst.LT_STATUS_YES).equals(Objects.toString(report.get("lt_status"), ""))) stages.put("lt", "成品检漏检验");
        stages.put("funq", "外观检验"); stages.put("appq", "包装检验"); stages.put("allq", "批准");
        for (Map.Entry<String, String> stage : stages.entrySet()) {
            String prefix = stage.getKey();
            if (seriesId(report.get(prefix + "_uid")) == null || blank(report.get(prefix + "_name")) || blank(report.get(prefix + "_time"))) {
                return stage.getValue() + "签名信息不完整，请先核对审批记录";
            }
        }
        for (String field : new String[]{"order_id", "formnum", "sp_model", "sp_number", "sc_name", "c_time"}) {
            if (blank(report.get(field))) return "报告基础信息不完整：" + field;
        }
        return null;
    }

    private static boolean blank(Object value) { return value == null || value.toString().isBlank(); }

    /**
     * 显式区分正式发布与归档副本，避免通过目录名或来源字符串推断业务行为。
     */
    public enum Mode {
        /**
         * 发布到 export/siargo/qarep/reports/{版号} 并更新产品 pdfstr，提交后清理无引用旧文件。
         */
        FORMAL,
        /**
         * 生成到 export/siargo/qarep/archives/{月份或月份区间}/{任务ID} 并压缩，不更新产品 pdfstr。
         */
        MONTHLY_ARCHIVE
    }
}
