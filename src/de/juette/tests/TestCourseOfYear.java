package de.juette.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import de.juette.model.Booking;
import de.juette.model.CourseOfYearWorker;
import de.juette.model.Group;
import de.juette.model.Member;
import de.juette.model.Settings;
import de.juette.model.Year;

public class TestCourseOfYear {
	DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");

	@Test
	public void testNewInstance() {
		CourseOfYearWorker worker = new CourseOfYearWorker(new Year(), new Settings());
		assertTrue(worker instanceof CourseOfYearWorker);
	}
	
	@Test
	public void testYear() {
		CourseOfYearWorker worker = new CourseOfYearWorker(new Year(2014), new Settings());
		assertEquals(2014, worker.getYear().getYear());
	}
	
	@Test
	public void testSettings() {
		Settings s = new Settings();
		s.setDueDate("31.12");
		CourseOfYearWorker worker = new CourseOfYearWorker(new Year(), s);
		assertEquals("31.12", worker.getSettings().getDueDate());
	}
	
	@Test
	public void testTimespan() {
		Settings s = new Settings();
		s.setDueDate("31.12");
		CourseOfYearWorker worker = new CourseOfYearWorker(new Year(2014), s, dateStringFormat.parseDateTime("31.12.2013").toDate());
		assertEquals(dateStringFormat.parseDateTime("01.01.2014"), worker.getFromDate());
		assertEquals(dateStringFormat.parseDateTime("31.12.2014"), worker.getToDate());
		
		s.setDueDate("01.01");
		worker = new CourseOfYearWorker(new Year(2014), s);
		assertEquals(dateStringFormat.parseDateTime("02.01.2013"), worker.getFromDate());
		assertEquals(dateStringFormat.parseDateTime("01.01.2014"), worker.getToDate());
		
		// 29 February exists
		s.setDueDate("28.02");
		worker = new CourseOfYearWorker(new Year(2013), s);
		assertEquals(dateStringFormat.parseDateTime("28.02.2013"), worker.getToDate());
		assertEquals(dateStringFormat.parseDateTime("29.02.2012"), worker.getFromDate());
		
		// 29 February not exists
		s.setDueDate("28.02");
		worker = new CourseOfYearWorker(new Year(2014), s);
		assertEquals(dateStringFormat.parseDateTime("28.02.2014"), worker.getToDate());
		assertEquals(dateStringFormat.parseDateTime("01.03.2013"), worker.getFromDate());
		
		// There due date changed since the last course of year
		s.setDueDate("31.12");
		worker = new CourseOfYearWorker(new Year(2014), s, dateStringFormat.parseDateTime("01.02.2014").toDate());
		assertEquals(dateStringFormat.parseDateTime("02.02.2014"), worker.getFromDate());
		assertEquals(dateStringFormat.parseDateTime("31.12.2014"), worker.getToDate());
	}

	@Test
	public void testLiberate() {
		Settings s = new Settings();
		s.setDueDate("31.12");
		s.setAgeFrom(18);
		s.setAgeTo(67);
		Member m = new Member();
		CourseOfYearWorker worker = new CourseOfYearWorker(new Year(2013), s);
		
		// Member is not active in the current state
		m.setActive(false);
		worker.setMember(m);
		assertEquals(true, worker.isMemberLiberated());
		
		// For further tests the member is active
		m.setActive(true);
		
		// Member is too young for DLS
		m.setBirthdate(dateStringFormat.parseDateTime("01.01.1996").toDate());
		worker.setMember(m);
		assertEquals(true, worker.isMemberLiberated());
		
		// Member is too old for DLS
		m.setBirthdate(dateStringFormat.parseDateTime("31.12.1945").toDate());
		worker.setMember(m);
		assertEquals(true, worker.isMemberLiberated());
		
		// Age is OK, test the entry date
		m.setBirthdate(dateStringFormat.parseDateTime("27.05.1987").toDate());
		
		// Member joined later than the due date
		m.setEntryDate(dateStringFormat.parseDateTime("01.01.2014").toDate());
		worker.setMember(m);
		assertEquals(true, worker.isMemberLiberated());
		
		// Entry date is before the due date
		m.setEntryDate(dateStringFormat.parseDateTime("01.01.1999").toDate());
		worker.setMember(m);
		assertEquals(false, worker.isMemberLiberated());
		
		// Leaving date is before the from date
		m.setLeavingDate(dateStringFormat.parseDateTime("31.12.2012").toDate());
		worker.setMember(m);
		assertEquals(true, worker.isMemberLiberated());
		
		// Leaving date is equal the from date
		m.setLeavingDate(dateStringFormat.parseDateTime("01.01.2014").toDate());
		worker.setMember(m);
		assertEquals(false, worker.isMemberLiberated());

		// Member is liberated by a group
		List<Group> groups = new ArrayList<Group>();
		Group g = new Group();
		g.setLiberated(true);
		groups.add(g);
		m.setGroups(groups);
		worker.setMember(m);
		assertEquals(true, worker.isMemberLiberated());
		
		// Member is not liberated by a group
		groups = new ArrayList<Group>();
		g = new Group();
		g.setLiberated(false);
		groups.add(g);
		m.setGroups(groups);
		worker.setMember(m);
		assertEquals(false, worker.isMemberLiberated());
		
		// Member is in 2 groups where none liberates
		g = new Group();
		g.setLiberated(false);
		groups.add(g);
		m.setGroups(groups);
		worker.setMember(m);
		assertEquals(false, worker.isMemberLiberated());
		
		// Member is in 2 groups where one liberates
		groups.get(1).setLiberated(true);
		m.setGroups(groups);
		worker.setMember(m);
		assertEquals(true, worker.isMemberLiberated());
	}
	
	@Test
	public void testDebitOfMember() {
		Settings s = new Settings();
		s.setDueDate("31.12");
		s.setCountDls(10.0);
		s.setCostDls(5.0);
		CourseOfYearWorker worker = new CourseOfYearWorker(new Year(2013), s);
		List<Booking> bookings = new ArrayList<Booking>();
		Booking b = new Booking();
		b.setCountDls(3);
		b.setDoneDate(dateStringFormat.parseDateTime("01.01.2013").toDate());
		bookings.add(b);
		
		b = new Booking();
		b.setCountDls(1);
		b.setDoneDate(dateStringFormat.parseDateTime("31.12.2013").toDate());
		bookings.add(b);
		
		b = new Booking();
		b.setCountDls(1);
		b.setDoneDate(dateStringFormat.parseDateTime("05.05.2013").toDate());
		bookings.add(b);
		
		b = new Booking();
		b.setCountDls(1);
		b.setDoneDate(dateStringFormat.parseDateTime("31.12.2012").toDate());
		bookings.add(b);
		// Normal member, 
		// made 3 DLS at the very beginning
		// 1 at the end
		// 1 in the middle
		// 1 outside
		assertEquals(25, worker.getMemberDebit(bookings, 12), 0.01);
		
		// No Bookings in the timespan
		bookings = new ArrayList<Booking>();
		b = new Booking();
		b.setCountDls(1);
		b.setDoneDate(dateStringFormat.parseDateTime("31.12.2012").toDate());
		bookings.add(b);
		assertEquals(50, worker.getMemberDebit(bookings, 12), 0.01);
		
		// Made enough DLS -> nothing to pay
		b = new Booking();
		b.setCountDls(11);
		b.setDoneDate(dateStringFormat.parseDateTime("10.04.2013").toDate());
		bookings.add(b);
		assertEquals(0, worker.getMemberDebit(bookings, 12), 0.01);
		
		// Some DLS and only a few month
		bookings = new ArrayList<Booking>();
		b = new Booking();
		b.setCountDls(1);
		b.setDoneDate(dateStringFormat.parseDateTime("02.02.2013").toDate());
		bookings.add(b);
		assertEquals( ( (10.0 / 12.0 * 4.0) - 1.0) * 5.0, worker.getMemberDebit(bookings, 4), 0.01);
		
		// No DLS and only 1 month
		bookings = new ArrayList<Booking>();
		assertEquals( 10.0 / 12.0 * 1.0 * 5.0, worker.getMemberDebit(bookings, 1), 0.01);
	}

	//@Test
	public void testFullMonthCalc() {
		Settings s = new Settings();
		s.setDueDate("01.08");
		CourseOfYearWorker worker = new CourseOfYearWorker(new Year(2013), s);
		
		// Not liberated, no changes Member
		Member m = new Member();
		m.setActive(true);
		m.setBirthdate(dateStringFormat.parseDateTime("27.05.1987").toDate());
		m.setEntryDate(dateStringFormat.parseDateTime("01.01.1999").toDate());
		
		List<Group> groups =  new ArrayList<Group>();
		m.setGroups(groups);
		
		worker.setMember(m);
		assertEquals(12, worker.getFullDlsMonth());
	}
}
