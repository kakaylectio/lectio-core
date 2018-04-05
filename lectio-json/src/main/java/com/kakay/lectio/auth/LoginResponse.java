package com.kakay.lectio.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
	protected String token;
	protected String expiration;
	protected String name;
	
	public LoginResponse(){}
	
	@JsonProperty
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	@JsonProperty
	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	
	@JsonProperty
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}