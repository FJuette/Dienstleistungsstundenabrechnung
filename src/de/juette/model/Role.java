package de.juette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Rollen")
public class Role extends AbstractEntity {
	@Column(name = "rollenname")
	private String rolename;

	public Role() {

	}

	public Role(String rolename) {
		super();
		this.rolename = rolename;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

}
