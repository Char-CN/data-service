package org.blazer.dataservice.dao;

import java.util.HashMap;
import java.util.Map;

import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.DSConfig;

public class DSConfigDao {

	private final static Map<Integer, DSConfig> configMap = new HashMap<Integer, DSConfig>();

	private DSConfigDao() {
	}

	/**
	 * 新增一个数据源，如果存在则覆盖。
	 */
	public static void addConfig(DSConfig config) throws UnknowDataSourceException {
		configMap.put(config.getId(), config);
	}

	public static DSConfig getConfig(Integer id) {
		return configMap.get(id);
	}

}
