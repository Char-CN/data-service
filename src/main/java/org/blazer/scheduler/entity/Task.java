package org.blazer.scheduler.entity;

import java.util.Date;

public class Task {

	private Integer id;
	private Integer job_id;
	private Integer status_id;
	private String task_name;
	private Date execute_time;
	private Date end_time;
	private String command;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJob_id() {
		return job_id;
	}

	public void setJob_id(Integer job_id) {
		this.job_id = job_id;
	}

	public Integer getStatus_id() {
		return status_id;
	}

	public void setStatus_id(Integer status_id) {
		this.status_id = status_id;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public Date getExecute_time() {
		return execute_time;
	}

	public void setExecute_time(Date execute_time) {
		this.execute_time = execute_time;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
