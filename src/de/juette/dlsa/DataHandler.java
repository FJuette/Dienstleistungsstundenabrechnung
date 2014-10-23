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
import de.juette.model.Cycle;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Log;
import de.juette.model.Member;
import de.juette.model.Role;
import de.juette.model.Settings;
import de.juette.model.Subject;
import de.juette.model.User;

public class DataHandler {

	public static BeanItemContainer<Subject> getDummySubjects() {
		BeanItemContainer<Subject> subjects = new BeanItemContainer<Subject>(
				Subject.class);
		subjects.addItem(new Subject("Freizeitsportler"));
		subjects.addItem(new Subject("Gelegenheitssportler"));
		subjects.addItem(new Subject("Rutinesportler"));
		subjects.addItem(new Subject("Leistungssportler"));
		return subjects;
	}

	public static void createDummySubjects() {
		ArrayList<Subject> entrys = new ArrayList<Subject>(Arrays.asList(
				new Subject("Freizeitsportler"), new Subject(
						"Gelegenheitssportler"), new Subject("Rutinesportler"),
				new Subject("Leistungssportler")));
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

		ArrayList<Subject> sSubjects = new ArrayList<Subject>();
		sSubjects.add(getDummySubjects().getIdByIndex(2));
		sSubjects.add(getDummySubjects().getIdByIndex(3));

		ArrayList<Group> jGroups = new ArrayList<Group>();
		jGroups.add(getDummyGroups().getIdByIndex(2));
		jGroups.add(getDummyGroups().getIdByIndex(3));

		ArrayList<Subject> jSubjects = new ArrayList<Subject>();
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

	@SuppressWarnings("unchecked")
	public static void createDummyMember() {
		ArrayList<Group> sGroups = (ArrayList<Group>) HibernateUtil
				.getAllAsList(Group.class);
		ArrayList<Subject> sSubjects = (ArrayList<Subject>) HibernateUtil
				.getAllAsList(Subject.class);

		sGroups.remove(1);
		sGroups.remove(2);
		sSubjects.remove(0);
		sSubjects.remove(1);

		ArrayList<Group> jGroups = (ArrayList<Group>) HibernateUtil
				.getAllAsList(Group.class);
		ArrayList<Subject> jSubjects = (ArrayList<Subject>) HibernateUtil
				.getAllAsList(Subject.class);

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

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	public static void createDummyBookings() {
		ArrayList<Member> members = (ArrayList<Member>) HibernateUtil
				.getAllAsList(Member.class);

		ArrayList<Campaign> activities = (ArrayList<Campaign>) HibernateUtil
				.getAllAsList(Campaign.class);

		ArrayList<Booking> entrys;
		try {
			entrys = new ArrayList<Booking>(Arrays.asList(
					new Booking(2, "Erste Buchung", new SimpleDateFormat(
							"dd.MM.yyyy").parse("01.10.2014"), members.get(3),
							activities.get(1)),
					new Booking(3, "Zweite Buchung", new SimpleDateFormat(
							"dd.MM.yyyy").parse("11.01.2014"), members.get(2),
							activities.get(2))));

			HibernateUtil.saveAll(entrys);
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

	@SuppressWarnings("unchecked")
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
							getDummyMembers().getIdByIndex(1)));
			logEntrys
					.addItem(new Log(
							new SimpleDateFormat("dd.MM.yyyy hh:mm")
									.parse("14.03.2014 17:53"),
							"Für das Mitglied Tom Tester sind für die Aktion (Erste Aktion) 4 Dienstleistungssunden verbucht",
							getDummyMembers().getIdByIndex(0)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return logEntrys;
	}

	@SuppressWarnings("unchecked")
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
									members.get(1)),
							new Log(
									new SimpleDateFormat("dd.MM.yyyy hh:mm")
											.parse("14.03.2014 17:53"),
									"Für das Mitglied Tom Tester sind für die Aktion (Erste Aktion) 4 Dienstleistungssunden verbucht",
									members.get(0))));

			HibernateUtil.saveAll(entrys);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static BeanItemContainer<Cycle> getDummyCycles() {
		BeanItemContainer<Cycle> cycles = new BeanItemContainer<Cycle>(
				Cycle.class);
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

		ArrayList<Cycle> entrys;
		try {
			entrys = new ArrayList<Cycle>(Arrays.asList(new Cycle(bFile,
					new SimpleDateFormat("dd.MM.yyyy hh:mm")
							.parse("13.03.2014 10:23"),
					"Jahreslauf vom 31.03.2013", "2013-03-31_Jahreslauf.csv")));
			HibernateUtil.saveAll(entrys);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createDummySettings() {
		ArrayList<Settings> entrys = new ArrayList<Settings>(
				Arrays.asList(new Settings("31.12", 5, (double) 10, 18, 67,
						"Anteilig bis zum Stichtag", true, false)));
		HibernateUtil.saveAll(entrys);
	}
}
