package cn.jbolt.index;

import cn.jbolt.core.controller.base.JBoltBaseController;
/**
 * 处理Pjax访问异常
 * @ClassName:  PjaxErrorController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年3月8日   
 */
public class PjaxErrorController extends JBoltBaseController {
	public void error() {
		Integer errorCode=getInt("errorCode",404);
		switch (errorCode) {
		case 404:
			renderPjaxFail("404,您访问的资源不存在!");
			break;
		case 500:
			renderPjaxFail("500 Internal Server Error");
			break;
			default:
			renderPjaxFail(errorCode+" Error");
			break;
		}
	}
}
