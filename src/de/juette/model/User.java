package de.juette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Benutzer")
public class User extends AbstractEntity {
	@Column(name = "benutzername")
	private String username;
	@Column(name = "passwort")
	private String password;
	@Column(name = "aktiv")
	private Boolean active = true;
	@ManyToOne
	private Role role;

	public User() {

	}

	public User(String username, String password, Boolean active, Role role) {
		super();
		this.username = username;
		this.password = password;
		this.active = active;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
