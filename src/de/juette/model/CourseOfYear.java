package de.juette.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Jahreslaeufe")
public class CourseOfYear extends AbstractEntity {
	@Column(name = "datei")
	private byte[] file;
	private Date timestamp;
	@Column(name = "anzeigename")
	private String displayName;
	@Column(name = "dateiname")
	private String filename;

	public CourseOfYear() {

	}

	public CourseOfYear(byte[] file, Date timestamp, String displayName,
			String filename) {
		this.file = file;
		this.timestamp = timestamp;
		this.displayName = displayName;
		this.filename = filename;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
