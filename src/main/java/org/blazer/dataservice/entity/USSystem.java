package org.blazer.dataservice.entity;

public class USSystem {

	private Integer id;
	private String systemName;
	private String remark;

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "USSystem [id=" + id + ", systemName=" + systemName + ", remark=" + remark + "]";
	}

}
