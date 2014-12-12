package de.juette.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class MemberChanges extends AbstractEntity {
	@Column(name = "bezugsdatum")
	private Date refDate;

	@Column(name = "spaltenname")
	private String column;

	@Column(name = "alterWert")
	private String oldValue;
	
	@Column(name = "neuerWert")
	private String newValue;

	public Date getRefDate() {
		return refDate;
	}

	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}
}
