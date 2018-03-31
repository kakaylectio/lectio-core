package com.kakay.lectio.test.scenarios;

import java.util.Map;

import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Studio;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

public interface SeedData {

	User getTeacher();

	User getStudent();

	Studio getStudio();

	Notebook getNotebook();

	Topic getTopic();

	LessonNote getLessonNote();

	Map<String, String> getEmailToPasswordMap();

}