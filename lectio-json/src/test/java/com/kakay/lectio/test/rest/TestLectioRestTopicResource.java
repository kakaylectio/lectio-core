package com.kakay.lectio.test.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.LessonNoteResource;
import com.kakay.lectio.rest.resources.TopicResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

import io.dropwizard.jackson.Jackson;

public class TestLectioRestTopicResource extends TestRestResources {


	@Override
	protected SeedData getSeedData() {
		RandomSeedData seedData = new RandomSeedData();
		seedData.generateSeed(1, 1, 1, 1, 1, 0);
		return seedData;
	}

	@Override
	protected Object getResource() {
		return new TopicResource(new LectioPersistence().getLectioControlById());
	}
	
	@Test
	public void testCreateNewLessonNote() {
		Topic topic = seedData.getTopic();
		String targetString = "/lectio/topic/" + topic.getId() + "/createlessonnote";

		String newContent = "Here is some data that is used as  content.\n"
				+ "<ol> <li> It contains ordered </li><li>lists.</li></ol> \n"
				+ "<ul> <li> and unordered </li><li> lists.</li></ul> \n"
				+ " and some text that is not formatted that way.";
		LessonNoteResource.LessonNoteContent lessonNoteContent = new LessonNoteResource.LessonNoteContent();
		lessonNoteContent.setContent(newContent);
		Response resp = postEndpoint(targetString, lessonNoteContent);
		Assert.assertEquals("Status response of creating new lesson note should be OK.", 
				Status.OK.getStatusCode(), resp.getStatus());
		LessonNote retrievedLessonNote = resp.readEntity(LessonNote.class);
		Assert.assertEquals("Lesson note content should match.", newContent,
				retrievedLessonNote.getContent());
	}
	
	@Test
	public void testCreateLessonNoteWrongUser() throws Exception{
		Topic topic = seedData.getTopic();
		String targetString = "/lectio/topic/" + topic.getId() + "/createlessonnote";

		LectioControl lectioControl = new LectioPersistence().getLectioControlById();
		String wrongUserPassword = "pwd";
		User wrongUser = lectioControl.addNewUser("testWrongUserNewLessonNote Name",  "testWrongUserNewLessonNote@email.com", wrongUserPassword );
		LoginResponse wrongUserLoginResponse = getLoginResponse(wrongUser.getEmail(), wrongUserPassword, "/login");
		savedTokenString = wrongUserLoginResponse.getToken();

		String newContent = "Here is some data that is used as  content.\n"
				+ "<ol> <li> It contains ordered </li><li>lists.</li></ol> \n"
				+ "<ul> <li> and unordered </li><li> lists.</li></ul> \n"
				+ " and some text that is not formatted that way.";
		LessonNoteResource.LessonNoteContent lessonNoteContent = new LessonNoteResource.LessonNoteContent();
		lessonNoteContent.setContent(newContent);
		Response resp = postEndpoint(targetString, lessonNoteContent);
		Assert.assertEquals("Response should have status FORBIDDEN.", Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	
	}
	
	/**
	 * Tests the getLessonNote function that uses a LessonNoteCriteria 
	 * that specifies getting the lesson note after a specific lesson note.
	 */
	@Test 
	public void testGetLessonNoteAfter() throws Exception{
		Topic topic = seedData.getTopic();
		String targetString = "/lectio/topic/" + topic.getId() + "/createlessonnote";

		String newContent = "Seed content 1.";
		LessonNoteResource.LessonNoteContent lessonNoteContent = new LessonNoteResource.LessonNoteContent();
		lessonNoteContent.setContent(newContent);
		Response resp = postEndpoint(targetString, lessonNoteContent);
		Assert.assertEquals("Status response of creating new lesson note should be OK.", 
				Status.OK.getStatusCode(), resp.getStatus());
		
		LessonNote lessonNote1 = resp.readEntity(LessonNote.class);

		lessonNoteContent.setContent("Seed content 2.");
		Response resp2 = postEndpoint(targetString, lessonNoteContent);
		Assert.assertEquals("Status response of creating new lesson note should be OK.", 
				Status.OK.getStatusCode(), resp2.getStatus());
		LessonNote lessonNote2 = resp2.readEntity(LessonNote.class);
		
		String targetString2 = "/lectio/topic/" + topic.getId() + "/findlessonnote";
		Map<String, Object> queryParamMap = new HashMap(1);
		queryParamMap.put("afterid",  String.valueOf(lessonNote2.getId()));
		String foundLessonNoteJsonString = hitEndpoint(targetString2, queryParamMap);
		Assert.assertNotNull("LessonNote should have been found.",
				foundLessonNoteJsonString);
		ObjectMapper objectMapper = Jackson.newObjectMapper();
		LessonNote foundLessonNote = objectMapper.readValue(foundLessonNoteJsonString, LessonNote.class);
        Assert.assertNotNull("LessonNote should note be null when returned from findlessonnote.",
        		foundLessonNote);

		Assert.assertEquals("LessonNoteFound ID should match the one that was added.",
				lessonNote1.getId(), foundLessonNote.getId());
		
	}
}
