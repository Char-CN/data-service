package org.blazer.dataservice.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.service.AnalyticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller(value = "analyticAction")
@RequestMapping("/analytic")
public class AnalyticAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(AnalyticAction.class);

	@Autowired
	AnalyticService analyticService;
	
	@ResponseBody
	@RequestMapping("/getRunTask")
	public List<Map<String, Object>> findReportByTaskName(HttpServletRequest request, HttpServletResponse response) {
		try {
			return analyticService.getRunTask(getParamMap(request));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
