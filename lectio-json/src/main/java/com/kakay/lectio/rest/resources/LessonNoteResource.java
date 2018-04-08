package com.kakay.lectio.rest.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.rest.exceptions.LectioSystemException;
import com.kakay.lectio.rest.resources.views.Views;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.exception.LectioAuthorizationException;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.control.exception.LectioObjectNotFoundException;
import com.kktam.lectio.model.LessonNote;

import io.dropwizard.auth.Auth;

@PermitAll
@Path("/lectio/lessonnote/{lessonnote-id}")
@Produces(MediaType.APPLICATION_JSON)
public class LessonNoteResource {
	LectioControl lectioControl;

	public LessonNoteResource(LectioControl lectioControl) {
		this.lectioControl = lectioControl;
	}

	/**
	 * Encapsulates the lesson note content requested by the client to be
	 * deserialized as Json string.
	 *
	 */
	public static class LessonNoteContent {
		@JsonProperty
		String content;

		public LessonNoteContent() {
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

	/**
	 * Updates the content of an existing lesson note.
	 * 
	 * @param notebookId
	 *            ID of the notebook
	 * @param principal
	 *            User updating the lesson note
	 * @param lessonNoteContent
	 *            New content. This should be a sanitized rich text.
	 * @return The lesson note created.
	 * @throws LectioObjectNotFoundException 
	 * @throws LectioAuthorizationException 
	 * 
	 * @throws WebApplicationException(Status.FORBIDDEN)
	 *             if user is not authorized to update the lessonnote.
	 *
	 */
	@PermitAll
	@POST
	@Timed
	@Path("/updatecontent")
	@JsonView(Views.NoDetails.class)
	public LessonNote updateLessonNote(@PathParam("lessonnote-id") int lessonNoteId, @Auth LectioPrincipal principal,
			LessonNoteContent lessonNoteContent) throws LectioAuthorizationException, LectioObjectNotFoundException {

		if (!lectioControl.authCheckUpdateLessonNote(principal.getId(), lessonNoteId)) {
			throw new WebApplicationException(
					"User " + principal.getId() + " is not authorized to modify lessonnote " + lessonNoteId,
					Status.FORBIDDEN);
		}
		LessonNote lessonNote = lectioControl.updateLessonNoteContent(principal.getId(), lessonNoteId,
				lessonNoteContent.getContent());
		return lessonNote;
	}

}

