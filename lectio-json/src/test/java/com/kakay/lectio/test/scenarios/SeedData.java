package com.kakay.lectio.test.scenarios;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
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
/* This class creates some seed data, and can return one instance of the data. */

public class SeedData {
	private final static Logger logger = Logger.getLogger(SeedData.class.getName());

	User teacher;
	User student;
	Studio studio;
	Notebook notebook;
	Topic topic;
	LessonNote lessonNote;

	int adminId;
	private static final int NUM_LESSONNOTE_LINES_MAX = 20;

	public SeedData() {

	}

	public void generateSeed(int numTeachers, int numStudios, int numNotebooks, int numStudents, int numTopics, int numLessonNotes) {
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		try {
			adminId = lectioControl.addRootAdmin("secret");
			
			for (int i=0; i<numTeachers; i++) {
				User randomUser = createRandomUser();
				teacher = lectioControl.addNewUser(adminId, randomUser.getName(),
						randomUser.getEmail(), randomUser.getPassword());

				for (int j=0; j<numStudios; j++) {
					Studio randomStudio = createRandomStudio();
					studio = lectioControl.addNewStudio(teacher.getId(), randomStudio.getName());
					for (int k=0; k<numNotebooks; k++) {
						Notebook randomNotebook = createRandomNotebook();
						notebook = lectioControl.addNewNotebook(teacher.getId(), studio.getId(), randomNotebook.getName());
						for (int p=0; p<numStudents; p++) {
							User randomStudent = createRandomUser();
							student = lectioControl.addNewUser(adminId,  randomStudent.getName(),
									randomStudent.getEmail(), randomStudent.getPassword());
							lectioControl.addNewNotebookUser(teacher.getId(),  notebook.getId(), student.getId(),  Role.student);
						}
						for (int m=0; m<numTopics; m++) {
							Topic randomTopic = createRandomTopic();
							topic = lectioControl.addNewTopic(teacher.getId(), notebook.getId(), randomTopic.getName());
							for (int n=0; n<numLessonNotes; n++) {
								LessonNote randomLessonNote = createRandomLessonNote();
								lessonNote = lectioControl.addNewLessonNote(teacher.getId(), topic.getId(), randomLessonNote.getContent());

							}
						}
					}	
				}
			}
			lectioPersistence.close();
		}
		catch(LectioException ex) {
			logger.error("Exception while generating seed.", ex);
			lectioPersistence.close();
		}
	
	}

	String createRandomEmail() {
		String email = RandomStringUtils.randomAlphabetic(1) + RandomStringUtils.randomAlphanumeric(3, 10);
		boolean withDot = RandomUtils.nextBoolean();
		if (withDot) {
			email += "." + RandomStringUtils.randomAlphanumeric(3, 10);
		}
		email += "@";
		email += RandomStringUtils.randomAlphabetic(1) + RandomStringUtils.randomAlphanumeric(3, 10);
		email += ".";
		String suffix;
		switch (RandomUtils.nextInt(0, 3)) {
		case 0:
			suffix = "com";
			break;
		case 1:
			suffix = "net";
			break;
		case 2:
			suffix = "org";
			break;
		case 3:
		default:
			suffix = "gov";
		}
		email += suffix;

		return email;
	}

	LessonNote createRandomLessonNote() {
		LessonNote lessonNote = new LessonNote();
		lessonNote.setContent(createRandomLinesOfWords(1, NUM_LESSONNOTE_LINES_MAX));
		return lessonNote;
	}

	String createRandomLinesOfWords(int minLines, int maxLines) {
		int numLines = RandomUtils.nextInt(1, 15);
		String lines = createRandomStringOfWords(1, 10);
		for (int i = 0; i < numLines; i++) {
			lines += "\n";
			lines += createRandomStringOfWords(1, 10);
		}
		return lines;
	}

	Notebook createRandomNotebook() {
		Notebook notebook = new Notebook();
		notebook.setName(createRandomStringOfWords(1, 3));
		return notebook;
	}

	String createRandomStringOfWords(int minWords, int maxWords) {
		int numWords = RandomUtils.nextInt(minWords, maxWords);
		String words = RandomStringUtils.randomAlphanumeric(3, 8);
		for (int i = 0; i < numWords; i++) {
			words += " ";
			words += RandomStringUtils.randomAlphanumeric(3, 8);
		}
		return words;
	}

	Studio createRandomStudio() {
		Studio studio = new Studio();
		studio.setName(createRandomStringOfWords(1, 4));
		return studio;
	}

	Topic createRandomTopic() {
		Topic topic = new Topic();
		topic.setName(createRandomStringOfWords(1, 8));
		return topic;
	}

	User createRandomUser() {
		User user = new User();
		String userName = RandomStringUtils.randomAlphabetic(3, 15);
		String email = createRandomEmail();
		String password = RandomStringUtils.randomGraph(6, 15);
		user.setName(userName);
		;
		user.setEmail(email);
		user.setPassword(password);
		;
		return user;
	}

	public User getTeacher() {
		return teacher;
	}

	public User getStudent() {
		return student;
	}

	public Studio getStudio() {
		return studio;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public Topic getTopic() {
		return topic;
	}

	public LessonNote getLessonNote() {
		return lessonNote;
	}

	public int getAdminId(String secret) {
		// Check for secret here
		return adminId;
	}

}
