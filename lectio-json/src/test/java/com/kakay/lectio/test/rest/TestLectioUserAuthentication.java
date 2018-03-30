package com.kakay.lectio.test.rest;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.kakay.lectio.auth.IdentityAuthentication;
import com.kakay.lectio.auth.LectioAuthorizer;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.User;

import io.dropwizard.testing.junit.ResourceTestRule;

public class TestLectioUserAuthentication {

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
    	.addResource(new NotebookActiveTopicsResource(new LectioPersistence().getLectioControlById()))
    	.addProvider(new LectioAuthorizer())
        .build();


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		LectioPersistence.clearData("secret");
	}

	
	@Test
	public void testUserLogin() {
		SeedData seedData = new SeedData();
		seedData.generateSeed(2, 1, 1, 1, 3, 3);
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();
		
		String teacherEmail = teacher.getEmail();
		int notebookId = notebook.getId();
		
		
	}

}
