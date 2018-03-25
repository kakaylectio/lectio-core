package com.kktam.lectio.model;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.kakay.lectio.rest.resources.views.Views;



/**
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */
 
@javax.persistence.Entity
@javax.persistence.Table
(
uniqueConstraints= 
{
		@UniqueConstraint
		(
		columnNames= {"name", "notebook_id"}, 
		name="UniqueTopicName"
		)
})
@javax.persistence.NamedQueries({
	@javax.persistence.NamedQuery(name="Topic.activeByNotebook",
			query="select tp FROM Topic as tp where tp.notebook = :notebook order by tp.activeOrder"
			),
	@javax.persistence.NamedQuery(name="Topic.activeByNotebookId",
	query="select tp FROM Topic as tp where tp.notebook.id = :notebookId order by tp.activeOrder"
	),
	@javax.persistence.NamedQuery(name="TopicAndLessonNote.activeByNotebook",
	query="select tp FROM Topic as tp LEFT JOIN FETCH tp.lastLessonNote where  tp.notebook = :notebook  order by tp.activeOrder"
	),
	@javax.persistence.NamedQuery(name="TopicAndLessonNote.activeByNotebookId",
	query="select tp FROM Topic as tp LEFT JOIN FETCH tp.lastLessonNote where  tp.notebook.id = :notebookId  order by tp.activeOrder"
	)
})


public class Topic
{
	
	public static final String QUERY_FINDTOPICS_NOTEBOOK = "Topic.activeByNotebook";
	public static final String QUERY_FINDTOPICS_NOTEBOOKID = "Topic.activeByNotebookId";
	public static final String QUERY_FINDTOPICSWITHLESSONNOTE_NOTEBOOK = "TopicAndLessonNote.activeByNotebook";
	public static final String QUERY_FINDTOPICSWITHLESSONNOTE_NOTEBOOKID = "TopicAndLessonNote.activeByNotebookId";
	//	public static final String QUERY_FINDMUSICPIECES = "Notebook.FindTopics";
	public static final String QUERYPARAM_TOPIC_NOTEBOOK = "notebook";
	public static final String QUERYPARAM_TOPIC_NOTEBOOKID = "notebookId";
	public static final String QUERY_COUNTMUSICPIECES_NOTEBOOK = "Notebook.CountTopics";
	public static final String QUERY_MAXORDER_NOTEBOOK = "Notebook.MaxActiveOrderTopics";
	public static final String UNIQUE_TOPIC_NAME = "UniqueTopicName";
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@javax.persistence.Enumerated(javax.persistence.EnumType.STRING) 
	@javax.persistence.Column(nullable = false) 
	protected TopicState topicState;

	 
	@javax.persistence.Column(nullable = false) 
	protected String name;

	/*  When a music piece is active, user has
	 * specifically put the music piece list in
	 * a certain order.  Active order is that  order.
	 * When the piece goes to archive, user order
	 * is no longer maintained.
	 */
	@javax.persistence.Column(nullable = false) 
	protected int activeOrder;

	public int getActiveOrder() {
		return activeOrder;
	}



	public void setActiveOrder(int activeOrder) {
		this.activeOrder = activeOrder;
	}


	public void unsetActiveOrder() {
		this.activeOrder = -1;
	}




	@JsonProperty 
	public TopicState getTopicState() {
		return topicState;
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setTopicState(TopicState myTopicState) {
		this.topicState = myTopicState;
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetTopicState() {
		this.topicState = null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	 
	@javax.persistence.Column(nullable = false) 
	protected String shortname;


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	 
	@javax.persistence.Column(nullable = false) 
	protected LocalDateTime dateCreated;

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	 
	@javax.persistence.Column(nullable = true) 
	protected String information;

	@javax.persistence.Column(nullable = true) 
	protected String graphic;
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	
	@javax.persistence.ManyToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	protected Notebook notebook;

	@javax.persistence.ManyToOne
	@javax.persistence.JoinColumn(nullable = true) 
	protected LessonNote lastLessonNote;
	 
	@JsonView(Views.LastLessonNotes.class)
	public LessonNote getLastLessonNote() {
		return lastLessonNote;
	}

	public void setLastLessonNote(LessonNote lastLessonNote) {
		this.lastLessonNote = lastLessonNote;
	}
	
	public void unsetLastLessonNote() {
		this.lastLessonNote = null;
	}

	@javax.persistence.Column(nullable = true) 
	protected String uiPreference;
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public String getUiPreference() {
		
		return this.uiPreference;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetUiPreference() {
		this.uiPreference = "";
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setUiPreference(String myUiPreference) {
		this.uiPreference = myUiPreference;
	}

	@JsonIgnore
	public Notebook getNotebook() {
		return this.notebook;
	}
	
	public void setNotebook(Notebook myNotebook) {
		this.notebook = myNotebook;
	}
	
	public void unsetNotebook() {
		this.notebook = null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	 
	@javax.persistence.ManyToMany 
	@javax.persistence.JoinTable 
	protected Set<Tag> tag;


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@javax.persistence.Id 
	@javax.persistence.GeneratedValue(strategy=javax.persistence.GenerationType.IDENTITY)
	protected int id;
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public int getId() {
		return this.id;
	}

	
	public void setId(int myId) {
		this.id = myId;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 */
	public Topic(){
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public String getName() {
		return this.name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public String getShortname() {
		return this.shortname;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public LocalDateTime getDateCreated() {
		return this.dateCreated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public String getInformation() {
		return this.information;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public String getGraphic() {
		return this.graphic;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public Set<Tag> getTag() {
		if(this.tag == null) {
				this.tag = new HashSet<Tag>();
		}
		return (Set<Tag>) this.tag;
	}

	/**

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void addAllTag(Set<Tag> newTag) {
		if (this.tag == null) {
			this.tag = new HashSet<Tag>();
		}
		this.tag.addAll(newTag);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void removeAllTag(Set<Tag> newTag) {
		if(this.tag == null) {
			return;
		}
		
		this.tag.removeAll(newTag);
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setName(String myName) {
		this.name = myName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setShortname(String myShortname) {
		this.shortname = myShortname;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setDateCreated(LocalDateTime myDateCreated) {
		this.dateCreated = myDateCreated;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setInformation(String myInformation) {
		this.information = myInformation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setGraphic(String myGraphic) {
		this.graphic = myGraphic;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void addTag(Tag newTag) {
		if(this.tag == null) {
			this.tag = new HashSet<Tag>();
		}
		
		this.tag.add(newTag);
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetName() {
		this.name = "";
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetShortname() {
		this.shortname = "";
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetDateCreated() {
		this.dateCreated = LocalDateTime.MIN;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetInformation() {
		this.information = "";
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetGraphic() {
		this.graphic = "";
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void removeTag(Tag oldTag) {
		if(this.tag == null)
			return;
		
		this.tag.remove(oldTag);
	}
	

	
}

