package org.blazer.scheduler.core;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.expression.CronException;
import org.blazer.scheduler.util.DateUtil;

public class JobManager {

	private static ConcurrentHashMap<Integer, Job> jobMap = new ConcurrentHashMap<Integer, Job>();

	private static ConcurrentHashMap<String, ConcurrentHashMap<Integer, Job>> timeMap = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, Job>>();

	

	public static void main(String[] args) {
		
	}

	public static void add(Job job) throws CronException {
		JobManager jm = new JobManager();
		jm.setJob(job);
		Date nextDate = CronParser.getNextDate(DateUtil.newDate(), job.getCron());
		jm.setNextDate(nextDate);
	}

	private Job job;

	private Date nextDate;

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Date getNextDate() {
		return nextDate;
	}

	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}

}
