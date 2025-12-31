_view/admin这个目录下，存放非JBolt核心自身维护模块的UI
admin自然是放后台管理类UI

例如，使用JBolt二开，增加了一个CRM模块，请在_view/admin下创建目录_crm
也就是_view/admin/sales
这个模块创建对应的路由
CrmAdminRoutes
里面配置baseViewPath
setBaseViewPath("/_view/admin/_crm");
