package org.blazer.dataservice.init;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.dao.CustomJdbcDao;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.StringUtil;
import org.blazer.dataservice.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("dataSourceInit")
public class DataSourceInit implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(DataSourceInit.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CustomJdbcDao customJdbcDao;

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
