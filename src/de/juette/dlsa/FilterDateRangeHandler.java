package de.juette.dlsa;

public class FilterDateRangeHandler {
	
	public final static Boolean isSingle(String value) {
		if (value.contains("-")) {
			return true;
		}
		return false;
	}
	
}
