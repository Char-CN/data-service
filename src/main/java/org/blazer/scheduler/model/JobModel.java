package org.blazer.scheduler.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.blazer.scheduler.entity.Job;

public class JobModel {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");

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

	public String getNextDateStr() {
		return sdf.format(nextDate);
	}

	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}

}
