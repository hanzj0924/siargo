---
name: siargo-package
description: siargo 项目完整发布打包技能——代码变更比对、生成 CHANGELOG、版本号递增与双处同步、mvn clean 与打包（排除测试）、产出切 pro、压缩 siargo.rar、更新打包基线。 / Complete siargo release packaging workflow covering change review, CHANGELOG generation, version synchronization, Maven packaging, production configuration, archive creation, and package-baseline updates.
whenToUse: 用户明确要求执行 siargo 发布/打包，或输入 siargo-package 主版本/子版本/修订版；仅提及或修改本文档不触发打包。没有已确认目标版本且缺少递增级别时先询问用户。
user-invocable: true
---

# siargo-package — siargo 项目打包技能

> 触发方式：`siargo-package 主版本` / `siargo-package 子版本` / `siargo-package 修订版`
> 用户已明确确认的待发布目标版本优先；没有已确认目标时，参数决定版本号递增级别。两者均缺少时**必须先询问用户**（默认建议“修订版”），禁止自行假定。

本技能仅在用户明确要求发布/打包时执行：**代码变更比对 → 确定目标版本并生成 CHANGELOG → 版本号双处同步 → 检查并增量压缩指定资源 → 清理旧产物（clean）→ Maven 打包（排除测试）→ 产出配置切 pro → 压缩 siargo.rar → 更新打包基线**。日常开发、修复和文档修改不触发发布，也不向 CHANGELOG 写入“未发布”分组或提前生成版本记录。

所有命令在项目根目录 `D:\Workspace\siargo` 下执行（PowerShell 7）。

---

## 版本号规则

先读取 `pom.xml` 的 `<version>X.Y.Z</version>`（文件顶部 `<artifactId>siargo</artifactId>` 下一行）、`ProjectServer.getProjectVersion()` 与 `last_package.json.version`，区分代码版本、待发布目标与上次成功打包版本。

- 用户已明确确认待发布目标时，优先沿用该目标；本文末尾“当前待发布说明”可记录用户已确认但尚未发布的目标。两处代码版本已是目标版本时，不再递增，不能仅因提前改过版本号就把下一次发布跳到更高版本。
- 没有已确认目标时，按用户指定的递增级别计算；版本来源不一致、或待发布目标已经存在于成功基线时，先核对当前状态，不擅自覆盖或推断新的发布意图。
- 同一次失败打包恢复时始终沿用已记录目标。

以当前版本 `2.9.1`、尚无已确认目标为例：

| 技能参数 | 递增级别 | 新版本 |
|----------|----------|--------|
| `siargo-package 主版本` | major +1，minor/patch 归零 | `3.0.0` |
| `siargo-package 子版本` | minor +1，patch 归零 | `2.10.0` |
| `siargo-package 修订版` | patch +1 | `2.9.2` |

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

成功打包后还需保存完整 `assetHashes`（结构见第 9 步），用于精确识别清单资源修改。旧基线没有该字段时，首次对所有有源文件的清单项建立压缩基线；仅有 min 的项保持现状。

---

## 执行流程（严格按依赖顺序执行）

某步失败时，停止依赖该步的后续发布操作，报告错误和当前状态；不更新成功基线，不自行回滚版本或 CHANGELOG。可继续只读诊断和其他独立且已授权的工作。修复与重试仅在已有授权明确覆盖时执行；否则提交具体修复建议。

同一次失败打包恢复时，沿用首次记录的旧版本和目标版本；先回读现有文件核对已完成步骤，避免重复递增版本或新建重复的 CHANGELOG 版本分组。若修复产生本次发布范围内的业务变化，按第 2 步重新归纳目标版本的描述。版本或 CHANGELOG 回滚仍由用户决定。

### 第 1 步：读取基线，比对代码变更

1. 读取 `last_package.json`：
   - **基线存在**：以 `headFull` 为比对起点。若该 commit 在历史中不存在（rebase/重置过），降级为仅统计当前未提交变更，并明确告知用户比对范围受限。
   - **基线不存在**（首次执行）：变更范围 = 当前全部未提交变更（tracked 修改 + 未跟踪新文件），并提示用户确认是否还有更早的已提交变更需要纳入本次 CHANGELOG。
2. 收集变更（三类合并）：
   - 已提交：`git log <headFull>..HEAD --name-status` 看文件清单，再 `git diff <headFull>..HEAD` 看具体 diff。
   - 未提交 tracked：`git diff HEAD --name-status` 与 `git diff HEAD`。
   - 未跟踪新文件：`git ls-files --others --exclude-standard`（忽略 `target/`、`.idea/`、`.claude/`、`src/main/webapp/upload/`、`src/main/webapp/export/`、`last_package.json`、`.codex/tmp/project-assets-manifest.json` 等非业务文件）。
3. **逐个阅读变更文件的实际 diff 内容**理解改动意图（本项目 commit message 无信息量，禁止依赖 commit message 写 CHANGELOG）。
4. diff 体量过大时优先读 `--stat` 摘要，再对核心业务文件（java/html/js/css）逐个看 diff；压缩产物不计入业务变更、不据其生成 CHANGELOG。清单中的 min 文件在第 4 步按哈希增量生成，日常未同步是正常状态。
5. 若比对结果为**零业务变更**：警告用户并询问是否仍要继续打包，未确认不得继续。

### 第 2 步：归纳本次更新，整理 CHANGELOG

更新文件：`D:\Workspace\siargo\src\main\webapp\_view\admin\siargo\changelog\CHANGELOG.md`。面向系统使用者，借鉴版本说明中“功能主题＋简短说明”的表达方式，每次重新组织语言，概括本次最终改了什么、使用上有什么变化，不照抄开发过程或堆叠代码细节。

```markdown
### vX.Y.Z (yyyy-MM-dd)

- 报告单归档：支持按月份区间导出 PDF，便于集中整理历史报告。
- 模板关联：支持搜索和批量选择产品系列，修复已有绑定显示不完整的问题。
```

**写作规则：**

- 以第 1 步从上次成功打包基线到当前最终状态的真实 diff 为事实依据，可结合本次待发布参考摘要或遗留草稿理解业务变化；按最终功能主题归并、去重并重写，纳入后续修改并去除已被替代的旧行为，不逐文件、逐提交或逐次修复复述，不把草稿直接复制拼接。
- 每条使用 `- 中文功能主题：简短说明。`，通常一至两句，先说新增能力、调整后的行为或修复的问题。主题使用“报告单归档”“产品选型”等用户熟悉的名称，不使用 `type(scope)`、英文模块代号或提交类型作标题。
- 同一功能的界面调整、关联修复和后续完善尽量合成一条；独立的业务变化分别保留，不为凑条数遗漏变化，也不把无关内容塞进一条。优先写影响用户使用的内容，避免只有“优化体验”“修复若干问题”等空泛描述。
- 不罗列类名、方法名、字段名、文件路径、SQL、样式属性及排查过程；只有用户理解功能或完成必要操作确实需要时，才保留相关技术名称。纯内部重构、测试和构建细节不单独作为用户更新内容。
- 保留关键适用范围、限制、不兼容变化及用户需要执行的操作，可用 `- 重要变更：...` 单独说明。只描述已有证据支持的变化，不把尚未执行的数据迁移、部署或运行验证写成已经完成，不宣称未经验证的效果。
- 复杂版本可增加一条 `- 本次更新：一句话概览。`，与后续主题重复时省略。当前日志页面只识别 `###` 分组和 `- ` 列表行，条目保持单行纯文本；不使用独立概览段落、加粗标记、引用块或嵌套列表。
- 静态资源压缩及压缩产物同步完全不写入 CHANGELOG，也不附在业务条目末尾；实际业务功能与样式变化依据源文件 diff 正常概括。仅 `last_package.json`、`CHANGELOG.md` 自身、版本号变化产生的 diff 不写入日志（除非用户要求）。

**生成时机与归档规则：**

1. 日常开发不修改 CHANGELOG，不新建按日期排列的“未发布”记录，也不因已确定版本号就提前写入正式版本。用户仅要求整理规则或说明未来发布安排时，不执行本步骤。
2. 用户正式调用 `siargo-package` 发布时，再将本次范围内的业务变化重新归纳到同一目标版本。标题使用已确认或按规则计算的目标版本，以及实际打包当天的日期，不预填未来发布日期。
3. 如有遗留未发布分组，仅将属于本次发布范围的内容纳入核对；归档后移除对应草稿及空分组，合并同主题并去除过时行为。不属于本次范围的遗留内容不擅自删除，也不冒充本次已发布内容。
4. 新版本写在 `## 更新日志` 之后、已发布历史之前；同一版本只保留一个分组，标题与条目、各版本之间保留空行。已发布历史默认不改，用户明确要求重写或纠错时例外。
5. 文件由 `ChangelogController` 从 webapp 目录读取，并随 assembly 进入发布包，因此正式打包时必须在 Maven 构建之前生成。此时条目仍处于本次发布准备阶段，只有全部打包步骤成功才报告打包完成；失败时不更新成功基线、不自行回滚，恢复时更新同一目标版本，避免重复递增和重复记录。

### 第 3 步：版本号递增与同步

按“版本号规则”确定本次目标，再同步两处。已确认目标、提前同步过的版本和失败恢复均不重复递增；文件已经是目标版本时跳过修改：

1. `pom.xml`：`<version>旧版本</version>` → `<version>新版本</version>`
2. `ProjectServer.java`：`return "旧版本";` → `return "新版本";`（位于 `getProjectVersion()` 方法内）

修改后回读两处确认一致。

### 第 4 步：检查指定资源，有变化时增量压缩

加载与压缩共用 `src/main/resources/application.properties` 中的 `project_assets.environment_files`，仅处理以下 12 项，同名 `.min` 文件视为同一资源：

| 目录 | 未压缩文件 |
|------|------------|
| `src/main/webapp/assets/css/` | `jbolt-admin.css`、`jbolt-mine.css`、`jbolt-page-loading.css`、`jbolt-wechat-menu.css`、`login.css`、`siargo.css` |
| `src/main/webapp/assets/js/` | `jbolt-admin.js`、`jbolt-mine.js`、`jbolt-wechat-menu.js`、`login.js`、`relogin.js`、`siargo.js` |

外部引用、CDN、第三方插件及清单外文件不处理，保持现状。日常修改不压缩；每次打包先执行：

```powershell
./.codex/scripts/Compress-ProjectAssets.ps1 -ManifestPath .codex/tmp/project-assets-manifest.json
```

- 可先加 `-CheckOnly` 只读查看待压缩清单，该模式不修改文件、不生成 manifest。
- 脚本比较当前源文件和 min 文件的 SHA-256 与上次成功打包的 `last_package.json.assetHashes`：源文件有修改、min 内容变化、min 缺失或尚无哈希基线时重新压缩；均未变化则跳过。
- 源文件缺失但 min 存在时保留 min，不伪造未压缩文件；两者均缺失则停止依赖该资源的后续发布操作并报告，可继续只读诊断。空 JS 是合法源文件，允许压缩产物为空。
- 使用已安装的 terser / csso；工具缺失或压缩失败时停止依赖该步的后续发布操作，可继续只读诊断，修复重试遵守本节的授权条件。不安装未知依赖，不使用旧 min 假装压缩成功。临时产物生成成功后才替换相应 min，JS 不开启顶层符号改名。
- 脚本将源/min 哈希写入 `.codex/tmp/project-assets-manifest.json`，不修改成功打包基线；该清单放在 target 之外，不受随后 `mvn clean` 影响，不写 CHANGELOG、不加入发布业务资源。
- 构建后按 manifest 校验发布目录中的清单资源与源目录一致；打包期间源/min 发生变化时重新执行本步并重新打包。第 9 步仅在全部发布步骤成功后合并新哈希到基线。

**限定清单的运行时加载校验：**

- `pdev=dev` 优先使用未压缩 `.css` / `.js`，缺少对应未压缩版本时，再使用压缩文件；`pdev=pro` 优先使用 `.min.css` / `.min.js`，缺少对应压缩版本时，再使用未压缩文件。
- 模板通过 `ProjectAssets.url(...)` 选择实际存在的清单内文件，保留查询参数、版本号及片段；外部 URL 与其他资源引用保持原样。
- 发布目录必须保留清单内未压缩文件；检查 `package.xml` 不排除缺少压缩版本时所需的文件。压缩及产物同步完全不写 CHANGELOG。

### 第 5 步：清理旧打包产物（clean）

执行 package 之前必须先 clean，避免上次打包的旧产物混入本次发布包：

```powershell
mvn clean
```

- `mvn clean` 删除整个 `target` 目录（含旧的 `siargo-release\`、残留的 `siargo.rar` / `siargo-release.tar.gz`）。
- 若 `mvn clean` 因文件被占用失败，改为手动删除：`Remove-Item -Recurse -Force D:\Workspace\siargo\target`；仍失败则停止依赖 clean 的后续发布操作并报告占用原因，可继续只读诊断，修复重试遵守本节的授权条件。
- 确认 `target\siargo-release` 目录已不存在，方可进入下一步。

### 第 6 步：Maven 打包（排除测试）

```powershell
mvn package -Dmaven.test.skip=true
```

- 上一步已完成 clean，此处只执行 package。
- `-Dmaven.test.skip=true` 跳过 `src\test` 下全部测试的编译与执行（用户明确要求排除测试文件）。
- assembly 按 `package.xml` 描述符产出 `target\siargo-release\siargo\`（dir 格式）与 `siargo-release.tar.gz`。
- 构建失败：报告 Maven 错误和当前状态，停止依赖构建成功的后续发布操作，可继续只读诊断；仅在已有授权明确覆盖时修复重试，否则提交具体修复建议。不回滚已做的 CHANGELOG/版本号修改（由用户决定），不更新基线；同一次打包恢复沿用目标版本，在已有 CHANGELOG 分组内归纳必要变化，避免重复记录。
- 构建成功后校验产物存在：`target\siargo-release\siargo\config\application.properties`、`siargo.bat`、`lib\siargo-<目标版本>.jar`。
- `src/main/webapp/upload/siargo/dms/` 内的内容由用户手动复制；`package.xml` 使用 `upload/siargo/dms/**/*` 排除所有子项并保留空的 `dms` 目录。发布目录及 RAR、tar.gz 必须包含 `webapp/upload/siargo/dms/` 空目录，均不得包含其下的文件或子目录，源目录保持不变。

### 第 7 步：产出配置切换生产环境

修改产出文件 `D:\Workspace\siargo\target\siargo-release\siargo\config\application.properties`：

- 将 `pdev=dev` 改为 `pdev=pro`（仅改产出目录，**禁止改动 `src\main\resources\application.properties` 源文件**）。
- 回读确认该行已为 `pdev=pro`。
- 对发布目录中的 12 项清单资源按生产规则检查：有对应压缩版本时使用压缩文件，缺少压缩版本时使用实际存在的未压缩文件；外部引用和清单外资源保持现状。
- Maven 生成的 tar.gz 早于此配置切换，必须在切换后用 `.codex/scripts/create-release-tar.py` 从发布目录重新生成 `target/siargo-release.tar.gz`，保留 `siargo/` 根目录及根目录启动脚本的执行权限；最终 RAR 与 tar.gz 均核对 `pdev=pro`、DMS 内容排除及空目录保留结果。

### 第 8 步：压缩 siargo.rar

WinRAR 路径取自项目配置 `config.properties` 的 `winrar_exe_path`（默认 `C:\Program Files\WinRAR\WinRAR.exe`），先校验可执行文件存在：

```powershell
& "C:\Program Files\WinRAR\WinRAR.exe" a -r -idq "D:\Workspace\siargo\target\siargo-release\siargo.rar" "D:\Workspace\siargo\target\siargo-release\siargo"
```

- 归档内含 `siargo\` 根目录及其下全部文件（config/webapp/lib/启动脚本），与 tar.gz 结构一致，便于直接解压部署。
- 压缩前确认不存在同名旧 rar（第 5 步 clean 已清理 target，正常不会残留；若有则先删除，避免追加模式混入旧文件）。
- 压缩完成后校验 rar 存在且体积非零。
- **WinRAR 不存在的降级方案**：改用 `Compress-Archive` 生成 `siargo.zip`，并明确告知用户产物格式已变更：
  ```powershell
  Compress-Archive -Path "D:\Workspace\siargo\target\siargo-release\siargo" -DestinationPath "D:\Workspace\siargo\target\siargo-release\siargo.zip" -Force
  ```

### 第 9 步：更新打包基线

全部成功后更新 `last_package.json` 的发布字段，并将本次 `.codex/tmp/project-assets-manifest.json` 的完整 `assetHashes` 对象合并进去；保留其他已有字段。失败时不得更新哈希基线。

```powershell
git -C D:\Workspace\siargo rev-parse HEAD   # 取当前 HEAD 完整哈希
```

```json
{
  "head": "<短哈希>",
  "headFull": "<完整哈希>",
  "version": "<本次发布版本>",
  "packageTime": "<当前时间>",
  "assetHashes": {
    "assets/css/siargo.css": { "sourceSha256": "<源文件SHA256>", "minSha256": "<压缩文件SHA256>" }
  }
}
```

上述 JSON 只展示一个资源的哈希结构；实际写入必须使用 manifest 内完整的 12 项，不能只保存示例项。

### 第 10 步：输出打包报告

向用户汇报：

- 版本变更：旧版本 → 新版本（主/子/修订）
- 本次目标版本整理后的 CHANGELOG 全文
- 变更文件统计（已提交 N 个 commit / 未提交修改 M 个文件）
- 产物路径：`target\siargo-release\siargo.rar`（及 `siargo-release.tar.gz`）
- `pdev=pro` 切换确认
- 12 项清单中实际重压/跳过/仅有 min 的文件及发布目录产物一致性校验结果（仅打包报告，不写入 CHANGELOG）
- 基线已更新至新 HEAD

---

## 注意事项

1. **顺序不可颠倒**：CHANGELOG、版本号修改和清单资源的增量压缩校验必须在 `mvn package` 之前完成，确保发布包携带最新内容。
2. 禁止修改 `src\main\resources\` 下的任何源配置；环境切换只作用于 `target` 产出目录。
3. 禁止在技能流程中执行 `git commit` / `git push`（提交时机由用户自行决定）。
4. 日常开发不写 CHANGELOG；正式发布时按第 2 步从真实 diff 归纳业务主题，统一处理本次范围内的遗留草稿及待发布参考，不提前写入版本记录。已发布历史默认不改；用户明确要求重写或修正时，保留原版本、日期及事实边界，不凭空补充功能或改变发布归属。
5. 每次打包执行第 4 步，仅对 12 项清单中有变化或缺少 min 的文件重压；未变化跳过。日常不压缩，压缩及产物同步不写入 CHANGELOG。
6. `mvn` 不在 PATH 时，先只读检查项目 Wrapper、现有环境变量及用户或项目已配置的 Maven 路径，校验存在后复用；仍不可用时再报告并请用户提供配置。不得猜测路径、自动安装或修改环境配置。
