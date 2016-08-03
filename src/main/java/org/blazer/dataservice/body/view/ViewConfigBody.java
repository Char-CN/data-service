package org.blazer.dataservice.body.view;

import java.util.List;

public class ViewConfigBody {

	private Integer id;
	private Integer datasourceId;
	private String configName;
	private String configType;
	private List<ViewConfigDetailBody> list;

	public Integer getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(Integer datasourceId) {
		this.datasourceId = datasourceId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public List<ViewConfigDetailBody> getList() {
		return list;
	}

	public void setList(List<ViewConfigDetailBody> list) {
		this.list = list;
	}

}
