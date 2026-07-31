package cn.jbolt.admin.siargo.qarep;

import cn.jbolt.siargo.model.Product;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
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

	/** 检验报告单数据访问对象 */
	private final Qareport dao = new Qareport().dao();
	// ========== 流程统计缓存（30分钟有效期） ==========
	private static final long FLOW_COUNTS_CACHE_TTL = 30 * 60 * 1000L; // 30分钟
	private volatile Map<String, Long> cachedFlowCounts;
	private volatile long flowCountsCacheTimestamp;
	private final ReentrantLock flowCountsCacheLock = new ReentrantLock();

	// ========== 管理端分页数据缓存（30秒有效期，降低重复查询开销） ==========
	// 仅缓存"空关键字 + 无日期范围 + 第一页"的查询，key空间有限（prodType×insp×pageSize），防止无限增长
	private static final long PAGINATE_CACHE_TTL = 30 * 1000L;
	private volatile Map<String, Page<Record>> cachedPaginateData;
	private volatile long paginateCacheTimestamp;
	private final ReentrantLock paginateCacheLock = new ReentrantLock();

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

	@Override
	protected Qareport dao() {
		return dao;
	}
	
	/**
	 * 获取各流程阶段的数量统计（带30分钟缓存）
	 * <p>返回不可变Map，防止调用方误改缓存内容</p>
	 * @return Map包含各阶段数量：all(全部), noq(精度待检), accq(外观待检), funq(包装待检), appq(待批准), allq(已完成)
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
	 * 主动清除流程统计缓存（数据变更时调用）
	 */
	public void clearFlowCountsCache() {
		cachedFlowCounts = null;
		flowCountsCacheTimestamp = 0;
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
		// 入口校验：insp必须在[2,5]范围内
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
		String now = DateUtil.getDateString(DateUtil.YMDHMS);
		List<String> failedItems = new ArrayList<>();
		int successCount = 0;
		for (Long id : ids) {
			if (id == null) {
				continue;
			}
			// 条件更新：仅当前状态为目标状态-1且有效时才更新，避免并发重复处理/状态跳跃
			int rows = Db.update(
					"UPDATE siargo_product SET insp = ?, " + stageCol + "_uid = ?, " + stageCol + "_time = ? "
					+ "WHERE id = ? AND insp = ? AND vd = " + QarepConst.VD_VALID,
					insp, userId, now, id, insp - 1);
			if (rows > 0) {
				successCount++;
			} else {
				failedItems.add(describeProduct(id));
			}
		}
		if (failedItems.isEmpty()) {
			return Ret.ok();
		}
		String failMsg = "以下产品未处理成功（当前状态已变化或已被他人处理）：" + String.join("、", failedItems);
		if (successCount == 0) {
			return fail(failMsg);
		}
		// 部分成功：成功的行保留，失败明细通过msg返回
		return Ret.ok().set("msg", "已成功处理 " + successCount + " 条。" + failMsg);
	}

	/**
	 * 批量驳回产品检验状态至上一阶段
	 * <p>状态回退映射：</p>
	 * <ul>
	 *   <li>insp=2（外观待检）→ insp=1（精度待检），清空精度检验完成记录（accq_uid/accq_time）</li>
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
		int successCount = 0;
		for (Long id : ids) {
			if (id == null) {
				continue;
			}
			Product product = productService.findById(id);
			if (product == null) {
				failedItems.add("ID:" + id + "（数据不存在）");
				continue;
			}
			// 当前检验阶段：仅2~4可驳回（已完成insp=5与精度待检insp=1不可驳回）
			Integer cur = product.getInt("insp");
			if (cur == null || cur < QarepConst.INSP_REJECT_MIN || cur > QarepConst.INSP_REJECT_MAX) {
				failedItems.add(describeProduct(id) + "（当前状态不可驳回）");
				continue;
			}
			// 服务端角色校验：当前环节负责角色（212~214）或超管才可驳回
			if (!canRejectStage(userId, cur)) {
				failedItems.add(describeProduct(id) + "（无该环节驳回权限）");
				continue;
			}
			// 条件更新：清空被驳回阶段的完成记录并回退，仅当状态未被他人变更时生效
			String stageCol = QarepConst.rejectClearStageColumn(cur);
			int rows = Db.update(
					"UPDATE siargo_product SET insp = ?, " + stageCol + "_uid = NULL, " + stageCol + "_time = NULL "
					+ "WHERE id = ? AND insp = ? AND vd = " + QarepConst.VD_VALID,
					cur - 1, id, cur);
			if (rows > 0) {
				successCount++;
				// 追加驳回历史记录：环节（2=外观检验 3=包装检验 4=批准）、原因、驳回人、时间
				productRejectLogService.saveLog(id, cur, rejectDes, userId);
			} else {
				failedItems.add(describeProduct(id) + "（状态已变化或已被他人处理）");
			}
		}
		if (failedItems.isEmpty()) {
			return Ret.ok();
		}
		String failMsg = "以下产品未驳回成功：" + String.join("、", failedItems);
		if (successCount == 0) {
			return fail(failMsg);
		}
		return Ret.ok().set("msg", "已成功驳回 " + successCount + " 条。" + failMsg);
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
				if (!product.update()) {
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
		return product.update();
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
		Product product = productService.findById(id);
		if (product == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		product.setDes(des == null ? "" : des.trim());
		boolean success = product.update();
		return ret(success);
	}

	/**
	 * 批量永久删除产品（物理删除，事务性级联）
	 * <p>级联顺序：</p>
	 * <ol>
	 *   <li>删除 siargo_product_reject_log 对应驳回历史</li>
	 *   <li>删除产品记录</li>
	 *   <li>若报告单下已无产品，一并删除 siargo_qareport</li>
	 *   <li>删除已生成的PDF物理文件（DB操作成功后执行，带路径穿越检测）</li>
	 * </ol>
	 * <p>DB删除失败时抛出RuntimeException触发事务回滚（需在事务中调用）</p>
	 * @param ids 产品ID列表
	 * @return 操作结果
	 */
	public Ret permanentDelete(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Long userId = JBoltUserKit.getUserId();
		String webRoot = PathKit.getWebRootPath();
		for (Long id : ids) {
			if (id == null) {
				continue;
			}
			Product product = productService.findById(id);
			if (product == null) {
				continue;
			}
			// 删除前组装日志描述（报告单编号/订单号/型号/编号/客户/删除原因）
			String logDesc = buildPermanentDeleteLogDesc(product);
			// 1. 级联删除驳回历史
			Db.delete("DELETE FROM siargo_product_reject_log WHERE product_id = ?", id);
		// 2. 删除产品记录（失败返回 fail，配合 Db.tx() 回滚）
			if (!product.delete()) {
				return fail("产品记录删除失败，ID=" + id);
			}
			// 3. 报告单下已无产品（含回收站中的）则一并删除报告单
			Long reportId = product.getReportId();
			if (reportId != null) {
				Long remain = Db.queryLong("SELECT COUNT(*) FROM siargo_product WHERE report_id = ?", reportId);
				if (remain != null && remain == 0) {
					Db.deleteById("siargo_qareport", reportId);
				}
			}
			// 4. 删除已生成的PDF物理文件（放在DB操作成功之后）
			deleteGeneratedPdf(webRoot, product.getPdfstr());
			// 5. 记录永久删除系统日志
			addDeleteSystemLog(id, userId, logDesc);
		}
		return Ret.ok();
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
	 * 删除产品已生成的PDF物理文件（带路径穿越检测）
	 * @param webRoot Web根目录
	 * @param pdfstr PDF相对路径
	 */
	private void deleteGeneratedPdf(String webRoot, String pdfstr) {
		if (pdfstr == null || pdfstr.isEmpty()) {
			return;
		}
		// 路径穿越检测
		if (pdfstr.contains("..")) {
			LOG.warn("检测到非法PDF路径，跳过删除: " + pdfstr);
			return;
		}
		File pdfFile = new File(webRoot + (pdfstr.startsWith("/") ? pdfstr : "/" + pdfstr));
		if (pdfFile.exists() && pdfFile.isFile() && !pdfFile.delete()) {
			LOG.warn("PDF文件删除失败: " + pdfFile.getAbsolutePath());
		}
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
				+ ", SUM(CASE WHEN insp = 1 THEN sp.qsi ELSE 0 END) AS qsi_1"
				+ ", SUM(CASE WHEN insp = 2 THEN sp.qsi ELSE 0 END) AS qsi_2"
				+ ", SUM(CASE WHEN insp = 3 THEN sp.qsi ELSE 0 END) AS qsi_3"
				+ ", SUM(CASE WHEN insp = 4 THEN sp.qsi ELSE 0 END) AS qsi_4"
				+ ", SUM(CASE WHEN insp = 5 THEN sp.qsi ELSE 0 END) AS qsi_5"
				+ " FROM siargo_product sp WHERE sp.vd = 1";
		Record row = Db.findFirst(sql);
		if (row != null) {
			counts.put("all", row.getLong("all_count") != null ? row.getLong("all_count") : 0L);
			counts.put("noq", row.getLong("insp_1") != null ? row.getLong("insp_1") : 0L);
			counts.put("accq", row.getLong("insp_2") != null ? row.getLong("insp_2") : 0L);
			counts.put("funq", row.getLong("insp_3") != null ? row.getLong("insp_3") : 0L);
			counts.put("appq", row.getLong("insp_4") != null ? row.getLong("insp_4") : 0L);
			counts.put("allq", row.getLong("insp_5") != null ? row.getLong("insp_5") : 0L);
			counts.put("noq_qsi", row.getLong("qsi_1") != null ? row.getLong("qsi_1") : 0L);
			counts.put("accq_qsi", row.getLong("qsi_2") != null ? row.getLong("qsi_2") : 0L);
			counts.put("funq_qsi", row.getLong("qsi_3") != null ? row.getLong("qsi_3") : 0L);
			counts.put("appq_qsi", row.getLong("qsi_4") != null ? row.getLong("qsi_4") : 0L);
			counts.put("allq_qsi", row.getLong("qsi_5") != null ? row.getLong("qsi_5") : 0L);
		} else {
			counts.put("all", 0L);
			counts.put("noq", 0L);
			counts.put("accq", 0L);
			counts.put("funq", 0L);
			counts.put("appq", 0L);
			counts.put("allq", 0L);
			counts.put("noq_qsi", 0L);
			counts.put("accq_qsi", 0L);
			counts.put("funq_qsi", 0L);
			counts.put("appq_qsi", 0L);
			counts.put("allq_qsi", 0L);
		}
		return counts;
	}
	
	/**
	 * 获取上个月已完成最终放行的产品ID列表
	 * <p>用于批量生成上个月的归档PDF文件</p>
	 * <p>查询条件：vd=1（有效）、insp=5（已放行）、allq_time在上月范围内</p>
	 * @return 上月已放行产品的ID列表
	 */
	public List<Record> getIds() {
	    // 构建查询：查询上个月完成最终放行的有效产品
	    Sql sql = Sql.mysql()
	            .select("sp.id")
	            .from("siargo_product", "sp")
	            .eq("sp.vd", QarepConst.VD_VALID)  // 有效数据
	            .eq("sp.insp", QarepConst.INSP_COMPLETED)  // 已完成最终放行
	            .bwDate("sp.allq_time",  // 最终放行时间在上月范围内
	                    DateUtil.lastMonthFirstDay(DateUtil.getNow()),
	                    DateUtil.lastMonthLastDay(DateUtil.getNow()));

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
	 * <p>关联查询产品表、客户表、用户表和字典表，获取完整展示信息</p>
	 * <p>说明：id/spid 使用 CAST(... AS CHAR) 输出，避免前端雪花ID精度丢失；sp_des 输出前做HTML转义</p>
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @param keywords 搜索关键字（订单号模糊匹配）
	 * @param prodType 产品类型（1=传感器，2=小流量，3=大流量，0=全部）
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
				.select("CAST(sq.id AS CHAR) AS id", "sq.order_id", "sc.name AS sc_name", "sq.formnum","sp.insp",
						// 检验时间信息
						"sp.accq_time", "sp.funq_time", "sp.appq_time", "sp.allq_time",
						// 检验人员姓名
						"accq_user.name AS accq_name", "funq_user.name AS funq_name", "appq_user.name AS appq_name",
						"allq_user.name AS allq_name", "DATE_FORMAT(sq.create_time, '%Y-%m-%d %H:%i') as create_time",
						// 产品信息字段
						"CAST(sp.id AS CHAR) as spid", "sp.model as sp_model", "sp.number as sp_number", "sp.type as sp_type",
						"sp.qsi as sp_qsi", "sp.qi as sp_qi", "sp.flow_range as sp_flow_range", "sp.des as sp_des", 
						"sp.pdfstr AS sp_pdfstr", "sp.pdfver AS sp_pdfver","sp.cuc as sp_cuc", "sp.pv as sp_pv", 
						"sp.thv as sp_thv", "sp.zp as sp_zp", "sp.fl as sp_fl", "sp.cucmax as sp_cucmax", 
						"sp.cucmin as sp_cucmin", "sp.bv as sp_bv", "sp.la as sp_la", 
						// 字典翻译字段
						"d_type.name AS type_name","d_insp.name AS insp_name","d_flow.name AS flow_name",
						"d_pdfver.name AS pdfver_name","d_retype.name AS retype_name",
						// 驳回历史条数（>0 时前端显示「驳」角标，点击查看历史）
						"sp.reject_count"
						)
				.page(pageNumber, pageSize).from("siargo_product", "sp")
				// ========== 关联报告单表 ==========
				.leftJoin("siargo_qareport", "sq", "sq.id = sp.report_id")
				// ========== 关联客户表 ==========
				.leftJoin("siargo_customer", "sc", "sc.id = sq.cust_id")
				// ========== 关联字典表获取产品类型名称 ==========
				.leftJoin("jb_dictionary", "d_type", "d_type.type_key = 'siargo_prod_type' "
						+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(sp.type AS CHAR) "
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
				.leftJoin("jb_user", "allq_user", "allq_user.id = sp.allq_uid").eq("sp.vd", QarepConst.VD_VALID);
	
		// ========== 应用搜索条件 ==========
		sql.like("sq.order_id", keywords);
			
		// ========== 应用日期范围筛选 ==========
		if (isOk(startTime) && isOk(endTime)) {
			sql.bwDate("sq.create_time",startTime,endTime);
		}
			
		// ========== 应用产品类型筛选 ==========
		if (prodType > 0) {
			sql.eq("sp.type", prodType);
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
	        	 sql.orderBy("sq.create_time", true);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
	         case QarepConst.INSP_PENDING_PACKAGING:
	        	 sql.orderBy("sp.funq_time", true);  // 主排序：上一个进度(外观检验)完成时间
	        	 sql.orderBy("sq.create_time", true);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
	         case QarepConst.INSP_PENDING_APPROVAL:
	        	 sql.orderBy("sp.appq_time", true);  // 主排序：上一个进度(包装检验)完成时间
	        	 sql.orderBy("sq.create_time", true);
	        	 sql.orderBy("sq.formnum", true);
	        	 break;
	         case QarepConst.INSP_COMPLETED:
	        	 sql.orderBy("sq.formnum", true);    // 报告单标号倒序
	        	 sql.orderBy("sq.order_id", true);   // 订单号倒序
	        	 sql.orderBy("sp.type", true);       // 产品类型倒序
	        	 sql.orderBy("sp.allq_time", true);  // 批准时间倒序（最新优先）
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

	/**
	 * 保存报告单和产品数据
	 * <p>事务协调说明：先保存报告单获取ID，再关联产品记录；所有业务校验在任何写库操作之前完成</p>
	 * <p>如果检验进度为精度检验（insp=2），自动记录精度检验人员和时间</p>
	 * <p>写库失败抛出RuntimeException触发事务回滚（需在事务中调用）</p>
	 * @param qareport 报告单对象
	 * @param product 产品对象
	 * @return 操作结果
	 */
	public Ret save(Qareport qareport, Product product) {
		// ========== 全部校验前置：任何写库操作之前 ==========
		if (qareport == null || product == null || product.getInsp() == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}

		// 校验检验进度：精度检验之前不能跳过
		if (product.getInsp() > QarepConst.INSP_PENDING_APPEARANCE) {
			return fail("未检验精度，请重新选择检验进度！");
		}

		// 校验数量：送检数量不能小于检验数量
		if (product.getQsi() != null && product.getQi() != null && product.getQsi() < product.getQi()) {
			return fail("送检数量小于检验数量，重新输入！");
		}

		// ========== 保存报告单（如果不存在）==========
		if (notOk(qareport.getId())) {

			// 设置创建时间和自动生成报告单编号
			qareport.set("create_time", DateUtil.getDateString(DateUtil.YMDHMS));
			Ret formnumRet = creatFormnum();
			if (formnumRet.isFail()) {
				return formnumRet;
			}
			qareport.set("formnum", formnumRet.get("data"));

			boolean qaSaved;
			try {
				qaSaved = qareport.save();
			} catch (Exception e) {
				// UNIQUE索引冲突时给出可读报错（formnum唯一索引由DBA维护）
				String msg = e.getMessage();
				if (msg != null && msg.contains("Duplicate")) {
					return fail("报告单编号生成冲突（并发操作），请重试！");
				}
				return fail("报告单保存失败：" + (msg != null ? msg : e.getClass().getSimpleName()));
			}
			if (!qaSaved) {
				return fail("报告单保存失败，请重试！");
			}
		}

		// ========== 保存产品记录 ==========
		boolean prodsuccess;
		if (notOk(product.getId())) {
			// 新建产品记录
			Product pro = new Product();

			// 如果检验进度为精度检验，记录精度检验数据
			if (product.getInsp() == QarepConst.INSP_PENDING_APPEARANCE) {
				pro.set("accq_uid", JBoltUserKit.getUserId());
				pro.set("accq_time", DateUtil.getDateString(DateUtil.YMDHMS));
			}
			
			// 复制产品属性
			pro.set("insp", product.getInsp());
			pro.set("type", product.getType());
			pro.set("model", product.getModel());
			pro.set("qsi", product.getQsi());
			pro.set("qi", product.getQi());
			pro.set("number", product.getNumber());
			pro.set("flow_range", product.getFlowRange());
			pro.set("des", product.getDes());
			pro.set("pdfver", product.getPdfver());
			// 关联报告单
			pro.set("report_id", qareport.getId());
			// 电气参数
			pro.set("cuc", product.getCuc());
			pro.set("cucmax", product.getCucmax());
			pro.set("cucmin", product.getCucmin());
			pro.set("pv", product.getPv());
			pro.set("thv", product.getThv());
			pro.set("zp", product.getZp());
			pro.set("fl", product.getFl());
			pro.set("bv", product.getBv());
			pro.set("la", product.getLa());
			pro.set("vd", QarepConst.VD_VALID);  // 标记为有效数据
			prodsuccess = pro.save();

		} else {
			// 更新已有产品记录
			// 如果检验进度为精度检验，记录精度检验数据
			if (product.getInsp() == QarepConst.INSP_PENDING_APPEARANCE) {
				product.set("accq_uid", JBoltUserKit.getUserId());
				product.set("accq_time", DateUtil.getDateString(DateUtil.YMDHMS));
			}

			product.set("report_id", qareport.getId());
			product.set("vd", QarepConst.VD_VALID);
			prodsuccess = product.save();
		}

		if (!prodsuccess) {
			return fail("产品记录保存失败，请重试！");
		}
		return Ret.ok();
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
				+ "  sq.cust_id,\n" + "  sq.formnum,\n" + "  sp.type AS prod_type,\n" + "  sq.rep_type,\n"
				+ "  sp.insp,\n" + "  DATE_FORMAT(sp.accq_time, '%Y-%m-%d %H:%i') AS accq_time,\n"
				+ "  DATE_FORMAT(sp.funq_time, '%Y-%m-%d %H:%i') AS funq_time,\n"
				+ "  DATE_FORMAT(sp.appq_time, '%Y-%m-%d %H:%i') AS appq_time,\n"
				+ "  DATE_FORMAT(sp.allq_time, '%Y-%m-%d %H:%i') AS allq_time,\n" 
				+ "  accq_user.NAME AS accq_name,\n funq_user.NAME AS funq_name,\n" 
				+ "  appq_user.NAME AS appq_name,\n allq_user.NAME AS allq_name,\n"
				+ "  accq_user.email AS accq_email,\n funq_user.email AS funq_email,\n" 
				+ "  appq_user.email AS appq_email,\n allq_user.email AS allq_email,\n"
				+ "  DATE_FORMAT(sq.create_time, '%Y-%m-%d %H:%i') AS create_time,\n"
				+ "  DATE_FORMAT(sq.create_time, '%Y.%m.%d') AS c_time,\n" + "  sp.id AS spid,\n"
				+ "  sp.model AS sp_model,\n" + "  sp.number AS sp_number,\n" + "  sp.type AS sp_type,\n"
				+ "  sp.qsi AS sp_qsi,\n" + "  sp.qi AS sp_qi,\n" + "  sp.flow_range AS sp_flow_range,\n"
				+ "  sp.des AS sp_des,\n" + "  sp.pdfstr AS sp_pdfstr,\n" + " sp.pdfver AS sp_pdfver,\n" + "  sp.cuc AS sp_cuc,\n" 
				+ "  sp.pv AS sp_pv,\n" + "  sp.thv AS sp_thv,\n" + "  sp.zp AS sp_zp,\n" + "  sp.fl AS sp_fl,\n" 
				+ "  sp.cucmax AS sp_cucmax,\n" + "  sp.cucmin AS sp_cucmin,\n"
				+ "	 sp.bv AS sp_bv,\n"+ "  sp.la AS sp_la\n, "
				+ "  d_type.NAME AS type_name, d_insp.NAME AS insp_name, "
				+ "  d_flow.NAME AS flow_name, d_pdfver.NAME AS pdfver_name, d_retype.NAME AS retype_name" 
				+ "  FROM\n" + "  `siargo_qareport` sq\n"
				+ "  LEFT JOIN `siargo_product` AS sp ON sq.id = sp.report_id\n"
				+ "  LEFT JOIN `siargo_customer` AS sc ON sc.id = sq.cust_id\n"
				+ "   LEFT JOIN `jb_dictionary` AS d_type ON d_type.type_key = 'siargo_prod_type'\r\n"
				+ "  AND d_type.sn COLLATE utf8mb4_general_ci = CAST(sp.type AS CHAR)\r\n"
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
				+ "  LEFT JOIN jb_user AS appq_user ON appq_user.id = sp.appq_uid\n"
				+ "  LEFT JOIN jb_user AS allq_user ON allq_user.id = sp.allq_uid\n" + "WHERE\n" 
				+ "  sp.id = ? ";

		return dao.findFirst(sql, id);
	}

	/**
	 * 根据报告单ID查询该报告单下的全部有效产品信息（含字典翻译）
	 * <p>跨Model查询已收敛至 ProductService，此处仅做委托</p>
	 * @param reportId 报告单ID
	 * @return 产品列表
	 */
	public List<Product> findProductsByReportId(Long reportId) {
		return productService.findProductsByReportId(reportId);
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
		String sql = "SELECT CAST(sp.id AS CHAR) AS id, sp.model, sp.number, sp.qsi, sp.qi, sp.des, sp.insp, "
			// 驳回历史条数（>0 时显示「驳」角标，点击查看历史）
			+ "sp.reject_count, "
			+ "sq.formnum, sq.order_id, "
			+ "sc.name AS sc_name, "
			+ "d_type.name AS type_name, "
			+ "d_retype.name AS retype_name "
			+ "FROM siargo_product sp "
			+ "LEFT JOIN siargo_qareport sq ON sq.id = sp.report_id "
			+ "LEFT JOIN siargo_customer sc ON sc.id = sq.cust_id "
			+ "LEFT JOIN jb_dictionary AS d_type ON d_type.type_key = 'siargo_prod_type' "
			+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(sp.type AS CHAR) "
			+ "AND d_type.enable = '1' "
			+ "LEFT JOIN jb_dictionary AS d_retype ON d_retype.type_key = 'siargo_rep_type' "
			+ "AND d_retype.sn COLLATE utf8mb4_general_ci = CAST(sq.rep_type AS CHAR) "
			+ "AND d_retype.enable = '1' "
			+ "WHERE sp.vd = 1 AND sp.id IN (" + placeholders + ") "
			+ "ORDER BY sq.formnum ASC, sp.id ASC";
		return Db.find(sql, ids.toArray());
	}

	/**
	 * 更新报告单和产品数据
	 * <p>安全说明（编辑白名单）：不信任前端提交的 insp 及各环节 uid/time 签名字段；
	 * 以库内记录为基准，仅拷贝允许编辑的业务字段（型号/编号/qi/qsi/描述/电气参数等），
	 * insp 相关字段一律以库内值为准（检验进度变更只能走批准/驳回流程）</p>
	 * <p>所有业务校验在任何写库操作之前完成；产品更新失败抛RuntimeException触发事务回滚</p>
	 * @param qareport 报告单对象（前端提交）
	 * @param product 产品对象（前端提交）
	 * @return 操作结果
	 */
	public Ret update(Qareport qareport, Product product) {
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

		// 更新时需要判断数据存在
		Qareport dbQareport = dao.findById(qareport.getId());
		if (dbQareport == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		Product dbProduct = productService.findById(product.getId());
		if (dbProduct == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}

		// ========== 白名单拷贝：报告单允许编辑的业务字段 ==========
		dbQareport.set("order_id", qareport.getOrderId());
		dbQareport.set("cust_id", qareport.getCustId());
		dbQareport.set("rep_type", qareport.getRepType());

		// ========== 白名单拷贝：产品允许编辑的业务字段（insp/各环节uid/time以库内值为准，不拷贝） ==========
		dbProduct.set("type", product.getType());
		dbProduct.set("model", product.getModel());
		dbProduct.set("number", product.getNumber());
		dbProduct.set("qsi", product.getQsi());
		dbProduct.set("qi", product.getQi());
		dbProduct.set("flow_range", product.getFlowRange());
		dbProduct.set("des", product.getDes());
		dbProduct.set("pdfver", product.getPdfver());
		dbProduct.set("cuc", product.getCuc());
		dbProduct.set("cucmax", product.getCucmax());
		dbProduct.set("cucmin", product.getCucmin());
		dbProduct.set("pv", product.getPv());
		dbProduct.set("thv", product.getThv());
		dbProduct.set("zp", product.getZp());
		dbProduct.set("fl", product.getFl());
		dbProduct.set("bv", product.getBv());
		dbProduct.set("la", product.getLa());

		boolean qasuccess = dbQareport.update();
		if (!qasuccess) {
			return fail("报告单更新失败，请联系开发人员！");
		}

		boolean prodSuccess = dbProduct.update();
		if (!prodSuccess) {
			return fail("产品信息更新失败，请联系开发人员！");
		}
		return Ret.ok();
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
	 * 回收站分页查询（已软删除的报告单）
	 * <p>查询 vd=0 的产品记录，关联报告单、客户、字典表获取完整信息</p>
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @param keywords 搜索关键字（订单号模糊匹配）
	 * @return 分页数据
	 */
	public Page<Record> paginateInactiveDatas(int pageNumber, int pageSize, String keywords) {
		Sql sql = Sql.mysql()
				// CAST 雪花ID为字符串，避免前端 JS Number 精度丢失
				.select("CAST(sp.id AS CHAR) AS spid", "sq.order_id", "sc.name AS sc_name",
						"d_type.name AS type_name",
						"sp.delete_des",
						"DATE_FORMAT(sp.delete_time, '%Y-%m-%d %H:%i') AS delete_time")
				.page(pageNumber, pageSize)
				.from("siargo_product", "sp")
				.leftJoin("siargo_qareport", "sq", "sq.id = sp.report_id")
				.leftJoin("siargo_customer", "sc", "sc.id = sq.cust_id")
				.leftJoin("jb_dictionary", "d_type",
						"d_type.type_key = 'siargo_prod_type' "
						+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(sp.type AS CHAR) "
						+ "AND d_type.enable = '1'")
				.eq("sp.vd", QarepConst.VD_DELETED);

		sql.like("sq.order_id", keywords);
		sql.orderBy("sp.delete_time", true);

		Page<Record> page = paginateRecord(sql, true);
		// 用户可控文本转义，防止列表展示时XSS
		escapeRecordFields(page.getList(), "delete_des");
		return page;
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
	 * <p>统计当年所有有效产品的送检数量总和</p>
	 * @param proType 产品类型（0=全部，1=传感器，2=小流量，3=大流量）
	 * @return 送检数量总计
	 */
	public Long getTotalQSI(int proType) {
		String sql = "SELECT SUM( sp.qsi ) AS qsi_Total "
				+ "FROM siargo_product sp "
				+ "INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
				+ "WHERE YEAR ( sq.create_time ) = YEAR (CURDATE()) "
				+ "AND sp.vd = 1 ";
		if (proType > 0) {
			// 参数化占位符，避免SQL拼接
			return Db.queryLong(sql + " AND sp.type = ?", proType);
		}
		return Db.queryLong(sql);
	}
	
	
	/**
	 * 获取本年度检验数量总计
	 * <p>统计当年所有有效产品的检验数量总和</p>
	 * @param proType 产品类型（0=全部，1=传感器，2=小流量，3=大流量）
	 * @return 检验数量总计
	 */
	public Long getTotalQI(int proType) {
		String sql = "SELECT SUM( sp.qi ) AS qi_Total "
				+ "FROM siargo_product sp "
				+ "INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
				+ "WHERE YEAR ( sq.create_time ) = YEAR (CURDATE()) "
				+ "AND sp.vd = 1 ";
		if (proType > 0) {
			// 参数化占位符，避免SQL拼接
			return Db.queryLong(sql + " AND sp.type = ?", proType);
		}
		return Db.queryLong(sql);
	}
	
	/**
	 * 获取每月返修品送检数量统计数据（含今年与去年）
	 * <p>用于生成首页图表，展示全年各月的返修品数量趋势，支持年度切换</p>
	 * <p>SQL说明：按年份+月份分组统计rep_type=2（返修品）的送检数量，一次查出两年数据</p>
	 * @return {curYear今年年份, lastYear去年年份, cur今年[1..12月], last去年[1..12月]}
	 */
	public Map<String, Object> getRepData() {
	    String sql = "SELECT "
	    		+ "  YEAR(sq.create_time) AS yr, "
	    		+ "  MONTH(sq.create_time) AS MONTH, "
	    		+ "  SUM(sp.qsi) AS qsi_reTotal "
	    		+ "FROM "
	    		+ "  siargo_product sp "
	    		+ "  INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
	    		+ "WHERE "
	    		+ "  YEAR(sq.create_time) IN (YEAR(CURDATE()), YEAR(CURDATE()) - 1) "
	    		+ "  AND sp.vd = 1 AND sq.rep_type = 2  "
	    		+ "GROUP BY "
	    		+ "  YEAR(sq.create_time), MONTH(sq.create_time) ";

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
	
	/**
	 * 获取每月各类产品送检数量统计数据
	 * <p>用于生成首页图表，按产品类型（传感器、小流量、大流量）分类统计</p>
	 * <p>SQL说明：使用CASE WHEN按产品类型分别统计送检数量</p>
	 * @return 1-12月的产品分类送检数量数据列表
	 */
	public List<Map<String, Object>> getRepAllData() {
	    String sql = "SELECT "
	    		+ "  MONTH(sq.create_time) AS MONTH, "
	    		+ "  SUM(CASE WHEN sp.type = 1 THEN sp.qsi ELSE 0 END) AS sensor_qsi, "
	    		+ "  SUM(CASE WHEN sp.type = 2 THEN sp.qsi ELSE 0 END) AS small_flow_qsi, "
	    		+ "  SUM(CASE WHEN sp.type = 3 THEN sp.qsi ELSE 0 END) AS large_flow_qsi "
	    		+ " FROM "
	    		+ "  siargo_product sp "
	    		+ "  INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
	    		+ "WHERE "
	    		+ "  YEAR(sq.create_time) = YEAR(CURDATE()) "
	    		+ "  AND sp.vd = 1 "
	    		+ "GROUP BY "
	    		+ "  MONTH(sq.create_time) "
	    		+ "ORDER BY "
	    		+ "  MONTH(sq.create_time) ";
	    		
	    List<Record> records = Db.find(sql);
	    
	    Map<Integer, Integer> sensorData  = new LinkedHashMap<>();
	    Map<Integer, Integer> smallFlowData  = new LinkedHashMap<>();
	    Map<Integer, Integer> largeFlowData  = new LinkedHashMap<>();
	    
	    for (Record record : records) {
	    	int month = record.getInt("MONTH");
	        sensorData.put(month, record.getInt("sensor_qsi"));
	        smallFlowData.put(month, record.getInt("small_flow_qsi"));
	        largeFlowData.put(month, record.getInt("large_flow_qsi"));
	    }
	    
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (int month = 1; month <= 12; month++) {
	        Map<String, Object> item = new LinkedHashMap<>();
	        item.put("y", month + "月");  
	        item.put("a", sensorData.getOrDefault(month, 0));      // 传感器数据
	        item.put("b", smallFlowData.getOrDefault(month, 0));   // 小流量数据
	        item.put("c", largeFlowData.getOrDefault(month, 0));   // 大流量数据
	        result.add(item);
	    }
	    return result;
	}
	
	/**
	 * 获取今年与去年各季度送检数量对比数据（按产品类型细分）
	 * <p>用于首页仪表盘季度同比图表</p>
	 * <p>SQL说明：按年份+季度分组，CASE WHEN按产品类型分列统计送检数量，一次查出两年数据</p>
	 * @return {curYear今年年份, lastYear去年年份, curA/curB/curC今年各类型[Q1..Q4], lastA/lastB/lastC去年各类型[Q1..Q4]}（A=传感器 B=小流量 C=大流量）
	 */
	public Map<String, Object> getQuarterCompareData() {
	    String sql = "SELECT "
	    		+ "  YEAR(sq.create_time) AS yr, "
	    		+ "  QUARTER(sq.create_time) AS qt, "
	    		+ "  SUM(CASE WHEN sp.type = 1 THEN sp.qsi ELSE 0 END) AS sensor_qsi, "
	    		+ "  SUM(CASE WHEN sp.type = 2 THEN sp.qsi ELSE 0 END) AS small_flow_qsi, "
	    		+ "  SUM(CASE WHEN sp.type = 3 THEN sp.qsi ELSE 0 END) AS large_flow_qsi "
	    		+ "FROM "
	    		+ "  siargo_product sp "
	    		+ "  INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
	    		+ "WHERE "
	    		+ "  sp.vd = 1 "
	    		+ "  AND YEAR(sq.create_time) IN (YEAR(CURDATE()), YEAR(CURDATE()) - 1) "
	    		+ "GROUP BY "
	    		+ "  YEAR(sq.create_time), QUARTER(sq.create_time) ";
	    List<Record> records = Db.find(sql);
	    
	    int curYear = java.time.LocalDate.now().getYear();
	    long[] curA = new long[4], curB = new long[4], curC = new long[4];
	    long[] lastA = new long[4], lastB = new long[4], lastC = new long[4];
	    for (Record record : records) {
	    	Integer qt = record.getInt("qt");
	    	if (qt == null || qt < 1 || qt > 4) {
	    		continue;
	    	}
	    	int i = qt - 1;
	    	Long a = record.getLong("sensor_qsi");
	    	Long b = record.getLong("small_flow_qsi");
	    	Long c = record.getLong("large_flow_qsi");
	    	if (record.getInt("yr") == curYear) {
	    		curA[i] = a == null ? 0L : a;
	    		curB[i] = b == null ? 0L : b;
	    		curC[i] = c == null ? 0L : c;
	    	} else {
	    		lastA[i] = a == null ? 0L : a;
	    		lastB[i] = b == null ? 0L : b;
	    		lastC[i] = c == null ? 0L : c;
	    	}
	    }
	    
	    Map<String, Object> result = new LinkedHashMap<>();
	    result.put("curYear", curYear);
	    result.put("lastYear", curYear - 1);
	    result.put("curA", curA);
	    result.put("curB", curB);
	    result.put("curC", curC);
	    result.put("lastA", lastA);
	    result.put("lastB", lastB);
	    result.put("lastC", lastC);
	    return result;
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
						"CAST(sp.id AS CHAR) as spid", "sp.model as sp_model", "sp.number as sp_number", "sp.type as sp_type",
						"sp.flow_range as sp_flow_range",
						"sp.pdfstr AS sp_pdfstr", "sp.pdfver AS sp_pdfver","sp.cuc as sp_cuc", "sp.pv as sp_pv",
						"sp.thv as sp_thv", "sp.zp as sp_zp", "sp.fl as sp_fl", "sp.cucmax as sp_cucmax",
						"sp.cucmin as sp_cucmin", "sp.bv as sp_bv", "sp.la as sp_la",
						// 字典翻译字段
						"d_type.name AS type_name","d_insp.name AS insp_name","d_flow.name AS flow_name",
						"d_pdfver.name AS pdfver_name","d_retype.name AS retype_name",
						// 删除信息
						"sp.delete_des", "DATE_FORMAT(sp.delete_time, '%Y-%m-%d %H:%i') as delete_time"
						)
				.page(pageNumber, pageSize).from("siargo_product", "sp")
				// ========== 关联报告单表 ==========
				.leftJoin("siargo_qareport", "sq", "sq.id = sp.report_id")
				// ========== 关联客户表 ==========
				.leftJoin("siargo_customer", "sc", "sc.id = sq.cust_id")
				// ========== 关联字典表获取产品类型名称 ==========
				.leftJoin("jb_dictionary", "d_type", "d_type.type_key = 'siargo_prod_type' "
						+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(sp.type AS CHAR) "
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
				+ "sp.accq_time, "
				+ "sp.funq_time, "
				+ "sp.appq_time, "
				+ "sp.allq_time, "
				+ "u1.name AS accq_name, "
				+ "u2.name AS funq_name, "
				+ "u3.name AS appq_name, "
				+ "u4.name AS allq_name "
				+ "FROM siargo_product sp "
				+ "LEFT JOIN siargo_qareport sq ON sp.report_id = sq.id "
				+ "LEFT JOIN jb_user u1 ON sp.accq_uid = u1.id "
				+ "LEFT JOIN jb_user u2 ON sp.funq_uid = u2.id "
				+ "LEFT JOIN jb_user u3 ON sp.appq_uid = u3.id "
				+ "LEFT JOIN jb_user u4 ON sp.allq_uid = u4.id "
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
		sql.append("SELECT sq.order_id, sp.model, sp.number, sp.insp, ")
			.append("sp.accq_time, sp.funq_time, sp.appq_time, sp.allq_time, ")
			.append("u1.name AS accq_name, u2.name AS funq_name, ")
			.append("u3.name AS appq_name, u4.name AS allq_name ")
			.append("FROM siargo_product sp ")
			.append("LEFT JOIN siargo_qareport sq ON sp.report_id = sq.id ")
			.append("LEFT JOIN jb_user u1 ON sp.accq_uid = u1.id ")
			.append("LEFT JOIN jb_user u2 ON sp.funq_uid = u2.id ")
			.append("LEFT JOIN jb_user u3 ON sp.appq_uid = u3.id ")
			.append("LEFT JOIN jb_user u4 ON sp.allq_uid = u4.id ")
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

	/**
	 * 获取本年度产品类型分布统计数据
	 * <p>用于生成首页饼图，展示各类产品报告单数量占比</p>
	 * <p>SQL说明：按产品类型分组统计报告单数量</p>
	 * @return 产品类型分布数据列表（传感器、小流量、大流量）
	 */
	public List<Map<String, Object>> getDonutData() {
	    String sql = "SELECT sp.type AS type, COUNT(DISTINCT sp.report_id) AS count "
	    		+ "FROM siargo_product sp "
	    		+ "LEFT JOIN siargo_qareport sq ON sq.id = sp.report_id "
	    		+ "WHERE YEAR ( sq.create_time ) = YEAR (CURDATE()) "
	    		+ "AND sp.vd = 1 GROUP BY sp.type ";
	    
	    List<Record> records = Db.find(sql);
	    
	    Map<Integer, Integer> monthData = new LinkedHashMap<>();
	    for (Record record : records) {
	        monthData.put(record.getInt("type"), record.getInt("count"));
	    }
	    
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (Map.Entry<Integer, Integer> entry : monthData.entrySet()) {
	        Map<String, Object> item = new LinkedHashMap<>();
	        if (entry.getKey() == QarepConst.PROD_TYPE_SENSOR) {
	        	item.put("label", "传感器" ); 
			}
	        if (entry.getKey() == QarepConst.PROD_TYPE_SMALL_FLOW) {
	        	item.put("label", "小流量" ); 
			}
	        if (entry.getKey() == QarepConst.PROD_TYPE_LARGE_FLOW) {
	        	item.put("label", "大流量" ); 
			} 
	        item.put("value", entry.getValue()); 
	        result.add(item);
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
