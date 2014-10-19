package de.juette.model;

import java.io.File;
import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Cycle extends AbstractEntity {
	private File datei;
	private Date timestamp;
	private String anzeigename;
	
	public Cycle(File datei, Date timestamp, String dateiname) {
		this.datei = datei;
		this.timestamp = timestamp;
		this.anzeigename = dateiname;
	}
	
	public File getDatei() {
		return datei;
	}
	public void setDatei(File datei) {
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
}
