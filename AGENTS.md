# siargo 项目开发规则（Codex）

> 适用于所有涉及 `E:/Workspace/siargo` 的开发、审查与排查请求。由 qoder 规则（`.qoder/rules/siargo.md`、`siargo-coding-rules` 技能）移植；编码规范已整合进 `E:/Workspace/agents/siargo-*.md` 子智能体定义。
> 项目 Wiki：`wiki/qdoer/`；JBolt 平台源码参考库：`E:/Workspace/源码/`（只读）。

## 项目概况

矽翔质管部管理系统：**JFinal 5.2.7 + JBolt Core 5.3.6 + JDK 17 + MySQL + Caffeine 2.9.3 + Undertow 2.2.37 + Fastjson + Hutool + POI/iTextPDF + Cron4j + JFinal Event**。

业务模块（`src/main/java/cn/jbolt/admin/siargo/`）：

| 模块 | 复杂度 | 说明 |
|------|--------|------|
| customer / supplier | 简单 CRUD | 新模块的第一参考 |
| dms | 中 | 主子表、文件上传、关键字搜索、软删除 |
| equipment | 高 | 设备全生命周期、主子表、编制→审核、时间线、证书 |
| qarep | 最高 | 多阶段审批（insp=1 待检→2 精度→3 外观→4 包装→5 批准）、Excel 导入、PDF 生成、回收站、流程统计缓存 |
| api / apicalllog | 中 | 对外 API + Token 签名 + 调用日志 |
| imi / cme / changelog | 低 | 简单模块 |

## P0 红线（违反即 bug）

1. **Controller 禁止直接操作数据库**（禁止 `Db.find()`、`Db.queryLong()` 等），必须通过 Service 层。
2. 后台 Controller 必须加 `@CheckPermission(PermissionKey.SIARGO)` + `@UnCheckIfSystemAdmin`。
3. 写操作禁止 `@Before(Tx.class)` 声明式事务，必须手动 `Db.tx(() -> {...})`。
4. **缓存清理/异步通知必须 afterCommit**：`clear*Cache()`、`EventKit.post`、WebSocket 推送都在 `Db.tx()` 返回 `true` 之后执行；Service 写方法不负责清缓存，由 Controller 在事务提交后统一清理。
5. 禁止手动修改 `siargo/model/base/Base*Model.java`（代码生成器自动维护）。
6. Model 字段读写用动态方法 `set("field", value)` / `getLong("field")`，不用传统 JavaBean getter/setter。
7. 主键统一雪花算法 `bigint`，JSON 序列化必须 `@JSONField(serializeUsing = ToStringSerializer.class)`，防前端精度丢失。
8. 时间字段用 `datetime`（非 timestamp）；业务表前缀 `siargo_`，表/字段 snake_case。
9. 禁止引入 Spring Boot/Spring MVC、MyBatis/JPA、Vue/React 等冲突框架；JSON 统一 fastjson。
10. 只读参考现有代码，不覆盖或修改未要求改动的文件。
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

**路由关键**：`ProjectConfig.configRoute()` 按子包显式 `this.scan("cn.jbolt.admin.siargo.xxx")`。新增业务子包必须补一行 scan，否则 404；`api` 子包单独注册（无登录拦截）。

## Controller 规范（事务模板）

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

`volatile 字段 + ReentrantLock + 双重检查锁（DCL）+ TTL 过期`；数据变更后由 Controller 在事务提交后调用 `clear*Cache()`。

## 权限与角色

- 角色 SN：`1` 系统管理员、`211` 精度检验员、`212` 外观检验员、`213` 包装检验员、`214` 批准员、`221` 审核员。
- 角色查询 `RoleService.findIdBySn(sn)`；判断 `JBoltUserAuthKit.hasRole(userId, roleId)`。
- 当前用户 `JBoltUserKit.getUserId()`；异步线程/定时任务中为 null，须先取值再传入。

## 前端规范（摘要）

- 页面用 `#@jboltLayout()`；`#set(pageId=RandomUtil.random(6))` 唯一 ID；表格 `data-jbolttable` + `jb_tpl_box`；弹窗 `data-dialogbtn`；删除 `data-ajaxbtn` + `data-confirm` + `data-handler`。
- 表单字段命名 `Model前缀.字段名`；提交链复用 `_formjs.html`。
- 流程环节颜色一律引用 `assets/css/siargo.css` 的 `--flow-*` 变量（acc 精度 / vis 外观 / pack 包装 / appr 批准 / done 完成），模板 `data-color` 用语义键 `acc|vis|pack|appr|done`，禁止散写十六进制。
- 修改 `assets/js` / `assets/css` 后必须同步 `.min.js` / `.min.css`（terser / csso）。
- 复杂前端任务派发子智能体 `E:/Workspace/agents/siargo-frontend.md`，简单任务主代理直接完成；详见 `wiki/qdoer/05-前端开发指南.md`。

## 数据库设计

- 主键 `id BIGINT` 雪花算法非自增；时间 `DATETIME`；状态 `TINYINT` 注释枚举含义；外键 `表名_id` 建 INDEX；唯一约束 UNIQUE KEY。
- 软删除模式 A：`status`(1 正常/0 删除) + `deleted_time`（DMS/Equipment）；模式 B：`vd` + `delete_time` + `delete_des`（qarep Product）。
- 新表后运行代码生成器生成 `Base*Model`；`model_package` 配置在 `dbconfig/mysql/config.properties`。

## 常见陷阱（速查）

1. `tinyint(1)` 被 JDBC 映射为 Boolean，注意 `getBoolean()` vs `getInt()`。
2. JDK 17 运行需完整 `--add-opens` 参数；classpath 通配符可能失效，Caffeine jar 显式添加。
3. `Ret.fail(Object, Object)` 已弃用 → `renderJsonFail(msg)` / `renderJson(ret)`。
4. 事务内禁止删磁盘文件：先收集路径，提交后再删；文件移动失败要有移回补偿。
5. 删除/重命名 action 前全局检索前端模板与 JS 中的 URL 引用。
6. 定时任务用 cron4j `ITask` + 5 段表达式，`run()` 全方法 try-catch；任务内不依赖 `JBoltUserKit`。
7. 事件/推送/清缓存必须 afterCommit（P0.4）。
8. 新增菜单五步法：`jb_permission` 插入 + `PermissionKey` 常量 + 角色分配 + 清缓存 + tab 注册。

## 审查与自学习

- 复杂代码审查派发子智能体 `E:/Workspace/agents/siargo-code-review.md`，输出 CRITICAL / WARNING / INFO 分级报告；简单检查主代理直接完成。
- 生成/修改 Java 代码后用 `$self-improving-auto` 记录原稿；用户确认后提取规则更新 `E:/Workspace/agents/siargo-backend.md` 中的编码规范。
