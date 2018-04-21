package com.kakay.lectio.test;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.control.exception.LectioAuthorizationException;
import com.kakay.lectio.control.exception.LectioConstraintException;
import com.kakay.lectio.model.LessonNote;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.model.User;
import com.kakay.lectio.test.scenarios.ClearData;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;

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
		
		LessonNote lessonNoteEntity = lectioControl.findLessonNoteById( lessonNoteId);
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
			List<LessonNote> lessonNoteList = lectioControl.findLessonNotesByTopicId(topicId, numToFind, startingIndex);
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
			lectioControl.addNewTopic( notebookId,  topic.getName());
			Assert.fail("Adding duplicate topic should throw LectioConstraintException.");
		}
		catch (LectioConstraintException ex) {
			Assert.assertTrue("Adding duplicate topic exception should have duplicate topic name.",
					ex.getMessage().contains(topic.getName()));
		}
		
	}
	
	@After
	public void tearDown() throws Exception {
		ClearData.main(new String[]{});
	}
}
