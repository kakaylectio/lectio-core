package com.kakay.lectio.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.control.exception.LectioException;
import com.kakay.lectio.model.LessonNote;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.model.TopicState;
import com.kakay.lectio.model.User;
import com.kakay.lectio.test.scenarios.ClearData;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;

public class TestLectioControlTopics {

	@Test public void testArchiveTopic() throws Exception {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 1, 1, 8, 1);
		SeedData seedData = randomSeedData;

		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();
		Topic topic = seedData.getTopic();

		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		
		int topicId = topic.getId();
		int notebookId = notebook.getId();

		List<Topic> activeTopics = lectioControl.findActiveTopicsAndLessonNotesByNotebook(notebookId);
		int numTopics = activeTopics.size();
		
		Topic archivedTopic = lectioControl.updateTopicState(topicId, TopicState.archived);
		assertNotNull("Archived topic is not null.", archivedTopic);
		assertEquals("Archived topic is the wrong topic.", topicId, archivedTopic.getId());
		assertEquals("Archived topic state is wrong.", TopicState.archived, archivedTopic.getTopicState());
		assertEquals("Archived topic activeOrder should be reset to -1.",-1, archivedTopic.getActiveOrder());
		
		List<Topic> activeTopicsAfter = lectioControl.findActiveTopicsAndLessonNotesByNotebook(notebookId);
		assertEquals("Number of active topics is wrong.", numTopics - 1, activeTopicsAfter.size());
		boolean isFound = false;

		Iterator<Topic> activeTopicsAfterIterator = activeTopicsAfter.iterator();
		for (Topic activeTopicAfter: activeTopicsAfter) {
			isFound = (activeTopicAfter.getId() == topicId);
			if (isFound) {
				break;
			}
		}
		assertFalse("New active topic list should not include archived topic.", isFound);
		
		// Make sure that all the other topics are appropriately ordered.
		int activeOrder = 0;
		for(Topic activeTopicAfter:activeTopicsAfter) {
			assertEquals("Active order was not renumbered.", activeOrder, activeTopicAfter.getActiveOrder()); 
			activeOrder++;
		}
	}
	
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

		Topic foundTopic = lectioControl.findTopicById( topicId);
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
		List<LessonNote> lessonNoteList = lectioControl.findLessonNotesByTopicId(topicId, numToFind, startingIndex);
		Assert.assertNotNull("Lesson note list should not be null.", lessonNoteList);
		Assert.assertEquals("Not enough lesson notes returned.",  numLessonNotes, lessonNoteList.size());

		// Make sure the very first lesson note is the one that topic is pointing to.
		Topic foundTopic = lectioControl.findTopicById( topicId);
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
