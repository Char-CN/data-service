package org.blazer.dataservice.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.util.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class WebRequestInterceptor extends HandlerInterceptorAdapter {

	private static Logger logger = LoggerFactory.getLogger("access");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		logger.debug("IP : " + IPUtil.getIpAddr(request));
		logger.debug("Query String: " + request.getQueryString());
		logger.debug("Request URL: " + request.getRequestURL());
		response.setHeader("Access-Control-Allow-Origin", "*");
		return super.preHandle(request, response, handler);
	}

}
