package cn.jbolt.common.safe;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.config.WebServletConfig;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import cn.jbolt.core.api.JBoltApiRet;
import cn.jbolt.core.base.JBoltRequestType;
import cn.jbolt.core.consts.JBoltConst;
/**
 * JBolt平台 Sentinel的 UrlBlockHandler 实现
 * @ClassName:  JBoltSentinelUrlBlockHandler   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年11月1日   
 *    
 */
public class JBoltSentinelUrlBlockHandler implements UrlBlockHandler{
	private static final String TYPE_PORTAL_ERROR = "portalerror";
	private static final String TYPE_PAGE_ERROR = "pageerror";
	private static final String TYPE_PJAX_ERROR = "pjaxerror";
	private static final String HTML_TPL = "<div class=\"jbolt_page\"><div class=\"jbolt_page_content\"><div style=\"text-align:center;font-size:16px;color:red;\">%s</div></div></div>";
	private static final String PORTAL_TPL = "<div style=\"text-align:center;font-size:16px;color:red;\">%s</div>";
	/**
	 * 处理请求类型
	 * @param request
	 */
	private String getJBoltRequestType(HttpServletRequest request) {
		//判断是不是API SDK调用
		if(JBoltConst.TRUE.equalsIgnoreCase(request.getHeader(JBoltConst.RQ_HEAD_KEY_JBOLTAPI))) {
			return JBoltRequestType.JBOLTAPI;
		}
		
		//判断是不是pjax
		if(JBoltConst.TRUE.equalsIgnoreCase(request.getHeader(JBoltConst.RQ_HEAD_KEY_XPJAX))) {
			return JBoltRequestType.PJAX;
		}
		
		//判断是不是ajaxportal
		if(JBoltConst.TRUE.equalsIgnoreCase(request.getHeader(JBoltConst.RQ_HEAD_KEY_AJAXPORTAL))) {
			return JBoltRequestType.AJAXPORTAL;
		}
		
		//判断是不是 ajax
		String xrequestedwith = request.getHeader(JBoltConst.RQ_HEAD_KEY_XREQUESTEDWITH);
		if((xrequestedwith != null && JBoltConst.XMLHTTPREQUEST.equalsIgnoreCase(xrequestedwith))) {
			return JBoltRequestType.AJAX;
		}
		
		String rqParam=request.getParameter(JBoltConst.RQKEY_JB_RQTYPE);		
		//判断URL参数中带_jb_rqtype_=dialog
		if(JBoltConst.RQ_TYPE_DIALOG.equalsIgnoreCase(rqParam)) {
			return JBoltRequestType.DIALOG;
		}
		//判断URL参数中带_jb_rqtype_=iframe
		if(JBoltConst.RQ_TYPE_IFRAME.equalsIgnoreCase(rqParam)) {
			return JBoltRequestType.IFRAME;
		}
		
		//判断请求地址上带着_jb_rqtype_dialog 例如localhost/demo/table/1-2-_jb_rqtype_dialog
		String url=request.getRequestURI();
		if(StrKit.notBlank(url)) {
			if(url.endsWith(JBoltConst.RQ_TYPE_DIALOG_STR)) {
				return JBoltRequestType.DIALOG;
			}
			if(url.endsWith(JBoltConst.RQ_TYPE_IFRAME_STR)) {
				return JBoltRequestType.IFRAME;
			}
		}
		
		return JBoltRequestType.NORMAL;
	}
	@Override
	public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex)
			throws IOException {
		// FlowException 限流异常
		// ParamFlowException 参数限流异常
        // DegradeException 降级异常
        // SystemBlockException 系统负载异常
		// AuthorityException 授权异常
		String msg = "服务异常";
	    if (ex instanceof FlowException) {
	    	msg = "流控规则被触发，请求被限流";
        } else if (ex instanceof DegradeException) {
            msg = "降级规则被触发，请求被降级";
        } else if (ex instanceof AuthorityException) {
            msg = "授权规则被触发，无权访问";
        } else if (ex instanceof ParamFlowException) {
            msg = "热点规则被触发，请求被限流";
        } else if (ex instanceof SystemBlockException) {
            msg = "系统规则被触发，请求被限流";
        }
	    response.setStatus(WebServletConfig.getBlockPageHttpStatus());
	    response.setCharacterEncoding("UTF-8");
	    String rqType = getJBoltRequestType(request);
	    switch (rqType) {
		    case JBoltRequestType.DIALOG:
		    	renderHtml(response, msg,TYPE_PAGE_ERROR);
		    	break;
		    case JBoltRequestType.IFRAME:
		    	renderHtml(response, msg,TYPE_PAGE_ERROR);
		    	break;
		    case JBoltRequestType.AJAX:
		    	renderJson(response, msg);
		    	break;
		    case JBoltRequestType.JBOLTAPI:
		    	renderJBoltApiJson(response, msg);
		    	break;
		    case JBoltRequestType.AJAXPORTAL:
		    	renderHtml(response,msg,TYPE_PORTAL_ERROR);
		    	break;
		    case JBoltRequestType.NORMAL:
		    	renderHtml(response, msg,TYPE_PAGE_ERROR);
		    	break;
		    case JBoltRequestType.PJAX:
		    	renderHtml(response, msg,TYPE_PJAX_ERROR);
		    	break;
			case JBoltRequestType.NULL:
				renderJson(response, msg);
				break;
			default:
				renderJson(response, msg);
				break;
		}
	    
	}
	
	/**
	 * 返回html
	 * @param response
	 * @param msg
	 * @param errorType
	 * @throws IOException
	 */
	public void renderHtml(HttpServletResponse response,String msg,String errorType) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		String content = null;
		switch (errorType) {
			case TYPE_PJAX_ERROR:
				content = String.format(HTML_TPL, msg);
				break;
			case TYPE_PORTAL_ERROR:
				content = String.format(PORTAL_TPL, msg);
				break;
			case TYPE_PAGE_ERROR:
				content = String.format(HTML_TPL, msg);
				break;
			default:
				content = String.format(HTML_TPL, msg);
				break;
		}
		PrintWriter writer = response.getWriter();
		writer.print(content);
		writer.flush();
		writer.close();
	}
	
	/**
	 * 返回JSON消息
	 * @param response
	 * @param msg
	 * @throws IOException
	 */
	public void renderJson(HttpServletResponse response,String msg) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(Ret.fail(msg).toJson());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 返回JBolt Api Ret JSON消息
	 * @param response
	 * @param msg
	 * @throws IOException
	 */
	public void renderJBoltApiJson(HttpServletResponse response,String msg) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(JBoltApiRet.API_FAIL(msg).toJson());
		writer.flush();
		writer.close();
	}
	
	 
}
