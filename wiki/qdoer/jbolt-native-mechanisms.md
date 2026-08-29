# JBolt 原生机制手册（源码取证版）

> 本手册基于 JBolt pro 平台源码整理，作为 siargo 项目开发时"优先使用原生机制"的依据。
> 平台源码位置（jbolt_core.jar 无源码时的用法参考库）：
> `E:\Workspace\源码\jfinalxueyuan-jbolt_platform-jbolt_pro-\jfinalxueyuan-jbolt_platform-jbolt_pro-`（下文简称 **PRO**）
> 旧版 `jbolt_platform_3` 仅作辅助参考。siargo 项目与 PRO 同源，`_admin`/`_view/_admin` 平台代码基本一致。
>
> **使用原则：新需求先查本手册，源码中有原生机制的，禁止自行造轮子。**

---

## 一、后端机制

### 1. JBoltExcel 导入导出

**适用场景**：列表数据导出 Excel（按查询条件/选中行/全部）、Excel 批量导入、导入模板下载。

**源码参考**：
- `PRO\src\main\resources\gentpl\codegen\controller_common_template.jf`（L252-371：downloadTpl / importExcel / exportExcelByForm / exportExcelByCheckedIds / exportExcelAll 五个标准 action）
- `PRO\src\main\resources\gentpl\codegen\service_common_template.jf`（L299-410：exportExcel / importExcel / getImportExcelTpl）
- 真实用例：`PRO\src\main\java\cn\jbolt\_admin\codegen\CodeGenAdminController.java`（L711-765）

**Service 层标准写法**：

```java
// 导出：构建 JBoltExcel 对象，Controller 负责 render
public JBoltExcel exportExcel(List<Xxx> datas) {
    return JBoltExcel.create()
        .setSheets(
            JBoltExcelSheet.create()
                .setHeaders(1,
                    JBoltExcelHeader.create("name", "名称", 15),
                    JBoltExcelHeader.create("create_time", "创建时间", 20))
                .setModelDatas(2, datas));  // 数据从第2行开始
}

// 导入：从文件读取 Model 列表 + 事务批量保存
public Ret importExcel(File file) {
    StringBuilder errorMsg = new StringBuilder();
    JBoltExcel jBoltExcel = JBoltExcel.from(file)
        .setSheets(JBoltExcelSheet.create()
            .setHeaders(1, JBoltExcelHeader.create("name", "名称"))
            .setDataStartRow(2));
    List<Xxx> models = JBoltExcelUtil.readModels(jBoltExcel, 1, Xxx.class, errorMsg);
    if (notOk(models)) {
        return errorMsg.length() > 0 ? fail(errorMsg.toString()) : fail(JBoltMsg.DATA_IMPORT_FAIL_EMPTY);
    }
    boolean success = tx(() -> { batchSave(models); return true; });
    return success ? SUCCESS : fail(JBoltMsg.DATA_IMPORT_FAIL);
}
```

**Controller 层标准写法**：

```java
public void exportExcelByForm() {  // 按查询条件导出
    Page<Xxx> pageData = service.getAdminDatas(getPageNumber(), getPageSize(), getKeywords());
    if (notOk(pageData.getTotalRow())) { renderJsonFail("无有效数据导出"); return; }
    renderBytesToExcelXlsxFile(service.exportExcel(pageData.getList()).setFileName("导出数据"));
}
public void exportExcelByCheckedIds() {  // 选中行导出：前端传 ids
    String ids = get("ids");
    if (notOk(ids)) { renderJsonFail("未选择有效数据，无法导出"); return; }
    renderBytesToExcelXlsxFile(service.exportExcel(service.getListByIds(ids)).setFileName("导出数据"));
}
public void downloadTpl() {  // 下载导入模板
    renderBytesToExcelXlsFile(service.getImportExcelTpl().setFileName("导入模板"));
}
public void importExcel() {  // 执行导入
    String uploadPath = JBoltUploadFolder.todayFolder(JBoltUploadFolder.IMPORT_EXCEL_TEMP_FOLDER);
    UploadFile file = getFile("file", uploadPath);
    if (notExcel(file)) { renderJsonFail("请上传excel文件"); return; }
    renderJson(service.importExcel(file.getFile()));
}
```

**siargo 现状**：未使用。批量数据导出/导入需求应优先用此机制，禁止手写 POI。

### 2. 字典体系（JBoltDictionaryCache + options 接口）

**适用场景**：可配置的下拉选项、枚举标签翻译，避免硬编码选项到代码里。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\dictionary\DictionaryAdminController.java`

**后端 API**：

```java
JBoltDictionaryCache.me.getListByTypeKey("dict_key", true);   // 按字典类型 key 取启用项列表
JBoltDictionaryCache.me.getNameBySn("dict_key", sn);          // 按 sn 翻译为名称（标签）
```

**平台已内置的通用 options 接口**（前端直接用，无需自写）：
- `admin/dictionary/options?key=xxx` — 按字典类型 key 取选项
- `admin/dictionary/poptions?key=xxx` — 多级字典的一级选项
- `admin/dictionary/soptions?key=xxx&pid=xxx` — 按父 ID 取子选项（二级联动）

**前端渲染**（select/radio 组件声明式加载）：

```html
<select class="form-control" name="obj.type"
        data-url="admin/dictionary/options?key=dept_type"
        data-value-attr="sn" data-select="#(obj.type??)" data-autoload></select>
```

**siargo 现状**：少量使用。新增"可配置选项"类需求（如设备类别、故障类型）优先建字典而非建表/硬编码。

### 3. 事件机制（JFinal Event / EventPlugin）

**适用场景**：解耦异步任务——保存业务数据后发通知、写日志、推送消息等副作用，不阻塞主流程。

**源码参考**：
- 监听端：`PRO\src\main\java\cn\jbolt\_admin\event\JBoltEventListener.java`
- 注册：`ProjectConfig.configJFinalEvent()`（siargo 的 `cn.jbolt.common.config.ProjectConfig` 同样已注册 EventPlugin，扫描 `cn.jbolt._admin.event` 包）

**标准写法**：

```java
// 发布事件（任意 Service 中，事务提交后调用）
EventKit.post(sysNotice);  // 事件对象就是普通 Model/Bean，按类型分发

// 监听事件（监听器类放在 EventPlugin 扫描包下）
public class XxxEventListener {
    @EventListener(async = true)   // net.dreamlu.event.core.EventListener，异步执行
    public void onSysNotice(SysNotice notice) {
        // 处理副作用：如 WebSocket 推送
    }
}
```

**siargo 现状**：未使用（通知目前是同步方法调用）。跨模块异步通知类需求优先用事件解耦。
**注意**：事件发布必须在 `Db.tx()` 提交之后（同 afterCommit 纪律），监听器读库才能读到已提交数据。

### 4. Cron4j 定时任务

**适用场景**：定期数据维护、状态超时扫描、报表预生成。

**源码参考**：`PRO\src\main\java\cn\jbolt\common\config\ProjectConfig.java` 的 `configCron4jPlugin()`；内置示例任务 `JBoltOnlineUserClearTask`（每分钟清理在线用户过期数据）。

**标准写法**：

```java
// ProjectConfig.configCron4jPlugin() 或 ExtendProjectConfig 中注册
cron4jPlugin.addTask("*/5 * * * *", new XxxTask());  // cron 表达式（分 时 日 月 周）

// 任务类实现 Runnable
public class XxxTask implements Runnable {
    @Override public void run() { /* 任务逻辑，注意自行捕获异常 */ }
}
```

**siargo 现状**：仅平台内置任务。新增定时需求在 `configCron4jPlugin` / `ExtendProjectConfig.configPlugin` 中追加，禁止另起 Timer/ScheduledExecutor。

### 5. 站内消息 + WebSocket 推送（msgcenter + JBoltWebSocketUtil）

**适用场景**：流程流转通知（如"新检验单待处理"）、实时提醒、强制下线。

**源码参考**：
- `PRO\src\main\java\cn\jbolt\_admin\msgcenter\SysNoticeService.java`（站内通知，receiverType：1=全部/2=角色/3=部门/4=岗位/5=用户）
- `PRO\src\main\java\cn\jbolt\_admin\msgcenter\TodoService.java`（待办事项）
- `PRO\src\main\java\cn\jbolt\_admin\websocket\JBoltWebSocketUtil.java`（推送 API）

**推送 API 全集**：

```java
JBoltWebSocketUtil.sendMessageToUser(userId, msg);            // 指定用户
JBoltWebSocketUtil.sendMessageToUsers(userIds, msg);          // 多个用户
JBoltWebSocketUtil.sendMessageToUserByRoles(roleIds, msg);    // 按角色
JBoltWebSocketUtil.sendMessageToUserByDepts(deptIds, msg);    // 按部门
JBoltWebSocketUtil.sendMessageToUserByPosts(postIds, msg);    // 按岗位
JBoltWebSocketUtil.sendAllMessage(msg);                       // 全员
// 消息构造：
JBoltWebSocketMsg.createSystemCommandMsg("new_notice", "收到新通知,请及时查阅");
```

**标准链路**（源码惯例）：业务 Service 保存 SysNotice/Todo → `EventKit.post(...)` → `JBoltEventListener` 异步监听 → `JBoltWebSocketUtil` 推送 → 前端消息中心红点/弹提示。

**siargo 现状**：未使用。qarep 流程环节通知类需求优先走此链路（按角色推送与 siargo 角色 SN 体系天然契合）。

### 6. JBoltFile 统一附件库（JBoltFileService）

**适用场景**：通用附件管理（上传记录入库、统一文件元数据）。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\jboltfile\JBoltFileAdminController.java`；gentpl 生成的上传代码统一注入 `cn.jbolt.core.service.JBoltFileService`。

**标准写法**：Controller 注入 `JBoltFileService`，`getFile(...)` 拿到 UploadFile 后调用其 save 系列方法记录文件元数据（文件名/路径/大小/上传人），业务表只存 jbolt_file 的 id。

**siargo 现状**：DMS/证书模块自建了文件表（含删除时序、路径安全等定制逻辑，属合理定制）。**简单附件需求**（单表挂几个附件、无复杂生命周期）优先用 JBoltFileService，不再新建文件表。

### 7. JBoltDateRange 日期范围查询

**适用场景**：列表页"开始日期~结束日期"范围过滤。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\loginlog\LoginLogAdminController.java`（L28）+ `LoginLogService.java`；`PRO\src\main\java\cn\jbolt\_admin\msgcenter\JBoltMsgCenterAdminController.java`（L130）。

**标准写法**：

```java
// Controller：一行接收前端 startTime/endTime 参数对
JBoltDateRange dateRange = getDateRange();                 // 默认参数名
JBoltDateRange range2 = getDateRange("create_time", JBoltDateRange.TYPE_DATE);  // 指定列与类型

// Service：直接作为条件参数传入，配合 Sql 构建器构造 between 条件
public Page<LoginLog> paginateAdminDatas(..., JBoltDateRange dateRange, ...) { ... }
```

**siargo 现状**：手写 startTime/endTime 两个参数。新列表页的日期范围过滤优先用 `getDateRange()`。

### 8. 全局配置（JBoltGlobalConfigCache / globalconfig）

**适用场景**：可在后台界面维护的系统级参数（系统名、开关、阈值），避免硬编码或改 properties 重启。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\globalconfig\GlobalConfigAdminController.java`；布局模板中大量使用（如 `__admin_layout.html` L5-L13）。

**标准写法**：

```java
// 后端读取
JBoltGlobalConfigCache.getSystemName("默认值");
JBoltGlobalConfigCache.getConfigValue(key);
```

```html
#-- 前端模板读取 --#
#globalConfig(GlobalConfigKey.SYSTEM_NAME, "JBolt极速开发平台")
```

**siargo 现状**：未主动使用。新增"可配置参数"类需求优先入 globalconfig，不新建配置表。

### 9. 代码生成器标准 action 全集（gentpl）

**适用场景**：新模块 CRUD 的标准形态基准——即使手写代码，也应与生成器产物形态一致。

**源码参考**：`PRO\src\main\resources\gentpl\codegen\controller_common_template.jf` / `service_common_template.jf`（siargo resources 下同样存在）。

**标准 action 清单**（生成器可产出、手写时对齐命名）：

| action | 说明 | 关键实现 |
|--------|------|---------|
| `index` / `datas` | 首页 / 分页数据 | `renderJsonData(service.paginateAdminDatas(...))` |
| `add` / `save` | 新增 | `getModel(Xxx.class, "xxx")` |
| `edit` / `update` | 编辑 | `findById(getLong(0))` 判空 |
| `delete` / `deleteByIds` | 删除 / 批量删除 | `service.deleteById(s)` |
| `toggleEnable` / `toggle列名` | 布尔切换 | `service.toggleBoolean(id, Xxx.ENABLE)` |
| `up` / `down` / `initSortRank` | 排序上移/下移/初始化 | `service.doUp/down/initSortRank` |
| `options` / `tree` | 下拉选项 / 树数据 | 供 select/tree 组件 data-url 使用 |
| `downloadTpl` / `initImportExcel` / `importExcel` | Excel 导入三件套 | 见第 1 节 |
| `exportExcelByForm` / `ByCheckedIds` / `All` | Excel 导出三件套 | 见第 1 节 |

**注意（siargo 特例）**：siargo 的路由是**按子包显式 scan** 的（`ProjectConfig.java` L138-146），新增业务模块包（如 `cn.jbolt.admin.siargo.newmod`）必须在 `configRoute` 中补一行 `this.scan("cn.jbolt.admin.siargo.newmod");`，否则 404。仅在已扫描子包下新增 Controller 才无需改配置。

### 10. 常用 Controller 基类方法补遗（源码取证）

除已知的 renderJsonData/renderFail 系列外，源码中高频出现且 siargo 少用的：

```java
renderJBoltTableJsonData(list, extraData);   // JBoltTable 专用返回（带 extraData 附加数据）
renderBytesToExcelXlsxFile(jboltExcel);      // Excel 下载响应
renderAjaxPortalFail(msg);                   // ajaxportal 加载失败提示
renderPageFail(msg) / renderPjaxFail(msg);   // 页面级失败提示
getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST);  // 带默认值的分页尺寸
getSortColumn("create_time") / getSortType("desc"); // 排序参数（配合表头 data-column 排序）
getEnable() / getStatus() / getType() / getState();  // 常用过滤参数快捷取值
keepPara("selectedId");                      // 参数透传给模板
@ActionKey("tpl/content")                    // 子路径 action（一个 Controller 挂多级路径）
@Before(JBoltNoUrlPara.class)                // 禁止 URL 挂参（options 类接口惯例）
@UnCheck                                     // 单个 action 豁免权限检查
```

### 11. JBoltMsg 常量（统一提示语）

```java
JBoltMsg.PARAM_ERROR             // 参数错误
JBoltMsg.DATA_NOT_EXIST          // 数据不存在
JBoltMsg.DATA_SAME_NAME_EXIST    // 同名数据已存在
JBoltMsg.DATA_IMPORT_FAIL        // 数据导入失败
JBoltMsg.DATA_IMPORT_FAIL_EMPTY  // 导入数据为空
```

错误提示优先用 JBoltMsg 常量，不硬编码字符串。

### 12. JBoltBaseService 基类方法全集（二轮取证）

**源码参考**：`PRO\src\main\resources\gentpl\codegen\service_common_template.jf`（L68-561，生成器产物即基类标准用法）。

| 分类 | 方法 | 说明 |
|------|------|------|
| 查询 | `paginateByKeywords(...)` / `findById` / `findAll` / `getListByIds(ids)` | 标准分页/主键/全量/按 ids 批查 |
| | `findFirst(Okv.by("col", val))` | 单条条件查询（up/down 即用它按 sort_rank±1 找交换行） |
| | `getOptionList()` / `queryColumn(sql)` | 下拉选项 / 单列查询 |
| 批量 | `batchSave(models)` / `batchUpdate(models)` / `batchDelete` / `deleteByIds(ids)` | 批量增删改（配合事务） |
| 查重 | `exists(col, val)` / `existsName(name[, id])` / `existsSn(sn[, id])` | **禁止手写 COUNT SQL 查重**；带 id 参数=排除自身（编辑场景） |
| 检查 | `checkInUse(model)` / `getCount(...)` | 删除前引用检查 / 计数 |
| 排序 | `getNextSortRank()` / `initSortRank()` / `toggleBoolean(id, col)` | 新增取下一序号 / 重排 / 布尔切换 |
| 树 | `convertToModelTree(datas, "id", "pid", (p)->notOk(p.getPid()))` | Model 列表转树（见 §15） |
| 其他 | `selectSql()` / `paginate(sql)` / `find(sql)` / `ret(success)` / `tx(IAtom)` | Sql 构建器入口 / 执行 / Ret 快捷返回 |

**注意（siargo 特例）**：基类 `tx(IAtom)` 与 gentpl 的 `@Before(Tx.class)` 在 siargo 中**均不使用**——统一 Controller 手动 `Db.tx()` + afterCommit 清缓存（见项目规范 6.3）。

### 13. Sql 构建器（cn.jbolt.core.db.sql.Sql）API 全貌

**源码参考**：`PRO\src\main\resources\gentpl\codegen\service_common_template.jf`（L68-126 为完整分页查询模板）。

```java
Sql sql = selectSql().page(pageNumber, pageSize);   // 分页入口（Service 内）；独立创建用 Sql.mysql()
// 条件族
sql.eq("type", type).ne("status", 0).isNull("delete_time");
sql.like("name", keywords).leftLike("sn", kw).rightLike("code", kw);
sql.likeMulti(keywords, "name", "sn", "remark");    // 多列 OR 模糊（列表页关键词标准写法）
sql.betweenDateRange("create_time", dateRange);      // 直接吃 JBoltDateRange（§7）
sql.eqDate("create_time", date).eqDateTime("audit_time", dt);
sql.eqBooleanToChar("enable", enable);               // 布尔字段（tinyint(1)）条件
// 排序/分组/截取
sql.orderBy(sortColumn, sortType);                   // 配合 getSortColumn()/getSortType()
sql.asc("sort_rank").desc("id").groupBy("type").first();
// 软删除快捷
sql.enableEq(true).isDeletedEq(false);
```

**场景定位**：单表/简单条件动态组合用 Sql 构建器；多表 JOIN、聚合统计仍用 `Db.find/paginate` + 原生 SQL（遵循规范 6.7）。

### 14. 系统日志 API 全集与 afterXxx 回调钩子

**源码参考**：`service_common_template.jf`（L165/206/435/451/479 日志调用，L443-490 回调钩子）；扩展点 `PRO\src\main\java\cn\jbolt\_admin\systemlog\SystemLogService.java`。

```java
addSaveSystemLog(id, JBoltUserKit.getUserId(), name);                 // 新增
addUpdateSystemLog(id, userId, name);                                  // 更新
addUpdateSystemLog(id, userId, name, "的字段[enable]值:" + value);     // 更新（带变更明细）
addUpdateSystemLog(null, userId, "所有数据", "的顺序:初始化");          // 批量操作（targetId 传 null）
addDeleteSystemLog(id, userId, name);                                  // 删除
addBatchDeleteSystemLog(ids, userId, title);                           // 批量删除
addRecoverSystemLog(id, userId, name);                                 // 回收站恢复
```

**回调钩子**（Service 覆写，基类在对应操作成功后自动调用，标准日志即写在钩子里）：

```java
@Override protected void afterToggleBoolean(Xxx model, String column, Kv kv) { ... }
@Override protected void afterDelete(Xxx model, Kv kv) { ... }
@Override protected void afterRecover(Xxx model, Kv kv) { ... }
```

**扩展点**：`SystemLogService extends JBoltSystemLogService` 可覆写 `targetTypeToName` / `typeToName` / `processSystemLogUrl`，为自定义 `systemLogTargetType()` 提供名称翻译与跳转链接。

### 15. 树形数据构建（convertJsTree / convertToModelTree）

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\dept\DeptService.java`（L22-25）；`service_common_template.jf`（L292-297 getTreeDatas）。

```java
// jstree 组件数据（部门树标准写法，返回 List<JsTreeBean>）
public List<JsTreeBean> getAllCrudJsTreeDatas(Long checkedId, int openLevel) {
    List<Dept> depts = getAllList();
    return convertJsTree(depts, checkedId, openLevel, null, "sn,name", SORT_RANK, null, false);
}

// Model 自身树形化（树表格/级联数据）
public List<Xxx> getTreeDatas() {
    List<Xxx> datas = getCommonList();
    return convertToModelTree(datas, "id", "pid", (p) -> notOk(p.getPid()));
}
```

**siargo 现状**：未用。层级数据（分类/组织）需求禁止手写递归组树。

### 16. 注解全集补遗（HTTP 方法限定 / API 注解族）

**源码参考**：`PRO\src\main\java\cn\jbolt\apitest\ApiTestController.java`（L36-210）；`gentpl\codegen\controller_template.jf`（L26-36）。

```java
@JBoltHttpGet / @JBoltHttpPost / @JBoltHttpMethod({GET, POST})  // 限定 action 请求方法
@CrossOrigin                     // 跨域（API Controller 级）
@Clear                           // 清除父级拦截器
@OpenAPI                         // 开放接口（免登录）
@JBoltApplyJWT / @JBoltReApplyJWT  // 登录发 JWT / 刷新 JWT
@UnCheckJBoltApi                 // 豁免 JBolt API 校验
@Before(JBoltNoUrlPara.class)    // 禁止 URL 挂参（options 类接口惯例）
```

**⚠ 事务注解差异**：gentpl 模板对 save/update/delete 使用 `@Before(Tx.class)` 声明式事务，**siargo 明确禁止此写法**——必须手动 `Db.tx()`，缓存清理/通知放事务提交后（afterCommit）。参考平台源码时此处不照搬。

### 17. JBoltUploadFolder 与文件处理

**源码参考**：`controller_common_template.jf`（L275-276/L476）；`apitest\ApiTestController.java`（L167-175）。

```java
// 目录常量 + 按天分目录
String path = JBoltUploadFolder.todayFolder(JBoltUploadFolder.IMPORT_EXCEL_TEMP_FOLDER);
// 内置常量：IMPORT_EXCEL_TEMP_FOLDER / USER_AVATAR / EDITOR_NEDITOR_IMAGE / EDITOR_SUMMERNOTE_IMAGE 等
UploadFile file = getFile("file", path);            // Controller 接收上传
jboltFileService.saveImageFile(file, path);          // 图片入 jbolt_file 附件库
jboltFileService.saveAttachmentFile(file, path);     // 附件入库
renderFile(filePath);                                // 文件下载响应
```

**siargo 现状**：自建上传目录常量（合理定制）。新上传需求目录仍走 `JBoltUploadFolder` 常量 + `todayFolder()` 惯例，安全校验遵循规范 6.5 三层校验。

### 18. API 开发模式（JBoltApiBaseController + renderJBoltApi 族）

**源码参考**：`PRO\src\main\java\cn\jbolt\apitest\ApiTestController.java`（L36-210）。

```java
// API Controller 继承 JBoltApiBaseController，配合 §16 API 注解族
JBoltApiKit.getApiUserId();            // 当前 API 用户（JWT 解析）
JBoltApiKit.getWechatMpId();           // 微信公众号上下文
renderJBoltApiSuccess();               // 成功（无数据）
renderJBoltApiSuccessWithData(data);   // 成功（带数据）
renderJBoltApiFail(msg);               // 失败
renderJBoltApiRet(ret);                // 直接渲染 Ret
```

**siargo 现状**：现有 API 用自建 `SiargoApiTokenUtil` MD5 签名（合理定制，继续沿用）。若新增面向 App/小程序的用户级 API，优先评估 JWT 注解族。

### 19. 常用工具类速查

```java
JBoltSnowflakeKit.me.nextId() / nextIdStr()   // 雪花 ID（Long / String）
JBoltIpUtil.getIp(request)                     // 客户端真实 IP
JBoltPinYinUtil                                // 拼音转换（首字母检索）
JBoltArrayUtil                                 // remove/replace/prepend/append/insert/unique
JBoltListMap                                   // 一键分组 List→Map
JBoltStringUtil / JBoltDateUtil                // 字符串 / 日期
```

**模板内缓存直读**（Enjoy 共享对象，免后端组装）：

```html
#(JBoltUserCache.getName(data.createUserId))                    #-- 用户 ID → 姓名 --#
#setLocal(userRoles = JBoltRoleCache.getRoles(data.roles))       #-- 角色 ids → 角色列表 --#
```

同族还有 `JBoltDeptCache` / `JBoltPostCache` / `JBoltDictionaryCache` / `JBoltUserExtendCache`，列表页显示"xx人/xx部门"禁止后端循环查库拼名。

---

## 二、前端机制

### 20. JBoltTable data-* 属性全集

**源码参考**：`PRO\src\main\webapp\_view\_admin\dictionary\index_ajax.html`、`user\index.html`、`systemlog\index.html`、`_jbolt_code_gen\config\_table_portal.html`。

| 分类 | 属性 | 说明 |
|------|------|------|
| 核心 | `data-jbolttable` | 启用组件（必须） |
| | `data-ajax="true"` / `data-url` | AJAX 模式 + 数据接口 |
| | `data-rowtpl` | 行模板 textarea 的 ID |
| | `data-auto-load` | 是否自动加载（默认 true） |
| 分页 | `data-page` / `data-pagesize` / `data-pagesize-options` | 分页容器/每页行数/可选项 |
| 查询 | `data-conditions-form` | 搜索表单 ID（自动序列化为参数） |
| 尺寸 | `data-height="fill_box"` / `data-width` | 自适应填充 |
| 固定列 | `data-fixed-columns-left="1,2"` / `data-fixed-columns-right` | 左/右固定列序号 |
| 列功能 | `data-column-resize="true"` | 列宽拖拽 |
| | `data-column-prepend="1:checkbox:true"` | 前置 checkbox 列（批量操作） |
| 排序 | `data-sort-column` / `data-sort-type` / `data-sortable-columns` | 服务端排序（配合 `getSortColumn()`） |
| 树形 | `data-treetable="all:1"` | 树形表格 |
| 扩展区 | `data-toolbar` / `data-headbox` / `data-footbox` / `data-leftbox` / `data-rightbox` | 五向扩展容器 |
| 行交互 | `data-row-click-handler` / `data-row-click-active` | 行点击回调/激活高亮 |
| 回调 | `data-before-ajax-handler` / `data-ajax-success-handler` | 请求前/成功后钩子 |
| 其他 | `data-copy-to-excel` / `data-menu-option` / `data-shortcutkey-disabled` | 复制到 Excel/右键菜单/禁用快捷键 |

### 21. Excel 导入导出按钮（前端）

**源码参考**：`PRO\src\main\webapp\_view\_admin\_jbolt_code_gen\config\_table_portal.html`、`_import_excel.html`。

```html
<!-- 导出（查询结果）：data-downloadbtn + data-form 带上查询条件 -->
<button data-downloadbtn data-form="#searchForm_#(pageId)"
        data-url="admin/xxx/exportExcelByForm" class="btn btn-outline-primary btn-sm">
  <i class="fa fa-download"></i> 导出</button>

<!-- 导出（选中行）：data-usecheckedids 自动携带勾选行 ids -->
<button data-downloadbtn data-usecheckedids="true"
        data-url="admin/xxx/exportExcelByCheckedIds" class="btn btn-outline-success btn-sm">
  <i class="fa fa-download"></i> 导出选中</button>

<!-- 导入入口：dialog 打开 import_excel.html -->
<button data-dialogbtn data-btn="close" data-area="600,400"
        data-handler="jboltTablePageToFirst" data-title="导入Excel数据"
        data-url="admin/xxx/initImportExcel" class="btn btn-outline-primary btn-sm">
  <i class="fa fa-upload"></i> 导入</button>

<!-- 导入页内：下载模板 + 上传导入框 -->
<button data-downloadbtn data-url="admin/xxx/downloadTpl" class="btn btn-success btn-sm">下载模板</button>
<div class="j_upload_file_box" data-name="file" data-accept="excel" data-maxsize="20480"
     data-confirm="确认导入数据？" data-url="admin/xxx/importExcel"
     data-upload-success-handler="..."></div>
```

### 22. 表单校验体系（data-rule + FormChecker + _formjs.html）

**源码参考**：`PRO\src\main\webapp\_view\_admin\common\_formjs.html`（提交流程）、`dictionary\_form.html`、`dept\_form.html`；校验实现在 `assets\js\jbolt-admin.js` 的 `FormChecker.check(form)`。

**data-rule 规则值**：`required`、`len<=N`、`pint`（正整数）、`pzint`（非负整数）、`>=N;<=M`（范围，分号连接多规则）、`radio`（单选组）。配合 `data-tips` 定义提示语。

**标准 _form.html 提交机制**（源码惯例，免写 AJAX）：

```html
<form onsubmit="return false;" id="xxxForm" action="admin/xxx/save" method="post">
  <input class="form-control" data-rule="required" data-tips="请输入名称"
         name="obj.name" value="#(obj.name??)"/>
</form>
#define js()
  #include("/_view/_admin/common/_formjs.html", formId="xxxForm")
#end
```

`_formjs.html` 统一处理：防重复提交 → `FormChecker.check` → `ajaxSubmit` → state=="ok" 关弹窗刷表格。

### 23. 布局族与页面头部调用

**源码参考**：`PRO\src\main\webapp\_view\_admin\common\` 下布局模板；注册于 `ProjectConfig.configEngines()`（siargo 相同，见其 L194-212）。

| 布局调用 | 模板文件 | 适用场景 |
|---------|---------|---------|
| `#@jboltLayout()` | `__jbolt_layout.html` | **默认首选**，按请求类型（PJAX/AJAX/DIALOG/IFRAME/SINGLE_PAGE）自动分发 |
| `#@dialogLayout()` | `__admin_dialog_layout.html` | 明确只用于 Dialog 的页面 |
| `#@iframeLayout()` | `__admin_iframe_layout.html` | iframe 嵌入页 |
| `#@singlePageLayout(title)` | `__admin_singlepage_layout.html` | 独立单页（无导航菜单） |
| — | `__admin_jboltlayer_layout.html` | JBoltLayer 侧滑层页面 |
| `#@jboltassets()` 内各 define | `__jboltassets.html` | 按需引入组件 css/js（select2、laydate 等） |

### 24. data-handler 回调值全集与 data-openpage

**源码参考**：`PRO\src\main\webapp\assets\js\jbolt-admin.js`（handler 分发逻辑）。

**内置 handler**：
- 表格：`refreshJBoltTable`（含别名 `jboltTableRefresh`）、`refreshJBoltMainTable`、`jboltTablePageToFirst`、`jboltTablePageToLast`、`removeJBoltTableCheckedTr`、`removeTr`
- 页面：`refreshPjaxContainer`、`refreshCurrentAjaxPortal`、`refreshJstree`
- 弹窗：`closeDialog`；系统：`showJboltLockSystem`
- 非内置值会被当作自定义函数名执行（页面自定义回调入口）

**data-openpage 可选值**：`dialog`（弹窗）、`iframe`、`tab`（标签页）、`jboltlayer`（侧滑层）、留空 = 普通跳转。

### 25. 常用声明式组件速查

**源码参考**：`PRO\src\main\webapp\_view\_admin\dept\_form.html`、`globalconfig\_form.html`、`user\index.html` 等。

```html
<!-- (a) select 远程选项（字典/options 接口），select2 增强 -->
<select class="form-control" name="obj.pid" data-select-type="select2"
        data-url="admin/xxx/options" data-select="#(obj.pid??)" data-autoload></select>

<!-- (b) 日期选择：data-date 即 laydate -->
<input type="text" data-date class="form-control" name="startTime" value="#(startTime??)">

<!-- (c) 行内开关列：data-switchbtn 自动 POST toggle 接口 -->
<img data-switchbtn data-value="${data.enable}" data-confirm="确定切换？"
     data-handler="refreshJBoltTable" data-url="admin/xxx/toggleEnable/${data.id}"/>

<!-- (d) 文件上传框 -->
<div class="j_upload_file_box" data-name="file" data-accept="excel"
     data-maxsize="20480" data-url="admin/xxx/upload"></div>

<!-- (e) 批量操作：checkbox 列 + 取选中 ids -->
<!-- 表格上：data-column-prepend="1:checkbox:true" -->
<button data-ajaxbtn data-check-handler="jboltTableGetCheckedIds"
        data-url="admin/xxx/deleteByIds?ids=" data-confirm="确定删除选中数据？"
        data-handler="refreshJBoltTable" class="btn btn-outline-danger">批量删除</button>

<!-- (f) radio 组远程渲染 -->
<div data-radio data-rule="radio" data-name="obj.type" data-value="#(obj.type??)"
     data-url="admin/dictionary/options?key=xxx" data-value-attr="sn" data-inline="true"></div>

<!-- (g) 图片大图预览 -->
<img data-photobtn src="#realImage(data.avatar??,'assets/img/avatar.jpg')"/>
```

**Enjoy 共享对象**（siargo `configEngines` 已注册，模板中直接用）：`CACHE`、`CacheExtend`、`SessionKey`、`PermissionKey`、`JBoltConfig`（如 `#(JBoltConfig.ASSETS_VERSION)`）、`JBoltUserKit`、`JBoltStringUtil`；共享枚举 `DictionaryTypeMode` 等。

### 26. ajaxportal 局部加载（二轮取证）

**适用场景**：区块懒加载、无刷新局部更新（统计卡片、选项卡内容、消息列表）。

**源码参考**：`PRO\src\main\webapp\assets\js\jbolt-admin.js`（L8370-8518 ajaxPortal 实现）；典型用例 `_view\_admin\msgcenter\layer.html`。

```html
<!-- HTML 片段模式：Controller render 片段模板 -->
<div data-ajaxportal data-url="admin/xxx/portal"></div>
<!-- JSON + 客户端模板模式：data-tpl 指定模板，data-handler 后处理（如更新计数） -->
<div data-ajaxportal data-url="admin/msgcenter/sysnotice/portalDatas" data-type='json'
     data-tpl="tpl_id" data-handler="changeCountHandler"></div>
```

```javascript
AjaxPortalUtil.refresh(portal);        // 刷新指定 portal
portal.ajaxPortal(true);               // 重新加载
portal.ajaxPortal(true, newUrl);       // 切换 URL 加载
```

后端加载失败用 `renderAjaxPortalFail(msg)`（§10）。**siargo 现状**：少量使用，自写局部刷新 AJAX 的场景应改用此机制。

### 27. JBoltLayer 侧滑层完整属性

**适用场景**：侧边抽屉式详情/配置面板（不适合 Dialog 的长内容）。

**源码参考**：`jbolt-admin.js`（L4534-5100 JBoltLayerUtil）；布局 `__admin_jboltlayer_layout.html`（§23）。

```html
<button data-jboltlayertrigger data-url="admin/xxx/detail/123"
        data-dir="right" data-width="600" data-load-type="ajaxportal"
        data-mask-close="true" data-handler="afterOpen" data-close-handler="afterClose">详情</button>
```

| 属性 | 说明 |
|------|------|
| `href` / `data-url` | 内容地址 |
| `data-dir` | `right`（默认）/ `left` 滑出方向 |
| `data-width` / `data-height` / `data-top` | 尺寸定位 |
| `data-load-type` | `ajaxportal`（默认）/ `iframe` |
| `data-mask-close` / `data-nomask` / `data-noclose` | 遮罩点关 / 无遮罩 / 禁关 |
| `data-resize` / `data-confirm` / `data-keep-open` | 可拖宽 / 关闭确认 / 保持打开 |
| `data-check-handler` | 打开前校验函数（返回 false 阻止） |
| `data-handler` / `data-close-handler` / `data-before-close-handler` | 打开后 / 关闭后 / 关闭前回调 |

```javascript
JBoltLayerUtil.openByNav(url, {dir:"right", width:600, loadType:"ajaxportal"});  // JS 打开
JBoltLayerUtil.close();                                                          // JS 关闭
// layer 内表单：<form data-jboltlayersubmit ...> 提交后自动回写 portal
```

### 28. pjax 菜单体系与新页面挂菜单

**源码参考**：`_view\_admin\common\_menu.html`（L3 菜单来源）、`__admin_layout.html`（pjax 容器）、`jbolt-admin.js`（L19-40 pjax 配置）。

- 菜单数据：`JBoltPermissionCache.getCurrentUserMenus()`，存于 `jbolt_permission` 表（permissionKey/url/isMenu/openType），按用户权限过滤
- **新页面挂菜单 = permission 表新增记录**（isMenu=1），无需改代码；后台"权限资源管理"界面维护
- openType：1=系统默认（pjax 加载到 `#jbolt-container`）/ 2=IFrame / 3=Dialog / 4=JBoltLayer
- 页面内刷新当前 pjax 容器：`refreshPjaxContainer()`

### 29. 全局消息提示（LayerMsgBox / JBoltNotifyBox）

**源码参考**：`jbolt-admin.js`（L19900+）。自写提示框/toast 前先查这两个：

```javascript
LayerMsgBox.alert(msg);                        // 普通提示
LayerMsgBox.success(msg, 1000, callback);      // 成功（自动消失+回调）
LayerMsgBox.error(msg);                        // 错误
LayerMsgBox.confirm(msg, yesCallback);         // 确认框
LayerMsgBox.load() / LayerMsgBox.closeLoadingNow();  // 加载中遮罩
JBoltNotifyBox.success({msg:"收到新通知", position:"topRight", width:300});  // 角落通知
JBoltNotifyBox.warning({msg:"..."});
```

### 30. 图片上传与输入选择器（j_img_uploder / JBoltInput）

**源码参考**：`_view\_admin\user\avatar.html`（L14-23）、`user\_form.html`（L135-145）；JBoltInput 在 `jbolt-admin.js` L1387-1408。

```html
<!-- 图片上传（带裁剪）：data-area 裁剪尺寸，data-maxsize 单位 KB -->
<div class="j_img_uploder" data-url="admin/user/uploadMyAvatar"
     data-value="#realImage(user.avatar??)" data-area="200,200"
     data-maxsize="200" data-handler="uploadFile"></div>

<!-- 输入选择器：点击弹出 jstree 选择（部门/分类选择标准写法） -->
<input data-input-select data-load-type="jstree" data-read-url="admin/dept/tree"
       data-checkbox="true" data-onlyleaf="true" name="obj.deptId"/>
```

JBoltInput 的 `data-load-type` 可选：`html` / `jstree` / `ajaxportal`。

### 31. 多页签（JBoltTabUtil）与 textarea 增强

**源码参考**：`jbolt-admin.js`（L5303+ JBoltTabUtil；L111-180 TextareaUtil）。

```javascript
// 主框架标签页：data-openpage="tab" 声明式打开；JS API：
JBoltTabUtil.addJboltTab(key, title, url);        // 新开 pjax 标签
JBoltTabUtil.addJboltTabWithIFrame(key, title, url);
JBoltTabUtil.changeTabTitle(key, newTitle);
JBoltTabUtil.close(key);
```

```html
<!-- 页内选项卡：jbolt_tab_view + data-handler 懒加载（配合 ajaxportal） -->
<div class="jbolt_tab_view" data-handler="changeXxxTabHandler">...</div>

<!-- textarea 增强：自适应高度 + 字数统计（需 maxlength） -->
<textarea data-auto-height data-show-count maxlength="500" name="obj.remark"></textarea>
```

### 32. WebSocket 前端接入与自定义 command 扩展

**源码参考**：`assets\js\jbolt_websocket\jbolt-websocket.js`（连接/心跳）、`jbolt-websocket-command.js`（内置 command）、`jbolt-websocket-handler.js`（分发）；初始化在 `__admin_layout.html` L184-204。

```javascript
JBoltWS.initJBoltWebsocket(ctx, function() {                  // 初始化（布局已内置）
    JBoltWS.sendCommand({command: "msgcenter_check_unread"}); // 连接后发送指令
});
```

- 内置 command：`msgcenter_check_unread` / `new_notice` / `new_todo` / `user_forced_offline` / `user_terminal_offline` / `check_last_pwd_update_time` / `ping` / `pong` / `server_time`
- **自定义 command 扩展点**：`extend/jbolt-websocket-command-extend.js` 或页面 `registeredCommands` 注册，禁止自建 WebSocket 连接
- 后端推送端见 §5（`JBoltWebSocketMsg.createSystemCommandMsg("new_notice", ...)`，command 名与前端 handler 对应）
- 红点控制：`showMsgCenterRedDot()` / `hideMsgCenterRedDot()`

---

## 三、平台体系机制

### 33. 权限体系全链路（PermissionKey / @CheckPermission / #permission）

**适用场景**：新增模块的菜单权限、按钮级权限、角色授权。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\permission\PermissionKey.java`（L1-192，常量类非枚举，由 JBoltGeneratorGUI 生成）、`rolepermission\RolePermissionAdminController.java`、`permission\PermissionAdminController.java`（L1-115）。

```java
// 1. PermissionKey 定义（常量类，siargo 中手动追加常量即可）
public static final String SIARGO = "siargo";

// 2. Controller 级校验（core 的 JBoltAdminAuthInterceptor 自动拦截）
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
public class XxxAdminController extends JBoltBaseController { }
// 单个 action 放行：@UnCheck
```

```html
<!-- 3. 模板按钮级权限：#permission 指令（core 提供），siargo 已在用 #if(permissionKey) 变体 -->
#permission(PermissionKey.SIARGO)
<button ...>受控按钮</button>
#end
```

- 角色授权保存：`RolePermissionService.doSubmit(roleId, permissionStr)`；取角色已有权限 `getListByRole(roleId)`；清空 `deleteByRole(roleId)`
- 权限树数据：`PermissionService.getParentPermissionsWithLevel()` / `getAllPermissionsWithLevel()`；缓存 `JBoltPermissionCache.me.get(id)`
- **siargo 现状**：已按此体系使用，新增权限只需 PermissionKey 加常量 + jb_permission 表配菜单。

### 34. 登录链路与在线用户（AdminIndexController / OnlineUser）

**适用场景**：理解登录态/cookie 机制、强制下线、定制登录校验。

**源码参考**：`PRO\src\main\java\cn\jbolt\index\AdminIndexController.java`（L1-273）、`_admin\onlineuser\OnlineUserAdminController.java`（L1-53）、`JBoltOnlineUserClearTask.java`（L13-20，cron4j 每30秒清理离线用户）。

- login() 流程：验证码校验 → `UserService.getUser()` 查用户 → 启用状态检查 → 角色分配检查 → 登录日志 → afterLogin 设 cookie（`JBOLT_SESSIONID_KEY` / `JBOLT_SESSIONID_REFRESH_TOKEN`(JWT) / `JBOLT_KEEPLOGIN_KEY`）
- 验证码开关：`JBoltGlobalConfigCache.isJBoltLoginUseCapture()`，类型 `JBoltGlobalConfigKey.JBOLT_LOGIN_CAPTURE_TYPE`（后台全局参数可配，勿硬编码）
- 在线用户：`OnlineUserService` 管登录/登出/锁屏状态；强制下线走 OnlineUserAdminController + WebSocket `user_forced_offline` 指令
- **siargo 可复用点**：需要"踢人下线/单点互斥"时直接用 OnlineUser 体系，不自建 session 表。

### 35. JBoltCache 标准缓存类模式（JBoltCacheKit + IDataLoader）

**适用场景**：跨请求共享的低频变更数据缓存（配置、选项、扫描结果）。与 siargo 现有 volatile+DCL 模式互补——**平台级可共享数据优先用本模式**，页面内高频统计仍可用 DCL。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\cache\JBoltCodeGenCache.java`（L1-156）、`JBoltQiniuCache.java`（L118-157）、`common\util\CACHE.java`（L1-56 简化门面）、`extend\cache\CacheExtend.java`（L1-126 多 cacheName）。

```java
public class XxxCache extends JBoltCache {
    public static final XxxCache me = new XxxCache();   // 单例惯例
    @Override public String getCacheTypeName() { return "xxx"; }

    public List<Option> getDatas() {
        // 懒加载：无缓存时执行 IDataLoader.load() 并写入
        return JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME,
                buildCacheKey(Xxx.class, "datas_"), new IDataLoader() {
            @Override public Object load() { return loadFromDb(); }
        });
    }
    public void removeDatas() {  // 数据变更后失效（afterCommit 时机调用）
        JBoltCacheKit.remove(JBoltConfig.JBOLT_CACHE_NAME, buildCacheKey(Xxx.class, "datas_"));
    }
}
```

- 缓存后端由 `jbolt_cache_type` 配置决定（ehcache/caffeine/redis），业务代码不感知
- ⚠ 失效调用必须遵守 siargo afterCommit 纪律：`Db.tx()` 提交成功后才 remove

### 36. 二开扩展点（ExtendProjectConfig）

**适用场景**：新增定时任务、全局拦截器/Handler 白名单、扩展数据源、启动/停止钩子——**不改平台 ProjectConfig**。

**源码参考**：`PRO\src\main\java\cn\jbolt\extend\config\ExtendProjectConfig.java`（L1-197）。扩展位全集：

| 扩展位 | 用途 |
|--------|------|
| `configConstant(Constants)` | 常量配置 |
| `configRoute(Routes)` | 二开路由（内部调 ProjectCodeGenRoutesConfig.config） |
| `configPlugin(Plugins, Cron4jPlugin)` | 追加插件/定时任务（`cron4jPlugin.addTask("0-59/1 * * * *", new XxxTask())`） |
| `configInterceptor(Interceptors)` | 全局拦截器 |
| `configHandler(me, baseHandler, xssHandler)` | `baseHandler.unlimited("/path/")` 静态放行、`xssHandler.unlimited("admin/xxx/save")` XSS 白名单（富文本必用） |
| `configEngine(Engine)` | 模板引擎扩展 |
| `configMainDbPlugin / configExtendDbPlugins` | 主库/扩展库 arp 配置（addMapping 等） |
| `onStart / onStop` | 启动后/停止前钩子 |
| `configSaasTenant*Processor` 系列 | SaaS 租户转换器（siargo 未启用） |

**siargo 现状**：等价扩展位在 siargo 自己的 ProjectConfig 中；新增 cron 任务/XSS 白名单优先找这些扩展位，不散写。

### 37. 敏感词过滤（SensitiveWord + JBoltSensitiveWordUtil）

**适用场景**：用户输入内容合规过滤（评论、备注、名称）。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\sensitiveword\SensitiveWordAdminController.java`（L1-99）：`counts()` 统计 / `reload()` 重载词库 / `test(content)` 检测 / CRUD + `toggleEnable`。

```java
JBoltSensitiveWordUtil.me.findAllWords(content);  // 返回命中的敏感词（core 实现，词库在内存）
```

词库维护走平台后台界面（敏感词词库菜单），**禁止自写过滤词表**。

### 38. 用户个性化配置（UserConfig）与顶部导航（Topnav）

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\userconfig\UserConfigAdminController.java`（L1-63）、`topnav\TopnavAdminController.java`（L1-137）。

- **UserConfig**：每用户偏好项（如登录页背景模糊）。`toggleBooleanConfig()` / `changeStringValue()` 读写库并同步 cookie。siargo 如需"记住用户列表偏好/面板折叠状态"用它，不新建偏好表。
- **Topnav**：TopnavAdminController 是标准 CRUD 完整形态范本——`index/datas/options(@UnCheck)/add/edit/save/update/delete/up/down/initRank/toggleEnable/clearMenus`，新模块 action 命名照此对齐。⚠ 其 `@Before(Tx.class)` 在 siargo 不照搬（手动 Db.tx() + afterCommit）。

### 39. 路由注册与应用启动（AdminRoutes / Starter）

**源码参考**：`PRO\src\main\java\cn\jbolt\index\AdminRoutes.java`（L40-77）、`WechatAdminRoutes.java`（模块级拆分范例）、`starter\Starter.java` + `ProjectServer.java`（Undertow 启动）。

```java
public class XxxRoutes extends Routes {
    @Override public void config() {
        this.setBaseViewPath("/_view/_admin");                    // 模块视图根
        this.addInterceptor(new JBoltAdminAuthInterceptor());     // 后台鉴权拦截器（必加）
        this.add("/admin/xxx", XxxAdminController.class, "/xxx"); // 路径 + 控制器 + 子视图路径
    }
}
```

- 一个业务大模块一个 Routes 类（如 WechatAdminRoutes），在 configRoute 中 add；siargo 用包扫描已覆盖，新包记得补 scan
- Pjax 错误统一入口：`PjaxErrorController.error()` 按 errorCode `renderPjaxFail(msg)`

### 40. JBoltEnum 枚举注册与系统日志类型两层扩展

**适用场景**：新建业务枚举、新模块的系统日志 targetType。

**源码参考**：`common\enums\WechatAutoreplyType.java`（标准模式）、`base\JBoltProSystemLogTargetType.java`（平台层 10001+）、`extend\systemlog\ProjectSystemLogTargetType.java`（项目层）、`_admin\systemlog\ProjectSystemLogProcessor.java`（翻译处理器）。

```java
public enum XxxType {
    AAA("显示名", 1), BBB("显示名2", 2);
    private String text; private int value;      // 固定 text/value 字段名 + getter/setter
    // 必须：注册到 JBolt 枚举管理器，框架才能 getEnumOptionList/字典翻译
    static { JBoltEnum.addToTvBeanMap(XxxType.class); }
}
// 选项输出：renderJsonData(JBoltEnum.getEnumOptionList(XxxType.class));
```

系统日志 targetType 扩展：项目级枚举加常量（value 避开平台 10001+ 段），SystemLogService 覆写 `targetTypeToName/typeToName` 做两层翻译，`processSystemLogUrl` 生成日志关联跳转 URL。

### 41. 富文本编辑器上传端点（Neditor / Summernote 内置）

**适用场景**：页面引入富文本编辑器时，**不自写上传 action**。

**源码参考**：`common\controller\NeditorUploadAdminController.java`（L33-189：wordimg/image/catchImage(远程抓图)/video）、`SummernoteUploadAdminController.java`（L20-52）。平台已注册路由：`admin/neditor/upload`、`admin/neditor/preview`、`admin/summernote/upload`。

```java
// 标准上传处理链（自建上传端点时照此模式）：
UploadFile file = getFile("file", uploadPath);          // try-catch ExceededSizeException
if (notImage(file.getContentType())) { renderJsonFail("请上传图片类型文件"); return; }
Ret ret = jboltFileService.saveImageFile(file, uploadPath);  // 还有 saveVideoFile
renderJsonData(JBoltRealUrlUtil.getImage(ret.get("data")));  // URL 转换（本地/七牛透明）
```

⚠ 富文本提交的 save/update 需加 XSS 白名单：`xssHandler.unlimited("admin/xxx/save", ...)`（§36）。

### 42. extend/gen 生成器工具箱（本地 main 方法直接跑）

**源码参考**：`PRO\src\main\java\cn\jbolt\extend\gen\`：

| 工具 | 用途 |
|------|------|
| `JBoltPermissionKeyGen` | 从 jb_permission 表反向生成 PermissionKey.java（模板 `gentpl/permissionkey.tpl`） |
| `JBoltDictionaryTypeKeyGen` / `JBoltRoleSnGen` | 同理生成字典 key / 角色 SN 常量类 |
| `JBoltMineAssetsCompressor` | 压缩自写 jbolt-mine.js/css → .min（继承 JBoltAbstractAssetsCompressor，`assetsCompressor.js(src,min)` / `.css(src,min)`） |
| `ModelGenerator` / `MainLogicGenerator` | Model / 主逻辑（Controller+Service+页面）生成器入口 |
| `JBoltDatabaseEncryptGen` | 数据库配置加密串生成 |

**siargo 可复用点**：PermissionKey 常量多了可用 Gen 反向同步；js/css 压缩除 npx terser/csso 外还可用 JBoltAbstractAssetsCompressor 本地 main 跑（效果等价，siargo 维持 npx 方案即可）。

### 43. demopage 页面骨架三件套（新页起手模板）

**源码参考**：`PRO\src\main\webapp\_view\_admin\common\demopage\`：

- `_blank.html`（L1-48）：空白页骨架——`jbolt_page > jbolt_page_title（标题+搜索表单+按钮组）+ jbolt_page_content`，`#set(pageId=RandomUtil.random(6))` 惯例
- `_index.html`：JBoltTable 列表页骨架（表格定义 + 数据模板）
- `_master_slave.html`（L1-27）：主从表上下分屏——`jbolttable_master_slave_box > .split.master + .split.slave`，JS 用 `MasterSlaveUtil.initJBoltTable("页面ID")` 初始化（主/子表区域内**不写** data-jbolttable）

新建页面从对应骨架拷起，不从零手写。

### 44. User/Role/Post 管理模块要点（可复用 action 清单）

**源码参考**：`_admin\user\UserAdminController.java`、`role\RoleAdminController.java`（L1-106）、`post\PostAdminController.java`。

- **User 亮点 action**：`autocomplete`（输入联想）、`pwd/editpwd/submitpwd/updatepwd`（改密链路，盐值 `HashKit.generateSaltForSha256()`）、`uploadAvatar/uploadMyAvatar`、`extendForm/extendDetail`（扩展字段子表 UserExtend，主表同 id）、`sysnoticeUsers`（选人弹窗数据）
- **Role**：`users/userDatas`（角色下用户列表）、`clearUsers`（清空角色用户）；角色变更后需清 `JBoltPermissionCache` 权限/菜单缓存
- **UserExtend 模式**：扩展表主键 = 用户 id（共主键一对一），`initSaveOneExtend(userId)` 初始化——业务表需"主表+低频大字段扩展"时照此拆表
- ⚠ 其中 `@Before(Tx.class)` 在 siargo 不照搬

### 45. WebSocket 推送方法族（JBoltWebSocketUtil 全解）

**适用场景**：流程流转实时通知、强制下线提示、在线人数统计。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\websocket\JBoltWebSocketUtil.java`（L25-464）、`JBoltWebSocketMsg.java`、`JBoltWebSocketCommandHandler.java`。

- 客户端维护：`CLIENTS` 为 token→(sessionId→Endpoint) 双层 ConcurrentHashMap，天然支持一用户多端在线；SAAS 模式另有 `TENANT_CLIENTS` 租户隔离层
- **推送维度全集**（均静态方法）：

```java
JBoltWebSocketMsg msg = JBoltWebSocketMsg.createSystemCommandMsg("new_notice", "收到新通知,请及时查阅");
JBoltWebSocketUtil.sendMessageToUser(userId, msg);          // 单用户（内部由 JBoltOnlineUserCache 取该用户全部在线 token）
JBoltWebSocketUtil.sendMessageToUsers(userIds, msg);        // 多用户
JBoltWebSocketUtil.sendMessageToUserByRoles(roleIds, msg);  // 按角色（另有 Depts/Posts 同形方法）
JBoltWebSocketUtil.sendAllMessage(msg);                     // 全员广播
JBoltWebSocketUtil.getTotalClientSessionCount();            // 在线 session 数
```

- 按角色/部门/岗位推送底层走 `onlineUserService.getSessionListByRoleId/DeptId/PostId`——只发给**当前在线**的目标用户，离线用户靠消息中心拉取兼底
- 前端接收：主框架 `JBoltWS` 已全局初始化，页面内 `JBoltWS.sendCommand({command:"msgcenter_check_unread"})` 主动查询；服务端命令处理在 `JBoltWebSocketCommandHandler`（内置 PING/SERVER_TIME/CLIENT_SESSION_COUNT/MSGCENTER_CHECK_UNREAD），项目自定义命令扩展到 `websocket\extend\JBoltWebSocketExtendCommandHandler`
- **siargo 可复用点**：检验流程环节流转通知已用此机制；新增实时推送需求直接调推送方法族，禁自建 WebSocket 端点

### 46. EventKit 事件驱动（业务解耦标准模式）

**适用场景**：保存成功后触发异步通知/后续处理，不阻塞主流程。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\event\JBoltEventListener.java`（L19-113）；发布侧 `SysNoticeService.save()` L104-117、`TodoService.save()` L41-81。

```java
// 发布：Service 写操作成功后（net.dreamlu.event）
boolean success = sysNotice.save();
if (success) { EventKit.post(sysNotice); }   // 直接 post Model 对象

// 监听：按参数类型分发，async=true 异步执行
@EventListener(async = true)
public void sysNotice(SysNotice notice) {
    switch (notice.getReceiverType()) {   // 1全部/2角色/3部门/4岗位/5用户
        case 1: JBoltWebSocketUtil.sendAllMessage(...); break;
        case 2: JBoltWebSocketUtil.sendMessageToUserByRoles(values, ...); break;
        // ...
    }
}
```

- 监听器类在 `ProjectConfig.configPlugin` 注册（EventPlugin 扫包）；一个事件类型可多个监听器
- ⚠ **siargo 纪律**：PRO 在 Service.save 内 post；siargo 因手动 Db.tx() 规范，`EventKit.post` 必须同缓存清理一样放在**事务提交后**（afterCommit），否则异步监听器可能读到未提交数据

### 47. 消息中心全链路（SysNotice 通知 + Todo 待办）

**适用场景**：系统公告、定向通知、个人待办提醒——不自建消息表。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\msgcenter\SysNoticeService.java`（L35-521）、`TodoService.java`（L27-328）、`SysNoticeReaderService.java`；前端 `_view\_admin\msgcenter\layer.html`。

- **SysNotice**：`receiver_type`（1全部/2角色/3部门/4岗位/5用户）+ `receiver_value`（逗号拼 id）；已读用独立 `SysNoticeReader` 表记录（notice_id+user_id），未读查询用 `sql.notInSql("id", readerSubSql)`；阅读计数自增用 `updateSql().set("read_count", new SqlExpress("read_count+1"))`（SQL 表达式自增，非读-改-写）；查看前 `checkUserHasAuth(user, notice)` 按接收类型逐维度校权；假删除 `del_flag` + `deleteNotices(ids, realDelete)` 双模式；接收人回显用 `JBoltRoleCache/JBoltDeptCache/JBoltPostCache/JBoltUserCache` 拼名（processReceiverValues）
- **Todo**：4 种类型（title/content/url 组合），save 时按 type 用 `todo.remove("url","content")` 清理无关字段；严格用户隔离（只能操作自己的，userId 不符直接 fail）；`existNeedProcess(userId)` 用 `selectSql().eq().bracketLeft().eq("state",1).or().eq("state",2).bracketRight()` 拼括号条件
- **右上角 layer.html 前端形态**：jbolt_tab_view 双 Tab + 每 Tab 一个 `data-ajaxportal`（data-tpl 指定 text/template 客户端模板，data-handler 回调刷红点计数）；列表项 `data-openpage="dialog" data-keep-open="true" data-dialog-key="..." data-shade="false"` 打开详情弹窗
- **siargo 可复用点**：审批/检验流转通知直接 `EventKit.post(new SysNotice()...)` 或存 Todo，复用平台消息中心 UI，不自建通知表/铃铛组件

### 48. 字典选项端点族与缓存直读

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\dictionary\DictionaryAdminController.java`（L29-299）。

```java
// 平台已提供 4 个 @UnCheck 选项端点（前端下拉直接用，不自写）：
admin/dictionary/options?key=xxx        // 全部选项（走 JBoltDictionaryCache.me.getListByTypeKey(key,true)）
admin/dictionary/poptions?key=xxx       // 一级选项
admin/dictionary/soptions?key=&pid=     // 按父 ID 取子项（多级联动）
admin/dictionary/soptionsByPsn?key=&psn=// 按父 SN 取子项
// datas 端点带附加数据：renderJBoltTableJsonData(dics, Kv.by("typeLevel", type.getModeLevel()))
```

后端取字典值统一 `JBoltDictionaryCache.me.getListByTypeKey(key, true)`，禁直查 jb_dictionary 表。

### 49. 部门树 JSTree 双模式管理

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\dept\DeptAdminController.java`（L1-196）、`_view\_admin\dept\treemgr.html`。

- 支持表格/JSTree 树形双管理模式（系统配置切换）；树模式支持拖拽 `move` action 调整层级与排序
- `getAllCrudJsTreeDatas(checkedId, openLevel)` 输出 JSTree 格式（选中项+展开层级控制），选择器类需求直接复用；`crudJsTreeDatas/options/enableOptions` 均 `@UnCheck` 供全局组件调用
- siargo 层级数据（如设备分类、文件夹树）照此模式：Service 出 JsTree 数据 + 前端 jstree 组件 + move 拖拽排序

### 50. 平台小模块速览（globalconfig/qiniu/hiprint/custombusiness/monitor）

**源码参考**：`_admin\globalconfig\GlobalConfigAdminController.java`（L1-174）、`qiniu\QiniuAdminController.java`、`hiprint\HiprintTplService.java`（L1-101）、`customtablerender\custombusiness\CustomBusinessService.java`（L1-141）。

- **globalconfig**：内置/自定义参数分层（built_in 字段），`checkAndInit()` 启动自检初始化内置配置（平台惯例：内置数据用 checkandinit 幂等初始化，字典模块同款）；Boolean 配置 `toggleBooleanValue(id)` 快捷切换
- **qiniu**：`@OnlySaasPlatform` 注解（仅 SAAS 平台模式可访问）；`checkDefaultExist` 用基类 `checkColumnTrueExist("is_default", id)` 做"唯一默认项"冲突检测——业务表"设为默认"需求照抄
- **hiprint**：模板 JSON 存 content 字段，列表查询用 `withoutColumns` 排除大字段；`getCacheByKey(sn)` 按 SN 缓存取模板供打印
- **custombusiness**：通用选项源三件套 `datas/options/autocompleteDatas`，基类 `getOptionListEnable()/getAutocompleteList(query,limit,enable,columns)` 直接端出
- **monitor**：ServerMonitorAdminController 单文件接 oshi 系统信息，siargo 无需自建监控页

### 51. ProjectConfig 平台配置全景（新配置该放哪）

**适用场景**：新增插件/拦截器/Handler/模板共享对象时，先知道平台已经配了什么、扩展位在哪。

**源码参考**：`PRO\src\main\java\cn\jbolt\common\config\ProjectConfig.java`（520 行）。

- **configPlugins**：Cron4jPlugin（定时任务统一入口）+ JFinal EventPlugin（异步事件，自带线程池配置）——新任务/新监听器挂这里，禁另起 Timer/自建线程池
- **configInterceptors**：全局拦截器 `SessionInViewInterceptor`（模板可直读 session）+ `JBoltOnlineUserGlobalInterceptor`（在线状态维护）——已全局生效，业务代码不必重复处理
- **configHandlers**：Ureport 权限（UREPORT_DESIGNER/UREPORT_DETAIL 两级 PermissionKey）、Druid 监控权限（用户状态+锁屏+管理员+DRUID_MONITOR 四重校验）、SaaS 租户 SN/ID/Name 互转处理器
- **configEngines**：CACHE、SessionKey、PermissionKey、JBoltUserKit、JBoltStringUtil 等以共享对象注入模板——模板里 `#(PermissionKey.XXX)` 直用的来源
- **分表 Model 注册**：SysNotice/UserExtend/SysNoticeReader/Todo/PrivateMessage 标记为分表 Model（SAAS 场景）
- **siargo 可复用点**：siargo 的等价扩展位是 `ExtendProjectConfig`（手册 §36）；新增 cron/事件监听/共享对象一律进扩展位，不散写

### 52. 对外 API 端点标准形态与 JWT 生命周期

**适用场景**：给小程序/APP/第三方系统提供 HTTP API。

**源码参考**：`PRO\src\main\java\cn\jbolt\api\common\controller\JBoltUserAuthApiController.java`（L21-75）、`JBoltUserInfoApiController.java`、`apitest\ApiTestController.java`（L195-221）、`ApiTestRoutes.java`。

```java
@CrossOrigin                       // 跨域支持
@Path("/api/user/auth")
public class XxxApiController extends JBoltApiBaseController {   // 专用 API 基类（非 JBoltBaseController）
    @JBoltApplyJWT                 // 本 action 会签发 JWT（登录端点）
    @JBoltHttpMethod({HttpMethod.GET,HttpMethod.POST})            // 限定 HTTP 方法（另有 @JBoltHttpGet/@JBoltHttpPost）
    public void index(@Para("userName")String userName, @Para("password")String password) {
        // 校验通过后把用户塞进 ThreadLocal，拦截器据此签发 JWT：
        JBoltApiKit.setApplyJwtUser(new JBoltApiUserBean(appId, user.getId(), user.getUsername(), true));
        renderJBoltApiSuccess();   // 渲染族：renderJBoltApiSuccess/Fail/SuccessWithData/ApiRet
    }
    @JBoltApplyJWT @JBoltHttpGet
    public void refreshJwt(){ renderJBoltApiRet(JBoltApiJwtManger.me().refreshJwt(this)); }  // 刷新 JWT 标准写法
    @OpenAPI                       // 免 JWT 开放端点（如匿名上传）
    public void open(){ ... }
}
// 路由：独立 Routes 类 this.add("/api/test", XxxApiController.class)（setMappingSuperClass(true)）
```

- 权限查询链：`JBoltPermissionCache.me.getRolePermissionKeySet(JBoltUserCache.me.getRoles(userId))` 一行拿到用户全部权限 key
- WebSocket 访客令牌：`/api/ws/guest/token`（gen/check）为未登录用户发临时 ws token，`JBoltWebSocketUtil.getGuestWebsocketToken/checkGuestWebsocketToken`
- **siargo 现状**：已有 `SiargoApiTokenUtil`（MD5 签名）惯例；**新 API 二选一**——沿用 SiargoApiTokenUtil 或按本节 JWT 体系，禁发明第三种令牌方案

### 53. 应用身份体系（appdevcenter：API 调用方管理）

**适用场景**：多个外部调用方（小程序/H5/桌面端/第三方系统）接入 API，需要独立身份与密钥。

**源码参考**：`PRO\src\main\java\cn\jbolt\admin\appdevcenter\ApplicationAdminController.java`（154 行）、`ApplicationService.java`（342 行）、`ApplicationType.java`。

- 每个调用方注册一条 Application：`appId + appSecret`（可 `changeAppSecret` 动态换发）；`ApplicationType` 13 种类型枚举（text/value + static 块 `JBoltEnum.addToTvBeanMap` 注册，§40 同款）
- `toggleNeedCheckSign(id)`：每应用独立控制是否强制接口签名校验
- `linkTarget/removeLinkTarget`：应用绑定业务对象（如某公众号/小程序），删除应用时 `processLinkTarget` 级联清理关联
- 删除前置检查模式：`checkCanDelete() / checkInUse()`——先查引用再删，不留孤儿数据
- **siargo 可复用点**：未来对接 MES/ERP 等外部系统时，调用方身份直接用 Application 表管理，不自建 app 表

### 54. 角色授权链路与 Model 扩展方法模式

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\rolepermission\RolePermissionAdminController.java`（L16-81）；`common\model\Todo.java`（L14-24）、`SysNotice.java`。

- **角色授权 5 action**：`setting/settingTree`（列表/树双模式界面）+ `submit(roleId, permissions)` 批量提交 + `getCheckeds(roleId)` 回显 + `clear(roleId)` 一键清空；可分配范围按 `role.getPid()` 收窄——子角色只能从父角色权限集里选（`getParentPermissionsWithLevel(pid)`），顶级角色才见全量
- **Model 扩展方法模式**：列表翻译字段直接写在业务 Model 上（不在 Service 循环拼名、不在 SQL JOIN 字典表）：

```java
public class Todo extends BaseTodo<Todo> {
    public String getTypeName() { return JBoltDictionaryCache.me.getNameBySn("todo_type", getType()+""); }
    public String getStateName() { return JBoltDictionaryCache.me.getNameBySn("todo_state", getState()+""); }
}
// 模板/JSON 直接用 #(todo.typeName)——Enjoy 按 getter 惯例取值，序列化同样带出
```

- 预编译 Model（jar 内）没有对应 setter 的动态字段用 `set("field", value)/get("field")` 处理
- **siargo 可复用点**：Record 查询场景仍按 6.7 CAST 惯例；走 Model 返回的列表，翻译字段一律 Model 扩展方法 + 缓存直读

### 55. codegen 代码生成器全链路

**适用场景**：新模块开工前了解生成器能产出什么、产物落在哪（规则 6.4 标准流程第 2 步的底层）。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\codegen\CodeGenService.java`（1860 行）、`CodeGenAdminController.java`（996 行）、`modelattr\CodeGenModelAttrService.java`（1544 行）。

- **生成主干**：`genAll(codeGenId, cover)` → `genModel` → `genMainLogic`（依次 genPermissionKeys → genProjectSystemLogTargetType → genService → genController → genCache → genRoutes → genHtml）；`cover=false` 时已存在文件跳过（增量生成）
- **产物落盘**：Model → `{modelPackage}/base/Base*.java + *.java`；Service/Controller/Cache → 对应包；HTML → `webapp{htmlViewPath}/` 下 6 页（index/add/edit/form/import_excel/detail）；路由 scan 自动注入 `ProjectCodeGenRoutesConfig.java`；PermissionKey 常量与系统日志枚举也自动追加
- **字段智能映射**（CodeGenModelAttrService）：按字段名后缀/类型自动推断 UI 组件（`_id`→select/autocomplete、`_time/_date`→laydate、boolean→switchbtn）；四套独立排序（原始/表格/搜索/表单）；表结构变更后可同步字段
- 模板在 `resources/gentpl/`（17 个 .jf）与 `gentpl/codegen/`（8 个 .jf），多 Engine 实例隔离互不污染；controller/service_common_template.jf 是标准 action/方法全集的真正来源（§9/§12-§14 已详述）
- **siargo 可复用点**：新表建好后优先跑生成器拿骨架再改造，手写骨架容易漏掉标准 action/日志/权限链路

### 56. JBoltTable 行右键菜单（data-menu-option）

**适用场景**：列表行需要多个低频操作（状态转换/删除/编辑）又不想操作列堆按钮。

**源码参考**：`PRO\src\main\webapp\_view\_admin\msgcenter\index.html`（L171、L250-298）。

```html
<table data-jbolttable data-menu-option="getTodoTableMenus_#(pageId)" ...>
```
```javascript
function getTodoTableMenus_#(pageId)(){
  return { theme:"list", position:"mouse", width:180, menus:[
    {icon:"fa fa-refresh", text:"刷新", cssClass:"text-info", func:function(table,tr,td,trJsonData){ refreshJBoltTable(table); }},
    {br:true},   // 分隔线
    {icon:"fa fa-exchange", text:"状态转【已完成】", func:function(table,tr,td,trJsonData){ ... },
     visible:function(table,tr,td,trJsonData,menu){ return trJsonData.state!=3; }}  // 按行数据动态显隐
  ]};
}
```

- 回调参数 `trJsonData` 直接拿到行 JSON 数据；复用已有按钮可用 `tr.find("a.jbolt_table_editbtn").click()` 转发
- **siargo 可复用点**：检验单/报告列表的多状态流转操作可收进右键菜单，禁自实现 contextmenu

### 57. 声明式 JSTree 树管理页与权限级联勾选界面

**源码参考**：`PRO\src\main\webapp\_view\_admin\dept\treemgr.html`（L40-66）、`rolepermission\setting.html`（L1-214）/`setting_tree.html`。

```html
<!-- 左树右表单：整页零手写 JS，全声明式 -->
<div id="deptJstree_#(pageId)" data-jstree
     data-read-url="admin/dept/crudJsTreeDatas" data-search-input="搜索框id" data-open-level="2"
     data-curd="true" data-add-url="..." data-edit-url="..." data-delete-url="..."
     data-move-url="admin/dept/move/"                       <!-- 拖拽移动 -->
     data-toggle-enable-menu="true" data-toggle-enable-url="..."
     data-dialog-area="800,850" data-dialog-handler="refreshJstree"
     data-target="portal" data-portalid="deptPortal_#(pageId)" data-change-handler="portalEdit"></div>
<div id="deptPortal_#(pageId)" data-jstree-id="deptJstree_#(pageId)" data-ajaxportal data-url="admin/dept/edit/"></div>
<!-- 工具：JSTreeUtil.openAll/closeAll(treeId)；弹窗按钮 data-jstree-id + data-handler="refreshJstree" -->
```

- **权限级联勾选模式**（setting.html）：每行 `tr data-id/data-level/data-son/data-pid` + `button.checkboxBtn`；勾选时递归 `checkedSons(son,level+1)` + `checkedParent(pid,level-1)`，取消时递归 `removeCheckedSons`；提交时收集 `.checkboxBtn.checked` 的 data-value 逗号拼接 POST
- **父弹窗按钮动态控制 API**（弹窗内页面可用）：`changeParentLayerDialogBtnTitle(0,"保存设置")` 改按钮文案；`addParentLayerDialogBtn("全部清空","lay_danger",fn)` 追加自定义按钮
- **siargo 可复用点**：层级数据管理页（设备分类/文件夹树）直接照搬 treemgr 声明式写法；权限式多选分配界面（如批量授权）照搬级联勾选模式

### 58. 字段命名即 UI 约定（codegen 智能映射规则全集）

**适用场景**：新建表时的字段命名决策——JBolt 平台把命名后缀当作 UI 约定，遵循后无论手写页面还是参考生成器产物，组件选型都有唯一正答案。

**源码参考**：`PRO\src\main\java\cn\jbolt\_admin\codegen\modelattr\CodeGenModelAttrService.java` `convertModelAttr()`（L143-347）、`CodeGenService.java` `processCodeGenPrefixAndPackages()`（L250-315）/`processCacheSettings()`（L213-220）。

| 字段命名/类型 | 平台推断的 UI 组件（表单/搜索/表格） |
|------|------|
| 含 `type`/`state`/`category`（非 `_id` 结尾） | select + 字典数据源（data-value-attr="sn"），自动进搜索条件 |
| `xxx_id` 结尾（非 create/update/delete_user_id） | autocomplete 远程搜索下拉 |
| `_time` 结尾 / Date 类型 | laydate_datetime 表单 + 自动 sortable + `date_ymdhms` 列格式 |
| `_day`/`_date` 结尾 | laydate_date + `date_ymd` 列格式 |
| `_year`/`_month`/`_week` 结尾 | laydate_year / laydate_month / input_week |
| `is_` 开头 / `enable` 结尾 / Boolean | 表单 radio(options_enable) + 搜索 select + 表格 switchbtn 开关列 |
| `name`/`remark`/`brief`/`_info`/`_desc`/`description` 结尾 | 自动纳入 keywords 模糊搜索列；name→input，其余→textarea |
| String 长度 >100 | textarea（否则 input）；Integer → number |
| `id`/`pid`/`sort_rank`/`create_user_id`/`update_user_id`/`delete_user_id`/`is_deleted` | 内置隐藏：不进表格/表单/搜索 |
| 存在 `sort_rank` 列 | 自动启用行拖拽排序 + 初始化排序按钮 + asc 默认排序 |
| 存在 `is_deleted` 列 | 自动出回收站/恢复按钮（假删支持） |
| 存在 `name`/`sn` 列 | Cache 工具类自动开启 getName / getBySn / getNameBySn 方法 |

命名派生链（产物命名基准）：表名去前缀 → camelCase → `ModelName`；`{Model}{Type}Controller`、path=`/type/modelName`、`{Model}Service`、`{Model}Cache`、viewPath=`/_view/{controllerPath}`。

- **siargo 可复用点**：新表字段命名严格贴合上表后缀约定（已与第三章数据库规范一致：`_id`/`_time`/状态 tinyint），手写页面时组件选型直接查此表，不再主观决策；字典型字段（type/state）一律 select+字典 sn，关联字段一律 autocomplete

### 59. hiprint 打印模板体系（设计器/预览/直接打印/导 PDF）

**适用场景**：报告单/标签/证书等需要可视化设计打印模板的需求，平台已内置完整链路，不引新打印方案。

**源码参考**：`PRO\src\main\webapp\_view\_admin\hiprint\index.html`（305 行）。

```javascript
hiprint.init({ providers: [ new JBoltElementTypeProvider() ] });      // 初始化（JBolt 定制元素提供者）
hiprint.PrintElementTypeManager.buildByHtml($('.ep-draggable-item')); // 左侧元素面板可拖拽（a 标签 tid="designerModule.text/image/barcode/qrcode/table..."）
var tpl = new hiprint.PrintTemplate({ template: contentJson, settingContainer: '#属性面板', paginationContainer: '#分页容器' });
tpl.design('#画布');                          // 进入设计模式
tpl.setPaper('A4' /*或 width,height mm*/); tpl.rotatePaper(); tpl.clear();
tpl.getJson();                // 保存：模板 JSON 存库（tpl.content 字段）
tpl.getHtml(printData).html() // 预览 HTML（printData 可选测试数据）
tpl.print(printData);         // 直接打印
tpl.toPdf(printData, name);   // 导出 PDF
```

- 模板持久化：隐藏 form（tpl.sn/name/content/testApiUrl/testJsonData）`ajaxSubmitForm` 提交到 `admin/hiprint/tpl/submit`；`testApiUrl` 记录真实数据接口供测试预览
- **新发现 dialog 属性**：`data-content-func="函数名"`（弹窗内容由函数返回 HTML，适合动态预览）；`data-link-para-ele="#元素id"`（弹窗内选择结果回填到指定元素，配合子页 `data-para-name`）
- 顶层检测：`if(self==top)` 显示"返回主界面"按钮（页面既可嵌弹窗也可独立打开的兼容写法）
- **siargo 可复用点**：若后续有报告/标签打印需求，直接启用平台 hiprint 模块（表+设计器+模板库已全套），禁止另引 jsPDF/浏览器 print CSS 自造方案

### 60. 官方标准 Service 方法族（gentpl 母版，手写代码的基准形态）

**适用场景**：手写任何 Service 时对照官方生成器母版，方法签名、校验顺序、回调钩子保持一致。

**源码参考**：PRO `src/main/resources/gentpl/codegen/service_common_template.jf`（610 行）、`controller_common_template.jf`（559 行）

- `getAdminDatas(pageNumber,pageSize, keywords, sortColumn,sortType, 各条件..., Boolean isDeleted)`：条件构建按类型映射——Boolean(len=1)→`sql.eqBooleanToChar`；laydate_range_*→参数类型 `JBoltDateRange xxxRange` + `sql.betweenDateRange`；laydate_date→`sql.eqDate`；laydate_datetime→`sql.eqDateTime`；模糊→`sql.like/leftLike/rightLike`；默认→`sql.eq`；关键词多列→`sql.likeMulti(keywords, COL1,COL2)`；排序 `sql.orderBy(sortColumn,sortType)`
- `save(model)` 标准顺序：①`isOk(getId())` 拒绝带 ID 的新增 ②唯一性 `existsName()/existsSn()/exists(COL,val)` ③需排序时 `setSortRank(getNextSortRank())` ④save 成功后 `addSaveSystemLog(id, JBoltUserKit.getUserId(), name)`
- `update(model)` 标准顺序：①`notOk(getId())` 校验 ②`findById` 判 `DATA_NOT_EXIST` ③唯一性检查带排除自身 ID 的重载 `existsName(name, id)` ④update 后 `addUpdateSystemLog`
- `up/down(id)`：取 `getSortRank()`，`rank==null||rank<=0`→"顺序需要初始化"；`findFirst(Okv.by("sort_rank", rank±1))` 找相邻行互换 rank 后各自 update；`initSortRank()`：findAll 遍历重排 i+1 后 `batchUpdate`
- `getTreeDatas()`：`convertToModelTree(datas, "id", "pid", (p)->notOk(p.getPid()))`（JBoltBaseService 内置）
- **回调钩子族**（override 基类，返回 `null` 放行、返回 String 即阻止并作为错误提示）：`checkCanDelete(model,kv)`（默认转 `checkInUse`）、`checkCanToggle(model,column,kv)`、`checkCanRecover(model)`、`checkInUse(model,kv)`（检测被其它表引用）；操作后钩子 `afterDelete/afterRecover/afterToggleBoolean`（记系统日志处）
- **siargo 可复用点**：新 Service 的方法名/签名/校验顺序照抄本母版（现有 qarep/equipment 已大体一致）；被引用检查统一写进 `checkInUse` 而非散落各处；⚠️ 母版 Controller 的 `@Before(Tx.class)` 不照搬——siargo 规范为手动 `Db.tx()` + afterCommit 清缓存

### 61. JBoltExcel 导入导出标准模板

**适用场景**：任何 Excel 导入/导出/下载模板需求，统一走 JBoltExcel 链式 API。

**源码参考**：PRO `gentpl/codegen/service_common_template.jf` L299-413（s_exportExcel/s_importExcel/s_getImportExcelTpl）

```java
// 导出：Controller 端 getAdminDatas 查数据 → renderBytesToExcelXlsxFile(service.exportExcel(datas)...)
public JBoltExcel exportExcel(List<M> datas) {
    return JBoltExcel.create().setSheets(
        JBoltExcelSheet.create()
            .setHeaders(1, JBoltExcelHeader.create("col_name","列标题",15), ...) // 列映射+宽度
            .setModelDatas(2, datas)); // 数据从第2行开始
}
// 导入：文件从 JBoltUploadFolder.todayFolder(IMPORT_EXCEL_TEMP_FOLDER) 接收，notExcel() 校验后缀
public Ret importExcel(File file) {
    StringBuilder errorMsg = new StringBuilder();
    JBoltExcel jBoltExcel = JBoltExcel.from(file).setSheets(
        JBoltExcelSheet.create().setHeaders(1, JBoltExcelHeader.create("col_name","列标题"), ...)
            .setDataStartRow(2));
    List<M> models = JBoltExcelUtil.readModels(jBoltExcel, 1, M.class, errorMsg); // 自动翻译+收集错误
    if(notOk(models)) { return fail(errorMsg.length()>0 ? errorMsg.toString() : JBoltMsg.DATA_IMPORT_FAIL_EMPTY); }
    boolean success = tx(() -> { batchSave(models); return true; }); // 批量入库带事务
    return success ? SUCCESS : fail(JBoltMsg.DATA_IMPORT_FAIL);
}
// 下载导入模板：setHeaders(1, false, JBoltExcelHeader.create("列标题",15)...) —— 第二参 false=不处理列名别名，只输出标题行
```

- **siargo 可复用点**：导入导出需求全走此三件套，禁止手写 POI；错误信息通过 errorMsg StringBuilder 聚合返回给前端

### 62. 表单/搜索元素 UI 类型标准 HTML 全集（官方生成器母版）

**适用场景**：手写 `_form.html` 与 index 搜索条件时，各组件 data 属性写法以生成器母版为准。

**源码参考**：PRO `webapp/_view/_admin/_jbolt_code_gen/config/_form_ele_define_function.html`（541 行）、`_condition.html`（199 行）

| 组件 | 关键 data 属性（母版标准） |
|------|--------------------------|
| select 数据源三态 | 字典→`data-url="admin/dictionary/options?key=SN"`；枚举→`data-options="#enumToOptions(枚举类)"`；静态→`data-options="a:1,b:2"`；均配 `data-autoload data-text="=请选择=" data-value="" data-refresh="true" data-select="回显值"` |
| select2/多选 | `data-select-type="select2"` + `multiple`；select2 不写 name，配 hidden input + `data-sync-ele="#xxx_hidden"` / hidden 上 `data-sync-attr="value"` |
| autocomplete | 双 input：显示框 `data-autocomplete data-url data-value-attr/data-text-attr/data-column-attr data-hidden-input="xxx_hidden"` + hidden 存真实值（校验规则加在 hidden 上） |
| jboltinput | `data-jboltinput data-load-type="ajaxportal|jstree|table" data-url data-hidden-input data-height="300"`；jstree 增 `data-jstree-checkbox="true" data-onlyleaf="true"`；可配 `data-filter-handler` 过滤函数 |
| laydate | `data-date data-type="date|time|datetime|month|year" data-fmt="yyyy-MM-dd..." readonly data-with-clearbtn="true"`；搜索区间加 `data-range="~"`（后端参数即 JBoltDateRange） |
| radio/checkbox | 容器式：div 上 `data-radio|data-checkbox data-name="model.attr" data-url|data-options data-label data-width="col-sm-2,col" data-inline="true" data-default="options_first"`；checkbox 另需 hidden input + `data-hidden-input` |
| 数字校验 | `data-rule="number|pnumber|pznumber|int|pint|pzint"`，小数位限制拼 `;fix<=N;`（p=正数，z=含零） |
| 上传 | `data-imguploader|data-fileuploader data-url="控制器/上传action" data-maxsize="KB" data-hidden-input data-remove-confirm="true"`（图片另有 `data-area="宽,高"`）；七牛云换 `data-handler="uploadFileToQiniu" data-file-key data-bucket-sn` |
| 富文本 | `data-editor="summernote" data-height="350" data-hidden-input`（简化工具栏加 `data-toolbar="simple"`），值存 hidden textarea |
| 校验通用 | 必填三件套 `data-rule data-notnull="true" data-tips="提示语"`；提示 `<small class="form_tips d-block text-secondary mt-1">` |

- 搜索条件 UI 类型集合（searchUiType）：input/number、input_date 等 html5 五种、laydate 全家（含 `laydate_range_date/datetime/datetime_hm/time/month/year` 区间）、select/select2（含 multi）、autocomplete——与表单同名组件写法一致
- **siargo 可复用点**：手写表单组件不再自创属性组合，先查此表；区间搜索优先 laydate_range_* + 后端 JBoltDateRange + `sql.betweenDateRange` 一条链

### 63. Model/BaseModel/Cache 类与回收站页面标准形态

**适用场景**：新建模块时 Model 三件套与回收站 UI 的官方基准。

**源码参考**：PRO `gentpl/model_template.jf`、`base_model_template.jf`、`codegen/cache_template.jf`、`config/_table_portal.html` L373-397

- BaseModel（生成器产出，勿手改）：字段常量全大写 `public static final String NAME="name"`；每个 getter 带 `@JBoltField(name,columnName,type,remark,required,maxLength,fixed,order)` 元数据；**Long 字段 getter 自动加 `@JSONField(serializeUsing=ToStringSerializer.class)`**；链式 Setter `return (M)this`
- Model 字段翻译 getter 8 类（`get翻译属性名()` 直接给模板/JSON 用）：`sys_dic_id`→`JBoltDictionaryCache.me.getName(id)`；`sys_dic_sn`→`getNameBySn(key,sn)`（多选值用 `getNameStrBySns`）；`sys_user_id_to_username/name`→`JBoltUserCache.me.getUserName/getName(id)`；`cache`→自定义 Cache 方法；`enum`→`JBoltEnum.getTextByValue(枚举.class,val)`；`static_method/service_method`→指定方法；`kv_data`→类内 static Kv 常量查表
- Cache 类标准形态：`public static final XxxCache me = new XxxCache()` 单例 + `Aop.get(XxxService.class)` + `getCacheTypeName()` 返回表名；`get(id)`→`service.findById`；`getBySn(sn)`→`service.getCacheByKey(sn)`（依赖 Model 上 `@JBoltAutoCache(keyCache=true, column="sn")`）；`getName(id)/getNameBySn(sn)` 判空取 name
- 回收站页面惯例（有 is_deleted 列时自动生成）：form 内 `<input type="hidden" name="isDeleted" value="false">`；查询/回收站双按钮调 `formSearch(formId,isDeleted)`——切换 hidden 值、互换按钮实心/描边态、显隐 toolbar 的 recover/delete/add/edit 按钮后 `form.submit()`；行内 `{@if data.isDeleted}` 分支：eye 查看（dialog 只读）+ `realDelete` 真删 + `recover` 恢复，正常态为 edit + `delete` 假删
- **siargo 可复用点**：软删模块 UI 直接套此回收站三态（当前 siargo 用 status/vd 字段，语义一致可映射）；需要 `getBySn` 的字典型表在 Model 上加 `@JBoltAutoCache(keyCache=true,column="sn")` 即免手写缓存逻辑

### 64. 基础版（platform_3）与 PRO 版差异总结

**适用场景**：查阅框架内部实现、判断某机制是否 PRO 独有时的导航。

**源码参考**：BASE `E:\Workspace\源码\jfinalxueyuan-jbolt_platform-jbolt_platform_3-\...`（v3.4.0，JFinal 5.0） vs PRO（v5.2.0，JFinal 5.2.4）

- **BASE 本质是 PRO 的透明化基础版，无独有机制**；两版 API 约定一致
- **BASE 独有价值**：① `src/main/java/cn/jbolt/base/` 35 个框架源码文件（即 PRO jbolt_core.jar 的源码等价物，查 JBoltBaseService/JBoltBaseController/JBoltAutoCache 内部实现看这里）；② `_view/_admin/demo/` 78 个组件演示页（PRO 已删除，组件 data 属性最全案例库）；③ `readme/changelog.txt` 55KB 组件演进史
- **PRO 独有模块**：codegen 代码生成器、cache 缓存管理、redis 管理、sensitiveword 敏感词、customtablerender、devdoc；依赖增 aliyun-oss/toolgood-words/TTL
- **配置差异**：BASE 缓存默认 ehcache，PRO 默认 caffeine（siargo 同 PRO）；PRO 多出 jbolt_code_gen_enable/sensitive_word_check_enable/jbolt_proxy_type/resolve_json_request_enable 等 40+ 配置项
- **siargo 可复用点**：需理解框架内部行为（如 Sql 构建、AutoCache 失效时机）时读 BASE 的 base/ 源码而非反编译 jar；组件属性拿不准时查 BASE demo 目录对应页面

### 65. DialogBtn 进阶属性与 Dialog 选择数据回传模式（R9）

**适用场景**：弹窗按钮的参数动态化、打开前校验、弹窗内选择数据回填父页。

**源码参考**：BASE `_view/_admin/demo/dialogbtn.html`

- 弹窗形态：`data-btn="no|close"`（无按钮/仅关闭）、`data-scroll="yes"`、`data-fs="true"` 最大化、`data-shadeclose="true"` 点遮罩关闭、`data-shade="false"` 无遮罩、`data-btn-align="left|center|right"` 按钮对齐、`data-area="600,400"` 或 `"80%,80%"`
- `data-check-handler="fn"`：打开前调用——返 `true` 放行、`false` 阻止、**返字符串则拼进 url 作参数**
- `data-link-para-ele="#eleId"`：点击时取关联元素当前值作参数（参数名默认取其 name，可 `data-para-name` 自定义）；JS 手动设参：`btn.data("paras",{id:v})`
- **Dialog 选择数据标准模式**：子页定义 `submitThisForm()`（Dialog 确定按钮自动调用），父页定义 `setChooseDialogSelectResult(data)` 接收回填——弹窗选人/选数据一律走这对约定函数，不自造 window.opener 通信
- **siargo 可复用点**：报告/设备模块「从列表选关联数据」弹窗全部套 submitThisForm/setChooseDialogSelectResult 约定

### 66. AutoSelect 全形态与多级联动（R9）

**适用场景**：下拉选择数据源自动加载、select2/bootstrap-select 增强、N 级联动。

**源码参考**：BASE `_view/_admin/demo/autoselect.html`（L458-488 联动全链）

- 形态切换：`data-select-type="select2|bootstrap"`（bootstrap-select 配 `data-liveSearch/data-actionsBox/multiple`）
- 赋值扩展：`data-setvalueto="eleId"` 选中 value 写入指定元素、`data-settextto="eleId"` 写 text；多选 + `data-setvalueto` 写 hidden
- 取值定制：`data-text-attr="name,username,sex"` 多字段拼接 + `data-delimiter="-"` 分隔符 + `data-value-attr="id"`；`data-onlyleaf="true"` 多级数据只许选叶子；`data-refresh="true"` 出刷新钮
- **多级联动全链**：父 `data-linkage="true" + data-sonid="l2Select"`（**多值 "son1,son2" 一托 N**），子 `data-linkage="true"`（去掉 data-autoload）+ `data-origin-url="...&pid="` 尾拼父值
- 回调：`data-handler="fn"` 切换回调；`data-handler="refreshPortal" + data-portal="portalId"` 直接联动刷新 ajaxportal 区块
- **siargo 可复用点**：客户→联系人、设备类别→设备等级联下拉禁手写 change 事件，全用 data-linkage 链

### 67. FormCheck data-rule 校验规则全集（R9）

**适用场景**：任何表单校验——先查此表有无内置规则，禁自写正则。

**源码参考**：BASE `_view/_admin/demo/formcheck.html`；扩展自定义规则写 `jbolt-mine.js` 的 `initMineRuleMap`

- 数字：`pint`（正整数）/`pzint`（≥0 整数）/`int`/`pnumber`/`number`/`money`（2 位小数）/`money_4`
- 长度：`len[X-Y]`、`len<=3;>=18;<=60` 分号组合边界
- 格式：`email/phone/tel/url/url_nohttp/ip/idcardno/zh_cn`（中文）/`letter`（仅字母）/`letter_num`（字母数字下划线）
- 时间：`date/time/time_hm/datetime/datetime_hm`（_hm 不含秒）；跨字段：`data-rule="required;>=#startTime"`
- 组件规则：`select/radio/checkbox`（配 data-radio/data-checkbox 容器）
- 修饰：`data-tips="提示语"`；`data-notnull="false"`（非必填但填了要合规）；`data-ajax-check-url="/x"`（失焦 ajax 后台校验，参数名 data，后端返 success/fail）
- **siargo 可复用点**：所有 _form.html 校验先套内置规则；唯一性校验用 data-ajax-check-url 替代提交后报错

### 68. Checkbox/Radio 容器组件与 CheckboxUtil（R9）

**适用场景**：一组 checkbox/radio 的自动加载、自动选中、自动校验、全选反选。

**源码参考**：BASE `_view/_admin/demo/checkbox.html`、`radio.html`

- 容器声明（父 div 上）：`data-checkbox`/`data-radio` + `data-rule="checkbox|radio"` + `data-name="字段名"` + `data-value="#(回显值)"` + `data-default="1,3"` 默认选中
- 数据源自动加载：`data-url="/xx/dictionary?key=filetype"` + `data-label="类型："` + `data-width="col-2,col-10"`（label/内容列宽）+ `data-inline="true"` 横排 + `data-align-left="true"` 等宽左对齐
- `data-hidden-input="eleId"`：选中值逗号串自动同步到隐藏域（注意 checkbox 容器用带横线的 `data-hidden-input`）；`data-disabled` 查看态禁改
- JS API：`CheckboxUtil.checkAll('name')`/`uncheckAll('name')`/`convertCheckAll('name')`（参数是 data-name 值）
- **siargo 可复用点**：多选标签/类型勾选场景用容器组件 + data-url 字典源，免手写渲染与取值 JS

### 69. 图片/文件上传组件全属性（R9）

**适用场景**：单图上传预览、单文件上传（本地或七牛）。

**源码参考**：BASE `_view/_admin/demo/imguploader.html`、`fileuploader.html`

- 图片 `class="j_img_uploder"`：`data-handler="uploadFile"`（本地）或 `uploadFileToQiniu` + `data-bucket-sn` + `data-file-key="user/avatar/[date]/[randomId]/[filename]"`；`data-url` 上传接口、`data-value="#realImage(x??)"` 回显、`data-hiddeninput` 结果写入、`data-area="400,400"` 裁剪区、`data-maxsize`（KB）、`data-upload-success-handler="fn(type,fileInput,res)"`
- 文件 `class="j_upload_file_box"`：`data-name="file"`、`data-btn-class="btn btn-primary"` 按钮样式、`data-placeholder`、`data-accept="pdf|excel|mp4"` 限类型、`data-hiddeninput`（文件地址）+ `data-filenameinput`（文件名）+ `data-sizeinput`（大小）三隐藏域、`data-upload-success-callback="(js表达式)"` 全部完成后回调
- **siargo 可复用点**：证书/附件上传优先声明式组件，三隐藏域模式免手写赋值 JS；后端仍守 §6.5 三层路径校验

### 70. JStree 树组件声明式接入（R9）

**适用场景**：树形展示、树选择（单选/checkbox 多选）、异步大树。

**源码参考**：BASE `_view/_admin/demo/jstree/index.html`

- 声明：`data-jstree + data-read-url="树JSON接口"`（配 §15 convertJsTree 输出）
- `data-open-level="-1|0|1|2"`（-1 全展开/0 不展开/N 展开到 N 级）；`data-async="true"` 节点按需异步加载
- `data-checkbox="true"` 多选；`data-select`/`data-default-select="1,3,7"` 选中回显；`data-sync-ele="#idInput,#textInput"`（目标带 `data-sync-attr="id|text"`）选中自动同步
- `data-change-handler="fn(tree,data)"`；多选取值：`tree.jstree(true).get_all_checked(full,withoutRoot)`——full=true 返整条 json，withoutRoot=true 剔除根
- **siargo 可复用点**：部门树/分类树选择场景声明式接入；页面多实例时 id 带 `#(pageId)` 随机后缀防冲突（demo 惯例 `#set(pageId=RandomUtil.random(6))`）

### 71. NEditor/Summernote 富文本标准接入与多实例（R9）

**适用场景**：富文本编辑（含 Dialog/JBoltLayer 内、一页多实例）。

**源码参考**：BASE `_view/_admin/demo/neditor.html`、`neditor_multi.html`、`summernote.html`、`summernote_multi.html`

- NEditor：`<script type="text/plain" data-editor="neditor" data-height="300" data-urlprefix="#(imghost??)" data-hiddeninput="textareaId" id="boxId">初始内容</script>`；Summernote：`<div data-editor="summernote" data-height data-imghost data-hiddeninput>内容</div>`，可 `data-imguploadurl` 自定义图片上传地址、`data-maxsize` 限图片 KB
- 内容自动同步到 `data-hiddeninput` 指向的 form 内 textarea（textarea 带 data-rule="required" 即完成校验），编辑器本体放 form 外
- 多实例：每实例 id/hiddeninput 用 `#setLocal(formIdN=RandomUtil.random(6))` 随机后缀区分，互不影响；上传由内置 NEditorUploadAdminController/SummernoteUploadAdminController 自动处理
- Autocomplete 选数据填编辑器：`data-sync-editor-id="boxId" + data-sync-editor-type="neditor|summernote" + data-sync-attr="phone"`
- 表单提交三式：`ajaxSubmitForm(formId)`（首选）/`pageFormSubmit(formId)`/`submitFormInCurrentTab(formId)`（多选项卡局部提交）
- **siargo 可复用点**：报告备注/整改说明等富文本一律 hiddeninput 同步模式，禁手动 getContent 赋值

### 72. laydate 与 Autocomplete 进阶属性（R9）

**适用场景**：时间选择细粒度控制、自动补全多列/多隐藏域/联动。

**源码参考**：BASE `_view/_admin/demo/laydate.html`、`autocomplete.html`

- laydate：`data-type="year|month|date|datetime|time"` + `data-fmt` 格式；`data-minutes="00,15,30,45"` 限定可选分钟；`data-done-handler/data-change-handler(ele,value)` 确认/切换回调；`data-range="至"` 自定义区间分隔文本；`data-min/data-max` 边界；`data-small="true"` 紧凑版
- Autocomplete 列显示：`data-header="姓名,拼音-200-left,性别"`（`名称-宽度-对齐` 格式）+ `data-column-attr` 同格式多列 + `data-width="550"` 下拉宽度、`data-limit` 返回条数
- 多隐藏域：`data-hiddeninput="userId3,pinyin,phone"`——各隐藏域自带 `data-value-attr="pinyin"` 声明取哪个属性；`data-sync-ele="#a,#b"`（目标带 data-sync-attr）同步非隐藏元素
- 联动：`data-link-para-ele="#ele"` 关联元素值变则组件查询 URL 附加该参数（autocomplete 间联动）；`data-mustmatch="false"` 可当普通输入框（默认强制匹配列表值）
- **siargo 可复用点**：选人带出工号/电话用多隐藏域 + data-value-attr，一次选择多字段落库

### 73. JBoltTable 声明式配置与查询绑定全集（R10）

**适用场景**：任何列表页表格声明；复杂多条件查询表单与表格绑定。

**源码参考**：BASE `_view/_admin/demo/jbolttable/index_search.html`、`demo/demotable/filterbox.html`

- 声明属性全集：`data-jbolttable data-ajax="true" data-url data-rowtpl data-page data-pagesize data-column-resize data-tfoot-fixed data-height="fill_box|数值" data-fixed-columns-left="1,2" data-fixed-columns-right="7,8" data-column-prepend="1:checkbox|radio" data-toolbar data-headbox data-footbox data-conditions-form data-bind-elements`
- 排序三件套：`data-sortable-columns="a,b,c"` + `data-sort="#((sortColumn&&sortType)?(sortColumn+':'+sortType):'')"` + `data-default-sort-column`；后端 `setDefaultSortInfo("create_time","desc")` + `getSortColumn()/getSortType()`
- 行模板 juicer：`<script type="text/template">` 内 `{@each datas as data,index}`，序号 `${pageNumber,pageSize,index | rownum}`，格式化 `${data.price,2|number_format}`、`${data.time|date_ymdhm}`，条件 `{@if}...{@/if}`；tr 加 `data-id="${data.id}"` 供选中取值
- 条件绑定：`data-conditions-form` 指向查询 form（`onsubmit="return false;"`），form 内 input/select 的 name 即查询参数，点 submit 自动带参重载；`data-headbox` 折叠筛选区 + `jboltTableToggleHeadbox(this)` 切换
- **陷阱**：不存在 setConditions / jbolttable-bindOk API，条件加载唯一入口是全局 `jboltTableReadByConditions(tableId, conditions)`；控制自动加载的属性名是 `data-autoload`（非 data-auto-load）
- extraData：后端 `renderJBoltTableJsonData(pageData, Kv.by("selectId",id).set("warningMsg",...))` 随表格数据带回额外信息
- **siargo 可复用点**：qarep/equipment 列表页排序、筛选折叠区全部可用声明式属性完成，不写 JS

### 74. 可编辑表格双提交模式与 editableOption 全集（R10）

**适用场景**：单据明细行编辑（销售开单式）、表内直接改值。

**源码参考**：BASE `demo/demotable/editable_single.html`、`editable_multi.html`、`editable_triggersummary.html`

- 声明：`data-editable="true" + data-editable-option="fn名"`（整包配置函数）或拆开 `data-editable-trigger/data-editable-submit/data-editable-cols`；`data-copy-from-excel="true"` 支持从 Excel 粘贴
- option 结构：`{trigger:'click', initRowCount, maxRowCount, submit:{type:'all'|'cell', url, withForm:[formId...], params, commonAttr:{save:{...}}, success:fn}, insertDefaultValues:{...}, cols:{列名:{...}}}`
- **type:'all' 整体提交**：withForm 合并主表单+尾部表单一次 POST；后端 `@Before(Tx.class)` + `service.submitByJBoltTable(getJBoltTable())`（多表格用 `getJBoltTables()`+`submitByJBoltTables`）——**siargo 改用手动 Db.tx() + afterCommit**
- **type:'cell' 单元格提交**：url 指向单字段更新接口，`success(res,table,td)` 里用 `jboltTableUpdateOtherColumns(table,td.parent(),res.data,[{column:'update_time',handler:date_ymdhm}])` 回写关联列
- cols 编辑器 type 全集：`input/input_number/date/month(pattern)/datetime/age/price/amount/switchbtn/checkbox/select(url+valueAttr)/autocomplete(columnAttr+dialog)/jboltinput(loadType:'jstree'|portal, filterHandler, changeColumns:[{column,use:'id'}])`；通用项 `submitAttr`（蛇形列名→驼峰提交名映射）、required、rule、min/max/maxLength、placeholder、tooltip、`editable:false`（只读列）、`handler(table,td,trJsonData,checkbox,isChecked)` 值变回调、`linkPara/linkColumn` 联动参数
- 三格式化钩子：`textFormat/htmlFormat/editorFormat(table,td,text,value,trJsonData)`——显示文本、显示 HTML、编辑器内值各自定制
- summary 统计：`{dir:'v'|'h', tofixed, roundtag:'round', removezero, formula:'sum'|'avg'|'price*amount'表达式, syncval:'#eleId'}`，可数组同列多向统计；`triggerSummaryColumns:["total"]` 让 checkbox/switchbtn 列变化触发重算，公式可含布尔 `(enable?1:0)`
- 行操作函数族（toolbar onclick）：`jboltTableInsertEmptyRow/PrependEmptyRow/AppendEmptyRow/InsertEmptyRowBeforeChecked/AfterChecked/RemoveRow/RemoveCheckedRow(this,confirm,cb)/CopyCheckedRowPrepend|InsertBefore|InsertAfter|Append/jboltTableTrMoveUp|Down/jboltTableSubmit(this)/jboltTableRefresh(this,'确认文案')/jboltTableMaximize(this)`；`jboltTableChooseAndInsert(this)` + data-url/data-area/data-btn 弹窗批量选择插入
- **siargo 可复用点**：报告产品明细行、检定数据录入可整体套 editable type:'all' + withForm 模式，合计列用 summary formula 免手写计算

### 75. 主从表 [masterId] 占位符模式（R10）

**适用场景**：上下分栏主从联动（主表选行→子表/子区块刷新）。

**源码参考**：BASE `demo/masterslave/jbolttable.html`、`jbolttable_editable.html`、`jbolttable_editable2.html`

- 布局：`<div class="jbolttable_master_slave_box" data-sizes="5:5">` 内 `.split.master` + `.split.slave`；JS 初始化 `MasterSlaveUtil.initJBoltTable("页面容器ID")`
- 主表 tr：`onclick="masterTableTrTriggerShowSlave(this,'${data.id}',{extra:1},slaveCallback,slaveAjaxPortalCallback)"`（后三参可省）
- 子表/子按钮：声明 `data-origin-url="...路径/[masterId]"` + `data-url="...路径/0"`（初始占位），选主表行后 [masterId] 自动替换重载；ajaxportal 子区块、downloadbtn 同理
- 子表按钮加 `data-check-master="true"`：未选主表行时点击自动拦截提示
- slaveCallback(table,masterTable,masterData,extraParams)：可动态改 `table.editableOptions.insertDefaultValues={fk_id:masterData.id}`、`submit.params`——新增子行自动带主表外键
- 多子类型：slave 区放 `jbolt_tab_view` 多页签，每签一个子表或 ajaxportal；后端主/子接口各自 `renderJsonData(paginate)`，无数据时 `renderJsonData(service.emptyPage(getPageSize()))`
- **siargo 可复用点**：设备→证书/比对记录、报告→产品明细的主从页面直接套此布局，外键注入用 slaveCallback 的 insertDefaultValues

### 76. 右键菜单 menu 与 filterbox 列筛选组件（R10）

**适用场景**：表格右键菜单（刷新/筛选/排序/列显隐）、Excel 式列头筛选。

**源码参考**：BASE `demo/demotable/filterbox.html`、`jsonoption.html`；后端 `JBoltTableDemoController.ajaxDatas(JBoltTableMenuFilter)`

- 声明：`data-menu-option="fn名"`，函数返回 `{theme:'list'|'button', width, position:'mouse'|'td', menus:[...]}`
- 菜单项：`{icon,text,cssClass,func(table,tr,td,trJsonData,inThead,inTbody), visible(table,tr,td,trJsonData,menu,inThead,inTbody)}`；`{br:true}` 换行、`{custom:true,tpl:'模板id',data:{...}}` 自定义项
- 内置能力函数：`jboltTableGetCellSelectText(td)` 取单元格文本、`jboltTableMenuFilterByKeywords(table,text,isInclude)` 含/排筛选、`jboltTableSubmitConditionsForm(table)` 清筛选重查、`table.me.sortByColIndex(table,td.data("col-index"),'asc')/cancelSort(table)`、`jboltTableShowColumnConfigDialog(table)` 列显隐配置
- filterbox 接入：menu 项 `{custom:true,tpl:"filterbox_tpl",data:{columns:[{column,text,type,comparison}]}}`（type: 1文本like/2数字/3日期/4布尔/5时间戳/7布尔型；comparison: eq/ne/gt/ge/lt/le/like/notlike）+ `{custom:true,tpl:"filterbox_page_tpl"}` 分页项
- 后端接法：action 参数注入 `ajaxDatas(JBoltTableMenuFilter filter)`——filter 为 null 走普通 paginate，非 null 走 `service.paginateByJboltTableMenuFilter(filter, new String[]{"keywords列"})`
- **siargo 可复用点**：列表页「按此值筛选/排除」右键菜单零成本接入，无需自建筛选 UI

### 77. jsonoption 纯 JSON 配置与 checkedChangeHandler（R10）

**适用场景**：不写 thead/rowtpl 的极简表格声明；行选中联动业务。

**源码参考**：BASE `demo/demotable/jsonoption.html`、`jsonoption2.html`、`demo/jbolttable/checked_change_handler.html`

- 纯 JSON 模式：`<table data-jbolttable data-option="fn名"></table>`（空 table），函数返回 `{width:'fill', form, url, page:true, columnResize, primaryKey:'id', rowtpl(可选), cols:[{column,title,width,sort:true|{isDefault:true,type:'desc'},fixed:'left'|'right',valueTpl}], menu:{...}}`
- 智能自组装：cols 不配 rowtpl 时按 valueTpl 逐列拼行 HTML（`${data.field}` 语法），操作列/开关列 valueTpl 内联写 a/img 组件即可
- 选中回调：`data-checked-change-handler="fn"`，签名 `fn(isAll, table, tr, td, ele, trJsonData)`——isAll 全选(radio 时表示全取消)；trJsonData 全选/全取消时为空；配合 `getJboltTableCheckedIds(table)` 取选中 ID 集
- 取选中：`jboltTableGetCheckedId/jboltTableGetCheckedIds`（dialogbtn/ajaxbtn 的 data-check-handler 标配）
- **siargo 可复用点**：弹窗选择列表等轻量表格用 data-option 纯 JSON 声明，页面零模板更简洁

### 78. JSON 全局序列化定制（R11）

**适用场景**：理解 Long/BigDecimal 为何前端不丢精度、Record 为何要手动 CAST。

**源码参考**：BASE `src\main\java\cn\jbolt\base\json\JBoltFastJson.java`

- 静态块全局注册 `Long.class→ToStringSerializer`、`BigDecimal.class→ToStringSerializer`——**凡走 FastJson 的 Model 序列化，Long/BigDecimal 自动转 String**，这是 Model 出 JSON 不丢精度的根源
- Record 走单独注册的 FastJsonRecordSerializer，**不享受** Long→String 待遇——siargo「Record 查询必须 `CAST(id AS CHAR)`」规则的框架级原因
- `ParserConfig.setSafeMode(true)` 全局禁 autoType（反序列化安全）；WriteDateUseDateFormat 统一日期格式；DisableCircularReferenceDetect 关循环引用检测（互相引用的对象勿直接序列化）
- 可选 `processNullValueDefaultFeatures()`：null→false/[]/0/"" 默认值输出
- **siargo 可复用点**：新 Model 无需再手动加 `@JSONField(serializeUsing=ToStringSerializer.class)` 也能防精度丢失（但项目惯例仍显式标注更醒目）；Record 查询坚持 CAST 纪律

### 79. BaseModel 自动化生命周期（R11）

**适用场景**：理解 save/update 时哪些字段不用手动 set、约定列名的框架级收益。

**源码参考**：BASE `src\main\java\cn\jbolt\base\JBoltBaseModel.java`（1183 行）

- **beforeSave 自动链**（save() 内置调用）：autoProcessCreateTime→UpdateTime→CreateUserId→UpdateUserId→autoProcessIdValue（雪花/UUID/SEQUENCE 自动生成主键）——条件都是 `hasColumn(x) && get(x)==null`，**表里有约定列名即自动填**，业务代码零负担
- 约定列名：`create_time/update_time/delete_time/create_user_id/update_user_id/delete_user_id`；update() 自动刷 update_time+update_user_id（update_time 无条件覆盖）
- 批量 save 用 `beforeSaveInBatchSave(date)` 统一时间戳；`checkNeedBeforeSaveInBatchSave()` 判断是否需要
- **getBoolean(attr) 覆写**：1/0/"true"/"false" 智能转 Boolean，`@UnProcessBoolean` 注解可豁免字段；**set(attr,value) 覆写**：列不存在抛 ActiveRecordException（防拼写错）、Boolean 自动转 1/0
- `putItems(list)/getItems()` 挂子表数据（put 非 set，不入库）；`putEachLevel/processEachLevelByParentLevel` 树层级标记
- `superFindById(id)` 按 idGenMode 自动转 id 类型（String 雪花 id 传入也能查）
- **siargo 可复用点**：新表坚持约定列名可免写全部审计字段代码；siargo 的 creator_id/creator_time 命名**不在**自动链内（框架只认 create_user_id/create_time），新表建议直接用框架约定名

### 80. @JBoltAutoCache 模型级自动缓存（R11）

**适用场景**：单条数据高频 findById/findByKey 的透明缓存。

**源码参考**：BASE `JBoltAutoCache.java`（注解）+ `JBoltBaseModel.java` 缓存段 + `cache\JBoltCacheKit.java` + `cache\caffeine\CaffeineCacheKit.java`

- Model 类上加 `@JBoltAutoCache`：默认开 idCache（键 `prefix+类名小写_id`）；`keyCache=true, column="sn"` 开按列缓存（键 `prefix+类名_列名_值`），`bindColumn` 复合键（如 UserConfig 的 configKey+userId）
- findById/findByIds 自动走 `loadCacheById`（JBoltCacheKit.get + IDataLoader 懒加载）；User 类型自动 remove password/pwd_salt 再入缓存
- **写操作自动失效**：save→deleteExtraCache("save")；update→deleteIdCache+（key 列变更时）deleteKeyCache；delete→双清；`clearCache()` 手动全清；**deleteExtraCache(action) 是可覆写扩展点**（关联缓存联动清理）
- update 前 beforeUpdate 会 findById 对比 keyCache 列是否变更——变更则先删旧 key 缓存
- `superFindById` = 绕缓存直查库（缓存可能脏时用）
- JBoltCacheKit 按 `MainConfig.JBOLT_CACHE_TYPE` 分发 EHCACHE/CAFFEINE/REDIS；CaffeineCacheKit：ConcurrentHashMap 管多实例 + DCL 建默认实例（无 TTL，默认配置）
- **siargo 可复用点**：字典/配置类小表 Model 加 `@JBoltAutoCache` 即免手写缓存类；但 Caffeine 默认实例**无过期策略**，大表勿用；siargo 手写 TTL 缓存模式仍适用于聚合统计场景

### 81. BaseService 方法族全景（R11）

**适用场景**：写 Service 前先查基类有没有现成方法，禁止重复造轮子。

**源码参考**：BASE `src\main\java\cn\jbolt\base\JBoltBaseService.java`（3064 行）

- **查询族**：`getOptionList()`（默认 NAME/ID，表有 sort_rank 自动按其排序）/`getCommonList(columns,paras,orderColumns...)`/`findById`（notOk 返 null）/`getOneColumnValueById`/`getRandomOne`（按 dbType 选 RAND()/RANDOM()/NEWID()）/`getAutocompleteList`（keywords 空返空 list）
- **存在性族**：`existsName(name[,excludeId][,pid])`/`existsSn`/`exists(column,value,excludeId)`/`exists(Sql)`/`checkColumnTrueExist`——唯一性校验禁手写 COUNT
- **分页族**：`paginate(Sql)` 自动 clone 出 count SQL；totalRow=0 返 `emptyPage()`；`paginateByKeywords` 多列 like or 包裹；`paginateRecord(Sql, columnToCamelCase)` 列名转驼峰；`paginateByJboltTableMenuFilter(filter, matchColumns)` 列筛选
- **写操作族**：`deleteById(id)` **表有 is_deleted 列自动假删**（set TRUE + 自动填 delete_user_id/delete_time）否则真删；`toggleBoolean(kv,id,...)` 取反布尔列；`updateColumn(id,column,value)` 单列更新/`updateOneColumn(m)`（恰好主键+1 字段）；`batchSave/batchUpdate` **batchSize 上限 500**；`deleteByPid` 递归删子树
- **钩子族**（覆写，返 null 放行/返 String 阻止）：`checkInUse/checkCanDelete/checkCanToggle/afterDelete/afterRealDelete/afterToggleBoolean`
- **树族**：`convertToModelTree(list,idCol,pidCol,isParentFunc)`；`convertJsTree(...)`（openLevel=-1 全展开；禁用节点自动红字"[禁用]"）
- **Sql 起手式**：`selectSql()`=Sql.me(dbType()).select().from(table())；`updateSql()/deleteSql()` 同理
- ⚠ **recoverById 疑似框架 bug**：追回时 set is_deleted 仍为 TRUE（源码 L1229/1233），**siargo 勿依赖，自行实现 restore**（qarep 先例）
- **siargo 可复用点**：exists 族替代手写唯一性 SQL；toggleBoolean+钩子替代自写 toggle 端点；convertToModelTree 替代手写树构建；batchSave 替代循环 save

### 82. 请求生命周期与用户上下文（R11）

**适用场景**：理解 JBoltUserKit.getUserId() 从哪来、请求类型如何判定。

**源码参考**：BASE `JBoltBaseHandler.java`（279 行）+ `JBoltUserKit.java`（174 行）

- 每请求流程：processScheme（HTTPS 判定入 ThreadLocal）→ *.html 直访 404（unlimited 白名单除外）→ **processJBoltCurrentUserInfo**：cookie sessionId→CACHE 取 OnlineUser→`JBoltUserKit.setUserId`（ThreadLocal）；sessionId 失效时走 refreshToken JWT 自动重登（仅超管）→ processJBoltRequest 判请求类型 → finally **JBoltUserKit.clear()**（防线程池串号）
- 请求类型判定优先级：header `X-JBOLTAPI`→JBOLTAPI；`X-PJAX`→PJAX；`X-AJAXPORTAL`→AJAXPORTAL；`X-Requested-With: XMLHttpRequest`→AJAX；URL 参数 `_jb_rqtype_=dialog|iframe`；URL 后缀 `-_jb_rqtype_dialog`；默认 NORMAL——renderFail 系列按此自动选择返回形态
- JBoltUserKit 全 ThreadLocal：`getUserId()/getUser()`（经 CACHE）/`getUserName()/isSystemAdmin()/isEnable()/getOnlineUser()/userScreenIsLocked()`
- **siargo 注意**：异步线程/定时任务里 `JBoltUserKit.getUserId()` 为 **null**（ThreadLocal 不跨线程）——通知/任务场景需显式传 userId，勿在异步代码里调 UserKit

### 83. 参数校验与注入工具（R11）

**适用场景**：Controller/Service 参数判空、getModel 注入行为、confirm 二次确认返回。

**源码参考**：BASE `para\JBoltParaValidator.java` + `JBoltInjector.java` + `JBoltBaseController.java`

- **isOk/notOk 语义**（基类静态导入，全项目通用）：数值类 `>0` 才算 ok（0/负数视为无效！）；String=notBlank；集合/Map/数组=非空；Model/Record=非 null 且有属性；`hasNotOk(a,b,c)` 任一无效即 true（批量校验）
- ⚠ 数值 isOk 是 `>0`：合法值可为 0 的参数（如状态位）**勿用 isOk 校验**，用 `notNull`
- **JBoltInjector.injectModel**：参数名先按原样匹配列，不匹配且无下划线时**自动驼峰转下划线**再匹配（前端 name="userName" 可注入 user_name 列）；列不存在默认抛异常（skipConvertError=false）——表单 name 拼错会 500 而非静默丢失
- **BaseController 补充方法族**：`renderJsonConfirm(msg, optUrl, reqType)` 返回确认弹窗（前端点确认后自动请求 optUrl，GET/POST/DOWNLOAD 三态）——删除前二次确认的原生方案；`renderFail/renderDialogFail/renderFormFail/renderPageFail/renderPjaxFail` 按请求形态分流；`getParaToArray/getParaToIntArray/getParaToLongArray(paraName[,split])` 逗号串转数组（批量 id 参数标配）
- **siargo 可复用点**：批量操作端点用 `getParaToLongArray("ids")` 替代手动 split；高危操作用 renderJsonConfirm 做二次确认链

### 84. ControllerKit 渲染方法族与 JSON 结构（R12）

**适用场景**：理解 renderJson* 系列的真实返回结构、renderFail 分流机制。

**源码参考**：BASE `src\main\java\cn\jbolt\base\JBoltControllerKit.java`（1226 行）

- **JSON 返回结构规范**（JFinal Ret）：成功 `{state:"ok"[,msg][,data]}`，失败 `{state:"fail",msg[,data]}`；`renderJsonData(data)`=Ret.ok("data",data)；data 用 `setIfNotNull` 挂载（不传则无 data 字段）
- **renderJBoltTableJsonData(data, extraData)**：extraData 空时直接返 data；非空时包一层 `{tableData, extraData}`——前端 JBoltTable 自动识别双结构
- **renderJsonConfirm 返回结构**：`{needConfirm, msg, optUrl, reqType[, data]}`，msg/optUrl 缺失抛 RuntimeException；reqType 三态 GET/POST/DOWNLOAD
- **renderFail 分流**（L507-542）：按 `_jb_rqtype_` attr 分派——JBOLTAPI→JBoltApiRet；AJAX→普通 JSON；PJAX/DIALOG/IFRAME/NORMAL→对应错误页面片段；业务代码只需调 renderFail，形态自适应
- 跨域：processCrossOrign 设置 CORS 头并暴露 `jboltjwt` 响应头（API 客户端从 response header 读 JWT）
- **siargo 可复用点**：前端判断用 `ret.state==='ok'`；需附加统计数的列表端点用 renderJBoltTableJsonData(page, Kv.by(...)) 而非自拼结构

### 85. CommonController 便捷方法族（R12）

**适用场景**：Controller 取分页/排序/关键词/模型注入参数时先查基类。

**源码参考**：BASE `src\main\java\cn\jbolt\base\JBoltCommonController.java`（1082 行，JBoltBaseController 父类）

- **分页**：`getPageNumber()`（默认 1）/`getPageSize()`（默认 PageSize.PAGESIZE_ADMIN_LIST 全局配置）/`getPageSize(int)` 自定义默认
- **排序**：`getSortColumn(default)/getSortType(default)` 取参带默认；`setDefaultSortInfo(col,type)` 设默认并回写 view attr（index 页表头箭头状态依赖此）
- **参数便捷**：`getKeywords()/getState()/getEnable()/getStartTime()/getEndTime()/getDateRange()`；`getJSONObject()/getJSONArray()` 取 JSON body；`getCheckBoxValues/getCheckBoxBoolean` 复选框
- **模型注入**：`getModel(Class)/getBean(Class)` 走 JBoltInjector（支持 modelName 命名空间 + skipConvertError）
- **字节流渲染**：PDF/TXT/Image/Excel/Word 直出与下载封装；`renderJBoltCaptcha` 验证码
- `isOk/notOk` 实为基类实例方法委托 JBoltParaValidator（§83 语义同）
- **siargo 可复用点**：datas 端点固定起手式 `service.paginate(getPageNumber(), getPageSize(), getKeywords(), getSortColumn("xx"), getSortType("desc"), ...)`；日期范围用 getDateRange 不手拆参数

### 86. JBoltTable 提交数据结构解析（R12）

**适用场景**：后端接收表格整表提交（type:'table'）数据的标准解法。

**源码参考**：BASE `src\main\java\cn\jbolt\base\JBoltTable.java`（404 行）

- 参数名固定 `jboltTable`，五段结构：`delete`（Object[] id 数组）/`update`（JSONArray 改动行）/`save`（JSONArray 新增行）/`form`（JSONObject 主表单）/`params`（JSONObject 附加参数）
- 取数据：`getSaveModelList(M.class)/getUpdateModelList(M.class)`（toJavaList 强类型）；`getSaveRecordList()/getUpdateRecordList()`（动态字段场景）；`getDelete()` 直取 id 数组
- 主表单：`getFormModel(M.class[, modelName])`（带 modelName 走 JBoltInjector 命名空间注入）/`getFormRecord()`；附加参数 `getParamsModel/getParamToInt/getParamToLong`
- 判空族：`isBlank()`（五段全空）/`saveIsNotBlank()/updateIsNotBlank()/deleteIsNotBlank()` 分段判——Service 内标准写法：先 isBlank 拒绝，再分三段各自处理
- **siargo 可复用点**：主子表一次提交场景（主表单 form + 子表 save/update/delete）用 JBoltTable 五段结构替代自拼 JSON；事务内分段顺序：form→save→update→delete

### 87. JBoltEnum 枚举体系与 API JWT 认证（R12）

**适用场景**：枚举下拉数据源、对外 API 的认证链理解。

**源码参考**：BASE `enumutil\JBoltEnum.java`（381 行）+ `api\JBoltApiInterceptor.java`（299 行）+ `api\JBoltApiJwtManger.java`（306 行）

- **枚举约定**：枚举类含 text+value 两属性（可自定义属性名）；五层缓存 Map 懒加载注册（首次访问自动 addToXxxMap）
- 查询 API：`getTextByValue(E.class, v)`（列表页状态翻译）/`getEnumObjectByValue`/`getEnumOptionList(E.class)`（下拉数据源）/`getEnumOptionListWithout(E.class, 排除值...)`；⚠ `getIntValueByText` 不存在返 **-1000** 非 null
- `addEnjoyEngineShareObject(engine, E.class)` 注册后模板内直接用枚举名取选项（启动配置处注册，参考 ProjectConfig）
- **API 认证链**（拦截器）：控制器须继承 JBoltApiBaseController → CORS 预检 → `@JBoltHttpGet/Post` 注解校验请求方法 → header/URL 取 `jboltappid` 验应用存在+enable → 按 `@OpenAPI/@JBoltApplyJWT/@JBoltReApplyJWT` 决定 JWT 解析/签发 → JBoltApiKit ThreadLocal 存 application/jwtParseRet
- **JWT 细节**：HS256，密钥=应用 appSecret（跨应用不可伪造）；默认 2 小时过期，过期标记 expired=true 而非解析失败；needCheckSign=true 时验 `jboltsignature=SHA1(sorted(token,timestamp,nonce))` 三头
- **siargo 对比**：siargo API 用 SiargoApiTokenUtil MD5 签名（更简）；如未来需多应用/过期控制，可参考平台 appId+appSecret+JWT 三层方案（手册 §应用身份体系节）
- **siargo 可复用点**：状态类字段新增时定义 text/value 枚举 + getEnumOptionList 出下拉，替代前端硬编码选项

### 88. 统一附件库 JBoltFile + 上传目录体系（R13）

**适用场景**：新增文件上传需求、编辑器图片上传、附件元数据管理。
**源码参考**：`BASE\...\common\config\JBoltUploadFolder.java`、`BASE\...\_admin\jboltfile\JBoltFileService.java`

- `JBoltUploadFolder` 定义各类上传路径常量；`todayFolder(path)` 自动追加 yyyyMMdd 日期子目录（按天分目录防单目录文件过多）
- `JBoltFileService` 统一入库 `jb_jbolt_file`：`saveImageFile/saveExcelFile/saveAudioFile/saveVideoFile/saveAttachmentFile(UploadFile, uploadPath)` 返回含 localUrl 的 Ret；底层 `saveJBoltFile(...)` 返回 JboltFile 对象
- 文件类型常量：FILE_TYPE_IMAGE=1/VEDIO=2/AUDIO=3/OFFICE=4/ATTACHMENT=5；真实类型用 hutool `FileTypeUtil.getType()` 按内容识别而非后缀
- `setLocalUrl` 自动把反斜杠替换为正斜杠（Windows 兼容）；⚠ file_size 为 int 最大约 2GB
- 基础版无图片缩放/水印/CDN 能力，无未引用文件自动清理机制
- **siargo 可复用点**：新上传目录在扩展常量类中集中定义 + todayFolder 分目录；siargo 自建文件表（dms/certificate）属合理定制，新简单附件需求可直接走 JBoltFileService 统一附件库；上传仍须按规范 6.5 三层路径校验

### 89. WebSocket 推送组件（R13）

**适用场景**：实时通知、流程环节流转提醒、在线消息推送。
**源码参考**：`BASE\...\_admin\websocket\JBoltWebSocketUtil.java`、`JBoltWebSocketMsg.java`、`extend\JBoltWebSocketExtendCommandHandler.java`

- 端点 `@ServerEndpoint("/websocket.ws/{token}")` 以登录 token 认证；客户端映射存 ConcurrentHashMap
- 推送方法族（JBoltWebSocketUtil 静态方法）：`sendMessageToUser(userId,msg)` / `sendMessageToUsers(ids,msg)` / `sendMessageToUserByRole(s)/ByDept(s)/ByPost(s)` / `sendAllMessage`；内部通过 OnlineUserService 查用户在线 session 列表
- 消息体 `JBoltWebSocketMsg`：from/to/type/command/data；工厂方法 `createSystemCommandMsg(command,data).to(userId)` 链式构造；类型 TYPE_COMMAND/COMMAND_RET/TEXT/FILE
- 自定义指令扩展点：`extend/JBoltWebSocketExtendCommandHandler.process()`，禁止自建 WebSocket 端点
- ⚠ 目标用户不在线消息直接丢弃不落库——离线消息须配合消息中心/待办存储；端点本身无权限注解，业务校验在指令处理内做
- **siargo 可复用点**：流程通知类需求直接调 JBoltWebSocketUtil 方法族；推送必须在事务提交后（afterCommit）执行，与现有 notifyNextStageUsers 时序纪律一致

### 90. 定时任务 Cron4j 注册机制（R13）

**适用场景**：新增周期性后台任务（清理、同步、提醒）。
**源码参考**：`BASE\...\common\config\MainConfig.java` configCron4jPlugin、`BASE\...\_admin\onlineuser\JBoltOnlineUserClearTask.java`

- 任务类实现 cron4j `ITask` 接口（run()/stop()），Service 用 `Aop.get()` 获取；注册在 configCron4jPlugin（或 ExtendProjectConfig 扩展点）`cron4jPlugin.addTask(表达式, task)`
- cron4j 表达式为 5 段（分 时 日 月 周），如 `0-59/1 * * * *` 每分钟——⚠ 非 Quartz 6/7 段格式
- ⚠ run() 内异常框架不捕获，必须自行 try-catch 否则任务可能停止；cron4j 同步执行，长耗时任务会阻塞调度器；执行超过间隔可能重叠，需幂等/加锁
- **siargo 可复用点**：新定时需求实现 ITask + configCron4jPlugin 注册，禁止另起 Timer/ScheduledExecutor；任务内 JBoltUserKit 为 null（ThreadLocal 不跨线程，§82），操作人相关逻辑需显式传参

### 91. 数据字典 Dictionary 双表体系（R13）

**适用场景**：可配置下拉选项、二级联动选项、系统级可维护枚举。
**源码参考**：`BASE\...\_admin\dictionary\DictionaryService.java`、`DictionaryAdminController.java`

- 双表：`jb_dictionary_type`（typeKey 唯一 + modeLevel 一级/多级）+ `jb_dictionary`（type_key/pid/sn/name/sort_rank）；均 @JBoltAutoCache keyCache
- 前端下拉直接调 @UnCheck 端点：`/admin/dictionary/options?key=xxx`（全部）/ `poptions`（一级）/ `soptions?pid=`（子项）/ `soptionsByPsn?psn=`（按父 SN 联动）
- 后端取字典：`CACHE.me.getDictionaryListByTypeKey(key, true)` 走缓存；Service 层 `getListByTypeKey/getSonOptionListByTypeKey(pid)`
- 支持上移/下移排序（doUp/down + sort_rank）；`checkAndInit()` 从 JSON 配置自动补全缺失字典（适合部署初始化）
- **siargo 可复用点**：可维护的选项类数据（如设备类别、部门自定义标签）优先用字典而非建新表；固定枚举用 JBoltEnum（§87），可维护选项用 Dictionary，两者分工明确

### 92. 系统日志 SystemLog 存储结构（R13）

**适用场景**：理解 addSaveSystemLog 族底层、新业务对象接入操作日志。
**源码参考**：`BASE\...\common\model\SystemLog.java`、`BASE\...\_admin\systemlog\SystemLogService.java`

- 单表 `jb_system_log`（雪花 ID，不分表）：字段 user_id/user_name/target_type/target_id/type/title/url/open_type/create_time
- 操作类型 TYPE_SAVE=1/UPDATE=2/DELETE=3/LINK_DELETE=4/LINK_UPDATE=5/LINK_SAVE=6；非 DELETE 操作按 target_type 自动生成详情链接 URL（saveLog 内硬编码映射，新对象需补充）
- **无拦截器自动记录**——必须在 Service 写操作中显式调 `addSaveSystemLog/addUpdateSystemLog/addDeleteSystemLog`（JBoltBaseService 提供）
- ⚠ 高频操作日志膨胀无自动归档；查询仅支持 title 模糊 + 时间范围
- **siargo 可复用点**：继续沿用 addXxxSystemLog 模式（已是规范 2.2）；日志调用应在事务内随业务写操作一起提交

### 93. 菜单权限存储与 RBAC 链（R13）

**适用场景**：新增菜单/按钮权限、理解权限校验链路。
**源码参考**：`BASE\...\_admin\permission\PermissionService.java`、`BASE\...\_admin\rolepermission\RolePermissionService.java`

- 三表：`jb_permission`（资源树，permission_level 1/2/3 自动由父节点+1 计算，permission_key 唯一）+ `jb_role_permission`（角色绑定）+ `jb_topnav_menu`（顶部导航关联）
- 校验链：`@CheckPermission(PermissionKey.X)` → RolePermissionService.`checkUserHasPermission(userId, checkAll, keys...)`（超管走 is_system_admin_default 优先）；取角色权限集 `getPermissionsKeySetByRoles(roleIds)`
- 权限变更后自动清 `CACHE.me.removeMenusAndPermissionsByRoleGroups()`——角色组维度的菜单+权限缓存
- 角色权限分配 `RolePermissionService.doSubmit(roleId, permissionStr)` 先删后插 + 自动清缓存
- **siargo 可复用点**：新增权限 = PermissionKey 加常量 + jb_permission 配菜单行，无需动校验代码；按钮级用前端 `#permission(key)` 指令；禁止绕过 RolePermissionService 自查 jb_role_permission 表

### 94. JBoltExcel 导入导出链式 API（R14）

**适用场景**：Excel 导出列表、Excel 批量导入、导入模板下载。
**源码参考**：`BASE\...\common\poi\excel\JBoltExcel.java`、`JBoltExcelUtil.java`；siargo 可用包：`cn.jbolt.core.poi.excel.*`（CodeGenAdminController 已在用）

- **导出**：`JBoltExcel.create().setFileName("xx").addSheet(JBoltExcelSheet.create().setName("s").setHeaders(1, JBoltExcelHeader.create("字段","显示名",宽度)...).setModelDatas(2, list))` → Controller `renderBytesToExcelXlsxFile(excel)`
- **导入**：`JBoltExcel.from(file).setSheets(sheet.setHeaders(1,...).setDataStartRow(2))` → `JBoltExcelUtil.readModels(excel, 0, X.class, errorMsg)` 返回 Model 列表；返 null 时查 errorMsg.length() 区分"无数据"vs"校验失败"；动态列用 `readRecords`
- **大文件**：`setBig(true)` 启用 SXSSF 流式写防 OOM（强制 xlsx）；模板导出 `useTpl(路径)` 加载 exceltpl 目录模板
- 导入临时文件用 `JBoltUploadFolder.todayFolder(...)` 管理；文件类型先 `notExcel(file)` 拦截
- **siargo 可复用点**：新 Excel 需求优先 JBoltExcel 链式 API 而非手写 POI；Service 返 JBoltExcel 对象/导入返 Ret，Controller 只负责 render；导入批量保存套 Db.tx + batchSave（≤500/批，§81）

### 95. 消息中心 SysNotice/Todo 发送链（R14）

**适用场景**：审批流转通知、站内公告、待办下发（siargo QareportService 已在用 Todo+EventKit）。
**源码参考**：`PRO\...\_admin\msgcenter\SysNoticeService.java`、`TodoService.java`、`_admin\event\JBoltEventListener.java`（siargo 项目内同名文件）

- 三表：`jb_sys_notice`（receiver_type 1全部/2角色/3部门/4岗位/5用户 + receiver_value 逗号分隔 ID）+ `jb_sys_notice_reader`（已读记录）+ `jb_todo`（user_id 严格隔离，state 1待处理/2进行中/3完成，type 1-4 按 url/content 有无组合）
- 发送链：构造 SysNotice/Todo → service.save() 成功后 `EventKit.post(model)` → JBoltEventListener `@EventListener(async=true)` 按 receiverType 调 JBoltWebSocketUtil 推送 → 前端红点
- 已读机制：未读查询用 notInSql 子查询 reader 表；阅读计数用 SqlExpress `read_count+1` SQL 自增（禁先查后改，并发丢更新）
- ⚠ Todo.save 会按 type 自动 `remove("url","content")` 清理无关字段；receiverType≠1 时 receiverValue 必填否则推送跳过；角色/岗位匹配用 findInSet 非 contains
- **siargo 可复用点**：流转通知直接复用项目内 SysNoticeService/TodoService，不自建通知表；`EventKit.post` 必须在事务提交后（afterCommit）执行，否则异步监听器读到未提交数据

### 96. 全局配置 GlobalConfig 体系（R14）

**适用场景**：新增可运行时调整的系统参数（开关、阈值、文案）。
**源码参考**：`BASE\...\_admin\globalconfig\GlobalConfigService.java`、`common\directive\GlobalConfigDirective.java`；siargo 可用：`cn.jbolt.core.cache.JBoltGlobalConfigCache`

- 双表：`jb_global_config`（config_key 唯一/config_value/value_type string·int·decimal·boolean/built_in 内置标记）+ `jb_global_config_type`（分类）；Caffeine 缓存，支持按 type 清理
- 后端读：`JBoltGlobalConfigCache.getConfigValue(key)` 及类型化方法；前端模板：`#globalConfig(key)` / `#globalConfig(key, 默认值)` 指令
- 写：自定义配置 `saveCustomConfig/updateCustomConfig/deleteCustomConfig`（built_in=false）；布尔快捷切换 `toggleBooleanValue(id)`；改后 clearCache() 刷新
- `checkAndInit()` 幂等初始化内置配置；特定内置 key 变更会 refreshMainConfig 重新应用（如 assets_version）
- **siargo 可复用点**：新可配置参数存 jb_global_config（built_in=false）而非自建配置表；业务代码用 JBoltGlobalConfigCache 读避免硬编码；前端用 #globalConfig 指令注入

### 97. 雪花 ID 生成器 JBoltSnowflakeKit（R14）

**适用场景**：业务代码手动生成雪花 ID（非 Model 主键自动生成场景）。
**源码参考**：`BASE\...\base\JBoltSnowflakeKit.java`；siargo 可用：`cn.jbolt.core.kit.JBoltSnowflakeKit`

- 单例 `JBoltSnowflakeKit.me.nextId()`（long）/ `nextIdStr()`（String）；底层 hutool `IdUtil.getSnowflake(workerId, datacenterId)`
- workerId/datacenterId 来自配置文件；⚠ 集群部署各节点 workerId 必须不同否则 ID 重复；hutool 默认不处理时钟回拨
- **siargo 可复用点**：Model 主键由 idGenMode=snowflake 自动生成无需手动；需要预先拿 ID（如主子表同事务关联）时用 nextId()；JSON 出前端仍须 ToStringSerializer/CAST AS CHAR（§78）

### 98. 前端 Ajax 对象族与 needConfirm 处理链（R15）

- **适用场景**：页面 JS 发起后端请求（替代裸写 `$.ajax`/`$.post`）
- **源码参考**：`PRO\...\webapp\assets\js\jbolt-admin.js` L11509-11718（Ajax 对象）、L1966-2028（processAjaxResultNeedConfirmOr）；siargo 侧 `assets/js/jbolt-admin.js`（20348 行）同名函数均存在
- `Ajax.post(url, data, success, error, sync, timeout, dateType, cancelDefaultErrorMsgProcessor)` / `Ajax.get(url, success, ...)` / `Ajax.getWithForm(formEle, url, ...)`：url 自动过 actionUrl()；data 为 JSON 字符串时 contentType=application/json，否则表单编码；默认异步、超时 60s
- 上传变体：`Ajax.uploadFormData(url, formData, ...)` / `uploadBase64File(url, base64, fileName, ...)` / `uploadBlob(url, blob, fileName, ...)`
- 全局请求头：`Ajax.addCommonHeaders(key, value)` / `setCommonHeaders(headers)`（无单次请求级自定义头）
- 内部统一以 `state=="ok"` 判成功；msg 特殊值 system_locked/nologin/terminal_offline 触发锁屏/跳登录/下线处理；网络异常统一提示"网络通讯异常"
- **needConfirm 二次确认链**：后端 `renderJsonConfirm()` 返回 `{needConfirm:true, msg, optUrl, reqType}` → 前端 `processAjaxResultNeedConfirmOr(ele, ret, orDoThing)` 三条件（needConfirm&&optUrl&&reqType）齐才弹确认框 → 确认后按 reqType 分流 GET/POST/DOWNLOAD；DOWNLOAD 会临时改元素 data-url/href 并清 form/usecheckedids 参数，执行 DownloadUtil.downloadByEle 后恢复原值；success 回调签名为 `success(data, confirmRes)` 双参
- **siargo 可复用点**：新页面 JS 请求一律走 Ajax 对象，天然获得登录失效/锁屏/needConfirm 处理，禁止裸 `$.ajax`；需要"操作前后端动态决定是否二次确认"的场景直接用 renderJsonConfirm + 前端零代码

### 99. 前端消息提示双系统 LayerMsgBox / JBoltNotifyBox（R15）

- **适用场景**：操作反馈提示、确认框、输入框、加载中、右上角通知
- **源码参考**：`jbolt-admin.js` L11366-11503（LayerMsgBox，基于 layer）、L20296-20323（JBoltNotifyBox，基于 Lobibox）
- `LayerMsgBox.alert(msg, icon, handler)`：icon 1=成功/2=错误/3=确认；`confirm(msg|{msg,title}, handler, cancelHandler)`；`success(msg, time=1000, handler)`；`error(msg, time=1500)`；`prompt(title, defaultMsg, handler, type)` type 0 单行/1 多行/2 密码
- `loading(msg, time=10000)`：time 传 null 表示不自动关，须手动 `closeLoading()`（延迟 500ms 关）或 `closeLoadingNow()`（立即）；`closeAll(type)` 关全部
- `JBoltNotifyBox.success/error/warning/info(options)`：右上角通知，options 默认 width 300 / size mini / position "top right" / delay false（不自动消失需配置）
- 分工惯例：模态反馈（确认/成功/失败/loading）用 LayerMsgBox；非阻塞的角标通知（如 WebSocket 推送到达）用 JBoltNotifyBox
- **siargo 可复用点**：禁止自拼提示 DOM 或引入第三方 toast 库；WebSocket 消息到达提醒用 JBoltNotifyBox 与平台消息中心风格一致

### 100. FormChecker 表单验证 + ajaxSubmitForm 提交链（R15）

- **适用场景**：add/edit 表单的校验与 AJAX 提交
- **源码参考**：`jbolt-admin.js` L14713+（FormChecker）、L17298-17331（ajaxSubmitForm）；siargo 侧分别在 L14713/L16503 附近
- 声明式校验属性：`data-rule`（多规则分号分隔；`function:xxx` 经 eval 调用自定义函数，可返 boolean 或错误字符串）、`data-tips`（错误提示）、`data-show`（错误显示位置元素 ID）、`data-notnull`、`data-ajax-check-url`（异步校验，POST `{data:value}`）
- `FormChecker.check(form)` 整表验证（遍历 input/textarea/select/[data-rule]）；`checkEle` 单元素；失败加 is-invalid+shake 抖动，成功加 is-valid；disabled 元素跳过；checkbox/radio/select 有专属必选校验
- `ajaxSubmitForm(formEle, successCallback, failCallback)`：内部先 FormChecker.check → loading("提交中...") → jquery.form ajaxSubmit → `state=="ok"` 判断，成功默认提示后回调（回调收完整响应对象）
- ⚠ 防重复提交用 `data-submiting` 标志位；file 类型校验失败不显示错误文本样式
- **siargo 可复用点**：表单校验一律声明式 data-rule，禁止提交按钮里手写 if 校验链；提交统一 ajaxSubmitForm（自带验证+loading+防重）

### 101. 组件初始化机制 afterAjaxPortal + jboltPlugins 动态加载（R15）

- **适用场景**：局部刷新（ajaxportal/Dialog 弹出）后组件失效问题；按需加载重型插件
- **源码参考**：`jbolt-admin.js` L300-328（jboltPlugins 定义表）、afterAjaxPortal() 及各组件 initBy/initByParent
- 组件采用 data-* 声明式 + `initBy(cssSelector, parentEle)` 扫描初始化；`afterAjaxPortal()` 在局部刷新后重新初始化整族组件：SelectUtil/FileUploadUtil/ImgUploadUtil/SwitchBtnUtil/FormDate/CityPickerUtil/LayerTipsUtil/HtmlEditorUtil/ImageViewerUtil/RadioUtil/CheckboxUtil/AutocompleteUtil/Select2Util
- 动态插件表 `jboltPlugins` + 已载缓存 `loadedPlugins`：jbolttable/jstree/echarts/hiprint/fileinput/qiniu/summernote/neditor/pdfjs/videojs/fullcalendar/clipboardjs 等按需加载 JS/CSS 且带版本号
- resize 事件内置 350ms throttle（硬编码）；大数据渲染用 `jboltHandleBigData(data, pageSize=15, callback)` requestAnimationFrame 分批插入防卡顿
- **siargo 可复用点**：Dialog/ajaxportal 加载的片段内组件不生效时先查是否漏了 afterAjaxPortal 时机（平台已自动调用，自造局部刷新才需手动）；echarts/hiprint 等重插件不要 `<script>` 硬引，走 jboltPlugins 按需加载

### 102. 前端上传函数与通用工具（R15）

- **适用场景**：JS 主动触发上传（非 fileinput 组件场景）、字符串/数组处理
- **源码参考**：`jbolt-admin.js` L10616（uploadFile）、L10454（uploadFileToQiniu）、L256-291（String 原型补丁）
- `uploadFile(type, url, name, fileDatas, hiddeninput, filenameInput, sizeinput, isMultiple, imgbox, loading, successCallback, failCallback, dontShowDefaultSuccessMsg)`：type 为 img/file，本地上传统一入口
- `uploadFileToQiniu(...)`：fileKey 支持模板变量 `[dateTime]/[date]/[randomId]/[filename]/[filename_random]`
- String 原型已补丁：replaceAll/trim/startWith/endWith（注意是 startWith 非 startsWith）；`JBoltArrayUtil` remove/replace/merge/prepend/append/insert；`g(id)` 取 DOM 快捷方式
- **siargo 可复用点**：JS 端裁剪图/canvas 导出等场景用 Ajax.uploadBase64File/uploadBlob，勿自拼 FormData 裸传；数组增删用 JBoltArrayUtil 而非手写 splice 循环

### 103. JBoltTable option 与 cols 列定义全清单（R16）

- **适用场景**：新建/改造列表页时查配置项，避免自造轮子
- **源码参考**：`PRO\...\assets\plugins\jbolt-table\jbolt-table.js`（v3.8.9，15536 行）L5768-5975（initTableByJsonOption/initTableColumnByJsonOption）；siargo 侧 15554 行仅差 19 行，关键函数位置一致
- 主配置项：`url`/`ajax`（AJAX 模式开关）/`cols`（必需）/`page`（bool 或分页元素 ID）/`pageSize`/`height`/`width`（像素或 'fill'）/`rowtpl`/`treetable`（数字=展开级别或 'all'）/`fixedCols`/`checkbox`/`radio`/`editable`+`editableOptions`/`form`（条件查询表单 ID）/`toolbar`/`headbox·leftbox·rightbox·footbox`/`menu`/`primaryKey`（默认 'id'）/`columnResize`（默认 true 列宽可拖）/`columnPrepend`（'checkbox'|'radio'）/`data`（本地数据模式）
- cols 列属性：`column`（必需，特殊值 checkbox/radio/index）/`title`/`width`/`minWidth`/`align`/`sort`（bool 或 `{isDefault:true, type:'asc'|'desc'}`）/`fixed`（'left'|'right'）/`valueTpl`/`hide`/`type`（编辑器类型）/`editable`/`required`/`rowspan`/`clickChecked`/`linkParent·linkSon`（checkbox 父子联动）/`valueAttr`/`submitAttr`（提交属性名，缺省驼峰转换 column）
- 模板语法（juicer 引擎）：`#{xxx}` 占位符 + `${data.field}` 行数据绑定；cols 无 valueTpl 时默认 `${data.列名}`
- **siargo 可复用点**：需求提"列宽拖拽/行内 checkbox 父子联动/默认排序列"时直接用现成配置项，禁止另写 JS 实现

### 104. JBoltTable 实例 API 与全局函数族（R16）

- **适用场景**：JS 代码操作表格实例（取选中/增删行/隐藏列/刷新）
- **源码参考**：`jbolt-table.js` L3893+（jb_methods 内部方法）；全局包装函数 jboltTableXxx 在文件头部 L40-2024
- 选中族：`getJboltTableCheckedIds(table)` / `getCheckedCols(table, column)` / `getCheckedDatas(table, needAttrs)` / `getCheckedCount` / `setCheckedIds(table, ids)`
- 行操作：`addRowDatas`（批量加行）/ `insertRowData` / `replaceRow` / `getRowJsonData(table, rowOrIndex)` / `removeJBoltTableCheckedTr`（带确认框）/ `jboltTableTrMoveUp/MoveDown`（行上下移）/ `jboltTableSetAttrValue(tableEle, tr, attr, value)`（设非显示列属性）
- 列操作：`jboltTableHideColumn/ShowColumn`（按列名）/ `hideColumnByIndex/showColumnByIndex`；区域：`jboltTableHideBox(tableEle, 'headbox'|'footbox'|'leftbox'|'rightbox')`
- 数据读取：`getAllDatas(table, needAttrs)` / `getOneColumnDatas(table, attrName)`；客户端过滤高亮 `filterByKeywords(table, keywords, colIndexArr)`
- 全局函数统一模式：`getJBoltTableInst(tableEle)` 取实例（支持传组件或带 data-table-id 的元素）失败弹 LayerMsgBox 错误；实例方法通过 `table.me.xxx(table, ...)` 调用
- **siargo 可复用点**：批量操作按钮取 ID 用 getJboltTableCheckedIds；行排序需求用 TrMoveUp/Down 而非自写拖拽；隐藏列勿用 CSS display:none（固定列克隆会错位）而用 hideColumn API

### 105. 固定列 DOM 克隆机制细节（R16，印证 siargo 规则 1.5）

- **适用场景**：固定列行为异常排查、自定义事件绑定
- **源码参考**：`jbolt-table.js` L14791-14940（cloneTableBoxContent/processColumnFixedLeft/Right/getColumnFixedIndexArr）
- 实现：克隆 table_box 的 header+body，用 `:not(:nth-child(N))` 选择器删除非固定列；右侧固定列支持负数索引（按末列 data-col-index 换算）
- 克隆表 radio 改名为 `jboltTableRadio_fixed` 防与主表同名冲突；主表 scroll 事件同步固定列 margin-top；hover 状态双向遍历同步；横向滚动条变化时 refreshFixedColumnHScroll 重算可见性
- ⚠ **每次 reload/分页/排序都重新克隆 DOM**：缓存的固定列 jQuery 引用必失效，直接绑在克隆 tr/tbody 上的事件必丢——自定义事件只能从 `.jbolt_table_box` 持久容器委托（siargo 规则 1.5 的源码依据）
- **siargo 可复用点**：排查"固定列点击无反应/状态不同步"先查是否缓存了克隆 DOM 引用；自定义固定列交互一律容器委托 + 动态查找当前 DOM

### 106. JBoltTable 分页/排序/条件查询与后端对接（R16）

- **适用场景**：列表接口开发、复杂条件搜索
- **源码参考**：`jbolt-table.js` L7151-7421（readByConditions/readByPage/submitConditionsForm）、L2015（jboltTableReadByConditions 全局函数）
- 请求参数：`pageNumber`（从 1 起）/`pageSize`/`sortColumn`/`sortType`（asc|desc）/`jsonConditions`（条件表单序列化的 JSON）——与后端 Controller 的 getPageNumber()/getSortColumn()/renderJBoltTableJsonData 对应（手册 §10）
- 条件查询唯一入口：`jboltTableReadByConditions(ele, conditions)` → 设 jsonConditions → 自动回第 1 页；排序点击 th[data-can-sort] 自动在 asc/desc 间切换
- 响应格式：`datas` 数组 + total/pageNumber/pageSize 分页信息（renderJBoltTableJsonData 自动组装）
- **siargo 可复用点**：搜索区提交用 jboltTableReadByConditions 而非自拼 URL 参数重载页面；后端条件解析用 getKv()/jsonConditions 反序列化配合 Sql 链式拼接

### 107. 可编辑表格与树形表格高级特性（R16）

- **适用场景**：主子表子表行内编辑（如 qarep 产品行）、层级数据展示
- **源码参考**：`jbolt-table.js` L9947-10149（编辑器类型）、L4227-4460（提交数据汇总）、L7423-7600（treetable）
- 编辑器类型：auto/input/input_password/textarea/input_color/money/weight/price/amount/age/input_number/input_ranger/date/datetime/time/year/month/week/autocomplete/select/select2/jboltinput；`trigger` 可选 click|doubleclick
- 提交模式 `submit.type`：'all'（整表）/'cell'（单元格级实时提交，tr 必须有 data-id）/'multi'（多表合并提交，submitMulti 以表名为 key）；提交数据包含 delete|update|save|form|params 五部分（getSubmitData）；钩子 submit.before（返 false 阻断）/success/fail；`withForm` 可联外部表单一起 FormChecker 校验
- `changeColumns` 联动：单元格值变更触发其他列配置/值更新，⚠ 注意避免循环触发；必填校验靠 data-required + data-value，提交前调 `checkEditableCellRequired(tableEle)`
- 树形表：data-level + data-pid 维护层级，treetable 配置开启；collapse/expand/expandAll API
- **siargo 可复用点**：行内编辑需求用 editable + editableOptions 而非自写 input 替换 td；子表整体保存走 submit.type='all' + getSubmitData 五段数据后端一次事务处理

### 108. CACHE.me 缓存门面方法族（R17）

- **适用场景**：后端代码读用户/角色/字典/全局配置等基础数据（免重复查库）
- **源码参考**：`PRO\...\cn\jbolt\common\util\CACHE.java`（56 行）；单例 `CACHE.me`，继承 jbolt_core.jar 内 JBoltCacheParaValidator，具体方法族均来自父类；siargo 侧同包同类可直接用
- 常用方法（调用点取证）：`getUser(userId)` / `getUserName(userId)` / `getUserUsername(userId)`；`getGlobalConfig(key)` / `getGlobalConfigKeepLoginSeconds()` / `isLoginTerminalOnlyOne()`；`getDictionaryListByTypeKey(key, cacheFlag)`；基础操作 `put(key, value)` / `get(key)`（底层 JBoltCacheKit/EHCache）
- 刷新机制：写操作后需主动调对应 remove 方法；权限/菜单变更后 `CACHE.me.removeMenusAndPermissionsByRoleGroups()`；字典变更调对应 clearCache
- **siargo 可复用点**：展示“操作人姓名”用 CACHE.me.getUserName(userId) 而非 JOIN jb_user 或自建缓存；自建 Caffeine 缓存仅限业务聚合数据（如 flowCounts），平台基础数据一律走 CACHE.me

### 109. 登录流程与 Cookie/密码机制（R17）

- **适用场景**：登录相关定制、排查登录异常
- **源码参考**：`PRO\...\cn\jbolt\index\AdminIndexController.java` L132-230（login/afterLogin）；siargo 侧 339 行（扩展了 Win11 检测）
- 流程：验证码校验（开关由 GlobalConfig `isJBoltLoginUseCapture()` 控制，非硬编码）→ `UserService.getUser(username, password)` 验密 → 检查 enable 状态 + 角色分配（超管豁免）→ 登录日志（IP/UA/OS）→ `OnlineUserService.processUserLogin(keepLogin, user, log)`
- 密码传输格式：前端 `随机2字符 + md5(明文) + 随机3字符`（login.js/relogin.js），后端 UserService.checkPwd 验证
- Cookie 三件套：`JBOLT_SESSIONID_KEY`（sessionId）+ `JBOLT_SESSIONID_REFRESH_TOKEN`（JWT，时长=keepLoginSeconds+28800s）+ `JBOLT_KEEPLOGIN_KEY`；keepLogin 时长由 GlobalConfig keepLoginSeconds/notKeepLoginSeconds 控制
- **siargo 可复用点**：登录相关需求（验证码开关/保持时长）改 GlobalConfig 即可，勿改代码；自定义登录后动作插在 afterLogin 链路

### 110. 锁屏/在线用户/强制下线机制（R17）

- **适用场景**：会话安全需求、踢人功能、单端登录限制
- **源码参考**：`BASE\...\_admin\onlineuser\OnlineUserService.java`（361 行）、`AdminIndexController` lockSystem/unLockSystem L66-83、前端 `jbolt-admin.js` L17273-17564（锁屏 UI+前端 md5 函数）
- 在线用户表 jb_online_user：session_id/user_id/login_log_id/screen_locked/online_state（ONLINE/OFFLINE/TERMINAL_OFFLINE/FORCED_OFFLINE）/expiration_time；cron 任务 JBoltOnlineUserClearTask 每分钟清理离线/过期记录
- 锁屏：ifvisible 监听空闲超 `JBOLT_AUTO_LOCK_SCREEN_SECONDS` → POST /admin/lockSystem → screen_locked=true；解锁 /admin/unLockSystem 验 MD5 密码；前端 showJboltLockSystem() 带 1s 定时器防篡改（iframe 内自动升到 top 窗口）
- 单端登录：`isLoginTerminalOnlyOne()` 为 true 时新登录踢掉同用户其他会话（状态改 TERMINAL_OFFLINE + WebSocket 推送）；强制下线 forcedOffline 仅超管可操作且不能强退自己/超管，WebSocket 推 user_forced_offline 指令后前端弹窗跳登录页
- 定向推送基础：`getSessionListByUserId/RoleId/DeptId/PostId`（roles/posts 字段 InSet 关系）——WebSocket 按维度推送的底层依据（§89）
- 前端三大特殊错误码处理链（与 §98 呼应）：msg==jbolt_system_locked → showJboltLockSystem()；jbolt_nologin → showReloginDialog()；jbolt_terminal_offline → 全局 ajaxComplete 统一处理
- **siargo 可复用点**：需“互斥登录/踢人”直接开 isLoginTerminalOnlyOne 配置 + 现成 forcedOffline 链路，禁止自建会话表；自定义后端拦截响应时不要占用这三个保留 msg 值

### 111. 前端 Tab 管理机制（R17）

- **适用场景**：JS 主动打开/刷新/关闭工作区 Tab
- **源码参考**：`jbolt-admin.js` L5192-5900（openTab 族）、L6006-6040（sessionStorage 维护）
- `openTab/openTabWithOptions(key, url, text, triggerKey, active, openType, openOptions)`：openType 1=默认 pjax / 2=iframe / 3=dialog / 4=jboltlayer（也支持字符串 'iframe'|'dialog'|'jboltlayer'）；`refreshJboltTab(key)` 刷新；closeJboltTab/closeAllLeftJboltTab 等关闭族
- 状态持久化：`jbolt_tabs_array` 全局数组 + sessionStorage 同名 key；刷新后恢复受 `#JBOLT_TAB_KEEP_AFTER_RELOAD` 页面配置控制；sessionStorage 按浏览器标签页隔离（多浏览器标签互不影响）
- 菜单联动：菜单项 data-key 对应 tab key，点菜单自动打开或激活对应 tab
- **siargo 可复用点**：业务页“跳转到另一模块页面”用 parent.openTab 而非 window.location 整页跳转；需刷新目标 tab 用 refreshJboltTab(key)

### 112. Enjoy 自定义指令全集与共享对象注册（R18）

- **适用场景**：模板中需要权限判断、配置读取、格式化输出时优先使用平台内置指令，禁止在模板里拼 Java 逻辑
- **源码参考**：BASE `MainConfig.configEngine`（L744-L757 注册指令）；BASE `cn/jbolt/common/directive/` 目录（各指令实现）；PRO/siargo `ProjectConfig.configEngines`（L167-L192 共享对象）
- 内置指令清单（`#指令名(...)` 形式）：`ajaxPortal`（异步门户加载）、`realImage`/`realUrl`（真实路径/URL 转换）、`datetime`（日期格式化）、`prettytime`（相对时间 "3小时前"）、`permission`（`#permission(key) ... #end` 按权限渲染块）、`role`（按角色数组判断）、`globalConfig`（`#globalConfig(key [,default])` 读全局配置）、`sex`（性别值转文本/图标）、`rownum`（行号）、`jboltAdminLogo`、`action`（动作链接）、`json`（JSON 序列化输出）、`boolToStr`、`enable`（启用状态转换）
- SQL 引擎专属指令（注册在 ActiveRecordPlugin 的 sqlEngine，非页面引擎）：`sqlValue`、`likeValue`（模糊查询值处理）——模板 SQL 中防注入的标准方式
- 共享对象（模板中可直接调用静态成员）：`CACHE`、`SessionKey`、`PermissionKey`、`WechatConfigKey`、`JBoltConfig`、`JBoltUserKit`、`JBoltStringUtil` 及若干枚举类；共享方法类 `JBoltUserAuthKit`（hasPermission/hasRole）
- 自定义指令写法：继承 `com.jfinal.template.Directive`，`setExprList` 解析参数，`exec` 中 `stat.exec` 渲染内容块（hasEnd=true 的块指令）或 `writer.write` 直接输出
- **siargo 可复用点**：新指令通过 `ExtendProjectConfig.configEngine(Engine me)` 扩展点注册（勿改 ProjectConfig 本体）；模板权限显隐优先 `#permission`/`#role` 指令而非把布尔量塞进 Controller setAttr；共享对象注册必须在 addSharedFunction 之前完成

### 113. 全局拦截器链与 Handler 链（R18）

- **适用场景**：理解请求进入 action 前经过的全局处理层；新增放行路径/富文本白名单时找对扩展点
- **源码参考**：PRO/siargo `ProjectConfig.configInterceptors`（L269-L274）、`configHandlers`（L288-L297）；BASE `MainConfig.configInterceptor`（L630-L636）、`configHandler`（L641-L666）
- 全局拦截器：`SessionInViewInterceptor`（模板可直接读 session）→ `JBoltOnlineUserGlobalInterceptor`（维护在线状态，配合 §110 在线用户机制）；BASE 另有 `JBoltExceptionGlobalInterceptor` 全局异常处理
- 全局 Handler：`JBoltBaseHandler`（basePath/pmkey/静态 html 直访拦截）→ `XssHandler`（XSS 过滤）→ 条件启用 `UrlSkipHandler`（WebSocket 路径跳过）、`JBoltUreportViewHandler`、`JBoltDruidStatViewHandler`（监控页权限：校验用户存在/启用/未锁屏，超管放行，普通用户需对应 PermissionKey）
- 方法级拦截器惯例：`@Before(Tx.class)` 声明式事务（**siargo 禁用**，改手动 Db.tx + afterCommit）、`@Before(JBoltNoUrlPara.class)` 禁止 URL 挂参、`@Clear` 清除父级拦截器（公开页如登录/注册）
- Handler 白名单扩展：`baseHandler.unlimited("/assets/plugins/","/neditor/")` 静态资源放行；`xssHandler.unlimited("admin/sysnotice/save", ...)` 富文本提交白名单——均写在 `ExtendProjectConfig.configHandler`
- **siargo 可复用点**：富文本编辑器字段被 XSS 过滤转义时，正确做法是把该 action 加入 xssHandler.unlimited 白名单，而非自行解码；所有 Controller 必须继承 JBoltBaseController，否则权限拦截器抛 RuntimeException

### 114. ProjectConfig 装配机制与启动流程（R18）

- **适用场景**：新增业务模块包、定时任务、事件监听、扩展数据源时找对装配位置
- **源码参考**：PRO `ProjectConfig.java`（521 行）；siargo `ProjectConfig.java`（541 行）；siargo `cn/jbolt/extend/config/ExtendProjectConfig.java`（198 行，二开扩展点全集）；BASE `MainConfig.java`（894 行单体设计）
- 装配顺序（JFinal 标准生命周期）：configConstants（404/500 页）→ configRoutes（路由+包扫描）→ configEngines（指令/共享对象/layout 函数）→ configPlugins（Cron4j/EventPlugin/Druid/ARP）→ configInterceptors → configHandlers → onStart（数据初始化/升级/ActionReport/Assets 版本/异步加载 codegen 翻译类）
- **siargo 路由是显式包扫描**：configRoutes 中逐个 scan `cn.jbolt.admin.siargo` 各子包（apicalllog/changelog/cme/customer/dms/equipment/imi/qarep/supplier）+ api 包——新增顶层业务子包必须补 scan 一行否则 404；在已扫描子包下新增 Controller 则无需改配置
- 插件装配：Cron4jPlugin（定时任务，含 JBoltOnlineUserClearTask；二开任务在 ExtendProjectConfig.configCron4jPlugin 中 addTask）；EventPlugin（异步事件，16-32 线程池，扫描 `cn.jbolt._admin.event` 包——新事件监听器固定放此包）；条件插件 WebSocket/Ureport/Sentinel
- 二开扩展点（ExtendProjectConfig 静态方法，每阶段末尾被 ProjectConfig 调用）：configConstant/configRoute（含 ProjectCodeGenRoutesConfig.config 生成器路由）/configPlugin（configTx+configCron4jPlugin）/configInterceptor/configHandler/configEngine/onStart/onStop；另有 configMainDbPlugin/configExtendDbPlugins（多数据源 ARP+sqlEngine）与 SaaS 租户处理器族（configSaasTenant*Processor）
- **siargo 可复用点**：所有平台级定制一律走 ExtendProjectConfig 扩展点，不改 ProjectConfig 本体（升级平台版本时减少冲突）；新增定时任务用 cron4jPlugin.addTask 而非自建线程

### 115. JBoltUploadFolder 目录常量全集与落盘流程（R19，补充 §17）

- **适用场景**：任何文件上传需求的目录规划与落盘标准写法
- **源码参考**：siargo `cn/jbolt/common/config/JBoltUploadFolder.java`（56 行）；`NeditorUploadAdminController` L42-L94（标准上传流程）
- 目录常量体系：平台常量（DEMO_*/EDITOR_NEDITOR_*/EDITOR_SUMMERNOTE_IMAGE/MALL_*/WECHAT_*/USER_AVATAR/SYSNOTICE_FILES/IMPORT_EXCEL_TEMP_FOLDER/CODE_GEN_TEST_*）+ siargo 自定义（SIARGO_UPLOAD_IMI="imi"/SIARGO_UPLOAD_DMS="dms"/SIARGO_UPLOAD_EQUIPMENT_CERTIFICATE），均用 `File.separator` 拼接
- `todayFolder()` 返回 yyyyMMdd；`todayFolder(path)` 追加按天子目录——日常上传目录标准写法 `JBoltUploadFolder.todayFolder(JBoltUploadFolder.XXX)`
- 标准落盘流程：构建路径 → `getFile("file", uploadPath)` 接收 UploadFile → `jboltFileService.saveImageFile/saveAttachmentFile(file, uploadPath)`（重命名移动+MD5+真实类型识别+生成 local_url+入库 jbolt_file）→ 返回含 local_url/file_id 的 Ret → 业务表存 file_id
- 上传配置（config.properties）：`base_upload_path=upload`、`base_download_path=download`、`max_post_size`（单位 KB，siargo=102400 即 100MB，PRO/BASE=20480 即 20MB）、`jbolt_global_upload_to=local|qiniu|alioss`
- **siargo 可复用点**：新上传需求先在 JBoltUploadFolder 加常量再用，禁止在 Controller 里硬编码目录字符串；临时目录模式（如 dms/temp/ 先落盘、确认后移正式目录）适合表单未提交先传文件的交互

### 116. DownloadUtil 与文件下载渲染族（R19，补充 §88）

- **适用场景**：附件下载按钮、文件流响应、附件库文件访问
- **源码参考**：前端 `assets/js/jbolt-admin.js` L1960+（DownloadUtil）；后端 `DmsFileAdminController` L252-L279、`SysNoticeAdminController` L152（renderFile 用法）
- DownloadUtil 是**前端 JS 工具**（非 Java 类）：`DownloadUtil.init()` 绑定监听，`data-downloadbtn` + `data-url` + `data-filename` 属性声明式下载按钮，`downloadByEle(ele)` 按元素属性执行异步下载
- 后端下载用 JFinal `renderFile(file, fileName)`（触发浏览器下载对话框）；内联预览（图片/PDF）通过 jbolt_file.local_url 直接作为 URL 访问，浏览器按 Content-Type 内嵌显示
- jbolt_file 附件库要点：文件类型常量 IMAGE=1/VEDIO=2/AUDIO=3/OFFICE=4/ATTACHMENT=5；方法族 saveImageFile/saveAttachmentFile/saveVideoFile/saveExcelFile/saveAudioFile/getListByIds/findById；业务表存 jbolt_file_id 引用
- 已知陷阱：file_size 为 int 类型≈ 2GB 上限；文件类型识别应按内容（hutool FileTypeUtil.getType）而非后缀；Windows/Linux 路径分隔符由 setLocalUrl 自动转换但自建路径需自行处理
- **siargo 可复用点**：简单附件场景用 JBoltFileService 统一附件库不新建文件表；复杂生命周期（审核/生效日期/关联删除）自建文件表属合理定制（参考 DmsFileService）；文件删除必须 afterCommit（见规则 6.3）

### 117. 配置项全集与环境切换机制（R19）

- **适用场景**：部署/调参/新环境搭建时查配置含义；排查缓存/上传/WebSocket 等开关问题
- **源码参考**：siargo `src/main/resources/application.properties`、`config.properties`、`config-pro.properties`、`dbconfig/mysql/config.properties`
- 三层配置架构：application.properties（system_name/system_copyright_*/`pdev=dev|pro` 环境切换/`demo_mode`）→ 按 pdev 加载 config.properties 或 config-pro.properties（60+ 项）→ 按 db_type 加载 dbconfig/{dbType}/config.properties
- 重点配置项：`jbolt_cache_type=caffeine|ehcache|redis|j2cache`、`jbolt_websocket_enable`、`jbolt_ureport_enable`、`sentinel_enable`、`jbolt_code_gen_enable`+`jbolt_code_gen_class_scan_packages`、`dev_mode`/`engine_dev_mode`/`dbsql_engine_dev_mode`、`global_default_id_gen_mode=snowflake`、`process_get_request_target_xss_enable`、`sensitive_word_check_enable`、`disallowed_http_methods`、`jbolt_proxy_type`、`jbolt_redis_serializer_type=fst|jb_fst|fury`
- DB 配置结构：`jdbc_url`/`user`/`password`（`is_encrypted=true` 时为密文，框架连接前自动解密）/`id_gen_mode=snowflake`/`model_package`（逗号分隔，**新 Model 包必须加入否则框架不识别**）
- 版本差异：siargo 用 caffeine + 100MB 上传 + 敏感词检查启用 + 代码生成器启用；PRO/BASE 用 ehcache + 20MB；`demo_mode=true` 会禁用删除操作（生产必须 false）
- **siargo 可复用点**：环境差异只改 config-pro.properties 不改代码；数据库凭证一律加密存储（is_encrypted=true）；上传大小超限报错先查 max_post_size（KB 单位）

### 118. 参数校验体系（JBoltValidator + isOk 语义）（R20，补充 §10/§22）

- **适用场景**：Controller/Service 参数校验的分层写法；判断 isOk 语义避免误判
- **源码参考**：BASE `cn/jbolt/base/para/JBoltValidator.java`（L14-L75）、`JBoltParaValidator.java`（L37-L145）；实现类例 `WechatUserMgrValidator`（业务逻辑校验）
- **isOk 语义陷阱**：Integer/Long 必须 `!=null && >0`——0 和负数都无效！状态位等可为 0 的参数不能用 isOk，应用 notNull；String 为 notBlank；Collection 为非空且 size>0；Model/Record 为非空且有属性；`hasNotOk(a,b,c)` 批量校验任一无效即 true
- 四层校验分工：前端 FormChecker data-rule（快速反馈）→ Validator 类（@Before 注解，基本类型校验 validateJBoltLong/Integer，失败 handleError 走 renderValidatorError）→ Controller isOk/notOk/hasNotOk（参数存在性）→ Service 开头校验（业务逻辑：存在性/唯一性，失败 return fail 不抛异常）
- FormChecker data-rule 不支持自定义函数复杂规则→用 data-ajax-check-url 异步后端验证
- **siargo 可复用点**：不能仅依赖前端校验，Service 层必须二次确认；Model 字段注入拼错 name 会 500 异常而非静默丢失

### 119. 工具类族补全（Kv/Ret 差异 + JBoltUserKit 陷阱）（R20，补充 §19）

- **适用场景**：选对工具类避免重复造轮子；异步场景用户上下文处理
- **源码参考**：jbolt_core.jar 内 `cn.jbolt.core.util/*`、`cn.jbolt.core.kit.JBoltUserKit`（调用点反推）
- **Kv vs Ret**：Kv 是简单键值对（`Kv.by(k,v).set(k2,v2)`，内部参数传递）；Ret 含 state/msg/data 结构（`Ret.ok()/fail()`，HTTP JSON 响应必须用 Ret）；标准响应 `{state:"ok"[,msg][,data]}`
- **JBoltUserKit 全族**：getUserId/getUser/getUserName/isSystemAdmin/isEnable/getOnlineUser/userScreenIsLocked——全部 ThreadLocal；流程：JBoltBaseHandler 从 sessionId→CACHE 取 OnlineUser→setUserId，请求结束 finally clear 防线程池串号
- **陷阱**：异步线程/定时任务中 JBoltUserKit.getUserId() 为 null，必须在同步代码取出 userId 显式传入
- hutool 边界：复杂操作用 hutool（DateTime/FileTypeUtil/IdUtil），简单操作用 JBolt 自带（JBoltStringUtil.isBlank/join/randomUUID/filterEmoji、JBoltDateUtil.getNowStr/format/addDay、JBoltRandomUtil.randomNumber、JBoltMd5Util.md5）；JBoltSnowflakeKit 底层即 hutool IdUtil.getSnowflake
- **siargo 可复用点**：通知/事件/定时任务等异步链路中的操作人字段，在 Controller 同步代码取 userId 后传参，禁止在异步体内调 JBoltUserKit

### 120. hiprint 打印集成全流程（R20）

- **适用场景**：单据/标签/报告打印需求，优先用平台已集成的 hiprint 而非自引打印库
- **源码参考**：siargo `_admin/hiprint/HiprintAdminController.java`（L1-L146）、`HiprintTplService.java`（L1-L101）；前端 `_view/_admin/hiprint/index.html`（L1-L305 设计器）；插件 `assets/plugins/hiprint/`
- 模板存储：hiprint_tpl 表（id/name/sn/content(JSON)/enable/create_user_id）；submitTpl 做 name 必填+SN 唯一性（未传自动生成 6 位随机数）+content 默认结构初始化（panels=[{index:0,paperType:'A4',printElements:[]}]）；tplContent(sn) 带 @UnCheck+按 sn 缓存
- 前端流程：`hiprint.init({providers:[...]})` → `new hiprint.PrintTemplate({template:json, settingContainer, paginationContainer})` → 设计 `tpl.design('#画布')` / 保存 `tpl.getJson()` / 预览 `tpl.getHtml(printData)` / 直接打印 `tpl.print(printData)` / 导 PDF `tpl.toPdf(printData,name)` / 纸张 `tpl.setPaper(A4|宽,高)`
- 一键调用：`jboltHiprintPrint(ele, tplSn, type, printData)`——按 sn 取模板 JSON，type=json 用 print / type=url 用 print2（数据源差异注意区分）
- **siargo 可复用点**：siargo 已集成 hiprint 插件+模板管理，新打印需求直接建 hiprint_tpl 模板 + jboltHiprintPrint 调用，禁止另引打印库或手写 window.print 拼 HTML

### 121. EventKit 事件体系全链路（R21，补充 §45）

- **适用场景**：写操作后异步触发通知/推送/统计等解耦逻辑
- **源码参考**：`siargo\src\main\java\cn\jbolt\_admin\event\JBoltEventListener.java`（L19-L112）、`_admin\msgcenter\TodoService.java`（save L41-L81）、`SysNoticeService.java`（save L104-L117）
- 框架为 net.dreamlu.event：`EventKit.post(model)` 发布，**按监听方法参数类型自动匹配**，无显式事件名常量；事件对象可为任意 Model/POJO
- 监听器写法：类放 `cn.jbolt._admin.event` 包（EventPlugin 扫描该包），方法加 `@EventListener(async=true/false)`，参数类型即监听目标类型；async=true 在 16-32 线程池异步执行
- 平台内置 3 个监听：`sysNotice(SysNotice)`（按 receiverType 1=全部/2=角色/3=部门/4=岗位/5=用户 分发 WebSocket 推送）、`todo(Todo)`（推 new_todo）、`onlineUser(OnlineUser)`（推 user_forced_offline/user_terminal_offline）
- 标准链路：Service 保存 → EventKit.post → @EventListener 异步监听 → JBoltWebSocketUtil 推送 → 前端红点/提示
- **陷阱**：事务内 post 时异步监听器会读到未提交数据（脏读）；涉及缓存清理会失效、文件操作会误删临时态
- **siargo 可复用点**：**EventKit.post 必须在 Db.tx() 返回 true 之后（afterCommit）调用**——PRO/BASE 在 @Before(Tx.class) 的 save 内直接 post 的写法不可照搬；范例 QareportService.saveProcessData（L1521-L1562）：Db.tx 包裹批量保存，事务成功后统一 post(todo)

### 122. WebSocket 前后端全链路（R21，补充 §46/§89）

- **适用场景**：实时推送通知/待办/强制下线/自定义业务消息到在线用户浏览器
- **源码参考**：`_admin\websocket\JBoltWebSocketUtil.java`、`JBoltWebSocketMsg.java`、`JBoltWebSocketCommand.java`、`JBoltWebSocketServerEndpoint.java`；前端 `assets\js\jbolt_websocket\jbolt-websocket.js`
- 端点：`@ServerEndpoint("/websocket.ws/{token}")`，以登录 token 认证（无 @CheckPermission）；开关 `jbolt_websocket_enable=true`
- 后端推送 API 族（JBoltWebSocketUtil）：`sendMessageToUser(userId,msg)`（L331）/ `sendMessageToUsers(userIds[],msg)` / `sendMessageToUserByRole(s)`（L426/L442）/ `ByDept(s)`（L395/L411）/ `ByPost(s)`（L365/L379）/ `sendAllMessage(msg)`（L240）/ `sendMessage(token,msg)`（L291，支持 clientSessionId 精确定位）
- 消息结构 JBoltWebSocketMsg：from/to（FROM_SYSTEM=-1）/type（1=COMMAND 2=COMMAND_RET 3=TEXT 4=FILE）/command/data/isTenant/tenantSn/clientSessionId；系统指令用 `JBoltWebSocketMsg.createSystemCommandMsg(command, data)` 构造
- 内置指令：PING/PONG（心跳）、SERVER_TIME、CLIENT_USER_COUNT、MSGCENTER_CHECK_UNREAD（刷新未读红点）、CHECK_LAST_PWD_UPDATE_TIME；业务指令：new_notice / new_todo / user_forced_offline / user_terminal_offline
- 前端 JBoltWS：`init({protocol,host,ctx,token,tenantSn,reconnInterval,heartbeatTimeout})` → `connect()`；PING-PONG 心跳超时自动重连；`registerCommand(command, handler)` 注册自定义指令处理器；发送用 `sendCommand(command,data,to)` / `sendText(data,to)`
- **陷阱**：**离线用户消息直接丢弃不落库**——重要通知必须先落 sys_notice/todo 表（登录后仍可见），WebSocket 仅作在线实时提醒
- **siargo 可复用点**：新增实时推送 = 落业务表 → afterCommit 后 EventKit.post → 监听器内 createSystemCommandMsg + sendMessageToUser/ByRole → 前端 registerCommand 处理；推送同缓存清理一样必须在事务提交后执行

### 123. SelectUtil / DialogUtil / 组件初始化链（R21，补充 §33/§37）

- **适用场景**：下拉数据联动、弹窗打开、动态 DOM 后的组件重新初始化
- **源码参考**：`assets\js\jbolt-admin.js`——SelectUtil L11706-L11800、DialogUtil L13060-L13533
- SelectUtil：data-url 驱动异步加载下拉项（`data-url`/`data-text`/`data-value`/`data-placeholder`）；`data-link-para-ele` 声明级联参数来源元素实现联动；API：`SelectUtil.refresh(select)` / `readAndInsertItems` / `initAutoLoadSelect`
- DialogUtil：声明式 `data-dialogbtn` + `data-title/data-url/data-width/data-height/data-max/data-drag` 自动绑定；API：`DialogUtil.openBy(btn)` / `openNewDialog(obj)` / `close()` / `getCurrent()`；**平台无 openJBoltDialog/layerDialog 等函数**，勿凭空捏造
- 插件按需加载：`pageLoadRequirePluginAndInit` 管理，jboltPlugins 共 18 个：formdate/citypicker/select2/autocomplete/radio/checkbox/fileupload/imgupload/switch/jstree/inputwithclear/inputwithcalc/rangeSlider/textarea/jbolttable/jbolttreetable/tabview/jsoneditor；pageInit 初始化函数族 28+，均按 data-* 属性扫描绑定
- **陷阱**：组件全靠 data-* 声明驱动，AJAX 注入的 DOM 不会自动初始化，需手动调对应 init 函数；多层 Dialog 嵌套注意 z-index/焦点混乱；平台无内置穿梭框/颜色选择器
- **siargo 可复用点**：下拉联动优先用 data-link-para-ele 而非手写 $.get+append option；弹窗一律 data-dialogbtn 声明式或 DialogUtil.openBy，禁止自引 layer/bootstrap modal

### 124. Sql 链式 API 补全：QM 占位符族与终结方法（R22，补充 §13）

- **适用场景**：动态条件拼接、带别名的单层 JOIN、递归遍历子节点
- **源码参考**：`BASE\...\_admin\dept\DeptService.java`（L153-L226 Sql 实战）、`siargo\src\main\resources\gentpl\codegen\service_common_template.jf`（L68-L126）
- QM 后缀占位符族：`eqQM/neQM/gtQM/pidEqQM` 等生成 `?` 占位符不带值，配合 `toSql()` + 外部传参；范例：`dao().each(son->{...}, selectSql().pidEqQM().eq("enable",TRUE).toSql(), id)` 递归遍历子节点（DeptService L219-L226）
- 终结方法：`toSql()` 生纯 SQL 串；`toPara()` 分离参数数组防注入；Service 内直接 `find(sql)/paginate(sql)/findRecord(sql,true)`（第二参 true=isGroupBySql）
- 带别名 JOIN：`selectSql().from(table(),"dept").select("dept.*","dic.name as type_name").leftJoin(他service.table(),"dic","dept.type=dic.sn").eq("dic.type_key","dept_type")`（DeptService L168-L175，单层 JOIN 可用 Sql，复杂多表仍用原生 SQL）
- 快捷排序：`orderBySortRank()`（树形/排序表专用）；其余 orderBy/asc/desc/likeMulti/betweenDateRange/eqBooleanToChar 见 §13
- **siargo 可复用点**：动态条件一律 `if(isOk(x)) sql.eq(...)` 包裹；关键词搜索用 `likeMulti(keywords, 列...)`；禁止手拼 SQL 字符串拼接变量（注入风险）

### 125. GlobalConfigKey 常量全集与新增配置步骤（R22，补充 §8/§96）

- **适用场景**：新增可后台维护的系统参数，避免硬编码/改 properties 重启
- **源码参考**：`BASE\...\common\config\GlobalConfigKey.java`（L1-L144 常量清单）、`siargo\...\_admin\globalconfig\GlobalConfigAdminController.java`（L83-L185）；siargo 用 jar 内 `cn.jbolt.core.base.JBoltGlobalConfigKey`
- 常量分域：UI（SYSTEM_NAME/SYSTEM_ADMIN_LOGO/JBOLT_ADMIN_STYLE/JBOLT_ADMIN_WITH_TABS）、登录（JBOLT_LOGIN_USE_CAPTURE/CAPTURE_TYPE/BGIMG/NEST/FORM_STYLE_GLASS）、会话（KEEPLOGIN_SECONDS/NOT_KEEPLOGIN_SECONDS/AUTO_LOCKSCREEN_SECONDS/LOGIN_TERMINAL_ONLYONE）、功能开关（SYSTEM_DEPT_ENABLE/SYSTEM_POST_ENABLE/JBOLT_TAB_KEEP_AFTER_RELOAD）、资源（ASSETS_VERSION，changeAssetsVersion() 强制刷新前端静态资源缓存）
- 读 API：`JBoltGlobalConfigCache.me.getConfigValue(key)` / 快捷方法 `isJBoltLoginUseCapture()/getLoginFile()/getSystemName()`（AdminIndexController L210/L263/L323 实例）
- SYSTEM_NAME 约定：支持 `名称:#颜色值` 格式一体存储（_form.html L155-L180）
- 新增业务配置 6 步：① jb_global_config_type 加分类 ② jb_global_config 插记录（built_in=false） ③ 后端 JBoltGlobalConfigCache 读 ④ 前端 #globalConfig 指令 ⑤ 后台 globalconfig 页面维护 ⑥ 变更后 clearCache()
- **siargo 可复用点**：配置变更的 clearCache() 同样遵守 afterCommit 纪律（Controller 在事务提交后调）；开关/阈值类需求禁止自建配置表

### 126. JSTree 树形组件专项全链路（R22，补充 §15）

- **适用场景**：层级数据管理（分类/组织/目录树）、上级节点选择器、树形表格
- **源码参考**：`BASE\...\_admin\dept\DeptService.java`（L38-L325 全链路典范）、`siargo\...\_view\_admin\dept\treemgr.html`（L41-L57）、`assets\js\jbolt-admin.js`（_initTree L2509-L2753、setChecked L2822）
- Model 字段约定：id/pid（null 或 0 为根）/sort_rank（同级排序）；两种输出：`convertJsTree(list, checkedId, openLevel, disabledNodes, "sn,name", SORT_RANK, disableFunc, isCheckAll)` → JsTreeBean（jstree 选择器）；`convertToModelTree(list,"id","pid",(p)->notOk(p.getPid()))` / `convertToRecordTree` → items 嵌套（树表格/级联）
- 前端 data-jstree 属性清单：data-read-url（必填）/data-open-level（-1全展/0不动/>0指定）/data-checkbox/data-onlyleaf/data-search-input/data-async（异步按 pid 加载）/data-sync-ele/data-change-handler/data-conditions-form；CRUD 模式：data-curd=true + data-add-url/edit-url/delete-url/move-url + data-target=dialog|portal + data-dialog-area="W,H"（右键菜单增删改+拖拽排序）
- 取值 API：`tree.jstree(true).get_all_checked(false,withroot,"id"|"text",onlyleaf,onlytype)` 批量取勾选；`setChecked(tree, id或数组)` 回显；data-jstree-value-attr 可指定回写字段（需接口携带 data 原始 JSON）
- 排序/级联维护典范（DeptService）：新增 `getNextSortRankByPid(pid)`；换父 `updateSortRankAfterChangeParentNode`；上下移交换 sort_rank；删除前 `existsSon(id)` 拦截；启用级联 `processParentEnableTrue`（启用时父链全启）/`processSonEnableFalse`（禁用时子树全禁）
- 陷阱：convertToModelTree 会覆盖 Model 自定义字段（特殊场景手工挂树）；checkbox three_state=false 时不级联；删除/移动后需清树缓存
- **siargo 可复用点**：层级需求直接照搬 Dept 全链路（Service 方法族+treemgr.html 模板）；树更新涉及多行（move/级联 enable）必须 Db.tx() 包裹，afterCommit 后清缓存；禁止手写递归组树

### 127. 消息中心前端全链路：铃铛/红点/已读（R23，补充 §89/§122）

- **适用场景**：新业务消息接入铃铛提醒、自定义未读红点逻辑
- **源码参考**：`_view\_admin\common\__admin_layout.html`（L138 铃铛+红点）、`assets\js\jbolt_websocket\jbolt-websocket-command.js`（L1-L63）、`_admin\websocket\JBoltWebSocketCommandHandler.java`（L62-L98）、`_view\_admin\msgcenter\layer.html`
- 铃铛：`<a data-openpage="jboltlayer" href="admin/msgcenter/layer">` + `<span id="msgCenterRedDot" class="reddot">`；红点控制函数 `showMsgCenterRedDot()/hideMsgCenterRedDot()`
- 双机制：WebSocket 建连后发 `msgcenter_check_unread` 指令，后端 `processMsgCenterCheckUnreadCommand` 依次查 SysNotice.existUnread → Todo.existUnread → Todo.existNeedProcess 返 boolean；无 WebSocket 时 `initReadUserMsgCenterUnreadInfo()` 每 30 秒轮询 `admin/msgcenter/unreadInfo`
- 消息面板 layer.html：jbolt_tab_view 双 Tab（通知/待办）+ data-ajaxportal 加载 `admin/msgcenter/sysnotice|todo/portalDatas` + text/template 客户端模板；详情 dialog 打开 `sysnotice/detail/{id}`，已读端点 `markAsRead`（先校权限再 addReader + 自增 read_count）
- 自定义扩展两途径：① JBoltEventListener 加新 @EventListener 方法监听自定义 Model；② `_admin\websocket\extend\JBoltWebSocketExtendCommandHandler` 加自定义 command（内置 switch 未命中时自动路由到扩展处理器）
- receiver_type≠1 时 receiverValue 必填（逗号分隔 ID 串），否则推送直接跳过
- **siargo 可复用点**：新业务提醒接入铃铛优先复用 Todo 体系（落 todo 表→afterCommit post→自动红点），无需自建提醒 UI；自定义指令走 ExtendCommandHandler 不改内置 switch

### 128. JBoltInputUtil 输入下拉 portal 组件（R23，补充 §123）

- **适用场景**：输入框点击弹出树/表格/自定义内容选择器（选部门/选用户/可搜索选择）
- **源码参考**：`assets\js\jbolt-admin.js`（JBoltInputUtil L912-L1410，layer 机制 L1216-L1383）
- data-* 协议：`data-jboltinput`（声明）+ `data-load-type=html|ajaxportal|jstree` + `data-url`（数据源）+ `data-hidden-input`（隐藏域 id，支持逗号分隔多个）+ `data-filter-handler=filterTable|filterTree|自定义` + jstree 附加：data-onlyleaf/data-onlytype/data-textasvalue/data-open-level + `data-content-id`（html 模式内容容器）+ data-width/data-height
- API：`JBoltInputUtil.init(parentEle)`（扫描绑定）/ `setValue(ele,text,value,jsonData)`（text/value 分离回写，同步隐藏域）/ `filterTable/filterTree`（输入过滤）/ `hideJBoltInputLayer/removeLayer`
- 机制：点击 input 动态创建 `.jbolt_input_layer` 层（自动上下定位、宽随 input、高默认 350px）；table 内容点 `[data-jboltinput-setvalue-trigger]` 行回写；jstree 模式 textasvalue 控制 value 取 id 还是 text
- 选型：简单远程选项用 SelectUtil/select2；树形选择或表格筛选用 JBoltInputUtil；自定义 HTML 内容用 html 模式
- 陷阱：layer 复用可能内容不更新（data-input-layer-clear 强制重建）；AJAX 注入 DOM 需手动 JBoltInputUtil.init；filterHandler 为空且 readonly=true 时变只读选择模式
- **siargo 可复用点**：“可搜索选择器”类需求（选设备/选客户）直接用 data-jboltinput + ajaxportal/jstree，禁止自建下拉搜索组件

### 129. 字典消费与字典/枚举决策（R23，补充 §13 字典后端）

- **适用场景**：下拉选项类数据的存储与前端渲染选型
- **源码参考**：`_admin\dictionary\DictionaryAdminController.java`（L29-L82 四个 @UnCheck 端点）
- 前端消费：`<select data-autoload data-url="admin/dictionary/options?key=xxx" data-text="=请选择=" data-value-attr="sn">`；多级字典用 poptions（一级）/soptions(key,pid)/soptionsByPsn(key,psn) 级联
- 后端读：`JBoltDictionaryCache.me.getListByTypeKey(key,true)` / `getNameBySn(key,sn)` / `getNameStrBySns(key,sns)`（多选回显）；模板可用共享对象 CACHE 直读
- 表约定：jb_dictionary_type（type_key 唯一/mode_level 1单级 2多级）+ jb_dictionary（type_id/sn/name/rank/enable）；type_key 英文标识，系统预置（sys_notice_type/todo_state 等）禁改
- **字典 vs JBoltEnum 决策**：变化频率低、数量固定、代码分支依赖的用 JBoltEnum（#enumToOptions 渲染）；需后台维护、变化频繁的用 Dictionary；两者不混用
- 陷阱：options 系列端点均 @UnCheck 任何人可调，不得放敏感数据；字典变更后需清缓存（afterCommit）；**平台无 i18n 多语言机制**，三库均硬编码中文
- **siargo 可复用点**：siargo 已有 siargo_* 前缀 type_key 惯例，新可维护选项继续入字典；状态/环节类固定枚举用 JBoltEnum 不入库

### 130. Cron4j 定时任务全链路（R24，补充 §113）

- **适用场景**：siargo 新增定时任务（到期提醒、数据清理、定时同步）
- **源码参考**：siargo `ProjectConfig.configCron4jPlugin()` L258-L263；`ExtendProjectConfig.configPlugin/configCron4jPlugin` L48-L77；任务范例 `JBoltOnlineUserClearTask.java` L1-L26
- 平台用 **cron4j**（非 Quartz）：任务类实现 `it.sauronsoftware.cron4j.ITask`（run()+stop()），run() 内用 `Aop.get(XxxService.class)` 取 Service，日志用 `Log.getLog("JBoltCron4jLog")`（落 logs/jboltcron4j/）
- cron 表达式为 **5 段**（分 时 日 月 周），如 `"0-59/1 * * * *"` 每分钟一次——与 Quartz 6/7 段不同，不支持秒级
- 新增任务入口：`ExtendProjectConfig.configCron4jPlugin(me, cron4jPlugin)` 内 `cron4jPlugin.addTask("表达式", new XxxTask())`，不改 ProjectConfig 本体；无后台管理 UI，改任务需重启生效
- 陷阱：① 框架不捕获 run() 异常，抛出会停止任务且无告警——**必须全方法 try-catch**；② 任务线程无用户上下文，`JBoltUserKit.getUserId()` 返 null，需显式传参；③ 同步执行阻塞调度器，长任务自行异步；④ 禁止另起 Timer/ScheduledExecutor
- **siargo 可复用点**：定时任务一律走 ExtendProjectConfig 扩展点；任务内写库同样遵守 Db.tx()，重要任务建议落执行记录表便于审计

### 131. Excel 导入导出前端全链路（R24，补充 §46）

- **适用场景**：siargo 列表页新增导入/导出功能
- **源码参考**：`_table_portal.html` L45-L60（导出按钮三模式）；`_import_excel.html` L1-L23（导入弹窗全文）；`controller_common_template.jf` L252-L345；`service_common_template.jf` L299-L413
- 导出按钮三模式（均 `data-downloadbtn` + `data-url`，DownloadUtil 拦截 POST）：① 按查询结果加 `data-form="表单id"`自动带搜索参数；② 按选中行加 `data-usecheckedids="true"`自动带 JBoltTable 选中 ids；③ 全部导出仅 data-url
- 导出后端：`exportExcelByForm()` 复用 `getAdminDatas(同列表同参)` 取数据 → `service.exportExcel(list)` 链式 JBoltExcel.create().setSheets(JBoltExcelSheet.create().setHeaders(1, JBoltExcelHeader.create(col,label,15)...).setModelDatas(2,datas)) → `renderBytesToExcelXlsxFile(excel.setFileName("xx"))`；空数据先 renderJsonFail
- 导入弹窗四件套（_import_excel.html 范式）：模板下载按钮（data-downloadbtn → downloadTpl 端点 renderBytesToExcelXlsFile(service.getImportExcelTpl())）+ `j_upload_file_box`（data-name="file" data-accept="excel" data-maxsize="20480" data-confirm data-handler="uploadFile" data-url=导入端点 data-upload-success-handler="parent.refreshPjaxContainer();parent.layer.closeAll()"）
- 导入后端：Controller `importExcel()` 用 `JBoltUploadFolder.todayFolder(IMPORT_EXCEL_TEMP_FOLDER)` 接收 + `notExcel(file)` 拦截 → Service `JBoltExcel.from(file).setSheets(...setDataStartRow(2))` → `JBoltExcelUtil.readModels(excel,1,Model.class,errorMsg)` 逐行验证错误收集到 errorMsg → 失败返 fail(errorMsg) 前端直接展示；成功 tx 内 batchSave
- **siargo 可复用点**：平台模板用 `tx(IAtom)` 内嵌 Service——siargo 改为 Controller 层 Db.tx() + afterCommit；复杂多模板导入参考 qarep ExcelService 自定义解析

### 132. JBoltFile 上传组件与富文本上传通道（R24，补充 §93）

- **适用场景**：表单内附件/图片上传、富文本图片上传
- **源码参考**：`j_upload_file_box` 协议（_import_excel.html 范例）；`controller_common_template.jf` L478-L489（上传 action）；`NeditorUploadAdminController` / `SummernoteUploadAdminController`；`JBoltUploadFolder.java` 常量全集
- 前端声明式协议：`.j_upload_file_box` + data-name/data-btn-class/data-placeholder/data-accept(image|excel|video…)/data-maxsize(KB)/data-hiddeninput(存路径隐藏域)/data-filenameinput/data-confirm/data-upload-success-handler/data-upload-error-handler；FileUploadUtil/ImgUploadUtil.init() 自动扫描初始化
- 后端标准链：上传 action 取 `getFile("file", uploadPath)` → 图片 `notImage(file)` 拦截 → `jboltFileService.saveImageFile/saveAttachmentFile(file, uploadPath)` 返 Ret（data 含 jb_jbolt_file 记录 id/name/local_url/file_size/file_type）；前端回填 local_url 到 hidden input
- JBoltFileService 内置：UUID 重命名、MD5 去重、真实 MIME 识别、三层路径穿越检测；图片 URL 经 `JBoltRealUrlUtil.getImage(ret.get("data"))` 转真实地址
- 富文本通道：NEditor 图片走 `EDITOR_NEDITOR_IMAGE/日期目录`（含 catchImage 拓图拉取外链 HttpUtil.downloadFileFromUrl）；Summernote 走 `EDITOR_SUMMERNOTE_IMAGE`；端点均 @UnCheck 但限图片类型
- **siargo 可复用点**：简单附件直接用 JBoltFileService+jb_jbolt_file，不自建表；复杂生命周期（审核/版本/权限）才自建表参考 DmsFileService/EquipmentCertificateService；新上传目录常量继续加入 JBoltUploadFolder（已有 SIARGO_UPLOAD_* 惯例）

### 133. PRO 独有组件与版本谱系（R25）

- **适用场景**：判断某平台能力在 siargo 中是否可用、参考哪个版本源码
- **源码参考**：PRO `ExtendProjectConfig.java` L1-L198（含 SaaS 处理器族）vs BASE L1-L108（无 SaaS）
- PRO 独有包：`cn.jbolt.api`（API 应用开发中心）、`cn.jbolt.apitest`、`cn.jbolt.extend.gen`（代码生成扩展 9 子模块）、extend.cache/user/systemlog；BASE 独有 `cn.jbolt.mall` 商城
- 版本谱系：PRO 用 jboltcore 5.2.0 + Java 8；**siargo 用 jboltcore 5.3.6 + Java 17 + JFinal 5.2.7**，比 PRO 更新——查行为以 siargo 本地 jar/源码为准，PRO/BASE 源码仅作机制参考
- siargo 不含 PRO 独有业务模块（api/apitest/mall），但继承了 ExtendProjectConfig 的 SaaS 处理器空实现扩展点
- **siargo 可复用点**：新能力评估先查 siargo 本地是否已存在同名类（版本差异大），再参考 PRO/BASE 写法

### 134. SaaS 多租户与多数据源机制（R25）

- **适用场景**：未来需要接入第二数据源或租户隔离时
- **源码参考**：siargo `saas/saas_config.properties` L1-L23；`ExtendProjectConfig` L128-L196（数据源+SaaS 扩展点）；PRO `ProjectConfig.java` L315-L324/L430-L514
- SaaS 机制：`JBoltSaasTenantKit`（线程级租户上下文）+ 租户分表（@TableBind(separate=true)）+ 域名/header 解析租户；**siargo saas_enable=false 未启用**，处理器为空实现
- 多数据源：主源由 ProjectConfig.configMainDbPlugins 注册；第二源在 `ExtendProjectConfig.configExtendDbPlugins(DruidPlugin, ARP, Engine, configName, Setting)` 注册新 DruidPlugin + `new JBoltActiveRecordPlugin(dbPlugin, configName, idGenMode)`，每个 ARP 独立 sqlEngine
- Model 指定数据源：`@TableBind(dataSource="configName")`（默认 main）；临时切换 `Db.use("configName").find(sql)` / `model.use("configName")`
- 陷阱：**不支持跨库事务**——Tx/Db.tx 只在单个 ARP 内有效，跨库写操作需分库各自事务+手动补偿
- **siargo 可复用点**：当前单源（main），新增外部库只读接入走 configExtendDbPlugins，写操作尽量留在主库避开跨库事务

### 135. 报表/图表/PDF 能力盘点（R25，补充 §120）

- **适用场景**：siargo 新增统计图表、报表、PDF 导出需求选型
- **源码参考**：siargo `pom.xml`（ureport2-console 2.2.9 provided / itextpdf 5.5.13.4）；`ProjectConfig` L288-L382（JBoltUreportViewHandler 权限）；`dashboard.html` L198-L360（echarts 范例）
- 能力矩阵：① ureport2 可视化报表（需 applicationContext.xml 配 Spring bean，jbolt_ureport_enable 默认 false）；② hiprint 打印模板+PDF；③ itextpdf 动态 PDF（qarep PdfService 先例）；④ JBoltExcel；平台无 jfreechart/flying-saucer
- echarts 惯例（siargo dashboard/apicalllog 已有实践）：页面 `data-require-plugin="echarts"` 按需加载；init-handler 内 `echarts.getInstanceByDom(dom)` 检查已有实例先 dispose 再 init；ResizeObserver 监听容器变化调 chart.resize()
- 图表数据后端约定：Controller `renderJsonData(Kv列表/自定义结构)`，聚合在 Service 层完成（可配 30 分钟级缓存，参考 ApiCallLogService），禁止前端拉全量数据自行聚合
- 陷阱：echarts 实例不 dispose 会内存泄漏；ureport 依赖 Spring 容器配置；大数据集必须 Service 层聚合/分页
- **siargo 可复用点**：新图表直接照搬 dashboard.html 初始化模式；复杂报表优先 hiprint（已集成），ureport 需评估 Spring 配置成本后再启用

### 136. hutool 与 JSON 处理惯例（R26）
- **适用场景**：工具类选型、JSON 序列化、雪花 ID 精度保护
- **源码参考**：`siargo/pom.xml`（hutool 5.8.37 / fastjson 1.2.83）、`RemarkUtil` L18-19、`QareportService` L27、`CamelCaseUtil` L12
- hutool 高频类：`StrUtil`（isBlank/removeAllLineBreaks）、`FileUtil`、`DateUtil`、`IdUtil`、`MapUtil.toCamelCaseMap`、`NumberUtil`、`EscapeUtil.escapeHtml`（XSS 二次防护）、`HttpUtil`
- 选型分工：简单判空用 JFinal `StrKit.isBlank/notBlank`（框架代码惯例）；复杂字符串/文件/日期操作用 hutool——不重复造轮子
- JSON 统一 fastjson：`JBoltFastJson` 静态块全局注册 Long/BigDecimal → `ToStringSerializer`（Model 序列化自动防精度丢失）；Model getter 上仍显式加 `@JSONField(serializeUsing=ToStringSerializer.class)` 双保险
- **Record 不享受全局保护**——`Db.find()` 返回 Record 直出 JSON 时雪花 ID 必须 SQL 层 `CAST(id AS CHAR) AS id`（§93 纪律的根因）
- 禁忌：jackson 仅为 jwt 的传递依赖不主动使用；项目无 gson——新代码禁止混入其他 JSON 库
- **siargo 可复用点**：工具类调用前先查 hutool 是否已有实现；新增 JSON 输出场景先确认走 Model（有保护）还是 Record（需 CAST）

### 137. 异常处理与日志体系（R26）
- **适用场景**：错误页配置、日志分级落盘、Service 异常纪律、前端错误提示链
- **源码参考**：`ProjectConfig` L74-80、`Starter` L112-116、`siargo/src/main/resources/log4j2.xml` L1-320
- 全局错误页：`me.setError404View("/_view/_admin/common/msg/404.html")` + `setError500View(...)`（ProjectConfig.configConstant）；Undertow 层 Starter 另配错误页兜底
- 日志架构：slf4j 2.0.6 门面 + log4j2 2.23.1 实现（log4j-slf4j2-impl 桥接）；配置文件 `log4j2.xml`（root=DEBUG → Console + info/warn/error/debug.log 四级 RollingFile）
- 命名 logger 六件套（各落专用文件，**复用勿新建**）：`JBoltActionReportLog`、`JBoltAutoCacheLog`、`JBoltApiLog`、`JBoltControllerLog`、`JBoltWebsocketLog`、`JBoltCron4jLog`(INFO)；另 `druid.sql.Statement` → druid-sql.log（StringMatchFilter 排除 jb_online_user 噪音）
- Log 声明两模式：类级 `private static final Log LOG = Log.getLog(Xxx.class)`（QareportService 先例）vs 命名 `Log.getLog("JBoltCron4jLog")`（跨类聚合到同一文件）
- Service 异常纪律：无统一 try-catch；`Db.tx` 内抛异常自动回滚，但标准做法是**事务内 return false → 外层返回 fail()**（可控失败不用异常做流程控制）；Cron 任务 run() 必须全方法 try-catch（§130）
- 前端错误链：`renderJsonFail(msg)` → `data.state!="ok"` → `LayerMsgBox.alert(msg,2)` 红色提示；特殊 msg 约定：`jbolt_system_locked`→锁屏、`jbolt_nologin`→跳登录、`jbolt_terminal_offline`→强制下线；AJAX 默认超时 60s（jboltAjaxTimeout）
- 生产建议：root 级别 DEBUG 改 INFO（当前 debug.log 增长快）；第三方包（cn.hutool/org.xnio/io.undertow）已压制 error 勿动
- **siargo 可复用点**：新模块日志优先复用六件套命名 logger；业务失败走 fail() 返回而非抛异常

### 138. Undertow 部署与打包（R26）
- **适用场景**：本地启动、生产打包、配置文件优先级、JDK17 兼容
- **源码参考**：`siargo/src/main/resources/undertow.properties` L1-43、`siargo/package.xml` L1-96、`siargo.bat` L76-82、`siargo/pom.xml` L609-631
- undertow.properties 关键项：`undertow.devMode=true`（生产 false）、`host=0.0.0.0`、`port=80`、`hotSwapClassPrefix=cn.jbolt.core.`（热加载仅限此前缀包，模板/其他包改动需重启）、`gzip.enable=true minLength=1024`、`session.timeout=1800`（秒）、ssl.enable 生产可开 443 PKCS12
- 打包：maven-assembly + `package.xml` → tar.gz，产物结构 `config/`（resources）+ `webapp/`（排除未压缩源文件如 jbolt-admin.js）+ `lib/`（本工程 jar + 依赖）+ 启动脚本（755）
- **maven-jar-plugin 排除配置文件陷阱**：jar 内配置优先级**高于** config 目录——pom L609-631 已排除 *.properties/*.xml 等；新增配置文件类型必须同步加排除，否则部署改 config 目录不生效
- 启动脚本：`MAIN_CLASS=cn.jbolt.starter.Starter`，`CP=config;lib/*`；JDK17 需六项 `--add-opens`（java.base/java.lang 等）；`java -Xverify:none`；可 `-Dundertow.port` 临时覆盖端口
- 环境切换：`application.properties` 中 `pdev=pro` → 读 config-pro.properties（§参考 4.1）
- **siargo 可复用点**：打包用 /siargo_package 技能；新增配置文件时检查 maven-jar-plugin excludes；排查"改配置不生效"先想 jar 内旧配置覆盖

### 139. 平台安全体系汇总（R27）
- **适用场景**：XSS 白名单、登录安全、API 签名、敏感配置加密
- **源码参考**：`ExtendProjectConfig.configHandler` L92-98、`AdminIndexController` L279-296、`SiargoApiTokenUtil` L37-144、siargo `ProjectConfig.configDruidMonitor` L309-331
- XSS 分层：全局 `XssHandler` 自动过滤请求参数；富文本提交端点需 `xssHandler.unlimited("admin/xxx/save",...)` 白名单豁免（ExtendProjectConfig.configHandler 扩展点）；业务层对 Record 直出字段可 `EscapeUtil.escapeHtml4` 二次转义（QareportService L530-540 先例）；静态资源放行用 `baseHandler.unlimited("/assets/plugins/",...)`
- 登录安全：密码传输为前端随机2字符+md5(明文)+随机3字符；Cookie 三件套（JBOLT_SESSIONID_KEY / SESSIONID_REFRESH_TOKEN（JWT，时长=keepLoginSeconds+28800s）/ KEEPLOGIN_KEY）；Session+JWT 双模式，refreshToken 自动重登仅限超管
- API 签名（siargo 定制方案，新对外接口沿用）：`SHA256(密钥+订单号+YYYYMMDD)`；验证当天+前一天 token 解跨日边界；批量签名先 `Arrays.sort` 再逗号拼接；注意旧文档写 MD5 实际已是 SHA256
- 敏感配置：dbconfig `is_encrypted=true` 后框架连接前自动解密账号密码，加密凭证用 JBoltDatabaseEncryptGen 工具生成
- 权限层级：控制器级 `@CheckPermission` → 按钮级模板 `#permission(...)` → **无内置行级权限**，需 Service 手写条件过滤；Druid 监控页另有独立 IDruidStatViewAuth 实现（锁屏不让看/超管直通/否则查 DRUID_MONITOR 权限）
- **siargo 可复用点**：新富文本表单端点必须同步加 xssHandler 白名单，否则内容被转义；行级数据隔离在 Service 查询条件里实现

### 140. systemlog 操作审计体系（R27）
- **适用场景**：新模块接入操作日志、审计查询、登录/在线审计
- **源码参考**：`ProjectSystemLogTargetType` L10-43、`SystemLogService` L10-86、siargo `ProjectConfig` L536-539、`configCron4jPlugin` L258-263
- 接入三步：① `ProjectSystemLogTargetType` 枚举加一项（siargo 从 20001 起编，现有 QAREPORT/EQUIPMENT/…/EQUIPMENTCOMPARISON 八项；平台级 10001 起），静态块 `JBoltEnum.addToTvBeanMap` 注册；② Service 覆写 `systemLogTargetType()` 返回枚举 value（不需要日志则返 NONE）；③ 写操作后调 `addSaveSystemLog/addUpdateSystemLog/addDeleteSystemLog(id, userId, name)`（批量用 addBatchDeleteSystemLog(ids,...)，targetId 传 null 表批量）
- 日志为**同步写入**（事务内）；无归档机制——高频操作模块慎重开日志防表膨胀
- 日志详情可跳转：`SystemLogService.processSystemLogUrl(targetType, targetId)` 为自定义类型生成关联 URL（switch 枚举，hiprinttpl 先例）；targetType/type 翻译链：平台级枚举→项目级枚举→"未指定"
- 装配：ProjectConfig 覆写 `getProjectSystemLogProcessor()` 返回 ProjectSystemLogProcessor（与 SystemLogService 的翻译逻辑重复存在，两处同步改）
- 登录/在线审计：`jb_login_log`（IP/归属地/OS/浏览器/异地标记/成败状态）+ `jb_online_user`（session/锁屏/ONLINE·OFFLINE·TERMINAL_OFFLINE·FORCED_OFFLINE 四态/过期时间）；JBoltOnlineUserClearTask 每分钟清理；支持单端登录限制与强制下线
- **siargo 可复用点**：新模块上日志严格走三步法；枚举 value 续接 20007+；DmsCategoryService 返 NONE 是合法策略（字典类低价值操作不记）

### 141. devdoc 数据库文档模块（R27）
- **适用场景**：在线查看表结构文档、新成员熟悉库表
- **源码参考**：`JBoltDatabaseDevDocController` L1-93、siargo `ProjectConfig.configRoutes` L102（DevDocAdminRoutes 已挂载）
- 入口 `/admin/devdoc/database`；权限 `@CheckPermission(PermissionKey.APPLICATION)` + `@UnCheckIfSystemAdmin` + `@OnlySaasPlatform`
- 元数据来源：`JBoltDataSourceUtil.me.getAllTablesFromCache(datasource)`——基于 JDBC DatabaseMetaData（非 information_schema 直查），含列名/类型/主键/非空/默认值/注释；内存缓存，`refreshTables()` 手动刷新
- 多数据源：主源 `DbKit.MAIN_CONFIG_NAME` + `getExtendJBoltDataSources()` 全部展示；与代码生成器共享同一元数据通道（缓存独立）
- **siargo 可复用点**：表注释写全（建表时 COMMENT）即自动成为在线文档；新建表后到 devdoc 页 refreshTables 刷新缓存可验证结构

### 142. pjax 生命周期与布局分发细节（R28，补充 §28）
- **适用场景**：页面切换后 JS 不执行/残留、pjax 超时、后退异常排查
- **源码参考**：`jbolt-admin.js` L15142-L15319（initPjaxEvent/afterPjax）、`__jbolt_layout.html` L7-26、`__admin_layout.html` L88-206
- pjax 事件链：`pjax:start`（显示 loading+closeLayer）→ `pjax:success`（调 afterPjax 初始化 Select/FileUpload/FormDate 等全部组件 + 扫 data-require-plugin/data-init-handler）→ `pjax:end`；另有 error/timeout 处理；**超时固定 5000ms**（重页面首次加载慢会触发降级整页跳转）
- 清理钩子：`data-close-handler` 在 pjax 切走前执行——页面有 ResizeObserver/定时器/WebSocket command 时必须在此注销，否则旧页面 JS 残留（echarts 不 dispose 就是此类陷阱）
- 浏览器后退：`pjax:popstate` 且 direction=="back" 时若非初始 pjax 页则 `refreshPjaxContainer()` 重拉——后退不是 DOM 回滚而是重新请求
- 布局分发：`__jbolt_layout.html` 按 `_jb_rqtype_` 分流——PJAX 返 pjaxLayout（仅 #@css/@main/@jslib/@js 四段片），NORMAL 返 doAdminLayout 完整页；同一模板两种形态，所以页内 JS 必须写在 #define js() 内才能被 pjax 形态携带
- 主框架结构：`body[data-ispjax]` 标记请求形态；`.jbolt_admin.withtabs` 控制 tab 模式；`#jbolt-container` 主 pjax 容器（tab 模式下内含多个 `.jbolt_tabcontent`，各自 ajaxPortal 独立加载）
- **siargo 可复用点**：新页面带资源监听器时同步写 data-close-handler；排查"切页后功能失效"先确认 JS 在 #define js() 内且 init-handler 声明正确

### 143. 新增菜单/页面标准步骤（R28，补充 §31/§93）
- **适用场景**：新模块上线挂菜单、tab 模式行为确认
- **源码参考**：`_menu.html` L1-40、`jbolt-admin.js` L5082-L5995（JBoltTabUtil）
- tab 模式开关：`JBoltUserConfigCache.getCurrentUserJBoltAdminWithTabs()` 控制 `.withtabs` 类名——**用户级配置**非全局；siargo 默认 tab 模式；菜单 data-key 对应 tab key，点菜单自动开/激活 tab；双击 tab 或右键菜单可刷新（refreshJboltTab → refreshPjaxContainer 回调）
- 新增菜单五步（无需改前端代码）：① `jb_permission` 插记录（permission_key 唯一、is_menu=1、open_type=1、permission_level 由父+1）；② `PermissionKey.java` 加常量；③ 后台"权限资源管理"界面分配给角色（jb_role_permission）；④ 系统自动清角色组菜单缓存；⑤ 用户刷新即见——siargo 惯例走管理界面维护而非 SQL 直插
- 新增页面配套：Controller render("xxx.html") + 模板用 `#@jboltLayout()`（自动适配 pjax/直访双形态）+ jb_permission 配菜单行；open_type 选型：列表页=1(pjax)、外部系统/富交互页=2(iframe)、弹窗工具=3(dialog)、侧滑层=4(jboltlayer)
- 菜单渲染数据源：`JBoltPermissionCache.getCurrentUserMenus()`（角色组维度缓存）；选中态 `activeLeftNavByKey(key)` 自动高亮+展开父级
- **siargo 可复用点**：跨模块跳转用 `parent.openTab` 而非 window.location；新菜单上线清单化执行五步法，漏③会导致非超管看不到菜单

### 144. 前台 Web 站点与微信消息通道（R29）
- **适用场景**：判断对外能力应走哪条路由通道；接入公众号/小程序消息回调
- **源码参考**：siargo `cn/jbolt/index/WebRoutes.java` L1-13、`IndexController.java` L14-19；PRO `cn/jbolt/_admin/wechat/WechatMsgController.java` L21-196、`WechatRoutes.java`/`WechatApiRoutes.java`
- WebRoutes 仅挂 `add("/", IndexController.class)`，`setBaseViewPath("/_view/_web")` 但目录为空；IndexController.index() 加 `@Before(JBoltNoUrlPara.class)` 后直接 `forwardAction("/admin")`——**siargo 是纯后台系统，无前台页面**，前台无独立 layout/认证体系
- 对外通道三分法：① 需登录的后台功能 → AdminRoutes（`cn.jbolt.admin.siargo` 自动扫描）；② 无登录第三方 API → `cn.jbolt.admin.siargo.api` 包 + Token 签名（§139）；③ 微信消息回调 → WechatRoutes（`/wx/msg`）/ WechatApiRoutes（`/api/wechat`、`/api/wxa`）
- WechatMsgController 继承 jfinal-weixin SDK 的 `MsgControllerAdapter`，验签自动完成；按 `processInTextMsg`/`processInFollowEvent`/`processInMenuEvent` 等方法分发消息类型；约定 `renderNull()`（"rendernull"）表示不回复
- **siargo 可复用点**：新增对外接口先按三分法选通道，禁止在 WebRoutes 上加业务路由；若未来接公众号，直接扩展 WechatMsgController 的 process* 方法而非自建 servlet

### 145. Druid 监控与慢 SQL 排查（R29）
- **适用场景**：定位慢 SQL、N+1 查询、连接池异常；生产 SQL 性能巡检
- **源码参考**：siargo `cn/jbolt/base/JBoltDruidStatViewHandler.java` L1-150；`config.properties` L51-60；`log4j2.xml` druid.sql.Statement logger 段
- 监控入口 `/admin/druid/monitor`，由 JBoltDruidStatViewHandler 拦截 + IDruidStatViewAuth **四级校验**：user 存在 → enable → 非锁屏 → 超管直通否则需 `DRUID_MONITOR` 权限；无权限重定向锁屏页而非 403
- `jbolt_druid_dev_mode_full_sql_log=true` 仅开发环境开启全量 SQL 日志（输出 druid-sql.log，由 log4j2 命名 logger `druid.sql.Statement` 驱动，已用 StringMatchFilter 排除 jb_online_user 噪音）——**生产禁开**（日志量与性能开销大）
- 慢 SQL/N+1 排查流程：监控页「SQL 统计」看执行时间分布与执行次数 →「URI 统计」看单请求 JDBC 执行数异常（一次请求执行数十条同构 SQL 即 N+1）→ 结合 druid-sql.log 定位调用点
- **siargo 可复用点**：列表页上线前用 URI 统计自查 N+1；一对多聚合改 GROUP_CONCAT/EXISTS（规范 §6.7）而非循环查询

### 146. 后端缓存三层体系与选用原则（R29）
- **适用场景**：新增缓存时选择正确的层次与 API；避免缓存方案错位
- **源码参考**：siargo `config.properties` L51-60（`jbolt_cache_type=caffeine`、`jbolt_cache_name=jbolt_cache`）；PRO `cn/jbolt/_admin/cache/JBoltCodeGenCache.java` L29-36、`JBoltQiniuCache.java`、`CACHE.java`/`CacheExtend.java`
- 底层统一 API：`JBoltCacheKit.get(cacheName, key[, IDataLoader])` / `put` / `remove`；`jbolt_cache_type` 可切 caffeine/redis/ehcache/j2cache，业务代码不感知实现
- `@JBoltAutoCache` 注解（Model 级）：idCache 默认 true（findById 自动走缓存），keyCache+column/bindColumn 支持复合键；save/update/delete **自动失效**；User 等敏感 Model 自动 remove password；调试看 `jbolt_auto_cache_debug.log`
- 自定义 Cache 类标准形态（参考 JBoltCodeGenCache）：继承 `JBoltCache` + 静态 `me` 单例 + `getCacheTypeName()` + `JBoltCacheKit.get(JBoltConfig.JBOLT_CACHE_NAME, key, new IDataLoader(){...})` 懒加载；平台内置族：JBoltPermissionCache/JBoltUserConfigCache/JBoltDictionaryCache/JBoltUserCache/JBoltDeptCache/JBoltPostCache 等，业务侧统一经 `CACHE.me` 门面访问
- **选用原则四条**：① 平台基础数据（用户/部门/字典/权限）→ 直接用 `CACHE.me`，禁止重复缓存；② 业务配置小表 → Model 加 `@JBoltAutoCache`；③ 业务聚合统计（如 getFlowCounts）→ siargo 手写 volatile+DCL+TTL 模式；④ 多实例/分布式 → 切 `jbolt_cache_type=redis` 而非自引 Redis 客户端
- **siargo 可复用点**：缓存失效必须 afterCommit（规范 §6.3）；@JBoltAutoCache 的自动失效发生在 Model save/update/delete 内部，用 Db.update 原生 SQL 改表时**不会触发**，需手动 remove

---

## 四、siargo 推广优先级

| 优先级 | 机制 | 触发场景 |
|--------|------|---------|
| 高 | JBoltExcel 三件套（§1/§21） | 任何"导出/导入 Excel"需求 |
| 高 | 字典 options（§2） | 任何"可配置下拉选项"需求 |
| 高 | data-rule + _formjs（§22） | 新表单页（替代手写校验+AJAX） |
| 高 | 路由 scan 补行（§9 注意） | 新增业务模块包 |
| 高 | exists/existsName/existsSn 查重（§12） | 任何唯一性校验（禁手写 COUNT） |
| 高 | LayerMsgBox / JBoltNotifyBox（§29） | 任何前端提示/确认/通知 |
| 中 | 事件 + WebSocket 通知链路（§45/§46/§47） | 流程流转实时通知（post 必须 afterCommit） |
| 中 | JBoltDateRange（§7）+ betweenDateRange（§13） | 列表日期范围过滤 |
| 中 | data-switchbtn + toggleBoolean（§25c） | 启用/停用列 |
| 中 | 全局配置 globalconfig（§8） | 系统级可调参数 |
| 中 | ajaxportal 局部加载（§26） | 区块懒加载/局部刷新 |
| 中 | 缓存直读 JBoltUserCache 等（§19） | 列表显示人名/部门名 |
| 中 | 树构建 convertJsTree/convertToModelTree（§15） | 层级数据展示 |
| 中 | JBoltCache 缓存类模式（§35） | 平台级共享数据缓存 |
| 中 | 权限体系 #permission/@UnCheck（§33） | 新模块权限/按钮级控制 |
| 低 | Cron4j（§4）、JBoltFileService（§6）、hiprint 打印、JBoltLayer（§27）、挂菜单（§28） | 对应需求出现时 |
| 低 | 敏感词（§37）、UserConfig（§38）、OnlineUser 踢人（§34）、ExtendProjectConfig 扩展位（§36） | 对应需求出现时 |

> hiprint 打印：`PRO\src\main\java\cn\jbolt\_admin\hiprint\HiprintAdminController.java`——可视化打印模板设计器 + `tpl/content?sn=xxx` 取模板渲染打印，标签/单据打印需求用它。
