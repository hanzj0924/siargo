package cn.jbolt.admin.siargo.qarep;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.log.Log;

import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.siargo.model.ProductRejectLog;

/**
 * 产品驳回历史 Service
 * <p>一个产品可对应多条驳回记录，记录每次驳回的环节、原因、驳回人与时间</p>
 * @ClassName: ProductRejectLogService
 * @author: hanzj
 * @date: 2026-07-29
 */
public class ProductRejectLogService extends JBoltBaseService<ProductRejectLog> {
	private static final Log LOG = Log.getLog(ProductRejectLogService.class);
	/** 产品驳回历史数据访问对象 */
	private final ProductRejectLog dao = new ProductRejectLog().dao();

	@Override
	protected ProductRejectLog dao() {
		return dao;
	}

	/**
	 * 保存一条驳回记录
	 * @param productId 产品ID
	 * @param rejectInsp 驳回发生环节（2=外观检验 3=包装检验 4=批准）
	 * @param rejectDes 驳回原因
	 * @param rejectUid 驳回人ID
	 * @return 是否成功
	 */
	public boolean saveLog(Long productId, Integer rejectInsp, String rejectDes, Long rejectUid) {
		boolean saved = new ProductRejectLog()
				.setProductId(productId)
				.setRejectInsp(rejectInsp)
				.setRejectDes(rejectDes)
				.setRejectUid(rejectUid)
				.setRejectTime(new Date())
				.save();
		if (saved) {
			// 同步更新产品表冗余计数字段，避免列表查询时走子查询
			try {
				Db.update("UPDATE siargo_product SET reject_count = reject_count + 1 WHERE id = ?", productId);
			} catch (Exception e) {
				LOG.warn("更新产品驳回计数失败，不影响主流程", e);
			}
		}
		return saved;
	}

	/**
	 * 查询指定产品的全部驳回历史（按时间倒序，最新一条在前）
	 * <p>含驳回人姓名、格式化时间与环节名称翻译</p>
	 * @param productId 产品ID
	 * @return 驳回历史记录列表
	 */
	public List<Record> findLogsByProductId(Long productId) {
		String sql = "SELECT rl.id, rl.reject_insp, rl.reject_des, "
			+ "CASE rl.reject_insp WHEN 2 THEN '外观检验' WHEN 3 THEN '包装检验' WHEN 4 THEN '批准' END AS reject_insp_name, "
			+ "DATE_FORMAT(rl.reject_time, '%Y-%m-%d %H:%i') AS reject_time, "
			+ "u.name AS reject_name "
			+ "FROM siargo_product_reject_log rl "
			+ "LEFT JOIN jb_user u ON u.id = rl.reject_uid "
			+ "WHERE rl.product_id = ? "
			+ "ORDER BY rl.id ASC";
		return Db.find(sql, productId);
	}

	/**
	 * 设置系统日志的目标类型
	 * @return 日志目标类型
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}

}
