package org.blazer.dataservice.action;

import java.text.ParseException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.cache.ConfigCache;
import org.blazer.dataservice.cache.DataSourceCache;
import org.blazer.dataservice.dao.Dao;
import org.blazer.dataservice.util.DimensionInitUtil;
import org.blazer.dataservice.util.IntegerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "systemAction")
@RequestMapping("/system")
public class SystemAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(SystemAction.class);

	@Autowired
	DataSourceCache dataSourceCache;

	@Autowired
	ConfigCache configCache;

	@ResponseBody
	@RequestMapping("/refer")
	public String referDataSource(HttpServletRequest request, HttpServletResponse response) {
		try {
			dataSourceCache.initDataSource();
			configCache.initConfigEntity();
		} catch (Exception e) {
			logger.error(e.toString(), e);
			return "fail";
		}
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/gc")
	public String gc(HttpServletRequest request, HttpServletResponse response) {
		System.gc();
		return "ok";
	}

	// 请求url:/system/di.do?firstDay=20160101&dayCount=1000&dataSourceId=1&tableName=dw_realtime.rt_time
	@ResponseBody
	@RequestMapping("/di")
	public String dimensionInit(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> map = getParamMap(request);
		try {
			Dao dao = dataSourceCache.getDao(IntegerUtil.getInt0(map.get("dataSourceId")));
			DimensionInitUtil.init(map.get("firstDay"), map.get("dayCount"), dao, map.get("tableName"));
		} catch (ParseException e) {
			logger.error(e.toString(), e);
			return "fail";
		}
		return "ok";
	}

}
