# JBolt 平台核心架构

> 本文档系统梳理 JBolt 极速开发平台的架构体系，以及 siargo 项目在此基础上的具体实现。

---

## 一、JBolt 平台定位与历史

### 1.1 什么是 JBolt

JBolt 产品分为两部分：
- **JFinal 开发助手插件**（Eclipse 版 / IDEA 版）：帮助开发者在 IDE 中快速创建基于 JFinal 的 Maven 项目、一键打包发布、一键生成代码
- **JBolt 极速开发平台**：基于 JFinal 框架最佳实践的企业级项目开发平台，JFinal 学院推出

官网：http://jbolt.cn
视频教程：http://jfinalxueyuan.com/jiaocheng/jbolt

### 1.2 Pro 版（2022年迭代）

siargo 项目使用的是 **JBolt Pro 版本**，相比旧版的主要改进：
- 架构重新设计调整
- 修复和完善了之前版本的缺陷
- 支持 **SaaS 架构模式**的研发
- 支持**自动分表**、**租户自动找表**等特性
- 轻装上阵，更适合全新项目极速开发

---

## 二、核心技术栈

### 2.1 后端

| 组件 | 用途 | 说明 |
|------|------|------|
| JFinal 4.x | MVC 框架 | 国产轻量级 Java Web 框架 |
| Undertow | 嵌入式 Web 服务器 | 无需 Tomcat，内嵌启动 |
| ActiveRecord | ORM 框架 | JFinal 内置，操作数据库 |
| Druid | 数据库连接池 | 阿里开源，监控 SQL |
| Caffeine | 本地缓存 | 高性能，替代 Guava Cache |
| Cron4j | 定时任务调度 | 在线用户清理等定时任务 |
| JFinal Event | 事件驱动插件 | 异步事件处理 |
| Sentinel | 限流/熔断 | 阿里开源（可选开启） |

### 2.2 前端

| 组件 | 用途 |
|------|------|
| Enjoy | JFinal 内置模板引擎 |
| JBolt 前端组件库 | JBoltTable / Dialog / Layer 等 |
| Bootstrap 3.x | CSS 框架，响应式布局 |
| jQuery | JS 基础库 |
| Font Awesome | 图标库 |

### 2.3 缓存

- 默认使用 **Caffeine** 本地缓存
- 可通过 `jbolt_cache_type` 配置切换为：ehcache / redis / j2cache
- siargo 项目配置：`jbolt_cache_type = caffeine`

### 2.4 数据库

- MySQL 数据库
- 主键策略：**雪花算法（Snowflake）**，类型 `bigint`
- 配置文件：`dbconfig/mysql/config.properties`（账号密码加密存储）

---

## 三、项目分层架构

### 3.1 完整目录树

```
src/main/java/cn/jbolt/
├── starter/                  # 启动层
│   ├── Starter.java          # 项目启动入口
│   └── ProjectServer.java    # Undertow 服务器配置
├── common/
│   └── config/
│       └── ProjectConfig.java # 核心配置（路由、拦截器、插件）
├── base/                     # 二开扩展基础
│   ├── ProjectConfig.java    # (继承 JBoltProjectConfig)
│   └── ...
├── extend/                   # 扩展配置
│   ├── config/
│   │   ├── ExtendProjectConfig.java
│   │   └── ExtendProjectOfModule.java
│   └── systemlog/
│       └── ProjectSystemLogTargetType.java
├── _admin/                   # JBolt 内置系统管理
│   ├── permission/           # 权限管理
│   ├── role/                 # 角色管理
│   ├── user/                 # 用户管理
│   ├── dictionary/           # 数据字典
│   ├── globalconfig/         # 全局配置
│   ├── systemlog/            # 系统日志
│   ├── codegen/              # 代码生成器
│   └── ...
├── admin/                    # 业务模块
│   ├── siargo/               # ★ Siargo 业务模块
│   │   ├── api/              #   对外 API 接口
│   │   ├── apicalllog/       #   API 调用日志
│   │   ├── cme/              #   计量学习资料
│   │   ├── customer/         #   客户管理
│   │   ├── dms/              #   文档管理系统
│   │   ├── equipment/        #   设备管理
│   │   ├── imi/              #   IMI 模块
│   │   ├── qarep/            #   检验报告单
│   │   └── supplier/         #   供应商管理
│   ├── appdevcenter/         # API 应用开发中心
│   └── devdoc/               # 开发文档
├── siargo/
│   └── model/                # 数据模型层
│       ├── base/             # ★ 自动生成的 BaseModel（不可手动修改）
│       └── *.java            # 业务扩展 Model
├── index/                    # 首页/登录
├── wechat/                   # 微信模块
├── wxa/                      # 微信小程序
└── api/common/controller/    # 公共 API 控制器
```

### 3.2 视图层目录

```
src/main/webapp/_view/
├── _admin/                   # JBolt 内置页面
│   ├── common/               #   公共组件（layout/dialog/form）
│   ├── index/                #   后台首页
│   ├── permission/           #   权限管理页面
│   └── ...
├── admin/
│   └── siargo/               # ★ Siargo 业务页面
│       ├── customer/         #   index.html / add.html / edit.html / _form.html
│       ├── qarep/            #   index.html / add.html / details.html / edit.html / ...
│       ├── equipment/        #   index.html / add.html / edit.html / batchInspection.html / ...
│       ├── dms/              #   category/ + file/
│       ├── supplier/         #   index.html / add.html / edit.html / _form.html
│       ├── apicalllog/       #   index.html / detail.html
│       ├── imi/              #   index.html / add.html / edit.html
│       └── cme/              #   index.html + PDF 查看器
└── _common/h5/               # H5 公共页面
```

### 3.3 资源文件目录

```
src/main/resources/
├── application.properties    # 主配置文件（系统名称、环境切换）
├── config.properties         # 开发环境配置（60+ 配置项）
├── config-pro.properties     # 生产环境配置
├── dbconfig/mysql/
│   └── config.properties     # 数据库连接（加密存储）
├── caffeine/                 # Caffeine 缓存配置
├── redis/                    # Redis 配置（备用）
├── gentpl/                   # 代码生成器模板
├── exceltpl/                 # Excel 导入模板
├── wordtpl/                  # Word 导出模板
├── permission_init.json      # 权限初始化数据
├── dictionary_init.json      # 字典初始化数据
├── log4j2.xml                # 日志框架配置
├── undertow.properties       # Undertow 服务器配置
└── sentinel.properties       # Sentinel 限流配置
```

---

## 四、启动流程详解

### 4.1 启动入口

```java
// cn.jbolt.starter.Starter.java
public static void main(String[] args) {
    new Starter().run();
}

public void run() {
    // 1. 加载 JBolt 配置（读取 application.properties + config.properties）
    JBoltConfig.loadConfig();
    
    // 2. 创建 Undertow 服务器并配置
    ProjectServer.create(ProjectConfig.class, "undertow.properties")
        .configWeb(builder -> {
            configFilter(builder);       // Filter 配置（禁用 HTTP 方法）
            configServlet(builder);      // Servlet 配置（ureport）
            configListener(builder);     // Listener 配置
            configWebSocket(builder);    // WebSocket 端点注册
            configDefaultPage(builder);  // 404/500 默认页面
            configSentinel(builder);     // Sentinel 限流
        })
        .start();  // 3. 启动服务器
}
```

### 4.2 配置加载顺序

```
Starter.run()
  └── JBoltConfig.loadConfig()
  │     ├── 读取 application.properties（pdev=dev/pro）
  │     ├── 根据 pdev 加载 config.properties 或 config-pro.properties
  │     └── 初始化全局 JBoltConfig 静态属性
  └── ProjectServer.create(ProjectConfig.class)
        └── JFinal 启动
              ├── configConstants()    # 常量配置（404/500 页面）
              ├── configRoutes()       # 路由注册
              ├── configEngines()      # 模板引擎配置
              ├── configPlugins()      # 插件配置（Cron4j + Event）
              ├── configInterceptors() # 全局拦截器
              ├── configHandlers()     # 处理器配置（XSS/ureport/RenderFail）
              └── onStart()            # 启动后回调
                    ├── 自动初始化数据
                    ├── 自动升级操作
                    ├── 配置 Action Report
                    ├── 配置 AutoCacheLog
                    ├── 配置 Assets Version
                    └── 配置微信公众平台
```

---

## 五、ProjectConfig 核心配置详解

### 5.1 configConstants - 常量配置

```java
public void configConstants(Constants me) {
    me.setError404View("/_view/_admin/common/msg/404.html");
    me.setError500View("/_view/_admin/common/msg/500.html");
    ExtendProjectConfig.configConstant(me);  // 二开扩展
}
```

### 5.2 configRoutes - 路由注册（核心）

siargo 项目的路由通过 package 扫描方式注册，在 ProjectConfig 中集中配置：

```java
public void configRoutes(Routes me) {
    // 后台管理主路由
    me.add(new AdminRoutes());
    
    // ★ Siargo 业务路由（含权限拦截器）
    me.add(new Routes() {
        @Override
        public void config() {
            this.addInterceptor(new JBoltAdminAuthInterceptor());  // 登录校验
            this.scan("cn.jbolt.admin.siargo");  // 扫描 siargo 所有 Controller
        }
    });
    
    // ★ Siargo 对外 API 路由（无登录认证）
    me.add(new Routes() {
        @Override
        public void config() {
            this.setMappingSuperClass(true);
            this.scan("cn.jbolt.admin.siargo.api");  // 扫描 API Controller
        }
    });
    
    // 微信路由
    me.add(new WechatAdminRoutes());
    me.add(new WechatRoutes());
    me.add(new WechatApiRoutes());
    
    // 代码生成器路由（需 jbolt_code_gen_enable=true）
    if (JBoltConfig.JBOLT_CODE_GEN_ENABLE) {
        me.add(new JBoltCodeGenRoutes());
    }
    
    // 公共 Controller 扫描
    me.add(new Routes() {
        @Override
        public void config() {
            this.addInterceptor(new JBoltAdminAuthInterceptor());
            this.scan("cn.jbolt.common");
        }
    });
}
```

### 5.3 configEngines - 模板引擎配置

向 Enjoy 模板引擎注入共享对象，前端页面可直接访问：

```java
public void configEngines(Engine me) {
    me.addSharedObject("CACHE", CACHE.me);            // 缓存工具
    me.addSharedObject("PermissionKey", new PermissionKey()); // 权限常量
    me.addSharedObject("JBoltConfig", new JBoltConfig());    // 系统配置
    me.addSharedObject("JBoltUserKit", new JBoltUserKit());  // 用户工具
    me.addSharedObject("SessionKey", new SessionKey());      // Session 键
    
    // Layout 模板
    me.addSharedFunction("/_view/_admin/common/__admin_layout.html");      // 主布局
    me.addSharedFunction("/_view/_admin/common/__admin_dialog_layout.html"); // Dialog 布局
    me.addSharedFunction("/_view/_admin/common/__admin_iframe_layout.html"); // Iframe 布局
    me.addSharedFunction("/_view/_admin/common/__jbolt_layout.html");       // 自动识别布局
    me.addSharedFunction("/_view/_admin/common/__jboltassets.html");        // 公共组件
}
```

### 5.4 configPlugins - 插件配置

```java
public void configPlugins(Plugins me) {
    // Cron4j 定时任务：每分钟清理在线用户
    Cron4jPlugin cron4jPlugin = new Cron4jPlugin();
    cron4jPlugin.addTask("0-59/1 * * * *", new JBoltOnlineUserClearTask());
    me.add(cron4jPlugin);

    // JFinal Event 事件驱动：线程池异步处理
    EventPlugin eventPlugin = new EventPlugin();
    ExecutorService fixedThreadPool = new ThreadPoolExecutor(
        16, 32, 0L, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<>(1024));
    eventPlugin.threadPool(fixedThreadPool);
    eventPlugin.scanPackage("cn.jbolt._admin.event");
    me.add(eventPlugin);
}
```

### 5.5 onStart - 启动后处理

```java
public void onStart() {
    super.onStart();
    JBoltAutoInitData.me.exe();        // 自动初始化数据
    JBoltAutoUpgrade.me.exe();         // 自动升级操作
    JBoltConfig.configActionReportWriter();  // Action 报告输出
    JBoltConfig.configJBoltAutoCacheLog();  // 缓存日志
    JBoltConfig.configAssetsVersion();      // 静态资源版本号
    JBoltConfig.configWechat();             // 微信配置
    loadCodeGenTranslateUseClasses();       // 加载枚举和缓存
    ExtendProjectConfig.onStart();          // 二开扩展
}
```

---

## 六、配置管理体系

### 6.1 application.properties - 主配置

```properties
# 系统名称
system_name = 矽翔质管部管理系统
# 版权信息
system_copyright_company = ©Hanzj
system_copyright_link = https://www.siargo.com.cn/
# 环境切换：dev(开发) / pro(生产)
pdev = dev
# 演示模式
demo_mode = false
```

### 6.2 config.properties - 开发环境配置

```properties
# 开发模式
dev_mode = true
# 数据库类型
db_type = mysql
# 全局默认ID策略：雪花算法
global_default_id_gen_mode = snowflake
# 上传/下载路径
base_upload_path = upload
base_download_path = download
# 缓存类型（推荐 Caffeine）
jbolt_cache_type = caffeine
# WebSocket 开关
jbolt_websocket_enable = true
# ureport 报表开关
jbolt_ureport_enable = false
# Sentinel 限流开关
sentinel_enable = false
# 上传大小限制（100MB）
max_post_size = 102400
# 代码生成器开关
jbolt_code_gen_enable = true
# 上传策略（本地/七牛/阿里OSS）
jbolt_global_upload_to = local
# 代理类型
jbolt_proxy_type = jfinal_proxy
# 禁用的 HTTP 方法
disallowed_http_methods = TRACE,TRACK,PUT,DELETE,PATCH,HEAD,CONNECT
```

### 6.3 数据库连接配置

`dbconfig/mysql/config.properties`（加密存储）：
```properties
# 加密存储的数据库连接信息
is_encrypted = true
# 由 JBolt 框架处理加解密
```

### 6.4 环境切换

- 开发环境：`pdev=dev` → 加载 `config.properties`
- 生产环境：`pdev=pro` → 加载 `config-pro.properties`

---

## 七、关键类继承关系

### 7.1 Controller 继承链

```
JFinal Controller
  └── JBoltCommonController         # 参数获取、校验、render
        ├── JBoltBaseController     # 后台管理 Controller（renderJsonData 等）
        └── JBoltApiBaseController  # API Controller（JWT、App 认证等）
```

### 7.2 Service 继承链

```
JBoltCommonService                  # 公共校验、日志、条件拼接
  ├── JBoltBaseService<M>           # 绑定主 Model 的 CRUD 服务
  ├── JBoltBaseRecordService        # 操作 Record 的服务
  ├── JBoltBaseRecordTableSeparateService  # 分表 Record 服务
  └── JBoltApiBaseService           # API 服务基类
```

### 7.3 Model 继承链

```
JBoltBaseModel<M>                   # JFinal Model 增强（雪花ID、TableBind 等）
  └── BaseXxxModel<M>              # 自动生成的基础 Model（不可修改）
        └── XxxModel               # 业务扩展 Model（@TableBind 注解）
```

### 7.4 配置类继承链

```
JFinalConfig
  └── JBoltProjectConfig           # JBolt 项目配置基类
        └── ProjectConfig          # siargo 项目配置（src/.../base/）
              └── cn.jbolt.common.config.ProjectConfig  # 实际使用的配置类
```

---

## 八、Servlet / Filter / Listener 配置

### 8.1 Starter 中的 Web 配置

```java
public void run() {
    ProjectServer.create(ProjectConfig.class, "undertow.properties")
        .configWeb(builder -> {
            // 1. Filter：禁用 TRACE/TRACK/DELETE 等方法
            configFilter(builder);
            // 2. Servlet：ureport 报表（条件启用）
            if (JBoltConfig.JBOLT_UREPORT_ENABLE) {
                builder.addServlet("ureportServlet", "com.bstek.ureport.console.UReportServlet");
                builder.addServletMapping("ureportServlet", "/ureport/*");
            }
            // 3. Listener：Spring ContextLoaderListener（ureport 需要）
            if (JBoltConfig.JBOLT_UREPORT_ENABLE) {
                builder.addListener("org.springframework.web.context.ContextLoaderListener");
            }
            // 4. WebSocket 端点
            if (JBoltConfig.JBOLT_WEBSOCKET_ENABLE) {
                builder.addWebSocketEndpoint(JBoltWebSocketServerEndpoint.class);
            }
            // 5. 默认页面
            builder.add404ErrorPage("/_view/_admin/common/msg/undertow_404.html");
            builder.addErrorPage(500, "/_view/_admin/common/msg/undertow_500.html");
        })
        .start();
}
```

---

## 九、日志体系

### 9.1 日志框架

- **log4j2.xml** 为主配置文件
- **logging.properties** 为 JDK 日志配置（备用）

### 9.2 日志输出目录

```
logs/
├── info.log                     # 信息日志
├── debug.log                    # 调试日志
├── error.log                    # 错误日志
├── warn.log                     # 警告日志
├── druid-sql.log                # Druid SQL 监控日志
├── jbolt_api_debug.log          # API 调试日志
├── jbolt_api_error.log          # API 错误日志
├── jbolt_auto_cache_debug.log   # 自动缓存日志
├── jbolt_controller_debug.log   # Controller 调试日志
├── jbolt_controller_error.log   # Controller 错误日志
├── jfinal_action_report.log     # JFinal Action 报告
├── jboltcron4j/                 # 定时任务日志
└── jboltwebsocket/              # WebSocket 日志
```

---

## 十、总结

siargo 项目基于 JBolt Pro 平台构建，核心架构特点：

1. **纯 JFinal 生态**：无 Spring/Spring Boot 依赖，简洁轻量
2. **嵌入式部署**：Undertow 内嵌启动，无需外部 Tomcat
3. **约定优于配置**：@Path 注解 + 包扫描，快速注册路由
4. **SaaS 就绪**：支持多租户、自动分表
5. **前后端一体**：Enjoy 模板引擎 + JBolt 前端组件，无需分离部署
6. **高性能缓存**：Caffeine 本地缓存，双重检查锁模式
