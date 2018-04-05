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

public class WebTokenFilter extends AuthFilter<String, LectioPrincipal> {

	public WebTokenFilter() {
		this.prefix = "Token";
	}
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
     * Builder for {@link BasicCredentialAuthFilter}.
     * <p>An {@link Authenticator} must be provided during the building process.</p>
     *
     * @param <P> the principal
     */
    public static class Builder extends AuthFilterBuilder<String, LectioPrincipal, WebTokenFilter> {

        
        protected WebTokenFilter newInstance() {
            WebTokenFilter filter =  new WebTokenFilter();
            filter.prefix = "Token";
            return filter;
        }
    }
}
