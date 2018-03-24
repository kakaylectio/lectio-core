package com.kakay.lectio.rest.representation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kktam.lectio.model.Notebook;
import com.kktam.lectio.model.Topic;

public class NotebookRep {
	

    @JsonProperty
	Notebook notebook;

    @JsonProperty
	List<Topic> topicList;
	
	public NotebookRep() {
	}
	
	public NotebookRep(Notebook notebook, List<Topic> topicList) {
		this.notebook = notebook;
		this.topicList = topicList;
	}

	public Notebook getNotebook() {
		return notebook;
	}

	public void setNotebook(Notebook notebook) {
		this.notebook = notebook;
	}

	public List<Topic> getTopicList() {
		return topicList;
	}

	public void setTopicList(List<Topic> topicList) {
		this.topicList = topicList;
	}
	
	

}
