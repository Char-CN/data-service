package org.blazer.scheduler.core;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.expression.CronException;
import org.blazer.scheduler.model.ProcessModel;
import org.blazer.scheduler.service.JobService;
import org.blazer.scheduler.util.CronParserUtil;
import org.blazer.scheduler.util.DateUtil;
import org.blazer.scheduler.util.ProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service(value = "schedulerServer")
public class SchedulerServer extends Thread implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(SchedulerServer.class);

	private static ConcurrentLinkedQueue<ProcessModel> processQueue = new ConcurrentLinkedQueue<ProcessModel>();

	private static ConcurrentLinkedQueue<Integer> waitParseCronJobIdQueue = new ConcurrentLinkedQueue<Integer>();

	private static ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> timeToJobIdListMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>>();

	private static ConcurrentHashMap<Integer, Job> jobIdToJobMap = new ConcurrentHashMap<Integer, Job>();

	@Autowired
	JobService JobService;

	@Value("${schedulerProperties.task_log_dir:/tmp/dataservice/scheduler}")
	private String task_log_dir;

//	private static String task_log_dir;

	public static void initJob(Job job) throws CronException {
		jobIdToJobMap.put(job.getId(), job);
		if (job == null || CronParserUtil.isNotValid(job.getCron())) {
			logger.error("error job : " + job);
			return;
		}
		addTime2JobId(job);
	}

	public static void addTime2JobId(String time, Integer jobId) {
		if (!timeToJobIdListMap.containsKey(time)) {
			timeToJobIdListMap.put(time, new ConcurrentLinkedQueue<Integer>());
		}
		timeToJobIdListMap.get(time).add(jobId);
	}

	public static void addTime2JobId(Job job) throws CronException {
		Date nextDate = CronParserUtil.getNextDate(job.getCron());
		String currentTime = DateUtil.showDate(nextDate);
		addTime2JobId(currentTime, job.getId());
	}

	@Override
	public void run() {
		Thread cronParseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						if (waitParseCronJobIdQueue.isEmpty()) {
							Thread.sleep(1000);
							continue;
						}
						Integer jobId = waitParseCronJobIdQueue.element();
						Job job = jobIdToJobMap.get(jobId);
						addTime2JobId(job);
						waitParseCronJobIdQueue.remove();
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
		cronParseThread.start();
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
							String logPath = task_log_dir + currentTime + "_" + job.getJobName() + ".log";
							String errorLogPath = task_log_dir + currentTime + "_" + job.getJobName() + ".error";
							Process process = ProcessUtil.run(job.getCommand(), logPath, errorLogPath);
							ProcessModel pm = new ProcessModel();
							pm.setProcess(process);
							pm.setJob(job);
							processQueue.add(pm);
							waitParseCronJobIdQueue.add(jobId);
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
						if (processQueue.isEmpty()) {
							Thread.sleep(10000);
							continue;
						}
						ProcessModel pm = processQueue.element();
						// 程序跑完了
						if (!pm.getProcess().isAlive()) {
							processQueue.remove();
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
						logger.debug("processQueue       : " + processQueue);
						logger.debug("waitParseCronQueue : " + waitParseCronJobIdQueue);
						logger.debug("timeToJobIdListMap : " + timeToJobIdListMap);
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
//		task_log_dir = _task_log_dir.endsWith(File.separator) ? _task_log_dir : _task_log_dir + File.separator;
		logger.info("初始化日志目录：" + task_log_dir);
		List<Job> jobList = JobService.findAll();
		for (Job job : jobList) {
			initJob(job);
		}
		logger.info("初始化JobList：" + jobList);
		SchedulerServer s = new SchedulerServer();
		s.start();
		logger.info("启动scheduler程序成功。");
	}

}
