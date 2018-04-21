package com.kakay.lectio.auth;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kakay.lectio.model.User;

/**
 * DataAccessObject stored in the database containing User ID and password.
 * 
 *
 */
@javax.persistence.Entity
@javax.persistence.NamedQueries({
	@javax.persistence.NamedQuery(name="UserIdentity.byUserId",
			query="select uid FROM UserIdentity as uid where uid.user.id = :id"
			)
})
public class UserIdentity {
	

	public static final String QUERY_USERIDENTITY_BYUSERID = "UserIdentity.byUserId";
	public static final String QUERYPARAM_USERIDENTITY_USERID = "id";
	public UserIdentity() {
	}
	
	@javax.persistence.Id 
	@javax.persistence.GeneratedValue(strategy=javax.persistence.GenerationType.IDENTITY)
	int id;
	
	
	@javax.persistence.OneToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	@JsonIgnore
	protected User user;

	@Column
	@Convert(converter = PrivilegeSetAttributeConverter.class)
	protected Set<Privilege> privileges;

	@javax.persistence.Column(columnDefinition = "binary(32) not null" )
	
	protected byte[] password;


	@Column
	@JsonIgnore
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
