package org.blazer.dataservice.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.blazer.dataservice.model.ConfigModel;
import org.springframework.stereotype.Component;

@Component(value = "cacheService")
public class CacheService {

	private static final Map<Integer, ConfigModel> configMap = new ConcurrentHashMap<Integer, ConfigModel>();

	/**
	 * 清空所有配置
	 */
	public void clearConfigAll() {
		configMap.clear();
	}

	/**
	 * 清除某个配置
	 * 
	 * @param id
	 */
	public void clearConfigById(Integer id) {
		configMap.remove(id);
	}

	/**
	 * 新增一个配置，如果存在则覆盖。
	 */
	public void addConfig(ConfigModel config) {
		configMap.put(config.getId(), config);
	}

	/**
	 * 获得一个配置
	 * 
	 * @param id
	 * @return
	 */
	public ConfigModel getConfigById(Integer id) {
		return configMap.get(id);
	}

}
