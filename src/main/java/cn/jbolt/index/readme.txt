系统入口 Controller 与平台路由定义，由 common/config/ProjectConfig.java 装配。
IndexController.java、WebRoutes.java：根路径入口，转入后台。
AdminIndexController.java：后台首页、登录、退出、锁屏、验证码与看板。
AdminRoutes.java：平台公共管理路由；PjaxErrorController.java：Pjax 错误页面。
JBoltCodeGenRoutes.java：按配置启用的代码生成器路由。
WechatAdminRoutes.java：微信后台管理路由。
WechatRoutes.java：公众号及小程序消息回调路由。
WechatApiRoutes.java：公众号 H5 与小程序 API 路由。
WechatTestRoutes.java：微信 H5 测试路由。
Siargo 业务子包由 ProjectConfig.java 显式扫描注册。
