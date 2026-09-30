# siargo 项目开发规则（Codex）

> 适用于所有涉及 `D:/Workspace/siargo` 的开发、审查与排查请求。由 qoder 规则（`.qoder/rules/siargo.md`、`siargo-coding-rules` 技能）移植；编码规范已整合进 `D:/Workspace/.codex/agents/siargo-*.toml` 子智能体定义。
> 项目 Wiki：`wiki/qdoer/`；Git 历史模式：`.claude/skills/siargo-patterns/SKILL.md`；JBolt 平台源码参考库：`D:/Workspace/源码/`（只读）。

## 项目概况

矽翔质管部管理系统：**JFinal 5.2.7 + JBolt Core 5.3.6 + JDK 25 + MySQL + Caffeine 2.9.3 + Undertow 2.2.37 + Fastjson + Hutool + POI/iTextPDF + Cron4j + JFinal Event**。

业务模块（`src/main/java/cn/jbolt/admin/siargo/`）：

| 模块 | 复杂度 | 说明 |
|------|--------|------|
| customer / supplier | 简单 CRUD | 新模块的第一参考 |
| dms | 中 | 主子表、文件上传、关键字搜索、软删除 |
| equipment | 高 | 设备全生命周期、主子表、编制→审核、时间线、证书 |
| qarep | 最高 | 多阶段审批（逻辑顺序 insp=1 精度待检→按产品属性可选 6 成品检漏待检→2 外观待检→3 包装待检→4 待批准→5 完成，各环节签 accq/lt/funq/appq/allq）、Excel 导入、PDF 生成、回收站、流程统计缓存 |
| api / apicalllog | 中 | 对外 API + Token 签名 + 调用日志 |
| imi / cme / changelog | 低 | 简单模块 |

## P0 红线（违反即 bug）

1. **Controller 禁止直接操作数据库**（禁止 `Db.find()`、`Db.queryLong()` 等），必须通过 Service 层。
2. 后台 Controller 必须配置与模块匹配的 `@CheckPermission(...)` + `@UnCheckIfSystemAdmin`；多数 siargo 模块使用 `PermissionKey.SIARGO`，已有模块专属权限（如 `PermissionKey.SIARGO_CHANGE_LOG`）必须保留。`JBoltApiBaseController` 对外 API 不适用后台权限注解。
3. **事务写法分场景**（`@Before(Tx.class)` 是 JBolt 生成器/平台标准，尊重原生写法）：
   - 单条简单写（先校验后单条 DB、无文件/事件/多步）→ 用 `@Before(Tx.class)` 声明式事务。
   - **多步批量写、物理文件操作、事件/WebSocket 推送 → 必须手动 `Db.tx(() -> {...})`**。原因：JFinal `@Before(Tx.class)` 只认"抛异常/返回 boolean false"为回滚信号，siargo 的 `Ret.fail`（软失败不抛异常）不会触发回滚，多步写中途失败会**部分提交**。
   - Service 内禁止在 `@Before(Tx.class)` 事务内做文件删除/移动或嵌套 `Db.tx()`；物理文件路径在事务内收集、事务提交后删除。
4. **事务相关副作用和缓存失效必须对齐真正的提交边界**：`EventKit.post`、WebSocket 推送、物理文件删除必须在事务提交后执行。Controller 持有 `Db.tx()` 时，由 Controller 在 `txOk` 后清缓存；Service 完整持有内部事务时，可在内部事务成功返回后清缓存。参与外层事务的 Service 不得提前清缓存、发事件或删文件。
5. 禁止手动修改 `siargo/model/base/Base*Model.java`（代码生成器自动维护）。
6. Model 字段读写用动态方法 `set("field", value)` / `getLong("field")`，不用传统 JavaBean getter/setter。
7. 主键统一雪花算法 `bigint`，JSON 序列化必须 `@JSONField(serializeUsing = ToStringSerializer.class)`，防前端精度丢失。
8. 时间字段用 `datetime`（非 timestamp）；业务表前缀 `siargo_`，表/字段 snake_case。
9. 禁止引入 Spring Boot/Spring MVC、MyBatis/JPA、Vue/React 等冲突框架；JSON 统一 fastjson。
10. 为完成用户已授权的需求，可修改必要的实现文件、直接依赖、调用方和验证文件。用户明确限定文件范围时遵守该范围；必要改动超出该范围时，先说明原因并请求扩大范围。不得修改无关文件，不得覆盖、回退或清理用户已有改动。
11. 禁止查询或依赖 `src/main/resources/sql/` 下的 SQL 文件（仅备份用途，不代表当前表结构）。
12. 文件上传必须路径穿越三层校验：拒绝 `..` → 前缀白名单 → rename 前 `getCanonicalPath().startsWith()` 二次确认。

## 分层架构

```
src/main/java/cn/jbolt/
├── common/config/ProjectConfig.java         # 路由显式 scan、引擎、Handler 装配
├── extend/config/ExtendProjectConfig.java   # 扩展配置/定时任务
├── extend/systemlog/ProjectSystemLogTargetType.java
├── admin/siargo/<module>/                   # 业务 Controller + Service（按子包）
└── siargo/model/                            # 业务 Model
    ├── base/Base*Model.java                 # 自动生成，禁止修改
    └── *Model.java                          # 业务扩展
```

Controller 继承 `JBoltBaseController`（后台）或 `JBoltApiBaseController`（API）；Service 继承 `JBoltBaseService<M>`。

**路由关键**：`ProjectConfig.configRoutes(Routes me)` 按子包显式 `this.scan("cn.jbolt.admin.siargo.xxx")`。新增业务子包必须补一行 scan，否则 404；`api` 子包单独注册（无登录拦截）。

## Controller 规范（事务模板）

**单条简单写**可用 `@Before(Tx.class) + renderJson(service.xxx())`（与生成器产物一致）；**多步批量写 / 物理文件 / 事件通知**用以下 `Db.tx()` 模板——`Ret.fail` → lambda 返回 false → 回滚，事务提交后做 afterCommit：

```java
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/模块名", viewPath = "/_view/admin/siargo/模块名")
public class XxxAdminController extends JBoltBaseController {
    @Inject private XxxService service;

    public void save() {
        final Ret[] retHolder = {null};
        boolean txOk = Db.tx(() -> {
            retHolder[0] = service.save(getModel(XxxModel.class, "model前缀"));
            return retHolder[0] != null && retHolder[0].isOk();
        });
        if (txOk) {
            service.clearXxxCache(); // === afterCommit：事务提交后才清缓存 ===
        }
        renderJson(retHolder[0] != null ? retHolder[0] : Ret.fail("保存失败"));
    }
    // update / delete / batchXxx 同构
}
```

- 返回 JSON：`renderJsonData(data)` / `renderJsonFail(msg)` / `renderJsonSuccess()` / `renderJson(Ret)`；返回页面：`render("index.html")`。
- 参数：`getPara("name")`、`getLong(0)`、`getModel(Xxx.class, "前缀")`、`getFile()`；校验用 `isOk()` / `notOk()` / `hasNotOk()`。
- 注意 `isOk()` 数值语义是 **>0**，可为 0 的参数用 `notNull()`。

## Service 规范

```java
public class XxxService extends JBoltBaseService<XxxModel> {
    private final XxxModel dao = new XxxModel().dao();
    @Override protected XxxModel dao() { return dao; }
    @Override protected int systemLogTargetType() {
        return ProjectSystemLogTargetType.NONE.getValue();
    }
}
```

- 返回值统一 `Ret.ok()` / `Ret.fail()` 或 `success(msg)` / `fail(msg)`；写失败返回 `fail()`，**不抛 RuntimeException 做流程控制**。
- 使用其他 Model 时注入对应 Service，禁止直接操作非本 Service 的 Model。
- 删除前实现 `checkCanDelete(model, kv)` 实质引用检查（有子表引用的主数据禁止留空实现）。
- 日志：`addSaveSystemLog()` / `addUpdateSystemLog()` / `addDeleteSystemLog()`。

## SQL 场景选择

| 场景 | 方式 |
|------|------|
| 单表简单 CRUD | `paginateByKeywords("id","desc",pn,ps,kw,"name")` |
| 单表条件查询 | `Sql.mysql()` 链式 API（`eq/like/between/orderBy/build`） |
| 多表 JOIN / GROUP BY | `Db.find()` / `Db.paginate(select, from, params)` 原生 SQL + `?` 占位符 |
| 聚合统计 | `Db.queryLong()` / `Db.queryInt()` |

- Record 查询的雪花 ID 必须 `CAST(id AS CHAR) AS id`（Record 不走 @JSONField）。
- `Db.paginate()` 含 GROUP BY 时传 `isGroupBySql=true`。
- GROUP_CONCAT 聚合 + 关键字过滤用 `EXISTS` 子查询，避免 WHERE 对 JOIN 列 LIKE 丢聚合行。
- 前端模板引用的关联字段必须显式 LEFT JOIN + AS 别名（如 `c.name AS customerName`）。

## Caffeine 缓存模板

`volatile 字段 + ReentrantLock + 双重检查锁（DCL）+ TTL 过期`；缓存失效由真正的事务所有者在提交成功后触发：Controller 持有事务则由 Controller 清理，Service 完整持有内部事务则可由 Service 在事务成功后清理。

## 权限与角色

- 角色 SN：`1` 系统管理员、`211` 精度检验员、`212` 外观检验员、`213` 包装检验员、`214` 批准员、`215` 成品检漏检验员、`221` 审核员。
- 角色查询 `RoleService.findIdBySn(sn)`；判断 `JBoltUserAuthKit.hasRole(userId, roleId)`。
- 当前用户 `JBoltUserKit.getUserId()`；异步线程/定时任务中为 null，须先取值再传入。

## 前端规范（摘要）

- 页面用 `#@jboltLayout()`；`#set(pageId=RandomUtil.random(6))` 唯一 ID；表格 `data-jbolttable` + `jb_tpl_box`；弹窗 `data-dialogbtn`；删除 `data-ajaxbtn` + `data-confirm` + `data-handler`。
- 标准操作优先沿用平台原生图标和按钮，参考 `D:/Workspace/源码/jfinalxueyuan-jbolt_platform-jbolt_platform_3-/jfinalxueyuan-jbolt_platform-jbolt_platform_3-/src/main/webapp/_view/_admin/dictionary/`。上移/下移使用 `jbolt_table_btn` + `fa fa-arrow-up c-info` / `fa fa-arrow-down c-info`，沿用原生尺寸、间距、悬停样式，与相邻编辑/删除操作保持协调；禁止按 `data-title="上移/下移"` 添加全局描边、底色或强制尺寸。保留排序 URL、`data-ajaxbtn`、tooltip 和原有 handler；展开/收起箭头不属于排序操作。
- 表单字段命名 `Model前缀.字段名`；提交链复用 `_formjs.html`。
- 流程环节颜色一律引用 `assets/css/siargo.css` 的 `--flow-*` 变量（acc 精度 / leak 成品检漏 / vis 外观 / pack 包装 / appr 批准 / done 完成），模板 `data-color` 用语义键 `acc|leak|vis|pack|appr|done`，禁止散写十六进制。
- `src/main/webapp/_view/admin/siargo/` 下所有业务页面的 JS 统一放在 `src/main/webapp/assets/js/siargo.js`，CSS 统一放在 `src/main/webapp/assets/css/siargo.css`，按模块分区维护；模板只保留结构、数据传递和必要的初始化调用，不新增业务 JS/CSS 片段。Enjoy 动态数据通过页面属性或初始化参数传入，静态资源不含 Enjoy 指令。
- 日常修改后不压缩、不更新 `.min` 文件。打包前按 `siargo-package.md` 和下方资源清单增量压缩，源/min 哈希有变化、基线缺失或 min 缺失时才重压；压缩及产物同步不写入 CHANGELOG，也不并入业务条目。
- 复杂前端任务派发子智能体 `D:/Workspace/.codex/agents/siargo-frontend.toml`，简单任务主代理直接完成；详见 `wiki/qdoer/05-前端开发指南.md`。

### 指定 CSS/JS 的环境加载与打包

- 仅处理 `application.properties` 的 `project_assets.environment_files` 中的 12 项：`assets/css/` 下 `jbolt-admin.css`、`jbolt-mine.css`、`jbolt-page-loading.css`、`jbolt-wechat-menu.css`、`login.css`、`siargo.css`；`assets/js/` 下 `jbolt-admin.js`、`jbolt-mine.js`、`jbolt-wechat-menu.js`、`login.js`、`relogin.js`、`siargo.js`；同名 `.min` 版本视为同一资源。外部 URL、CDN、第三方插件和清单外资源不做任何处理，保持现状。
- 环境依据为 `src/main/resources/application.properties` 的 `pdev`：`pdev=dev` 优先使用未压缩的 `*.css` / `*.js`，缺少对应未压缩版本时，再使用压缩的 `*.min.css` / `*.min.js` 文件；`pdev=pro` 优先使用对应的 `*.min.css` / `*.min.js`，缺少对应压缩版本时，再使用未压缩的 `*.css` / `*.js` 文件。
- 按同目录、同版本、同基础名称匹配并验证文件实际存在，保留 URL 查询参数、版本号和片段；清单内模板引用使用 `ProjectAssets.url(...)`，外部引用保持原样，不修改第三方动态插件加载器。
- 开发环境缺少未压缩版本时，使用对应压缩文件，两种版本均缺失时才报告资源缺失；不生成不存在的路径，不将压缩内容改名冒充未压缩版本。
- 生产发布目录必须包含缺少压缩版本时所需的未压缩文件；两种版本均缺失时报告资源缺失。发布前检查 `package.xml` 排除规则，不得排除唯一可用版本。
- 打包脚本 `scripts/Compress-ProjectAssets.ps1` 只处理上述清单，按 `last_package.json.assetHashes` 判断源/min 哈希是否变化；变化、基线缺失或 min 缺失时重压，未变化跳过，仅有 min 保留。全部打包成功后才合并哈希基线，压缩操作不写 CHANGELOG。

## 数据库设计

- 主键 `id BIGINT` 雪花算法非自增；时间 `DATETIME`；状态 `TINYINT` 注释枚举含义；外键 `表名_id` 建 INDEX；唯一约束 UNIQUE KEY。
- 软删除模式 A：`status`(1 正常/0 删除) + `deleted_time`（DMS/Equipment）；模式 B：`vd` + `delete_time` + `delete_des`（qarep Product）。
- 新表后运行代码生成器生成 `Base*Model`；`model_package` 配置在 `dbconfig/mysql/config.properties`。

## 常见陷阱（速查）

1. `tinyint(1)` 被 JDBC 映射为 Boolean，注意 `getBoolean()` vs `getInt()`。
2. Maven 编译目标以 `pom.xml` 的 `<jdk.version>` 为准（当前为 JDK 25）；运行需完整 `--add-opens` 参数，classpath 通配符可能失效，Caffeine jar 显式添加。
3. `Ret.fail(Object, Object)` 已弃用 → `renderJsonFail(msg)` / `renderJson(ret)`。
4. 事务内禁止删磁盘文件：先收集路径，提交后再删；文件移动失败要有移回补偿。
5. 删除/重命名 action 前全局检索前端模板与 JS 中的 URL 引用。
6. 定时任务用 cron4j `ITask` + 5 段表达式，`run()` 全方法 try-catch；任务内不依赖 `JBoltUserKit`。
7. 事件/推送/清缓存必须 afterCommit（P0.4）。
8. 新增菜单五步法：`jb_permission` 插入 + `PermissionKey` 常量 + 角色分配 + 清缓存 + tab 注册。

## 审查与自学习

- 复杂代码审查派发子智能体 `D:/Workspace/.codex/agents/siargo-code-review.toml`，输出 CRITICAL / WARNING / INFO 分级报告；简单检查主代理直接完成。
- 开始修改前运行 `git status --short`，审查范围同时包含已跟踪 diff 与未跟踪业务文件；现有改动属于用户，不覆盖、不回退。
- 当前提交主题大多为 `fix bug`、`1`、`update`，CHANGELOG 与改动意图必须依据真实 diff，禁止只读 commit message。
- qarep 状态/字段变更按跨层变更处理；发布版本同步 `pom.xml` 与 `ProjectServer.getProjectVersion()`；日常只维护静态资源源文件，发布打包前统一生成并校验 `.min` 文件。
- 验证能力以当前仓库和可用环境为准。执行与改动相称且已获授权的测试及实际业务路径检查，可使用浏览器和自动化工具；验证时机遵守工作区统一打包与测试规则。
- 编译、单测、HTTP、浏览器、文件和 PDF 验证分别报告，不相互替代。只有确实依赖人工判断、缺少环境或尚未获得必要授权的步骤才列为待人工项，并说明原因；继续其他不受阻塞的验证。浏览器保存、审批等实际写库行为仍须满足数据库授权要求。
- 只把重复出现、可复用且有证据的模式沉淀到 `.claude/skills/siargo-patterns/` 或项目级 instincts；一次性实现细节不提升为规则。
