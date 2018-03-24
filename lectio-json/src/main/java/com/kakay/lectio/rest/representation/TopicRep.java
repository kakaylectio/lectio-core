package com.kakay.lectio.rest.representation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicRep {

	int id;
	String name;
	String graphic;
	@JsonProperty
	LessonNoteRep lastLessonNote;

	public TopicRep(int id, String name, String graphic) {
		this(id, name, graphic, null);
	}
	public TopicRep() {
	}
	
	public TopicRep(int id, String name, String graphic, LessonNoteRep lastLessonNote) {
		this.id = id;
		this.name = name;
		this.graphic = graphic;
		this.lastLessonNote = lastLessonNote;
	}
	@JsonProperty
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@JsonProperty
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	@JsonProperty
	public String getGraphic() {
		return graphic;
	}
	public void setGraphic(String graphic) {
		this.graphic = graphic;
	}
	
	public LessonNoteRep getLastLessonNote() {
		return lastLessonNote;
	}

	public void setLastLessonNote(LessonNoteRep lastLessonNote) {
		this.lastLessonNote = lastLessonNote;
	}

	
	
	
	
}
