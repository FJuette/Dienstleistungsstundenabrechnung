package de.juette.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table (name="Gruppe")
public class Group extends AbstractEntity {
	private String gruppenname;
	private Boolean befreit = false;
	
	@ManyToMany (mappedBy="gruppen")
	private Collection<Member> member = new ArrayList<Member>();
	
	public Group(String gruppenname, Boolean befreit) {
		super();
		this.gruppenname = gruppenname;
		this.befreit = befreit;
	}
	public String getGruppenname() {
		return gruppenname;
	}
	public void setGruppenname(String gruppenname) {
		this.gruppenname = gruppenname;
	}
	public Boolean getBefreit() {
		return befreit;
	}
	public void setBefreit(Boolean befreit) {
		this.befreit = befreit;
	}
	public Collection<Member> getMember() {
		return member;
	}
	public void setMember(Collection<Member> member) {
		this.member = member;
	}
	
}
