package org.blazer.dataservice.model;

import java.io.Serializable;
import java.util.List;

import org.blazer.dataservice.dao.Dao;

public class ConfigModel implements Serializable {

	private static final long serialVersionUID = 462879975846041095L;
	private Integer id;
	private Integer groupId;
	private Dao dataSource;
	private DataSourceModel dataSourceModel;
	private String configName;
	private String configType;
	private boolean isInterface;
	private boolean isTask;
	private List<ConfigDetailModel> detailList;

	public DataSourceModel getDataSourceModel() {
		return dataSourceModel;
	}

	public void setDataSourceModel(DataSourceModel dataSourceModel) {
		this.dataSourceModel = dataSourceModel;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

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

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public boolean isTask() {
		return isTask;
	}

	public void setTask(boolean isTask) {
		this.isTask = isTask;
	}

	public List<ConfigDetailModel> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<ConfigDetailModel> detailList) {
		this.detailList = detailList;
	}

}
