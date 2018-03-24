package com.kktam.lectio.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kktam.lectio.control.LectioControlById;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioConstraintException;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.User;
import com.kktam.lectio.test.scenarios.SeedData;

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
		SeedData seedData = new SeedData();
		seedData.generateSeed(10,  0,  0,  0,  0,  0);
		
		int adminId = seedData.getAdminId("secret");
		User teacher = seedData.getTeacher();
		int teacherId = teacher.getId();
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControlById control = lectioPersistence.getLectioControlById();
		
		User teacherEntity = control.findUserById(adminId, teacherId);
		Assert.assertNotNull("User should have been added to database.", teacherEntity);
		
		User teacherEntityByName = control.findUserByExactName(adminId,  teacher.getName());
		Assert.assertNotNull("User should have been found by name. " + teacher.getName(), teacherEntityByName);
		
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
