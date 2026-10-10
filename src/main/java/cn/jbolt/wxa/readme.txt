微信小程序 API 模块，路由由 index/WechatApiRoutes.java 注册。
api/JBoltWxaApiController.java：登录、JWT 刷新、用户与手机号解密、系统账号绑定及用户资料入口。
api/JBoltWxaApiService.java：code 换取会话、微信用户保存、账号绑定与解绑业务。
账号配置依赖 admin/wechat，认证及 JWT 处理由 cn.jbolt.core.api 提供。