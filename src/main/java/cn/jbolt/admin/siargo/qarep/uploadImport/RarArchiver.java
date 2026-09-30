package cn.jbolt.admin.siargo.qarep.uploadImport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** 每个任务使用显式输入清单，压缩及归档测试都要求正常退出且有时间上限。 */
public class RarArchiver {
    public Path executable(String configured) throws IOException {
        if (configured == null || configured.isBlank()) throw new IOException("未配置 winrar_exe_path");
        Path exe = Path.of(configured);
        if (!Files.isRegularFile(exe)) throw new IOException("WinRAR 程序不存在：" + configured);
        return exe;
    }

    public Path archive(Path exe, Path taskDir, List<Path> inputs, String name) throws Exception {
        if (inputs.isEmpty()) throw new IOException("归档输入为空");
        Path inputList = taskDir.resolve("archive-inputs.txt");
        List<String> names = inputs.stream().map(path -> {
            if (!path.getParent().equals(taskDir) || !Files.isRegularFile(path)) throw new IllegalArgumentException("归档文件不属于当前任务");
            return path.getFileName().toString();
        }).toList();
        Files.write(inputList, names, StandardCharsets.UTF_8);
        Path archive = taskDir.resolve(name);
        if (Files.exists(archive)) throw new IOException("归档文件已存在");
        run(exe, taskDir, "archive-create.log", "a", "-ma5", "-ep", "-scfl", "-y", archive.toString(), "@" + inputList);
        if (!Files.isRegularFile(archive) || Files.size(archive) == 0) throw new IOException("未生成有效归档");
        run(exe, taskDir, "archive-test.log", "t", "-y", archive.toString());
        return archive;
    }

    private void run(Path exe, Path cwd, String log, String... arguments) throws Exception {
        java.util.ArrayList<String> command = new java.util.ArrayList<>();
        command.add(exe.toString());
        command.addAll(List.of(arguments));
        Process process = new ProcessBuilder(command).directory(cwd.toFile()).redirectErrorStream(true)
                .redirectOutput(cwd.resolve(log).toFile()).start();
        try {
            if (!process.waitFor(10, TimeUnit.MINUTES)) throw new IOException("WinRAR 执行超过 10 分钟，已终止");
            if (process.exitValue() != 0) throw new IOException("WinRAR 返回码 " + process.exitValue() + "，日志：" + log);
        } finally {
            if (process.isAlive()) {
                process.descendants().forEach(ProcessHandle::destroyForcibly);
                process.destroyForcibly();
            }
        }
    }
}
