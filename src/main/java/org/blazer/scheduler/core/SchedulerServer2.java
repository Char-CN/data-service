package org.blazer.scheduler.core;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.Status;
import org.blazer.scheduler.entity.Task;
import org.blazer.scheduler.entity.TaskType;
import org.blazer.scheduler.expression.CmdException;
import org.blazer.scheduler.expression.CronException;
import org.blazer.scheduler.model.ProcessModel;
import org.blazer.scheduler.service.JobService;
import org.blazer.scheduler.service.TaskService;
import org.blazer.scheduler.util.DateUtil;
import org.blazer.scheduler.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class SchedulerServer2 extends Thread implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerServer2.class);

	// Task集合，线程安全
	private static final CopyOnWriteArrayList<ProcessModel> processTaskList = new CopyOnWriteArrayList<ProcessModel>();

	// 等待需要生成下一个Task的Job队列，线程安全
	// 等待需要解析下一个Cron表达式符合的时间的JobId队列，线程安全
	private static final ConcurrentLinkedQueue<Integer> waitSpawnTaskJobIdQueue = new ConcurrentLinkedQueue<Integer>();

	// 当前时间[yyyy_MM_dd_HH_mm]为key所对应的需要执行的JobId，线程安全
	private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> timeToJobIdListMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>>();

	// 等待立刻执行的Task队列，线程安全。
	@SuppressWarnings("unused")
	private static final ConcurrentLinkedQueue<Task> waitRightNowRunQueue = new ConcurrentLinkedQueue<Task>();

	// JobId对应的Job，线程安全
	private static final ConcurrentHashMap<Integer, Job> jobIdToJobMap = new ConcurrentHashMap<Integer, Job>();

	@Autowired
	JobService JobService;
	
	@Autowired
	TaskService taskService;

	private static String task_log_dir;

	public void initJob(Job job) throws Exception {
		if (job == null) {
			throw new NullPointerException("job is null.");
		}
		if (CronParserHelper.isNotValid(job.getCron())) {
			throw new CronException("cron [" + job.getCron() + "] expression is not valid.");
		}
		if (StringUtils.isBlank(job.getCommand())) {
			throw new CmdException("cmd [" + job.getCommand() + "] is not valid.");
		}
		jobIdToJobMap.put(job.getId(), job);
		waitSpawnTaskJobIdQueue.add(job.getId());
		// addTime2JobId(job);
	}

	public void removeJob(Integer jonId) {
	}

	private void addTime2JobId(String time, Integer jobId) {
		if (!timeToJobIdListMap.containsKey(time)) {
			timeToJobIdListMap.put(time, new ConcurrentLinkedQueue<Integer>());
		}
		timeToJobIdListMap.get(time).add(jobId);
	}

	private void addTime2JobId(Job job) throws CronException {
		Date nextDate = CronParserHelper.getNextDate(job.getCron());
		String currentTime = DateUtil.showDate(nextDate);
		addTime2JobId(currentTime, job.getId());
	}

	@Override
	public void run() {
		final String _task_log_dir = task_log_dir;
		Thread waitSpawnTaskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Exception exception = null;
				boolean error = false;
				while (true) {
					try {
						if (waitSpawnTaskJobIdQueue.isEmpty()) {
							Thread.sleep(1000);
							continue;
						}
						String currentTime = DateUtil.showDate(DateUtil.newDate());
						Integer jobId = waitSpawnTaskJobIdQueue.element();
						Job job = jobIdToJobMap.get(jobId);
						Task task = new Task();
						String typeName = TaskType.cron_auto.toString();
						String taskName = currentTime + "_" + job.getId() + "_" + typeName + "_01";
						String logPath = _task_log_dir + currentTime + ".log";
						String errorLogPath = _task_log_dir + currentTime + ".error";
						task.setJobId(job.getId());
						task.setTypeName(typeName);
						task.setTaskName(taskName);
						task.setStatusId(Status.WAIT.getId());
						task.setLogPath(logPath);
						task.setErrorLogPath(errorLogPath);
						task = taskService.add(task);
						addTime2JobId(job);
						waitSpawnTaskJobIdQueue.remove();
						error = false;
					} catch (Exception e) {
						error = true;
						exception = e;
					}
					// 在次可扩展，如果出现同一个错误，则不需要记录logger
					if (error) {
						try {
							logger.error(exception.getMessage(), exception);
							Thread.sleep(200);
						} catch (Exception ee) {
						}
					}
				}
			}
		});
		waitSpawnTaskThread.start();
		Thread schedulerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String currentTime = DateUtil.showDate(DateUtil.newDate());
						if (!timeToJobIdListMap.containsKey(currentTime)) {
							Thread.sleep(1000);
							continue;
						}
						ConcurrentLinkedQueue<Integer> jobIdQueue = timeToJobIdListMap.get(currentTime);
						if (jobIdQueue.isEmpty()) {
							timeToJobIdListMap.remove(currentTime);
						} else {
							Integer jobId = jobIdQueue.element();
							Job job = jobIdToJobMap.get(jobId);
							// 路径
							String logPath = _task_log_dir + currentTime + "_" + job.getJobName() + ".log";
							String errorLogPath = _task_log_dir + currentTime + "_" + job.getJobName() + ".error";
							Task task = new Task();
							task.setLogPath(logPath);
							task.setErrorLogPath(errorLogPath);
							String[] cmdArray = StringUtils.split(job.getCommand(), ";");
							String cmd = cmdArray[0];
							Process process = ProcessHelper.run(cmd, logPath, errorLogPath, false);
							ProcessModel pm = new ProcessModel();
							pm.setProcess(process);
							pm.setJob(job);
							pm.setTask(task);
							pm.setCmdIndex(0);
							pm.setCmdArray(cmdArray);
							processTaskList.add(pm);
							waitSpawnTaskJobIdQueue.add(jobId);
							jobIdQueue.remove();
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						try {
							Thread.sleep(100);
						} catch (Exception ee) {
						}
					}
				}
			}
		});
		schedulerThread.start();

		Thread processThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						if (processTaskList.isEmpty()) {
							Thread.sleep(10000);
							continue;
						}
						// 线程安全List可以在循环内直接删除元素。因为在源码中remove方法的时候会调用ReentrantLock。
						for (ProcessModel pm : processTaskList) {
							// 当前程序跑完了
							if (pm.getProcess() == null || !pm.getProcess().isAlive()) {
								if (pm.getProcess() != null) {
									pm.getProcess().destroy();
								}
								// 任务跑完了
								if (pm.getCmdArray().length - 1 == pm.getCmdIndex()) {
									// logger.debug("remove : " + pm);
									processTaskList.remove(pm);
								} else {
									pm.setCmdIndex(pm.getCmdIndex() + 1);
									// System.out.println(pm + "|" +
									// pm.getCmdArray());
									String cmd = pm.getCmdArray()[pm.getCmdIndex()];
									Process process = ProcessHelper.run(cmd, pm.getTask().getLogPath(), pm.getTask().getErrorLogPath());
									pm.setProcess(process);
								}
							}
						}
						Thread.sleep(1000);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						try {
							Thread.sleep(100);
						} catch (Exception ee) {
						}
					}
				}
			}
		});
		processThread.start();

		Thread loggerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						logger.debug("processTaskList         : " + processTaskList);
						logger.debug("waitSpawnTaskJobIdQueue : " + waitSpawnTaskJobIdQueue);
						logger.debug("timeToJobIdListMap      : " + timeToJobIdListMap);
						Thread.sleep(10000);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		});
		if (logger.isDebugEnabled()) {
			loggerThread.start();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 获取配置路径
		Properties properties = (Properties) SpringContextUtil.getBean("schedulerProperties");
		task_log_dir = (String) properties.get("task_log_dir");
		// 设置默认值
		task_log_dir = task_log_dir == null ? "/tmp/dataservice/scheduler/" : task_log_dir;
		// 设置以/结尾
		task_log_dir = task_log_dir.endsWith(File.separator) ? task_log_dir : task_log_dir + File.separator;
		logger.info("初始化日志目录：" + task_log_dir);
		List<Job> jobList = JobService.findAll();
		for (Job job : jobList) {
			try {
				initJob(job);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("初始化JobList：" + jobList);
		SchedulerServer2 s = new SchedulerServer2();
		s.start();
		logger.info("启动scheduler程序成功。");
	}

}
