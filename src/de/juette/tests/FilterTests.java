package de.juette.tests;

import static org.junit.Assert.assertEquals;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.vaadin.data.Validator.InvalidValueException;

import de.juette.dlsa.MyDateRangeFilter;
import de.juette.dlsa.MyDateRangeValidator;

public class FilterTests {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
	
	@Test
	public void testDateRangeValidatorPositive() {
		MyDateRangeValidator validator = new MyDateRangeValidator();
		validator.validate("10.10.2014");
		validator.validate("=10.10.2014 ");
		validator.validate(">10.10.2014");
		validator.validate("<10.10.2014	");
		validator.validate("  10.10.2014");
		validator.validate(" = 10.10.2014");
		validator.validate(">   10.10.2014");
		validator.validate("	<	10.10.2014");
		validator.validate("01.01.2014-01.01.2015");
		validator.validate("01.01.2014 - 01.01.2015");
		validator.validate("	01.01.2014 -	01.01.2015 ");
		validator.validate(null);
		validator.validate("");
		validator.validate(" ");
		validator.validate("    ");
	}

	@Test
	public void testDateRangeValidatorNegative() {
		MyDateRangeValidator validator = new MyDateRangeValidator();
		
		exception.expect(InvalidValueException.class);
		validator.validate("01.01 - 01.01.2015");
	}
	
	@Test
	public void testDateRangeFilter() {
		MyDateRangeFilter filter = new MyDateRangeFilter("", "");
		assertEquals(true, filter.checkValue(dateStringFormat.parseDateTime("02.01.2014"), ">01.01.2014"));
		assertEquals(false, filter.checkValue(dateStringFormat.parseDateTime("01.01.2014"), ">01.01.2014"));
		assertEquals(false, filter.checkValue(dateStringFormat.parseDateTime("01.01.2014"), "<01.01.2014"));
		assertEquals(true, filter.checkValue(dateStringFormat.parseDateTime("31.12.2013"), "<01.01.2014"));
		assertEquals(true, filter.checkValue(dateStringFormat.parseDateTime("01.01.2014"), "=01.01.2014"));
		assertEquals(true, filter.checkValue(dateStringFormat.parseDateTime("01.01.2014"), "01.01.2014"));
		assertEquals(false, filter.checkValue(dateStringFormat.parseDateTime("02.01.2014"), "01.01.2014"));
		
		assertEquals(true, filter.checkValue(dateStringFormat.parseDateTime("02.01.2014"), "01.01.2014-31.10.2014"));
		assertEquals(true, filter.checkValue(dateStringFormat.parseDateTime("02.01.2014"), "02.01.2014-31.10.2014"));
		assertEquals(false, filter.checkValue(dateStringFormat.parseDateTime("01.01.2014"), "02.01.2014-31.10.2014"));
		assertEquals(true, filter.checkValue(dateStringFormat.parseDateTime("28.10.2014"), "01.01.2014-28.10.2014"));
		assertEquals(false, filter.checkValue(dateStringFormat.parseDateTime("29.10.2014"), "01.01.2014-28.10.2014"));
	}
}
