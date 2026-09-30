package cn.jbolt.common.storage;

import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** 上传暂存、文件发布补偿；不持有数据库事务。 */
public final class SiargoUploadFiles {
    private SiargoUploadFiles() {}

    public static Path temp(SiargoStorage storage, String url, boolean mustExist) {
        Path path = storage.resolveUrl(url);
        Path root = storage.path("temp");
        if (path.equals(root) || !path.startsWith(root)) {
            throw new IllegalArgumentException("只能操作本业务临时目录内的文件");
        }
        if (mustExist && !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("临时文件不存在：" + path.getFileName());
        }
        return path;
    }

    /** 每次请求使用独立临时目录，保留业务展示文件名，避免同名上传互相覆盖。 */
    public static String newUploadDirectory(SiargoStorage storage) throws IOException {
        return storage.uploadDirectory("temp", UUID.randomUUID().toString());
    }

    public static String accept(SiargoStorage storage, UploadFile upload) throws IOException {
        Path source = storage.checked(upload.getFile().toPath());
        temp(storage, storage.toUrl(source), true);
        String name = upload.getOriginalFileName();
        if (name == null || name.isBlank()) name = upload.getFileName();
        name = SiargoStorage.safeSegment(name);
        Path target = storage.checked(source.getParent().resolve(name));
        if (!source.equals(target)) storage.moveNew(source, target);
        return storage.toUrl(target);
    }

    public static Ret delete(SiargoStorage storage, List<String> urls, boolean tempOnly) {
        List<String> failures = new ArrayList<>();
        if (urls != null) for (String url : urls) {
            if (url == null || url.isBlank()) continue;
            try {
                storage.deleteFile(tempOnly ? temp(storage, url, false) : storage.resolveUrl(url));
            } catch (Exception e) {
                failures.add(url + "：" + e.getMessage());
            }
        }
        return (failures.isEmpty() ? Ret.ok() : Ret.fail("部分文件清理失败，请检查失败清单"))
                .set("failedFiles", failures).set("failCount", failures.size())
                .set("successCount", (urls == null ? 0 : urls.size()) - failures.size());
    }

    /** 由真正事务所有者创建，rollback 必须在 Db.tx 返回失败或抛异常后调用。 */
    public static final class Moves {
        private final SiargoStorage storage;
        private final List<Path[]> moved = new ArrayList<>();
        public Moves(SiargoStorage storage) { this.storage = storage; }

        public void move(Path source, Path target) throws IOException {
            if (source.equals(target)) return;
            storage.moveNew(source, target);
            moved.add(new Path[]{target, source});
        }

        public Ret rollback(String message) {
            List<String> failures = new ArrayList<>();
            for (int i = moved.size() - 1; i >= 0; i--) {
                Path[] pair = moved.get(i);
                try { storage.moveNew(pair[0], pair[1]); }
                catch (Exception e) { failures.add(pair[0] + " -> " + pair[1] + "：" + e.getMessage()); }
            }
            // 补偿失败时保留现存文件，不以删除文件掩盖失败。
            return Ret.fail(message + (failures.isEmpty() ? "" : "；文件恢复失败，须人工处理"))
                    .set("fileRecoveryFailures", failures);
        }
    }
}
