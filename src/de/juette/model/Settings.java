package de.juette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Einstellungen")
public class Settings extends AbstractEntity {
	@Column(name = "stichtag")
	private String dueDate;
	@Column(name = "anzahlDls")
	private Double countDls;
	@Column(name = "kostenDls")
	private Double costDls;
	@Column(name = "alterVon")
	private Integer ageFrom;
	@Column(name = "alterBis")
	private Integer ageTo;
	@Column(name = "buchungsmethode")
	private String bookingMethod;
	@Column(name = "ausgleichbuchungen")
	private Boolean clearing = true;
	@Column(name = "Granularität")
	private String granularity;

	public Settings() {

	}

	public Settings(String dueDate, Double countDls, Double costDls,
			Integer ageFrom, Integer ageTo, String bookingMethod,
			Boolean clearing, String granularity) {
		super();
		this.dueDate = dueDate;
		this.countDls = countDls;
		this.costDls = costDls;
		this.ageFrom = ageFrom;
		this.ageTo = ageTo;
		this.bookingMethod = bookingMethod;
		this.clearing = clearing;
		this.granularity = granularity;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public Double getCostDls() {
		return costDls;
	}

	public void setCostDls(Double costDls) {
		this.costDls = costDls;
	}

	public Integer getAgeFrom() {
		return ageFrom;
	}

	public void setAgeFrom(Integer ageFrom) {
		this.ageFrom = ageFrom;
	}

	public Integer getAgeTo() {
		return ageTo;
	}

	public void setAgeTo(Integer ageTo) {
		this.ageTo = ageTo;
	}

	public String getBookingMethod() {
		return bookingMethod;
	}

	public void setBookingMethod(String bookingMethod) {
		this.bookingMethod = bookingMethod;
	}

	public Boolean getClearing() {
		return clearing;
	}

	public void setClearing(Boolean clearing) {
		this.clearing = clearing;
	}

	public Double getCountDls() {
		return countDls;
	}

	public void setCountDls(Double countDls) {
		this.countDls = countDls;
	}

	public String getGranularity() {
		return granularity;
	}

	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}

}
