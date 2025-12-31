package cn.jbolt._admin.msgcenter;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt.common.model.SysNotice;
import cn.jbolt.common.model.Todo;
import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.base.JBoltPageSize;
import cn.jbolt.core.bean.SortInfo;
import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;
import cn.jbolt.core.permission.UnCheck;
import cn.jbolt.core.service.JBoltFileService;
/**
 * JBolt 用户消息中心
 * @ClassName:  JBoltMsgCenterAdminController
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2021年5月31日
 *
 */
@UnCheck
public class JBoltMsgCenterAdminController extends JBoltBaseController {
	@Inject
	private SysNoticeService sysNoticeService;
	@Inject
	private TodoService todoService;
	@Inject
	private PrivateMessageService privateMessageService;
	@Inject
	private SysNoticeReaderService sysNoticeReaderService;
	@Inject
	private JBoltFileService jBoltFileService;
	/**
	 * 个人消息中心首页
	 */
	public void index() {
		setDefaultSortInfos(new SortInfo("sysNotice", "create_time", "desc"),new SortInfo("todo", "create_time", "desc"));
		set("tabIndex", getInt(0,0));
		render("index.html");
	}
	/**
	 * 个人右上角消息中心layer弹出层
	 */
	public void layer() {
		render("layer.html");
	}
	/**
	 * 消息中心  通知 获取10条数据 通知
	 */
	@ActionKey("/admin/msgcenter/sysnotice/portalDatas")
	public void sysNoticePortalDatas() {
		renderJsonData(sysNoticeService.getMsgCenterPortalDatas());
	}
	/**
	 * 消息中心  通知 详情
	 */
	@ActionKey("/admin/msgcenter/sysnotice/detail")
	public void sysNoticeDetail() {
		Long sysNoticeId = getLong(0);
		if(notOk(sysNoticeId)) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		SysNotice sysNotice=sysNoticeService.findById(sysNoticeId);
		if(sysNotice == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		Ret ret=sysNoticeService.checkUserHasAuth(JBoltUserKit.getUser(),sysNotice);
		if(ret.isFail()) {
			renderDialogFail(ret.getStr("msg"));
			return;
		}
		sysNoticeService.processReceiverValues(sysNotice);
		set("sysNotice",sysNotice);
		set("files",jBoltFileService.getListByIds(sysNotice.getFiles()));
		set("inDialog", true);
		set("readed", sysNoticeReaderService.existsReader(sysNoticeId,JBoltUserKit.getUserId()));
		render("sysnotice/detail.html");
	}

	/**
	 * 消息中心  通知 标记为已读
	 */
	@ActionKey("/admin/msgcenter/todo/markAsRead")
	public void todoMarkAsRead() {
		renderJson(todoService.markAsRead(getLong(0)));
	}
	/**
	 * 消息中心  通知 标记为已读
	 */
	@ActionKey("/admin/msgcenter/sysnotice/markAsRead")
	public void sysNoticeMarkAsRead() {
		renderJson(sysNoticeService.markAsRead(getLong(0)));
	}
	/**
	 * 消息中心  通知 全部标记为已读
	 */
//	@ActionKey("/admin/msgcenter/sysnotice/markAllAsRead")
//	@Before(Tx.class)
//	public void sysNoticeMarkAllAsRead() {
//		renderJson(sysNoticeService.markAllAsRead());
//	}
	/**
	 * 消息中心  通知 批量标记为已读
	 */
	@ActionKey("/admin/msgcenter/sysnotice/markMultiAsRead")
	@Before(Tx.class)
	public void sysNoticeMarkMultiAsRead() {
		renderJson(sysNoticeService.markMultiAsRead(get("ids")));
	}

	/**
	 * 个人消息中心 通知数据
	 */
	@ActionKey("/admin/msgcenter/sysnotice/userDatas")
	public void userSysNoticeDatas() {
		renderJsonData(sysNoticeService.paginateUserSysNotices(getPageNumber(), getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST), getKeywords(), JBoltUserKit.getUserId(), getSortColumn("create_time"), getSortType("desc"), getBoolean("readed"), false, true,false, "content"));
	}

	/**
	 * 个人消息中心 通知数据
	 */
	@ActionKey("/admin/msgcenter/todo/userDatas")
	public void userTodoDatas() {
		renderJsonData(todoService.paginateUserTodos(getPageNumber(), getPageSize(JBoltPageSize.PAGESIZE_ADMIN_LIST), getKeywords(), JBoltUserKit.getUserId(), getSortColumn("create_time"), getSortType("desc"), getType(),getState(),get("dateTimeColumn"),getDateRange(),false, "content"));
	}


	/**
	 * 消息中心  消息 10条 未读
	 */
//	@ActionKey("/admin/msgcenter/privateMessage/portalDatas")
//	public void privateMessagePortalDatas() {
//		renderJsonData(todoService.getMsgCenterPortalDatas());
//	}
	/**
	 * 消息中心  待办 10条 未完成的
	 */
	@ActionKey("/admin/msgcenter/todo/portalDatas")
	public void todoPortalDatas() {
		renderJsonData(todoService.getMsgCenterPortalDatas());
	}


	/**
	 * 个人消息中心 获取未读数据info
	 */
	@ActionKey("/admin/msgcenter/unreadInfo")
	public void userUnreadInfo() {
		boolean needRedDot = sysNoticeService.existUnread(JBoltUserKit.getUserId());
		if(!needRedDot) {
			needRedDot = todoService.existUnread(JBoltUserKit.getUserId());
			if(!needRedDot) {
				needRedDot = todoService.existNeedProcess(JBoltUserKit.getUserId());
			}
//			if(!needRedDot) {
//				needRedDot = privateMessageService.existUnread(JBoltUserKit.getUserId());
//			}
		}
		renderJsonData(needRedDot);
	}

	/**
	 * 消息中心  通知 详情
	 */
	@ActionKey("/admin/msgcenter/todo/detail")
	public void todoDetail() {
		Long todoId = getLong(0);
		if(notOk(todoId)) {
			renderDialogFail(JBoltMsg.PARAM_ERROR);
			return;
		}
		Todo todo=todoService.findById(todoId);
		if(todo == null){
			renderDialogFail(JBoltMsg.DATA_NOT_EXIST);
			return;
		}
		Long userId = JBoltUserKit.getUserId();
		if(todo.getUserId().longValue() != userId.longValue()) {
			renderDialogFail(JBoltMsg.NOPERMISSION);
			return;
		}
		set("todo",todo);
		render("todo/detail.html");
	}

}
