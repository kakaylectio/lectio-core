package com.kakay.lectio.test.scenarios;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;

import com.kakay.lectio.control.LectioControl;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.control.exception.LectioException;
import com.kakay.lectio.model.LessonNote;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.Role;
import com.kakay.lectio.model.Studio;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.model.User;

public class RandomSeedData implements SeedData {
	private final static Logger logger = Logger.getLogger(RandomSeedData.class.getName());

	User teacher;
	User student;
	Studio studio;
	Notebook notebook;
	Topic topic;
	LessonNote lessonNote;
	Map <String, String> emailToPasswordMap;

	int adminId;
	private static final int NUM_LESSONNOTE_LINES_MAX = 20;

	public RandomSeedData() {
		
	}

	public static void main(String[] args) {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(3, 1, 4, 1, 3, 5);
	}

	public void generateSeed(int numTeachers, int numStudios, int numNotebooks, int numStudents, int numTopics,
			int numLessonNotes) {
		emailToPasswordMap = new HashMap<String, String>(numTeachers + numStudios*numNotebooks*numStudents);
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		try {
			adminId = 0;

			for (int i = 0; i < numTeachers; i++) {
				User randomUser = createRandomUser();
				String randomPassword = createRandomPassword();
				emailToPasswordMap.put(randomUser.getEmail(),  randomPassword);

				teacher = lectioControl.addNewUser(randomUser.getName(), randomUser.getEmail(),
						randomPassword);

				for (int j = 0; j < numStudios; j++) {
					Studio randomStudio = createRandomStudio();
					studio = lectioControl.addNewStudio(teacher.getId(), randomStudio.getName());
					for (int k = 0; k < numNotebooks; k++) {
						Notebook randomNotebook = createRandomNotebook();
						notebook = lectioControl.addNewNotebook( studio.getId(),
								randomNotebook.getName());
						for (int p = 0; p < numStudents; p++) {
							User randomStudent = createRandomUser();
							randomPassword = createRandomPassword();
							emailToPasswordMap.put(randomStudent.getEmail(), randomPassword);
							student = lectioControl.addNewUser( randomStudent.getName(),
									randomStudent.getEmail(), randomPassword);
							lectioControl.addNewNotebookUser( notebook.getId(), student.getId(),
									Role.student);
						}
						for (int m = 0; m < numTopics; m++) {
							Topic randomTopic = createRandomTopic();
							topic = lectioControl.addNewTopic( notebook.getId(), randomTopic.getName());
							for (int n = 0; n < numLessonNotes; n++) {
								LessonNote randomLessonNote = createRandomLessonNote();
								lessonNote = lectioControl.addNewLessonNote(teacher.getId(), topic.getId(),
										randomLessonNote.getContent());

							}
						}
					}
				}
			}
			lectioPersistence.close();
		} catch (LectioException ex) {
			logger.error("Exception while generating seed.", ex);
			lectioPersistence.close();
		}

	}
	
	/**
	 * Adds some more lesson notes to the generated seed data, setting
	 * each lesson date one week apart.  The very last lesson note to be added
	 * because the lesson note returned by getLessonNote().
	 * 
	 * @param numExtraLessonNotes
	 * @throws Exception
	 */
	public void addExtraLessonNotes(int numExtraLessonNotes) throws Exception {
		LectioPersistence lectioPersistence = new LectioPersistence();
		LectioControl lectioControl = lectioPersistence.getLectioControlById();
		int topicId = topic.getId();
		if (lessonNote != null) {

			lectioControl.updateLessonNoteDate(lessonNote.getId(), LocalDateTime.now().minusWeeks(numExtraLessonNotes+1));
		}
		
		for (int i=0; i< numExtraLessonNotes; i++) {
			LessonNote newLessonNote = createRandomLessonNote();
			LessonNote lessonNoteDao = lectioControl.addNewLessonNote(teacher.getId(), topicId, newLessonNote.getContent());
			lectioControl.updateLessonNoteDate(lessonNoteDao.getId(), LocalDateTime.now().minusWeeks(numExtraLessonNotes - i));
			lessonNote = lessonNoteDao;
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
		user.setName(userName);
		user.setEmail(email);
		return user;
	}

	String createRandomPassword() {
		String password = RandomStringUtils.randomGraph(6, 15);
		return password;
	}

	/* (non-Javadoc)
	 * @see com.kakay.lectio.test.scenarios.SeedData#getTeacher()
	 */
	@Override
	public User getTeacher() {
		return teacher;
	}

	/* (non-Javadoc)
	 * @see com.kakay.lectio.test.scenarios.SeedData#getStudent()
	 */
	@Override
	public User getStudent() {
		return student;
	}

	/* (non-Javadoc)
	 * @see com.kakay.lectio.test.scenarios.SeedData#getStudio()
	 */
	@Override
	public Studio getStudio() {
		return studio;
	}

	/* (non-Javadoc)
	 * @see com.kakay.lectio.test.scenarios.SeedData#getNotebook()
	 */
	@Override
	public Notebook getNotebook() {
		return notebook;
	}

	/* (non-Javadoc)
	 * @see com.kakay.lectio.test.scenarios.SeedData#getTopic()
	 */
	@Override
	public Topic getTopic() {
		return topic;
	}

	/* (non-Javadoc)
	 * @see com.kakay.lectio.test.scenarios.SeedData#getLessonNote()
	 */
	@Override
	public LessonNote getLessonNote() {
		return lessonNote;
	}

	public int getAdminId(String secret) {
		// Check for secret here
		return adminId;
	}

	/* (non-Javadoc)
	 * @see com.kakay.lectio.test.scenarios.SeedData#getEmailToPasswordMap()
	 */
	@Override
	public Map<String, String> getEmailToPasswordMap() {
		return emailToPasswordMap;
	}

}
