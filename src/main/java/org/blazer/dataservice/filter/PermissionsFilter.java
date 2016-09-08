package org.blazer.dataservice.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class PermissionsFilter implements Filter {

	private String systemName = null;

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		System.out.println(systemName);
		if (systemName != null) {
			HttpServletRequest request = (HttpServletRequest) req;
			System.out.println(request.getRequestURL());
			System.out.println(request.getRequestURI());
			System.out.println(request.getRemoteHost());
			System.out.println(request.getRemoteAddr());
		}
		chain.doFilter(req, resp);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		systemName = filterConfig.getInitParameter("systemName");
		System.out.println("init filter : " + systemName);
	}

	public void destroy() {
	}

}
