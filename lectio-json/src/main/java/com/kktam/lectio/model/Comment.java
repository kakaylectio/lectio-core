package com.kktam.lectio.model;

import java.time.LocalDateTime;

/**
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */
 
@javax.persistence.Entity 
public class Comment
{
	@javax.persistence.ManyToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	protected LessonNote lessonNote;

	@javax.persistence.Column(nullable = false) 
	protected LocalDateTime date;

	@javax.persistence.Column(nullable = false) 
	protected String content;

	

	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}
	
	public void unsetContent() {
		this.content = null;
	}


	@javax.persistence.ManyToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	protected User author;

	@javax.persistence.Id 
	@javax.persistence.GeneratedValue(strategy=javax.persistence.GenerationType.IDENTITY)
	protected int id;
	
	public int getId() {
		return this.id;
	}

	
	public void setId(int myId) {
		this.id = myId;
	}



	public LocalDateTime getDate() {
		return this.date;
	}



	public User getAuthor() {
		return this.author;
	}

	public void setDate(LocalDateTime myDate) {
		this.date = myDate;
	}



	public void setAuthor(User myAuthor) {
		this.author = myAuthor;
	}

	public void unsetDate() {
		this.date = LocalDateTime.MIN;
	}


	public void unsetAuthor() {
		this.author = new User();
	}


	
}

