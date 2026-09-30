package cn.jbolt.common.util;

import com.jfinal.log.Log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

/** 按部署环境选择配置清单内的本地资源，外部 URL 和其他资源保持原样。 */
public class ProjectAssetResolver {
    private static final Log LOG = Log.getLog(ProjectAssetResolver.class);
    private final Path webRoot;
    private final Set<String> assets = new HashSet<>();
    private final BooleanSupplier production;

    public ProjectAssetResolver(Path webRoot, String configuredAssets, BooleanSupplier production) {
        this.webRoot = webRoot.toAbsolutePath().normalize();
        this.production = production;
        for (String entry : configuredAssets.split(",")) {
            String asset = entry.trim();
            if (asset.isEmpty()) {
                continue;
            }
            if (!asset.matches("assets/(?:css/[A-Za-z0-9_-]+\\.css|js/[A-Za-z0-9_-]+\\.js)")
                    || !assets.add(asset)) {
                throw new IllegalArgumentException("资源清单路径无效或重复：" + asset);
            }
        }
    }

    public String url(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        int query = url.indexOf('?');
        int fragment = url.indexOf('#');
        int end = query < 0 ? url.length() : query;
        if (fragment >= 0) {
            end = Math.min(end, fragment);
        }
        String path = url.substring(0, end);
        String suffix = url.substring(end);
        boolean rooted = path.startsWith("/") && !path.startsWith("//");
        String localPath = rooted ? path.substring(1) : path;
        String source = localPath.replaceFirst("\\.min\\.(css|js)$", ".$1");
        //精确清单匹配：不改写 CDN、第三方插件、同名外部文件及其他资源。
        if (!assets.contains(source)) {
            return url;
        }
        int dot = source.lastIndexOf('.');
        String minified = source.substring(0, dot) + ".min" + source.substring(dot);
        String preferred = production.getAsBoolean() ? minified : source;
        String alternative = preferred.equals(source) ? minified : source;
        String selected;
        if (Files.isRegularFile(webRoot.resolve(preferred))) {
            selected = preferred;
        } else if (Files.isRegularFile(webRoot.resolve(alternative))) {
            selected = alternative;
        } else {
            LOG.warn("项目资源的未压缩和压缩版本均不存在：" + source);
            return url;
        }
        return (rooted ? "/" : "") + selected + suffix;
    }
}
