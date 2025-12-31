package cn.jbolt.admin.wechat.autoreply;

import java.util.Date;

import cn.jbolt.common.enums.WechatAutoreplyType;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.common.enums.JBoltSystemLogTargetType;
import cn.jbolt.core.common.enums.JBoltSystemLogType;
import cn.jbolt.core.db.sql.Sql;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.service.base.JBoltBaseService;

/**
 * 微信公众号回复规则
 * @ClassName:  WechatAutoReplyService
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2019年5月8日 下午11:58:43
 */
public class WechatAutoReplyService extends JBoltBaseService<WechatAutoreply> {
	private WechatAutoreply dao = new WechatAutoreply().dao();
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatKeywordsService wechatKeywordsService;
	@Inject
	private WechatReplyContentService wechatReplyContentService;

	@Override
	protected WechatAutoreply dao() {
		return dao;
	}

	/**
	 * 检测指定公众平台是否已经有配置了
	 * @param mpId
	 * @return
	 */
	public boolean checkWechatMpinfoInUse(Long mpId) {
		return exists("mp_id", mpId);
	}
	/**
	 * 删除指定公众平台的自动回复模块的配置信息
	 * @param mpId
	 */
	public Ret deleteByMpId(Long mpId) {
		if(JBoltConfig.DEMO_MODE) {return fail(JBoltMsg.DEMO_MODE_CAN_NOT_DELETE);}
		//关键词
		wechatKeywordsService.deleteByMpId(mpId);
		//回复
		wechatReplyContentService.deleteByMpId(mpId);
		//自身
		return deleteBy(Okv.by("mp_id", mpId));

	}
	/**
	 * 分页条件查询自动回复规则
	 * @param mpId
	 * @param type
	 * @param keywords
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<WechatAutoreply> paginateAdminMgrList(Long mpId, Integer type,
			String keywords, int pageNumber,int pageSize) {
		Sql sql = selectSql().page(pageNumber,pageSize);
		sql.eq("mp_id", mpId);
		sql.eq("type", type);
		sql.like("name",keywords);
		sql.orderById(true);
		return paginate(sql);
		//return daoTemplate(
		//		"wechat.autoreply.paginateAdminList",
		//		Okv.by("type", type)
		//		.set("mpId",mpId)
		//		.setIfNotBlank("keywords", keywords)
		//		).paginate(pageNumber, pageSize);
	}
	/**
	 * 保存新建规则
	 * @param mpId
	 * @param type
	 * @param wechatAutoreply
	 * @return
	 */
	public Ret save(Long mpId, Integer type, WechatAutoreply wechatAutoreply) {
		return submit(mpId, type, wechatAutoreply, false);
	}
	/**
	 * 提交
	 * @param mpId
	 * @param type
	 * @param wechatAutoreply
	 * @param update
	 */
	public Ret submit(Long mpId, Integer type, WechatAutoreply wechatAutoreply,boolean update) {
		if(notOk(mpId)||notOk(type)||wechatAutoreply==null
				||(!update&&isOk(wechatAutoreply.getId()))
				||(update&&notOk(wechatAutoreply.getId()))
				||notOk(wechatAutoreply.getName())||notOk(wechatAutoreply.getType())||notOk(wechatAutoreply.getMpId())) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		//检测表单提交Model的参数和提交地址附带参数是否一致
		if(wechatAutoreply.getMpId().longValue()!=mpId.longValue()) {return fail("操作异常:公众平台mpId");}
		if(wechatAutoreply.getType().intValue()!=type.intValue()) {return fail("操作异常:公众平台type");}
		if(update) {
			//更新时需要判断数据存在
			WechatAutoreply dbAutoreply=findById(wechatAutoreply.getId());
			if(dbAutoreply==null) {return fail(JBoltMsg.DATA_NOT_EXIST);}
			//检测数据库数据和提交地址附带参数是否一致
			if(dbAutoreply.getMpId().longValue()!=mpId.longValue()) {return fail("操作异常:公众平台mpId");}
			if(dbAutoreply.getType().intValue()!=type.intValue()) {return fail("操作异常:公众平台type");}
		}
		//校验公共平台存在
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {return fail("关联的微信公众平台"+JBoltMsg.DATA_NOT_EXIST);}
		String name=wechatAutoreply.getName().trim();
		wechatAutoreply.setName(name);
		boolean success=false;
		//save和update分别处理
		if(update) {
			if(checkNameExist(mpId,type,name,wechatAutoreply.getId())) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
			success=wechatAutoreply.update();
		}else {
			if(checkNameExist(mpId,type,name,-1L)) {return fail(JBoltMsg.DATA_SAME_NAME_EXIST);}
			wechatAutoreply.setCreateTime(new Date());
			wechatAutoreply.setUserId(JBoltUserKit.getUserId());
			wechatAutoreply.setEnable(false);
			success=wechatAutoreply.save();
		}
		if(success) {
			//添加日志
			addSystemLog(wechatAutoreply.getId(), JBoltUserKit.getUserId(), update?JBoltSystemLogType.UPDATE.getValue():JBoltSystemLogType.SAVE.getValue(), JBoltSystemLogTargetType.WECHAT_AUTOREPLY.getValue(), wechatAutoreply.getName(),",属于公众平台【"+wechatMpinfo.getName()+"】");
		}
		return ret(success);
	}
	/**
	 * 检测重名 同一账号同一类型下不能重名
	 * @param mpId
	 * @param type
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean checkNameExist(Long mpId, Integer type,String name, Long id) {
		Sql sql=selectSql().selectId().eqQM("mp_id","type","name").idNotEqQM().first();
		Integer existId = queryInt(sql,mpId,type,name,id);
		return isOk(existId);
	}

	/**
	 * 更新规则
	 * @param mpId
	 * @param type
	 * @param wechatAutoreply
	 * @return
	 */
	public Ret update( Long mpId, Integer type, WechatAutoreply wechatAutoreply) {
		return submit(mpId, type, wechatAutoreply,true);
	}
	/**
	 * 删除
	 * @param mpId
	 * @param type
	 * @param id
	 * @return
	 */
	public Ret deleteAutoreply( Long mpId, Integer type, Long id) {
		if(JBoltConfig.DEMO_MODE) {return fail(JBoltMsg.DEMO_MODE_CAN_NOT_DELETE);}
		Ret ret=checkOptParas(mpId,type,id);
		if(ret.isFail()) {return ret;}
		Kv kv=ret.getAs("data");
		WechatAutoreply wechatAutoreply=kv.getAs("wechatAutoreply");
		WechatMpinfo wechatMpinfo=kv.getAs("wechatMpinfo");
		boolean success=wechatAutoreply.delete();
		if(success) {
			wechatKeywordsService.deleteByAutoReplyId(wechatAutoreply.getId());
			wechatReplyContentService.deleteByAutoReplyId(wechatAutoreply.getId());
			//添加日志
			addSystemLog(wechatAutoreply.getId(), JBoltUserKit.getUserId(), JBoltSystemLogType.DELETE.getValue(), JBoltSystemLogTargetType.WECHAT_AUTOREPLY.getValue(), wechatAutoreply.getName(),",属于公众平台【"+wechatMpinfo.getName()+"】");
		}
		return ret(success);
	}
	/**
	 * 更新删除操作时的校验处理
	 * @param mpId
	 * @param type
	 * @param id
	 * @return
	 */
	private Ret checkOptParas(Long mpId, Integer type, Long id) {
		if(notOk(mpId)||notOk(type)||notOk(id)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		WechatAutoreply wechatAutoreply=findById(id);
		if(wechatAutoreply.getMpId().longValue()!=mpId.longValue()) {return fail("操作异常:公众平台mpId");}
		if(wechatAutoreply.getType().intValue()!=type.intValue()) {return fail("操作异常:公众平台type");}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {return fail("关联的微信公众平台"+JBoltMsg.DATA_NOT_EXIST);}
		return success(Kv.by("wechatAutoreply", wechatAutoreply).set("wechatMpinfo",wechatMpinfo), JBoltMsg.SUCCESS);
	}
	/**
	 * 切换启用禁用状态
	 * @param mpId
	 * @param type
	 * @param id
	 * @return
	 */
	public Ret toggleEnable( Long mpId, Integer type, Long id) {
		Ret ret=checkOptParas(mpId,type,id);
		if(ret.isFail()) {return ret;}
		Kv kv=ret.getAs("data");
		WechatAutoreply wechatAutoreply=kv.getAs("wechatAutoreply");
		WechatMpinfo wechatMpinfo=kv.getAs("wechatMpinfo");
		if(wechatAutoreply.getEnable()==null) {
			wechatAutoreply.setEnable(false);
		}
		//执行切换之前需要处理一个事儿 就是判断是否必须只能启用唯一性
		if(type.intValue()== WechatAutoreplyType.DEFAULT.getValue()||type.intValue()==WechatAutoreplyType.SUBSCRIBE.getValue()) {
			if(wechatAutoreply.getEnable()==false) {
				//关注回复和无消息默认回复 需要唯一性处理
				changeCurrrentEnableToFalse(mpId,type);
			}
		}
		wechatAutoreply.setEnable(!wechatAutoreply.getEnable());
		boolean success=wechatAutoreply.update();
		if(success) {
			//添加日志
			addSystemLog(wechatAutoreply.getId(), JBoltUserKit.getUserId(), JBoltSystemLogType.UPDATE.getValue(), JBoltSystemLogTargetType.WECHAT_AUTOREPLY.getValue(), wechatAutoreply.getName(),"的启用状态:"+wechatAutoreply.getEnable()+"属于公众平台【"+wechatMpinfo.getName()+"】");
		}
		return ret(success);
	}
	/**
	 * 将指定类型数据的enable=true的设置为false
	 * @param mpId
	 * @param type
	 */
	private void changeCurrrentEnableToFalse(Long mpId,Integer type) {
		if(type.intValue()==WechatAutoreplyType.DEFAULT.getValue()||type.intValue()==WechatAutoreplyType.SUBSCRIBE.getValue()) {
			Sql sql=updateSql().set("enable", FALSE).eqQM("mp_id","enable","type");
			update(sql,mpId,TRUE,type);
		}
	}
	/**
	 * 查找指定公众平台下配置的一个启用的默认或者关注回复
	 * @param mpId
	 * @return
	 */
	public WechatAutoreply getTheEnableAutoReply(Long mpId,int type) {
		return findFirst(Okv.by("mp_id", mpId).set("type",type).set("enable",TRUE));
	}

	@Override
	protected int systemLogTargetType() {
		return JBoltSystemLogTargetType.WECHAT_AUTOREPLY.getValue();
	}



}
