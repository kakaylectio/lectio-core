package com.kktam.lectio.control.exception;

public class LectioSystemException extends RuntimeException {
	public LectioSystemException (String message) {
		super(message);
	}
	
	public LectioSystemException(String message, Throwable cause) {
		super(message, cause);
	}
}
