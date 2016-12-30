package org.blazer.scheduler.entity;

public enum Status {

	UNKNOWN(0, "未知状态"), WAIT(10, "等待执行"), RUN(20, "正在执行"), SUCCESS(30, "执行成功"), FAIL(40, "执行失败"), CANCEL(50, "执行取消");

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

	public static Status get(Integer statusId) {
		if (statusId == Status.WAIT.id)
			return Status.WAIT;
		if (statusId == Status.RUN.id)
			return Status.RUN;
		if (statusId == Status.SUCCESS.id)
			return Status.SUCCESS;
		if (statusId == Status.FAIL.id)
			return Status.FAIL;
		if (statusId == Status.CANCEL.id)
			return Status.CANCEL;
		return Status.UNKNOWN;
	}

	public static void main(String[] args) {
		System.out.println(WAIT.id);
		System.out.println(Status.RUN.getId());
		System.out.println(get(50));
	}

}
