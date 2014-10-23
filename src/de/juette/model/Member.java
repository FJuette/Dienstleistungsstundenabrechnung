package de.juette.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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
	private Boolean active = false;

	@ManyToMany(cascade = CascadeType.ALL)
	private Collection<Group> groups = new ArrayList<Group>();

	@ManyToMany(cascade = CascadeType.ALL)
	private Collection<Category> categories = new ArrayList<Category>();

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
		return "<p style='font-size:0.9em'><strong>" + forename + " " + surname
				+ "</strong></p><p style='font-size:0.8em'>" + memberId
				+ "</p>";
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
		this.entryDate = entryDate;
	}

	public Date getLeavingDate() {
		return leavingDate;
	}

	public void setLeavingDate(Date leavingDate) {
		this.leavingDate = leavingDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = groups;
	}

	public Collection<Category> getCategories() {
		return categories;
	}

	public void setCategories(Collection<Category> categories) {
		this.categories = categories;
	}

}
