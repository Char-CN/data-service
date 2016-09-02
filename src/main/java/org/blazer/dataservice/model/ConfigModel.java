package org.blazer.dataservice.model;

import java.util.List;

import org.blazer.dataservice.dao.Dao;

public class ConfigModel {

	private Integer id;
	private Dao dataSource;
	private String configName;
	private String configType;
	private List<ConfigDetailModel> detailList;

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

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public List<ConfigDetailModel> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<ConfigDetailModel> detailList) {
		this.detailList = detailList;
	}

}
