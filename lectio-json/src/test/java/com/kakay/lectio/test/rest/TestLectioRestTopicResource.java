package com.kakay.lectio.test.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.rest.resources.TopicResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

public class TestLectioRestTopicResource extends TestRestResources {


	@Override
	protected SeedData getSeedData() {
		RandomSeedData seedData = new RandomSeedData();
		seedData.generateSeed(1, 1, 1, 1, 1, 2);
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

		RandomSeedData randomSeedData = new RandomSeedData();
		
		String newContent = "Here is some data that is used as  content.\n"
				+ "<ol> <li> It contains ordered </li><li>lists.</li></ol> \n"
				+ "<ul> <li> and unordered </li><li> lists.</li></ul> \n"
				+ " and some text that is not formatted that way.";
		TopicResource.LessonNoteContent lessonNoteContent = new TopicResource.LessonNoteContent();
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

		RandomSeedData randomSeedData = new RandomSeedData();
		
		String newContent = "Here is some data that is used as  content.\n"
				+ "<ol> <li> It contains ordered </li><li>lists.</li></ol> \n"
				+ "<ul> <li> and unordered </li><li> lists.</li></ul> \n"
				+ " and some text that is not formatted that way.";
		TopicResource.LessonNoteContent lessonNoteContent = new TopicResource.LessonNoteContent();
		lessonNoteContent.setContent(newContent);
		Response resp = postEndpoint(targetString, lessonNoteContent);
		Assert.assertEquals("Response should have status FORBIDDEN.", Status.FORBIDDEN.getStatusCode(), resp.getStatus());
	
	}
}
