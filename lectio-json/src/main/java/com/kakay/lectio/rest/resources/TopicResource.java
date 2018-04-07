package com.kakay.lectio.rest.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@PermitAll
@Path("/lectio/topic/{topic-id}")
@Produces(MediaType.APPLICATION_JSON)
public class TopicResource {

}
