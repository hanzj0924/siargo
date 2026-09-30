# Controller 层开发指南

> 核对日期：2026-09-30；项目版本：v2.9.4；源码基线：`18c1de8`。本文依据当前源码及本地 JFinal/JBolt 依赖核实，不代表运行环境验收。
>
> [返回文档导航](README.md)

## 1. 职责与入口

Controller 接收请求、获取参数、调用 Service、选择响应，并在需要时持有业务事务。数据库查询、关联检查、状态流转和持久化由 Service 完成，Controller 不直接调用 `Db.find()`、`Db.update()` 或操作业务 Model 的 `save/update/delete`。

| 请求类型 | 基类与认证边界 | 当前参考 |
|---|---|---|
| 后台页面与浏览器业务请求 | `JBoltBaseController`；会话、后台权限、业务校验分别生效 | [CustomerAdminController](../../src/main/java/cn/jbolt/admin/siargo/customer/CustomerAdminController.java) |
| 对外 API | `JBoltApiBaseController`；遵守对应 API 的认证与响应协议 | [接口、应用中心与调用日志](16-对外接口应用中心与调用日志.md) |
| 复杂审批、批量操作 | Controller 或 Service 中一个明确的事务所有者 | [QareportAdminController](../../src/main/java/cn/jbolt/admin/siargo/qarep/QareportAdminController.java) |

业务 Controller 位于 `src/main/java/cn/jbolt/admin/siargo/<模块>/`，页面通常位于 `src/main/webapp/_view/admin/siargo/<模块>/`。模块内可以按子业务继续分包。

## 2. 路由与后台权限

后台 Controller 的基本声明如下，示例采用当前客户模块的真实路径：

```java
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/customer",
      viewPath = "/_view/admin/siargo/customer")
public class CustomerAdminController extends JBoltBaseController {
    @Inject
    private CustomerService service;

    public void index() {
        render("index.html");
    }

    public void datas() {
        renderJsonData(service.paginateAdminDatas(
            getPageNumber(), getPageSize(), getKeywords()));
    }
}
```

`@Path` 定义 Controller 路径与模板根目录；action 名形成后续路径。`render("index.html")` 在该 Controller 的 `viewPath` 中查找页面。

[ProjectConfig.configRoutes](../../src/main/java/cn/jbolt/common/config/ProjectConfig.java) 对业务子包逐项 `scan`，对外 `api` 子包另行注册。新建业务子包时要检查扫描入口，不能假定存在一个扫描整个 `cn.jbolt.admin.siargo` 的总入口。后台业务路由还接入了会话认证和异端登录下线拦截。

- 权限键必须匹配模块；已有专用键，例如版本日志权限，不得统一改成 `SIARGO`。
- `@UnCheckIfSystemAdmin` 配合后台权限注解使用，不表示跳过业务规则。
- 菜单或按钮隐藏不能代替后端权限校验；审批角色、可操作对象及当前阶段在业务处理前再次校验。
- 对外 API 不照搬后台权限注解，也不能因为没有后台登录拦截就视为匿名接口。
- 注释中的 `GET/POST` 不是请求方法限制。新增或调整方法约束前核对当前平台支持的注解与真实调用方，不能只修改说明文字。

新增或改造接口还须按工作区规则进入应用中心登记、授权及后端启停控制。现有接入范围、控制能力与待补齐项见[专题 16](16-对外接口应用中心与调用日志.md)，不得把应用资料 CRUD 视为已经完成接口运行控制。

## 3. 参数、表单与校验

| 需求 | 当前常用方式 | 注意事项 |
|---|---|---|
| URL 路径参数 | `getLong(0)` | 与 `edit/{id}` 一类路由约定对应 |
| 命名参数 | `getPara("name")`、`getInt("status")` | 可空值与默认值按业务约定处理 |
| 分页与关键字 | `getPageNumber()`、`getPageSize()`、`getKeywords()` | 查询条件传给 Service，不在 Controller 拼 SQL |
| Model 表单绑定 | `getModel(Customer.class, "customer")` | 对应 `customer.id`、`customer.name` 等提交字段 |
| 上传文件 | `getFile(...)` | 上传后续路径校验和归属校验必须执行 |

表单前缀、数据库字段和响应 JSON 字段是三个不同层面的契约。`getModel(..., "customer")` 绑定表单参数，并不证明响应对象采用相同字段名；响应消费规则见 [Model 指南](04-Model层开发指南.md)。

校验顺序建议为：参数是否完整 → 目标是否存在 → 当前用户是否有操作权限 → 目标当前状态能否操作 → 关联数据与业务规则。绑定得到的 Model 不是可信数据库实体，不能直接信任其中的审核人、状态、所属对象或文件地址。

`isOk()` 对数值的项目常用语义是大于零。允许 `0` 的枚举、开关或查询条件应明确判断 `null` 和取值范围，避免把合法的零当成未提供。批量 ID 的格式应与真实调用方一致，不能未经检查把 JSON 数组、逗号字符串互换。

当前业务状态和审批参数含义统一见[检验报告单专题](11-检验报告单业务.md)、[设备管理专题](14-设备管理.md)，本篇不另维护状态表。

## 4. 响应契约

| 返回内容 | 常用方式 |
|---|---|
| 页面 | `set("customer", customer)` 后 `render("edit.html")` |
| 列表或分页数据 | `renderJsonData(data)` |
| Service 操作结果 | `renderJson(ret)` |
| 简单成功/失败 | `renderJsonSuccess()` / `renderJsonFail(message)` |
| 文件 | 选择与当前文件类型相符的下载渲染方法；由 Service 生成或查找文件 |

沿用现有接口的响应封装。平台业务响应通常使用 `state`、`msg`、`data`；前端不能机械按 `result.success` 判断。API 的返回协议和后台 JSON 也不能混用。

响应发生在业务结果确定之后。正常渲染失败 JSON 只是构造响应，不是向事务系统抛出异常。参数错误、不存在与业务拒绝应有可理解的提示；日志中不得输出凭据、签名原文或敏感请求内容。

## 5. 事务选择与真正提交边界

| 场景 | 项目写法 | 回滚和提交后的处理 |
|---|---|---|
| 先完成校验，再单条简单数据库写入 | 可使用 `@Before(Tx.class)` | 不依赖返回 `Ret.fail()` 回滚已经发生的写入 |
| 多步、批量、关联更新 | 由一个入口手动 `Db.tx(IAtom)` | Service 失败结果必须转换为 lambda 的 `false` |
| 文件移动/删除、事件、WebSocket、业务缓存 | 明确事务所有者并单独处理副作用 | 不把数据库事务当作文件系统或消息系统事务 |

**JFinal 5.2.7 的默认 `Tx` 拦截器不会根据返回 `Ret` 或普通 boolean 决定提交。** 默认分支调用 action/方法后提交，异常路径才回滚；`renderJson(Ret.fail(...))` 同样不会触发回滚。`Db.tx(IAtom)` 则读取 `IAtom.run()` 的 boolean，`false` 会回滚。这两个机制不能混写为“返回失败就自动回滚”。本地依赖依据为 `com.jfinal.plugin.activerecord.tx.Tx` 与 `com.jfinal.plugin.activerecord.DbPro`。

下面是事务结构示意，`saveBatch`、`clearCache` 代表具体模块需实现的 Service 方法，不是框架内置 API：

```java
public void saveBatch() {
    final Ret[] result = {Ret.fail("保存失败")};
    boolean committed = Db.tx(() -> {
        result[0] = service.saveBatch(/* 已校验的业务参数 */);
        return result[0] != null && result[0].isOk();
    });
    if (!committed) {
        renderJson(result[0] != null && result[0].isFail()
            ? result[0] : Ret.fail("保存失败"));
        return;
    }
    service.clearCache();
    renderJson(result[0]);
}
```

这段结构成立的前提是 action 自己拥有最外层事务，不能再套 `@Before(Tx.class)`。嵌套 `Db.tx()` 会参与已有事务，内层返回成功不代表连接已经提交；副作用应交给最外层事务所有者安排。框架的提交回调能力与项目当前惯用写法要区分，不能仅把一个方法命名为 `afterCommit` 就认为已经接通提交边界。

缓存失效、事件发布、WebSocket 推送和物理删除安排在真正提交之后。需要事务中准备文件时，必须有失败移回/清理方案；删除路径先收集，数据库成功提交后再删除。详细职责见 [Service 指南](03-Service层开发指南.md) 和[文件管理专题](13-技术通知单与文件管理.md)。

## 6. 修改现有 action 的工作顺序

1. 检查路由、权限和入口，查找模板、`siargo.js`、API 调用方对 action 的引用。
2. 按真实提交字段和响应 JSON 确定契约；保留未涉及的查询与界面行为。
3. 确定 Service 还是 Controller 持有最外层事务，列明文件、通知与缓存副作用。
4. 修改后核对页面入口、Service 返回值、前端处理和错误路径；仅在关键逻辑需要时执行最小必要验证。

浏览器保存、审批和导入也会写数据库，不能因为是页面操作而绕过当次写库授权。代码检查、运行验证与实际生效分别说明；服务由用户手动重启。

## 7. 继续阅读

- [Service 层开发指南](03-Service层开发指南.md)：查询、事务参与、缓存、文件和日志。
- [前端开发指南](05-前端开发指南.md)：表格、表单、Dialog 与统一响应处理。
- [对外接口应用中心与调用日志](16-对外接口应用中心与调用日志.md)：API 身份、调用记录与统一管理边界。
