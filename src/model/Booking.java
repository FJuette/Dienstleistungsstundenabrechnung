package model;

import java.util.Date;

public class Booking {
	private int anzahlDLS;
	private String bemerkung;
	private Date ableistungsDatum;
	private Member mitglied;
	
	
	public int getAnzahlDLS() {
		return anzahlDLS;
	}
	public void setAnzahlDLS(int anzahlDLS) {
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
