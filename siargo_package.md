# siargo_package — siargo 项目打包技能

> 触发方式：`siargo_package 主版本` / `siargo_package 子版本` / `siargo_package 修订版`
> 参数决定版本号递增级别；缺少参数时**必须先询问用户**（默认建议"修订版"），禁止自行假定。

本技能完成一次完整发布打包：**代码变更比对 → 生成并追加 CHANGELOG → 版本号递增与双处同步 → 清理旧产物（clean）→ Maven 打包（排除测试）→ 产出配置切 pro → 压缩 siargo.rar → 更新打包基线**。

所有命令在项目根目录 `D:\Workspace\siargo` 下执行（PowerShell 7）。

---

## 版本号规则

当前版本读取自 `pom.xml` 的 `<version>X.Y.Z</version>`（文件顶部 `<artifactId>siargo</artifactId>` 下一行）。以当前版本 `2.9.1` 为例：

| 技能参数 | 递增级别 | 新版本 |
|----------|----------|--------|
| `siargo_package 主版本` | major +1，minor/patch 归零 | `3.0.0` |
| `siargo_package 子版本` | minor +1，patch 归零 | `2.10.0` |
| `siargo_package 修订版` | patch +1 | `2.9.2` |

新版本号必须**同时**写入以下两处，缺一不可（打包前校验两处一致）：

1. `D:\Workspace\siargo\pom.xml` —— `<version>新版本</version>`
2. `D:\Workspace\siargo\src\main\java\cn\jbolt\starter\ProjectServer.java` —— `getProjectVersion()` 方法内的 `return "新版本";`

---

## 打包基线机制

基线文件：`D:\Workspace\siargo\last_package.json`（每次打包成功后覆盖更新）。结构：

```json
{
  "head": "上次打包时的 git HEAD 短哈希",
  "headFull": "上次打包时的 git HEAD 完整哈希",
  "version": "上次打包发布的版本号",
  "packageTime": "上次打包时间 yyyy-MM-dd HH:mm:ss"
}
```

基线用于比对"两次执行打包技能之间"的代码变更。

---

## 执行流程（严格按顺序，任一步失败立即停止并报告，不得继续）

### 第 1 步：读取基线，比对代码变更

1. 读取 `last_package.json`：
   - **基线存在**：以 `headFull` 为比对起点。若该 commit 在历史中不存在（rebase/重置过），降级为仅统计当前未提交变更，并明确告知用户比对范围受限。
   - **基线不存在**（首次执行）：变更范围 = 当前全部未提交变更（tracked 修改 + 未跟踪新文件），并提示用户确认是否还有更早的已提交变更需要纳入本次 CHANGELOG。
2. 收集变更（三类合并）：
   - 已提交：`git log <headFull>..HEAD --name-status` 看文件清单，再 `git diff <headFull>..HEAD` 看具体 diff。
   - 未提交 tracked：`git diff HEAD --name-status` 与 `git diff HEAD`。
   - 未跟踪新文件：`git ls-files --others --exclude-standard`（忽略 `target/`、`.idea/`、`.claude/`、`src/main/webapp/upload/`、`src/main/webapp/export/`、`last_package.json` 等非业务文件）。
3. **逐个阅读变更文件的实际 diff 内容**理解改动意图（本项目 commit message 无信息量，禁止依赖 commit message 写 CHANGELOG）。
4. diff 体量过大时优先读 `--stat` 摘要，再对核心业务文件（java/html/js/css）逐个看 diff；`*.min.js`/`*.min.css` 只看是否同步更新，不分析内容。
5. 若比对结果为**零业务变更**：警告用户并询问是否仍要继续打包，未确认不得继续。

### 第 2 步：生成 CHANGELOG 条目并追加

格式参考 `D:\Workspace\siargo\src\main\webapp\_view\admin\siargo\changelog\CHANGELOG.md` 已有条目（**只追加，不改动历史条目**）：

```markdown
### vX.Y.Z (yyyy-MM-dd)
- type(scope): 简要描述——补充细节（含关键类名/字段/行为变化）
- type(scope): ...
```

写作规则：

- 标题日期为**本次打包当天**日期；版本号为本步骤计算出的新版本。
- 每条一行：`type(scope): 中文描述`。type 取值：`feat`（新功能）/`fix`（修复）/`refactor`（重构）/`improve`（改进）/`perf`（性能）/`style`（样式美化）/`chore`（构建/配置/杂项）/`test`（测试）/`docs`（文档）。
- scope 为业务模块名：`qarep`/`equipment`/`dms`/`api`/`apicalllog`/`customer`/`supplier`/`imi`/`cme`/`changelog`/`dashboard`/`config`/`build` 等；跨模块或无法归类时省略 scope。
- 描述聚焦"做了什么 + 关键实现点"，与现有条目密度一致（一条 30~80 字为宜，复杂改动可用"——"追加细节）。
- 同一模块的相关改动归并为一条或相邻排列；纯格式化/压缩产物同步（如 `siargo.min.css 同步`）并入对应 style 条目末尾，不单独成条。
- 仅 `last_package.json`、`CHANGELOG.md` 自身、版本号变更产生的 diff 不写入 CHANGELOG（除非用户要求）。

**插入位置**：`## 更新日志` 标题行之后、现有第一个 `### vX.Y.Z` 条目之前（新版本永远在最上方）。插入后保留空行分隔。

该文件运行时由 `ChangelogController` 从 webapp 目录直接读取，且随 assembly 打进发布包，因此**必须在 Maven 打包之前完成追加**。

### 第 3 步：版本号递增与同步

按"版本号规则"计算新版本，修改两处：

1. `pom.xml`：`<version>旧版本</version>` → `<version>新版本</version>`
2. `ProjectServer.java`：`return "旧版本";` → `return "新版本";`（位于 `getProjectVersion()` 方法内）

修改后回读两处确认一致。

### 第 4 步：清理旧打包产物（clean）

执行 package 之前必须先 clean，避免上次打包的旧产物混入本次发布包：

```powershell
mvn clean
```

- `mvn clean` 删除整个 `target` 目录（含旧的 `siargo-release\`、残留的 `siargo.rar` / `siargo-release.tar.gz`）。
- 若 `mvn clean` 因文件被占用失败，改为手动删除：`Remove-Item -Recurse -Force D:\Workspace\siargo\target`；仍失败则**停止流程**并报告占用原因。
- 确认 `target\siargo-release` 目录已不存在，方可进入下一步。

### 第 5 步：Maven 打包（排除测试）

```powershell
mvn package -Dmaven.test.skip=true
```

- 上一步已完成 clean，此处只执行 package。
- `-Dmaven.test.skip=true` 跳过 `src\test` 下全部测试的编译与执行（用户明确要求排除测试文件）。
- assembly 按 `package.xml` 描述符产出 `target\siargo-release\siargo\`（dir 格式）与 `siargo-release.tar.gz`。
- 构建失败：原样报告 Maven 错误，**停止流程**，不回滚已做的 CHANGELOG/版本号修改（由用户决定），不更新基线。
- 构建成功后校验产物存在：`target\siargo-release\siargo\config\application.properties`、`siargo.bat`、`lib\siargo.jar`。

### 第 6 步：产出配置切换生产环境

修改产出文件 `D:\Workspace\siargo\target\siargo-release\siargo\config\application.properties`：

- 将 `pdev=dev` 改为 `pdev=pro`（仅改产出目录，**禁止改动 `src\main\resources\application.properties` 源文件**）。
- 回读确认该行已为 `pdev=pro`。

### 第 7 步：压缩 siargo.rar

WinRAR 路径取自项目配置 `config.properties` 的 `winrar_exe_path`（默认 `C:\Program Files\WinRAR\WinRAR.exe`），先校验可执行文件存在：

```powershell
& "C:\Program Files\WinRAR\WinRAR.exe" a -r -idq "D:\Workspace\siargo\target\siargo-release\siargo.rar" "D:\Workspace\siargo\target\siargo-release\siargo"
```

- 归档内含 `siargo\` 根目录及其下全部文件（config/webapp/lib/启动脚本），与 tar.gz 结构一致，便于直接解压部署。
- 压缩前确认不存在同名旧 rar（第 4 步 clean 已清理 target，正常不会残留；若有则先删除，避免追加模式混入旧文件）。
- 压缩完成后校验 rar 存在且体积非零。
- **WinRAR 不存在的降级方案**：改用 `Compress-Archive` 生成 `siargo.zip`，并明确告知用户产物格式已变更：
  ```powershell
  Compress-Archive -Path "D:\Workspace\siargo\target\siargo-release\siargo" -DestinationPath "D:\Workspace\siargo\target\siargo-release\siargo.zip" -Force
  ```

### 第 8 步：更新打包基线

全部成功后覆盖写入 `last_package.json`：

```powershell
git -C D:\Workspace\siargo rev-parse HEAD   # 取当前 HEAD 完整哈希
```

```json
{
  "head": "<短哈希>",
  "headFull": "<完整哈希>",
  "version": "<本次发布版本>",
  "packageTime": "<当前时间>"
}
```

### 第 9 步：输出打包报告

向用户汇报：

- 版本变更：旧版本 → 新版本（主/子/修订）
- 本次 CHANGELOG 新增条目全文
- 变更文件统计（已提交 N 个 commit / 未提交修改 M 个文件）
- 产物路径：`target\siargo-release\siargo.rar`（及 `siargo-release.tar.gz`）
- `pdev=pro` 切换确认
- 基线已更新至新 HEAD

---

## 注意事项

1. **顺序不可颠倒**：CHANGELOG 与版本号修改必须在 `mvn package` 之前，否则发布包内的 CHANGELOG 与版本号是旧的。
2. 禁止修改 `src\main\resources\` 下的任何源配置；环境切换只作用于 `target` 产出目录。
3. 禁止在技能流程中执行 `git commit` / `git push`（提交时机由用户自行决定）。
4. CHANGELOG 只增不改不删历史条目；发现历史条目有错也不得顺手修改。
5. 打包期间如检测到 `src\main\webapp\assets\js` 或 `assets\css` 有本次变更但对应 `.min.js` / `.min.css` 未同步，应警告用户（项目规范要求同步压缩产物），由用户决定是否先补齐再打包。
6. `mvn` 不在 PATH 时提示用户配置，不得猜测 Maven 路径。
