package de.juette.dlsa;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.vaadin.data.Validator;

public class MyDateRangeValidator implements Validator {
	
	private static final long serialVersionUID = 7535409812865116262L;
	DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
	
	private String date1;
	private String date2;
	private String method;

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (value != null && !((String)value).trim().equals("")) {
			String v = (String) value;
			if (!isRange(v)) {
				if (v.trim().startsWith("=") || v.trim().startsWith(">") || v.trim().startsWith("<")) {
					date1 = v.trim().substring(1).trim();
					method = v.trim().substring(0, 1);
				} else {
					date1 = v.trim();
					method = "=";
				}
				if (!tryParseDate(date1)) {
					throw new InvalidValueException("Das Eingabeformat kann nicht gelesen werden.");
				}
			} else {
				String[] dates = v.split("-");
				if (dates.length != 2) {
					throw new InvalidValueException("Das Eingabeformat kann nicht gelesen werden.");
				}
				date1 = dates[0].trim();
				date2 = dates[1].trim();
				if (!tryParseDate(date1)) {
					throw new InvalidValueException("Das linke Datum kann nicht gelesen werden.");
				}
				if (!tryParseDate(date2)) {
					throw new InvalidValueException("Das rechte Datum kann nicht gelesen werden.");
				}
				method = "-";
			}
		}
	}
	
	public Boolean isRange(String value) {
		if (value.contains("-")) {
			return true;
		}
		return false;
	}
	
	public boolean tryParseDate(String date) {
		try {
			dateStringFormat.parseDateTime(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
