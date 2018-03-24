package com.kakay.lectio.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.kakay.lectio.rest.LectioRestControl;
import com.kakay.lectio.rest.representation.NotebookRep;

@Path("/lectio/notebook/{notebook-id}/activetopicswithlessons")
@Produces(MediaType.APPLICATION_JSON)
public class NotebookActiveTopicsWithLessonsResource {
	LectioRestControl lectioRestControl;
	
	public NotebookActiveTopicsWithLessonsResource (LectioRestControl lectioRestControl) {
		this.lectioRestControl = lectioRestControl;
	}
	
	/* Get the active topic names and ids for a notebook. */
    @GET
    @Timed
    public NotebookRep getTopicsAndLessons(@PathParam("notebook-id") int notebookId) {
    	return lectioRestControl.getActiveTopics(0, notebookId, true);
    }

	
	
}
