package de.juette.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.joda.time.Years;

@Entity
@Table(name = "Mitglieder")
public class Member extends AbstractEntity {
	@Column(name = "nachname")
	private String surname;
	@Column(name = "vorname")
	private String forename;
	@Column(name = "mitgliedsnummer")
	private String memberId;
	@Column(name = "eintrittsdatum")
	private Date entryDate;
	@Column(name = "austrittsdatum")
	private Date leavingDate;
	@Column(name = "aktiv")
	private Boolean active = true;
	@Column(name = "geburtsdatum")
	private Date birthdate;

	@OneToOne
	private BasicMember basicMember;

	@ManyToMany(cascade = CascadeType.ALL)
	private Collection<Group> groups = new ArrayList<Group>();

	@ManyToMany(cascade = CascadeType.ALL)
	private Collection<Category> categories = new ArrayList<Category>();

	@Transient
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public Member() {

	}

	public Member(String surname, String forename, String memberId,
			Date entryDate) {
		this.surname = surname;
		this.forename = forename;
		this.memberId = memberId;
		this.entryDate = entryDate;
	}

	public Member(String surname, String forename, String memberId,
			Date entryDate, Collection<Group> groups,
			Collection<Category> categories) {
		this.surname = surname;
		this.forename = forename;
		this.memberId = memberId;
		this.entryDate = entryDate;
		this.groups = groups;
		this.categories = categories;
	}

	public String getFullName() {
		return forename + " " + surname;
	}

	public String getHtmlName() {
		return "<div style='font-size:0.9em'><strong>"
				+ forename
				+ " "
				+ surname
				+ "</strong></div> <div style='font-size:0.8em'>Mitgliedernummer: "
				+ memberId + "</div>";
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		Date oldDate = this.entryDate;
		this.entryDate = entryDate;
		changes.firePropertyChange(MemberColumn.ENTRYDATE.toString(), oldDate,
				entryDate);
	}

	public Date getLeavingDate() {
		return leavingDate;
	}

	public void setLeavingDate(Date leavingDate) {
		Date oldDate = this.leavingDate;
		this.leavingDate = leavingDate;
		changes.firePropertyChange(MemberColumn.LEAVINGDATE.toString(),
				oldDate, leavingDate);
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		Boolean oldValue = this.active;
		this.active = active;
		changes.firePropertyChange(MemberColumn.ACTIVE.toString(), oldValue,
				active);
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		Collection<Group> oldGroups = this.groups;
		this.groups = groups;
		changes.firePropertyChange(MemberColumn.GROUP.toString(), oldGroups,
				groups);
	}

	public Collection<Category> getCategories() {
		return categories;
	}

	public void setCategories(Collection<Category> categories) {
		this.categories = categories;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public int getAge(DateTime dueDate) {
		if (birthdate == null) {
			return -1;
		}
		Years age = Years.yearsBetween(new DateTime(birthdate), dueDate);
		return age.getYears();
	}

	public BasicMember getBasicMember() {
		return basicMember;
	}

	public void setBasicMember(BasicMember basicMember) {
		this.basicMember = basicMember;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

}
