package org.blazer.scheduler.expression;

public class DuplicateJobException extends Exception {

	private static final long serialVersionUID = -4950184635625245157L;

	public DuplicateJobException() {
		super();
	}

	public DuplicateJobException(String message) {
		super(message);
	}

	public DuplicateJobException(String message, Throwable tb) {
		super(message, tb);
	}

	public DuplicateJobException(Throwable tb) {
		super(tb);
	}

}
