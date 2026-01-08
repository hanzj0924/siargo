package cn.jbolt.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Cookie工具类
 * @ClassName:  CookitUtil   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2021年3月17日   
 */
public class CookieUtil {
	/**
	 * 获取Cookie
	 * @param request
	 * @param name
	 * @return
	 */
	public static Cookie get(HttpServletRequest request,String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取cookie Value
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getValue(HttpServletRequest request,String name) {
		Cookie cookie = get(request, name);
		return cookie==null?null:cookie.getValue();
	}
		
	/**
	 * 设置Cookie
	 * @param response
	 * @param name
	 * @param value
	 * @param domain
	 * @param path
	 * @param isHttpOnly
	 * @param maxAgeInSeconds
	 */
	public static void set(HttpServletResponse response,String name,String value,String domain,String path,Boolean isHttpOnly,int maxAgeInSeconds) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAgeInSeconds);
		// set the default path value to "/"
		if (path == null) {
			path = "/";
		}
		cookie.setPath(path);
		
		if (domain != null) {
			cookie.setDomain(domain);
		}
		if (isHttpOnly != null) {
			cookie.setHttpOnly(isHttpOnly);
		}
		response.addCookie(cookie);
	}
}
