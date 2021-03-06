package de.juette.views.windows;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;




import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;


import com.vaadin.ui.Notification.Type;

import de.juette.dlsa.MyDlsValidator;
import de.juette.model.Booking;
import de.juette.model.Campaign;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;
import de.juette.model.Settings;
import de.juette.model.Year;

public class NewBookingWindow extends Window {

	private static final long serialVersionUID = 2753029665964576554L;
	private Booking booking;
	private List<Booking> bookings = new ArrayList<Booking>();

	public Booking getBooking() {
		return booking;
	}
	
	public List<Booking> getBookings() {
		return bookings;
	}

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
			members.addAll(HibernateUtil.orderedWhereList(Member.class, "aikz = " + true,
					"surname asc, forename asc, memberId asc"));

			cbMembers.setWidth("100%");
			cbMembers.setImmediate(true);
			cbMembers.setContainerDataSource(members);
			cbMembers.setItemCaptionPropertyId("fullName");
			cbMembers.setFilteringMode(FilteringMode.CONTAINS);
			layout.addComponent(cbMembers);
		}		

		DateField dfDate = new DateField("Ableistungsdatum");
		dfDate.setWidth("100%");
		dfDate.addValidator(new MyDateValidator());
		dfDate.setImmediate(true);
		layout.addComponent(dfDate);

		BeanItemContainer<Campaign> activities = new BeanItemContainer<Campaign>(
				Campaign.class);
		activities.addAll(HibernateUtil.getAllAsList(Campaign.class));

		ComboBox cbCampaigns = new ComboBox("Aktion");
		cbCampaigns.setWidth("100%");
		cbCampaigns.setContainerDataSource(activities);
		cbCampaigns.setItemCaptionPropertyId("description");
		cbCampaigns.setFilteringMode(FilteringMode.CONTAINS);
		cbCampaigns.setRequired(true);
		cbCampaigns.setNullSelectionAllowed(false);
		layout.addComponent(cbCampaigns);

		TextField txtCountDls = new TextField("Anzahl DLS");
		txtCountDls.setWidth("100%");
		txtCountDls.setValue("0");
		Settings s = HibernateUtil.getSettings();
		txtCountDls.addValidator(new MyDlsValidator(s.getGranularity()));
		layout.addComponent(txtCountDls);

		TextArea txtComment = new TextArea("Bemerkung");
		txtComment.setWidth("100%");
		layout.addComponent(txtComment);

		Button btnSaveNewBooking = new Button("Speichern");
		btnSaveNewBooking.setStyleName("friendly");
		layout.addComponent(btnSaveNewBooking);

		btnSaveNewBooking.addClickListener(event -> {
			if (dfDate.isValid() && txtCountDls.isValid() && cbCampaigns.isValid()) {
				booking = new Booking();
				booking.setCountDls(Double.parseDouble(txtCountDls.getValue()
						.replace(',', '.')));
				booking.setComment(txtComment.getValue());
				booking.setDoneDate(dfDate.getValue());
				booking.setCampaign((Campaign) cbCampaigns.getValue());
				List<Year> years = HibernateUtil.getAllAsList(Year.class);
				Calendar c = Calendar.getInstance();
				c.setTime(dfDate.getValue());
				List<Integer> pureYears = new ArrayList<Integer>();
				years.forEach(year -> {
					pureYears.add(year.getYear());
				});
				if (!pureYears.contains(c.get(Calendar.YEAR))) {
					HibernateUtil.save(new Year(c.get(Calendar.YEAR)));
				}
				
				if (single) {
					booking.setMember((Member) cbMembers.getValue());
					HibernateUtil.save(booking);
					bookings.add(booking);
					txtCountDls.setValue("0");
					txtComment.setValue("");
					cbCampaigns.setValue(null);
					dfDate.focus();
					Notification.show("Speichern erfolgreich", Type.TRAY_NOTIFICATION);
					System.out.println("Einfache Buchung durchgeführt");
				} else
					close();
			}
		});
	}
	
	class MyDateValidator implements Validator {

	private static final long serialVersionUID = 659045920967566923L;
		@Override
	    public void validate(Object value)
	            throws InvalidValueException {
	    	if (value instanceof Date && ((Date)value).compareTo(new Date()) > 0)
	            throw new InvalidValueException("Das Ableistungsdatum kann nicht in der Zukunft liegen.");
	    }
	}
}
