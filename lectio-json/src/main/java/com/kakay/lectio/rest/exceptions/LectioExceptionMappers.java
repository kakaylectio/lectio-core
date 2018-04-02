package com.kakay.lectio.rest.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.kktam.lectio.control.exception.LectioAuthorizationException;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.control.exception.LectioObjectNotFoundException;

public class LectioExceptionMappers  {

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
			return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON)					.entity(ex.getMessage())
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
