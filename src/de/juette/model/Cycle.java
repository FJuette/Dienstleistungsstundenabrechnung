package de.juette.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table (name="Jahreslauf")
public class Cycle extends AbstractEntity {
	private byte[] datei;
	private Date timestamp;
	private String anzeigename;
	private String dateiname;
	
	public Cycle() {
		
	}
	
	public Cycle(byte[] datei, Date timestamp, String anzeigename, String dateiname) {
		this.datei = datei;
		this.timestamp = timestamp;
		this.anzeigename = anzeigename;
		this.dateiname = dateiname;
	}
	
	public byte[] getDatei() {
		return datei;
	}
	public void setDatei(byte[] datei) {
		this.datei = datei;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getAnzeigename() {
		return anzeigename;
	}
	public void setAnzeigename(String anzeigename) {
		this.anzeigename = anzeigename;
	}

	public String getDateiname() {
		return dateiname;
	}

	public void setDateiname(String dateiname) {
		this.dateiname = dateiname;
	}
}
