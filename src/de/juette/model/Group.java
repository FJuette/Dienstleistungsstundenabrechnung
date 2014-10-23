package de.juette.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Gruppen")
public class Group extends AbstractEntity {
	@Column(name = "gruppenname")
	private String groupName;
	@Column(name = "befreit")
	private Boolean liberated = false;

	@ManyToMany(mappedBy = "groups")
	private Collection<Member> member = new ArrayList<Member>();

	public Group() {

	}

	public Group(String groupName, Boolean liberated) {
		this.groupName = groupName;
		this.liberated = liberated;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Boolean getLiberated() {
		return liberated;
	}

	public void setLiberated(Boolean liberated) {
		this.liberated = liberated;
	}

	public Collection<Member> getMember() {
		return member;
	}

	public void setMember(Collection<Member> member) {
		this.member = member;
	}

}
