package com.kakay.lectio.rest.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.kakay.lectio.control.exception.LectioAuthorizationException;
import com.kakay.lectio.control.exception.LectioConstraintException;
import com.kakay.lectio.control.exception.LectioNotImplementedException;
import com.kakay.lectio.control.exception.LectioObjectNotFoundException;

public class LectioExceptionMappers  {

	public static class LectioNotImplementedExceptionMapper implements ExceptionMapper<LectioNotImplementedException> {

		@Override
		public Response toResponse(LectioNotImplementedException exception) {
			// TODO Auto-generated method stub
			return Response.status(Response.Status.NOT_IMPLEMENTED)
					.type(MediaType.APPLICATION_JSON)
					.entity(exception.getMessage())
					.build();
		}
	}
	
	public static class LectioObjectNotFoundExceptionMapper implements ExceptionMapper<LectioObjectNotFoundException> {
		@Override
		public Response toResponse(LectioObjectNotFoundException ex) {
			return Response.status(Response.Status.NOT_FOUND)
					.type(MediaType.APPLICATION_JSON)
					.entity(ex.getMessage())
					.build();
		}
	}

	public static class LectioAuthorizationExceptionMapper implements ExceptionMapper<LectioAuthorizationException> {
		@Override
		public Response toResponse(LectioAuthorizationException ex) {
			return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON)					
					.entity(ex.getMessage())
					.build();
		}
	}

	public static class LectioConstraintExceptionMapper implements ExceptionMapper<LectioConstraintException> {
		@Override
		public Response toResponse(LectioConstraintException ex) {
			return Response.status(Response.Status.CONFLICT).type(MediaType.APPLICATION_JSON)
					.entity(ex.getMessage())
					.build();
		}
	}

}
