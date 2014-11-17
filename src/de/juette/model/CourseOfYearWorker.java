package de.juette.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CourseOfYearWorker {
	private Settings settings;
	private Year year;
	private List<Member> members;

	public CourseOfYearWorker(Year year) {
		this.settings = (Settings) HibernateUtil.getUnique(Settings.class,
				"1=1");
		this.year = year;
		this.members = HibernateUtil.getAllAsList(Member.class);
	}

	public void runCourseOfYear(Boolean finalize) {
		Date from;
		Date to;
		try {
			from = new SimpleDateFormat("dd.MM.yyyy").parse(settings
					.getDueDate() + "." + (year.getYear() - 1));
			to = new SimpleDateFormat("dd.MM.yyyy").parse(settings.getDueDate()
					+ "." + year.getYear());

			for (Member m : members) {
				double cDls = -settings.getCountDls();
				System.out.println("----------");
				System.out.println(m.getFullName());
				List<Booking> bookings = HibernateUtil.getBookingsFromYear(m, from, to);
				for (Booking b : bookings) {
					System.out.println(b.getComment());
					cDls += b.getCountDls();
				}
				System.out.println("Anzahl Dls: " + Math.floor(cDls) + " Kosten: " + Math.floor(cDls) * settings.getCostDls() );
				System.out.println("----------");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
