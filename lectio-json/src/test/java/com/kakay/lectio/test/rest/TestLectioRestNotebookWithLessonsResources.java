package com.kakay.lectio.test.rest;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Topic;

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

	@Override
	protected Object getResource() {
		NotebookActiveTopicsResource notebookActiveTopicResource = new NotebookActiveTopicsResource();
		notebookActiveTopicResource.setLectioControl(new LectioPersistence().getLectioControlById());

		Object resource = notebookActiveTopicResource;
		return resource;
	}

}
