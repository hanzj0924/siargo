# JBolt 原生机制手册

> 核对日期：2026-09-30；源码基线：Siargo v2.9.4 / `18c1de8`，JFinal 5.2.7、JBolt Core 5.3.6。依据当前仓库、依赖 API 和组件源码整理，未进行浏览器或服务运行验收。返回[文档导航](README.md)。

本篇说明如何找到和复用平台能力。业务流程以 09—17 专题为准；Controller、Service、Model、前端和数据库的完整开发步骤分别由 02—06 维护。平台支持某能力不代表 Siargo 业务已使用；示例代码、注释注册和可选依赖也不能视为运行事实。

## 一、后端基础

### Controller、Service 与标准操作

后台继承 `JBoltBaseController`，API 继承 `JBoltApiBaseController`，数据 Service 通常继承 `JBoltBaseService<M>`。核心实现位于 [jbolt_core.jar](../../lib/jbolt_core.jar)，当前项目中的调用方和生成器模板用于确认具体签名；不同平台参考版本的方法不可直接照搬。

| 需求 | 当前复用方式 | 约束 |
| --- | --- | --- |
| 后台页面与表单 | `render`、`set`、`getModel`、`getLong`、分页参数方法 | 传入名称须匹配表单前缀，Model 绑定不替代字段白名单 |
| 查询 JSON | `renderJsonData`、`renderJBoltTableJsonData` | 应按消费方区分普通数据与表格返回结构 |
| 操作结果 | `Ret.ok/fail`、Service `success/fail`、Controller JSON 渲染族 | 返回失败本身不是事务回滚信号 |
| 标准 CRUD | `dao()`、分页查询、`save/update/delete`、删除前引用检查 | 按业务实际补校验，不复制空引用检查 |
| 日志 | 保存/修改/删除系统日志方法，`systemLogTargetType()` | 记录真实动作和目标；敏感内容脱敏 |
| 注入与扩展 | `@Inject`、现有 Service 协作、`ExtendProjectConfig` | 不为每个辅助工具强行创建数据库 Service |

标准后台示例见 [CustomerAdminController](../../src/main/java/cn/jbolt/admin/siargo/customer/CustomerAdminController.java)、[CustomerService](../../src/main/java/cn/jbolt/admin/siargo/customer/CustomerService.java)。事务模板及返回形态由 [Controller](02-Controller层开发指南.md)和 [Service](03-Service层开发指南.md)指南集中说明。

### 参数、注解、枚举和 JSON

- `isOk` 对数值通常要求大于 0；允许零的计数、开关或位置参数要单独校验，不能把 0 当作统一空值。参数缺失、格式错误、业务不存在应分别处理。
- `@Path`、`@ActionKey` 确定入口；权限注解、`@Before` 与各类 `@UnCheck` 只影响所属机制，不存在一个可通用于所有认证层的“免校验”。HTTP 方法还受 Starter 的拒绝集合影响。
- `JBoltEnum` 将枚举加入平台管理器，必要时再注册 Enjoy 共享对象。实际例子见 [ProjectSystemLogTargetType](../../src/main/java/cn/jbolt/extend/systemlog/ProjectSystemLogTargetType.java)及 `ProjectConfig.configEngines()`。
- Model、Record、普通 Map/Kv 和 Fastjson 对象的序列化途径不同。Record 的实际列键、雪花 ID、BigDecimal 等处理见 [Model 指南](04-Model层开发指南.md)，不要推断所有类型均使用字符串序列化。
- `Ret` 用于业务操作结果，`Kv` 用于键值参数或模板数据；不能仅因二者都能保存键值就混用其成功失败语义。
- `JBoltUserKit` 依赖请求上下文。异步监听、定时任务需提前接收必要身份参数，不能在新线程假设当前用户仍存在。

### 扩展方法与错误观察

平台提供 Model 自动字段处理、缓存标记和 Service 回调。接入前需检查具体基类调用顺序；方法名包含 `after` 不代表数据库事务已经提交。默认 `Tx` 不根据返回的 `false` 或 `Ret.fail` 自动回滚，`Db.tx(IAtom)` 才处理 lambda 的布尔结果；当前 `ExtendProjectConfig.configTx()` 没有配置自定义处理函数。

[RenderFailLogHandler](../../src/main/java/cn/jbolt/common/handler/RenderFailLogHandler.java) 补充失败响应日志；[ProjectSystemLogProcessor](../../src/main/java/cn/jbolt/_admin/systemlog/ProjectSystemLogProcessor.java)负责系统日志扩展。框架操作日志、业务 API 调用日志和 Java 异常日志是不同来源。排查失败先看实际返回数据和调用链，不把“没有异常堆栈”解释为操作成功。

## 二、数据与缓存

### 字典、枚举和全局配置

字典适合管理员维护的选项；枚举适合代码约束的固定集合；全局配置适合已有平台机制管理的配置项。业务表中的型号参数、模板绑定等关联不能为了复用下拉框而全部改成字典。

| 入口 | 参数 / 用途 |
| --- | --- |
| `/admin/dictionary/options` | `key`，按类型键获取有效选项 |
| `/admin/dictionary/poptions` | `key`，根级选项 |
| `/admin/dictionary/soptions` | `key`、`pid`，父 ID 下选项 |
| `/admin/dictionary/soptionsByPsn` | `key`、`psn`，父 SN 下选项 |

以上来自 [DictionaryAdminController](../../src/main/java/cn/jbolt/_admin/dictionary/DictionaryAdminController.java)，底层配合 `DictionaryService` 和核心 `JBoltDictionaryCache`。这些 action 的 `@UnCheck` 不应解释为绕过全部路由登录控制。树字典还有类型、层级和父子约束，表格或树组件必须使用匹配的数据形态。

[GlobalConfigService](../../src/main/java/cn/jbolt/_admin/globalconfig/GlobalConfigService.java)和对应 Controller 管理平台全局配置。新增配置键要同步定义、读取与缓存失效；不能假设修改数据库后所有已加载静态字段马上更新。

### 查询、日期范围与树形数据

简单单表可复用基类查询，多条件查询可使用 `Sql`，复杂关联与统计使用参数化 SQL。SQL 条件、分页总数、聚合粒度和列序列化必须分别核实；不能把多表 JOIN 行数直接叫主单数量。分组分页、关键字过滤及排序白名单见 [Service 指南](03-Service层开发指南.md)。

日期范围需明确字段、时区和边界，业务月区间通常按起点含、下一边界不含实现；具体接口是否采用该规则必须看执行 SQL。平台日期工具与前端日期选择器不会替代后端范围验证。

平台 `getDateRange()` 返回 `JBoltDateRange`，可通过 `getStartDate(...)`、`getEndDate(...)` 指定缺省值，并用 `setDateRange(...)` 回显。当前实例见 [LoginLogAdminController](../../src/main/java/cn/jbolt/_admin/loginlog/LoginLogAdminController.java)：缺省为昨日到当前时间。其他页面应自行确定默认时间和查询边界，不照搬该业务默认值或未核实的重载签名。

树转换需要明确 ID、父 ID、文本和根节点约定。真实例子见 [DeptService](../../src/main/java/cn/jbolt/_admin/dept/DeptService.java)、字典管理及前端 JSTree。角色授权树的数据形态与普通业务目录不同，不直接复用勾选级联规则。

### 三类缓存

| 层次 | 入口 | 维护要点 |
| --- | --- | --- |
| 平台通用缓存 | `JBoltCacheKit`、[CACHE](../../src/main/java/cn/jbolt/common/util/CACHE.java) | 使用配置的缓存区域；键包含必要的业务维度 |
| 实体或专用缓存 | 核心缓存类、`@JBoltAutoCache`、项目自有 Cache 类 | 核对更新/删除失效链与缓存查询范围 |
| Service 字段缓存 | `volatile`、锁、时间戳、TTL | 独立于 Caffeine 区域，业务写后需清理相应字段 |

[CacheExtend](../../src/main/java/cn/jbolt/extend/cache/CacheExtend.java)提供扩展门面；注释内的缓存示例不等于已实现方法。`CustomBusinessCache` 可作为专用缓存形态参考，但该模块路由启用状态需另查。不得靠清一个平台缓存来声称业务字段缓存也已更新。

### Model 与生成

`@TableBind` 确定模型映射，基础模型由生成器维护，业务逻辑留在扩展层。实际表结构、生成模型和前端响应属于三个层面；命名可帮助生成器选择 UI，但不能替代真实数据类型与业务验证。[Model 指南](04-Model层开发指南.md)列出当前实体，[数据库指南](06-数据库设计规范.md)给出已核实结构与变更规则。

## 三、前端组件

当前入口为 [jbolt-admin.js](../../src/main/webapp/assets/js/jbolt-admin.js)、[JBoltTable](../../src/main/webapp/assets/plugins/jbolt-table/)及[_admin/common 布局](../../src/main/webapp/_view/_admin/common/)。`jbolt-admin.js` 自带的前端版本标记与后端 Core 版本不是同一个编号体系。

### 页面生命周期与数据流

页面可能以 pjax、普通页面、Dialog、iframe、单页或 JBoltLayer 载入。布局负责公共资源，局部 DOM 载入后仍需走平台组件初始化。`afterAjaxPortal`、插件动态加载、选择器和业务初始化的先后关系要按当前 `jbolt-admin.js` 核实；不要反复全页面初始化或对同一节点重复绑定事件。

业务脚本和样式归入 `siargo.js`、`siargo.css`，模板只保留结构与必要初始化。跨页面字段通过属性或初始化参数传入。服务端 Enjoy 与客户端表格模板分别转义，不能将 `${...}` 当成 Enjoy 的另一种输出形式。

### 常用组件定位

| 组件 | 接入与使用要点 |
| --- | --- |
| JBoltTable | `data-jbolttable`、数据 URL、行模板、主键及查询表单绑定；表头与数据单元格保持一致 |
| 分页、排序、条件与列筛选 | 前端参数必须与后端分页和排序协议相符；数据筛选后重新检查总数与选中行 |
| 行模板、右键菜单与选中事件 | 使用组件实际提供的行数据和回调上下文，不从固定列克隆 DOM 反推唯一业务行 |
| 固定列、树形表与展开 | 部分结构会克隆节点；避免重复 ID 和同一动作触发两次，按业务主键定位 |
| 可编辑表格 | 核对提交模式、新增/修改/删除数据集合及 `editableOption`，服务端验证每行所属主表 |
| 主从表 | `[masterId]` 等占位符需与实际选中主数据同步，未选主项时不提交孤立子项 |
| Dialog | 声明式 `data-dialogbtn` 或 `DialogUtil.openNewDialog`；明确 `handler`、确认回调和返回值归属 |
| JBoltLayer | 使用平台声明式侧栏配置；与 Dialog 生命周期分开，不拼一套不匹配的关闭逻辑 |
| ajaxportal / JBoltInput | 返回对应局部模板或数据，重载后按组件链初始化；与整页跳转区分 |
| AutoSelect / SelectUtil | 对齐选项值、文本、默认值和父子联动；动态加载完成后再设置回显 |
| Checkbox / Radio | 区分容器配置、单项值及集合返回；不能把空回显直接当主动清空 |
| JSTree | 区分目录浏览、单选与授权多选，明确节点 ID 和级联行为 |
| laydate / Autocomplete | 前端格式与接口格式一致；输入建议不替代最终值验证 |
| 表单校验 | `data-rule`、`FormChecker` 和 `_formjs.html` 形成前端链，后端独立验证 |
| Ajax 按钮与回调 | `data-ajaxbtn`、确认文案、`data-handler` 与实际响应一致；失败不刷新成成功态 |
| LayerMsgBox / JBoltNotifyBox | 按短提示、确认、加载或通知的实际用途选择；插入 HTML 前做上下文转义 |
| JBoltTabUtil / textarea | 使用平台页签和输入组件 API；页面关闭时清理所属事件和状态 |
| 图片/文件上传与编辑器 | 上传回执只是暂存结果；Neditor、Summernote、普通业务上传各有协议 |

属性和方法签名以当前组件实现及相近页面为准。可直接参考[字典页面](../../src/main/webapp/_view/_admin/dictionary/)、[产品参数页面](../../src/main/webapp/_view/admin/siargo/prodparam/)和[业务脚本](../../src/main/webapp/assets/js/siargo.js)。旧参考库的 demopage 属于页面骨架参考，当前项目不保证存在同名目录。

旧称 `jsonoption` 的表格配置能力在当前 [jbolt-table.js](../../src/main/webapp/assets/plugins/jbolt-table/jbolt-table.js) 中由 `data-option` 和 `initTableByJsonOption` 实现：属性值求值后必须是函数，函数接收 table 并返回配置对象，再设置列、行模板、URL、分页及工具栏等。不能直接把任意 JSON 字符串当作该属性的受支持输入；函数引用应来自可信业务脚本。

平台二次确认由 `jbolt-admin.js` 的 `processAjaxResultNeedConfirmOr` 处理：响应同时提供 `needConfirm`、`optUrl` 和 `reqType` 时提示确认，随后按 `GET`、`POST` 或 `DOWNLOAD` 再次请求；其他类型会提示不支持。确认后的后端请求仍须重新验证权限和业务状态。DMS 同名覆盖另有业务快照校验，不能仅用通用确认提示代替它。

### 常见组合的维护顺序

1. 新列表：先确认 Controller 响应字段，再定义表头、行模板、查询表单和权限按钮。
2. 新表单：确定提交协议、后端白名单与错误返回，再补前端校验、回显及成功回调。
3. 联动选择：先保持原绑定回显，再处理用户更改、选项重载与清空；保存前区分未加载和主动清空。
4. 弹窗返回：明确调用页面、唯一 DOM 范围、关闭行为和刷新对象。复杂内容使用项目已有 DialogUtil 方式，避免直接传离线节点造成仅遮罩显示。
5. 排序操作：复用 `jbolt_table_btn` 与原生上下箭头，保留原有 handler；展开箭头不等于排序按钮。

完整编码、转义、资源及组件案例见[前端指南](05-前端开发指南.md)。

## 四、文件与报表

### 平台附件与业务文件

平台 `JBoltFileService`、`JBoltUploadFolder`、编辑器上传用于框架附件通道；Siargo 本地业务采用 `SiargoStorage` / `SiargoUploadFiles` 管理业务根、临时文件、发布与补偿。两种机制共存，不应假设 DMS、IMI、证书和 PDF 都登记在统一附件表。

[NeditorUploadAdminController](../../src/main/java/cn/jbolt/common/controller/NeditorUploadAdminController.java)与 [SummernoteUploadAdminController](../../src/main/java/cn/jbolt/common/controller/SummernoteUploadAdminController.java)展示不同编辑器的上传响应形态。下载需沿实际受控文件定位与返回链处理，不能将用户传入 URL 任意拼接为磁盘路径。文件名、业务根、临时目录、补偿失败与提交后删除规则见[配置手册](08-项目配置速查手册.md)和 [Service 指南](03-Service层开发指南.md)。

### Excel、Word、PDF 和打印

| 能力 | 接入位置与状态 |
| --- | --- |
| JBoltExcel | 平台导入导出封装，参考当前生成器模板；适用一般表格读写，配置表头、列、数据起始行及转换规则 |
| 报告 Excel | 当前 `ExcelService` 使用 POI 解析指定业务格式并返回预填数据；不是通用 JBoltExcel 保存流程 |
| Word 模板 | `wordtpl` 及平台文档能力；模板字段和图片地址需按消费者验证，不作为报告 PDF 当前链路 |
| 报告 PDF | 当前 iTextPDF 加版号/系列模板规则，生成与数据库发布需分别成功；详见[导入输出专题](12-Excel导入与PDF输出.md) |
| hiprint | [HiprintAdminController](../../src/main/java/cn/jbolt/_admin/hiprint/HiprintAdminController.java)及模板库支持设计和内容读取；是平台打印入口，未据此认定报告业务使用它 |
| ureport | 源码保留 Servlet/Handler 集成，当前配置关闭；不能写成已运行报表服务 |
| 图表 | 首页数据口径来自业务 Service，图表组件仅负责呈现，参见[看板与统计](15-首页看板与报告统计.md) |

复用 Excel 导入时先确认输入列、必填值、转换、错误反馈、事务和重复处理，再决定写库时机。导出按钮、模板、数据构造和响应文件应作为一条链核对。框架的“导入完成”与业务的“保存完成”不是同一事实。

hiprint 的真实 action 包括 `tpl/designer`、`tpl/content`、`tpl/load`、`tpl/jsonDataEditor` 和 `tpl/submit`。后端提供模板和数据，打印客户端连接、浏览器打印及 PDF 导出是否可用仍需相应环境验证，不能由路由存在推断。

## 五、认证与消息

### 用户、角色、权限和应用身份

平台后台权限来自用户、角色、权限资源及相关缓存。菜单可见性、模板 `#permission` 和后端 `@CheckPermission` 各有作用；普通用户不因能看页面就拥有全部 action 权限。新增菜单需配合权限常量、资源记录、角色分配、缓存与前端导航，但文档编写不授权自动插入数据库记录。

平台部门、岗位、顶部导航、个性化配置和锁屏有各自模块，注册位置见 [AdminRoutes](../../src/main/java/cn/jbolt/index/AdminRoutes.java)。具体 Model 扩展与角色授权关系要看对应 Service，不能假设删除资料会级联撤销所有权限。

登录、重新登录、Cookie、在线用户和强制下线需一起理解：WebSocket 消息用于提示，HTTP 拦截补充阻断后续请求；当前 `SiargoTerminalOfflineInterceptor` 不应推广为所有路由组都已接入。详见[核心架构](01-JBolt平台核心架构.md)。

API 基类具有应用上下文和平台 API/JWT 能力，但当前订单接口另有 SHA-256 Token 协议。`@UnCheckJBoltApi` 不能简单解释为接口完全公开。应用中心现状、订单协议与新的接口统一管理规范见[接口专题](16-对外接口应用中心与调用日志.md)，不得套用另一项目的治理实现。

平台 JWT 身份链可从 [JBoltUserAuthApiController](../../src/main/java/cn/jbolt/api/common/controller/JBoltUserAuthApiController.java) 的 `/api/user/auth` 追踪：校验用户和启用状态后，调用 `JBoltApiKit.setApplyJwtUser(...)` 提交申请身份，配合 `@JBoltApplyJWT` 由框架处理签发。刷新入口为 [JBoltRefreshJwtApiController](../../src/main/java/cn/jbolt/api/common/controller/JBoltRefreshJwtApiController.java) 的 `/api/jwt/refresh`，调用 `JBoltApiJwtManger.refreshJwt(...)`。Token 载体、有效期与请求鉴权需继续按当前 Core 配置核实，不能从这两个入口推断固定刷新周期，也不能将该链套到订单接口的日期签名协议。

### 事件、待办和 WebSocket

```mermaid
flowchart LR
    A[业务操作] --> B[保存待办或通知]
    B --> C[确认事务提交边界]
    C --> D[EventKit.post]
    D --> E[JBoltEventListener]
    E --> F[WebSocket 系统命令]
    F --> G[前端通知与数据刷新]
```

图中提交边界是应满足的顺序；实际调用点是否满足必须沿外层事务核实，不能仅凭 `EventKit.post` 位于某个方法结尾断言安全。

[JBoltEventListener](../../src/main/java/cn/jbolt/_admin/event/JBoltEventListener.java)中，`SysNotice` 按全部用户、角色、部门、岗位或用户集合分发 `new_notice`；`Todo` 按目标用户分发 `new_todo`；在线用户事件发送 `user_forced_offline` 或 `user_terminal_offline`。这些是平台分发能力，报告业务通知范围由[报告专题](11-检验报告单业务.md)单独定义。

[TodoService](../../src/main/java/cn/jbolt/_admin/msgcenter/TodoService.java)和 [SysNoticeService](../../src/main/java/cn/jbolt/_admin/msgcenter/SysNoticeService.java)管理读取、已读、状态和删除。发通知前确认 Service 是否重写接收用户字段；不能因传入 userId 就假设已发给指定他人。业务主事务成功与消息送达分开观察。

WebSocket 服务端入口位于 [_admin/websocket](../../src/main/java/cn/jbolt/_admin/websocket/)，项目命令扩展位于 `extend/JBoltWebSocketExtendCommandHandler`。新增命令需成对检查后端路由、参数和前端消费；当前框架通知命令不可随意复用为具有不同语义的业务动作。

### 审计、敏感词与安全开关

系统日志记录操作，API 调用日志记录接口调用，登录日志记录认证活动。敏感词/XSS 配置开关不替代参数白名单、文件路径边界和 HTML 输出编码。主键字符串化解决前端整数精度，不提供权限保护。文档示例一律不包含真实凭据。

## 六、配置与扩展

### 注册和生成器

[ProjectConfig](../../src/main/java/cn/jbolt/common/config/ProjectConfig.java)承担项目装配，[ExtendProjectConfig](../../src/main/java/cn/jbolt/extend/config/ExtendProjectConfig.java)提供路由、数据库、模板、插件、Handler 和生命周期扩展。[ProjectCodeGenRoutesConfig](../../src/main/java/cn/jbolt/extend/config/ProjectCodeGenRoutesConfig.java)承接生成器路由；实际业务仍需核对显式扫描。

[ModelGenerator](../../src/main/java/cn/jbolt/extend/gen/ModelGenerator.java)、[MainLogicGenerator](../../src/main/java/cn/jbolt/extend/gen/MainLogicGenerator.java)及权限、角色 SN、字典键生成器是本地执行工具。生成目标、覆盖开关、包和表名都可能是某次任务配置，运行前必须重新确认，不把当前源码里的名单当作永久默认。BaseModel、业务 Model、Service、Controller、模板和 Cache 的生成职责应区分，生成代码不等于业务校验已经完成。

### 可选能力的当前边界

| 主题 | 当前证据与采用边界 |
| --- | --- |
| 调度 | 在线用户清理已注册每分钟执行；微信媒体下载注册被注释 |
| 微信与前台 Web | 路由类已注册；账号、回调、网络与业务可用性需单独配置验证 |
| SaaS 与多数据源 | 基类扩展及租户转换钩子存在；未据此确认 Siargo 已运行多租户部署或额外数据源 |
| 自定义业务表格 | 源码模块存在，但 `CustomTableRenderAdminRoutes` 注册被注释 |
| 七牛、Redis 管理 | 平台路由存在；当前业务上传目标为 local、主缓存为 Caffeine，第三方服务连通性未验证 |
| 开发数据库文档 | [DevDocAdminRoutes](../../src/main/java/cn/jbolt/admin/devdoc/database/DevDocAdminRoutes.java)注册 `/admin/devdoc/database`；与静态 Wiki 不同 |
| Druid / 服务器监控 | 平台监控入口有相应权限要求；监控能否访问以运行环境为准 |
| ureport / Sentinel | 当前开关关闭；不要将集成代码或依赖清单写成实际启用状态 |
| 基础版 / PRO 参考库 | 只用于查证示例及差异；实际 API、资源、布局和能力以本项目依赖为准 |

### 配置、部署与变更维护

环境文件、上传目录、日志、VM 参数、JDK 与发布布局见[配置手册](08-项目配置速查手册.md)。`.codex/` 是辅助资料区，不是运行依赖。框架启动可能调用自动初始化和升级入口，不能为了核实静态文档而自行启停服务。

新增业务能力先确定是否已有合适组件，再从当前项目的真实使用点验证参数、权限、数据与副作用。维护文档时记录适用版本和来源；旧源码绝对行号、历史轮次标签和“API 全集”称谓不作为正确性依据。

## 旧主题归并索引

下表只保留旧手册的主题编号作为迁移检索线索，内容已归入本篇及其链接的新指南；阅读和使用新版不需要旧目录。业务推广建议改为[业务模块地图](07-业务模块地图.md)中的场景选择，不继续将平台可选功能作为待实现需求。

| 新章节 | 旧主题编号 |
| --- | --- |
| 一 后端基础 | 9、10、11、12、14、16、19、40、44、54、60、78、79、81、82、83、84、85、86、87、112、118、119、136、137 |
| 二 数据与缓存 | 2、7、8、13、15、35、48、58、63、80、91、96、97、108、124、125、129、146 |
| 三 前端组件 | 20、22、23、24、25、26、27、28、29、30、31、38、43、56、57、62、65、66、67、68、69、70、71、72、73、74、75、76、77、98、99、100、101、102、103、104、105、106、107、111、123、126、128、142、143 |
| 四 文件与报表 | 1、6、17、21、41、59、61、88、94、115、116、120、131、132、135 |
| 五 认证与消息 | 3、5、18、32、33、34、37、45、46、47、52、53、89、92、93、95、109、110、121、122、127、139、140 |
| 六 配置与扩展 | 4、36、39、42、49、50、51、55、64、90、113、114、117、130、133、134、138、141、144、145 |
