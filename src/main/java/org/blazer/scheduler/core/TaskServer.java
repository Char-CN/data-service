package org.blazer.scheduler.core;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.JobParam;
import org.blazer.scheduler.entity.Status;
import org.blazer.scheduler.entity.Task;
import org.blazer.scheduler.entity.TaskType;
import org.blazer.scheduler.expression.CmdException;
import org.blazer.scheduler.expression.CronException;
import org.blazer.scheduler.expression.TaskNotFoundException;
import org.blazer.scheduler.model.ProcessModel;
import org.blazer.scheduler.service.TaskService;
import org.blazer.scheduler.util.DateUtil;
import org.blazer.scheduler.util.SequenceUtil;
import org.blazer.scheduler.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskServer extends Thread {

	private static final Logger logger = LoggerFactory.getLogger("scheduler");

	private static final String TASK_NAME = "%s_%s_%s_%s";

	// 任务日志
	private static String task_log_path;

	// Task索引，线程安全
	private static final ConcurrentHashMap<String, ProcessModel> indexTaskNameToTask = new ConcurrentHashMap<String, ProcessModel>();

	// 当前时间[yyyy_MM_dd_HH_mm]为key所对应的需要执行的Task，线程安全
	private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<ProcessModel>> timeToProcessModelMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<ProcessModel>>();

	private static final TaskService taskService = (TaskService) SpringContextUtil.getBean("taskService");

	public static Integer taskSize() {
		return indexTaskNameToTask.size();
	}

	public static Collection<ProcessModel> tasks() {
		return indexTaskNameToTask.values();
	}

	public static ConcurrentLinkedQueue<ProcessModel> getTimeToProcess(String taskName) {
		return timeToProcessModelMap.get(taskName);
	}

	public static void removeTaskByJobId(Integer jobId) {
		for (ConcurrentLinkedQueue<ProcessModel> queue : timeToProcessModelMap.values()) {
			for (ProcessModel pm : queue) {
				if (pm.getJob().getId() == jobId) {
					pm.getTask().setStatusId(Status.CANCEL.getId());
					taskService.updateEndTimeNowAndStatus(pm.getTask());
					queue.remove(pm);
				}
			}
		}
	}

	/**
	 * 取消正在执行的任务
	 * 
	 * @param taskName
	 *            任务名称
	 * @throws TaskNotFoundException
	 */
	public static void cancelTaskByName(String taskName) {
		ProcessModel pm = indexTaskNameToTask.get(taskName);
		if (pm != null) {
			// 删除索引的 Task
			indexTaskNameToTask.remove(taskName);
			try {
				if (pm.getProcess() != null && pm.getProcess().isAlive()) {
					pm.getProcess().destroy();
				}
			} catch (Exception e) {
				logger.error("[Notice] cancel task error.");
				logger.error(e.getMessage(), e);
			}
		}
		// 删除数据库中的Task
		Task task = new Task();
		task.setStatusId(Status.CANCEL.getId());
		task.setTaskName(taskName);
		taskService.updateEndTimeNowAndStatus(task);
		logger.info("停止任务[" + taskName + "]成功。");
	}

	/**
	 * 根据Job生成一个立即执行的任务
	 * 
	 * @param job
	 * @return
	 * @throws Exception
	 */
	public static ProcessModel spawnRightNowTaskProcess(Job job) throws Exception {
		ProcessModel pm = spawnTaskProcess(job, TaskType.right_now);
		return pm;
	}

	/**
	 * 根据命令以及JobParam生成一个立即执行的任务
	 * 
	 * @param cmd
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static ProcessModel spawnRightNowTaskProcess(String cmd, List<JobParam> params) throws Exception {
		Job job = new Job();
		job.setId(0);
		job.setJobName("即时调度");
		job.setCommand(cmd);
		job.setParams(params);
		ProcessModel pm = spawnTaskProcess(job, TaskType.right_now);
		return pm;
	}

	public static ProcessModel spawnAutoCronTaskProcess(Job job) throws Exception {
		return spawnTaskProcess(job, TaskType.cron_auto);
	}

	/**
	 * 根据JobId和任务类型生成任务
	 * 
	 * @param jobId
	 * @param taskType
	 * @return
	 * @throws Exception
	 */
	private static ProcessModel spawnTaskProcess(Job job, TaskType taskType) throws Exception {
		if (taskType == TaskType.cron_auto && CronParserHelper.isNotValid(job.getCron())) {
			throw new CronException("cron [" + job.getCron() + "] expression is not valid." + job);
		}
		if (StringUtils.isBlank(job.getCommand())) {
			throw new CmdException("cmd [" + job.getCommand() + "] is not valid." + job);
		}
		String nextTime = null;
		if (taskType == TaskType.cron_auto) {
			nextTime = DateUtil.showDate(CronParserHelper.getNextDate(job.getCron()));
		} else {
			nextTime = DateUtil.getSeconds() >= 58 ? DateUtil.newDateStrNextMinute() : DateUtil.newDateStr();
		}
		ProcessModel pm = new ProcessModel();
		// task entity
		Task task = new Task();
		String typeName = taskType.toString();
		// taskName = nextTime + "_" + job.getId() + "_" + typeName + "_" +
		// SequenceUtil.getStrMin();
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
	 * 
	 * @param params
	 * @param task
	 * @return
	 */
	private static String[] addSystemParams(String[] params, Task task) {
		params = ArrayUtils.add(params, "SYS_TASK_NAME=" + task.getTaskName());
		return params;
	}

	@Override
	public void run() {
		/**
		 * TODO : 2.当前时间需要调度的守护线程
		 */
		Thread schedulerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String currentTime = DateUtil.newDateStr();
						ConcurrentLinkedQueue<ProcessModel> pmQueue = timeToProcessModelMap.get(currentTime);
						// 判断当前时间是否存在任务
						if (pmQueue == null) {
							Thread.sleep(1000);
							continue;
						} else if (pmQueue.isEmpty()) {
							timeToProcessModelMap.remove(currentTime);
							continue;
						}
						ProcessModel pm = null;
						try {
							// 获取任务信息
							pm = pmQueue.element();
							String cmd = pm.getCmdArray()[pm.getCmdIndex()];
							String logPath = pm.getTask().getLogPath();
							String errorLogPath = pm.getTask().getErrorLogPath();
							// 执行时候增加系统参数处理
							String[] params = pm.getJob().toArrayParams();
							params = addSystemParams(params, pm.getTask());
							pm.setCmdParams(params);
							// 存入task的参数信息
							pm.getTask().setParams(pm.getJob().toStrParams());
							pm.getTask().setStatusId(Status.RUN.getId());
							// 并且更新状态
							taskService.updateExecuteTimeNowAndParamsAndStatus(pm.getTask());
							// 增加索引
							indexTaskNameToTask.put(pm.getTask().getTaskName(), pm);
							if (TaskType.cron_auto.toString().equals(pm.getTask().getTypeName())) {
								JobServer.addQueue(pm.getJob().getId());
							}
							// 执行任务
							Process process = ProcessHelper.run(cmd, params, logPath, errorLogPath, false);
							pm.setProcess(process);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							if (pm == null) {
								logger.error("[Notice]队列中存在null值，请管理员检查。");
							} else if (pm.getTask() != null && pm.getTask().getTaskName() != null) {
								logger.error("[Notice]" + pm.getTask().getTaskName() + "执行错误，请管理员检查。");
								// 任务错误日志
								pm.getTask().setException(e.toString());
								pm.getTask().setStatusId(Status.FAIL.getId());
								// 更新状态
								taskService.updateEndTimeNowAndStatusAndException(pm.getTask());
							} else {
								logger.error("[Notice]TaskName存在null值，请管理员检查。");
							}
						} finally {
							try {
								pmQueue.remove();
								if (pmQueue.isEmpty()) {
									timeToProcessModelMap.remove(currentTime);
								}
								Thread.sleep(100);
							} catch (Exception ee) {
							}
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
						// 当前正在执行的任务不为空则扫描一次
						if (!indexTaskNameToTask.isEmpty()) {
							// 线程安全CopyOnWriteArrayList可以在循环内直接删除元素。因为在源码中remove方法的时候会调用ReentrantLock。
							// 20160105改成了map形式，注释上说支持删除元素
							for (ProcessModel pm : indexTaskNameToTask.values()) {
								// process对象对null，此情况判定为任务执行失败
								if (pm.getProcess() == null) {
									indexTaskNameToTask.remove(pm.getTask().getTaskName());
									pm.getTask().setStatusId(Status.FAIL.getId());
									taskService.updateEndTimeNowAndStatus(pm.getTask());
									continue;
								}
								// 当前任务跑完了
								if (!pm.getProcess().isAlive()) {
									// process返回值不等于0，此情况判定为任务执行失败
									if (pm.getProcess().exitValue() != 0) {
										indexTaskNameToTask.remove(pm.getTask().getTaskName());
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
										indexTaskNameToTask.remove(pm.getTask().getTaskName());
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
								}
								// 当前任务未跑完
								else {
									// waitFor
									// do nothing
								}
							}
						}
						// 检查完休眠
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
	}

	public TaskServer() {
		// 初始化配置参数
		Properties properties = (Properties) SpringContextUtil.getBean("schedulerProperties");
		task_log_path = (String) properties.get("task_log_path");
		// 设置默认值
		task_log_path = task_log_path == null ? "/tmp/dataservice/scheduler/" : task_log_path;
		// 设置以/结尾
		task_log_path = task_log_path.endsWith(File.separator) ? task_log_path : task_log_path + File.separator;
		logger.info("初始化日志目录：" + task_log_path);

		// 检查数据库，如果有[正在执行]的任务，则cancel
		try {
			List<Task> list = taskService.findTasksByStatus(Status.RUN);
			for (Task task : list) {
				cancelTaskByName(task.getTaskName());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
