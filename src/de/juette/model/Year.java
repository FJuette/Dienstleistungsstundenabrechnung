package de.juette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "jahre")
public class Year extends AbstractEntity {
	@Column(name = "jahr")
	private int year;

	public Year() {

	}

	public Year(int year) {
		super();
		this.year = year;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
