package de.juette.dlsa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.juette.model.BasicMember;
import de.juette.model.Group;
import de.juette.model.GroupInfo;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.MemberChanges;
import de.juette.model.MemberColumn;

public class MemberInfo {

	private Member member;
	DateTimeFormatter dateStringFormat = DateTimeFormat
			.forPattern("dd.MM.yyyy");

	public MemberInfo(Member member) {
		this.setMember(member);
	}

	public void printHistory() {
		System.out.println("--------- Changes for Member "
				+ member.getFullName() + "---------");
		
		Collection<MemberChanges> changes = HibernateUtil
				.getMemberChanges(member.getId());
		
		for (MemberChanges change : changes) {
			System.out.printf("Spalte '%s': '%s' -> '%s'%n",
					change.getColumn(), change.getOldValue(),
					change.getNewValue());
		}
		System.out.println("--------- End of changes ---------");
	}

	// Compare the basic and the current Member, must be the same in the
	// required columns
	public Boolean compareMembers(BasicMember bm) {
		Member m = getMemberStateFromDate(bm, DateTime.now().plusDays(1).toDate());

		if (m.getActive().equals(member.getActive())
				&& new DateTime(m.getEntryDate()).equals(new DateTime(member
						.getEntryDate()))
				&& new DateTime(m.getLeavingDate()).equals(new DateTime(member
						.getLeavingDate()))) {
			return true;
		}
		return false;
	}
	
	public Member getMemberStateFromDate(BasicMember bm, Date dt) {
		Member m = new Member();
		m.setActive(bm.getActive());
		m.setEntryDate(bm.getEntryDate());
		m.setLeavingDate(bm.getLeavingDate());
		Collection<MemberChanges> changes = HibernateUtil
				.getMemberChanges(member.getId(), dt);
		for (MemberChanges change : changes) {
			
			if (change.getColumn().equals(MemberColumn.ACTIVE.toString())) {
				m.setActive(Boolean.parseBoolean(change.getNewValue()));
				
			} else if (change.getColumn().equals(
					MemberColumn.ENTRYDATE.toString())) {
				m.setEntryDate(dateStringFormat.parseDateTime(
						change.getNewValue()).toDate());
				
			} else if (change.getColumn().equals(
					MemberColumn.LEAVINGDATE.toString())) {
				m.setLeavingDate(dateStringFormat.parseDateTime(
						change.getNewValue()).toDate());
			} else if (change.getColumn().equals(
					MemberColumn.GROUP.toString())) {
				Collection<Group> groups = new ArrayList<Group>();
				String[] ids = change.getNewValue().trim().split(" ");
				for (String id : ids) {
					if (id != null && !id.equals("")) {
						Group currentGroup = (Group) HibernateUtil.getUnique(Group.class, "id = " + id);
						GroupInfo gi = new GroupInfo(currentGroup);
						groups.add(gi.getGroupStateFromDate(HibernateUtil.getBasicGroup(currentGroup.getId()), dt));
					}
				}
				m.setGroups(groups);
			}
		}
		return m;
	}
	
	public void updateMember(Member m) {
		m.removeAllPropertyChangeListeners();
		Collection<MemberChanges> changes = HibernateUtil
				.getMemberChanges(member.getId());
		for (MemberChanges change : changes) {
			
			if (change.getColumn().equals(MemberColumn.ACTIVE.toString())) {
				m.setActive(Boolean.parseBoolean(change.getNewValue()));
				
			} else if (change.getColumn().equals(
					MemberColumn.ENTRYDATE.toString())) {
				m.setEntryDate(dateStringFormat.parseDateTime(
						change.getNewValue()).toDate());
				
			} else if (change.getColumn().equals(
					MemberColumn.LEAVINGDATE.toString())) {
				m.setLeavingDate(dateStringFormat.parseDateTime(
						change.getNewValue()).toDate());
			} else if (change.getColumn().equals(
					MemberColumn.GROUP.toString())) {
				Collection<Group> groups = new ArrayList<Group>();
				String[] ids = change.getNewValue().trim().split(" ");
				for (String id : ids) {
					if (id != null && !id.equals("")) {
						Group currentGroup = (Group) HibernateUtil.getUnique(Group.class, "id = " + id);
						GroupInfo gi = new GroupInfo(currentGroup);
						groups.add(gi.getGroupStateFromDate(HibernateUtil.getBasicGroup(currentGroup.getId()), DateTime.now().toDate()));
					}
				}
				m.setGroups(groups);
			}
		}
		HibernateUtil.save(m);
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
