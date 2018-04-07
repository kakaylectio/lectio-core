package com.kakay.lectio.rest.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.codahale.metrics.annotation.Timed;
import com.kakay.lectio.auth.EmailPassword;
import com.kakay.lectio.auth.IdentityAuthenticator;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.auth.LectioToken;
import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.auth.TokenAuthenticator;


/**
 * JSON resource to allow users to log in based on email address and password.
 
 */
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
public class LoginResource  {
	public static final String LOGIN_COOKIE_NAME = "LectioCookie";
	public static final String LOGIN_COOKIE_DOMAIN = "localhost";  // Change this to read from a file

	IdentityAuthenticator authenticator;
	TokenAuthenticator tokenAuthenticator;
	
	/**
	 * 
	 * 
	 * @param authenticator  The authenticator used to verify a username and password
	 * @param tokenAuthenticator  The authenticator used to verify tokens.  Tokens are generated
	 *                            using this resource and returned as a response.
	 */
	public LoginResource( IdentityAuthenticator authenticator, TokenAuthenticator tokenAuthenticator) {
		this.authenticator = authenticator;
		this.tokenAuthenticator = tokenAuthenticator;
	}

	/**
	 * Authenticates the email/password pair from a client and returns the token
	 * and a bit of information about the user in the form of a LoginResponse.  
	 * If the email and password do not pass authentication, a WebApplicationException
	 * is thrown with status set to UNAUTHORIZED.
	 * 
	 * @param Email and Password object deserialized JSON object from client
	 * @return A LoginResponse that contains the token.  The token needs to be 
	 *    sent as part of the HTTP Authorization header prepended with "Token ".  
	 *    The LoginResponse also contains the username and user ID for clients.
	 *    
	 * @throws WebApplicationException(UNAUTHORIZED) if authentication fails.
	 */
	@POST
    @Timed
	public LoginResponse login(EmailPassword info) {
		
		LectioPrincipal lectioPrincipal = authenticator.checkStringCredentials(info.getEmail(), info.getPassword());
		if (lectioPrincipal == null) {
		
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		LoginResponse loginResponse = tokenAuthenticator.principal2loginResponse(lectioPrincipal);
		return loginResponse;
	}
	
	
}
