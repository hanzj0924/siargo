package cn.jbolt._admin.role;

import com.jfinal.kit.StrKit;
import com.jfinal.kit.Okv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.core.service.JBoltRoleService;
import cn.jbolt.core.model.Role;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

	/**
	 * 根据角色SN编码获取角色ID
	 */
	public Long findIdBySn(int sn) {
		Role role = findFirst(Okv.by("sn", sn));
		return role == null ? null : role.getId();
	}

	/**
	 * 覆盖父类方法。手工构建角色树，确保 type 字段原样保留在 JSON 中。
	 * convertToModelTree 会创建新对象并覆盖 type="true"，
	 * 而 JFinal Model 序列化器对新 set() 的字段不可靠。
	 */
	@Override
	public List<Role> getAllRoleTreeDatas() {
		List<Record> records = Db.find("SELECT id, name, sn, pid, type FROM jb_role ORDER BY pid, sn");

		// 创建所有角色节点
		Map<Long, Role> roleMap = new HashMap<>();
		List<Role> roots = new ArrayList<>();
		for (Record rec : records) {
			Role role = new Role();
			role.put("id", rec.getLong("id"));
			role.put("name", rec.getStr("name"));
			role.put("sn", rec.getStr("sn"));
			role.put("pid", rec.getLong("pid"));
			role.put("type", rec.getInt("type"));
			roleMap.put(rec.getLong("id"), role);
		}

		// 手工挂树（不调 convertToModelTree，type 不会被覆盖）
		for (Role role : roleMap.values()) {
			Long pid = role.getLong("pid");
			if (pid == null || pid == 0L) {
				roots.add(role);
			} else {
				Role parent = roleMap.get(pid);
				if (parent != null) {
					@SuppressWarnings("unchecked")
					List<Role> items = parent.get("items");
					if (items == null) {
						items = new ArrayList<>();
						parent.put("items", items);
					}
					items.add(role);
				}
			}
		}

		return roots;
	}

}
