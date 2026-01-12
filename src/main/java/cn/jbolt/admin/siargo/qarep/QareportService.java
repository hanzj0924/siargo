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
import cn.jbolt.admin.siargo.siargoutil.SiargoUtil;
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
	private final Qareport dao = new Qareport().dao();

	@Override
	protected Qareport dao() {
		return dao;
	}

	/**
	 * 后台管理分页查询
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param prodType
	 * @param insp
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Page<Record> paginateAdminDatas(int pageNumber, int pageSize, String keywords, int prodType, int insp, Date startTime, Date endTime) {
		Sql sql = Sql.mysql()
				.select("sq.id", "sq.order_id", "sc.name sc_name", "sq.formnum", "sp.type AS prod_type", "sq.rep_type",
						"sp.insp", "sp.accq_time", "sp.funq_time", "sp.appq_time", "sp.allq_time",
						"accq_user.name AS accq_name", "funq_user.name AS funq_name", "appq_user.name AS appq_name",
						"allq_user.name AS allq_name", "DATE_FORMAT(sq.create_time, '%Y-%m-%d %H:%i') as create_time",
						"sp.id as spid", "sp.modle as sp_modle", "sp.number as sp_number", "sp.type as sp_type",
						"sp.qsi as sp_qsi", "sp.qi as sp_qi", "sp.flow_range as sp_flow_range", "sp.des as sp_des", 
						"sp.pdfstr AS sp_pdfstr", "CONCAT(sp.pdfstr, '?t=', UNIX_TIMESTAMP()) as sp_pdfstrtime", 
						"sp.pdfver AS sp_pdfver","sp.cuc as sp_cuc", "sp.pv as sp_pv", "sp.thv as sp_thv", 
						"sp.zp as sp_zp", "sp.fl as sp_fl", "sp.cucmax as sp_cucmax", "sp.cucmin as sp_cucmin",
						"sp.bv as sp_bv", "sp.la as sp_la"
						)
				.page(pageNumber, pageSize).from("siargo_qareport", "sq")
				.leftJoin("siargo_product", "sp", "sq.id = sp.report_id")
				.leftJoin("siargo_customer", "sc", "sc.id = sq.cust_id")
				.leftJoin("jb_user", "accq_user", "accq_user.id = sp.accq_uid")
				.leftJoin("jb_user", "funq_user", "funq_user.id = sp.funq_uid")
				.leftJoin("jb_user", "appq_user", "appq_user.id = sp.appq_uid")
				.leftJoin("jb_user", "allq_user", "allq_user.id = sp.allq_uid").eq("sp.vd", 1);

		sql.like("order_id", keywords);

		if (prodType > 0) {
			sql.eq("sp.type", prodType);
		}
		if (insp > 0) {
			sql.eq("sp.insp", insp);
		}
		
		if (isOk(startTime) && isOk(endTime)) {
			sql.bwDate("create_time",startTime,endTime);
		}


		sql.orderBy("sq.create_time", true);

		return paginateRecord(sql, true);
	}

	/**
	 * 保存
	 * 
	 * @param qareport
	 * @param product
	 * @return
	 */
	public Ret save(Qareport qareport, Product product) {
		if (qareport == null) {
			return fail(JBoltMsg.PARAM_ERROR);
		}

		if (product.getInsp() > 2) {
			return fail("未检验精度，请重新选择检验进度！");
		}

		if (notOk(qareport.getId())) {

			// 保存到报告单表
			qareport.set("create_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
			qareport.set("formnum", creatFormnum());

			qareport.save();
		}

		boolean prodsuccess = false;
		if (notOk(product.getId())) {
			Product pro = new Product();

			// 增加精度检验数据
			if (product.getInsp() == 2) {
				pro.set("accq_uid", JBoltUserKit.getUserId());
				pro.set("accq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
			}
			
			pro.set("insp", product.getInsp());
			pro.set("type", product.getType());
			pro.set("modle", product.getModle());
			pro.set("qsi", product.getQsi());
			pro.set("qi", product.getQi());
			pro.set("number", product.getNumber());
			pro.set("flow_range", product.getFlowRange());
			pro.set("des", product.getDes());
			pro.set("pdfver", product.getPdfver());
			pro.set("report_id", qareport.getId());
			pro.set("cuc", product.getCuc());
			pro.set("cucmax", product.getCucmax());
			pro.set("cucmin", product.getCucmin());
			pro.set("pv", product.getPv());
			pro.set("thv", product.getThv());
			pro.set("zp", product.getZp());
			pro.set("fl", product.getFl());
			pro.set("bv", product.getBv());
			pro.set("la", product.getLa());
			pro.set("vd", 1);
			prodsuccess = pro.save();

		} else {
			// 增加精度检验数据
			if (product.getInsp() == 2) {
				product.set("accq_uid", JBoltUserKit.getUserId());
				product.set("accq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
			}

			product.set("report_id", qareport.getId());
			product.set("vd", 1);
			prodsuccess = product.save();
		}

		return ret(prodsuccess);
	}

	
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
				+ "  sp.cucmax AS sp_cucmax,\n" + "  sp.cucmin AS sp_cucmin,\n"+ "  sd.`VALUE` AS sp_flow_range_name,\n "
				+ "	 sp.bv AS sp_bv,\n"+ "  sp.la AS sp_la\n" 
				+ "  FROM\n" + "  `siargo_qareport` sq\n"
				+ "  LEFT JOIN `siargo_product` AS sp ON sq.id = sp.report_id\n"
				+ "  LEFT JOIN `siargo_customer` AS sc ON sc.id = sq.cust_id\n"
				+ "  LEFT JOIN `siargo_dictionary` AS sd ON sd.`KEY` = sp.flow_range AND sd.type_id = 4\n"
				+ "  LEFT JOIN jb_user AS accq_user ON accq_user.id = sp.accq_uid\n"
				+ "  LEFT JOIN jb_user AS funq_user ON funq_user.id = sp.funq_uid\n"
				+ "  LEFT JOIN jb_user AS appq_user ON appq_user.id = sp.appq_uid\n"
				+ "  LEFT JOIN jb_user AS allq_user ON allq_user.id = sp.allq_uid\n" + "WHERE\n" + "  sp.vd = 1\n"
				+ "  AND sp.id = ? ";

		return dao.findFirst(sql, id);
	}

	/**
	 * 更新
	 * 
	 * @param qareport
	 * @return
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

		// 增加精度进度
		if (product.getInsp() == 2) {
			product.set("accq_uid", JBoltUserKit.getUserId());
			product.set("accq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		}

		if (product.getInsp() == 3) {
			product.set("funq_uid", JBoltUserKit.getUserId());
			product.set("funq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		}

		if (product.getInsp() == 4) {
			product.set("appq_uid", JBoltUserKit.getUserId());
			product.set("appq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		}

		if (product.getInsp() == 5) {
			product.set("allq_uid", JBoltUserKit.getUserId());
			product.set("allq_time", SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
		}

		return ret(product.update());
	}
	

	/**
	 * 获取检验报告单编号
	 * 
	 * @param qareport
	 * @return
	 */
	public Long creatFormnum() {
		LocalDate now = LocalDate.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		long fornum = year * 100L + month;

		String sql = "SELECT COUNT(*) FROM `siargo_qareport` sq WHERE DATE_FORMAT(sq.create_time, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')";

		return fornum * 1000 + Db.queryLong(sql) + 1;
	}


	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public Ret delete(Long id) {
		Product product = new Product().findById(id);

		product.set("delete_time",SiargoUtil.getDateString(SiargoUtil.PATTERN_DATE_TIME));
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
		return ProjectSystemLogTargetType.NONE.getValue();
	}
	
	/**
	 * 获取送检数量
	 * 
	 * @param proType
	 * @return
	 */
	public Long getTotalQSI(int proType) {
		String sql = "SELECT SUM( sp.qsi ) AS qsi_Total "
				+ "FROM siargo_product sp "
				+ "INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
				+ "WHERE YEAR ( sq.create_time ) = YEAR (CURDATE()) AND MONTH ( sq.create_time ) = MONTH (CURDATE()) "
				+ "AND sp.vd = 1 ";
		if (proType>0) {
			sql += " AND sp.type = " + proType;
		}
		return Db.queryLong(sql);
	}
	
	
	/**
	 * 获取检验数量
	 * 
	 * @param proType
	 * @return
	 */
	public Long getTotalQI(int proType) {
		String sql = "SELECT SUM( sp.qi ) AS qi_Total "
				+ "FROM siargo_product sp "
				+ "INNER JOIN siargo_qareport sq ON sp.report_id = sq.id "
				+ "WHERE YEAR ( sq.create_time ) = YEAR (CURDATE()) AND MONTH ( sq.create_time ) = MONTH (CURDATE())  "
				+ "AND sp.vd = 1 ";
		if (proType>0) {
			sql += "AND sp.type = " + proType;
		}
		return Db.queryLong(sql);
	}
	
	/**
	* 获取返修单数量
	 * @return
	 */
	
	public List<Map<String, Object>> getRepData() {
	    String sql = "SELECT MONTH ( sq.create_time ) AS MONTH, "
	    		+ "COUNT(DISTINCT sp.report_id) AS count "
	    		+ "FROM siargo_qareport sq "
	    		+ "INNER JOIN siargo_product sp ON sp.report_id = sq.id "
	    		+ "WHERE YEAR ( sq.create_time ) = YEAR (CURDATE()) "
	    		+ "AND sp.vd = 1 AND sq.rep_type = 2 "
	    		+ "GROUP BY MONTH "
	    		+ "ORDER BY MONTH " ; 

	    List<Record> records = Db.find(sql);
	    
	    // 转换为月份为key的Map
	    Map<Integer, Integer> monthData = new LinkedHashMap<>();
	    for (Record record : records) {
	        monthData.put(record.getInt("MONTH"), record.getInt("count"));
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
	 * 获取报告单数量
	 * @param repType
	 * @return
	 */
	
	public List<Map<String, Object>> getRepAllData() {
	    String sql = "SELECT MONTH ( sq.create_time ) AS MONTH, COUNT(DISTINCT sp.report_id) AS count "
	    		+ "FROM siargo_qareport sq "
	    		+ "INNER JOIN siargo_product sp ON sp.report_id = sq.id "
	    		+ "WHERE YEAR ( sq.create_time ) = YEAR (CURDATE()) "
	    		+ "AND sp.vd = 1 GROUP BY MONTH ORDER BY MONTH";
	    		
	    List<Record> records = Db.find(sql);
	    
	    Map<Integer, Integer> monthData = new LinkedHashMap<>();
	    for (Record record : records) {
	        monthData.put(record.getInt("MONTH"), record.getInt("count"));
	    }
	    
	    List<Map<String, Object>> result = new ArrayList<>();
	    for (int month = 1; month <= 12; month++) {
	        Map<String, Object> item = new LinkedHashMap<>();
	        item.put("y", month + "月");  
	        item.put("a", monthData.getOrDefault(month, 0)); 
	        result.add(item);
	    }
	    return result;
	}
	
	/**
	 * 获取订单来源
	 * @param repType
	 * @return
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