package org.blazer.scheduler.service;

import java.util.List;
import java.util.Map;
import java.util.UnknownFormatConversionException;

import org.blazer.dataservice.util.HMap;
import org.blazer.scheduler.core.ProcessHelper;
import org.blazer.scheduler.entity.Status;
import org.blazer.scheduler.entity.Task;
import org.blazer.scheduler.entity.TaskType;
import org.blazer.scheduler.model.LogModel;
import org.blazer.scheduler.model.TaskLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service(value = "taskService")
public class TaskService {

	private static final Logger logger = LoggerFactory.getLogger("scheduler");

	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * taskName规则：${yyyy_MM_dd_HH_mm}_${JobId}_[cron_auto|right_now]_01
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	public Task addOrGet(Task task) throws Exception {
		logger.debug("add task : " + task);
		if (TaskType.cron_auto.toString().equals(task.getTypeName())) {
			return add2CronAuto(task);
		} else if (TaskType.right_now.toString().equals(task.getTypeName())) {
			return add2RightNow(task);
		}
		throw new UnknownFormatConversionException("task type [" + task.getTypeName() + "] is not valid.");
	}

	/**
	 * 如果是cron_auto类型，存在重复的taskName，则会返回该条记录
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private Task add2CronAuto(Task task) throws Exception {
		String sql = "select * from scheduler_task where task_name=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, task.getTaskName());
		if (list == null || list.size() == 0) {
			logger.debug("task not exists.");
			sql = "insert into scheduler_task(job_id, status_id, type_name, task_name, command, log_path, error_log_path)" + " values(?,?,?,?,?,?,?)";
			jdbcTemplate.update(sql, task.getJobId(), task.getStatusId(), task.getTypeName(), task.getTaskName(), task.getCommand(),
					task.getLogPath() + task.getTaskName() + ".log", task.getErrorLogPath() + task.getTaskName() + ".error");
			sql = "select * from scheduler_task where task_name=?";
			list = jdbcTemplate.queryForList(sql, task.getTaskName());
		} else {
			logger.debug("task exists.");
		}
		return HMap.to(list.get(0), Task.class);
	}

	/**
	 * 如果是right_now类型，存在重复的taskName，则会新增一条该taskName最后2位+1的task返回该条记录
	 * 
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private Task add2RightNow(Task task) throws Exception {
		String sql = "insert into scheduler_task(job_id, status_id, type_name, task_name, command, log_path, error_log_path)" + " values(?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, task.getJobId(), task.getStatusId(), task.getTypeName(), task.getTaskName(), task.getCommand(),
				task.getLogPath() + task.getTaskName() + ".log", task.getErrorLogPath() + task.getTaskName() + ".error");
		sql = "select * from scheduler_task where task_name=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, task.getTaskName());
		return HMap.to(list.get(0), Task.class);
	}

	/**
	 * 传入task对象前请修改statusid
	 * 
	 * @param task
	 */
	public void updateExecuteTimeNowAndParamsAndStatus(Task task) {
		String sql = "update scheduler_task set execute_time=now(), status_id=?, params=? where task_name=?";
		jdbcTemplate.update(sql, task.getStatusId(), task.getParams(), task.getTaskName());
	}

	/**
	 * 传入task对象前请修改statusid
	 * 
	 * @param task
	 */
	public void updateEndTimeNowAndStatus(Task task) {
		String sql = "update scheduler_task set end_time=now(), status_id=? where task_name=?";
		jdbcTemplate.update(sql, task.getStatusId(), task.getTaskName());
	}

	/**
	 * 传入task对象前请修改exception和statusid
	 * 
	 * @param task
	 */
	public void updateExceptionAndStatus(Task task) {
		String sql = "update scheduler_task set exception=?, status_id=? where task_name=?";
		jdbcTemplate.update(sql, task.getException(), task.getStatusId(), task.getTaskName());
	}

	public static void main(String[] args) {
		String str = "${yyyy_MM_dd_HH_mm}_${JobId}_[cron_auto|right_now]_01";
		System.out.println(str.lastIndexOf("_"));
		System.out.println(str.substring(str.lastIndexOf("_") + 1));
	}

	public void updateTaskRemark(Task task) {
		String sql = "update scheduler_task set remark=? where task_name=?";
		jdbcTemplate.update(sql, task.getRemark(), task.getTaskName());
	}

	public Task findTaskByName(String taskName) throws Exception {
		String sql = "select * from scheduler_task where task_name=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, taskName);
		if (list == null || list.size() == 0) {
			return new Task();
		}
		Task task = HMap.to(list.get(0), Task.class);
		task.setStatus(Status.get(task.getStatusId()));
		return task;
	}

	public TaskLog findTaskLogByName(String taskName, Integer skipRowNumber) throws Exception {
		Task task = findTaskByName(taskName);
		// 为了减小服务器压力，每次均只允许读取100行
		LogModel lm = ProcessHelper.readLog(task.getLogPath(), task.getErrorLogPath(), skipRowNumber, 200);
		lm.setContent(lm.getContent().replaceAll("\n", "<br/>"));
		TaskLog taskLog = new TaskLog();
		taskLog.setTask(task);
		taskLog.setLogModel(lm);
		return taskLog;
	}

}
