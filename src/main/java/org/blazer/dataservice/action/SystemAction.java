package org.blazer.dataservice.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.exception.UnknowDataSourceException;
import org.blazer.dataservice.init.ConfigInit;
import org.blazer.dataservice.init.DataSourceInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "systemAction")
@RequestMapping("/system")
public class SystemAction {

	private static Logger logger = LoggerFactory.getLogger(SystemAction.class);

	@Autowired
	DataSourceInit dataSourceInit;

	@Autowired
	ConfigInit configInit;

	@ResponseBody
	@RequestMapping("/referConfig")
	public String getConfig(HttpServletRequest request, HttpServletResponse response) {
		try {
			configInit.initConfigEntity();
		} catch (UnknowDataSourceException e) {
			logger.error(e.toString(), e);
			return "fail";
		}
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/referDataSource")
	public String referDataSource(HttpServletRequest request, HttpServletResponse response) {
		dataSourceInit.initDataSource();
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/gc")
	public String gc(HttpServletRequest request, HttpServletResponse response) {
		System.gc();
		return "ok";
	}

}
