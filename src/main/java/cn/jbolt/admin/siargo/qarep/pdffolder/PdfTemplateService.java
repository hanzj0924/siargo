package cn.jbolt.admin.siargo.qarep.pdffolder;

import cn.jbolt.admin.siargo.qarep.product.ProductSeriesService;
import cn.jbolt.admin.siargo.prodmodel.ProdModelService;
import cn.jbolt.admin.siargo.qarep.uploadImport.PdfRenderer;
import cn.jbolt.admin.siargo.qarep.uploadImport.PdfStoragePaths;
import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.PdfTemplate;
import cn.jbolt.siargo.model.ProdModel;
import cn.jbolt.siargo.model.Product;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/** 按系列及版号管理报告模板；模板与关联实时读取，不再缓存文本匹配规则。 */
public class PdfTemplateService extends JBoltBaseService<PdfTemplate> {
    private static final String TEMPLATE_NOT_BOUND = "template_not_bound";
    /** 仅用于缺参提示的名称，不决定系列适用范围或 PDF 填充值。 */
    private static final Map<String, String> PARAMETER_NAMES;
    static {
        Map<String, String> names = new LinkedHashMap<>();
        names.put("cuc", "整机电流");
        names.put("cucmax", "整机电流24V");
        names.put("cucmin", "整机电流12V");
        names.put("pv", "脉冲电压");
        names.put("thv", "热头电压");
        names.put("zp", "零点内码");
        names.put("la", "本地地址");
        names.put("fl", "故障电平");
        names.put("bv", "电池电压");
        names.put("flow_range", "流量范围");
        PARAMETER_NAMES = Collections.unmodifiableMap(names);
    }
    private final PdfTemplate dao = new PdfTemplate().dao();
    @Inject private PdfFolderService pdfFolderService;
    @Inject private PdfTemplateProdService bindings;
    @Inject private ProdModelService models;
    // 同一进程中的模板文件删除与绑定保存互斥，避免校验文件后被另一个请求移除。
    private static final ReentrantLock TEMPLATE_FILES_LOCK = new ReentrantLock();
    @Override protected PdfTemplate dao() { return dao; }
    @Override protected int systemLogTargetType() { return ProjectSystemLogTargetType.NONE.getValue(); }

    /** 详情与编辑的大表参数共用系列关联规则，不限制报告版号或模板启用状态。 */
    public boolean isLargeMeterSeries(Long seriesId) {
        if (seriesId == null || seriesId <= 0) return false;
        return !findLargeMeterBindings(seriesId).isEmpty();
    }

    /** 编辑页一次读取系列清单，切换系列时无需另发请求；ID 保持字符串以免丢失精度。 */
    public List<String> findLargeMeterSeriesIds() {
        return findLargeMeterBindings(null).stream()
                .map(row -> String.valueOf(row.getLong("siargo_prod_model_id"))).toList();
    }

    private List<PdfTemplate> findLargeMeterBindings(Long seriesId) {
        String sql = "SELECT DISTINCT p.siargo_prod_model_id FROM siargo_pdf_template t"
                + " INNER JOIN siargo_pdf_template_prod p ON p.siargo_pdf_template_id=t.id"
                + " WHERE t.template_file IN (?, ?) AND p.siargo_prod_model_id>0";
        if (seriesId != null) {
            return dao.find(sql + " AND p.siargo_prod_model_id=? LIMIT 1",
                    "中低压模板.pdf", "工业表模板.pdf", seriesId);
        }
        return dao.find(sql, "中低压模板.pdf", "工业表模板.pdf");
    }

    public Ret resolveTemplate(Long modelId, String version) {
        if (modelId == null || modelId <= 0) return fail("产品未关联系列，请先编辑产品选择系列");
        if (version == null || version.isBlank()) return fail("产品未选择报告版号");
        ProdModel model = models.findById(modelId);
        if (model == null) return fail("产品关联的系列不存在，请重新选择系列");
        if (!enabled(model.get("is_active"))) return fail("产品关联的系列已停用：" + model.getStr("model_series"));
        if (pdfFolderService.getByVersion(version) == null) return fail("报告版号未配置或未启用：" + version);
        List<PdfTemplate> candidates = dao.find("SELECT DISTINCT t.* FROM siargo_pdf_template t"
                + " INNER JOIN siargo_pdf_template_prod p ON p.siargo_pdf_template_id=t.id"
                + " WHERE p.siargo_prod_model_id=? AND t.pdfver=? ORDER BY t.id", modelId, version);
        return selectUniqueTemplate(candidates, model.getStr("model_series"), version);
    }

    /** 显式拒绝歧义；不通过行顺序、文件名或型号推测模板。 */
    public static Ret selectUniqueTemplate(List<PdfTemplate> candidates, String series, String version) {
        String scope = "系列「" + series + "」、版号「" + version + "」";
        if (candidates == null || candidates.isEmpty()) {
            return Ret.fail(scope + "尚未关联报告模板").set("code", TEMPLATE_NOT_BOUND);
        }
        List<PdfTemplate> active = candidates.stream().filter(t -> enabled(t.get("is_active"))).toList();
        if (active.isEmpty()) return Ret.fail(scope + "关联的报告模板均已停用");
        if (active.size() != 1) return Ret.fail(scope + "关联了多个启用模板，请先修正模板配置");
        return Ret.ok().set("template", active.get(0));
    }

    public static Path templatePath(SiargoStorage storage, PdfTemplate template) throws IOException {
        String file = template.getStr("template_file");
        if (file == null || file.isBlank()) throw new IOException("报告模板未配置文件名");
        Path path = PdfStoragePaths.path(storage, "templates", template.getStr("pdfver"), SiargoStorage.safeSegment(file));
        if (!Files.isRegularFile(path)) throw new IOException("报告模板文件不存在：" + file);
        return path;
    }

    /** 参与正式发布事务；锁序与模板保存一致，阻止渲染后配置变化仍发布旧模板。 */
    public Ret validatePublication(Long seriesId, String version, PdfTemplate expected) {
        if (seriesId == null || expected == null || expected.getLong("id") == null) return fail("报告模板快照缺失，请重新生成");
        Record folder = Db.findFirst("SELECT id,is_active FROM siargo_pdf_folder WHERE pdfver=? FOR UPDATE", version);
        if (folder == null || !enabled(folder.get("is_active"))) return fail("报告版号已停用或删除，请重新生成");
        PdfTemplate locked = dao.findFirst("SELECT * FROM siargo_pdf_template WHERE id=? FOR UPDATE", expected.getLong("id"));
        if (locked == null || !enabled(locked.get("is_active"))
                || !Objects.equals(version, locked.getStr("pdfver"))
                || !Objects.equals(expected.getStr("template_file"), locked.getStr("template_file"))) {
            return fail("报告模板配置已变化，请重新生成");
        }
        Record series = Db.findFirst("SELECT id,model_series,is_active FROM siargo_prod_model WHERE id=? FOR UPDATE", seriesId);
        if (series == null || !enabled(series.get("is_active"))) return fail("产品系列已停用或删除，请重新生成");
        List<PdfTemplate> current = dao.find("SELECT DISTINCT t.* FROM siargo_pdf_template t"
                + " JOIN siargo_pdf_template_prod b ON b.siargo_pdf_template_id=t.id"
                + " WHERE b.siargo_prod_model_id=? AND t.pdfver=? FOR UPDATE", seriesId, version);
        Ret unique = selectUniqueTemplate(current, series.getStr("model_series"), version);
        if (unique.isFail()) return unique;
        PdfTemplate selected = unique.getAs("template");
        if (!Objects.equals(expected.getLong("id"), selected.getLong("id"))) return fail("系列关联模板已变化，请重新生成");
        try { templatePath(SiargoStorage.forReportResources(), selected); }
        catch (IOException error) { return fail("报告模板文件已不可用，请重新生成"); }
        return Ret.ok();
    }

    /** 只读提示入口；调用方须传入 ReportFieldMapper 生成的最终字段，不能传原始产品参数。 */
    public List<String> collectProductWarnings(Product product, Map<String, String> reportFields) {
        List<String> warnings = new ArrayList<>();
        if (product == null) return warnings;
        try {
            Ret resolution = resolveTemplate(product.getLong("siargo_prod_model_id"), product.getStr("pdfver"));
            if (resolution.isFail()) {
                if (!TEMPLATE_NOT_BOUND.equals(resolution.getStr("code"))) warnings.add(resolution.getStr("msg"));
                return warnings;
            }
            PdfTemplate template = resolution.getAs("template");
            Set<String> names = PdfRenderer.fieldNames(templatePath(SiargoStorage.forReportResources(), template));
            warnings.addAll(missingValueWarnings(names, reportFields));
        } catch (Exception error) {
            warnings.add("报告模板检查未通过：" + readable(error));
        }
        return warnings;
    }

    /** 只提示模板实际包含、业务实际填写且最终值为空的参数；未映射字段保留模板预设值。 */
    public static List<String> missingValueWarnings(Set<String> fieldNames, Map<String, String> reportFields) {
        List<String> missing = new ArrayList<>();
        for (Map.Entry<String, String> field : PARAMETER_NAMES.entrySet()) {
            String key = field.getKey();
            if (!fieldNames.contains(key) || !reportFields.containsKey(key)) continue;
            String value = reportFields.get(key);
            if (value == null || value.isBlank()) missing.add(field.getValue());
        }
        List<String> warnings = new ArrayList<>();
        if (!missing.isEmpty()) warnings.add("缺少参数：" + String.join("、", missing));
        return warnings;
    }

    /** 关联实时查库；此入口保留用于清理版号元数据缓存。 */
    public void clearCache() { pdfFolderService.clearCache(); }

    public List<Map<String, Object>> listTemplates(String version) {
        List<Map<String, Object>> result = new ArrayList<>();
        String directory = pdfFolderService.getTemplatePath(version);
        if (directory == null) return result;
        File dir = SiargoStorage.forReportResources().resolveUrl(directory).toFile();
        File[] files = dir.listFiles((d, name) -> name.toLowerCase(Locale.ROOT).endsWith(".pdf"));
        if (files == null) return result;
        Arrays.sort(files, Comparator.comparing(File::getName));
        for (File file : files) result.add(Map.of("fileName", file.getName(), "fileSize", file.length()));
        return result;
    }

    public Ret uploadTemplate(String version, UploadFile file) {
        if (file == null) return fail("请选择文件");
        SiargoStorage storage = SiargoStorage.forReportResources();
        try {
            String directory = pdfFolderService.getTemplatePath(version);
            if (directory == null) return fail("版号未配置或未启用");
            String name = SiargoStorage.safeSegment(file.getOriginalFileName());
            if (!name.toLowerCase(Locale.ROOT).endsWith(".pdf")) return fail("仅支持 PDF 文件");
            Path source = storage.checked(file.getFile().toPath());
            if (!source.startsWith(storage.path("imports"))) return fail("模板上传来源非法");
            PdfRenderer.fieldNames(source);
            Path target = storage.checked(storage.resolveUrl(directory).resolve(name));
            storage.moveNew(source, target);
            return Ret.ok();
        } catch (Exception error) { return fail("模板上传失败：" + readable(error)); }
    }

    public Ret deleteTemplate(String version, String fileName) {
        TEMPLATE_FILES_LOCK.lock();
        try {
            String directory = pdfFolderService.getTemplatePath(version);
            if (directory == null) return fail("版号未配置或未启用");
            if (Db.queryLong("SELECT COUNT(*) FROM siargo_pdf_template WHERE pdfver=? AND template_file=?", version, fileName) > 0)
                return fail("该文件仍被报告模板引用，请先调整模板");
            SiargoStorage storage = SiargoStorage.forReportResources();
            storage.deleteFile(storage.resolveUrl(directory + "/" + SiargoStorage.safeSegment(fileName)));
            return Ret.ok();
        } catch (Exception error) { return fail("模板删除失败：" + readable(error)); }
        finally { TEMPLATE_FILES_LOCK.unlock(); }
    }

    public Page<Record> paginateRules(String version, int pageNumber, int pageSize) {
        Page<Record> page = Db.paginate(pageNumber, pageSize,
                "SELECT CAST(id AS CHAR) AS id,pdfver,template_file,error_hint,is_active",
                "FROM siargo_pdf_template WHERE pdfver=? ORDER BY template_file,id", version);
        for (Record row : page.getList()) {
            List<Record> associated = bindings.listForTemplate(Long.valueOf(row.getStr("id")));
            List<String> series = associated.stream().map(r -> ProductSeriesService.branchTitle(r.getStr("model_series"), r.getStr("branch_label"))).toList();
            row.set("modelIds", associated.stream().map(r -> r.getStr("id")).toList());
            row.set("modelSeries", series).set("modelNames", String.join("、", series));
        }
        return page;
    }

    /** 全局未关联候选，不区分版号或模板启用状态；排除当前模板自身，保留原有关联的回填能力。 */
    public List<Record> seriesOptions(String version, Long templateId, String keywords, String selectedIds) {
        if (version == null || version.isBlank()) return List.of();
        List<Long> selected;
        try { selected = parseModelIds(selectedIds); } catch (IllegalArgumentException error) { selected = List.of(); }
        StringBuilder sql = new StringBuilder("SELECT CAST(m.id AS CHAR) id,m.model_series modelSeries,m.branch_label branchLabel,m.prod_type prodType,"
                + "CONCAT(m.model_series,CASE WHEN IFNULL(m.branch_label,'')='' OR m.branch_label=m.model_series THEN '' ELSE CONCAT(' - ',m.branch_label) END,"
                + "COALESCE(CONCAT(' · ',d.name),''),CASE WHEN m.is_active=1 THEN '' ELSE '（已停用）' END) name"
                + " FROM siargo_prod_model m LEFT JOIN " + ProductSeriesService.TYPE_LABELS_SQL + " d ON d.type_key='siargo_prod_type'"
                + " AND d.sn COLLATE utf8mb4_general_ci=CAST(m.prod_type AS CHAR) AND d.enable='1' WHERE (");
        List<Object> parameters = new ArrayList<>();
        sql.append("m.is_active=1");
        if (!selected.isEmpty()) {
            sql.append(" OR m.id IN (").append(placeholders(selected.size())).append(")");
            parameters.addAll(selected);
        }
        sql.append(") AND NOT EXISTS (SELECT 1 FROM siargo_pdf_template_prod b"
                + " INNER JOIN siargo_pdf_template t ON t.id=b.siargo_pdf_template_id"
                + " WHERE b.siargo_prod_model_id=m.id AND t.id<>?)");
        parameters.add(templateId == null ? 0L : templateId);
        if (keywords != null && !keywords.isBlank()) {
            sql.append(" AND (m.model_series LIKE ? OR m.branch_label LIKE ? OR d.name LIKE ?)");
            parameters.add("%" + keywords.trim() + "%"); parameters.add("%" + keywords.trim() + "%"); parameters.add("%" + keywords.trim() + "%");
        }
        sql.append(" ORDER BY m.model_series,m.catalog_sort,m.id");
        return Db.find(sql.toString(), parameters.toArray());
    }

    /** 模板与关联共同提交；同版号统一锁定，再锁定系列，防止并发绑定冲突。 */
    public Ret saveRule(PdfTemplate incoming, String selectedIds) {
        TEMPLATE_FILES_LOCK.lock();
        try { return saveRuleWithFileLock(incoming, selectedIds); }
        finally { TEMPLATE_FILES_LOCK.unlock(); }
    }

    private Ret saveRuleWithFileLock(PdfTemplate incoming, String selectedIds) {
        if (incoming == null) return fail("模板参数错误");
        final List<Long> ids;
        final String version, file;
        final int active;
        try {
            ids = parseModelIds(selectedIds);
            version = incoming.getStr("pdfver");
            PdfStoragePaths.versionSegments("templates", version);
            file = SiargoStorage.safeSegment(incoming.getStr("template_file"));
            if (version.length() > 10 || file.length() > 100 || Objects.toString(incoming.get("error_hint"), "").trim().length() > 50)
                return fail("版号、文件名或备注超出允许长度");
            if (!file.toLowerCase(Locale.ROOT).endsWith(".pdf")) return fail("请选择 PDF 模板文件");
            Object state = incoming.get("is_active");
            if (!"0".equals(String.valueOf(state)) && !"1".equals(String.valueOf(state))) return fail("模板启用状态无效");
            active = Integer.parseInt(state.toString());
            PdfTemplate check = new PdfTemplate().set("pdfver", version).set("template_file", file);
            PdfRenderer.fieldNames(templatePath(SiargoStorage.forReportResources(), check));
        } catch (Exception error) { return fail("模板参数校验失败：" + readable(error)); }
        final Ret[] result = {Ret.fail("模板保存失败")};
        try {
            boolean committed = Db.tx(() -> {
                Record folder = Db.findFirst("SELECT id,is_active FROM siargo_pdf_folder WHERE pdfver=? FOR UPDATE", version);
                if (folder == null || !enabled(folder.get("is_active"))) { result[0] = fail("版号未配置或未启用"); return false; }
                PdfTemplate previous = incoming.getLong("id") == null ? null : dao.findFirst("SELECT * FROM siargo_pdf_template WHERE id=? FOR UPDATE", incoming.getLong("id"));
                if (incoming.getLong("id") != null && previous == null) { result[0] = fail("报告模板不存在"); return false; }
                if (previous != null && !version.equals(previous.getStr("pdfver"))) { result[0] = fail("模板版号不能直接修改，请在目标版号新增模板"); return false; }
                List<Record> oldBindings = previous == null ? List.of() : bindings.listForTemplate(previous.getLong("id"));
                Set<Long> oldIds = new HashSet<>();
                for (Record item : oldBindings) oldIds.add(Long.valueOf(item.getStr("id")));
                SortedSet<Long> affected = new TreeSet<>(ids); affected.addAll(oldIds);
                Map<Long, Record> locked = new HashMap<>();
                for (Long id : affected) {
                    Record model = Db.findFirst("SELECT id,model_series,is_active FROM siargo_prod_model WHERE id=? FOR UPDATE", id);
                    if (model != null) locked.put(id, model);
                }
                for (Long id : ids) {
                    Record model = locked.get(id);
                    if (model == null || (!enabled(model.get("is_active")) && !oldIds.contains(id))) {
                        result[0] = fail("关联系列不存在或已停用，请刷新系列列表"); return false;
                    }
                    if (active == 1) {
                        Long count = Db.queryLong("SELECT COUNT(*) FROM siargo_pdf_template_prod p INNER JOIN siargo_pdf_template t ON t.id=p.siargo_pdf_template_id"
                                + " WHERE p.siargo_prod_model_id=? AND t.pdfver=? AND t.is_active=1 AND t.id<>?", id, version, previous == null ? 0L : previous.getLong("id"));
                        if (count > 0) { result[0] = fail("系列「" + model.getStr("model_series") + "」在版号「" + version + "」已有启用模板"); return false; }
                    }
                }
                PdfTemplate target = previous == null ? new PdfTemplate().set("id", JBoltSnowflakeKit.me.nextId()) : previous;
                target.set("pdfver", version).set("template_file", file).set("error_hint", Objects.toString(incoming.get("error_hint"), "").trim()).set("is_active", active);
                if (!(previous == null ? target.save() : target.update()) || !bindings.replaceForTemplate(target.getLong("id"), ids)) return false;
                result[0] = Ret.ok().set("id", target.getLong("id").toString());
                return true;
            });
            if (committed) { clearCache(); return result[0]; }
            return result[0].isFail() ? result[0] : fail("模板保存失败");
        } catch (Exception error) { return fail("模板保存失败：" + readable(error)); }
    }

    public Ret deleteRule(Long id) {
        if (id == null || id <= 0) return fail("请选择报告模板");
        final Ret[] result = {Ret.fail("模板删除失败")};
        try {
            boolean committed = Db.tx(() -> {
                PdfTemplate template = dao.findFirst("SELECT * FROM siargo_pdf_template WHERE id=? FOR UPDATE", id);
                if (template == null) { result[0] = fail("报告模板不存在"); return false; }
                if (bindings.hasReferences(id)) { result[0] = fail("模板仍有关联系列，请先编辑模板解除关联"); return false; }
                if (!template.delete()) return false;
                result[0] = Ret.ok(); return true;
            });
            if (committed) { clearCache(); return result[0]; }
            return result[0].isFail() ? result[0] : fail("模板删除失败");
        } catch (Exception error) { return fail("模板删除失败：" + readable(error)); }
    }

    @Override public String checkCanDelete(PdfTemplate template, Kv kv) {
        return bindings.hasReferences(template.getLong("id")) ? "模板仍有关联系列" : checkInUse(template, kv);
    }

    public static List<Long> parseModelIds(String value) {
        if (value == null || value.isBlank()) return List.of();
        Set<Long> ids = new LinkedHashSet<>();
        for (String raw : value.split(",", -1)) {
            if (!raw.trim().matches("[1-9][0-9]*")) throw new IllegalArgumentException("系列 ID 格式错误");
            try { ids.add(Long.valueOf(raw.trim())); }
            catch (NumberFormatException error) { throw new IllegalArgumentException("系列 ID 超出范围"); }
        }
        if (ids.size() > 1000) throw new IllegalArgumentException("关联系列数量过多");
        return List.copyOf(ids);
    }

    private static String placeholders(int count) { return String.join(",", Collections.nCopies(count, "?")); }
    private static boolean enabled(Object value) { return Boolean.TRUE.equals(value) || "1".equals(String.valueOf(value)); }
    private static String readable(Exception error) { return error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage(); }
}
