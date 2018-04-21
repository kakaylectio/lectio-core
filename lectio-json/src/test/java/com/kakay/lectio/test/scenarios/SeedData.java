package com.kakay.lectio.test.scenarios;

import java.util.Map;

import com.kakay.lectio.model.LessonNote;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.Studio;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.model.User;

public interface SeedData {

	User getTeacher();

	User getStudent();

	Studio getStudio();

	Notebook getNotebook();

	Topic getTopic();

	LessonNote getLessonNote();

	Map<String, String> getEmailToPasswordMap();

}