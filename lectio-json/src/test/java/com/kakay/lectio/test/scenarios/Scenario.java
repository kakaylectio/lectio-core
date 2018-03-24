package com.kakay.lectio.test.scenarios;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;

import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Role;
import com.kktam.lectio.model.Studio;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

public class Scenario {
	protected Set<User> students;
	protected Map<User, Set<Studio>> teacherToStudioMap;
	protected Map<Studio, Set<Notebook>> studioToNotebookMap;
	protected Map<Notebook, Set<Topic>> notebookToTopicMap;
	protected Map<Notebook, Map<User, Role>> notebookToUserRoleMap;
	protected Map<Topic, Set<LessonNote>> topicToLessonNoteMap;
	
	protected Map<String, User> nameToUserMap;
	protected Map<String, Notebook> nameToNotebookMap;

	public Scenario() {
		teacherToStudioMap = (new HashMap<User, Set<Studio>>());
		studioToNotebookMap = (new HashMap<Studio, Set<Notebook>>());
		notebookToUserRoleMap = (new HashMap<Notebook, Map<User, Role>>());
		notebookToTopicMap = (new HashMap<Notebook, Set<Topic>>());
		topicToLessonNoteMap = (new HashMap<Topic, Set<LessonNote>>());
		nameToNotebookMap = new HashMap<String, Notebook>();
		nameToUserMap = new HashMap<String, User>();
		students = (new HashSet<User>());
	}
	
	public void generateData(){
		// No data.  Should be overridden.
	}


	public Set<User> getStudents() {
		return students;
	}

	public Map<User, Set<Studio>> getTeacherToStudioMap() {
		return teacherToStudioMap;
	}

	public Map<Studio, Set<Notebook>> getStudioToNotebookMap() {
		return studioToNotebookMap;
	}

	public Map<Notebook, Set<Topic>> getNotebookToTopicMap() {
		return notebookToTopicMap;
	}

	public Map<Notebook, Map<User, Role>> getNotebookToUserRoleMap() {
		return notebookToUserRoleMap;
	}

	public Map<Topic, Set<LessonNote>> getTopicToLessonNoteMap() {
		return topicToLessonNoteMap;
	}

	public Map<String, User> getNameToUserMap() {
		return nameToUserMap;
	}

	public Map<String, Notebook> getNameToNotebookMap() {
		return nameToNotebookMap;
	}

}