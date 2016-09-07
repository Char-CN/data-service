package org.blazer.dataservice.init;

import java.util.List;
import java.util.Map;

import org.blazer.dataservice.cache.UserCache;
import org.blazer.dataservice.model.PermissionsModel;
import org.blazer.dataservice.model.RoleModel;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.StringUtil;
import org.blazer.dataservice.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("initSystem")
public class UserAndPermissionsInit implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(UserAndPermissionsInit.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	UserCache userCache;

	public void afterPropertiesSet() throws Exception {
		logger.debug("系统配置开始加载");
		TimeUtil timeUtil = TimeUtil.createAndPoint().setLogger(logger);
		//////////////////////// 加载用户和权限 ////////////////////////
		initUserAndPermissions();
		timeUtil.printMs("加载用户和权限");
	}

	private void initUserAndPermissions() {
		// 先清空
		userCache.clearAll();
		// 查询所有权限
		String sql = "select up.id,up.permissions_name,up.url,us.system_name,us.id as system_id from us_permissions up inner join us_system us on up.system_id=us.id where us.enable=1 and up.enable=1";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			PermissionsModel permissionsModel = new PermissionsModel();
			permissionsModel.setId(IntegerUtil.getInt0(map.get("id")));
			permissionsModel.setPermissionsName(StringUtil.getStrEmpty(map.get("permissions_name")));
			permissionsModel.setUrl(StringUtil.getStrEmpty(map.get("url")));
			permissionsModel.setSystemId(IntegerUtil.getInt0(map.get("system_id")));
			permissionsModel.setSystemName(StringUtil.getStrEmpty(map.get("system_name")));
			userCache.addPermissions(permissionsModel);
		}
		// 查询所有角色
		sql = "select * from us_role where enable=1";
		list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			sql = "select GROUP_CONCAT(role_id) as roleids from us_user_role where user_id=? group by user_id";
			RoleModel roleModel = new RoleModel();
			roleModel.setId(IntegerUtil.getInt0(map.get("id")));
			roleModel.setRoleName(StringUtil.getStrEmpty(map.get("role_name")));
			userCache.addRole(roleModel);
		}
		// 查询所有用户
	}

	public void reload() {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			logger.error("重载数据失败", e);
		}
	}

}
