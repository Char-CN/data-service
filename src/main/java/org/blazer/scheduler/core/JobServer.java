package org.blazer.scheduler.core;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.model.ProcessModel;
import org.blazer.scheduler.service.JobService;
import org.blazer.scheduler.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobServer extends Thread {

	private static final Logger logger = LoggerFactory.getLogger("scheduler");

	// 等待需要生成下一个Task的Job队列，线程安全
	// 等待需要解析下一个Cron表达式符合的时间的JobId队列，线程安全
	private static final ConcurrentLinkedQueue<Integer> waitSpawnTaskJobIdQueue = new ConcurrentLinkedQueue<Integer>();

	// JobId对应的Job，线程安全
	private static final ConcurrentHashMap<Integer, Job> jobIdToJobMap = new ConcurrentHashMap<Integer, Job>();

	private static final JobService jobService = (JobService) SpringContextUtil.getBean("jobService");

	public static Integer waitSpawnSize() {
		return waitSpawnTaskJobIdQueue.size();
	}

	public static ConcurrentLinkedQueue<Integer> waitSpawns() {
		return waitSpawnTaskJobIdQueue;
	}

	public static Integer jobSize() {
		return jobIdToJobMap.size();
	}

	public static void addQueue(Integer jobId) {
		waitSpawnTaskJobIdQueue.add(jobId);
	}

	public JobServer() throws Exception {
		List<Job> jobList = jobService.findAll();
		for (Job job : jobList) {
			try {
				initJob(job);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("初始化JobList：" + jobList);
	}

	public static void reloadJob(Job job) throws Exception {
		removeJob(job.getId());
		// 再init
		initJob(job);
	}

	public static void removeJob(Integer jobId) {
		TaskServer.removeTaskByJobId(jobId);
		jobIdToJobMap.remove(jobId);
	}

	public static void initJob(Job job) throws Exception {
		if (job == null || job.getId() == null) {
			throw new NullPointerException("job or job id is null.");
		}
		logger.info("init job in scheduler : " + job);
		jobIdToJobMap.put(job.getId(), job);
		waitSpawnTaskJobIdQueue.add(job.getId());
	}

	public static Job getJobById(Integer jobId) {
		return jobIdToJobMap.get(jobId);
	}

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
				ProcessModel pm = TaskServer.spawnAutoCronTaskProcess(jobIdToJobMap.get(jobId));
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

}
