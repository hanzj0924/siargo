package cn.jbolt.admin.wechat.autoreply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jbolt.common.enums.WechatAutoreplyReplyType;
import cn.jbolt.common.enums.WechatAutoreplyType;
import cn.jbolt.common.enums.WechatReplyContentType;
import cn.jbolt.core.cache.JBoltWechatConfigCache;
import cn.jbolt.core.cache.JBoltWechatMpinfoCache;
import cn.jbolt.core.enumutil.JBoltEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.CustomServiceApi.Articles;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutImageMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutMusicMsg;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import com.jfinal.weixin.sdk.msg.out.OutVideoMsg;
import com.jfinal.weixin.sdk.msg.out.OutVoiceMsg;

import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.config.WechatConfigService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.common.model.WechatKeywords;
import cn.jbolt.common.model.WechatReplyContent;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.common.enums.JBoltSystemLogTargetType;
import cn.jbolt.core.common.enums.JBoltSystemLogType;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.WechatConfig;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.permission.JBoltUserAuthKit;
import cn.jbolt.core.service.base.JBoltBaseService;
import cn.jbolt.core.util.JBoltRealUrlUtil;
import org.jsoup.safety.Safelist;

/**   
 * 自动回复内容管理Service
 * @ClassName:  WechatReplyContnet   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月14日 上午4:49:53   
 */
public class WechatReplyContentService extends JBoltBaseService<WechatReplyContent> {
	private WechatReplyContent dao = new WechatReplyContent().dao();
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatConfigService wechatConfigService;
	@Inject
	private WechatKeywordsService wechatKeywordsService;
	@Override
	protected WechatReplyContent dao() {
		return dao;
	}
	/**
	 *  刪除指定公众平台的配置
	 * @param mpId
	 * @return
	 */
	public Ret deleteByMpId(Long mpId) {
		return deleteBy(Okv.by("mp_id", mpId));
	}
	/**
	 * 得到一个回复规则下的已经启用的replycontent
	 * @param autoReplyId
	 * @return
	 */
	public List<WechatReplyContent> getEnableListByAutoReplyId(Long autoReplyId) {
		if(notOk(autoReplyId)) {return Collections.emptyList();}
		return getCommonList(Okv.by("auto_reply_id", autoReplyId).set("enable",TRUE), "sort_rank", "asc");
	}
	/**
	 * 得到一个回复规则下的所有replycontent
	 * @param autoReplyId
	 * @return
	 */
	public List<WechatReplyContent> getListByAutoReplyId(Long autoReplyId) {
		if(notOk(autoReplyId)) {return Collections.emptyList();}
		return getCommonList(Okv.by("auto_reply_id", autoReplyId), "sort_rank", "asc");
	}
	/**
	 * 检测操作权
	 * @param autoReplyId
	 * @return
	 */
	public Ret checkPermission(Long autoReplyId) {
			WechatAutoreply wechatAutoreply=wechatAutoReplyService.findById(autoReplyId);
			if(wechatAutoreply==null) {
				return fail("消息回复规则信息不存在");
			}
			boolean success=false;
			Long userId = JBoltUserKit.getUserId();
			WechatAutoreplyType wechatAutoreplyType = JBoltEnum.getEnumObjectByValue(WechatAutoreplyType.class,wechatAutoreply.getType().intValue());
			if(wechatAutoreplyType!=null){
				switch (wechatAutoreplyType) {
				case SUBSCRIBE:
					success=JBoltUserAuthKit.hasPermission(userId, true, PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE);
					break;
				case KEYWORDS:
					success=JBoltUserAuthKit.hasPermission(userId, true, PermissionKey.WECHAT_AUTOREPLY_KEYWORDS);
					break;
				case DEFAULT:
					success=JBoltUserAuthKit.hasPermission(userId, true, PermissionKey.WECHAT_AUTOREPLY_DEFAULT);
					break;
				}
			}
			return success?success(wechatAutoreply,JBoltMsg.SUCCESS):fail(JBoltMsg.NOPERMISSION);
	}
	/**
	 * 保存
	 * @param autoReplyId
	 * @param wechatReplyContent
	 * @return
	 */
	public Ret save( Long autoReplyId, WechatReplyContent wechatReplyContent) {
		return submit(autoReplyId, wechatReplyContent, JBoltSystemLogType.SAVE.getValue());
	}
	/**
	 *  更新
	 * @param autoReplyId
	 * @param wechatReplyContent
	 * @return
	 */
	public Ret update(Long autoReplyId, WechatReplyContent wechatReplyContent) {
		return submit(autoReplyId, wechatReplyContent, JBoltSystemLogType.UPDATE.getValue());
	}
	/**
	 * 提交
	 * @param autoReplyId
	 * @param wechatReplyContent
	 * @param systemLogType
	 * @return
	 */
	public Ret submit(Long autoReplyId, WechatReplyContent wechatReplyContent,int systemLogType) {
		if(notOk(autoReplyId)||wechatReplyContent==null||notOk(wechatReplyContent.getAutoReplyId())||notOk(wechatReplyContent.getType())
				||(systemLogType==JBoltSystemLogType.SAVE.getValue()&&isOk(wechatReplyContent.getId()))||(systemLogType==JBoltSystemLogType.UPDATE.getValue()&&notOk(wechatReplyContent.getId()))
				||autoReplyId.longValue()!=wechatReplyContent.getAutoReplyId().longValue()
				) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		 
		//检测操作权限
		Ret ret=checkPermission(autoReplyId);
		if(ret.isFail()) {return ret;}
		
		WechatAutoreply wechatAutoreply=ret.getAs("data");
		Long mpId=wechatAutoreply.getMpId();
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {return fail("关联微信公众平台不存在");}
		
		//校验输入 
		boolean success=false;
		WechatReplyContentType wechatReplyContentType = JBoltEnum.getEnumObjectByValue(WechatReplyContentType.class,wechatReplyContent.getType());
		if(wechatReplyContentType!=null) {
			switch (wechatReplyContentType) {
				case TEXT:
					if (notOk(wechatReplyContent.getContent())) {
						return fail("请输入描述内容");
					}
					OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
					String content = Jsoup.clean(wechatReplyContent.getContent(), "", Safelist.basic().removeEnforcedAttribute("a", "rel"), outputSettings);
					wechatReplyContent.setContent(content);
					break;
				case IMG:
					if (notOk(wechatReplyContent.getUrl())) {
						return fail("请选择图片");
					}
					break;
				case VIDEO:
					if (notOk(wechatReplyContent.getMediaId())) {
						return fail("请选择视频素材");
					}
					break;
				case VOICE:
					if (notOk(wechatReplyContent.getMediaId())) {
						return fail("请选择语音素材");
					}
					break;
				case MUSIC:
					if (notOk(wechatReplyContent.getUrl())) {
						return fail("请填写音乐地址");
					}
					break;
				case NEWS:
					if (notOk(wechatReplyContent.getTitle())) {
						return fail("请填写标题");
					}
					if (notOk(wechatReplyContent.getContent())) {
						return fail("请填写描述内容");
					}
					if (notOk(wechatReplyContent.getUrl())) {
						return fail("请填写链接地址");
					}
					break;
			}
		}
		
		//强制定死了绑定的MPID autoReplyId
		wechatReplyContent.setAutoReplyId(autoReplyId);
		wechatReplyContent.setMpId(wechatAutoreply.getMpId());
		if(systemLogType==JBoltSystemLogType.SAVE.getValue()) {
			wechatReplyContent.setSortRank(getNextSortRank(Okv.by("auto_reply_id", autoReplyId)));
			wechatReplyContent.setEnable(false);
			success=wechatReplyContent.save();
		}else {
			success=wechatReplyContent.update();
		}
		if(success) {
			processCache(mpId, wechatAutoreply.getType());
			//添加日志
			addSystemLog(wechatReplyContent.getId(), JBoltUserKit.getUserId(), systemLogType, JBoltSystemLogTargetType.WECHAT_REPLYCONTENT.getValue(), "ID:"+wechatReplyContent.getId(),"所属公众平台：["+wechatMpinfo.getName()+"] 规则:["+wechatAutoreply.getType()+"]["+wechatAutoreply.getName()+"]");
		}
		return ret(success);
	}
	/**
	 * 删除
	 * @param autoReplyId
	 * @param id
	 * @return
	 */
	public Ret delete(Long autoReplyId, Long id) {
		Ret checkRet=checkCanOpt(autoReplyId,id);
		if(checkRet.isFail()) {
			return checkRet;
		}
		Ret ret=deleteById(id);
		if(ret.isOk()) {
			Kv result=checkRet.getAs("data");
			WechatReplyContent wechatReplyContent=result.getAs("wechatReplyContent");
			//删除后需要把此数据之后的数据更新顺序
			updateSortRankAfterDelete(Okv.by("auto_reply_id",autoReplyId),wechatReplyContent.getSortRank());
			addSystemLog(id, JBoltUserKit.getUserId(),  JBoltSystemLogType.DELETE.getValue(), JBoltSystemLogTargetType.WECHAT_REPLYCONTENT.getValue(), "ID:"+id, result.getStr("append"));
		}
		return ret;
	}
	/**
	 * 执行删除和修改操作前 检查一下参数和数据
	 * @param autoReplyId
	 * @return
	 */
	public Ret checkCanOpt(Long autoReplyId) {
		if(notOk(autoReplyId)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Ret ret=checkPermission(autoReplyId);
		if(ret.isFail()) {
			return ret;
		}
		WechatAutoreply wechatAutoreply=ret.getAs("data");
		Long mpId=wechatAutoreply.getMpId();
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {
			return fail("关联微信公众平台不存在");
		}
		String append="所属公众平台：["+wechatMpinfo.getName()+"] 规则:["+wechatAutoreply.getType()+"]["+wechatAutoreply.getName()+"]";
		return success(Kv.by("append", append).set("wechatAutoreply",wechatAutoreply), JBoltMsg.SUCCESS);
	}
	/**
	 * 执行删除和修改操作前 检查一下参数和数据
	 * @param autoReplyId
	 * @param id
	 * @return
	 */
	public Ret checkCanOpt(Long autoReplyId,Long id) {
		if(notOk(autoReplyId)||notOk(id)) {
			return fail(JBoltMsg.PARAM_ERROR);
		}
		Ret ret=checkCanOpt(autoReplyId);
		if(ret.isFail()) {
			return ret;
		}
	 
		WechatReplyContent wechatReplyContent=findById(id);
		if(wechatReplyContent==null) {
			return fail(JBoltMsg.DATA_NOT_EXIST);
		}
		if(wechatReplyContent.getEnable()==null) {
			wechatReplyContent.setEnable(false);
		}
		Kv kv=ret.getAs("data");
		kv.set("wechatReplyContent", wechatReplyContent);
		return success(kv,JBoltMsg.SUCCESS);
	}
	/**
	 * 切换启用状态
	 * @param autoReplyId
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Long autoReplyId, Long id) {
		Ret ret=checkCanOpt(autoReplyId,id);
		if(ret.isFail()) {
			return ret;
		}
		Kv result=ret.getAs("data");
		WechatReplyContent wechatReplyContent=result.getAs("wechatReplyContent");
		wechatReplyContent.setEnable(!wechatReplyContent.getEnable());
		boolean success=wechatReplyContent.update();
		if(success) {
			WechatAutoreply wechatAutoreply=result.getAs("wechatAutoreply");
			processCache(wechatAutoreply.getMpId(),wechatAutoreply.getType());
			//添加日志
			String append=result.getStr("append");
			addSystemLog(id, JBoltUserKit.getUserId(),  JBoltSystemLogType.UPDATE.getValue(), JBoltSystemLogTargetType.WECHAT_REPLYCONTENT.getValue(), "ID:"+id, "的启用状态："+wechatReplyContent.getEnable()+" "+append);
		}
		return ret(success);
	}

	private void processCache(Long mpId, Integer type) {
//		switch (type.intValue()) {
//		case WechatAutoreply.TYPE_DEFAULT:
//			CACHE.me.removeWechcatDefaultOutMsg(mpId);
//			break;
//		case WechatAutoreply.TYPE_SUBSCRIBE:
//			CACHE.me.removeWechcatSubscribeOutMsg(mpId);
//			break;
//		}
		
	}

	/**
	 * 上移
	 * @param id
	 * @return
	 */
	public Ret doUp(Long autoReplyId,Long id) {
		Ret ret=checkCanOpt(autoReplyId,id);
		if(ret.isFail()) {
			return ret;
		}
		Kv result=ret.getAs("data");
		WechatReplyContent wechatReplyContent=result.getAs("wechatReplyContent");
		Integer rank=wechatReplyContent.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		if(rank==1){
			return fail("已经是第一个");
		}
		WechatReplyContent upWechatReplyContent=findFirst(Okv.by("sort_rank", rank-1).set("auto_reply_id",autoReplyId));
		if(upWechatReplyContent==null){
			return fail("顺序需要初始化");
		}
		upWechatReplyContent.setSortRank(rank);
		wechatReplyContent.setSortRank(rank-1);
		upWechatReplyContent.update();
		wechatReplyContent.update();
		WechatAutoreply wechatAutoreply=result.getAs("wechatAutoreply");
		processCache(wechatAutoreply.getMpId(), wechatAutoreply.getType());
		return SUCCESS;
	}
 
	
	
	/**
	 * 下移
	 * @param id
	 * @return
	 */
	public Ret doDown(Long autoReplyId,Long id) {
		Ret ret=checkCanOpt(autoReplyId,id);
		if(ret.isFail()) {
			return ret;
		}
		Kv result=ret.getAs("data");
		WechatReplyContent wechatReplyContent=result.getAs("wechatReplyContent");
		Integer rank=wechatReplyContent.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		int max=getCount(Okv.by("auto_reply_id",autoReplyId));
		if(rank==max){
			return fail("已经是最后一个");
		}
		WechatReplyContent upWechatReplyContent=findFirst(Okv.by("sort_rank", rank+1).set("auto_reply_id", autoReplyId));
		if(upWechatReplyContent==null){
			return fail("顺序需要初始化");
		}
		upWechatReplyContent.setSortRank(rank);
		wechatReplyContent.setSortRank(rank+1);
		upWechatReplyContent.update();
		wechatReplyContent.update();
		WechatAutoreply wechatAutoreply=result.getAs("wechatAutoreply");
		processCache(wechatAutoreply.getMpId(), wechatAutoreply.getType());
		return SUCCESS;
	}
	/**
	  * 初始化
	 * @param autoReplyId
	 * @return
	 */
	public Ret doInitRank(Long autoReplyId) {
		Ret ret=checkCanOpt(autoReplyId);
		if(ret.isFail()) {
			return ret;
		}
		List<WechatReplyContent> contents=getCommonList(Okv.by("auto_reply_id", autoReplyId));
		if(isOk(contents)) {
			int size=contents.size();
			for(int i=0;i<size;i++) {
				contents.get(i).setSortRank(i+1);
			}
			batchUpdate(contents, size);
		}
		return SUCCESS;
	}
	/**
	 * 刪除一个规则下的管理子表 回复内容
	 * @param autoReplyId
	 */
	public void deleteByAutoReplyId(Long autoReplyId) {
		if(isOk(autoReplyId)) {
			deleteBy(Okv.by("auto_reply_id", autoReplyId));
		}
	}
	/**
	 * 得到一个公众平台配置的关注回复内容
	 * @param appId
	 * @param openId 
	 * @return
	 */
	public OutMsg getWechcatSubscribeOutMsg(String appId, String openId) {
		return getWechcatSubscribeOrDefaultOutMsg(appId, WechatAutoreplyType.SUBSCRIBE.getValue(),openId);
	}
	/**
	 * 得到一个公众平台配置的关键词回复内容
	 * @param appId
	 * @return
	 */
	public OutMsg getWechcatKeywordsOutMsg(String appId,String keywords,String openId) {
		if(notOk(appId)||notOk(keywords)) {return null;}
		//根据APPID 得到具体配置项
		WechatConfig appIdConfig=wechatConfigService.getAppIdConfig(appId);
		if(appIdConfig==null) {return null;}
		//得到对应的是哪个公众平台
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(appIdConfig.getMpId());
		if(wechatMpinfo==null) {return null;}
		//先去wechatKeywords里寻找全等的关键词
		WechatKeywords wechatKeywords=wechatKeywordsService.getEqualsTypeKeywords(wechatMpinfo.getId(),keywords);
		if(wechatKeywords==null) {
			//如果没找到全等的 就去找模糊匹配的 那得随机一个吧
			wechatKeywords=wechatKeywordsService.getRandLikeTypeKeywords(wechatMpinfo.getId(),keywords);
		}
		//TODO 如果还是找不到关键词 就可以去本地微信文章里找 去wechatMedia里找
		if(wechatKeywords==null) {
			return null;
		}
			//如果找到了 直接用它所在规则 去找返回content
			WechatAutoreply wechatAutoreply=wechatAutoReplyService.findById(wechatKeywords.getAutoReplyId());
			if(wechatAutoreply==null) {return null;}
			
			OutMsg outMsg=null;
			WechatAutoreplyReplyType wechatAutoreplyReplyType = JBoltEnum.getEnumObjectByValue(WechatAutoreplyReplyType.class,wechatAutoreply.getReplyType().intValue());
			if(wechatAutoreplyReplyType!=null){
				//根据类型 返回不同outMsg
				switch (wechatAutoreplyReplyType) {
					case RANDOMONE://随机一条
						outMsg=getRandomWechatOutMsg(appIdConfig.getMpId(),wechatAutoreply.getId());
						break;
					case ALL://全部返回
						if(wechatMpinfo.getIsAuthenticated()) {
							//已经认证的可以启用客服消息
							outMsg=sendAllWechatOutMsg(appIdConfig.getMpId(),wechatAutoreply.getId(),openId);
						}else {
							//没有认证的就随机一个
							outMsg=getRandomWechatOutMsg(appIdConfig.getMpId(),wechatAutoreply.getId());
						}
						break;
				}

			}
			return outMsg;
	}
	/**
	 * 得到一个公众平台配置的默认回复内容
	 * @param appId
	 * @return
	 */
	public OutMsg getWechcatDefaultOutMsg(String appId,String openId) {
		return getWechcatSubscribeOrDefaultOutMsg(appId, WechatAutoreplyType.DEFAULT.getValue(),openId);
	}
	/**
	 * 得到一个公众平台配置的回复内容
	 * @param appId
	 * @param openId 
	 * @return
	 */
	private OutMsg getWechcatSubscribeOrDefaultOutMsg(String appId,int type, String openId) {
		if(notOk(appId)||notOk(openId)) {return null;}
		//根据APPID 得到具体配置项
		WechatConfig appIdConfig= JBoltWechatConfigCache.me.getAppIdConfig(appId);
		if(appIdConfig==null) {return null;}
		//得到对应的是哪个公众平台
		WechatMpinfo wechatMpinfo= JBoltWechatMpinfoCache.me.get(appIdConfig.getMpId());
		if(wechatMpinfo==null) {return null;}
		
		//然后就可以拿到自定义回复的配置了
		WechatAutoreply wechatAutoreply=wechatAutoReplyService.getTheEnableAutoReply(appIdConfig.getMpId(),type);
		if(wechatAutoreply==null) {return null;}
		
		OutMsg outMsg=null;
		WechatAutoreplyReplyType wechatAutoreplyReplyType = JBoltEnum.getEnumObjectByValue(WechatAutoreplyReplyType.class,wechatAutoreply.getReplyType().intValue());
		if(wechatAutoreplyReplyType!=null){

		//根据类型 返回不同outMsg
		switch (wechatAutoreplyReplyType) {
			case RANDOMONE://随机一条
				outMsg=getRandomWechatOutMsg(appIdConfig.getMpId(),wechatAutoreply.getId());
				break;
			case ALL://全部返回
				if(type==WechatAutoreplyType.SUBSCRIBE.getValue()) {
					//如果是关注 可以返回多图文
					outMsg=processWechatSubscruibeNews(appIdConfig.getMpId(),wechatAutoreply.getId());
					if(outMsg==null) {//如果没有图文类型的 就按照下面逻辑进行
						if(wechatMpinfo.getIsAuthenticated()) {
							outMsg=sendAllWechatOutMsg(appIdConfig.getMpId(),wechatAutoreply.getId(),openId);
						}else {
							outMsg=getRandomWechatOutMsg(appIdConfig.getMpId(),wechatAutoreply.getId());
						}
					}else {//如果有图文返回后 就调用客服接口返回非图文的
						if(wechatMpinfo.getIsAuthenticated()) {
							sendSubscribeWechatOutMsgWithoutNews(appIdConfig.getMpId(),wechatAutoreply.getId(),openId);
						}
					}
				}else {
					if(wechatMpinfo.getIsAuthenticated()) {
						outMsg=sendAllWechatOutMsg(appIdConfig.getMpId(),wechatAutoreply.getId(),openId);
					}else {
						outMsg=getRandomWechatOutMsg(appIdConfig.getMpId(),wechatAutoreply.getId());
					}
				}
				
				break;
		}

		}
		return outMsg;
	}
	/**
	 * 认证的公众号 关注时候 除了返回多图文 如果还有其他类型的数据 需要调用客服接口返回
	 * @param mpId
	 * @param autoReplyId
	 * @param openId
	 * @return
	 */
	private OutMsg sendSubscribeWechatOutMsgWithoutNews(Long mpId, Long autoReplyId, String openId) {
		List<WechatReplyContent> wechatReplyContents=getEnableListByAutoReplyIdWithoutNews(autoReplyId);
		if(wechatReplyContents!=null&&wechatReplyContents.size()>0) {
			for(WechatReplyContent replyContent:wechatReplyContents) {
				sendOneReplyContentByCustomService(replyContent,openId);
			}
			OutTextMsg outTextMsg=new OutTextMsg();
			outTextMsg.setContent("rendernull");
			return outTextMsg;
		}
		return null;
	}
	/**
	 * 获取一个回复规则下除了news类型之外的其他可用回复
	 * @param autoReplyId
	 * @return
	 */
	private List<WechatReplyContent> getEnableListByAutoReplyIdWithoutNews(Long autoReplyId) {
		if(notOk(autoReplyId)) {return Collections.emptyList();}
		return getCommonList(Okv.by("auto_reply_id =", autoReplyId).set("type <> ",WechatReplyContentType.NEWS.getValue()).set("enable =",TRUE), "sort_rank", "asc",true);
	}
	/**
	 * 返回关注后回复的多图文
	 * @param mpId
	 * @param autoReplyId
	 * @return
	 */
	private OutMsg processWechatSubscruibeNews(Long mpId, Long autoReplyId) {
		Page<WechatReplyContent> page=paginate(Okv.by("mp_id", mpId).set("type",WechatReplyContentType.NEWS.getValue()).set("auto_reply_id",autoReplyId).set("enable",TRUE),1,8);
		if(page.getTotalRow()==0) {
			return null;
		}
		OutNewsMsg outMsg=new OutNewsMsg();
		outMsg.setArticles(converToArticles(page.getList()));
		return outMsg;
	}
	/**
	 *    得到一个规则下的所有可回复内容
	 * @param mpId
	 * @param autoReplyId
	 * @param openId 
	 * @return
	 */
	private OutMsg sendAllWechatOutMsg(Long mpId, Long autoReplyId,String openId) {
		List<WechatReplyContent> wechatReplyContents=getEnableListByAutoReplyId(autoReplyId);
		if(wechatReplyContents!=null&&wechatReplyContents.size()>0) {
			for(WechatReplyContent replyContent:wechatReplyContents) {
				sendOneReplyContentByCustomService(replyContent,openId);
			}
			OutTextMsg outTextMsg=new OutTextMsg();
			outTextMsg.setContent("rendernull");
			return outTextMsg;
		}
		return null;
	}
	 

	/**
	 * 将一个replyContent 发送给指定openId的用户
	 * 使用客服消息接口
	 * @param wechatReplyContent
	 * @param openId
	 */
	private void sendOneReplyContentByCustomService(WechatReplyContent wechatReplyContent, String openId) {
		WechatReplyContentType wechatReplyContentType = JBoltEnum.getEnumObjectByValue(WechatReplyContentType.class,wechatReplyContent.getType());
		if(wechatReplyContentType!=null) {
			switch (wechatReplyContentType) {
				case TEXT:
					CustomServiceApi.sendText(openId, wechatReplyContent.getContent());
					break;
				case NEWS:
					CustomServiceApi.sendNews(openId, converToCustomArticles(wechatReplyContent));
					break;
				case IMG:
					CustomServiceApi.sendImage(openId, wechatReplyContent.getMediaId());
					break;
				case VOICE:
					CustomServiceApi.sendVoice(openId, wechatReplyContent.getMediaId());
					break;
				case VIDEO:
					CustomServiceApi.sendVideo(openId, wechatReplyContent.getMediaId(), wechatReplyContent.getTitle(), wechatReplyContent.getContent());
					break;
				case MUSIC:
					sendMusicCustomService(openId, wechatReplyContent);
					break;
			}
		}
		
	}
	/**
	 * 单独处理发送music
	 * @param openId
	 * @param wechatReplyContent
	 */
	private void sendMusicCustomService(String openId, WechatReplyContent wechatReplyContent) {
		String mediaId=wechatConfigService.getWechatConfigMusicPostMediaId(wechatReplyContent.getMpId());
		if(StrKit.notBlank(mediaId)) {
			CustomServiceApi.sendMusic(openId,  wechatReplyContent.getUrl(), wechatReplyContent.getUrl(),mediaId,wechatReplyContent.getTitle(), wechatReplyContent.getContent());
		}else {
			System.out.println("wechatMpInfo:id:"+wechatReplyContent.getId()+" 没有配置Music_POST_MediaId");
		}
		
	}

	public List<WechatReplyContent> getListByType(Long mpId,Long autoReplyId, String type) {
		return getCommonList(Okv.by("mp_id",mpId).set("auto_reply_id", autoReplyId).set("type",type));
	}

	/**
	 * 得到一个公众号 具体一个规则下的随机一个回复内容
	 * @param mpId
	 * @param autoReplyId
	 * @return
	 */
	private OutMsg getRandomWechatOutMsg(Long mpId, Long autoReplyId) {
		WechatReplyContent wechatReplyContent=getRandomOneEnableReplyContent(mpId,autoReplyId);
		if(wechatReplyContent==null) {return null;}
		OutMsg outMsg=null;
		WechatReplyContentType wechatReplyContentType = JBoltEnum.getEnumObjectByValue(WechatReplyContentType.class,wechatReplyContent.getType());
		if(wechatReplyContentType!=null) {
			switch (wechatReplyContentType) {
				case TEXT:
					outMsg = processOneTextOutMsg(wechatReplyContent);
					break;
				case NEWS:
					outMsg = processOneNewsOutMsg(wechatReplyContent);
					break;
				case IMG:
					outMsg = processOneImageOutMsg(wechatReplyContent);
					break;
				case VOICE:
					outMsg = processOneVoiceOutMsg(wechatReplyContent);
					break;
				case VIDEO:
					outMsg = processOneVideoOutMsg(wechatReplyContent);
					break;
				case MUSIC:
					outMsg = processOneMusicOutMsg(wechatReplyContent);
					break;
			}
		}
		return outMsg;
	}
	/**
	 * 随机获取一个公众号规则下的启用的回复内容
	 * @param mpId
	 * @param autoReplyId
	 * @return
	 */
	private WechatReplyContent getRandomOneEnableReplyContent(Long mpId, Long autoReplyId) {
		return getRandomOne(Okv.by("mp_id", mpId).set("auto_reply_id",autoReplyId).set("enable",TRUE));
	}

	/**
	 *  将一个音乐类回复内容转为微信的OUTMSG
	 * @param wechatReplyContent
	 * @return
	 */
	private OutMusicMsg processOneMusicOutMsg(WechatReplyContent wechatReplyContent) {
		OutMusicMsg outMusicMsg=new OutMusicMsg();
		outMusicMsg.setDescription(wechatReplyContent.getContent());
		outMusicMsg.setTitle(wechatReplyContent.getTitle());
		outMusicMsg.setMusicUrl(wechatReplyContent.getUrl());
		return outMusicMsg;
	}

	/**
	 *   将一个视频类回复内容转为微信的OUTMSG
	 * @param wechatReplyContent
	 * @return
	 */
	private OutVideoMsg processOneVideoOutMsg(WechatReplyContent wechatReplyContent) {
		OutVideoMsg outVideoMsg=new OutVideoMsg();
		outVideoMsg.setMediaId(wechatReplyContent.getMediaId());
		outVideoMsg.setTitle(wechatReplyContent.getTitle());
		outVideoMsg.setDescription(wechatReplyContent.getContent());
		return outVideoMsg;
	}

	/**
	 * 将一个语音类回复内容转为微信的OUTMSG
	 * @param wechatReplyContent
	 * @return
	 */
	private OutVoiceMsg processOneVoiceOutMsg(WechatReplyContent wechatReplyContent) {
		OutVoiceMsg outVoiceMsg =new OutVoiceMsg();
		outVoiceMsg.setMediaId(wechatReplyContent.getMediaId());
		return outVoiceMsg;
	}

	/**
	 * 将一个文字类回复内容转为微信的OUTMSG
	 * @param wechatReplyContent
	 * @return
	 */
	private OutTextMsg processOneTextOutMsg(WechatReplyContent wechatReplyContent) {
		OutTextMsg outTextMsg=new OutTextMsg();
		outTextMsg.setContent(wechatReplyContent.getContent());
		return outTextMsg;
	}

	/**
	 * 将一个图片类回复内容转为微信的OUTMSG
	 * @param wechatReplyContent
	 * @return
	 */
	private OutImageMsg processOneImageOutMsg(WechatReplyContent wechatReplyContent) {
		OutImageMsg outImageMsg=new OutImageMsg();
		outImageMsg.setMediaId(wechatReplyContent.getMediaId());
		return outImageMsg;
	}

	/**
	 * 将一个图文类的回复内容转换为微信的OUTMSG
	 * @param wechatReplyContent
	 * @return
	 */
	private OutNewsMsg processOneNewsOutMsg(WechatReplyContent wechatReplyContent) {
		OutNewsMsg outNewMsg=new OutNewsMsg();
		outNewMsg.setArticles(converToArticles(wechatReplyContent));
		return outNewMsg;
	}

	private List<News> converToArticles(WechatReplyContent wechatReplyContent) {
		List<News> articles=new ArrayList<News>();
		articles.add(converToNews(wechatReplyContent));
		return articles;
	}
	private News converToNews(WechatReplyContent wechatReplyContent) {
		News news=new News();
		news.setTitle(wechatReplyContent.getTitle());
		news.setDescription(wechatReplyContent.getContent());
		news.setUrl(wechatReplyContent.getUrl());
		news.setPicUrl(JBoltRealUrlUtil.getWechatImage(wechatReplyContent.getPoster(), "assets/img/defaultposter.jpg"));
		return news;
	}
	private List<News> converToArticles(List<WechatReplyContent> wechatReplyContents) {
		List<News> articles=new ArrayList<News>();
		for(WechatReplyContent wechatReplyContent:wechatReplyContents) {
			articles.add(converToNews(wechatReplyContent));
		}
		return articles;
	}
	private List<Articles> converToCustomArticles(WechatReplyContent wechatReplyContent) {
		List<Articles> articlesList=new ArrayList<Articles>();
		Articles articles=new Articles();
		articles.setTitle(wechatReplyContent.getTitle());
		articles.setDescription(wechatReplyContent.getContent());
		articles.setUrl(wechatReplyContent.getUrl());
		articles.setPicurl(JBoltRealUrlUtil.getWechatImage(wechatReplyContent.getPoster(), "assets/img/defaultposter.jpg"));
		articlesList.add(articles);
		return articlesList;
	}
	@Override
	protected int systemLogTargetType() {
		return JBoltSystemLogTargetType.WECHAT_REPLYCONTENT.getValue();
	}

}
