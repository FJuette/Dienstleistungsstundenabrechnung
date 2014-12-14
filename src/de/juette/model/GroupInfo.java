package de.juette.model;

import java.util.Collection;
import java.util.Date;

public class GroupInfo {

	private Group group;

	public GroupInfo(Group group) {
		this.group = group;
	}
	
	public void printHistory() {
		System.out.println("--------- Changes for Group "
				+ group.getGroupName() + "---------");
		Collection<GroupChanges> changes = HibernateUtil
				.getGroupChanges(group.getId());
		for (GroupChanges change : changes) {
			System.out.printf("Spalte '%s' -> '%s'%n",
					change.getOldValue(),
					change.getNewValue());
		}
		System.out.println("--------- End of changes ---------");
	}
	
	public Group getGroupStateFromDate(BasicGroup bg, Date dt) {
		Group g = new Group();
		g.setLiberated(bg.getLiberate());
		Collection<GroupChanges> changes = HibernateUtil
				.getGroupChangesUntilDate(group.getId(), dt);
		for (GroupChanges change : changes) {
			g.setLiberated(change.getNewValue());
		}
		return g;
	}
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
}
