package com.theincgi.pyBind;

public class PyTypeMismatchException extends PyBindException {

	public PyTypeMismatchException() {
		super();
	}

	public PyTypeMismatchException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PyTypeMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public PyTypeMismatchException(String message) {
		super(message);
	}

	public PyTypeMismatchException(Throwable cause) {
		super(cause);
	}
	
}
