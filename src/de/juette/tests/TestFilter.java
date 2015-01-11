package de.juette.tests;

import static org.junit.Assert.assertEquals;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import de.juette.dlsa.MyDateRangeValidator;
import de.juette.dlsa.filter.MyDateRangeFilter;

public class TestFilter {

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
