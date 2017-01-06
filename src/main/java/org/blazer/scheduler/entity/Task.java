package org.blazer.scheduler.entity;

import java.util.Date;

public class Task {

	private Integer id;
	private Integer jobId;
	private Status status;
	private Integer statusId;
	private String typeName;
	private String taskName;
	private Date executeTime;
	private Date endTime;
	private String command;
	private String params;
	private String logPath;
	private String errorLogPath;
	private String exception;
	private String remark;

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getErrorLogPath() {
		return errorLogPath;
	}

	public void setErrorLogPath(String errorLogPath) {
		this.errorLogPath = errorLogPath;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", jobId=" + jobId + ", status=" + status + ", statusId=" + statusId + ", typeName=" + typeName + ", taskName=" + taskName
				+ ", executeTime=" + executeTime + ", endTime=" + endTime + ", command=" + command + ", params=" + params + ", logPath=" + logPath + ", errorLogPath="
				+ errorLogPath + "]";
	}


}
