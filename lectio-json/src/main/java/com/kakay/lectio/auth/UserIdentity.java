package com.kakay.lectio.auth;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kktam.lectio.model.User;

@javax.persistence.Entity 
public class UserIdentity {

	public UserIdentity() {
	}
	
	@javax.persistence.Id 
	@javax.persistence.GeneratedValue(strategy=javax.persistence.GenerationType.IDENTITY)
	int id;
	
	
	@javax.persistence.OneToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	protected User user;

	@Column
	@Convert(converter = PrivilegeSetAttributeConverter.class)
	protected Set<Privilege> privileges;

	@javax.persistence.Column(columnDefinition = "binary(32) not null" )
	
	protected byte[] password;


	@javax.persistence.Column(columnDefinition = "binary(128) not null")
	protected String salt;

	@JsonIgnore
	public byte[] getPassword() {
		return this.password;
	}

	public void setPassword(byte[] myPassword) {
		this.password = myPassword;
	}


	public void unsetPassword() {
		this.password = null;
	}

	@JsonIgnore
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@JsonIgnore
	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}

	public void addPrivilege(Privilege myPrivilege) {
		this.privileges.add(myPrivilege);
	}

	public void removePrivilege(Privilege myPrivilege) {
		this.privileges.remove(myPrivilege);
	}


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
