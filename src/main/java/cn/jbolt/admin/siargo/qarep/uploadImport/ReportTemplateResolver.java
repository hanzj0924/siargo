package cn.jbolt.admin.siargo.qarep.uploadImport;

import java.io.IOException;
import java.nio.file.Path;

import com.jfinal.aop.Inject;
import cn.jbolt.admin.siargo.qarep.pdffolder.PdfTemplateService;
import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.siargo.model.PdfTemplate;
import com.jfinal.kit.Ret;

/**
 * 系列与版号唯一决定启用模板；存储组件决定物理目录。
 */
public class ReportTemplateResolver {
    public record ResolvedTemplate(Path path, PdfTemplate template) {}
    @Inject
    private PdfTemplateService templates;

    public ResolvedTemplate resolveSnapshot(SiargoStorage storage, String version, Long seriesId) throws IOException {
        Ret resolved = templates.resolveTemplate(seriesId, version);
        if (resolved.isFail()) throw new IOException(resolved.getStr("msg"));
        PdfTemplate template = resolved.getAs("template");
        return new ResolvedTemplate(PdfTemplateService.templatePath(storage, template), template);
    }
}
