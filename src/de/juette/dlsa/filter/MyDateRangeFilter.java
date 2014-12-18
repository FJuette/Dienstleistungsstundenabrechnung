package de.juette.dlsa.filter;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;

import de.juette.dlsa.MyDateRangeValidator;

public class MyDateRangeFilter implements Container.Filter {

	private static final long serialVersionUID = 7797702058188707418L;
	DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
	protected String propertyId;
	protected String value;
	
	public MyDateRangeFilter(String propertyId, String value) {
		this.propertyId = propertyId;
		this.value = value;
	}
	
	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		@SuppressWarnings("unchecked")
		Property<Date> p = item.getItemProperty(propertyId);
		// Should always check validity
		if (p == null || !p.getType().equals(Date.class))
			return false;
		DateTime dtProp = new DateTime(p.getValue());
		return checkValue(dtProp, value);
	}
	
	public Boolean checkValue(DateTime dtProp, String value) {
		MyDateRangeValidator validator = new MyDateRangeValidator();
		try {
			validator.validate(value);
			if (validator.isRange(value)) {
				if ( dtProp.isAfter( dateStringFormat.parseDateTime(validator.getDate1()).minusDays(1) ) && 
						dtProp.isBefore( dateStringFormat.parseDateTime(validator.getDate2()).plusDays(1) ) ) {
					return true;
				}
				return false;
			} else {
				switch (validator.getMethod()) {
				case "=":
					if (dtProp.isEqual(dateStringFormat.parseDateTime(validator.getDate1()))) {
						return true;
					}
					return false;
				case ">":
					if (dtProp.isAfter(dateStringFormat.parseDateTime(validator.getDate1()))) {
						return true;
					}
					return false;
				case "<":
					if (dtProp.isBefore(dateStringFormat.parseDateTime(validator.getDate1()))) {
						return true;
					}
					return false;
				default:
					return false;
				}
			}
		} catch (InvalidValueException e) {
			return false;
		}
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return propertyId != null && propertyId.equals(this.propertyId);
	}
	
}
