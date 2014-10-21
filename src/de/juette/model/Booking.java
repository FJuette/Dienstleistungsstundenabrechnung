package de.juette.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table (name="Buchung")
public class Booking extends AbstractEntity {
	private double anzahlDLS;
	private String bemerkung;
	private Date ableistungsDatum;
	
	@ManyToOne
	private Member mitglied;
	
	@ManyToOne
	private Activity aktion;
	
	public Booking() {
		
	}
	
	public Booking(double anzahlDLS, String bemerkung, Date ableistungsDatum,
			Member mitglied, Activity aktion) {
		this.anzahlDLS = anzahlDLS;
		this.bemerkung = bemerkung;
		this.ableistungsDatum = ableistungsDatum;
		this.mitglied = mitglied;
		this.aktion = aktion;
	}
	public Member getMitglied() {
		return mitglied;
	}
	public void setMitglied(Member mitglied) {
		this.mitglied = mitglied;
	}
	public Activity getAktion() {
		return aktion;
	}
	public void setAktion(Activity aktion) {
		this.aktion = aktion;
	}
	public double getAnzahlDLS() {
		return anzahlDLS;
	}
	public void setAnzahlDLS(double anzahlDLS) {
		this.anzahlDLS = anzahlDLS;
	}
	public String getBemerkung() {
		return bemerkung;
	}
	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}
	public Date getAbleistungsDatum() {
		return ableistungsDatum;
	}
	public void setAbleistungsDatum(Date ableistungsDatum) {
		this.ableistungsDatum = ableistungsDatum;
	}
	
	
}
