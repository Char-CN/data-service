package org.blazer.dataservice.action;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.blazer.dataservice.body.Body;

public class BaseAction {

	public HashMap<String, String> getParamMap(HttpServletRequest request) {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			paramMap.put(key, request.getParameter(key));
		}
		return paramMap;
	}

	public Body fail() {
		return new Body().error();
	}

	public Body success() {
		return new Body();
	}

}
