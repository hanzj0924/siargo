package cn.jbolt.admin.siargo.qarep;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import cn.jbolt.common.util.DateUtil;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.siargo.model.Product;
import cn.jbolt.siargo.model.Qareport;

/**
 * 检验报告单管理 Service
 * 
 * @ClassName: QareportService
 * @author: hanzj
 * @date: 2025-12-02 14:14
 */
public class QareportService extends JBoltBaseService<Qareport> {
	/** 检验报告单数据访问对象 */
	private final Qareport dao = new Qareport().dao();

	@Override
	protected Qareport dao() {
		return dao;
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
	            .eq("sp.vd", 1)  // 有效数据
	            .eq("sp.insp", 5)  // 已完成最终放行
	            .bwDate("sp.allq_time",  // 最终放行时间在上月范围内
	                    DateUtil.lastMonthFirstDay(DateUtil.getNow()),
	                    DateUtil.lastMonthLastDay(DateUtil.getNow()));

	    return findRecord(sql);
	}
	
	/**
	 * 后台管理分页查询报告单列表
	 * <p>关联查询产品表、客户表、用户表和字典表，获取完整展示信息</p>
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
		// ========== 构建基础查询 ==========
		Sql sql = Sql.mysql()
				// 选择字段：报告单基础信息
				.select("sq.id", "sq.order_id", "sc.name AS sc_name", "sq.formnum","sp.insp",
						// 检验时间信息
						"sp.accq_time", "sp.funq_time", "sp.appq_time", "sp.allq_time",
						// 检验人员姓名
						"accq_user.name AS accq_name", "funq_user.name AS funq_name", "appq_user.name AS appq_name",
						"allq_user.name AS allq_name", "DATE_FORMAT(sq.create_time, '%Y-%m-%d %H:%i') as create_time",
						// 产品信息字段
						"sp.id as spid", "sp.modle as sp_modle", "sp.number as sp_number", "sp.type as sp_type",
						"sp.qsi as sp_qsi", "sp.qi as sp_qi", "sp.flow_range as sp_flow_range", "sp.des as sp_des", 
						"sp.pdfstr AS sp_pdfstr", "sp.pdfver AS sp_pdfver","sp.cuc as sp_cuc", "sp.pv as sp_pv", 
						"sp.thv as sp_thv", "sp.zp as sp_zp", "sp.fl as sp_fl", "sp.cucmax as sp_cucmax", 
						"sp.cucmin as sp_cucmin", "sp.bv as sp_bv", "sp.la as sp_la", 
						// 字典翻译字段
						"d_type.name AS type_name","d_insp.name AS insp_name","d_flow.name AS flow_name",
						"d_pdfver.name AS pdfver_name","d_retype.name AS retype_name"
						)
				.page(pageNumber, pageSize).from("siargo_product", "sp")
				// ========== 关联报告单表 ==========
				.leftJoin("siargo_qareport", "sq", "sq.id = sp.report_id")
				// ========== 关联客户表 ==========
				.leftJoin("siargo_customer", "sc", "sc.id = sq.cust_id")
				// ========== 关联字典表获取产品类型名称 ==========
				.leftJoin("jb_dictionary", "d_type", "d_type.type_key = 'siargo_prod_type' "
						+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(sp.type AS CHAR) "
						+ "AND d_type.enable = '1 '")
				// ========== 关联字典表获取报告类型名称 ==========
				.leftJoin("jb_dictionary", "d_retype", "d_retype.type_key = 'siargo_rep_type' "
						+ "AND d_retype.sn COLLATE utf8mb4_general_ci = CAST(sq.rep_type AS CHAR) "
						+ "AND d_retype.enable = '1 '")
				// ========== 关联字典表获取检验进度名称 ==========
				.leftJoin("jb_dictionary", "d_insp", "d_insp.type_key = 'siargo_insp' "
						+ "AND d_insp.sn COLLATE utf8mb4_general_ci = CAST(sp.insp AS CHAR) "
						+ "AND d_insp.enable = '1 '")
				// ========== 关联字典表获取PDF版本名称 ==========
				.leftJoin("jb_dictionary", "d_pdfver", "d_pdfver.type_key = 'siargo_pdfver' "
						+ "AND d_pdfver.sn COLLATE utf8mb4_general_ci = CAST(sp.pdfver AS CHAR) "
						+ "AND d_pdfver.enable = '1 '")
				// ========== 关联字典表获取流量范围名称 ==========
				.leftJoin("jb_dictionary", "d_flow", "d_flow.type_key = 'siargo_flow_range' "
						+ "AND d_flow.sn COLLATE utf8mb4_general_ci = sp.flow_range "
						+ "AND d_flow.enable = '1 '")
				// ========== 关联用户表获取各阶段检验人员信息 ==========
				.leftJoin("jb_user", "accq_user", "accq_user.id = sp.accq_uid")
				.leftJoin("jb_user", "funq_user", "funq_user.id = sp.funq_uid")
				.leftJoin("jb_user", "appq_user", "appq_user.id = sp.appq_uid")
				.leftJoin("jb_user", "allq_user", "allq_user.id = sp.allq_uid").eq("sp.vd", 1);
	
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
				
			// 根据检验进度设置对应的排序字段
			switch(insp){
	         case 1:
	        	 sql.orderBy("sq.create_time", true);  // 待检验：按创建时间
	         case 2:
	        	 sql.orderBy("sp.accq_time", true);  // 精度检验：按精度检验时间
	         case 3:
	        	 sql.orderBy("sp.funq_time", true);  // 功能检验：按功能检验时间
	         case 4:
	        	 sql.orderBy("sp.appq_time", true);  // 批准检验：按批准时间
	         case 5:
	        	 sql.orderBy("sp.allq_time", true);  // 最终放行：按放行时间
	         default:
	        	 sql.orderBy("sq.create_time", true);
			}
				
		}else {
			sql.orderBy("sq.create_time", true);
		}
			
		return paginateRecord(sql, true);
	}

	/**
	 * 保存报告单和产品数据
	 * <p>事务协调说明：先保存报告单获取ID，再关联产品记录</p>
	 * <p>如果检验进度为精度检验（insp=2），自动记录精度检验人员和时间</p>
	 * @param qareport 报告单对象
	 * @param product 产品对象
	 * @return 操作结果
	 */
	public Ret save(Qareport qareport, Product product) {
		if (qareport == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}

		// 校验检验进度：精度检验之前不能跳过
		if (product.getInsp() > 2) {
			return fail("未检验精度，请重新选择检验进度！");
		}

		// ========== 保存报告单（如果不存在）==========
		if (notOk(qareport.getId())) {

			// 设置创建时间和自动生成报告单编号
			qareport.set("create_time", DateUtil.getDateString(DateUtil.YMDHMS));
			qareport.set("formnum", creatFormnum());

			qareport.save();
		}

		// ========== 保存产品记录 ==========
		boolean prodsuccess = false;
		if (notOk(product.getId())) {
			// 新建产品记录
			Product pro = new Product();

			// 如果检验进度为精度检验，记录精度检验数据
			if (product.getInsp() == 2) {
				pro.set("accq_uid", JBoltUserKit.getUserId());
				pro.set("accq_time", DateUtil.getDateString(DateUtil.YMDHMS));
			}
			
			// 复制产品属性
			pro.set("insp", product.getInsp());
			pro.set("type", product.getType());
			pro.set("modle", product.getModle());
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
			pro.set("vd", 1);  // 标记为有效数据
			prodsuccess = pro.save();

		} else {
			// 更新已有产品记录
			// 如果检验进度为精度检验，记录精度检验数据
			if (product.getInsp() == 2) {
				product.set("accq_uid", JBoltUserKit.getUserId());
				product.set("accq_time", DateUtil.getDateString(DateUtil.YMDHMS));
			}

			product.set("report_id", qareport.getId());
			product.set("vd", 1);
			prodsuccess = product.save();
		}

		return ret(prodsuccess);
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
				+ "  sp.modle AS sp_modle,\n" + "  sp.number AS sp_number,\n" + "  sp.type AS sp_type,\n"
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
				+ "  AND d_pdfver.sn COLLATE utf8mb4_general_ci = CAST(sp.pdfver AS CHAR)\r\n"
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
	 * 更新报告单和产品数据
	 * <p>同时更新报告单信息和产品信息，根据检验进度设置对应的检验人员</p>
	 * @param qareport 报告单对象
	 * @param product 产品对象
	 * @return 操作结果
	 */
	public Ret update(Qareport qareport, Product product) {
		if (qareport == null || notOk(qareport.getId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}

		// 更新时需要判断数据存在
		Qareport dbqareport = dao.findById(qareport.getId());
		if (dbqareport == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}

		boolean qasuccess = qareport.update();
		if (!qasuccess) {
			return fail("报告单更新失败，请联系开发人员！");
			// 添加日志
			// addUpdateSystemLog(qareport.getId(), JBoltUserKit.getUserId(), qareport.get);
		}
		
		if (product.getQsi() < product.getQi()) {	
			return fail("送检数量小于检验数量，重新输入！");
		}

		// 根据检验进度设置对应的检验人员和时间
		if (product.getInsp() == 2) {
			// 精度检验
			product.set("accq_uid", JBoltUserKit.getUserId());
			product.set("accq_time", DateUtil.getDateString(DateUtil.YMDHMS));
		}

		if (product.getInsp() == 3) {
			// 功能检验
			product.set("funq_uid", JBoltUserKit.getUserId());
			product.set("funq_time", DateUtil.getDateString(DateUtil.YMDHMS));
		}

		if (product.getInsp() == 4) {
			// 批准检验
			product.set("appq_uid", JBoltUserKit.getUserId());
			product.set("appq_time", DateUtil.getDateString(DateUtil.YMDHMS));
		}

		if (product.getInsp() == 5) {
			// 最终放行
			product.set("allq_uid", JBoltUserKit.getUserId());
			product.set("allq_time", DateUtil.getDateString(DateUtil.YMDHMS));
		}

		return ret(product.update());
	}
	

	/**
	 * 生成检验报告单编号
	 * <p>编号规则：年月(YYYYMM) + 当月序号(3位)</p>
	 * <p>示例：202512001 表示2025年12月第1份报告单</p>
	 * @return 报告单编号
	 */
	public Long creatFormnum() {
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		long fornum = year * 100L + month;

		String sql = "SELECT COUNT(sq.id) FROM `siargo_qareport` sq WHERE DATE_FORMAT(sq.create_time, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')";

		return fornum * 1000 + Db.queryLong(sql) + 1;
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
				.select("sp.id AS spid", "sq.order_id", "sc.name AS sc_name",
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
						+ "AND d_type.enable = '1 '")
				.eq("sp.vd", 0);

		sql.like("sq.order_id", keywords);
		sql.orderBy("sp.delete_time", true);

		return paginateRecord(sql, true);
	}

	/**
	 * 逻辑删除报告单
	 * <p>将产品记录标记为已删除（vd=0），记录删除时间</p>
	 * @param id 产品ID
	 * @return 操作结果
	 */
	public Ret delete(Long id) {
		Product product = new Product().findById(id);

		product.set("delete_time",DateUtil.getDateString(DateUtil.YMDHMS));
		product.set("vd", 0);

		return ret(product.save());
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
	 * 记录删除操作的系统日志（供Controller调用）
	 * @param id 被删除记录的ID
	 * @param userId 操作用户ID
	 * @param name 日志描述信息
	 */
	public void logDelete(Object id, Long userId, String name) {
		addDeleteSystemLog(id, userId, name);
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
		if (proType>0) {
			sql += " AND sp.type = " + proType;
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
		if (proType>0) {
			sql += "AND sp.type = " + proType;
		}
		return Db.queryLong(sql);
	}
	
	/**
	 * 获取每月返修品送检数量统计数据
	 * <p>用于生成首页图表，展示全年各月的返修品数量趋势</p>
	 * <p>SQL说明：按月份分组统计rep_type=2（返修品）的送检数量</p>
	 * @return 1-12月的返修品数量数据列表
	 */
	public List<Map<String, Object>> getRepData() {
	    String sql = "SELECT "
	    		+ "  MONTH(sq.create_time) AS MONTH, "
	    		+ "  SUM(sp.qsi) AS qsi_reTotal "
	    		+ "FROM "
	    		+ "  siargo_product sp "
	    		+ "  INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
	    		+ "WHERE "
	    		+ "  YEAR(sq.create_time) = YEAR(CURDATE()) "
	    		+ "  AND sp.vd = 1 AND sq.rep_type = 2  "
	    		+ "GROUP BY "
	    		+ "  MONTH(sq.create_time) "
	    		+ "ORDER BY "
	    		+ "  MONTH(sq.create_time) ";

	    List<Record> records = Db.find(sql);
	    
	    // 转换为月份为key的Map
	    Map<Integer, Integer> monthData = new LinkedHashMap<>();
	    for (Record record : records) {
	        monthData.put(record.getInt("MONTH"), record.getInt("qsi_reTotal"));
	    }
	    
	    // 生成1-12月的完整数据，使用LinkedHashMap保持特定顺序
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (int month = 1; month <= 12; month++) {
	    	String monthKey = String.format("%s-%02d", String.valueOf(java.time.Year.now().getValue()), month);
	        Map<String, Object> item = new LinkedHashMap<>();
	        item.put("x", monthKey); 
	        item.put("a", monthData.getOrDefault(month, 0)); 
	        result.add(item);
	    }
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
				// 选择字段：报告单基础信息
				.select("sq.id", "sq.order_id", "sc.name AS sc_name", "sq.formnum", "sp.insp",
						// 检验时间信息
						"sp.accq_time", "sp.funq_time", "sp.appq_time", "sp.allq_time",
						// 检验人员姓名
						"accq_user.name AS accq_name", "funq_user.name AS funq_name", "appq_user.name AS appq_name",
						"allq_user.name AS allq_name", "DATE_FORMAT(sq.create_time, '%Y-%m-%d %H:%i') as create_time",
						// 产品信息字段
						"sp.id as spid", "sp.modle as sp_modle", "sp.number as sp_number", "sp.type as sp_type",
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
						+ "AND d_type.enable = '1 '")
				// ========== 关联字典表获取报告类型名称 ==========
				.leftJoin("jb_dictionary", "d_retype", "d_retype.type_key = 'siargo_rep_type' "
						+ "AND d_retype.sn COLLATE utf8mb4_general_ci = CAST(sq.rep_type AS CHAR) "
						+ "AND d_retype.enable = '1 '")
				// ========== 关联字典表获取检验进度名称 ==========
				.leftJoin("jb_dictionary", "d_insp", "d_insp.type_key = 'siargo_insp' "
						+ "AND d_insp.sn COLLATE utf8mb4_general_ci = CAST(sp.insp AS CHAR) "
						+ "AND d_insp.enable = '1 '")
				// ========== 关联字典表获取PDF版本名称 ==========
				.leftJoin("jb_dictionary", "d_pdfver", "d_pdfver.type_key = 'siargo_pdfver' "
						+ "AND d_pdfver.sn COLLATE utf8mb4_general_ci = CAST(sp.pdfver AS CHAR) "
						+ "AND d_pdfver.enable = '1 '")
				// ========== 关联字典表获取流量范围名称 ==========
				.leftJoin("jb_dictionary", "d_flow", "d_flow.type_key = 'siargo_flow_range' "
						+ "AND d_flow.sn COLLATE utf8mb4_general_ci = sp.flow_range "
						+ "AND d_flow.enable = '1 '")
				// ========== 关联用户表获取各阶段检验人员信息 ==========
				.leftJoin("jb_user", "accq_user", "accq_user.id = sp.accq_uid")
				.leftJoin("jb_user", "funq_user", "funq_user.id = sp.funq_uid")
				.leftJoin("jb_user", "appq_user", "appq_user.id = sp.appq_uid")
				.leftJoin("jb_user", "allq_user", "allq_user.id = sp.allq_uid")
				// ========== 查询回收站数据（vd=0）==========
				.eq("sp.vd", 0);
	
		// ========== 应用搜索条件 ==========
		sql.like("sq.order_id", keywords);
				
		sql.orderBy("sp.delete_time", true);
				
		return paginateRecord(sql, true);
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
	        if (entry.getKey() == 1) {
	        	item.put("label", "传感器" ); 
			}
	        if (entry.getKey() == 2) {
	        	item.put("label", "小流量" ); 
			}
	        if (entry.getKey() == 3) {
	        	item.put("label", "大流量" ); 
			} 
	        item.put("value", entry.getValue()); 
	        result.add(item);
	    }
	    return result;
	}

}