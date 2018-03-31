package com.kakay.lectio.test.rest.authentication;

import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kktam.lectio.control.LectioPersistence;

public class TestUserAuthNotebookActiveTopicsAndLessons extends TestUserAuth {

	@Override
	protected String getTargetString() {
		int notebookId = notebook.getId();
		String targetString = "/lectio/notebook/" + notebookId + "/activetopics/withlessons";
		return targetString;
	}

	@Override
	protected Object getResource() {
		NotebookActiveTopicsResource notebookActiveTopicResource = new NotebookActiveTopicsResource();
		notebookActiveTopicResource.setLectioControl(new LectioPersistence().getLectioControlById());

		Object resource = notebookActiveTopicResource;
		return resource;
	}

}
