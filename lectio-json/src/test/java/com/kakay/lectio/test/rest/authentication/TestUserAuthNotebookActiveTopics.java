package com.kakay.lectio.test.rest.authentication;

import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.model.Notebook;

import io.dropwizard.jackson.Jackson;

public class TestUserAuthNotebookActiveTopics extends TestUserAuth{

	protected String getTargetString() {
		int notebookId = notebook.getId();

		String targetString = "/lectio/notebook/" + notebookId + "/activetopics";
		return targetString;
	}
	
	@Override
	protected Object getResource() {
		NotebookActiveTopicsResource notebookActiveTopicResource = new NotebookActiveTopicsResource();
		notebookActiveTopicResource.setLectioControl(new LectioPersistence().getLectioControlById());

		Object resource = notebookActiveTopicResource;
		return resource;
	}

	@Override
	protected SeedData getSeedData() {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 1, 0, 0, 0);

		return randomSeedData;

	}


}
