package org.blazer.dataservice.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.blazer.dataservice.model.PermissionsModel;
import org.blazer.dataservice.model.RoleModel;
import org.blazer.dataservice.model.UserModel;
import org.springframework.stereotype.Component;

@Component(value = "userCacheService")
public class UserCacheService {

	private static final Map<Integer, UserModel> userMap = new ConcurrentHashMap<Integer, UserModel>();
	private static final Map<Integer, RoleModel> roleMap = new ConcurrentHashMap<Integer, RoleModel>();
	private static final Map<String, PermissionsModel> permissionsMap = new ConcurrentHashMap<String, PermissionsModel>();

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
