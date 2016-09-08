package org.blazer.dataservice.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.model.ConfigDetailModel;
import org.blazer.dataservice.model.ConfigModel;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.StringUtil;
import org.blazer.dataservice.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * memory cache
 * 
 * @author hyy
 *
 */
@Component(value = "configCache")
@DependsOn("dataSourceCache")
public class ConfigCache extends BaseCache implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(ConfigCache.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	DataSourceCache dataSourceCache;

	/**
	 * 清空所有配置
	 */
	public void clear() {
		getCache().clear();
	}

	/**
	 * 清除某个配置
	 * 
	 * @param id
	 */
	public void remove(Integer id) {
		getCache().evict(id);
	}

	/**
	 * 新增一个配置，如果存在则覆盖。
	 */
	public void add(ConfigModel config) {
		getCache().put(config.getId(), config);
	}

	/**
	 * 获得一个配置
	 * 
	 * @param id
	 * @return
	 */
	public ConfigModel get(Integer id) {
		if (this.contains(id)) {
			logger.debug("config is not in cache ... id : " + id);
			try {
				this.initConfigEntity(id);
			} catch (UnknowDataSourceException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.debug("config in cache ... id : " + id);
		}
		return (ConfigModel) getCache().get(id).get();
	}

	public boolean contains(Integer id) {
		return getCache().get(id) != null;
	}

	public void afterPropertiesSet() throws Exception {
		logger.debug("系统配置开始加载");
		TimeUtil timeUtil = TimeUtil.createAndPoint().setLogger(logger);
		//////////////////////// 加载配置项 ////////////////////////
		initConfigEntity();
		timeUtil.printMs("加载配置项");
	}

	public void initConfigEntity() throws UnknowDataSourceException {
		// 先清空
		this.clear();
		List<Map<String, Object>> configList = jdbcTemplate.queryForList("select id,datasource_id,config_name,config_type from ds_config where enable=1");
		for (Map<String, Object> configMap : configList) {
			ConfigModel config = new ConfigModel();
			config.setId(IntegerUtil.getInt0(configMap.get("id")));
			// 找不到数据源使用默认数据源
			if (StringUtils.isBlank(StringUtil.getStr(configMap.get("datasource_id")))) {
				config.setDataSource(dataSourceCache.getDao(1));
			} else {
				config.setDataSource(dataSourceCache.getDao(IntegerUtil.getInt0(configMap.get("datasource_id"))));
			}
			config.setConfigName(StringUtil.getStrEmpty(configMap.get("config_name")));
			config.setConfigType(StringUtil.getStrEmpty(configMap.get("config_type")));
			List<ConfigDetailModel> detailList = new ArrayList<ConfigDetailModel>();
			List<Map<String, Object>> rstList = jdbcTemplate
					.queryForList("select id,datasource_id,config_id,`key`,`values` from ds_config_detail where config_id=? and enable=1", config.getId());
			for (Map<String, Object> detailMap : rstList) {
				ConfigDetailModel detail = new ConfigDetailModel();
				detail.setId(IntegerUtil.getInt0(detailMap.get("id")));
				// 找不到数据源使用config的数据源
				if (StringUtils.isBlank(StringUtil.getStr(detailMap.get("datasource_id")))) {
					detail.setDataSource(config.getDataSource());
				} else {
					detail.setDataSource(dataSourceCache.getDao(IntegerUtil.getInt0(detailMap.get("datasource_id"))));
				}
				detail.setKey(StringUtil.getStrEmpty(detailMap.get("key")));
				detail.setValues(StringUtil.getStrEmpty(detailMap.get("values")));
				detailList.add(detail);
			}
			config.setDetailList(detailList);
			this.add(config);
		}
		logger.info("init success config list size : " + configList.size());
	}

	public void initConfigEntity(Integer id) throws UnknowDataSourceException {
		if (id == null) {
			logger.info("config id is null, init fail");
			return;
		}
		// 先清除
		this.remove(id);
		List<Map<String, Object>> configList = jdbcTemplate
				.queryForList("select id,datasource_id,config_name,config_type from ds_config where enable=1 and id=?", id);
		if (configList.size() == 0) {
			logger.info("config is not found, id : " + id + ", init fail");
			return;
		}
		ConfigModel config = new ConfigModel();
		config.setId(IntegerUtil.getInt0(configList.get(0).get("id")));
		// 找不到数据源使用默认数据源
		if (StringUtils.isBlank(StringUtil.getStr(configList.get(0).get("datasource_id")))) {
			config.setDataSource(dataSourceCache.getDao(1));
		} else {
			config.setDataSource(dataSourceCache.getDao(IntegerUtil.getInt0(configList.get(0).get("datasource_id"))));
		}
		config.setConfigName(StringUtil.getStrEmpty(configList.get(0).get("config_name")));
		config.setConfigType(StringUtil.getStrEmpty(configList.get(0).get("config_type")));
		List<ConfigDetailModel> detailList = new ArrayList<ConfigDetailModel>();
		List<Map<String, Object>> rstList = jdbcTemplate
				.queryForList("select id,datasource_id,config_id,`key`,`values` from ds_config_detail where config_id=? and enable=1", config.getId());
		for (Map<String, Object> detailMap : rstList) {
			ConfigDetailModel detail = new ConfigDetailModel();
			detail.setId(IntegerUtil.getInt0(detailMap.get("id")));
			// 找不到数据源使用config的数据源
			if (StringUtils.isBlank(StringUtil.getStr(detailMap.get("datasource_id")))) {
				detail.setDataSource(config.getDataSource());
			} else {
				detail.setDataSource(dataSourceCache.getDao(IntegerUtil.getInt0(detailMap.get("datasource_id"))));
			}
			detail.setKey(StringUtil.getStrEmpty(detailMap.get("key")));
			detail.setValues(StringUtil.getStrEmpty(detailMap.get("values")));
			detailList.add(detail);
		}
		config.setDetailList(detailList);
		this.add(config);
		logger.info("init success config id : " + config.getId());
	}

	public void reload() {
		try {
			afterPropertiesSet();
		} catch (Exception e) {
			logger.error("重载数据失败", e);
		}
	}

	@Override
	public String getCacheName() {
		return "config_cache";
	}

}
