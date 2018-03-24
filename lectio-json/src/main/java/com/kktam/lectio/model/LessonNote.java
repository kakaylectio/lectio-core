package com.kktam.lectio.model;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */
 
@javax.persistence.Entity 
@javax.persistence.NamedQueries({
	// TODO Make this query more efficient
	@javax.persistence.NamedQuery(name="LessonNote.byTopicAndLastDate",
			query="SELECT ln FROM LessonNote ln " + 
					"WHERE ln.date = (SELECT MAX(lnln.date) FROM LessonNote lnln WHERE lnln.topic = :topic) AND ln.topic = :topic "
			),
	@javax.persistence.NamedQuery(name="LessonNote.byTopic",
	query="SELECT ln FROM LessonNote ln " + 
			"WHERE ln.topic = :topic "
			+ "ORDER BY ln.date DESC"
	),
	@javax.persistence.NamedQuery(name="LessonNote.countByTopic",
			query="SELECT COUNT (ln) FROM LessonNote ln WHERE ln.topic = :topic"
			),
	@javax.persistence.NamedQuery(name="LessonNote.byActiveTopicByNotebook",
		query="SELECT ln FROM LessonNote ln JOIN Topic t WHERE t.lastLessonNote = ln AND t.topicState = active AND t.notebook = :notebook")
	
})

public class LessonNote
{
	
	
	public static final String QUERY_LASTLESSONNOTE = "LessonNote.byTopicAndLastDate";
	public static final String QUERY_LESSONNOTES = "LessonNote.byTopic";
	public static final String QUERY_COUNTLESSONNOTE = "LessonNote.countByTopic";
	public static final String QUERYPARAM_LESSONNOTE_TOPIC = "topic";
	public static final String QUERY_LASTLESSONNOTEBYNOTEBOOK = "LessonNote.byActiveTopicByNotebook";
	public static final String QUERYPARAM_LESSONNOTE_NOTEBOOK = "notebook";
	
	@javax.persistence.Column(nullable = false) 
	protected LocalDateTime date;

	@javax.persistence.Column(nullable = false, length = 2000) 
	protected String content;

	

	@JsonProperty 
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
	
	@JsonProperty 
	public int getId() {
		return this.id;
	}

	
	public void setId(int myId) {
		this.id = myId;
	}



	@JsonProperty 
	public LocalDateTime getDate() {
		return this.date;
	}



	@JsonProperty 
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


	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */	
	@javax.persistence.ManyToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	protected Topic topic;
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 */
	public LessonNote(){
		super();
	}
	
	@JsonIgnore
	public Topic getTopic() {
		return this.topic;
	}
	
	public void setTopic(Topic myTopic) {
		this.topic = myTopic;
	}
	
	public void unsetTopic() {
		this.topic = null;
	}


	
}

