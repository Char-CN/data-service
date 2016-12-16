package org.blazer.scheduler.model;

import java.util.Arrays;

import org.blazer.scheduler.entity.Job;
import org.blazer.scheduler.entity.Task;

public class ProcessModel {

	// 执行时间
	private String nextTime;
	// 执行的命令索引
	private Integer cmdIndex;
	// 命令集合，以；分隔，如果一条命令有多个；，则会逐步执行。
	private String[] cmdArray;
	// 当前执行的Process
	private Process process;
	// Job引用
	private Job job;
	// Task引用
	private Task task;

	public String getNextTime() {
		return nextTime;
	}

	public void setNextTime(String nextTime) {
		this.nextTime = nextTime;
	}

	public Integer getCmdIndex() {
		return cmdIndex;
	}

	public void setCmdIndex(Integer cmdIndex) {
		this.cmdIndex = cmdIndex;
	}

	public String[] getCmdArray() {
		return cmdArray;
	}

	public void setCmdArray(String[] cmdArray) {
		this.cmdArray = cmdArray;
	}

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

	@Override
	public String toString() {
		return "ProcessModel [nextTime=" + nextTime + ", cmdIndex=" + cmdIndex + ", cmdArray=" + Arrays.toString(cmdArray) + ", process=" + process + ", job="
				+ job + ", task=" + task + "]";
	}

}
