package de.juette.dlsa;

import com.vaadin.data.Validator;

public class MyDlsValidator implements Validator {

	private static final long serialVersionUID = 4640541028296750688L;
	private String granularity;
	
	public MyDlsValidator(String granularity) {
		this.granularity = granularity;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!tryParseDouble((String)value) ) {
    		throw new InvalidValueException("Die Anzahl der DLS muss eine Zahl sein.");
    	}
		if (!checkGranularity(Double.parseDouble((String) value.toString().replace(",", ".")))) {
			throw new InvalidValueException("Die Granularität muss dem Typ " + granularity + " sein.");
		}
	}
	
	public boolean checkGranularity(double dls) {
		switch(granularity){
        case "Keine":
            return true;
        case "Ganze":
        	if (dls % 1.0 > 0) {
				return false;
			} else {
				return true;
			}
        case "Halbe":
        	if (dls * 10 % 5.0 > 0) {
				return false;
			} else {
				return true;
			}
		case "Viertel":
	    	if ( dls % 1.0 == 0 || 
	    		(dls + 0.25) % 1.0 == 0 ||
	    		(dls + 0.5) % 1.0 == 0 ||
	    		(dls + 0.75) % 1.0 == 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	private Boolean tryParseDouble(String s) {
		try {
			Double.parseDouble(s.replace(',', '.'));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public String getGranularity() {
		return granularity;
	}

	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}
	
}
