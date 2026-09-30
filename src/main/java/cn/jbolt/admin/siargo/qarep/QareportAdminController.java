package cn.jbolt.admin.siargo.qarep;

import cn.jbolt.admin.siargo.qarep.product.ProductRejectLogService;
import cn.jbolt.admin.siargo.qarep.product.ProductService;
import cn.jbolt.admin.siargo.qarep.siargoconst.QarepConst;
import cn.jbolt.admin.siargo.qarep.uploadImport.ExcelService;
import cn.jbolt.admin.siargo.qarep.uploadImport.PDFService;
import com.jfinal.aop.Inject;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.CheckPermission;
import cn.jbolt.core.permission.UnCheckIfSystemAdmin;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.admin.siargo.customer.CustomerService;

import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import java.time.LocalDate;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.siargo.model.Product;
import cn.jbolt.siargo.model.Qareport;

/**
 * 检验报告单管理 Controller
 *
 * @ClassName: QareportAdminController
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
@CheckPermission(PermissionKey.SIARGO)
@UnCheckIfSystemAdmin
@Path(value = "/admin/siargo/qarep", viewPath = "/_view/admin/siargo/qarep")

public class QareportAdminController extends JBoltBaseController {

    private static final Log LOG = Log.getLog(QareportAdminController.class);

    /**
     * 检验报告单服务
     */
    @Inject
    private QareportService service;
    /**
     * PDF生成服务
     */
    @Inject
    private PDFService pdfservice;
    /**
     * Excel解析服务
     */
    @Inject
    private ExcelService excelservice;
    /**
     * 产品服务
     */
    @Inject
    private ProductService proservice;
    /**
     * 客户服务
     */
    @Inject
    private CustomerService custservice;
    @Inject
    private QarepFlowRangeService flowRangeService;
    /**
     * 角色服务
     */
    @Inject
    private RoleService roleService;
    /**
     * 产品驳回历史服务
     */
    @Inject
    private ProductRejectLogService productRejectLogService;

    /**
     * 解析逗号分隔的ID字符串为List&lt;Long&gt;
     *
     * @param idsJson 逗号分隔的ID字符串
     * @return 去重后的正整数ID列表；空值、空项或格式非法时返回null
     */
    private List<Long> parseIds(String idsJson) {
        if (StrKit.isBlank(idsJson)) return null;
        try {
            List<Long> ids = Arrays.stream(idsJson.split(",", -1)).map(String::trim)
                    .map(Long::parseLong).distinct().collect(Collectors.toList());
            return ids.stream().anyMatch(id -> id <= 0) ? null : ids;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 首页
     */
    public void index() {
        Long userId = JBoltUserKit.getUserId();

        // 报告单子权限：管理员/报告单 角色可覆盖，或直接拥有该子角色
        set("accuracy", roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_ACCURACY));
        set("leaktest", roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_LEAK_TEST));
        set("appearance", roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_APPEARANCE));
        set("packaging", roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_PACKAGING));
        set("approval", roleService.hasRoleOrAbove(userId, QarepConst.ROLE_SN_APPROVAL));

        LocalDate today = LocalDate.now();
        set("pdfExportYear", today.getYear());
        set("pdfExportMaxMonth", today.getMonthValue() - 1);

        render("index.html");
    }

    /**
     * 获取各流程阶段的数量统计
     * URL: /admin/siargo/qarep/getFlowCounts
     *
     * @return 各阶段数量统计
     */
    public void getFlowCounts() {
        renderJsonData(service.getFlowCounts());
    }

    /**
     * 处理Excel导入
     * URL: /admin/siargo/qarep/importExcel
     * <p>Excel导入流程：</p>
     * <ol>
     *   <li>接收上传的Excel文件</li>
     *   <li>验证文件格式为xls或xlsx</li>
     *   <li>读取Excel内容并解析为数据列表</li>
     *   <li>提取订单号、型号、编号等关键信息</li>
     *   <li>返回处理结果供前端表单使用</li>
     * </ol>
     */
    public void importExcel() {
        try {
            // 获取上传的文件
            UploadFile uploadFile = getFile("file", cn.jbolt.common.storage.SiargoStorage.forReportResources().uploadDirectory("imports", java.util.UUID.randomUUID().toString()));
            if (uploadFile == null) {
                renderJsonFail("请上传excel文件");
                return;
            }
            if (notExcel(uploadFile)) {
                // 非Excel文件早退时删除已上传的临时文件，避免临时目录堆积
                File tempFile = uploadFile.getFile();
                if (tempFile != null) {
                    cn.jbolt.common.storage.SiargoStorage.forReportResources().deleteFile(tempFile.toPath());
                }
                renderJsonFail("请上传excel文件");
                return;
            }

            File excelFile = uploadFile.getFile();

            // 统一入口：自动检测模板类型并提取数据
            Map<String, Object> result = excelservice.processExcelFile(excelFile);

            if (result == null || result.isEmpty()) {
                renderJsonFail("Excel文件中没有数据");
                return;
            }

            // 返回处理结果
            result.put("success", true);
            renderJsonData(result);

        } catch (Exception e) {
            LOG.error("Excel导入失败", e);
            renderJsonFail("导入失败：" + e.getMessage());
            return;
        }
    }

    /**
     * 按本年度已结束的月份区间批量生成已放行报告单的PDF
     * URL: /admin/siargo/qarep/toPdfs
     * <p>year、startMonth、endMonth 指定导出区间，范围为今年1月至上月</p>
     * <p>WinRAR 路径从 config.properties 的 winrar_exe_path 配置读取</p>
     *
     * @throws Exception PDF生成异常
     */
    public void toPdfs() {
        renderJson(pdfservice.generateMonthRange(getPara("year"), getPara("startMonth"), getPara("endMonth")));
    }

    /**
     * 批量生成选中报告单的PDF
     * URL: /admin/siargo/qarep/toPdf
     * <p>用于日常操作中批量导出选中报告单的PDF文件</p>
     *
     * @throws Exception PDF生成异常
     */
    public void toPdf() {
        List<Long> ids = parseIds(getPara("ids"));
        renderJson(pdfservice.generate(ids, PDFService.Mode.FORMAL));
    }

    /**
     * 查询所有客户名称列表
     * URL: /admin/siargo/qarep/getCustName
     * <p>用于新增报告单时选择客户</p>
     */
    public void getCustName() {
        renderJsonData(custservice.findAll());
    }

    /**
     * 获取报告单列表数据（分页）
     * URL: /admin/siargo/qarep/datas
     * <p>支持按日期范围、产品类型、检验进度筛选</p>
     */
    public void datas() {
        Date startTime = null;
        Date endTime = null;

        // ========== 解析日期范围参数 ==========
        if (isOk(getPara("dateRange"))) {
            // 日期范围格式：开始日期~结束日期
            String[] dates = getPara("dateRange").split("~");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (dates.length == 2) {
                try {
                    startTime = sdf.parse(dates[0].trim());
                    endTime = sdf.parse(dates[1].trim());
                } catch (ParseException e) {
                    // 日期解析失败直接返回错误，避免带着null条件继续查询
                    renderJsonFail("日期格式错误");
                    return;
                }
            }
        }

        // 获取产品类型和检验进度筛选条件
        int prodType = getInt("prodType") == null ? 0 : getInt("prodType");
        int insp = getInt("insp") == null ? 0 : getInt("insp");

        renderJsonData(service.paginateAdminDatas(getPageNumber(), getPageSize(), getKeywords(), prodType, insp, startTime, endTime));

    }

    /**
     * 新增
     */
    public void add() {
        render("add.html");
    }

    /** 新增流量范围弹窗，字典类型在服务端固定。 */
    public void addFlowRange() {
        set("nextFlowRangeSn", flowRangeService.suggestNextSn());
        render("flow_range_add.html");
    }

    /** 仅接收名称、编码，禁止请求指定字典类型、ID 或父级。 */
    public void saveFlowRange() {
        renderJson(flowRangeService.saveFlowRange(getPara("name"), getPara("sn")));
    }

    /**
     * 编辑
     */
    public void edit() {
        Qareport qareport = service.qareportFindByProId(getLong(0));
        if (qareport == null) {
            renderFail(JBoltMsg.DATA_NOT_EXIST);
            return;
        }
        set("qapro", qareport);
        List<String> largeMeterSeriesIds = service.findLargeMeterSeriesIds();
        set("largeMeterSeriesIds", String.join(",", largeMeterSeriesIds));
        set("showLargeMeterParams", largeMeterSeriesIds.contains(qareport.getStr("siargo_prod_model_id")));
        render("edit.html");
    }

    /**
     * 编辑产品描述页面
     * URL: /admin/siargo/qarep/editDes
     *
     * @param id 产品ID（URL路径参数）
     */
    public void editDes() {
        // 查询走ProductService，Controller不直接操作Model
        Product product = proservice.findById(getLong(0));
        if (product == null) {
            renderFail(JBoltMsg.DATA_NOT_EXIST);
            return;
        }
        set("product", product);
        render("editdes.html");
    }


    /**
     * 报告单详情页面
     * URL: /admin/siargo/qarep/details
     *
     * @param id 产品ID（请求参数）
     */
    public void details() {
        Long proId = getParaToLong("id");
        if (proId == null) {
            renderFail("参数错误");
            return;
        }
        Qareport qareport = service.qareportFindByProId(proId);
        if (qareport == null) {
            renderFail(JBoltMsg.DATA_NOT_EXIST);
            return;
        }
        set("qapro", qareport);
        // 查询该报告单下的全部产品
        List<Product> products = service.findProductsByReportId(qareport.getLong("id"));
        set("products", products);
        render("details.html");
    }

    /**
     * 批量检验批准操作
     * URL: /admin/siargo/qarep/batchInspection
     * <p>根据检验进度更新不同级别的批准信息：</p>
     * <ul>
     *   <li>insp=2：精度检验批准</li>
     *   <li>insp=6：成品检漏检验批准</li>
     *   <li>insp=3：外观检验批准</li>
     *   <li>insp=4：包装检验批准</li>
     *   <li>insp=5：批准放行</li>
     * </ul>
     * <p>服务端会按目标环节校验当前用户角色（211~215，超管豁免），并使用条件更新防并发</p>
     */
    public void batchInspection() {
        Integer insp = getParaToInt("insp");
        String idsJson = getPara("ids");
        // 参数校验：目标阶段与ID列表必填
        if (insp == null || StrKit.isBlank(idsJson)) {
            renderJsonFail("参数错误");
            return;
        }
        List<Long> ids = parseIds(idsJson);
        if (ids == null || ids.isEmpty()) {
            renderJsonFail("参数格式错误");
            return;
        }

        // Db.tx() 手动事务 —— 缓存清理和通知均在事务提交后执行
        final Ret[] retHolder = {null};
        boolean txOk = Db.tx(() -> {
            retHolder[0] = service.batchUpdateInspStatus(ids, insp);
            return retHolder[0] != null && retHolder[0].isOk();
        });
        if (!txOk) {
            renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "操作失败");
            return;
        }
        // === afterCommit: 缓存清理 + 异步通知 ===
        service.clearFlowCountsCache();
        service.cleanupInvalidatedPdfs(retHolder[0]);
        service.notifyNextStageUsers(insp);
        String msg = retHolder[0].getStr("msg");
        if (StrKit.notBlank(msg)) {
            // 部分成功：把未成功的单号信息反馈给前端
            renderJsonSuccess(msg);
        } else {
            renderJsonSuccess();
        }
    }

    /**
     * 审批工作台页面（JBoltLayer抽屉iframe加载）
     * URL: /admin/siargo/qarep/approval
     * <p>根据目标检验阶段和选中产品ID列表加载待审批产品数据：</p>
     * <ul>
     *   <li>insp=2：精度检验批准</li>
     *   <li>insp=6：成品检漏检验批准</li>
     *   <li>insp=3：外观检验批准</li>
     *   <li>insp=4：包装检验批准</li>
     *   <li>insp=5：批准放行</li>
     * </ul>
     */
    public void approval() {
        Integer insp = getParaToInt("insp");
        String idsJson = getPara("ids");

        // 参数校验：目标阶段必须在2~5范围内，且产品ID列表不能为空
        if (insp == null || insp < QarepConst.INSP_APPROVE_MIN || insp > QarepConst.INSP_APPROVE_MAX || StrKit.isBlank(idsJson)) {
            renderFail("参数错误");
            return;
        }

        List<Long> ids = parseIds(idsJson);
        if (ids == null || ids.isEmpty()) {
            renderFail("参数格式错误");
            return;
        }

        List<Record> products = service.findApprovalProducts(ids);
        // 精度审批（insp=2）时，下一环节名称按选中产品的成品检漏标记动态展示：
        // 全有→成品检漏待检，全无→外观待检，混合→“成品检漏/外观待检”
        if (insp == QarepConst.INSP_PENDING_APPEARANCE) {
            boolean anyLt = false;
            boolean allLt = true;
            for (Record p : products) {
                Integer ltStatus = p.getInt("lt_status");
                boolean hasLt = ltStatus != null && ltStatus == QarepConst.LT_STATUS_YES;
                if (hasLt) {
                    anyLt = true;
                } else {
                    allLt = false;
                }
            }
            set("nextStageName", allLt ? "成品检漏待检" : (anyLt ? "成品检漏/外观待检" : "外观待检"));
        }
        set("insp", insp);
        set("products", products);
        render("approval.html");
    }

    /**
     * 批量驳回至上一阶段
     * URL: /admin/siargo/qarep/batchReject
     * <p>将选中产品的检验进度回退到上一阶段，同时记录驳回原因、驳回人和驳回时间</p>
     * <p>服务端会按当前环节校验当前用户角色（212~215，超管豁免），并使用条件更新防并发</p>
     */
    public void batchReject() {
        String idsJson = getPara("ids");
        String rejectDes = getPara("rejectDes");

        // 参数校验：产品ID列表不能为空
        if (StrKit.isBlank(idsJson)) {
            renderJsonFail("请选择要驳回的数据");
            return;
        }
        // 参数校验：驳回原因必填
        if (StrKit.isBlank(rejectDes)) {
            renderJsonFail("请填写驳回原因");
            return;
        }

        List<Long> ids = parseIds(idsJson);
        if (ids == null || ids.isEmpty()) {
            renderJsonFail("参数格式错误");
            return;
        }

        // Db.tx() 手动事务 —— 缓存清理在事务提交后执行
        final String trimmedDes = rejectDes.trim();
        final Ret[] retHolder = {null};
        boolean txOk = Db.tx(() -> {
            retHolder[0] = service.batchRejectInspStatus(ids, trimmedDes);
            return retHolder[0] != null && retHolder[0].isOk();
        });
        if (!txOk) {
            renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "操作失败");
            return;
        }
        // === afterCommit: 缓存清理 ===
        service.clearFlowCountsCache();
        service.cleanupInvalidatedPdfs(retHolder[0]);
        String msg = retHolder[0].getStr("msg");
        if (StrKit.notBlank(msg)) {
            renderJsonSuccess(msg);
        } else {
            renderJsonSuccess();
        }
    }

    /**
     * 产品驳回历史弹窗
     * URL: /admin/siargo/qarep/rejectHistory?productId=xxx
     * <p>展示指定产品的全部驳回记录（按时间倒序）</p>
     */
    public void rejectHistory() {
        Long productId = getParaToLong("productId");
        if (productId == null) {
            renderFail("参数错误");
            return;
        }
        set("logs", productRejectLogService.findLogsByProductId(productId));
        render("reject_history.html");
    }


    /** 保存一份报告头及独立的产品卡片，由 Service 完整持有事务。 */
    public void save() {
        renderJson(service.saveProducts(getModel(Qareport.class, "qareport"),
                getModel(Product.class, "product"), getPara("productsJson")));
    }

    /** 编辑单产品及其所属报告头，与新增使用相同系列和参数校验。 */
    public void update() {
        renderJson(service.update(getModel(Qareport.class, "qareport"), getModel(Product.class, "product")));
    }

    /**
     * 更新Des
     * <p>Db.tx() 包裹事务，更新成功后清分页缓存</p>
     */
    public void updateDes() {
        Product prold = getModel(Product.class, "product");
        if (prold == null || notOk(prold.getId())) {
            renderJsonFail(JBoltMsg.PARAM_ERROR);
            return;
        }
        final Ret[] retHolder = {null};
        boolean txOk = Db.tx(() -> {
            retHolder[0] = service.updateDes(prold.getId(), prold.getDes());
            return retHolder[0] != null && retHolder[0].isOk();
        });
        if (txOk) {
            // 统一联动：产品信息变更后刷新流程统计/看板缓存（含首页 dashboard）
            service.clearFlowCountsCache();
            service.cleanupInvalidatedPdfs(retHolder[0]);
        }
        if (!txOk) {
            renderJsonFail(retHolder[0] != null && retHolder[0].isFail() ? retHolder[0].getStr("msg") : "更新失败");
            return;
        }
        renderJson(retHolder[0] != null ? retHolder[0] : Ret.fail("更新失败"));
    }

    /**
     * 删除（软删除到回收站）
     */
    public void deleteByIds() {
        String idsJson = getPara("ids");
        if (StrKit.isBlank(idsJson)) {
            renderFail("请选择要删除的数据");
            return;
        }
        String deleteDes = getPara("delete_des"); // 删除原因
        // 服务端强制必填（前端弹窗已校验，此处兜底防绕过）
        if (StrKit.isBlank(deleteDes)) {
            renderJsonFail("请填写删除原因！");
            return;
        }

        List<Long> ids = parseIds(idsJson);
        if (ids == null || ids.isEmpty()) {
            renderFail("请选择要删除的数据");
            return;
        }

        final Ret[] retHolder = {null};
        boolean txOk = Db.tx(() -> {
            retHolder[0] = service.batchSoftDeleteProduct(ids, deleteDes);
            return retHolder[0] != null && retHolder[0].isOk();
        });
        if (!txOk) {
            renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "删除失败");
            return;
        }
        // === afterCommit: 缓存清理 ===
        service.clearFlowCountsCache();
        renderJsonSuccess();
    }

    /**
     * 回收站列表页面（服务端渲染 + JBolt原生分页）
     * URL: /admin/siargo/qarep/inactiveList
     */
    public void inactiveList() {
        render("inactiveList.html");
    }

    /**
     * 回收站列表AJAX数据接口（用于dialog弹窗内搜索和分页）
     * URL: /admin/siargo/qarep/inactiveDatas
     */
    public void inactiveDatas() {
        renderJsonData(service.paginateInactiveListDatas(getPageNumber(), getPageSize(), getKeywords()));
    }

    /**
     * 恢复报告单（从回收站还原）
     * URL: /admin/siargo/qarep/restore/:id
     */
    public void restore() {
        Long id = getLong(0);
        if (id == null) {
            renderFail("参数错误");
            return;
        }
        final boolean[] successHolder = {false};
        boolean txOk = Db.tx(() -> {
            successHolder[0] = service.restoreProduct(id);
            return successHolder[0];
        });
        if (!txOk || !successHolder[0]) {
            renderFail("数据不存在");
            return;
        }
        // === afterCommit: 缓存清理 ===
        service.clearFlowCountsCache();
        renderJsonSuccess();
    }

    /**
     * 永久删除报告单（物理删除）
     * URL: /admin/siargo/qarep/permanentDelete
     * <p>afterCommit 模式：物理文件删除不可回滚，必须在事务提交后执行——</p>
     * <ol>
     *   <li>Db.tx() 锁定并校验回收站产品，读取最终PDF地址，再级联删除数据库记录</li>
     *   <li>事务提交成功后检查文件引用并清理PDF，同时清理缓存</li>
     * </ol>
     */
    public void permanentDelete() {
        String idsJson = getPara("ids");
        if (StrKit.isBlank(idsJson)) {
            renderFail("参数错误");
            return;
        }
        List<Long> ids = parseIds(idsJson);
        if (ids == null || ids.isEmpty()) {
            renderFail("参数格式错误");
            return;
        }
        // 手动事务：锁内校验全部回收站状态，并返回实际删除记录的最终PDF地址。
        final Ret[] retHolder = {null};
        boolean txOk = Db.tx(() -> {
            retHolder[0] = service.permanentDelete(ids);
            return retHolder[0] != null && retHolder[0].isOk();
        });
        if (!txOk) {
            renderJsonFail(retHolder[0] != null ? retHolder[0].getStr("msg") : "删除失败");
            return;
        }
        // === afterCommit: 缓存清理 + 物理文件删除 ===
        service.clearFlowCountsCache();
        service.cleanupInvalidatedPdfs(retHolder[0]);
        renderJsonSuccess();
    }

}
