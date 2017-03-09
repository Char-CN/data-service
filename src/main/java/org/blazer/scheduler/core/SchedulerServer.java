package org.blazer.scheduler.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.blazer.dataservice.util.TimeUtil;
import org.blazer.scheduler.model.ProcessModel;
import org.blazer.scheduler.util.DateUtil;
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

	@Override
	public void run() {
		while (true) {
			try {
				logger.debug("======================================================================");
				logger.debug("indexTaskNameToTask        size : " + TaskServer.taskSize());
				for (ProcessModel pm : TaskServer.tasks()) {
					logger.debug("                | process model : " + pm);
				}
				logger.debug("waitSpawnTaskJobIdQueue    size : " + JobServer.waitSpawnSize());
				for (Integer jobId : JobServer.waitSpawns()) {
					logger.debug("                |     job model : " + JobServer.getJobById(jobId));
				}
				String nextMinuteTime = DateUtil.newDateStrNextMinute();
				ConcurrentLinkedQueue<ProcessModel> nextMinuteTimeProcessQueue = TaskServer.getTimeToProcess(nextMinuteTime);
				logger.debug("nextMinuteTimeProcessQueue size : " + (nextMinuteTimeProcessQueue == null ? 0 : nextMinuteTimeProcessQueue.size()));
				if (nextMinuteTimeProcessQueue != null) {
					for (ProcessModel pm : nextMinuteTimeProcessQueue) {
						logger.debug("                | process model : " + pm);
					}
				}
				logger.debug("jobIdToJobMap              size : " + JobServer.jobSize());
				Thread.sleep(10000);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 获取配置路径
		TimeUtil tu = TimeUtil.createAndPoint();
		tu.setLogger(logger);

		/**
		 * 先启动task server，因为在初始化task server的时候会清除当前时间之前的所有正在执行的任务。
		 * 再启动job  server，此时会把当前时间之后的任务初始化。
		 */

		////////////////////// 启动 task server //////////////////////
		try {
			TaskServer taskServer = new TaskServer();
			taskServer.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		tu.printMs("启动Task服务成功。");

		////////////////////// 启动 job server //////////////////////
		try {
			JobServer jobServer = new JobServer();
			jobServer.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		tu.printMs("启动Job服务成功。");

		////////////////////// 启动 scheduler server //////////////////////
		try {
			SchedulerServer schedulerServer = new SchedulerServer();
			schedulerServer.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		tu.printMs("启动Scheduler服务成功。");
	}

}
