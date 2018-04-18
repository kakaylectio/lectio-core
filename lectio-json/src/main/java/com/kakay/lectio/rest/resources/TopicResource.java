package com.kakay.lectio.rest.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.rest.resources.views.Views;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.exception.LectioAuthorizationException;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.control.exception.LectioNotImplementedException;
import com.kktam.lectio.control.exception.LectioSystemException;
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
	 * Gets the lesson note of a topic based on a specific criteria
	 * @param topicId  ID of the topic to which lesson note belongs
	 * @param principal  The person working with the client to invoke getLessonNote
	 * @param criteria  The criteria
	 * @return
	 */
	@PermitAll
	@GET
	@Timed
	@Path("/findlessonnote")
	@JsonView(Views.NoDetails.class)
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
	public LessonNote findLessonNote(@Auth LectioPrincipal principal,
			@QueryParam("afterid") Integer afterId,
			@PathParam("topic-id") int topicId
			
			) throws LectioAuthorizationException {

		if (!lectioControl.authCheckReadTopic(principal.getId(), topicId)) {
			throw new LectioAuthorizationException("User " + principal.getId() + 
					" is not authorized to read lesson notes in topic " + topicId);
		}
		if (afterId != null) {
			List<LessonNote> lessonNoteList = lectioControl.findLessonNotesByTopicId(topicId, 2, 0);
			if (lessonNoteList.size() < 2) {
				return null;
			}
			else {
				LessonNote lessonNote = lessonNoteList.get(0);
				if (lessonNote.getId() == afterId.intValue()) {
					LessonNote foundLessonNote = lessonNoteList.get(1);
					return foundLessonNote;
				}
				else {
					// TODO:   Iterate to search for Lesson Note.
					throw new LectioNotImplementedException("Search for deeper than 2 lesson notes in a topic has not been implemented.");
				}
			}
		}
		return null;
	}

	
	/**
	 * Gets the lesson notes of a topic based on a specific criteria
	 * @param topicId  ID of the topic to which lesson note belongs
	 * @param principal  The person working with the client to invoke getLessonNote
	 * @param criteria  The criteria
	 * @return
	 */
	@PermitAll
	@GET
	@Timed
	@Path("/findlessonnotes")
	@JsonView(Views.NoDetails.class)
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
	public List<LessonNote> findLessonNotes(@Auth LectioPrincipal principal,
			@QueryParam("afterid") Integer afterId,
			@QueryParam("startindex") Integer startIndex,
			@QueryParam("numitems") Integer numItems,
			@PathParam("topic-id") int topicId
			
			) throws LectioAuthorizationException {

		if (!lectioControl.authCheckReadTopic(principal.getId(), topicId)) {
			throw new LectioAuthorizationException("User " + principal.getId() + 
					" is not authorized to read lesson notes in topic " + topicId);
		}
		if (afterId != null) {
			List<LessonNote> lessonNoteList = lectioControl.findLessonNotesByTopicId(topicId, 2, 0);
			return lessonNoteList;
		}
		else if (numItems != null) {
			int startIndexInt = 0;
			if (startIndex != null) {
				startIndexInt = startIndex.intValue();
			}
			List<LessonNote> lessonNoteList = lectioControl.findLessonNotesByTopicId(topicId,  numItems.intValue(), startIndexInt);
			return lessonNoteList;
			
		}
		return null;
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
