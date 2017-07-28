package org.blazer.dataservice.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.cache.ConfigCache;
import org.blazer.dataservice.model.CacheModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "cacheAction")
@RequestMapping("/cache")
public class CacheAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(CacheAction.class);

	@Autowired
	ConfigCache configCache;

	@ResponseBody
	@RequestMapping("/space")
	public List<CacheModel> list(HttpServletRequest request, HttpServletResponse response) {
		List<CacheModel> list = new ArrayList<CacheModel>();
		list.add(configCache.getCalcCacheModel());
		logger.debug("Use Space : " + list.toString());
		return list;
	}

}
