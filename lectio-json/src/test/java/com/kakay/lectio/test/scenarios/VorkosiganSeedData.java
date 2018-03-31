package com.kakay.lectio.test.scenarios;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.kktam.lectio.control.LectioControl;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.control.exception.LectioException;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Role;
import com.kktam.lectio.model.Studio;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

public class VorkosiganSeedData implements SeedData {
	private final static Logger logger = Logger.getLogger(VorkosiganSeedData.class.getName());

	User aral;
	Map<String, String> emailToPasswordMap = new HashMap<String, String>(2);
	Studio studio;
	Notebook notebook;
	User miles;
	Topic topic;
	LessonNote lessonNote;

	public VorkosiganSeedData() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		VorkosiganSeedData vorkosiganData = new VorkosiganSeedData();
		vorkosiganData.seedData();

	}

	public void seedData() {
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();

		try {
			aral = lectioControl.addNewUser(0, "Aral Vorkosigan", "aral@vorkosigan.com", "cordelia");
			emailToPasswordMap.put("aral@vorkosigan.com", "cordelia");
			int aralId = aral.getId();

			studio = lectioControl.addNewStudio(aralId, "Imperial Service Academy");
			int studioId = studio.getId();

			User miles = lectioControl.addNewUser(0, "Miles Vorkosigan", "miles@dendarii.com", "naismith");
			emailToPasswordMap.put("miles@dendarii.com", "naismith");
			int milesId = miles.getId();

			notebook = lectioControl.addNewNotebook(aralId, studioId, "Miles Vorkosigan");
			int notebookId = notebook.getId();

			lectioControl.addNewNotebookUser(aralId, notebookId, milesId, Role.student);

			topic = lectioControl.addNewTopic(aralId, notebookId, "Mendelssohn Songs Without Words, Opus 19 No. 1");
			Topic topic2 = lectioControl.addNewTopic(aralId, notebookId, "Schubert Moment Musicaux Opus 39. no 3");
		} catch (LectioException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lectioPersistence.close();
	}

	public User getTeacher() {
		return aral;
	}

	public Map<String, String> getEmailToPasswordMap() {
		return emailToPasswordMap;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public User getStudent() {
		return miles;
	}

	@Override
	public Studio getStudio() {
		// TODO Auto-generated method stub
		return studio;
	}

	@Override
	public Topic getTopic() {
		// TODO Auto-generated method stub
		return topic;
	}

	@Override
	public LessonNote getLessonNote() {
		// TODO Auto-generated method stub
		return lessonNote;
	}

}
