package cn.jbolt.admin.wechat.menu;

import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Okv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MenuApi;

import cn.jbolt.admin.wechat.config.WechatConfigService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.model.WechatMenu;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.cache.JBoltWechatConfigCache;
import cn.jbolt.core.common.enums.JBoltSystemLogTargetType;
import cn.jbolt.core.enumutil.JBoltEnum;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.model.WechatMpinfo;
import cn.jbolt.core.service.base.JBoltBaseService;

/**   
 * 微信公众号的菜单管理Service
 * @ClassName:  WechatMenuService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日 下午11:56:35   
 */
public class WechatMenuService extends JBoltBaseService<WechatMenu> {
	private WechatMenu dao = new WechatMenu().dao();
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatConfigService wechatConfigService;

	@Override
	protected WechatMenu dao() {
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
	 * 删除一个公众平台的菜单配置
	 * @param mpId
	 * @return
	 */
	public Ret deleteByMpId(Long mpId) {
		return deleteBy(Okv.by("mp_id", mpId));
	}
	/**
	 * 得到一级菜单
	 * @param mpId
	 * @return
	 */
	public List<WechatMenu> getLevel1Menus(Long mpId) {
		return getCommonList(Okv.by("mp_id", mpId).set("pid",0),"sort_rank");
	}
	/**
	 * 得到二级菜单
	 * @param mpId
	 * @param pid
	 * @return
	 */
	public List<WechatMenu> getLevel2Menus(Long mpId,Long pid) {
		return getCommonList(Okv.by("mp_id", mpId).set("pid",pid),"sort_rank");
	}
	/**
	 * 得到子菜单
	 * @param mpId
	 * @param pid
	 * @return
	 */
	public List<WechatMenu> getListByPid(Long mpId,Long pid) {
		return getCommonList(Okv.by("mp_id", mpId).set("pid",pid==null?0:pid),"sort_rank");
	}
	/**
	 * 保存
	 * @param mpId
	 * @param menu
	 * @return
	 */
	public Ret save( Long mpId, WechatMenu menu) {
		if(notOk(mpId)||isOk(menu.getId())){return fail(JBoltMsg.PARAM_ERROR);}
		if(notOk(menu.getPid())){
			menu.setPid(0L);
		}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			return fail("微信公众平台信息不存在");
		}
		int count=getCountByPid(mpId, menu.getPid());
		if(menu.getPid()==0){
			if(count>=3){
				return fail("一级菜单最多3个");
			}
			menu.setType(JBoltWechatMenuType.TYPE_NONE.getValue());
		}else{
			if(count>=5){
				return fail("二级菜单最多5个");
			}
			menu.setType(JBoltWechatMenuType.TYPE_VIEW.getValue());
		}
		menu.setMpId(mpId);
		int rank=getNextSortRank(Okv.by("mp_id",mpId).set("pid", menu.getPid()));
		menu.setSortRank(rank);
		menu.setName("菜单");
	
		boolean success=menu.save();
		if(success){
			menu.setName("菜单_"+menu.getId());
			success=menu.update();
			if(success){
				addSaveSystemLog(menu.getId(), JBoltUserKit.getUserId(), menu.getName());
			}
		}
		return success?success(menu,JBoltMsg.SUCCESS):FAIL;
	}
	/**
	 * 更新
	 * @param mpId
	 * @param menu
	 * @return
	 */
	public Ret update(Long mpId, WechatMenu menu) {
		if(notOk(mpId)||notOk(menu.getId())||notOk(menu.getName())){return fail(JBoltMsg.PARAM_ERROR);}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			return fail("微信公众平台信息不存在");
		}
		WechatMenu db  = findById(menu.getId());
		int oldRank=db.getSortRank();
		int newRank=menu.getSortRank();
		if(db.getMpId().longValue()!=mpId.longValue()){
			return fail("操作的数据不是此公众平台下的，请谨慎操作");
		}
		menu.setMpId(mpId);
		if(oldRank!=newRank){
			WechatMenu otherMenu=findFirst(Okv.by("mp_id =",mpId).set("pid =", db.getPid()).set("sort_rank =", newRank).set("id !=", db.getId()),true);
			if(otherMenu!=null){
				otherMenu.setSortRank(oldRank);
				otherMenu.update();
			}
		}
		boolean success=menu.update();
		if(success) {
			addUpdateSystemLog(menu.getId(), JBoltUserKit.getUserId(), menu.getName());
		}
		return success?success(db,JBoltMsg.SUCCESS):FAIL;
	}
	/**
	 * 删除
	 * @param mpId
	 * @param id
	 * @return
	 */
	public Ret delete(Long mpId, Long id) {
		if(JBoltConfig.DEMO_MODE) {return fail(JBoltMsg.DEMO_MODE_CAN_NOT_DELETE);}
		if(notOk(mpId)||notOk(id)){return fail(JBoltMsg.PARAM_ERROR);}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			return fail("微信公众平台信息不存在");
		}
		WechatMenu db  = findById(id);
		if(mpId.longValue()!=db.getMpId().longValue()){
			return fail("操作的数据不是此公众平台下的，请谨慎操作");
		}
		if(notOk(db.getPid())){
			int count=getCountByPid(mpId, db.getId());
			if(count>0){
				return fail("存在子菜单，不能删除");
			}
		}
		Ret result=deleteById(id);
		if(result.isOk()){
			doInitRankByPid(mpId,db.getPid());
		}
		return result;
	}
	@Override
	protected String afterDelete(WechatMenu menu, Kv kv) {
		addDeleteSystemLog(menu.getId(), JBoltUserKit.getUserId(), menu.getName());
		return null;
	}
	
	private void doInitRankByPid(Long mpId, Long pid) {
		List<WechatMenu> weixinMenus=getListByPid(mpId, pid);
		if(weixinMenus.size()>0){
			for(int i=1;i<=weixinMenus.size();i++){
				weixinMenus.get(i-1).setSortRank(i);
			}
			batchUpdate(weixinMenus);
		}
	}
	
	private int getCountByPid(Long mpId, Long pid) {
		return getCount(Okv.by("mp_id", mpId).set("pid",pid));
	}
	/**
	 * 发布自定义菜单
	 * @param mpId
	 * @return
	 */
	public Ret publish(Long mpId) {
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			return fail("微信公众平台信息不存在");
		}
		List<WechatMenu> level1Menus=getLevel1Menus(mpId);
		if(level1Menus==null||level1Menus.size()==0){
			return fail("请添加菜单后再生成");
		}
		List<WechatMenu> level2Menus=null;
		StringBuilder msg=new StringBuilder();
		msg.append("{\"button\":[");
		for(WechatMenu menu:level1Menus){
			level2Menus=getLevel2Menus(mpId,menu.getId());
			if(level2Menus.size()==0){
				JBoltWechatMenuType menuType= JBoltEnum.getEnumObjectByValue(JBoltWechatMenuType.class, menu.getType());
				switch (menuType) {
				case TYPE_EVENT:
					msg.append("{\"type\":\"click\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"key\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置key");	
					}
					break;
				case TYPE_KEYWORDS:
					msg.append("{\"type\":\"click\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"key\":\"" + "keywords_").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置关键词");	
					}
					break;
				case TYPE_VIEW:
					msg.append("{\"type\":\"view\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"url\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置URL");	
					}
					break;
				case TYPE_PIC_SYSPHOTO:
					msg.append("{\"type\":\"pic_sysphoto\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"key\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置事件KEY");	
					}
					break;
				case TYPE_PIC_PHOTO_OR_ALBUM:
					msg.append("{\"type\":\"pic_photo_or_album\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"key\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置事件KEY");	
					}
					break;
				case TYPE_PIC_WEIXIN:
					msg.append("{\"type\":\"pic_weixin\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"key\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置事件KEY");	
					}
					break;
				case TYPE_MINIPROGRAM:
					msg.append("{\"type\":\"miniprogram\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"appid\":\"").append(menu.getAppId()).append("\",");
					msg.append("\"pagepath\":\"").append(menu.getPagePath()).append("\",");
					msg.append("\"url\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getAppId())){
						return fail(menu.getName()+"未设置跳转小程序的APPID");	
					}
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置跳转小程序的页面路径pagePath");	
					}
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置默认URL");	
					}
					break;
				case TYPE_LOCATION_SELECT:
					msg.append("{\"type\":\"location_select\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"key\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置key");	
					}
					break;
				case TYPE_MEDIA_ID:
					msg.append("{\"type\":\"media_id\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"media_id\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置media_id");	
					}
					break;
				case TYPE_SCANCODE_PUSH:
					msg.append("{\"type\":\"scancode_push\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"key\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置Key");	
					}
					break;
				case TYPE_SCANCODE_WAITMSG:
					msg.append("{\"type\":\"scancode_waitmsg\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"key\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置Key");	
					}
					break;
				case TYPE_VIEW_LIMITED:
					msg.append("{\"type\":\"view_limited\",\"name\":\"").append(menu.getName()).append("\",");
					msg.append("\"url\":\"").append(menu.getValue()).append("\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置URL");	
					}
					break;
				case TYPE_NONE:
					return fail(menu.getName()+"请添加二级微信菜单");
				}
			}else{
				msg.append("{\"name\":\"").append(menu.getName()).append("\",\"sub_button\":[");
				for(WechatMenu menu2:level2Menus){
					JBoltWechatMenuType menuType2= JBoltEnum.getEnumObjectByValue(JBoltWechatMenuType.class, menu2.getType());
					switch (menuType2) {
					case TYPE_EVENT:
						msg.append("{\"type\":\"click\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"key\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu.getName()+"未设置key");	
						}
						break;
					case TYPE_KEYWORDS:
						msg.append("{\"type\":\"click\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"key\":\"" + "keywords_").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置关键词");	
						}
						break;
					case TYPE_VIEW:
						msg.append("{\"type\":\"view\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"url\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置URL");	
						}
						break;
					case TYPE_PIC_SYSPHOTO:
						msg.append("{\"type\":\"pic_sysphoto\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"key\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置事件KEY");	
						}
						break;
					case TYPE_PIC_PHOTO_OR_ALBUM:
						msg.append("{\"type\":\"pic_photo_or_album\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"key\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置事件KEY");	
						}
						break;
					case TYPE_PIC_WEIXIN:
						msg.append("{\"type\":\"pic_weixin\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"key\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置事件KEY");	
						}
						break;
					case TYPE_MINIPROGRAM:
						msg.append("{\"type\":\"miniprogram\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"appid\":\"").append(menu2.getAppId()).append("\",");
						msg.append("\"pagepath\":\"").append(menu2.getPagePath()).append("\",");
						msg.append("\"url\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getAppId())){
							return fail(menu2.getName()+"未设置跳转小程序的APPID");	
						}
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置跳转小程序的页面路径pagePath");	
						}
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置默认URL");	
						}
						break;
					case TYPE_LOCATION_SELECT:
						msg.append("{\"type\":\"location_select\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"key\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置key");	
						}
						break;
					case TYPE_MEDIA_ID:
						msg.append("{\"type\":\"media_id\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"media_id\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置media_id");	
						}
						break;
					case TYPE_SCANCODE_PUSH:
						msg.append("{\"type\":\"scancode_push\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"key\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置key");	
						}
						break;
					case TYPE_SCANCODE_WAITMSG:
						msg.append("{\"type\":\"scancode_waitmsg\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"key\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置key");	
						}
						break;
					case TYPE_VIEW_LIMITED:
						msg.append("{\"type\":\"view_limited\",\"name\":\"").append(menu2.getName()).append("\",");
						msg.append("\"url\":\"").append(menu2.getValue()).append("\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置URL");	
						}
						break;
					case TYPE_NONE:
						break;
					}
				}
				msg.setLength(msg.length()-1);
				msg.append("]},");
			}
		}
		msg.setLength(msg.length()-1);
		msg.append("]}");
		String appId=JBoltWechatConfigCache.me.getAppId(mpId);
		if(StrKit.notBlank(appId)){
			ApiConfigKit.setThreadLocalAppId(appId);
			try {
				ApiResult apiResult=MenuApi.createMenu(msg.toString());
				if(!apiResult.isSucceed()){
					return fail(apiResult.getErrorMsg());
				}
			} catch (RuntimeException e) {
				return fail(e.getMessage());
			}finally {
				ApiConfigKit.removeThreadLocalAppId();
			}
		}else{
			return fail(wechatMpinfo.getName()+"基础配置不正确!");
		}
		
		return SUCCESS;
	}
	@Override
	protected int systemLogTargetType() {
		return JBoltSystemLogTargetType.WECHAT_MENU.getValue();
	}
	


}
