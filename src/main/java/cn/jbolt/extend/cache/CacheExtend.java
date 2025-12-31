package cn.jbolt.extend.cache;

import cn.jbolt.common.util.CACHE;
import cn.jbolt.core.base.config.JBoltConfig;
import cn.jbolt.core.cache.JBoltCacheKit;
import cn.jbolt.core.cache.JBoltCacheParaValidator;
import cn.jbolt.core.consts.JBoltConst;

/**
 * 全局CACHE操作工具类
 * 
 * @ClassName: CacheUtil
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2021年04月13日
 */
public class CacheExtend extends JBoltCacheParaValidator {
	public static final CacheExtend me = new CacheExtend();
	/**
	 * 拼接cacheKey
	 * @param pre
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unused")
	private String buildCacheKey(String pre,Object value) {
		return JBoltConst.JBOLT_CACHE_DEFAULT_PREFIX+pre+value.toString();
	}
	
	/**
	 * put
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		put(JBoltConfig.JBOLT_CACHE_NAME, key, value);
	}
	/**
	 * put
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public void put(String cacheName,String key, Object value) {
		JBoltCacheKit.put(cacheName, key, value);
	}
	
	/**
	 * get
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T> T get(String key) {
		return get(JBoltConfig.JBOLT_CACHE_NAME, key);
	}
	/**
	 * get
	 * @param <T>
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public <T> T get(String cacheName,String key) {
		return JBoltCacheKit.get(cacheName, key);
	}
	 

	/**
	 * 举例 删除removeRoleMenus
	 * 
	 * @param roleIds
	 */
//	public void removeRolesMenus(String roleIds) {
//		if (StrKit.notBlank(roleIds)) {
//			JBoltCacheKit.remove(JBOLT_CACHE_NAME, buildCacheKey("roles_menus_" ,roleIds));
//		}
//	}
	
	/**
	 * 举例  根据roleIds获取对应菜单
	 * @param roleIds
	 * @return
	 */
//	public List<Permission> getRoleMenus(String roleIds) {
//		if (StrKit.isBlank(roleIds)) {
//			return null;
//		}
//		return JBoltCacheKit.get(JBOLT_CACHE_NAME, buildCacheKey("roles_menus_" , roleIds), new IDataLoader() {
//			@Override
//			public Object load() {
//				//这里的service可以直接调用AOP获取 也可以拿到上面去定义出来
//				return Aop.get(PermissionService.class).getMenusByRoles(roleIds);
//			}
//		});
//
//	}
	
	
	/**
	 * 举例 获取岗位信息
	 * @param id
	 * @return
	 */
//	public Post getPost(Object id) {
//		//这里的service可以直接调用AOP获取 也可以拿到上面去定义出来
//		return Aop.get(PostService.class).findById(id);
//	} 
	
	/**
	 * 举例 获取岗位Name
	 * @param deptId
	 * @return
	 */
//	public String getPostName(Object id) {
//		Post post=getPost(id);
//		return post==null?"":post.getName();
//	}
	
	
	
	

	
	 
}
