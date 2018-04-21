package com.kakay.lectio.rest.representation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakay.lectio.model.Notebook;
import com.kakay.lectio.model.Role;
import com.kakay.lectio.model.Topic;

public class NotebookRep {
	

    @JsonProperty
	Notebook notebook;

    @JsonProperty
	List<Topic> topicList;
    
    @JsonProperty
    Role userRole;
	
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

	public Role getUserRole() {
		return userRole;
	}

	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}
	
	

}
