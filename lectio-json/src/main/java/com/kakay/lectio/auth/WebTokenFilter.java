package com.kakay.lectio.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.google.common.io.BaseEncoding;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;

/**
 * This class is used in every incoming HTTP request requiring authentication
 * to extract the HTTP header information containing authentication token and
 * authenticating the token.  It blocks the HTTP request if authentication fails.
 *
 */
public class WebTokenFilter extends AuthFilter<String, LectioPrincipal> {

	/**
	 *  Created by Rest Application to add allow token authentication  
	 */
	public WebTokenFilter() {
		this.prefix = "Token";
	}

	/* (non-Javadoc)
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String headerValue = requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		String tokenString;
		
		if (headerValue == null) {	
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
        final int space = headerValue.indexOf(' ');
        if (space <= 0) {
        	throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        final String method = headerValue.substring(0, space);
        if (!method.equalsIgnoreCase("Token")) {
        	throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        
        tokenString = headerValue.substring(space+1); 
        
		
	  if (!authenticate(requestContext, tokenString, SecurityContext.BASIC_AUTH)) {
		  throw new WebApplicationException(Response.Status.UNAUTHORIZED);
	  }
		
		
	}
	
    /**
     * Builder for {@link WebTokenFilter}.
     * <p>A TokenAuthenticator must be provided during the building process.</p>
     *
     * This Builder builds a WebTokenFilter that takes a String as credentials (JSON of email and password),
     * and uses the TokenAuthenticator that returns LectioPrincipal as its principal.
     * 
     */
    public static class Builder extends AuthFilterBuilder<String, LectioPrincipal, WebTokenFilter> {
        
        protected WebTokenFilter newInstance() {
            WebTokenFilter filter =  new WebTokenFilter();
            filter.prefix = "Token";
            return filter;
        }
    }
}
