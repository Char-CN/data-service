package org.blazer.dataservice.model;

import java.io.Serializable;

public class PermissionsModel implements Serializable {

	private static final long serialVersionUID = -5468934981914473709L;
	private Integer id;
	private Integer systemId;
	private String systemName;
	private String permissionsName;
	private String url;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getPermissionsName() {
		return permissionsName;
	}

	public void setPermissionsName(String permissionsName) {
		this.permissionsName = permissionsName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "PermissionsModel [id=" + id + ", systemId=" + systemId + ", systemName=" + systemName + ", permissionsName=" + permissionsName + ", url=" + url
				+ "]";
	}

}
