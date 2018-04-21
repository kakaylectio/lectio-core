package com.kakay.lectio.rest.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.exception.LectioAuthorizationException;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.views.Views;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Role;
import com.kktam.lectio.model.Topic;

import io.dropwizard.auth.Auth;

/**
 * REST API to retrieve a notebook and all its topic resources.
 */
@PermitAll
@Path("/lectio/notebook/{notebook-id}/activetopics")
@Produces(MediaType.APPLICATION_JSON)
public class NotebookActiveTopicsResource {

	LectioControl lectioControl;
	int bogusId = 20;

	public NotebookActiveTopicsResource() {

	}

	public void setLectioControl(LectioControl control) {
		lectioControl = control;
	}

	/* Get the active topic names and ids for a notebook.  Does not include all the lesssons associated with the topics. */
	@PermitAll
	@GET
	@Timed
	@JsonView(Views.NoDetails.class)
	public NotebookRep getTopicNames(@PathParam("notebook-id") int notebookId, @Auth LectioPrincipal principal) throws LectioAuthorizationException {
		if (!lectioControl.authCheckReadNotebook(principal.getId(), notebookId) ){
			throw new LectioAuthorizationException(
					"User " + principal.getId() + " is not authorized to view notebook " + notebookId);
		}
		
		List<Topic> topicList = lectioControl.findActiveTopicsByNotebook( notebookId);
		Notebook notebook = lectioControl.findNotebookById( notebookId);
		Role role = lectioControl.findRoleOfUserInNotebook(principal.getId(), notebookId);
		
		NotebookRep notebookRep = new NotebookRep(notebook, topicList);
		notebookRep.setUserRole(role);
		return notebookRep;
	}

	/* Get the active topic names and ids for a notebook. Also retrieves the last lesson of
	 * each active topic.  Last lessons are retrieved because this is a common notebook view.
	 * @param principal  The user retrieving topics and lessons
	 * @param notebookId
	 * @return
	 */
	@GET
	@Path("/withlessons")
	@Timed
	@JsonView(Views.LastLessonNotes.class)
	public NotebookRep getTopicsAndLessons(@Auth LectioPrincipal principal, @PathParam("notebook-id") int notebookId) throws LectioAuthorizationException {

		boolean authorized = lectioControl.authCheckReadNotebook(principal.getId(), notebookId);
		if (!authorized) {
			throw new LectioAuthorizationException(
					"User " + principal.getId() + " is not authorized to view notebook " + notebookId);
		}
		List<Topic> topicList = lectioControl.findActiveTopicsAndLessonNotesByNotebook(notebookId);
		Notebook notebook = lectioControl.findNotebookById(notebookId);
		Role role = lectioControl.findRoleOfUserInNotebook(principal.getId(), notebookId);

		NotebookRep notebookRep = new NotebookRep(notebook, topicList);
		notebookRep.setUserRole(role);
		return notebookRep;
	}
	

}
