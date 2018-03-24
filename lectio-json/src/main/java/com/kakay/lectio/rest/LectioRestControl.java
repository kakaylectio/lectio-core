package com.kakay.lectio.rest;

import java.time.LocalDateTime;
import java.util.List;

import com.kakay.lectio.rest.representation.LessonNoteRep;
import com.kakay.lectio.rest.representation.NotebookRep;
import com.kakay.lectio.rest.representation.TopicRep;
import com.kktam.lectio.control.LectioControlById;
import com.kktam.lectio.control.LectioPersistence;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;

public class LectioRestControl {
	LectioControlById lectioControl;
	public LectioRestControl() {
		LectioPersistence lectioPersistence = new LectioPersistence();
		lectioControl = lectioPersistence.getLectioControlById();
		
	    
	}
	
	
	public NotebookRep getActiveTopics(int executorId, int notebookId, boolean isWithLessons) {
		List<Topic> topicList = lectioControl.findActiveTopicsByNotebook(executorId, notebookId);
		Notebook notebook = lectioControl.findNotebookById(executorId,  notebookId);
		
		NotebookRep notebookRep = new NotebookRep(notebook, topicList);
		
			    
	    return notebookRep;
	}
	
	
}
