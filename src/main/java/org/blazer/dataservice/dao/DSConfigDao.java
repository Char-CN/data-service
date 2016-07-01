package org.blazer.dataservice.dao;

import java.util.HashMap;
import java.util.Map;

import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.DSConfig;
import org.springframework.stereotype.Component;

@Component(value = "dsConfigDao")
public class DSConfigDao {

	private Map<Integer, DSConfig> configMap = new HashMap<Integer, DSConfig>();

	/**
	 * 新增一个数据源，如果存在则覆盖。
	 */
	public void addConfig(DSConfig config) throws UnknowDataSourceException {
		configMap.put(config.getId(), config);
	}

	public DSConfig getConfig(Integer id) {
		return configMap.get(id);
	}

}
