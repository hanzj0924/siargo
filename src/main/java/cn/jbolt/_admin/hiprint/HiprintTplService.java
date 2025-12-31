package cn.jbolt._admin.hiprint;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.JBoltProSystemLogTargetType;
import cn.jbolt.common.model.HiprintTpl;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.util.JBoltRandomUtil;

/**
 * hiprint 模板库service
 * @ClassName:  HiprintTplService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年9月4日   
 */
public class HiprintTplService extends JBoltBaseService<HiprintTpl> {
	private HiprintTpl dao = new HiprintTpl().dao();
	@Override
	protected HiprintTpl dao() {
		return dao;
	}
	
	/**
	 * 分页读取指定条件模板数据
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param enable
	 * @param withoutColumns
	 * @return
	 */
	public Page<HiprintTpl> paginateAdminDatas(int pageNumber, int pageSize, String keywords, Boolean enable,String... withoutColumns) {
		return paginate(selectSql().select(getTableSelectColumnsWithout(withoutColumns)).page(pageNumber, pageSize).eq("enable", enable).likeMulti(keywords, "name","sn","remark"));
	}

	/**
	 * 提交设计器里的模板保存
	 * @param tpl
	 * @return
	 */
	public Ret submitTpl(HiprintTpl tpl) {
		if(tpl==null||notOk(tpl.getName())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		boolean isSave = notOk(tpl.getId());
		
		if(isSave) {
			if(notOk(tpl.getSn())) {
				tpl.setSn(JBoltRandomUtil.randomNumber(6));
			}
			if(exists("sn", tpl.getSn())) {
				return fail(JBoltMsg.DATA_SAME_SN_EXIST+"["+tpl.getSn()+"]");
			}
			if(notOk(tpl.getContent())) {
				JSONObject obj = new JSONObject();
				obj.put("index", 0);
				obj.put("paperType", "A4");
				obj.put("printElements", new JSONArray());
				JSONArray panels = new JSONArray();
				panels.add(obj);
				JSONObject defaultJson = new JSONObject();
				defaultJson.put("panels", panels);
				tpl.setContent(defaultJson.toString());
			}
			tpl.setCreateUserId(JBoltUserKit.getUserId());
			tpl.setUpdateUserId(JBoltUserKit.getUserId());
		}else {
			HiprintTpl dbTpl = findById(tpl.getId());
			if(dbTpl == null) {
				return fail(JBoltMsg.PARAM_ERROR);
			}
			if(isOk(tpl.getSn())) {
				if(exists("sn", tpl.getSn(), tpl.getId())) {
					return fail(JBoltMsg.DATA_SAME_SN_EXIST+"["+tpl.getSn()+"]");
				}
			}
			tpl.setUpdateUserId(JBoltUserKit.getUserId());
		}
		
		boolean success =  false;
		if(isSave) {
			success = tpl.save();
		}else {
			success = tpl.update();
		}
		return success?successWithData(tpl):fail("打印模板提交异常");
	}

	@Override
	protected int systemLogTargetType() {
		return JBoltProSystemLogTargetType.HIPRINT_TPL.getValue();
	}
	

}
