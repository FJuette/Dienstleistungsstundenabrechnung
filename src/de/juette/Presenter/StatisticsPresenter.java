package de.juette.Presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.navigator.View;

import de.juette.model.Booking;
import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.Sector;
import de.juette.views.StatisticsView;

public class StatisticsPresenter {

	// Model
	private List<Member> members = new ArrayList<Member>();
    StatisticsView view;

    public StatisticsPresenter(View view) {
		this.view = (StatisticsView) view;
		members.addAll(HibernateUtil.getAllAsList(Member.class));
		
		this.view.setSectionData(dlsPerSection());
		this.view.setGroupData(dlsPerGroup());
		this.view.setCategoryData(dlsPerCategory());
	}
    
    private List<String> dlsPerCategory() {
		List<Member> members = new ArrayList<Member>();
		members.addAll(HibernateUtil.getAllAsList(Member.class));
		
		List<String> items = new ArrayList<String>();
		for (Category c : HibernateUtil.getAllAsList(Category.class)) {
			double dlsCount = 0;
			for (Member m : members) {
				if (m.getCategories().contains(c)) {
					for (Booking b : HibernateUtil.getBookings(m)) {
						dlsCount += b.getCountDls();
					}
				}	
			}
			items.add("<strong>" + c.getCategoryName() + ":</strong> " + dlsCount);
		}
		return items;
	}

	private List<String> dlsPerGroup() {
		List<String> items = new ArrayList<String>();
		for (Group g : HibernateUtil.getAllAsList(Group.class)) {
			bookingIds = new ArrayList<String>();
			double dlsCount = getDlsForGroup(g);
			items.add("<strong>" + g.getGroupName() + ":</strong> " + dlsCount);
		}
		return items;
	}

	private Collection<String> bookingIds;
	private List<String> dlsPerSection() {
		List<Member> members = new ArrayList<Member>();
		members.addAll(HibernateUtil.getAllAsList(Member.class));
		
		List<String> items = new ArrayList<String>();
		for (Sector s : HibernateUtil.getAllAsList(Sector.class)) {
			bookingIds = new ArrayList<String>();
			double dlsCount = 0;
			for (Group g : s.getGroups()) {
				dlsCount += getDlsForGroup(g);
			}
			items.add("<strong>" + s.getSectorname() + ":</strong> " + dlsCount);
		}
		return items;
	}
	
	private double getDlsForGroup(Group g) {
		double dlsCount = 0;
		for (Member m : members) {
			if (m.getGroups().contains(g)) {
				for (Booking b : HibernateUtil.getBookings(m)) {
					if (!bookingIds.contains(b.getId().toString())) { // Do not count the same booking twice
						System.out.println("Gesucht: " + b.getId().toString() + " Booking IDs enhält:");
						bookingIds.forEach(item -> {
							System.out.print(item + ", ");
						});
						bookingIds.add(b.getId().toString());
						dlsCount += b.getCountDls();
					}
				}
			}
		}
		return dlsCount;
	}
}
