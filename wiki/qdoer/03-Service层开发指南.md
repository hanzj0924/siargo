# Service 层开发指南

> 本文档基于 JBolt 平台 wiki 第九章 + siargo 项目实战源码，涵盖 Service 层的完整开发模式。

---

## 一、Service 层职责与调用场景

Service 层处理一切与业务、数据处理、事务相关的操作。

### 1.1 调用场景

Service 可以在任意场景中使用：
1. **Controller 中注入 Service**（最常见）
2. **Model 中**：`Aop.get(ServiceClass.class)`
3. **拦截器中注入 Service**
4. **Cache 操作类中**：`Aop.get(ServiceClass.class)`

### 1.2 重要原则

- Controller 不是单例的，但 **Service 默认是无状态、单例模式**
- Service 应保持**单一职责**：一个 Service 只处理一个主 Model 的业务
- 需要使用其他 Model 时，应**注入对应的 Service**，而非直接操作 Model

---

## 二、继承体系

```
JBoltCommonService
  ├── JBoltBaseService<M>                      # ★ 最常用：绑定主 Model
  ├── JBoltBaseRecordService                   # 操作 Record 的场景
  ├── JBoltBaseRecordTableSeparateService      # 分表 + Record
  └── JBoltApiBaseService                      # API Service
```

---

## 三、JBoltCommonService 提供的基础能力

所有 Service 继承 JBoltCommonService，获得以下能力：

| 能力 | 方法/说明 |
|------|---------|
| 数据库常用字段常量 | `ID`/`NAME`/`STATUS` 等静态常量 |
| 快速参数校验 | `isOk(param)`/`notOk(param)`/`hasNotOk(params)` |
| 快速添加系统日志 | `addSaveSystemLog()`/`addUpdateSystemLog()`/`addDeleteSystemLog()` |
| 数据转 jstreeBean | 树形数据转换 |
| 表格 filterBox 自动拼接 SQL | 前端筛选条件自动构建 WHERE 子句 |
| 时间参数按数据库类型转换 | 兼容不同数据库的日期格式 |

---

## 四、JBoltBaseService 完整开发流程

### 4.1 最小模板（CustomerService）

```java
public class CustomerService extends JBoltBaseService<Customer> {
    
    // ====== 步骤1：创建 DAO 对象 ======
    private final Customer dao = new Customer().dao();
    
    // ====== 步骤2：实现 dao() 方法 ======
    @Override
    protected Customer dao() {
        return dao;
    }
    
    // ====== 步骤3：实现 systemLogTargetType() ======
    @Override
    protected int systemLogTargetType() {
        return ProjectSystemLogTargetType.NONE.getValue();
    }
    
    // ====== 步骤4：实现 checkCanDelete()（可选） ======
    @Override
    public String checkCanDelete(Customer customer, Kv kv) {
        return checkInUse(customer, kv);  // 检查是否被引用
    }
    
    // ====== 步骤5：实现 afterDelete()（可选） ======
    @Override
    protected String afterDelete(Customer customer, Kv kv) {
        // addDeleteSystemLog(customer.getId(), JBoltUserKit.getUserId(), customer.getName());
        return null;  // 返回 null 表示正常，返回字符串表示错误
    }
}
```

### 4.2 步骤详解

#### 步骤1+2：DAO 对象 + dao() 方法

```java
// Model 对象通过 .dao() 获取 DAO 实例
private final Customer dao = new Customer().dao();

@Override
protected Customer dao() {
    return dao;
}
```

#### 步骤3：systemLogTargetType()

如果需要记录操作日志，必须在 `ProjectSystemLogTargetType.java` 中定义日志类型：

```java
// cn.jbolt.extend.systemlog.ProjectSystemLogTargetType.java
public enum ProjectSystemLogTargetType {
    NONE(0),
    CUSTOMER(1),
    QAREPORT(2),
    // ...
}
```

然后在 Service 中返回对应类型：
```java
@Override
protected int systemLogTargetType() {
    return ProjectSystemLogTargetType.NONE.getValue();
}
```

#### 步骤4：checkCanDelete() - 删除前检查

```java
@Override
public String checkCanDelete(Customer customer, Kv kv) {
    // 检查是否被其他业务引用
    // 返回 null：允许删除
    // 返回字符串：阻止删除，显示错误信息
    return checkInUse(customer, kv);
}
```

#### 步骤5：afterDelete() - 删除后回调

```java
@Override
protected String afterDelete(Customer customer, Kv kv) {
    // 记录删除日志
    addDeleteSystemLog(customer.getId(), JBoltUserKit.getUserId(), customer.getName());
    return null;  // 返回 null 正常执行
}
```

---

## 五、内置查询方法详解

### 5.1 关键字分页查询

```java
// 最简单的分页查询（按 id 倒序）
public Page<Customer> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
    return paginateByKeywords("id", "desc", pageNumber, pageSize, keywords, "name");
    //                          排序字段  排序方向  页码        每页条数   关键词     搜索字段
}
```

### 5.2 按 ID 查询

```java
Customer customer = findById(id);           // 按主键查询单条
List<Customer> list = findByIds(ids);       // 按主键批量查询
```

### 5.3 条件查询

```java
// 查找第一条
Customer customer = findFirst("SELECT * FROM siargo_customer WHERE name = ?", name);

// 查找列表
List<Customer> list = find("SELECT * FROM siargo_customer WHERE status = ?", status);
```

### 5.4 自定义分页

```java
public Page<Customer> paginate(int pageNumber, int pageSize) {
    return dao.paginate(pageNumber, pageSize, 
        "SELECT *", "FROM siargo_customer WHERE status = ? ORDER BY id DESC", 1);
}
```

---

## 六、SQL 构建（3种方式）

### 方式 1：Sql.mysql() 链式 API（★ 推荐）

类型安全，适合中等复杂度的查询：

```java
Sql sql = Sql.mysql()
    .select("*")
    .from("siargo_qareport")
    .where()
    .eq("id", 1)
    .build();

// 复杂条件
Sql sql = Sql.mysql()
    .select("q.*, c.name AS customerName")
    .from("siargo_qareport q")
    .leftJoin("siargo_customer c ON q.cust_id = c.id")
    .where()
    .eq("q.status", 1)
    .like("q.formnum", keywords)
    .between("q.create_time", startTime, endTime)
    .orderBy("q.id", "desc")
    .build();
```

### 方式 2：Db.find()/Db.paginate() + 原生 SQL（★ 最灵活）

适合复杂查询、多表 JOIN、GROUP BY、聚合函数：

```java
// 分页查询（DmsFileService 实战）
StringBuilder selectSql = new StringBuilder();
selectSql.append("SELECT f.id, f.file_name AS fileName, ")
    .append("f.file_path AS filePath, f.file_ext AS fileExt, ")
    .append("ju.name AS uploaderName, ")
    .append("GROUP_CONCAT(DISTINCT k.keyword ORDER BY k.id SEPARATOR ',') AS keywords");

StringBuilder fromSql = new StringBuilder();
fromSql.append(" FROM siargo_dms_file f ")
    .append("LEFT JOIN jb_user ju ON ju.id = f.uploader_id ")
    .append("LEFT JOIN siargo_dms_file_keyword k ON k.file_id = f.id ")
    .append("WHERE f.category_id = ? AND f.status = ?");

List<Object> params = new ArrayList<>();
params.add(categoryId);
params.add(STATUS_NORMAL);

// 动态条件拼接
if (StrKit.notBlank(keywords)) {
    fromSql.append(" AND (f.file_name LIKE ? OR k.keyword LIKE ?)");
    params.add("%" + keywords + "%");
    params.add("%" + keywords + "%");
}

fromSql.append(" GROUP BY f.id ORDER BY f.upload_time DESC");

Page<Record> page = Db.paginate(pageNumber, pageSize, 
    selectSql.toString(), fromSql.toString(), params.toArray());
```

```java
// 聚合查询
Long count = Db.queryLong("SELECT COUNT(*) FROM siargo_product WHERE insp = ?", insp);

// 列表查询
List<Record> records = Db.find("SELECT * FROM siargo_equipment WHERE status = ?", status);
```

### 方式 3：dao.template() 模板查询

简单查询，自动处理分页和排序：

```java
// 简单条件查询
Kv cond = Kv.by("name", name).set("status", 1);
List<Customer> list = dao.template("customer.find", cond).find();

// 分页查询
Page<Customer> page = dao.template("customer.paginate", cond).paginate(pageNumber, pageSize);
```

### 场景选择指南

| 场景 | 推荐方式 |
|------|---------|
| 单表简单 CRUD | `paginateByKeywords()` 内置方法 |
| 单表条件查询 | `Sql.mysql()` 链式 API |
| 多表 JOIN | `Db.find()`/`Db.paginate()` + 原生 SQL |
| 聚合统计 | `Db.queryLong()`/`Db.queryInt()` |
| GROUP BY / HAVING | `Db.find()` + 原生 SQL |

---

## 七、日志记录

### 7.1 操作方法

```java
// 新增日志
addSaveSystemLog(id, JBoltUserKit.getUserId(), "客户名称");

// 更新日志
addUpdateSystemLog(id, JBoltUserKit.getUserId(), "客户名称");

// 删除日志
addDeleteSystemLog(id, JBoltUserKit.getUserId(), "客户名称");

// 自定义日志
addSystemLog(id, userId, desc, targetType);
```

### 7.2 日志的 targetType

日志记录需要指定 `systemLogTargetType()` 返回的类型，在 `ProjectSystemLogTargetType` 中定义。如果不需要记录日志，返回 `NONE`。

---

## 八、返回值体系

```java
// Ret 标准返回
Ret.ok()                            // 成功
Ret.ok("msg")                       // 成功带消息
Ret.fail()                          // 失败
Ret.fail("错误原因")                 // 失败带错误信息

// Service 快捷方法（继承自 JBoltCommonService）
success("操作成功")                  // Ret 成功
fail("操作失败")                     // Ret 失败
ret(success)                        // success=true→Ret.ok(); false→Ret.fail()

// Controller 响应（在 Controller 中调用）
renderJson(service.save(model))     // 直接返回 Ret 给前端
```

---

## 九、Caffeine 缓存模式（★ 完整模板）

### 9.1 代码模板

```java
public class XxxService extends JBoltBaseService<XxxModel> {
    private final XxxModel dao = new XxxModel().dao();
    
    // ====== 缓存字段 ======
    private static final long CACHE_TTL = 10 * 60 * 1000L;     // TTL: 10分钟
    private volatile Map<String, Long> cachedData;              // volatile 保证可见性
    private volatile long cacheTimestamp;                       // 缓存时间戳
    private final ReentrantLock cacheLock = new ReentrantLock(); // 可重入锁
    
    // ====== 获取缓存数据（双重检查锁） ======
    public Map<String, Long> getCachedData() {
        // 第一重检查：无锁快速路径
        if (cachedData != null && 
            (System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL) {
            return cachedData;
        }
        
        cacheLock.lock();
        try {
            // 第二重检查：防止并发穿透
            if (cachedData != null && 
                (System.currentTimeMillis() - cacheTimestamp) < CACHE_TTL) {
                return cachedData;
            }
            
            // 从数据库加载
            Map<String, Long> fresh = loadFromDb();
            cachedData = fresh;
            cacheTimestamp = System.currentTimeMillis();
            return fresh;
        } finally {
            cacheLock.unlock();
        }
    }
    
    // ====== 清除缓存（数据变更后调用） ======
    public void clearCache() {
        cachedData = null;
        cacheTimestamp = 0;
    }
}
```

### 9.2 实战示例：QareportService.getFlowCounts()

```java
// 缓存字段
private static final long FLOW_COUNTS_CACHE_TTL = 10 * 60 * 1000L;
private volatile Map<String, Long> cachedFlowCounts;
private volatile long flowCountsCacheTimestamp;
private final ReentrantLock flowCountsCacheLock = new ReentrantLock();

// 获取各流程阶段的数量统计（带缓存）
public Map<String, Long> getFlowCounts() {
    // 无锁快速路径
    if (cachedFlowCounts != null && 
        (System.currentTimeMillis() - flowCountsCacheTimestamp) < FLOW_COUNTS_CACHE_TTL) {
        return cachedFlowCounts;
    }
    
    flowCountsCacheLock.lock();
    try {
        // 双重检查
        if (cachedFlowCounts != null && 
            (System.currentTimeMillis() - flowCountsCacheTimestamp) < FLOW_COUNTS_CACHE_TTL) {
            return cachedFlowCounts;
        }
        
        // 从数据库加载
        Map<String, Long> counts = loadFlowCountsFromDb();
        cachedFlowCounts = counts;
        flowCountsCacheTimestamp = System.currentTimeMillis();
        return counts;
    } finally {
        flowCountsCacheLock.unlock();
    }
}

// 数据变更后清除缓存
public void clearFlowCountsCache() {
    cachedFlowCounts = null;
    flowCountsCacheTimestamp = 0;
}

// 从数据库加载统计数据（单条 SQL 一次查询）
private Map<String, Long> loadFlowCountsFromDb() {
    Map<String, Long> counts = new HashMap<>();
    Record record = Db.findFirst(
        "SELECT COUNT(*) AS total, " +
        "SUM(CASE WHEN insp=1 THEN 1 ELSE 0 END) AS noq, " +
        "SUM(CASE WHEN insp=2 THEN 1 ELSE 0 END) AS accq, " +
        "SUM(CASE WHEN insp=3 THEN 1 ELSE 0 END) AS funq, " +
        "SUM(CASE WHEN insp=4 THEN 1 ELSE 0 END) AS appq, " +
        "SUM(CASE WHEN insp=5 THEN 1 ELSE 0 END) AS allq " +
        "FROM siargo_product WHERE vd = 1");
    
    counts.put("all", record.getLong("total"));
    counts.put("noq", record.getLong("noq"));
    counts.put("accq", record.getLong("accq"));
    counts.put("funq", record.getLong("funq"));
    counts.put("appq", record.getLong("appq"));
    counts.put("allq", record.getLong("allq"));
    return counts;
}
```

### 9.3 缓存刷新时机

在以下操作后调用 `clearCache()`：
- `save()` 新增数据后
- `update()` 更新数据后
- `delete()` 删除数据后
- `batchInspection()` 批量审批后

---

## 十、实战参考：三种 Service 模式

### 模式 1：简单 CRUD（CustomerService，130行）

```java
public class CustomerService extends JBoltBaseService<Customer> {
    private final Customer dao = new Customer().dao();
    
    @Override protected Customer dao() { return dao; }
    @Override protected int systemLogTargetType() { return ProjectSystemLogTargetType.NONE.getValue(); }
    
    // 分页查询
    public Page<Customer> paginateAdminDatas(int pageNumber, int pageSize, String keywords) {
        return paginateByKeywords("id", "desc", pageNumber, pageSize, keywords, "name");
    }
    
    // 保存（含参数校验）
    public Ret save(Customer customer) {
        if (customer == null || isOk(customer.getId())) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        boolean success = customer.save();
        return ret(success);
    }
    
    // 更新（含存在性检查）
    public Ret update(Customer customer) {
        if (customer == null || notOk(customer.getId())) {
            return fail(JBoltMsg.PARAM_ERROR);
        }
        Customer dbCustomer = findById(customer.getId());
        if (dbCustomer == null) { return fail(JBoltMsg.DATA_NOT_EXIST); }
        boolean success = customer.update();
        return ret(success);
    }
    
    // 删除
    public Ret delete(Long id) {
        return deleteById(id, true);
    }
    
    // 删除前检查
    @Override public String checkCanDelete(Customer customer, Kv kv) {
        return checkInUse(customer, kv);
    }
    
    // 删除后回调
    @Override protected String afterDelete(Customer customer, Kv kv) {
        return null;
    }
}
```

### 模式 2：复杂查询（DmsFileService，388行）

```java
public class DmsFileService extends JBoltBaseService<DmsFile> {
    private final DmsFile dao = new DmsFile().dao();
    private final DmsFileKeyword keywordDao = new DmsFileKeyword().dao();
    
    // 多条件组合搜索（原生 SQL + 动态条件）
    public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, 
            Long categoryId, String keywords, Integer isActive, String activeDate) {
        
        if (categoryId == null) { return new Page<>(); }
        
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("SELECT f.id, f.file_name AS fileName, ...")
                 .append("GROUP_CONCAT(DISTINCT k.keyword SEPARATOR ',') AS keywords");
        
        StringBuilder fromSql = new StringBuilder();
        fromSql.append(" FROM siargo_dms_file f ")
               .append("LEFT JOIN jb_user ju ON ju.id = f.uploader_id ")
               .append("LEFT JOIN siargo_dms_file_keyword k ON k.file_id = f.id ")
               .append("WHERE f.category_id = ? AND f.status = ?");
        
        List<Object> params = new ArrayList<>();
        params.add(categoryId);
        params.add(STATUS_NORMAL);
        
        // 动态条件拼接
        if (StrKit.notBlank(keywords)) {
            fromSql.append(" AND (f.file_name LIKE ? OR k.keyword LIKE ?)");
            params.add("%" + keywords + "%");
            params.add("%" + keywords + "%");
        }
        if (isActive != null) {
            fromSql.append(" AND f.is_active = ?");
            params.add(isActive);
        }
        if (StrKit.notBlank(activeDate)) {
            fromSql.append(" AND DATE_FORMAT(f.active_date, '%Y-%m') = ?");
            params.add(activeDate);
        }
        
        fromSql.append(" GROUP BY f.id ORDER BY f.upload_time DESC");
        return Db.paginate(pageNumber, pageSize, 
            selectSql.toString(), fromSql.toString(), params.toArray());
    }
}
```

### 模式 3：缓存+统计+审批流程（QareportService，992行）

核心特点：
- 缓存统计：volatile + ReentrantLock + DCL + TTL
- 聚合查询：CASE WHEN 单 SQL 一次统计所有阶段
- 待办通知：审批完成后通知下一阶段用户
- 事务管理：多表操作确保一致性

---

## 十一、批量操作

```java
// 批量保存
List<Customer> list = new ArrayList<>();
// ... 填充数据
Db.batchSave(list, batchSize);

// 批量更新
Db.batchUpdate(list, batchSize);

// 事务中的批量处理
@Before(Tx.class)
public void batchAudit(String ids) {
    // 在事务中循环处理
    for (Long id : idList) {
        // ... 业务逻辑
    }
}
```

---

## 十二、Service 中注入其他 Service

```java
public class QareportService extends JBoltBaseService<Qareport> {
    private final Qareport dao = new Qareport().dao();
    
    @Inject
    private UserService userService;    // 查询用户
    
    @Inject
    private RoleService roleService;    // 查询角色
    
    // 使用注入的 Service
    public void notifyNextStageUsers(int currentInsp) {
        Long nextRoleId = getNextRoleId(currentInsp);
        List<User> users = userService.findUsersByRole(nextRoleId);
        // ... 创建待办通知
    }
}
```

---

## 十三、开发检查清单

1. [ ] 继承 `JBoltBaseService<M>`（绑定主 Model）
2. [ ] 创建 DAO：`private final XxxModel dao = new XxxModel().dao();`
3. [ ] 实现 `dao()` 方法
4. [ ] 实现 `systemLogTargetType()` 日志类型
5. [ ] 实现 `checkCanDelete()` 删除前检查（可选）
6. [ ] 实现 `afterDelete()` 删除后回调（可选）
7. [ ] 参数校验使用 `isOk()/notOk()`
8. [ ] 返回值使用 `Ret.ok()/Ret.fail()`
9. [ ] 缓存模式：volatile + ReentrantLock + DCL + TTL
10. [ ] 数据变更后调用 `clearCache()` 刷新
11. [ ] 避免 Service 循环依赖
