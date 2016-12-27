package org.blazer.scheduler.core;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.blazer.dataservice.util.TimeUtil;
import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.JobParam;
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

	private static final String TASK_NAME = "%s_%s_%s_%s";

	// 任务日志
	private static String task_log_path;

	public Job getJobById(Integer jobId) {
		return jobIdToJobMap.get(jobId);
	}

	public void reloadJob(Job job) throws Exception {
		// 先清除time中已经生成的process task
//		for (String time : timeToProcessModelMap.keySet()) {
//			for (ProcessModel pm : timeToProcessModelMap.get(time)) {
//				if (pm.getJob().getId() == job.getId()) {
//					pm.getTask().setStatusId(Status.CANCEL.getId());
//					taskService.updateEndTimeNowAndStatus(pm.getTask());
//					timeToProcessModelMap.get(time).remove(pm);
//				}
//			}
//		}
		removeJob(job.getId());
		// 再init
		initJob(job);
	}

	public void initJob(Job job) throws Exception {
		if (job == null || job.getId() == null) {
			throw new NullPointerException("job or job id is null.");
		}
//		if (CronParserHelper.isNotValid(job.getCron())) {
//			throw new CronException("cron [" + job.getCron() + "] expression is not valid.");
//		}
//		if (StringUtils.isBlank(job.getCommand())) {
//			throw new CmdException("cmd [" + job.getCommand() + "] is not valid.");
//		}
		logger.info("init job in scheduler : " + job);
		jobIdToJobMap.put(job.getId(), job);
		waitSpawnTaskJobIdQueue.add(job.getId());
	}

	public void removeJob(Job job) {
		removeJob(job.getId());
	}

	public void removeJob(Integer jonId) {
		// 先清除time中已经生成的process task
		for (String time : timeToProcessModelMap.keySet()) {
			for (ProcessModel pm : timeToProcessModelMap.get(time)) {
				if (pm.getJob().getId() == jonId) {
					pm.getTask().setStatusId(Status.CANCEL.getId());
					taskService.updateEndTimeNowAndStatus(pm.getTask());
					timeToProcessModelMap.get(time).remove(pm);
				}
			}
		}
		jobIdToJobMap.remove(jonId);
	}

	public void cancelTaskByName(String taskName) {
		for (ProcessModel pm : processTaskList) {
			if (pm.getTask().getTaskName().equals(taskName) && pm.getProcess() != null && pm.getProcess().isAlive()) {
				processTaskList.remove(pm);
				try {
					pm.getProcess().destroy();
				} catch (Exception e) {
				}
				pm.getTask().setStatusId(Status.CANCEL.getId());
				taskService.updateEndTimeNowAndStatus(pm.getTask());
			}
		}
	}

	/**
	 * 根据JobId生成一个立即执行的任务
	 * 
	 * @param jobId
	 * @return
	 * @throws Exception
	 */
	public ProcessModel spawnRightNowTaskProcess(Job job) throws Exception {
		ProcessModel pm = spawnTaskProcess(job, TaskType.right_now);
		return pm;
	}

	/**
	 * 根据命令以及JobParam生成一个立即执行的任务
	 * @param cmd
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public ProcessModel spawnRightNowTaskProcess(String cmd, List<JobParam> params) throws Exception {
		Job job = new Job();
		job.setId(0);
		job.setJobName("即时调度");
		job.setCommand(cmd);
		job.setParams(params);
		ProcessModel pm = spawnTaskProcess(job, TaskType.right_now);
		return pm;
	}

	/**
	 * 根据JobId和任务类型生成任务
	 * 
	 * @param jobId
	 * @param taskType
	 * @return
	 * @throws Exception
	 */
	private ProcessModel spawnTaskProcess(Job job, TaskType taskType) throws Exception {
		if (taskType == TaskType.cron_auto && CronParserHelper.isNotValid(job.getCron())) {
			throw new CronException("cron [" + job.getCron() + "] expression is not valid.");
		}
		if (StringUtils.isBlank(job.getCommand())) {
			throw new CmdException("cmd [" + job.getCommand() + "] is not valid.");
		}
		String nextTime = null;
		if (taskType == TaskType.cron_auto) {
			nextTime = DateUtil.showDate(CronParserHelper.getNextDate(job.getCron()));
		} else {
			nextTime = DateUtil.getSeconds() > 50 ? DateUtil.newDateStrNextMinute() : DateUtil.newDateStr();
		}
		ProcessModel pm = new ProcessModel();
		// task entity
		Task task = new Task();
		String typeName = taskType.toString();
		// taskName = nextTime + "_" + job.getId() + "_" + typeName + "_" + SequenceUtil.getStrMin();
		String taskName = String.format(TASK_NAME, nextTime, job.getId(), typeName, TaskType.cron_auto == taskType ? SequenceUtil.getStrMin() : SequenceUtil.getStr0());
		task.setJobId(job.getId());
		task.setTypeName(typeName);
		task.setTaskName(taskName);
		task.setStatusId(Status.WAIT.getId());
		task.setLogPath(task_log_path);
		task.setErrorLogPath(task_log_path);
		task.setCommand(job.getCommand());
		task = taskService.addOrGet(task);
		// process model
		pm.setJob(job);
		pm.setTask(task);
		pm.setCmdIndex(0);
		// split commands by ;
		pm.setCmdArray(StringUtils.split(job.getCommand(), ";"));
		pm.setNextTime(nextTime);
		// 不存在该时间点的。new一个出来。
		if (!timeToProcessModelMap.containsKey(pm.getNextTime())) {
			timeToProcessModelMap.put(pm.getNextTime(), new ConcurrentLinkedQueue<ProcessModel>());
		}
		timeToProcessModelMap.get(pm.getNextTime()).add(pm);
		return pm;
	}

	/**
	 * 系统参数增加
	 * @param params
	 * @param task
	 * @return
	 */
	private String[] addSystemParams(String[] params, Task task) {
		params = ArrayUtils.add(params, "SYS_TASK_NAME=" + task.getTaskName());
		return params;
	}

	@Override
	public void run() {
		/**
		 * TODO : 1.等待根据JobId生成Task的守护线程
		 */
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
						ProcessModel pm = spawnTaskProcess(jobIdToJobMap.get(jobId), TaskType.cron_auto);
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

		/**
		 * TODO : 2.当前时间需要调度的守护线程
		 */
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
							// 执行任务
							ProcessModel pm = pmQueue.element();
							String cmd = pm.getCmdArray()[pm.getCmdIndex()];
							String logPath = pm.getTask().getLogPath();
							String errorLogPath = pm.getTask().getErrorLogPath();
							// 执行时候增加系统参数处理
							String[] params = pm.getJob().toArrayParams();
							params = addSystemParams(params, pm.getTask());
							pm.setCmdParams(params);
							Process process = ProcessHelper.run(cmd, params, logPath, errorLogPath, false);
							pm.setProcess(process);
							if (TaskType.cron_auto.toString().equals(pm.getTask().getTypeName())) {
								waitSpawnTaskJobIdQueue.add(pm.getJob().getId());
							}
							pm.getTask().setStatusId(Status.RUN.getId());
							taskService.updateExecuteTimeNowAndStatus(pm.getTask());
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

		/**
		 * TODO : 3.每一个Job的Task实例需要逐步执行命令的守护线程。
		 */
		Thread processTaskThread = new Thread(new Runnable() {
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
							// process对象对null，此情况判定为任务执行失败
							if (pm.getProcess() == null) {
								processTaskList.remove(pm);
								pm.getTask().setStatusId(Status.FAIL.getId());
								taskService.updateEndTimeNowAndStatus(pm.getTask());
								continue;
							}
							// 当前任务跑完了
							if (!pm.getProcess().isAlive()) {
								// process返回值不等于0，此情况判定为任务执行失败
								if (pm.getProcess().exitValue() != 0) {
									processTaskList.remove(pm);
									pm.getTask().setStatusId(Status.FAIL.getId());
									taskService.updateEndTimeNowAndStatus(pm.getTask());
									continue;
								}
								// 手动杀死任务
								try {
									pm.getProcess().destroy();
								} catch (Exception e) {
								}
								// 任务组跑完了
								if (pm.getCmdArray().length - 1 == pm.getCmdIndex()) {
									processTaskList.remove(pm);
									pm.getTask().setStatusId(Status.SUCCESS.getId());
									taskService.updateEndTimeNowAndStatus(pm.getTask());
								}
								// 任务组没有跑完，执行下一个任务
								else {
									pm.setCmdIndex(pm.getCmdIndex() + 1);
									String cmd = pm.getCmdArray()[pm.getCmdIndex()];
									String logPath = pm.getTask().getLogPath();
									String errorLogPath = pm.getTask().getErrorLogPath();
									String[] params = pm.getCmdParams();
									Process process = ProcessHelper.run(cmd, params, logPath, errorLogPath, false);
									pm.setProcess(process);
								}
							} else {
								// waitFor...
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
		processTaskThread.start();

		/**
		 * TODO : 4.打印日志的守护线程
		 */
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
		TimeUtil tu = TimeUtil.createAndPoint();
		tu.setLogger(logger);
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
		Job systemJob = new Job();
		systemJob.setId(0);
		logger.info("初始化JobList：" + jobList);
		SchedulerServer s = new SchedulerServer();
		s.start();
		tu.printMs("启动scheduler服务成功。");
	}

}
