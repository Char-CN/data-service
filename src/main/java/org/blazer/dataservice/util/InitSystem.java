package org.blazer.dataservice.util;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("initSystem")
public class InitSystem implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(InitSystem.class);

	@Autowired
	Properties dataSourceProperties;

	@Autowired
	JdbcTemplate jdbcTemplate;

	public void afterPropertiesSet() throws Exception {
		List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from ds_datasource");
		for (Map<String, Object> map : list) {
			Integer id = Integer.parseInt(map.get("id").toString());
			if (id == 1) {
				
			}
			String database_name = map.get("database_name").toString();
			String title = map.get("title").toString();
			String url = map.get("url").toString();
			String username = map.get("username").toString();
			String password = map.get("password").toString();
			String remark = map.get("remark").toString();
			CustomJdbcDao.addDataSource(id, database_name, title, url, username, password, remark);
		}
		try {
			System.out.println(dataSourceProperties.getProperty("url"));
			System.out.println(dataSourceProperties.getProperty("username"));
			System.out.println(dataSourceProperties.getProperty("password"));
		} catch (Exception e) {
			logger.error("Initialize system data ERROR[{}]", e);
		}
	}

	public void reload() {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			logger.error("重载数据失败", e);
		}
	}

}
