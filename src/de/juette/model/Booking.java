package de.juette.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Buchungen")
public class Booking extends AbstractEntity {
	@Column(name = "anzahlDls")
	private double countDls;
	@Column(name = "bemerkung")
	private String comment;
	@Column(name = "ableistungsDatum")
	private Date doneDate;
	@Column(name = "storniert")
	private Boolean canceled = false;
	@Column(name = "buchungsdatum")
	private Date bookingDate = new Date();

	@ManyToOne
	private Member member;

	@ManyToOne
	private Campaign campaign;

	public Booking() {

	}

	public Booking(double countDls, String comment, Date doneDate,
			Member member, Campaign campaign) {
		super();
		this.countDls = countDls;
		this.comment = comment;
		this.doneDate = doneDate;
		this.member = member;
		this.campaign = campaign;
	}

	public double getCountDls() {
		return countDls;
	}

	public void setCountDls(double countDls) {
		this.countDls = countDls;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDoneDate() {
		return doneDate;
	}

	public void setDoneDate(Date doneDate) {
		this.doneDate = doneDate;
	}

	public Boolean getCanceled() {
		return canceled;
	}

	public void setCanceled(Boolean canceled) {
		this.canceled = canceled;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}
