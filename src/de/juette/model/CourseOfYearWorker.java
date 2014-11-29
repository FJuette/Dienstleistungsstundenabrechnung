package de.juette.model;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CourseOfYearWorker {
	private Settings settings;
	private Year year;
	private Member member;
	private List<Booking> bookings;
	private DateTime fromDate;
	private DateTime toDate;
	private DateTime lastCOYDueDate;
	
	private final DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
	
	public CourseOfYearWorker(Year year, Settings settings) {
		this.settings = settings;
		this.year = year;
		
		calculateDates();
	}
	
	public CourseOfYearWorker(Year year, Settings settings, Date lastCOYDueDate) {
		this.settings = settings;
		this.year = year;
		this.setLastCOYDueDate(new DateTime(lastCOYDueDate));
		
		calculateDates();
	}
	
	private void calculateDates() {
		if (year != null && settings != null && settings.getDueDate() != null) {
			toDate = dateStringFormat.parseDateTime(settings.getDueDate() + "." + (year.getYear()));
			
			fromDate = dateStringFormat.parseDateTime(settings.getDueDate() + "." + (year.getYear()));
			fromDate = fromDate.minusYears(1);
			fromDate = fromDate.plusDays(1);
			
			// Did the due date change since the last course of year?
			if (lastCOYDueDate != null && fromDate.isBefore(lastCOYDueDate)) {
				fromDate = lastCOYDueDate.plusDays(1);
			} else {
				
			}
		}
	}

	public Boolean isMemberLiberated() {
		// Member is not active at the moment
		if (member.getActive() != null && !member.getActive()) {
			return true;
		}
		// Member is too Young
		if (member.getAge(toDate) < settings.getAgeFrom()) {
			return true;
		}
		// Member is too old
		if (member.getAge(fromDate) >= settings.getAgeTo()) {
			return true;
		}
		// Entry date is after the due date
		if ( member.getEntryDate() != null && (new DateTime(member.getEntryDate())).isAfter(toDate) ) {
			return true;
		}
		// Leaving date is before the due date
		if ( member.getLeavingDate() != null && new DateTime(member.getLeavingDate()).isBefore(fromDate) ) {
			return true;
		}
		// Member is part of a group which liberates him at the moment
		for (Group g : member.getGroups()) {
			if (g.getLiberated()) {
				return true;
			}
		}
		// Default is false
		return false;
	}
	
	public double getMemberDebit(List<Booking> bookings, int month) {
		double sum = 0;
		// Calculating the sum of all DLS
		for (Booking b : bookings) {
			DateTime dDate = new DateTime(b.getDoneDate());
			// Only use bookings from the timespan
			if ( (fromDate.isBefore(dDate) || fromDate.isEqual(dDate) ) && 
					(toDate.isAfter(dDate) || toDate.isEqual(dDate)) ) {
				sum += b.getCountDls();
			}
		}
		sum = (settings.getCountDls() / 12) * month - sum;
		// If the sum is lower than 0, the member has no debt
		if (sum < 0) {
			sum = 0;
		}
		double debit = sum * settings.getCostDls();
		return debit;
	}
	

	public DateTime getFromDate() {
		return fromDate;
	}

	public DateTime getToDate() {
		return toDate;
	}
	
	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public DateTime getLastCOYDueDate() {
		return lastCOYDueDate;
	}

	public void setLastCOYDueDate(DateTime lastCOYDueDate) {
		this.lastCOYDueDate = lastCOYDueDate;
	}
}
