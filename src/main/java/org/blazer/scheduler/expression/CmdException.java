package org.blazer.scheduler.expression;

public class CmdException extends Exception {

	private static final long serialVersionUID = 8910366154163930773L;

	public CmdException() {
		super();
	}

	public CmdException(String message) {
		super(message);
	}

	public CmdException(String message, Throwable tb) {
		super(message, tb);
	}

	public CmdException(Throwable tb) {
		super(tb);
	}

}
