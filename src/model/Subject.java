package model;

import javax.persistence.Entity;

@Entity
public class Subject extends AbstractEntity {
	private String spartenname;

	public String getSpartenname() {
		return spartenname;
	}

	public void setSpartenname(String spartenname) {
		this.spartenname = spartenname;
	}

	public Subject(String spartenname) {
		super();
		this.spartenname = spartenname;
	}
	
	
}
