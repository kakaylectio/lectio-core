package com.kakay.lectio.test;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.control.exception.LectioConstraintException;
import com.kakay.lectio.control.exception.LectioException;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.model.TopicState;
import com.kakay.lectio.model.User;
import com.kakay.lectio.test.scenarios.ClearData;
import com.kakay.lectio.test.scenarios.RandomSeedData;

public class TestLectioControlNotebooks  {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		ClearData.main(new String[]{});
	}

	@Test
	public void testDuplicateNotebook() throws LectioException {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 1, 0, 0, 0);

		User teacher = randomSeedData.getTeacher();
		Notebook notebook = randomSeedData.getNotebook();

		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		try {
			lectioControl.addNewNotebook( randomSeedData.getStudio().getId(), notebook.getName());
			Assert.fail("Adding duplicate notebook to same studio should fail.");
		} catch (LectioConstraintException ex) {
			Assert.assertTrue("Adding duplicate notebook name exception should state the duplicate name.",
					ex.getMessage().contains(notebook.getName()));
		}
	}

	@Test
	public void testNotebookData() throws Exception {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 5, 1, 0, 0);

		User teacher = randomSeedData.getTeacher();
		Notebook notebook = randomSeedData.getNotebook();

		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
	

		List<Notebook> notebooksByTeacher = lectioControl.findNotebooksByUser(teacher.getId());
		boolean notebookFound = false;
		String lastNotebookName = " ";
//		Collator mysqlRuleCollator = new RuleBasedCollator("< ' ' < A < a < B < b < C < c < D < d < E < e < F < f < G < g < H < h < I < i < J < j < K < k < L < l < M < m < N < n < O < o < P < p < Q < q < R < r < S < s < T < t < U < u < V < v < W < w < X < x < Y < y < Z < z");
		Collator mysqlRuleCollator = Collator.getInstance(Locale.US);
		mysqlRuleCollator.setStrength(Collator.TERTIARY);
		for (Notebook notebookitem : notebooksByTeacher) {
			if (notebookitem.getId() == notebook.getId() && notebookitem.getName().equals(notebook.getName())) {
				notebookFound = true;
			}
			Assert.assertTrue(
					"Notebooks returned by findNotebooksByUser needs to be in alphabetical order."
							+ notebookitem.getName() + " should be before " + lastNotebookName,
							mysqlRuleCollator.compare(lastNotebookName, notebookitem.getName()) < 0);
			lastNotebookName = notebookitem.getName();
		}
		Assert.assertTrue("Notebook needs to be in list returned by findNotebooksByUser.", notebookFound);

		User student = randomSeedData.getStudent();
		List<Notebook> notebooksByStudent = lectioControl.findNotebooksByUser(student.getId());
		boolean studentNotebookFound = false;
		String lastStudentNotebookName = "";
		for (Notebook notebookitem : notebooksByStudent) {
			if (notebookitem.getId() == notebook.getId() && notebookitem.getName().equals(notebook.getName())) {
				studentNotebookFound = true;
			}
			Assert.assertTrue(
					"Notebooks returned by findNotebooksByUser for student needs to be in alphabetical order."
							+ notebookitem.getName() + " should be before " + lastStudentNotebookName,
					notebookitem.getName().compareToIgnoreCase(lastStudentNotebookName) > 0);
			lastStudentNotebookName = notebookitem.getName();
		}
		Assert.assertTrue("Notebook needs to be in list returned by findNotebooksByUser.", studentNotebookFound);

	}

	@Test
	public void testDuplicateTopic() throws LectioException {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 1, 0, 1, 0);

		User teacher = randomSeedData.getTeacher();
		Notebook notebook = randomSeedData.getNotebook();

		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		try {
			lectioControl.addNewTopic( notebook.getId(), randomSeedData.getTopic().getName());
			Assert.fail("Adding duplicate topic in same notebook should fail.");
		} catch (LectioConstraintException ex) {
			Assert.assertTrue("Exception thrown when adding duplicate topic should show topic name.",
					ex.getMessage().contains(randomSeedData.getTopic().getName()));
		}

		// Adding duplicate topic to different notebook should succeed.
		Notebook newNotebook = lectioControl.addNewNotebook(randomSeedData.getStudio().getId(),
				"New Notebook");
		Topic newTopic = lectioControl.addNewTopic(newNotebook.getId(),
				randomSeedData.getTopic().getName());
		Assert.assertNotNull("Adding duplicate topic name to another notebook should succeed.", newTopic);

	}

	@Test
	public void testNotebookTopics() throws LectioException {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 1, 1, 5, 0);

		User teacher = randomSeedData.getTeacher();
		Notebook notebook = randomSeedData.getNotebook();
		User student = randomSeedData.getStudent();

		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		// Student should be able find active topics.
		List<Topic> activeTopicList = lectioControl.findActiveTopicsByNotebook( notebook.getId());
		// Make sure topics are all active and in active order.
		int lastActiveOrder = -1;
		for (Topic topic : activeTopicList) {
			Assert.assertEquals("Topic should be active to be on the list.", TopicState.active, topic.getTopicState());
			Assert.assertTrue("Topics returned by findActiveTopicsByNotebook should be in active order.",
					topic.getActiveOrder() > lastActiveOrder);
			lastActiveOrder = topic.getActiveOrder();
		}

		// When you add a new topic, it should become the first one in the active order.
		Topic newTopic = lectioControl.addNewTopic(notebook.getId(), "Latest Topic");
		List<Topic> newActiveTopicList = lectioControl.findActiveTopicsByNotebook( notebook.getId());
		Assert.assertEquals("After adding new topic, active topic list count should be one more than before",
				activeTopicList.size() + 1, newActiveTopicList.size());
		Assert.assertEquals("Newest topic should be at top of list.", newTopic.getId(),
				newActiveTopicList.get(0).getId());
		// Make sure the rest of the list stays in the same order.
		for (int i = 0; i < activeTopicList.size(); i++) {
			Assert.assertEquals("After adding new topic, the rest of the Topic list should stay in the same order.",
					activeTopicList.get(i).getId(), newActiveTopicList.get(i + 1).getId());

		}
	}


}
