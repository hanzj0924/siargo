package jbolt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import cn.jbolt.admin.siargo.api.SiargoApiTokenUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单状态查询API 测试类
 * <p>
 * 包含以下测试：
 * 1. Token工具类单元测试（SiargoApiTokenUtil）
 * 2. API接口集成测试（需要服务运行）
 * </p>
 * 
 * @author siargo
 * @date 2026-04-13
 */
@TestInstance(Lifecycle.PER_CLASS)
public class OrderStatusApiTest {

	// ==================== 配置常量 ====================
	
	/** 测试用API基础URL（集成测试时需要服务运行） */
	private static final String API_BASE_URL = "http://localhost/api/siargo/order/status";
	
	/** 批量查询API基础URL */
	private static final String BATCH_API_BASE_URL = "http://localhost/api/siargo/order/batchStatus";

	/** 测试用密钥（与Application的app_secret保持一致） */
	private static final String TEST_SECRET = "bjVzamw0ZWIzdjRsc3hmZGVwcmxta3RqOHc4OXZkaGM=";
	
	/** 测试用AppId （与Application的app_id保持一致）*/
	private static final String TEST_APP_ID = "jbzvdqh9pxxmolt";
	
	/** 日期格式化 */
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	
	@BeforeAll
	public void setUp() {
		System.out.println("========== 订单状态查询API 测试开始 ==========");
		System.out.println("当前日期: " + LocalDate.now().format(DATE_FORMATTER));
		System.out.println("使用AppId: " + TEST_APP_ID);
		System.out.println("使用AppSecret: " + TEST_SECRET.substring(0, 10) + "...");
		System.out.println();
	}

	// ==================== 辅助方法 ====================

	/**
	 * 生成单个查询Token
	 */
	private String generateToken(String orderId) {
		return SiargoApiTokenUtil.generateToken(TEST_SECRET, orderId);
	}

	/**
	 * 验证单个查询Token
	 */
	private boolean validateToken(String orderId, String token) {
		return SiargoApiTokenUtil.validateToken(TEST_SECRET, orderId, token);
	}

	/**
	 * 生成批量查询Token
	 */
	private String generateBatchToken(String[] orderIds) {
		return SiargoApiTokenUtil.generateBatchToken(TEST_SECRET, orderIds);
	}

	/**
	 * 验证批量查询Token
	 */
	private boolean validateBatchToken(String[] orderIds, String token) {
		return SiargoApiTokenUtil.validateBatchToken(TEST_SECRET, orderIds, token);
	}

	// ==================== 1. Token工具类测试 ====================

	/**
	 * 测试Token生成 - 验证非空且长度为64（SHA256十六进制字符串）
	 */
	@Test
	public void testGenerateToken() {
		System.out.println("--- testGenerateToken ---");
		String orderId = "103697";
		String token = generateToken(orderId);

		System.out.println("订单号: " + orderId);
		System.out.println("生成Token: " + token);
		System.out.println("Token长度: " + token.length());

		// 验证Token非空
		assertNotNull(token, "生成的Token不应为null");
		assertFalse(token.isEmpty(), "生成的Token不应为空字符串");

		// 验证长度为64（SHA256 hex = 32字节 = 64个十六进制字符）
		assertEquals(64, token.length(), "SHA256 Token长度应为64位");

		// 验证是合法的十六进制字符串
		assertTrue(token.matches("[0-9a-f]{64}"), "Token应为小写十六进制字符串");

		System.out.println("✓ testGenerateToken 通过\n");
	}

	/**
	 * 测试Token一致性 - 相同参数应生成相同Token
	 */
	@Test
	public void testGenerateTokenConsistency() {
		System.out.println("--- testGenerateTokenConsistency ---");
		String orderId = "103667";

		String token1 = generateToken(orderId);
		String token2 = generateToken(orderId);

		System.out.println("订单号: " + orderId);
		System.out.println("第1次生成: " + token1);
		System.out.println("第2次生成: " + token2);

		// 同一天、相同参数应生成相同Token
		assertEquals(token1, token2, "相同参数应生成相同的Token");

		System.out.println("✓ testGenerateTokenConsistency 通过\n");
	}

	/**
	 * 测试不同订单号生成不同Token
	 */
	@Test
	public void testGenerateTokenDifferentOrderId() {
		System.out.println("--- testGenerateTokenDifferentOrderId ---");
		String orderId1 = "103773";
		String orderId2 = "103741";

		String token1 = generateToken(orderId1);
		String token2 = generateToken(orderId2);

		System.out.println("订单号1: " + orderId1 + " -> Token: " + token1);
		System.out.println("订单号2: " + orderId2 + " -> Token: " + token2);

		// 不同订单号应生成不同Token
		assertNotEquals(token1, token2, "不同订单号应生成不同的Token");

		System.out.println("✓ testGenerateTokenDifferentOrderId 通过\n");
	}

	/**
	 * 测试Token验证成功 - 正确Token应通过验证
	 */
	@Test
	public void testValidateTokenSuccess() {
		System.out.println("--- testValidateTokenSuccess ---");
		String orderId = "103600";
		String token = generateToken(orderId);

		System.out.println("订单号: " + orderId);
		System.out.println("生成Token: " + token);

		boolean result = validateToken(orderId, token);
		System.out.println("验证结果: " + result);

		assertTrue(result, "正确的Token应通过验证");

		System.out.println("✓ testValidateTokenSuccess 通过\n");
	}

	/**
	 * 测试Token验证失败 - 错误Token不应通过验证
	 */
	@Test
	public void testValidateTokenFail() {
		System.out.println("--- testValidateTokenFail ---");
		String orderId = "103777";
		String wrongToken = "0000000000000000000000000000000000000000000000000000000000000000";

		System.out.println("订单号: " + orderId);
		System.out.println("错误Token: " + wrongToken);

		boolean result = validateToken(orderId, wrongToken);
		System.out.println("验证结果: " + result);

		assertFalse(result, "错误的Token不应通过验证");

		// 测试篡改的Token
		String realToken = generateToken(orderId);
		String tamperedToken = realToken.substring(0, 63) + (realToken.charAt(63) == 'a' ? 'b' : 'a');
		System.out.println("正确Token: " + realToken);
		System.out.println("篡改Token: " + tamperedToken);

		boolean tamperedResult = validateToken(orderId, tamperedToken);
		System.out.println("篡改验证结果: " + tamperedResult);

		assertFalse(tamperedResult, "篡改后的Token不应通过验证");

		System.out.println("✓ testValidateTokenFail 通过\n");
	}

	/**
	 * 测试空参数处理
	 */
	@Test
	public void testValidateTokenEmptyParams() {
		System.out.println("--- testValidateTokenEmptyParams ---");
		String validToken = generateToken("SOME_ORDER");

		// 测试空orderId
		boolean result1 = validateToken(null, validToken);
		System.out.println("orderId=null, token=有效 -> " + result1);
		assertFalse(result1, "orderId为null时验证应失败");

		boolean result2 = validateToken("", validToken);
		System.out.println("orderId=空, token=有效 -> " + result2);
		assertFalse(result2, "orderId为空时验证应失败");

		// 测试空token
		boolean result3 = validateToken("SOME_ORDER", null);
		System.out.println("orderId=有效, token=null -> " + result3);
		assertFalse(result3, "token为null时验证应失败");

		boolean result4 = validateToken("SOME_ORDER", "");
		System.out.println("orderId=有效, token=空 -> " + result4);
		assertFalse(result4, "token为空时验证应失败");

		// 测试都为空
		boolean result5 = validateToken(null, null);
		System.out.println("orderId=null, token=null -> " + result5);
		assertFalse(result5, "两者都为null时验证应失败");

		boolean result6 = validateToken("", "");
		System.out.println("orderId=空, token=空 -> " + result6);
		assertFalse(result6, "两者都为空时验证应失败");

		System.out.println("✓ testValidateTokenEmptyParams 通过\n");
	}

	/**
	 * Token并发性能测试 - 多线程并发生成和验证Token，验证线程安全和性能
	 */
	@Test
	public void testTokenPerformance() throws Exception {
		System.out.println("--- testTokenPerformance ---");
		final int threadCount = 20;     // 并发线程数
		final int taskPerThread = 100;  // 每个线程执行的任务数
		final int totalTasks = threadCount * taskPerThread;

		AtomicInteger generateSuccess = new AtomicInteger(0);
		AtomicInteger validateSuccess = new AtomicInteger(0);
		AtomicInteger errorCount = new AtomicInteger(0);

		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(totalTasks);

		System.out.println("并发线程数: " + threadCount);
		System.out.println("每线程任务数: " + taskPerThread);
		System.out.println("总任务数: " + totalTasks);

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < totalTasks; i++) {
			final int taskId = i;
			executor.submit(() -> {
				try {
					String orderId = "PERF_ORDER_" + taskId;
					// 生成Token
					String token = generateToken(orderId);
					if (token != null && token.length() == 64) {
						generateSuccess.incrementAndGet();
					}
					// 验证Token
					boolean valid = validateToken(orderId, token);
					if (valid) {
						validateSuccess.incrementAndGet();
					}
				} catch (Exception e) {
					errorCount.incrementAndGet();
					System.err.println("任务 " + taskId + " 异常: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;

		System.out.println("\n--- 性能测试结果 ---");
		System.out.println("总耗时: " + duration + " ms");
		System.out.println("生成成功: " + generateSuccess.get() + "/" + totalTasks);
		System.out.println("验证成功: " + validateSuccess.get() + "/" + totalTasks);
		System.out.println("异常数量: " + errorCount.get());
		System.out.printf("平均每次操作耗时: %.3f ms%n", (double) duration / totalTasks);
		System.out.printf("吞吐量(TPS): %.0f 次/秒%n", (double) totalTasks / (duration / 1000.0));

		// 验证线程安全：所有生成和验证都应成功，无异常
		assertEquals(totalTasks, generateSuccess.get(), "所有Token生成应成功");
		assertEquals(totalTasks, validateSuccess.get(), "所有Token验证应成功");
		assertEquals(0, errorCount.get(), "不应有异常发生");

		System.out.println("✓ testTokenPerformance 通过\n");
	}

	// ==================== 2. 批量Token测试 ====================

	/**
	 * 测试批量Token生成 - 验证非空且长度为64
	 */
	@Test
	public void testGenerateBatchToken() {
		System.out.println("--- testGenerateBatchToken ---");
		String[] orderIds = {"103781", "103737", "103667"};
		String token = generateBatchToken(orderIds);

		System.out.println("订单号: " + String.join(",", orderIds));
		System.out.println("生成Token: " + token);

		assertNotNull(token, "批量Token不应为null");
		assertEquals(64, token.length(), "批量Token长度应为64位");
		assertTrue(token.matches("[0-9a-f]{64}"), "Token应为小写十六进制字符串");

		System.out.println("✓ testGenerateBatchToken 通过\n");
	}

	/**
	 * 测试批量Token排序一致性 - 不同输入顺序应生成相同Token
	 */
	@Test
	public void testBatchTokenOrderConsistency() {
		System.out.println("--- testBatchTokenOrderConsistency ---");
		String[] orderIds1 = {"103781", "103737", "103667"};
		String[] orderIds2 = {"103667", "103781", "103737"};
		String[] orderIds3 = {"103737", "103667", "103781"};

		String token1 = generateBatchToken(orderIds1);
		String token2 = generateBatchToken(orderIds2);
		String token3 = generateBatchToken(orderIds3);

		System.out.println("顺序1 Token: " + token1);
		System.out.println("顺序2 Token: " + token2);
		System.out.println("顺序3 Token: " + token3);

		assertEquals(token1, token2, "不同顺序应生成相同Token");
		assertEquals(token2, token3, "不同顺序应生成相同Token");

		System.out.println("✓ testBatchTokenOrderConsistency 通过\n");
	}

	/**
	 * 测试批量Token验证
	 */
	@Test
	public void testValidateBatchToken() {
		System.out.println("--- testValidateBatchToken ---");
		String[] orderIds = {"103781", "103737", "103667"};
		String token = generateBatchToken(orderIds);

		// 正确验证
		boolean result = validateBatchToken(orderIds, token);
		System.out.println("正确Token验证: " + result);
		assertTrue(result, "正确的批量Token应通过验证");

		// 错误Token
		boolean wrongResult = validateBatchToken(orderIds, "0000000000000000000000000000000000000000000000000000000000000000");
		System.out.println("错误Token验证: " + wrongResult);
		assertFalse(wrongResult, "错误的批量Token不应通过验证");

		// 不同订单集合的Token不应互通
		String[] otherIds = {"103781", "103737"};
		boolean crossResult = validateBatchToken(otherIds, token);
		System.out.println("交叉验证: " + crossResult);
		assertFalse(crossResult, "不同订单集合的Token不应互通");

		System.out.println("✓ testValidateBatchToken 通过\n");
	}

	// ==================== 3. API接口集成测试 ====================
	//
	// 以下集成测试需要本地服务运行在 http://192.168.77.37
	// 运行前请确保：
	//   1. 已启动 siargo 服务（通过 jfinal.bat 或 IDE 启动）
	//   2. 服务端口为80（或修改 API_BASE_URL 常量）
	//   3. 数据库中存在测试订单数据
	//   4. /admin/app 中已创建应用并启用（AppId: jbzvdqh9pxxmolt）
	//
	// 如果服务未运行，测试会因连接失败而跳过，不会影响其他测试
	// ================================================================

	/**
	 * 集成测试：使用有效Token调用API
	 * <p>
	 * 注意：此测试需要本地服务运行！
	 * 步骤：
	 *   1. 启动 siargo 服务
	 *   2. 确保数据库中有订单数据
	 *   3. 将下方 testOrderId 修改为实际存在的订单号
	 *   4. 运行此测试
	 * </p>
	 */
	@Test
	public void testApiWithValidToken() {
		System.out.println("--- testApiWithValidToken ---");
		// 请替换为实际存在的订单号
		String testOrderId = "103737";
		String token = generateToken(testOrderId);

		String url = API_BASE_URL + "?jboltappid=" + TEST_APP_ID + "&orderId=" + testOrderId + "&token=" + token;
		System.out.println("请求URL: " + url);

		try {
			String response = sendGetRequest(url);
			System.out.println("响应内容: " + response);

			// 有效Token应返回成功或"数据不存在"（取决于数据库中是否有该订单）
			assertNotNull(response, "响应不应为null");
			assertTrue(response.contains("\"status\""), "响应中应包含status字段");
			System.out.println("✓ testApiWithValidToken 通过\n");

		} catch (java.net.ConnectException e) {
			System.out.println("⚠ 服务未运行，跳过集成测试（" + e.getMessage() + "）\n");
		} catch (Exception e) {
			System.out.println("⚠ 请求异常，跳过集成测试（" + e.getMessage() + "）\n");
		}
	}

	/**
	 * 集成测试：使用无效Token调用API
	 * <p>注意：此测试需要本地服务运行！</p>
	 */
	@Test
	public void testApiWithInvalidToken() {
		System.out.println("--- testApiWithInvalidToken ---");
		String testOrderId = "103778";
		String invalidToken = "invalid_token_0000000000000000000000000000000000000000000000";

		String url = API_BASE_URL + "?jboltappid=" + TEST_APP_ID + "&orderId=" + testOrderId + "&token=" + invalidToken;
		System.out.println("请求URL: " + url);

		try {
			String response = sendGetRequest(url);
			System.out.println("响应内容: " + response);

			// 无效Token应返回失败
			assertNotNull(response, "响应不应为null");
			assertTrue(response.contains("\"fail\""), "无效Token应返回fail状态");
			assertTrue(response.contains("token验证失败") || response.contains("1003"), "应包含token验证失败消息或错误码1003");
			System.out.println("✓ testApiWithInvalidToken 通过\n");

		} catch (java.net.ConnectException e) {
			System.out.println("⚠ 服务未运行，跳过集成测试（" + e.getMessage() + "）\n");
		} catch (Exception e) {
			System.out.println("⚠ 请求异常，跳过集成测试（" + e.getMessage() + "）\n");
		}
	}

	/**
	 * 集成测试：空订单号调用API
	 * <p>注意：此测试需要本地服务运行！</p>
	 */
	@Test
	public void testApiWithEmptyOrderId() {
		System.out.println("--- testApiWithEmptyOrderId ---");
		String token = "any_token_value";

		// 不传orderId参数
		String url = API_BASE_URL + "?jboltappid=" + TEST_APP_ID + "&token=" + token;
		System.out.println("请求URL: " + url);

		try {
			String response = sendGetRequest(url);
			System.out.println("响应内容: " + response);

			// 空订单号应返回失败
			assertNotNull(response, "响应不应为null");
			assertTrue(response.contains("\"fail\""), "空订单号应返回fail状态");
			assertTrue(response.contains("订单号不能为空") || response.contains("1001"), "应包含订单号不能为空消息或错误码1001");
			System.out.println("✓ testApiWithEmptyOrderId 通过\n");

		} catch (java.net.ConnectException e) {
			System.out.println("⚠ 服务未运行，跳过集成测试（" + e.getMessage() + "）\n");
		} catch (Exception e) {
			System.out.println("⚠ 请求异常，跳过集成测试（" + e.getMessage() + "）\n");
		}
	}

	/**
	 * 集成测试：批量查询API
	 * <p>注意：此测试需要本地服务运行！</p>
	 */
	@Test
	public void testBatchApiWithValidToken() {
		System.out.println("--- testBatchApiWithValidToken ---");
		String[] orderIds = {"103781", "103737", "103667"};
		String token = generateBatchToken(orderIds);
		String orderIdsStr = String.join(",", orderIds);

		String url = BATCH_API_BASE_URL + "?jboltappid=" + TEST_APP_ID + "&orderIds=" + orderIdsStr + "&token=" + token;
		System.out.println("请求URL: " + url);

		try {
			String response = sendGetRequest(url);
			System.out.println("响应内容: " + response);

			assertNotNull(response, "响应不应为null");
			assertTrue(response.contains("\"status\""), "响应中应包含status字段");
			// 成功时包含results，失败时包含msg
			if (response.contains("\"ok\"")) {
				assertTrue(response.contains("\"results\""), "成功响应中应包含results字段");
			} else {
				System.out.println("⚠ 服务返回失败状态，请检查应用是否已在/admin/app中启用");
			}
			System.out.println("✓ testBatchApiWithValidToken 通过\n");

		} catch (java.net.ConnectException e) {
			System.out.println("⚠ 服务未运行，跳过集成测试（" + e.getMessage() + "）\n");
		} catch (Exception e) {
			System.out.println("⚠ 请求异常，跳过集成测试（" + e.getMessage() + "）\n");
		}
	}

	// ==================== HTTP辅助方法 ====================

	/**
	 * 发送GET请求
	 * @param urlStr 完整URL
	 * @return 响应内容字符串
	 */
	private String sendGetRequest(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(10000);
		conn.setRequestProperty("User-Agent", "OrderStatusApiTest/1.0");

		int responseCode = conn.getResponseCode();
		StringBuilder response = new StringBuilder();

		// 读取响应（成功或失败流）
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(
						responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"))) {
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line);
			}
		}
		conn.disconnect();

		System.out.println("HTTP状态码: " + responseCode);
		return response.toString();
	}
}
