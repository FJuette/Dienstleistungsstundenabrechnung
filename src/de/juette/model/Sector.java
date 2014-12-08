package de.juette.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Bereiche")
public class Sector extends AbstractEntity {
	@Column(name = "Name")
	private String sectorname;
	
	@ManyToMany(cascade = CascadeType.ALL)
	private Collection<Group> groups = new ArrayList<Group>();
	
	public String getHtmlName() {
		return "<div style='font-size:0.9em'><strong>" + sectorname + " </strong></div>";
	}

	public String getSectorname() {
		return sectorname;
	}

	public void setSectorname(String sectorname) {
		this.sectorname = sectorname;
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = groups;
	}
}
