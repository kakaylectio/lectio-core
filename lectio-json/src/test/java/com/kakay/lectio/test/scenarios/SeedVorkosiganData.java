package com.kakay.lectio.test.scenarios;

import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Role;
import com.kktam.lectio.model.Studio;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

public class SeedVorkosiganData {

	public SeedVorkosiganData() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		SeedVorkosiganData vorkosiganData = new SeedVorkosiganData();
		try {
			vorkosiganData.seedData();
		} catch (LectioException e) {
			System.out.println("Error seeding Vorkosigan data.");
			e.printStackTrace();
		}

	}

	public void seedData() throws LectioException {
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		User aral = lectioControl.addNewUser(0, "Aral Vorkosigan", "aral@vorkosigan.com", "cordelia");
		int aralId = aral.getId();

		Studio studio = lectioControl.addNewStudio(aralId, "Imperial Service Academy");
		int studioId = studio.getId();

		User miles = lectioControl.addNewUser(0, "Miles Vorkosigan", "miles@dendarii.com", "naismith");
		int milesId = miles.getId();

		Notebook notebook = lectioControl.addNewNotebook(aralId, studioId, "Miles Vorkosigan");
		int notebookId = notebook.getId();

		lectioControl.addNewNotebookUser(aralId, notebookId, milesId, Role.student);

		Topic topic1 = lectioControl.addNewTopic(aralId, notebookId, "Mendelssohn Songs Without Words, Opus 19 No. 1");
		Topic topic2 = lectioControl.addNewTopic(aralId, notebookId, "Schubert Moment Musicaux Opus 39. no 3");

		lectioPersistence.close();
	}

}
