package de.juette.tests;

import static org.junit.Assert.assertEquals;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import de.juette.dlsa.GeneralHandler;

public class TestGeneralHandler {

	DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
	
	@Test
	public void testRefDate() {
		assertEquals(false,
				GeneralHandler.validateRefDate(dateStringFormat.parseDateTime("31.12.2013"), 
						dateStringFormat.parseDateTime("30.12.2011").toDate())
			);
		assertEquals(false,
			GeneralHandler.validateRefDate(dateStringFormat.parseDateTime("31.12.2013"), 
					dateStringFormat.parseDateTime("30.12.2013").toDate())
		);
		assertEquals(false,
				GeneralHandler.validateRefDate(dateStringFormat.parseDateTime("31.12.2013"), 
						dateStringFormat.parseDateTime("31.12.2013").toDate())
		);
		assertEquals(true,
				GeneralHandler.validateRefDate(dateStringFormat.parseDateTime("31.12.2013"), 
						dateStringFormat.parseDateTime("01.01.2014").toDate())
		);
		assertEquals(true,
				GeneralHandler.validateRefDate(dateStringFormat.parseDateTime("31.12.2013"), 
						dateStringFormat.parseDateTime("10.10.2014").toDate())
		);
	}
}
