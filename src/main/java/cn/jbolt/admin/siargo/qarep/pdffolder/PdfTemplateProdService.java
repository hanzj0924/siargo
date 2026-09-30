package cn.jbolt.admin.siargo.qarep.pdffolder;

import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.PdfTemplateProd;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import java.util.List;

/** 系列与报告模板关联；写入由 PdfTemplateService 持有完整事务。 */
public class PdfTemplateProdService extends JBoltBaseService<PdfTemplateProd> {
    private final PdfTemplateProd dao = new PdfTemplateProd().dao();
    @Override protected PdfTemplateProd dao() { return dao; }
    @Override protected int systemLogTargetType() { return ProjectSystemLogTargetType.NONE.getValue(); }

    public List<Record> listForTemplate(Long templateId) {
        return Db.find("SELECT CAST(p.siargo_prod_model_id AS CHAR) id, m.model_series, m.branch_label, m.is_active"
                + " FROM siargo_pdf_template_prod p LEFT JOIN siargo_prod_model m ON m.id=p.siargo_prod_model_id"
                + " WHERE p.siargo_pdf_template_id=? ORDER BY m.model_series,m.catalog_sort,p.siargo_prod_model_id", templateId);
    }

    public boolean hasReferences(Long templateId) {
        return Db.queryLong("SELECT COUNT(*) FROM siargo_pdf_template_prod WHERE siargo_pdf_template_id=?", templateId) > 0;
    }

    public boolean replaceForTemplate(Long templateId, List<Long> modelIds) {
        Db.delete("DELETE FROM siargo_pdf_template_prod WHERE siargo_pdf_template_id=?", templateId);
        for (Long modelId : modelIds) {
            if (!new PdfTemplateProd().set("id", JBoltSnowflakeKit.me.nextId())
                    .set("siargo_pdf_template_id", templateId).set("siargo_prod_model_id", modelId).save()) return false;
        }
        return true;
    }
}
