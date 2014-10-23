package de.juette.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table (name="Einstellungen")
public class Settings extends AbstractEntity {
	private String stichtag;
	private Integer countDls;
	private Double costDls;
	private Integer ageFrom;
	private Integer ageTo;
	private String bookingMethod;
	private Boolean clearing = true;
	private Boolean dlsTransfer = true;
	
	public Settings() {
		
	}
	
	public Settings(String stichtag, Integer countDls, Double costDls,
			Integer ageFrom, Integer ageTo, String bookingMethod,
			Boolean clearing, Boolean dlsTransfer) {
		super();
		this.stichtag = stichtag;
		this.countDls = countDls;
		this.costDls = costDls;
		this.ageFrom = ageFrom;
		this.ageTo = ageTo;
		this.bookingMethod = bookingMethod;
		this.clearing = clearing;
		this.dlsTransfer = dlsTransfer;
	}
	public String getStichtag() {
		return stichtag;
	}
	public void setStichtag(String stichtag) {
		this.stichtag = stichtag;
	}
	public double getCostDls() {
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
	public Boolean getDlsTransfer() {
		return dlsTransfer;
	}
	public void setDlsTransfer(Boolean dlsTransfer) {
		this.dlsTransfer = dlsTransfer;
	}

	public Integer getCountDls() {
		return countDls;
	}

	public void setCountDls(Integer countDls) {
		this.countDls = countDls;
	}
	
	
}
