package de.juette.views.tabs;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

import de.juette.model.Booking;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.Year;

@SuppressWarnings("serial")
public class MemberStatisticTab extends FormLayout {

	public MemberStatisticTab(BeanItem<Member> beanItem) {
		setMargin(true);
		setSizeFull();
		setStyleName("myFormLayout");
		
		// Auswahl der Jahre mit einer ComboBox?

		Label lblBooking = new Label("Bisherige Buchungen ");
		addComponent(lblBooking);
		double dlsCount = 0;
		for (Booking b : HibernateUtil.getBookings(beanItem.getBean())) {
			Label lblDls = new Label(
					b.getCountDls() + " DLS für die Aktion " + b.getCampaign().getDescription());
			addComponent(lblDls);
			dlsCount += b.getCountDls();
		}
		Label lblStatistic = new Label(
				"Stand der Dienstleistungsstunden: " + (Math.round(dlsCount * 100.0) / 100.0));
		addComponent(lblStatistic);
	}
}
