package com.kakay.lectio.auth;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class LectioPrincipal implements Principal {

	protected String name;
	protected int id;
	protected Set<Privilege> privileges;
	
	public LectioPrincipal(String name, int id, HashSet<Privilege> privileges) {
		this.name = name;
		this.id = id;
		this.privileges = privileges;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}

	public void setName(String name) {
		this.name = name;
	}
	
		

}
