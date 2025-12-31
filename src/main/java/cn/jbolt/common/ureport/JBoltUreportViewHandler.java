package cn.jbolt.common.ureport;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import com.jfinal.kit.Ret;

import cn.jbolt.core.base.JBoltRequestType;
import cn.jbolt.core.consts.JBoltConst;
import cn.jbolt.core.kit.JBoltControllerKit;
/**
 * ureport访问和权限处理
 * @author 山东小木
 */
public class JBoltUreportViewHandler extends Handler {
	private Pattern ureportUrlPattern = Pattern.compile("^\\/ureport.*");
	private IUreportViewAuth ureportViewAuth;
	public JBoltUreportViewHandler(IUreportViewAuth ureportViewAuth) {
		if(ureportViewAuth==null) {
			throw new RuntimeException("JBoltUreportViewHandler必须设置IUreportViewAuth");
		}
		this.ureportViewAuth=ureportViewAuth;
	}
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if (ureportUrlPattern.matcher(target).matches()) {
				if (!ureportViewAuth.isPermitted(request,target)) {
					Object msgObj = request.getAttribute("msg");
					String msg = msgObj==null?"无权访问报表资源":msgObj.toString();
					if(JBoltRequestType.AJAX.equals(request.getAttribute(JBoltConst.RQKEY_JB_RQTYPE))) {
						JBoltControllerKit.renderJson(response, Ret.fail(msg));
					}else {
						request.setAttribute("msg", msg);
						HandlerKit.renderError404("/_view/_admin/common/msg/pageerror.html", request, response, isHandled);
					}
				}
			return;
		}else {
			next.handle(target, request, response, isHandled);
		}
		
		
	}
}
	