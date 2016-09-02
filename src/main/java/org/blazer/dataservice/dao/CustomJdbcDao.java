package org.blazer.dataservice.dao;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.DataSourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

@Component(value = "customJdbcDao")
public class CustomJdbcDao {

	private static Logger logger = LoggerFactory.getLogger(CustomJdbcDao.class);

	private Map<Integer, DataSourceModel> dataSourceMap = new ConcurrentHashMap<Integer, DataSourceModel>();

	public static final Integer DEFAULT_DATASOURCE_ID = 1;

	@Value("#{dataSourceProperties.url}")
	public String url;

	@Value("#{dataSourceProperties.username}")
	public String username;

	@Value("#{dataSourceProperties.password}")
	public String password;

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
		DataSourceModel dataSourceModel = new DataSourceModel();
		try {
			dataSourceModel.setId(id);
			dataSourceModel.setDatabase_name(database_name);
			dataSourceModel.setTitle(title);
			dataSourceModel.setUrl(url);
			dataSourceModel.setUsername(username);
			dataSourceModel.setPassword(password);
			dataSourceModel.setRemark(remark);
			DruidDataSource dataSource = new DruidDataSource();
			// 设置连接信息
			dataSource.setUsername(username);
			dataSource.setUrl(url);
			dataSource.setPassword(password);
			// 配置参数，需要后期优化
			dataSource.setInitialSize(10);
			dataSource.setMinIdle(10);
			dataSource.setMaxActive(100);
			// 对于长时间不使用的连接强制关闭
			dataSource.setRemoveAbandoned(true);
			// 数据库链接超过多少分钟开始关闭空闲连接,秒为单位
			dataSource.setRemoveAbandonedTimeout(300);
			// 获取连接时最大等待时间，单位毫秒
			dataSource.setMaxWait(60000);
			// Destroy线程会检测连接的间隔时间，testWhileIdle的判断依据，详细看testWhileIdle属性的说明
			dataSource.setTimeBetweenEvictionRunsMillis(60000);
			// 配置一个连接在池中最小生存的时间，单位是毫秒
			dataSource.setMinEvictableIdleTimeMillis(300000);
			dataSource.setValidationQuery("SELECT 'x'");
			// 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
			dataSource.setTestWhileIdle(true);
			dataSource.setTestOnBorrow(false);
			dataSource.setTestOnReturn(false);
			// 官方建议mysql为false，oracle为true
			dataSource.setPoolPreparedStatements(false);
			// dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
			Dao dao = new TransactionDao(dataSource);
			dataSourceModel.setDao(dao);
		} catch (Exception e) {
			logger.error("ERROR[{}]", e);
		}
		dataSourceMap.put(id, dataSourceModel);
	}

	public void addDefaultDataSource() {
		addDataSource(DEFAULT_DATASOURCE_ID, "mysql", "default", url, username, password, "系统默认数据源，即datasource.properties里的数据源。");
	}

	public DataSourceModel getDefaultDataSource() {
		return dataSourceMap.get(DEFAULT_DATASOURCE_ID);
	}

	public Dao getDefaultDao() {
		return dataSourceMap.get(DEFAULT_DATASOURCE_ID).getDao();
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
	public DataSourceModel getDataSourceBean(Integer id) throws UnknowDataSourceException {
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
