package org.blazer.scheduler.service;

import java.util.List;
import java.util.Map;

import org.blazer.dataservice.util.HMap;
import org.blazer.scheduler.entity.Task;
import org.blazer.scheduler.entity.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import net.sf.ehcache.search.attribute.UnknownAttributeException;

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
	public Task add(Task task) throws Exception {
		logger.debug("add task : " + task);
		if (TaskType.cron_auto.toString().equals(task.getTypeName())) {
			return add2CronAuto(task);
		} else if (TaskType.right_now.toString().equals(task.getTypeName())) {
			return add2RightNow(task);
		}
		throw new UnknownAttributeException("task type [" + task.getTypeName() + "] is not valid.");
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
//		String likeTaskName = task.getTaskName().substring(0, task.getTaskName().lastIndexOf("_") + 1) + "___";
//		String sql = "select * from scheduler_task where task_name=(select max(s.task_name) from scheduler_task s where s.task_name like ?)";
//		logger.debug(SqlUtil.Show(sql, likeTaskName));
//		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, likeTaskName);
//		// 存在taskName的任务则需要重新新增一个。
//		if (list != null && list.size() != 0) {
////    			task = HMap.to(list.get(0), Task.class);
//			task.setTaskName(StringUtil.getStrEmpty(list.get(0).get("task_name")));
//			logger.debug("task name [" + task.getTaskName() + "] exists");
//			String number = task.getTaskName().substring(task.getTaskName().lastIndexOf("_") + 1);
//			String newTaskName = task.getTaskName().substring(0, task.getTaskName().lastIndexOf("_") + 1);
//			Integer newNumber = IntegerUtil.getInt0(number) + 1;
//			// 补0
//			if (newNumber < 10) {
//				newTaskName += "00" + newNumber;
//			} else if (newNumber < 100) {
//				newTaskName += "0" + newNumber;
//			} else {
//				newTaskName += newNumber;
//			}
//			task.setTaskName(newTaskName);
//			logger.debug("new task name : " + newTaskName);
//		}
		String sql = "insert into scheduler_task(job_id, status_id, type_name, task_name, command, log_path, error_log_path)" + " values(?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, task.getJobId(), task.getStatusId(), task.getTypeName(), task.getTaskName(), task.getCommand(),
				task.getLogPath() + task.getTaskName() + ".log", task.getErrorLogPath() + task.getTaskName() + ".error");
		sql = "select * from scheduler_task where task_name=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, task.getTaskName());
		return HMap.to(list.get(0), Task.class);
	}

	public void updateExecuteTime(Task task) {
		String sql = "update scheduler_task set execute_time=now() where task_name=?";
		jdbcTemplate.update(sql, task.getTaskName());
	}

	public void updateEndTime(Task task) {
		String sql = "update scheduler_task set end_time=now() where task_name=?";
		jdbcTemplate.update(sql, task.getTaskName());
	}

	public static void main(String[] args) {
		String str = "${yyyy_MM_dd_HH_mm}_${JobId}_[cron_auto|right_now]_01";
		System.out.println(str.lastIndexOf("_"));
		System.out.println(str.substring(str.lastIndexOf("_") + 1));
	}

}
