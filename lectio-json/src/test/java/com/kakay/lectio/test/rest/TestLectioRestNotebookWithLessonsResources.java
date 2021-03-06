package com.kakay.lectio.test.rest;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.control.LectioPersistence;
import com.kakay.lectio.model.LessonNote;
import com.kakay.lectio.model.Role;
import com.kakay.lectio.model.Topic;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.NotebookResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;

import io.dropwizard.jackson.Jackson;

public class TestLectioRestNotebookWithLessonsResources extends TestRestResources{

	int numTopics = 6;
	int numLessonNotes = 10;

    @Test
    public void testNotebookActiveTopicsLessonNotesJsonRest() throws IOException {
		

		int notebookId = notebook.getId();
		String targetString = "/lectio/notebook/" + notebookId + "/activetopics/withlessons";
		
		String resp = hitEndpoint(targetString);
		ObjectMapper objectMapper = Jackson.newObjectMapper();
		NotebookRep notebookRep = objectMapper.readValue(resp, NotebookRep.class);
        Assert.assertNotNull("NotebookRep should note be null when returned from activetopicswithlessons.",
        		notebookRep);
        
        Assert.assertNotNull("Topic list should not be null when returned from activetopicswithlessons.", notebookRep.getTopicList());
        List<Topic> topicList = notebookRep.getTopicList();
        Assert.assertNotNull("NotebookRep role should not be null.", notebookRep.getUserRole());
        Assert.assertEquals("NotebookRep has the wrong role.",  Role.teacher, notebookRep.getUserRole());
        for (Topic topic:topicList) {
        	LessonNote lastLessonNote = topic.getLastLessonNote();
        	Assert.assertNotNull("Last lesson note should not be null when returned from activetopicwithlessons.",
        			lastLessonNote);
        	String content = lastLessonNote.getContent();
        	Assert.assertNotNull("Last lesson note should have content. ", content);
        	Assert.assertTrue("Last lesson note content should not be empty.", content.length()>0);
        	Assert.assertNotNull("Date of last lesson should not be null.", lastLessonNote.getDate());
        }
    }

	@Override
	protected SeedData getSeedData() {
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1 ,1, 1, 1, numTopics, numLessonNotes);

		return randomSeedData;
	}

}
