package com.kktam.lectio.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class TopicTag {
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected int id;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	protected Tag tag;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	protected Topic topic;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	
}
