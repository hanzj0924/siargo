package cn.jbolt.admin.siargo.prodmodel;

import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.ProdModel;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 参数值物理删除的型号引用清理；不另开事务，不触发提交前副作用。 */
public class ProdModelValueDeletionService extends JBoltBaseService<ProdModel> {
    private final ProdModel dao = new ProdModel().dao();
    @Inject private ProdModelParamService paramService;

    @Override protected ProdModel dao() { return dao; }
    @Override protected int systemLogTargetType() { return ProjectSystemLogTargetType.NONE.getValue(); }

    /** 调用方先按值 ID 升序锁定待删值，随后在同一事务中调用并按返回值决定回滚。 */
    public boolean removeParameterValues(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return true;
        List<Long> selected = ids.stream().distinct().sorted().toList();
        Set<String> deletedIds = new LinkedHashSet<>();
        selected.forEach(id -> deletedIds.add(id.toString()));
        Map<Long, ProdModel> affected = new LinkedHashMap<>();
        for (ProdModel model : findAffectedModelsForUpdate(selected)) affected.put(model.getLong("id"), model);
        for (ProdModel model : affected.values()) {
            Long modelId = model.getLong("id");
            Map<String, List<String>> references = new LinkedHashMap<>();
            Map<String, String> typeNames = new LinkedHashMap<>(), dictionaryCodes = new LinkedHashMap<>();
            for (Record reference : loadReferenceDetails(modelId)) {
                String typeId = reference.getStr("typeId"), valueId = reference.getStr("valueId");
                references.computeIfAbsent(typeId, ignored -> new ArrayList<>()).add(valueId);
                if (reference.getStr("typeName") != null) typeNames.put(typeId, reference.getStr("typeName"));
                dictionaryCodes.put(valueId, reference.getStr("paramValue"));
            }
            String source = model.getStr("model_desc");
            String updated;
            try { updated = ProdModelValueRemoval.remove(source, deletedIds, references, typeNames, dictionaryCodes); }
            catch (IllegalArgumentException error) { return false; }
            if (!source.equals(updated) && !updateDescription(modelId, updated)) return false;
        }
        return deleteReferences(selected);
    }

    protected List<ProdModel> findAffectedModelsForUpdate(List<Long> ids) {
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        // 此读取必须在调用方的值当前读加锁之后：目标值已锁定，不会新增目标引用。
        // 候选关联不提前加锁，逐个锁型号，避免与型号更新的“型号→关联”顺序反转。
        List<Long> modelIds = Db.query("SELECT DISTINCT model_id FROM siargo_prod_model_param"
                + " WHERE param_value_id IN (" + placeholders + ") ORDER BY model_id", ids.toArray());
        List<ProdModel> models = new ArrayList<>();
        for (Long modelId : modelIds) {
            ProdModel model = dao.findFirst("SELECT * FROM siargo_prod_model WHERE id=? FOR UPDATE", modelId);
            if (model != null) models.add(model);
        }
        return models;
    }

    protected List<Record> loadReferenceDetails(Long modelId) {
        // 型号等待结束后当前读关联，仅锁关联表，不反向锁该型号其他参数值。
        List<Record> references = Db.find("SELECT CAST(param_type_id AS CHAR) typeId,"
                + "CAST(param_value_id AS CHAR) valueId FROM siargo_prod_model_param"
                + " WHERE model_id=? ORDER BY id FOR UPDATE", modelId);
        if (references.isEmpty()) return references;
        String placeholders = String.join(",", Collections.nCopies(references.size(), "?"));
        Map<String, Record> details = new LinkedHashMap<>();
        Object[] valueIds = references.stream().map(row -> row.getStr("valueId")).toArray();
        for (Record value : Db.find("SELECT CAST(v.id AS CHAR) valueId,v.param_value paramValue,t.type_name typeName"
                + " FROM siargo_prod_param_value v LEFT JOIN siargo_prod_param_type t ON t.id=v.param_type_id"
                + " WHERE v.id IN (" + placeholders + ")", valueIds)) details.put(value.getStr("valueId"), value);
        for (Record reference : references) {
            Record detail = details.get(reference.getStr("valueId"));
            if (detail != null) reference.set("typeName", detail.getStr("typeName")).set("paramValue", detail.getStr("paramValue"));
        }
        return references;
    }

    protected boolean updateDescription(Long modelId, String description) {
        return new ProdModel().set("id", modelId).set("model_desc", description).update();
    }

    protected boolean deleteReferences(List<Long> ids) { return paramService.deleteForValues(ids); }
}
