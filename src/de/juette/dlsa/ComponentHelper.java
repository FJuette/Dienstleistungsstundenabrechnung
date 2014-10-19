package de.juette.dlsa;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javafx.scene.shape.Cylinder;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

import de.juette.model.Activity;
import de.juette.model.Booking;
import de.juette.model.Cycle;
import de.juette.model.Group;
import de.juette.model.Log;
import de.juette.model.Member;
import de.juette.model.Role;
import de.juette.model.Subject;
import de.juette.model.User;

public class ComponentHelper {

	// Set the Caption of the TextField on the Top
	public static HorizontalLayout getCaptionOnTop(Component c) {
		HorizontalLayout txtLayout = new HorizontalLayout();
		txtLayout.addComponent(c);
		return txtLayout;
	}

	public static void updateTable(Table table) {
		if (table.size() > 15) {
			table.setPageLength(15);
		} else {
			table.setPageLength(table.size() + 1);
		}
		table.markAsDirtyRecursive();
	}

	public static BeanItemContainer<Subject> getDummySubjects() {
		BeanItemContainer<Subject> subjects = new BeanItemContainer<Subject>(
				Subject.class);
		subjects.addItem(new Subject("Freizeitsportler"));
		subjects.addItem(new Subject("Gelegenheitssportler"));
		subjects.addItem(new Subject("Rutinesportler"));
		subjects.addItem(new Subject("Leistungssportler"));
		return subjects;
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

	public static BeanItemContainer<Activity> getDummyActivities() {
		BeanItemContainer<Activity> activities = new BeanItemContainer<>(
				Activity.class);
		activities.addItem(new Activity("2014", "Erste Aktion", 5,
				getDummyMembers().getIdByIndex(1), getDummyMembers()
						.getIdByIndex(2)));
		activities.addItem(new Activity("2014", "Zweite Aktion", (float) 3.5,
				getDummyMembers().getIdByIndex(2), getDummyMembers()
						.getIdByIndex(3)));
		activities.addItem(new Activity("2014", "Dritte Aktion", (float) 2.5,
				getDummyMembers().getIdByIndex(3), getDummyMembers()
						.getIdByIndex(1)));
		return activities;
	}

	public static BeanItemContainer<Booking> getDummyBookings() {
		BeanItemContainer<Booking> bookings = new BeanItemContainer<Booking>(
				Booking.class);
		try {
			bookings.addItem(new Booking(2, "Erste Buchung",
					new SimpleDateFormat("dd.MM.yyyy").parse("01.10.2014"),
					getDummyMembers().getIdByIndex(3), getDummyActivities()
							.getIdByIndex(1), getDummyMembers().getIdByIndex(2)));
			bookings.addItem(new Booking(3, "Zweite Buchung",
					new SimpleDateFormat("dd.MM.yyyy").parse("11.01.2014"),
					getDummyMembers().getIdByIndex(2), getDummyActivities()
							.getIdByIndex(2), getDummyMembers().getIdByIndex(1)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bookings;
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

	public static BeanItemContainer<Role> getDummyRoles() {
		BeanItemContainer<Role> roles = new BeanItemContainer<Role>(Role.class);
		roles.addItem(new Role("Admin"));
		roles.addItem(new Role("Benutzer"));
		return roles;
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

	public static BeanItemContainer<Cycle> getDummyCycles() {
		BeanItemContainer<Cycle> cycles = new BeanItemContainer<Cycle>(
				Cycle.class);
		File file = new File(VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath()
				+ "/WEB-INF/Files/ExampleResult.csv");
		try {
			cycles.addItem(new Cycle(file, new SimpleDateFormat(
					"dd.MM.yyyy hh:mm").parse("13.03.2014 10:23"),
					"Jahreslauf vom 31.03.2013"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cycles;
	}
}
