package org.blazer.scheduler.action;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.action.BaseAction;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.scheduler.core.SchedulerServer;
import org.blazer.scheduler.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/scheduler")
public class SchedulerAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger("scheduler");

	@Autowired
	SchedulerServer schedulerServer;

	/**
	 * 根据id与参数获取配置执行的结果值
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/add")
	public Task add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, String> paramMap = getParamMap(request);
		logger.debug("job id : " + paramMap.get("jobId"));
		return schedulerServer.spawnRightNowTaskProcess(IntegerUtil.getInt0(paramMap.get("jobId"))).getTask();
	}

}
