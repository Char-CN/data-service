package org.blazer.dataservice.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AllowCrossDomainInterceptor extends HandlerInterceptorAdapter {

	private static Logger logger = LoggerFactory.getLogger(AllowCrossDomainInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.debug(request.getRequestURI());
		return super.preHandle(request, response, handler);
	}

}
