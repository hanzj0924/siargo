package cn.jbolt.admin.siargo.prodmodel;

import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.ProdTechnicalParam;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Arrays;

/** 产品系列技术参数，数值保留区间、条件及文字。 */
public class ProdTechnicalParamService extends JBoltBaseService<ProdTechnicalParam> {
    private static final String SELECT_FIELDS = "CAST(id AS CHAR) id,CAST(model_id AS CHAR) modelId,param_name paramName,param_value paramValue,unit,sort_rank sortRank,remark,"
            + "catalog_row_key catalogRowKey,catalog_cell_key catalogCellKey,catalog_column_key catalogColumnKey,"
            + "catalog_column_label catalogColumnLabel,catalog_column_sort catalogColumnSort";
    private static final Map<String, String> CATALOG_FIELDS = Map.of(
            "catalogRowKey", "catalog_row_key", "catalogCellKey", "catalog_cell_key",
            "catalogColumnKey", "catalog_column_key", "catalogColumnLabel", "catalog_column_label", "catalogColumnSort", "catalog_column_sort");
    private final ProdTechnicalParam dao=new ProdTechnicalParam().dao();
    @Override protected ProdTechnicalParam dao() { return dao; }
    @Override protected int systemLogTargetType() { return ProjectSystemLogTargetType.PROD_TECHNICAL_PARAM.getValue(); }
    public List<Record> listForModel(Long modelId) {
        return Db.find("SELECT " + SELECT_FIELDS + " FROM siargo_prod_technical_param WHERE model_id=? ORDER BY sort_rank,id",modelId);
    }
    public List<Record> listForModels(Collection<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) return List.of();
        String placeholders = String.join(",", java.util.Collections.nCopies(modelIds.size(), "?"));
        return Db.find("SELECT " + SELECT_FIELDS + " FROM siargo_prod_technical_param WHERE model_id IN (" + placeholders + ") ORDER BY sort_rank,id", modelIds.toArray());
    }
    public boolean belongsTo(Long id,Long modelId) {
        return id==null || (modelId!=null && Db.queryLong("SELECT COUNT(*) FROM siargo_prod_technical_param WHERE id=? AND model_id=?",id,modelId)>0);
    }
    public boolean hasForModel(Long id) { return Db.queryLong("SELECT COUNT(*) FROM siargo_prod_technical_param WHERE model_id=?",id)>0; }
    /** 编辑数据聚合与资料弹窗共用；id 字符串化防雪花精度丢失。 */
    public JSONArray listJsonForModel(Long modelId) {
        JSONArray arr=new JSONArray();
        for(Record r:listForModel(modelId)) {
            JSONObject o=new JSONObject();
            o.put("id",r.getStr("id"));
            o.put("paramName",r.getStr("paramName"));
            o.put("paramValue",r.getStr("paramValue"));
            o.put("unit",r.getStr("unit")==null?"":r.getStr("unit"));
            o.put("sortRank",r.getInt("sortRank"));
            o.put("remark",r.getStr("remark")==null?"":r.getStr("remark"));
            for (String field : CATALOG_FIELDS.keySet()) o.put(field, r.get(field));
            arr.add(o);
        }
        return arr;
    }
    /** 接收 JSON 体（modelId/id/paramName/paramValue/unit/sortRank/remark），字段缺省时由 persist 校验拒绝。 */
    public Ret persistFromJson(JSONObject json,boolean update) {
        if(json==null) return fail("参数错误");
        ProdTechnicalParam input=new ProdTechnicalParam().set("model_id",json.getLong("modelId"))
                .set("param_name",json.getString("paramName")).set("param_value",json.getString("paramValue"))
                .set("unit",json.getString("unit")).set("sort_rank",json.getInteger("sortRank"))
                .set("remark",json.getString("remark"));
        if(update) input.set("id",json.getLong("id"));
        for (var field : CATALOG_FIELDS.entrySet()) if (json.containsKey(field.getKey()))
            input.set(field.getValue(), "catalogColumnSort".equals(field.getKey()) ? json.get(field.getKey()) : json.getString(field.getKey()));
        return persist(input,update);
    }
    public int nextRank(Long modelId) { Integer rank=Db.queryInt("SELECT MAX(sort_rank) FROM siargo_prod_technical_param WHERE model_id=?",modelId); return rank==null?1:rank+1; }

    public Ret persist(ProdTechnicalParam input,boolean update) {
        if(input==null || notOk(input.getLong("model_id")) || (update && notOk(input.getLong("id"))) || (!update && input.getLong("id")!=null)) return fail("参数错误");
        String name=clean(input.getStr("param_name")),value=clean(input.getStr("param_value"));
        String unit=clean(input.getStr("unit")),remark=clean(input.getStr("remark"));
        if(name.isEmpty() || name.length()>200) return fail("参数名称不能为空且不超过200字");
        if(value.length()>16000) return fail("参数数值不超过16000字");
        if(unit.length()>100 || remark.length()>500) return fail("单位或备注超出长度限制");
        String catalogError = validateCatalogMetadata(input);
        if (catalogError != null) return fail(catalogError);
        Integer rank=input.getInt("sort_rank");
        if(rank==null || rank<0) return fail("排序须为非负整数");
        Long modelId=input.getLong("model_id"),id=update?input.getLong("id"):JBoltSnowflakeKit.me.nextId();
        Ret[] result={fail("保存失败")};
        boolean ok=Db.tx(()->{
            if(Db.queryLong("SELECT id FROM siargo_prod_model WHERE id=? FOR UPDATE",modelId)==null) { result[0]=fail("产品系列不存在"); return false; }
            ProdTechnicalParam row=update?findById(id):new ProdTechnicalParam().set("id",id).set("model_id",modelId);
            if(row==null || !modelId.equals(row.getLong("model_id"))) { result[0]=fail("技术参数不存在或不属于当前系列"); return false; }
            mergeCatalogMetadata(row, input, name, update);
            String requestedColumn = row.getStr("catalog_column_key");
            String requestedRow = row.getStr("catalog_row_key");
            if (!clean(requestedRow).isEmpty() && Db.queryLong("SELECT COUNT(*) FROM siargo_prod_technical_param WHERE model_id=? AND catalog_row_key=? AND COALESCE(catalog_column_key,'')=? AND id<>?",
                    modelId, requestedRow, clean(requestedColumn), id) > 0) {
                result[0]=fail("当前技术参数列已存在同一目录参数行，请修改已有记录"); return false;
            }
            row.set("param_name",name).set("param_value",value).set("unit",unit).set("remark",remark).set("sort_rank",rank);
            String valueError = validateValue(value, row);
            if (valueError != null) { result[0]=fail(valueError); return false; }
            if(!(update?row.update():row.save())) return false;
            // 列标题与排序属于整列；单行编辑后同步同型号同列的其他参数，避免表头取到旧值。
            for (String field : List.of("catalog_column_label", "catalog_column_sort")) if (Arrays.asList(input._getAttrNames()).contains(field))
                Db.update("UPDATE siargo_prod_technical_param SET " + field + "=? WHERE model_id=? AND COALESCE(catalog_column_key,'')=?",
                        input.get(field), modelId, clean(requestedColumn));
            if(update) addUpdateSystemLog(id,JBoltUserKit.getUserId(),name); else addSaveSystemLog(id,JBoltUserKit.getUserId(),name);
            result[0]=Ret.ok().set("id",id.toString()); return true;
        });
        return ok?result[0]:(result[0].isFail()?result[0]:fail("保存失败"));
    }
    public Ret remove(Long id) {
        ProdTechnicalParam old=notOk(id)?null:findById(id);
        if(old==null) return fail("技术参数不存在");
        Ret[] result={fail("删除失败")};
        boolean ok=Db.tx(()->{
            Db.queryLong("SELECT id FROM siargo_prod_model WHERE id=? FOR UPDATE",old.getLong("model_id"));
            ProdTechnicalParam row=findById(id);
            if(row==null) {result[0]=fail("技术参数不存在");return false;}
            String error=checkCanDelete(row,null);
            if(error!=null) {result[0]=fail(error);return false;}
            if(!row.delete()) return false;
            addDeleteSystemLog(id,JBoltUserKit.getUserId(),row.getStr("param_name"));
            result[0]=Ret.ok();return true;
        });
        return ok?result[0]:(result[0].isFail()?result[0]:fail("删除失败"));
    }
    /** 级联删除：参与调用方事务，无物理文件需要清理。 */
    public void deleteForModelCascade(Long modelId) {
        Db.delete("DELETE FROM siargo_prod_technical_param WHERE model_id=?",modelId);
    }
    private static String clean(String value) { return value==null?"":value.trim(); }

    /** 单格改名只拆出本格，不借原目录行键改变其他分支的参数语义。 */
    static void mergeCatalogMetadata(ProdTechnicalParam row, ProdTechnicalParam input, String name, boolean update) {
        String oldRowKey = clean(row.getStr("catalog_row_key"));
        String oldName = clean(row.getStr("param_name"));
        for (String field : CATALOG_FIELDS.values()) if (Arrays.asList(input._getAttrNames()).contains(field)) row.set(field, input.get(field));
        if (update && !oldName.equals(clean(name)) && !oldRowKey.isEmpty()
                && oldRowKey.equals(clean(row.getStr("catalog_row_key")))) {
            row.set("catalog_row_key", "manual_" + row.getLong("id")).set("catalog_cell_key", null);
        }
    }

    /** 目录空白格也占据明确的行与列；更新时使用与库中原值合并后的元数据判断。 */
    static String validateValue(String value, ProdTechnicalParam merged) {
        return clean(value).isEmpty() && (clean(merged.getStr("catalog_row_key")).isEmpty()
                || clean(merged.getStr("catalog_column_key")).isEmpty()) ? "普通技术参数的数值不能为空" : null;
    }

    static String validateCatalogMetadata(ProdTechnicalParam input) {
        for (String field : List.of("catalog_row_key", "catalog_cell_key", "catalog_column_key")) {
            if (!Arrays.asList(input._getAttrNames()).contains(field)) continue;
            String key = clean(input.getStr(field));
            if (!key.isEmpty() && !key.matches("[A-Za-z0-9_.:-]{1,80}")) return "目录行、单元格和列标识只允许字母、数字及 _ . : -，且不超过80个字符";
            input.set(field, key.isEmpty() ? null : key);
        }
        if (Arrays.asList(input._getAttrNames()).contains("catalog_column_label")) {
            String label = clean(input.getStr("catalog_column_label"));
            if (label.length() > 100 || label.chars().anyMatch(Character::isISOControl)) return "技术参数列名称不超过100字，不能包含控制字符";
            input.set("catalog_column_label", label.isEmpty() ? null : label);
        }
        if (Arrays.asList(input._getAttrNames()).contains("catalog_column_sort")) {
            Object value = input.get("catalog_column_sort");
            if (value != null && !value.toString().matches("[0-9]{1,9}")) return "技术参数列排序须为非负整数";
            input.set("catalog_column_sort", value == null ? 0 : Integer.parseInt(value.toString()));
        }
        return null;
    }
}
