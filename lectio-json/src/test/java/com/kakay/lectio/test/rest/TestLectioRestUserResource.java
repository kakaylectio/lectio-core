package com.kakay.lectio.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.UserResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.User;

import io.dropwizard.jackson.Jackson;

public class TestLectioRestUserResource extends TestRestResources {

	static final int numNotebooks = 5;
	

	@Override
	protected SeedData getSeedData() {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, numNotebooks, 0, 1, 0);
		return randomSeedData;
	}

	@Override
	protected Object getResource() {
		LectioPersistence persistence = new LectioPersistence();
		return new UserResource(persistence.getLectioControlById());
	}
    @Test
    
    public void testUserNotebooksRest() throws IOException {
		
    	User teacher = seedData.getTeacher();

		int teacherId = teacher.getId();
		String targetString = "/lectio/user/" + teacherId + "/notebooks";
		
		String resp = hitEndpoint(targetString);
		ObjectMapper objectMapper = Jackson.newObjectMapper();
		NotebookRep[] notebookRepList = objectMapper.readValue(resp, NotebookRep[].class);

		assertNotNull("Notebook array returned should not be null.", notebookRepList);
		assertEquals("Wrong number of notebooks returned.", numNotebooks, notebookRepList.length);
		
		for (NotebookRep notebookRep : notebookRepList) {
			assertNull("Notebook should not have topic information.", notebookRep.getTopicList());
			assertNotNull("NotebookRep should have notebook information.", notebookRep.getNotebook());
			assertNotNull("Notebooks should have names in them.", notebookRep.getNotebook().getName());
			assertNotNull("User role in notebook should not be null.", notebookRep.getUserRole());
		}
    
    }
}
