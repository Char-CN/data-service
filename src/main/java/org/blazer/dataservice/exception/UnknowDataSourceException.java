package org.blazer.dataservice.exception;

public class UnknowDataSourceException extends Exception {

	private static final long serialVersionUID = -4463165501700335673L;

	public UnknowDataSourceException() {
		super();
	}

	public UnknowDataSourceException(String message) {
		super(message);
	}

	public UnknowDataSourceException(String message, Throwable tb) {
		super(message, tb);
	}

	public UnknowDataSourceException(Throwable tb) {
		super(tb);
	}

}
