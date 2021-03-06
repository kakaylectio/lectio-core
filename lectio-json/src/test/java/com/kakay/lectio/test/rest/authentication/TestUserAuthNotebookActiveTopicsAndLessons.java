package com.kakay.lectio.test.rest.authentication;

import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.rest.resources.NotebookResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;

public class TestUserAuthNotebookActiveTopicsAndLessons extends TestUserAuth {

	@Override
	protected String getTargetString() {
		int notebookId = notebook.getId();
		String targetString = "/lectio/notebook/" + notebookId + "/activetopics/withlessons";
		return targetString;
	}

	@Override
	protected SeedData getSeedData() {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1, 1, 1, 0, 1, 0);

		return randomSeedData;

	}

}
