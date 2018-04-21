package com.kakay.lectio.test.rest;

import static org.junit.Assert.fail;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.kakay.lectio.auth.LoginResponse;
import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.control.exception.LectioConstraintException;
import com.kakay.lectio.rest.resources.LessonNoteResource;
import com.kakay.lectio.rest.resources.TopicResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

/**
 *  Run tests on LessonNoteResource
 */
public class TestLectioRestLessonNoteResource extends TestRestResources {



	@Override
	protected SeedData getSeedData() {
		RandomSeedData seedData = new RandomSeedData();
		seedData.generateSeed(1, 1, 1, 1, 1, 1);
		return seedData;
	}

	@Override
	protected Object getResource() {
		return new LessonNoteResource(new LectioPersistence().getLectioControlById());
	}

	@Test
	public void testUpdateLessonNote() {
		LessonNote lessonNote = seedData.getLessonNote();
		
		String targetString = "/lectio/lessonnote/" + lessonNote.getId() + "/updatecontent";

		String newContent = lessonNote.getContent() + "\n  I've added another line to the content.";
		LessonNoteResource.LessonNoteContent lessonNoteContent = new LessonNoteResource.LessonNoteContent();
		lessonNoteContent.setContent(newContent);
		Response resp = postEndpoint(targetString, lessonNoteContent);
		Assert.assertEquals("Status response of updating new lesson note should be OK.", 
				Status.OK.getStatusCode(), resp.getStatus());
		LessonNote retrievedLessonNote = resp.readEntity(LessonNote.class);
		Assert.assertEquals("Lesson note content should match.", newContent,
				retrievedLessonNote.getContent());
	}
	
	@Test
	public void testWrongUserUpdateLessonNote() throws LectioConstraintException {
		LessonNote lessonNote = seedData.getLessonNote();
		
		String targetString = "/lectio/lessonnote/" + lessonNote.getId() + "/updatecontent";

		// Login in a wrong user.
		LectioControl lectioControl = new LectioPersistence().getLectioControlById();
		String wrongUserPassword = "pwd";
		User wrongUser = lectioControl.addNewUser("testWrongUserUpdateLessonNote Name",  "testWrongUserUpdateLessonNote@email.com", wrongUserPassword );
		LoginResponse wrongUserLoginResponse = getLoginResponse(wrongUser.getEmail(), wrongUserPassword, "/login");
    	savedTokenString = wrongUserLoginResponse.getToken();

		
		
		String newContent = lessonNote.getContent() + "\n  I've added another line to the content.";
		LessonNoteResource.LessonNoteContent lessonNoteContent = new LessonNoteResource.LessonNoteContent();
		lessonNoteContent.setContent(newContent);
		Response resp = postEndpoint(targetString, lessonNoteContent);
		Assert.assertEquals("Status response of updating lesson note should be FORBIDDEN.", 
				Status.FORBIDDEN.getStatusCode(), resp.getStatus());
		

	}
}
