package org.blazer.dataservice.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.blazer.dataservice.dao.Dao;
import org.blazer.dataservice.dao.TransactionDao;
import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.DSDataSource;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.pool.DruidDataSource;

@Repository(value = "CustomJdbcDao")
public class CustomJdbcDao {

	private final static Map<Integer, DSDataSource> dataSourceMap = new HashMap<Integer, DSDataSource>();

	private CustomJdbcDao() {
	}

	public static void addDataSource(Integer id, String database_name, String title, String url, String username, String password, String remark) {
		if (dataSourceMap.containsKey(id)) {
			return;
		}
		DSDataSource dsDataSource = new DSDataSource();
		dsDataSource.setId(id);
		dsDataSource.setDatabase_name(database_name);
		dsDataSource.setTitle(title);
		dsDataSource.setUrl(url);
		dsDataSource.setUsername(username);
		dsDataSource.setPassword(password);
		dsDataSource.setRemark(remark);

		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUsername(username);
		dataSource.setUrl(url);
		dataSource.setPassword(password);
		dataSource.setMaxActive(100);
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
		Dao dao = new TransactionDao(dataSource);
		dsDataSource.setDao(dao);
		dataSourceMap.put(id, dsDataSource);
	}

	public static Dao getDao(Integer id) throws UnknowDataSourceException {
		if (!dataSourceMap.containsKey(id)) {
			throw new UnknowDataSourceException("not found the datasource id[" + id + "]");
		}
		return dataSourceMap.get(id).getDao();
	}

	public static DSDataSource getDataSourceBean(Integer id) throws UnknowDataSourceException {
		if (!dataSourceMap.containsKey(id)) {
			throw new UnknowDataSourceException("not found the datasource id[" + id + "]");
		}
		return dataSourceMap.get(id);
	}

	public static Set<Integer> getKeySet() {
		return dataSourceMap.keySet();
	}

}
