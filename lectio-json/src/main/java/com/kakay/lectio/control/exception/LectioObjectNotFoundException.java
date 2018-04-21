package com.kakay.lectio.control.exception;

public class LectioObjectNotFoundException extends LectioException {

	
	String objectClassName;
	int objectId;

	public <T> LectioObjectNotFoundException(String message, Class<T> theClass, int objectId) {
		super(message);
		objectClassName = theClass.getName();
		this.objectId = objectId;
		
	}

	public String getObjectClassName() {
		return objectClassName;
	}

	public int getObjectId() {
		return objectId;
	}


}
