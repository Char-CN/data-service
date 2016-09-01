package org.blazer.dataservice.exception;

public class NotAllowDeleteException extends Exception {

	private static final long serialVersionUID = -703510874930399278L;

	public NotAllowDeleteException() {
		super();
	}

	public NotAllowDeleteException(String message) {
		super(message);
	}

	public NotAllowDeleteException(String message, Throwable tb) {
		super(message, tb);
	}

	public NotAllowDeleteException(Throwable tb) {
		super(tb);
	}

}
