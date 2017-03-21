package org.blazer.scheduler.entity;

public enum TaskType {

	cron_auto("cron_auto", "自动调度"), right_now("right_now", "即时任务");

	private String name;

	private String CNName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCNName() {
		return CNName;
	}

	public void setCNName(String cNName) {
		CNName = cNName;
	}

	private TaskType(String name, String cNName) {
		this.name = name;
		CNName = cNName;
	}

	public static void main(String[] args) {
		System.out.println(TaskType.right_now);
	}

}
