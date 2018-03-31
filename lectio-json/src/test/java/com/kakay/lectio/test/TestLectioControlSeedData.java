package com.kakay.lectio.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.User;

public class TestLectioControlSeedData {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		LectioPersistence.clearData("secret");
	}

	@Test
	public void test() throws LectioException {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(10,  0,  0,  0,  0,  0);
		
		int adminId = randomSeedData.getAdminId("secret");
		User teacher = randomSeedData.getTeacher();
		int teacherId = teacher.getId();
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl control = lectioPersistence.getLectioControlById();
		
		User teacherEntity = control.findUserById(adminId, teacherId);
		Assert.assertNotNull("User should have been added to database.", teacherEntity);
		
		User teacherEntityByName = control.findUserByExactName(adminId,  teacher.getName());
		Assert.assertNotNull("User should have been found by name. " + teacher.getName(), teacherEntityByName);
		
		User bogusTeacher = control.findUserByExactName(adminId, "bogusname");
		Assert.assertNull("Finding non-existent user should return a null user.", bogusTeacher);
		
		User teacherEntityByEmail = control.findUserByEmail(adminId,  teacher.getEmail());
		Assert.assertNotNull("User should have been found by name. " + teacher.getEmail(), teacherEntityByEmail);
		Assert.assertEquals("Wrong user found when searching by email.", teacher.getId(), teacherEntityByEmail.getId());
		
		// Try adding duplicate teacher.
		try {
			control.addNewUser(adminId,  teacher.getName(), "bogusemail", "boguspassword");
			Assert.fail("Duplicate user name should have caused an exception.");
		}
		catch(LectioConstraintException ex) {
			Assert.assertTrue("Exception for duplicate user name should mention the name.",
					ex.getMessage().contains(teacher.getName()));
		}
		catch(Exception ex) {
			Assert.fail("Wrong exception thrown when adding duplicate user name.");
		}

		// Try adding duplicate email.
		try {
			control.addNewUser(adminId,  "bogusname", teacher.getEmail(), "boguspassword");
			Assert.fail("Duplicate email entry should have caused an exception.");
		}
		catch(LectioConstraintException ex) {
			Assert.assertTrue("Exception for duplicate email=should mention the name.",
					ex.getMessage().contains(teacher.getEmail()));
		}
		catch(Exception ex) {
			Assert.fail("Wrong exception thrown when adding duplicate email.");
		}
		
	}

}
