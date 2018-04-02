package com.kakay.lectio.rest.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.views.Views;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.exception.LectioAuthorizationException;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;

import io.dropwizard.auth.Auth;
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
	
	
	/* Get the active topic names and ids for a notebook. */
    @PermitAll
	@GET
    @Timed
    @JsonView(Views.NoDetails.class)
    public NotebookRep getTopicNames(@PathParam("notebook-id") int notebookId, @Auth LectioPrincipal principal) throws LectioAuthorizationException {
		List<Topic> topicList = lectioControl.findActiveTopicsByNotebook(principal.getId(), notebookId);
		Notebook notebook = lectioControl.findNotebookById(principal.getId(),  notebookId);
		
		NotebookRep notebookRep = new NotebookRep(notebook, topicList);
		return notebookRep;
    }
    
   
	/* Get the active topic names and ids for a notebook. */
    @GET
    @Path("/withlessons")
    @Timed
    @JsonView(Views.LastLessonNotes.class)
    public NotebookRep getTopicsAndLessons(@Auth LectioPrincipal principal, @PathParam("notebook-id") int notebookId) {
    	int bogusId = 20;
    	List<Topic> topicList = lectioControl.findActiveTopicsAndLessonNotesByNotebook(principal.getId(), notebookId);
		Notebook notebook = lectioControl.findNotebookById(principal.getId(),  notebookId);
		
		NotebookRep notebookRep = new NotebookRep(notebook, topicList);
		
			    
	    return notebookRep;
   }

    

}
