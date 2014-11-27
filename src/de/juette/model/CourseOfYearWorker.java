package de.juette.model;

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
	
	
	public CourseOfYearWorker(Year year, Settings settings) {
		this.settings = settings;
		this.year = year;
		
		calculateDates();
	}
	
	private void calculateDates() {
		if (year != null && settings != null && settings.getDueDate() != null) {
			DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("dd.MM.yyyy");
			fromDate = dateStringFormat.parseDateTime(settings.getDueDate() + "." + (year.getYear()));
			fromDate = fromDate.minusYears(1);
			fromDate = fromDate.minusDays(-1);
			
			toDate = dateStringFormat.parseDateTime(settings.getDueDate() + "." + (year.getYear()));
		}
	}

	public Boolean isMemberLiberated() {
		// Member is not active at the moment
		if (!member.getActive()) {
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
		if ( (new DateTime(member.getEntryDate())).isAfter(toDate) ) {
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
}
