package de.juette.views;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.juette.dlsa.ComponentHelper;
import de.juette.dlsa.DateToShortGermanStringConverter;
import de.juette.model.AbstractEntity;
import de.juette.model.Activity;
import de.juette.model.Booking;
import de.juette.model.HibernateUtil;
import de.juette.model.Member;

@SuppressWarnings("serial")
public class BookingView extends VerticalLayout implements View {

	private Handler actionHandler = new Handler() {
		private final Action STORBNO = new Action("Stornieren");
		private final Action[] ACTIONS = new Action[] { STORBNO };

		@Override
		public void handleAction(final Action action, final Object sender,
				final Object target) {
			if (action.getCaption().equals("Stornieren")) {
				if (!((Booking) tblBookings.getValue()).getStorniert()) {
					Booking b = new Booking();
					b.setAbleistungsDatum(new Date());
					b.setAktion(((Booking) tblBookings.getValue()).getAktion());
					b.setAnzahlDLS(-((Booking) tblBookings.getValue())
							.getAnzahlDLS());
					b.setBemerkung("Stornierung von "
							+ ((Booking) tblBookings.getValue()).getBemerkung());
					b.setMitglied(((Booking) tblBookings.getValue())
							.getMitglied());

					((Booking) tblBookings.getValue()).setStorniert(true);

					bookings.addItem(b);
					ComponentHelper.updateTable(tblBookings);
					HibernateUtil
							.saveAll((List<? extends AbstractEntity>) bookings
									.getItemIds());
				} else {
					Notification
							.show("Das Stornieren der Buchung ist leider nicht möglich, "
									+ "da diese bereits Storniert wurde.", Type.ERROR_MESSAGE);
				}

			}
		}

		@Override
		public Action[] getActions(final Object target, final Object sender) {
			return ACTIONS;
		}
	};

	private final Table tblBookings = new Table();
	private final HorizontalLayout filterLayout = new HorizontalLayout();
	private final Button btnNewBookings = new Button("Neue Buchung(en)");
	private final Button btnYear = new Button("Jahreslauf durchführen");
	private final Button btnThisYear = new Button("Vorläufiger Jahreslauf 2014");
	private final Button btnYearBefore = new Button(
			"Vorläufiger Jahreslauf 2013");

	//

	private BeanItemContainer<Booking> bookings = new BeanItemContainer<>(
			Booking.class);

	public BookingView() {
		initLayout();
		initTable();
		initFilter();
	}

	private void initLayout() {
		setSpacing(true);
		setMargin(true);

		Label title = new Label("Journal");
		title.addStyleName("h1");
		addComponent(title);

		addComponent(btnNewBookings);
		btnNewBookings.setIcon(FontAwesome.PLUS);
		btnNewBookings.addClickListener(event -> {
			newBookingsWindow();
		});

		Label lblFilter = new Label("Filter:");
		lblFilter.setStyleName("h3");
		addComponent(lblFilter);
		addComponent(filterLayout);
		addComponent(tblBookings);

		btnYear.setStyleName("primary");
		btnYearBefore.setStyleName("primary");
		btnThisYear.setStyleName("primary");

		HorizontalLayout btnsYear = new HorizontalLayout();
		btnsYear.setSpacing(true);
		btnsYear.addComponent(btnYearBefore);
		btnsYear.addComponent(btnThisYear);
		btnsYear.addComponent(btnYear);
		addComponent(btnsYear);

		btnYear.addClickListener(event -> {
			YearWindow();
		});

	}

	@SuppressWarnings("unchecked")
	private void initTable() {
		bookings.addAll((Collection<? extends Booking>) HibernateUtil
				.getAllAsList(Booking.class));
		// Add the nested Property to the available columns
		bookings.addNestedContainerProperty("mitglied.mitgliedsnummer");

		tblBookings.setContainerDataSource(bookings);
		tblBookings.setSelectable(true);
		tblBookings.setImmediate(true);
		tblBookings.setVisibleColumns(new Object[] { "anzahlDLS",
				"ableistungsDatum", "bemerkung", "mitglied.mitgliedsnummer" });
		tblBookings.setColumnHeaders("Anzahl DLS", "Ableistungsdatum",
				"Bemerkung", "Mitgliedsnummer");
		tblBookings.setConverter("ableistungsDatum",
				new DateToShortGermanStringConverter());
		tblBookings.setWidth("100%");
		tblBookings.addActionHandler(getActionHandler());
		tblBookings.setColumnExpandRatio("anzahlDLS", (float) 0.1);
		tblBookings.setColumnExpandRatio("ableistungsDatum", (float) 0.2);
		tblBookings.setColumnExpandRatio("bemerkung", (float) 0.5);
		tblBookings.setColumnExpandRatio("mitglied.mitgliedsnummer",
				(float) 0.2);

		ComponentHelper.updateTable(tblBookings);
	}

	private void initFilter() {
		filterLayout.setSpacing(true);
		TextField txtFilterDls = new TextField("Anzahl DLS:");
		txtFilterDls.setConverter(Integer.class);
		txtFilterDls
				.setConversionError("Die Anzahl der Dienstleistungsstunden muss eine Zahl sein.");
		txtFilterDls.setNullRepresentation("");
		txtFilterDls.addTextChangeListener(event -> {
			if (!event.getText().equals("")) {
				filterTable("anzahlDLS", event.getText());
			} else {
				bookings.removeContainerFilters("anzahlDLS");
			}

		});

		DateField dfFilterDate = new DateField("Datum:");
		dfFilterDate
				.addValueChangeListener(event -> {
					if (((DateField) event.getProperty()).getValue() != null) {
						filterTable("ableistungsDatum", ((DateField) event
								.getProperty()).getValue().toString());
					} else {
						bookings.removeContainerFilters("ableistungsDatum");
					}

				});

		TextField txtFilterNote = new TextField("Bemerkung:");
		txtFilterNote.setConverter(String.class);
		txtFilterNote.addTextChangeListener(event -> {
			if (!event.getText().equals("")) {
				filterTable("bemerkung", event.getText());
			} else {
				bookings.removeContainerFilters("bemerkung");
			}

		});
		filterLayout.addComponents(txtFilterDls, dfFilterDate, txtFilterNote);
	}

	private void filterTable(Object columnId, String value) {
		// bookings.removeAllContainerFilters();
		bookings.addContainerFilter(columnId, value, true, false);

		ComponentHelper.updateTable(tblBookings);
	}

	@SuppressWarnings("unchecked")
	private void newBookingsWindow() {
		Window window = new Window("Anlegen einer neuen Buchung");
		window.setModal(true);
		window.setWidth("400");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		BeanItemContainer<Member> members = new BeanItemContainer<Member>(
				Member.class);
		members.addAll((Collection<? extends Member>) HibernateUtil
				.getAllAsList(Member.class));

		ComboBox cbMembers = new ComboBox("Mitglied");
		cbMembers.setWidth("100%");
		cbMembers.setImmediate(true);
		cbMembers.setContainerDataSource(members);
		cbMembers.setItemCaptionPropertyId("fullName");
		cbMembers.setFilteringMode(FilteringMode.CONTAINS);
		layout.addComponent(cbMembers);

		DateField dfDate = new DateField("Ableistungsdatum");
		dfDate.setWidth("100%");
		layout.addComponent(dfDate);

		BeanItemContainer<Activity> activities = new BeanItemContainer<Activity>(
				Activity.class);
		activities.addAll((Collection<? extends Activity>) HibernateUtil
				.getAllAsList(Activity.class));

		ComboBox cbActivities = new ComboBox("Aktion");
		cbActivities.setWidth("100%");
		cbActivities.setImmediate(true);
		cbActivities.setContainerDataSource(activities);
		cbActivities.setItemCaptionPropertyId("beschreibung");
		cbActivities.setFilteringMode(FilteringMode.CONTAINS);
		layout.addComponent(cbActivities);

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
			bookings.addItem(new Booking(Double.parseDouble(txtCountDls
					.getValue().replace(',', '.')), txtComment.getValue(),
					dfDate.getValue(), (Member) cbMembers.getValue(),
					(Activity) cbActivities.getValue()));
			ComponentHelper.updateTable(tblBookings);
			HibernateUtil.saveAll((List<? extends AbstractEntity>) bookings);
			txtCountDls.setValue("");
			txtComment.setValue("");
			cbActivities.setValue(null);
			dfDate.focus();
		});

		getUI().addWindow(window);
	}

	private void YearWindow() {
		Window window = new Window("Bestätigung");
		window.setModal(true);
		window.setWidth("500");

		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		window.setContent(layout);

		Label lblQuestion = new Label(
				"Soll der Jahreslauf jetzt durchgeführt werden?<br /> "
						+ "Ein erneuter Durchlauf für dieses Jahr ist dann <strong>nicht</strong> mehr möglich.");
		lblQuestion.setStyleName("h4");
		lblQuestion.setContentMode(ContentMode.HTML);
		lblQuestion.setStyleName("center");
		layout.addComponent(lblQuestion);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setSpacing(true);
		btnLayout.setStyleName("center");
		layout.addComponent(btnLayout);

		Button btnYes = new Button("Ja");
		btnLayout.addComponent(btnYes);

		Button btnNo = new Button("Nein");
		btnLayout.addComponent(btnNo);

		String basepath = VaadinService.getCurrent().getBaseDirectory()
				.getAbsolutePath();
		Resource res = new FileResource(new File(basepath
				+ "/WEB-INF/Files/ExampleResult.csv"));
		FileDownloader fd = new FileDownloader(res);
		fd.extend(btnYes);

		btnYes.addClickListener(evnet -> {
			window.close();
		});

		btnNo.addClickListener(event -> {
			window.close();
		});

		getUI().addWindow(window);
	}

	private Handler getActionHandler() {
		return actionHandler;
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
