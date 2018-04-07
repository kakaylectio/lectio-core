package com.kakay.lectio.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * An object defined for Json purposes.  Email and password come from the client.
 *
 */
@JsonSerialize
public class EmailPassword {
	String email;
	String password;
	
	@JsonProperty
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@JsonProperty
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
