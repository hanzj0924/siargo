package cn.jbolt._admin.codegen.modelattr;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.jbolt._admin.codegen.CodeGenService;
import cn.jbolt.common.model.CodeGen;
import cn.jbolt.common.model.CodeGenModelAttr;
import cn.jbolt.core.base.JBoltIDGenMode;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.db.datasource.JBoltTableMetaUtil;
import cn.jbolt.core.db.sql.DBType;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.enjoy.directive.ColumnTypeToDirective;
import cn.jbolt.core.para.JBoltPara;
import cn.jbolt.core.service.base.JBoltBaseService;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CodeGenModelAttrService extends JBoltBaseService<CodeGenModelAttr> {
	private final CodeGenModelAttr dao = new CodeGenModelAttr().dao();
	@Inject
	CodeGenService codeGenService;
	@Override
	protected CodeGenModelAttr dao() {
		return dao;
	}

	/**
	 * 获取指定codeGen的attrs
	 * @param codeGenId
	 * @param sortColumn
	 * @param desc
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenModelAttrs(Long codeGenId,String sortColumn,boolean desc) {
		if(notOk(codeGenId)) {return new ArrayList<>();}
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			throw new RuntimeException("未找到代码生成的基础配置");
		}
		return find(selectSql().eq("code_gen_id", codeGenId).orderBy(sortColumn,desc));
	}

	/**
	 * 为指定的主表生成modelAttrs
	 * @param codeGenId
	 */
	public boolean genMainTableAttrs(Long codeGenId) {
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			throw new RuntimeException("未找到代码生成的基础配置");
		}
		return genMainTableAttrs(codeGen);
	}

	/**
	 * 为指定的主表生成modelAttrs
	 * @param codeGen
	 */
	public boolean genMainTableAttrs(CodeGen codeGen) {
		if(codeGen == null) {
			throw new RuntimeException("未找到代码生成的基础配置");
		}
		TableMeta tableMeta = JBoltTableMetaUtil.getTableMeta(codeGen.getDatasourceName(), codeGen.getDatabaseType(), codeGen.getMainTableName(),false);
		if(tableMeta!=null) {
			codeGen.setMainTableRemark(tableMeta.remarks);
			codeGen.setMainTablePkey(tableMeta.primaryKey);
			if(notOk(codeGen.getTableDefaultSortColumn()) || (codeGen.getTableDefaultSortColumn().equalsIgnoreCase(ID) && tableMeta.primaryKey.equalsIgnoreCase(ID))){
				codeGen.setTableDefaultSortColumn(tableMeta.primaryKey);
			}
			List<ColumnMeta> columnMetas = tableMeta.columnMetas;
			if(isOk(columnMetas)) {
				List<CodeGenModelAttr> attrs = new ArrayList<>();
				int len = columnMetas.size();
				ColumnMeta columnMeta;
				CodeGenModelAttr attrTemp;
				for(int i=0;i<len;i++) {
					columnMeta = columnMetas.get(i);
					attrTemp = convertModelAttr((i+1),codeGen.getId(),codeGen.getIsEditable(),codeGen.getIsCrud(),columnMeta);
					if(attrTemp.getIsPkey()){
						//设置主键为默认排序列
						codeGen.setTableDefaultSortColumn(attrTemp.getColName());
						codeGen.setTableDefaultSortType("desc");
						if(columnMeta.javaType.equalsIgnoreCase("java.lang.integer")) {
							switch (codeGen.getDatabaseType()){
								case DBType.MYSQL:
									codeGen.setMainTableIdgenmode(JBoltIDGenMode.AUTO);
									break;
								case DBType.SQLSERVER:
									codeGen.setMainTableIdgenmode(JBoltIDGenMode.AUTO);
									break;
								case DBType.ORACLE:
									codeGen.setMainTableIdgenmode(JBoltIDGenMode.SEQUENCE);
									break;
								case DBType.POSTGRESQL:
									codeGen.setMainTableIdgenmode(JBoltIDGenMode.SERIAL);
									break;
								case DBType.DM:
									codeGen.setMainTableIdgenmode(JBoltIDGenMode.AUTO);
									break;
								default:
									codeGen.setMainTableIdgenmode(JBoltIDGenMode.AUTO);
									break;
							}
						}
					}
					attrs.add(attrTemp);
					if(columnMeta.name.equalsIgnoreCase("sort_rank") || columnMeta.name.toLowerCase().contains("sort_rank")) {
						codeGen.setIsShowOptcolSort(true);
						codeGen.setTableDefaultSortColumn(columnMeta.name);
						codeGen.setTableDefaultSortType("asc");
						codeGen.setIsPageTitleInitRankBtn(true);
						codeGen.setTableOptcolWidth(120);
					}
				}
				if(attrs.size()>0) {
					for(CodeGenModelAttr attr:attrs){
						attr.save();
					}
					codeGen.update();
				}
			}
			return true;
		}
		return false;
	}

	private CodeGenModelAttr convertModelAttr(int sortRank,Long codeGenId,boolean isEditable,boolean isCrud,ColumnMeta col) {
		CodeGenModelAttr attr = new CodeGenModelAttr();
		attr.setCodeGenId(codeGenId);//所属
		//是不是主键
		attr.setIsPkey(col.isPrimaryKey.equalsIgnoreCase("pri"));
		//是不是必填
		attr.setIsRequired(!col.isNullable.equalsIgnoreCase("yes"));
		//数据库列名
		attr.setColName(col.name);
		//java属性名
		attr.setAttrName(col.attrName);
		//java类型
		attr.setJavaType(col.javaType);
		//属性备注
		attr.setRemark(StrKit.defaultIfBlank(col.remarks, col.attrName));
		if(StrKit.notBlank(col.defaultValue)) {
			if(col.javaType.equalsIgnoreCase("java.lang.boolean")) {
				boolean isTrue = col.defaultValue.equalsIgnoreCase("true") || col.defaultValue.equalsIgnoreCase(TRUE);
				//属性默认值
				attr.setAttrDefaultValue(isTrue?TRUE:FALSE);
				attr.setFormDefaultValue(isTrue?TRUE_STR:FALSE_STR);
			}else {
				//属性默认值
				attr.setAttrDefaultValue(col.defaultValue);
				attr.setFormDefaultValue(col.defaultValue);
			}
		}
		attr.setFormLabel(attr.getRemark());
		attr.setTableLabel(attr.getRemark());
		attr.setSortRank(sortRank);
		attr.setSortRankIntable(sortRank);
		attr.setSortRankInform(sortRank);
		attr.setSortRankInsearch(sortRank);
		String len = ColumnTypeToDirective.getLength(col.type);
		if(StrKit.isBlank(len)||len.equals("0")) {
			attr.setAttrLength(0);
		}else {
			attr.setAttrLength(Integer.parseInt(len));
		}

		String fixed = ColumnTypeToDirective.getFixed(col.type);
		if(StrKit.isBlank(fixed)||fixed.equals("0")) {
			attr.setAttrFixed(0);
		}else {
			attr.setAttrFixed(Integer.parseInt(fixed));
		}
		if(attr.getJavaType().equals("java.lang.String")) {
			attr.setTableColWidth(150);
			attr.setIsNeedFixedWidth(true);
		}else if(attr.getJavaType().equals("java.util.Date")) {
			attr.setTableColWidth(180);
			attr.setIsNeedFixedWidth(true);
		}else {
			attr.setTableColWidth(100);
			attr.setIsNeedFixedWidth(true);
		}

		String colLow = col.name.toLowerCase();
		//如果是内置的几个字段名字 就不要显示在表格里
		if(colLow.equals("id")||colLow.equals("pid")||colLow.equals("sort_rank")||colLow.equals("create_user_id")||colLow.equals("update_user_id") ||colLow.equals("delete_user_id") || colLow.equals("is_deleted")){
			attr.setIsTableCol(false);
			attr.setIsFormEle(false);
			attr.setIsSearchEle(false);
		}

		if(!colLow.endsWith("_id") && (colLow.contains("type") || colLow.contains("state") || colLow.contains("category"))){
			attr.setSearchUiType("select");
			attr.setSearchDataType("sys_dictionary");
			attr.setSearchDataValueAttr("sn");
			attr.setFormUiType("select");
			attr.setFormDataType("sys_dictionary");
			attr.setFormDataValueAttr("sn");
			if(isEditable){
				attr.setTableUiType("select");
				attr.setTableDataType("sys_dictionary");
				attr.setTableDataValueAttr("sn");
			}
			attr.setIsSearchEle(true);
		}
		if(colLow.endsWith("_id") && !colLow.equals("create_user_id") && !colLow.equals("update_user_id") && !colLow.equals("delete_user_id")){
			attr.setSearchUiType("autocomplete");
			attr.setSearchDataType("url");
			attr.setSearchDataValue("admin/codegen/autocompletedemodatas");
			attr.setFormUiType("autocomplete");
			attr.setFormDataType("url");
			attr.setFormDataValue("admin/codegen/autocompletedemodatas");
			if(isEditable){
				attr.setTableUiType("autocomplete");
				attr.setTableDataType("url");
				attr.setTableDataValue("admin/codegen/autocompletedemodatas");
			}
		}

		if(colLow.equals("create_time") || colLow.equals("update_time") || colLow.equals("delete_time") || colLow.endsWith("_time") || colLow.endsWith("_date")){
			attr.setIsSortable(true);
		}

		if(colLow.equals("create_time") || colLow.equals("update_time") || colLow.equals("delete_time") || (colLow.endsWith("_time") && col.javaType.equalsIgnoreCase("java.util.date"))){
			attr.setColFormat("date_ymdhms");
		}else if((colLow.endsWith("_day") || colLow.endsWith("_date")) && col.javaType.equalsIgnoreCase("java.util.date")){
			attr.setColFormat("date_ymd");
		}else if(col.javaType.equalsIgnoreCase("java.sql.time")){
			attr.setColFormat("time_hm");
		}

		if(!colLow.equals("id")&&!colLow.equals("create_time")&&!colLow.equals("update_time")&&!colLow.equals("create_user_id")&&!colLow.equals("update_user_id") &&!colLow.equals("delete_user_id")&& !colLow.equals("is_deleted")&& !colLow.equals("delete_time")){
			if(isCrud){
				if(col.javaType.equalsIgnoreCase("java.util.date")){
					if(colLow.endsWith("_day") || colLow.endsWith("_date")){
						attr.setFormUiType("laydate_date");
					}else if(colLow.endsWith("_time")){
						attr.setFormUiType("laydate_datetime");
					}else{
						attr.setFormUiType("laydate_date");
					}
				}else if(col.javaType.equalsIgnoreCase("java.sql.time")){
					attr.setFormUiType("laydate_time");
				}else if(col.javaType.equalsIgnoreCase("java.lang.integer") && (colLow.endsWith("_year") || colLow.equals("year"))){
					attr.setFormUiType("laydate_year");
				}else if(colLow.endsWith("_month") || colLow.equals("month")){
					attr.setFormUiType("laydate_month");
				}else if(col.javaType.equalsIgnoreCase("java.lang.string")){
					if(colLow.endsWith("_week") || colLow.equals("week")){
						attr.setFormUiType("input_week");
					}else{
						if(attr.getAttrLength()>100){
							attr.setFormUiType("textarea");
						}else{
							attr.setFormUiType("input");
						}
					}
				}else if(col.javaType.equalsIgnoreCase("java.lang.integer")){
					attr.setFormUiType("number");
				}
			}
			if(isEditable){
				if(col.javaType.equalsIgnoreCase("java.util.date")){
					if(colLow.endsWith("_day") || colLow.endsWith("_date")){
						attr.setTableUiType("date");
					}else if(colLow.endsWith("_time")){
						attr.setTableUiType("datetime");
					}else{
						attr.setTableUiType("date");
					}
				}else if(col.javaType.equalsIgnoreCase("java.sql.time")){
					attr.setTableUiType("time");
				}else if(col.javaType.equalsIgnoreCase("java.lang.integer") && (colLow.endsWith("_year") || colLow.equals("year"))){
					attr.setTableUiType("year");
				}else if(colLow.endsWith("_month") || colLow.equals("month")){
					attr.setTableUiType("month");
				}else if(col.javaType.equalsIgnoreCase("java.lang.string")){
					if((colLow.endsWith("_week") || colLow.equals("week"))){
						attr.setTableUiType("week");
					}else{
						if(attr.getAttrLength()>100){
							attr.setTableUiType("textarea");
						}else{
							attr.setTableUiType("input");
						}
					}
				}else if(col.javaType.equalsIgnoreCase("java.lang.integer")){
					attr.setTableUiType("input_number");
				}
			}
		}else{
			attr.setIsFormEle(false);
		}

		if(colLow.endsWith("name") || colLow.endsWith("remark") || colLow.endsWith("brief") || colLow.endsWith("_info") || colLow.endsWith("_desc") || colLow.endsWith("description")){
			attr.setIsKeywordsColumn(true);
			if(colLow.endsWith("name")){
				if(isCrud){
					attr.setFormUiType("input");
				}
			}else{
				attr.setFormUiType("textarea");
			}
		}
		if(!colLow.equals("is_deleted") && (colLow.endsWith("enable") || colLow.startsWith("is_") || col.javaType.equalsIgnoreCase("java.lang.boolean"))){
			if(isCrud){
				attr.setFormUiType("radio");
				attr.setFormDataType("sys_dictionary");
				attr.setFormDataValue("options_enable");
				attr.setFormDataValueAttr("sn");
				attr.setDataRule("radio");
			}
			attr.setSearchUiType("select");
			attr.setSearchDataType("sys_dictionary");
			if(colLow.endsWith("enable")){
				attr.setSearchDataValue("options_enable");
			}else{
				attr.setSearchDataValue("options_boolean");
			}
			attr.setSearchDataValueAttr("sn");
			attr.setIsSearchEle(true);

			if(isEditable){
				attr.setTableUiType("switchbtn");
				attr.setTableDataType("user_input");
			}
			attr.setIsTableSwitchbtn(true);
		}

		return attr;
	}
	@Override
	public String checkCanToggle(CodeGenModelAttr attr, String column, Kv kv) {
		if(!attr.getCodeGenId().equals(kv.getLong("code_gen_id"))) {
			return JBoltMsg.PARAM_ERROR;
		}
		return null;
	}

	@Override
	protected String afterToggleBoolean(CodeGenModelAttr attr, String column, Kv kv) {
		if(column.equalsIgnoreCase("is_pkey") && attr.getIsPkey()) {
			//如果操作的是isPkey 然后设置为true后 就需要把其他的true的改为false
			changeColumnFalse(column,attr.getId(),updateSql().eq("code_gen_id", attr.getCodeGenId()));
		}else if(column.equalsIgnoreCase("is_keywords_column")) {
			codeGenService.updateKeywordsMatchColumn(attr.getCodeGenId());
		}else if(column.equalsIgnoreCase(CodeGenModelAttr.IS_TABLE_COL)) {
			//如果切换的是isTableCol字段 就要每次重新 设置顺序
			initSortRankIntableByRank(attr.getCodeGenId());
		}
		return null;
	}

	/**
	 * 处理字段按照sortrankIntable 重新排列
	 * @param codeGenId
	 */
	private void initSortRankIntableByRank(Long codeGenId) {
		List<CodeGenModelAttr> attrs = find(selectSql().select("id").eq("code_gen_id", codeGenId).eq("is_table_col", TRUE).asc(CodeGenModelAttr.SORT_RANK_INTABLE));
		if(isOk(attrs)) {
			int total = attrs.size();
			for(int i=0;i<total;i++) {
				attrs.get(i).setSortRankIntable(i+1);
			}
			batchUpdate(attrs);
		}
	}

	/**
	 * 切换指定条件指定属性的boolean
	 * @param column
	 * @param id
	 * @param codeGenId
	 * @return
	 */
	public Ret toggleAttrBoolean(String column, Long id, Long codeGenId) {
		return toggleBoolean(Kv.by("id", id).set("code_gen_id",codeGenId),id, column);
	}

	/**
	 * 获取指定表的keyCache Autocomplete下拉使用的columns
	 * @param codeGenId
	 * @param column
	 * @param limit
	 * @param keywords
	 * @return
	 */
	public List<CodeGenModelAttr> getKeyCacheColumnsAutocomplete(Long codeGenId,String column,int limit, String keywords) {
		if(notOk(codeGenId)) {
			return Collections.emptyList();
		}
		Sql sql = selectSql().eq("code_gen_id", codeGenId);
		sql.likeMulti(keywords, "col_name","attr_name");
		sql.eq("is_pkey", FALSE);
		sql.notEq("col_name", "id");
		sql.notEq("col_name", "ID");
		if(isOk(column)) {
			sql.notEq("col_name", column);
		}
		sql.orderBySortRank();
		sql.firstPage(limit);
		sql.select("col_name","remark","java_type");
		return find(sql);
	}

	/**
	 * 获取指定表的keyCache Autocomplete下拉使用的columns
	 * @param codeGenId
	 * @param column
	 * @param limit
	 * @param keywords
	 * @return
	 */
	public List<CodeGenModelAttr> getOptionsTextIntSortColumnsAutocomplete(Long codeGenId,String column,int limit, String keywords) {
		if(notOk(codeGenId)) {
			return Collections.emptyList();
		}
		Sql sql = selectSql().eq("code_gen_id", codeGenId);
		sql.likeMulti(keywords, "col_name","attr_name");
		sql.eq("is_pkey", FALSE);
		sql.notEq("col_name", "id");
		sql.notEq("col_name", "ID");
		if(isOk(column)) {
			sql.notEq("col_name", column);
		}
		sql.eq(CodeGenModelAttr.JAVA_TYPE, "java.lang.Integer");
		sql.orderBySortRank();
		sql.firstPage(limit);
		sql.select("col_name","remark","java_type");
		return find(sql);
	}
	/**
	 * 获取指定表的Autocomplete接口配置匹配的columns
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getAutocompleteColumns(Long codeGenId) {
		if(notOk(codeGenId)) {
			return Collections.emptyList();
		}
		Sql sql = selectSql().eq("code_gen_id", codeGenId);
		sql.eq("is_pkey", FALSE);
		sql.notIn("col_name", "id","ID","create_time","CREATE_TIME","update_time","UPDATE_TIME","update_user_id","UPDATE_USER_ID","create_user_id","CREATE_USER_ID","is_deleted","IS_DELETED","enable","ENABLE");
		sql.orderBySortRank();
		sql.select("col_name","remark","java_type");
		return find(sql);
	}

	/**
	 * 获取指定表的options Autocomplete下拉使用的columns
	 * @param codeGenId
	 * @param column
	 * @param limit
	 * @param keywords
	 * @return
	 */
	public List<CodeGenModelAttr> getOptionsTextColumnsAutocomplete(Long codeGenId,String column,int limit, String keywords) {
		if(notOk(codeGenId)) {
			return Collections.emptyList();
		}
		Sql sql = selectSql().eq("code_gen_id", codeGenId);
		sql.likeMulti(keywords, "col_name","attr_name");
		sql.eq("is_pkey", FALSE);
		sql.notIn("col_name", "id","ID","create_time","CREATE_TIME","update_time","UPDATE_TIME","update_user_id","UPDATE_USER_ID","create_user_id","CREATE_USER_ID","is_deleted","IS_DELETED","enable","ENABLE");
		if(isOk(column)) {
			sql.notEq("col_name", column);
		}
		sql.orderBySortRank();
		sql.firstPage(limit);
		sql.select("col_name","remark","java_type");
		return find(sql);
	}

	/**
	 * 获取指定表的options value Autocomplete下拉使用的columns
	 * @param codeGenId
	 * @param column
	 * @param limit
	 * @param keywords
	 * @return
	 */
	public List<CodeGenModelAttr> getOptionsValueColumnsAutocomplete(Long codeGenId,String column,int limit, String keywords) {
		if(notOk(codeGenId)) {
			return Collections.emptyList();
		}
		Sql sql = selectSql().eq("code_gen_id", codeGenId);
		sql.likeMulti(keywords, "col_name","attr_name");
		sql.notIn("col_name", "create_time","CREATE_TIME","update_time","UPDATE_TIME","update_user_id","UPDATE_USER_ID","create_user_id","CREATE_USER_ID","is_deleted","IS_DELETED","enable","ENABLE");
		if(isOk(column)) {
			sql.notEq("col_name", column);
		}
		sql.orderBySortRank();
		sql.firstPage(limit);
		sql.select("col_name","remark","java_type");
		return find(sql);
	}

	public void deleteByCodeGenId(Long codeGenId) {
		if(isOk(codeGenId)) {
			delete(deleteSql().eq("code_gen_id", codeGenId));
		}
	}

	/**
	 * 获取可以作为关键词查询匹配的字段列表
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getKeywordsMatchColumns(Long codeGenId) {
		if(notOk(codeGenId)) {
			return null;
		}
		Sql sql = selectSql().eq("code_gen_id", codeGenId);
		sql.eq("is_keywords_column", TRUE);
		sql.select("id","col_name","table_label");
		sql.orderBySortRank();
		return find(sql);
	}

	/**
	 * 获取form表单顺序使用的属性分列数据
	 * @param codeGenId
	 * @param editMode
	 * @return
	 */
	public List<Kv> getCodeGenFormColDatas(Long codeGenId, boolean editMode) {
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			return null;
		}
		Integer formColumnSize = editMode?1:codeGen.getFormColumnSize();
		String formColumnProportion = codeGen.getFormColumnProportion();
		String formGroupProportion = codeGen.getFormGroupProportion();
		String formColumnDirection = codeGen.getFormColumnDirection();
		boolean isFormGroupRow = codeGen.getIsFormGroupRow();
		if(notOk(formColumnDirection)) {
			formColumnDirection = "v";
		}

		List<CodeGenModelAttr> attrs = getCodeGenInFormModelAttrs(codeGenId);
		List<Kv> kvs = null;
		int[] formGroupArr = null;
		if(isFormGroupRow && isOk(formGroupProportion)) {
			try {
				formGroupArr = StrUtil.splitToInt(formGroupProportion, ":");
			} catch (Exception e) {
				throw new RuntimeException("设置的组件左右比例数总和必须 2<=x<=12");
			}
		}else {
			formGroupArr = new int[] {2,10};
		}
		if(formGroupArr==null || formGroupArr.length!=2) {
			throw new RuntimeException("设置的组件左右比例数总和必须 2<=x<=12");
		}else {
			int totalCol = formGroupArr[0] + formGroupArr[1];
			if(totalCol >12 || totalCol<2) {
				throw new RuntimeException("设置的组件左右比例数总和必须 2<=x<=12");
			}
		}
		int total = attrs.size();
		if(formColumnSize<=1||formColumnSize>6) {
			kvs = new ArrayList<>();
			//当做一列处理
			kvs.add(Kv.by("datas",attrs).set("labelWidth",formGroupArr[0]).set("formControlWidth",formGroupArr[1]).set("modelName",StrKit.firstCharToLowerCase(codeGen.getModelName())).set("isFormGroupRow",isFormGroupRow));
		}else {
			//每列个数
			int count = total / formColumnSize + (total % formColumnSize != 0 ? 1 : 0);
			int[] arr;
			if(isOk(formColumnProportion)) {
				arr = StrUtil.splitToInt(formColumnProportion, ":");
				if(arr.length != formColumnSize) {
					throw new RuntimeException("设置的表单分几列与具体比例个数不匹配");
				}
				int totalCol = 0;
				for(int i=0;i<formColumnSize;i++) {
					totalCol=totalCol+arr[i];
				}
				if(totalCol != 12) {
					throw new RuntimeException("设置的表单分列比例数总和必须等于12");
				}
			}else {
				arr=new int[formColumnSize];
				for(int i=0;i<formColumnSize;i++) {
					arr[i] = 0;
				}
			}

			int startIndex = 0;
			int endIndex = 0;
			List<CodeGenModelAttr> subList;
			if(formColumnDirection.equals("v")) {
				kvs = new ArrayList<>();
				for(int i=0;i<formColumnSize;i++) {
					endIndex = startIndex + count;
					if(endIndex<=total-1){
						subList = attrs.subList(startIndex, endIndex);
					}else{
						subList = attrs.subList(startIndex,total);
					}
					kvs.add(Kv.by("col", arr[i]).set("labelWidth",formGroupArr[0]).set("formControlWidth",formGroupArr[1]).set("modelName",StrKit.firstCharToLowerCase(codeGen.getModelName())).set("isFormGroupRow",isFormGroupRow).set("datas",subList));
					startIndex = endIndex;
				}
			}else {
				kvs = new ArrayList<>();
				List<CodeGenModelAttr> kvAttrs;
				for(int i=0;i<formColumnSize;i++) {
					kvAttrs = new ArrayList<>();
					for(int k=i;k<total;) {
						kvAttrs.add(attrs.get(k));
						k=k+formColumnSize;
					}
					kvs.add(Kv.by("col",arr[i]).set("labelWidth",formGroupArr[0]).set("formControlWidth",formGroupArr[1]).set("modelName",StrKit.firstCharToLowerCase(codeGen.getModelName())).set("isFormGroupRow",isFormGroupRow).set("datas",kvAttrs));
				}
			}
		}
		return kvs;
	}

	/**
	 * 获取指定codeGen的attrs
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenInFormModelAttrs(Long codeGenId) {
		if(notOk(codeGenId)) {return new ArrayList<>();}
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			throw new RuntimeException("未找到代码生成的基础配置");
		}
		return find(selectSql().eq("code_gen_id", codeGenId).asc("sort_rank_inform").eq("is_form_ele", TRUE));
	}
	/**
	 * 移动表格交换位置顺序
	 * @param codeGenId
	 * @param id
	 * @param prevId
	 * @param nextId
	 * @return
	 */
	public Ret moveTableEle(Long codeGenId, Long id, Long prevId, Long nextId) {
		if(notOk(codeGenId)||notOk(id)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			return fail(JBoltMsg.PARAM_ERROR +",codeGenId错误");
		}
		CodeGenModelAttr attr = findById(id);
		if(attr == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		if(!attr.getCodeGenId().equals(codeGenId)) {
			return fail(JBoltMsg.PARAM_ERROR+"指定操作的数据与指定的codeGenId不匹配");
		}
		int count = getCount(Okv.by("code_gen_id", codeGenId).set("is_table_col",TRUE));
		if(count == 1) {
			attr.setSortRankInform(1);
			boolean success = attr.update();
			return ret(success);
		}
		if(notOk(prevId) && notOk(nextId)) {
			return fail("此表数据字段不止一个 prevId和nextId至少传一个");
		}
		//有前无后 说明是挪到最后一个
		if(isOk(prevId) && notOk(nextId)) {
			return moveToTableEnd(codeGenId,attr,count);
		}
		//有后无前 说明是挪到第一个
		if(notOk(prevId) && isOk(nextId)) {
			return moveToTableStart(codeGenId,attr);
		}
		//有前有后 中间了
		return moveToTableCenter(codeGenId,attr,prevId);
	}
	/**
	 * 移动表单交换位置顺序
	 * @param codeGenId
	 * @param id
	 * @param prevId
	 * @param nextId
	 * @return
	 */
	public Ret moveFormEle(Long codeGenId, Long id, Long prevId, Long nextId) {
		if(notOk(codeGenId)||notOk(id)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			return fail(JBoltMsg.PARAM_ERROR +",codeGenId错误");
		}
		CodeGenModelAttr attr = findById(id);
		if(attr == null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		if(!attr.getCodeGenId().equals(codeGenId)) {
			return fail(JBoltMsg.PARAM_ERROR+"指定操作的数据与指定的codeGenId不匹配");
		}
		int count = getCount(Okv.by("code_gen_id", codeGenId).set("is_form_ele",TRUE));
		if(count == 1) {
			attr.setSortRankInform(1);
			boolean success = attr.update();
			return ret(success);
		}
		if(notOk(prevId) && notOk(nextId)) {
			return fail("此表数据字段不止一个 prevId和nextId至少传一个");
		}
		//有前无后 说明是挪到最后一个
		if(isOk(prevId) && notOk(nextId)) {
			return moveToFormEnd(codeGenId,attr,count);
		}
		//有后无前 说明是挪到第一个
		if(notOk(prevId) && isOk(nextId)) {
			return moveToFormStart(codeGenId,attr);
		}
		//有前有后 中间了
		return moveToFormCenter(codeGenId,attr,prevId);
	}

	private Ret moveToFormCenter(Long codeGenId, CodeGenModelAttr attr, Long prevId) {
		List<CodeGenModelAttr> attrs = getCodeGenModelAttrsForSortRankInform(codeGenId, attr.getId());
		if(isOk(attrs)) {
			CodeGenModelAttr prevAttr = findById(prevId);
			if(prevAttr == null) {
				return fail("参数指定prevId 找不到数据");
			}
			CodeGenModelAttr atr;
			for(int i=0;i<attrs.size();i++){
				atr = attrs.get(i);
				atr.setSortRankInform(i+1);
				if(atr.getId().equals(prevId)) {
					attr.setSortRankInform(i+2);
					attrs.add(i+1,attr);
					i=i+1;
				}
			}
			batchUpdate(attrs);
		}
		return SUCCESS;
	}

	private Ret moveToTableCenter(Long codeGenId, CodeGenModelAttr attr, Long prevId) {
		List<CodeGenModelAttr> attrs = getCodeGenModelAttrsForSortRankInTable(codeGenId, attr.getId());
		if(isOk(attrs)) {
			CodeGenModelAttr prevAttr = findById(prevId);
			if(prevAttr == null) {
				return fail("参数指定prevId 找不到数据");
			}
			CodeGenModelAttr atr;
			for(int i=0;i<attrs.size();i++){
				atr = attrs.get(i);
				atr.setSortRankIntable(i+1);
				if(atr.getId().equals(prevId)) {
					attr.setSortRankIntable(i+2);
					attrs.add(i+1,attr);
					i=i+1;
				}
			}
			batchUpdate(attrs);
		}
		return SUCCESS;
	}
	/**
	 * 移动位置到开头 form
	 * @param codeGenId
	 * @param attr
	 * @return
	 */
	private Ret moveToFormStart(Long codeGenId, CodeGenModelAttr attr) {
		attr.setSortRankInform(1);
		boolean success = attr.update();
		if(!success) {
			return fail("调整表单属性组件排序[移动到最前]出现异常，调整失败");
		}
		List<CodeGenModelAttr> attrs = getCodeGenModelAttrsForSortRankInform(codeGenId, attr.getId());
		if(isOk(attrs)) {
			int rank = 2;
			for(CodeGenModelAttr atr:attrs) {
				atr.setSortRankInform(rank);
				rank++;
			}
			batchUpdate(attrs);
		}
		return SUCCESS;
	}

	/**
	 * 移动位置到开头 table
	 * @param codeGenId
	 * @param attr
	 * @return
	 */
	private Ret moveToTableStart(Long codeGenId, CodeGenModelAttr attr) {
		attr.setSortRankIntable(1);
		boolean success = attr.update();
		if(!success) {
			return fail("调整表格列排序[移动到最前]出现异常，调整失败");
		}
		List<CodeGenModelAttr> attrs = getCodeGenModelAttrsForSortRankInTable(codeGenId, attr.getId());
		if(isOk(attrs)) {
			int rank = 2;
			for(CodeGenModelAttr atr:attrs) {
				atr.setSortRankIntable(rank);
				rank++;
			}
			batchUpdate(attrs);
		}
		return SUCCESS;
	}

	/**
	 * 移动到form结尾
	 * @param codeGenId
	 * @param attr
	 * @param count
	 * @return
	 */
	private Ret moveToFormEnd(Long codeGenId, CodeGenModelAttr attr,int count) {
		attr.setSortRankInform(count);
		boolean success = attr.update();
		if(!success) {
			return fail("调整表单属性组件排序[移动到最后]出现异常，调整失败");
		}
		List<CodeGenModelAttr> attrs = getCodeGenModelAttrsForSortRankInform(codeGenId, attr.getId());
		if(isOk(attrs)) {
			int rank = 1;
			for(CodeGenModelAttr atr:attrs) {
				atr.setSortRankInform(rank);
				rank++;
			}
			batchUpdate(attrs);
		}
		return SUCCESS;
	}

	/**
	 * 移动到form结尾
	 * @param codeGenId
	 * @param attr
	 * @param count
	 * @return
	 */
	private Ret moveToTableEnd(Long codeGenId, CodeGenModelAttr attr,int count) {
		attr.setSortRankIntable(count);
		boolean success = attr.update();
		if(!success) {
			return fail("调整表格列排序[移动到最后]出现异常，调整失败");
		}
		List<CodeGenModelAttr> attrs = getCodeGenModelAttrsForSortRankInTable(codeGenId, attr.getId());
		if(isOk(attrs)) {
			int rank = 1;
			for(CodeGenModelAttr atr:attrs) {
				atr.setSortRankIntable(rank);
				rank++;
			}
			batchUpdate(attrs);
		}
		return SUCCESS;
	}
	/**
	 * 获取到除了排除ID之外的 一个表的字段列表数据
	 * @param codeGenId
	 * @param excludeId
	 * @return
	 */
	private List<CodeGenModelAttr> getCodeGenModelAttrsForSortRankInform(Long codeGenId,Long excludeId){
		return find(selectSql().select("id","sort_rank_inform").eq("code_gen_id", codeGenId).eq("is_form_ele", TRUE).idNotEq(excludeId).orderBySortRank("sort_rank_inform"));
	}

	/**
	 * 获取到除了排除ID之外的 一个表的字段列表数据 表里
	 * @param codeGenId
	 * @param excludeId
	 * @return
	 */
	private List<CodeGenModelAttr> getCodeGenModelAttrsForSortRankInTable(Long codeGenId,Long excludeId){
		return find(selectSql().select("id","sort_rank_intable").eq("code_gen_id", codeGenId).eq("is_table_col", TRUE).idNotEq(excludeId).orderBySortRank("sort_rank_intable"));
	}
	/**
	 * 初始化表单元素排序
	 * @param codeGenId
	 * @return
	 */
	public Ret initSortRankInformById(Long codeGenId) {
		List<CodeGenModelAttr> attrs = find(selectSql().select("id").eq("code_gen_id", codeGenId).eq("is_form_ele", TRUE).orderById());
		if(isOk(attrs)) {
			int total = attrs.size();
			for(int i=0;i<total;i++) {
				attrs.get(i).setSortRankInform(i+1);
			}
			batchUpdate(attrs);
		}
		return SUCCESS;
	}

	/**
	 * 初始化表格显示列元素排序
	 * @param codeGenId
	 * @return
	 */
	public Ret initSortRankIntableById(Long codeGenId) {
		List<CodeGenModelAttr> attrs = find(selectSql().select("id").eq("code_gen_id", codeGenId).eq("is_table_col", TRUE).orderById());
		if(isOk(attrs)) {
			int total = attrs.size();
			for(int i=0;i<total;i++) {
				attrs.get(i).setSortRankIntable(i+1);
			}
			batchUpdate(attrs);
		}
		return SUCCESS;
	}

	/**
	 *同步一个表的attrs
	 * @param codeGenId
	 * @return
	 */
	public Ret syncAttrs(Long codeGenId) {
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			throw new RuntimeException("未找到代码生成的基础配置");
		}
		List<CodeGenModelAttr> modelAttrs = getCodeGenModelAttrs(codeGenId, "sort_rank", false);
		if(isOk(modelAttrs)) {
			TableMeta tableMeta = JBoltTableMetaUtil.getTableMeta(codeGen.getDatasourceName(), codeGen.getDatabaseType(), codeGen.getMainTableName(),false);
			if(tableMeta!=null) {
				List<ColumnMeta> columnMetas = tableMeta.columnMetas;
				if(isOk(columnMetas)) {
					//先处理需要删除的
					processSyncDeleteAttrs(modelAttrs, columnMetas);
					//然后处理新增和更新
					int len = columnMetas.size();
					Ret ret;
					for(int i=0;i<len;i++) {
						ret = processOneColSync(codeGenId,codeGen.getIsEditable(),codeGen.getIsCrud(),modelAttrs,columnMetas.get(i),i+1);
						if(ret.isFail()) {
							return ret;
						}
					}

				}
			}
			codeGenService.reprocessMainTablePrimaryKey(codeGenId);
			codeGenService.processCodeGenRecoverById(codeGenId);
			initSortRankIntableByRank(codeGenId);
		}else {
			boolean success = genMainTableAttrs(codeGen);
			if(success){
				codeGenService.reprocessMainTablePrimaryKey(codeGenId);
				codeGenService.processCodeGenRecoverById(codeGenId);
				initSortRankIntableByRank(codeGenId);
			}
			return ret(success);
		}
		return SUCCESS;
	}


	/**
	 * 获取主键属性
	 * @param codeGenId
	 * @return
	 */
	public CodeGenModelAttr getPrimaryKeyAttr(Long codeGenId) {
		if(notOk(codeGenId)){return null;}
		return findFirst(selectSql().eq("code_gen_id",codeGenId).eq("is_pkey",TRUE));
	}

	private void processSyncDeleteAttrs(List<CodeGenModelAttr> modelAttrs, List<ColumnMeta> columnMetas) {
		List<CodeGenModelAttr> deletes = new ArrayList<>();
		List<String> names = columnMetas.stream().map(col->col.name).collect(Collectors.toList());
		CodeGenModelAttr tempAttr;
		for(int i = 0;i<modelAttrs.size();i++) {
			tempAttr = modelAttrs.get(i);
			if(!names.contains(tempAttr.getColName())) {
				deletes.add(modelAttrs.remove(i));
				i--;
			}
		}
		if(deletes.size() > 0) {
			for(CodeGenModelAttr del:deletes) {
				del.delete();
			}
		}
	}

	/**
	 * 处理一个字段的同步
	 * @param codeGenId
	 * @param modelAttrs
	 * @param col
	 * @param sortRank
	 */
	private Ret processOneColSync(Long codeGenId,boolean isEditable,boolean isCrud,List<CodeGenModelAttr> modelAttrs,ColumnMeta col,int sortRank) {
		//同步规则 查询有的就看类型是否需要更新 没有的就新增  modelattr里有但是col没有的就删除
		CodeGenModelAttr attr = null;
		for(CodeGenModelAttr ca:modelAttrs) {
			if(ca.getColName().equals(col.name)) {
				attr = ca;
				break;
			}
		}
		if(attr == null) {
			//说明这个是新加的字段 需要save
			return saveNewAttrBySync(sortRank,codeGenId,isEditable,isCrud,col);
		}
		//如果存在就要判断是否需要更新了
		if(!attr.getAttrName().equals(col.attrName)) {
			attr.setAttrName(col.attrName);
		}

		if(!attr.getJavaType().equals(col.javaType)) {
			attr.setJavaType(col.javaType);
		}
		String rek = StrKit.defaultIfBlank(col.remarks, col.attrName);
		if(!attr.getRemark().equals(rek)) {
			attr.setRemark(rek);
		}

		String len = ColumnTypeToDirective.getLength(col.type);
		Integer attrLen = null;
		if(StrKit.isBlank(len)||len.equals("0")) {
			attrLen = 0;
		}else {
			attrLen = Integer.parseInt(len);
		}
		if(attr.getAttrLength().intValue() != attrLen.intValue()) {
			attr.setAttrLength(attrLen);
		}

		String fixed = ColumnTypeToDirective.getFixed(col.type);
		Integer attrFixed = null;
		if(StrKit.isBlank(fixed)||fixed.equals("0")) {
			attrFixed = 0;
		}else {
			attrFixed = Integer.parseInt(fixed);
		}

		if(attr.getAttrFixed().intValue() != attrFixed.intValue()) {
			attr.setAttrFixed(attrFixed);
		}

		boolean colIsPkey = col.isPrimaryKey.equalsIgnoreCase("pri");
		if(attr.getIsPkey().booleanValue() != colIsPkey) {
			attr.setIsPkey(colIsPkey);
		}
		boolean colIsRequired = !col.isNullable.equalsIgnoreCase("yes");
		if(attr.getIsRequired().booleanValue() != colIsRequired) {
			attr.setIsRequired(colIsRequired);
		}

		if(StrKit.isBlank(attr.getAttrDefaultValue()) && StrKit.notBlank(col.defaultValue)) {
			if(col.javaType.equalsIgnoreCase("java.lang.boolean")) {
				boolean isTrue = col.defaultValue.equalsIgnoreCase("true") || col.defaultValue.equalsIgnoreCase(TRUE);
				//属性默认值
				attr.setAttrDefaultValue(isTrue?TRUE:FALSE);
				attr.setFormDefaultValue(isTrue?TRUE_STR:FALSE_STR);
				attr.setSearchDefaultValue(isTrue?TRUE_STR:FALSE_STR);
			}else {
				//属性默认值
				attr.setAttrDefaultValue(col.defaultValue);
				attr.setFormDefaultValue(col.defaultValue);
				attr.setSearchDefaultValue(col.defaultValue);
			}
		}

		attr.setSortRank(sortRank);
		boolean success = attr.update();
		return ret(success);
	}

	/**
	 * 单个同步save
	 * @param sortRank
	 * @param codeGenId
	 * @param col
	 * @return
	 */
	private Ret saveNewAttrBySync(int sortRank, Long codeGenId,boolean isEditable,boolean isCrud, ColumnMeta col) {
		CodeGenModelAttr attr = convertModelAttr(sortRank,codeGenId,isEditable,isCrud,col);
		boolean success = attr.save();
		return ret(success);
	}
	/**
	 * 更新formEle config
	 * @param para
	 * @return
	 */
	public Ret updateFormEleConfig(JBoltPara para) {
		if(para == null || para.isEmpty() || !para.containsKey("attr")) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		JSONObject attrJson = para.getJSONObject("attr");
		if(!attrJson.containsKey("id")) {
			return fail(JBoltMsg.PARAM_ERROR + " 未提交属性ID");
		}
		Long id = attrJson.getLong("id");
		boolean dbExists = existsById(id);
		if(!dbExists) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		CodeGenModelAttr attr = attrJson.toJavaObject(CodeGenModelAttr.class);
		if(attrJson.containsKey("isJBoltInputFilterTable")) {
			Boolean isJBoltInputFilterTable = attrJson.getBoolean("isJBoltInputFilterTable");
			if(isJBoltInputFilterTable == null) {
				isJBoltInputFilterTable = false;
			}
			if(isJBoltInputFilterTable) {
				attr.setFormJboltinputFilterHandler("filterTable");
			}else {
				attr.setFormJboltinputFilterHandler(null);
			}
		}else if(attrJson.containsKey("isJBoltInputFilterTree")) {
			Boolean isJBoltInputFilterTree = attrJson.getBoolean("isJBoltInputFilterTree");
			if(isJBoltInputFilterTree == null) {
				isJBoltInputFilterTree = false;
			}
			if(isJBoltInputFilterTree) {
				attr.setFormJboltinputFilterHandler("filterTree");
			}else {
				attr.setFormJboltinputFilterHandler(null);
			}
		}
		if(attrJson.containsKey("isItemInline")) {
			Boolean isItemInline = attrJson.getBoolean("isItemInline");
			if(isItemInline == null) {
				isItemInline = false;
			}
			attr.setIsItemInline(isItemInline);
		}
		if(attrJson.containsKey("isFormJboltinputJstreeCheckbox")) {
			Boolean isFormJboltinputJstreeCheckbox = attrJson.getBoolean("isFormJboltinputJstreeCheckbox");
			if(isFormJboltinputJstreeCheckbox == null) {
				isFormJboltinputJstreeCheckbox = false;
			}
			attr.setIsFormJboltinputJstreeCheckbox(isFormJboltinputJstreeCheckbox);
		}
		if(attrJson.containsKey("isFormJBoltInputJstreeOnlyLeaf")) {
			Boolean isFormJBoltInputJstreeOnlyLeaf = attrJson.getBoolean("isFormJBoltInputJstreeOnlyLeaf");
			if(isFormJBoltInputJstreeOnlyLeaf == null) {
				isFormJBoltInputJstreeOnlyLeaf = false;
			}
			attr.setIsFormJboltinputJstreeOnlyLeaf(isFormJBoltInputJstreeOnlyLeaf);
		}
		//如果是上传组件
		if(attrJson.containsKey("isUploader") && attrJson.getBooleanValue("isUploader")){
			boolean isUploadMulti = attrJson.getBooleanValue("isUploadMulti");
			boolean isImgUploader = attrJson.getBooleanValue("isImgUploader");
			boolean isFileUploader = attrJson.getBooleanValue("isFileUploader");
			attr.setFormUploadUrl(attrJson.getString("formUploadUrl"));
			Integer maxSize = attrJson.getInteger("formMaxsize");
			if(notOk(maxSize)){
				maxSize = 200;
			}
			attr.setFormMaxsize(maxSize);
			boolean isUploadToQiniu = attrJson.getBooleanValue("isUploadToQiniu");
			attr.setIsUploadToQiniu(isUploadToQiniu);
			if(isUploadToQiniu){
				String qiniuBucketSn = attrJson.getString("qiniuBucketSn");
				if(notOk(qiniuBucketSn)){
					attr.setQiniuBucketSn(qiniuBucketSn);
				}
			}

			if(isImgUploader){
				String formImgUploaderArea = attrJson.getString("formImgUploaderArea");
				if(notOk(formImgUploaderArea)){
					if(isUploadMulti){
						attr.setFormImgUploaderArea("150,150");
					}else{
						attr.setFormImgUploaderArea("200,200");
					}
				}else{
					if(!formImgUploaderArea.contains(",")){
						throw new RuntimeException("图片上传区域尺寸【"+formImgUploaderArea+"】格式不正确 举例: 200,200");
					}
					String[] arr = formImgUploaderArea.split(",");
					if(arr==null||arr.length!=2){
						throw new RuntimeException("图片上传区域尺寸【"+formImgUploaderArea+"】格式不正确 举例: 200,200");
					}
					if(!NumberUtil.isInteger(arr[0])||!NumberUtil.isInteger(arr[1])){
						throw new RuntimeException("图片上传区域尺寸【"+formImgUploaderArea+"】格式不正确 举例: 200,200");
					}
					attr.setFormImgUploaderArea(formImgUploaderArea);
				}
			}
		}
		boolean success = attr.update();

		return ret(success);
	}

	/**
	 * 获取表格列数据
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenTableColumns(Long codeGenId) {
		if(notOk(codeGenId)) {
			return new ArrayList<>();
		}
		return find(selectSql().eq("code_gen_id", codeGenId).eq("is_table_col", TRUE).orderBySortRank("sort_rank_intable"));
	}
	/**
	 * 获取表格导入数据
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenImportColumns(Long codeGenId) {
		if(notOk(codeGenId)) {
			return new ArrayList<>();
		}
		return find(selectSql().eq("code_gen_id", codeGenId).eq("is_import_col", TRUE).orderBySortRank("sort_rank_intable"));
	}

	/**
	 * 获取表格导出数据
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenExportColumns(Long codeGenId) {
		if(notOk(codeGenId)) {
			return new ArrayList<>();
		}
		return find(selectSql().eq("code_gen_id", codeGenId).eq("is_export_col", TRUE).orderBySortRank("sort_rank_intable"));
	}

	/**
	 * 表格查询条件
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenTableConditions(Long codeGenId) {
		if(notOk(codeGenId)) {
			return new ArrayList<>();
		}
		return find(selectSql().eq("code_gen_id", codeGenId).eq("is_search_ele", TRUE).orderBySortRank("sort_rank_insearch"));
	}

	public Page<Okv> paginateCodeGenTableColumnsDatas(Long codeGenId,Integer pageNumber,Integer pageSize) {
		if(notOk(codeGenId)) {
			return null;
		}
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			return null;
		}
		Sql sql = selectSql().eq("code_gen_id", codeGenId).eq("is_table_col", TRUE).orderBySortRank("sort_rank_intable");
		if(codeGen.getIsTableUseRecord() && !codeGen.getIsTableRecordCamelCase()) {
			sql.select("col_name as newname","java_type");
		}else {
			sql.select("attr_name as newname","java_type");
		}
		List<Record> columns = findRecord(sql);
		String javaType;
		List<Okv> datas = new ArrayList<>();
		Okv temp;
		for(int i=0;i<pageSize;i++) {
			temp = Okv.create();
			for(Record col:columns) {
				javaType = col.getStr("java_type");
				switch (javaType) {
				case "java.lang.String":
					temp.set(col.getStr("newname"), col.getStr("newname")+"_"+(pageNumber-1)*i);
					break;
				case "java.lang.Integer":
					temp.set(col.getStr("newname"), (pageNumber-1)*pageSize+i);
					break;
				case "java.lang.Long":
					temp.set(col.getStr("newname"), (pageNumber-1)*pageSize+i);
					break;
				case "java.math.BigInteger":
					temp.set(col.getStr("newname"), (pageNumber-1)*pageSize+i);
					break;
				case "java.math.BigDecimal":
					temp.set(col.getStr("newname"), new BigDecimal((pageNumber-1)*pageSize+i));
					break;
				case "java.lang.Boolean":
					temp.set(col.getStr("newname"), ((pageNumber-1)*pageSize+i)%2==0);
					break;
				case "java.util.Date":
					temp.set(col.getStr("newname"), new Date());
					break;
				case "java.sql.Time":
					temp.set(col.getStr("newname"), new Time(12,12,12));
					break;
				default:
					temp.set(col.getStr("newname"), col.getStr("newname")+"_"+((pageNumber-1)*pageSize+i));
					break;
				}
			}
			datas.add(temp);
		}
		return new Page<>(datas, pageNumber, pageSize, 3, 30);
	}
	public List<Okv> getCodeGenTableColumnsDatas(Long codeGenId) {
		if(notOk(codeGenId)) {
			return new ArrayList<>();
		}
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			return new ArrayList<>();
		}
		Sql sql = selectSql().eq("code_gen_id", codeGenId).eq("is_table_col", TRUE).orderBySortRank("sort_rank_intable");
		if(codeGen.getIsTableUseRecord() && !codeGen.getIsTableRecordCamelCase()) {
			sql.select("col_name as newname","java_type");
		}else {
			sql.select("attr_name as newname","java_type");
		}
		List<Record> columns = findRecord(sql);
		String javaType;
		List<Okv> datas = new ArrayList<>();
		Okv temp;
		for(int i=0;i<50;i++) {
			temp = Okv.create();
			for(Record col:columns) {
				javaType = col.getStr("java_type");
				switch (javaType) {
					case "java.lang.String":
						temp.set(col.getStr("newname"), col.getStr("newname")+"_"+i);
						break;
					case "java.lang.Integer":
						temp.set(col.getStr("newname"), i);
						break;
					case "java.lang.Long":
						temp.set(col.getStr("newname"), i+1L);
						break;
					case "java.math.BigInteger":
						temp.set(col.getStr("newname"), i+1);
						break;
					case "java.math.BigDecimal":
						temp.set(col.getStr("newname"), new BigDecimal(i+1));
						break;
					case "java.lang.Boolean":
						temp.set(col.getStr("newname"), i%2==0);
						break;
					case "java.util.Date":
						temp.set(col.getStr("newname"), new Date());
						break;
					case "java.sql.Time":
						temp.set(col.getStr("newname"), new Time(12,12,12));
						break;
					default:
						temp.set(col.getStr("newname"), col.getStr("newname")+"_"+i);
						break;
				}
			}
			datas.add(temp);
		}
		return datas;
	}
	/**
	 * 获取排序字段
	 * @param codeGenId
	 * @return
	 */
	public List<String> getSortableColumns(Long codeGenId){
		if(notOk(codeGenId)) {return null;}
		return query(selectSql().select("col_name").eq("code_gen_id",codeGenId).eq("is_table_col", TRUE).eq("is_sortable", TRUE).orderBySortRank("sort_rank_intable"));
	}
	/**
	 * 获取排序字段
	 * @param codeGenId
	 * @return
	 */
	public String getSortableColumnsStr(Long codeGenId) {
		if(notOk(codeGenId)) {return null;}
		List<String> columns = getSortableColumns(codeGenId);
		if(notOk(columns)) {return null;}
		return StrUtil.join(",", columns);
	}


	@Override
	protected int systemLogTargetType() {
		return 0;
	}

	/**
	 * 获取一个表格中的设置为表格里显示并且是switchbtn的列属性
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getTableToggleCodeGenModelAttrs(Long codeGenId) {
		return find(selectSql().eq("code_gen_id",codeGenId).eq("is_table_col",TRUE).eq("is_table_switchbtn",TRUE).asc("sort_rank_intable"));
	}

	/**
	 * 获取一个表格中的查询条件
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenSearchConditionsAttrs(Long codeGenId) {
		return find(selectSql().eq("code_gen_id",codeGenId).eq("is_search_ele",TRUE).asc("sort_rank_insearch"));
	}

	/**
	 * 获取作为关键词查询的列
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenSearchKeywordsColumnAttrs(Long codeGenId) {
		return find(selectSql().eq("code_gen_id",codeGenId).eq("is_keywords_column",TRUE).orderBySortRank());
	}

	/**
	 * 获取需要翻译的列
	 * @param codeGenId
	 * @return
	 */
	public List<CodeGenModelAttr> getCodeGenNeedTranslateAttrs(Long codeGenId) {
		return find(selectSql().eq("code_gen_id",codeGenId).eq("is_need_translate",TRUE).isNotNull("translate_type").orderBySortRank());
	}

	/**
	 * 检测是否存在is_deleted字段
	 * @param codeGenId
	 * @return
	 */
	public boolean checkHasIsDeletedColumn(Long codeGenId) {
		return exists(selectSql().eq("code_gen_id",codeGenId).bracketLeft().eq("col_name","is_deleted").or().eq("col_name","IS_DELETED").bracketRight());
	}

	/**
	 * 检测是否存在enable字段
	 * @param codeGenId
	 * @return
	 */
	public boolean checkHasEnableColumn(Long codeGenId) {
		return exists(selectSql().eq("code_gen_id",codeGenId).bracketLeft().eq("col_name","enable").or().eq("col_name","ENABLE").bracketRight());
	}

	/**
	 * 检测是否存在is_keywords_column字段
	 * @param codeGenId
	 * @return
	 */
	public boolean checkHasIsKeywordsColumn(Long codeGenId) {
		return exists(selectSql().eq("code_gen_id",codeGenId).eq("is_keywords_column",TRUE));
	}


	/**
	 * 提交表格列配置 更新
	 * @param attr
	 * @return
	 */
	public Ret updateTableColConfig(CodeGenModelAttr attr) {
		if(attr==null || hasNotOk(attr.getId(),attr.getTableLabel(),attr.getIsNeedFixedWidth(),attr.getTableColWidth())){
			return fail(JBoltMsg.PARAM_ERROR);
		}
		boolean success = attr.update();
		return ret(success);
	}

	public Ret tableColUp(Long id) {
		CodeGenModelAttr attr = findById(id);
		if (attr == null) {
			return fail("数据不存在或已被删除");
		}
		Integer rank = attr.getSortRankIntable();
		if (rank == null || rank <= 0) {
			return fail("顺序需要初始化");
		}
		if (rank == 1) {
			return fail("已经是第一个");
		}
		CodeGenModelAttr upAttr = findFirst(Okv.by(CodeGenModelAttr.SORT_RANK_INTABLE, rank - 1).set(CodeGenModelAttr.IS_TABLE_COL, TRUE));
		if (upAttr == null) {
			return fail("顺序需要初始化");
		}
		upAttr.setSortRankIntable(rank);
		attr.setSortRankIntable(rank - 1);
		upAttr.update();
		attr.update();
		return SUCCESS;
	}

	public Ret tableColDown(Long id) {
		CodeGenModelAttr attr = findById(id);
		if (attr == null) {
			return fail("数据不存在或已被删除");
		}
		Integer rank = attr.getSortRankIntable();
		if (rank == null || rank <= 0) {
			return fail("顺序需要初始化");
		}
		int max = getCount(Okv.by(CodeGenModelAttr.IS_TABLE_COL, TRUE));
		if (rank == max) {
			return fail("已经是最后一个");
		}
		CodeGenModelAttr upAttr = findFirst(Okv.by(CodeGenModelAttr.SORT_RANK_INTABLE, rank + 1).set(CodeGenModelAttr.IS_TABLE_COL, TRUE));
		if (upAttr == null) {
			return fail("顺序需要初始化");
		}
		upAttr.setSortRankIntable(rank);
		attr.setSortRankIntable(rank + 1);
		upAttr.update();
		attr.update();
		return SUCCESS;
	}

	public boolean checkHasNameAttr(Long codeGenId) {
		return exists(selectSql().eq(CodeGenModelAttr.CODE_GEN_ID,codeGenId).eq(CodeGenModelAttr.ATTR_NAME,"name"));
	}

	public boolean checkHasSnAttr(Long codeGenId) {
		return exists(selectSql().eq(CodeGenModelAttr.CODE_GEN_ID,codeGenId).eq(CodeGenModelAttr.ATTR_NAME,"sn"));
	}

	public List<CodeGenModelAttr> getCodeGenInFormUploadModelAttrs(Long codeGenId) {
		if(notOk(codeGenId)) {return new ArrayList<>();}
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			throw new RuntimeException("未找到代码生成的基础配置");
		}
		return find(selectSql().eq("code_gen_id", codeGenId).eq("is_form_ele", TRUE).isNotNull(CodeGenModelAttr.FORM_UPLOAD_URL));
	}

	public List<CodeGenModelAttr> getNeedCheckExistsColumns(Long codeGenId) {
		if(notOk(codeGenId)) {return new ArrayList<>();}
		CodeGen codeGen = codeGenService.findById(codeGenId);
		if(codeGen == null) {
			throw new RuntimeException("未找到代码生成的基础配置");
		}
		return find(selectSql().eq("code_gen_id", codeGenId).eqBooleanToChar(CodeGenModelAttr.IS_NEED_CHECK_EXISTS,true));
	}
}
