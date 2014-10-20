package de.juette.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table (name="Sparte")
public class Subject extends AbstractEntity {
	private String spartenname;
	
	@ManyToMany (mappedBy="sparten")
	private Collection<Member> member = new ArrayList<Member>();
	
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

	public Collection<Member> getMember() {
		return member;
	}

	public void setMember(Collection<Member> member) {
		this.member = member;
	}
	
	
}
