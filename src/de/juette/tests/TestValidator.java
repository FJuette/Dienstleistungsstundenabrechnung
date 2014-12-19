package de.juette.tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.vaadin.data.Validator.InvalidValueException;

import de.juette.dlsa.MyDateRangeValidator;
import de.juette.dlsa.MyDueDateValidator;

public class TestValidator {
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testDateRangeValidatorNegative() {
		MyDateRangeValidator validator = new MyDateRangeValidator();
		
		exception.expect(InvalidValueException.class);
		validator.validate("01.01 - 01.01.2015");
	}
	
	@Test
	public void testDueDate() {
		MyDueDateValidator v = new MyDueDateValidator();
		v.validate("31.12");
		
		exception.expect(InvalidValueException.class);
		v.validate("32.12");
		
		exception.expect(InvalidValueException.class);
		v.validate("29.02");
		
		v.validate("28.02");
		v.validate("01.01");
		v.validate("1.1");
		v.validate("11.1");
		v.validate("1.01");
		
		exception.expect(InvalidValueException.class);
		v.validate("Crap text");
		
		exception.expect(InvalidValueException.class);
		v.validate("3112");
	}
}
