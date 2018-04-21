package com.kakay.lectio.rest.resources;

import java.util.ArrayList;
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
import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.exception.LectioAuthorizationException;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.views.Views;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Role;

import io.dropwizard.auth.Auth;

@PermitAll
@Path("/lectio/user/{user-id}")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	LectioControl lectioControl;
	public UserResource (LectioControl lectioControl) {
		this.lectioControl = lectioControl;
	}
	/* Returns the list of notebooks available to user. */
	@PermitAll
	@GET
    @Timed
    @Path("/notebooks")
    @JsonView(Views.NoDetails.class)
    public List<NotebookRep> getTopicNames(@PathParam("notebook-id") int notebookId, @Auth LectioPrincipal principal)  {
		List<Notebook> notebookList = lectioControl.findNotebooksByUser(principal.getId());
		List<NotebookRep> notebookRepList = new ArrayList<NotebookRep>(notebookList.size());

		for (Notebook notebook: notebookList) {
			NotebookRep notebookRep = new NotebookRep();
			notebookRep.setNotebook(notebook);
			Role role = lectioControl.findRoleOfUserInNotebook(principal.getId(), notebook.getId());
			notebookRep.setUserRole(role);
			notebookRepList.add(notebookRep);
		}
		return notebookRepList;
		
		
	}

}
