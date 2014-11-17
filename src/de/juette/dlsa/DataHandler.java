package de.juette.dlsa;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.shiro.crypto.hash.Sha256Hash;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinService;

import de.juette.model.Campaign;
import de.juette.model.Booking;
import de.juette.model.ColumnMapping;
import de.juette.model.CourseOfYear;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Log;
import de.juette.model.Member;
import de.juette.model.Role;
import de.juette.model.Settings;
import de.juette.model.Category;
import de.juette.model.User;
import de.juette.model.Year;

public class DataHandler {

	public static BeanItemContainer<Category> getDummySubjects() {
		BeanItemContainer<Category> subjects = new BeanItemContainer<Category>(
				Category.class);
		subjects.addItem(new Category("Freizeitsportler"));
		subjects.addItem(new Category("Gelegenheitssportler"));
		subjects.addItem(new Category("Rutinesportler"));
		subjects.addItem(new Category("Leistungssportler"));
		return subjects;
	}

	public static void createDummySubjects() {
		ArrayList<Category> entrys = new ArrayList<Category>(Arrays.asList(
				new Category("Freizeitsportler"), new Category(
						"Gelegenheitssportler"),
				new Category("Rutinesportler"), new Category(
						"Leistungssportler")));
		HibernateUtil.saveAll(entrys);
	}

	public static BeanItemContainer<Group> getDummyGroups() {
		BeanItemContainer<Group> groups = new BeanItemContainer<Group>(
				Group.class);
		groups.addItem(new Group("Trainer", true));
		groups.addItem(new Group("Vorstand", true));
		groups.addItem(new Group("Mitglied", false));
		groups.addItem(new Group("Aufsicht", false));
		return groups;
	}

	public static void createDummyGroups() {
		ArrayList<Group> entrys = new ArrayList<Group>(Arrays.asList(new Group(
				"Trainer", true), new Group("Vorstand", true), new Group(
				"Mitglied", false), new Group("Aufsicht", false)));
		HibernateUtil.saveAll(entrys);
	}

	public static BeanItemContainer<Member> getDummyMembers() {
		BeanItemContainer<Member> members = new BeanItemContainer<Member>(
				Member.class);

		ArrayList<Group> sGroups = new ArrayList<Group>();
		sGroups.add(getDummyGroups().getIdByIndex(0));
		sGroups.add(getDummyGroups().getIdByIndex(1));

		ArrayList<Category> sSubjects = new ArrayList<Category>();
		sSubjects.add(getDummySubjects().getIdByIndex(2));
		sSubjects.add(getDummySubjects().getIdByIndex(3));

		ArrayList<Group> jGroups = new ArrayList<Group>();
		jGroups.add(getDummyGroups().getIdByIndex(2));
		jGroups.add(getDummyGroups().getIdByIndex(3));

		ArrayList<Category> jSubjects = new ArrayList<Category>();
		jSubjects.add(getDummySubjects().getIdByIndex(0));
		jSubjects.add(getDummySubjects().getIdByIndex(1));

		try {
			members.addItem(new Member("Sander", "Thorsten", "123456",
					new SimpleDateFormat("dd.MM.yyyy").parse("11.01.2014"),
					sGroups, sSubjects));
			members.addItem(new Member("Juette", "Fabian", "987654",
					new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2014"),
					jGroups, jSubjects));
			members.addItem(new Member("Tester", "Tom", "565645",
					new SimpleDateFormat("dd.MM.yyyy").parse("25.03.2000"),
					sGroups, sSubjects));
			members.addItem(new Member("Juppie", "Jörn", "848484",
					new SimpleDateFormat("dd.MM.yyyy").parse("14.12.19987"),
					jGroups, jSubjects));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return members;
	}

	public static void createDummyMember() {
		ArrayList<Group> sGroups = (ArrayList<Group>) HibernateUtil.getAllAsList(Group.class);
		ArrayList<Category> sSubjects = (ArrayList<Category>) HibernateUtil.getAllAsList(Category.class);

		sGroups.remove(1);
		sGroups.remove(2);
		sSubjects.remove(0);
		sSubjects.remove(1);

		ArrayList<Group> jGroups = (ArrayList<Group>) HibernateUtil
				.getAllAsList(Group.class);
		ArrayList<Category> jSubjects = (ArrayList<Category>) HibernateUtil
				.getAllAsList(Category.class);

		jGroups.remove(0);
		jGroups.remove(2);
		jSubjects.remove(1);
		jSubjects.remove(2);

		try {
			ArrayList<Member> entrys = new ArrayList<Member>(Arrays.asList(
					new Member("Sander", "Thorsten", "123456",
							new SimpleDateFormat("dd.MM.yyyy")
									.parse("11.01.2014"), sGroups, sSubjects),
					new Member("Juette", "Fabian", "987654",
							new SimpleDateFormat("dd.MM.yyyy")
									.parse("01.01.2014"), jGroups, jSubjects),
					new Member("Tester", "Tom", "565645", new SimpleDateFormat(
							"dd.MM.yyyy").parse("25.03.2000"), sGroups,
							sSubjects),
					new Member("Juppie", "Jörn", "848484",
							new SimpleDateFormat("dd.MM.yyyy")
									.parse("14.12.19987"), jGroups, jSubjects)));
			HibernateUtil.saveAll(entrys);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static BeanItemContainer<Campaign> getDummyActivities() {
		BeanItemContainer<Campaign> activities = new BeanItemContainer<>(
				Campaign.class);
		activities.addItem(new Campaign("2014", "Erste Aktion",
				getDummyMembers().getIdByIndex(1)));
		activities.addItem(new Campaign("2014", "Zweite Aktion",
				getDummyMembers().getIdByIndex(2)));
		activities.addItem(new Campaign("2014", "Dritte Aktion",
				getDummyMembers().getIdByIndex(3)));
		return activities;
	}

	public static void createDummyActivities() {
		ArrayList<Member> members = (ArrayList<Member>) HibernateUtil
				.getAllAsList(Member.class);

		ArrayList<Campaign> entrys = new ArrayList<Campaign>(Arrays.asList(
				new Campaign("2014", "Erste Aktion", members.get(1)),
				new Campaign("2014", "Zweite Aktion", members.get(2)),
				new Campaign("2014", "Dritte Aktion", members.get(3))));
		HibernateUtil.saveAll(entrys);
	}

	public static BeanItemContainer<Booking> getDummyBookings() {
		BeanItemContainer<Booking> bookings = new BeanItemContainer<Booking>(
				Booking.class);
		try {
			bookings.addItem(new Booking(2, "Erste Buchung",
					new SimpleDateFormat("dd.MM.yyyy").parse("01.10.2014"),
					getDummyMembers().getIdByIndex(3), getDummyActivities()
							.getIdByIndex(1)));
			bookings.addItem(new Booking(3, "Zweite Buchung",
					new SimpleDateFormat("dd.MM.yyyy").parse("11.01.2014"),
					getDummyMembers().getIdByIndex(2), getDummyActivities()
							.getIdByIndex(2)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bookings;
	}

	public static void createDummyBookings() {
		ArrayList<Member> members = (ArrayList<Member>) HibernateUtil
				.getAllAsList(Member.class);

		ArrayList<Campaign> activities = (ArrayList<Campaign>) HibernateUtil
				.getAllAsList(Campaign.class);

		ArrayList<Booking> entrys;
		ArrayList<Year> years;
		try {
			entrys = new ArrayList<Booking>(Arrays.asList(
					new Booking(2, "Buchung mitte 2013", new SimpleDateFormat(
							"dd.MM.yyyy").parse("01.10.2013"), members.get(3),
							activities.get(1)),
					new Booking(2.1, "Buchung 01.01.2014", new SimpleDateFormat(
									"dd.MM.yyyy").parse("01.01.2014"), members.get(3),
									activities.get(1)),
					new Booking(2.2, "Buchung 31.12.2014", new SimpleDateFormat(
							"dd.MM.yyyy").parse("31.12.2014"), members.get(3),
							activities.get(2)),
					new Booking(2.4, "Buchung 31.12.2013", new SimpleDateFormat(
							"dd.MM.yyyy").parse("31.12.2013"), members.get(3),
							activities.get(2)),
					new Booking(2.5, "Buchung 01.01.2013", new SimpleDateFormat(
							"dd.MM.yyyy").parse("01.01.2013"), members.get(3),
							activities.get(2)),
					new Booking(2.6, "Buchung 30.12.2013", new SimpleDateFormat(
							"dd.MM.yyyy").parse("30.12.2013"), members.get(3),
							activities.get(2)),
					new Booking(3, "Buchung mitte 2014", new SimpleDateFormat(
							"dd.MM.yyyy").parse("11.01.2014"), members.get(2),
							activities.get(2))));
			years = new ArrayList<Year>(Arrays.asList(new Year(2013), new Year(2014)));
			HibernateUtil.saveAll(entrys);
			HibernateUtil.saveAll(years);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static BeanItemContainer<Role> getDummyRoles() {
		BeanItemContainer<Role> roles = new BeanItemContainer<Role>(Role.class);
		roles.addItem(new Role("Admin"));
		roles.addItem(new Role("Benutzer"));
		return roles;
	}

	public static void createDummyRoles() {
		ArrayList<Role> entrys = new ArrayList<Role>(Arrays.asList(new Role(
				"Administrator"), new Role("Benutzer"), new Role("Gast")));
		HibernateUtil.saveAll(entrys);
	}

	public static BeanItemContainer<User> getDummyUsers() {
		BeanItemContainer<User> users = new BeanItemContainer<User>(User.class);
		users.addItem(new User("Administrator", "geheim", true, getDummyRoles()
				.getIdByIndex(0)));
		users.addItem(new User("Benutzer1", "nochgeheimer", true,
				getDummyRoles().getIdByIndex(1)));
		users.addItem(new User("Inaktiver", "geheim", false, getDummyRoles()
				.getIdByIndex(1)));
		return users;
	}

	public static void createDummyUsers() {
		ArrayList<Role> roles = (ArrayList<Role>) HibernateUtil
				.getAllAsList(Role.class);

		ArrayList<User> entrys = new ArrayList<User>(Arrays.asList(
				new User("admin", new Sha256Hash("admin").toString(), true,
						roles.get(0)), new User("Benutzer 1", new Sha256Hash(
						"geheime").toString(), true, roles.get(1))));
		HibernateUtil.saveAll(entrys);
	}

	public static BeanItemContainer<Log> getDummyLog() {
		BeanItemContainer<Log> logEntrys = new BeanItemContainer<Log>(Log.class);
		try {
			logEntrys
					.addItem(new Log(
							new SimpleDateFormat("dd.MM.yyyy hh:mm")
									.parse("13.03.2014 10:23"),
							"Benutzer Juette bearbeitet, veränderte(s) Feld(er): Nachname",
							"Fabian Jütte", "Fabian Jütte"));
			logEntrys
					.addItem(new Log(
							new SimpleDateFormat("dd.MM.yyyy hh:mm")
									.parse("14.03.2014 17:53"),
							"Für das Mitglied Tom Tester sind für die Aktion (Erste Aktion) 4 Dienstleistungssunden verbucht",
							"Fabian Jütte", "Fabian Jütte"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return logEntrys;
	}

	public static void createDummyLogs() {
		ArrayList<Member> members = (ArrayList<Member>) HibernateUtil
				.getAllAsList(Member.class);

		ArrayList<Log> entrys;
		try {
			entrys = new ArrayList<Log>(
					Arrays.asList(
							new Log(
									new SimpleDateFormat("dd.MM.yyyy hh:mm")
											.parse("13.03.2014 10:23"),
									"Benutzer Juette bearbeitet, veränderte(s) Feld(er): Nachname",
									"Fabian Jütte", "Fabian Jütte"),
							new Log(
									new SimpleDateFormat("dd.MM.yyyy hh:mm")
											.parse("14.03.2014 17:53"),
									"Für das Mitglied Tom Tester sind für die Aktion (Erste Aktion) 4 Dienstleistungssunden verbucht",
									"Fabian Jütte", "Fabian Jütte")));

			HibernateUtil.saveAll(entrys);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static BeanItemContainer<CourseOfYear> getDummyCycles() {
		BeanItemContainer<CourseOfYear> cycles = new BeanItemContainer<CourseOfYear>(
				CourseOfYear.class);
		@SuppressWarnings("unused")
		File file = new File(VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath()
				+ "/WEB-INF/Files/ExampleResult.csv");
		return cycles;
	}

	public static void createDummyCycles() {
		File file = new File(VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath()
				+ "/WEB-INF/Files/ExampleResult.csv");
		byte[] bFile = new byte[(int) file.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			// convert file into array of bytes
			fileInputStream.read(bFile);
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<CourseOfYear> entrys;
		try {
			entrys = new ArrayList<CourseOfYear>(
					Arrays.asList(new CourseOfYear(bFile, new SimpleDateFormat(
							"dd.MM.yyyy hh:mm").parse("13.03.2014 10:23"),
							"Jahreslauf vom 31.03.2013",
							"2013-03-31_Jahreslauf.csv")));
			HibernateUtil.saveAll(entrys);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void createDummySettings() {
		ArrayList<Settings> entrys = new ArrayList<Settings>(
				Arrays.asList(new Settings("31.12", 5, (double) 10, 18, 67,
						"Anteilig bis zum Stichtag", true, false)));
		HibernateUtil.saveAll(entrys);
	}

	public static void createMappingEntrys() {
		ArrayList<ColumnMapping> entrys = new ArrayList<ColumnMapping>(
				Arrays.asList(new ColumnMapping("surname", "Nachname"), new ColumnMapping(
						"forename", "Vorname"), new ColumnMapping("memberId", "Mitgliedsnummer"),
						new ColumnMapping("entryDate", "Eintrittsdatum"), new ColumnMapping(
								"leavingDate", "Austrittsdatum"), new ColumnMapping(
								"categoryName", "Sparte"), new ColumnMapping("birthdate", "Geburtsdatum")));
		HibernateUtil.saveAll(entrys);
	}
}
