package org.blazer.dataservice.entity;

public class DSGroup {

	private Integer id;
	private Integer parentId;
	private String groupName;
	private Integer orderAsc;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getOrderAsc() {
		return orderAsc;
	}

	public void setOrderAsc(Integer orderAsc) {
		this.orderAsc = orderAsc;
	}

}
