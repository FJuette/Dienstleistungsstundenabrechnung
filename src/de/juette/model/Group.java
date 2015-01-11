package de.juette.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Gruppen")
public class Group extends AbstractEntity {
	@Column(name = "gruppenname")
	private String groupName;
	@Column(name = "befreit")
	private Boolean liberated = false;

	@ManyToMany(mappedBy = "groups")
	private Collection<Member> member = new ArrayList<Member>();
	
	@OneToOne
	private BasicGroup basicGroup;

	@Transient
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
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
		Boolean oldValue = this.liberated;
		this.liberated = liberated;
		changes.firePropertyChange("Liberated", oldValue,
				liberated);
	}

	public Collection<Member> getMember() {
		return member;
	}

	public void setMember(Collection<Member> member) {
		this.member = member;
	}

	public BasicGroup getBasicGroup() {
		return basicGroup;
	}

	public void setBasicGroup(BasicGroup basicGroup) {
		this.basicGroup = basicGroup;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}
	
	public void removeAllPropertyChangeListeners() {
		for (PropertyChangeListener l : changes.getPropertyChangeListeners()) {
			changes.removePropertyChangeListener(l);
		} 
	}
}
