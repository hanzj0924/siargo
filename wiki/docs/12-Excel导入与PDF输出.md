# Excel 导入与 PDF 输出

> 核对基线：2026-09-30 / v2.9.4 / `18c1de8`。本文依据当前源码核实，不代表数据库现状或运行验收已完成。

[返回文档目录](README.md)

## 业务链与入口

本篇统一定义报告单 Excel 预填、系列匹配结果、报告版号和模板、PDF 正式发布及月份归档。录入字段和审批状态见 [检验报告单业务](11-检验报告单业务.md)，型号结构与目录删除影响见 [产品目录与参数](10-产品目录与参数.md)。这里的报告生成不参与 [DMS 技术通知单](13-技术通知单与文件管理.md) 的原文件命名或元数据维护。

```mermaid
flowchart LR
  E[Excel 文件] --> P[解析与系列匹配]
  P --> C[产品卡片预填]
  C --> S[用户确认并保存]
  S --> A[检验批准]
  A --> T[按系列及版号选模板]
  T --> G[字段映射与 PDF 渲染]
  G --> F[正式发布并写回产品地址]
  G --> R[区间归档副本与 RAR]
```

| 入口 | 主要输入 |
|---|---|
| `/admin/siargo/qarep/importExcel` | multipart 文件字段 `file`，接受 xls/xlsx |
| `/admin/siargo/qarep/toPdf` | `ids`：逗号分隔的产品 ID；不是报告头 ID |
| `/admin/siargo/qarep/toPdfs` | `year`、`startMonth`、`endMonth` |
| `/admin/siargo/qarep/pdffolder` | 版号目录、模板文件和系列关联管理页面 |

这些后台 Controller 使用 `PermissionKey.SIARGO` 和系统管理员豁免；PDF Service 还会检查产品有效性、放行状态和签名完整性。模板管理没有另设本篇自定义的应用或授权机制，项目接口治理现状见 [对外接口、应用中心与调用日志](16-对外接口应用中心与调用日志.md)。

## Excel 解析与预填协议

导入只生成前端预填数据，不直接创建报告头或产品记录。临时文件进入报告资源目录的独立 `imports/{任务ID}`；解析结束在 `finally` 中尝试删除临时文件。没有数据、缺必填列或模板内容冲突时返回失败，由用户修正文件后再导入。

### 两类输入模板

| 模板 | 识别与取数规则 |
|---|---|
| 逐行表格 | 未找到“检定记录”工作表时，读取第一张表；首行是列名，必须含 `型号`，后续非空行为数据 |
| 检定记录 | 任一工作表名称以 `检定记录` 开头即优先使用；从 E1 取型号规格、E2 取编号 |

逐行表格识别 `型号`、`编号`、`订单号`、`返修表`，以及 `描述/备注/产品描述/型号描述`。同型号行合成一张产品卡片，`qsi` 和 `qi` 都取该组行数，编号按输入顺序压缩可连续的数字后缀，描述去重合并。不同型号各建一张卡片。

同次逐行导入不能包含多个不同的非空订单号，不能混入不同报告类型；`返修表` 的非空值只接受 `YES/NO`，全部缺省时按正常报告预填。缺型号的非空行会报出行号，不会静默跳过。

检定记录允许型号以逗号、中文逗号或换行拆分；多个型号时，非空编号必须能逐项对应，存在空型号或数量不匹配会拒绝。该模板不自动推断订单号、报告类型和数量，返回空值供用户填写。

### 返回数据

成功由 `renderJsonData` 包装，业务数据位于 `data`：

| 字段 | 含义 |
|---|---|
| `success` | Controller 在成功解析后设置为 `true` |
| `orderId`、`repType` | 提取的报告公共信息，可能为空 |
| `products` | 独立产品卡片数组 |
| 产品基础字段 | `model,number,qsi,qi,des,lt_status,flow_range`，以及报告电气参数字段 |
| 产品匹配字段 | `siargo_prod_model_id,model_series,prodType,prodTypeName,matchStatus,matchReason,candidates` |

这些数据由 `Map` 显式构造，驼峰字段保持原样，不能套用 `Record` 小写规则。`lt_status` 初始为空，电气值初始为空，流量范围为空字符串；即使型号匹配成功，也仍需选择检漏状态、版号、客户等并通过保存校验。

### 型号系列匹配

匹配依据是当前启用系列的 `model_desc` 和显式引用的参数值，不以系列显示名称做简单前缀猜测。单次导入复用同一份目录快照；下一次导入重新读取。归一化处理包含全角字母/数字、大写化、横线形式及空白，匹配仍保留型号身份边界和分支约束。

备注包含可识别型号证据时备注优先；同等候选再尝试以完整规则区分分支。返回状态只有以下三种：

| `matchStatus` | 处理方式 |
|---|---|
| `matched` | 唯一匹配，返回系列字符串 ID；用户仍应核对型号及描述 |
| `ambiguous` | 多个同等候选，系列 ID 留空，由用户选择 |
| `unmatched` | 无有效候选，系列 ID 留空，提示手动选择 |

`candidates` 最多提供三个候选，每项为 `id/model_series/score`；`matchReason` 解释本次建议。不得把 `ambiguous/unmatched` 改成默认首项后静默保存。

## 版号、模板与系列绑定

模板管理前缀为 `/admin/siargo/qarep/pdffolder`。报告版号来自启用字典 `siargo_pdfver` 的 **name**，如 `G/2`；它在产品和模板中是字符串。

| 数据 | 职责 |
|---|---|
| `siargo_pdf_folder` | 版号、关联字典项、路径元数据及 `is_active` |
| `siargo_pdf_template` | `pdfver`、`template_file`、`error_hint`、`is_active` |
| `siargo_pdf_template_prod` | `siargo_pdf_template_id` 与 `siargo_prod_model_id` 的绑定 |

常用顺序是：从 `dictVersions` 选择字典项，以 `createFolder(dictId)` 建目录；上传可填写的 PDF；用 `saveRule` 保存模板及系列关联。`folders/templates/rules/seriesOptions` 分别提供目录、文件、规则和系列候选。

`saveRule` 使用 `rule.id`、`rule.pdfver`、`rule.template_file`、`rule.error_hint`、`rule.is_active` 及 `modelIds`；多值 `modelIds` 会拼成逗号分隔 ID，也允许空集合表示解除绑定。模板文件名最多 100 字符、版号最多 10 字符、备注最多 50 字符；模板必须有有效 PDF 表单字段。模板保存事务同时保存关联，不能把保存成功理解为仅文件已经上传。

查询 `rules` 返回 `Record`，实际关联字段为 **`modelids`、`modelseries`、`modelnames`**；前端读取 `modelIds/modelNames` 会丢失回显。请求参数仍是 `modelIds`。编辑再提交时必须保留已有字符串 ID 集合，空显示不能自动当作用户解除绑定。

系列候选的现状与最终生成约束需要区分：`seriesOptions` 当前排除已被其他模板绑定的系列，排除范围不区分版号和模板启用状态，仅排除当前模板自身以支持回显；`saveRule` 对启用模板检查的唯一性则是 **同系列、同版号不能有其他启用模板**。文档不能把候选过滤改写成按版号过滤的理想行为。

正式解析按产品系列 ID 和版号查绑定：系列必须存在且启用，版号目录必须启用，绑定中必须恰好一个启用模板。无关联、全停用、多启用候选或文件不存在时失败，不以文件名、行顺序或产品类型猜模板。

### 文件与目录删除边界

- 上传只检查文件、表单字段和路径，并按提交版号落盘；当前上传入口不查询版号是否存在/启用。启用检查发生在保存规则和解析模板时。
- 同名上传使用新建移动语义，不覆盖原模板文件。扫描件或没有可填写表单字段的 PDF 不能用作模板。
- 删除模板规则前必须先解除所有系列绑定；删除模板文件前必须解除对该文件的模板规则引用。
- 删除版号目录前检查产品版号引用、目录中的文件和模板规则；任一仍存在就拒绝。产品引用检查包括回收站记录。目录物理删除在数据库提交后执行。
- 版号元数据缓存有效期为 2 小时，模板关联实时查询；管理写入及 `clearCache` 清理版号缓存。清缓存不是修改绑定的替代步骤。

## PDF 字段填写与生成条件

正式生成及月份副本均要求：有效产品 `vd=1`、已批准放行、各必需环节的签名用户 ID/姓名/时间完整，以及订单号、报告编号、型号、编号、客户和创建时间完整。有成品检漏产品还必须有检漏签名。流程定义只在 [报告单业务](11-检验报告单业务.md) 维护。

`ReportFieldMapper` 构造最终字段，`PdfRenderer` 只填写模板中存在且映射中提供的同名字段，再扁平化表单、关闭文件并逐页读取验证。字体使用 Web 根目录下 `assets/fonts/SIMSUN.TTC`。模板未映射字段保留原预设值；不能声称所有空白表单域均由生成器计算。

| 字段组 | 当前映射 |
|---|---|
| 基础字段 | `formnum,sp_qsi,sp_qi,sc_name,order_id,sp_model,c_time,sp_number` |
| 报告类型 | `rep_type_name` 输出产成品/退修品选中标记 |
| 签名字段 | `accq/lt/funq/appq/allq` 各自的 `_name/_time/_email` |
| 通用参数 | `flow_range` 优先使用流量范围名称；`thv`、`zp` 取产品实测值 |
| 特定检验项 | `para2/para6/para7` 分别按 `MF65/6600`、`MF5200`、`MF5700` 系列填 `ok`，其他填 `/` |
| MFI | 映射 `cucmax,cucmin,pv`，`pulseValue` 为 `/` |
| MF-GD | 映射 `cuc,fl` |
| MF-FD | 映射 `cucmax,cucmin,pv,la`；根据完整型号按选型规则解析第二个参数（供电方式）决定 `pulseValue/fl/bv` |

MF-FD 第二参数为 `E` 时，`pulseValue=ok` 且 `fl/bv=/`；其他值映射产品实测 `fl/bv`。解析不到唯一供电方式时本产品生成失败，错误带型号和订单号，不退回随意猜测。模板实际包含、映射中存在但最终为空的参数会形成 `warnings`；警告不会自动把已成功发布的 PDF 改判为失败。

## 正式发布、归档与文件生命周期

| 项目 | 选中正式生成 `toPdf` | 月份区间归档 `toPdfs` |
|---|---|---|
| 输入范围 | 明确产品 ID，按首次出现顺序去重 | 本年度 1 月至上月，含起止月份；1 月无可归档月份 |
| 时间选择 | 不再额外按日期筛选所选 ID | 按 `allq_time >= 起始月首日` 且 `< 结束月次月首日`，取有效已完成产品 |
| 写回 | 每个成功产品更新正式 `pdfstr` | 只生成任务副本，不更新 `pdfstr` |
| 事务 | 单产品独立发布，非整批事务 | 不进行正式地址发布事务 |
| 压缩 | 无 RAR 归档要求 | 检查 `winrar_exe_path`，以本任务显式清单打 RAR 并执行完整性测试 |

月份参数只接受数字，年份必须是服务器当前年，起始月不得晚于结束月。例如 2026-09-30 可选 2026 年 1—8 月，不能归档 9 月或跨年。无符合产品时返回失败提示。

模板固定根为 `upload/siargo/qarep/templates/{版号层级}`；报告资源临时导入根为 `upload/siargo/qarep/imports`。正式输出根由 `siargo_qarep_export_path` 配置；其下使用 `reports/{版号层级}`、`pdf-tasks/{任务ID}`、`archives/{月份或区间}/{任务ID}`。`G/2` 按合法的两个路径段处理，禁止反斜线或危险路径段。配置及共享存储机制见 [配置速查](08-项目配置速查手册.md)。

正式生成先写 `.part`，验证后移到带唯一文件名的 PDF，再在事务中复核模板配置、系列绑定、报告/产品快照与旧 PDF 地址。生成期间业务内容或状态变化会拒绝发布旧快照。新地址提交成功后，才清理没有其他产品引用的旧 PDF；单项失败仅清理自己的未发布产物，不删除旧正式文件。

报告业务修改也会影响正式地址：共享报告头变化使该报告下产品 PDF 全部失效；产品字段、状态、检漏标记或描述变化使相应产品 PDF 失效。审批/驳回清空该产品 PDF 地址。永久删除在事务提交后清理无引用 PDF；软删除到回收站本身保留文件和地址。目录维护不会自动批量重发历史 PDF。

月份归档压缩的是本任务重新生成的副本，不扫描历史目录，也不删除原正式 PDF。压缩程序缺失时在任务开始阶段失败；压缩失败保留已生成副本供排查。批次失败记录、压缩输入清单和日志属于业务导出任务产物，由生成器保存在任务目录；当前类没有自动清空历史任务目录的机制。

## 批次结果与失败边界

PDF 批次结束返回顶层 `Ret` 字段，下表不是 `data` 内对象。准备阶段直接失败可能只有状态和提示；调用方不能假定下列字段总存在。

| 字段 | 说明 |
|---|---|
| `successCount` | 成功 PDF 数量 |
| `failCount`、`failures` | 错误条数及明细；包含任务收尾错误时不等于失败产品数 |
| `warnings` | 参数缺失、清理异常等提示 |
| `outputs` | 已成功正式发布或生成归档副本的 PDF URL 列表 |
| `archiveUrl` | 成功的 RAR 地址，无归档时为空 |
| `taskUrl` | 本次任务目录地址 |

**PDF 部分成功仍返回失败状态**，成功产物不会因此撤销；这与审批批次的部分成功协议不同。失败记录在收尾阶段写入 `导出失败记录.txt`，归档有成功副本时一起入包；若随后压缩失败，还会增加收尾错误。

当前页面提示结果数量、失败明细和警告，并刷新列表；虽然响应提供 `outputs/archiveUrl`，现有提示组件没有列出这些文件链接，也没有自动下载归档。正式 PDF 可从刷新后的产品列表入口查看，不能将接口返回地址等同于页面已展示下载链接。

前端请求超时不是服务端任务已取消的证据；重试是新的独立任务。不能以接口返回成功代替文件存在、签名正确、PDF 可读及 RAR 可用的实际验收。

## 源码定位

- [ExcelService](../../src/main/java/cn/jbolt/admin/siargo/qarep/uploadImport/ExcelService.java)、[ProductSeriesMatcher](../../src/main/java/cn/jbolt/admin/siargo/qarep/product/ProductSeriesMatcher.java)：模板解析、分组及匹配。
- [PdfFolderAdminController](../../src/main/java/cn/jbolt/admin/siargo/qarep/pdffolder/PdfFolderAdminController.java)、[PdfFolderService](../../src/main/java/cn/jbolt/admin/siargo/qarep/pdffolder/PdfFolderService.java)、[PdfTemplateService](../../src/main/java/cn/jbolt/admin/siargo/qarep/pdffolder/PdfTemplateService.java)：版号、文件、绑定和真实响应字段。
- [PDFService](../../src/main/java/cn/jbolt/admin/siargo/qarep/uploadImport/PDFService.java)：两种模式、生成条件、月份边界及批次结果。
- [ReportTemplateResolver](../../src/main/java/cn/jbolt/admin/siargo/qarep/uploadImport/ReportTemplateResolver.java)、[ReportFieldMapper](../../src/main/java/cn/jbolt/admin/siargo/qarep/uploadImport/ReportFieldMapper.java)、[PdfRenderer](../../src/main/java/cn/jbolt/admin/siargo/qarep/uploadImport/PdfRenderer.java)：选择、映射和文件验证。
- [QareportService](../../src/main/java/cn/jbolt/admin/siargo/qarep/QareportService.java) 的 `publishPdf`、[ProductService](../../src/main/java/cn/jbolt/admin/siargo/qarep/product/ProductService.java)：正式发布和引用清理。
- [RarArchiver](../../src/main/java/cn/jbolt/admin/siargo/qarep/uploadImport/RarArchiver.java)、[PdfStoragePaths](../../src/main/java/cn/jbolt/admin/siargo/qarep/uploadImport/PdfStoragePaths.java)、[SiargoStorage](../../src/main/java/cn/jbolt/common/storage/SiargoStorage.java)：归档和路径。

本次未上传 Excel/PDF、检查当前模板文件、调用 WinRAR 或生成报告；文档中的流程与保护来自源码，环境可用性仍需实际操作确认。
