package org.blazer.scheduler.model;

import java.util.List;

public class ResultModel {

	private Integer total;

	private List<String[]> result;

	private boolean complete;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<String[]> getResult() {
		return result;
	}

	public void setResult(List<String[]> result) {
		this.result = result;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

}
