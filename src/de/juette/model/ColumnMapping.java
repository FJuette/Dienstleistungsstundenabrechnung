package de.juette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Spaltenzuordnung")
public class ColumnMapping extends AbstractEntity {
	@Column(name = "datenbankspalte")
	private String dbColumnn;
	@Column(name = "csvSpalte")
	private String csvColumn;

	public ColumnMapping() {

	}

	public ColumnMapping(String dbColumnn, String csvColumn) {
		this.dbColumnn = dbColumnn;
		this.csvColumn = csvColumn;
	}

	public String getDbColumnn() {
		return dbColumnn;
	}

	public void setDbColumnn(String dbColumnn) {
		this.dbColumnn = dbColumnn;
	}

	public String getCsvColumn() {
		return csvColumn;
	}

	public void setCsvColumn(String csvColumn) {
		this.csvColumn = csvColumn;
	}
}
