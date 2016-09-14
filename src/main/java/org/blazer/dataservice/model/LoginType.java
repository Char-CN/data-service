package org.blazer.dataservice.model;

public enum LoginType {

	unknow("unknow", 0), userName("userName", 1), phoneNumber("phoneNumber", 2), email("email", 3);

	public String name;
	public int index;

	private LoginType(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public static LoginType valueOf(int value) {
		switch (value) {
		case 0:
			return LoginType.unknow;
		case 1:
			return LoginType.userName;
		case 2:
			return LoginType.phoneNumber;
		case 3:
			return LoginType.email;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return this.index + "_" + this.name;
	}

	public static void main(String[] args) {
	}

}
