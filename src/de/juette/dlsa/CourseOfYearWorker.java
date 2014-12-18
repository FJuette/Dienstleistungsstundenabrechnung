package de.juette.dlsa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.juette.model.Booking;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.Settings;
import de.juette.model.Year;

public class CourseOfYearWorker {
	private Settings settings;
	private Year year;
	private Member member;
	private List<Booking> bookings;
	private DateTime fromDate;
	private DateTime toDate;
	private DateTime lastCOYDueDate;
	private Double achievedDls;
	private int monthCount;
	
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
	
	// Tested
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

	// Tested
	public Boolean isMemberLiberated() {
		return getMemberLiberated(member);
	}
	
	private Boolean getMemberLiberated(Member m) {
		// Member is not active at the moment
		if (m.getActive() != null && !m.getActive()) {
			return true;
		}
		// Member is too Young
		if (m.getAge(toDate) != -1 && m.getAge(toDate) < settings.getAgeFrom()) {
			return true;
		}
		// Member is too old
		if (m.getAge(fromDate) != -1 && m.getAge(fromDate) >= settings.getAgeTo()) {
			return true;
		}
		// Entry date is after the due date
		if ( m.getEntryDate() != null && (new DateTime(m.getEntryDate())).isAfter(toDate) ) {
			return true;
		}
		// Leaving date is before the due date
		if ( m.getLeavingDate() != null && new DateTime(m.getLeavingDate()).isBefore(fromDate) ) {
			return true;
		}
		// Member is part of a group which liberates him at the moment
		for (Group g : m.getGroups()) {
			if (g.getLiberated()) {
				return true;
			}
		}
		// Default is false
		return false;
	}
		
	// Tested
	public double getMemberDebit(List<Booking> bookings, int fullMonth) {
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
		achievedDls = sum;
		sum = getRequiredDls(fullMonth) - sum;
		// If the sum is lower than 0, the member has no debt
		if (sum < 0) {
			sum = 0;
		}
		double debit = sum * settings.getCostDls();
		return debit;
	}
	
	public double getRequiredDls(int fullMonth) {
		return (((double) Math.round(settings.getCountDls() / 12 * fullMonth * 10)) / 10);
	}
	
	// Number of non liberated months
	public int getFullDlsMonth() {
		monthCount = 0;
		DateTime month = getStartMonthDate(fromDate);
		List<DateTime> months = new ArrayList<DateTime>();
		do {
			months.add(month);
			month = getNextMonth(month);
			monthCount++;
		} while (month.isBefore(toDate) || month.isEqual(toDate));

		MemberInfo mi = new MemberInfo(member);
		int dlsLibMonth = 0;
		
		for (DateTime dt : months) {
			Member m = mi.getMemberStateFromDate(HibernateUtil.getBasicMember(member.getId()), getLastDayOfMonth(dt).plusDays(1).toDate());
			m.setBirthdate(member.getBirthdate());
			if (getMemberLiberated(m)) {
				dlsLibMonth++;
			}
		}
		return monthCount - dlsLibMonth;
	}
	
	private DateTime getNextMonth(DateTime dt) {
		return dt.plusDays(1).plusMonths(1).minusDays(1);
	}
	
	private DateTime getLastDayOfMonth(DateTime dt) {
		return dt.plusMonths(1).minusDays(1);
	}
	
	private DateTime getStartMonthDate(DateTime dt) {
		if (dt.equals(dt.withDayOfMonth(1))) {
			return dt;
		} else {
			return dt.plusMonths(1).withDayOfMonth(1);
		}
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

	public Double getAchievedDls() {
		return achievedDls;
	}

	public void setAchievedDls(Double achievedDls) {
		this.achievedDls = achievedDls;
	}

	public int getMonthCount() {
		return monthCount;
	}

	public void setMonthCount(int monthCount) {
		this.monthCount = monthCount;
	}
}
