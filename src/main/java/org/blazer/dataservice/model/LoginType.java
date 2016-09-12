package org.blazer.dataservice.model;

public enum LoginType {

	userName("userName", 1), phoneNumber("phoneNumber", 2), email("email", 3);

	public String name;
	public int index;

	private LoginType(String name, int index) {
		this.name = name;
		this.index = index;
	}

	@Override
	public String toString() {
		return this.index + "_" + this.name;
	}

	public static void main(String[] args) {
		System.out.println(LoginType.email.index);
	}

}
