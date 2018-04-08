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
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.model.LessonNote;

import io.dropwizard.auth.Auth;

@PermitAll
@Path("/lectio/topic/{topic-id}")
@Produces(MediaType.APPLICATION_JSON)
public class TopicResource {
	LectioControl lectioControl;
	
	public TopicResource(LectioControl lectioControl) {
		this.lectioControl = lectioControl;
	}
	

	/**
	 * Creates a new lesson note. The body of this request is the 
	 * lesson note content.
	 * @param topicId  Parent topic ID
	 * @param principal  User requesting the new lesson note
	 * @param lessonNoteContent   Content to be added.  This should be a
	 *      sanitized rich text.
	 * @return  The Lesson Note information.
	 * 
	 * @throws WebApplicationException(Status.FORBIDDEN) if user is not authorized
	 *      to create a new lesson note here.
	 *
	 */
	@PermitAll
	@POST
	@Timed
	@Path("/createlessonnote")
	@JsonView(Views.NoDetails.class)
	public LessonNote createLessonNote(@PathParam("topic-id") int topicId, @Auth LectioPrincipal principal, LessonNoteResource.LessonNoteContent lessonNoteContent) {

		if (!lectioControl.authCheckModifyTopic(principal.getId(), topicId)) {
			throw new WebApplicationException("User " + principal.getId() + " is not authorized to modify topic " + topicId, Status.FORBIDDEN);
		}
		try {
			LessonNote lessonNote = lectioControl.addNewLessonNote(principal.getId(), topicId, lessonNoteContent.getContent());
			return lessonNote;
		}
		catch(LectioConstraintException exception ) {
			throw new LectioSystemException("Lesson notes should not have constraints.");
		}
	}
}
