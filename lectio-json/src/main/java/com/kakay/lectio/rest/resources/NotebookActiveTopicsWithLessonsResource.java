package com.kakay.lectio.rest.resources;

import java.util.List;

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
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;

import io.dropwizard.auth.Auth;

@Path("/lectio/notebook/{notebook-id}/activetopicswithlessons")
@Produces(MediaType.APPLICATION_JSON)
public class NotebookActiveTopicsWithLessonsResource {
	LectioControl lectioControl;
	
	public NotebookActiveTopicsWithLessonsResource (LectioControl lectioControl) {
		this.lectioControl = lectioControl;
	}
	
	/* Get the active topic names and ids for a notebook. */
    @GET
    @Timed
    @JsonView(Views.LastLessonNotes.class)
    public NotebookRep getTopicsAndLessons(@Auth LectioPrincipal principal, @PathParam("notebook-id") int notebookId) {
    	int bogusId = 20;
    	List<Topic> topicList = lectioControl.findActiveTopicsAndLessonNotesByNotebook(bogusId, notebookId);
		Notebook notebook = lectioControl.findNotebookById(bogusId,  notebookId);
		
		NotebookRep notebookRep = new NotebookRep(notebook, topicList);
		
			    
	    return notebookRep;
   }

	
	
}
