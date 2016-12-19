package org.blazer.dataservice.body;

import java.util.List;

public class TreeBody {

	private Integer id;
	private String text;
	private String state;
	private String iconCls;
	private List<TreeBody> children;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public List<TreeBody> getChildren() {
		return children;
	}

	public void setChildren(List<TreeBody> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "TreeBody [id=" + id + ", text=" + text + ", state=" + state + ", iconCls=" + iconCls + ", children=" + children + "]";
	}

}
