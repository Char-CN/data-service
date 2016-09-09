package org.blazer.dataservice.body;

public class LoginBody extends Body {

	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public LoginBody setSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}

}
