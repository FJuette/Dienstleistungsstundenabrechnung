package model;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Log extends AbstractEntity {
	private Date timestamp;
	private String beschreibung;
	private Member bearbeiter;
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getBeschreibung() {
		return beschreibung;
	}
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}
	public Member getBearbeiter() {
		return bearbeiter;
	}
	public void setBearbeiter(Member bearbeiter) {
		this.bearbeiter = bearbeiter;
	}
	public Log(Date timestamp, String beschreibung, Member bearbeiter) {
		this.timestamp = timestamp;
		this.beschreibung = beschreibung;
		this.bearbeiter = bearbeiter;
	}
}