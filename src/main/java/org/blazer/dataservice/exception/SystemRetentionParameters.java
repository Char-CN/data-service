package org.blazer.dataservice.exception;

public class SystemRetentionParameters extends Exception {

	private static final long serialVersionUID = 6439709219305003856L;

	public SystemRetentionParameters() {
		super();
	}

	public SystemRetentionParameters(String message) {
		super(message);
	}

	public SystemRetentionParameters(String message, Throwable tb) {
		super(message, tb);
	}

	public SystemRetentionParameters(Throwable tb) {
		super(tb);
	}

}
