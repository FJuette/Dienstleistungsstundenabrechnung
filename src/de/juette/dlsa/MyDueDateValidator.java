package de.juette.dlsa;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.vaadin.data.Validator;

public class MyDueDateValidator implements Validator {

	private static final long serialVersionUID = -5373740096978710160L;
	DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
	
	@Override
	public void validate(Object value) throws InvalidValueException {
		String val = (String) value + ".2014";
		if (!tryParseDateTime(val) ) {
    		throw new InvalidValueException("Das eingegebene Datum kann nicht validiert werden. "
    				+ "Bitte das Datum im Format 'Tag.Monat' eingeben. (z.B. 31.12)");
    	}
	}
	
	public Boolean tryParseDateTime(String value) {
		try {
			DateTime dt = dateStringFormat.parseDateTime(value);
			String[] parts = value.split("\\.");
			if (parts[0].equals(dt.toString("dd")) && parts[1].equals(dt.toString("MM"))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}
