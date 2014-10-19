package de.juette.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Member extends AbstractEntity {
	private String nachname;
	private String vorname;
	private String mitgliedsnummer;
	private Date eintrittsdatum;
	private Date austrittsdatum;
	private Boolean aktiv = false;
	private Collection<Group> gruppen = new ArrayList<Group>();
	private Collection<Subject> sparten = new ArrayList<Subject>();

	public Member() {

	}

	public Member(String nachname, String vorname, String mitgliedsnummer,
			Date eintrittsdatum, Collection<Group> gruppen,
			Collection<Subject> sparten) {
		this.nachname = nachname;
		this.vorname = vorname;
		this.mitgliedsnummer = mitgliedsnummer;
		this.eintrittsdatum = eintrittsdatum;
		this.gruppen = gruppen;
		this.sparten = sparten;
	}

	public Member(String nachname, String vorname, String mitgliedsnummer) {
		super();
		this.nachname = nachname;
		this.vorname = vorname;
		this.mitgliedsnummer = mitgliedsnummer;
	}

	public Member(String nachname, String vorname, String mitgliedsnummer, Date eintrittsdatum) {
		super();
		this.nachname = nachname;
		this.vorname = vorname;
		this.mitgliedsnummer = mitgliedsnummer;
		this.eintrittsdatum = eintrittsdatum;
	}

	public Member(String nachname, String vorname, String mitgliedsnummer,
			Collection<Group> gruppen, Collection<Subject> sparten) {
		super();
		this.nachname = nachname;
		this.vorname = vorname;
		this.mitgliedsnummer = mitgliedsnummer;
		this.gruppen = gruppen;
		this.sparten = sparten;
	}

	public String getFullName() {
		return vorname + " " + nachname;
	}
	
	public String getHtmlName() {
		return "<p style='font-size:0.9em'><strong>" + vorname + " " + nachname + "</strong></p><p style='font-size:0.8em'>" + mitgliedsnummer + "</p>";
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getMitgliedsnummer() {
		return mitgliedsnummer;
	}

	public void setMitgliedsnummer(String mitgliedsnummer) {
		this.mitgliedsnummer = mitgliedsnummer;
	}

	public Collection<Group> getGruppen() {
		return gruppen;
	}

	public void setGruppen(Collection<Group> gruppen) {
		this.gruppen = gruppen;
	}

	public Collection<Subject> getSparten() {
		return sparten;
	}

	public void setSparten(Collection<Subject> sparten) {
		this.sparten = sparten;
	}

	public Date getEintrittsdatum() {
		return eintrittsdatum;
	}

	public void setEintrittsdatum(Date eintrittsdatum) {
		this.eintrittsdatum = eintrittsdatum;
	}

	public Date getAustrittsdatum() {
		return austrittsdatum;
	}

	public void setAustrittsdatum(Date austrittsdatum) {
		this.austrittsdatum = austrittsdatum;
	}

	public Boolean getAktiv() {
		return aktiv;
	}

	public void setAktiv(Boolean aktiv) {
		this.aktiv = aktiv;
	}

}
