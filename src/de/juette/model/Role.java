package de.juette.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table (name="Rolle")
public class Role extends AbstractEntity {
	private String rollenname;
	
	public Role() {
		
	}

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
