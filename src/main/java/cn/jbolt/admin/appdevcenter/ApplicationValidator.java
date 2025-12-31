package cn.jbolt.admin.appdevcenter;

import com.jfinal.core.Controller;

import cn.jbolt.core.base.JBoltMsg;
import cn.jbolt.core.para.JBoltValidator;
/**
   * 应用开发中心 管理应用 参数校验
 * @ClassName:  ApplicationValidator   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年6月22日   
 */
public class ApplicationValidator extends JBoltValidator {
	@Override
	protected void validate(Controller c) {
		validateJBoltLong(0,  JBoltMsg.PARAM_ERROR+":数据ID");
	}


}
