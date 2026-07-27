## 更新日志

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
