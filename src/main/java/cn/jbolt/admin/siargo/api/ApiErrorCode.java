package cn.jbolt.admin.siargo.api;

/**
 * 对外API接口错误码常量
 *
 * @author Hanzj
 * @date 2026-07-06
 */
public class ApiErrorCode {

	/** 订单号不能为空 */
	public static final int ORDER_ID_EMPTY = 1001;

	/** Token不能为空 */
	public static final int TOKEN_EMPTY = 1002;

	/** Token验证失败 */
	public static final int TOKEN_INVALID = 1003;

	/** 订单未创建 */
	public static final int ORDER_NOT_FOUND = 1004;

	/** 单次批量查询超过上限 */
	public static final int BATCH_SIZE_EXCEEDED = 1005;

	/** 应用认证失败（Application不存在） */
	public static final int APP_AUTH_FAILED = 1006;

	/** 服务内部错误 */
	public static final int INTERNAL_ERROR = 1007;

	/**
	 * 根据错误码获取默认消息
	 */
	public static String defaultMsg(int code) {
		switch (code) {
			case ORDER_ID_EMPTY:
				return "订单号不能为空";
			case TOKEN_EMPTY:
				return "Token不能为空";
			case TOKEN_INVALID:
				return "Token验证失败";
			case ORDER_NOT_FOUND:
				return "订单未创建";
			case BATCH_SIZE_EXCEEDED:
				return "单次批量查询不能超过100个订单";
			case APP_AUTH_FAILED:
				return "应用认证失败";
			case INTERNAL_ERROR:
				return "服务内部错误";
			default:
				return "未知错误";
		}
	}

	private ApiErrorCode() {}
}
