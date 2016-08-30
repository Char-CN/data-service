package org.blazer.dataservice.body;

import java.util.List;

public class PageBody<T> {

	private List<T> rows;

	private Integer total;

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	private String toListStr() {
		StringBuilder sb = new StringBuilder("");
		for (Object obj : rows) {
			sb.append(obj.toString());
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "PageBody [rows=" + rows + ", total=" + toListStr() + "]";
	}

}
