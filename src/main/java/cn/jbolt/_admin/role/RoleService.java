package cn.jbolt._admin.role;

import com.jfinal.kit.StrKit;
import com.jfinal.kit.Okv;
import cn.jbolt.core.service.JBoltRoleService;
import cn.jbolt.core.model.Role;
/**
 * 角色管理Service
 * @ClassName:  RoleService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月27日 上午11:54:25   
 */
public class RoleService extends JBoltRoleService {

	/**
	 * 根据角色名称获取角色ID
	 */
	public Long findIdByName(String name) {
		if (StrKit.isBlank(name)) {
			return null;
		}
		Role role = findFirst(Okv.by("name", name.trim()));
		return role == null ? null : role.getId();
	}
	
}
