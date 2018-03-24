package com.kktam.lectio.test;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kktam.lectio.control.LectioControlById;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.TopicState;
import com.kktam.lectio.model.User;
import com.kktam.lectio.test.scenarios.SeedData;




public class TestLectioControlNotebooks {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		LectioPersistence.clearData("secret");
	}

	@Test
	public void testDuplicateNotebook() throws LectioException {
		SeedData seedData = new SeedData();
		seedData.generateSeed(1 ,1, 1, 0, 0, 0);
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControlById lectioControl = lectioPersistence.getLectioControlById();
		
		try {
			lectioControl.addNewNotebook(teacher.getId(), seedData.getStudio().getId(), notebook.getName());
			Assert.fail("Adding duplicate notebook to same studio should fail.");
		}
		catch(LectioConstraintException ex) {
			Assert.assertTrue("Adding duplicate notebook name exception should state the duplicate name.",
					ex.getMessage().contains(notebook.getName()));
		}
	}
	
	@Test
	public void testNotebookData() throws LectioException{
		SeedData seedData = new SeedData();
		seedData.generateSeed(1 ,1, 5, 1, 0, 0);
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControlById lectioControl = lectioPersistence.getLectioControlById();

		
		List<Notebook> notebooksByTeacher = lectioControl.findNotebooksByUser(teacher.getId());
		boolean notebookFound = false;
		String lastNotebookName = "";
		for (Notebook notebookitem:notebooksByTeacher) {
			if (notebookitem.getId() == notebook.getId() && notebookitem.getName().equals(notebook.getName())) {
				notebookFound = true;
			}
			Assert.assertTrue("Notebooks returned by findNotebooksByUser needs to be in alphabetical order." 
					+ notebookitem.getName() + " should be before " + lastNotebookName,
					notebookitem.getName().compareToIgnoreCase(lastNotebookName) > 0);
			lastNotebookName = notebookitem.getName();
		}
		Assert.assertTrue("Notebook needs to be in list returned by findNotebooksByUser.",
				notebookFound);

		User student = seedData.getStudent();
		List<Notebook> notebooksByStudent = lectioControl.findNotebooksByUser(student.getId());
		boolean studentNotebookFound = false;
		String lastStudentNotebookName = "";
		for (Notebook notebookitem:notebooksByStudent) {
			if (notebookitem.getId() == notebook.getId() && notebookitem.getName().equals(notebook.getName())) {
				studentNotebookFound = true;
			}
			Assert.assertTrue("Notebooks returned by findNotebooksByUser for student needs to be in alphabetical order." +
			  notebookitem.getName() + " should be before " + lastStudentNotebookName,
					notebookitem.getName().compareToIgnoreCase(lastStudentNotebookName) > 0);
			lastStudentNotebookName = notebookitem.getName();
		}
		Assert.assertTrue("Notebook needs to be in list returned by findNotebooksByUser.",
				studentNotebookFound);

		
		
	}
	
	@Test
	public void testDuplicateTopic() throws LectioException {
		SeedData seedData = new SeedData();
		seedData.generateSeed(1 ,1, 1, 0, 1, 0);
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();

		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControlById lectioControl = lectioPersistence.getLectioControlById();

		try {
			lectioControl.addNewTopic(teacher.getId(), notebook.getId(), seedData.getTopic().getName());
			Assert.fail("Adding duplicate topic in same notebook should fail.");
		}
		catch(LectioConstraintException ex) {
			Assert.assertTrue("Exception thrown when adding duplicate topic should show topic name.",
					ex.getMessage().contains(seedData.getTopic().getName()));
		}
		
		// Adding duplicate topic to different notebook should succeed.
		Notebook newNotebook = lectioControl.addNewNotebook(teacher.getId(), seedData.getStudio().getId(), "New Notebook");
		Topic newTopic = lectioControl.addNewTopic(teacher.getId(), newNotebook.getId(), seedData.getTopic().getName());
		Assert.assertNotNull("Adding duplicate topic name to another notebook should succeed.",
				newTopic);
		
	}
	
	@Test 
	public void testNotebookTopics() throws LectioException {
		SeedData seedData = new SeedData();
		seedData.generateSeed(1 ,1, 1, 1, 5, 0);
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		User student = seedData.getStudent();

		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControlById lectioControl = lectioPersistence.getLectioControlById();
		
		List<Topic> activeTopicList = lectioControl.findActiveTopicsByNotebook(student.getId(), notebook.getId());
		// Make sure topics are all active and in active order.
		int lastActiveOrder = -1;
		for (Topic topic: activeTopicList) {
			Assert.assertEquals("Topic should be active to be on the list.",
					TopicState.active, topic.getTopicState());
			Assert.assertTrue("Topics returned by findActiveTopicsByNotebook should be in active order.",
					topic.getActiveOrder() > lastActiveOrder);
			lastActiveOrder = topic.getActiveOrder();
		}
		
		//When you add a new topic, it should become the first one in the active order.
		Topic newTopic = lectioControl.addNewTopic(teacher.getId(), notebook.getId(), "Latest Topic");
		List<Topic> newActiveTopicList = lectioControl.findActiveTopicsByNotebook(student.getId(), notebook.getId());
		Assert.assertEquals("After adding new topic, active topic list count should be one more than before",
				activeTopicList.size() + 1, newActiveTopicList.size());
		Assert.assertEquals("Newest topic should be at top of list.",
				newTopic.getId(), newActiveTopicList.get(0).getId());
		//Make sure the rest of the list stays in the same order.
		for (int i=0; i<activeTopicList.size(); i++) {
			Assert.assertEquals("After adding new topic, the rest of the Topic list should stay in the same order.",
					activeTopicList.get(i).getId(), newActiveTopicList.get(i+1).getId());
			
		}
	}

}
