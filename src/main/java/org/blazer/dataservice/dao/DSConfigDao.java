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
	 * 清空所有配置
	 */
	public void clear() {
		configMap.clear();
	}

	/**
	 * 清除某个配置
	 * @param id
	 */
	public void clear(Integer id) {
		configMap.remove(id);
	}

	/**
	 * 新增一个配置，如果存在则覆盖。
	 */
	public void addConfig(DSConfig config) throws UnknowDataSourceException {
		configMap.put(config.getId(), config);
	}

	/**
	 * 获得一个配置
	 * @param id
	 * @return
	 */
	public DSConfig getConfig(Integer id) {
		return configMap.get(id);
	}

}
