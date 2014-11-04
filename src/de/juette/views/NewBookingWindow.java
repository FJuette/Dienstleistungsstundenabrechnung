package de.juette.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.juette.model.Booking;
import de.juette.model.Campaign;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class NewBookingWindow extends Window {

	private Booking booking;
	private List<Booking> bookings = new ArrayList<Booking>();

	public Booking getBooking() {
		return booking;
	}
	
	public List<Booking> getBookings() {
		return bookings;
	}

	@SuppressWarnings("unchecked")
	public NewBookingWindow(Boolean single) {
		setCaption("Anlegen einer neuen Buchung");
		setModal(true);
		setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		setContent(layout);
		
		ComboBox cbMembers = new ComboBox("Mitglied");
		if (single) {
			BeanItemContainer<Member> members = new BeanItemContainer<Member>(
					Member.class);
			members.addAll((Collection<? extends Member>) HibernateUtil
					.getAllAsList(Member.class));

			cbMembers.setWidth("100%");
			cbMembers.setImmediate(true);
			cbMembers.setContainerDataSource(members);
			cbMembers.setItemCaptionPropertyId("fullName");
			cbMembers.setFilteringMode(FilteringMode.CONTAINS);
			layout.addComponent(cbMembers);
		}		

		DateField dfDate = new DateField("Ableistungsdatum");
		dfDate.setWidth("100%");
		layout.addComponent(dfDate);

		BeanItemContainer<Campaign> activities = new BeanItemContainer<Campaign>(
				Campaign.class);
		activities.addAll((Collection<? extends Campaign>) HibernateUtil
				.getAllAsList(Campaign.class));

		ComboBox cbCampaigns = new ComboBox("Aktion");
		cbCampaigns.setWidth("100%");
		cbCampaigns.setImmediate(true);
		cbCampaigns.setContainerDataSource(activities);
		cbCampaigns.setItemCaptionPropertyId("description");
		cbCampaigns.setFilteringMode(FilteringMode.CONTAINS);
		layout.addComponent(cbCampaigns);

		TextField txtCountDls = new TextField("Anzahl DLS");
		txtCountDls.setWidth("100%");
		layout.addComponent(txtCountDls);

		TextArea txtComment = new TextArea("Bemerkung");
		txtComment.setWidth("100%");
		layout.addComponent(txtComment);

		Button btnSaveNewBooking = new Button("Speichern");
		btnSaveNewBooking.setStyleName("friendly");
		layout.addComponent(btnSaveNewBooking);

		btnSaveNewBooking.addClickListener(event -> {
			booking = new Booking();
			booking.setCountDls(Double.parseDouble(txtCountDls.getValue()
					.replace(',', '.')));
			booking.setComment(txtComment.getValue());
			booking.setDoneDate(dfDate.getValue());
			booking.setCampaign((Campaign) cbCampaigns.getValue());
			if (single) {
				booking.setMember((Member) cbMembers.getValue());
				HibernateUtil.save(booking);
				bookings.add(booking);
				txtCountDls.setValue("");
				txtComment.setValue("");
				cbCampaigns.setValue(null);
				dfDate.focus();
			} else
				close();
		});
	}
}
