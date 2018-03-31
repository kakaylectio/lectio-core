package com.kakay.lectio.test.rest;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.resources.NotebookActiveTopicsWithLessonsResource;
import com.kakay.lectio.test.scenarios.RandomSeedData;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.LessonNote;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;
import com.kktam.lectio.model.User;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.ResourceTestRule;

public class TestLectioRestNotebookWithLessonsResources {

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder()
    	    .addResource(new NotebookActiveTopicsWithLessonsResource(new LectioPersistence().getLectioControlById()))
//    		.addProvider(new LectioAuthorizer())
    	        .build();
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		LectioPersistence.clearData("secret");
	}

    @Test
    public void testNotebookActiveTopicsLessonNotesJsonRest() throws IOException {
    	int numTopics = 6;
    	int numLessonNotes = 10;
		RandomSeedData randomSeedData = new RandomSeedData();
		randomSeedData.generateSeed(1 ,1, 1, 1, numTopics, numLessonNotes);
		
		User teacher = randomSeedData.getTeacher();
		Notebook notebook = randomSeedData.getNotebook();

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
