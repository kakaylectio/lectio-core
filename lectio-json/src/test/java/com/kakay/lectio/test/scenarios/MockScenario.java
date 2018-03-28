package com.kakay.lectio.test.scenarios;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Role;
import com.kktam.lectio.model.Studio;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.TopicState;
import com.kktam.lectio.model.User;

public class MockScenario extends Scenario{


	private static final int NUM_LESSONNOTE_LINES_MAX = 3;
	private static final int NUM_LESSON_NOTES_MAX = 5;
	private static final int NUM_TOPICS_MAX = 5;
	private static final int NUM_KIDS_MAX = 3;
	private static final int NUM_PARENTS_MAX = 1;
	private static final int NUM_FAMILIES_MAX = 5;
	private static final int NUM_TEACHERS_MAX = 3;
	private static final int NUM_TEACHERS_MIN = 2;
	private static final int NUM_STUDIOS_MAX = 1;
	private static final int NUM_STUDIOS_MIN = 1;

//	private static final int NUM_LESSONNOTE_LINES_MAX = 10;
//	private static final int NUM_LESSON_NOTES_MAX = 50;
//	private static final int NUM_KIDS_MAX = 3;
//	private static final int NUM_PARENTS_MAX = 2;
//	private static final int NUM_FAMILIES_MAX = 20;
//	private static final int NUM_TEACHERS_MAX = 20;
//	private static final int NUM_TEACHERS_MIN = 10;

	public MockScenario() {

	}

	@Override
	public void generateData() {
		generateRandomData();
	}

	protected void generateRandomData() {
		int numTeachers = RandomUtils.nextInt(NUM_TEACHERS_MIN, NUM_TEACHERS_MAX);

		for (int i = 0; i < numTeachers; i++) {
			User teacher = createRandomUser();
			nameToUserMap.put(teacher.getName(), teacher);
			setupStudios(teacher);
		}
		
	}

	void setupStudios(User teacher) {
		Set<Studio> studios = new HashSet<Studio>();
		int numStudios = RandomUtils.nextInt(NUM_STUDIOS_MIN, NUM_STUDIOS_MAX);
		for (int j = 0; j < numStudios + 1; j++) 
		{
			Studio studio = createRandomStudio();
			studios.add(studio);
			studio.setOwner(teacher);
			setupNotebooks(studio);
		}
		teacherToStudioMap.put(teacher, studios);
	}

	void setupNotebooks(Studio studio) {

		Set<Notebook> notebooks = new HashSet<Notebook>();
			int numFamilies = RandomUtils.nextInt(1, NUM_FAMILIES_MAX);
			for (int j=0; j<numFamilies; j++) {
				int numParentsInFamily = RandomUtils.nextInt(0,NUM_PARENTS_MAX);
				Set<User> parentsInFamily = new HashSet<User>(numParentsInFamily);
				for (int i=0; i<numParentsInFamily; i++) {
					parentsInFamily.add(createRandomUser());
				}
				int minKids = (numParentsInFamily==0? 1:0);
				int kidsInFamily = RandomUtils.nextInt(minKids,NUM_KIDS_MAX);
				for (int k=0; k<kidsInFamily; k++) {
					
					int numNotebooks = 1;
					
					for (int i = 0; i < numNotebooks; i++) {
						Notebook notebook = createRandomNotebook();
						notebook.setStudio(studio);
						Map<User,Role> mapUserToRole = new HashMap<User,Role>();
						User student = createRandomUser();
						students.add(student);
						mapUserToRole.put(student, Role.student);
						for (User parent:parentsInFamily) {
							mapUserToRole.put(parent, Role.parent);
						}
					notebookToUserRoleMap.put(notebook, mapUserToRole);
					setupTopics(notebook);
					nameToNotebookMap.put(studio.getName() + "." + notebook.getName(), notebook);
					notebooks.add(notebook);
				}
			}
		}
		studioToNotebookMap.put(studio, notebooks);
	}
	
	void setupTopics(Notebook notebook) {
		int numTopics = RandomUtils.nextInt(0, NUM_TOPICS_MAX);
		int numActiveTopics = RandomUtils.nextInt(0, Math.min(numTopics,  8));
		Set<Topic> topics = new HashSet<Topic>(numTopics);
		for (int i=0; i<numTopics; i++) {
			Topic topic = createRandomTopic();
			if (i<numActiveTopics) {
				topic.setTopicState(TopicState.active);
			}
			else {
				topic.setTopicState(TopicState.archived);
			}
			topics.add(topic);
			setupLessonNotes(topic);
		}
		notebookToTopicMap.put(notebook, topics);
		
	}
	
	void setupLessonNotes(Topic topic) {
		int numLessonNotes = RandomUtils.nextInt(1, NUM_LESSON_NOTES_MAX);
		Set<LessonNote> lessonNotes = new HashSet<LessonNote>(numLessonNotes);
		for (int i=0; i<numLessonNotes; i++) {
			LessonNote lessonNote = createRandomLessonNote();
			lessonNote.setTopic(topic);
			lessonNotes.add(lessonNote);
		}
		topicToLessonNoteMap.put(topic, lessonNotes);
	}
	

	User createRandomUser() {
		User user = new User();
		String userName = RandomStringUtils.randomAlphabetic(3, 15);
		String email = createRandomEmail();
		String password = RandomStringUtils.randomGraph(6, 15);
		user.setName(userName);
		;
		user.setEmail(email);
		;
		return user;
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

	String createRandomStringOfWords(int minWords, int maxWords) {
		int numWords = RandomUtils.nextInt(minWords, maxWords);
		String words = RandomStringUtils.randomAlphanumeric(3, 8);
		for (int i = 0; i < numWords; i++) {
			words += " ";
			words += RandomStringUtils.randomAlphanumeric(3, 8);
		}
		return words;
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

	Studio createRandomStudio() {
		Studio studio = new Studio();
		studio.setName(createRandomStringOfWords(1, 4));
		return studio;
	}

	Notebook createRandomNotebook() {
		Notebook notebook = new Notebook();
		notebook.setName(createRandomStringOfWords(1, 3));
		return notebook;
	}

	LessonNote createRandomLessonNote() {
		LessonNote lessonNote = new LessonNote();
		lessonNote.setContent(createRandomLinesOfWords(1, NUM_LESSONNOTE_LINES_MAX));
		return lessonNote;
	}

	Topic createRandomTopic() {
		Topic topic = new Topic();
		topic.setName(createRandomStringOfWords(1,8));
		return topic;
	}
}
