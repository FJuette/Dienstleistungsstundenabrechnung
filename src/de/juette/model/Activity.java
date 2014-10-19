package de.juette.model;

import javax.persistence.Entity;

@Entity
public class Activity extends AbstractEntity {
	private String jahr;
	private String beschreibung;
	private float umfangDLS;
	
	private Member ansprechpartner;
	private Member autorisierungVorstand;
	
	public Activity(String jahr, String beschreibung, float umfangDLS,
			Member ansprechpartner, Member autorisierungVorstand) {
		this.jahr = jahr;
		this.beschreibung = beschreibung;
		this.umfangDLS = umfangDLS;
		this.ansprechpartner = ansprechpartner;
		this.autorisierungVorstand = autorisierungVorstand;
	}
	public String getJahr() {
		return jahr;
	}
	public void setJahr(String jahr) {
		this.jahr = jahr;
	}
	public String getBeschreibung() {
		return beschreibung;
	}
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}
	public float getUmfangDLS() {
		return umfangDLS;
	}
	public void setUmfangDLS(float umfangDLS) {
		this.umfangDLS = umfangDLS;
	}
	public Member getAnsprechpartner() {
		return ansprechpartner;
	}
	public void setAnsprechpartner(Member ansprechpartner) {
		this.ansprechpartner = ansprechpartner;
	}
	public Member getAutorisierungVorstand() {
		return autorisierungVorstand;
	}
	public void setAutorisierungVorstand(Member autorisierungVorstand) {
		this.autorisierungVorstand = autorisierungVorstand;
	}
	
	
}
