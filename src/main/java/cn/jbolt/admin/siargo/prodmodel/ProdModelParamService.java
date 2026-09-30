package cn.jbolt.admin.siargo.prodmodel;

import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.ProdModelParam;
import com.jfinal.plugin.activerecord.Db;
import java.util.Collections;
import java.util.List;

/** 产品系列选型值关联；由主表 Service 持有完整事务。 */
public class ProdModelParamService extends JBoltBaseService<ProdModelParam> {
    private final ProdModelParam dao=new ProdModelParam().dao();
    @Override protected ProdModelParam dao() { return dao; }
    @Override protected int systemLogTargetType() { return ProjectSystemLogTargetType.NONE.getValue(); }

    public boolean saveReference(Long modelId, Long typeId, Long valueId) {
        return new ProdModelParam().set("id",JBoltSnowflakeKit.me.nextId()).set("model_id",modelId)
                .set("param_type_id",typeId).set("param_value_id",valueId).save();
    }
    public void deleteForModel(Long modelId) { Db.delete("DELETE FROM siargo_prod_model_param WHERE model_id=?",modelId); }

    /** 物理删除参数值的全部型号关联，必须参与调用方持有的事务。 */
    public boolean deleteForValues(List<Long> valueIds) {
        if (valueIds == null || valueIds.isEmpty()) return true;
        String placeholders = String.join(",", Collections.nCopies(valueIds.size(), "?"));
        return Db.delete("DELETE FROM siargo_prod_model_param WHERE param_value_id IN (" + placeholders + ")",
                valueIds.toArray()) >= 0;
    }
}
