package com.kakay.lectio.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Test;

import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.control.exception.LectioConstraintException;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.Studio;
import com.kakay.lectio.model.User;
import com.kakay.lectio.test.scenarios.ClearData;
import com.kakay.lectio.test.scenarios.RandomSeedData;

/**
 * Test how MySql database alphabetizes names
 */
public class TestAlphabet {

	@Test
	public void testUsernameCase() throws Exception {
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		String username1 = "MyFullName";
		String username2 = "myfullname";
		
		User user1 = lectioControl.addNewUser(username1,  "email@company.com",  "password");
		try {
			lectioControl.addNewUser(username2, "otheremail@company.com", "password");
			fail("Adding new user name with duplicate case insensitive name should fail.");
		}
		catch(LectioConstraintException ex) {
			assertTrue("Constraint should be called out.", true);
		}
		
		String email1 = "myemail@company.com";
		String email2 = "MyEmail@Company.com";
		
		User user3 = lectioControl.addNewUser("some name", email1, "password");
		try {
			User user4 = lectioControl.addNewUser("another name", email2, "password");
			fail("Adding new email with duplicate case insentive email should fail.");
		}
		catch(LectioConstraintException ex) {
			assertTrue("Constraint should be called out.", true);
		}
	}
	
	
	
	
	@Test
	public void testAlphabet() throws Exception{
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 0, 0, 0, 0);
		Studio studio = randomSeedData.getStudio();

		Collator mysqlRuleCollator = new RuleBasedCollator("< ' ' < A < a < B < b < C < c < D < d < E < e < F < f < G < g < H < h < I < i < J < j < K < k < L < l < M < m < N < n < O < o < P < p < Q < q < R < r < S < s < T < t < U < u < V < v < W < w < X < x < Y < y < Z < z");
		mysqlRuleCollator.setStrength(Collator.IDENTICAL);
				
		
		List<Notebook> notebooks = lectioControl.findNotebooksByUser(randomSeedData.getTeacher().getId());
		
		String previousNotebookName = "AAA";
		for (Notebook notebook : notebooks) {
			System.out.println("Notebook is " + notebook.getName());
			String notebookName = notebook.getName();
			assertTrue("Alphabetical order problem:  " + previousNotebookName + " " + notebookName,
					mysqlRuleCollator.compare(previousNotebookName, notebookName) < 0);
			previousNotebookName = notebookName;
			
		}
		
		
	}
	
	@After
	public void tearDown() throws Exception {
		ClearData.main(new String[]{});
	}
}
