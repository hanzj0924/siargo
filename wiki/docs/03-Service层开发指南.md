# Service 层开发指南

> 核对日期：2026-09-30；项目版本：v2.9.4；源码基线：`18c1de8`。本文依据当前源码及本地依赖核实，不代表运行环境验收。
>
> [返回文档导航](README.md)

## 1. 职责与基本结构

Service 负责查询、业务校验、关联关系、状态流转和持久化。Controller 不承担数据库操作；Service 不依赖页面渲染和浏览器对象。需要其他模块数据时通过对应 Service 协作，不在新代码中随意操作其他模块 Model。

常见基类是 `JBoltBaseService<M>`。下面采用客户模块的实际查询形态：

```java
public class CustomerService extends JBoltBaseService<Customer> {
    private final Customer dao = new Customer().dao();

    @Override
    protected Customer dao() {
        return dao;
    }

    @Override
    protected int systemLogTargetType() {
        return ProjectSystemLogTargetType.NONE.getValue();
    }

    public Page<Customer> paginateAdminDatas(
            int pageNumber, int pageSize, String keywords) {
        return paginateByKeywords("id", "desc",
            pageNumber, pageSize, keywords, "name");
    }
}
```

完整参考：[CustomerService](../../src/main/java/cn/jbolt/admin/siargo/customer/CustomerService.java)。旧业务中仍能看到生成 getter/setter，新增业务代码遵守项目动态字段规则；不要为统一风格重写无关旧代码。

## 2. 查询方式与分页

| 场景 | 选择 | 约束 |
|---|---|---|
| 单表简单列表 | 基类分页和查询方法 | 字段、排序和过滤以真实需求为准 |
| 单表组合条件 | `Sql.mysql()` 或 Service 的 `selectSql()` | 使用条件 API 传值，不拼接用户输入 |
| JOIN、聚合、复杂列表 | Service 内 `Db.find()` / `Db.paginate()` | SQL 参数占位；输出列与前端契约一起检查 |
| 单值统计 | `Db.queryLong()` 等 | 明确统计对象、时间口径和空结果 |

基础参数化查询示例：

```java
List<Record> rows = Db.find(
    "SELECT CAST(id AS CHAR) AS id, name "
    + "FROM siargo_customer WHERE name LIKE ? ORDER BY id DESC",
    "%" + keywords + "%");
```

`Record` 查询中的雪花 ID 采用显式 `CAST(... AS CHAR)` 保证接口契约，前端保留字符串。关联列需要显式选择和别名；别名最终如何序列化，见 [Model 与 JSON 契约](04-Model层开发指南.md)。

排序列、排序方向和表名不能作为普通值占位，若允许前端选择，需在 Service 中映射到明确的允许列表。不要把原始参数直接拼接为 SQL 结构。

聚合分页使用与当前 JBolt/JFinal 签名匹配的 `isGroupBySql=true` 重载。DMS 列表同时使用 `GROUP_CONCAT` 与关键字搜索：匹配关键字通过 `EXISTS` 子查询，避免先过滤 JOIN 行而丢掉未命中的其他关键字。真实参考：[DmsFileService](../../src/main/java/cn/jbolt/admin/siargo/dms/file/DmsFileService.java)。

分页、导出和统计应各自说明数据口径；不能只因方法名含 `all`、`total` 就认为其范围相同。[首页统计专题](15-首页看板与报告统计.md) 负责现行业务统计定义，本篇不复制这些 SQL。

## 3. 写入、软失败与事务参与

Service 的预期业务失败通常返回 `Ret.fail(...)` 或 `fail(...)`；成功返回 `Ret.ok()`、`success(...)` 或 `ret(boolean)`。框架异常、I/O 异常与业务拒绝不是同一类结果，不为普通校验失败人为抛异常作为流程控制，也不能捕获异常后假装写入成功。

写入的一般顺序是：校验参数 → 查目标与关联数据 → 校验权限/当前状态 → 设置服务器负责的字段 → 执行写入 → 检查返回结果。更新时需区分“字段未提交”“主动清空”和“前端没有正确回显”；部分表单不能无条件清空未提交的关联数据。

多步写入必须先确定事务所有者：

| 所有者 | Service 内行为 | 提交后工作 |
|---|---|---|
| Controller | Service 返回 `Ret`，不再独立假定提交 | Controller 将 `Ret.isOk()` 转为事务 lambda 结果，成功后清缓存/通知 |
| 当前 Service 完整持有事务 | 内部使用 `Db.tx()`，失败转 `false` | 仅在确认不存在外层事务且本次提交成功后执行 |
| 当前 Service 参与外层事务 | 不提前执行不可回滚副作用 | 将待执行信息返回给最外层所有者 |

`@Before(Tx.class)` 的默认实现不读取 `Ret` 或普通返回 boolean；`Db.tx(IAtom)` 才按 lambda 的 boolean 回滚。批量处理不能先成功保存若干条，再返回 `Ret.fail()` 并假定声明式事务会自动撤销。详细入口示例见 [Controller 事务章节](02-Controller层开发指南.md)。

嵌套事务共享连接，内层 `Db.tx()` 成功返回不代表外层已提交；框架抛出的嵌套事务失败信号也不能随意吞掉。新增嵌套调用前应追踪整个调用链，而不是只查看当前方法的 `Db.tx()`。

审批和状态修改须核对数据当前状态，不能仅依赖页面按钮是否可见。现行业务状态、签名字段、可编辑范围与回退行为分别由[检验报告单](11-检验报告单业务.md)、[设备管理](14-设备管理.md)专题维护。

## 4. 删除与关联完整性

基类删除通常经过 `checkCanDelete(model, kv)` 等钩子；有引用关系时必须进行实际检查，不能保留一个无条件返回 `null` 的占位实现。需要了解现有删除链时从当前模块 `delete`、`checkCanDelete`、`afterDelete` 一起追踪。

`afterDelete`、`afterSave` 等模型或 Service 生命周期钩子名称不等同于数据库提交回调。钩子可能仍运行在事务内；物理文件删除和消息发布不能因为位于 `afterXxx` 中就直接执行。

软删除、回收站恢复、永久删除是不同业务操作。关联关系是否一起失效、文件是否仍被其他对象引用、统计是否排除删除记录，都要按所属专题确定；不在通用 Service 模板中套用同一删除规则。

## 5. 文件与数据库的一致性

项目已经有统一存储工具：[SiargoStorage](../../src/main/java/cn/jbolt/common/storage/SiargoStorage.java) 和 [SiargoUploadFiles](../../src/main/java/cn/jbolt/common/storage/SiargoUploadFiles.java)。它们负责业务根目录、URL/物理路径转换、临时文件归属、路径边界与文件移动等基础能力，业务 Service 负责对象归属和保存时机。

- 不把请求中的路径直接传给 `File` 删除或移动；复用当前存储工具及模块允许的目录。
- 临时文件转正式文件前校验临时目录、文件归属、文件存在性和目标冲突。
- 数据库事务不能回滚文件系统；事务中发生移动的流程需要明确的移回/清理补偿。
- 删除时先在数据库事务中完成关联处理并收集路径，真正提交后再执行物理删除；仍被引用的文件不能误删。
- 文件发布失败与发布后旧文件清理失败要区别记录，不能把已经提交的数据库操作当作尚未发生。

已实现的文件生命周期分别见[技术通知单与文件管理](13-技术通知单与文件管理.md)、[设备管理](14-设备管理.md)、[Excel 导入与 PDF 输出](12-Excel导入与PDF输出.md)。

## 6. 缓存与失效

平台用户、角色、字典、权限等共享数据优先复用现有缓存入口。业务聚合统计使用自己的缓存时，先检查现有实现的 key、TTL、锁和清理方法；不要为同一数据再建平行缓存。

[QareportService](../../src/main/java/cn/jbolt/admin/siargo/qarep/QareportService.java) 的统计缓存使用 `volatile` 字段、`ReentrantLock`、双重检查与 TTL，并在统一清理入口联动多个统计缓存。该业务缓存模式和底层配置的 Caffeine 缓存组件不是同一个概念，不能把所有 `volatile Map` 都称作 Caffeine 缓存。

缓存设计至少明确：

1. key 包含哪些过滤条件，返回的是产品记录数、数量还是其他聚合。
2. 返回集合能否被调用者修改；共享缓存对象不能被页面组装逻辑污染。
3. 哪些新增、更新、删除、恢复和审批操作会影响该缓存。
4. 失效由真正的事务所有者在提交成功后触发，回滚路径不发布新统计。

采用模型自动缓存时还要核实其触发范围；原生 SQL 写入不会因为存在模型类就自动执行该模型的生命周期逻辑。不要在没有确认钩子的情况下宣称已经同步失效。

## 7. 日志、事件与异步上下文

业务日志类型由 [ProjectSystemLogTargetType](../../src/main/java/cn/jbolt/extend/systemlog/ProjectSystemLogTargetType.java) 与 Service 的 `systemLogTargetType()` 对应；按现有模块调用 `addSaveSystemLog`、`addUpdateSystemLog`、`addDeleteSystemLog` 等方法。数据库审计写入与事务的关系要跟随实际调用链，不把所有日志都视作提交后的外部副作用。

事件和 WebSocket 推送必须在业务事务提交后发布。事件监听器读取到的应是已提交数据；必要的通知先保留业务记录，实时推送不能代替持久化通知。

`JBoltUserKit` 的登录上下文属于请求线程，异步监听器、线程池和定时任务中不能假定能取得当前用户。需要操作者时，在请求入口取得并显式传入。项目实际通知触发范围见[检验报告单专题](11-检验报告单业务.md)，不能因为平台具有推送能力就推定所有环节已使用。

## 8. 修改后的核对重点

围绕本次修改确认参数、查询条件、关联字段、返回值、事务所有者及副作用时序即可。关键路径需要验证时优先复用已有检查，不机械新增全套测试；数据库写入和服务启停按工作区授权规则执行。

列表展示和编辑保存共用关联数据时，要同时检查“已有名称显示”和“打开编辑不修改仍保留绑定”。查询编译通过不证明 JSON 字段正确，静态检查也不等于文件、PDF或浏览器流程已经验收。
