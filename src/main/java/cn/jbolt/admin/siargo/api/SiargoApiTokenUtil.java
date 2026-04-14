package cn.jbolt.admin.siargo.api;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.jfinal.kit.StrKit;

/**
 * Siargo 对外API Token工具类
 * <p>用于生成和验证对外API接口的访问Token</p>
 * 
 * <pre>
 * 使用方 Token 生成算法说明：
 * token = SHA256(密钥 + 订单号 + 当天日期YYYYMMDD)
 * 
 * Java 示例：
 *   String raw = secretKey + orderId + "20260413";
 *   MessageDigest md = MessageDigest.getInstance("SHA-256");
 *   byte[] hash = md.digest(raw.getBytes("UTF-8"));
 *   String token = bytesToHex(hash); // 转为64位十六进制小写字符串
 * 
 * Python 示例：
 *   import hashlib
 *   raw = secret_key + order_id + "20260413"
 *   token = hashlib.sha256(raw.encode('utf-8')).hexdigest()
 * 
 * 接口调用：
 *   单个查询：GET /api/siargo/order/status?orderId=12345&amp;token=xxx
 *   批量查询：GET /api/siargo/order/batchStatus?orderIds=12345,67890&amp;token=xxx
 *   批量token = SHA256(密钥 + 订单号排序拼接 + 当天日期YYYYMMDD)
 * </pre>
 * 
 * @author siargo
 * @date 2026-04-13
 */
public class SiargoApiTokenUtil {

	/** 日期格式化：YYYYMMDD */
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	/**
	 * 生成Token
	 * @param secretKey 签名密钥（Application的appSecret）
	 * @param orderId 订单号
	 * @return 64位十六进制小写SHA256字符串
	 */
	public static String generateToken(String secretKey, String orderId) {
		String dateStr = LocalDate.now().format(DATE_FORMATTER);
		return sha256Hex(secretKey + orderId + dateStr);
	}

	/**
	 * 验证Token
	 * <p>同时验证当天和前一天的Token，解决跨日边界问题</p>
	 * @param secretKey 签名密钥（Application的appSecret）
	 * @param orderId 订单号
	 * @param token 待验证的Token
	 * @return true=验证通过，false=验证失败
	 */
	public static boolean validateToken(String secretKey, String orderId, String token) {
		if (StrKit.isBlank(secretKey) || StrKit.isBlank(orderId) || StrKit.isBlank(token)) {
			return false;
		}
		// 验证当天Token
		String todayDate = LocalDate.now().format(DATE_FORMATTER);
		String todayToken = sha256Hex(secretKey + orderId + todayDate);
		if (token.equals(todayToken)) {
			return true;
		}
		// 验证前一天Token（跨日边界）
		String yesterdayDate = LocalDate.now().minusDays(1).format(DATE_FORMATTER);
		String yesterdayToken = sha256Hex(secretKey + orderId + yesterdayDate);
		return token.equals(yesterdayToken);
	}

	/**
	 * 生成批量查询Token
	 * <p>将多个订单号排序后用逗号拼接，再计算SHA256</p>
	 * @param secretKey 签名密钥（Application的appSecret）
	 * @param orderIds 订单号数组
	 * @return 64位十六进制小写SHA256字符串
	 */
	public static String generateBatchToken(String secretKey, String[] orderIds) {
		if (orderIds == null || orderIds.length == 0) {
			return "";
		}
		java.util.Arrays.sort(orderIds);
		String joined = String.join(",", orderIds);
		String dateStr = LocalDate.now().format(DATE_FORMATTER);
		return sha256Hex(secretKey + joined + dateStr);
	}

	/**
	 * 验证批量查询Token
	 * <p>同时验证当天和前一天的Token，解决跨日边界问题</p>
	 * @param secretKey 签名密钥（Application的appSecret）
	 * @param orderIds 订单号数组
	 * @param token 待验证的Token
	 * @return true=验证通过，false=验证失败
	 */
	public static boolean validateBatchToken(String secretKey, String[] orderIds, String token) {
		if (StrKit.isBlank(secretKey) || orderIds == null || orderIds.length == 0 || StrKit.isBlank(token)) {
			return false;
		}
		java.util.Arrays.sort(orderIds);
		String joined = String.join(",", orderIds);
		// 验证当天Token
		String todayDate = LocalDate.now().format(DATE_FORMATTER);
		if (token.equals(sha256Hex(secretKey + joined + todayDate))) {
			return true;
		}
		// 验证前一天Token（跨日边界）
		String yesterdayDate = LocalDate.now().minusDays(1).format(DATE_FORMATTER);
		return token.equals(sha256Hex(secretKey + joined + yesterdayDate));
	}

	/**
	 * 计算SHA256哈希值
	 * @param input 输入字符串
	 * @return 64位十六进制小写字符串
	 */
	private static String sha256Hex(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(input.getBytes("UTF-8"));
			return bytesToHex(hash);
		} catch (Exception e) {
			throw new RuntimeException("SHA-256计算失败", e);
		}
	}

	/**
	 * 字节数组转十六进制小写字符串
	 * @param bytes 字节数组
	 * @return 十六进制字符串
	 */
	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
}
