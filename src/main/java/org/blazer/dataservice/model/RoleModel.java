package org.blazer.dataservice.model;

import java.io.Serializable;
import java.util.List;

public class RoleModel implements Serializable {

	private static final long serialVersionUID = -2151798820711872969L;
	private Integer id;
	private String roleName;
	private List<Integer> permissionsIdList;
	private List<Integer> userIdList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<Integer> getPermissionsIdList() {
		return permissionsIdList;
	}

	public void setPermissionsIdList(List<Integer> permissionsIdList) {
		this.permissionsIdList = permissionsIdList;
	}

	public List<Integer> getUserIdList() {
		return userIdList;
	}

	public void setUserIdList(List<Integer> userIdList) {
		this.userIdList = userIdList;
	}

	@Override
	public String toString() {
		return "RoleModel [id=" + id + ", roleName=" + roleName + "]";
	}

}
