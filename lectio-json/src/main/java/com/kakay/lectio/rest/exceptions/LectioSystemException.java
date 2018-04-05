package com.kakay.lectio.rest.exceptions;

public class LectioSystemException extends RuntimeException {
	public LectioSystemException (String message) {
		super(message);
	}
	
	public LectioSystemException(String message, Throwable cause) {
		super(message, cause);
	}
}
