package org.blazer.dataservice.model;

import org.blazer.dataservice.dao.Dao;

public class DSConfigDetail {

	private Integer id;
	private Dao dataSource;
	private DSConfig config;
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

	public DSConfig getConfig() {
		return config;
	}

	public void setConfig(DSConfig config) {
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
