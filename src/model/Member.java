package model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;

@Entity
public class Member extends AbstractEntity {
	private String nachname;
	private String vorname;
	private String mitgliedsnummer;
	private Collection<Group> gruppen = new ArrayList<Group>();
	private Collection<Subject> sparten = new ArrayList<Subject>();
	
	public Member() {
		
	}
	
	public Member(String nachname, String vorname, String mitgliedsnummer) {
		super();
		this.nachname = nachname;
		this.vorname = vorname;
		this.mitgliedsnummer = mitgliedsnummer;
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
	
	
}
