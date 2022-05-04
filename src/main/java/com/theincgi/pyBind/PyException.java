package com.theincgi.pyBind;

public class PyException extends PyBindException {

	public PyException() {
	}

	public PyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PyException(String message, Throwable cause) {
		super(message, cause);
	}

	public PyException(String message) {
		super(message);
	}

	public PyException(Throwable cause) {
		super(cause);
	}

}
