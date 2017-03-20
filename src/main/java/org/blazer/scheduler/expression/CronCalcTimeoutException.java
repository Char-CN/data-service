package org.blazer.scheduler.expression;

public class CronCalcTimeoutException extends Exception {

	private static final long serialVersionUID = 5394292297721061401L;

	public CronCalcTimeoutException() {
		super();
	}

	public CronCalcTimeoutException(String message) {
		super(message);
	}

	public CronCalcTimeoutException(String message, Throwable tb) {
		super(message, tb);
	}

	public CronCalcTimeoutException(Throwable tb) {
		super(tb);
	}

}
