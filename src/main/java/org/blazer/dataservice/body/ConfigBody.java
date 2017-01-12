package org.blazer.dataservice.body;

import java.util.HashMap;

public class ConfigBody extends Body {

	private Integer id;
	private String configName;
	private String configType;
	private String remark;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public HashMap<String, ConfigDetailBody> getDetails() {
		return details;
	}

	public void setDetails(HashMap<String, ConfigDetailBody> details) {
		this.details = details;
	}

}
