package de.juette.model;

import javax.persistence.Entity;

@Entity
public class Role extends AbstractEntity {
	private String rollenname;

	public Role(String rollenname) {
		super();
		this.rollenname = rollenname;
	}
	
	public String getRollenname() {
		return rollenname;
	}

	public void setRollenname(String rollenname) {
		this.rollenname = rollenname;
	}
}
