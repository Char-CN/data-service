package org.blazer.dataservice.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.DSDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

@Component(value = "customJdbcDao")
public class CustomJdbcDao {

	private static Logger logger = LoggerFactory.getLogger(CustomJdbcDao.class);

	private Map<Integer, DSDataSource> dataSourceMap = new HashMap<Integer, DSDataSource>();

	/**
	 * 新增一个数据源，如果存在则覆盖。
	 * 
	 * @param id
	 * @param database_name
	 * @param title
	 * @param url
	 * @param username
	 * @param password
	 * @param remark
	 */
	public void addDataSource(Integer id, String database_name, String title, String url, String username, String password, String remark) {
		DSDataSource dsDataSource = new DSDataSource();
		try {
			dsDataSource.setId(id);
			dsDataSource.setDatabase_name(database_name);
			dsDataSource.setTitle(title);
			dsDataSource.setUrl(url);
			dsDataSource.setUsername(username);
			dsDataSource.setPassword(password);
			dsDataSource.setRemark(remark);
			DruidDataSource dataSource = new DruidDataSource();
			// dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			// System.out.println("<<<<<<<<<<<<<<<<" + url);
			// System.out.println("<<<<<<<<<<<<<<<<" + username);
			// System.out.println("<<<<<<<<<<<<<<<<" + password);
			dataSource.setUsername(username);
			dataSource.setUrl(url);
			dataSource.setPassword(password);
			// 配置参数，需要后期优化
			dataSource.setInitialSize(2);
			dataSource.setMinIdle(2);
			dataSource.setMaxActive(50);
			dataSource.setMaxWait(60000);
			dataSource.setTimeBetweenEvictionRunsMillis(60000);
			dataSource.setMinEvictableIdleTimeMillis(300000);
			dataSource.setValidationQuery("SELECT 'x'");
			dataSource.setTestWhileIdle(true);
			dataSource.setTestOnBorrow(false);
			dataSource.setTestOnReturn(false);
			// 官方建议mysql为false，oracle为true
			dataSource.setPoolPreparedStatements(false);
			// dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
			Dao dao = new TransactionDao(dataSource);
			dsDataSource.setDao(dao);
		} catch (Exception e) {
			logger.error("ERROR[{}]", e);
		}
		dataSourceMap.put(id, dsDataSource);
	}

	/**
	 * 获得一个数据源连接
	 * 
	 * @param id
	 * @return
	 * @throws UnknowDataSourceException
	 */
	public Dao getDao(Integer id) throws UnknowDataSourceException {
		if (!dataSourceMap.containsKey(id)) {
			throw new UnknowDataSourceException("not found the datasource id[" + id + "]");
		}
		return dataSourceMap.get(id).getDao();
	}

	/**
	 * 获得一个数据源实体
	 * 
	 * @param id
	 * @return
	 * @throws UnknowDataSourceException
	 */
	public DSDataSource getDataSourceBean(Integer id) throws UnknowDataSourceException {
		if (!dataSourceMap.containsKey(id)) {
			throw new UnknowDataSourceException("not found the datasource id[" + id + "]");
		}
		return dataSourceMap.get(id);
	}

	/**
	 * 获得所有数据源id
	 * 
	 * @return
	 */
	public Set<Integer> getKeySet() {
		return dataSourceMap.keySet();
	}

}
