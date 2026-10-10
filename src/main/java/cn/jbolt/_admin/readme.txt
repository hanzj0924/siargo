JBolt 后台公共管理模块，页面主要位于 _view/_admin。
user、role、permission、rolepermission：用户、角色、权限及角色授权。
dept、post：部门与岗位；userconfig：用户个性化配置。
dictionary、globalconfig：数据字典与全局配置。
systemlog、loginlog：操作日志与登录日志。
msgcenter：私信、系统通知与待办；onlineuser：在线用户管理。
jboltfile、qiniu：文件记录与七牛存储管理。
topnav：顶部导航；monitor、redis：服务器与 Redis 管理。
codegen：代码生成；customtablerender：自定义表格渲染示例。
hiprint：打印模板；sensitiveword：敏感词管理。
cache、event、websocket：公共缓存、事件监听与消息推送。
项目业务模块位于 cn.jbolt.admin，Siargo 业务位于其 siargo 子包。