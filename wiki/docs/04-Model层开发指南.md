# Model 层开发指南

> 核对日期：2026-09-30；项目版本：v2.9.4；源码基线：`18c1de8`。本文依据当前源码、生成类和本地序列化依赖核实；实体映射另与当日本机 `siargo` 数据库只读列元数据对照，不代表运行环境验收。
>
> [返回文档导航](README.md)

## 1. Model 与 BaseModel 的边界

业务实体位于 `src/main/java/cn/jbolt/siargo/model/`；生成基类位于其 `base/` 子目录。业务 Model 继承对应 `Base*`，BaseModel 再继承 JBolt 的 `JBoltBaseModel`。

| 层次 | 负责内容 | 修改规则 |
|---|---|---|
| `BaseCustomer` 等生成基类 | 字段常量、类型访问方法、`@JBoltField`、序列化注解 | 由代码生成器维护，禁止手工修改 |
| `Customer` 等业务 Model | `@TableBind` 和确有需要的模型扩展 | 可以按需求修改，不承担 Controller 或完整业务流程职责 |
| Service | 查询、校验、关联操作、事务协作 | 业务写入的主要组织位置 |

当前示例：[Customer](../../src/main/java/cn/jbolt/siargo/model/Customer.java)、[BaseCustomer](../../src/main/java/cn/jbolt/siargo/model/base/BaseCustomer.java)。

```java
@TableBind(dataSource = "main", table = "siargo_customer",
           primaryKey = "id", idGenMode = JBoltIDGenMode.SNOWFLAKE)
public class Customer extends BaseCustomer<Customer> {
}
```

`@TableBind` 表示源码中的数据源、表名和主键策略映射，不证明实际数据库已创建该表、字段齐全或索引存在。

## 2. 字段访问与 DAO

新增业务代码优先使用项目规定的动态字段访问：

```java
Customer customer = new Customer();
customer.set("name", name);
Long id = customer.getLong("id");
String customerName = customer.getStr("name");
```

动态访问使用数据库列名，通常为 snake_case。生成的 getter/setter 仍存在，历史源码也可能继续使用；本次业务修改不要求顺带把其他代码全部改写。

Service 通常持有 `new Customer().dao()` 作为查询对象。DAO 是复用的查询入口，写入数据使用独立业务 Model，不能在共享 DAO 上设置每个请求的字段。

字段取值类型以当前生成类、查询表达式和实际 JDBC 映射共同判断：

- `getLong`、`getInt`、`getBoolean`、`getBigDecimal`、`getDate` 不能相互随意替换。
- `tinyint(1)` 可能被 JDBC 映射成 Boolean，必须核对当前字段与连接行为。
- `CAST(... AS CHAR)`、`DATE_FORMAT(...)` 等 SQL 表达式会改变查询结果类型，不能按原表类型读取。
- `null` 不等于 `0` 或空串，业务字段是否允许清空按业务约定判断。

## 3. 主键与 JSON 精度

当前业务 Model 的 `@TableBind` 使用 `idGenMode = JBoltIDGenMode.SNOWFLAKE`。生成基类对 ID getter 保留 `@JSONField(serializeUsing = ToStringSerializer.class)`，使雪花 ID 输出为字符串。不得移除此注解或手工修改生成基类以绕过生成结果。

雪花 ID 在浏览器中始终按字符串传递、保存和比较，不使用 `Number(id)`、`parseInt(id)` 或算术运算。选中项数组、隐藏表单、URL 与再次提交也要保持原值。

`Record` 不使用业务 Model 的 getter 注解。项目对直出 Record 的雪花 ID 采用 SQL 层 `CAST(id AS CHAR) AS id`；关联 ID 同样处理，不能只保护主记录 ID。

当前 `lib/jbolt_core.jar` 中 `JBoltFastJson.init()` 注册了 Record 序列化器、Long 的字符串序列化和专用 `BigDecimalSerializer`。不要把“Model getter 有注解”“全局 Long 配置”“Record 查询列类型”混成一条未经验证的通用结论，也不要把 BigDecimal 一律描述为 `ToStringSerializer`。新响应仍应以实际 JSON 为准。

## 4. Model、Record 与 Map 的字段契约

| 返回对象 | 字段依据 | 前端应如何接入 |
|---|---|---|
| 业务 Model | 当前 getter、序列化注解及框架配置 | 查看真实序列化字段，不从数据库列名猜测 |
| JFinal Record | 当前列容器保存的键，交给 `FastJsonRecordSerializer` 输出 | 当前项目列键转全小写，SQL 别名与 `set` 参数的大小写不能直接当 JSON 键 |
| 普通 Map/Kv/DTO | 对应对象与序列化规则 | 不套用 Record 全小写规则 |

当前 Record 容器采用 `CaseInsensitiveContainerFactory(true)`，`FastJsonRecordSerializer` 输出其 `getColumns()`。例如：

```java
row.set("modelIds", associatedIds);
row.set("modelNames", names);
```

实际 Record 响应键为 `modelids`、`modelnames`，前端应读取：

```javascript
const ids = Array.isArray(data.modelids) ? data.modelids : [];
const names = data.modelnames;
```

真实生产代码参照 [PdfTemplateService](../../src/main/java/cn/jbolt/admin/siargo/qarep/pdffolder/PdfTemplateService.java) 的关联列组装与 [siargo.js](../../src/main/webapp/assets/js/siargo.js) 的模板编辑回显。提交参数可以仍叫 `modelIds`，因为请求参数契约与响应 Record 键是两个方向，不能为“统一命名”擅自改掉调用方。

修改字段契约时应同时检查：

1. 列表是否读到已有名称。
2. 编辑窗口是否回显原有 ID 和显示文本。
3. 不修改关联字段直接提交时是否仍保留原绑定。
4. 用户主动清空与接口字段未读取成功是否被正确区分。

禁止为了一个页面修改全局容器或序列化配置，也不无依据地增加多套大小写兜底。需要验证时使用真实容器和序列化器，不使用手写驼峰模拟数据证明 Record 契约正确。

## 5. 当前源码中的业务实体映射

以下为当前 `siargo/model` 下 **23 个 `@TableBind` 业务 Model 的源码映射**。当日本机数据库列元数据中的 23 张 `siargo_` 业务表与其表名相符；这不是包括平台表在内的数据库总表数。已核实的结构范围与局限见 [数据库设计规范](06-数据库设计规范.md)，完整索引和约束不能从 Model 推断。

| Model | 映射表 | 业务专题 |
|---|---|---|
| `Customer` | `siargo_customer` | [客户、供应商与来料图片](09-客户供应商与来料图片.md) |
| `Supplier` | `siargo_supplier` | 同上 |
| `Image` | `siargo_image` | 同上 |
| `ProdModel` | `siargo_prod_model` | [产品目录与参数](10-产品目录与参数.md) |
| `ProdModelDimension` | `siargo_prod_model_dimension` | 同上 |
| `ProdModelParam` | `siargo_prod_model_param` | 同上 |
| `ProdParamType` | `siargo_prod_param_type` | 同上 |
| `ProdParamValue` | `siargo_prod_param_value` | 同上 |
| `ProdTechnicalParam` | `siargo_prod_technical_param` | 同上 |
| `Qareport` | `siargo_qareport` | [检验报告单](11-检验报告单业务.md) |
| `Product` | `siargo_product` | 同上 |
| `ProductRejectLog` | `siargo_product_reject_log` | 同上 |
| `PdfFolder` | `siargo_pdf_folder` | [Excel 导入与 PDF 输出](12-Excel导入与PDF输出.md) |
| `PdfTemplate` | `siargo_pdf_template` | 同上 |
| `PdfTemplateProd` | `siargo_pdf_template_prod` | 同上 |
| `DmsCategory` | `siargo_dms_category` | [技术通知单与文件管理](13-技术通知单与文件管理.md) |
| `DmsFile` | `siargo_dms_file` | 同上 |
| `DmsFileKeyword` | `siargo_dms_file_keyword` | 同上 |
| `Equipment` | `siargo_equipment` | [设备管理](14-设备管理.md) |
| `EquipmentComparison` | `siargo_equipment_comparison` | 同上 |
| `EquipmentRepair` | `siargo_equipment_repair` | 同上 |
| `EquipmentCertificate` | `siargo_equipment_certificate` | 同上 |
| `ApiCallLog` | `siargo_api_call_log` | [接口、应用中心与调用日志](16-对外接口应用中心与调用日志.md) |

字段语义、角色、审批状态、软删除取值和模块关系在对应专题维护，本篇不复制一份易失真的业务字典。平台实体另有 `common/model` 与核心 jar 中的类，不应把上表当作全部平台模型。

## 6. 字段变更与代码生成

当前生成入口为 [ModelGenerator](../../src/main/java/cn/jbolt/extend/gen/ModelGenerator.java)。该入口目前明确设置 `configName="main"`、`modelPackage="cn.jbolt.siargo.model"`、`tableNames={"siargo_product"}`、`cover=false`，并生成字段常量；它不是默认生成所有表的脚本。运行前应核对入口参数与所属数据源配置，不能只改某个配置文件中的包名就假定目标已改变。运行生成器会更新源码，不属于只读核查，不能在一次文档或排查任务中默认执行。

字段变更按照以下顺序组织：

1. 先确定业务语义、旧数据处理和调用方影响，数据库结构变更另行满足授权要求。
2. 核实目标库实际元数据及生成器目标范围，避免向错误目录或错误数据源生成。
3. 使用生成器维护 BaseModel，检查生成 diff；保留业务 Model 的人工扩展。
4. 同步 Service、表单绑定、列表、编辑回显、导出/PDF/API 等实际消费方。
5. 分别说明源码已更新、数据库是否已变更、运行环境是否已重启与验证。

不能把旧备份 SQL、生成注释或 `@JBoltField` 单独作为数据库当前结构的证明。数据库核对方式和设计约束见 [数据库设计规范](06-数据库设计规范.md)。
