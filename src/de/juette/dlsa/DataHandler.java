package de.juette.dlsa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.juette.model.Booking;
import de.juette.model.Campaign;
import de.juette.model.Category;
import de.juette.model.ColumnMapping;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.Role;
import de.juette.model.Settings;
import de.juette.model.User;
import de.juette.model.Year;

/**
 * Use this class to create default entries in the database or
 * to create dummy entries for testing
 * @author Fabian Juette
 */
public class DataHandler {

	static DateTimeFormatter dateStringFormat = DateTimeFormat
			.forPattern("dd.MM.yyyy");

	/**
	 * Creates the default roles of the system in the databse
	 */
	public static void createRoles() {
		ArrayList<Role> entrys = new ArrayList<Role>(Arrays.asList(new Role(
				"Administrator"), new Role("Benutzer"), new Role("Gast")));
		HibernateUtil.saveAll(entrys);
	}

	/**
	 * Creates the default administrator (user)
	 */
	public static void createAdminUser() {
		ArrayList<Role> roles = (ArrayList<Role>) HibernateUtil
				.getAllAsList(Role.class);

		ArrayList<User> entrys = new ArrayList<User>(
				Arrays.asList(new User("admin", new Sha256Hash("admin")
						.toString(), true, roles.get(0))));
		HibernateUtil.saveAll(entrys);
	}
	
	/**
	 * Sets the settings in the database to default, does not delete the old entries
	 */
	public static void createSettings() {
		ArrayList<Settings> entrys = new ArrayList<Settings>(
				Arrays.asList(new Settings("31.12", 5.0, 10.0, 18, 67,
						"Anteilig bis zum Stichtag", true, "Keine")));
		HibernateUtil.saveAll(entrys);
	}

	/**
	 * Creates the default mapping structure in the database.
	 * If you need more columns on the page change it here and @see getMappingEntrys()
	 */
	public static void createMappingEntrys() {
		ArrayList<ColumnMapping> entrys = new ArrayList<ColumnMapping>(
				Arrays.asList(new ColumnMapping("surname", "Nachname"),
						new ColumnMapping("forename", "Vorname"),
						new ColumnMapping("memberId", "Mitgliedsnummer"),
						new ColumnMapping("entryDate", "Eintrittsdatum"),
						new ColumnMapping("leavingDate", "Austrittsdatum"),
						new ColumnMapping("categoryName", "Sparte"),
						new ColumnMapping("birthdate", "Geburtsdatum")));
		HibernateUtil.saveAll(entrys);
	}

	/**
	 * Returns the mapping structure from the database
	 * @return List of mapping entries
	 */
	public static ArrayList<ColumnMapping> getMappingEntrys() {
		ArrayList<ColumnMapping> entrys = new ArrayList<ColumnMapping>(
				Arrays.asList(new ColumnMapping("surname", "Nachname"),
						new ColumnMapping("forename", "Vorname"),
						new ColumnMapping("memberId", "Mitgliedsnummer"),
						new ColumnMapping("entryDate", "Eintrittsdatum"),
						new ColumnMapping("leavingDate", "Austrittsdatum"),
						new ColumnMapping("categoryName", "Sparte"),
						new ColumnMapping("birthdate", "Geburtsdatum")));
		return entrys;
	}

	/**
	 * Method for creating dummy data
	 */
	public static void createDummyUsers() {
		ArrayList<Role> roles = (ArrayList<Role>) HibernateUtil
				.getAllAsList(Role.class);

		ArrayList<User> entrys = new ArrayList<User>(Arrays.asList(
				new User("admin", new Sha256Hash("admin").toString(), true,
						roles.get(0)), new User("Benutzer 1", new Sha256Hash(
						"geheime").toString(), true, roles.get(1))));
		HibernateUtil.saveAll(entrys);
	}

	/**
	 * Method for creating dummy data
	 */
	public static void createDummySubjects() {
		ArrayList<Category> entrys = new ArrayList<Category>(Arrays.asList(
				new Category("Freizeitsportler"), new Category(
						"Gelegenheitssportler"),
				new Category("Rutinesportler"), new Category(
						"Leistungssportler")));
		HibernateUtil.saveAll(entrys);
	}

	/**
	 * Method for creating dummy data
	 */
	public static void createDummyGroups() {
		ArrayList<Group> entrys = new ArrayList<Group>(Arrays.asList(new Group(
				"Trainer", true), new Group("Vorstand", true), new Group(
				"Mitglied", false), new Group("Aufsicht", false)));
		HibernateUtil.saveAll(entrys);
	}

	/**
	 * Method for creating dummy data
	 */
	public static void createDummyMember() {
		ArrayList<Group> sGroups = (ArrayList<Group>) HibernateUtil
				.getAllAsList(Group.class);
		ArrayList<Category> sSubjects = (ArrayList<Category>) HibernateUtil
				.getAllAsList(Category.class);

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

	/**
	 * Method for creating dummy data
	 */
	public static void createDummyActivities() {
		ArrayList<Member> members = (ArrayList<Member>) HibernateUtil
				.getAllAsList(Member.class);

		ArrayList<Campaign> entrys = new ArrayList<Campaign>(Arrays.asList(
				new Campaign("2014", "Erste Aktion", members.get(1)),
				new Campaign("2014", "Zweite Aktion", members.get(2)),
				new Campaign("2014", "Dritte Aktion", members.get(3))));
		HibernateUtil.saveAll(entrys);
	}

	/**
	 * Method for creating dummy data
	 */
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
					new Booking(2.1, "Buchung 01.01.2014",
							new SimpleDateFormat("dd.MM.yyyy")
									.parse("01.01.2014"), members.get(3),
							activities.get(1)),
					new Booking(2.2, "Buchung 31.12.2014",
							new SimpleDateFormat("dd.MM.yyyy")
									.parse("31.12.2014"), members.get(3),
							activities.get(2)),
					new Booking(2.4, "Buchung 31.12.2013",
							new SimpleDateFormat("dd.MM.yyyy")
									.parse("31.12.2013"), members.get(3),
							activities.get(2)),
					new Booking(2.5, "Buchung 01.01.2013",
							new SimpleDateFormat("dd.MM.yyyy")
									.parse("01.01.2013"), members.get(3),
							activities.get(2)),
					new Booking(2.6, "Buchung 30.12.2013",
							new SimpleDateFormat("dd.MM.yyyy")
									.parse("30.12.2013"), members.get(3),
							activities.get(2)),
					new Booking(3, "Buchung mitte 2014", new SimpleDateFormat(
							"dd.MM.yyyy").parse("11.01.2014"), members.get(2),
							activities.get(2))));
			years = new ArrayList<Year>(Arrays.asList(new Year(2013), new Year(
					2014)));
			HibernateUtil.saveAll(entrys);
			HibernateUtil.saveAll(years);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
