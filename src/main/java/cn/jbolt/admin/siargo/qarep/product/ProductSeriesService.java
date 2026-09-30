package cn.jbolt.admin.siargo.qarep.product;

import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.admin.siargo.prodparam.value.ProdParamValueService;
import cn.jbolt.admin.siargo.prodparam.ProdParamTypeService;
import cn.jbolt.admin.siargo.prodmodel.ProdModelLayout;
import cn.jbolt.admin.siargo.prodmodel.ProdModelTokens;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.ProdModel;
import cn.jbolt.siargo.model.Product;
import cn.jbolt.siargo.model.ProdParamValue;
import cn.jbolt.siargo.model.ProdParamType;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;

/** 报告单的系列读取与导入建议；分类来自系列，字典只提供展示名称。 */
public class ProductSeriesService extends JBoltBaseService<ProdModel> {
    /** 展示名称按分类值合并，重复字典记录不能放大产品列表或统计数量。 */
    public static final String TYPE_LABELS_SQL = "(SELECT sn,MIN(name) name,MIN(sort_rank) sort_rank,'siargo_prod_type' type_key,'1' enable "
            + "FROM jb_dictionary WHERE type_key='siargo_prod_type' AND enable='1' GROUP BY sn)";
    private final ProdModel dao = new ProdModel().dao();
    @Inject private ProdParamValueService valueService;
    @Inject private ProdParamTypeService typeService;
    @Override protected ProdModel dao() { return dao; }
    @Override protected int systemLogTargetType() { return ProjectSystemLogTargetType.NONE.getValue(); }

    public List<Record> options(Long includeId) {
        String typeName = typeNameSql("m", "d");
        List<Record> rows = Db.find("SELECT CAST(m.id AS CHAR) id,m.model_series,m.model_desc,m.branch_label branchLabel,m.prod_type prodType,m.is_active isActive,"
                + typeName + " prodTypeName "
                + "FROM siargo_prod_model m LEFT JOIN " + TYPE_LABELS_SQL + " d "
                + "ON d.sn=CAST(m.prod_type AS CHAR) COLLATE utf8mb4_general_ci WHERE m.is_active=1 OR m.id=? "
                + "ORDER BY m.model_series,m.catalog_sort,m.id", includeId);
        Map<String, String> types = new LinkedHashMap<>();
        for (ProdParamType type : typeService.getListAll()) types.put(String.valueOf(type.getLong("id")), type.getStr("type_name"));
        for (Record row : rows) decorateOption(row, types);
        return rows;
    }

    /** 同一记录的下拉与回显使用相同标签，标签不参与匹配，也不改变所提交的字符串 ID。 */
    static void decorateOption(Record row, Map<String, String> typeNames) {
        String series = row.getStr("model_series"), source = row.getStr("model_desc");
        List<String> structures;
        try {
            ProdModelTokens.branches(source);
            structures = new ArrayList<>(new LinkedHashSet<>(ProdModelLayout.summaryBranches(series, source, typeNames)));
        } catch (IllegalArgumentException invalid) {
            structures = List.of("型号结构待核对");
        }
        String structure = String.join("；", structures);
        Object active = row.get("isActive");
        boolean enabled = Boolean.TRUE.equals(active) || "1".equals(java.util.Objects.toString(active, ""));
        String branchLabel = row.getStr("branchLabel");
        String branchHint = branchLabel == null || branchLabel.isBlank() || branchLabel.equals(series) ? "" : " · " + branchLabel;
        String label = (series == null ? "" : series) + " - " + structure + branchHint + (enabled ? "" : "（已停用）");
        row.set("branchStructures", structures).set("branchStructure", structure)
                .set("displayLabel", label).set("name", label);
    }

    public static String branchTitle(String series, String branchLabel) {
        if (series == null) return "已删除系列";
        return branchLabel == null || branchLabel.isBlank() || branchLabel.equals(series) ? series : series + " - " + branchLabel;
    }

    /** 参数仅为后端固定SQL别名，不接受请求输入。 */
    public static String typeNameSql(String seriesAlias, String labelAlias) {
        return "CASE WHEN " + seriesAlias + ".id IS NULL THEN '未关联系列' WHEN " + seriesAlias
                + ".prod_type IS NULL THEN '系列类型未设置' ELSE COALESCE(" + labelAlias
                + ".name,CONCAT('类型（',CAST(" + seriesAlias + ".prod_type AS CHAR),'）')) END";
    }

    /** sn是看板兼容分组键，值来自系列分类；未知状态使用独立键，不猜测类型。 */
    public static String typeGroupSql(String seriesAlias) {
        return "CASE WHEN " + seriesAlias + ".id IS NULL THEN '' WHEN " + seriesAlias
                + ".prod_type IS NULL THEN '__unclassified__' ELSE CAST(" + seriesAlias + ".prod_type AS CHAR) END";
    }

    public Ret applySeries(Product product, boolean allowInactive) {
        return applySeries(product, allowInactive, false);
    }

    /** 在产品写事务内调用，与系列修改、删除使用同一行锁。 */
    public Ret applySeriesForUpdate(Product product, boolean allowInactive) {
        return applySeries(product, allowInactive, true);
    }

    public void lockSeriesRowsForUpdate(Collection<Long> ids) {
        ids.stream().filter(id -> id != null && id > 0).distinct().sorted()
                .forEach(id -> Db.findFirst("SELECT id FROM siargo_prod_model WHERE id=? FOR UPDATE", id));
    }

    private Ret applySeries(Product product, boolean allowInactive, boolean lock) {
        if (product == null) return Ret.fail("产品信息不能为空");
        Long id;
        try { id = product.getLong("siargo_prod_model_id"); }
        catch (RuntimeException invalid) { return Ret.fail("型号系列ID格式不正确"); }
        if (id == null || id <= 0) return Ret.fail("请选择型号系列");
        // 仅锁系列行，避免多系列录入时同时锁住不同顺序的字典行。
        Record row = findSeries(id, lock);
        if (row == null) return Ret.fail("型号系列不存在");
        if (!allowInactive && !Boolean.TRUE.equals(row.get("is_active")) && !"1".equals(java.util.Objects.toString(row.get("is_active"), "")))
            return Ret.fail("型号系列已停用，请重新选择");
        product.set("siargo_prod_model_id", id);
        return Ret.ok();
    }

    protected Record findSeries(Long id, boolean lock) {
        return Db.findFirst("SELECT id,prod_type,is_active FROM siargo_prod_model WHERE id=?" + (lock ? " FOR UPDATE" : ""), id);
    }

    public Map<String, Object> match(String model, String description) {
        return match(model, description, loadMatchingSeries());
    }

    /** 单次导入复用同一份匹配目录，避免每张产品卡重复查询和解析全部型号结构。 */
    public List<ProductSeriesMatcher.Series> loadMatchingSeries() {
        List<Record> rows = options(null);
        Set<Long> valueIds = new LinkedHashSet<>();
        for (Record row : rows) valueIds.addAll(ProductSeriesMatcher.referencedValueIds(row.getStr("model_desc")));
        Map<String, ProductSeriesMatcher.Parameter> parameters = new LinkedHashMap<>();
        for (ProdParamValue value : valueService.listValueRecordsByIds(valueIds)) {
            Object active = value.get("is_active");
            parameters.put(String.valueOf(value.getLong("id")), new ProductSeriesMatcher.Parameter(
                    String.valueOf(value.getLong("param_type_id")), value.getStr("param_value"),
                    Boolean.TRUE.equals(active) || (active instanceof Number number && number.intValue() == 1)));
        }
        List<ProductSeriesMatcher.Series> series = new ArrayList<>();
        for (Record row : rows) series.add(new ProductSeriesMatcher.Series(row.getStr("id"),
                row.getStr("model_series"), row.getInt("prodType"), row.getStr("prodTypeName"),
                row.getStr("model_desc"), parameters));
        return series;
    }

    public Map<String, Object> match(String model, String description, List<ProductSeriesMatcher.Series> series) {
        ProductSeriesMatcher.Match match = ProductSeriesMatcher.match(model, description, series);
        Map<String, Object> result = new LinkedHashMap<>();
        ProductSeriesMatcher.Series selected = match.selected() == null ? null : match.selected().series();
        result.put("siargo_prod_model_id", selected == null ? null : selected.id());
        result.put("model_series", selected == null ? null : selected.name());
        result.put("prodType", selected == null ? null : selected.prodType());
        result.put("prodTypeName", selected == null ? null : selected.typeName());
        result.put("matchStatus", match.status());
        result.put("matchReason", match.reason());
        List<Map<String, Object>> candidates = new ArrayList<>();
        for (ProductSeriesMatcher.Candidate c : match.candidates()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", c.series().id()); item.put("model_series", c.series().name()); item.put("score", c.score());
            candidates.add(item);
        }
        result.put("candidates", candidates);
        return result;
    }
}
