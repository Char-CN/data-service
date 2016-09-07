package org.blazer.dataservice.cache;

import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.init.ConfigInit;
import org.blazer.dataservice.model.ConfigModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "configCache")
public class ConfigCache extends BaseCache {

	private static Logger logger = LoggerFactory.getLogger(ConfigCache.class);

	@Autowired
	ConfigInit configInit;

	/**
	 * 清空所有配置
	 */
	public void clearConfigAll() {
		getCache().clear();
	}

	/**
	 * 清除某个配置
	 * 
	 * @param id
	 */
	public void clearConfigById(Integer id) {
		getCache().evict(id);
	}

	/**
	 * 新增一个配置，如果存在则覆盖。
	 */
	public void addConfig(ConfigModel config) {
		getCache().put(config.getId(), config);
	}

	/**
	 * 获得一个配置
	 * 
	 * @param id
	 * @return
	 */
	public ConfigModel getConfigById(Integer id) {
		if (getCache().get(id) == null) {
			logger.debug("config is not in cache ... id : " + id);
			try {
				configInit.initConfigEntity(id);
			} catch (UnknowDataSourceException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.debug("config in cache ... id : " + id);
		}
		return (ConfigModel) getCache().get(id).get();
	}

	@Override
	public String getCacheName() {
		return "config_cache";
	}

}
