package cn.jbolt.common.ureport;

import javax.servlet.http.HttpServletRequest;

/**
 * 授权
 */
@FunctionalInterface
public interface IUreportViewAuth {
	boolean isPermitted(HttpServletRequest request,String target);
}