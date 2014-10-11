package de.juette.dlsa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import model.Activity;
import model.Booking;
import model.Group;
import model.Member;
import model.Role;
import model.Subject;
import model.User;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

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
		BeanItemContainer<Subject> subjects = new BeanItemContainer<Subject>(Subject.class);
		subjects.addItem(new Subject("Spasstänzer"));
		subjects.addItem(new Subject("Standdardstänzer"));
		subjects.addItem(new Subject("Rutinetänzer"));
		subjects.addItem(new Subject("Proditänzer"));
		return subjects;
	}
	
	public static BeanItemContainer<Group> getDummyGroups() {
		BeanItemContainer<Group> groups = new BeanItemContainer<Group>(Group.class);
		groups.addItem(new Group("Trainer", true));
		groups.addItem(new Group("Vorstand", true));
		groups.addItem(new Group("Mitglied", false));
		groups.addItem(new Group("Aufsicht", false));
		return groups;
	}

	public static BeanItemContainer<Member> getDummyMembers() {
		BeanItemContainer<Member> members = new BeanItemContainer<Member>(Member.class);
		
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
		
		members.addItem(new Member("Sander", "Thorsten", "123456", sGroups, sSubjects));
		members.addItem(new Member("Juette", "Fabian", "987654", jGroups, jSubjects));
		members.addItem(new Member("Tester", "Tom", "565645", sGroups, sSubjects));
		members.addItem(new Member("Juppie", "Jörn", "848484", jGroups, jSubjects));

		return members;
	}
		
	public static BeanItemContainer<Activity> getDummyActivities() {
		BeanItemContainer<Activity> activities = new BeanItemContainer<>(Activity.class);
		activities.addItem(new Activity(
				"2014", "Erste Aktion", 
				5, getDummyMembers().getIdByIndex(1), 
				getDummyMembers().getIdByIndex(2)));
		activities.addItem(new Activity(
				"2014", "Zweite Aktion", 
				(float) 3.5, getDummyMembers().getIdByIndex(2), 
				getDummyMembers().getIdByIndex(3)));
		activities.addItem(new Activity(
				"2014", "Dritte Aktion", 
				(float) 2.5, getDummyMembers().getIdByIndex(3), 
				getDummyMembers().getIdByIndex(1)));
		return activities;
	}
	
	public static BeanItemContainer<Booking> getDummyBookings() {
		BeanItemContainer<Booking> bookings = new BeanItemContainer<Booking>(Booking.class);
		try {
			bookings.addItem(new Booking(
					2, "Erste Buchung", 
					new SimpleDateFormat("dd.MM.yyyy").parse("01.10.2014"), getDummyMembers().getIdByIndex(3), 
					getDummyActivities().getIdByIndex(1), getDummyMembers().getIdByIndex(2)));
			bookings.addItem(new Booking(
					3, "Zweite Buchung", 
					new SimpleDateFormat("dd.MM.yyyy").parse("11.01.2014"), getDummyMembers().getIdByIndex(2), 
					getDummyActivities().getIdByIndex(2), getDummyMembers().getIdByIndex(1)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bookings;
	}
	
	public static BeanItemContainer<User> getDummyUsers() {
		BeanItemContainer<User> users = new BeanItemContainer<User>(User.class);
		users.addItem(new User("Administrator", "geheim", true, getDummyRoles().getIdByIndex(0)));
		users.addItem(new User("Benutzer1", "nochgeheimer", true, getDummyRoles().getIdByIndex(1)));
		users.addItem(new User("Inaktiver", "geheim", false, getDummyRoles().getIdByIndex(1)));
		return users;
	}
	
	public static BeanItemContainer<Role> getDummyRoles() {
		BeanItemContainer<Role> roles = new BeanItemContainer<Role>(Role.class);
		roles.addItem(new Role("Admin"));
		roles.addItem(new Role("Benutzer"));
		return roles;
	}
	
}
