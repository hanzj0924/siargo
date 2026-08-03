package cn.jbolt.admin.siargo.qarep;

/**
 * 检验报告单模块常量
 * <p>集中管理 insp 检验进度、角色 SN、产品类型等散落的魔法数字</p>
 *
 * <p>insp 检验进度语义（siargo_product.insp）：</p>
 * <ul>
 *   <li>1 = 精度待检（初始状态）</li>
 *   <li>2 = 外观待检（精度检验已完成，accq_uid/accq_time 已签）</li>
 *   <li>3 = 包装待检（外观检验已完成，funq_uid/funq_time 已签）</li>
 *   <li>4 = 待批准（包装检验已完成，appq_uid/appq_time 已签）</li>
 *   <li>5 = 已完成/最终放行（批准已完成，allq_uid/allq_time 已签）</li>
 * </ul>
 *
 * <p>批准（batchInspection）目标 insp 与操作角色映射：</p>
 * <ul>
 *   <li>目标 insp=2（完成精度检验）→ 角色 SN 211 精度检验员</li>
 *   <li>目标 insp=3（完成外观检验）→ 角色 SN 212 外观检验员</li>
 *   <li>目标 insp=4（完成包装检验）→ 角色 SN 213 包装检验员</li>
 *   <li>目标 insp=5（完成批准放行）→ 角色 SN 214 批准员</li>
 * </ul>
 *
 * <p>驳回（batchReject）当前 insp 与操作角色映射（当前环节负责人才能驳回）：</p>
 * <ul>
 *   <li>当前 insp=2（外观待检）→ 角色 SN 212 外观检验员，回退到 1 并清空 accq</li>
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

	/** 批准操作允许的目标 insp 下限（含） */
	public static final int INSP_APPROVE_MIN = INSP_PENDING_APPEARANCE;
	/** 批准操作允许的目标 insp 上限（含） */
	public static final int INSP_APPROVE_MAX = INSP_COMPLETED;
	/** 驳回操作允许的当前 insp 下限（含） */
	public static final int INSP_REJECT_MIN = INSP_PENDING_APPEARANCE;
	/** 驳回操作允许的当前 insp 上限（含） */
	public static final int INSP_REJECT_MAX = INSP_PENDING_APPROVAL;

	// ==================== 角色 SN ====================
	/** 系统管理员角色 SN */
	public static final int ROLE_SN_ADMIN = 1;
	/** 精度检验员角色 SN */
	public static final int ROLE_SN_ACCURACY = 211;
	/** 外观检验员角色 SN */
	public static final int ROLE_SN_APPEARANCE = 212;
	/** 包装检验员角色 SN */
	public static final int ROLE_SN_PACKAGING = 213;
	/** 批准员角色 SN */
	public static final int ROLE_SN_APPROVAL = 214;

	// ==================== 产品类型（siargo_prod_type 字典 sn） ====================
	/** 传感器 */
	public static final int PROD_TYPE_SENSOR = 1;
	/** 小流量 */
	public static final int PROD_TYPE_SMALL_FLOW = 2;
	/** 大流量 */
	public static final int PROD_TYPE_LARGE_FLOW = 3;

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

	/**
	 * 批准操作：目标 insp → 需要签名的环节列前缀（accq/funq/appq/allq）
	 * @param targetInsp 目标 insp（2~5）
	 * @return 列前缀，非法值返回 null
	 */
	public static String approveStageColumn(int targetInsp) {
		switch (targetInsp) {
			case INSP_PENDING_APPEARANCE: return "accq";
			case INSP_PENDING_PACKAGING: return "funq";
			case INSP_PENDING_APPROVAL: return "appq";
			case INSP_COMPLETED: return "allq";
			default: return null;
		}
	}

	/**
	 * 驳回操作：当前 insp → 需要清空的上一环节签名列前缀
	 * @param currentInsp 当前 insp（2~4）
	 * @return 列前缀，非法值返回 null
	 */
	public static String rejectClearStageColumn(int currentInsp) {
		switch (currentInsp) {
			case INSP_PENDING_APPEARANCE: return "accq";
			case INSP_PENDING_PACKAGING: return "funq";
			case INSP_PENDING_APPROVAL: return "appq";
			default: return null;
		}
	}

	/**
	 * 批准操作：目标 insp → 所需角色 SN（211~214）
	 * @param targetInsp 目标 insp（2~5）
	 * @return 角色 SN，非法值返回 -1
	 */
	public static int approveRoleSn(int targetInsp) {
		switch (targetInsp) {
			case INSP_PENDING_APPEARANCE: return ROLE_SN_ACCURACY;
			case INSP_PENDING_PACKAGING: return ROLE_SN_APPEARANCE;
			case INSP_PENDING_APPROVAL: return ROLE_SN_PACKAGING;
			case INSP_COMPLETED: return ROLE_SN_APPROVAL;
			default: return -1;
		}
	}

	/**
	 * 驳回操作：当前 insp → 所需角色 SN（212~214，当前环节负责人）
	 * @param currentInsp 当前 insp（2~4）
	 * @return 角色 SN，非法值返回 -1
	 */
	public static int rejectRoleSn(int currentInsp) {
		switch (currentInsp) {
			case INSP_PENDING_APPEARANCE: return ROLE_SN_APPEARANCE;
			case INSP_PENDING_PACKAGING: return ROLE_SN_PACKAGING;
			case INSP_PENDING_APPROVAL: return ROLE_SN_APPROVAL;
			default: return -1;
		}
	}
}
