package org.blazer.scheduler.model;

import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.Task;

public class ProcessModel {

	private Process process;
	private Job job;
	private Task task;

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
