package com.kakay.lectio.test.rest.authentication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.net.HttpHeaders;
import com.kakay.lectio.auth.IdentityAuthenticator;
import com.kakay.lectio.auth.LectioAuthorizer;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.rest.resources.LoginResource;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.test.rest.TestRestResources;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kakay.lectio.test.scenarios.VorkosiganSeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioException;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;

public abstract class TestUserAuth extends TestRestResources {

	

	public TestUserAuth() {
		super();
	}
	abstract protected String getTargetString();


	@Test
	public void testNoToken() throws Exception {
		String targetString = getTargetString();
	
		try {

			// Hit endpoint without the token
		    String resp = appRule.client().target(appUri + targetString)
		            .request()
		            .get(String.class);
			fail("Not authorized exception should have been thrown.");
		} catch (NotAuthorizedException e) {
			assertTrue("This exception is supposed to be thrown when using no credentials.", true);
		}
	
	}

	@Test
	public void testBogusToken() throws Exception {
		String targetString = getTargetString();
	
		try {
			String bogusToken = RandomStringUtils.randomAlphanumeric(32);
			// Hit endpoint with a bogus token
		    String resp = appRule.client().target(appUri + targetString)
		            .request()
		            .header(HttpHeaders.AUTHORIZATION, "Token " + bogusToken)
		            .get(String.class);
			fail("Not authorized exception should have been thrown.");
		} catch (NotAuthorizedException e) {
			assertTrue("This exception is supposed to be thrown when using no credentials.", true);
		}
	
	}

	


}