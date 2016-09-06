package org.blazer.dataservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.dao.CustomJdbcDao;
import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.ConfigModel;
import org.blazer.dataservice.model.PermissionsModel;
import org.blazer.dataservice.model.RoleModel;
import org.blazer.dataservice.model.ConfigDetailModel;
import org.blazer.dataservice.service.CacheService;
import org.blazer.dataservice.service.UserCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("initSystem")
public class InitSystem implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(InitSystem.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CustomJdbcDao customJdbcDao;

	@Autowired
	CacheService cacheService;

	@Autowired
	UserCacheService userCacheService;

	@Value("#{dataSourceProperties.url}")
	public String url;

	@Value("#{dataSourceProperties.username}")
	public String username;

	@Value("#{dataSourceProperties.password}")
	public String password;

	public void afterPropertiesSet() throws Exception {
		logger.debug("系统配置开始加载");
		TimeUtil timeUtil = TimeUtil.createAndPoint().setLogger(logger);
		//////////////////////// 加载数据源 ////////////////////////
		initDataSource();
		timeUtil.printMs("加载数据源");
		//////////////////////// 加载配置项 ////////////////////////
		initConfigEntity();
		timeUtil.printMs("加载配置项");
		//////////////////////// 加载用户和权限 ////////////////////////
		initUserAndPermissions();
		timeUtil.printMs("加载用户和权限");
	}

	private void initUserAndPermissions() {
		// 先清空
		userCacheService.clearAll();
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
			userCacheService.addPermissions(permissionsModel);
		}
		// 查询所有角色
		sql = "select * from us_role where enable=1";
		list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			sql = "select GROUP_CONCAT(role_id) as roleids from us_user_role where user_id=? group by user_id";
			RoleModel roleModel = new RoleModel();
			roleModel.setId(IntegerUtil.getInt0(map.get("id")));
			roleModel.setRoleName(StringUtil.getStrEmpty(map.get("role_name")));
			userCacheService.addRole(roleModel);
		}
		// 查询所有用户
	}

	public void initConfigEntity() throws UnknowDataSourceException {
		// 先清空
		cacheService.clearConfigAll();
		List<Map<String, Object>> configList = jdbcTemplate.queryForList("select id,datasource_id,config_name,config_type from ds_config where enable=1");
		for (Map<String, Object> configMap : configList) {
			ConfigModel config = new ConfigModel();
			config.setId(IntegerUtil.getInt0(configMap.get("id")));
			// 找不到数据源使用默认数据源
			if (StringUtils.isBlank(StringUtil.getStr(configMap.get("datasource_id")))) {
				config.setDataSource(customJdbcDao.getDao(1));
			} else {
				config.setDataSource(customJdbcDao.getDao(IntegerUtil.getInt0(configMap.get("datasource_id"))));
			}
			config.setConfigName(StringUtil.getStrEmpty(configMap.get("config_name")));
			config.setConfigType(StringUtil.getStrEmpty(configMap.get("config_type")));
			List<ConfigDetailModel> detailList = new ArrayList<ConfigDetailModel>();
			List<Map<String, Object>> rstList = jdbcTemplate
					.queryForList("select id,datasource_id,config_id,`key`,`values` from ds_config_detail where config_id=? and enable=1", config.getId());
			for (Map<String, Object> detailMap : rstList) {
				ConfigDetailModel detail = new ConfigDetailModel();
				detail.setId(IntegerUtil.getInt0(detailMap.get("id")));
				// 找不到数据源使用config的数据源
				if (StringUtils.isBlank(StringUtil.getStr(detailMap.get("datasource_id")))) {
					detail.setDataSource(config.getDataSource());
				} else {
					detail.setDataSource(customJdbcDao.getDao(IntegerUtil.getInt0(detailMap.get("datasource_id"))));
				}
				detail.setKey(StringUtil.getStrEmpty(detailMap.get("key")));
				detail.setValues(StringUtil.getStrEmpty(detailMap.get("values")));
				detailList.add(detail);
			}
			config.setDetailList(detailList);
			cacheService.addConfig(config);
		}
		logger.info("init success config list size : " + configList.size());
	}

	public void initConfigEntity(Integer id) throws UnknowDataSourceException {
		if (id == null) {
			logger.info("config id is null, init fail");
			return;
		}
		// 先清除
		cacheService.clearConfigById(id);
		List<Map<String, Object>> configList = jdbcTemplate
				.queryForList("select id,datasource_id,config_name,config_type from ds_config where enable=1 and id=?", id);
		if (configList.size() == 0) {
			logger.info("config is not found, id : " + id + ", init fail");
			return;
		}
		ConfigModel config = new ConfigModel();
		config.setId(IntegerUtil.getInt0(configList.get(0).get("id")));
		// 找不到数据源使用默认数据源
		if (StringUtils.isBlank(StringUtil.getStr(configList.get(0).get("datasource_id")))) {
			config.setDataSource(customJdbcDao.getDao(1));
		} else {
			config.setDataSource(customJdbcDao.getDao(IntegerUtil.getInt0(configList.get(0).get("datasource_id"))));
		}
		config.setConfigName(StringUtil.getStrEmpty(configList.get(0).get("config_name")));
		config.setConfigType(StringUtil.getStrEmpty(configList.get(0).get("config_type")));
		List<ConfigDetailModel> detailList = new ArrayList<ConfigDetailModel>();
		List<Map<String, Object>> rstList = jdbcTemplate
				.queryForList("select id,datasource_id,config_id,`key`,`values` from ds_config_detail where config_id=? and enable=1", config.getId());
		for (Map<String, Object> detailMap : rstList) {
			ConfigDetailModel detail = new ConfigDetailModel();
			detail.setId(IntegerUtil.getInt0(detailMap.get("id")));
			// 找不到数据源使用config的数据源
			if (StringUtils.isBlank(StringUtil.getStr(detailMap.get("datasource_id")))) {
				detail.setDataSource(config.getDataSource());
			} else {
				detail.setDataSource(customJdbcDao.getDao(IntegerUtil.getInt0(detailMap.get("datasource_id"))));
			}
			detail.setKey(StringUtil.getStrEmpty(detailMap.get("key")));
			detail.setValues(StringUtil.getStrEmpty(detailMap.get("values")));
			detailList.add(detail);
		}
		config.setDetailList(detailList);
		cacheService.addConfig(config);
		logger.info("init success config id : " + config.getId());
	}

	public void initDataSource() {
		List<Map<String, Object>> dataSourceList = jdbcTemplate.queryForList("select id,database_name,title,url,username,password,remark from ds_datasource");
		for (Map<String, Object> map : dataSourceList) {
			Integer id = IntegerUtil.getInt0(map.get("id"));
			// id为1是系统默认的连接，如果有则覆盖
			String database_name = StringUtil.getStrEmpty(map.get("database_name"));
			String title = StringUtil.getStrEmpty(map.get("title"));
			String url = StringUtil.getStrEmpty(map.get("url"));
			String username = StringUtil.getStrEmpty(map.get("username"));
			String password = StringUtil.getStrEmpty(map.get("password"));
			String remark = StringUtil.getStrEmpty(map.get("remark"));
			if (id == 1) {
				if (StringUtils.isNotBlank(url) || StringUtils.isNotBlank(username) || StringUtils.isNotBlank(password)) {
					logger.info("检测到配置默认数据源中url、username、password不为空，系统将强行覆盖该数据源为系统配置的datasource.properties里的数据源。");
				}
				continue;
			}
			customJdbcDao.addDataSource(id, database_name, title, url, username, password, remark);
		}
		customJdbcDao.addDefaultDataSource();
		logger.info("init sucess datasource ids : {}", customJdbcDao.getKeySet());
	}

	public void reload() {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			logger.error("重载数据失败", e);
		}
	}

}
