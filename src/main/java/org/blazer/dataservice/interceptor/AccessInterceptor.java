package org.blazer.dataservice.interceptor;

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
		logger.debug("IP:" + IPUtil.getIpAddr(request) + ",URL:" + request.getRequestURL() + "?" + request.getQueryString());
		return super.preHandle(request, response, handler);
	}

}
