package org.blazer.dataservice.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.dao.Dao;
import org.blazer.dataservice.dao.TransactionDao;
import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.DataSourceModel;
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

import com.alibaba.druid.pool.DruidDataSource;

/**
 * memory cache
 * 
 * @author hyy
 *
 */
@Component(value = "dataSourceCache")
public class DataSourceCache implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(DataSourceCache.class);

	public static final Integer DEFAULT_DATASOURCE_ID = 1;

	private Map<Integer, DataSourceModel> dataSourceMap = new ConcurrentHashMap<Integer, DataSourceModel>();

	@Autowired
	JdbcTemplate jdbcTemplate;

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
		// 初始化系统数据源
		String sql = "insert into ds_datasource(id,database_name,title,url,host,port,dbname,username,password,remark,enable) values(?,?,?,?,?,?,?,?,?,?,?)"
				+ " on duplicate key update database_name=?,title=?,url=?,host=?,port=?,dbname=?,username=?,password=?,remark=?,enable=?";
		String host = StringUtil.findOneStrByReg(url, ".*://(.*):.*");
		String port = StringUtil.findOneStrByReg(url, ".*:(\\d*).*");
		String dbName = StringUtil.findOneStrByReg(url, ".*/([A-Za-z0-9_]*)[?]*.*");
		jdbcTemplate.update(sql, 1, "mysql", "default", url, host, port, dbName, username, password, "当前数据源，也就是当前库的数据源，由系统启动时初始化配置。该数据不可删除！", 1, "mysql", "default", url, host, port,
				dbName, username, password, "当前数据源，也就是当前库的数据源，由系统启动时初始化配置。该数据不可删除！", 1);
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
			this.addDataSource(id, database_name, title, url, username, password, remark);
		}
		this.addDefaultDataSource();
		logger.info("init sucess datasource ids : {}", this.getKeySet());
	}

	public void reload() {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			logger.error("重载数据失败", e);
		}
	}

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
			if ("mysql".equals(database_name) || "oracle".equals(database_name)) {
				DruidDataSource dataSource = new DruidDataSource();
				// 设置连接信息
				dataSource.setUsername(username);
				dataSource.setUrl(url);
				dataSource.setPassword(password);
				// 配置参数，需要后期优化
				dataSource.setInitialSize(50);
				dataSource.setMinIdle(50);
				dataSource.setMaxActive(200);
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
			} else {
				dataSourceModel.setDao(null);
			}
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
