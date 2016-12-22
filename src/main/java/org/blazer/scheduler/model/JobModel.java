package org.blazer.scheduler.model;

import java.util.List;

import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.JobParam;

public class JobModel {

	private Job job;

	private List<JobParam> jobParams;

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public List<JobParam> getJobParams() {
		return jobParams;
	}

	public void setJobParams(List<JobParam> jobParams) {
		this.jobParams = jobParams;
	}

	@Override
	public String toString() {
		return "JobModel [job=" + job + ", jobParams=" + jobParams + "]";
	}

}
