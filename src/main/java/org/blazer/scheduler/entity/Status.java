package org.blazer.scheduler.entity;

public enum Status {

	WAIT(10, "等待执行"), RUN(20, "正在执行"), SUCCESS(30, "执行成功"), FAIL(40, "执行失败"), CANCEL(50, "执行取消");

	private Integer id;

	private String statusName;

	public Integer getId() {
		return id;
	}

	public String getStatusName() {
		return statusName;
	}

	private Status(Integer id, String statusName) {
		this.id = id;
		this.statusName = statusName;
	}

	public static void main(String[] args) {
		System.out.println(Status.RUN.getId());
	}

}
