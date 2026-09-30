package cn.jbolt.common.storage;

import cn.jbolt.core.base.config.JBoltConfig;
import com.jfinal.kit.PathKit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/** Siargo 本地业务文件唯一定位入口。配置相对 Web 根，URL 与磁盘路径不可混用。 */
public final class SiargoStorage {
    public enum Business {
        IMI("imi"), DMS("dms"), EQCERT("eqcert"), QAREP("qarep"), PROD_MODEL("prod_model");

        private final String code;
        Business(String code) { this.code = code; }
        public String configKey() { return "siargo_" + code + (this == QAREP ? "_export_path" : "_upload_path"); }
    }

    private final Path webRoot;
    private final Path root;
    private final String rootUrl;

    /** 不缓存环境相关路径，避免测试/部署切换后仍使用旧根目录。 */
    public static SiargoStorage forBusiness(Business business) {
        return new SiargoStorage(Path.of(PathKit.getWebRootPath()), configuredPaths(), business);
    }

    private static Map<Business, String> configuredPaths() {
        if (JBoltConfig.prop == null) {
            throw new IllegalStateException("业务文件配置尚未加载");
        }
        Map<Business, String> paths = new EnumMap<>(Business.class);
        for (Business item : Business.values()) {
            paths.put(item, JBoltConfig.prop.get(item.configKey()));
        }
        return paths;
    }

    /** Excel 导入暂存沿用已有内部资源目录，不属于成品报告的上传业务。 */
    public static SiargoStorage forReportResources() {
        Map<Business, String> paths = configuredPaths();
        paths.put(Business.QAREP, "upload/siargo/qarep");
        return new SiargoStorage(Path.of(PathKit.getWebRootPath()), paths, Business.QAREP);
    }

    /** 报告模板沿用独立模板根目录，版号目录直接位于该根目录下。 */
    public static SiargoStorage forReportTemplates() {
        Map<Business, String> paths = configuredPaths();
        paths.put(Business.QAREP, "upload/siargo/qarep/templates");
        return new SiargoStorage(Path.of(PathKit.getWebRootPath()), paths, Business.QAREP);
    }

    /** 显式依赖构造器供迁移预检、独立测试复用，与线上入口使用相同校验。 */
    public SiargoStorage(Path webRoot, Map<Business, String> paths, Business business) {
        try {
            this.webRoot = webRoot.toFile().getCanonicalFile().toPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("无法解析 Web 根目录", e);
        }
        Map<Business, Path> roots = new EnumMap<>(Business.class);
        for (Business item : Business.values()) {
            String configured = paths.get(item);
            if (configured == null || configured.isBlank()) {
                throw new IllegalArgumentException("缺少业务文件路径配置：" + item.configKey());
            }
            String relative = validateRelative(configured);
            Path resolved = this.webRoot.resolve(relative).normalize();
            verifyContained(this.webRoot, resolved, false);
            for (Path other : roots.values()) {
                if (resolved.startsWith(other) || other.startsWith(resolved)) {
                    throw new IllegalArgumentException("不同业务的文件目录不能相同或相互包含");
                }
            }
            roots.put(item, resolved);
        }
        root = roots.get(business);
        rootUrl = "/" + this.webRoot.relativize(root).toString().replace('\\', '/');
    }

    public static void validateConfiguration() {
        forBusiness(Business.QAREP);
    }

    public Path root() { return checked(root); }

    /** segments 可包含分层相对路径（如 G/2），但每一段必须是合法文件名。 */
    public Path path(String... segments) {
        Path result = root;
        for (String segment : segments) {
            result = result.resolve(validateRelative(segment));
        }
        return checked(result);
    }

    public String url(String... segments) { return toUrl(path(segments)); }

    public String toUrl(Path file) {
        Path relative = root.relativize(checked(file));
        return relative.toString().isEmpty() ? rootUrl
                : rootUrl + "/" + relative.toString().replace('\\', '/');
    }

    /** 数据库存储的 Web 相对 URL；同时兼容有/无前导斜杠，不接受外部 URL。 */
    public Path resolveUrl(String url) {
        if (url == null || url.startsWith("//")) {
            throw new IllegalArgumentException("业务文件地址为空或非法");
        }
        String relative = validateRelative(url.startsWith("/") ? url.substring(1) : url);
        return checked(webRoot.resolve(relative));
    }

    public Path checked(Path file) {
        for (Path part : file) {
            if (part.toString().equals("..") || part.toString().equals(".")) throw new IllegalArgumentException("文件路径不允许穿越段");
        }
        Path absolute = file.toAbsolutePath().normalize();
        verifyContained(root, absolute, true);
        verifyContained(webRoot, absolute, false);
        return absolute;
    }

    public Path ensureDirectory(String... segments) throws IOException {
        Path directory = path(segments);
        Files.createDirectories(directory);
        return checked(directory);
    }

    /**
     * JFinal MultipartRequest 会将 saveDirectory 追加到 UploadConfig.baseUploadPath，
     * Windows 盘符绝对路径也不例外。仅返回相对框架上传根的路径，物理操作仍使用 checked/path。
     */
    public String uploadDirectory(String... segments) throws IOException {
        String configuredBase = com.jfinal.upload.UploadConfig.getBaseUploadPath();
        if (configuredBase == null || configuredBase.isBlank()) {
            throw new IOException("JFinal 上传根目录尚未初始化");
        }
        Path base = Path.of(configuredBase).toFile().getCanonicalFile().toPath();
        Path target = path(segments);
        verifyContained(base, target, true);
        String relative = base.relativize(target).toString().replace('\\', '/');
        if (!relative.isEmpty()) validateRelative(relative);
        ensureDirectory(segments);
        return relative;
    }

    public void moveNew(Path source, Path target) throws IOException {
        Path from = checked(source);
        Path to = checked(target);
        if (!Files.isRegularFile(from, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("待移动的业务文件不存在或不是普通文件");
        }
        if (Files.exists(to, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("目标文件已经存在，拒绝覆盖：" + to.getFileName());
        }
        Files.createDirectories(checked(to.getParent()));
        // 不使用 REPLACE_EXISTING/ATOMIC_MOVE，防止实现相关的静默覆盖。
        Files.move(checked(from), checked(to));
    }

    /** 仅创建新文件，复制失败只清理本次成功创建的目标，不触碰重名既有文件。 */
    public void copyNew(Path source, Path target) throws IOException {
        Path from = checked(source), to = checked(target);
        if (!Files.isRegularFile(from, LinkOption.NOFOLLOW_LINKS)) throw new IOException("源文件不存在");
        Files.createDirectories(checked(to.getParent()));
        boolean created = false;
        try {
            try (var output = Files.newOutputStream(checked(to), java.nio.file.StandardOpenOption.CREATE_NEW)) {
                created = true;
                Files.copy(checked(from), output);
            }
        } catch (IOException | RuntimeException e) {
            if (created) {
                try { deleteFile(to); } catch (Exception cleanup) { e.addSuppressed(cleanup); }
            }
            throw e;
        }
    }

    public void deleteFile(Path file) throws IOException {
        deleteFileIfExists(file);
    }

    /** 返回是否实际删除，供调用方区分文件已不存在与清理成功。 */
    public boolean deleteFileIfExists(Path file) throws IOException {
        Path target = checked(file);
        if (target.equals(root) || Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("禁止通过文件删除接口删除目录");
        }
        return Files.deleteIfExists(target);
    }

    public static String safeSegment(String segment) {
        if (segment == null || segment.isBlank() || segment.contains("..")
                || segment.equals(".") || segment.matches(".*[\\\\/:*?\"<>|%#\\p{Cntrl}].*")
                || segment.endsWith(".") || segment.endsWith(" ")) {
            throw new IllegalArgumentException("文件名或目录段非法");
        }
        String stem = segment.split("\\.", 2)[0].toUpperCase(Locale.ROOT);
        if (stem.matches("CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9]")) {
            throw new IllegalArgumentException("不允许使用系统保留文件名");
        }
        return segment;
    }

    private static String validateRelative(String relative) {
        if (relative == null || relative.isBlank() || relative.startsWith("/")
                || relative.contains("\\")) {
            throw new IllegalArgumentException("必须使用以 / 分隔的非空相对路径");
        }
        for (String segment : relative.split("/", -1)) { safeSegment(segment); }
        return relative;
    }

    private static void verifyContained(Path boundary, Path candidate, boolean allowRoot) {
        if (!candidate.startsWith(boundary) || (!allowRoot && candidate.equals(boundary))) {
            throw new IllegalArgumentException("文件路径不在对应业务目录内");
        }
        try {
            // 逐级拒绝符号链接/目录联接越界，避免仅字符串前缀比较。
            Path cursor = candidate;
            while (cursor != null && cursor.startsWith(boundary)) {
                if (Files.isSymbolicLink(cursor)) {
                    throw new IllegalArgumentException("业务文件路径不允许符号链接");
                }
                if (Files.exists(cursor, LinkOption.NOFOLLOW_LINKS)
                        && !cursor.toRealPath().startsWith(boundary.toFile().getCanonicalFile().toPath())) {
                    throw new IllegalArgumentException("业务文件路径存在目录联接越界");
                }
                cursor = cursor.getParent();
            }
            Path canonical = candidate.toFile().getCanonicalFile().toPath();
            Path canonicalBoundary = boundary.toFile().getCanonicalFile().toPath();
            if (!canonical.startsWith(canonicalBoundary)) {
                throw new IllegalArgumentException("业务文件规范路径越界");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("无法校验业务文件路径", e);
        }
    }
}
