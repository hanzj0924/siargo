package cn.jbolt.admin.siargo.qarep.siargoconst;



import cn.jbolt.siargo.model.Product;

/**
 * 检验报告单模块常量
 * <p>集中管理 insp 检验进度、角色 SN、产品类型等散落的魔法数字</p>
 *
 * <p>insp 检验进度语义（siargo_product.insp）：</p>
 * <ul>
 *   <li>1 = 精度待检（初始状态）</li>
 *   <li>6 = 成品检漏待检（仅 lt_status=1 的产品在精度检验后进入，accq_uid/accq_time 已签）</li>
 *   <li>2 = 外观待检（精度检验已完成；lt_status=1 时表示成品检漏也已签 lt_uid/lt_time）</li>
 *   <li>3 = 包装待检（外观检验已完成，funq_uid/funq_time 已签）</li>
 *   <li>4 = 待批准（包装检验已完成，appq_uid/appq_time 已签）</li>
 *   <li>5 = 已完成/最终放行（批准已完成，allq_uid/allq_time 已签）</li>
 * </ul>
 *
 * <p>批准（batchInspection）目标 insp 与操作角色映射：</p>
 * <ul>
 *   <li>目标 insp=2（完成精度检验）→ 角色 SN 211 精度检验员</li>
 *   <li>目标 insp=6（完成成品检漏检验）→ 角色 SN 215 成品检漏检验员</li>
 *   <li>目标 insp=3（完成外观检验）→ 角色 SN 212 外观检验员</li>
 *   <li>目标 insp=4（完成包装检验）→ 角色 SN 213 包装检验员</li>
 *   <li>目标 insp=5（完成批准放行）→ 角色 SN 214 批准员</li>
 * </ul>
 *
 * <p>驳回（batchReject）当前 insp 与操作角色映射（当前环节负责人才能驳回）：</p>
 * <ul>
 *   <li>当前 insp=2（外观待检）→ 角色 SN 212 外观检验员，lt_status=1 回退到 6 并清空 lt，否则回退到 1 并清空 accq</li>
 *   <li>当前 insp=6（成品检漏待检）→ 角色 SN 215 成品检漏检验员，回退到 1 并清空 accq</li>
 *   <li>当前 insp=3（包装待检）→ 角色 SN 213 包装检验员，回退到 2 并清空 funq</li>
 *   <li>当前 insp=4（待批准）→ 角色 SN 214 批准员，回退到 3 并清空 appq</li>
 * </ul>
 *
 * @author: hanzj
 * @date: 2026-07-30
 */
public final class QarepConst {

	private QarepConst() {
	}

	// ==================== insp 检验进度 ====================
	/** 精度待检（初始状态） */
	public static final int INSP_PENDING_ACCURACY = 1;
	/** 外观待检（精度检验已完成） */
	public static final int INSP_PENDING_APPEARANCE = 2;
	/** 包装待检（外观检验已完成） */
	public static final int INSP_PENDING_PACKAGING = 3;
	/** 待批准（包装检验已完成） */
	public static final int INSP_PENDING_APPROVAL = 4;
	/** 已完成 / 最终放行 */
	public static final int INSP_COMPLETED = 5;
	/** 成品检漏待检（仅 lt_status=1 的产品使用，精度检验已完成） */
	public static final int INSP_PENDING_LEAK_TEST = 6;

	/** 批准操作允许的目标 insp 下限（含） */
	public static final int INSP_APPROVE_MIN = INSP_PENDING_APPEARANCE;
	/** 批准操作允许的目标 insp 上限（含） */
	public static final int INSP_APPROVE_MAX = INSP_PENDING_LEAK_TEST;

	// ==================== 成品检漏标记（siargo_product.lt_status） ====================
	/** 有成品检漏环节 */
	public static final int LT_STATUS_YES = 1;
	/** 无成品检漏环节 */
	public static final int LT_STATUS_NO = 2;

	// ==================== 角色 SN ====================
	/** 精度检验员角色 SN */
	public static final int ROLE_SN_ACCURACY = 211;
	/** 成品检漏检验员角色 SN */
	public static final int ROLE_SN_LEAK_TEST = 215;
	/** 外观检验员角色 SN */
	public static final int ROLE_SN_APPEARANCE = 212;
	/** 包装检验员角色 SN */
	public static final int ROLE_SN_PACKAGING = 213;
	/** 批准员角色 SN */
	public static final int ROLE_SN_APPROVAL = 214;

	// ==================== 报告单类型（siargo_qareport.rep_type） ====================
	/** 正常/产成品报告单 */
	public static final int REP_TYPE_NORMAL = 1;
	/** 返修品报告单 */
	public static final int REP_TYPE_REPAIR = 2;

	// ==================== 软删除标记（siargo_product.vd） ====================
	/** 有效数据 */
	public static final int VD_VALID = 1;
	/** 已删除（回收站） */
	public static final int VD_DELETED = 0;

	// ==================== 报告单编号 ====================
	/** 报告单编号并发冲突最大重试次数（creatFormnum 撞号后重新生成） */
	public static final int FORMNUM_RETRY_MAX = 3;

	// ==================== 产品电气参数阈值（save/update 落库前校验） ====================
	/** 整机最大电流下限（mA，含） */
	public static final double PARAM_CUC_MIN = 6D;
	/** 整机最大电流上限（mA，含） */
	public static final double PARAM_CUC_MAX = 25D;
	/** 整机最小电流上限（mA，含） */
	public static final double PARAM_CUCMIN_MAX = 31D;
	/** 整机最大电流（另一档）上限（mA，含） */
	public static final double PARAM_CUCMAX_MAX = 21D;
	/** 脉冲电压下限（V，含） */
	public static final double PARAM_PV_MIN = 2.7D;
	/** 脉冲电压上限（V，含） */
	public static final double PARAM_PV_MAX = 99.999D;
	/** 零点内码上限（含） */
	public static final int PARAM_ZP_MAX = 30;
	/** 故障电平下限（V，含） */
	public static final double PARAM_FL_MIN = 2.7D;
	/** 故障电平上限（V，含） */
	public static final double PARAM_FL_MAX = 99.99D;
	/** 电池电压下限（V，含） */
	public static final double PARAM_BV_MIN = 3.2505D;
	/** 电池电压上限（V，含） */
	public static final double PARAM_BV_MAX = 3.3495D;
	/** 本地地址上限（含） */
	public static final int PARAM_LA_MAX = 50;
	/** 热头电压上限（含） */
	public static final int PARAM_THV_MAX = 1690;
	
	/**
	 * 批准操作：目标 insp → 需要签名的环节列前缀（accq/lt/funq/appq/allq）
	 * @param targetInsp 目标 insp（2~6）
	 * @return 列前缀，非法值返回 null
	 */
	public static String approveStageColumn(int targetInsp) {
		switch (targetInsp) {
			case INSP_PENDING_APPEARANCE: return "accq";
			case INSP_PENDING_LEAK_TEST: return "lt";
			case INSP_PENDING_PACKAGING: return "funq";
			case INSP_PENDING_APPROVAL: return "appq";
			case INSP_COMPLETED: return "allq";
			default: return null;
		}
	}

	/**
	 * 批准操作：目标 insp → 所需角色 SN（211~215）
	 * @param targetInsp 目标 insp（2~6）
	 * @return 角色 SN，非法值返回 -1
	 */
	public static int approveRoleSn(int targetInsp) {
		switch (targetInsp) {
			case INSP_PENDING_APPEARANCE: return ROLE_SN_ACCURACY;
			case INSP_PENDING_LEAK_TEST: return ROLE_SN_LEAK_TEST;
			case INSP_PENDING_PACKAGING: return ROLE_SN_APPEARANCE;
			case INSP_PENDING_APPROVAL: return ROLE_SN_PACKAGING;
			case INSP_COMPLETED: return ROLE_SN_APPROVAL;
			default: return -1;
		}
	}

	/**
	 * 驳回操作：当前 insp → 所需角色 SN（212~215，当前环节负责人）
	 * @param currentInsp 当前 insp（2~6，当前环节负责人）
	 * @return 角色 SN，非法值返回 -1
	 */
	public static int rejectRoleSn(int currentInsp) {
		switch (currentInsp) {
			case INSP_PENDING_APPEARANCE: return ROLE_SN_APPEARANCE;
			case INSP_PENDING_LEAK_TEST: return ROLE_SN_LEAK_TEST;
			case INSP_PENDING_PACKAGING: return ROLE_SN_PACKAGING;
			case INSP_PENDING_APPROVAL: return ROLE_SN_APPROVAL;
			default: return -1;
		}
	}

	/**
	 * 驳回操作允许的当前 insp 集合（2、3、4、6；已完成 5 与精度待检 1 不可驳回）
	 * @param currentInsp 当前 insp
	 * @return 是否可驳回
	 */
	public static boolean isRejectableInsp(int currentInsp) {
		return currentInsp == INSP_PENDING_APPEARANCE
				|| currentInsp == INSP_PENDING_LEAK_TEST
				|| currentInsp == INSP_PENDING_PACKAGING
				|| currentInsp == INSP_PENDING_APPROVAL;
	}



    /** 仅校验已填写的电气参数，空值允许保存；系列与模板适用性由关联数据决定。 */
    public static String validateElectricalParams(Product product) {
		if (product == null) {
			return null;
		}
        for (String field : java.util.List.of("cuc", "cucmax", "cucmin", "pv", "fl", "bv")) {
            Double value = product.getDouble(field);
            if (value != null && !Double.isFinite(value)) return "电气参数必须为有限数值，请重新输入";
        }
		if (product.getDouble("cuc") != null && (product.getDouble("cuc") < PARAM_CUC_MIN || product.getDouble("cuc") > PARAM_CUC_MAX)) {
			return "整机最大电流6-24mA，请重新输入！";
		}
		if (product.getDouble("cucmin") != null && product.getDouble("cucmin") > PARAM_CUCMIN_MAX) {
			return "整机电流超过30mA，请重新输入！";
		}
		if (product.getDouble("cucmax") != null && product.getDouble("cucmax") > PARAM_CUCMAX_MAX) {
			return "整机电流超过20mA，请重新输入！";
		}
		if (product.getDouble("pv") != null && (product.getDouble("pv") < PARAM_PV_MIN || product.getDouble("pv") > PARAM_PV_MAX)) {
			return "脉冲电压低于2.7V或超上限99.999，请重新输入！";
		}
		if (product.getInt("zp") != null && product.getInt("zp") > PARAM_ZP_MAX) {
			return "零点内码超过30，请重新输入！";
		}
		if (product.getDouble("fl") != null && (product.getDouble("fl") < PARAM_FL_MIN || product.getDouble("fl") > PARAM_FL_MAX)) {
			return "故障电平低于2.7V或超上限99.99，请重新输入！";
		}
		if (product.getDouble("bv") != null && (product.getDouble("bv") >= PARAM_BV_MAX || product.getDouble("bv") <= PARAM_BV_MIN)) {
			return "电池电压3.2505V-3.3495V，请重新输入！";
		}
		if (product.getInt("la") != null && product.getInt("la") > PARAM_LA_MAX) {
			return "本地地址超过50，请重新输入！";
		}
		if (product.getInt("thv") != null && product.getInt("thv") > PARAM_THV_MAX) {
			return "热头电压超过1690，请重新输入！";
		}
		return null;
	}
}
