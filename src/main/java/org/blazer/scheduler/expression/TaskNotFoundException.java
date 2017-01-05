package org.blazer.scheduler.expression;

public class TaskNotFoundException extends Exception {

	private static final long serialVersionUID = -1626502341616024722L;

	public TaskNotFoundException() {
		super();
	}

	public TaskNotFoundException(String message) {
		super(message);
	}

	public TaskNotFoundException(String message, Throwable tb) {
		super(message, tb);
	}

	public TaskNotFoundException(Throwable tb) {
		super(tb);
	}

}
