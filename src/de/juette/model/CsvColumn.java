package de.juette.model;

public class CsvColumn {
	private Integer index;
	private String value;

	public CsvColumn() {
		
	}
	
	public CsvColumn(Integer index, String value) {
		this.index = index;
		this.value = value;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
