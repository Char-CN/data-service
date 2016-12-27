package org.blazer.dataservice.exception;

public class NoPermissionsException extends Exception {

	private static final long serialVersionUID = -4308152748361813269L;

	public NoPermissionsException() {
		super();
	}

	public NoPermissionsException(String message) {
		super(message);
	}

	public NoPermissionsException(String message, Throwable tb) {
		super(message, tb);
	}

	public NoPermissionsException(Throwable tb) {
		super(tb);
	}

}
