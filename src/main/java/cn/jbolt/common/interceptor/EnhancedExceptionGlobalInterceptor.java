package cn.jbolt.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.jbolt.core.consts.JBoltConst;
import cn.jbolt.core.base.JBoltRequestType;

/**
 * 增强版异常处理全局拦截器
 * 用于增强renderFail的日志记录，便于追踪问题根源
 * 
 * @ClassName: EnhancedExceptionGlobalInterceptor
 * @author: Siargo Team
 * @date: 2026-04-17
 */
public class EnhancedExceptionGlobalInterceptor implements Interceptor {
    protected static final Log LOG = Log.getLog(EnhancedExceptionGlobalInterceptor.class);
    
    @Override
    public void intercept(Invocation inv) {
        try {
            inv.invoke();
        } catch (Exception e) {
            // 获取请求信息
            String actionKey = inv.getActionKey();
            String controllerName = inv.getController().getClass().getSimpleName();
            String methodName = inv.getMethodName();
            String requestUri = inv.getController().getRequest().getRequestURI();
            String requestMethod = inv.getController().getRequest().getMethod();
            String rqType = inv.getController().getAttr(JBoltConst.RQKEY_JB_RQTYPE);
            
            // 构建详细的错误日志
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("【异常拦截】");
            errorMsg.append(" Controller: ").append(controllerName);
            errorMsg.append(", Action: ").append(methodName);
            errorMsg.append(", ActionKey: ").append(actionKey);
            errorMsg.append(", URI: ").append(requestUri);
            errorMsg.append(", Method: ").append(requestMethod);
            errorMsg.append(", RequestType: ").append(rqType != null ? rqType : "null");
            errorMsg.append(", Exception: ").append(e.getClass().getSimpleName());
            errorMsg.append(", Message: ").append(StrKit.defaultIfBlank(e.getMessage(), "服务异常"));
            
            // 记录详细错误日志
            LOG.error(errorMsg.toString());
            
            // 如果是AJAX请求，输出警告日志便于追踪
            if (StrKit.notBlank(rqType) && rqType.equals(JBoltRequestType.AJAX)) {
                LOG.warn("【AJAX请求失败】Controller: " + controllerName 
                    + ", Action: " + methodName 
                    + ", ActionKey: " + actionKey 
                    + ", URI: " + requestUri
                    + ", Error: " + StrKit.defaultIfBlank(e.getMessage(), "未知错误"));
            }
            
            // 记录完整堆栈（仅在开发模式或需要详细调试时）
            if (LOG.isDebugEnabled()) {
                LOG.debug("异常堆栈详情:", e);
            }
            
            // 使用JBolt原生的renderFail处理
            String errorMessage = StrKit.defaultIfBlank(e.getMessage(), "服务异常");
            cn.jbolt.core.kit.JBoltControllerKit.renderFail(inv.getController(), errorMessage);
        }
    }
}
