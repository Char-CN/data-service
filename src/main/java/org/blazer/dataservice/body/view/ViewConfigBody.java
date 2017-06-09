package org.blazer.dataservice.body.view;

import java.util.List;

public class ViewConfigBody {

	private Integer id;
	private Integer groupId;
	private Integer datasourceId;
	private String userName;
	private String configName;
	private String configType;
	private Integer isInterface;
	private Integer isTask;
	private String remark;
	private Integer enable;
	private List<ViewConfigDetailBody> list;

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

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

	public Integer getIsInterface() {
		return isInterface;
	}

	public void setIsInterface(Integer isInterface) {
		this.isInterface = isInterface;
	}

	public Integer getIsTask() {
		return isTask;
	}

	public void setIsTask(Integer isTask) {
		this.isTask = isTask;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<ViewConfigDetailBody> getList() {
		return list;
	}

	public void setList(List<ViewConfigDetailBody> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "ViewConfigBody [id=" + id + ", groupId=" + groupId + ", datasourceId=" + datasourceId + ", configName=" + configName + ", configType=" + configType
				+ ", enable=" + enable + ", list=" + list + "]";
	}

}
