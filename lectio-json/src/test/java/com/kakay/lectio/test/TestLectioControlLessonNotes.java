package com.kakay.lectio.test;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.kakay.lectio.test.scenarios.ClearData;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioAuthorizationException;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

public class TestLectioControlLessonNotes {

	@Test
	public void testUpdateLessonNotes() throws Exception{
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1 , 1, 1, 1, 1, 5);
		SeedData seedData = randomSeedData;
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();
		Topic topic = seedData.getTopic();
		LessonNote lessonNote = seedData.getLessonNote();
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		
		int teacherId = teacher.getId();
		int notebookId = notebook.getId();
		int studentId = student.getId();
		int topicId = topic.getId();
		int lessonNoteId = lessonNote.getId();
		
		LessonNote lessonNoteEntity = lectioControl.findLessonNoteById(teacherId,  lessonNoteId);
		String lessonNoteEntityContent = lessonNoteEntity.getContent();
		Assert.assertNotNull("LessonNote should not be null.", lessonNoteEntityContent);
		Assert.assertEquals("Lesson note content does not match.", lessonNote.getContent(), lessonNoteEntityContent);
		Assert.assertEquals("For newly created lesson note, created date and last content update should be the same.",
				lessonNoteEntity.getDate(), lessonNoteEntity.getLastContentUpdate());
		
		String additionalContent = "\nAdded some more addition to the content.";
		String newLessonNoteEntityContent = lessonNoteEntityContent + additionalContent;
		
		LocalDateTime justBeforeUpdate = LocalDateTime.now().minusNanos(1000);
		LessonNote updatedLessonNoteEntity = lectioControl.updateLessonNoteContent(teacherId, lessonNoteId, additionalContent);
		Assert.assertNotNull("Updated lesson note should not be null.", updatedLessonNoteEntity);
		Assert.assertEquals("Updated lesson note content doesn't match.", newLessonNoteEntityContent, newLessonNoteEntityContent);
		Assert.assertTrue("Updated lesson note time of last update is wrong.", justBeforeUpdate.isBefore(updatedLessonNoteEntity.getLastContentUpdate()));		
		Assert.assertEquals("Wrong author in lesson note.", teacherId, lessonNoteEntity.getAuthor().getId());
	}
	
	
	
	/* Test what happens when wrong user adds or edits a lesson note. */
	@Test
	public void testLessonNotesWrongUser() throws Exception {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 1, 1, 1, 1);
		SeedData seedData = randomSeedData;

		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();
		Topic topic = seedData.getTopic();
		LessonNote lessonNote = seedData.getLessonNote();

		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		int teacherId = teacher.getId();
		int notebookId = notebook.getId();
		int studentId = student.getId();
		int topicId = topic.getId();
		int lessonNoteId = lessonNote.getId();

		User anotherUser = lectioControl.addNewUser(-1, "New User", "newuser@newisp.com", "newPassword");
		try {
			lectioControl.addNewLessonNote(anotherUser.getId(), topicId, "Here is the new lesson note content.");
			Assert.fail("An unauthorized user was allowed to add a new lesson note to the topic.");
		} catch (LectioAuthorizationException e) {
			Assert.assertTrue("Correct exception for another user adding new lesson note to topic.", true);
		}


		
		try {
			lectioControl.addNewLessonNote(student.getId(), topicId, "Here is the new lesson note content.");
			Assert.fail("A students was allowed to add a new lesson note to the topic.");
		} catch (LectioAuthorizationException e) {
			Assert.assertTrue("Correct exception for student adding new lesson note to topic.", true);
		}
		
		try {
			lectioControl.updateLessonNoteContent(anotherUser.getId(), lessonNoteId, "Updated content.");
			Assert.fail("An unauthorized user was allowed to update a lesson note.");
		} catch (LectioAuthorizationException e) {
			Assert.assertTrue("Correct exception for another user updating a lesson note to topic.", true);
		}

		try {
			lectioControl.updateLessonNoteContent(student.getId(), lessonNoteId, "Updated content.");
			Assert.fail("A student was allowed to update a lesson note.");
		} catch (LectioAuthorizationException e) {
			Assert.assertTrue("Correct exception for student updating a lesson note to topic.", true);
		}

	}
	
	public void testLessonNotePagination() throws Exception{
		int numLessonNotes = 23;
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1 , 1, 1, 1, 1, numLessonNotes);
		SeedData seedData = randomSeedData;
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();
		Topic topic = seedData.getTopic();
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		
		int teacherId = teacher.getId();
		int notebookId = notebook.getId();
		int studentId = student.getId();
		int topicId = topic.getId();
		
		int count = lectioControl.getCountLessonNotesByTopic(teacherId, topicId);
		Assert.assertEquals("Wrong count returned by getCountLessonNotesByTopic", numLessonNotes, count);

		int numToFind = 4;
		int startingIndex = 0;
		while(startingIndex < numLessonNotes) {
			List<LessonNote> lessonNoteList = lectioControl.findLessonNotesByTopicId(teacherId, topicId, numToFind, startingIndex);
			if ((numLessonNotes - startingIndex)  < numToFind) {
				Assert.assertEquals("Wrong number of lesson notes in list.", numToFind, lessonNoteList.size());
			}
			else {
				Assert.assertEquals("Wrong number of lesson notes at the very end.", numLessonNotes - startingIndex, lessonNoteList.size());
			}
		}
	}	

	@Test
	public void testDuplicateTopic() throws Exception {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1 , 1, 1, 0, 1, 1);
		SeedData seedData = randomSeedData;
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();
		Topic topic = seedData.getTopic();
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		
		int teacherId = teacher.getId();
		int notebookId = notebook.getId();
		int topicId = topic.getId();

		try {
			lectioControl.addNewTopic(teacherId,  notebookId,  topic.getName());
			Assert.fail("Adding duplicate topic should throw LectioConstraintException.");
		}
		catch (LectioConstraintException ex) {
			Assert.assertTrue("Adding duplicate topic exception should have duplicate topic name.",
					ex.getMessage().contains(topic.getName()));
		}
		
	}
	
	@Test
	public void testWrongUserAddsTopic() throws Exception {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1 , 1, 1, 1, 0, 0);
		SeedData seedData = randomSeedData;
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		
		int teacherId = teacher.getId();
		int notebookId = notebook.getId();
		int studentId =  student.getId();

		User newUser = lectioControl.addNewUser(-1,  "testWrongUserAddsTopic.Name",  "testWrongUserAddsTopic@SomeIsp.com",  "SomePassword");
		
		try {
			lectioControl.addNewTopic(newUser.getId(),  notebookId,  "testWrongUserAddsTopicName New User");
			Assert.fail("Topic addition by wrong user should throw  LectioAuthorizationException.");
		}
		catch (LectioAuthorizationException ex) {
			Assert.assertTrue("LectioAuthorizationException should happen.", true);
		}

		try {
			lectioControl.addNewTopic(studentId,  notebookId,  "testWrongUserAddsTopicName.student");
			Assert.fail("Topic addition by student should throw  LectioAuthorizationException.");
		}
		catch (LectioAuthorizationException ex) {
			Assert.assertTrue("LectioAuthorizationException should happen.", true);
		}

	}

	@After
	public void tearDown() throws Exception {
		ClearData.main(new String[]{});
	}
}
