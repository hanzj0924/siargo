package cn.jbolt.admin.siargo.prodmodel;

import cn.jbolt.admin.siargo.qarep.product.ProductSeriesService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;
import cn.jbolt.core.kit.JBoltSnowflakeKit;

import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.aop.Inject;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.admin.siargo.prodparam.ProdParamTypeService;
import cn.jbolt.admin.siargo.prodparam.value.ProdParamValueService;
import cn.jbolt.admin.siargo.qarep.product.ProductService;
import cn.jbolt.siargo.model.ProdModel;
import cn.jbolt.siargo.model.ProdParamType;
import cn.jbolt.siargo.model.ProdParamValue;

/**
 * 产品型号 Service
 * <p>model_desc 采用 JSON 数组格式存储交错布局：字面文本与 {"t":typeId} 参数占位交替排列，
 * 如 ["FS11","-",{"t":1},"F",{"t":2}]；数组顺序即展示/编辑顺序。
 * model_series 保留 PDF 原系列；同系列的独立产品按 branch_label 区分，型号结构保持其固定字符及参数顺序。
 * 主表与附表多步批量写，一律 Db.tx 事务内完成，Ret.fail 软失败经 lambda 返回 false 回滚。
 *
 * @ClassName: ProdModelService
 * @author: hanzj
 * @date: 2026-09-02 16:16
 */
public class ProdModelService extends JBoltBaseService<ProdModel> {
    private final ProdModel dao = new ProdModel().dao();

    @Override
    protected ProdModel dao() {
        return dao;
    }

    /**
     * 系列合法字符：字母数字开头，可含-和/，总长1~50（按录入原样保存，保留大小写）
     */
    private static final Pattern SERIES_PATTERN = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9/-]{0,49}$");

    /**
     * 产品简介 / 产品特点长度上限，与前端 textarea maxlength 一致
     */
    private static final int MAX_TEXT_LEN = 10000;

    @Inject
    private ProdParamTypeService typeService;
    @Inject
    private ProdParamValueService valueService;
    @Inject
    private ProdModelParamService paramService;
    @Inject
    private ProdModelDimensionService dimensionService;
    @Inject
    private ProdTechnicalParamService technicalService;
    @Inject
    private ProductService productService;

    /**
     * 解析过程中的段位数据
     */
    private static class SegData {
        Long typeId;
        List<Long> valueIds;
    }

    /**
     * 后台管理分页查询
     *
     * @param pageNumber
     * @param pageSize
     * @param keywords
     * @return
     */
    public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
        // 一个种类可关联多个参数值，先按“型号+种类”消重，再聚合完整的种类名称。
        String typeNamesSql = "(SELECT mp.model_id,"
                + "GROUP_CONCAT(pt.type_name ORDER BY pt.sort_rank ASC,pt.id ASC SEPARATOR '-') AS type_names,"
                + "GROUP_CONCAT(pt.id ORDER BY pt.sort_rank ASC,pt.id ASC SEPARATOR ',') AS type_ids "
                + "FROM (SELECT DISTINCT model_id,param_type_id FROM siargo_prod_model_param) mp "
                + "INNER JOIN siargo_prod_param_type pt ON pt.id=mp.param_type_id AND pt.is_active=1 GROUP BY mp.model_id)";
        Sql sql = Sql.mysql()
                .select("CAST(pm.id AS CHAR) AS id", "pm.model_series",
                        "pm.catalog_group", "pm.branch_label", "pm.catalog_sort",
                        "CAST((SELECT MIN(sm.id) FROM siargo_prod_model sm WHERE sm.model_series=pm.model_series) AS CHAR) AS selection_model_id",
                        "pm.model_desc", "td.type_ids AS selection_type_ids",
                        "pm.prod_type", ProductSeriesService.typeNameSql("pm", "d_type") + " AS prod_type_name", "pm.is_active", "pm.remark")
                .page(pageNumber, pageSize)
                .from("siargo_prod_model", "pm")
                .leftJoin(typeNamesSql, "td", "td.model_id=pm.id")
                .leftJoin(ProductSeriesService.TYPE_LABELS_SQL, "d_type", "d_type.type_key = 'siargo_prod_type' "
                        + "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(pm.prod_type AS CHAR) "
                        + "AND d_type.enable = '1'")
                .likeMulti(keywords, "pm.model_series", "pm.branch_label", "pm.model_desc", "pm.remark", "td.type_names")
                .orderBySql("pm.prod_type ASC,pm.model_series ASC,pm.catalog_sort ASC,pm.id ASC");
        Page<Record> page = paginateRecord(sql, true);
        if (!page.getList().isEmpty()) {
            Map<String, String> typeNames = new HashMap<>();
            for (ProdParamType type : typeService.getListAll()) {
                typeNames.put(String.valueOf(type.getLong("id")), type.getStr("type_name"));
            }
            for (Record row : page.getList()) {
                Map<String, String> modelTypes = new LinkedHashMap<>();
                String typeIds = row.getStr("selectionTypeIds");
                if (typeIds != null) {
                    for (String typeId : typeIds.split(",")) modelTypes.put(typeId, typeNames.get(typeId));
                }
                for (String typeId : parameterTypeIds(row.getStr("modelDesc"))) modelTypes.put(typeId, typeNames.get(typeId));
                ProdModelLayout.DisplayDescription description = ProdModelLayout.displayDescription(row.getStr("modelSeries"), row.getStr("modelDesc"), modelTypes);
                row.set("modelDescBranches", description.branches()).set("modelDescParts", description.parts())
                        .set("modelDesc", description.summary());
                row.remove("selectionTypeIds");
            }
        }
        return page;
    }

    /**
     * 保存（JSON：series/modelDesc/prodType/isActive/remark/segments[{paramTypeId,valueIds[]}]）
     *
     * @param json
     * @return
     */
    public Ret save(JSONObject json) {
        if (json == null) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        // ===== 事务外解析与校验 =====
        String err = checkBaseParams(json);
        if (err != null) {
            return fail(err);
        }
        String series = json.getString("series").trim();
        String modelDesc = ProdModelLayout.editDescription(json.getString("modelDesc"));
        if (!validDescriptionSize(modelDesc)) {
            return fail("型号描述数据格式错误");
        }
        Integer prodType = json.getInteger("prodType");
        Integer isActive = json.getInteger("isActive");
        String remarkVal = text(json, "remark");
        String introVal = text(json, "productIntro");
        String featuresVal = text(json, "productFeatures");
        List<SegData> segList = new ArrayList<>();
        String segErr = parseSegments(json.getJSONArray("segments"), segList);
        if (segErr != null) {
            return fail(segErr);
        }
        String layoutErr = validateLayout(modelDesc, segList);
        if (layoutErr != null) return fail(layoutErr);
        String canonicalDesc = ProdModelSelectionSchema.dictionaryDescription(modelDesc, currentTypeNames());
        // ===== 事务内 主表 + 附表（多步批量写，无缓存/事件/文件副作用） =====
        final String[] failure = {"操作失败"};
        boolean ok = Db.tx(() -> {
            if (!lockReferencedValues(segList)) return false;
            ProdModel prodModel = new ProdModel();
            // 主子表共享同一预先生成的雪花ID，不依赖运行配置是否开启自动赋值。
            prodModel.set("id", JBoltSnowflakeKit.me.nextId());
            prodModel.setModelSeries(series).setModelDesc(canonicalDesc).setProdType(prodType).setIsActive(isActive).setRemark(remarkVal)
                    .setProductIntro(introVal).setProductFeatures(featuresVal);
            applyCatalogMetadata(prodModel, json);
            String identityError = validateSeriesBranch(series, prodModel.getStr("branch_label"), 0L);
            if (identityError != null) { failure[0] = identityError; return false; }
            if (!prodModel.save()) {
                return false;
            }
            return saveParams(prodModel.getId(), segList);
        });
        if (!ok) {
            return fail(failure[0]);
        }
        return SUCCESS;
    }

    /**
     * 更新（JSON 同 save，另含 id）
     *
     * @param json
     * @return
     */
    public Ret update(JSONObject json) {
        if (json == null) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        Long id = parseLongObject(json.get("id"));
        if (notOk(id)) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        if (findById(id) == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        // ===== 事务外解析与校验 =====
        String err = checkBaseParams(json);
        if (err != null) {
            return fail(err);
        }
        String series = json.getString("series").trim();
        String modelDesc = ProdModelLayout.editDescription(json.getString("modelDesc"));
        if (!validDescriptionSize(modelDesc)) {
            return fail("型号描述数据格式错误");
        }
        Integer prodType = json.getInteger("prodType");
        Integer isActive = json.getInteger("isActive");
        String remarkVal = text(json, "remark");
        String introVal = text(json, "productIntro");
        String featuresVal = text(json, "productFeatures");
        List<SegData> segList = new ArrayList<>();
        String segErr = parseSegments(json.getJSONArray("segments"), segList);
        if (segErr != null) {
            return fail(segErr);
        }
        String layoutErr = validateLayout(modelDesc, segList);
        if (layoutErr != null) return fail(layoutErr);
        String canonicalDesc = ProdModelSelectionSchema.dictionaryDescription(modelDesc, currentTypeNames());
        // ===== 事务内 主表更新 + 附表删插 =====
        final String[] failure = {"操作失败"};
        boolean ok = Db.tx(() -> {
            if (!lockReferencedValues(segList)) return false;
            ProdModel db = dao.findFirst("SELECT * FROM siargo_prod_model WHERE id=? FOR UPDATE", id);
            if (db == null) {
                return false;
            }
            applyCatalogMetadata(db, json);
            String identityError = validateSeriesBranch(series, db.getStr("branch_label"), id);
            if (identityError != null) { failure[0] = identityError; return false; }
            if (!db.setModelSeries(series).setModelDesc(canonicalDesc).setProdType(prodType).setIsActive(isActive).setRemark(remarkVal)
                    .setProductIntro(introVal).setProductFeatures(featuresVal).update()) {
                return false;
            }
            paramService.deleteForModel(id);
            return saveParams(id, segList);
        });
        if (!ok) {
            return fail(failure[0]);
        }
        // 本Service完整持有事务；提交后延迟取得报告服务，避免循环注入并刷新系列分类展示。
        com.jfinal.aop.Aop.get(cn.jbolt.admin.siargo.qarep.QareportService.class).clearFlowCountsCache();
        return SUCCESS;
    }

    /**
     * 切换产品型号启用状态：1 切换为 0，0 或 null 切换为 1
     *
     * @param id 产品型号ID
     * @return 操作结果
     */
    public Ret toggleActive(Long id) {
        if (notOk(id)) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        ProdModel prodModel = findById(id);
        if (prodModel == null) {
            return fail(JBoltMsg.DATA_NOT_EXIST);
        }
        Integer currentActive = prodModel.getIsActive();
        prodModel.setIsActive(currentActive != null && currentActive == 1 ? 0 : 1);
        return ret(prodModel.update());
    }

    /**
     * 校验顶层公共参数（series/prodType/isActive），失败返回错误信息
     *
     * @param json
     * @return
     */
    private String checkBaseParams(JSONObject json) {
        String series = json.getString("series");
        if (notOk(series)) {
            return "型号系列不能为空";
        }
        series = series.trim();
        if (!SERIES_PATTERN.matcher(series).matches()) {
            return "产品系列须以字母或数字开头，仅允许字母、数字、横杠（-）和斜杠（/），长度不超过50个字符";
        }
        Integer prodType = json.getInteger("prodType");
        if (prodType == null || prodType <= 0) {
            return "请选择正确的产品类型";
        }
        Integer isActive = json.getInteger("isActive");
        if (isActive == null || (isActive != 0 && isActive != 1)) {
            return "启用状态参数错误";
        }
        String intro = json.getString("productIntro");
        if (intro != null && intro.trim().length() > MAX_TEXT_LEN) {
            return "产品简介不超过" + MAX_TEXT_LEN + "字";
        }
        String features = json.getString("productFeatures");
        if (features != null && features.trim().length() > MAX_TEXT_LEN) {
            return "产品特点不超过" + MAX_TEXT_LEN + "字";
        }
        return validateCatalogMetadata(json);
    }

    static String validateCatalogMetadata(JSONObject json) {
        String label = text(json, "branchLabel");
        if (label.length() > 100 || label.chars().anyMatch(Character::isISOControl)) return "分支显示名称不超过100字，不能包含控制字符";
        Object rank = json.get("catalogSort");
        if (rank != null && !rank.toString().matches("[0-9]{1,9}")) return "目录排序须为非负整数";
        return null;
    }

    static void applyCatalogMetadata(ProdModel model, JSONObject json) {
        // 目录系列只有一个来源；兼容旧请求的 catalogGroup，但不允许其另建分组。
        String series = json.containsKey("series") ? text(json, "series") : model.getStr("model_series");
        model.set("catalog_group", series);
        if (json.containsKey("branchLabel")) model.set("branch_label", text(json, "branchLabel").isEmpty() ? null : text(json, "branchLabel"));
        if (json.containsKey("catalogSort")) model.set("catalog_sort", json.get("catalogSort") == null ? 0 : json.getInteger("catalogSort"));
    }

    private String validateSeriesBranch(String series, String branchLabel, Long excludeId) {
        boolean hasSibling = Db.queryLong("SELECT COUNT(*) FROM siargo_prod_model WHERE model_series=? AND id<>?", series, excludeId) > 0;
        String error = validateBranchLabel(branchLabel, hasSibling);
        if (error != null) return error;
        if (Db.queryLong("SELECT COUNT(*) FROM siargo_prod_model WHERE model_series=? AND IFNULL(branch_label,'')=? AND id<>?",
                series, branchLabel == null ? "" : branchLabel.trim(), excludeId) > 0) return "该系列下的分支名称已存在";
        return null;
    }

    static String validateBranchLabel(String branchLabel, boolean hasSibling) {
        return hasSibling && (branchLabel == null || branchLabel.isBlank()) ? "同一系列包含多个独立产品，请填写分支名称" : null;
    }

    /**
     * 读取长文本字段并 trim，缺省返回空串（数据库列为 NULL，统一存空串便于回显）
     *
     * @param json
     * @param key
     * @return
     */
    private static String text(JSONObject json, String key) {
        String v = json.getString(key);
        return v == null ? "" : v.trim();
    }

    /**
     * 解析并校验段位：允许重复种类，各段独立选值；关联按种类汇总去重。
     * 种类必须启用、每段至少一个值、值必须属于该种类且启用。
     *
     * @param segments       请求段位数组
     * @param segOut         解析结果输出
     * @return 错误信息，null 表示通过
     */
    private String parseSegments(JSONArray segments, List<SegData> segOut) {
        if (segments == null || segments.isEmpty()) {
            return null;
        }
        Map<Long, LinkedHashSet<Long>> valuesByType = new LinkedHashMap<>();
        if (segments.size() > 480) return "参数种类数量过多";
        for (int i = 0; i < segments.size(); i++) {
            if (!(segments.get(i) instanceof JSONObject)) return "段位数据格式错误";
            JSONObject seg = segments.getJSONObject(i);
            if (seg == null) {
                return "段位数据格式错误";
            }
            Long typeId = parseLongObject(seg.get("paramTypeId"));
            if (typeId == null) {
                return "段位参数种类格式错误";
            }
            if (!typeService.isActiveType(typeId)) {
                return "参数种类不存在或已停用";
            }
            JSONArray valueIds = seg.getJSONArray("valueIds");
            if (valueIds == null || valueIds.isEmpty() || valueIds.size() > 2400) {
                return "每个段位至少要选择一个参数值";
            }
            Set<Long> vset = new LinkedHashSet<>();
            for (int j = 0; j < valueIds.size(); j++) {
                Long vid = parseLongObject(valueIds.get(j));
                if (vid == null) {
                    return "参数值格式错误";
                }
                if (!valueService.isActiveValueOfType(typeId, vid)) {
                    return "参数值不属于该种类或已停用";
                }
                if (!vset.add(vid)) return "同一参数种类不能重复引用参数值";
            }
            valuesByType.computeIfAbsent(typeId, ignored -> new LinkedHashSet<>()).addAll(vset);
        }
        for (var entry : valuesByType.entrySet()) {
            SegData sd = new SegData();
            sd.typeId = entry.getKey();
            sd.valueIds = new ArrayList<>(entry.getValue());
            segOut.add(sd);
        }
        return null;
    }

    /**
     * 事务内批量写入型号-参数附表。
     *
     * @param modelId
     * @param segs
     * @return
     */
    private boolean saveParams(Long modelId, List<SegData> segs) {
        for (SegData seg : segs) {
            for (Long vid : seg.valueIds) {
                if (!paramService.saveReference(modelId, seg.typeId, vid)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** 与值删除统一先锁值、后锁型号，防止过期表单重新写入已物理删除的值。 */
    private boolean lockReferencedValues(List<SegData> segments) {
        Map<Long, Long> expected = new LinkedHashMap<>();
        for (SegData segment : segments) for (Long valueId : segment.valueIds) expected.put(valueId, segment.typeId);
        if (expected.isEmpty()) return true;
        List<ProdParamValue> current = valueService.listValueRecordsByIdsForUpdate(expected.keySet());
        return current.size() == expected.size() && current.stream().allMatch(value ->
                Boolean.TRUE.equals(value.getBoolean("is_active"))
                        && java.util.Objects.equals(expected.get(value.getLong("id")), value.getLong("param_type_id")))
                && segments.stream().allMatch(segment -> typeService.isActiveType(segment.typeId));
    }

    /** 有效种类即使暂时没有可用值，也保留空段位供用户在字典中维护后重选。 */
    private static Set<String> parameterTypeIds(String source) {
        Set<String> result = new LinkedHashSet<>();
        if (source == null || source.isBlank()) return result;
        try {
            for (JSONArray branch : ProdModelTokens.branches(source)) for (Object item : branch)
                if (item instanceof JSONObject slot && slot.getString("t") != null) result.add(slot.getString("t"));
        } catch (IllegalArgumentException ignored) {
            // 损坏数据仍由编辑器加载错误与保存校验处理。
        }
        return result;
    }

    private Map<String, String> currentTypeNames() {
        Map<String, String> result = new LinkedHashMap<>();
        for (ProdParamType type : typeService.getListAll())
            result.put(String.valueOf(type.getLong("id")), type.getStr("type_name"));
        return result;
    }

    private String validateLayout(String modelDesc, List<SegData> segments) {
        return ProdModelSelectionSchema.validate(modelDesc, references(segments), dictionaryValues(segments));
    }

    private static boolean validDescriptionSize(String source) {
        return source != null && source.stripLeading().startsWith("[")
                && source.getBytes(StandardCharsets.UTF_8).length <= ProdModelTokens.MAX_BYTES;
    }

    private Map<String, List<String>> references(List<SegData> segments) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (SegData segment : segments) result.put(segment.typeId.toString(), segment.valueIds.stream().map(String::valueOf).toList());
        return result;
    }

    private Map<String, JSONObject> dictionaryValues(List<SegData> segments) {
        Set<Long> ids = new LinkedHashSet<>();
        for (SegData segment : segments) ids.addAll(segment.valueIds);
        Map<String, JSONObject> result = new LinkedHashMap<>();
        if (!ids.isEmpty()) for (ProdParamValue value : valueService.listValueRecordsByIds(ids)) {
            if (!Boolean.TRUE.equals(value.getBoolean("is_active"))) continue;
            JSONObject item = new JSONObject();
            item.put("paramTypeId", String.valueOf(value.getLong("param_type_id")));
            item.put("paramValue", value.getStr("param_value"));
            item.put("description", value.getStr("description"));
            result.put(String.valueOf(value.getLong("id")), item);
        }
        return result;
    }

    /** 只读组合预览：引用先校验，实际编码只从字典查询，忽略前端同名编码字段。 */
    public Ret compose(JSONObject json) {
        if (json == null) return fail(JBoltMsg.PARAM_ERROR);
        try {
            String source = ProdModelLayout.editDescription(json.getString("modelDesc"));
            if (!validDescriptionSize(source)) return fail("型号描述数据格式错误或过长");
            List<SegData> segments = new ArrayList<>();
            String error = parseSegments(json.getJSONArray("segments"), segments);
            if (error != null) return fail(error);
            Map<String, List<String>> refs = references(segments);
            Map<String, JSONObject> values = dictionaryValues(segments);
            error = ProdModelSelectionSchema.validate(source, refs, values);
            if (error != null) return fail(error);
            Map<String, String> names = new LinkedHashMap<>();
            for (ProdParamType type : typeService.getListAll())
                if (refs.containsKey(String.valueOf(type.getLong("id"))))
                    names.put(String.valueOf(type.getLong("id")), type.getStr("type_name"));
            JSONObject schema = ProdModelSelectionSchema.read(source, refs, names);
            String variantKey = json.getString("variantKey");
            if (variantKey == null) {
                Object branchIndex = json.get("branchIndex");
                if (branchIndex != null && !branchIndex.toString().matches("[0-9]+")) return fail("型号分支无效");
                variantKey = "branch_" + (branchIndex == null ? "0" : branchIndex.toString());
            }
            return Ret.ok().set("data", Kv.by("model", ProdModelSelectionSchema.compose(schema, variantKey, json.getJSONObject("choices"), values)));
        } catch (IllegalArgumentException | com.alibaba.fastjson.JSONException | ClassCastException error) {
            return fail(error.getMessage() == null ? "选型参数格式错误" : error.getMessage());
        }
    }

    /**
     * 编辑回显数据（全 id 字符串化防雪花精度丢失；种类和值仅来自当前启用字典）。
     *
     * @param modelId
     * @return {id,series,prodType,isActive,remark,productIntro,productFeatures,modelDesc,segments,dimensions,technicalParams}
     */
    public String getEditDataJson(Long modelId) {
        ProdModel m = findById(modelId);
        if (m == null) {
            return null;
        }
        List<Record> rows = Db.find("select CAST(mp.param_type_id AS CHAR) AS paramTypeId,CAST(mp.param_value_id AS CHAR) AS paramValueId"
                + " from siargo_prod_model_param mp inner join siargo_prod_param_type pt on pt.id=mp.param_type_id"
                + " inner join siargo_prod_param_value pv on pv.id=mp.param_value_id and pv.param_type_id=pt.id and pv.is_active=1"
                + " where mp.model_id=? and pt.is_active=1 order by pt.sort_rank asc,pt.id asc,mp.id asc", modelId);
        //按种类分组收集
        Map<String, List<String>> typeValueMap = new LinkedHashMap<>();
        Set<Long> allValueIds = new LinkedHashSet<>();
        Set<Long> allTypeIds = new LinkedHashSet<>();
        for (Record r : rows) {
            String t = r.getStr("paramTypeId");
            String v = r.getStr("paramValueId");
            typeValueMap.computeIfAbsent(t, k -> new ArrayList<>()).add(v);
            allValueIds.add(Long.parseLong(v));
            allTypeIds.add(Long.parseLong(t));
        }
        for (String typeId : parameterTypeIds(m.getStr("model_desc"))) {
            typeValueMap.computeIfAbsent(typeId, ignored -> new ArrayList<>());
            allTypeIds.add(Long.parseLong(typeId));
        }
        //种类名与值文本映射
        Map<Long, String> typeNameMap = new HashMap<>();
        Map<String, String> currentTypeNames = new LinkedHashMap<>();
        List<ProdParamType> types = typeService.getListAll();
        for (ProdParamType t : types) {
            currentTypeNames.put(String.valueOf(t.getLong("id")), t.getStr("type_name"));
            if (allTypeIds.contains(t.getId())) {
                typeNameMap.put(t.getId(), t.getTypeName());
            }
        }
        Map<Long, ProdParamValue> valueMap = new HashMap<>();
        if (!allValueIds.isEmpty()) {
            for (ProdParamValue v : valueService.listValueRecordsByIds(allValueIds)) {
                valueMap.put(v.getId(), v);
            }
        }
        //组装 JSON
        JSONObject root = new JSONObject();
        root.put("id", String.valueOf(m.getId()));
        root.put("series", m.getModelSeries());
        root.put("catalogGroup", m.getStr("model_series"));
        root.put("branchLabel", m.getStr("branch_label"));
        root.put("catalogSort", m.getInt("catalog_sort"));
        root.put("prodType", m.getProdType());
        root.put("isActive", m.getIsActive());
        root.put("remark", m.getRemark() == null ? "" : m.getRemark());
        root.put("productIntro", m.getProductIntro() == null ? "" : m.getProductIntro());
        root.put("productFeatures", m.getProductFeatures() == null ? "" : m.getProductFeatures());
        root.put("modelDesc", dictionaryEditDescription(m.getStr("model_desc"), currentTypeNames));
        root.put("dimensions", dimensionService.listJsonForModel(modelId));
        root.put("technicalParams", technicalService.listJsonForModel(modelId));
        JSONArray segArr = new JSONArray();
        for (Map.Entry<String, List<String>> e : typeValueMap.entrySet()) {
            Long tid = Long.parseLong(e.getKey());
            if (!typeNameMap.containsKey(tid)) continue;
            JSONObject seg = new JSONObject();
            seg.put("paramTypeId", e.getKey());
            seg.put("typeName", typeNameMap.getOrDefault(tid, ""));
            JSONArray valueIdsArr = new JSONArray();
            JSONArray valuesArr = new JSONArray();
            for (String vs : e.getValue()) {
                ProdParamValue v = valueMap.get(Long.parseLong(vs));
                if (v != null && Boolean.TRUE.equals(v.getBoolean("is_active"))
                        && tid.equals(v.getLong("param_type_id"))) {
                    valueIdsArr.add(vs);
                    JSONObject vo = new JSONObject();
                    vo.put("id", vs);
                    vo.put("paramValue", v.getParamValue());
                    vo.put("description", v.getDescription() == null ? "" : v.getDescription());
                    valuesArr.add(vo);
                }
            }
            seg.put("valueIds", valueIdsArr);
            seg.put("values", valuesArr);
            segArr.add(seg);
        }
        root.put("segments", segArr);
        return serializeEditData(root);
    }

    static String dictionaryEditDescription(String source, Map<String, String> types) {
        try { return ProdModelSelectionSchema.dictionaryDescription(source, types); }
        catch (RuntimeException error) { return source; }
    }

    static String serializeEditData(Object editData) {
        return JSON.toJSONString(editData)
                .replace("&", "\\u0026")
                .replace("<", "\\u003C");
    }

    /** 同一产品系列的完整选型数据，不受列表分页、搜索条件或分支启用状态限制。 */
    public String getSelectionDataJson(ProdModel model) {
        Map<String, String> typeNames = currentTypeNames();
        List<Kv> branches = new ArrayList<>();
        for (ProdModel sibling : catalogModels(model)) {
            branches.add(selectionBranchData(sibling, typeNames,
                    listSelectionsForDisplay(sibling.getLong("id"))));
        }
        return serializeEditData(Kv.by("series", model.getStr("model_series")).set("branches", branches));
    }

    /** 保留每个原始方案及其编码约束，图形合并展示不能改变分支记录或可选组合。 */
    static Kv selectionBranchData(ProdModel model, Map<String, String> typeNames, List<Kv> selections) {
        String branchLabel = model.getStr("branch_label");
        Kv branch = Kv.by("modelId", String.valueOf(model.getLong("id")))
                .set("branchLabel", branchLabel == null ? "" : branchLabel)
                .set("variants", List.of()).set("message", "");
        String source = model.getStr("model_desc");
        if (source == null || source.isBlank()) {
            return branch.set("message", "尚未维护型号描述");
        }
        Map<String, JSONObject> values = new LinkedHashMap<>();
        for (Kv selection : selections) {
            List<Kv> options = selection.getAs("values");
            for (Kv option : options) {
                JSONObject value = new JSONObject();
                value.put("paramTypeId", selection.getStr("typeId"));
                value.put("paramValue", option.getStr("paramValue"));
                value.put("description", option.getStr("description"));
                values.put(option.getStr("id"), value);
            }
        }
        try {
            List<Kv> variants = ProdModelSelectionSchema.displayVariants(source, typeNames, values);
            branch.set("variants", variants);
            if (variants.isEmpty() || variants.stream().allMatch(variant -> {
                List<Kv> tokens = variant.getAs("tokens");
                return tokens.isEmpty();
            })) branch.set("message", "暂无可展示的型号描述，请核对型号参数");
        } catch (RuntimeException error) {
            branch.set("message", "型号描述格式异常，请核对型号参数");
        }
        return branch;
    }

    /**
     * 产品资料展示页只读数据：产品类型名 / 简介段落 / 特点条目 / 机械尺寸图 / 技术参数与原始备注 / 选型段位
     *
     * @param model 产品型号
     * @return 供 Controller setAttrs 直接铺开的数据集
     */
    public Kv getDisplayData(ProdModel model) {
        Kv data = Kv.create();
        String typeName = getProdTypeName(model.getProdType());
        String displayTitle = typeName;
        data.set("prodTypeName", typeName);
        data.set("displayTitle", displayTitle);
        //简介首段自带“型号系列+产品名”时只高亮已有前缀，避免与正文重复
        List<String> introParagraphs = splitTextLines(model.getProductIntro());
        String seriesPrefix = model.getModelSeries() + "系列";
        String introLead = seriesPrefix + displayTitle;
        if (!introParagraphs.isEmpty() && introParagraphs.get(0).startsWith(seriesPrefix)) {
            introLead = seriesPrefix;
            introParagraphs.set(0, introParagraphs.get(0).substring(seriesPrefix.length()));
        }
        data.set("introLead", introLead);
        data.set("introParagraphs", introParagraphs);
        data.set("features", splitTextLines(model.getProductFeatures()));
        data.set("dimensions", dimensionService.listForModel(model.getId()));
        List<ProdModel> catalogModels = catalogModels(model);
        List<Kv> catalogColumns = new ArrayList<>();
        List<Long> catalogIds = new ArrayList<>();
        for (ProdModel sibling : catalogModels) {
            catalogIds.add(sibling.getLong("id"));
            String label = sibling.getStr("branch_label");
            catalogColumns.add(Kv.by("modelId", String.valueOf(sibling.getLong("id")))
                    .set("title", label == null || label.isBlank() ? sibling.getStr("model_series") : label));
        }
        data.set("catalogGroup", model.getStr("model_series"));
        data.set("branchLabel", model.getStr("branch_label"));
        data.set("technicalTable", ProdTechnicalTable.buildCatalog(catalogColumns, technicalService.listForModels(catalogIds)));
        List<Kv> selections = listSelectionsForDisplay(model.getId());
        Map<String, String> typeNames = new LinkedHashMap<>();
        for (Kv selection : selections) {
            String typeId = selection.getStr("typeId");
            typeNames.put(typeId, selection.getStr("typeName"));
        }
        Set<String> emptyTypes = parameterTypeIds(model.getStr("model_desc"));
        if (!emptyTypes.isEmpty()) for (ProdParamType type : typeService.getListAll()) {
            String typeId = String.valueOf(type.getLong("id"));
            if (emptyTypes.contains(typeId)) typeNames.put(typeId, type.getStr("type_name"));
        }
        Map<String, JSONObject> values = new LinkedHashMap<>();
        for (Kv selection : selections) {
            List<Kv> options = selection.getAs("values");
            for (Kv option : options) {
                JSONObject value = new JSONObject();
                value.put("paramTypeId", selection.getStr("typeId"));
                value.put("paramValue", option.getStr("paramValue"));
                value.put("description", option.getStr("description"));
                values.put(option.getStr("id"), value);
            }
        }
        List<Kv> variants;
        try {
            variants = ProdModelSelectionSchema.displayVariants(model.getStr("model_desc"), typeNames, values);
        } catch (RuntimeException error) {
            variants = List.of(Kv.by("key", "invalid").set("name", "型号参数待核对")
                    .set("tokens", List.of()).set("selections", List.of()));
        }
        data.set("selectionTokens", variants.get(0).get("tokens"));
        data.set("selections", variants.get(0).get("selections"));
        data.set("selectionVariants", variants);
        return data;
    }

    private List<ProdModel> catalogModels(ProdModel model) {
        String group = model.getStr("model_series");
        if (group == null || group.isBlank()) return List.of(model);
        return dao.find("SELECT * FROM siargo_prod_model WHERE model_series=? ORDER BY catalog_sort,id", group);
    }

    /**
     * 字典仅提供分类展示名称，缺失时展示系列自身的分类值
     *
     * @param prodType 系列的产品分类值
     * @return 类型名称
     */
    private String getProdTypeName(Integer prodType) {
        if (prodType == null) {
            return "系列类型未设置";
        }
        String name = Db.queryStr("SELECT MIN(name) FROM jb_dictionary WHERE type_key=? AND sn=? AND enable='1'",
                "siargo_prod_type", String.valueOf(prodType));
        return name == null ? "类型（" + prodType + "）" : name;
    }

    /**
     * 多行文本按行拆分，丢弃空行并去掉行首项目符号（避免与模板自带符号重复）
     *
     * @param text 原始文本
     * @return 行列表
     */
    private List<String> splitTextLines(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return lines;
        }
        for (String raw : text.split("\\r\\n|\\r|\\n")) {
            String line = raw.replaceAll("^[\\s\\u3000]*[●•·◆▪○]+[\\s\\u3000]*", "").trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * 选型段位（种类 + 可选值），种类顺序与 model_desc 拼接顺序一致
     *
     * @param modelId 型号ID
     * @return 段位列表，元素含 typeName 与 values（paramValue/description）
     */
    private List<Kv> listSelectionsForDisplay(Long modelId) {
        List<Record> rows = Db.find("SELECT CAST(pt.id AS CHAR) typeId,pt.type_name typeName,CAST(pv.id AS CHAR) paramValueId,"
                + "pv.param_value paramValue,pv.description description"
                + " FROM siargo_prod_model_param mp"
                + " INNER JOIN siargo_prod_param_type pt ON pt.id=mp.param_type_id AND pt.is_active=1"
                + " INNER JOIN siargo_prod_param_value pv ON pv.id=mp.param_value_id AND pv.param_type_id=pt.id AND pv.is_active=1"
                + " WHERE mp.model_id=? ORDER BY pt.sort_rank,pt.id,pv.sort_rank,pv.id", modelId);
        Map<String, String> typeNameMap = new LinkedHashMap<>();
        Map<String, List<Kv>> valueMap = new LinkedHashMap<>();
        for (Record r : rows) {
            String typeId = r.getStr("typeId");
            typeNameMap.putIfAbsent(typeId, r.getStr("typeName"));
            valueMap.computeIfAbsent(typeId, k -> new ArrayList<>()).add(Kv.create()
                    .set("id", r.getStr("paramValueId"))
                    .set("value", r.getStr("paramValue"))
                    .set("paramValue", r.getStr("paramValue"))
                    .set("description", r.getStr("description") == null ? "" : r.getStr("description")));
        }
        List<Kv> segments = new ArrayList<>(typeNameMap.size());
        for (Map.Entry<String, String> e : typeNameMap.entrySet()) {
            segments.add(Kv.create().set("typeId", e.getKey()).set("typeName", e.getValue()).set("values", valueMap.get(e.getKey())));
        }
        return segments;
    }

    /**
     * 删除 指定多个ID（主表 + 选型附表 + 机械尺寸图 + 技术参数事务内联动删除；
     * 图片物理文件在事务提交后清理，事务内只收集路径）
     *
     * @param ids
     * @return
     */
    public Ret deleteByBatchIds(String ids) {
        if (notOk(ids)) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        String[] idStrs = ids.split(",");
        Set<Long> idList = new TreeSet<>();
        for (String s : idStrs) {
            Long id = parseLongObject(s);
            if (id == null || id <= 0) {
                return fail(JBoltMsg.PARAM_ERROR);
            }
            idList.add(id);
        }
        List<String> orphanImages = new ArrayList<>();
        final String[] failure = {"操作失败"};
        boolean ok = Db.tx(() -> {
            for (Long id : idList) {
                ProdModel db = dao.findFirst("SELECT * FROM siargo_prod_model WHERE id=? FOR UPDATE", id);
                if (db == null) {
                    return false;
                }
                String inUse = checkCanDelete(db, null);
                if (inUse != null) {
                    failure[0] = inUse;
                    return false;
                }
                paramService.deleteForModel(id);
                orphanImages.addAll(dimensionService.deleteForModelCascade(id));
                technicalService.deleteForModelCascade(id);
                if (!db.delete()) {
                    return false;
                }
            }
            return true;
        });
        if (!ok) {
            return fail(failure[0]);
        }
        //事务已提交才动磁盘，回滚时不清理
        dimensionService.deleteFilesAfterCommit(orphanImages);
        return SUCCESS;
    }

    /**
     * 宽松解析 Long（接受数字或字符串，空/非法返回 null）
     *
     * @param obj
     * @return
     */
    private Long parseLongObject(Object obj) {
        if (obj == null) {
            return null;
        }
        String s = String.valueOf(obj).trim();
        if (!s.matches("[1-9][0-9]*")) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 删除数据后执行的回调
     *
     * @param prodModel 要删除的model
     * @param kv        携带额外参数一般用不上
     * @return
     */
    @Override
    protected String afterDelete(ProdModel prodModel, Kv kv) {
        //addDeleteSystemLog(prodModel.getId(), JBoltUserKit.getUserId(),prodModel.getModelSeries());
        return null;
    }

    /**
     * 检测是否可以删除
     *
     * @param prodModel 要删除的model
     * @param kv        携带额外参数一般用不上
     * @return
     */
    @Override
    public String checkCanDelete(ProdModel prodModel, Kv kv) {
        return checkInUse(prodModel, kv);
    }

    /**
     * 设置返回二开业务所属的关键systemLog的targetType
     *
     * @return
     */
    @Override
    protected int systemLogTargetType() {
        return ProjectSystemLogTargetType.NONE.getValue();
    }

    /**
     * 检测是否可以删除
     *
     * @param prodModel model
     * @param kv        携带额外参数一般用不上
     * @return
     */
    @Override
    public String checkInUse(ProdModel prodModel, Kv kv) {
        if (prodModel == null) return "型号系列不存在";
        Long id = prodModel.getLong("id");
        // 当前读在等待系列锁之后仍可见刚提交的引用，避免批量删除复用旧快照漏检。
        if (productService.hasSeriesReferencesForUpdate(id))
            return "型号系列已被产品引用（含回收站），不能删除";
        if (Db.queryLong("SELECT id FROM siargo_pdf_template_prod WHERE siargo_prod_model_id=? LIMIT 1 FOR UPDATE", id) != null)
            return "型号系列已关联报告模板，请先解除关联";
        return null;
    }

}
