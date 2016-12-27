package org.blazer.scheduler.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.blazer.dataservice.action.BaseAction;
import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.dataservice.util.StringUtil;
import org.blazer.scheduler.core.SchedulerServer;
import org.blazer.scheduler.entity.JobParam;
import org.blazer.scheduler.entity.Task;
import org.blazer.scheduler.service.TaskService;
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

	@Autowired
	TaskService taskService;

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
		return schedulerServer.spawnRightNowTaskProcess(schedulerServer.getJobById(IntegerUtil.getInt0(paramMap.get("jobId")))).getTask();
	}

	@ResponseBody
	@RequestMapping("/addcustom")
	public Task addCustom(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, String> paramMap = getParamMap(request);
		logger.debug("params:" + paramMap);
		List<JobParam> list = new ArrayList<JobParam>();
		for (String param : StringUtil.getStrEmpty(paramMap.get("params")).split(",")) {
			String[] strs = param.split("=");
			JobParam jp = new JobParam(strs[0], strs[1]);
			list.add(jp);
		}
		return schedulerServer.spawnRightNowTaskProcess(paramMap.get("command"), list).getTask();
	}

	@ResponseBody
	@RequestMapping("/findTaskByName")
	public Task findTaskByName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, String> paramMap = getParamMap(request);
		String taskName = paramMap.get("taskName");
		return taskService.findTaskByName(taskName);
	}

}
