package org.blazer.dataservice.body;

public class GroupBody {

	private Integer id;

	private String text;

	private String state;

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

	@Override
	public String toString() {
		return "GroupBody [id=" + id + ", text=" + text + ", state=" + state + "]";
	}

}
