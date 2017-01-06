package org.blazer.dataservice.body;

import java.util.HashMap;

public class ConfigBody extends Body {

	private Integer id;
	private String configName;
	private String configType;
	private HashMap<String, ConfigDetailBody> details;

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

	public HashMap<String, ConfigDetailBody> getDetails() {
		return details;
	}

	public void setDetails(HashMap<String, ConfigDetailBody> details) {
		this.details = details;
	}

}
