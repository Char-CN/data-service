package org.blazer.dataservice.body;

import java.util.List;

public class ConfigBody {

	private Integer id;
	private String configName;
	private String configType;
	private List<ConfigDetailBody> list;

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

	public List<ConfigDetailBody> getList() {
		return list;
	}

	public void setList(List<ConfigDetailBody> list) {
		this.list = list;
	}

}
