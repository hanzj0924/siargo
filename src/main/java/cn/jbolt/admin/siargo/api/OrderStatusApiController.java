package cn.jbolt.admin.siargo.api;

import java.util.ArrayList;
import java.util.List;

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

@Path("/api/siargo/order")
public class OrderStatusApiController extends JBoltApiBaseController {

	@Inject
	private QareportService qareportService;

	@Inject
	private ApiCallLogService apiCallLogService;

	@UnCheckJBoltApi
	@OpenAPI
	public void status() {
		long startTime = System.currentTimeMillis();
		String orderId = get("orderId");
		String token = get("token");
		String apiPath = "/api/siargo/order/status";
		String apiMethod = "GET";
		String requestIp = JBoltIpUtil.getIp(getRequest());
		String userAgent = getRequest().getHeader("User-Agent");
		Long jboltAppId = getApplication() != null ? getApplication().getId() : null;

		if (StrKit.isBlank(orderId)) {
			logApi(apiPath, apiMethod, orderId, null, requestIp, userAgent, "fail", 1001, "订单号不能为空", startTime, jboltAppId);
			renderJson(Kv.by("status", "fail").set("code", 1001).set("msg", "订单号不能为空").set("orderId", orderId).set("insp", 0));
			return;
		}
		if (StrKit.isBlank(token)) {
			logApi(apiPath, apiMethod, orderId, null, requestIp, userAgent, "fail", 1002, "token不能为空", startTime, jboltAppId);
			renderJson(Kv.by("status", "fail").set("code", 1002).set("msg", "token不能为空").set("orderId", orderId).set("insp", 0));
			return;
		}

		String secretKey = getApplication().getAppSecret();
		if (!SiargoApiTokenUtil.validateToken(secretKey, orderId, token)) {
			logApi(apiPath, apiMethod, orderId, null, requestIp, userAgent, "fail", 1003, "token验证失败", startTime, jboltAppId);
			renderJson(Kv.by("status", "fail").set("code", 1003).set("msg", "token验证失败").set("orderId", orderId).set("insp", 0));
			return;
		}

		List<Record> dataList = qareportService.queryOrderStatusByOrderId(orderId);
		if (dataList == null || dataList.isEmpty()) {
			logApi(apiPath, apiMethod, orderId, null, requestIp, userAgent, "fail", 1004, "订单未创建", startTime, jboltAppId);
			renderJson(Kv.by("status", "fail").set("code", 1004).set("msg", "订单未创建").set("orderId", orderId).set("insp", 0));
			return;
		}

		logApi(apiPath, apiMethod, orderId, null, requestIp, userAgent, "ok", 200, "查询成功", startTime, jboltAppId);
		renderJson(Kv.by("status", "ok").set("code", 200).set("msg", "查询成功").set("orderId", orderId).set("data", dataList));
	}

	@UnCheckJBoltApi
	@OpenAPI
	public void batchStatus() {
		long startTime = System.currentTimeMillis();
		String orderIdsStr = get("orderIds");
		String token = get("token");
		String apiPath = "/api/siargo/order/batchStatus";
		String apiMethod = "GET";
		String requestIp = JBoltIpUtil.getIp(getRequest());
		String userAgent = getRequest().getHeader("User-Agent");
		Long jboltAppId = getApplication() != null ? getApplication().getId() : null;

		if (StrKit.isBlank(orderIdsStr)) {
			logApi(apiPath, apiMethod, null, orderIdsStr, requestIp, userAgent, "fail", 1001, "订单号不能为空", startTime, jboltAppId);
			renderJson(Kv.by("status", "fail").set("code", 1001).set("msg", "订单号不能为空"));
			return;
		}
		if (StrKit.isBlank(token)) {
			logApi(apiPath, apiMethod, null, orderIdsStr, requestIp, userAgent, "fail", 1002, "token不能为空", startTime, jboltAppId);
			renderJson(Kv.by("status", "fail").set("code", 1002).set("msg", "token不能为空"));
			return;
		}

		String[] orderIds = orderIdsStr.split(",");
		if (orderIds.length > 100) {
			logApi(apiPath, apiMethod, null, orderIdsStr, requestIp, userAgent, "fail", 1005, "单次批量查询不能超过100个订单", startTime, jboltAppId);
			renderJson(Kv.by("status", "fail").set("code", 1005).set("msg", "单次批量查询不能超过100个订单"));
			return;
		}

		String secretKey = getApplication().getAppSecret();
		if (!SiargoApiTokenUtil.validateBatchToken(secretKey, orderIds, token)) {
			logApi(apiPath, apiMethod, null, orderIdsStr, requestIp, userAgent, "fail", 1003, "token验证失败", startTime, jboltAppId);
			renderJson(Kv.by("status", "fail").set("code", 1003).set("msg", "token验证失败"));
			return;
		}

		List<Kv> results = new ArrayList<>();
		for (String orderId : orderIds) {
			String id = orderId.trim();
			if (StrKit.isBlank(id)) {
				continue;
			}
			List<Record> dataList = qareportService.queryOrderStatusByOrderId(id);
			if (dataList == null || dataList.isEmpty()) {
				results.add(Kv.by("orderId", id).set("insp", 0).set("msg", "订单未创建"));
			} else {
				results.add(Kv.by("orderId", id).set("data", dataList));
			}
		}

		logApi(apiPath, apiMethod, null, orderIdsStr, requestIp, userAgent, "ok", 200, "查询成功", startTime, jboltAppId);
		renderJson(Kv.by("status", "ok").set("code", 200).set("msg", "查询成功").set("total", results.size()).set("results", results));
	}

	private void logApi(String apiPath, String apiMethod, String orderId, String orderIds,
			String requestIp, String userAgent, String responseStatus, Integer responseCode,
			String responseMsg, long startTime, Long jboltAppId) {
		try {
			long costTime = System.currentTimeMillis() - startTime;
			apiCallLogService.logApiCall(apiPath, apiMethod, orderId, orderIds,
				requestIp, userAgent, responseStatus, responseCode, responseMsg, costTime, jboltAppId);
		} catch (Exception e) {
			// 日志记录失败不影响正常API响应
		}
	}
}
