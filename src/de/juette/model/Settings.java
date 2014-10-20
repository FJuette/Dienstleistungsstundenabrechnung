package de.juette.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table (name="Einstellungen")
public class Settings extends AbstractEntity {
	private String stichtag;
	private String costDls;
	private int ageFrom;
	private int ageTo;
	private int bookingMethod; // Index of the selected value
	private Boolean clearing = true;
	private Boolean dlsTransfer = true;
	
	public Settings() {
	}
	
	public Settings(String stichtag, String costDls, int ageFrom, int ageTo,
			int bookingMethod) {
		this.stichtag = stichtag;
		this.costDls = costDls;
		this.ageFrom = ageFrom;
		this.ageTo = ageTo;
		this.bookingMethod = bookingMethod;
	}
	public String getStichtag() {
		return stichtag;
	}
	public void setStichtag(String stichtag) {
		this.stichtag = stichtag;
	}
	public String getCostDls() {
		return costDls;
	}
	public void setCostDls(String costDls) {
		this.costDls = costDls;
	}
	public int getAgeFrom() {
		return ageFrom;
	}
	public void setAgeFrom(int ageFrom) {
		this.ageFrom = ageFrom;
	}
	public int getAgeTo() {
		return ageTo;
	}
	public void setAgeTo(int ageTo) {
		this.ageTo = ageTo;
	}
	public int getBookingMethod() {
		return bookingMethod;
	}
	public void setBookingMethod(int bookingMethod) {
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
	
	
}
