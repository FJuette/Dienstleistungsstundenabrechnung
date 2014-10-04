package de.juette.dlsa;

import java.util.ArrayList;

import model.Group;
import model.Member;
import model.Subject;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public class ComponentHelper {
	
	// Set the Caption of the TextField on the Top
	public static HorizontalLayout getCaptionOnTop(Component c) {
		HorizontalLayout txtLayout = new HorizontalLayout();
		txtLayout.addComponent(c);
		return txtLayout;
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

		return members;
	}
	
}
