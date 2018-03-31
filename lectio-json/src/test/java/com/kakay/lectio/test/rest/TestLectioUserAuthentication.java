package com.kakay.lectio.test.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kakay.lectio.test.scenarios.VorkosiganSeedData;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.User;

import io.dropwizard.jackson.Jackson;

/* This test uses known username and passwords so that I can debug some functionality.
 * 
 */
public class TestLectioUserAuthentication extends TestRestResources {

	@Test
	public void testUserLogin() throws LectioException, JsonParseException, JsonMappingException, IOException {

		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();

		String teacherEmail = teacher.getEmail();
		int notebookId = notebook.getId();

		String targetString = "/lectio/notebook/" + notebookId + "/activetopics";

		// Create authentication parameters
		Map<String, String> emailToPasswordMap = seedData.getEmailToPasswordMap();
		String teacherPassword = emailToPasswordMap.get(teacher.getEmail());
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(teacherEmail, teacherPassword);
		// Hit the endpoint and get the raw json string
		Client client = resources.client();
		client.register(authFeature);
		String resp = client.target(targetString).request().get(String.class);

		ObjectMapper objectMapper = Jackson.newObjectMapper();
        NotebookRep notebookRep = objectMapper.readValue(resp,  NotebookRep.class);
        Assert.assertNotNull("NotebookRep retrieved should have had a notebook name.", notebookRep.getNotebook());
        assertNotNull("Notebook retrieved was null", notebookRep);;
	}
	
	@Test
	public void testWrongPassword() throws IOException {

		
		HttpAuthenticationFeature wrongAuthFeature = HttpAuthenticationFeature.basic(teacher.getName(),
				"bogusPassword");
		Client wrongClient = resources.client();
		wrongClient.register(wrongAuthFeature);

		int notebookId = notebook.getId();
		String targetString = "/lectio/notebook/" + notebookId + "/activetopics";

		try {
			String resp = wrongClient.target(targetString).request()
					.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, teacher.getName())
				    .property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, "boguspassword")
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
