package org.blazer.dataservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.dao.CustomJdbcDao;
import org.blazer.dataservice.dao.DSConfigDao;
import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.DSConfig;
import org.blazer.dataservice.model.DSConfigDetail;
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
	DSConfigDao dsConfigDao;

	@Value("#{dataSourceProperties.url}")
	public String url;

	@Value("#{dataSourceProperties.username}")
	public String username;

	@Value("#{dataSourceProperties.password}")
	public String password;

	public void afterPropertiesSet() throws Exception {
		//////////////////////// 加载数据源 ////////////////////////
		initDataSource();
		//////////////////////// 加载配置项 ////////////////////////
		initConfigEntity();
	}

	public void initConfigEntity() throws UnknowDataSourceException {
		List<Map<String, Object>> configList = jdbcTemplate.queryForList("select id,datasource_id,config_name,config_type from ds_config");
		for (Map<String, Object> configMap : configList) {
			DSConfig config = new DSConfig();
			config.setId(IntegerUtil.getInt0(configMap.get("id")));
			// 找不到数据源使用默认数据源
			if (StringUtils.isBlank(StringUtil.getStr(configMap.get("datasource_id")))) {
				config.setDataSource(customJdbcDao.getDao(1));
			} else {
				config.setDataSource(customJdbcDao.getDao(IntegerUtil.getInt0(configMap.get("datasource_id"))));
			}
			config.setConfigName(StringUtil.getStrEmpty(configMap.get("config_name")));
			config.setConfigType(StringUtil.getStrEmpty(configMap.get("config_type")));
			List<DSConfigDetail> detailList = new ArrayList<DSConfigDetail>();
			List<Map<String, Object>> rstList = jdbcTemplate
					.queryForList("select id,datasource_id,config_id,`key`,`values` from ds_config_detail where config_id=?", config.getId());
			for (Map<String, Object> detailMap : rstList) {
				DSConfigDetail detail = new DSConfigDetail();
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
			dsConfigDao.addConfig(config);
		}
		logger.info("init config list size : " + configList.size());
	}

	public void initDataSource() {
		List<Map<String, Object>> dataSourceList = jdbcTemplate.queryForList("select id,database_name,title,url,username,password,remark from ds_datasource");
		boolean defaultSource = false;
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
				initDefaultDataSource();
				defaultSource = true;
				continue;
			}
			customJdbcDao.addDataSource(id, database_name, title, url, username, password, remark);
		}
		if (!defaultSource) {
			initDefaultDataSource();
		}
		logger.info("init datasource ids : {}", customJdbcDao.getKeySet());
	}

	private void initDefaultDataSource() {
		customJdbcDao.addDataSource(1, "mysql", "default", url, username, password, "");
	}

	public void reload() {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			logger.error("重载数据失败", e);
		}
	}

}
