# Siargo · 矽翔质管部管理系统

Siargo 是面向矽翔质管部的业务管理系统，覆盖客户与供应商、产品型号与参数、检验报告、设备、技术通知单和学习资料等日常工作。项目基于 JFinal 与 JBolt 开发，使用 MySQL 保存业务数据，通过 Enjoy 模板与 JBolt 前端组件提供管理页面。


- 代码仓库：[hanzj0924/siargo](https://github.com/hanzj0924/siargo)
- 公司官网：[矽翔](https://www.siargo.com.cn)
- 更新日志：[CHANGELOG.md](src/main/webapp/_view/admin/siargo/changelog/CHANGELOG.md)

## 主要功能

| 模块 | 内容 | 后端业务目录 |
| --- | --- | --- |
| 客户与供应商 | 客户、供应商基础资料维护 | `customer`、`supplier` |
| 产品型号与参数 | 产品系列、型号、选型参数及资料管理 | `prodmodel`、`prodparam` |
| 检验报告 | Excel 导入、精度/成品检漏/外观/包装检验、批准放行、PDF 生成、归档与回收站 | `qarep` |
| 设备管理 | 设备台账、审核、维修、比对及证书管理 | `equipment` |
| 技术通知单 | 分类、文件上传、检索与回收管理 | `dms` |
| 来料到货单 | 到货图片及相关资料管理 | `imi` |
| 注册计量师学习 | 学习资料目录与文档浏览 | `cme` |
| 对外接口与日志 | 订单状态等接口及调用日志查询 | `api`、`apicalllog` |
| 更新日志 | 系统版本说明展示 | `changelog` |

业务目录均位于 `src/main/java/cn/jbolt/admin/siargo/`。用户、角色、权限、系统设置等基础能力由 JBolt 平台模块提供。报告单的成品检漏环节按产品属性决定是否经过。

## 技术栈

| 层次 | 当前配置 |
| --- | --- |
| Java 与构建 | JDK 25、Maven、UTF-8 源码 |
| 后端框架 | JFinal 5.2.7、JBolt Core 5.3.6 |
| Web 服务 | JFinal Undertow 3.8、Undertow 2.2.37.Final |
| 数据访问 | MySQL、JFinal ActiveRecord、Druid |
| 页面与组件 | Enjoy、JBoltTable、jQuery、Bootstrap 4.6.2 |
| 缓存与任务 | Caffeine 2.9.3、Cron4j、JFinal Event |
| 文件与工具 | Apache POI、iTextPDF、Fastjson、Hutool |

依赖版本以 [pom.xml](pom.xml) 为准。JBolt 核心依赖由项目内 `lib/jbolt_core.jar` 提供，导入或构建项目时需要保留该文件。项目版本同时维护在 `pom.xml` 与 `ProjectServer.getProjectVersion()` 中，正式发布时保持一致。

## 目录结构

```text
siargo/
├── README.md                         # 项目说明
├── AGENTS.md                         # 项目开发与协作约定
├── pom.xml                           # Maven 依赖和构建配置
├── package.xml                       # 发布目录及归档内容配置
├── siargo-package.md                 # 正式发布流程
├── siargo.bat                        # Windows 发布目录启动脚本
├── siargo.sh                         # Linux 发布目录管理脚本
├── lib/
│   └── jbolt_core.jar                # JBolt 授权核心库
├── src/main/
│   ├── java/cn/jbolt/
│   │   ├── starter/                  # Starter 入口与 ProjectServer
│   │   ├── _admin/                   # 平台后台模块
│   │   ├── admin/siargo/             # Siargo 业务 Controller、Service
│   │   ├── common/                   # 配置、拦截器、文件存储等公共能力
│   │   ├── extend/                   # 扩展配置、缓存、代码生成和日志
│   │   ├── siargo/model/             # 业务 Model
│   │   │   └── base/                 # 自动生成的基础 Model
│   │   ├── api/                      # 平台 API 配套
│   │   └── wechat/、wxa/             # 微信相关模块
│   ├── resources/
│   │   ├── application.properties    # 环境选择及全局配置
│   │   ├── config.properties         # 开发环境配置
│   │   ├── config-pro.properties     # 生产环境配置
│   │   ├── undertow.properties       # 监听地址、端口、会话和 HTTPS
│   │   ├── dbconfig/mysql/           # MySQL 连接及模型生成配置
│   │   ├── caffeine/                 # 缓存配置
│   │   └── exceltpl/、wordtpl/        # 文档模板资源
│   └── webapp/
│       ├── _view/_admin/             # 平台页面模板
│       ├── _view/admin/siargo/        # 业务页面模板
│       ├── assets/js/siargo.js        # 业务 JavaScript
│       ├── assets/css/siargo.css      # 业务样式
│       ├── assets/plugins/           # 第三方前端组件
│       ├── upload/                   # 上传文件与业务资料
│       └── export/                   # 报告等导出文件
├── wiki/qdoer/                       # 架构、开发及配置手册
├── .codex/                           # AI 工作过程文件，不参与发布
├── logs/                             # 运行日志
└── target/                           # 编译与发布产物
```

业务调用通常采用 Controller → Service → Model 分层。业务路由在 `common/config/ProjectConfig.java` 中按模块显式扫描；扩展配置入口为 `extend/config/ExtendProjectConfig.java`。`siargo/model/base/` 由代码生成器维护，不手动修改。

## 基础配置

### 环境选择

配置文件位于 `src/main/resources/`。`application.properties` 的 `pdev` 选择环境；当前仓库配置为：

```properties
pdev=dev
```

| 环境 | 主配置 | MySQL 配置 |
| --- | --- | --- |
| `pdev=dev` | `config.properties` | `dbconfig/mysql/config.properties` |
| `pdev=pro` | `config-pro.properties` | `dbconfig/mysql/config-pro.properties` |

主配置中的 `db_type=mysql` 决定使用 `dbconfig/mysql/`。常用配置如下：

| 配置项 | 用途 |
| --- | --- |
| `dev_mode` | JFinal 开发模式；当前开发配置为 `true`，生产为 `false` |
| `engine_dev_mode` | Enjoy 模板开发模式；当前开发配置为 `true`，生产为 `false` |
| `jbolt_cache_type` | 缓存实现，当前为 `caffeine` |
| `jbolt_global_upload_to` | 全局上传方式，当前为 `local` |
| `jbolt_code_gen_enable` | 代码生成器开关；当前开发配置开启，生产关闭 |

### 数据库

按当前环境编辑对应的 `dbconfig/mysql/config*.properties`：

| 配置项 | 用途 |
| --- | --- |
| `db_name`、`db_schema` | 数据库名称及 Schema 配置 |
| `jdbc_url` | JDBC 连接地址与连接参数 |
| `user`、`password` | 数据库访问凭据 |
| `is_encrypted` | 配置加密标志，须与账号密码的实际保存形式匹配 |
| `id_gen_mode` | 主键生成方式 |
| `model_package` | 生成 Model 时使用的 Java 包 |

使用与当前业务代码匹配的数据库结构和数据，实际连接凭据按部署环境配置。本说明不包含账号密码或加密串。`src/main/resources/sql/` 仅为历史备份目录，不作为当前表结构或自动初始化依据。

### Web 服务

`undertow.properties` 当前的主要监听配置为：

```properties
undertow.host=0.0.0.0
undertow.port=80
undertow.devMode=true
undertow.session.timeout=1800
undertow.ssl.enable=false
```

默认后台入口为 `http://127.0.0.1/admin`。`0.0.0.0` 表示监听所有网卡；对外访问使用服务器实际 IP 或域名。部署时同时核对 Undertow 开发模式、端口与 HTTPS 设置，不能只调整 `pdev`。

若与 Siargo AI 或其他占用 80 端口的服务同时运行，可在 IDE 的 VM options 中为本项目指定独立端口，例如 `-Dundertow.port=8088`，对应入口为 `http://127.0.0.1:8088/admin`。

### 业务文件目录

以下配置位于 `config.properties` 和 `config-pro.properties`，均相对于 Web 根目录：

| 配置项 | 当前相对路径 | 用途 |
| --- | --- | --- |
| `siargo_imi_upload_path` | `upload/siargo/imi` | 来料到货图片 |
| `siargo_dms_upload_path` | `upload/siargo/dms` | 技术通知单附件 |
| `siargo_eqcert_upload_path` | `upload/siargo/eqcert` | 设备证书 |
| `siargo_prod_model_upload_path` | `upload/siargo/prod_model` | 产品型号资料 |
| `siargo_qarep_export_path` | `export/siargo/qarep` | 检验报告导出文件 |

开发时 Web 根通常为 `src/main/webapp/`，发布后为发布目录中的 `webapp/`。业务路径使用 `/` 分隔的相对路径，不填写 Windows 盘符绝对路径；不同业务目录不能相同或互相包含。报告模板另位于 `upload/siargo/qarep/templates/`。

数据库备份不包含这些文件。迁移或部署时需分别保留附件、模板和报告，保持数据库记录的相对路径，并确保运行账号拥有所需读写权限。

### 前端资源

业务脚本统一维护在 `assets/js/siargo.js`，业务样式统一维护在 `assets/css/siargo.css`；业务模板位于 `_view/admin/siargo/`。

`application.properties` 的 `project_assets.environment_files` 指定参与环境切换的 12 项 CSS/JS。模板通过 `ProjectAssets.url(...)` 选择实际文件：开发优先使用源文件，生产优先使用 `.min` 文件，首选文件缺失时使用存在的另一版本。日常修改源文件即可，正式打包时按发布流程增量压缩。

## 本地开发与启动

1. 准备 JDK 25 和 Maven，使用 IDE 导入根目录 `pom.xml`，确认 `lib/jbolt_core.jar` 存在且依赖加载完成。
2. 核对开发环境数据库、业务文件及 Web 端口配置，确保目标数据库可访问。
3. 创建 Java Application 运行配置，主类为 `cn.jbolt.starter.Starter`，工作目录为项目根目录 `D:\Workspace\siargo`。
4. 在 VM options 中保留下列 JDK 模块开放参数，由使用者手动运行后访问后台。

```text
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.lang.reflect=ALL-UNNAMED
--add-opens java.base/java.lang.invoke=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.io=ALL-UNNAMED
--add-opens java.base/sun.reflect.annotation=ALL-UNNAMED
```

以上六项作为同一组 VM 参数传入，已有 IDE 配置的其他参数按实际环境保留。`Starter` 加载配置后创建 `ProjectServer`；`ProjectServer` 负责服务器定制和项目版本信息，本身不是 main 入口。

## 发布与运行目录

正式发布按 [siargo-package.md](siargo-package.md) 执行，包含版本同步、资源压缩、Maven 打包和生产配置核验。`package.xml` 将配置、Web 资源和依赖组织为以下运行结构：

```text
siargo/
├── config/         # 运行配置
├── lib/            # 项目 JAR、JBolt 核心库及依赖
├── webapp/         # 页面、静态资源与业务文件
├── siargo.bat      # Windows 启动脚本
└── siargo.sh       # Linux 管理脚本
```

Windows 发布目录由使用者手动执行 `./siargo.bat start`。该脚本按 `config/` 和 `lib/` 组装运行 classpath，应在完整发布目录使用；源码开发使用前述 IDE 入口。Linux 部署使用 `siargo.sh` 前需核对 JDK 25 的 VM 参数和目标环境配置。服务启动、停止及重启由使用者操作。

技术通知单目录 `upload/siargo/dms/` 下的内容按发布规则排除，由使用者另行复制；源附件保留。`.codex/` 中的辅助内容不进入发布包。

## 开发文档与授权

- [项目开发规则](AGENTS.md)：分层、权限、事务、文件处理及前端资源约定。
- [平台核心架构](wiki/qdoer/01-JBolt平台核心架构.md)：框架组成与扩展入口。
- [业务模块地图](wiki/qdoer/07-业务模块地图.md)：模块关系与业务流程。
- [项目配置手册](wiki/qdoer/08-项目配置速查手册.md)：配置说明；具体值以当前源码为准。
- [更新日志](src/main/webapp/_view/admin/siargo/changelog/CHANGELOG.md)：面向系统使用者的正式版本说明。

本项目已获得 JBolt 授权，仅限本次二次开发，使用与分发须遵循相应授权范围。

