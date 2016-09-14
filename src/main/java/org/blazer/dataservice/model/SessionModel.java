package org.blazer.dataservice.model;

import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.LongUtil;
import org.blazer.dataservice.util.StringUtil;

public class SessionModel {

	// LoginType,UserId,UserName,ExpireTime

	private LoginType loginType;

	private Integer userId;

	private String userName;

	private Long expireTime;

	private boolean isValid;

	public static void main(String[] args) {

	}

	public SessionModel(String sessionStr) {
		if (StringUtils.isBlank(sessionStr)) {
			setLoginType(null);
			setUserId(null);
			setUserName(null);
			setExpireTime(null);
			setValid(false);
			return;
		}
		String[] content = StringUtils.splitByWholeSeparator(sessionStr, ",");
		if (content.length != 4) {
			setLoginType(null);
			setUserId(null);
			setUserName(null);
			setExpireTime(null);
			setValid(false);
			return;
		}
		setLoginType(LoginType.valueOf(IntegerUtil.getInt0(content[0])));
		setUserId(IntegerUtil.getInt0(content[1]));
		setUserName(StringUtil.getStr(content[2]));
		setExpireTime(LongUtil.getLong(content[3]));
		setValid(true);
	}

	public LoginType getLoginType() {
		return loginType;
	}

	public void setLoginType(LoginType loginType) {
		this.loginType = loginType;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public String toString() {
		return "SessionModel [loginType=" + loginType + ", userId=" + userId + ", userName=" + userName + ", expireTime=" + expireTime + "]";
	}

}
