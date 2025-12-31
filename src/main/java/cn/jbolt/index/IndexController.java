package cn.jbolt.index;

import com.jfinal.aop.Before;

import cn.jbolt.core.controller.base.JBoltBaseController;
import cn.jbolt.core.para.JBoltNoUrlPara;

/**
 * 平台主入口
 * @ClassName:  IndexController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2020年12月3日   
 */
public class IndexController extends JBoltBaseController {
	@Before(JBoltNoUrlPara.class)
	public void index(){
		forwardAction("/admin");
	}
}
