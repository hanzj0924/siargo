package cn.jbolt.base;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.support.http.StatViewServlet;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.druid.IDruidStatViewAuth;

import cn.jbolt.core.consts.JBoltConst;
import cn.jbolt.core.handler.base.JBoltBaseHandler;
/**
 * JBolt Druid 监控 handler
 * @ClassName:  JBoltDruidStatViewHandler
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2020年2月28日
 */
public class JBoltDruidStatViewHandler extends Handler {


	private IDruidStatViewAuth auth;
	private String visitPath = "/druid";
	private String finalVisitPath;
	private String ctx="";
	private StatViewServlet servlet = new JFinalStatViewServlet();

	public JBoltDruidStatViewHandler(String visitPath) {
		this(visitPath,
			new IDruidStatViewAuth(){
				public boolean isPermitted(HttpServletRequest request) {
					return true;
				}
			});
	}

	public JBoltDruidStatViewHandler(String visitPath , IDruidStatViewAuth druidStatViewAuth) {
		if (StrKit.isBlank(visitPath))
			throw new IllegalArgumentException("visitPath can not be blank");
		if (druidStatViewAuth == null)
			throw new IllegalArgumentException("druidStatViewAuth can not be null");

		visitPath = visitPath.trim();
		if (! visitPath.startsWith("/"))
			visitPath = "/" + visitPath;
		this.visitPath = visitPath;
		this.finalVisitPath=this.visitPath;
		this.auth = druidStatViewAuth;

		//处理ctx和finalVisitPath
		String contextPath=JFinal.me().getContextPath();
		if (contextPath != null && !"".equals(contextPath) && !"/".equals(contextPath)) {
			this.ctx=contextPath;
			if(this.visitPath.startsWith(this.ctx)) {
				this.visitPath=this.visitPath.replace(this.ctx, "");
			}else {
				this.finalVisitPath=this.ctx+this.visitPath;
			}
		}
	}

	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {		if (target.startsWith(visitPath)) {
			isHandled[0] = true;

			// 支持 context path
			if (StrKit.notBlank(this.ctx)) {
				target = this.ctx + target;
			}

			if (target.equals(finalVisitPath) && !target.endsWith("/index.html")) {
				StringBuilder basePath=JBoltBaseHandler.processBasePathStrBuilder(request,false);
				basePath.append(target).append("/index.html");
				HandlerKit.redirect(basePath.toString(), request, response, isHandled);
				return ;
			}

			try {
				servlet.service(request, response);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			next.handle(target, request, response, isHandled);
		}
	}

	class JFinalStatViewServlet extends StatViewServlet {

		private static final long serialVersionUID = 2898674199964021798L;

		public boolean isPermittedRequest(HttpServletRequest request) {
			return auth.isPermitted(request);
		}

		public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        String contextPath = request.getContextPath();
	        // String servletPath = request.getServletPath();
	        String requestURI = request.getRequestURI();

	        response.setCharacterEncoding("utf-8");

	        if (contextPath == null) { // root context
	            contextPath = "";
	        }
	        // String uri = contextPath + servletPath;
	        // String path = requestURI.substring(contextPath.length() + servletPath.length());
	        int index = contextPath.length() + visitPath.length();
	        String uri = requestURI.substring(0, index);
	        String path = requestURI.substring(index);

	        if (!isPermittedRequest(request)) {
//	            path = "/nopermit.html";
	            next.handle(JBoltConst.JBOLT_ADMIN_REDIRECT_TO_SCREENLOCK_PAGE_ACTION_KEY, request, response, new boolean[] {true});
//	            returnResourceFile(path, uri, response);
	            return;
	        }

	        if ("".equals(path)) {
	            if (contextPath == null || contextPath.equals("") || contextPath.equals("/")) {
	                response.sendRedirect("/druid/index.html");
	            } else {
	                response.sendRedirect("druid/index.html");
	            }
	            return;
	        }

	        if ("/".equals(path)) {
	            response.sendRedirect("index.html");
	            return;
	        }

	        if (path.indexOf(".json") >= 0) {
	            String fullUrl = path;
	            if (request.getQueryString() != null && request.getQueryString().length() > 0) {
	                fullUrl += "?" + request.getQueryString();
	            }
	            response.getWriter().print(process(fullUrl));
	            return;
	        }

	        // find file in resources path
	        returnResourceFile(path, uri, response);
	    }
	}

}
