package org.blazer.dataservice.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.blazer.dataservice.model.PermissionsModel;
import org.blazer.dataservice.model.RoleModel;
import org.blazer.dataservice.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

/**
 * disk cache
 * 
 * 流程：
 * 
 * 1.缓存用户与所对应权限Bigmap
 * 
 * 2.缓存权限的map
 * 
 * 3.缓存用户-角色、角色-权限
 * 
 * @author hyy
 *
 */
@Component(value = "userCache")
public class UserCache extends BaseCache {

	private static final String CACHE_NAME = "user_cache";
	private static final Map<Integer, UserModel> userMap = new ConcurrentHashMap<Integer, UserModel>();
	private static final Map<Integer, RoleModel> roleMap = new ConcurrentHashMap<Integer, RoleModel>();
	private static final Map<String, PermissionsModel> permissionsMap = new ConcurrentHashMap<String, PermissionsModel>();

	@Autowired
	EhCacheCacheManager ehCacheManager;

	@Override
	public String getCacheName() {
		return "user_cache";
	}

	/**
	 * TODO : 所有
	 */
	public void clearAll() {
		userMap.clear();
		roleMap.clear();
		permissionsMap.clear();
	}

	/**
	 * TODO : 用户相关
	 */

	/**
	 * 清除某个配置
	 * 
	 * @param id
	 */
	public void clearUserById(Integer id) {
		userMap.remove(id);
	}

	/**
	 * 新增一个用户，如果存在则覆盖。
	 */
	public void addUser(UserModel user) {
		userMap.put(user.getId(), user);
	}

	/**
	 * 获得一个用户
	 * 
	 * @param id
	 * @return
	 */
	public UserModel getUserById(Integer id) {
		return userMap.get(id);
	}

	/**
	 * TODO : 角色相关
	 */
	public void addRole(RoleModel roleModel) {
		roleMap.put(roleModel.getId(), roleModel);
	}

	/**
	 * TODO : 权限相关
	 */
	public void addPermissions(PermissionsModel permissionsModel) {
		String key = permissionsModel.getSystemName() + "_" + permissionsModel.getUrl();
		permissionsMap.put(key, permissionsModel);
	}

}
