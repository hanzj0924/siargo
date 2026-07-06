package cn.jbolt.admin.siargo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

import cn.jbolt.admin.siargo.apicalllog.ApiCallLogService;
import cn.jbolt.admin.siargo.qarep.QareportService;
import cn.jbolt.core.util.JBoltIpUtil;
import cn.jbolt.core.api.JBoltApiBaseController;
import cn.jbolt.core.api.OpenAPI;
import cn.jbolt.core.api.UnCheckJBoltApi;

/**
 * 对外API - 订单状态查询接口
 * <p>提供单个/批量订单检验进度查询，使用自定义SHA256 Token签名认证</p>
 *
 * @author Hanzj
 * @date 2026-04-13
 */
@Path("/api/siargo/order")
public class OrderStatusApiController extends JBoltApiBaseController {

	@Inject
	private QareportService qareportService;

	@Inject
	private ApiCallLogService apiCallLogService;

	/** 单次批量查询上限 */
	private static final int BATCH_SIZE_LIMIT = 100;

	// ==================== 统一输出格式 ====================

	/**
	 * 统一成功响应：{status:"ok", code:200, msg:"查询成功", data:{...}}
	 */
	private void renderApiSuccess(Kv data) {
		renderJson(Kv.by("status", "ok").set("code", 200).set("msg", "查询成功").set("data", data));
	}

	/**
	 * 统一失败响应：{status:"fail", code:xxx, msg:"..."}
	 */
	private void renderApiFail(int code, String msg) {
		renderJson(Kv.by("status", "fail").set("code", code).set("msg", msg));
	}

	// ==================== 公共请求上下文（消除重复代码） ====================

	/**
	 * API请求上下文，封装所有公共请求元数据
	 */
	private static class ApiContext {
		final String apiPath;
		final String apiMethod;
		final String orderId;
		final String orderIds;
		final String requestIp;
		final String userAgent;
		final Long jboltAppId;
		final String traceId;

		ApiContext(String apiPath, String apiMethod, String orderId, String orderIds,
				String requestIp, String userAgent, Long jboltAppId, String traceId) {
			this.apiPath = apiPath;
			this.apiMethod = apiMethod;
			this.orderId = orderId;
			this.orderIds = orderIds;
			this.requestIp = requestIp;
			this.userAgent = userAgent;
			this.jboltAppId = jboltAppId;
			this.traceId = traceId;
		}
	}

	/**
	 * 提取请求公共上下文（消除 status/batchStatus 中 ~7 行重复代码）
	 */
	private ApiContext extractContext(String apiPath, String orderId, String orderIds) {
		String requestIp = JBoltIpUtil.getIp(getRequest());
		String userAgent = getRequest().getHeader("User-Agent");
		Long jboltAppId = getApplication() != null ? getApplication().getId() : null;
		String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		return new ApiContext(apiPath, "GET", orderId, orderIds, requestIp, userAgent, jboltAppId, traceId);
	}

	// ==================== 日志记录（简化签名） ====================

	/**
	 * 记录API调用日志（traceId 通过独立字段写入数据库，支持精确索引检索）
	 */
	private void logApi(ApiContext ctx, String responseStatus, int responseCode, String responseMsg, long startTime) {
		try {
			long costTime = System.currentTimeMillis() - startTime;
			apiCallLogService.logApiCall(ctx.apiPath, ctx.apiMethod, ctx.orderId, ctx.orderIds,
				ctx.requestIp, ctx.userAgent, responseStatus, responseCode, responseMsg,
				costTime, ctx.jboltAppId, ctx.traceId);
		} catch (Exception e) {
			// 日志记录失败不影响正常API响应
		}
	}

	// ==================== Token 验证 ====================

	/**
	 * 统一的应用认证 + Token验证（消除两个接口的重复token验证逻辑）
	 * <p>验证 getApplication() 非空 + Token有效性</p>
	 * @param startTime 请求开始时间，用于日志耗时计算
	 * @return true=验证通过，false=已直接输出错误响应
	 */
	private boolean validateAppAndToken(ApiContext ctx, String[] orderIdsForBatch, String token, long startTime) {
		// 修复 NPE 风险：getApplication() 可能为null
		if (getApplication() == null) {
			logApi(ctx, "fail", ApiErrorCode.APP_AUTH_FAILED,
				ApiErrorCode.defaultMsg(ApiErrorCode.APP_AUTH_FAILED), startTime);
			renderApiFail(ApiErrorCode.APP_AUTH_FAILED,
				ApiErrorCode.defaultMsg(ApiErrorCode.APP_AUTH_FAILED));
			return false;
		}
		String secretKey = getApplication().getAppSecret();
		boolean valid;
		if (orderIdsForBatch != null && orderIdsForBatch.length > 0) {
			valid = SiargoApiTokenUtil.validateBatchToken(secretKey, orderIdsForBatch, token);
		} else {
			valid = SiargoApiTokenUtil.validateToken(secretKey, ctx.orderId, token);
		}
		if (!valid) {
			logApi(ctx, "fail", ApiErrorCode.TOKEN_INVALID,
				ApiErrorCode.defaultMsg(ApiErrorCode.TOKEN_INVALID), startTime);
			renderApiFail(ApiErrorCode.TOKEN_INVALID,
				ApiErrorCode.defaultMsg(ApiErrorCode.TOKEN_INVALID));
			return false;
		}
		return true;
	}

	// ==================== DTO字段语义化映射 ====================

	/**
	 * 将数据库 Record 转换为语义化的API输出结构
	 * <pre>
	 * 输出格式：
	 * {
	 *   "model": "SFM-3013",
	 *   "serialNo": "SN001",
	 *   "status": { "insp": 2, "label": "精度检验已完成" },
	 *   "timeline": {
	 *     "accuracy":   { "time": "2026-01-01 10:00", "operator": "张三" },
	 *     "appearance": { "time": "", "operator": "" },
	 *     "packaging":  { "time": "", "operator": "" },
	 *     "approval":   { "time": "", "operator": "" }
	 *   }
	 * }
	 * </pre>
	 */
	private Kv mapToOrderStatusItem(Record record) {
		int insp = record.getInt("insp") != null ? record.getInt("insp") : 0;
		return Kv.by("model", record.getStr("model"))
			.set("serialNo", record.getStr("number"))
			.set("status", Kv.by("insp", insp).set("label", inspLabel(insp)))
			.set("timeline", Kv.by("accuracy", timelineNode(record, "accq_time", "accq_name"))
				.set("appearance", timelineNode(record, "funq_time", "funq_name"))
				.set("packaging", timelineNode(record, "appq_time", "appq_name"))
				.set("approval", timelineNode(record, "allq_time", "allq_name")));
	}

	/**
	 * 检验进度码转中文标签
	 */
	private String inspLabel(int insp) {
		switch (insp) {
			case 1: return "待检验";
			case 2: return "精度检验已完成";
			case 3: return "外观检验已完成";
			case 4: return "包装检验已完成";
			case 5: return "已完成检验";
			default: return "未知";
		}
	}

	/**
	 * 构建检验时间线节点
	 */
	private Kv timelineNode(Record record, String timeField, String nameField) {
		String time = record.getStr(timeField);
		String name = record.getStr(nameField);
		return Kv.by("time", time != null ? time : "").set("operator", name != null ? name : "");
	}

	// ==================== API接口 ====================

	/**
	 * 单个订单状态查询
	 * <p>GET /api/siargo/order/status?orderId=xxx&amp;token=xxx</p>
	 */
	@UnCheckJBoltApi
	@OpenAPI
	public void status() {
		long startTime = System.currentTimeMillis();
		String orderId = get("orderId");
		String token = get("token");
		String apiPath = "/api/siargo/order/status";
		ApiContext ctx = extractContext(apiPath, orderId, null);

		// 参数校验
		if (StrKit.isBlank(orderId)) {
			logApi(ctx, "fail", ApiErrorCode.ORDER_ID_EMPTY,
				ApiErrorCode.defaultMsg(ApiErrorCode.ORDER_ID_EMPTY), startTime);
			renderApiFail(ApiErrorCode.ORDER_ID_EMPTY,
				ApiErrorCode.defaultMsg(ApiErrorCode.ORDER_ID_EMPTY));
			return;
		}
		if (StrKit.isBlank(token)) {
			logApi(ctx, "fail", ApiErrorCode.TOKEN_EMPTY,
				ApiErrorCode.defaultMsg(ApiErrorCode.TOKEN_EMPTY), startTime);
			renderApiFail(ApiErrorCode.TOKEN_EMPTY,
				ApiErrorCode.defaultMsg(ApiErrorCode.TOKEN_EMPTY));
			return;
		}

		// Token验证
		if (!validateAppAndToken(ctx, null, token, startTime)) {
			return;
		}

		// 查询数据
		List<Record> dataList = qareportService.queryOrderStatusByOrderId(orderId);

		// 订单不存在也返回成功，通过 found 字段区分（与批量查询行为一致）
		logApi(ctx, "ok", 200, "查询成功", startTime);
		getResponse().setHeader("X-Trace-Id", ctx.traceId);
		if (dataList == null || dataList.isEmpty()) {
			renderApiSuccess(Kv.by("orderId", orderId).set("found", false)
				.set("msg", "订单未创建").set("traceId", ctx.traceId));
			return;
		}

		// DTO转换
		List<Kv> items = new ArrayList<>();
		for (Record r : dataList) {
			items.add(mapToOrderStatusItem(r));
		}
		renderApiSuccess(Kv.by("orderId", orderId).set("found", true)
			.set("items", items).set("traceId", ctx.traceId));
	}

	/**
	 * 批量订单状态查询
	 * <p>GET /api/siargo/order/batchStatus?orderIds=xxx,yyy&amp;token=xxx</p>
	 * <p>单次最多查询100个订单，使用IN查询一次性获取所有数据</p>
	 */
	@UnCheckJBoltApi
	@OpenAPI
	public void batchStatus() {
		long startTime = System.currentTimeMillis();
		String orderIdsStr = get("orderIds");
		String token = get("token");
		String apiPath = "/api/siargo/order/batchStatus";
		ApiContext ctx = extractContext(apiPath, null, orderIdsStr);

		// 参数校验
		if (StrKit.isBlank(orderIdsStr)) {
			logApi(ctx, "fail", ApiErrorCode.ORDER_ID_EMPTY,
				ApiErrorCode.defaultMsg(ApiErrorCode.ORDER_ID_EMPTY), startTime);
			renderApiFail(ApiErrorCode.ORDER_ID_EMPTY,
				ApiErrorCode.defaultMsg(ApiErrorCode.ORDER_ID_EMPTY));
			return;
		}
		if (StrKit.isBlank(token)) {
			logApi(ctx, "fail", ApiErrorCode.TOKEN_EMPTY,
				ApiErrorCode.defaultMsg(ApiErrorCode.TOKEN_EMPTY), startTime);
			renderApiFail(ApiErrorCode.TOKEN_EMPTY,
				ApiErrorCode.defaultMsg(ApiErrorCode.TOKEN_EMPTY));
			return;
		}

		// 拆分并校验数量上限
		String[] orderIds = orderIdsStr.split(",");
		if (orderIds.length > BATCH_SIZE_LIMIT) {
			logApi(ctx, "fail", ApiErrorCode.BATCH_SIZE_EXCEEDED,
				ApiErrorCode.defaultMsg(ApiErrorCode.BATCH_SIZE_EXCEEDED), startTime);
			renderApiFail(ApiErrorCode.BATCH_SIZE_EXCEEDED,
				ApiErrorCode.defaultMsg(ApiErrorCode.BATCH_SIZE_EXCEEDED));
			return;
		}

		// Token验证
		if (!validateAppAndToken(ctx, orderIds, token, startTime)) {
			return;
		}

		// 收集有效orderId
		List<String> validOrderIds = new ArrayList<>();
		for (String id : orderIds) {
			String trimmed = id.trim();
			if (StrKit.notBlank(trimmed)) {
				validOrderIds.add(trimmed);
			}
		}

		// 一次性批量查询（消除 N+1 问题）
		Map<String, List<Record>> batchResult = qareportService.batchQueryOrderStatus(validOrderIds);

		// 组装结果
		List<Kv> results = new ArrayList<>();
		for (String oid : validOrderIds) {
			List<Record> records = batchResult.get(oid);
			Kv resultItem = Kv.by("orderId", oid);
			if (records == null || records.isEmpty()) {
				resultItem.set("found", false).set("msg", "订单未创建");
			} else {
				List<Kv> items = new ArrayList<>();
				for (Record r : records) {
					items.add(mapToOrderStatusItem(r));
				}
				resultItem.set("found", true).set("items", items);
			}
			results.add(resultItem);
		}

		logApi(ctx, "ok", 200, "查询成功", startTime);
		getResponse().setHeader("X-Trace-Id", ctx.traceId);
		renderApiSuccess(Kv.by("total", results.size()).set("results", results).set("traceId", ctx.traceId));
	}
}
