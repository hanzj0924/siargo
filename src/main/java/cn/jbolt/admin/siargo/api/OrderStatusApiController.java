package cn.jbolt.admin.siargo.api;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

import cn.jbolt.admin.siargo.qarep.QareportService;
import cn.jbolt.core.api.JBoltApiBaseController;
import cn.jbolt.core.api.OpenAPI;
import cn.jbolt.core.api.UnCheckJBoltApi;

/**
 * 订单检验状态查询 对外API控制器
 * <p>无需登录认证，通过URL参数 orderId + token 验证合法性</p>
 * <p>接口地址：</p>
 * <ul>
 *   <li>单个查询：GET /api/siargo/order/status?orderId=xxx&amp;token=xxx</li>
 *   <li>批量查询：GET /api/siargo/order/batchStatus?orderIds=xxx,yyy&amp;token=xxx</li>
 * </ul>
 * 
 * @author siargo
 * @date 2026-04-13
 */
@Path("/api/siargo/order")
public class OrderStatusApiController extends JBoltApiBaseController {

	@Inject
	private QareportService qareportService;

	/**
	 * 查询订单检验状态
	 * <p>GET /api/siargo/order/status?jboltappid=xxx&orderId=xxx&token=xxx</p>
	 */
	@UnCheckJBoltApi
	@OpenAPI
	public void status() {
		String orderId = get("orderId");
		String token = get("token");

		// 参数校验
		if (StrKit.isBlank(orderId)) {
			renderJson(Kv.by("status", "fail").set("code", 1001).set("msg", "订单号不能为空").set("orderId", orderId).set("insp", 0));
			return;
		}
		if (StrKit.isBlank(token)) {
			renderJson(Kv.by("status", "fail").set("code", 1002).set("msg", "token不能为空").set("orderId", orderId).set("insp", 0));
			return;
		}

		// Token验证（使用框架Application的appSecret作为签名密钥）
		String secretKey = getApplication().getAppSecret();
		if (!SiargoApiTokenUtil.validateToken(secretKey, orderId, token)) {
			renderJson(Kv.by("status", "fail").set("code", 1003).set("msg", "token验证失败").set("orderId", orderId).set("insp", 0));
			return;
		}

		// 查询数据
		List<Record> dataList = qareportService.queryOrderStatusByOrderId(orderId);
		if (dataList == null || dataList.isEmpty()) {
			renderJson(Kv.by("status", "fail").set("code", 1004).set("msg", "订单未创建").set("orderId", orderId).set("insp", 0));
			return;
		}

		// 返回成功数据
		renderJson(Kv.by("status", "ok").set("code", 200).set("msg", "查询成功").set("orderId", orderId).set("data", dataList));
	}

	/**
	 * 批量查询订单检验状态
	 * <p>GET /api/siargo/order/batchStatus?jboltappid=xxx&orderIds=xxx,yyy,zzz&amp;token=xxx</p>
	 */
	@UnCheckJBoltApi
	@OpenAPI
	public void batchStatus() {
		String orderIdsStr = get("orderIds");
		String token = get("token");

		// 参数校验
		if (StrKit.isBlank(orderIdsStr)) {
			renderJson(Kv.by("status", "fail").set("code", 1001).set("msg", "订单号不能为空"));
			return;
		}
		if (StrKit.isBlank(token)) {
			renderJson(Kv.by("status", "fail").set("code", 1002).set("msg", "token不能为空"));
			return;
		}

		String[] orderIds = orderIdsStr.split(",");
		// 数量限制
		if (orderIds.length > 100) {
			renderJson(Kv.by("status", "fail").set("code", 1005).set("msg", "单次批量查询不能超过100个订单"));
			return;
		}

		// Token验证（使用框架Application的appSecret作为签名密钥）
		String secretKey = getApplication().getAppSecret();
		if (!SiargoApiTokenUtil.validateBatchToken(secretKey, orderIds, token)) {
			renderJson(Kv.by("status", "fail").set("code", 1003).set("msg", "token验证失败"));
			return;
		}

		// 逐个查询并组装结果
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

		renderJson(Kv.by("status", "ok").set("code", 200).set("msg", "查询成功").set("total", results.size()).set("results", results));
	}
}
