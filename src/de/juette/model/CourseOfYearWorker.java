package de.juette.model;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.juette.dlsa.FileHandler;

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

	public File runCourseOfYear(Boolean finalize) {
		Date from;
		Date to;
		try {
			from = new SimpleDateFormat("dd.MM.yyyy").parse(settings
					.getDueDate() + "." + (year.getYear() - 1));
			to = new SimpleDateFormat("dd.MM.yyyy").parse(settings.getDueDate()
					+ "." + year.getYear());
			// List of all history entries made in this year
			List<Long> historyIds = new ArrayList<Long>();
			for (Log l : HibernateUtil.getHistoryIdsFromYear(from,to)) {
				historyIds.add(l.getChangedMemberId());
			}
			
			List<String> lines = new ArrayList<String>();
			lines.add("MitgliedNummer;Familienname;Vorname;Alter;geleistete Dls;benötigte Dls;zu Zahlen in Euro;manuell betrachten");

			for (Member m : members) {
				double requiredDls = settings.getCountDls();
				double madeDls = 0;
				for (Group g : m.getGroups()) {
					// If the member is liberated from paying DLS because he is
					// a member of a group which liberates
					if (g.getLiberated()) {
						requiredDls = 0;
						break;
					}
				}
				// If the member is not active anymore
				if (!m.getActive()) {
					requiredDls = 0;
				}
				// Member data was changed during the year, this member must be
				// marked as dirty
				Boolean isDirty = historyIds.contains(m.getId());

				System.out.println("----------");
				System.out.println(m.getFullName());
				List<Booking> bookings = HibernateUtil.getBookingsFromYear(m,
						from, to);
				for (Booking b : bookings) {
					System.out.println(b.getComment());
					madeDls += b.getCountDls();
				}
				double doneDls = 0;
				if (requiredDls > 0) {
					doneDls = requiredDls - Math.round(madeDls * 10) / 10;
				}
				lines.add(m.getMemberId() + ";" + 
						m.getSurname()  + ";" + 
						m.getForename() + ";" + 
						getAge(m.getBirthdate()) + ";" + 
						Math.round(madeDls * 10) / 10 + ";" + 
						requiredDls + ";" + 
						doneDls * settings.getCostDls() + ";" + 
						isDirty);
				System.out.println("----------");
			}
			FileHandler fh = new FileHandler();
			return fh.writeCsvFile(to, lines, finalize);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private int getAge(Date date)
    {
		if (date != null) {
			GregorianCalendar birthd = new GregorianCalendar();
	        birthd.setTime(date);
	       
	        GregorianCalendar today = new GregorianCalendar();
	       
	        int year = today.get(Calendar.YEAR) - birthd.get(Calendar.YEAR);
	       
	        if(today.get(Calendar.MONTH) <= birthd.get(Calendar.MONTH))
	        {
	            if(today.get(Calendar.DATE) < birthd.get(Calendar.DATE))
	            {
	                year -= 1;
	            }
	        }
	       
	        if(year < 0)
	            throw new IllegalArgumentException("invalid age: "+year);
	       
	        return year;
		}
        return 0;
    }
}
