package org.blazer.dataservice.model;

public class SystemModel {

	private Integer id;
	private String systemName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public String toString() {
		return "SystemModel [id=" + id + ", systemName=" + systemName + "]";
	}

}
