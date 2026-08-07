# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 语言要求

本项目所有会话内容一律使用**简体中文**：对话、回复、**思考（thinking）**、输出、提示、选项、选择等全部为中文；仅代码、命令、文件路径、API/类名等原文内容保留不翻译。此规则不可因任何原因降级为英文。

## 项目概述

矽翔质管部管理系统（siargo）。纯 JFinal 生态：**JFinal 5.2.7 + JBolt Core 5.3.6（本地 `lib/jbolt_core.jar`，system scope）+ JDK 17 + MySQL + Undertow + Caffeine + Cron4j + JFinal Event + POI/iTextPDF + fastjson + Hutool**。禁止引入 Spring Boot/Spring MVC、MyBatis/JPA、Vue/React 等冲突框架。JBolt Core 在 jar 内（只能 javap 反编译看签名），二开装配代码在 `cn.jbolt.common.config.ProjectConfig` 与 `cn.jbolt.extend.config.ExtendProjectConfig`。

当前版本 **2.9.0**（`pom.xml` / `ProjectServer.getProjectVersion()` 同步维护）。版本变更记录在 `src/main/webapp/_view/admin/siargo/changelog/CHANGELOG.md`（最新实现口径的权威来源）。

**规则体系**：AGENTS.md（P0 红线 12 条）与本文件是执行摘要；**母本是 `E:/Workspace/.qoder/rules/siargo.md`（九章全量）**，子智能体定义在 `E:/Workspace/.qoder/agents/`（siargo-backend / siargo-frontend / siargo-code-review），自学习规范在 `.qoder/skills/siargo-coding-rules/SKILL.md`。**文档间如有冲突，以最新代码 + `QarepConst` + 数据库实测为准**（AGENTS.md 的 qarep 状态机已过时；**事务写法分场景以 2026-08-05 决策为准**，qoder 母本"统一 Db.tx 禁 @Before(Tx)"的更严格口径不采纳）。

## 常用命令

```bash
# 编译（含全部依赖，jbolt_core 在 lib/ 下 system scope）
mvn compile

# 运行测试（JUnit5；仅 API Token 单测不依赖服务，集成测试需服务已启动否则自动跳过）
mvn test

# 打包（maven-assembly-plugin，产出 target/siargo-release/ 目录 + siargo-release.tar.gz；
# src/main/resources → 部署包 config/，webapp → webapp/，依赖 → lib/，见 package.xml）
mvn clean package

# 启动/停止/重启（根目录 siargo.bat / siargo.sh；启动类 cn.jbolt.starter.Starter，端口见 undertow.properties，默认 80）
siargo.bat start     # Windows
./siargo.sh start    # Linux

# 代码生成器（main 方法直接运行，连 MySQL 生成后重新编译）
# - Model/BaseModel：cn.jbolt.extend.gen.ModelGenerator（当前聚焦生成 siargo_product）
# - Controller/Service/页面：cn.jbolt.extend.gen.MainLogicGenerator

# qoder 自学习（改完 .java 后记录原稿，提交/用户要求时提炼规则）
python e:/Workspace/.qoder/skills/self-improving-auto/scripts/observe-original.py <文件绝对路径>
python e:/Workspace/.qoder/skills/self-improving-auto/scripts/observe-and-improve.py --auto
```

- 打包发布走 `/siargo_package` 技能（自动升级版本号 + 汇总变更生成 CHANGELOG + 部署）。
- 开发期 IDE 直接运行 `cn.jbolt.starter.Starter`；JDK 17 需六个 `--add-opens java.base/...=ALL-UNNAMED`（见 siargo.bat）。
- 数据库：本地 MCP MySQL `127.0.0.1:3306` root/siargo，db=siargo。**表结构一律以数据库实测为准**，`src/main/resources/sql/` 仅备份勿信。

## 架构大图（需跨文件理解的关键）

### 三层 + 路由 scan 机制

`Controller（JBoltBaseController 后台 / JBoltApiBaseController API）→ Service（JBoltBaseService<M>）→ Model（cn.jbolt.siargo.model，业务类 @TableBind + base/Base*Model 自动生成禁改）`。

**路由是最大的坑**：`ProjectConfig.configRoutes()` 按子包显式 `this.scan("cn.jbolt.admin.siargo.xxx")`，9 个业务子包（apicalllog/changelog/cme/customer/dms/equipment/imi/qarep/supplier）挂后台拦截器（`SiargoTerminalOfflineInterceptor` 多端登录 + `JBoltAdminAuthInterceptor`）；`api` 子包单独注册且 `setMappingSuperClass(true)` 无登录。**新增业务子包必须补一行 scan，否则 404**（仅在已扫描子包下加 Controller 才免改配置）；新页面建 `webapp/_view/admin/siargo/<模块>/` 目录。新 Routes 类要 `setBaseViewPath` + `addInterceptor(JBoltAdminAuthInterceptor)`。

装配链：`Starter.main → ProjectServer.create → ProjectConfig（常量/路由/引擎/插件/拦截器/handler）→ ExtendProjectConfig`。**平台级定制（拦截器/Handler/定时任务/多数据源/XSS 白名单/模板指令）一律走 `ExtendProjectConfig` 扩展点，不改 ProjectConfig 本体**。定时任务（cron4j `ITask` + 5 段表达式）注册在 `configCron4jPlugin`；JFinal Event 异步线程池在 `configJFinalEvent`。跨库写操作无事务（ARP 局限），写操作尽量留主库。新 Model 包必须加入 `dbconfig` 的 `model_package`，否则框架不识别。

### 事务与缓存纪律

**事务分场景（2026-08-05 决策为准，尊重 JBolt 生成器/平台原生写法）**：
- **单条简单写**（先校验后单条 DB、无文件/事件/多步）→ 用 `@Before(Tx.class)` 声明式事务。
- **多步批量写、物理文件操作、事件/WebSocket 推送** → 必须手动 `Db.tx(() -> {...})`。原因：`@Before(Tx.class)` 只认抛异常/boolean false 回滚，`Ret.fail`（软失败）不触发，多步写中途失败会**部分提交**。Controller 模板：事务外校验 → `Db.tx` 调 service（**lambda 内不取请求参数**，`getLong(0)` 等在 lambda 外先取）→ 返回 true 后 afterCommit 清缓存/发事件/删文件。
- Service 内禁止在 `@Before(Tx.class)` 事务里做文件删除/移动或嵌套 `Db.tx()`（嵌套失败抛 `NestedTransactionHelpException`，须先 `catch (NestedTransactionHelpException e) { throw e; }`，别被宽泛 `catch(Exception)` 吞掉）。
- Service 写方法：校验失败/写失败返回 `fail()`，**禁止抛 RuntimeException 做流程控制**；**不负责清缓存/发事件/删文件**（统一由 Controller afterCommit 做）。
- 物理文件删除/移动必须事务提交后执行（`Db.tx` 只回滚 DB 不回滚文件系统）；删除前事务外 `getFilePathsByIds` 收集路径；事务内移动文件失败须 `moveFilesBack` 移回补偿；替换文件旧路径经 `ret.set("oldFilePath")` 带出给 Controller。参考 `DmsFileService`。
- `EventKit.post`、WebSocket 推送等副作用必须 afterCommit，否则异步监听器读到未提交数据；事件监听器放 `cn.jbolt._admin.event` 包 + `@EventListener(async=true)`。
- 缓存选型：基础数据→`CACHE.me`；小表→`@JBoltAutoCache`（idCache/keyCache）；聚合统计→`volatile + ReentrantLock + DCL + TTL`（qarep 30 分钟，分页 30 秒）。**`@JBoltAutoCache` 只在 Model save/update/delete 自动失效，`Db.update` 等原生 SQL 改表不触发，需手动 remove**；聚合统计勿用 `@JBoltAutoCache`（无 TTL）。缓存失效同样遵守 afterCommit。
- 原子自增用 `updateSql().set(col, new SqlExpress("col+1"))`，禁止读-改-写。查重/唯一性用 `exists`/`existsName`/`existsSn`（编辑时传 id 排除自身），禁手写 COUNT。
- `isOk()` 数值语义是 **>0**（0/负数无效），可为 0 的参数用 `notNull()`。`JBoltEnum.getIntValueByText` 不存在时返回 **-1000** 而非 null。

### 权限与角色

后台 Controller 必须 `@CheckPermission(PermissionKey.SIARGO)` + `@UnCheckIfSystemAdmin`。角色 SN：1 系统管理员 / 211 精度检验 / 215 成品检漏检验 / 212 外观检验 / 213 包装检验 / 214 批准 / 221 审核。`JBoltUserKit.getUserId()` 是 ThreadLocal，在定时任务/异步线程中为 null，须先取值再传入。按钮显隐用模板 `#permission(PermissionKey.XXX)`，禁止用 JS 隐藏代替（Controller 级注解才是安全底线）；行级数据隔离必须在 Service 查询条件实现（平台无内置行级权限）；`@UnCheck` 的字典/options 端点禁放敏感数据。

### qarep 状态机（唯一权威：`QarepConst.java`）

`siargo_product.insp` + `lt_status`（1 有成品检漏 / 2 无）语义（**表/模型注释、AGENTS.md 第 17 行均已过时，勿参照**）：

- 无检漏（lt_status=2）：1 精度待检 → 2 外观待检 → 3 包装待检 → 4 待批准 → 5 完成
- 有检漏（lt_status=1）：1 精度待检 → **6 成品检漏待检** → 2 外观待检 → 3 包装待检 → 4 待批准 → 5 完成

批准（batchInspection）按目标 insp 映射签名列与角色：2→accq/211、6→lt/215、3→funq/212、4→appq/213、5→allq/214。驳回（batchReject）按当前 insp 清空上一环节签名：2（lt=1 回 6 清 lt，否则回 1 清 accq）/6（回 1 清 accq）/3（回 2 清 funq）/4（回 3 清 appq）。可驳回范围用 `isRejectableInsp()` 判断（2/3/4/6；**INSP_REJECT_MIN/MAX 常量已删除**）。环节名运行时口径：funq=外观、appq=包装、allq=批准放行。`ProductModelClassifier` 返回值 1 小流量/2 大流量/3 传感器，与 `prod_type` 字典 sn（1 传感器/2 小流量/3 大流量）相反，需经 `ExcelService.resolveProdType` 换算。

- 编辑页 `QareportService.updateProductInfo` 支持服务端 insp 更新（1~6）：lt_status 变更时自动归一化（无→有且当前外观待检→退回 6；有→无且当前检漏待检→进 2）；`syncInspWithSignatures` 用 COALESCE 补签缺失环节、清空超出环节，条件更新 `WHERE insp=库内旧值` 防并发；签名 uid/time 一律以服务端生成为准。
- SQL 新增成品检漏字段须同步 `LEFT JOIN jb_user` 取检验员姓名（`lt_user ON lt_user.id=sp.lt_uid`）；PDF 报告新增字段用 `optionalStr`（可选取值），勿用 `safeStr` 强校验，否则无检漏产品 PDF 生成失败。
- `PDFService.safeRelativePath` 最新口径：只拒 `..`、反斜杠开头的 UNC 路径、盘符绝对路径；`/` 开头视为相对 webRoot 的模板管理路径（先循环去前导斜杠）。**旧文档"拒绝 / 开头"已废弃**。

### 前端规范

- 页面必须用 JBolt 原生布局 `#@jboltLayout()`；`#set(pageId=RandomUtil.random(6))` 唯一 ID，页面内元素/表格/行模板 id 带 `_#(pageId)` 后缀；新页面从 `_view/_admin/common/demopage/` 三件套骨架起步。
- 表格 `data-jbolttable` + `<textarea class="jb_tpl_box">` 行模板（`{@each}`/`${data.x}`）；**条件刷新唯一入口是 `jboltTableReadByConditions(tableId, conditions)`**（自动回第 1 页，无 setConditions API）；隐藏列必须 `jboltTableHideColumn`，禁 CSS `display:none`（固定列克隆会错位）；排序三件套 `data-sortable-columns` + `data-sort` + `data-default-sort-column`；自动加载属性名是 `data-autoload`。
- **JBoltTable 固定列是克隆主表 DOM，每次加载/分页/排序重建：禁止把固定列 tr/tbody 的 jQuery 引用缓存为闭包变量，事件必须从永不被销毁的 `.jbolt_table_box` 容器委托 + 回调内动态 find**；**行模板每行片段禁止固定 id**（多行渲染重复 id，`$('#..')` 只命中第一个），用 class + `closest().find()` 相对查找。
- 弹窗 `data-dialogbtn`（data-title/url/area/btn/handler）或 `DialogUtil.openBy/openNewDialog`；**平台无 openJBoltDialog/layerDialog 函数**，禁止自引 layer/bootstrap modal。行操作超过 3 个收进右键菜单 `data-menu-option`。
- 表单字段 `Model前缀.字段名`（对应后端 `getModel(Xxx.class,"前缀")`）；提交链复用 `_formjs.html`（防重→FormChecker→ajaxSubmit）+ `data-rule` 声明式校验；后端 Service 必须二次校验，不能只依赖前端。
- AJAX 一律 `Ajax.post/get/uploadFormData`，禁裸 `$.ajax/$ .post`；提示用 `LayerMsgBox`/`JBoltNotifyBox`，禁自写 toast；后端 `renderJsonFail(msg)` → 前端 `data.state!="ok"` → `LayerMsgBox.alert`。**三个保留 msg 值 `jbolt_system_locked`/`jbolt_nologin`/`jbolt_terminal_offline` 由平台全局处理，业务提示禁止使用同名文本**。
- 流程环节颜色一律引用 `assets/css/siargo.css` 的 `:root --flow-*` 变量（acc 精度 / leak 检漏 / vis 外观 / pack 包装 / appr 批准 / done 完成），模板 `data-color` 用语义键，禁散写十六进制；衍生色用 `color-mix(in srgb, var(--flow-X) N%, ...)`。**siargo.css 是多模块共享大文件，禁止 Write 整体重写，用 Edit 以分区末尾/下一分区标题为锚点定点插入**。
- **Enjoy `#date(value, pattern)` 只接受 Date，SQL `DATE_FORMAT` 出的 String 直接 `#(field??)` 输出**；页内 JS 必须写在 `#define js()` 段内（pjax 只携带四段，散写 script 在 pjax 切入时失效）；初始化走 `data-init-handler`、清理走 `data-close-handler`；跨模块跳转用 `parent.openTab`/`refreshJboltTab`，不用 `window.location`。
- **自定义 onclick 打开 JBoltLayer 抽屉必须显式传 `event` 并在入口 `e.stopPropagation()`**，否则点击冒泡到 `.jbolt_admin_main` 全局委托导致抽屉一闪即关。
- echarts 用 `data-require-plugin="echarts"` + init-handler 内 `getInstanceByDom` 检查再 dispose 重建，关闭前 dispose；打印用 hiprint，禁止 window.print/jsPDF。
- **改 `assets/js`/`assets/css` 后必须同步 `.min.js`/`.min.css`**（terser/csso）；大 HTML 页面做多次精确 Edit，禁整体 Write 重写，禁止遗留 `_tmp_*` 临时文件。列表页上线前用 `/admin/druid/monitor` 自查 N+1。
- 首页看板 ECharts 全部在 `webapp/_view/_admin/index/dashboard.html`（服务端注入数据）；统计口径：`getFlowCounts`（全量有效产品）vs `getDashboardFlowCounts`/`getDonutData`/`getTotalQSI`（限定当年 `YEAR(create_time)=YEAR(CURDATE())`）；`getDonutData` 当年 insp=5 分组 COUNT(*) 不去重；`clearFlowCountsCache` 须联动清理 dashboard 缓存。

### 数据模型

16 张 `siargo_` 业务表；主键雪花 bigint **非自增**（新增须显式 set id）；时间字段 datetime；状态 tinyint 注释枚举含义；无公共审计列、时间戳命名不统一。软删除两种模式：A `status`+`deleted_time`（dms/equipment）、B `vd`+`delete_time`+`delete_des`（qarep Product 回收站）。Record 查询雪花 ID 必须 `CAST(id AS CHAR)`；模型字段用动态 `set("f",v)`/`getLong("f")`；雪花 Long 加 `@JSONField(serializeUsing=ToStringSerializer.class)`。

BaseModel 自动填 `create_time`/`update_time`/`create_user_id`/`update_user_id`（save/update 无需手动 set）；**siargo 旧表 `creator_id`/`creator_time` 命名不在自动链内，新表务必用框架约定名**。字段命名即 UI 组件约定：`type`/`state`/`category` 结尾→字典 select+sn、`xxx_id`→autocomplete、`_time`→datetime 可排序、`is_`/`enable`→switchbtn、`name`/`remark` 结尾自动进 keywords 模糊匹配。代码生成器重跑后 getter 签名可能变化（Long→Integer），调用处显式 `.intValue()`，别改 BaseModel。

## 原生机制优先（新需求先查再动手，禁止自行实现）

- Excel：`JBoltExcel`（downloadTpl/importExcel/exportExcelByForm 六件套 + readModels），禁手写 POI/EasyExcel
- 异步解耦：`EventKit.post` + `@EventListener`；推送/通知复用 `SysNotice`/`Todo` + `JBoltWebSocketUtil`，禁自建 WebSocket/通知表/轮询
- 定时任务：`Cron4jPlugin.addTask`（5 段分时日月周，非 Quartz，禁秒级），禁另起 Timer/ScheduledExecutor；run() 全 try-catch
- 附件：简单附件用 `JBoltFileService`（业务表只存 jbolt_file id）；复杂生命周期才自建文件表（参考 DmsFileService）
- 层级数据：`convertJsTree`/`convertToModelTree`，禁手写递归
- 列表人名/部门名：模板直读 `JBoltUserCache`/`JBoltRoleCache`/`CACHE.me.getUserName`，禁后端循环查库或 JOIN 拼名
- 富文本：平台内置端点（Neditor/Summernote），新富文本端点必须加 `xssHandler.unlimited` 白名单
- 打印：hiprint 模板库；敏感词：`JBoltSensitiveWordUtil`；业务枚举：`JBoltEnum.addToTvBeanMap()` 注册（value 续接 20007+）
- 对外 API：`JBoltApiBaseController` + `@JBoltApplyJWT`/`@OpenAPI` 或沿用 `SiargoApiTokenUtil`，**禁止第三种令牌方案**；外部调用方身份用 `Application` 表（appId+appSecret+签名开关），不自建 app 表
- 自定义 WebSocket 指令用 command 扩展点（`JBoltWS.registerCommand`），禁止自建 WebSocket 连接

## 权威文档索引（开发前先查）

1. `AGENTS.md` — P0 红线 12 条 + 规范（本文件是它的执行摘要；**注意其中 qarep 状态机已过时；事务分场景写法仍有效，以 2026-08-05 决策为准**）。
2. `E:/Workspace/.qoder/rules/siargo.md` — **规则母本**（九章全量：前端/后端分层/数据库/部署/禁止事项/编码规范/参考模块/MCP 查询/原生机制 146 条索引）。
3. `E:/Workspace/.qoder/agents/` — 子智能体定义：`siargo-backend.md`（67KB 后端细则）、`siargo-frontend.md`（64KB 前端细则）、`siargo-code-review.md`（审查清单 + R1-R131 复用对照）。**Claude Code 原生版已整合至 `~/.claude/agents/`**（siargo-backend / siargo-frontend / siargo-code-review，`tools: *`，用 Agent 工具 `subagent_type` 调用）。
4. `E:/Workspace/.qoder/skills/siargo-coding-rules/SKILL.md` — 自学习编码规范（P0/P1/P2 分级），生成代码前先读。
5. `wiki/qdoer/` — 分层开发手册（01 平台核心架构 ~ 08 项目配置速查；`jbolt-native-mechanisms.md` 2159 行全索引）。
6. `src/main/webapp/_view/admin/siargo/changelog/CHANGELOG.md` — 最近实现口径与迁移史。
7. `E:/Workspace/源码/` — JBolt 平台源码参考库（只读；PRO 版 5.2.0+Java8 比本地旧，行为以本地为准）。

## 常见陷阱（速查）

- `tinyint(1)` JDBC 映射为 Boolean（`getBoolean()` vs `getInt()`）；`Ret.fail(Object,Object)` 已弃用。
- Record 直出雪花 Long 丢精度，必须 `CAST(id AS CHAR)`；`Db.paginate()` 含 GROUP BY 必须 `isGroupBySql=true`；一对多 GROUP_CONCAT + 关键字过滤用 EXISTS 子查询，勿在 WHERE 对 JOIN 列 LIKE。
- **`recoverById` 疑似框架 bug（追回仍 set is_deleted=TRUE），回收站功能自行实现 restore**（qarep 先例）。
- 模型/表注释多处过时（rep_type、repair_result、qarep 环节名双语义）——**一切以 `QarepConst` + 数据库实测为准**；`reject_insp`（2 外观/3 包装/4 批准）与 `product.insp` 数值语义不同，误用会串环节。
- 删除/重命名 action 前全局检索前端模板与 JS 的 URL 引用；同一端点可能存在不同别名（changeActive/toggleActive 曾并存）。
- 文件上传三层路径校验：拒 `..` → 前缀白名单 → `getCanonicalPath().startsWith()` 二次确认（P0.12）；上传目录用 `JBoltUploadFolder` 常量 + `todayFolder`，禁硬编码。
- 新增菜单五步法：权限插入 + PermissionKey 常量 + 角色分配 + 清缓存 + tab 注册。
- 平台自带模块（wechat/wxa/appdevcenter/devdoc）siargo 业务不使用；`ApiTestRoutes`/`WechatTestRoutes` 注释"正式上线请删掉"。
- qarep 批量 PDF 导出依赖服务器 WinRAR（`winrar_exe_path` 配置项），缺失即 renderFail。
- 生产环境 `demo_mode` 必须 false；上传大小超限查 `max_post_size`（KB，siargo=102400）；生产禁开 `jbolt_druid_dev_mode_full_sql_log`。
- 环境差异只改 `config-pro.properties`（`application.properties` 的 pdev 切换）；新增配置文件类型须补 maven-jar-plugin 排除清单（jar 内配置优先，否则部署改配置不生效）。
- 批量插入用 `batchSave`（batchSize ≤500），禁循环 save；`getParaToLongArray` 可能为 null 需判空；用 `getPageNumber`/`getSortColumn` 取参，禁手写解析。
