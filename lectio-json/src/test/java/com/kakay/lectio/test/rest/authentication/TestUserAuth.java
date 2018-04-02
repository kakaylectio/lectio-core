package com.kakay.lectio.test.rest.authentication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kakay.lectio.auth.LectioAuthorizer;
import com.kakay.lectio.auth.LectioPrincipal;
import com.kakay.lectio.test.rest.TestRestResources;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kakay.lectio.test.scenarios.VorkosiganSeedData;
import com.kktam.lectio.control.exception.LectioException;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;

public abstract class TestUserAuth extends TestRestResources {
	@Rule
	public ResourceTestRule noCredentialResource = ResourceTestRule.builder()
				.addResource(getResource())
				.addProvider(new AuthDynamicFeature(BASIC_AUTH_HANDLER))
				.addProvider(new LectioAuthorizer())
		        .addProvider(RolesAllowedDynamicFeature.class)
		        .addProvider(new AuthValueFactoryProvider.Binder<>(LectioPrincipal.class))
				.build();

	@Rule
	public ResourceTestRule wrongPasswordResource = ResourceTestRule.builder()
				.addResource(getResource())
				.addProvider(new AuthDynamicFeature(BASIC_AUTH_HANDLER))
				.addProvider(new LectioAuthorizer())
		        .addProvider(RolesAllowedDynamicFeature.class)
		        .addProvider(new AuthValueFactoryProvider.Binder<>(LectioPrincipal.class))
				.build();

	public TestUserAuth() {
		super();
	}
	abstract protected String getTargetString();

	@Test
	public void testUserLogin() throws LectioException, JsonParseException, JsonMappingException, IOException {

		String teacherEmail = teacher.getEmail();
		String targetString = getTargetString();
	
		// Create authentication parameters
		Map<String, String> emailToPasswordMap = seedData.getEmailToPasswordMap();
		String teacherPassword = emailToPasswordMap.get(teacher.getEmail());
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(teacherEmail, teacherPassword);
		// Hit the endpoint and get the raw json string
		Client client = resources.client();
		client.register(authFeature);
		String resp = client.target(targetString).request().get(String.class);
	
		assertNotNull("With valid login, returned value should be non-null.", resp);
	}

	@Test
	public void testNoCredentials() throws Exception {
		Client noCredentialClient = noCredentialResource.client();
	
		String targetString = getTargetString();
	
		try {
			String resp = noCredentialClient.target(targetString).request()
				    .get(String.class);
			fail("Not authorized exception should have been thrown.");
		} catch (NotAuthorizedException e) {
			assertTrue("This exception is supposed to be thrown when using no credentials.", true);
		}
	
	}

	@Test
	public void testWrongPassword() throws IOException {
	
		HttpAuthenticationFeature wrongAuthFeature = HttpAuthenticationFeature.basic(teacher.getName(),
				"bogusPassword");
		Client wrongClient = wrongPasswordResource.client();
		wrongClient.register(wrongAuthFeature);
	
		String targetString = getTargetString();
	
		try {
			String resp = wrongClient.target(targetString).request()
				    .get(String.class);
			fail("Not authorized exception should have been thrown.");
		} catch (NotAuthorizedException e) {
			assertTrue("This exception is supposed to be thrown when using wrong password.", true);
		}
	
	}

	@Override
	protected SeedData getSeedData() {
		VorkosiganSeedData vorkosiganSeedData = new VorkosiganSeedData();
		vorkosiganSeedData.seedData();
		
		return vorkosiganSeedData;
	
	}

}