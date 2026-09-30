package cn.jbolt.admin.siargo.qarep.uploadImport;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import cn.jbolt.common.storage.SiargoStorage;

/** 版号允许 G/2 这样的业务层级，每一级仍须通过文件名单段校验。 */
public final class PdfStoragePaths {
    private PdfStoragePaths() {}

    public static String[] versionSegments(String category, String version, String... tail) {
        if (version == null || version.isBlank() || version.contains("\\")) {
            throw new IllegalArgumentException("版号为空或非法");
        }
        List<String> parts = new ArrayList<>();
        parts.add(SiargoStorage.safeSegment(category));
        for (String part : version.split("/", -1)) parts.add(SiargoStorage.safeSegment(part));
        for (String part : tail) parts.add(SiargoStorage.safeSegment(part));
        return parts.toArray(new String[0]);
    }

    public static Path path(SiargoStorage storage, String category, String version, String... tail) {
        return storage.path(versionSegments(category, version, tail));
    }

    public static String url(SiargoStorage storage, String category, String version, String... tail) {
        return storage.url(versionSegments(category, version, tail));
    }

    /** 模板存储已以 templates 为根目录，不再重复追加该目录段。 */
    public static String[] templateSegments(String version, String... tail) {
        String[] segments = versionSegments("templates", version, tail);
        return Arrays.copyOfRange(segments, 1, segments.length);
    }

    public static Path templatePath(SiargoStorage storage, String version, String... tail) {
        return storage.path(templateSegments(version, tail));
    }

    public static String templateUrl(SiargoStorage storage, String version, String... tail) {
        return storage.url(templateSegments(version, tail));
    }
}
