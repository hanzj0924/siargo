package cn.jbolt.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.kit.JBoltUserKit;

/**
 * 同一账号多端登录 HTTP 层拦截器
 * <p>
 * 绕过 JBoltOnlineUserCache 的缓存失效问题，直接查询 jb_online_user 表：
 * processAllSameUserOffline() 先触发 WebSocket 事件、再删除旧记录，
 * 导致缓存可能返回陈旧数据（online_state=1）或已清除（null），
 * 两种情况下均无法正确拦截旧会话的后续 HTTP 请求。
 * <p>
 * 本拦截器直接按 user_id 查询当前活跃会话，与 WebSocket toast 形成互补。
 */
public class SiargoTerminalOfflineInterceptor implements Interceptor {

	private static final Log LOG = Log.getLog(SiargoTerminalOfflineInterceptor.class);

	/** 登录、下线相关页面不拦截，必须放行让用户重新认证 */
	private static final java.util.Set<String> LOGIN_PAGES = java.util.Set.of(
		"/admin", "/admin/captcha", "/admin/terminalOffline",
		"/admin/relogin", "/admin/forcedOffline", "/admin/logout"
	);

	@Override
	public void intercept(Invocation inv) {
		String actionKey = inv.getActionKey();

		// 登录/下线相关页面直接放行
		if (LOGIN_PAGES.contains(actionKey)) {
			inv.invoke();
			return;
		}
		Long userId = JBoltUserKit.getUserId();
		if (userId == null) {
			// userId 为空：可能是真的未登录，也可能是 Handler 找不到已失效的会话
			// 如果 cookie 中还有 jboltid，说明用户曾有会话但被踢下线
			String cookieSessionId = inv.getController().getCookie("jboltid");
			if (StrKit.notBlank(cookieSessionId)) {
				LOG.warn(">>> 拦截[" + actionKey + "]: cookie存在但userId为空, 会话已失效");
				kickUser(inv);
				return;
			}
			inv.invoke();
			return;
		}

		String sessionId = JBoltUserKit.getUserSessionId();
		if (StrKit.isBlank(sessionId)) {
			inv.invoke();
			return;
		}

		// 直接查库：获取当前用户唯一的活跃会话
		Record record = Db.findFirst(
			"SELECT session_id FROM jb_online_user WHERE user_id = ? AND online_state = ?",
			userId, 1);

		if (record == null) {
			// 该用户没有任何活跃会话 —— 旧记录已被删除，当前被踢下线
			LOG.warn(">>> 拦截[" + actionKey + "]: 无活跃会话, userId=" + userId);
			kickUser(inv);
			return;
		}

		String activeSessionId = record.getStr("session_id");
		if (!sessionId.equals(activeSessionId)) {
			// 活跃会话是另一个 session —— 当前浏览器被踢下线
			LOG.warn(">>> 拦截[" + actionKey + "]: session 不匹配, userId=" + userId
				+ ", 当前=" + sessionId + ", 活跃=" + activeSessionId);
			kickUser(inv);
			return;
		}

		// session 匹配，正常放行
		inv.invoke();
	}

	private void kickUser(Invocation inv) {
		JBoltUserKit.clear();

		Controller ctl = inv.getController();
		if (ctl instanceof JBoltBaseController) {
			JBoltBaseController jboltCtl = (JBoltBaseController) ctl;
			String rqtype = jboltCtl.getAttr("_jb_rqtype_");
			// JBolt 页面请求（dialog/iframe/singlepage/normal）或 AJAX 请求 → 返回 JSON
			boolean isAjax = StrKit.notBlank(ctl.getHeader("X-Requested-With"));
			if (isAjax || (StrKit.notBlank(rqtype)
					&& ("dialog".equals(rqtype) || "iframe".equals(rqtype)
						|| "singlepage".equals(rqtype) || "normal".equals(rqtype)))) {
				jboltCtl.renderJsonFail("jbolt_terminal_offline");
			} else {
				jboltCtl.redirect("/admin/terminalOffline");
			}
		} else {
			// 非 JBoltBaseController 兜底：直接重定向
			ctl.redirect("/admin/terminalOffline");
		}
	}
}
