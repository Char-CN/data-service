package org.blazer.scheduler.expression;

public class CronException extends Exception {

	private static final long serialVersionUID = -7764468604666255680L;

	public CronException() {
		super();
	}

	public CronException(String message) {
		super(message);
	}

	public CronException(String message, Throwable tb) {
		super(message, tb);
	}

	public CronException(Throwable tb) {
		super(tb);
	}

}
