矽翔质管部管理系统 基于Jbolt + mysql

## 公司官网
https://www.siargo.com.cn

## 源码授权说明
- 已获得jbolt授权，仅限本次二开

## 更新日志

### v2.3.0 (2026-04-23)
- feat(dashboard): Dashboard页面全面美化，统计卡片重构为清爽简约风格，图表卡片增加圆角阴影和标题装饰，甜甜圈图配色差异化
- style(qarep): 报告单模块多页面UI一致性美化（add/edit/details/editdes/index/inactiveList），表单分区卡片化，详情页CSS抽象重构
- style(apicalllog): API调用日志详情页和列表页UI美化，样式对齐qarep模块规范
- style(equipment): 设备记录页面微调优化

### v2.2.0 (2026-04-22)
- feat(apicalllog): 修复日期控件data-datepicker改为data-date，修复JBoltTable布局与分页（添加jb_vflex/jb_vbody/fill_box），优化Morris柱状图Y轴自适应取整与整数显示
- feat(pdf): PDFService增加safeStr空值安全处理，防止报告生成时字段空指针异常
- feat(equipment): 设备记录事件日期升级为datetime格式(yyyy-MM-dd HH:mm:ss)，描述列增加省略溢出样式，一键审核按钮增加权限控制(#if(audit))，隐藏Tab导航栏，调整弹窗尺寸
- feat(qarep): 质检报告精度/外观/包装检验按钮优化权限控制(#if(accuracy/appearance/packaging))
- 新增API管理模块

### v2.1.0 (2026-04-21)
- feat(equipment): 批量编制增加设备状态选择功能
- feat(equipment): 设备记录事件日期升级为日期时间格式（精确到秒）
- feat(permission): 设备管理和质量报告操作按钮增加权限控制
- fix(pdf): PDF报告生成增加空值安全处理（safeStr）
- style(equipment): 优化批量操作弹窗尺寸和记录描述列样式
- style(equipment): 隐藏设备分类Tab标签栏

### v2.0.0 (2026-04-20)
- 质量报告检验流程UI重构：使用流程步骤条替代审批进度条，新增流程操作按钮样式，添加Tab切换和流程数量统计
- 登录页增加 Client Hints API 获取平台版本及架构信息，精准识别操作系统
- 新增设备管理模块

### v1.9.1 (2026-04-17)
- 优化质量报告审批流程 UI，改为现代化步骤条导航
- 增强检验操作按钮样式与交互体验
- 新增流程阶段数量实时统计功能（支持定时刷新）
- 登录页面增加平台版本和 CPU 架构信息获取（支持 Windows 10/11 精准识别）

### v1.9.0 (2026-04-15)
- 修改报告单前端UI

### v1.8.1 (2026-04-14)
- 修复报告单表单验证逻辑及显示UI

### v1.8.0 (2026-04-14)
- 增加对外订单查询API
- 优化Excel导入逻辑

### v1.7.0 (2026-04-10)
- 增加报告单回收站、删除日志更新功能

### v1.6.3 (2026-04-09)
- 报告单修复部分注释和搜索条件

### v1.6.2 (2026-04-09)
- 修复报告单进度条显示bug

### v1.6.1 (2026-04-09)
- 修复报告单保存bug

### v1.6.0 (2026-04-09)
- 更新检验报告单进度条显示效果

