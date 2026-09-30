package cn.jbolt.admin.siargo.qarep.uploadImport;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/** PDF 渲染只负责模板填充及完整性检查，不执行数据库或历史文件操作。 */
public class PdfRenderer {
    public void render(Path template, Path output, Path font, Map<String, String> values) throws Exception {
        // 使用字节输入，避免 iText 的内存映射在 Windows/JDK 25 下延迟释放文件锁。
        PdfReader reader = new PdfReader(Files.readAllBytes(template));
        try (OutputStream stream = Files.newOutputStream(output, StandardOpenOption.CREATE_NEW)) {
            PdfStamper stamper = new PdfStamper(reader, stream);
            try {
                AcroFields form = stamper.getAcroFields();
                form.addSubstitutionFont(BaseFont.createFont(font + ",0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED));
                if (form.getFields().isEmpty()) throw new IOException("模板没有可填写的表单字段");
                for (Map.Entry<String, String> field : values.entrySet()) {
                    if (form.getFields().containsKey(field.getKey()) && !form.setField(field.getKey(), field.getValue())) {
                        throw new IOException("模板字段填写失败：" + field.getKey());
                    }
                }
                stamper.setFormFlattening(true);
            } finally {
                // close 写入交叉引用表，关闭失败必须向调用方传播，不能发布半成品。
                stamper.close();
            }
        } finally {
            reader.close();
        }
        validate(output);
    }

    public static void validate(Path file) throws IOException {
        if (!Files.isRegularFile(file) || Files.size(file) == 0) throw new IOException("PDF 文件不存在或为空");
        PdfReader reader = new PdfReader(Files.readAllBytes(file));
        try {
            if (reader.getNumberOfPages() < 1) throw new IOException("PDF 没有页面");
            for (int page = 1; page <= reader.getNumberOfPages(); page++) reader.getPageContent(page);
        } finally {
            reader.close();
        }
    }

    /** 只读取得模板表单字段；不得把整页扫描件当作可生成报告的模板。 */
    public static Set<String> fieldNames(Path file) throws IOException {
        validate(file);
        PdfReader reader = new PdfReader(Files.readAllBytes(file));
        try {
            Set<String> fields = Set.copyOf(reader.getAcroFields().getFields().keySet());
            if (fields.isEmpty()) throw new IOException("模板没有可填写的表单字段");
            return fields;
        } finally { reader.close(); }
    }
}
