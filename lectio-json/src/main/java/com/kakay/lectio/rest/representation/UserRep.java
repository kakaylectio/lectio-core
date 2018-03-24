package com.kakay.lectio.rest.representation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRep {
	@JsonProperty
	int id;
	
	@JsonProperty
	String name;
	
	@JsonProperty
	String email;
	
	public UserRep(){
	}
	
	public UserRep(int id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
