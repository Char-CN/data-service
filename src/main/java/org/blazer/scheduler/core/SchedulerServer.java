package org.blazer.scheduler.core;

import java.io.File;
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
import org.blazer.scheduler.util.SequenceUtil;
import org.blazer.scheduler.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 调度原理：
 * 
 * 一.基础类
 * 
 * 1.[ProcessModel] 阅读该类。
 * 
 * 二.基础变量
 * 
 * 1.[jobIdToJobMap]
 * 该Map以JobId为唯一标识，缓存JobId与Job的对应关系，对应的initJob、removeJob方法将维护该Map。
 * 
 * 2.[timeToProcessModelMap]
 * 该Map以时间[yyyy_MM_dd_HH_mm]为key，需要执行Process任务，对应的守护线程[schedulerThread]
 * 会轮询执行符合当前时间的任务。
 * 
 * 3.[waitSpawnTaskJobIdQueue]
 * 该Queue存入需要下一次执行任务的JobId，对应的守护线程[waitSpawnTaskThread]会去轮询生成任务。
 * 
 * 4.[processTaskList] 该集合维护一组正在执行的程序。对应的守护线程[processThread]会去检查是否需要执行下一跳命令
 * 
 * @author hyy
 *
 */
@Component(value = "schedulerServer")
public class SchedulerServer extends Thread implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger("scheduler");

	// Task集合，线程安全
	private static final CopyOnWriteArrayList<ProcessModel> processTaskList = new CopyOnWriteArrayList<ProcessModel>();

	// 等待需要生成下一个Task的Job队列，线程安全
	// 等待需要解析下一个Cron表达式符合的时间的JobId队列，线程安全
	private static final ConcurrentLinkedQueue<Integer> waitSpawnTaskJobIdQueue = new ConcurrentLinkedQueue<Integer>();

	// 当前时间[yyyy_MM_dd_HH_mm]为key所对应的需要执行的Task，线程安全
	private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<ProcessModel>> timeToProcessModelMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<ProcessModel>>();

	// JobId对应的Job，线程安全
	private static final ConcurrentHashMap<Integer, Job> jobIdToJobMap = new ConcurrentHashMap<Integer, Job>();

	private static final TaskService taskService = (TaskService) SpringContextUtil.getBean("taskService");

	private static final JobService jobService = (JobService) SpringContextUtil.getBean("jobService");

	// 任务日志
	private static String task_log_path;

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
		logger.info("init job in scheduler : " + job);
		jobIdToJobMap.put(job.getId(), job);
		waitSpawnTaskJobIdQueue.add(job.getId());
		// addTime2JobId(job);
	}

	public void removeJob(Integer jonId) {
	}

	public void cancelTaskByName(String taskName) {
		for (ProcessModel pm : processTaskList) {
			if (pm.getTask().getTaskName().equals(taskName) && pm.getProcess() != null && pm.getProcess().isAlive()) {

			}
		}
	}

	public ProcessModel spawnRightNowTaskProcess(Integer jobId) throws Exception {
		ProcessModel pm = spawnTaskProcess(jobId, TaskType.right_now);
		return pm;
	}

	private ProcessModel spawnTaskProcess(Integer jobId, TaskType taskType) throws Exception {
		Job job = jobIdToJobMap.get(jobId);
		if (taskType == TaskType.cron_auto && CronParserHelper.isNotValid(job.getCron())) {
			throw new CronException("cron [" + job.getCron() + "] expression is not valid.");
		}
		String nextTime = null;
		if (taskType == TaskType.cron_auto) {
			nextTime = DateUtil.showDate(CronParserHelper.getNextDate(job.getCron()));
		} else {
			nextTime = DateUtil.getSeconds() > 50 ? DateUtil.newDateStrNextMinute() : DateUtil.newDateStr();
		}
		if (StringUtils.isBlank(job.getCommand())) {
			throw new CmdException("cmd [" + job.getCommand() + "] is not valid.");
		}
		String currentTime = DateUtil.newDateStr();
		ProcessModel pm = new ProcessModel();
		// task entity
		Task task = new Task();
		String typeName = taskType.toString();
		String taskName = null;
		if (TaskType.cron_auto == taskType) {
			taskName = currentTime + "_" + job.getId() + "_" + typeName + "_00001";
		} else {
			taskName = currentTime + "_" + job.getId() + "_" + typeName + "_" + SequenceUtil.getStr0();
		}
		task.setJobId(job.getId());
		task.setTypeName(typeName);
		task.setTaskName(taskName);
		task.setStatusId(Status.WAIT.getId());
		task.setLogPath(task_log_path);
		task.setErrorLogPath(task_log_path);
		task = taskService.add(task);
		// process model
		pm.setJob(job);
		pm.setTask(task);
		pm.setCmdIndex(0);
		pm.setCmdArray(cmdArray(job));
		pm.setNextTime(nextTime);
		if (!timeToProcessModelMap.containsKey(pm.getNextTime())) {
			timeToProcessModelMap.put(pm.getNextTime(), new ConcurrentLinkedQueue<ProcessModel>());
		}
		timeToProcessModelMap.get(pm.getNextTime()).add(pm);
		return pm;
	}

	public static String[] cmdArray(Job job) {
		return StringUtils.split(job.getCommand(), ";");
	}

	@Override
	public void run() {
		// TODO : 1.等待根据JobId生成Task的守护线程
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
						Integer jobId = waitSpawnTaskJobIdQueue.element();
						ProcessModel pm = spawnTaskProcess(jobId, TaskType.cron_auto);
						logger.debug("spawn cron auto task process : " + pm);
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
		// TODO : 2.当前时间需要调度的守护线程
		Thread schedulerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String currentTime = DateUtil.newDateStr();
						if (!timeToProcessModelMap.containsKey(currentTime)) {
							Thread.sleep(1000);
							continue;
						}
						ConcurrentLinkedQueue<ProcessModel> pmQueue = timeToProcessModelMap.get(currentTime);
						if (pmQueue.isEmpty()) {
							timeToProcessModelMap.remove(currentTime);
						} else {
							ProcessModel pm = pmQueue.element();
							Job job = pm.getJob();
							Process process = ProcessHelper.run(pm.getCmdArray()[pm.getCmdIndex()], pm.getTask().getLogPath(), pm.getTask().getErrorLogPath(),
									false);
							pm.setProcess(process);
							if (TaskType.cron_auto.toString().equals(pm.getTask().getTypeName())) {
								waitSpawnTaskJobIdQueue.add(job.getId());
							}
							processTaskList.add(pm);
							pmQueue.remove();
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
		// TODO : 3.每一个Job的Task实例需要逐步执行命令的守护线程。
		Thread processThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						if (processTaskList.isEmpty()) {
							Thread.sleep(1000);
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
		// TODO : 4.打印日志的守护线程
		Thread loggerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						logger.debug("======================================================================");
						logger.debug("processTaskList            size : " + processTaskList.size());
						for (ProcessModel pm : processTaskList) {
							logger.debug("                | process model : " + pm);
						}
						logger.debug("waitSpawnTaskJobIdQueue    size : " + waitSpawnTaskJobIdQueue.size());
						for (Integer jobId : waitSpawnTaskJobIdQueue) {
							logger.debug("                |     job model : " + jobIdToJobMap.get(jobId));
						}
						String nextMinuteTime = DateUtil.newDateStrNextMinute();
						ConcurrentLinkedQueue<ProcessModel> nextMinuteTimeProcessQueue = timeToProcessModelMap.get(nextMinuteTime);
						logger.debug("nextMinuteTimeProcessQueue size : " + (nextMinuteTimeProcessQueue == null ? 0 : nextMinuteTimeProcessQueue.size()));
						if (nextMinuteTimeProcessQueue != null) {
							for (ProcessModel pm : nextMinuteTimeProcessQueue) {
								logger.debug("                | process model : " + pm);
							}
						}
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
		task_log_path = (String) properties.get("task_log_path");
		// 设置默认值
		task_log_path = task_log_path == null ? "/tmp/dataservice/scheduler/" : task_log_path;
		// 设置以/结尾
		task_log_path = task_log_path.endsWith(File.separator) ? task_log_path : task_log_path + File.separator;
		logger.info("初始化日志目录：" + task_log_path);
		List<Job> jobList = jobService.findAll();
		for (Job job : jobList) {
			try {
				initJob(job);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("初始化JobList：" + jobList);
		SchedulerServer s = new SchedulerServer();
		s.start();
		logger.info("启动scheduler服务成功。");
	}

}
