## 更新日志

### v2.8.4 (2026-08-04)
- feat(equipment): 批量编制/批量更改状态表单页增加设备列表数据回显——EquipmentAdminController 批量表单 action 查询设备列表传入页面，batchInspection.html/batchStatus.html 展示设备信息表格
- feat(equipment): 设备时间线分页增加类型筛选——paginateTimelineDatas 支持 type 参数过滤，timeline/index.html 增加类型筛选下拉
- style(dashboard): hero 卡片层次感全面增强——多层渐变背景、分层阴影系统、玻璃拟态状态栏、标题图标芯片感立体样式、统计卡片渐变光条与数字渐变、年份标签渐变边框胶囊
- style(dashboard): 进度条科技感重构——去除白色卡片容器改为透明融合设计，轨道增加凹陷立体感与刻度线，填充条多段渐变+发光效果，百分比数字纯绿发光
- style(dashboard): pipeline 托盘细节丰富——四段渐变背景、六层阴影系统、顶部/底部装饰线、左右侧装饰条、刻度线、中央光晕、四角金属铆钉
- style(dashboard): 环节卡片细节增强——三段渐变背景、顶部五段渐变发光色带、底部色带、背景纹理、图标容器三层渐变+光晕+反射、序号徽章增大发光、大数字渐变+反射线、标签色点发光+底部微光、送检只数渐变分隔线
- style(dashboard): 动画系统优化——入场动画增加 scale 缩放与更流畅 easing，新增箭头流光 db-flow-glow 与箭头脉冲 db-arrow-pulse 动画，扫描线增加透明度渐变

### v2.8.3 (2026-08-03)
- feat(dashboard): 首页看板流程统计改为报告单级本年度口径——QareportService 新增 getDashboardFlowCounts（当年创建报告单下全部有效产品按 insp 分环节，含各环节送检只数），带 30 分钟 DCL+TTL 缓存，clearFlowCountsCache 联动失效；hero 右卡由"在流程报告单"改为"已完成报告单"，与环形图口径一致
- feat(dashboard): hero 新增"本年度完成进度"进度条（已完成/本年度总量百分比，db-progress 样式 + 动效，prefers-reduced-motion 降级）
- fix(dashboard): 环形图统计口径修正——getDonutData 改为统计当年 insp=5（已完成）的有效产品数（COUNT(*) 不去重 + INNER JOIN），修复扇区合计与看板"已完成"总数不一致
- fix(dashboard): 退修趋势图去年对比线改灰色实线并固定 emphasis 样式，消除鼠标移入时线型/粗细跳动
- fix(qarep): 报告单列表页多标签页清理逻辑增加 data-page-id 判断，F5 后 JBolt 从 sessionStorage 恢复标签页时不再清空当前实例，修复报告单页面空白
- refactor(qarep): QarepConst 新增 REP_TYPE_NORMAL/REPAIR 报告单类型常量，退修月度统计 SQL 魔法数字替换为 QarepConst.REP_TYPE_REPAIR
- style(dashboard): siargo.css 新增 hero 网格/光晕背景、环节卡水印图标、db-flow-arrow 语义色渐变箭头等样式（+117/-50），同步 siargo.min.css

### v2.8.2 (2026-08-03)
- refactor(qarep): 报告单模块全栈审查修复——PDFService 输出目录/模板路径三层路径穿越校验（拒绝 `..`、强制相对路径、canonical 二次确认），生成/批量导出失败返回明确原因
- fix(qarep): PDFService 空值 NPE——订单号/报告单编号缺失时提前返回失败信息，不再进入模板渲染；sp_pdfstr 异常值跳过旧文件删除
- fix(qarep): 新增/删除报告单服务端校验——id 注入拦截、订单号/客户必填、批量删除必填删除原因
- fix(qarep): 报告单编号并发冲突自动重试（捕获 Duplicate 重试上限 3 次，FORMNUM_RETRY_MAX 常量化），消除并发创建偶发失败
- refactor(qarep): 永久删除物理文件移至事务提交后统一删除（getPdfPathsByIds 事务外收集 + deletePhysicalPdfs 带穿越二次检测），符合 afterCommit 纪律
- perf(qarep): 首页年度送检/检验总量统一 getTotalCount 30 分钟 DCL+TTL 缓存，clearFlowCountsCache 联动失效
- feat(qarep): 编辑报告单检验进度服务端支持——select 五个环节可选，update() 校验合法范围（1~5）并联动维护环节签名（前进 COALESCE 补签缺失环节/回退清空超出环节，条件更新防并发覆盖）
- improve(qarep): 首页流程计数改用 Ajax.get 平台对象族（自动处理登录失效/锁定/离线）；新增页删除跨窗口 DOM 注入 hack 改为表单顶部编号提示条；删除 flow-summary-count 死代码，siargo.css/.min.css 同步
- refactor(dms): 类别/文件 Controller 全部写操作 @Before(Tx.class) → 手动 Db.tx() + afterCommit（save/update/deleteByIds/toggleActive/up/down），删除未实现的 move 端点与重复端点 changeActive（统一 toggleActive）
- feat(dms): 文件编辑支持替换物理文件——新文件事务内落位、旧文件事务提交后删除（oldFilePath 带出），编辑表单上传时提示"保存后替换原文件"
- fix(dms): 上传安全加固——文件名净化 sanitizeFileName + validateTempFile 三层路径校验（拒绝 `..`、强制 temp 目录前缀、canonical 二次确认）；批量保存事务化，失败时数据库回滚且已移动文件移回临时目录（moveFilesBack 补偿）
- fix(dms): 文件/类别列表雪花 ID 统一 CAST AS CHAR 防前端精度丢失；关键字搜索改 EXISTS 子查询，GROUP_CONCAT 关键字聚合不再丢失
- feat(dms): 类别删除保护——类别下仍有有效文件时阻止删除（checkInUse 覆盖）；初始化排序按 sort_rank, id 稳定重排
- fix(dms): PDF 链接重复 id="pdfFrame" 改 class（多行渲染 id 冲突），加载统一时间戳防缓存
- chore: 清理 upload/equipment_certificate 目录 3 个测试图片

### v2.8.1 (2026-07-31)
- refactor(dashboard): 首页数据看板整体重构——顶部 hero 流程看板（精度→外观→包装→批准→完成五环节卡片，引用 --flow-* 共享色库）+ ECharts 四图表（送检总量柱状/季度同比堆叠柱/退修趋势双年折线/产品占比环图），替代原静态数字卡片
- feat(dashboard): 环节卡展示在制报告单数 + 送检只数（flowCounts 统计 SQL 并入 SUM(qsi)，复用 30 分钟缓存），hero 右侧展示在流程/待处理报告单双统计
- feat(dashboard): 新增季度送检同比图——QareportService.getQuarterCompareData() 单 SQL 查两年按季度×产品类型（传感器/小流量/大流量）分列统计，去年淡色/今年实色双堆叠柱，tooltip 逐类型及合计展示同比涨跌百分比（去年为 0 标「新增」）
- feat(dashboard): 退修总量折线图增加去年对比——getRepData() 重写为一次查两年返回 {curYear,lastYear,cur[12],last[12]}，今年红色实线+渐变面积、去年灰色虚线，ECharts 原生 legend 切换显隐
- improve(dashboard): 页面内容区增加竖向滚动（.db-scroll 仅本页生效），环节卡尺寸缩小、底色/边框加深提升识别度；图表 dispose 重建 + ResizeObserver 自适应，侧边栏折叠/全屏无变形
- improve(dashboard): 季度图 legend 强制不透明实色，与送检总量图产品三色（蓝/青/红）完全一致；同名系列合并 legend，点击联动切换两年同类型
- refactor(admin): AdminIndexController.dashboard() 移除旧 dashboard map 拼接逻辑，改为 flowCounts/totalQsi/quarterData 结构化数据直出
- style(dashboard): siargo.css 新增 Dashboard 独立分区（db-* 前缀样式组：hero/环节卡/图表卡/入场动画），同步更新 siargo.min.css
- chore(config): WinRAR 可执行文件路径外置为 winrar_exe_path 配置项（config/config-pro.properties），报告单批量导出 PDF 压缩前增加配置与程序存在性前置校验

### v2.8.0 (2026-07-31)
- refactor(qarep): 全部 Controller 写操作改用手动 `Db.tx()` + afterCommit 缓存清理模式，彻底消除事务内清缓存导致的并发脏读风险（替代原 `@Before(Tx.class)` 声明式事务）
- refactor(qarep): Service 写方法（save/update/permanentDelete）失败时返回 `Ret.fail()` 而非抛 RuntimeException，修复 try/catch 吞异常导致事务不回滚的潜在 bug
- refactor(qarep): `clearFlowCountsCache()` 统一收敛至 Controller afterCommit 节点，Service 内不再调用，消除职责分散问题
- feat(qarep): 新增审批工作台页面 approval.html（JBoltLayer 抽屉 iframe 加载），支持按 formnum 分组卡片、全选/单选、通过/驳回二态操作、驳回原因输入 + 两次点击确认
- feat(qarep): 新增产品驳回历史记录服务 ProductRejectLogService + 弹窗页面 reject_history.html（紫色时间线 UI）
- feat(qarep): 新增 QarepConst 常量类，消除全模块魔法数字（insp 1~5、角色 SN 211~214、产品类型等）
- feat(qarep): 列表页同 formnum 产品自动合并行（rowspan）+ 主表与固定列双向 hover 同步（基于 .jbolt_table_box 容器委托）
- feat(qarep): 流程计数 DCL 缓存（flowCounts 30min TTL + paginate 30s TTL）、Tab 懒加载 + 脏标记、visibilitychange 暂停刷新
- feat(qarep): 详情页检验流程图显示 per-stage 驳回角标（reject_count_2/3/4 > 0）+ 重检中/已驳回状态节点
- feat(qarep): 产品表新增 `reject_count` 冗余字段，列表/审批查询改读字段替代子查询，消除 N+1 性能问题
- improve(qarep): batchSoftDeleteProduct 改为返回 Ret，删除失败时可感知并回滚
- improve(qarep): 审批页跨 iframe 通知 `_qarepRefresh` 增加 try/catch 兜底，父页未注册时降级为关闭弹窗
- improve(qarep): notifyNextStageUsers 移至事务提交后执行，确保通知读到已提交数据
- fix(qarep): 详情页驳回显示消失——ProductService LEFT JOIN siargo_product_reject_log 取最新驳回记录 + 每环节独立计数
- fix(qarep): SQL CASE 缺少 ELSE 兜底导致未知环节返回 NULL，增加 `ELSE '未知环节'`
- style(qarep): 审批页全套 CSS 设计（卡片分组、勾选高亮、驳回输入框、动画箭头、主题色适配）
- style(qarep): 驳回历史时间线 CSS（紫色圆点+卡片+左侧线）、合并组视觉（accent 边框+悬停高亮+计数徽标）
- style(qarep): siargo.css qarep 分区大幅重组，新增 flow 按钮渐变色、搜索栏、回收站、驳回角标等样式组
- refactor(cme): 首页 index.html 重设计为学习门户介绍页——Hero 大标题 + 三学科介绍卡片（法规蓝/实务青绿/案例紫主题色，含核心知识点列表）+ 学习路径流程图（法规→实务→案例），唯一入口"进入学习"，移除原知识点/思维导图 6 个入口按钮
- feat(cme): 新增学习界面（/admin/siargo/cme/learn），左侧目录树按法规→实务→案例→补充知识→计算器说明书固定顺序展示（默认全折叠、根节点主题色、关键词搜索过滤自动展开祖先），右侧复用 embed 机制查看 PDF、img 查看 PNG，含空状态插画与面包屑路径
- feat(cme): CMEController 新增 listFiles 目录树 JSON 接口（递归扫描 pdf/png、目录在前文件在后按拼音排序、排除空目录）与 viewFile 动态文件流接口（canonicalPath 路径穿越校验加分隔符防同前缀绕过 + pdf/png 扩展名白名单）
- fix(cme): 重复点击"进入学习"新开第二个空白标签页，CTA 链接增加 data-key="cme_learn" 固定标识触发 JBolt 标签页去重，再次点击切换至已打开的学习页
- style(cme): siargo.css CME 分区整体重写——清除旧首页/思维导图/PDF viewer 废弃样式（含污染全局的裸 * 与 body 选择器），新增 cme-/cme-learn- 前缀样式组，learn.html 页内仅保留 JBolt 容器全局覆盖，同步更新 siargo.min.css
- chore(cme): 学习资料库重组为五大分类目录（法规/实务/案例/补充知识/计算器说明书，60+ 份 PDF/PNG），移除旧版三科合集 PDF 及 viewer 页面

### v2.7.16 (2026-07-28)
- refactor(dms): 类别管理页（category/index.html）表格内重设计：四列布局（序号/类别名称/文件数/操作）消除空白区域，删除勾选列，操作列改为行内编辑/删除/上移/下移/移动到（JBolt 裸图标风格），工具栏简化为新增类别+初始化排序+刷新+搜索
- feat(dms): 文件管理页左侧新增类别文件夹树导航，按类别 id 哈希着色文件夹图标，选中类别后右侧加载文件列表，未选择类别时右侧保持空白
- improve(dms): DmsFileService.paginateAdminDatas 类别 ID 为空时返回带分页参数的空页对象（原裸 new Page 会导致前端分页组件异常），类别过滤条件改为动态拼接
- improve(dms): DmsCategoryService.getCategoryListWithCount 增加 lastupdatetime（类别下文件最近上传时间），供文件页类别树展示
- style(dms): 类别页引入 siargo.css（原页面从未加载导致自定义样式全部失效），新增 dms-cat-* 样式组：960px 限宽居中、48px 行高、黄色描边文件夹图标、文件数药丸徽标
- style(global): 上移/下移全局 outline 边框按钮规则加 :not(.dms-cat-table) 排除类别页，类别页保持 JBolt 裸图标风格，supplier 等其他模块不受影响

### v2.7.15 (2026-07-24)
- feat(pdffolder): 新增报告单模板管理模块（/admin/siargo/qarep/pdffolder），支持版号与PDF模板规则CRUD，含首页/分页/新增编辑版号/复制删除版号/规则增删改
- feat(pdffolder): PdfRule 新增 error_hint 错误提示字段，规则编辑弹窗支持配置匹配失败时的提示信息
- fix(layout): supplier/customer/dms category 页面分页消失，改用 jb_vflex + jb_vbody + fill_box 弹性布局恢复分页组件显示
- improve(qarep): 报告单版号下拉框 data-value-attr 从 sn 改为 name，兼容字典新格式
- improve(qarep): 新增报告单页面版号下拉框增加模板配置快捷入口（齿轮按钮），可快速跳转 pdffolder 管理
- style(qarep): 新增/编辑报告单页面 input-group-append 改为 input-group-btn 统一样式

### v2.7.14 (2026-07-15)
- feat(qarep): Excel导入自动判定产品类型，根据型号通过ProductModelClassifier分类并映射到siargo_prod_type字典sn，前端上传后自动选择产品类型下拉框并触发change事件联动送检/检验数量
- feat(qarep): ProductModelClassifier新增MF3000S前缀映射为传感器类型(type=3)
- fix(qarep): 报告单详情页html/body背景色统一为#eef1f5，dt-wrap容器背景色同步调整

### v2.7.13 (2026-07-09)
- chore(changelog): CHANGELOG.md 从项目根目录移至 Controller 同包，改由 classpath getResourceAsStream 加载，消除文件系统路径依赖
- chore(build): Maven resources 配置新增 src/main/java 资源目录，确保 CHANGELOG.md 随构建复制至 target/classes
- chore(path): README 及 siargo_package SKILL.md 同步更新 CHANGELOG.md 引用路径

### v2.7.12 (2026-07-09)
- feat(changelog): 新增更新日志显示页面（/admin/changelog），读取 CHANGELOG.md 以 Markdown→HTML 渲染，权限 siargo_change_log
- feat(admin): ProjectConfig 新增 changelog 包扫描注册，确保 JFinal 路由自动发现
- chore(changelog): CHANGELOG.md v1.x 条目格式统一为 type(scope): 描述

### v2.7.11 (2026-07-09)
- improve(qarep): 已完成 tab 排序按 formnum→order_id→type→allq_time 全部倒序，最新产品优先
- fix(qarep): 详情弹窗竖向滚动条消失，添加 body overflow-y:auto 覆盖 layui iframe 的 overflow:hidden 裁剪导致内容超出 733px 固定高度被隐藏
- refactor(qarep): 首页引入 Enjoy 宏消除 6 个行模板+5 个搜索表单的重复代码（35%），文件从 1152 行减至 1006 行
- fix(qarep): 首页 loadFlowCounts 因 JBolt 双重渲染被调用 4 次，添加 window._qarepReady 全局守卫防重复初始化
- refactor(qarep): 首页 flow-stepper nth-of-type 改为 data-color 属性选择器，消除对 DOM 顺序的隐式依赖
- style(qarep): 首页清除所有内联样式提取至 siargo.css，dt-wrap 移除 min-height:100vh 避免 layui iframe 弹窗高度计算异常

### v2.7.10 (2026-07-08)
- refactor(css): 13 个页面内联 `<style>` 块集中提取至 siargo.css（CME 7页 / DMS 1页 / Equipment 5页），按模块分区组织减轻页面体积
- style(qarep): 新增审批流程步进器样式（5步主题色/连接线/选中态/过渡动效）、流程推进按钮渐变色、检验 badge 配色、行合并交替背景色、非活跃列表搜索栏样式

### v2.7.9 (2026-07-07)
- fix(qarep): 修改产品备注或生成 PDF 后对应 tab 分页缓存未同步刷新，数据展示与实际不一致

### v2.7.8 (2026-07-06)
- feat(api): 请求追踪 traceId 机制，响应体 JSON/X-Trace-Id 响应头/数据库独立字段三通道透传，便于日志串联排查
- feat(api): 新增 ApiErrorCode 错误码常量（1001-1007），统一 API 错误标识与客户端解析
- feat(api): 订单状态查询新增 found 字段区分"订单不存在"与"无检验记录"，单查/批量行为统一
- feat(apicalllog): 调用记录页新增 traceId 搜索列、路径超链接跳转详情页、修复搜索按钮无响应及筛选条件失效（responseStatus/日期范围）
- refactor(api): OrderStatusApiController 统一输出格式，ApiContext 消除冗余字段，批量查询 N+1 改为 IN 单次查询
- test(api): 新增 7 个多场景覆盖测试（检验进度/不存在订单/批量混合/超限），累计 21 个全通过
- chore(qarep): 检验进度 inspLabel 映射更新至 v6.7 版
- chore(api): siargo_api_call_log 表新增 trace_id VARCHAR(32) 字段及索引

### v2.7.7 (2026-07-06)
- fix(api): 外部订单查询 API 被 JBoltAdminAuthInterceptor 误拦截（/api/siargo/* 路径无登录态），路由扫描拆分为独立 Routes 排除 api 子包
- improve(qarep): PDF 报告生成兼容旧型号 MFXX-F-E/MFXX-F-D 参数映射，避免旧型号传感器数据缺失
- chore: io.undertow 日志级别调至 DEBUG 便于请求排查

### v2.7.6 (2026-07-06)
- chore: 启用 Undertow Access Log 访问日志配置，记录所有 HTTP 请求详情

### v2.7.5 (2026-07-03)
- feat(cache): 管理端分页数据缓存（30秒TTL），减少重复查询开销
- feat(ui): Tab 懒加载机制，首次切换时才加载表格数据，避免页面初始化时全部 tab 同时请求
- feat(auth): 终端下线自动检测与重新登录提示，全局 AJAX 响应拦截下线状态后跳转登录页

### v2.7.4 (2026-06-30)
- feat(qarep): Excel 双模板导入（104842+PFQVF81007 自动检测），解决不同客户模板格式差异导致的字段映射问题
- feat(equipment): 设备分类卡片动态化，分类配置化替代硬编码，支持无限扩展
- feat(permission): RoleService 新增 hasRoleOrAbove 层级角色权限校验方法
- fix(qarep): Excel 导入 qsi/qsis 键名不匹配导致数据无法填充，统一映射逻辑
- refactor(qarep): importExcel 统一入口 processExcelFile，消除双模板分支判断重复
- chore: pom.xml 新增 spring-core 依赖

### v2.7.3 (2026-06-27)
- refactor(permission): 报告单/设备权限覆盖逻辑重构，引入 hasRoleOrAbove 层级角色遍历替代硬编码角色 ID 比较
- feat(permission): 角色管理新增菜单权限与功能权限类型区分（jb_role.type 字段），支持菜单角色与操作角色分离
- chore: siargo.bat 添加 JDK --add-opens 模块开放启动参数，适配 JDK 16+ 反射限制

### v2.7.2 (2026-06-27)
- feat(qarep): 批量审批功能，支持精度/外观/包装/批准合格一键批量更新产品状态，替代逐个审批提升效率
- feat(qarep): 产品回收站功能，支持批量软删除与还原，删除原因必填
- feat(qarep): 进度统计缓存实时刷新，审批按钮点击后自动更新流程数量，无需手动刷新页面
- refactor(qarep): 报告单列表排序规则优化，按上一进度完成时间倒序排列
- feat(admin): 路由添加 JBoltAdminAuthInterceptor 统一鉴权拦截器
- chore(db): 数据库配置从 siargodev 切换至 siargo

### v2.7.0 (2026-05-25)
- feat(qarep): PDFService 新增 MFI 型号（插入式）报告生成支持，复用工业表模板避免重复代码
- fix(qarep): FD-D 型号 else if 阻断后续型号参数填充，改为独立 if 分支确保各型号互不干扰

### v2.6.0 (2026-05-25)
- feat(cme): 学习门户模块卡片新增思维导图入口按钮，法规/实务/案例三科各支持双入口（重要知识点 + 思维导图）
- style(cme): 思维导图按钮采用蓝/绿/紫三色主题区分，卡片布局改为 flex column 响应式
- chore: 删除 .qoder/commands/comment.md 命令文件

### v2.5.0 (2026-05-15)
- refactor(equipment): 移除独立的检校批次和设备记录管理页面，整合至时间线视图减少页面跳转
- feat(equipment): 新增设备状态"异常"(status=5)展示支持
- feat(equipment): 证书按钮增加证书日期显示
- fix(equipment): 修复设备ID未加引号导致的 JS 类型问题
- fix(equipment): 修复设备状态刷新时空值未处理的 JS 错误
- feat(cme): 新增计量资料学习模块（CME），支持法规/实务/案例三科分类学习

### v2.4.0 (2026-04-29)
- feat(equipment): 设备搜索支持规格型号模糊查询
- feat(equipment): 设备主页编制/审核列改由最新检校批次数据驱动，不再依赖静态字段
- feat(equipment): 新增"检校结果"列展示最新批次检定状态
- feat(equipment): 检校批次状态枚举值调整（不合格: 0→2）
- feat(equipment): 设备状态与检校批次状态双向联动，编制操作同步更新设备使用状态
- fix(equipment): 移除 siargo_equipment_record 表已删除字段的后端引用
- fix(equipment): 修复 SQL 子查询 Unknown column 'lr.auditor_id' 错误
- fix(equipment): 移除审核操作中设备状态前置校验，简化流程
- refactor(equipment): 统一所有 Tab 表格列宽配置
- fix(qarep): 修复质量报告单时间字段 ISO 格式显示问题
- style(qarep): 详情弹窗尺寸和样式优化

### v2.3.1 (2026-04-24)
- feat(qarep): QA 报告详情页检验进度从报告单级别下沉到每个产品独立展示，支持产品级检验人和时间显示
- feat(qarep): 批准 Tab 表格新增行合并功能，同一报告单的多个产品行自动合并显示，交替背景色区分
- refactor(qarep): 批准 Tab 表格列布局调整，查看PDF列移至末尾，固定列配置优化

### v2.3.0 (2026-04-23)
- feat(dashboard): Dashboard 页面全面美化，统计卡片重构为清爽简约风格，图表卡片增加圆角阴影和标题装饰，甜甜圈图配色差异化
- style(qarep): 报告单模块多页面 UI 一致性美化（add/edit/details/editdes/index/inactiveList），表单分区卡片化，详情页 CSS 抽象重构
- style(apicalllog): API 调用日志详情页和列表页 UI 美化，样式对齐 qarep 模块规范
- style(equipment): 设备记录页面微调优化

### v2.2.0 (2026-04-22)
- feat(apicalllog): 修复日期控件 data-datepicker 改为 data-date，修复 JBoltTable 布局与分页（添加 jb_vflex/jb_vbody/fill_box），优化 Morris 柱状图 Y 轴自适应取整与整数显示
- feat(pdf): PDFService 增加 safeStr 空值安全处理，防止报告生成时字段空指针异常
- feat(equipment): 设备记录事件日期升级为 datetime 格式（yyyy-MM-dd HH:mm:ss），描述列增加省略溢出样式，一键审核按钮增加权限控制（#if(audit)），隐藏 Tab 导航栏，调整弹窗尺寸
- feat(qarep): 质检报告精度/外观/包装检验按钮优化权限控制（#if(accuracy/appearance/packaging)）
- feat(api): 新增 API 调用日志管理模块，记录外部接口请求/响应详情

### v2.1.0 (2026-04-21)
- feat(equipment): 批量编制增加设备状态选择功能
- feat(equipment): 设备记录事件日期升级为日期时间格式（精确到秒）
- feat(permission): 设备管理和质量报告操作按钮增加权限控制
- fix(pdf): PDF 报告生成增加空值安全处理（safeStr），避免空字段导致 NPE
- style(equipment): 优化批量操作弹窗尺寸和记录描述列样式
- style(equipment): 隐藏设备分类 Tab 标签栏

### v2.0.0 (2026-04-20)
- refactor(qarep): 检验流程 UI 重构，流程步骤条替代审批进度条，新增流程操作按钮样式，添加 Tab 切换和流程数量实时统计，整体交互从单一进度条升级为分步操作流
- feat(login): 登录页使用 Client Hints API 获取平台版本及 CPU 架构信息，精准识别操作系统版本
- feat(equipment): 新增设备管理模块，支持设备分类、检校批次、维修记录及证书附件管理

### v1.9.1 (2026-04-17)
- refactor(qarep): 审批流程 UI 改为现代化步骤条导航
- style(qarep): 检验操作按钮样式与交互体验增强
- feat(qarep): 流程阶段数量实时统计，支持定时刷新
- feat(login): 登录页新增平台版本与 CPU 架构精准识别

### v1.9.0 (2026-04-15)
- style(qarep): 报告单前端 UI 优化

### v1.8.1 (2026-04-14)
- fix(qarep): 表单验证逻辑及显示 UI

### v1.8.0 (2026-04-14)
- feat(api): 新增对外订单查询接口
- refactor(qarep): Excel 导入逻辑优化

### v1.7.0 (2026-04-10)
- feat(qarep): 报告单回收站与删除日志功能

### v1.6.3 (2026-04-09)
- fix(qarep): 修复注释和搜索条件

### v1.6.2 (2026-04-09)
- fix(qarep): 进度条显示异常

### v1.6.1 (2026-04-09)
- fix(qarep): 保存功能异常

### v1.6.0 (2026-04-09)
- style(qarep): 进度条显示效果更新

### v1.0.0 (2026-01-01)
- siargo: 版本上线
