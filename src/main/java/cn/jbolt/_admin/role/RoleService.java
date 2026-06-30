package cn.jbolt._admin.role;

import com.jfinal.kit.StrKit;
import com.jfinal.kit.Okv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import cn.jbolt.core.service.JBoltRoleService;
import cn.jbolt.core.model.Role;
import cn.jbolt.core.cache.JBoltRoleCache;
import cn.jbolt.core.permission.JBoltUserAuthKit;
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
	 * 检查用户是否拥有指定SN的角色或被其上级角色覆盖
	 * 规则：沿 pid 链向上遍历，type=1 的功能角色参与覆盖检查，type=0 的菜单角色跳过
	 *   管理员(sn=1) → 全局覆盖，单独判断
	 *   质检(sn=2, type=0) → 纯菜单入口，不覆盖任何子按钮
	 *   报告单(sn=21, type=1) → 覆盖 精度/外观/包装/批准
	 *   设备(sn=22, type=1) → 覆盖 设备审核
	 * 新增角色只需在 jb_role 中配置正确的 pid 和 type 即可生效，无需修改代码
	 */
	public boolean hasRoleOrAbove(Long userId, int sn) {
		// 管理员拥有全部权限
		Long adminRoleId = findIdBySn(1);
		if (adminRoleId != null && JBoltUserAuthKit.hasRole(userId, adminRoleId)) {
			return true;
		}

		Long roleId = findIdBySn(sn);
		if (roleId == null) return false;

		// 沿 pid 链向上遍历，跳过 type=0 的菜单角色
		Long currentId = roleId;
		while (currentId != null && currentId > 0) {
			Role role = JBoltRoleCache.me.get(currentId);
			if (role == null) break;

			Integer type = role.getInt("type");
			// type=1 的功能角色参与覆盖检查
			if (type != null && type == 1 && JBoltUserAuthKit.hasRole(userId, currentId)) {
				return true;
			}

			Long pid = role.getPid();
			if (pid == null || pid == 0) break;
			currentId = pid;
		}
		return false;
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
