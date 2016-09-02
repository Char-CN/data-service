package org.blazer.dataservice.model;

import java.util.List;

public class UserModel {

	private Integer id;
	private String userName;
	private String phoneNumber;
	private String email;
	private List<Integer> roleIdList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Integer> getRoleIdList() {
		return roleIdList;
	}

	public void setRoleIdList(List<Integer> roleIdList) {
		this.roleIdList = roleIdList;
	}

	@Override
	public String toString() {
		return "UserModel [id=" + id + ", userName=" + userName + ", phoneNumber=" + phoneNumber + ", email=" + email + "]";
	}

}
