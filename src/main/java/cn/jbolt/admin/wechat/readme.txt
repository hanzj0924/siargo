微信公众号及小程序后台管理，按账号分别维护配置和内容。
mpinfo：账号资料、启用状态及应用关联。
config：基本配置、支付配置与运行配置加载。
article：图文内容模型服务；media：微信素材同步、下载与回复素材选择。
menu：公众号自定义菜单。
autoreply：关注、默认及关键词回复，处理公众号和小程序消息回调。
user：微信用户资料同步、启停及账号绑定。
后台路由位于 index/WechatAdminRoutes.java，模板位于 _view/_admin/_wechat。
消息回调由 index/WechatRoutes.java 注册；小程序 API 位于 cn.jbolt.wxa.api。
