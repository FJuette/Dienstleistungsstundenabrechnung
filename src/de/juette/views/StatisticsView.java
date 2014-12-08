package de.juette.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.SecurityUtils;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.juette.dlsa.GeneralHandler;
import de.juette.model.Booking;
import de.juette.model.Category;
import de.juette.model.Group;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.Sector;

public class StatisticsView extends VerticalLayout implements View {

	private static final long serialVersionUID = -6517121952079289467L;
	private List<Member> members = new ArrayList<Member>();

	public StatisticsView() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if (SecurityUtils.getSubject().hasRole("Gast")) {
			addComponent(GeneralHandler.getNoGuestLabel());
			return;
		}
		Label title = new Label("<h3><strong>Statistik</strong></h3>", ContentMode.HTML);
		addComponent(title);
		addStyleName("myHeaderLabel");
		
		members.addAll(HibernateUtil.getAllAsList(Member.class));
		
		dlsPerSection();
		dlsPerGroup();
		dlsPerCategory();

	}
	
	private void dlsPerCategory() {
		Label title = new Label("<strong>Aktuell gebuchte DLS pro Sparte:</strong>", ContentMode.HTML);
		title.addStyleName("myFormLayout");
		addComponent(title);
		
		List<Member> members = new ArrayList<Member>();
		members.addAll(HibernateUtil.getAllAsList(Member.class));
		
		for (Category c : HibernateUtil.getAllAsList(Category.class)) {
			double dlsCount = 0;
			for (Member m : members) {
				if (m.getCategories().contains(c)) {
					for (Booking b : HibernateUtil.getBookings(m)) {
						dlsCount += b.getCountDls();
					}
				}
				
			}
			
			Label lblEntry = new Label("<strong>" + c.getCategoryName() + ":</strong> " + dlsCount, ContentMode.HTML);
			lblEntry.addStyleName("myHeaderLabel");
			addComponent(lblEntry);
		}
		
	}

	private void dlsPerGroup() {
		Label title = new Label("<strong>Aktuell gebuchte DLS pro Gruppe:</strong>", ContentMode.HTML);
		title.addStyleName("myFormLayout");
		addComponent(title);

		for (Group g : HibernateUtil.getAllAsList(Group.class)) {
			double dlsCount = getDlsForGroup(g);
			Label lblEntry = new Label("<strong>" + g.getGroupName() + ":</strong> " + dlsCount, ContentMode.HTML);
			lblEntry.addStyleName("myHeaderLabel");
			addComponent(lblEntry);
		}
		
	}

	private void dlsPerSection() {
		Label title = new Label("<strong>Aktuell gebuchte DLS pro Bereich:</strong>", ContentMode.HTML);
		title.addStyleName("myFormLayout");
		addComponent(title);
		
		List<Member> members = new ArrayList<Member>();
		members.addAll(HibernateUtil.getAllAsList(Member.class));
		
		for (Sector s : HibernateUtil.getAllAsList(Sector.class)) {
			double dlsCount = 0;
			for (Group g : s.getGroups()) {
				dlsCount += getDlsForGroup(g);
			}
			
			Label lblEntry = new Label("<strong>" + s.getSectorname() + ":</strong> " + dlsCount, ContentMode.HTML);
			lblEntry.addStyleName("myHeaderLabel");
			addComponent(lblEntry);
		}
		
	}
	
	private double getDlsForGroup(Group g) {
		
		Collection<String> bookingIds = new ArrayList<String>();
		
		double dlsCount = 0;
		for (Member m : members) {
			if (m.getGroups().contains(g)) {
				for (Booking b : HibernateUtil.getBookings(m)) {
					if (!bookingIds.contains(b.getId().toString())) { // Do not count the same booking twice
						System.out.println(b.getComment() + "  " + b.getCountDls() + "   " + b.getId());
						bookingIds.add(b.getId().toString());
						dlsCount += b.getCountDls();
					}
				}
			}
			
		}
		return dlsCount;
	}

}
