package com.kakay.lectio.rest.representation;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LessonNoteRep {
	@JsonProperty
	int id;

	@JsonProperty
	LocalDateTime date;
	
	@JsonProperty
	String content;
	
	@JsonProperty
	String author;
	
	public LessonNoteRep() {
	}
	
	public LessonNoteRep(int id,LocalDateTime localDateTime, String content, String author) {
		this.id = id;
		
		this.date = localDateTime;
		this.content = content;
		this.author = author;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	
}
