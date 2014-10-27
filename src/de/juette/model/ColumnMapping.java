package de.juette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Spaltenzuordnung")
public class ColumnMapping extends AbstractEntity {

	@Column(name = "csvSpaltenindex")
	private int csvColumnIndex;
	@Column(name = "csvSpaltenname")
	private String csvColumnName;
	@Column(name = "datenbankspaltenname")
	private String dbColumnName;
	@Column(name = "anzeigename")
	private String displayname;

	public ColumnMapping() {

	}

	public ColumnMapping(String dbColumnName, String displayname) {
		this.dbColumnName = dbColumnName;
		this.displayname = displayname;
	}

	public int getCsvColumnIndex() {
		return csvColumnIndex;
	}

	public void setCsvColumnIndex(int csvColumnIndex) {
		this.csvColumnIndex = csvColumnIndex;
	}

	public String getCsvColumnName() {
		return csvColumnName;
	}

	public void setCsvColumnName(String csvColumnName) {
		this.csvColumnName = csvColumnName;
	}

	public String getDbColumnName() {
		return dbColumnName;
	}

	public void setDbColumnName(String dbColumnName) {
		this.dbColumnName = dbColumnName;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

}
