package com.kakay.lectio.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.model.User;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;

import io.dropwizard.jackson.Jackson;

public class TestLectioRestNotebookResources extends TestRestResources {
	protected int numTopics = 6;
	
	@Test
	public void testCreateNewTopic() throws Exception {
		int notebookId = notebook.getId();
		String topicName = "Composer Opus 1 # 3";
		String targetString = "/lectio/notebook/" + notebookId + "/createtopic";
		Response response = this.postEndpoint(targetString, topicName);
		assertEquals("Response status should be okay.", Status.OK.getStatusCode(), response.getStatus());
		Topic topic = response.readEntity(Topic.class);
		assertNotNull("Topic should not be null when creating new topic.", topic);
		assertEquals("Topic name is wrong.", topicName, topic.getName());
	}

	@Test
	public void testNotebookActiveTopics() throws IOException {
		int notebookId = notebook.getId();
		String targetString = "/lectio/notebook/" + notebookId + "/activetopics";
		String resp = hitEndpoint(targetString);

		ObjectMapper objectMapper = Jackson.newObjectMapper();
		NotebookRep notebookRep = objectMapper.readValue(resp, NotebookRep.class);
		Assert.assertNotNull("NotebookRep retrieved should have had a notebook name.", notebookRep.getNotebook());
		Assert.assertEquals("Notebook names don't match.", notebook.getName(), notebookRep.getNotebook().getName());

		Assert.assertNull("NotebookRep should not carry studio information.", notebookRep.getNotebook().getStudio());

		Assert.assertNotNull("NotebookRep from notebook/activetopics should return a list of topics.",
				notebookRep.getTopicList());
		Assert.assertEquals("Wrong number of topics returned from notebook/activetopics.", numTopics,
				notebookRep.getTopicList().size());

	}

	
	@Test
	public void testWrongUserReadNotebook() throws Exception{

		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();
		String wrongUserPassword =  "pwd";
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		User wrongUser = lectioControl.addNewUser("testWrongUserReadNotebook Name",  "testWrongUserReadNotebook@email.com", wrongUserPassword );
		LoginResponse wrongUserLoginResponse = getLoginResponse(wrongUser.getEmail(), wrongUserPassword, "/login");
		savedTokenString = wrongUserLoginResponse.getToken();
		
		String targetString = "/lectio/notebook/" + notebook.getId() + "/activetopics";
		
		try {
		String resp = hitEndpoint(targetString);
		Assert.fail("Getting active topics by wrong user should have thrown a LectioAuthorizationException.");
		}
		catch(ForbiddenException ex) {
			Assert.assertTrue("ForbiddenException is the correct exception to throw.", true);
		}
	}

	
	@Override
	protected SeedData getSeedData() {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 5, 1, numTopics, 0);

		return randomSeedData;
	}


}
