package org.blazer.dataservice.body;

import java.util.List;
import java.util.Map;

public class ConfigDetailBody {

	private Integer id;
	private List<Map<String, Object>> values;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Map<String, Object>> getValues() {
		return values;
	}

	public void setValues(List<Map<String, Object>> values) {
		this.values = values;
	}

}
