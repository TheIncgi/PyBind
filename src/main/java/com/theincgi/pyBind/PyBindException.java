package com.theincgi.pyBind;

public class PyBindException extends RuntimeException {

	public PyBindException() {
		super();
	}

	public PyBindException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PyBindException(String message, Throwable cause) {
		super(message, cause);
	}

	public PyBindException(String message) {
		super(message);
	}

	public PyBindException(Throwable cause) {
		super(cause);
	}
	
}
