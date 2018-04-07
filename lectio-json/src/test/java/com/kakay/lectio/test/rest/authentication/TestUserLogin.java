package com.kakay.lectio.test.rest.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.test.rest.TestRestResources;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kakay.lectio.test.scenarios.VorkosiganSeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioException;

public class TestUserLogin extends TestRestResources{

	@Test
	public void testWrongPassword() throws IOException {
		String targetString = "/login";		
		try {
		LoginResponse loginResponse = getLoginResponse(teacher.getEmail(), "bogusPassword", targetString);
		assertTrue("Login with wrong email and password should have failed.", loginResponse == null);
		}
		catch(WebApplicationException ex) {
			assertEquals("Exception should be of status Status.UNAUTHORIZED for a login failure.",
					Status.UNAUTHORIZED, ex.getResponse().getStatusInfo());
		}
	}
	
	@Test
	public void testUserLogin() throws LectioException, JsonParseException, JsonMappingException, IOException {

		
		String teacherEmail = teacher.getEmail();
		String targetString = "/login";
	
		// Create authentication parameters
		Map<String, String> emailToPasswordMap = seedData.getEmailToPasswordMap();
		String teacherPassword = emailToPasswordMap.get(teacher.getEmail());

		
		LoginResponse loginResponse = getLoginResponse(teacherEmail, teacherPassword, targetString);
		assertNotNull("With valid login, returned value should be non-null.", loginResponse);
		
		assertNotNull("Token content should have token string.", loginResponse.getToken());
		assertTrue("Token string should not be blank.", loginResponse.getToken().length() > 0);
		savedTokenString = loginResponse.getToken();
		
		
		String endpointTarget = hitEndpoint(getTargetString());
		assertNotNull("hitEndpoint returned a null", endpointTarget);
		assertEquals("User ID on loginResponse is wrong.",  teacher.getId(), loginResponse.getUserId());
	}

	@Override
	protected SeedData getSeedData() {
		VorkosiganSeedData vorkosiganSeedData = new VorkosiganSeedData();
		vorkosiganSeedData.seedData();
		
		return vorkosiganSeedData;
	
	}
	protected String getTargetString() {
		int notebookId = notebook.getId();

		String targetString = "/lectio/notebook/" + notebookId + "/activetopics";
		return targetString;
	}
	@Override
	protected Object getResource() {
		NotebookActiveTopicsResource notebookActiveTopicResource = new NotebookActiveTopicsResource();
		notebookActiveTopicResource.setLectioControl(new LectioPersistence().getLectioControlById());

		Object resource = notebookActiveTopicResource;
		return resource;
	}	
}
