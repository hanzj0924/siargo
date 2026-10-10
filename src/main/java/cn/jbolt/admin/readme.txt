项目后台模块，与 cn.jbolt._admin 的平台公共管理模块配合使用。
appdevcenter：应用资料、凭据、启用状态及关联管理。
devdoc/database：数据源、表结构与字段开发文档。
wechat：公众号及小程序账号、配置、菜单、素材、回复与用户管理。
siargo：矽翔质量管理业务，页面位于 _view/admin/siargo。
siargo/customer、supplier：客户与供应商基础资料。
siargo/dms：技术通知单分类、检索、有效状态及文件管理。
siargo/equipment：设备档案、证书、检校对比、维修与审核。
siargo/imi：供应商来料图片、暂存上传与文件管理。
siargo/prodmodel：产品系列、目录分支、型号、资料与技术参数。
siargo/prodparam：参数类型与可选值字典。
siargo/qarep：检验报告、产品、多阶段审批、回收站及统计。
siargo/qarep/pdffolder：报告版号、PDF 模板文件与产品系列绑定。
siargo/qarep/uploadImport：Excel 导入、PDF 生成与归档。
siargo/api：订单检验查询 API；siargo/apicalllog：调用日志与统计。
siargo/cme：学习资料目录与文件浏览；siargo/changelog：版本说明展示。
路由在 common/config/ProjectConfig.java 及各模块 Routes 中注册。