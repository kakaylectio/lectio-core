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
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

public class TestLectioControlTopics {

	@Test
	public void testUpdateTopic() throws Exception {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 1, 1, 1, 5);
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

		// Update the lesson note name
		String newTopicName = "Updated Topic Name";
		Topic updatedTopic = lectioControl.updateTopicName(teacherId, topicId, newTopicName);
		Assert.assertNotNull("Updated added should not be null.", updatedTopic);
		Assert.assertEquals("Updated topic name does not match.", newTopicName, updatedTopic.getName());
		Assert.assertEquals("Updated topic is not the same topic.", topicId, updatedTopic.getId());

		Topic foundTopic = lectioControl.findTopicById(teacherId, topicId);
		Assert.assertNotNull("After update, topic should be found.", foundTopic);
		Assert.assertEquals("Find topic by Id after topic update doesn't have matching topic name.", newTopicName,
				foundTopic.getName());

	}

	@Test
	public void testGetLessonNotes() throws LectioException {
		int numLessonNotes = 5;
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1 , 1, 1, 1, 1, numLessonNotes);
		SeedData seedData = randomSeedData;
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();
		Topic topic = seedData.getTopic();
		LessonNote lessonNote;
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		
		int teacherId = teacher.getId();
		int notebookId = notebook.getId();
		int studentId = student.getId();
		int topicId = topic.getId();
		int lessonNoteId = seedData.getLessonNote().getId();

		int numToFind = numLessonNotes;
		int startingIndex = 0;
		List<LessonNote> lessonNoteList = lectioControl.findLessonNotesByTopicId(teacherId, topicId, numToFind, startingIndex);
		Assert.assertNotNull("Lesson note list should not be null.", lessonNoteList);
		Assert.assertEquals("Not enough lesson notes returned.",  numLessonNotes, lessonNoteList.size());

		// Make sure the very first lesson note is the one that topic is pointing to.
		Topic foundTopic = lectioControl.findTopicById(teacherId,  topicId);
		LessonNote foundTopicLastLesson = foundTopic.getLastLessonNote();
		LessonNote lastLessonNoteFound = lessonNoteList.get(0);
		Assert.assertEquals("Topic is not point to last lesson note.", foundTopicLastLesson.getId(), lastLessonNoteFound.getId());


		LocalDateTime lastTimestamp = LocalDateTime.MAX;
		for (LessonNote lessonNoteFromList : lessonNoteList) {
			Assert.assertTrue("Lesson notes should be returned in order from latest to earliest",
					lastTimestamp.isAfter(lessonNoteFromList.getDate()));
			lastTimestamp = lessonNoteFromList.getDate();
		}


	}



	@After
	public void tearDown() throws Exception {
		ClearData.main(new String[]{});
	}

}
