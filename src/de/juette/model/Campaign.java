package de.juette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Aktionen")
public class Campaign extends AbstractEntity {
	@Column(name = "jahr")
	private String year;
	@Column(name = "beschreibung")
	private String description;
	@ManyToOne
	private Member contact;

	public Campaign() {
		
	}

	public Campaign(String year, String description, Member contact) {
		super();
		this.year = year;
		this.description = description;
		this.contact = contact;
	}
	
	public String getHtml() {
		return "<div style='font-size:0.9em'><strong>" + description + " </strong></div>"
				+ " <div style='font-size:0.8em'>Jahr: " + year + "</div>";
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Member getContact() {
		return contact;
	}

	public void setContact(Member contact) {
		this.contact = contact;
	}

}
