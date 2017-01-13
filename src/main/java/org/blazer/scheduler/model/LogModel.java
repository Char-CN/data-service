package org.blazer.scheduler.model;

public class LogModel {

	private Integer total;

	private String content;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "LogModel [total=" + total + ", content=" + content + "]";
	}

}
