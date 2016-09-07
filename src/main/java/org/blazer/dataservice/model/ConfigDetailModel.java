package org.blazer.dataservice.model;

import java.io.Serializable;

import org.blazer.dataservice.dao.Dao;

public class ConfigDetailModel implements Serializable {

	private static final long serialVersionUID = -5812714497821525878L;
	private Integer id;
	private Dao dataSource;
	private ConfigModel config;
	private String key;
	private String values;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Dao getDataSource() {
		return dataSource;
	}

	public void setDataSource(Dao dataSource) {
		this.dataSource = dataSource;
	}

	public ConfigModel getConfig() {
		return config;
	}

	public void setConfig(ConfigModel config) {
		this.config = config;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

}
