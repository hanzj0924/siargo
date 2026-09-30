package cn.jbolt.admin.siargo.qarep;

import cn.jbolt.admin.siargo.qarep.product.ProductRejectLogService;
import cn.jbolt.admin.siargo.qarep.product.ProductService;
import cn.jbolt.admin.siargo.qarep.product.ProductSeriesService;
import cn.jbolt.admin.siargo.qarep.product.ReportProductInput;
import cn.jbolt.admin.siargo.qarep.pdffolder.PdfTemplateService;
import cn.jbolt.admin.siargo.customer.CustomerService;
import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.admin.siargo.qarep.siargoconst.QarepConst;
import cn.jbolt.siargo.model.Product;
import cn.jbolt.siargo.model.PdfTemplate;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;

import cn.hutool.core.util.EscapeUtil;
import cn.jbolt.common.model.Todo;
import cn.jbolt.common.util.DateUtil;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.User;
import cn.jbolt.siargo.model.Qareport;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt._admin.user.UserService;
import net.dreamlu.event.EventKit;

/**
 * 检验报告单管理 Service
 * 
 * @ClassName: QareportService
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
public class QareportService extends JBoltBaseService<Qareport> {

	private static final Log LOG = Log.getLog(QareportService.class);
    private static final String PRODUCT_TYPE_NAME_SQL = ProductSeriesService.typeNameSql("pm", "d_type");
    private static final List<Map.Entry<String, String>> DASHBOARD_CATEGORIES = List.of(
            Map.entry("large_meter", "大表"), Map.entry("small_flow", "小流量计"), Map.entry("sensor", "传感器"));
    // 同系列、同版号先合并到唯一模板类别，避免一对多关联重复累计；停用不改变历史报告分类。
    private static final String DASHBOARD_TEMPLATE_CATEGORY_JOIN =
            " INNER JOIN (SELECT mapped.siargo_prod_model_id, mapped.pdfver, MIN(mapped.category) AS sn"
            + " FROM (SELECT b.siargo_prod_model_id, t.pdfver, CASE"
            + " WHEN t.template_file IN ('中低压模板.pdf','工业表模板.pdf') THEN 'large_meter'"
            + " WHEN t.template_file IN ('小流量计模板.pdf','控制器模板.pdf') THEN 'small_flow'"
            + " WHEN t.template_file = '传感器模板.pdf' THEN 'sensor' END AS category"
            + " FROM siargo_pdf_template_prod b INNER JOIN siargo_pdf_template t ON t.id=b.siargo_pdf_template_id) mapped"
            + " GROUP BY mapped.siargo_prod_model_id, mapped.pdfver"
            + " HAVING COUNT(*)=COUNT(mapped.category) AND COUNT(DISTINCT mapped.category)=1) category"
            + " ON category.siargo_prod_model_id=sp.siargo_prod_model_id"
            + " AND category.pdfver COLLATE utf8mb4_general_ci=sp.pdfver COLLATE utf8mb4_general_ci ";
	// ========== 流程统计缓存（30分钟有效期） ==========
	private static final long FLOW_COUNTS_CACHE_TTL = 30 * 60 * 1000L; // 30分钟
	// ========== 看板报告单级流程统计缓存（30分钟有效期，与列表页产品级 flowCounts 分开） ==========
	private static final long DASHBOARD_FLOW_COUNTS_CACHE_TTL = 30 * 60 * 1000L; // 30分钟
	// ========== 年度数量总计缓存（30分钟有效期，key = 列名+类型） ==========
	private static final long TOTAL_COUNTS_CACHE_TTL = 30 * 60 * 1000L; // 30分钟
	// ========== 管理端分页数据缓存（30秒有效期，降低重复查询开销） ==========
	// 仅缓存"空关键字 + 无日期范围 + 第一页"的查询，key空间有限（prodType×insp×pageSize），防止无限增长
	private static final long PAGINATE_CACHE_TTL = 30 * 1000L;
	/** 检验报告单数据访问对象 */
	private final Qareport dao = new Qareport().dao();
	private final ReentrantLock flowCountsCacheLock = new ReentrantLock();
	private final ReentrantLock dashboardFlowCountsCacheLock = new ReentrantLock();
	private final ReentrantLock totalCountsCacheLock = new ReentrantLock();
	private final ReentrantLock paginateCacheLock = new ReentrantLock();
	private volatile Map<String, Long> cachedFlowCounts;
	private volatile long flowCountsCacheTimestamp;
	private volatile Map<String, Long> cachedDashboardFlowCounts;
	private volatile long dashboardFlowCountsCacheTimestamp;
	private volatile Map<String, Long> cachedTotalCounts;
	private volatile long totalCountsCacheTimestamp;
	private volatile Map<String, Page<Record>> cachedPaginateData;
	private volatile long paginateCacheTimestamp;
	/** 用户服务（用于查询拥有指定角色的用户列表） */
	@Inject
	private UserService userService;

	/** 产品驳回历史服务（一个产品可有多条驳回记录） */
	@Inject
	private ProductRejectLogService productRejectLogService;

	/** 角色服务（用于根据 SN 查询角色ID） */
	@Inject
	private RoleService roleService;

	/** 产品服务 */
	@Inject
	private ProductService productService;
    @Inject private ProductSeriesService seriesService;
    @Inject private PdfTemplateService templateService;
    @Inject private CustomerService customerService;

    /** 备注编辑与完整编辑采用相同长度、有效状态和PDF失效规则。 */
    static Ret prepareDescriptionEdit(Product product, String description) {
        if (product == null) return Ret.fail(JBoltMsg.DATA_NOT_EXIST);
        if (!Integer.valueOf(QarepConst.VD_VALID).equals(product.getInt("vd")))
            return Ret.fail("产品已删除，请从回收站恢复后编辑");
        String value = description == null ? "" : description.trim();
        if (value.length() > 1000) return Ret.fail("产品描述不能超过 1000 个字符");
        List<String> stalePdfs = new ArrayList<>();
        boolean changed = !Objects.equals(product.getStr("des"), value);
        if (changed) {
            addPdfUrl(stalePdfs, product.getStr("pdfstr"));
            product.set("des", value).set("pdfstr", null);
        }
        return Ret.ok().set("descriptionChanged", changed).set("pdfCleanupUrls", stalePdfs);
    }
	
    static String validatePermanentDeleteProducts(List<Product> products) {
        for (Product product : products) {
            if (!Integer.valueOf(QarepConst.VD_DELETED).equals(product.getInt("vd")))
                return "仅允许永久删除回收站中的产品，请先移入回收站（ID=" + product.getLong("id") + "）";
        }
        return null;
    }

    private static void addPdfUrl(List<String> urls, String url) {
        if (url != null && !url.isBlank() && !urls.contains(url)) urls.add(url);
    }

    static List<String> splitLogDescription(String text) {
        List<String> parts = new ArrayList<>();
        String source = text == null ? "" : text;
        for (int offset = 0; offset < source.length();) {
            int end = source.offsetByCodePoints(offset, Math.min(180, source.codePointCount(offset, source.length())));
            parts.add(source.substring(offset, end));
            offset = end;
        }
        return parts.isEmpty() ? List.of("") : parts;
    }

    static boolean samePdfSnapshot(Qareport expected, Qareport current) {
        return expected != null && current != null && expected.toMap().equals(current.toMap());
    }

    static String chartTypeName(String key, String label) {
        if (label != null && !label.isBlank()) return label;
        if (key == null || key.isEmpty()) return "未关联系列";
        if ("__unclassified__".equals(key)) return "系列类型未设置";
        return "类型（" + key + "）";
    }

	@Override
	protected Qareport dao() {
		return dao;
	}

	/**
	 * 获取各流程阶段的数量统计（带30分钟缓存）
	 * <p>返回不可变Map，防止调用方误改缓存内容</p>
	 * @return Map包含各阶段数量：all(全部), noq(精度待检), ltq(成品检漏待检), accq(外观待检), funq(包装待检), appq(待批准), allq(已完成)
	 */
	public java.util.Map<String, Long> getFlowCounts() {
		// 先检查缓存是否有效（无锁快速路径）
		if (cachedFlowCounts != null && (System.currentTimeMillis() - flowCountsCacheTimestamp) < FLOW_COUNTS_CACHE_TTL) {
			return cachedFlowCounts;
		}
		// 缓存失效，加锁查询并刷新缓存
		flowCountsCacheLock.lock();
		try {
			// 双重检查：防止多线程同时穿透
			if (cachedFlowCounts != null && (System.currentTimeMillis() - flowCountsCacheTimestamp) < FLOW_COUNTS_CACHE_TTL) {
				return cachedFlowCounts;
			}
			// 存入不可变视图，getFlowCounts 对外始终只读
			Map<String, Long> counts = Collections.unmodifiableMap(loadFlowCountsFromDb());
			cachedFlowCounts = counts;
			flowCountsCacheTimestamp = System.currentTimeMillis();
			return counts;
		} finally {
			flowCountsCacheLock.unlock();
		}
	}

	/**
	 * 获取首页看板流程统计（当前在检、本年度已完成，带30分钟缓存）
	 * <p>产品级口径：有效产品（vd=1）的在检环节按当前 insp 统计，不限制时间；
	 * 已完成产品（insp=5）按批准时间 allq_time 归属年度，不去重。</p>
	 * @return Map包含各阶段数量：all(在检与本年度已完成总数), allq(本年度已完成数), noq/ltq/accq/funq/appq(当前在检数),
	 *         noq_qsi~allq_qsi(各环节产品送检只数)
	 */
	public java.util.Map<String, Long> getDashboardFlowCounts() {
		// 先检查缓存是否有效（无锁快速路径）
		if (cachedDashboardFlowCounts != null
				&& (System.currentTimeMillis() - dashboardFlowCountsCacheTimestamp) < DASHBOARD_FLOW_COUNTS_CACHE_TTL) {
			return cachedDashboardFlowCounts;
		}
		// 缓存失效，加锁查询并刷新缓存
		dashboardFlowCountsCacheLock.lock();
		try {
			// 双重检查：防止多线程同时穿透
			if (cachedDashboardFlowCounts != null
					&& (System.currentTimeMillis() - dashboardFlowCountsCacheTimestamp) < DASHBOARD_FLOW_COUNTS_CACHE_TTL) {
				return cachedDashboardFlowCounts;
			}
			// 存入不可变视图，getDashboardFlowCounts 对外始终只读
			Map<String, Long> counts = Collections.unmodifiableMap(loadDashboardFlowCountsFromDb());
			cachedDashboardFlowCounts = counts;
			dashboardFlowCountsCacheTimestamp = System.currentTimeMillis();
			return counts;
		} finally {
			dashboardFlowCountsCacheLock.unlock();
		}
	}

	/**
	 * 主动清除流程统计缓存（数据变更时调用）
	 * <p>联动清理：年度数量总计缓存（getTotalQSI/getTotalQI）与流程统计同源，一并失效</p>
	 */
	public void clearFlowCountsCache() {
		cachedFlowCounts = null;
		flowCountsCacheTimestamp = 0;
		cachedDashboardFlowCounts = null;
		dashboardFlowCountsCacheTimestamp = 0;
		cachedTotalCounts = null;
		totalCountsCacheTimestamp = 0;
		clearPaginateCache();
	}

	/**
	 * 主动清除管理端分页数据缓存（数据变更时调用）
	 */
	public void clearPaginateCache() {
		cachedPaginateData = null;
		paginateCacheTimestamp = 0;
	}

	/**
	 * 校验当前用户是否具备目标环节的批准权限（超管豁免）
	 * @param userId 用户ID
	 * @param targetInsp 目标检验进度（2~5）
	 * @return 是否有权限
	 */
	private boolean canApproveStage(Long userId, int targetInsp) {
		if (JBoltUserKit.isSystemAdmin()) {
			return true;
		}
		int roleSn = QarepConst.approveRoleSn(targetInsp);
		// hasRoleOrAbove 内部已包含管理员角色(SN=1)豁免与上级角色覆盖逻辑
		return roleSn > 0 && roleService.hasRoleOrAbove(userId, roleSn);
	}

	/**
	 * 校验当前用户是否具备当前环节的驳回权限（超管豁免）
	 * @param userId 用户ID
	 * @param currentInsp 产品当前检验进度（2~4）
	 * @return 是否有权限
	 */
	private boolean canRejectStage(Long userId, int currentInsp) {
		if (JBoltUserKit.isSystemAdmin()) {
			return true;
		}
		int roleSn = QarepConst.rejectRoleSn(currentInsp);
		return roleSn > 0 && roleService.hasRoleOrAbove(userId, roleSn);
	}

	/**
	 * 生成产品可读描述（报告单号+型号/编号），用于批量操作失败信息提示
	 * @param id 产品ID
	 * @return 可读描述
	 */
	private String describeProduct(Long id) {
		Record r = Db.findFirst(
				"SELECT sp.model, sp.number, sq.formnum FROM siargo_product sp "
				+ "LEFT JOIN siargo_qareport sq ON sq.id = sp.report_id WHERE sp.id = ?", id);
		if (r == null) {
			return "ID:" + id;
		}
		StringBuilder sb = new StringBuilder();
		Object formnum = r.get("formnum");
		sb.append(formnum != null ? "单号" + formnum : "ID:" + id);
		String model = r.getStr("model");
		String number = r.getStr("number");
		if (model != null || number != null) {
			sb.append("(").append(model != null ? model : "")
			  .append("/").append(number != null ? number : "").append(")");
		}
		return sb.toString();
	}

	/**
	 * 批量更新产品检验状态（批准操作）
	 * <p>安全与并发控制：</p>
	 * <ul>
	 *   <li>服务端角色校验：目标环节对应角色（211~214）或超管才可操作</li>
	 *   <li>条件更新（乐观并发）：UPDATE ... WHERE id=? AND insp=目标-1 AND vd=1，按受影响行数判定</li>
	 *   <li>部分失败时返回信息指明哪些单号未成功（状态已变化/已被他人处理）</li>
	 * </ul>
	 * @param ids 产品ID列表
	 * @param insp 目标检验阶段（2~5）
	 * @return 操作结果；部分成功时 Ret.ok 且携带 msg 说明
	 */
	public Ret batchUpdateInspStatus(List<Long> ids, Integer insp) {
		// 入口校验：insp必须在[2,6]范围内（2=精度审批 6=成品检漏审批 3=外观审批 4=包装审批 5=批准）
		if (ids == null || ids.isEmpty() || insp == null
				|| insp < QarepConst.INSP_APPROVE_MIN || insp > QarepConst.INSP_APPROVE_MAX) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Long userId = JBoltUserKit.getUserId();
		// 服务端角色校验（超管豁免）
		if (!canApproveStage(userId, insp)) {
			return fail("您没有该检验环节的批准权限，无法执行此操作");
		}
		String stageCol = QarepConst.approveStageColumn(insp);
		if (stageCol == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		String now = DateUtil.getDateString(DateUtil.YMDHMS);
		List<String> failedItems = new ArrayList<>();
        List<String> stalePdfs = new ArrayList<>();
		int successCount = 0;
		for (Long id : ids.stream().filter(Objects::nonNull).distinct().sorted().toList()) {
			// 读取产品当前状态与成品检漏标记，逐产品计算目标状态
			Record cur = Db.findFirst(
					"SELECT insp, lt_status, pdfstr FROM siargo_product WHERE id = ? AND vd = " + QarepConst.VD_VALID + " FOR UPDATE", id);
			if (cur == null) {
				failedItems.add("ID:" + id + "（数据不存在）");
				continue;
			}
			int ltStatus = cur.getInt("lt_status") == null ? QarepConst.LT_STATUS_NO : cur.getInt("lt_status");
			// 各审批操作的前置状态与目标状态：精度审批按 lt_status 分流到 6 或 2
			int prevInsp;
			int newInsp;
			switch (insp) {
				case QarepConst.INSP_PENDING_APPEARANCE: // 2：精度审批
					prevInsp = QarepConst.INSP_PENDING_ACCURACY;
					newInsp = ltStatus == QarepConst.LT_STATUS_YES
							? QarepConst.INSP_PENDING_LEAK_TEST
							: QarepConst.INSP_PENDING_APPEARANCE;
					break;
				case QarepConst.INSP_PENDING_LEAK_TEST: // 6：成品检漏审批
					prevInsp = QarepConst.INSP_PENDING_LEAK_TEST;
					newInsp = QarepConst.INSP_PENDING_APPEARANCE;
					break;
				case QarepConst.INSP_PENDING_PACKAGING: // 3：外观审批
					prevInsp = QarepConst.INSP_PENDING_APPEARANCE;
					newInsp = QarepConst.INSP_PENDING_PACKAGING;
					break;
				case QarepConst.INSP_PENDING_APPROVAL: // 4：包装审批
					prevInsp = QarepConst.INSP_PENDING_PACKAGING;
					newInsp = QarepConst.INSP_PENDING_APPROVAL;
					break;
				case QarepConst.INSP_COMPLETED: // 5：批准
					prevInsp = QarepConst.INSP_PENDING_APPROVAL;
					newInsp = QarepConst.INSP_COMPLETED;
					break;
				default:
					failedItems.add(describeProduct(id) + "（非法审批目标）");
					continue;
			}
			// 条件更新：仅当前状态匹配且有效时才更新，避免并发重复处理/状态跳跃
			int rows = Db.update(
					"UPDATE siargo_product SET insp = ?, pdfstr = NULL, " + stageCol + "_uid = ?, " + stageCol + "_time = ? "
					+ "WHERE id = ? AND insp = ? AND vd = " + QarepConst.VD_VALID,
					newInsp, userId, now, id, prevInsp);
			if (rows > 0) {
				successCount++;
                addPdfUrl(stalePdfs, cur.getStr("pdfstr"));
			} else {
				failedItems.add(describeProduct(id));
			}
		}
		if (failedItems.isEmpty()) {
			return Ret.ok().set("pdfCleanupUrls", stalePdfs);
		}
		String failMsg = "以下产品未处理成功（当前状态已变化或已被他人处理）：" + String.join("、", failedItems);
		if (successCount == 0) {
			return fail(failMsg);
		}
		// 部分成功：成功的行保留，失败明细通过msg返回
		return Ret.ok().set("msg", "已成功处理 " + successCount + " 条。" + failMsg).set("pdfCleanupUrls", stalePdfs);
	}

	/**
	 * 批量驳回产品检验状态至上一阶段
 * <p>状态回退映射：</p>
 * <ul>
 *   <li>insp=2（外观待检）→ lt_status=1 回退到 6（成品检漏待检）并清空 lt_uid/lt_time；否则回退到 1（精度待检）并清空 accq_uid/accq_time</li>
 *   <li>insp=6（成品检漏待检）→ insp=1（精度待检），清空精度检验完成记录（accq_uid/accq_time）</li>
 *   <li>insp=3（包装待检）→ insp=2（外观待检），清空外观检验完成记录（funq_uid/funq_time）</li>
 *   <li>insp=4（待批准）→ insp=3（包装待检），清空包装检验完成记录（appq_uid/appq_time）</li>
 *   <li>insp=5（已完成）与 insp=1（精度待检）不可驳回</li>
	 * </ul>
	 * <p>安全与并发控制：逐产品做服务端角色校验（当前环节负责角色或超管）+ 条件更新（WHERE insp=当前值）</p>
	 * <p>每次驳回向历史表 siargo_product_reject_log 追加一条记录（环节/原因/驳回人/时间），支持一个产品多次驳回</p>
	 * @param ids 产品ID列表
	 * @param rejectDes 驳回原因
	 * @return 操作结果；部分成功时 Ret.ok 且携带 msg 说明
	 */
	public Ret batchRejectInspStatus(List<Long> ids, String rejectDes) {
		if (ids == null || ids.isEmpty() || notOk(rejectDes)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Long userId = JBoltUserKit.getUserId();
		List<String> failedItems = new ArrayList<>();
        List<String> stalePdfs = new ArrayList<>();
		int successCount = 0;
		for (Long id : ids.stream().filter(Objects::nonNull).distinct().sorted().toList()) {
			Product product = productService.findForUpdate(id);
			if (product == null) {
				failedItems.add("ID:" + id + "（数据不存在）");
				continue;
			}
			// 当前检验阶段：仅2、3、4、6可驳回（已完成insp=5与精度待检insp=1不可驳回）
			Integer cur = product.getInt("insp");
			if (cur == null || !QarepConst.isRejectableInsp(cur)) {
				failedItems.add(describeProduct(id) + "（当前状态不可驳回）");
				continue;
			}
			// 服务端角色校验：当前环节负责角色（212~215）或超管才可驳回
			if (!canRejectStage(userId, cur)) {
				failedItems.add(describeProduct(id) + "（无该环节驳回权限）");
				continue;
			}
			int ltStatus = product.getInt("lt_status") == null ? QarepConst.LT_STATUS_NO : product.getInt("lt_status");
			// 回退目标与清空列：外观环节按 lt_status 分流（1→成品检漏待检清lt，2→精度待检清accq）
			int newInsp;
			String clearCol;
			switch (cur) {
				case QarepConst.INSP_PENDING_LEAK_TEST: // 6：成品检漏驳回 → 精度待检，清空 accq
					newInsp = QarepConst.INSP_PENDING_ACCURACY;
					clearCol = "accq";
					break;
				case QarepConst.INSP_PENDING_APPEARANCE: // 2：外观驳回
					if (ltStatus == QarepConst.LT_STATUS_YES) {
						newInsp = QarepConst.INSP_PENDING_LEAK_TEST;
						clearCol = "lt";
					} else {
						newInsp = QarepConst.INSP_PENDING_ACCURACY;
						clearCol = "accq";
					}
					break;
				case QarepConst.INSP_PENDING_PACKAGING: // 3：包装驳回 → 外观待检，清空 funq
					newInsp = QarepConst.INSP_PENDING_APPEARANCE;
					clearCol = "funq";
					break;
				case QarepConst.INSP_PENDING_APPROVAL: // 4：批准驳回 → 包装待检，清空 appq
					newInsp = QarepConst.INSP_PENDING_PACKAGING;
					clearCol = "appq";
					break;
				default:
					failedItems.add(describeProduct(id) + "（当前状态不可驳回）");
					continue;
			}
			// 条件更新：清空被驳回阶段的完成记录并回退，仅当状态未被他人变更时生效
			int rows = Db.update(
					"UPDATE siargo_product SET insp = ?, pdfstr = NULL, " + clearCol + "_uid = NULL, " + clearCol + "_time = NULL "
					+ "WHERE id = ? AND insp = ? AND vd = " + QarepConst.VD_VALID,
					newInsp, id, cur);
			if (rows > 0) {
				successCount++;
                addPdfUrl(stalePdfs, product.getStr("pdfstr"));
				// 追加驳回历史记录：环节（2=外观检验 3=包装检验 4=批准 6=成品检漏）、原因、驳回人、时间
				productRejectLogService.saveLog(id, cur, rejectDes, userId);
			} else {
				failedItems.add(describeProduct(id) + "（状态已变化或已被他人处理）");
			}
		}
		if (failedItems.isEmpty()) {
			return Ret.ok().set("pdfCleanupUrls", stalePdfs);
		}
		String failMsg = "以下产品未驳回成功：" + String.join("、", failedItems);
		if (successCount == 0) {
			return fail(failMsg);
		}
		return Ret.ok().set("msg", "已成功驳回 " + successCount + " 条。" + failMsg).set("pdfCleanupUrls", stalePdfs);
	}

	/**
	 * 批量软删除产品（移至回收站）
	 * <p>删除失败时返回 fail，配合 Db.tx() 触发回滚</p>
	 * @param ids 产品ID列表
	 * @param deleteDes 删除原因
	 * @return 操作结果
	 */
	public Ret batchSoftDeleteProduct(List<Long> ids, String deleteDes) {
		for (Long id : ids) {
			Product product = productService.findById(id);
			if (product != null) {
				product.set("delete_time", DateUtil.getDateString(DateUtil.YMDHMS));
				product.set("vd", QarepConst.VD_DELETED);
				product.set("delete_des", deleteDes);
				if (!productService.updateReportProduct(product)) {
					return fail("软删除产品失败，ID=" + id);
				}
			}
		}
		return Ret.ok();
	}

	/**
	 * 恢复产品（从回收站还原）
	 * @param id 产品ID
	 * @return 是否成功
	 */
	public boolean restoreProduct(Long id) {
		Product product = productService.findById(id);
		if (product == null) {
			return false;
		}
		product.set("vd", QarepConst.VD_VALID);
		product.set("delete_time", null);
		product.set("delete_des", null);
		return productService.updateReportProduct(product);
	}

	/**
	 * 更新产品描述（备注）
	 * @param id 产品ID
	 * @param des 新的描述内容（可为空串）
	 * @return 操作结果
	 */
	public Ret updateDes(Long id, String des) {
		if (notOk(id)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		String description = des == null ? "" : des.trim();
        if (description.length() > 1000) return fail("产品描述不能超过 1000 个字符");
        // 调用方持有事务；锁定当前内容与PDF地址，发布请求必须等本次备注修改提交。
		Product product = productService.findForUpdate(id);
        Ret prepared = prepareDescriptionEdit(product, description);
        if (prepared.isFail()) return prepared;
        boolean changed = Boolean.TRUE.equals(prepared.get("descriptionChanged"));
        prepared.remove("descriptionChanged");
        if (changed && !productService.updateReportProduct(product)) return fail("产品描述更新失败");
        return prepared;
	}

	/**
	 * 批量永久删除产品（物理删除，事务性级联）
	 * <p>级联顺序：</p>
	 * <ol>
	 *   <li>删除 siargo_product_reject_log 对应驳回历史</li>
	 *   <li>删除产品记录</li>
	 *   <li>若报告单下已无产品，一并删除 siargo_qareport</li>
	 * </ol>
	 * <p>锁定全部现存产品后先验证回收站状态，再级联删除；混入有效产品时整批拒绝。</p>
	 * <p>只参与调用方事务，返回锁内读取的pdfCleanupUrls，由调用方在提交后清理文件。</p>
	 * @param ids 产品ID列表
	 * @return 操作结果
	 */
    public Ret permanentDelete(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Long userId = JBoltUserKit.getUserId();
        if (ids.stream().anyMatch(id -> id != null && id <= 0)) return fail(JBoltMsg.PARAM_ERROR);
        List<Long> orderedIds = ids.stream().filter(Objects::nonNull).distinct().sorted().toList();
        java.util.SortedSet<Long> reportIds = new java.util.TreeSet<>();
        for (Long id : orderedIds) {
            Product snapshot = productService.findById(id);
            if (snapshot != null && snapshot.getLong("report_id") != null) reportIds.add(snapshot.getLong("report_id"));
        }
        // 删除也遵循报告头→产品，避免与发布/编辑持锁次序倒置。
        for (Long reportId : reportIds) dao.findFirst("SELECT id FROM siargo_qareport WHERE id=? FOR UPDATE", reportId);
        List<Product> lockedProducts = new ArrayList<>();
        java.util.Set<Long> requestedIds = new java.util.HashSet<>(orderedIds);
        for (Product product : productService.findDeletionScopeForUpdate(reportIds, orderedIds)) {
            // 不存在的ID维持幂等跳过；已有记录必须逐项通过回收站校验。
            if (requestedIds.contains(product.getLong("id"))) lockedProducts.add(product);
        }
        String validation = validatePermanentDeleteProducts(lockedProducts);
        if (validation != null) return fail(validation);
        List<String> stalePdfs = new ArrayList<>();
		for (Product product : lockedProducts) {
            Long id = product.getLong("id");
            addPdfUrl(stalePdfs, product.getStr("pdfstr"));
			// 删除前组装日志描述（报告单编号/订单号/型号/编号/客户/删除原因）
			String logDesc = buildPermanentDeleteLogDesc(product);
			// 1. 级联删除驳回历史
			Db.delete("DELETE FROM siargo_product_reject_log WHERE product_id = ?", id);
		// 2. 删除产品记录（失败返回 fail，配合 Db.tx() 回滚）
			if (!productService.deleteReportProduct(product)) {
				return fail("产品记录删除失败，ID=" + id);
			}
			// 3. 报告单下已无产品（含回收站中的）则一并删除报告单
			Long reportId = product.getReportId();
			if (reportId != null) {
				if (!productService.hasReportProductsForUpdate(reportId)) {
					Db.deleteById("siargo_qareport", reportId);
				}
			}
			// 4. 记录永久删除系统日志
            // 系统日志标题上限255，完整业务描述按段保存，避免合法长型号使删除事务失败。
            List<String> parts = splitLogDescription(EscapeUtil.escapeHtml4(logDesc));
            for (int index = 0; index < parts.size(); index++) {
                // 原生完整标题接口不再叠加HTML/姓名前缀，用户与操作类型仍分别写入日志字段。
                addSystemLogWithTitle(id, userId, cn.jbolt.core.common.enums.JBoltSystemLogType.DELETE.getValue(),
                        systemLogTargetType(), "报告产品永久删除（" + (index + 1) + "/" + parts.size() + "）：" + parts.get(index));
            }
		}
		return Ret.ok().set("pdfCleanupUrls", stalePdfs);
	}

    /** 仅在删除事务提交成功后调用，共用历史 PDF 的其他产品不会受影响。 */
    public void deletePhysicalPdfs(List<String> urls) {
        if (urls == null) return;
        for (String url : urls) {
            try { productService.deleteUnreferencedPdf(url); }
            catch (Exception ex) { LOG.error("PDF 文件清理失败：" + url, ex); }
        }
    }

    /** 审批/驳回等由外层持有事务的入口，只能在提交成功后调用。 */
    public void cleanupInvalidatedPdfs(Ret result) {
        if (result != null && result.isOk()) deletePhysicalPdfs(result.getAs("pdfCleanupUrls"));
    }

    /** 正式发布前在同一事务中复核模板和生成快照，防止发布编辑前的内容。 */
    public Ret publishPdf(Qareport expectedReport, PdfTemplate expectedTemplate, String newPdfUrl) {
        if (expectedReport == null || expectedTemplate == null || newPdfUrl == null || newPdfUrl.isBlank()) return fail("PDF发布参数错误");
        final Long productId, reportId, seriesId;
        try {
            productId = Long.valueOf(Objects.toString(expectedReport.get("proid"), ""));
            reportId = Long.valueOf(Objects.toString(expectedReport.get("id"), ""));
            seriesId = Long.valueOf(Objects.toString(expectedReport.get("siargo_prod_model_id"), ""));
            if (productId <= 0 || reportId <= 0 || seriesId <= 0) return fail("PDF发布记录无效");
        } catch (RuntimeException invalid) { return fail("PDF发布记录无效"); }
        Ret[] result = {fail("PDF发布失败")};
        try {
            // 模板校验可能先进行普通查询；READ_COMMITTED保证等待产品锁后仍能读取最新快照。
            boolean committed = Db.tx(java.sql.Connection.TRANSACTION_READ_COMMITTED, () -> {
                result[0] = templateService.validatePublication(seriesId, expectedReport.getStr("sp_pdfver"), expectedTemplate);
                if (result[0].isFail()) return false;
                // 与编辑统一：版号/模板/系列 → 报告头 → 产品。
                Qareport header = dao.findFirst("SELECT * FROM siargo_qareport WHERE id=? FOR UPDATE", reportId);
                Product product = productService.findForUpdate(productId);
                if (header == null || product == null || !Objects.equals(product.getLong("report_id"), reportId)) {
                    result[0] = fail("报告单已被修改或删除，请刷新后重新生成"); return false;
                }
                Qareport current = qareportFindByProId(productId);
                if (!samePdfSnapshot(expectedReport, current)) {
                    result[0] = fail("报告信息、检验参数或审批记录已变化，请重新生成PDF"); return false;
                }
                if (!productService.publishPdfInTransaction(productId, expectedReport.getStr("sp_pdfstr"), newPdfUrl)) {
                    result[0] = fail("报告审批状态或PDF地址已变化，请重新生成"); return false;
                }
                result[0] = Ret.ok(); return true;
            });
            return committed ? result[0] : (result[0].isFail() ? result[0] : fail("PDF发布失败"));
        } catch (Exception error) {
            LOG.error("PDF发布事务失败", error);
            return fail("PDF发布失败，请刷新后重试");
        }
    }

	/**
	 * 组装永久删除操作的日志描述
	 * @param product 产品记录
	 * @return 日志描述文本
	 */
	private String buildPermanentDeleteLogDesc(Product product) {
		Long reportId = product.getReportId();
		if (reportId == null) {
			return " 产品ID：" + product.getId();
		}
		Record info = Db.findFirst(
				"SELECT sq.formnum, sq.order_id, sc.name AS cust_name FROM siargo_qareport sq "
				+ "LEFT JOIN siargo_customer sc ON sc.id = sq.cust_id WHERE sq.id = ?", reportId);
		if (info == null) {
			return " 产品ID：" + product.getId();
		}
		String formnum = info.get("formnum") != null ? String.valueOf((Object) info.get("formnum")) : "";
		String orderId = info.getStr("order_id") != null ? info.getStr("order_id") : "";
		String customerName = info.getStr("cust_name") != null ? info.getStr("cust_name") : "";
		String model = product.getModel() != null ? product.getModel() : "";
		String number = product.getNumber() != null ? product.getNumber() : "";
		String deleteDes = product.getDeleteDes() != null ? product.getDeleteDes() : "";
		return " 报告单编号：" + formnum + " ==订单号：" + orderId + " ==型号：" + model
				+ " ==编号：" + number + " ==客户：" + customerName + " ==删除原因：" + deleteDes;
	}
	
	/**
	 * 从数据库加载各流程阶段的数量统计
	 * <p>合并为单条SQL：一次性统计全部数量及insp=1~5各分类数量，避免多次查询导致的性能损耗</p>
	 */
	private Map<String, Long> loadFlowCountsFromDb() {
		Map<String, Long> counts = new java.util.HashMap<>();
		// 合并为单条SQL：使用SUM(CASE WHEN)一次性统计所有指标（单量 + 各环节送检只数）
		String sql = "SELECT"
				+ "  COUNT(*) AS all_count"
				+ ", SUM(CASE WHEN insp = 1 THEN 1 ELSE 0 END) AS insp_1"
				+ ", SUM(CASE WHEN insp = 2 THEN 1 ELSE 0 END) AS insp_2"
				+ ", SUM(CASE WHEN insp = 3 THEN 1 ELSE 0 END) AS insp_3"
				+ ", SUM(CASE WHEN insp = 4 THEN 1 ELSE 0 END) AS insp_4"
				+ ", SUM(CASE WHEN insp = 5 THEN 1 ELSE 0 END) AS insp_5"
				+ ", SUM(CASE WHEN insp = 6 THEN 1 ELSE 0 END) AS insp_6"
				+ ", SUM(CASE WHEN insp = 1 THEN sp.qsi ELSE 0 END) AS qsi_1"
				+ ", SUM(CASE WHEN insp = 2 THEN sp.qsi ELSE 0 END) AS qsi_2"
				+ ", SUM(CASE WHEN insp = 3 THEN sp.qsi ELSE 0 END) AS qsi_3"
				+ ", SUM(CASE WHEN insp = 4 THEN sp.qsi ELSE 0 END) AS qsi_4"
				+ ", SUM(CASE WHEN insp = 5 THEN sp.qsi ELSE 0 END) AS qsi_5"
				+ ", SUM(CASE WHEN insp = 6 THEN sp.qsi ELSE 0 END) AS qsi_6"
				+ " FROM siargo_product sp WHERE sp.vd = 1";
		Record row = Db.findFirst(sql);
		if (row != null) {
			counts.put("all", row.getLong("all_count") != null ? row.getLong("all_count") : 0L);
			counts.put("noq", row.getLong("insp_1") != null ? row.getLong("insp_1") : 0L);
			counts.put("accq", row.getLong("insp_2") != null ? row.getLong("insp_2") : 0L);
			counts.put("funq", row.getLong("insp_3") != null ? row.getLong("insp_3") : 0L);
			counts.put("appq", row.getLong("insp_4") != null ? row.getLong("insp_4") : 0L);
			counts.put("allq", row.getLong("insp_5") != null ? row.getLong("insp_5") : 0L);
			counts.put("ltq", row.getLong("insp_6") != null ? row.getLong("insp_6") : 0L);
			counts.put("noq_qsi", row.getLong("qsi_1") != null ? row.getLong("qsi_1") : 0L);
			counts.put("accq_qsi", row.getLong("qsi_2") != null ? row.getLong("qsi_2") : 0L);
			counts.put("funq_qsi", row.getLong("qsi_3") != null ? row.getLong("qsi_3") : 0L);
			counts.put("appq_qsi", row.getLong("qsi_4") != null ? row.getLong("qsi_4") : 0L);
			counts.put("allq_qsi", row.getLong("qsi_5") != null ? row.getLong("qsi_5") : 0L);
			counts.put("ltq_qsi", row.getLong("qsi_6") != null ? row.getLong("qsi_6") : 0L);
		} else {
			counts.put("all", 0L);
			counts.put("noq", 0L);
			counts.put("accq", 0L);
			counts.put("funq", 0L);
			counts.put("appq", 0L);
			counts.put("allq", 0L);
			counts.put("ltq", 0L);
			counts.put("noq_qsi", 0L);
			counts.put("accq_qsi", 0L);
			counts.put("funq_qsi", 0L);
			counts.put("appq_qsi", 0L);
			counts.put("allq_qsi", 0L);
			counts.put("ltq_qsi", 0L);
		}
		return counts;
	}
	
	/**
	 * 从数据库加载首页看板流程统计
	 * <p>当前在检记录没有批准时间，按 insp 分环节统计；
	 * 仅已完成记录按 allq_time 筛选本年度，两者均要求 vd=1。</p>
	 */
	private Map<String, Long> loadDashboardFlowCountsFromDb() {
		Map<String, Long> counts = new java.util.HashMap<>();
		String sql = "SELECT"
				+ "  COUNT(*) AS all_count"
				+ ", SUM(CASE WHEN insp = 1 THEN 1 ELSE 0 END) AS insp_1"
				+ ", SUM(CASE WHEN insp = 2 THEN 1 ELSE 0 END) AS insp_2"
				+ ", SUM(CASE WHEN insp = 3 THEN 1 ELSE 0 END) AS insp_3"
				+ ", SUM(CASE WHEN insp = 4 THEN 1 ELSE 0 END) AS insp_4"
				+ ", SUM(CASE WHEN insp = 5 THEN 1 ELSE 0 END) AS approved_count"
				+ ", SUM(CASE WHEN insp = 6 THEN 1 ELSE 0 END) AS insp_6"
				+ ", SUM(CASE WHEN insp = 1 THEN sp.qsi ELSE 0 END) AS qsi_1"
				+ ", SUM(CASE WHEN insp = 2 THEN sp.qsi ELSE 0 END) AS qsi_2"
				+ ", SUM(CASE WHEN insp = 3 THEN sp.qsi ELSE 0 END) AS qsi_3"
				+ ", SUM(CASE WHEN insp = 4 THEN sp.qsi ELSE 0 END) AS qsi_4"
				+ ", SUM(CASE WHEN insp = 5 THEN sp.qsi ELSE 0 END) AS approved_qsi"
				+ ", SUM(CASE WHEN insp = 6 THEN sp.qsi ELSE 0 END) AS qsi_6"
				+ " FROM siargo_product sp"
				+ " INNER JOIN siargo_qareport sq ON sq.id = sp.report_id"
				+ " WHERE sp.vd = 1 AND (sp.insp IN (1, 6, 2, 3, 4)"
				+ " OR (sp.insp = 5 AND YEAR(sp.allq_time) = YEAR(CURDATE())))";
		Record row = Db.findFirst(sql);
		if (row != null) {
			counts.put("all", row.getLong("all_count") != null ? row.getLong("all_count") : 0L);
			counts.put("noq", row.getLong("insp_1") != null ? row.getLong("insp_1") : 0L);
			counts.put("accq", row.getLong("insp_2") != null ? row.getLong("insp_2") : 0L);
			counts.put("funq", row.getLong("insp_3") != null ? row.getLong("insp_3") : 0L);
			counts.put("appq", row.getLong("insp_4") != null ? row.getLong("insp_4") : 0L);
			counts.put("allq", row.getLong("approved_count") != null ? row.getLong("approved_count") : 0L);
			counts.put("ltq", row.getLong("insp_6") != null ? row.getLong("insp_6") : 0L);
			counts.put("noq_qsi", row.getLong("qsi_1") != null ? row.getLong("qsi_1") : 0L);
			counts.put("accq_qsi", row.getLong("qsi_2") != null ? row.getLong("qsi_2") : 0L);
			counts.put("funq_qsi", row.getLong("qsi_3") != null ? row.getLong("qsi_3") : 0L);
			counts.put("appq_qsi", row.getLong("qsi_4") != null ? row.getLong("qsi_4") : 0L);
			counts.put("allq_qsi", row.getLong("approved_qsi") != null ? row.getLong("approved_qsi") : 0L);
			counts.put("ltq_qsi", row.getLong("qsi_6") != null ? row.getLong("qsi_6") : 0L);
		} else {
			counts.put("all", 0L);
			counts.put("noq", 0L);
			counts.put("accq", 0L);
			counts.put("funq", 0L);
			counts.put("appq", 0L);
			counts.put("allq", 0L);
			counts.put("ltq", 0L);
			counts.put("noq_qsi", 0L);
			counts.put("accq_qsi", 0L);
			counts.put("funq_qsi", 0L);
			counts.put("appq_qsi", 0L);
			counts.put("allq_qsi", 0L);
			counts.put("ltq_qsi", 0L);
		}
		return counts;
	}

	/** 按最终放行时间查询有效、已完成产品，起始日包含、截止日不包含。 */
	public List<Record> getIdsByReleaseMonthRange(LocalDate startInclusive, LocalDate endExclusive) {
	    Sql sql = Sql.mysql()
	            .select("sp.id")
	            .from("siargo_product", "sp")
	            .eq("sp.vd", QarepConst.VD_VALID)
	            .eq("sp.insp", QarepConst.INSP_COMPLETED)
	            .ge("sp.allq_time", java.sql.Timestamp.valueOf(startInclusive.atStartOfDay()))
	            .lt("sp.allq_time", java.sql.Timestamp.valueOf(endExclusive.atStartOfDay()));
	    return findRecord(sql);
	}

	/**
	 * 对Record列表中的用户可控文本字段做HTML转义（防XSS，仅用于列表展示数据）
	 * @param records 记录列表
	 * @param fields 需转义的字段名
	 */
	private void escapeRecordFields(List<Record> records, String... fields) {
		if (records == null || records.isEmpty()) {
			return;
		}
		for (Record r : records) {
			for (String field : fields) {
				String value = r.getStr(field);
				if (value != null && !value.isEmpty()) {
					r.set(field, EscapeUtil.escapeHtml4(value));
				}
			}
		}
	}

	/**
	 * 后台管理分页查询报告单列表
	 * <p>关联查询产品、型号、报告单、用户和字典，仅返回列表展示所需字段</p>
	 * <p>说明：id/spid 使用 CAST(... AS CHAR) 输出，避免前端雪花ID精度丢失；sp_des 输出前做HTML转义</p>
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @param keywords 搜索关键字（订单号模糊匹配）
	 * @param prodType 产品类型字典 SN（0=全部）
	 * @param insp 检验进度（1-5，0=全部）
	 * @param startTime 创建时间起始
	 * @param endTime 创建时间结束
	 * @return 分页数据
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, String keywords, int prodType, int insp, Date startTime, Date endTime) {
		// ========== 缓存键构建与快速路径检查 ==========
		// 仅缓存"空关键字+无日期范围+第一页"的查询，key空间有限（prodType×insp×pageSize），防止缓存无限增长
		boolean cacheable = notOk(keywords) && startTime == null && endTime == null && pageNumber == 1;
		String cacheKey = pageNumber + "_" + pageSize + "_" + prodType + "_" + insp;
		if (cacheable) {
			Map<String, Page<Record>> cache = cachedPaginateData;
			if (cache != null && (System.currentTimeMillis() - paginateCacheTimestamp) < PAGINATE_CACHE_TTL) {
				Page<Record> cached = cache.get(cacheKey);
				if (cached != null) {
					return cached;
				}
			}
		}

		// ========== 构建基础查询 ==========
		Sql sql = Sql.mysql()
				// 选择字段：报告单基础信息（id/spid转CHAR防止前端雪花ID精度丢失）
				.select("CAST(sq.id AS CHAR) AS id", "sq.order_id", "sq.formnum","sp.insp",
						// 检验时间信息
						"sp.accq_time", "sp.funq_time", "sp.appq_time", "sp.allq_time", "sp.lt_status", "sp.lt_time",
						// 检验人员姓名
						"accq_user.name AS accq_name", "funq_user.name AS funq_name", "appq_user.name AS appq_name",
						"lt_user.name AS lt_name", "allq_user.name AS allq_name", "DATE_FORMAT(sq.create_time, '%Y-%m-%d %H:%i') as create_time",
						// 产品信息字段
						"CAST(sp.id AS CHAR) as spid", "sp.model as sp_model", "sp.number as sp_number", "pm.model_series", "pm.branch_label",
						"sp.qsi as sp_qsi", "sp.qi as sp_qi", "sp.des as sp_des", "sp.pdfstr AS sp_pdfstr",
						// 字典翻译字段
						PRODUCT_TYPE_NAME_SQL + " AS type_name","d_insp.name AS insp_name","d_retype.name AS retype_name",
						// 驳回历史条数（>0 时前端显示「驳」角标，点击查看历史）
						"sp.reject_count"
						)
				.page(pageNumber, pageSize).from("siargo_product", "sp")
                .leftJoin("siargo_prod_model", "pm", "pm.id=sp.siargo_prod_model_id")
				// ========== 关联报告单表 ==========
				.leftJoin("siargo_qareport", "sq", "sq.id = sp.report_id")
				// ========== 关联字典表获取产品类型名称 ==========
				.leftJoin(ProductSeriesService.TYPE_LABELS_SQL, "d_type", "d_type.type_key = 'siargo_prod_type' "
						+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(pm.prod_type AS CHAR) "
						+ "AND d_type.enable = '1'")
				// ========== 关联字典表获取报告类型名称 ==========
				.leftJoin("jb_dictionary", "d_retype", "d_retype.type_key = 'siargo_rep_type' "
						+ "AND d_retype.sn COLLATE utf8mb4_general_ci = CAST(sq.rep_type AS CHAR) "
						+ "AND d_retype.enable = '1'")
				// ========== 关联字典表获取检验进度名称 ==========
				.leftJoin("jb_dictionary", "d_insp", "d_insp.type_key = 'siargo_insp' "
						+ "AND d_insp.sn COLLATE utf8mb4_general_ci = CAST(sp.insp AS CHAR) "
						+ "AND d_insp.enable = '1'")
				// ========== 关联用户表获取各阶段检验人员信息 ==========
				.leftJoin("jb_user", "accq_user", "accq_user.id = sp.accq_uid")
				.leftJoin("jb_user", "funq_user", "funq_user.id = sp.funq_uid")
				.leftJoin("jb_user", "lt_user", "lt_user.id = sp.lt_uid")
				.leftJoin("jb_user", "appq_user", "appq_user.id = sp.appq_uid")
				.leftJoin("jb_user", "allq_user", "allq_user.id = sp.allq_uid").eq("sp.vd", QarepConst.VD_VALID);

		// ========== 应用搜索条件 ==========
		sql.like("sq.order_id", keywords);

		// ========== 应用日期范围筛选 ==========
		if (isOk(startTime) && isOk(endTime)) {
			sql.bwDate("sq.create_time",startTime,endTime);
		}

		// ========== 应用产品类型筛选 ==========
		if (prodType > 0) {
			sql.eq("pm.prod_type", prodType);
		}

		// ========== 应用检验进度筛选并设置排序 ==========
		if (insp > 0) {
			sql.eq("sp.insp", insp);

			// 排序：按上一个进度的操作时间倒序，次要按创建时间、formnum保证同一报告单行相邻
			switch(insp){
	         case QarepConst.INSP_PENDING_ACCURACY:
	        	 sql.orderBy("sq.create_time", true);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
	         case QarepConst.INSP_PENDING_APPEARANCE:
	        	 sql.orderBy("sp.accq_time", true);  // 主排序：上一个进度(精度检验)完成时间
	        	 sql.orderBy("sq.create_time", false);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
	         case QarepConst.INSP_PENDING_LEAK_TEST:
	        	 sql.orderBy("sp.accq_time", true);  // 主排序：上一个进度(精度检验)完成时间
	        	 sql.orderBy("sq.create_time", false);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
	         case QarepConst.INSP_PENDING_PACKAGING:
	        	 sql.orderBy("sp.funq_time", true);  // 主排序：上一个进度(外观检验)完成时间
	        	 sql.orderBy("sq.create_time", false);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
	         case QarepConst.INSP_PENDING_APPROVAL:
	        	 sql.orderBy("sp.appq_time", true);  // 主排序：上一个进度(包装检验)完成时间
	        	 sql.orderBy("sq.create_time", false);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
	         case QarepConst.INSP_COMPLETED:
				 sql.orderBy("sp.allq_time", true);   // 批准时间倒序
				 sql.orderBy("sq.create_time", false);
				 sql.orderBy("sq.formnum", true);    // 报告单标号倒序
	        	 break;
	         default:
	        	 sql.orderBy("sq.create_time", true);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
			}

		}else {
			sql.orderBy("sq.create_time", true);
			sql.orderBy("sq.formnum", true);
		}

		Page<Record> result = paginateRecord(sql, true);

		// ========== 列表展示数据防XSS：用户可控文本HTML转义（不影响编辑回显接口） ==========
		escapeRecordFields(result.getList(), "sp_des");

		// ========== 将查询结果放入缓存（仅可缓存查询） ==========
		if (cacheable) {
			paginateCacheLock.lock();
			try {
				if (cachedPaginateData == null || (System.currentTimeMillis() - paginateCacheTimestamp) >= PAGINATE_CACHE_TTL) {
					cachedPaginateData = new java.util.concurrent.ConcurrentHashMap<>();
					paginateCacheTimestamp = System.currentTimeMillis();
				}
				cachedPaginateData.put(cacheKey, result);
			} finally {
				paginateCacheLock.unlock();
			}
		}

		return result;
	}

    /** 逐产品校验后一次性保存报告头和全部产品；本方法完整持有事务。 */
    public Ret saveProducts(Qareport input, Product defaults, String productsJson) {
        String reportError = validateReportInput(input);
        if (reportError != null) return fail(reportError);
        if (input.getLong("id") != null || (defaults != null && defaults.getLong("id") != null)) {
            return fail("新增报告单不能携带已有记录 ID");
        }
        Ret parsed = ReportProductInput.parse(productsJson, defaults);
        if (parsed.isFail()) return parsed;
        List<Product> products = parsed.getAs("data");
        for (int index = 0; index < products.size(); index++) {
            Product product = products.get(index);
            Integer insp = product.getInt("insp");
            if (insp == null || (insp != QarepConst.INSP_PENDING_ACCURACY && insp != QarepConst.INSP_PENDING_APPEARANCE)) {
                return fail("新增产品只能选择精度待检或精度已检");
            }
            Ret series = seriesService.applySeries(product, false);
            if (series.isFail()) return fail("产品 #" + (index + 1) + "：" + series.getStr("msg"));
            String error = QarepConst.validateElectricalParams(product);
            if (error != null) return fail("产品 #" + (index + 1) + "：" + error);
        }
        Qareport report = new Qareport().set("id", JBoltSnowflakeKit.me.nextId())
                .set("order_id", input.getLong("order_id")).set("cust_id", input.getLong("cust_id"))
                .set("rep_type", input.getInt("rep_type")).set("create_time", new Date());
        Ret[] result = {Ret.ok()};
        boolean committed;
        try {
            committed = Db.tx(() -> {
                // 与系列删除使用同一行锁；固定锁顺序避免多产品交叉选择引起死锁。
                List<Product> lockOrder = products.stream()
                        .sorted(java.util.Comparator.comparing(p -> p.getLong("siargo_prod_model_id"))).toList();
                for (Product product : lockOrder) {
                    result[0] = seriesService.applySeriesForUpdate(product, false);
                    if (result[0].isFail()) return false;
                }
                result[0] = saveReportHeader(report);
                if (result[0].isFail()) return false;
                for (Product product : products) {
                    result[0] = productService.saveReportProduct(product, report.getLong("id"));
                    if (result[0].isFail()) return false;
                }
                addSaveSystemLog(report.getLong("id"), JBoltUserKit.getUserId(), "报告单：" + report.getLong("formnum"));
                return true;
            });
        } catch (Exception ex) {
            LOG.error("报告单批量保存失败，事务已回滚", ex);
            return fail("报告单保存失败，请重试");
        }
        if (!committed) return result[0].isFail() ? result[0] : fail("报告单保存失败，请重试");
        clearFlowCountsCache();
        Ret saved = Ret.ok().set("msg", "保存成功");
        List<String> ids = products.stream().map(p -> String.valueOf(p.getLong("id"))).toList();
        return saved.set("data", Kv.by("reportId", String.valueOf(report.getLong("id"))).set("productIds", ids));
    }

    private String validateReportInput(Qareport report) {
        if (report == null || report.getLong("order_id") == null || report.getLong("order_id") <= 0) return "订单号必填";
        if (report.getLong("cust_id") == null || customerService.findById(report.getLong("cust_id")) == null) return "请选择有效客户";
        Integer kind = report.getInt("rep_type");
        if (kind == null || (kind != QarepConst.REP_TYPE_NORMAL && kind != QarepConst.REP_TYPE_REPAIR)) return "请选择报告单类型";
        return null;
    }

    /** 仅在外层报告单事务内调用，唯一索引冲突时重新分配报告编号。 */
    private Ret saveReportHeader(Qareport report) {
        for (int attempt = 1; attempt <= QarepConst.FORMNUM_RETRY_MAX; attempt++) {
            Ret number = creatFormnum();
            if (number.isFail()) return number;
            report.set("formnum", number.get("data"));
            try {
                return report.save() ? Ret.ok() : fail("报告单保存失败");
            } catch (Exception ex) {
                String message = ex.getMessage();
                if (message != null && message.contains("Duplicate") && attempt < QarepConst.FORMNUM_RETRY_MAX) continue;
                LOG.error("报告单编号或记录保存失败", ex);
                return fail("报告单编号保存失败，请重试");
            }
        }
        return fail("报告单编号分配失败，请重试");
    }

	/**
	 * 根据产品ID查询完整的报告单信息
	 * <p>关联查询：产品、客户、用户、字典表，用于PDF生成和详情展示</p>
	 * <p>返回字段包括：报告单基础信息、产品信息、各阶段检验人员信息、字典翻译值</p>
	 * @param id 产品ID
	 * @return 完整报告单信息
	 */
	public Qareport qareportFindByProId(Long id) {
		String sql = "SELECT\n" + "  sq.id,\n" + "  sc.NAME sc_name,\n" + "  sp.id AS proid,\n" + "  sq.order_id,\n"
				+ "  sq.cust_id,\n" + "  sq.formnum,\n" + "  pm.prod_type AS prod_type,\n CAST(sp.siargo_prod_model_id AS CHAR) siargo_prod_model_id, pm.model_series, pm.prod_type prodType, " + PRODUCT_TYPE_NAME_SQL + " prodTypeName, sp.vd, sp.vd AS sp_vd,\n" + "  sq.rep_type,\n"
				+ "  pm.model_desc AS selection_rule,\n"
				+ "  (SELECT JSON_OBJECTAGG(CAST(pv.id AS CHAR), JSON_OBJECT('type', CAST(pv.param_type_id AS CHAR), 'value', pv.param_value))"
				+ " FROM siargo_prod_model_param mp INNER JOIN siargo_prod_param_value pv ON pv.id=mp.param_value_id AND pv.param_type_id=mp.param_type_id"
				+ " INNER JOIN siargo_prod_param_type pt ON pt.id=mp.param_type_id AND pt.is_active=1"
				+ " WHERE mp.model_id=pm.id AND pv.is_active=1 AND pm.model_series='MF-FD') AS selection_values,\n"
				+ "  sp.insp,\n" + "  DATE_FORMAT(sp.accq_time, '%Y-%m-%d %H:%i') AS accq_time,\n"
				+ "  DATE_FORMAT(sp.funq_time, '%Y-%m-%d %H:%i') AS funq_time,\n"
				+ "  DATE_FORMAT(sp.appq_time, '%Y-%m-%d %H:%i') AS appq_time,\n"
				+ "  DATE_FORMAT(sp.allq_time, '%Y-%m-%d %H:%i') AS allq_time,\n"
				+ " CAST(sp.accq_uid AS CHAR) accq_uid, CAST(sp.funq_uid AS CHAR) funq_uid, CAST(sp.appq_uid AS CHAR) appq_uid, CAST(sp.allq_uid AS CHAR) allq_uid, CAST(sp.lt_uid AS CHAR) lt_uid, sp.lt_status,\n" + "  DATE_FORMAT(sp.lt_time, '%Y-%m-%d %H:%i') AS lt_time,\n"
				+ "  accq_user.NAME AS accq_name,\n funq_user.NAME AS funq_name,\n"
				+ "  lt_user.NAME AS lt_name,\n appq_user.NAME AS appq_name,\n allq_user.NAME AS allq_name,\n"
				+ "  accq_user.email AS accq_email,\n funq_user.email AS funq_email,\n"
				+ "  lt_user.email AS lt_email,\n appq_user.email AS appq_email,\n allq_user.email AS allq_email,\n"
				+ "  DATE_FORMAT(sq.create_time, '%Y-%m-%d %H:%i') AS create_time,\n"
				+ "  DATE_FORMAT(sq.create_time, '%Y.%m.%d') AS c_time,\n" + "  sp.id AS spid,\n"
				+ "  sp.model AS sp_model,\n" + "  sp.number AS sp_number,\n" + "  pm.prod_type AS sp_type,\n"
				+ "  sp.qsi AS sp_qsi,\n" + "  sp.qi AS sp_qi,\n" + "  sp.flow_range AS sp_flow_range,\n"
				+ "  sp.des AS sp_des,\n" + "  sp.pdfstr AS sp_pdfstr,\n" + " sp.pdfver AS sp_pdfver,\n" + "  sp.cuc AS sp_cuc,\n"
				+ "  sp.pv AS sp_pv,\n" + "  sp.thv AS sp_thv,\n" + "  sp.zp AS sp_zp,\n" + "  sp.fl AS sp_fl,\n"
				+ "  sp.cucmax AS sp_cucmax,\n" + "  sp.cucmin AS sp_cucmin,\n"
				+ "	 sp.bv AS sp_bv,\n"+ "  sp.la AS sp_la\n, "
				+ "  " + PRODUCT_TYPE_NAME_SQL + " AS type_name, d_insp.NAME AS insp_name, "
				+ "  d_flow.NAME AS flow_name, d_pdfver.NAME AS pdfver_name, d_retype.NAME AS retype_name"
				+ "  FROM\n" + "  `siargo_qareport` sq\n"
				+ "  LEFT JOIN `siargo_product` AS sp ON sq.id = sp.report_id\n LEFT JOIN siargo_prod_model pm ON pm.id=sp.siargo_prod_model_id\n"
				+ "  LEFT JOIN `siargo_customer` AS sc ON sc.id = sq.cust_id\n"
				+ "   LEFT JOIN " + ProductSeriesService.TYPE_LABELS_SQL + " AS d_type ON d_type.type_key = 'siargo_prod_type'\r\n"
				+ "  AND d_type.sn COLLATE utf8mb4_general_ci = CAST(pm.prod_type AS CHAR)\r\n"
				+ "  AND d_type.ENABLE = '1'\r\n"
				+ "  LEFT JOIN `jb_dictionary` AS d_retype ON d_retype.type_key = 'siargo_rep_type'\r\n"
				+ "  AND d_retype.sn COLLATE utf8mb4_general_ci = CAST(sq.rep_type AS CHAR)\r\n"
				+ "  AND d_retype.ENABLE = '1'\r\n"
				+ "  LEFT JOIN `jb_dictionary` AS d_insp ON d_insp.type_key = 'siargo_insp'\r\n"
				+ "  AND d_insp.sn COLLATE utf8mb4_general_ci = CAST(sp.insp AS CHAR)\r\n"
				+ "  AND d_insp.ENABLE = '1'\r\n"
				+ "  LEFT JOIN `jb_dictionary` AS d_pdfver ON d_pdfver.type_key = 'siargo_pdfver'\r\n"
				+ "  AND d_pdfver.name COLLATE utf8mb4_general_ci = sp.pdfver\r\n"
				+ "  AND d_pdfver.ENABLE = '1'\r\n"
				+ "  LEFT JOIN `jb_dictionary` AS d_flow ON d_flow.type_key = 'siargo_flow_range'\r\n"
				+ "  AND d_flow.sn COLLATE utf8mb4_general_ci = sp.flow_range\r\n"
				+ "  AND d_flow.ENABLE = '1'"
				+ "  LEFT JOIN jb_user AS accq_user ON accq_user.id = sp.accq_uid\n"
				+ "  LEFT JOIN jb_user AS funq_user ON funq_user.id = sp.funq_uid\n"
				+ "  LEFT JOIN jb_user AS lt_user ON lt_user.id = sp.lt_uid\n"
				+ "  LEFT JOIN jb_user AS appq_user ON appq_user.id = sp.appq_uid\n"
				+ "  LEFT JOIN jb_user AS allq_user ON allq_user.id = sp.allq_uid\n" + "WHERE\n"
				+ "  sp.id = ? ";

		return dao.findFirst(sql, id);
	}

	/**
	 * 根据报告单ID查询该报告单下的全部有效产品信息（含字典翻译）
	 * <p>产品查询委托 ProductService，并按模板系列关联补充详情页大表参数显示标记</p>
	 * @param reportId 报告单ID
	 * @return 产品列表
	 */
	public List<Product> findProductsByReportId(Long reportId) {
		List<Product> products = productService.findProductsByReportId(reportId);
		Map<Long, List<Record>> rejectLogsByProductId = productRejectLogService.findLogsByReportId(reportId);
		Map<Long, Boolean> largeMeterSeries = new LinkedHashMap<>();
		for (Product product : products) {
			Long seriesId = product.getLong("siargo_prod_model_id");
			// 仅供视图使用的附加属性，不作为数据库列参与持久化。
			product.put("reject_logs", rejectLogsByProductId.getOrDefault(product.getLong("id"), Collections.emptyList()));
			product.put("show_large_meter_params",
					largeMeterSeries.computeIfAbsent(seriesId, templateService::isLargeMeterSeries));
		}
		return products;
	}

	/** 编辑页与详情页复用 PDF 模板的系列关联判断。 */
	public List<String> findLargeMeterSeriesIds() {
		return templateService.findLargeMeterSeriesIds();
	}

	/**
	 * 根据产品ID列表查询审批工作台展示数据（含字典翻译与驳回信息）
	 * <p>用于审批抽屉页面加载待审批产品清单，关联报告单、客户、字典和驳回人信息</p>
	 * <p>id 使用 CAST(... AS CHAR) 输出，避免前端雪花ID精度丢失</p>
	 * @param ids 产品ID列表
	 * @return 产品记录列表
	 */
	public List<Record> findApprovalProducts(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}
		// 构建 IN 子句的 ? 占位符
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < ids.size(); i++) {
			if (i > 0) {
				placeholders.append(",");
			}
			placeholders.append("?");
		}
		String sql = "SELECT CAST(sp.id AS CHAR) AS id, sp.model, sp.number, sp.qsi, sp.qi, sp.des, sp.insp, sp.lt_status, "
			// 驳回历史条数（>0 时显示「驳」角标，点击查看历史）
			+ "sp.reject_count, "
			+ "sq.formnum, sq.order_id, "
			+ "sc.name AS sc_name, "
			+ PRODUCT_TYPE_NAME_SQL + " AS type_name, pm.prod_type AS prod_type, pm.model_series, CAST(sp.siargo_prod_model_id AS CHAR) siargo_prod_model_id, "
			+ "d_retype.name AS retype_name "
			+ "FROM siargo_product sp LEFT JOIN siargo_prod_model pm ON pm.id=sp.siargo_prod_model_id "
			+ "LEFT JOIN siargo_qareport sq ON sq.id = sp.report_id "
			+ "LEFT JOIN siargo_customer sc ON sc.id = sq.cust_id "
			+ "LEFT JOIN " + ProductSeriesService.TYPE_LABELS_SQL + " AS d_type ON d_type.type_key = 'siargo_prod_type' "
			+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(pm.prod_type AS CHAR) "
			+ "AND d_type.enable = '1' "
			+ "LEFT JOIN jb_dictionary AS d_retype ON d_retype.type_key = 'siargo_rep_type' "
			+ "AND d_retype.sn COLLATE utf8mb4_general_ci = CAST(sq.rep_type AS CHAR) "
			+ "AND d_retype.enable = '1' "
			+ "WHERE sp.vd = 1 AND sp.id IN (" + placeholders + ") "
			+ "ORDER BY sq.formnum ASC, sp.id ASC";
		return Db.find(sql, ids.toArray());
	}

    /** 编辑由 Service 持有事务；与新增共用基础字段、系列归属和电气参数校验。 */
    public Ret update(Qareport qareport, Product product) {
        String reportError = validateReportInput(qareport);
        if (reportError != null) return fail(reportError);
        String productError = ReportProductInput.validateBasics(product);
        if (productError != null) return fail(productError);
        String electricalError = QarepConst.validateElectricalParams(product);
        if (electricalError != null) return fail(electricalError);
        Ret[] result = {Ret.ok()};
        List<String> stalePdfs = new ArrayList<>();
        boolean committed;
        try {
            committed = Db.tx(() -> {
                result[0] = updateInTransaction(qareport, product, stalePdfs);
                return result[0] != null && result[0].isOk();
            });
        } catch (Exception ex) {
            LOG.error("报告单更新失败，事务已回滚", ex);
            return fail("报告单更新失败，请重试");
        }
        if (!committed) return result[0] != null && result[0].isFail() ? result[0] : fail("报告单更新失败");
        clearFlowCountsCache();
        deletePhysicalPdfs(stalePdfs);
        return Ret.ok().set("msg", "更新成功");
    }

	/**
	 * 更新报告单和产品数据
	 * <p>安全说明（编辑白名单）：不信任前端提交的各环节 uid/time 签名字段；
	 * 以库内记录为基准，仅拷贝允许编辑的业务字段（型号/编号/qi/qsi/描述/电气参数等）；
	 * insp 支持服务端更新：服务端校验合法范围（1~6）后，按状态机语义联动维护各环节签名
	 * （前进补签缺失环节/回退清空超出环节），签名列一律以服务端生成为准</p>
	 * <p>所有业务校验在任何写库操作之前完成；产品更新失败抛RuntimeException触发事务回滚</p>
	 * @param qareport 报告单对象（前端提交）
	 * @param product 产品对象（前端提交）
	 * @return 操作结果
	 */
	private Ret updateInTransaction(Qareport qareport, Product product, List<String> stalePdfs) {
		// ========== 全部校验前置：任何写库操作之前 ==========
		if (qareport == null || notOk(qareport.getId()) || product == null || notOk(product.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		// 数量校验（防拆箱NPE：先判空再比较）
		if (product.getQsi() == null || product.getQi() == null) {
			return fail("送检数量和检验数量不能为空！");
		}
		if (product.getQsi() < product.getQi()) {
			return fail("送检数量小于检验数量，重新输入！");
		}
		// update入口的validateBasics已要求明确选择成品检漏状态，不自动补默认值。
		int ltStatus = product.getInt("lt_status");
		if (ltStatus != QarepConst.LT_STATUS_YES && ltStatus != QarepConst.LT_STATUS_NO) {
			return fail("成品检漏参数非法！");
		}

        Product originalProduct = productService.findById(product.getLong("id"));
        if (originalProduct == null) return fail(JBoltMsg.DATA_NOT_EXIST);
        if (!Objects.equals(originalProduct.getLong("report_id"), qareport.getLong("id"))) return fail("产品不属于当前报告单");
        Long originalSeriesId = originalProduct.getLong("siargo_prod_model_id");
        // 与正式发布统一锁序：先升序锁原/新系列，再报告头，再产品。
        seriesService.lockSeriesRowsForUpdate(java.util.Arrays.asList(originalSeriesId, product.getLong("siargo_prod_model_id")));
        Qareport dbQareport = dao.findFirst("SELECT * FROM siargo_qareport WHERE id=? FOR UPDATE", qareport.getLong("id"));
        if (dbQareport == null) return fail(JBoltMsg.DATA_NOT_EXIST);
        Product dbProduct = productService.findReportProductsForUpdate(qareport.getLong("id")).stream()
                .filter(item -> Objects.equals(item.getLong("id"), product.getLong("id"))).findFirst().orElse(null);
		if (dbProduct == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
        if (!Objects.equals(dbProduct.getLong("report_id"), qareport.getLong("id"))) return fail("产品不属于当前报告单");
        if (!Objects.equals(dbProduct.getLong("siargo_prod_model_id"), originalSeriesId)) return fail("产品型号系列已被修改，请刷新后重试");
        if (!Integer.valueOf(QarepConst.VD_VALID).equals(dbProduct.getInt("vd"))) return fail("产品已删除，请从回收站恢复后编辑");
        Ret series = seriesService.applySeriesForUpdate(product,
                Objects.equals(dbProduct.getLong("siargo_prod_model_id"), product.getLong("siargo_prod_model_id")));
        if (series.isFail()) return series;

        boolean headerChanged = !Objects.equals(dbQareport.get("order_id"), qareport.get("order_id"))
                || !Objects.equals(dbQareport.get("cust_id"), qareport.get("cust_id"))
                || !Objects.equals(dbQareport.get("rep_type"), qareport.get("rep_type"));
        boolean productChanged = ReportProductInput.EDITABLE_FIELDS.stream()
                .anyMatch(field -> !Objects.equals(dbProduct.get(field), product.get(field)));

		// ========== 白名单拷贝：报告单允许编辑的业务字段 ==========
		dbQareport.set("order_id", qareport.getOrderId());
		dbQareport.set("cust_id", qareport.getCustId());
		dbQareport.set("rep_type", qareport.getRepType());

		// ========== 检验进度更新（服务端支持，保持状态机一致） ==========
		// 前端 select 可绕过，服务端兜底校验合法范围（1~6）
		Integer insp = product.getInsp();
		if (insp == null || insp < QarepConst.INSP_PENDING_ACCURACY || insp > QarepConst.INSP_PENDING_LEAK_TEST) {
			return fail("检验进度参数非法！");
		}
		Integer dbInsp = dbProduct.getInt("insp");
		Integer dbLtStatus = dbProduct.getInt("lt_status");
		boolean ltChanged = dbLtStatus == null || dbLtStatus != ltStatus;
		Integer targetInsp = insp;
		if (ltChanged) {
			// 编辑页调整成品检漏标记时的状态归一化：
			// 无→有 且当前在外观待检（检漏未做）时退回成品检漏待检；有→无 且当前在成品检漏待检时直接进入外观待检
			if (ltStatus == QarepConst.LT_STATUS_YES
					&& dbInsp != null && dbInsp == QarepConst.INSP_PENDING_APPEARANCE
					&& insp != QarepConst.INSP_PENDING_LEAK_TEST) {
				targetInsp = QarepConst.INSP_PENDING_LEAK_TEST;
			} else if (ltStatus == QarepConst.LT_STATUS_NO
					&& dbInsp != null && dbInsp == QarepConst.INSP_PENDING_LEAK_TEST) {
				targetInsp = QarepConst.INSP_PENDING_APPEARANCE;
			}
		}
        // 先允许原成品检漏待检产品切换为无检漏并归一化，再校验最终状态。
        if (targetInsp == QarepConst.INSP_PENDING_LEAK_TEST && ltStatus != QarepConst.LT_STATUS_YES) {
            return fail("成品检漏待检仅适用于有成品检漏的产品！");
        }
        if (headerChanged) {
            stalePdfs.addAll(productService.invalidateReportPdfs(dbQareport.getLong("id")));
            dbProduct.set("pdfstr", null);
        } else if (productChanged || !Objects.equals(targetInsp, dbInsp) || ltChanged) {
            addPdfUrl(stalePdfs, dbProduct.getStr("pdfstr"));
            dbProduct.set("pdfstr", null);
        }
		if (dbInsp != null && (!targetInsp.equals(dbInsp) || ltChanged)) {
			// 进度变更：联动维护各环节签名（前进补签缺失环节/回退清空超出环节），
			// 条件更新（WHERE insp=库内旧值）防并发覆盖他人已推进的状态
			if (!syncInspWithSignatures(product.getId(), dbInsp, targetInsp, ltStatus)) {
				return fail("检验进度更新失败（状态已变化），请刷新后重试！");
			}
		}

        // 只复制允许编辑字段，签名及报告关联由服务端维护。
        for (String field : ReportProductInput.EDITABLE_FIELDS) dbProduct.set(field, product.get(field));

		boolean qasuccess = dbQareport.update();
		if (!qasuccess) {
			return fail("报告单更新失败，请联系开发人员！");
		}

		boolean prodSuccess = productService.updateReportProduct(dbProduct);
		if (!prodSuccess) {
			return fail("产品信息更新失败，请联系开发人员！");
		}
		return Ret.ok();
	}

	/**
	 * 检验进度变更时的签名一致性维护（编辑页服务端支持）
	 * <p>状态机语义（与批准/驳回流程一致）：按逻辑顺序 1→(6)→2→3→4→5，
	 * 目标状态要求的签名集合（accq；lt 仅 lt_status=1 且越过成品检漏环节后；funq/appq/allq）必须完整，
	 * 不要求的签名一律清空，避免详情页/PDF 生成（safeStr 强校验）因缺签名异常</p>
	 * <p>签名采用 COALESCE 补签，仅补缺失、不覆盖已有签名</p>
	 * <p>条件更新（WHERE insp=库内旧值）保证并发下不会覆盖他人已推进的状态</p>
	 * @param productId 产品ID
	 * @param oldInsp 库内当前进度
	 * @param newInsp 目标进度
	 * @param ltStatus 成品检漏标记（1=有 2=无）
	 * @return 是否更新成功（false=状态已被他人变更）
	 */
	private boolean syncInspWithSignatures(Long productId, Integer oldInsp, Integer newInsp, int ltStatus) {
		Long userId = JBoltUserKit.getUserId();
		String now = DateUtil.getDateString(DateUtil.YMDHMS);
		StringBuilder sql = new StringBuilder("UPDATE siargo_product SET insp = ?");
		List<Object> params = new ArrayList<>();
		params.add(newInsp);
		boolean needAccq = newInsp != QarepConst.INSP_PENDING_ACCURACY;
		boolean needLt = ltStatus == QarepConst.LT_STATUS_YES
				&& newInsp != QarepConst.INSP_PENDING_ACCURACY
				&& newInsp != QarepConst.INSP_PENDING_LEAK_TEST;
		boolean needFunq = newInsp == QarepConst.INSP_PENDING_PACKAGING
				|| newInsp == QarepConst.INSP_PENDING_APPROVAL
				|| newInsp == QarepConst.INSP_COMPLETED;
		boolean needAppq = newInsp == QarepConst.INSP_PENDING_APPROVAL
				|| newInsp == QarepConst.INSP_COMPLETED;
		boolean needAllq = newInsp == QarepConst.INSP_COMPLETED;
		appendSignatureColumn(sql, params, "accq", needAccq, userId, now);
		appendSignatureColumn(sql, params, "lt", needLt, userId, now);
		appendSignatureColumn(sql, params, "funq", needFunq, userId, now);
		appendSignatureColumn(sql, params, "appq", needAppq, userId, now);
		appendSignatureColumn(sql, params, "allq", needAllq, userId, now);
		sql.append(" WHERE id = ? AND insp = ? AND vd = ").append(QarepConst.VD_VALID);
		params.add(productId);
		params.add(oldInsp);
		return Db.update(sql.toString(), params.toArray()) > 0;
	}

	/**
	 * 追加单个签名列到 UPDATE 语句：需要时 COALESCE 补签，不需要时清空
	 * @param sql UPDATE 语句构建器
	 * @param params 参数列表
	 * @param col 列前缀（accq/lt/funq/appq/allq）
	 * @param need 目标状态是否需要该签名
	 * @param userId 当前用户ID
	 * @param now 当前时间字符串
	 */
	private void appendSignatureColumn(StringBuilder sql, List<Object> params, String col,
			boolean need, Long userId, String now) {
		if (need) {
			sql.append(", ").append(col).append("_uid = COALESCE(").append(col).append("_uid, ?)")
				.append(", ").append(col).append("_time = COALESCE(").append(col).append("_time, ?)");
			params.add(userId);
			params.add(now);
		} else {
			sql.append(", ").append(col).append("_uid = NULL, ").append(col).append("_time = NULL");
		}
	}

	/**
	 * 生成报告单编号（带行级锁防并发重号）
	 * <p>编号规则：年月(YYYYMM) + 当月序号(3位)</p>
	 * <p>示例：202512001 表示2025年12月第1份报告单</p>
	 * <p>并发说明：使用 SELECT ... FOR UPDATE 锁定当月已有最大单号，
	 * 替代原 COUNT+1 方式，避免并发请求生成重复单号；
	 * formnum 的 UNIQUE 索引由 DBA 另行建立作为最终兜底</p>
	 * @return 报告单编号
	 */
	public Ret creatFormnum() {
		LocalDate now = LocalDate.now();
		long fornum = now.getYear() * 100L + now.getMonthValue();
		long rangeStart = fornum * 1000 + 1;
		long rangeEnd = fornum * 1000 + 999;

		// FOR UPDATE 行级锁：并发生成时串行化读取当月最大单号
		Long maxFormnum = Db.queryLong(
				"SELECT MAX(formnum) FROM siargo_qareport WHERE formnum BETWEEN ? AND ? FOR UPDATE",
				rangeStart, rangeEnd);
		long seq = (maxFormnum == null) ? 1 : (maxFormnum % 1000) + 1;
		if (seq > 999) {
			return fail("当月报告单号已用尽（超过999单），请联系开发人员！");
		}
		return Ret.ok().set("data", fornum * 1000 + seq);
	}

	/**
	 * 删除数据后执行的回调
	 *
	 * @param qareport 要删除的model
	 * @param kv       携带额外参数一般用不上
	 * @return
	 */
	@Override
	protected String afterDelete(Qareport qareport, Kv kv) {
		 addDeleteSystemLog(qareport.getId(),
		 JBoltUserKit.getUserId(),qareport._getIdGenMode());
		return null;
	}

	/**
	 * 检测是否可以删除
	 *
	 * @param qareport 要删除的model
	 * @param kv       携带额外参数一般用不上
	 * @return
	 */
	@Override
	public String checkCanDelete(Qareport qareport, Kv kv) {
		// 如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(qareport, kv);
	}
	
	/**
	 * 设置返回二开业务所属的关键systemLog的targetType
	 *
	 * @return
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.QAREPORT.getValue();
	}
	
	/**
	 * 获取本年度送检数量总计
	 * <p>按批准时间 allq_time 统计当年已完成有效产品（insp=5、vd=1）的送检数量总和（带30分钟缓存）</p>
	 * @param proType 产品类型字典 SN（0=全部）
	 * @return 送检数量总计
	 */
	public Long getTotalQSI(int proType) {
		return getTotalCount("qsi", proType);
	}

	/**
	 * 获取本年度检验数量总计
	 * <p>按批准时间 allq_time 统计当年已完成有效产品（insp=5、vd=1）的检验数量总和（带30分钟缓存）</p>
	 * @param proType 产品类型字典 SN（0=全部）
	 * @return 检验数量总计
	 */
	public Long getTotalQI(int proType) {
		return getTotalCount("qi", proType);
	}
	
	/**
	 * 年度数量总计统一查询入口（带30分钟 DCL+TTL 缓存）
	 * <p>缓存 key = 统计列 + 产品类型，数据变更时由 clearFlowCountsCache 联动失效</p>
	 * @param column 统计列（qsi=送检数量 / qi=检验数量）
	 * @param proType 产品类型字典 SN（0=全部）
	 * @return 数量总计
	 */
	private Long getTotalCount(String column, int proType) {
		String cacheKey = column + "_" + proType;
		// 先检查缓存是否有效（无锁快速路径）
		if (cachedTotalCounts != null && (System.currentTimeMillis() - totalCountsCacheTimestamp) < TOTAL_COUNTS_CACHE_TTL) {
			Long cached = cachedTotalCounts.get(cacheKey);
			if (cached != null) {
				return cached;
			}
		}
		// 缓存失效，加锁查询并刷新缓存
		totalCountsCacheLock.lock();
		try {
			// 双重检查：防止多线程同时穿透
			if (cachedTotalCounts != null && (System.currentTimeMillis() - totalCountsCacheTimestamp) < TOTAL_COUNTS_CACHE_TTL) {
				Long cached = cachedTotalCounts.get(cacheKey);
				if (cached != null) {
					return cached;
				}
			}
			Map<String, Long> counts = new java.util.HashMap<>();
			// 参数化占位符，避免SQL拼接
			String sql = "SELECT SUM( sp." + column + " ) AS total "
					+ "FROM siargo_product sp LEFT JOIN siargo_prod_model pm ON pm.id=sp.siargo_prod_model_id "
					+ "INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
					+ "WHERE sp.insp = 5 AND sp.vd = 1 "
					+ "AND YEAR(sp.allq_time) = YEAR(CURDATE()) ";
			if (proType > 0) {
				counts.put(cacheKey, Db.queryLong(sql + " AND pm.prod_type = ?", proType));
			} else {
				counts.put(cacheKey, Db.queryLong(sql));
			}
			cachedTotalCounts = counts;
			totalCountsCacheTimestamp = System.currentTimeMillis();
			return counts.get(cacheKey);
		} finally {
			totalCountsCacheLock.unlock();
		}
	}
	
	/**
	 * 获取每月返修品送检数量统计数据（含今年与去年）
	 * <p>用于生成首页图表，展示全年各月的返修品数量趋势，支持年度切换</p>
	 * <p>SQL说明：按批准时间 allq_time 的年份+月份分组统计rep_type=2（返修品）的送检数量，一次查出两年数据</p>
	 * @return {curYear今年年份, lastYear去年年份, cur今年[1..12月], last去年[1..12月]}
	 */
	public Map<String, Object> getRepData() {
	    String sql = "SELECT "
				+ "  YEAR(sp.allq_time) AS yr, "
				+ "  MONTH(sp.allq_time) AS MONTH, "
				+ "  SUM(sp.qsi) AS qsi_reTotal "
				+ "FROM "
				+ "  siargo_product sp "
				+ "  INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
				+ "WHERE "
				+ "  sp.insp = 5 AND sp.vd = 1 "
				+ "  AND YEAR(sp.allq_time) IN (YEAR(CURDATE()), YEAR(CURDATE()) - 1) "
				+ "  AND sq.rep_type = " + QarepConst.REP_TYPE_REPAIR + "  "
				+ "GROUP BY "
				+ "  YEAR(sp.allq_time), MONTH(sp.allq_time) ";

	    List<Record> records = Db.find(sql);

	    int curYear = java.time.Year.now().getValue();
	    long[] cur = new long[12];
	    long[] last = new long[12];
	    for (Record record : records) {
	    	Integer month = record.getInt("MONTH");
	    	if (month == null || month < 1 || month > 12) {
	    		continue;
	    	}
	    	Long qsi = record.getLong("qsi_reTotal");
	    	if (record.getInt("yr") == curYear) {
	    		cur[month - 1] = qsi == null ? 0L : qsi;
	    	} else {
	    		last[month - 1] = qsi == null ? 0L : qsi;
	    	}
	    }

	    Map<String, Object> result = new LinkedHashMap<>();
	    result.put("curYear", curYear);
	    result.put("lastYear", curYear - 1);
	    result.put("cur", cur);
	    result.put("last", last);
	    return result;
	}

    /** 按批准时间allq_time进行月度统计，按报告系列及版号关联的模板归入三类。 */
    public Map<String, Object> getRepAllData() {
        Map<String, Map<String, Object>> series = newChartSeries(12, false);
        List<Record> rows = Db.find("SELECT MONTH(sp.allq_time) period, category.sn, SUM(sp.qsi) quantity "
                + "FROM siargo_product sp " + DASHBOARD_TEMPLATE_CATEGORY_JOIN
                + "JOIN siargo_qareport sq ON sq.id=sp.report_id WHERE sp.insp=5 AND sp.vd=1 AND YEAR(sp.allq_time)=YEAR(CURDATE()) "
                + "GROUP BY 1,2 ORDER BY period,sn");
        for (Record row : rows) {
            Map<String, Object> item = series.get(row.getStr("sn"));
            ((long[]) item.get("data"))[row.getInt("period") - 1] = row.getLong("quantity") == null ? 0 : row.getLong("quantity");
        }
        List<Map<String, Object>> types = new ArrayList<>();
        for (Map<String, Object> item : series.values()) types.add(Map.of("sn", item.get("sn"), "name", item.get("name")));
        return Map.of("types", types, "months", List.of(1,2,3,4,5,6,7,8,9,10,11,12), "series", new ArrayList<>(series.values()));
    }

    /** 按批准时间allq_time进行本年和上年季度统计，与月度统计使用相同的模板三分类。 */
    public Map<String, Object> getQuarterCompareData() {
        int year = LocalDate.now().getYear();
        Map<String, Map<String, Object>> series = newChartSeries(4, true);
        List<Record> rows = Db.find("SELECT YEAR(sp.allq_time) yr,QUARTER(sp.allq_time) period,category.sn,SUM(sp.qsi) quantity "
                + "FROM siargo_product sp " + DASHBOARD_TEMPLATE_CATEGORY_JOIN
                + "JOIN siargo_qareport sq ON sq.id=sp.report_id WHERE sp.insp=5 AND sp.vd=1 AND YEAR(sp.allq_time) IN (?,?) "
                + "GROUP BY 1,2,3", year, year - 1);
        for (Record row : rows) {
            Map<String, Object> item = series.get(row.getStr("sn"));
            long[] values = (long[]) item.get(row.getInt("yr") == year ? "current" : "previous");
            values[row.getInt("period") - 1] = row.getLong("quantity") == null ? 0 : row.getLong("quantity");
        }
        return Map.of("curYear", year, "lastYear", year - 1, "series", new ArrayList<>(series.values()));
    }

    private Map<String, Map<String, Object>> newChartSeries(int periods, boolean quarterly) {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> category : DASHBOARD_CATEGORIES) {
            chartSeries(result, category.getKey(), category.getValue(), periods, quarterly);
        }
        return result;
    }

    private Map<String, Object> chartSeries(Map<String, Map<String, Object>> series, String sn, String name, int periods, boolean quarterly) {
        String key = sn == null ? "" : sn;
        return series.computeIfAbsent(key, ignored -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("sn", key);
            item.put("name", chartTypeName(key, name));
            if (quarterly) { item.put("current", new long[periods]); item.put("previous", new long[periods]); }
            else item.put("data", new long[periods]);
            return item;
        });
    }

	/**
	 * 分页查询回收站中的报告单列表
	 * <p>查询条件：vd=0（已删除）</p>
	 * <p>关联查询产品表、客户表、用户表和字典表，获取完整展示信息</p>
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @param keywords 搜索关键字（订单号模糊匹配）
	 * @return 回收站数据分页
	 */
	public Page<Record> paginateInactiveListDatas(int pageNumber, int pageSize, String keywords) {
		Sql sql = Sql.mysql()
				// 选择字段：报告单基础信息（雪花ID统一CAST为字符串，避免前端精度丢失）
				.select("CAST(sq.id AS CHAR) AS id", "sq.order_id", "sc.name AS sc_name", "sq.formnum", "sp.insp",
						// 检验时间信息
						"sp.accq_time", "sp.funq_time", "sp.appq_time", "sp.allq_time",
						// 检验人员姓名
						"accq_user.name AS accq_name", "funq_user.name AS funq_name", "appq_user.name AS appq_name",
						"allq_user.name AS allq_name", "DATE_FORMAT(sq.create_time, '%Y-%m-%d %H:%i') as create_time",
						// 产品信息字段
						"CAST(sp.id AS CHAR) as spid", "sp.model as sp_model", "sp.number as sp_number", "pm.prod_type as sp_type", "pm.prod_type AS prod_type", "CAST(sp.siargo_prod_model_id AS CHAR) AS siargo_prod_model_id", "pm.model_series",
						"sp.flow_range as sp_flow_range",
						"sp.pdfstr AS sp_pdfstr", "sp.pdfver AS sp_pdfver","sp.cuc as sp_cuc", "sp.pv as sp_pv",
						"sp.thv as sp_thv", "sp.zp as sp_zp", "sp.fl as sp_fl", "sp.cucmax as sp_cucmax",
						"sp.cucmin as sp_cucmin", "sp.bv as sp_bv", "sp.la as sp_la",
						// 字典翻译字段
						PRODUCT_TYPE_NAME_SQL + " AS type_name","d_insp.name AS insp_name","d_flow.name AS flow_name",
						"d_pdfver.name AS pdfver_name","d_retype.name AS retype_name",
						// 删除信息
						"sp.delete_des", "DATE_FORMAT(sp.delete_time, '%Y-%m-%d %H:%i') as delete_time"
						)
				.page(pageNumber, pageSize).from("siargo_product", "sp")
                .leftJoin("siargo_prod_model", "pm", "pm.id=sp.siargo_prod_model_id")
				// ========== 关联报告单表 ==========
				.leftJoin("siargo_qareport", "sq", "sq.id = sp.report_id")
				// ========== 关联客户表 ==========
				.leftJoin("siargo_customer", "sc", "sc.id = sq.cust_id")
				// ========== 关联字典表获取产品类型名称 ==========
				.leftJoin(ProductSeriesService.TYPE_LABELS_SQL, "d_type", "d_type.type_key = 'siargo_prod_type' "
						+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(pm.prod_type AS CHAR) "
						+ "AND d_type.enable = '1'")
				// ========== 关联字典表获取报告类型名称 ==========
				.leftJoin("jb_dictionary", "d_retype", "d_retype.type_key = 'siargo_rep_type' "
						+ "AND d_retype.sn COLLATE utf8mb4_general_ci = CAST(sq.rep_type AS CHAR) "
						+ "AND d_retype.enable = '1'")
				// ========== 关联字典表获取检验进度名称 ==========
				.leftJoin("jb_dictionary", "d_insp", "d_insp.type_key = 'siargo_insp' "
						+ "AND d_insp.sn COLLATE utf8mb4_general_ci = CAST(sp.insp AS CHAR) "
						+ "AND d_insp.enable = '1'")
				// ========== 关联字典表获取PDF版本名称 ==========
				.leftJoin("jb_dictionary", "d_pdfver", "d_pdfver.type_key = 'siargo_pdfver' "
						+ "AND d_pdfver.name COLLATE utf8mb4_general_ci = sp.pdfver "
						+ "AND d_pdfver.enable = '1'")
				// ========== 关联字典表获取流量范围名称 ==========
				.leftJoin("jb_dictionary", "d_flow", "d_flow.type_key = 'siargo_flow_range' "
						+ "AND d_flow.sn COLLATE utf8mb4_general_ci = sp.flow_range "
						+ "AND d_flow.enable = '1'")
				// ========== 关联用户表获取各阶段检验人员信息 ==========
				.leftJoin("jb_user", "accq_user", "accq_user.id = sp.accq_uid")
				.leftJoin("jb_user", "funq_user", "funq_user.id = sp.funq_uid")
				.leftJoin("jb_user", "appq_user", "appq_user.id = sp.appq_uid")
				.leftJoin("jb_user", "allq_user", "allq_user.id = sp.allq_uid")
				// ========== 查询回收站数据（vd=0）==========
				.eq("sp.vd", QarepConst.VD_DELETED);
	
		// ========== 应用搜索条件 ==========
		sql.like("sq.order_id", keywords);
				
		sql.orderBy("sp.delete_time", true);
				
		Page<Record> page = paginateRecord(sql, true);
		// 用户可控文本转义，防止列表展示时XSS
		escapeRecordFields(page.getList(), "delete_des");
		return page;
	}
	
	/**
	 * 根据订单号查询订单检验状态（对外API使用）
	 * <p>查询指定订单下所有有效产品的检验状态信息，包括检验进度、各阶段检验时间和检验人员</p>
	 * @param orderId 订单号
	 * @return 产品检验状态列表，如无数据返回空集合（与 batchQueryOrderStatus 风格一致）
	 */
	public List<Record> queryOrderStatusByOrderId(String orderId) {
		String sql = "SELECT " 
				+ "sp.model, "
				+ "sp.number, "
				+ "sp.insp, "
				+ "sp.lt_status, "
				+ "sp.accq_time, "
				+ "sp.funq_time, "
				+ "sp.appq_time, "
				+ "sp.allq_time, "
				+ "sp.lt_time, "
				+ "u1.name AS accq_name, "
				+ "u2.name AS funq_name, "
				+ "u3.name AS appq_name, "
				+ "u4.name AS allq_name, "
				+ "u5.name AS lt_name "
				+ "FROM siargo_product sp LEFT JOIN siargo_prod_model pm ON pm.id=sp.siargo_prod_model_id "
				+ "LEFT JOIN siargo_qareport sq ON sp.report_id = sq.id "
				+ "LEFT JOIN jb_user u1 ON sp.accq_uid = u1.id "
				+ "LEFT JOIN jb_user u2 ON sp.funq_uid = u2.id "
				+ "LEFT JOIN jb_user u3 ON sp.appq_uid = u3.id "
				+ "LEFT JOIN jb_user u4 ON sp.allq_uid = u4.id "
				+ "LEFT JOIN jb_user u5 ON sp.lt_uid = u5.id "
				+ "WHERE sq.order_id = ? AND sp.vd = 1 "
				+ "ORDER BY sp.id ASC";

		List<Record> list = Db.find(sql, orderId);
		return list != null ? list : new ArrayList<>();
	}

	/**
	 * 批量查询订单检验状态（对外API使用，一次查询代替 N 次）
	 * <p>使用 IN 查询一次性获取所有 orderId 对应的产品状态，按 orderId 分组返回</p>
	 * @param orderIds 有效订单号列表（非空且已trim）
	 * @return Map<orderId, List<Record>>，key为订单号，value为该订单的产品状态列表
	 */
	public Map<String, List<Record>> batchQueryOrderStatus(List<String> orderIds) {
		if (orderIds == null || orderIds.isEmpty()) {
			return new java.util.LinkedHashMap<>();
		}

		// 构建 IN 子句的 ? 占位符
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT sq.order_id, sp.model, sp.number, sp.insp, sp.lt_status, ")
			.append("sp.accq_time, sp.funq_time, sp.appq_time, sp.allq_time, sp.lt_time, ")
			.append("u1.name AS accq_name, u2.name AS funq_name, ")
			.append("u3.name AS appq_name, u4.name AS allq_name, u5.name AS lt_name ")
			.append("FROM siargo_product sp LEFT JOIN siargo_prod_model pm ON pm.id=sp.siargo_prod_model_id ")
			.append("LEFT JOIN siargo_qareport sq ON sp.report_id = sq.id ")
			.append("LEFT JOIN jb_user u1 ON sp.accq_uid = u1.id ")
			.append("LEFT JOIN jb_user u2 ON sp.funq_uid = u2.id ")
			.append("LEFT JOIN jb_user u3 ON sp.appq_uid = u3.id ")
			.append("LEFT JOIN jb_user u4 ON sp.allq_uid = u4.id ")
			.append("LEFT JOIN jb_user u5 ON sp.lt_uid = u5.id ")
			.append("WHERE sq.order_id IN (");

		for (int i = 0; i < orderIds.size(); i++) {
			if (i > 0) {
				sql.append(",");
			}
			sql.append("?");
		}
		sql.append(") AND sp.vd = 1 ORDER BY sp.id ASC");

		List<Record> allRecords = Db.find(sql.toString(), orderIds.toArray());

		// 按 orderId 分组（保持传入顺序）
		Map<String, List<Record>> result = new LinkedHashMap<>();
		for (String oid : orderIds) {
			result.put(oid, new ArrayList<>());
		}
		for (Record r : allRecords) {
			String oid = r.getStr("order_id");
			List<Record> list = result.get(oid);
			if (list != null) {
				list.add(r);
			}
		}
		return result;
	}

    /** 按批准时间allq_time统计本年度已完成产品的模板三分类，与月度、季度统计保持一致。 */
    public List<Map<String, Object>> getDonutData() {
        List<Record> rows = Db.find("SELECT category.sn,COUNT(*) quantity "
                + "FROM siargo_product sp " + DASHBOARD_TEMPLATE_CATEGORY_JOIN
                + "JOIN siargo_qareport sq ON sq.id=sp.report_id WHERE sp.insp=5 AND sp.vd=1 AND YEAR(sp.allq_time)=YEAR(CURDATE()) "
                + "GROUP BY category.sn");
        Map<String, Long> quantities = new java.util.HashMap<>();
        for (Record row : rows) quantities.put(row.getStr("sn"), row.getLong("quantity"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, String> category : DASHBOARD_CATEGORIES) {
            result.add(Map.of("sn", category.getKey(), "label", category.getValue(),
                    "value", quantities.getOrDefault(category.getKey(), 0L)));
        }
        return result;
    }

	/**
	 * 当 insp 状态变更时，为下一阶段对应权限的用户创建待办通知
	 * <p>映射关系：insp=4 → 通知角色SN=214（批准）；insp=2/3 的通知暂时关闭</p>
	 * <p>jb_todo 集中裸写说明：TodoService.save() 会强制将 userId 覆盖为当前登录用户，
	 * 无法为其他目标用户创建待办，故此处直接操作 Todo Model，集中在本方法内维护</p>
	 * <p>事务与事件：待办批量落库包裹在 Db.tx 中，事务返回成功后才统一 EventKit.post，
	 * 避免落库回滚后仍推送 WebSocket 消息；注意若本方法被外层 action 事务（@Before(Tx.class)）
	 * 嵌套调用，内层 Db.tx 会加入外层事务，事件仍会在外层提交前发出，属已知局限</p>
	 * <p>任何异常不影响主流程，全部 try-catch 包裹</p>
	 * @param newInsp 产品更新后的新 insp 值
	 */
	public void notifyNextStageUsers(int newInsp) {
		// 目前仅在产品进入待批准（insp=4）时通知批准员，其他环节通知暂时关闭
		if (newInsp != QarepConst.INSP_PENDING_APPROVAL) {
			return;
		}
		try {
			int roleSn = QarepConst.ROLE_SN_APPROVAL;
			String stageName = "批准";
			String countKey = "appq";

			// ========== 查询目标角色ID ==========
			Long roleId = roleService.findIdBySn(roleSn);
			if (roleId == null) {
				// 角色不存在，无法通知，直接返回
				return;
			}

			// ========== 查询拥有该角色的用户列表 ==========
			List<User> users = userService.getUsersByRoleId(roleId);
			if (users == null || users.isEmpty()) {
				// 该角色下无用户，无需通知
				return;
			}

			// ========== 获取当前阶段待处理数量 ==========
			// clearFlowCountsCache() 已在 batchInspection 中调用，此处可得到最新数据
			Map<String, Long> flowCounts = getFlowCounts();
			long pendingCount = flowCounts.getOrDefault(countKey, 0L);

			// ========== 构建待办标题前缀（用于防重复检查） ==========
			String titlePrefix = "有新的" + stageName + "报告单需要处理";
			String title = titlePrefix + "：当前还剩" + pendingCount + "条！";
			String url = "/admin/siargo/qarep";

			// 当前操作用户（待办创建人）
			Long operatorUserId = JBoltUserKit.getUserId();
			// 待办规定完成时间：当前时间 + 15 天
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 15);
			Date finishTime = cal.getTime();
			String now = DateUtil.getDateString(DateUtil.YMDHMS);

			// ========== 事务内批量落库，事务提交成功后再统一发送事件 ==========
			List<Todo> savedTodos = new ArrayList<>();
			boolean txOk = Db.tx(() -> {
				for (User user : users) {
					Long targetUserId = user.getId();
					if (targetUserId == null) {
						continue;
					}

					// 防重复检查：该用户是否已有同类未完成待办（state IN (1,2)）
					String checkSql = "SELECT COUNT(*) FROM jb_todo WHERE user_id = ? AND title LIKE ? AND state IN (1, 2)";
					Long existCount = Db.queryLong(checkSql, targetUserId, titlePrefix + "%");
					if (existCount != null && existCount > 0) {
						// 已存在未完成的同类待办，跳过该用户
						continue;
					}

					// 构建 Todo 对象并手动设置所有必填字段
					Todo todo = new Todo();
					todo.autoProcessIdValue();                // 自动生成雪花 ID
					todo.setTitle(title);                    // 待办标题
					todo.setUserId(targetUserId);            // 目标用户
					todo.setState(2);                        // 状态：进行中
					todo.setType(1);                         // 类型：无链接无内容
					todo.setPriorityLevel(1);                // 优先级：普通
					todo.setUrl(url);                        // 待办链接
					todo.setIsReaded(false);                 // 未读
					todo.setCreateUserId(operatorUserId);    // 创建人：当前操作用户
					todo.setUpdateUserId(operatorUserId);    // 更新人：当前操作用户
					todo.set("create_time", now);                 // 创建时间
					todo.set("update_time", now);                 // 更新时间
					todo.setSpecifiedFinishTime(finishTime); // 规定完成时间

					if (todo.save()) {
						savedTodos.add(todo);
					}
				}
				return true;
			});

			// 事务提交成功后才触发 EventKit 事件，推送 WebSocket 消息给目标用户
			if (txOk) {
				for (Todo todo : savedTodos) {
					EventKit.post(todo);
				}
			}
		} catch (Exception e) {
			// 通知失败不影响主流程，记录异常日志
			LOG.error("通知下一环节用户创建待办失败", e);
		}
	}

}
