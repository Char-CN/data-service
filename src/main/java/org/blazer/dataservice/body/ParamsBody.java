package org.blazer.dataservice.body;

import java.util.HashMap;
import java.util.List;

public class ParamsBody {

	private List<String> params;

	private HashMap<String, List<String>> details;

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public HashMap<String, List<String>> getDetails() {
		return details;
	}

	public void setDetails(HashMap<String, List<String>> details) {
		this.details = details;
	}

}
