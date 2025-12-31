package cn.jbolt.admin.wechat.autoreply;

import java.util.Collections;
import java.util.List;

import cn.jbolt.common.enums.WechatKeywordsType;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;

import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.common.model.WechatKeywords;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.common.enums.JBoltSystemLogTargetType;
import cn.jbolt.core.common.enums.JBoltSystemLogType;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.service.base.JBoltBaseService;

/**  
 * 微信自定义菜单配置 
 * @ClassName:  WechatKeywordsService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月14日 上午4:31:49   
 */
public class WechatKeywordsService extends JBoltBaseService<WechatKeywords> {
	private WechatKeywords dao = new WechatKeywords().dao();
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Override
	protected WechatKeywords dao() {
		return dao;
	}
	/**
	 * 删除指定公众平台的自定义菜单数据
	 * @param mpId
	 * @return
	 */
	public Ret deleteByMpId(Long mpId) {
		return deleteBy(Okv.by("mp_id", mpId));
	}
	public List<WechatKeywords> getListByAutoReplyId(Long autoReplyId) {
		if(notOk(autoReplyId)) {return Collections.emptyList();}
		return getCommonList(Okv.by("auto_reply_id", autoReplyId));
	}
	/**
	 * 保存
	 * @param autoReplyId
	 * @param wechatKeywords
	 * @return
	 */
	public Ret save( Long autoReplyId, WechatKeywords wechatKeywords) {
		return submit(autoReplyId,wechatKeywords,false);
	}
	/**
	 * 更新
	 * @param autoReplyId
	 * @param wechatKeywords
	 * @return
	 */
	public Ret update(Long autoReplyId, WechatKeywords wechatKeywords) {
		return submit(autoReplyId,wechatKeywords,true);
	}
	public  Ret checkCanOpt(Long autoReplyId) {
		return checkCanOpt(autoReplyId, null);
	}
	public  Ret checkCanOpt(Long autoReplyId,Long id) {
		WechatAutoreply wechatAutoreply=wechatAutoReplyService.findById(autoReplyId);
		if(wechatAutoreply==null) {
			return fail("消息回复规则信息不存在");
		}
		Long mpId=wechatAutoreply.getMpId();
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {return fail("关联微信公众平台不存在");}
		String append="所属公众平台：["+wechatMpinfo.getName()+"] 规则:["+wechatAutoreply.getType()+"]["+wechatAutoreply.getName()+"]";
		if(isOk(id)) {
			WechatKeywords wechatKeywords=findById(id);
			if(wechatKeywords==null) {
				return fail(JBoltMsg.DATA_NOT_EXIST);
			}
			if(wechatKeywords.getMpId().longValue()!=mpId.longValue()) {return fail("参数异常:公众平台mpId");}
			if(wechatKeywords.getAutoReplyId().longValue()!=autoReplyId.longValue()) {return fail("参数异常:所属规则 autoReplyId");}
		}
		return success(Kv.by("mpId", mpId).set("append",append),JBoltMsg.SUCCESS);
	}
	/**
	 * 提交
	 * @param autoReplyId
	 * @param wechatKeywords
	 * @param update
	 * @return
	 */
	private Ret submit(Long autoReplyId, WechatKeywords wechatKeywords, boolean update) {
		if(notOk(autoReplyId)||wechatKeywords==null||notOk(wechatKeywords.getName())||notOk(wechatKeywords.getAutoReplyId())||notOk(wechatKeywords.getType())
				||(update==false&&isOk(wechatKeywords.getId()))||(update&&notOk(wechatKeywords.getId()))
				||autoReplyId.longValue()!=wechatKeywords.getAutoReplyId().longValue()
				) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//校验参数和数据
		Ret checkRet=checkCanOpt(autoReplyId);
		if(checkRet.isFail()) {
			return checkRet;
		}
		Long mpId=wechatKeywords.getMpId();
		if(mpId.longValue()!=wechatKeywords.getMpId().longValue()) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//判断和执行操作
		boolean success=false;
		wechatKeywords.setAutoReplyId(autoReplyId);
		String name=wechatKeywords.getName().trim();
		if(update) {
			if(checkNameExist(mpId,name,wechatKeywords.getId())) {
				return fail(JBoltMsg.DATA_SAME_NAME_EXIST); 
			}
		}else {
			if(checkNameExist(mpId,name,-1L)) {
				return fail(JBoltMsg.DATA_SAME_NAME_EXIST); 
			}
		}
		
		wechatKeywords.setName(name);
		if(update) {
			success=wechatKeywords.update();
		}else {
			success=wechatKeywords.save();
		}
		
		if(success) {
			//添加日志
			Kv checkResultKv=checkRet.getAs("data");
			String append=checkResultKv.getStr("append");
			addSystemLog(wechatKeywords.getId(), JBoltUserKit.getUserId(), update?JBoltSystemLogType.UPDATE.getValue():JBoltSystemLogType.SAVE.getValue(), JBoltSystemLogTargetType.WECHAT_KEYWORDS.getValue(), wechatKeywords.getName(), append);
		}
		return ret(success);
	}
	/**
	 * 检测数据name是否存在重名
	 * @param mpId
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean checkNameExist(Long mpId, String name, Long id) {
		Sql sql=selectSql().selectId().eqQM("mp_id","name").idNotEqQM().first();
		Integer existId=queryInt(sql,mpId,name,id);
		return isOk(existId);
	}
	/**
	 * 删除
	 * @param autoReplyId
	 * @param id
	 * @return
	 */
	public Ret delete( Long autoReplyId, Long id) {
		Ret checkRet=checkCanOpt(autoReplyId,id);
		if(checkRet.isFail()) {
			return checkRet;
		}
		Ret ret=deleteById(id);
		if(ret.isOk()) {
			Kv result=checkRet.getAs("data");
			addSystemLog(id, JBoltUserKit.getUserId(),  JBoltSystemLogType.DELETE.getValue(), JBoltSystemLogTargetType.WECHAT_REPLYCONTENT.getValue(), "ID:"+id, result.getStr("append"));
		}
		return ret;
	}
	/**
	 * 刪除一个规则下的关联子表 关键词回复设置的关键词
	 * @param autoReplyId
	 */
	public void deleteByAutoReplyId(Long autoReplyId) {
		if(isOk(autoReplyId)) {
			deleteBy(Okv.by("auto_reply_id", autoReplyId));
		}
	}
	/**
	 * 找到公众平台里配置的全等关键词
	 * @param mpId
	 * @param keywords
	 * @return
	 */
	public WechatKeywords getEqualsTypeKeywords(Long mpId, String keywords) {
		if(notOk(mpId)||notOk(keywords)) {return null;}
		return findFirst(Okv.by("mp_id", mpId).set("type",WechatKeywordsType.EQUALS.getValue()).set("name",keywords.trim()));
	}
	/**
	 * 随机一个公众平台里配置的模糊匹配关键词
	 * @param mpId
	 * @param keywords
	 * @return
	 */
	public WechatKeywords getRandLikeTypeKeywords(Long mpId, String keywords) {
		if(notOk(mpId)||notOk(keywords)) {return null;}
		String sql="select * from #(table) where mp_id=#(mpId) and type=#(type) and name like '%#(keywords)%' order by rand()";
		Kv data=Kv.create();
		data.set("mpId", mpId)
		.set("table",table())
		.set("type", WechatKeywordsType.LIKE.getValue())
		.set("keywords",keywords.trim());
		return dao().templateByString(sql, data).findFirst();
	}
	@Override
	protected int systemLogTargetType() {
		return JBoltSystemLogTargetType.WECHAT_KEYWORDS.getValue();
	}
	 
}
