package org.blazer.dataservice.exception;

public class FileHandleException extends Exception {

	private static final long serialVersionUID = 5982934688904972291L;

	public FileHandleException() {
		super();
	}

	public FileHandleException(String message) {
		super(message);
	}

	public FileHandleException(String message, Throwable tb) {
		super(message, tb);
	}

	public FileHandleException(Throwable tb) {
		super(tb);
	}

}
