package de.juette.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.juette.dlsa.MyDlsValidator;

public class TestBooking {
	
	private MyDlsValidator gv;
	
	@Before
	public void initialize() {
		gv = new MyDlsValidator("Keine");
	}
	
	@Test
	public void testGranularityNone() {
		assertEquals(true, gv.checkGranularity(2));
		assertEquals(true, gv.checkGranularity(2.5));
		assertEquals(true, gv.checkGranularity(2.25));
		assertEquals(true, gv.checkGranularity(2.75));
		assertEquals(true, gv.checkGranularity(2.15));
		assertEquals(true, gv.checkGranularity(2.00000000000001));
		assertEquals(true, gv.checkGranularity(2.99999999999999));
	}
	
	@Test
	public void testGranularityFull() {
		gv.setGranularity("Ganze");
		assertEquals(true, gv.checkGranularity(2));
		assertEquals(false, gv.checkGranularity(2.5));
		assertEquals(false, gv.checkGranularity(2.25));
		assertEquals(false, gv.checkGranularity(2.75));
		assertEquals(false, gv.checkGranularity(2.15));
		assertEquals(false, gv.checkGranularity(2.00000000000001));
		assertEquals(false, gv.checkGranularity(2.99999999999999));
	}
	
	@Test
	public void testGranularityHalf() {
		gv.setGranularity("Halbe");
		assertEquals(true, gv.checkGranularity(2));
		assertEquals(true, gv.checkGranularity(2.5));
		assertEquals(false, gv.checkGranularity(2.25));
		assertEquals(false, gv.checkGranularity(2.75));
		assertEquals(false, gv.checkGranularity(2.15));
		assertEquals(false, gv.checkGranularity(2.00000000000001));
		assertEquals(false, gv.checkGranularity(2.99999999999999));
	}
	
	@Test
	public void testGranularityQuarter() {
		gv.setGranularity("Viertel");
		assertEquals(true, gv.checkGranularity(2));
		assertEquals(true, gv.checkGranularity(2.5));
		assertEquals(true, gv.checkGranularity(2.25));
		assertEquals(true, gv.checkGranularity(2.75));
		assertEquals(false, gv.checkGranularity(2.15));
		assertEquals(false, gv.checkGranularity(2.00000000000001));
		assertEquals(false, gv.checkGranularity(2.99999999999999));
	}
}
