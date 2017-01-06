package org.blazer.scheduler.model;

import org.blazer.scheduler.entity.Task;

public class TaskLog {

	private LogModel logModel;

	private Task task;

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public LogModel getLogModel() {
		return logModel;
	}

	public void setLogModel(LogModel logModel) {
		this.logModel = logModel;
	}

}
