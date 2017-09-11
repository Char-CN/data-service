package org.blazer.dataservice.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.util.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AccessInterceptor extends HandlerInterceptorAdapter {

	private static Logger logger = LoggerFactory.getLogger("access");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String sessionStr = null;
		if (request.getCookies() != null)
		for (Cookie cookie : request.getCookies()) {
			if ("US_SESSION_ID".equals(cookie.getName())) {
				sessionStr = cookie.getValue();
				break;
			}
		}
		logger.debug("IP:" + IPUtil.getIpAddr(request) + "|^_^|US_SESSION_ID:" + sessionStr + "|^_^|URL:" + request.getRequestURL() + "|^_^|QueryString:"
				+ request.getQueryString());
		return super.preHandle(request, response, handler);
	}

}
