package org.blazer.dataservice.body.view;

import java.util.List;

import org.blazer.dataservice.entity.MappingConfigJob;

public class ViewMappingConfigJobBody {

	private Integer configId;

	private List<MappingConfigJob> list;

	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public List<MappingConfigJob> getList() {
		return list;
	}

	public void setList(List<MappingConfigJob> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "ViewMappingConfigJobBody [configId=" + configId + ", list=" + list + "]";
	}

}
