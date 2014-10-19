package de.juette.model;

import java.util.Date;

public class Booking extends AbstractEntity {
	private double anzahlDLS;
	private String bemerkung;
	private Date ableistungsDatum;
	
	private Member mitglied;
	private Activity aktion;
	private Member abzeichner;
	
	public Booking(double anzahlDLS, String bemerkung, Date ableistungsDatum,
			Member mitglied, Activity aktion, Member abzeichner) {
		this.anzahlDLS = anzahlDLS;
		this.bemerkung = bemerkung;
		this.ableistungsDatum = ableistungsDatum;
		this.mitglied = mitglied;
		this.aktion = aktion;
		this.abzeichner = abzeichner;
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
	public Member getAbzeichner() {
		return abzeichner;
	}
	public void setAbzeichner(Member abzeichner) {
		this.abzeichner = abzeichner;
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
