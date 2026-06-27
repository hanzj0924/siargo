# Controller 层开发指南

> 本文档基于 JBolt 平台 wiki 第八章 + siargo 项目实战源码，涵盖 Controller 层的完整开发模式。

---

## 一、Controller 层职责

在 JFinal MVC 架构下，Controller 的职责：
- 接收用户请求（request）
- 调用 Service 层处理业务逻辑
- 响应（response）返回数据 / 信息 / UI

**重要原则**：Controller 不应该直接操作数据库，所有数据操作必须通过 Service 层。

---

## 二、继承体系

```
JFinal Controller
  └── JBoltCommonController         # 基础封装
        ├── JBoltBaseController     # ★ 后台管理 Controller（siargo 主要使用）
        └── JBoltApiBaseController  # API 接口 Controller
```

### 2.1 JBoltCommonController 提供的能力

- 便捷参数获取（getPara/getModel/getFile/getBean/getHeader/getRawData 等）
- 万能参数获取器（getJBoltPara）
- 参数校验（isOk/notOk/hasNotOk/isExcel/isImage）
- 各种 render 响应（renderJson/renderJsonData/renderFail/renderBytes 等）
- 验证码图片 render

### 2.2 JBoltBaseController（后台管理专用）

继承 JBoltCommonController，额外提供：
- `renderJsonData(Object data)` - 返回成功数据 JSON
- `renderJsonFail(String msg)` - 返回失败信息 JSON
- `renderJsonSuccess()` - 返回成功 JSON
- `renderFail(String msg)` - **智能识别请求类型**，自动返回对应格式
- `renderJBoltResult(Ret ret)` - 返回 JBolt 标准结果

**特别关注 `renderFail()`**：
无论前端是直接请求、dialog iframe 加载、pjax 请求还是请求 JSON 数据，调用 `renderFail("错误信息")` 即可，JBolt 会自动识别请求类型返回正确格式。

### 2.3 JBoltApiBaseController（API 接口专用）

用于对外 API 接口，提供：
- JWT Token 验证
- App 认证
- `@OpenAPI` 注解支持
- `@UnCheckJBoltApi` 免登录

---

## 三、路由配置（3种方式）

### 方式 1：Routes 类集中配置

```java
// 在 ProjectConfig.configRoutes() 中
me.add("/admin/customer", CustomerAdminController.class, "/_view/admin/siargo/customer");
```

### 方式 2：@Path 注解 + Package 扫描（★ siargo 项目使用此方式）

```java
@Path(value = "/admin/siargo/customer", viewPath = "/_view/admin/siargo/customer")
public class CustomerAdminController extends JBoltBaseController {
    // ...
}
```

在 ProjectConfig 中配置包扫描：
```java
me.add(new Routes() {
    @Override
    public void config() {
        this.addInterceptor(new JBoltAdminAuthInterceptor());  // 登录拦截
        this.scan("cn.jbolt.admin.siargo");                    // 扫描整个包
    }
});
```

### 方式 3：@Path 全路径（无 baseViewPath 时）

```java
@Path(value = "/api/siargo/order", viewPath = "/_view/...")  // 必须写全路径
public class OrderStatusApiController extends JBoltApiBaseController {
}
```

---

## 四、viewPath 规则详解

### 4.1 基本规则

- 系统默认根目录：`webapp/_view`
- Controller 的 `viewPath` 是在 `_view` 基础上的相对路径
- `render("index.html")` 实际查找：`_view + viewPath + "index.html"`

### 4.2 场景示例

```java
// 场景1：有 baseViewPath（推荐）
// AdminRoutes 设置了 baseViewPath = "/_view/_admin"
// Controller 的 viewPath = "/user"
// render("index.html") → /_view/_admin/user/index.html

// 场景2：@Path 中的 viewPath 依赖 baseViewPath
@Path(value = "/admin/siargo/customer", viewPath = "/_view/admin/siargo/customer")
// 完整路径 = webapp/_view/admin/siargo/customer/index.html

// 场景3：API Controller 无 baseViewPath
@Path("/api/siargo/order")
// 不 render 页面，只返回 JSON
```

---

## 五、权限控制

### 5.1 注解方式

```java
@CheckPermission(PermissionKey.SIARGO)    // 需要 SIARGO 模块权限
@UnCheckIfSystemAdmin                      // 系统管理员自动豁免
@Path(value = "/admin/siargo/customer", viewPath = "/_view/admin/siargo/customer")
public class CustomerAdminController extends JBoltBaseController {
```

### 5.2 角色 SN 体系

角色使用 `jb_role` 表中的 `sn` 字段进行标识，代码中通过 SN 判断角色：

```java
// 查询角色
Long adminRoleId = roleService.findIdBySn(1);       // 管理员
Long accuracyRoleId = roleService.findIdBySn(211);   // 精度检验员
Long auditRoleId = roleService.findIdBySn(221);      // 审核员

// 判断用户是否具有某角色
boolean isAdmin = JBoltUserAuthKit.hasRole(userId, adminRoleId);
```

### 5.3 siargo 项目使用的角色 SN

| SN 值 | 角色名称 | 用途 |
|-------|---------|------|
| 1 | 系统管理员 | 全部权限 |
| 211 | 精度检验 | 精度检验结果录入 |
| 212 | 外观检验 | 外观检验结果录入 |
| 213 | 包装检验 | 包装检验结果录入 |
| 214 | 批准 | 最终批准 |
| 221 | 审核 | 设备对比审核 |

### 5.4 Controller 中权限条件判断

```java
public void index() {
    Long userId = JBoltUserKit.getUserId();
    
    // 管理员权限判断
    Long adminRoleId = roleService.findIdBySn(1);
    boolean isAdmin = adminRoleId != null && JBoltUserAuthKit.hasRole(userId, adminRoleId);
    
    // 各子权限判断（管理员自动拥有所有权限）
    Long accuracyRoleId = roleService.findIdBySn(211);
    set("accuracy", isAdmin || (accuracyRoleId != null && JBoltUserAuthKit.hasRole(userId, accuracyRoleId)));
    
    // 将权限标识传到前端，控制按钮显隐
    render("index.html");
}
```

---

## 六、参数获取完整手册

### 6.1 基本参数获取

```java
// URL 参数 / query string：GET /admin/siargo/customer?name=xxx
String name = getPara("name");
Integer type = getParaToInt("type");
Long id = getLong("id");           // 优先从 URL 路径参数获取
String value = get("key");          // 等同于 getPara("key")
```

### 6.2 Model 自动映射

```java
// 表单提交：<input name="customer.name" value="...">
Customer customer = getModel(Customer.class, "customer");
// 自动将表单中 customer.xxx 的字段映射到 Customer 对象
```

### 6.3 文件上传

```java
UploadFile uploadFile = getFile();  // 获取上传的文件
// 文件类型校验
if (notExcel(uploadFile)) {
    renderJsonFail("请上传excel文件");
    return;
}
File excelFile = uploadFile.getFile();
```

### 6.4 URL 路径参数

```java
// URL: /admin/siargo/customer/edit/12345
Long id = getLong(0);   // 获取第一个路径参数 12345
Long id2 = getLong(1);  // 获取第二个路径参数
```

### 6.5 Header 参数

```java
String userAgent = getRequest().getHeader("User-Agent");
String token = getHeader("Authorization");
```

### 6.6 Raw JSON 请求体

```java
String rawData = getRawData();
// 或直接注入
MyBean bean = getBean(MyBean.class);
```

### 6.7 万能参数获取器（JBolt 增强）

```java
// getJBoltPara() 自动识别参数类型，从多种来源获取
JBoltPara para = getJBoltPara();
// 直接注入方式
public void action(JBoltPara para) {
    // para 包含所有请求参数
}
```

---

## 七、参数校验

```java
// 单个参数有效性校验
if (isOk(name)) { /* name 有效 */ }
if (notOk(id)) { /* id 无效 */ }

// 批量校验
if (hasNotOk(name, age, email)) {
    renderFail("参数不完整");
    return;
}

// 文件类型校验
if (notExcel(uploadFile)) { renderFail("请上传Excel文件"); }
if (notImage(uploadFile)) { renderFail("请上传图片"); }
```

---

## 八、render 响应类型完整列表

### 8.1 模板渲染

```java
render("index.html");       // 渲染页面
render("add.html");         // 新增页面
render("edit.html");        // 编辑页面
render("details.html");     // 详情页面
```

### 8.2 JSON 响应

```java
// 成功返回数据
renderJsonData(data);

// 失败返回错误信息
renderJsonFail("错误信息");

// 简单成功/失败
renderJsonSuccess();
renderJson(Ret.ok());
renderJson(Ret.fail("错误"));

// 智能 Fail（自动识别请求类型）
renderFail("操作失败");
```

### 8.3 响应前端的 set 方法

```java
// 在 render 之前设置数据到模板
set("customer", customer);              // 设置对象
set("accuracy", true);                  // 设置权限标志
set("products", productList);           // 设置列表
set("ids", get("ids"));                // 传递参数
```

### 8.4 文件下载

```java
// renderBytes/renderFile - 下载文件
```

---

## 九、事务管理

```java
@Before(Tx.class)   // ★ 声明式事务
public void save() {
    renderJson(service.save(getModel(Customer.class, "customer")));
}

@Before(Tx.class)
public void update() {
    renderJson(service.update(getModel(Customer.class, "customer")));
}
```

`@Before(Tx.class)` 声明该方法内的所有数据库操作在一个事务中执行，失败则自动回滚。

---

## 十、拦截器配置

```java
// 全局拦截器（在 ProjectConfig 中配置）
me.add(new Routes() {
    @Override
    public void config() {
        this.addInterceptor(new JBoltAdminAuthInterceptor());  // 登录校验
        this.scan("cn.jbolt.admin.siargo");
    }
});

// 单个 Controller 拦截器
@Before(MyInterceptor.class)
public class XxxController extends JBoltBaseController { }

// 子模块共用拦截器（按包配置）
me.add(new Routes() {
    @Override
    public void config() {
        this.addInterceptor(new MyInterceptor());
        this.scan("cn.jbolt.admin.siargo.xxx");
    }
});
```

---

## 十一、三种 Controller 模式实战

### 模式 1：简单 CRUD（CustomerAdminController）

```java
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/customer", viewPath = "/_view/admin/siargo/customer")
public class CustomerAdminController extends JBoltBaseController {

    @Inject
    private CustomerService service;
    
    // 首页
    public void index() {
        render("index.html");
    }
    
    // 分页数据
    public void datas() {
        renderJsonData(service.paginateAdminDatas(
            getPageNumber(), getPageSize(), getKeywords()));
    }
    
    // 新增页面
    public void add() {
        render("add.html");
    }
    
    // 编辑页面（URL 路径参数获取 ID）
    public void edit() {
        Customer customer = service.findById(getLong(0));
        if (customer == null) {
            renderFail(JBoltMsg.DATA_NOT_EXIST);
            return;
        }
        set("customer", customer);
        render("edit.html");
    }
    
    // 保存
    @Before(Tx.class)
    public void save() {
        renderJson(service.save(getModel(Customer.class, "customer")));
    }
    
    // 更新
    @Before(Tx.class)
    public void update() {
        renderJson(service.update(getModel(Customer.class, "customer")));
    }
    
    // 删除
    @Before(Tx.class)
    public void delete() {
        renderJson(service.delete(getLong(0)));
    }
}
```

### 模式 2：复杂业务（QareportAdminController，643行）

```java
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/qarep", viewPath = "/_view/admin/siargo/qarep")
public class QareportAdminController extends JBoltBaseController {

    @Inject private QareportService service;
    @Inject private PDFService pdfservice;
    @Inject private ExcelService excelservice;
    @Inject private ProductService proservice;
    @Inject private CustomerService custservice;
    @Inject private RoleService roleService;
    
    // 首页（含权限判断）
    public void index() {
        Long userId = JBoltUserKit.getUserId();
        Long adminRoleId = roleService.findIdBySn(1);
        boolean isAdmin = adminRoleId != null && JBoltUserAuthKit.hasRole(userId, adminRoleId);
        
        set("accuracy", isAdmin || hasRole(userId, 211));
        set("appearance", isAdmin || hasRole(userId, 212));
        set("packaging", isAdmin || hasRole(userId, 213));
        set("approval", isAdmin || hasRole(userId, 214));
        render("index.html");
    }
    
    // 多条件分页查询
    public void datas() {
        Date startTime = null, endTime = null;
        if (isOk(getPara("dateRange"))) {
            // 解析日期范围参数
        }
        int prodType = getInt("prodType") == null ? 0 : getInt("prodType");
        int insp = getInt("insp") == null ? 0 : getInt("insp");
        renderJsonData(service.paginateAdminDatas(
            getPageNumber(), getPageSize(), getKeywords(),
            prodType, insp, startTime, endTime));
    }
    
    // 批量审批
    public void batchInspection() {
        Integer insp = getParaToInt("insp");
        String idsJson = getPara("ids");
        List<Long> ids = Arrays.stream(idsJson.split(","))
            .map(String::trim).map(Long::parseLong)
            .collect(Collectors.toList());
        
        for (Long id : ids) {
            Product product = proservice.findById(id);
            if (product != null) {
                // 根据 insp 阶段设置不同审批信息
                if (insp == 2) { product.set("accq_uid", JBoltUserKit.getUserId()); }
                else if (insp == 3) { product.set("funq_uid", JBoltUserKit.getUserId()); }
                // ... 其他阶段
                product.set("insp", insp);
                product.update();
            }
        }
        service.clearFlowCountsCache();    // 清除缓存
        service.notifyNextStageUsers(insp); // 发送待办通知
        renderJsonSuccess();
    }
    
    // Excel 导入
    public void importExcel() {
        UploadFile uploadFile = getFile();
        if (notExcel(uploadFile)) {
            renderJsonFail("请上传excel文件");
            return;
        }
        File excelFile = uploadFile.getFile();
        List<Map<String, Object>> dataList = excelservice.readExcel(excelFile);
        Map<String, Object> result = excelservice.processExcelData(dataList);
        renderJsonData(result);
    }
    
    // PDF 生成
    public void toPdf() throws Exception {
        String idsJson = getPara("ids");
        List<Long> ids = Arrays.stream(idsJson.split(","))
            .map(String::trim).map(Long::parseLong)
            .collect(Collectors.toList());
        
        for (Long id : ids) {
            pdfservice.generateReportPdf(id, "export/PDF");
        }
        renderJsonSuccess();
    }
    
    // 软删除（移到回收站）
    @Before(Tx.class)
    public void deleteByIds() {
        // 设置 vd=0, delete_time, delete_des
        // ...
    }
    
    // 回收站数据
    public void inactiveDatas() {
        renderJsonData(service.paginateInactiveListDatas(
            getPageNumber(), getPageSize(), getKeywords()));
    }
    
    // 恢复
    public void restore() {
        Product product = proservice.findById(getLong(0));
        product.set("vd", 1);
        product.set("delete_time", null);
        product.update();
        renderJsonSuccess();
    }
    
    // 永久删除
    @Before(Tx.class)
    public void permanentDelete() {
        // 物理删除 + 记录日志
    }
}
```

### 模式 3：API 接口（OrderStatusApiController，136行）

```java
@Path("/api/siargo/order")
public class OrderStatusApiController extends JBoltApiBaseController {

    @Inject private QareportService qareportService;
    @Inject private ApiCallLogService apiCallLogService;

    @UnCheckJBoltApi           // 不需要登录认证
    @OpenAPI                    // 标记为开放 API
    public void status() {
        long startTime = System.currentTimeMillis();
        String orderId = get("orderId");
        String token = get("token");
        String requestIp = JBoltIpUtil.getIp(getRequest());
        
        // 参数校验
        if (StrKit.isBlank(orderId)) {
            logApi(..., "fail", 1001, "订单号不能为空", startTime);
            renderJson(Kv.by("status", "fail").set("code", 1001)...);
            return;
        }
        
        // Token 验证
        String secretKey = getApplication().getAppSecret();
        if (!SiargoApiTokenUtil.validateToken(secretKey, orderId, token)) {
            logApi(..., "fail", 1003, "token验证失败", startTime);
            renderJson(Kv.by("status", "fail").set("code", 1003)...);
            return;
        }
        
        // 业务查询
        List<Record> dataList = qareportService.queryOrderStatusByOrderId(orderId);
        
        logApi(..., "ok", 200, "查询成功", startTime);
        renderJson(Kv.by("status", "ok").set("code", 200).set("data", dataList));
    }
}
```

---

## 十二、常用 JBoltMsg 常量

```java
JBoltMsg.PARAM_ERROR              // "参数错误"
JBoltMsg.DATA_NOT_EXIST           // "数据不存在"
JBoltMsg.DATA_SAME_NAME_EXIST     // "同名数据已存在"
```

---

## 十三、开发检查清单

1. [ ] 继承 `JBoltBaseController` 或 `JBoltApiBaseController`
2. [ ] 添加 `@Path` 注解（value + viewPath）
3. [ ] 添加 `@CheckPermission(PermissionKey.SIARGO)` 权限注解
4. [ ] 添加 `@UnCheckIfSystemAdmin` 超管豁免
5. [ ] 使用 `@Inject` 注入所需 Service
6. [ ] 写操作添加 `@Before(Tx.class)` 事务声明
7. [ ] Controller 不直接操作数据库（不调用 `Db.find()` 等）
8. [ ] 返回 JSON 使用 `renderJsonData()/renderJsonFail()` 系列
9. [ ] 返回页面使用 `render("xxx.html")`
10. [ ] 错误处理使用 `renderFail(msg)` 统一响应
