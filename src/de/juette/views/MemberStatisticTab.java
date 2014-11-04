package de.juette.views;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

import de.juette.model.Booking;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class MemberStatisticTab extends FormLayout {

	public MemberStatisticTab(BeanItem<Member> beanItem) {
		setMargin(true);
		setSizeFull();
		setStyleName("myFormLayout");

		// Must be set on the default value from the settings
		float dlsCount = -10;
		for (Booking b : HibernateUtil.getMembers(beanItem.getBean())) {
			dlsCount += b.getCountDls();
		}

		Label lblStatistic = new Label(
				"Aktueller Stand der Dienstleistungsstunden: " + dlsCount);
		addComponent(lblStatistic);
	}
}
