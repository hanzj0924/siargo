package cn.jbolt.admin.siargo.qarep.product;

import cn.jbolt.common.storage.SiargoStorage;
import cn.jbolt.core.base.config.JBoltConfig;
import com.jfinal.kit.PathKit;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.jbolt.extend.systemlog.ProjectSystemLogTargetType;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import cn.jbolt.siargo.model.Product;
import cn.jbolt.admin.siargo.qarep.siargoconst.QarepConst;
import cn.jbolt.core.kit.JBoltSnowflakeKit;
import cn.jbolt.core.kit.JBoltUserKit;
import java.util.ArrayList;
import java.util.Date;
/**
 * 检验报告单管理 Service
 * @ClassName: ProductService   
 * @author: hanzj
 * @date: 2025-12-16 17:06  
 */
public class ProductService extends JBoltBaseService<Product> {
	/** 产品数据访问对象 */
	private final Product dao=new Product().dao();

    /** 参与报告单外层事务，单个卡片独立构造产品，禁止客户端控制签名与记录 ID。 */
    public Ret saveReportProduct(Product input, Long reportId) {
        if (input == null || input.getLong("id") != null || reportId == null) return fail("产品保存参数错误");
        Product product = new Product();
        for (String field : ReportProductInput.EDITABLE_FIELDS) product.set(field, input.get(field));
        product.set("id", JBoltSnowflakeKit.me.nextId());
        product.set("report_id", reportId);
        product.set("vd", QarepConst.VD_VALID).set("reject_count", 0);
        Integer insp = input.getInt("insp");
        if (Integer.valueOf(QarepConst.INSP_PENDING_APPEARANCE).equals(insp)) {
            product.set("accq_uid", JBoltUserKit.getUserId()).set("accq_time", new Date());
            if (Integer.valueOf(QarepConst.LT_STATUS_YES).equals(input.getInt("lt_status"))) {
                insp = QarepConst.INSP_PENDING_LEAK_TEST;
            }
        }
        product.set("insp", insp);
        if (!product.save()) return fail("产品记录保存失败");
        input.set("id", product.getLong("id"));
        return Ret.ok();
    }

    /** 编辑与审批并发时锁定产品，在事务结束前保持所属报告及状态一致。 */
    public Product findForUpdate(Long id) {
        return id == null ? null : dao.findFirst("SELECT * FROM siargo_product WHERE id=? FOR UPDATE", id);
    }

    /** 删除系列前使用当前读，不能复用批量事务此前建立的查询快照。 */
    public boolean hasSeriesReferencesForUpdate(Long seriesId) {
        return com.jfinal.plugin.activerecord.Db.queryLong(
                "SELECT id FROM siargo_product WHERE siargo_prod_model_id=? LIMIT 1 FOR UPDATE", seriesId) != null;
    }

    /** 公共报告信息变更后，同报告所有产品的现有 PDF 均失效；调用方持有报告头锁。 */
    public List<String> invalidateReportPdfs(Long reportId) {
        List<String> urls = new ArrayList<>();
        for (Product product : dao.find("SELECT id,pdfstr FROM siargo_product WHERE report_id=? ORDER BY id FOR UPDATE", reportId)) {
            String url = product.getStr("pdfstr");
            if (url != null && !url.isBlank() && !urls.contains(url)) urls.add(url);
        }
        com.jfinal.plugin.activerecord.Db.update("UPDATE siargo_product SET pdfstr=NULL WHERE report_id=?", reportId);
        return urls;
    }

    /** 编辑公共报告信息前先按产品ID锁完整报告，避免先锁当前产品再反向等待其他产品。 */
    public List<Product> findReportProductsForUpdate(Long reportId) {
        return dao.find("SELECT * FROM siargo_product WHERE report_id=? ORDER BY id FOR UPDATE", reportId);
    }

    /**
     * 删除前先锁涉及报告的全部产品，避免删后检查剩余产品时反向取得较小ID的锁。
     * 调用方先锁报告头；现有新增仅创建新报告，编辑不允许移动产品所属报告。
     * 候选查询可能包含刚被其他事务删掉的旧ID，逐ID当前读会跳过；请求ID始终纳入以覆盖孤儿记录。
     */
    public List<Product> findDeletionScopeForUpdate(java.util.Collection<Long> reportIds, java.util.Collection<Long> requestedIds) {
        java.util.SortedSet<Long> scope = new java.util.TreeSet<>(requestedIds);
        if (!reportIds.isEmpty()) scope.addAll(findReportProductIds(reportIds));
        List<Product> products = new ArrayList<>();
        for (Long id : scope) {
            Product product = findForUpdate(id);
            if (product != null) products.add(product);
        }
        return products;
    }

    protected List<Long> findReportProductIds(java.util.Collection<Long> reportIds) {
        String placeholders = String.join(",", java.util.Collections.nCopies(reportIds.size(), "?"));
        return dao.find("SELECT id FROM siargo_product WHERE report_id IN (" + placeholders + ")", reportIds.toArray())
                .stream().map(product -> product.getLong("id")).toList();
    }

    /** 当前读检查剩余产品，不能复用等待报告头锁之前建立的可重复读快照。 */
    public boolean hasReportProductsForUpdate(Long reportId) {
        return com.jfinal.plugin.activerecord.Db.queryLong(
                "SELECT id FROM siargo_product WHERE report_id=? ORDER BY id LIMIT 1 FOR UPDATE", reportId) != null;
    }

    /** 已由报告服务锁定并校验完整生成快照，只参与调用方事务。 */
    public boolean publishPdfInTransaction(Long id, String expected, String value) {
        return com.jfinal.plugin.activerecord.Db.update(
                "UPDATE siargo_product SET pdfstr=? WHERE id=? AND pdfstr <=> ? AND vd=1 AND insp=?",
                value, id, expected, QarepConst.INSP_COMPLETED) == 1;
    }

    public boolean updateReportProduct(Product product) {
        return product != null && product.update();
    }

    public boolean deleteReportProduct(Product product) {
        return product != null && product.delete();
    }

    /** 提交后清理，多个记录共用历史文件时不误删。 */
    public void deleteUnreferencedPdf(String url) throws java.io.IOException {
        if (com.jfinal.plugin.activerecord.Db.queryLong(
                "SELECT COUNT(*) FROM siargo_product WHERE pdfstr=?", url) > 0) return;
        SiargoStorage storage = oldPdfStorage(url);
        storage.deleteFile(storage.resolveUrl(url));
    }

    /** 新报告发布成功后清理旧文件；返回提示原因，不将清理失败升级为生成失败。 */
    public String cleanupOldPdfAfterPublication(String url) {
        try {
            SiargoStorage storage = oldPdfStorage(url);
            Path file = storage.resolveUrl(url);
            if (Files.notExists(file, LinkOption.NOFOLLOW_LINKS)) return "旧文件不存在";
            if (hasPdfReferences(url)) return null;
            return storage.deleteFileIfExists(file) ? null : "旧文件不存在";
        } catch (Exception error) {
            return error.getMessage() == null ? "旧文件删除失败" : error.getMessage();
        }
    }

    protected boolean hasPdfReferences(String url) {
        String relative = url.startsWith("/") ? url.substring(1) : url;
        return com.jfinal.plugin.activerecord.Db.queryLong(
                "SELECT COUNT(*) FROM siargo_product WHERE pdfstr=? OR pdfstr=?", relative, "/" + relative) > 0;
    }

    private SiargoStorage oldPdfStorage(String url) {
        String relative = url != null && url.startsWith("/") ? url.substring(1) : url;
        if (relative == null || !relative.toLowerCase(Locale.ROOT).endsWith(".pdf"))
            throw new IllegalArgumentException("旧文件不是有效的PDF地址");
        // 仅兼容已存在的历史报告目录，继续复用规范路径、目录联接及业务隔离校验。
        for (String legacyRoot : List.of("export/PDF/G2", "export/G/2", "export/siargo/qarep/reports", "upload/siargo/qarep/reports")) {
            if (!relative.startsWith(legacyRoot + "/")) continue;
            Map<SiargoStorage.Business, String> paths = new EnumMap<>(SiargoStorage.Business.class);
            for (SiargoStorage.Business business : SiargoStorage.Business.values())
                paths.put(business, JBoltConfig.prop.get(business.configKey()));
            paths.put(SiargoStorage.Business.QAREP, legacyRoot);
            return new SiargoStorage(Path.of(PathKit.getWebRootPath()), paths, SiargoStorage.Business.QAREP);
        }
        return SiargoStorage.forBusiness(SiargoStorage.Business.QAREP);
    }
	@Override
	protected Product dao() {
		return dao;
	}
	
	/**
	 * 根据报告单ID查询该报告单下的全部有效产品信息（含字典翻译、各环节检验人姓名、各环节驳回计数、最新驳回记录）
	 * <p>从 QareportService 收敛而来：Product 相关查询统一走本 Service</p>
	 * <p>驳回数据分两部分：</p>
	 * <ul>
	 *   <li>reject_count_2/3/4：各环节历史驳回次数（>0 时前端显示"驳"角标）</li>
	 *   <li>reject_insp/reject_des/reject_time/reject_name/reject_insp_name：最新一条驳回记录（用于当前驳回状态节点展示）</li>
	 * </ul>
	 * @param reportId 报告单ID
	 * @return 产品列表
	 */
	public List<Product> findProductsByReportId(Long reportId) {
		String sql = "SELECT sp.*, "
			+ ProductSeriesService.typeNameSql("pm", "d_type") + " AS type_name, pm.prod_type AS prod_type, pm.model_series, pm.prod_type prodType, " + ProductSeriesService.typeNameSql("pm", "d_type") + " prodTypeName, d_flow.name flow_name, "
			+ "d_insp.NAME AS insp_name, "
			+ "accq_user.NAME AS accq_name, "
			+ "funq_user.NAME AS funq_name, "
			+ "lt_user.NAME AS lt_name, "
			+ "appq_user.NAME AS appq_name, "
			+ "allq_user.NAME AS allq_name, "
			// 各环节驳回历史计数（用于角标显示）
			+ "(SELECT COUNT(*) FROM siargo_product_reject_log r2 WHERE r2.product_id = sp.id AND r2.reject_insp = 2) AS reject_count_2, "
			+ "(SELECT COUNT(*) FROM siargo_product_reject_log r3 WHERE r3.product_id = sp.id AND r3.reject_insp = 3) AS reject_count_3, "
			+ "(SELECT COUNT(*) FROM siargo_product_reject_log r4 WHERE r4.product_id = sp.id AND r4.reject_insp = 4) AS reject_count_4, "
			+ "(SELECT COUNT(*) FROM siargo_product_reject_log r6 WHERE r6.product_id = sp.id AND r6.reject_insp = 6) AS reject_count_6, "
			// 最新一条驳回记录（用于当前驳回状态节点详情）
			+ "rl.reject_insp, rl.reject_des, "
			+ "DATE_FORMAT(rl.reject_time, '%Y-%m-%d %H:%i') AS reject_time, "
			+ "reject_user.NAME AS reject_name, "
			+ "CASE rl.reject_insp WHEN 2 THEN '外观检验' WHEN 3 THEN '包装检验' WHEN 4 THEN '批准' WHEN 6 THEN '成品检漏检验' ELSE '未知环节' END AS reject_insp_name "
			+ "FROM siargo_product sp LEFT JOIN siargo_prod_model pm ON pm.id=sp.siargo_prod_model_id "
			+ "LEFT JOIN " + ProductSeriesService.TYPE_LABELS_SQL + " AS d_type ON d_type.type_key = 'siargo_prod_type' "
			+ "AND d_type.sn COLLATE utf8mb4_general_ci = CAST(pm.prod_type AS CHAR) "
			+ "AND d_type.enable = '1' "
			+ "LEFT JOIN jb_dictionary AS d_insp ON d_insp.type_key = 'siargo_insp' "
			+ "AND d_insp.sn COLLATE utf8mb4_general_ci = CAST(sp.insp AS CHAR) "
			+ "AND d_insp.enable = '1' "
			+ "LEFT JOIN jb_dictionary d_flow ON d_flow.type_key='siargo_flow_range' AND d_flow.sn COLLATE utf8mb4_general_ci=sp.flow_range AND d_flow.enable='1' "
			+ "LEFT JOIN jb_user AS accq_user ON accq_user.id = sp.accq_uid "
			+ "LEFT JOIN jb_user AS funq_user ON funq_user.id = sp.funq_uid "
			+ "LEFT JOIN jb_user AS lt_user ON lt_user.id = sp.lt_uid "
			+ "LEFT JOIN jb_user AS appq_user ON appq_user.id = sp.appq_uid "
			+ "LEFT JOIN jb_user AS allq_user ON allq_user.id = sp.allq_uid "
			// 关联最新一条驳回日志（按 id DESC 取第一条）
			+ "LEFT JOIN siargo_product_reject_log AS rl ON rl.id = "
			+ "(SELECT rl2.id FROM siargo_product_reject_log rl2 WHERE rl2.product_id = sp.id ORDER BY rl2.id DESC LIMIT 1) "
			+ "LEFT JOIN jb_user AS reject_user ON reject_user.id = rl.reject_uid "
			+ "WHERE sp.report_id = ? AND sp.vd = 1 "
			+ "ORDER BY sp.id ASC";
		return dao.find(sql, reportId);
	}
		
	/**
	 * 检测产品是否可以被删除
	 * <p>检查产品是否被其他数据引用，如果被引用则阻止删除</p>
	 * @param product 要删除的model
	 * @param kv 携带额外参数一般用不上
	 * @return 返回null表示可以删除，返回错误信息则阻止删除
	 */
	@Override
	public String checkCanDelete(Product product, Kv kv) {
		//如果检测被用了 返回信息 则阻止删除 如果返回null 则正常执行删除
		return checkInUse(product, kv);
	}
	
	/**
	 * 设置系统日志的目标类型
	 * <p>用于标识二开业务所属的日志类型，当前返回NONE表示不记录系统日志</p>
	 * @return 日志目标类型
	 */
	@Override
	protected int systemLogTargetType() {
		return ProjectSystemLogTargetType.NONE.getValue();
	}
	
}
