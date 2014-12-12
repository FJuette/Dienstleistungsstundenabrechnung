package de.juette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class BasicGroup extends AbstractEntity {
	@Column(name = "dlsBefreit")
	private Boolean liberate;
	
	@Column(name = "gruppenname")
	private String groupName;
	
	@OneToOne
	private Group group;

	public Boolean getLiberate() {
		return liberate;
	}

	public void setLiberate(Boolean liberate) {
		this.liberate = liberate;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
}
