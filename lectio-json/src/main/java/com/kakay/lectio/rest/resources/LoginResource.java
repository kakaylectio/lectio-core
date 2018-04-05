package com.kakay.lectio.rest.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codahale.metrics.annotation.Timed;
import com.kakay.lectio.auth.EmailPassword;
import com.kakay.lectio.auth.IdentityAuthenticator;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.auth.LectioToken;
import com.kakay.lectio.auth.TokenAuthenticator;


@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
public class LoginResource  {
	public static final String LOGIN_COOKIE_NAME = "LectioCookie";
	public static final String LOGIN_COOKIE_DOMAIN = "localhost";  // Change this to read from a file

	IdentityAuthenticator authenticator;
	TokenAuthenticator tokenAuthenticator;
	public LoginResource( IdentityAuthenticator authenticator, TokenAuthenticator tokenAuthenticator) {
		this.authenticator = authenticator;
		this.tokenAuthenticator = tokenAuthenticator;
	}

	@POST
    @Timed
	public Response login(EmailPassword info) {
		
		LectioPrincipal lectioPrincipal = authenticator.checkStringCredentials(info.getEmail(), info.getPassword());
		if (lectioPrincipal == null) {
			Response.status(Status.UNAUTHORIZED).build();
		}
		LectioToken token = tokenAuthenticator.principal2Token(lectioPrincipal);
		String tokenString = tokenAuthenticator.serializeToken(token);
		NewCookie newCookie = new NewCookie(LOGIN_COOKIE_NAME, tokenString);
		return Response.ok().cookie(newCookie).build();
	}
	
	
}
