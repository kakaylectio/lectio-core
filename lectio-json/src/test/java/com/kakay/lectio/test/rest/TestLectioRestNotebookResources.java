package com.kakay.lectio.test.rest;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.auth.LectioAuthorizer;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsResource;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsWithLessonsResource;
import com.kakay.lectio.test.scenarios.SeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.ResourceTestRule;

public class TestLectioRestNotebookResources {

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder()
    	    .addResource(new NotebookActiveTopicsResource(new LectioPersistence().getLectioControlById()))
    		.addProvider(new LectioAuthorizer())
    	        .build();

	@After
	public void tearDown() throws Exception {
		LectioPersistence.clearData("secret");
	}

    @Test
    public void testNotebookJsonRest() throws IOException {
    	int numTopics = 6;
		SeedData seedData = new SeedData();
		seedData.generateSeed(1 ,1, 5, 1, numTopics, 0);
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();

		int notebookId = notebook.getId();
		
		String targetString = "/lectio/notebook/" + notebookId + "/activetopicswithlessons";
		
		// Hit the endpoint and get the raw json string
        String resp = resources.client().target(targetString)
                .request().get(String.class);

        ObjectMapper objectMapper = Jackson.newObjectMapper();
        NotebookRep notebookRep = objectMapper.readValue(resp,  NotebookRep.class);
        Assert.assertNotNull("NotebookRep retrieved should have had a notebook name.", notebookRep.getNotebook());
        Assert.assertEquals("Notebook names don't match.",  notebook.getName(), notebookRep.getNotebook().getName());
        
        Assert.assertNull("NotebookRep should not carry studio information.",
        	notebookRep.getNotebook().getStudio());
        
        Assert.assertNotNull("NotebookRep from notebook/activetopics should return a list of topics.",
        		notebookRep.getTopicList());
        Assert.assertEquals("Wrong number of topics returned from notebook/activetopics.",
        		numTopics, notebookRep.getTopicList().size());
        
    }
    
    @Test
    public void testNotebookActiveTopicsLessonNotesJsonRest() throws IOException {
    	int numTopics = 6;
    	int numLessonNotes = 10;
		SeedData seedData = new SeedData();
		seedData.generateSeed(1 ,1, 1, 1, numTopics, numLessonNotes);
		
		User teacher = seedData.getTeacher();
		Notebook notebook = seedData.getNotebook();

		int notebookId = notebook.getId();
		
		String targetString = "/lectio/notebook/" + notebookId + "/activetopicswithlessons";
		
		// Hit the endpoint and get the raw json string
        String resp = resources.client().target(targetString)
                .request().get(String.class);
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        NotebookRep notebookRep = objectMapper.readValue(resp,  NotebookRep.class);
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

}
